% ----------------------------------------------------------------------
% $Id: dcfsfkacem.red 613 2010-05-14 13:41:59Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 2004-2009 Andreas Dolzmann and Thomas Sturm
% ----------------------------------------------------------------------
% Redistribution and use in source and binary forms, with or without
% modification, are permitted provided that the following conditions
% are met:
%
%    * Redistributions of source code must retain the relevant
%      copyright notice, this list of conditions and the following
%      disclaimer.
%    * Redistributions in binary form must reproduce the above
%      copyright notice, this list of conditions and the following
%      disclaimer in the documentation and/or other materials provided
%      with the distribution.
%
% THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
% "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
% LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
% A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
% OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
% SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
% LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
% DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
% THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
% (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
% OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
% 

lisp <<
   fluid '(dcfsfkacem_rcsid!* dcfsfkacem_copyright!*);
   dcfsf_kacem_rcsid!* :=
      "$Id: dcfsfkacem.red 613 2010-05-14 13:41:59Z thomas-sturm $";
   dcfsf_kacem_copyright!* :=
      "Copyright (c) 2004-2009 A. Dolzmann andn T. Sturm"
>>;

module dcfsfkacem;
% diferentially closed field standard form.

% part 1

fluid '(dqe_counter!* !*dqeverbose !*dqegradord !*dqeoptqelim !*dqeoptsimp);

switch dqeverbose;
switch dqegradord;
switch dqeoptqelim;
switch dqeoptsimp;

on1 'dqeverbose;
on1 'dqegradord;
on1 'dqeoptqelim;
on1 'dqeoptsimp;

algebraic (for all x,n let df(d(x,n),x)=0);

% part 2

symbolic procedure dqe_isconstant(phi);
   % is a constant. [phi] is differential polynomial. Returns nom-nil
   % iff phi is a constant.
   numberp phi or (pairp phi and car phi eq 'quotient and 
      numberp caddr phi and numberp reval cadr phi);
 
%%%%%%%%%%%%%%   dqe_isatomarp   %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                               %
% diese prozedur  testet ob phi eine atomare formel ist.        %
% (siehe kapitel 4 abschnitt 4.8)                               %
%                                                               %
% eingabe : beliebige formel phi .                              %
%                                                               %
% ausgabe : true   falls phi atomar ist d.h. in sm. ist phi von %
%                  der form list(elem,f,g) wobei elem = equal   %
%                  order neq und f,g differentiale polynome sind%
%           false  sonst .                                      %
%                                                               %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
 
procedure dqe_isatomarp(phi);
   pairp phi and (car phi eq 'neq or car phi eq 'equal);
 
%%%%%%%%%%%%%%   dqe_isquantfree %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                               %
% diese prozedur  testet ob eine formel phi quantorenfrei ist.  %
% (siehe kapitel 4 abschnitt 4.8)                               %
%                                                               %
% eingabe : beliebige formel phi .                              %
%                                                               %
% ausgabe : true   falls phi quantorenfreie formel ist.         %
%           false  sonst .                                      %
%                                                               %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
 
symbolic procedure dqe_isquantfree(phi);
   begin scalar erg;
      if atom phi or (not phi) or dqe_isatomarp phi then
	 return T;
      if car phi = 'nott  then
	 return dqe_isquantfree cadr phi;      
      if car phi eq 'or or car phi eq 'and then <<
	 phi := cdr phi;
	 erg := T;
	 while erg and phi do <<
	    erg := dqe_isquantfree car phi;
	    phi := cdr phi
	 >>;
	 return erg;
      >>;
      return nil;
end;
 
 
%%%%%%%%%%%%%%   dqe_isprenexp   %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                               %
% diese prozedur  testet ob eine formel phi in prenexform ist.  %
% (siehe kapitel 4 abschnitt 4.8)                               %
%                                                               %
% eingabe : beliebige formel phi .                              %
%                                                               %
% ausgabe : true   falls phi quantorenfrei ist oder phi von der %
%                  q_1 x_1...q_n x_n psi wobei q_i = ex oder all%
%                  und psi quantorenfrei ist.                   %
%           false  sonst .                                      %
%                                                               %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
 
procedure dqe_isprenexp(phi);
   begin scalar erg;
      if  atom phi or (not phi) then
	 erg := t
      else <<
	 while (car phi ='ex) or (car phi ='all) do
	    phi := caddr phi;
      	 erg := dqe_isquantfree phi
      >>; 
      return erg;
   end;

%%%%%%%%%%%%%%%%   dqe_modatomar     %%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                               %
% dqe_modatomar ist eine sub-routine fuer dqe_helpelim.                 %
% (siehe kapitel 4 abschnitt 4.6)                               %
%                                                               %
% eingabe : atomare formel von der form "f = g" oder "not(f =g)"%
%                                                               %
% ausgabe : "f - g = 0" bzw "not(f - g = 0 )".                  %
%                                                               %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
 
procedure dqe_modatomar(phi);
   if caddr phi = 0 then
      phi
   else
      {car phi,reval {'difference,cadr phi,caddr phi},0};
 
%%%%%%%%%%%%%%%%    dqe_helpelim    %%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                             %
% dqe_helpelim ist eine hilfsprozedur fuer dqe_elim.          %
% (siehe kapitel 4 abschnitt 4.6)                             %
%                                                             %
% eingabe : eine teilformel phi.                              %
%                                                             %
% ausgabe : list(g)     falls phi von der form not(g= 0) oder %
%                       g = g1*g2*..*gm und phi von der form  %
%                       not(g1=0) and ...and not(gm=0) .      %
%           list(1,f)   falls phi von der form f = 0          %
%           list(1,f1,...,fn)  falls phi von der form         %
%                              f1 = 0 and ...and fn = 0 .     %
%           list(g,f1,...,fn)  falls phi von der form         %
%                              f1 = 0 and ...and fn = 0 and   %
%                              not(g1=0) and ...and not(gm=0) %
%                              wobei    g = g1*g2*..*gm .     %
%                                                             %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
 
procedure dqe_helpelim(phi);
   begin scalar op;
      if (phi eq t) or (not phi) then
	 return phi;
      op := car phi;
      if op eq 'neq then
	 return {reval cadr dqe_modatomar phi};
      if op eq 'equal then
	 return {1,reval cadr dqe_modatomar phi};
      if op eq 'and then
	 return dqe_helpelim!-and cdr phi;
      rederr "dqe_helpelim: internal error";
   end;

procedure dqe_helpelim!-and(phi);
   begin scalar a,eqs,g;
      g := 1;
      while phi do <<
	 a := car phi;
	 if car a eq 'equal then
	    eqs := adjoin(reval cadr dqe_modatomar a,eqs)
	 else
	    g := reval {'times,g,reval cadr dqe_modatomar a};
	 phi := cdr phi
      >>;
      return g . reversip eqs
   end;
 
 
%%%%%%%%%%%%%%%%   dqe_andorvaleur   %%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                               %
% and-or-valeur gibt bei einer disjunktion bzw. konjunktion     %
% zweier formeln eine vereinfachte flache formel aus, die zur   %
% disjunktion bzw. konjunktion aequivalent ist.                 %
% (siehe kapitel 4 abschnitt 4.9)                               %
%                                                               %
% eingabe : eine liste der form list(elem,phi,psi)              %
%           wobei elem  = ' and  oder elem = 'or.               %
%                                                               %
% ausgabe : cons(elem,cons(phi,cdr psi) falls car psi = elem    %
%                  und not(car phi = elem) .                    %
%                                                               %
%           cons(elem,cons(psi,cdr phi) falls car phi = elem    %
%                  und not(car psi = elem).                     %
%                                                               %
%           appand(phi,cdr psi) falls car phi = car psi = elem. %
%                                                               %
%           phi    falls psi  leer ist.                         %
%                                                               %
%           psi    falls phi  leer ist.                         %
%                                                               %
%           list(elem,phi,psi) sonst                            %
%                                                               %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
 
symbolic procedure dqe_andorvaleur(phi);
begin scalar erg,hilf,hilff,andor;
erg := nil;andor := car phi; hilf := cadr phi; hilff:= caddr phi;
if hilf
   then
   <<if hilff
        then
        << if car hilf = andor
              and car hilff = andor
              then
              << hilf := reverse cdr hilf;
                 hilff := cdr hilff;
                 while hilf do
                 << hilff := dqe_consm(car hilf,hilff);
                    hilf  := cdr hilf >> ;
                 if not cdr hilff then erg := car hilff
                    else
                    erg := cons(andor,hilff) >>
              else
           if car hilf = andor
              then erg := dqe_modcons(hilff,hilf)
              else
           if car hilff = andor
              then erg := cons(andor,
                        dqe_consm(hilf,cdr hilff))
              else erg := phi >>
         else erg := hilf >>
   else  erg := hilff ;
 
return erg;
end;
 
 
 
 
%%%%%%%%%%%%%%    dqe_consm    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                               %
% durch dieser prozedur wird jedes element nur einmal in der    %
% liste eingetragen.                                            %
% falls es schon in der liste enthalten ist, so bleibt die liste%
% unveraendert. (siehe kapitel 4 abschnitt 4.9)                 %
%                                                               %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
 
symbolic procedure dqe_consm(elem,liste);
if elem member liste
   then liste
   else cons(elem,liste);
 
 
 
 
%%%%%%%%%%%%%%   dqe_modcons   %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                               %
% durch dieser prozedur wird jedes element nur einmel in der    %
% liste eingetragen.                                            %
% falls es schon in der liste enthalten ist, so bleibt die liste%
% unveraendert. sonst wird es an das ende der liste angehaengt. %
% (siehe kapitel 4 abschnitt 4.9)                               %
%                                                               %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
 
symbolic procedure dqe_modcons(elem,liste);
if elem member liste
   then liste
   else reverse cons(elem,reverse liste);

% part 3

%%%%%%%%%%%%%%%  dqe_makepositiveat  %%%%%%%%%%%%%%%%%%%%%%%%%%%%       %
%                                                               %
% diese prozedur wurde von k.d. burhenne uebernommen und ent-   %
% sprechend geandert. (siehe kapitel 3 abschnitt 3.1)           %
% dqe_makepositiveat berechnet bei eingabe einer negierten ato- %       %
% maren formel die entsprechende aequivalente positive formel.  %
%                                                               %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


symbolic procedure dqe_makepositiveat (phi);
   begin scalar psi;
      psi := cadr phi;
      return if car psi eq 'equal then
	 {'neq,cadr psi,caddr psi}
      else
	 {'equal,cadr psi,caddr psi}
   end;




%%%%%%%%%%%%%%%  dqe_makepositive   %%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                               %
% dqe_makepositive berechnet zu einer gegebenen formel die ent- %
% sprechend aequivalente positive formel.                       %
% die rechenvorschrift fuer diese berechnung wurde von          %
% k.d. burhenne uebernommen. anstelle der von burhenne verwen-  %
% verwendeten stack-verwaltung bei der programmierung wurde     %
% jedoch der rekursive programmierstil benutzt, d.h. die        %
% positive formel wird durch rekursion ueber den aufbau von     %
% formeln berechnet. (siehe kapitel 3 abschnitt 3.1)            %
%                                                               %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


symbolic procedure dqe_makepositive(formel);
begin scalar erg,hilfserg,hilf;
if (formel = t) or (not formel) then erg := formel
   else
if car formel='nott
   then
   << formel:=cadr formel;
      if formel = t then erg := nil
         else
      if formel = nil then erg := t
         else
      if car formel='nott
         then erg:=dqe_makepositive(cadr formel)
         else
      if car formel='ex
         then <<erg:=dqe_makepositive(list('nott,caddr formel));
                erg:=list('all,cadr formel,erg)>>
         else
      if car formel='all
         then <<erg:=dqe_makepositive(list('nott,caddr formel));
                erg:=list('ex,cadr formel,erg)>>
         else
      if car formel='and
         then <<hilf:=cdr formel;hilfserg:=nil;
                while hilf do
                <<hilfserg:= dqe_makepositive(list('nott,car hilf));
                  erg := cons(hilfserg,erg);
                  hilf:=cdr hilf >>;
                if cdr erg
                   then erg:=cons('or,reverse erg)>>
         else
      if car formel='or
         then <<hilf:=cdr formel;hilfserg:=nil;
                while hilf do
                <<hilfserg:= dqe_makepositive(list('nott,car hilf));
                  erg := cons(hilfserg,erg);
                  hilf:=cdr hilf >>;
                if cdr erg
                   then erg:=cons('and,reverse erg) >>
         else
         erg:=dqe_makepositiveat(list('nott,formel)) >>
    else
    <<
      if car formel='ex
         then <<erg:=dqe_makepositive(caddr formel);
                erg:=list('ex,cadr formel,erg)>>
         else
      if car formel='all
         then <<erg:=dqe_makepositive(caddr formel);
                erg:=list('all,cadr formel,erg)>>
         else
      if car formel='and
         then <<hilf:=cdr formel;hilfserg:=nil;
                while hilf do
                <<hilfserg:= dqe_makepositive(car hilf);
                  erg := cons(hilfserg,erg);
                  hilf:=cdr hilf >>;
                if cdr erg
                 then erg:=cons('and,reverse erg)>>
         else
      if car formel='or
         then <<hilf:=cdr formel;hilfserg:=nil;
                while hilf do
                <<hilfserg:= dqe_makepositive(car hilf);
                   erg := cons(hilfserg,erg);
                  hilf:=cdr hilf >>;
                if cdr erg
                   then erg:=cons('or,reverse erg) >>
         else erg:=formel >>;

return erg;
end;




%%%%%%%%%%%%%%%   dqe_interchange7   %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                   %
% dqe_interchange7 ist eine subroutine von  dqe_makeprenex und wurde%    %
% unveraendert von k.d. burhenne uebernommen.                       %
% sei  l =  (phi_1,...phi_n), wobei alle phi_j praenexe formeln     %
%            sind,                                                  %
%     ls aus and,or ,                                               %
%      a aus ex,all .                                               %
% dann ist dqe_interchange7(l,ls,a) ein paar (phi,qb) mit folgenden %
% eigenschaften:                                                    %
% 1. phi ist wieder praenex und aequivalent zu                      %
%   (phi_1 ls ... ls phi_n).                                        %
%   ferner ist fs(phi)=a, falls fs(phi_j)=a fuer ein j, d.h.        %
%   phi beginnt mit einem block von a-quantoren, falls moeglich.    %
% 2. qb=qb(phi).                                                    %
% die prozedur dqe_interchange7 hat die eigenschaft, dass eine der  %
% formeln dqe_interchange7(l,ls,ex), dqe_interchange7(l,ls,all) op- %   %
% mal bzgl. der anzahl der quantorenbloecke ist, falls dies fuer    %
% alle phi_j aus l schon der fall war.                              %
%                                                                   %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


symbolic procedure dqe_interchange7(l,ls,a);
    begin scalar qlist,hilf,phi,qb,qb1,weiter;
       qlist:=nil;weiter:=t;hilf:=nil; qb:=0;
       while l do << hilf:=cons(caar l,hilf); l:=cdr l >>;
       l:=hilf;
       while weiter do
         << weiter:=nil;hilf:=nil;qb1:=0;
            while l do
               << phi:=car l;l:=cdr l;
                  while car phi=a do
                     << qlist:=cons(list(car phi,cadr phi),qlist);
                        phi:=caddr phi;qb1:=qb1+1 >>;
                  hilf:=cons(phi,hilf) >>;
            l:=hilf;if qb1>0 then qb:=qb+1;
            if a='ex then a:='all else a:='ex;
            while hilf and not weiter do
               << if caar hilf='ex or caar hilf='all
                     then weiter:=t;
                   hilf:=cdr hilf >> >>;
       phi:=cons(ls,l);
       while qlist do << phi:=append(car qlist,list phi);
                         qlist:=cdr qlist >>;
     return list(phi,qb)
 end;


%%%%%%%%%%%%%%%    dqe_pnfquantor  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                               %
% soubroutine von  dqe_makeprenex, die unveraendert von         %
% k.d. burhenne uebernommen wurde.                              %
%  dqe_pnfquantor ist eine hilfsprozedur zur realisierung des   %
% rekursionsschritts fuer dqe_pnf(siehe dort),                  %
% der erforderlich wird, wenn die eingabe phi mit               %
% einem quantor beginnt, also etwa phi=ex(x,psi);               %
% pnfquantor(phi) berechnet zunaechst die menge m=pnf(psi<n/x>),%       ,%
% wobei n neuer identifikator ist, und berechnet daraus eine    %
% optimale formel, die auch im hoeheren kontext optimal ist.    %
% seiteneffekte:siehe unter dqe_pnf.                            %
%                                                               %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


symbolic procedure dqe_pnfquantor(phi);
   begin scalar erg,n,m,hilf,hilf1,z,dec;
      dec:=car phi;
      dqe_counter!*:=dqe_counter!*+1;z:=mkid('newid,dqe_counter!*);
      erg:=dqe_pnf subst(z,cadr phi,caddr phi);
      if cdr erg then
         <<n:=cadr car erg;m:=cadr cadr erg;
           if n<m then << hilf:=caar erg;hilf1:=list(dec,z,hilf);
                          if car hilf=dec then hilf1:=list(hilf1,n)
                                          else hilf1:=list(hilf1,n+1);
                          erg:=list hilf1 >>
                  else
           if n>m then << hilf:=caadr erg;hilf1:=list(dec,z,hilf);
                          if car hilf=dec then hilf1:=list(hilf,m)
                                          else hilf1:=list(hilf,m+1);
                          erg:=list hilf1  >>
                  else << hilf:=erg;
                          while hilf and caaar hilf neq dec do
                          hilf:=cdr hilf;
                          if hilf
                            then << hilf:=list(list(dec,z,caar hilf),n);
                                    erg:=list hilf >>

                            else << erg:=list(list(dec,z,caar erg),n+1);
                                    erg:=list erg >>  >> >>
                 else << if caaar erg neq dec then m:=cadar erg+1
                                              else m:=cadar erg;
                         erg:=list list(list(dec,z,caar erg),m) >>;

      return erg
   end;


%%%%%%%%%%    dqe_pnfjunktor   %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                               %
% soubroutine von  dqe_makeprenex, die unveraendert von         %
% k.d. burhenne uebernommen wurde.                              %
%  dqe_pnfjunktor ist eine hilfsprozedur zur realisierung des   %
% rekursionsschritts fuer dqe_pnf (siehe dort),                 %
% der erforderlich wird, wenn fuer die eingabe phi gilt:        %
% fs(phi) aus and,or, also etwa phi = phi_1 ls ... ls phi_n.    %
% pnfjunktor(phi) berechnet zunaechst die mengen m_j=pnf(psi_j) %        %
% und daraus das gewuenschte ergebnis.                          %
% seiteneffekte:siehe unter dqe_pnf.                            %
%                                                               %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


symbolic procedure dqe_pnfjunktor(phi);
   begin scalar erg,dec,hilf,hilf1,hilf2,psi,pair1,pair2,poss1,poss2,
         l1,l2,m,m1;
      dec:=car phi;m:=-1;poss1:=t;poss2:=t;hilf1:=nil;hilf2:=nil;
      hilf:=cdr phi;l1:=nil;l2:=nil;
      while hilf do << psi:=dqe_pnf car hilf;hilf:=cdr hilf;
                       hilf1:=cons(car psi,hilf1);
                       if cdr psi  then hilf2:=cons(cadr psi,hilf2)
                                   else hilf2:=cons(car psi,hilf2);
                       m1:=cadar psi;if m1>m then m:=m1 >>;
  if m>0 then
   << while hilf1 do
          << pair1:=car hilf1;pair2:=car hilf2;
             hilf1:=cdr hilf1;hilf2:=cdr hilf2;
             l1:=cons(pair1,l1);l2:=cons(pair2,l2);
             if cadr pair1=m and caar pair1 neq 'ex then poss1:=nil;
             if cadr pair2=m and caar pair2 neq 'all then poss2:=nil >>;
      if poss1 and not poss2
         then erg:=list dqe_interchange7(l1,dec,'ex)
         else if poss2 and not poss1
              then erg:=list dqe_interchange7(l2,dec,'all)
              else erg:=list(dqe_interchange7(l1,dec,'ex),
                             dqe_interchange7(l2,dec,'all)) >>
         else erg:=list list(phi,0); return erg
  end;


%%%%%%%%%%%%%%%   dqe_pnf       %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                %
% soubroutine von  dqe_makeprenex, die unveraendert von          %
% k.d. burhenne uebernommen wurde.                               %
% pnf(phi) berechnet eine ein-oder zweielementige menge m von    %
% praenexen formeln phi' derart,dass jede formel phi' in m aequi-%
% valent zu phi und optimal bzgl. der anzahl der quantorenbloecke%
% ist.in jedem fall ist eine der formeln aus m auch "im hoeheren %
% kontext" optimal.                                              %
% falls #m=2, so beginnt eine formel mit einem existenzquantor   %
% und eine mit einem allquantor. in der m darstellenden liste l  %
% ist dann car l die formel, die mit einem existenzquantor       %
% beginnt. die formeln werden so verwaltet, dass zusaetzlich die %
% anzahl der quantorenbloecke mitberechnet wird, d.h.            %
% pnf(phi) ist entweder von der form                             %
%            ( (phi_ex, qbex), (phi_all,qball)),                 %
%            wobei phi_ex,phi_all die optimalen formeln sind,    %
%            qbex=qb(phi_ex) , qball=qb(phi_all),                %
%            oder von der form ((phi',qb)), wobei qb=qb(phi').   %
% verfahren : rekursion ueber den aufbau von phi.                %
%             falls phi atomar ist, wird ((phi,0)) ausgegeben.   %
%             ansonsten wird eine der prozeduren qnaquantor      %
%             oder qnajunktor aufgerufen, die die entsprechenden %
%             rekursionsschritte(--> zunaechst rekursiver auf-   %
%             ruf von dqe_pnf) unter beibehaltung der            %
%             optimalitaet realisieren.                          %
% fuer die umbenennung von  variablen greift dqe_pnf ueber qna-  %
% quantor auf eine relativ globale variable counter zu. daraus   %
% ergibt sich ein seiteneffekt an dieser variable.               %
% dieser effekt ist jedoch unproblematisch, da pnf nur hilfs-    %
% prozedur fuer die prozedur  dqe_makeprenex ist, in der die     %
% variable counter deklariert ist. letztere prozedur (nur diese  %
% wird fuer weitere berechnungen verwendet) arbeitet ohne sei-   %
% teneffekte.                                                    %
%                                                                %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


symbolic procedure dqe_pnf(phi);
   begin scalar dec,erg;
      dec:=car phi;
      if dec='ex or dec='all then erg:=dqe_pnfquantor phi
                             else
      if dec='or or dec='and then erg:=dqe_pnfjunktor phi
                             else erg:=list list(phi,0);
      return erg;
   end;


%%%%%%%%%%%%%%    dqe_makeprenex     %%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                               %
%  dqe_makeprenex berechnet zu einer gegebenen positiven formel %
% eine aequivalente praenexe formel, die optimal ist bzgl. der  %
% anzahl der quantorenbloecke.                                  %
% diese prozedur wurde unveraendert von k.d. burhenne ueber-    %
% nommen. (siehe auch kapitel 3)                                %
%                                                               %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


symbolic procedure dqe_makeprenex(phi);
   begin scalar erg;
      dqe_counter!*:=0;erg:=dqe_pnf phi;
      if cdr erg then << if cadr car erg<= cadr cadr erg
                            then erg:=caar erg
                            else erg:=caadr erg >>
                 else erg:=caar erg;
      return erg
end;




%%%%%%%%%%%%%%%    dqe_pnfquantormod  %%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                               %
% pnfquantormod ist eine subroutine fuer pnfmod. sie arbeitet   %        %
% wie  dqe_pnfquantor (siehe kapitel 3 abschnitt 3.2).          %
%                                                               %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


symbolic procedure dqe_pnfquantormod(phi,liste);
   begin scalar erg,n,m,hilf,hilf1,z,dec;
      dec:=car phi;
      dqe_counter!*:=dqe_counter!*+1;z:=mkid('newid,dqe_counter!*);
      liste := cons(cadr phi,cons(z,liste));
      erg:= dqe_pnfmod(subst(z,cadr phi,caddr phi),liste);
      liste := cadr erg;
      erg := car erg;
      if cdr erg then
         <<n:=cadr car erg;m:=cadr cadr erg;
           if n<m then << hilf:=caar erg;hilf1:=list(dec,z,hilf);
                          if car hilf=dec then hilf1:=list(hilf1,n)
                                          else hilf1:=list(hilf1,n+1);
                          erg:=list hilf1 >>
                  else
           if n>m then << hilf:=caadr erg;hilf1:=list(dec,z,hilf);
                          if car hilf=dec then hilf1:=list(hilf,m)
                                          else hilf1:=list(hilf,m+1);
                          erg:=list hilf1  >>
                  else << hilf:=erg;
                          while hilf and caaar hilf neq dec do
                          hilf:=cdr hilf;
                          if hilf
                            then << hilf:=list(list(dec,z,caar hilf),n);
                                    erg:=list hilf >>

                            else << erg:=list(list(dec,z,caar erg),n+1);
                                    erg:=list erg >>  >> >>
                 else << if caaar erg neq dec then m:=cadar erg+1
                                              else m:=cadar erg;
                         erg:=list list(list(dec,z,caar erg),m) >>;

      return list(erg,liste);
   end;


%%%%%%%%%%    dqe_pnfjunktormod   %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                               %
%pnfjunktormod ist eine subroutine fuer dqe_pnfmod. sie arbeitet%        %
% wie  dqe_pnfjunktor (siehe kapitel 3 abschnitt 3.2).          %
%                                                               %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


symbolic procedure dqe_pnfjunktormod(phi,liste);
   begin scalar erg,dec,hilf,hilf1,hilf2,psi,pair1,pair2,poss1,poss2,
         l1,l2,m,m1;
      dec:=car phi;m:=-1;poss1:=t;poss2:=t;hilf1:=nil;hilf2:=nil;
      hilf:=cdr phi;l1:=nil;l2:=nil;
      while hilf do << psi:=dqe_pnfmod(car hilf,liste);
                       liste := cadr psi;
                       psi := car psi;
                       hilf:=cdr hilf;
                       hilf1:=cons(car psi,hilf1);
                       if cdr psi  then hilf2:=cons(cadr psi,hilf2)
                                   else hilf2:=cons(car psi,hilf2);
                       m1:=cadar psi;if m1>m then m:=m1 >>;
  if m>0 then
   << while hilf1 do
          << pair1:=car hilf1;pair2:=car hilf2;
             hilf1:=cdr hilf1;hilf2:=cdr hilf2;
             l1:=cons(pair1,l1);l2:=cons(pair2,l2);
             if cadr pair1=m and caar pair1 neq 'ex then poss1:=nil;
             if cadr pair2=m and caar pair2 neq 'all then poss2:=nil >>;
      if poss1 and not poss2
         then erg:=list(list dqe_interchange7(l1,dec,'ex),liste)
         else if poss2 and not poss1
              then erg:=list(list(dqe_interchange7(l2,dec,'all)),liste)
              else erg:=list(list(dqe_interchange7(l1,dec,'ex),
                             dqe_interchange7(l2,dec,'all)),liste) >>
         else erg:=list(list(list(phi,0)),liste); return erg
  end;


%%%%%%%%%%%%%%%   dqe_pnfmod    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                               %
%pnfmod ist eine subroutine fuer  makeprenexmod. sie arbeitet   %        %
% wie dqe_pnf (siehe kapitel 3 abschnitt 3.2).                  %
%                                                               %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


symbolic procedure dqe_pnfmod(phi,liste);
   begin scalar dec,erg;
      dec:=car phi;
      if dec='ex or dec='all then erg:=dqe_pnfquantormod(phi,liste)
                             else
      if dec='or or dec='and then erg:=dqe_pnfjunktormod(phi,liste)
                             else erg:=list(list(list(phi,0)),liste);
      return erg;
   end;


%%%%%%%%%%   dqe_makeprenexmod  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                               %
% makeprenexopt arbeitet genau wie makeprenex. sie berechnet zu %       u %
% einer gegebnen positeven formel die selbe  aequivalente prae- %
% nexe formel wie bei  dqe_makeprenex.                          %
% sie berechnetet noch dazu die up-dating der liste diffequa-   %
% liste (siehe auch kapitel 3 abschnitt 3.2).                   %
%                                                               %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


symbolic procedure dqe_makeprenexmod(phi,diffequaliste);
   begin scalar erg,hilfliste,liste,ausg;
         scalar var,newvar,hilf;
      ausg := nil;
      dqe_counter!*:=0;
      liste := nil;
      hilfliste := diffequaliste;
      erg:=dqe_pnfmod(phi,liste);
      liste := cadr erg;
      erg := car erg;
      if cdr erg then << if cadr car erg<= cadr cadr erg
                            then erg:=caar erg
                            else erg:=caadr erg >>
                 else erg:=caar erg;
      while liste do
      << var := car liste;
         newvar := cadr liste;
         liste := cddr liste;
         hilfliste := subst(newvar,var,hilfliste) >>;

      while hilfliste do
      << var := car hilfliste;
         hilf := cadr hilfliste;
         hilfliste := cddr hilfliste;
         if not(var member diffequaliste)
            then diffequaliste := cons(var,
                  cons(hilf,diffequaliste)) >>;
      ausg := list(erg,diffequaliste);
      return ausg;
   end;


%%%%%%%%%%%%%%%    dqe_disjnf    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                               %
% dqe_dnfjnf berechnet eine disjunktive normalform einer positi-%
% ven quantorenfreien formel.                                   %
%             (siehe kapitel 3 abschnitt 3.3)                   %
% vorgehen:                                                     %
%    1.: formel = t oder nil  --> stop                          %
%    2.: formel = (and ...)   --> aufruf dqe_distributiv formel %
%    3.: formel = (or ...)    --> fuer alle teilformeln         %
%                                 aufruf dqe_disjnf             %
%                                                               %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


symbolic procedure dqe_disjnf(formel);
begin scalar erg,hilf;
erg := nil;
if (formel = t) or (not formel)
   or dqe_isatomarp(formel)
   then erg := formel
   else
if car formel ='and
   then  erg :=  dqe_distributiv(formel)
   else
if car formel ='or
   then
   << formel := cdr formel;
      while formel do
      << hilf := car formel; formel := cdr formel;
         hilf := dqe_disjnf(hilf);
         if (hilf = t) or (not hilf)
                       or
            dqe_isatomarp(hilf) or (car hilf = 'and)
            then
            << if not erg then erg := list(hilf)
                  else
               if not cdr(erg)
                  then
                  << if not(hilf = car erg)
                        then erg := list('or,
                             car erg,hilf) >>
                  else erg := dqe_modcons(hilf,erg) >>

          else
          << if length erg = 1
                then erg := car erg;
             erg := dqe_andorvaleur
                 list('or,erg,hilf) >> >>;
      if length erg = 1 then erg := car erg>>
   else erg := formel;

if !*dqeoptsimp then erg := dqe_dknfsimplify(erg);
return erg;
end;




%%%%%%%%%%%%%%%    dqe_konjnf  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                               %
% dqe_konjnf berechnet eine konjunktive normalform einer positi-%
% ven quantorenfreien formel.                                   %
%                    (siehe auch kapitel 3 abschnitt 3.3)       %
% vorgehen:                                                     %
%    1.: formel = t oder nil  --> stop                          %
%    2.: formel = (or ...)    --> aufruf dqe_distributiv formel %
%    3.: formel = (and ...)   --> fuer alle teilformeln         %
%                                 aufruf dqe_konjnf             %
%                                                               %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


symbolic procedure dqe_konjnf(formel);
begin scalar erg,hilf;
erg := nil;
if (formel = t) or (not formel)
   or dqe_isatomarp(formel)
   then erg := formel
   else
if car formel ='or
   then  erg :=  dqe_distributiv(formel)
   else
if car formel ='and
   then
   << formel := cdr formel;
      while formel do
      << hilf := car formel; formel := cdr formel;
         hilf := dqe_konjnf(hilf);
         if (hilf = t) or (not hilf)
                       or
            dqe_isatomarp(hilf) or (car hilf = 'or)
            then
            << if not erg then erg := list(hilf)
                  else
               if not cdr(erg)
                  then
                  << if not(hilf = car erg)
                        then erg := list('and,
                             car erg,hilf) >>
                  else erg := dqe_modcons(hilf,erg) >>

          else
          << if length erg = 1
                then erg := car erg;
             erg := dqe_andorvaleur
                 list('and,erg,hilf) >> >>;
      if length erg = 1 then erg := car erg>>
   else erg := formel;

if !*dqeoptsimp
   then  erg := dqe_dknfsimplify(erg);

return erg;
end;




%%%%%%%%%%%%%%%     dqe_distributiv   %%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                               %
% sub-routine von dqe_disjnf und dqe_konjnf zur anwendung der   %       %
% distributivgesetze.                                           %
%        (siehe auch kapitel 3  abschnitt 3.3)                  %
% vorgehen:                                                     %
% 1.fall: eingabe: (or ...)                                     %
%         ausgabe: (and phi_1 phi_2 ...) ,                      %
%                 wobei phi_1, phi_2, ... keine and's enthalten.%
% 2.fall: eingabe: (and ...)                                    %
%         ausgabe: (or phi_1 phi_2 ...) ,                       %
%                  wobei phi_1, phi_2, ... keine or's enthalten.%
%                                                               %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


symbolic procedure  dqe_distributiv(formel);
begin scalar symb1,symb2,ausg,hilf1,hilf2,hilf,hilf3,hilff;
symb1 := car formel; ausg := nil;

if symb1='or
   then symb2 := 'and
   else symb2 := 'or;
formel := cdr formel;

while formel do
<< hilf := car formel; formel := cdr formel;
   if (hilf = t) or not(hilf)
        or dqe_isatomarp(hilf)
      then
      << if not ausg
            then ausg := cons(hilf,ausg)
            else
         if not cdr ausg
            then
            << hilf1 := car ausg;
               if not( hilf = hilf1)
                  then ausg := list(symb1,hilf1,hilf) >>
            else
         if car ausg = symb1
            then ausg := dqe_modcons(hilf,ausg)
            else
            << hilf1 := cdr ausg; ausg := nil;
               while hilf1 do
               << hilf2 := car hilf1;
                  hilf1 := cdr hilf1;
                  if (hilf2 = t) or not hilf2
                      or dqe_isatomarp(hilf2)
                      then
                      <<if not( hilf2 = hilf1)
                           then hilf2 := list(symb1,hilf2,hilf) >>
                      else hilf2 := dqe_modcons(hilf,hilf2);
                  ausg := dqe_modcons(hilf2,ausg) >>;
               if cdr ausg
                  then ausg := cons(symb2,ausg) >> >>

      else
   if car hilf = symb1
      then
      << hilf :=  dqe_distributiv(hilf);
        if (hilf = t) or not(hilf)
           or dqe_isatomarp(hilf)
           then
           <<if not ausg
                then ausg := cons(hilf,ausg)
                else
             if not cdr ausg
                then
                 <<hilf1 := car ausg;
                   if not( hilf = hilf1)
                      then ausg := list(symb1,hilf1,hilf) >>
                      else
                   if car ausg = symb1
                      then ausg := dqe_modcons(hilf,ausg)
                      else
                      <<hilf1 := cdr ausg; ausg := nil;
                        while hilf1 do
                        <<hilf2 := car hilf1;
                          hilf1 := cdr hilf1;
                          if (hilf2 = t) or not hilf2
                             or dqe_isatomarp(hilf2)
                             then
                             <<if not( hilf2 = hilf1)
                                  then hilf2 :=
                                    list(symb1,hilf2,hilf) >>
                             else hilf2 :=
                                  dqe_modcons(hilf,hilf2);
                          ausg := dqe_modcons(hilf2,ausg) >>;
                        if cdr ausg
                           then ausg := cons(symb2,ausg)>> >>
            else
         if car hilf = symb1
            then
            << if not ausg
                  then ausg := hilf
                  else
               if not cdr ausg
                  then ausg := cons(symb1,dqe_consm(car ausg,cdr hilf))
                  else
               if car ausg = symb1
                  then  ausg := dqe_andorvaleur
                        list(symb1,ausg,hilf)
                  else
                  << hilf1 := cdr ausg; ausg := nil;
                     while hilf1 do
                     << hilf2 := car hilf1; hilf1 := cdr hilf1;
                        if (hilf2 = t) or (not hilf2)
                            or dqe_isatomarp(hilf2)
                            then hilf2 := list(symb1,
                                      dqe_consm(hilf2,cdr hilf))
                            else hilf2 := dqe_andorvaleur
                                  list(symb1,hilf2,hilf);
                        ausg := dqe_modcons(hilf2,ausg) >>;
                     if cdr ausg
                        then ausg := cons(symb2,ausg) >> >>
            else
            << if not ausg
                  then ausg := hilf
                  else
               if not cdr ausg
                  then
                  << hilf1 := car ausg; ausg := nil;
                     hilf  := cdr hilf;
                     while hilf do
                     << hilf2 := car hilf; hilf := cdr hilf;
                        if (hilf2 = t) or (not hilf2)
                           or dqe_isatomarp hilf2
                           then
                           <<if not(hilf1 = hilf2)
                                then hilf2 := list(symb1,hilf1,hilf2)>>
                           else
                        hilf2 := cons(symb1,dqe_consm(hilf1,cdr hilf2));
                        ausg := dqe_modcons(hilf2,ausg) >>;
                     if cdr ausg
                        then ausg := cons(symb2,ausg)>>
                  else
               if car ausg = symb2
                  then
                  << hilf1 := cdr ausg; ausg := nil;
                     while hilf1 do
                     << hilf2 := car hilf1; hilf1 := cdr hilf1;
                        hilff := cdr hilf;
                        while hilff  do
                        << hilf3 := car hilff; hilff := cdr hilff;
                          if (hilf2 = t) or (not hilf2)
                             or dqe_isatomarp hilf2
                             then
                             <<if (hilf3 = t) or (not hilf3)
                                 or dqe_isatomarp hilf3
                                 then
                                 << if not(hilf3 = hilf2)
                                       then hilf3 := list(symb1,
                                               hilf2,hilf3) >>
                                 else
                                 << hilf3 := dqe_consm(hilf2,cdr hilf3);
                                    hilf3 := cons(symb1,hilf3) >> >>
                             else
                             <<if (hilf3 = t) or (not hilf3)
                                 or dqe_isatomarp hilf3
                                 then
                                  hilf3 := dqe_modcons(hilf3,hilf2)
                                 else hilf3 := dqe_andorvaleur
                                   list(symb1,hilf2,hilf3) >>;
                          ausg := dqe_modcons(hilf3,ausg) >> >>;
                    if cdr ausg
                       then ausg := cons(symb2,ausg) >>

                  else
                  << hilf := cdr hilf;
                     hilf1 := ausg; ausg := nil;
                     while hilf do
                     << hilf2 := car hilf; hilf := cdr hilf;
                        if (hilf2 = t) or (not hilf2)
                           or dqe_isatomarp hilf2
                           then
                            hilf2 := dqe_modcons(hilf2,hilf1)
                           else hilf2 := dqe_andorvaleur
                              list(symb1,hilf1,hilf2);
                        ausg := dqe_modcons(hilf2,ausg) >>;
                     if cdr ausg
                        then ausg := cons(symb2,ausg) >> >> >>

      else
      << if symb2 = 'or
            then hilf := dqe_disjnf(hilf)
            else hilf := dqe_konjnf(hilf);

        if (hilf = t) or not(hilf)
           or dqe_isatomarp(hilf)
           then
           <<if not ausg
                then ausg := cons(hilf,ausg)
                else
             if not cdr ausg
                then
                <<hilf1 := car ausg;
                  if not( hilf = hilf1)
                     then ausg := list(symb1,hilf1,hilf) >>
                else
             if car ausg = symb1
                then ausg := dqe_modcons(hilf,ausg)
                else
                <<hilf1 := cdr ausg; ausg := nil;
                  while hilf1 do
                  <<hilf2 := car hilf1;
                    hilf1 := cdr hilf1;
                    if (hilf2 = t) or not hilf2
                       or dqe_isatomarp(hilf2)
                       then
                       <<if not( hilf2 = hilf1)
                            then hilf2 :=
                              list(symb1,hilf2,hilf) >>
                       else hilf2 :=
                            dqe_modcons(hilf,hilf2);
                    ausg := dqe_modcons(hilf2,ausg) >>;
                  if cdr ausg
                     then ausg := cons(symb2,ausg)>> >>
            else
         if car hilf = symb2
            then
            << if not ausg
                  then ausg := hilf
                  else
               if not cdr ausg
                  then
                  << hilf1 := car ausg; ausg := nil;
                     hilf := cdr hilf;
                     while hilf do
                     << hilf2 := car hilf; hilf := cdr hilf;
                        if (hilf2 = t) or (not hilf2)
                            or dqe_isatomarp(hilf2)
                            then
                            << if not(hilf2 = hilf1)
                                  then hilf2 := list(symb1,
                                               hilf1,hilf2)>>
                            else hilf2 := cons(symb1,
                                 dqe_consm(hilf1,cdr hilf2));
                        ausg := dqe_modcons(hilf2,ausg) >>;
                     if cdr ausg
                        then ausg := cons(symb2,ausg) >>
                  else
               if car ausg = symb2
                  then
                  << hilf1 := cdr ausg; ausg := nil;
                     while hilf1 do
                     << hilf2 := car hilf1; hilf1 := cdr hilf1;
                        hilff := cdr hilf;
                        while hilff  do
                        << hilf3 := car hilff; hilff := cdr hilff;
                          if (hilf2 = t) or (not hilf2)
                             or dqe_isatomarp hilf2
                             then
                             <<if (hilf3 = t) or (not hilf3)
                                 or dqe_isatomarp hilf3
                                 then
                                 << if not(hilf2 = hilf3)
                                       then hilf3 := list(symb1,
                                               hilf2,hilf3) >>
                                 else
                                 << hilf3 :=dqe_consm(hilf2,cdr hilf3);
                                    hilf3 := cons(symb1,hilf3) >> >>
                             else
                             <<if (hilf3 = t) or (not hilf3)
                                 or dqe_isatomarp hilf3
                                 then
                                   hilf3 := dqe_modcons(hilf3,hilf2)
                                 else hilf3 := dqe_andorvaleur
                                       list(symb1,hilf2,hilf3) >>;
                          ausg := dqe_modcons(hilf3,ausg) >> >>;
                    if cdr ausg
                       then ausg := cons(symb2, ausg) >>
                  else
                  << hilf1 := ausg; ausg := nil;
                     hilf := cdr hilf;
                     while hilf do
                     << hilf2 := car hilf; hilf := cdr hilf;
                        if (hilf2 = t) or (not hilf2)
                             or dqe_isatomarp(hilf2)
                           then hilf2 := dqe_modcons(hilf2,hilf1)
                           else hilf2 := dqe_andorvaleur
                                          list(symb1,hilf1,hilf2);
                        ausg := dqe_modcons(hilf2,ausg) >>;
                     if cdr ausg
                        then ausg := cons(symb2,ausg) >> >>

             else  %car hilf = symb1%
             <<if not ausg
                  then ausg := hilf
                  else
               if not cdr ausg
                  then ausg := cons(symb1,dqe_consm(car ausg,cdr hilf))
                  else
               if car ausg = symb1
                  then ausg := dqe_andorvaleur
                              list(symb1,ausg,hilf)
                  else
                  << hilf1 := cdr ausg; ausg := nil;
                     while hilf1 do
                     << hilf2 := car hilf1; hilf1 := cdr hilf1;
                        if (hilf2 = t) or (not hilf2)
                            or dqe_isatomarp(hilf2)
                            then hilf2 := cons(symb1,
                                   dqe_consm(hilf2,cdr hilf))
                            else hilf2 :=
                         dqe_andorvaleur list(symb1,hilf2,hilf);
                        ausg := dqe_modcons(hilf2,ausg) >>;
                     if cdr ausg
                        then ausg := cons(symb2,ausg) >> >>
                 >> >>;

if length ausg = 1
   then ausg := car ausg;

return ausg;
end;





%%%%%%%%%%%%%%%  dqe_simplifyat  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                               %
% diese prozedur wurde von k.d. burhenne uebernommen und ent-   %
% sprechend den hier auftretenden atomaren formeln angepasst.   %
% dqe_simplifyat versucht eine atomare formel aussagenlogisch zu%
% vereinfachen, d.h. es wird versucht die atomare formel, falls %
% moeglich zu true oder false auszuwerten. (siehe abschnitt 3.4)%
%                                                               %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


 symbolic procedure dqe_simplifyat(phi);
 begin scalar diff,erg,hilf,liste;
 if (atom phi) or (not phi)
    then erg:=phi
    else
    << diff:= cadr phi;
       if dqe_isconstant diff
          then erg:= eval list(car phi,diff,0)
          else
       if listp diff
          then
          << if car diff ='minus  or  car diff = 'expt
                then
                << diff := cadr diff;
                   erg := dqe_simplifyat(list(car phi,diff,0))>>
                else
             if car diff ='times
                then
                << diff := cdr diff;
                   while diff do
                   << hilf := car diff;
                      if not(dqe_isconstant hilf)
                         then liste := dqe_consm(hilf,liste);
                      diff := cdr diff >>;
                   if not liste
                      then erg := eval list(car phi,1,0)
                      else
                   if not cdr liste
                      then erg := list(car phi,car liste,0)
                      else
                      << while liste do
                         << hilf := car liste; liste := cdr liste;
                            hilf :=dqe_simplifyat(list(car phi,hilf,0));;
                             erg := dqe_modcons(hilf,erg) >>;
                         if not cdr erg then erg := car erg
                            else
                         if car phi = 'neq
                            then erg := cons('and,erg)
                            else erg := cons('or,erg) >> >>
                else
             if car diff = 'plus
                then
                << hilf := qe92_lin_normcontent diff;
                   if not( hilf = 1)
                      then diff := reval list('quotient,diff, hilf);

                   if minusf numr simp diff
                      then diff := reval list('minus,diff);
                   erg := list(car phi,diff,0) >>

                else  erg := list(car phi,diff,0)  >>
          else erg := phi>>;
return erg;
end;




%%%%%%%%%%%%%%%  dqe_simplify    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                               %
% dqe_simplify vereinfacht eine positive quantorenfreie formel  %
% mit abstuetzung auf dqe_simplifyat durch rekursion ueber den  %
% aufbau der formel.                                            %
% diese prozedur wurde von k.d. burhenne uebernommen und        %
% entsprechend geaendert. (siehe kapitel 3 abschnitt 3.4)       %
%                                                               %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


symbolic procedure dqe_simplify(phi);
begin scalar erg,hilf,erghilf,weiter;
if (phi = t) or (not phi)
   then erg := phi
   else
if car phi ='and
   then
   << weiter:=t;hilf:=cdr phi;erg:=nil;
      while weiter and hilf do
      << erghilf:=dqe_simplify car hilf;hilf:=cdr hilf;
         if erghilf=nil
            then weiter:=nil
            else
         if erghilf neq t
            then erg:= dqe_modcons(erghilf,erg) >>;

      if weiter=nil then erg:= nil
         else
      if not erg then erg:= t
         else
      if cdr erg
         then erg:=cons('and, erg)
         else erg:=car erg  >>
   else
if car phi ='or
   then
   << weiter:=t;hilf:=cdr phi;erg:=nil;
       while weiter and hilf do
       << erghilf:=dqe_simplify car hilf;hilf:=cdr hilf;
          if erghilf=t
             then weiter:=nil
             else
          if erghilf neq  nil
             then erg:=dqe_modcons(erghilf,erg) >>;

       if weiter=nil then erg:= t
          else
       if not erg  then erg:= nil
          else
       if cdr erg then erg:=cons('or, erg)
          else erg:=car erg  >>

   else erg:=dqe_simplifyat phi ;

if !*dqeoptsimp
   then erg := dqe_helpsimplify(erg);
return erg ;
end;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                             %
% die folgenden prozeduren wurden unveraendert aus der arbeit %
% qe92 von t. sturm uebernommen.                              %
% die procedur qe92_lin_normconten berechnet den zahligen ggt.%
% aller monomen eines gegebenen polynomes.                    %
%                                                             %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

symbolic procedure qe92_lin_normcontent u;
prepf qe92_lin_normcontent1(numr simp u,nil);

symbolic procedure qe92_lin_normcontent1(u,g);
% g is the gcd collected so far.
if g = 1 then g
else if domainp u then gcdf(absf u,g)
else qe92_lin_normcontent1(red u,qe92_lin_normcontent1(lc u,g));

% part 4

%%%%%%%%%%%%%%%%  dqe_helpremainder  %%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                              %
% dqe_helpremainder ist eine hilfsprozedur fuer dqe_restfkt.sie%
% ist eine umbennenung fuer die reduce-funktion remainder, die %
% nur im algebraischen modus verwendet werden kann.            %
%                                                              %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
algebraic procedure dqe_helpremainder(phi,psi,var);
begin scalar erg;
korder var;
erg := remainder(phi,psi);
return erg;
end;
 
 
 
%%%%%%%%%%%%%%%%% dqe_ helpcoeff  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                              %
% dqe_helpcoeff ist eine hilfsprozedur fuer dqe_koeff. sie ist %
% eine umbennenung der reduce-funktion coeff, die nur im alge- %
% braischen modus verwendet werden kann.                       %
%                                                              %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
algebraic procedure dqe_helpcoeff(phi,var);
begin scalar erg;
erg := coeff(phi,var);
return erg;
end;
 
 
 
 
%%%%%%%%%%%%%%%%%%%% dqe_koeff  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                 %
%dqe_koeff ist eine hilfsprozedur fuer dqe_termcoefkt.sie bestimmt%
% die liste der koeffizienten eines differentialpolynoms phi      %
% bzgl. der variable var.sie verwendet die hilfsprozedur dqe_help-%
% coeff. (siehe kapitel 4 abschnitt 4.5)                          %
%                                                                 %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
 
symbolic procedure dqe_koeff(phi,var);
begin scalar erg;
erg := cdr reval dqe_helpcoeff(phi,var);
return erg;
end;
 
 
 
 
%%%%%%%%%%%%%%%%%%%%  dqe_restfkt   %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                   %
%dqe_restfkt ist eine hilfsprozedur fuer dqe_termcoefkt.sie bestimmt%
% den rest der divition eines differentialpolynoms phi durch        %
% ein differentialpolynom psi bzgl. der variable var. sie verwendet %
% die hilfsprozedur dqe_helpremainder.                              %
% (siehe kapitel 4 abschnitt 4.5)                                   %
%                                                                   %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
 
symbolic procedure dqe_restfkt(phi,psi,var);
begin scalar erg;
erg := dqe_pform dqe_helpremainder(phi,psi,var);
if not erg then erg := 0;
return erg;
end;
 
 
 
 
%%%%%%%%%%%%%%%%%  dqe_pseudf  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                 %
%dqe_pseudf ist eine hilfsprozedur fuer dqe_partialdf.sie bestimmt%
% die normale partialableitung eines differentialpolynoms phi     %
% bzgl. der variable var.sie verwendet die hilfsprozedur dqe_help-%
% df. (siehe kapitel 4 abschnitt 4.2)                             %
%                                                                 %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
 
symbolic procedure dqe_pseudf(phi,var);
reval {'df,phi,var};
 
 
 
 
%%%%%%%%%%%%%%%%%%   dqe_varmengefkt    %%%%%%%%%%%%%%%%%%%%%%%%
%                                                              %
% varmengefkt berechnet die menge aller im differentialpolynom %
% vorkommenden variablen. (siehe kapitel 4 abschnitt 4.2)      %
%                                                              %
% eingabe : ein differentialpolynom phi.                       %
%                                                              %
% ausgabe : eine liste der allen variablen, die in phi         %
%           vorkommen .                                        %
%                                                              %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
 
symbolic procedure dqe_varmengefkt(phi);
begin scalar varmenge,hilf,elem,hilfmenge;
hilf     := phi;
varmenge := nil;
if atom hilf
   then << if not dqe_isconstant hilf
              then  varmenge := list(hilf)>>
   else
if car hilf = 'd
   then varmenge := list(hilf)
   else
   <<while hilf do
     << elem := car hilf;
        hilf := cdr hilf;
        if atom elem
           then
           << if not(elem ='plus or elem ='times or elem ='expt
                      or elem ='minus or dqe_isconstant elem )
                 then varmenge := dqe_modcons(elem,varmenge)>>
           else
           << hilfmenge := dqe_varmengefkt(elem);
              while hilfmenge do
              << varmenge  := dqe_modcons(car hilfmenge,varmenge);
                 hilfmenge := cdr hilfmenge >>  >> >> >>;
 
return varmenge;
end;
 
 
 
 
%%%%%%%%%%%%%%%%%%%% dqe_partieldf  %%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                              %
% die prozedur dqe_partieldf berechnet die partielle ableitung %
% von phi bezueglich der variable var .                        %
% die liste diffequaliste ist leer oder sie besteht aus einer  %
% liste von der form list(var_1,var_1',var_2,var_2',...) wobei %
% die ableitung von var_k gleich var_k' ist.                   %
% (siehe kapitel 4 abschnitt 4.2)                              %
%                                                              %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
 
symbolic procedure dqe_partieldf(phi,var,diffequaliste);
begin scalar hilf,liste,ausg;
ausg := 0;
hilf := dqe_pseudf(phi,var);
if not(var member diffequaliste)
   then
   << if atom var
         then  ausg := reval list('times,hilf,list('d,var,1))
         else  ausg := reval list('times,hilf,list('d,cadr var,
                          eval list('plus,caddr var,1))) >>
   else
   << liste := diffequaliste;
      while not(var = car liste) do << liste := cddr liste >>;
      if cadr liste = 0
         then ausg := 0
         else ausg := reval list('times,hilf,cadr liste) >>;
 
return ausg;
end;
 
 
 
 
%%%%%%%%%%%%%%%%    dqe_diffkt    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                              %
%dqe_diffkt berechnet die erste ableitung des differentialpoly-%
% nomes phi. sie ist eine sub-routine von dqe_diff.            %
% (siehe kapitel 4 abschnitt 4.2)                              %
%                                                              %
% eingabe : phi und diffequaliste.                             %
% ausgabe : die ableitung  von phi .                           %
%                                                              %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
 
symbolic procedure dqe_diffkt(phi,diffequaliste);
begin scalar var,varmenge,hilf,erg;
erg := nil;
varmenge := dqe_varmengefkt(phi);
while varmenge do
<< var      := car varmenge;
   varmenge := cdr varmenge;
   hilf     := dqe_partieldf(phi,var,diffequaliste);
   if not(hilf = 0)
      then erg := cons(hilf,erg)  >>;
if not erg
   then erg := 0
   else
if not cdr erg
   then erg := car erg
   else erg := reval cons('plus,erg);
 
return erg ;
end;
 
 
 
 
%%%%%%%%%%%%%%%%%%    dqe_diff  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                              %
% die prozedur dqe_diff berechnet die n_te ableitung des diffe-%
% rentialpolynoms phi. (siehe kapitel 4 abschnitt 4.2)         %
%                                                              %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
 
symbolic procedure dqe_diff(phi,const,diffequaliste);
begin scalar hilf, erg;
erg  := phi;
hilf := 1;
while const >= hilf  do
<< erg  := dqe_diffkt(erg,diffequaliste);
   hilf := hilf +1 >>;
 
return erg;
end;
 
 
 
 
%%%%%%%%%%%%%%%%   dqe_termcoefkt  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                               %
% dqe_termcoefkt bestimmt die liste von koeffizienten der terme %
% eines differentialpolynoms bzgl. der variable var.            %
% (siehe kapitel 4 abschnitt 4.5)                               %
%                                                               %
% eingabe : ein differentialpolynom phi und eine variable var . %
%                                                               %
% ausgabe : list(c1,c2,...,cn) wobei phi =c1*t1+c2*t2+...+cn*tn %
%           und die ti's  die terme von phi sind.               %
%                                                               %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
 
symbolic procedure dqe_termcoefkt(phi,var);
begin scalar hilf,ordc,rest,const,erg,ausg;
ausg  := nil;
ordc  := dqe_ord(phi,var);
rest  := dqe_restfkt(phi,var,var);
const := 1;
if not(ordc = 0) and not(rest = 0)
   then
   while const <= ordc  do
   << rest :=dqe_restfkt(rest,list('d,var,const),list('d,var,const));
      if rest = 0
         then  const := ordc + 1
         else  const := const + 1 >> ;
 
hilf  := reval list('difference,phi,rest);
hilf  := dqe_koeff(hilf,var);
const := 1;
 
while const <= ordc  do
<< while hilf do
   << if not(car hilf = 0)
         then << erg := dqe_koeff(car hilf,list('d,var,const));
                ausg := append(ausg,erg) >>;
      hilf := cdr hilf  >>;
   hilf  := ausg;
   ausg  := nil;
   const := const + 1>>;
 
while hilf do
<< if not(car hilf = 0)
      then  ausg := dqe_modcons(car hilf,ausg);
   hilf := cdr hilf >>;
 
ausg := cons(rest,ausg);
 
return ausg;
end;
 
 
 
 
%%%%%%%%%%%%%%%%%   dqe_helpord      %%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                              %
% dqe_helpord ist eine hilfsprozedur fuer dqe_ord.sie berechnet%
% die ordnung eines monomes phi bzgl. der variable var.        %
% (siehe kapitel 4 abschnitt 4.3)                              %
%                                                              %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
 
symbolic procedure dqe_helpord(phi,var);
begin scalar  erg, hilf;
erg  := 0;
if atom phi
   then  erg := 0
   else
if car phi = 'd
   then
   << if cadr phi = var
               then erg := caddr phi
               else erg := 0 >>
   else
if car phi = 'expt
   then erg := dqe_helpord(cadr phi,var)
   else
if car phi ='minus
   then erg := dqe_helpord(cadr phi,var)
   else
if car phi = 'times
   then
   << phi := cdr phi; erg := 0;
      while phi do
      << hilf := car phi;
         phi  := cdr phi;
         hilf := dqe_helpord(hilf,var);
         erg  := erg + hilf>> >>
   else erg := 0;
return erg;
end;
 
 
 
 
%%%%%%%%%%%%%%%%%%%%   dqe_ord %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                            %
%dqe_ord bestimmt die ordnung eines diffedifferentialpolynoms%
% phi bezueglich der variable var.                           %
% (siehe kapitel 4 abschnitt 4.3)                            %
%                                                            %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
 
symbolic procedure dqe_ord(phi,var);
begin scalar  ausg,hilf;
ausg := 0;
if atom phi
   then ausg := 0
   else
if not(car phi = 'plus )
   then ausg := dqe_helpord(phi,var)
   else
   << phi := cdr phi;
      while phi  do
      << hilf := car phi;
         phi  := cdr phi;
         hilf := dqe_helpord(hilf,var);
         if ausg < hilf
            then ausg := hilf >> >>;
 
return ausg;
end;
 
 
 
 
%%%%%%%%%%%%%%%%%%% dqe_grad    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                           %
% die prozedur dqe_grad berechnet den grad des differential-%
% polynoms phi bezueglich der variable  var .               %
% (siehe kapitel 4 abschnitt 4.3)                           %
%                                                           %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
 
symbolic procedure dqe_grad(phi,var);
begin scalar erg,hilf,ordc;
ordc := dqe_ord(phi,var);
if ordc = 0 then hilf := var
            else hilf := list('d,var,ordc);
erg := deg(phi,hilf);
if null erg then erg := 0;
return erg;
end;
 
 
 
 
%%%%%%%%%%%%%%%%% dqe_initial       %%%%%%%%%%%%%%%%%%%%%%%%%
%                                                           %
% die prozedur dqe_initial berechnet die initiale des diffe-%
% rentialpolynomes bezueglich der variable  var .           %
% (siehe kapitel 4 abschnitt 4.4)                           %
%                                                           %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
 
symbolic procedure dqe_initial(phi,var);
begin scalar ordc,hilfvar,ausg;
ordc     := dqe_ord(phi,var);
if ordc = 0 then hilfvar := var
            else hilfvar := list('d,var,ordc);
ausg     := reval lcof(phi,hilfvar);
 
return ausg;
end;
 
 
 
 
%%%%%%%%%%%%%%%%%  dqe_reduktum    %%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                            %
% die prozedur dqe_reduktum berechnet das reduktum des diffe-%
% rentialpolynomes bezueglich der variable  var .            %
% (siehe kapitel 4 abschnitt 4.4)                            %
%                                                            %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
 
symbolic procedure dqe_reduktum(phi,var);
begin scalar ordc,gradc,hilf,hilfvar,ausg;
ordc  := dqe_ord(phi,var);
gradc := dqe_grad(phi,var);
if ordc = 0 then hilfvar := var
    else hilfvar := list('d,var,ordc);
hilf  := list('expt,hilfvar,gradc);
hilf  := reval list('times,dqe_initial(phi,var),hilf);
ausg  := reval list('difference,phi,hilf);
 
return ausg;
end;
 
 
 
%%%%%%%%%%%%%%%%%  dqe_separante   %%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                            %
% die prozedur dqe_separante berechnet die separante eines   %
% differentialpolynomes phi bezueglich der variable  var .   %
% (siehe kapitel 1 definition 1.1.7)                         %
%                                                            %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
 
symbolic procedure dqe_separante(phi,var);
begin scalar ordc,hilfvar,ausg;
ordc  := dqe_ord(phi,var);
if ordc = 0 then hilfvar := var
    else hilfvar := list('d,var,ordc);
ausg  := dqe_pseudf(phi,hilfvar);
 
return ausg;
end;
 
 
 
 
 
%%%%%%%%%%%%%%%%% dqe_pseudrest    %%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                           %
%die prozedur dqe_pseudrest berechnet den rest einer pseudo-%
% divition von phi durch psi bezueglich der variable var .  %
% (siehe kapitel 4 abschnitt 4.1)                           %
%                                                           %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
 
symbolic procedure dqe_pseudrest(phi,psi,var);
begin scalar rest, q, k, l, hilf;
rest := phi;
hilf := deg(rest,var);
if not hilf then hilf := 0;
q := 0;
k := 0;
l := deg(psi,var);
if not l then l := 0;
 
while not(hilf = 0) and not(l = 0) and hilf >= l  do
<< k    := list('times,reval lcof(rest,var),
            list('expt,var,reval list('difference,hilf,l)));
   q    := list('plus,list('times,reval lcof(psi,var),q),k);
   rest :=reval list('difference,reval list('times,lcof(psi,var),rest),
            list('times,k,psi));
   hilf := deg(rest,var);
   if not hilf then hilf := 0 >>;
if not rest then rest := 0
            else rest := reval rest;
 
return rest;
end;
 
 
 
 
 
 
%%%%%%%%%%%%%%%%%  dqe_listenord   %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                               %
%dqe_listenord ordnet eine liste von differentialpolynomen bzgl.%
% der lexikographischen ordnung der paare, die aus der ordnung  %
% und dem grad jedes polynoms bzgl. der variable var bestehen.  %
% (siehe kapitel 4 abschnitt 4.7)                               %
%                                                               %
% eingabe : phi von der form list(f1,f2,f3,..,fn) und var.      %
%                                                               %
% ausgabe : list(f'1,f'2,f'3,...,f'n) wobei                     %
%(ord f'1,grad f'1)<=(ord f'2,grad f'2)<=...<=(ord f'n,grad f'n)%
%                                                               %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
 
symbolic procedure dqe_listenord(phi,var);
begin scalar geordliste,hilflist,hilf,hilf1,erg,testvar;
geordliste := nil;
erg        := nil;
testvar    := t;
if cdr phi
   then
   << hilflist := list(car phi);
      phi      := cdr phi;
      while phi do
      << hilf := car phi;
         phi  := cdr phi;
         while hilflist and testvar do
         << hilf1 := car hilflist;
            if dqe_ord(hilf,var) > dqe_ord(hilf1,var)
               then
               << erg        := dqe_consm(hilf,hilflist);
                  geordliste := append(geordliste,erg);
                  testvar    := nil >>
               else
            if dqe_ord(hilf,var) = dqe_ord(hilf1,var) and
              dqe_grad(hilf,var) >= dqe_grad(hilf1,var)
               then
               << erg        := dqe_consm(hilf,hilflist);
                  geordliste := append(geordliste,erg);
                  testvar    := nil >>
               else
               << geordliste  := reverse geordliste;
                  geordliste  := reverse dqe_consm(hilf1,geordliste);
                  hilflist    := cdr hilflist >>;
         if not(hilflist) and testvar
            then geordliste := dqe_modcons(hilf,geordliste)>>;
      if phi
         then << hilflist   := geordliste;
                 geordliste := nil;
                 testvar    := t >>
         else geordliste := reverse geordliste  >> >>
   else geordliste := phi ;
 
return geordliste;
end;
 
 
%%%%%%%%%%%%%%%% dqe_neqnullfkt  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                               %
% dqe_neqnullfkt ist hilfsprozedur fuer dqe_elim.               %
% (siehe kapitel 4 abschnitt 4.9)                               %
%                                                               %
% eingabe : eine liste phi der form list(elem1,....,elemn).     %
%                                                               %
% ausgabe : list('or,list('neq,elem1,0),...,list('neq,elmn,0)). %
%                                                               %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 

 
procedure dqe_neqnullfkt(phi);
   begin scalar r;
      if not phi then
	 return nil;
      r := for each elem in phi collect
   	 {'neq,reval elem,0};
      if not cdr r then
	 return car r;
      return 'or . r
   end;
 
 
 
 
 
%%%%%%%%%%%%%%%%%%  dqe_equalnullfkt   %%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                               %
% dqe_equalnullfkt ist hilfsprozedur fuer dqe_elim.             %
% (siehe kapitel 4 abschnitt 4.9)                               %
%                                                               %
% eingabe : eine liste phi der form list(elem1,....,elemn).     %
%                                                               %
% ausgabe : list(list('equal,elem1,0),...,                      %
%                             list('equal,elmn,0)).             %
%                                                               %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 

procedure dqe_equalnullfkt(phi);   
   for each elem in phi collect 
      {'equal,reval elem,0}; 
 
%%%%%%%%%%%%%%%% dqe_elimsimplify   %%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                               %
% dqe_elimsimplify ist hilfsprozedur fuer dqe_elim.             %
% (siehe kapitel 4 abschnitt 4.9)                               %
%                                                               %
% eingabe : phi von der form list(f1,f2,...,fn), zwerg und var. %
%                                                               %
% ausgabe : ausg, die aus nzwerg und erg besteht.               %
%           nzwerg ist die neue zwichenergliste, die aus zwerg  %
%           und der liste der konstanten polynome bzgl. var     %
%           gleichgesetzt mit 0.                                %
%           erg ist die liste der differentialpolynome,die nicht%
%           konstant bzgl. der variable var sind.               %
%                                                               %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
 
symbolic procedure dqe_elimsimplify(phi,zwerg,var);
begin scalar hilfg,hilf,erg1,erg2,ausg;
ausg := nil;
erg1 := nil;
erg2 := nil;
 
while phi do
<< hilf  := car phi;
   hilfg := dqe_grad(hilf,var);
   if hilfg = 0
      then erg1 := dqe_modcons(reval hilf,erg1)
      else erg2 := dqe_consm(hilf,erg2) ;
   phi := cdr phi >>;
 
erg1 := dqe_equalnullfkt(erg1);
if erg1
   then
   << if not cdr erg1 then erg1 := car erg1
        else erg1 := cons('and,erg1)>>;
 
if zwerg and not cdr zwerg then zwerg := car zwerg;
 
erg1 := dqe_andorvaleur(list('and,zwerg,erg1));
ausg := list(erg1,erg2);
 
return ausg;
end;

% part 5

%%%%%%%%%%%%%%%%   dqe_start1  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                             %
% diese prozedur fuehrt die quantorenelimination  durch.      %
% eingegeben wird nur die eingabeformel.                      %
%                                                             %
% eingabe : eine beliebige formel phi                         %
%                                                             %
% ausgabe : eine positive quantorenfreie formel, die aequi-   %
%           valent zu phi ist.                                %
%                                                             %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
 
symbolic procedure dqe_start1(phi);
begin scalar ausg,diffequaliste;
diffequaliste := nil;
 
if  !*dqeverbose
    then <<
prin2t "+++++++++++++++++++++++++++++++++++++++++++++++++++++++";
if !*dqeoptsimp then <<
prin2t "+++ dqeoptsimp ist on d.h. die ergebnisse von simplify+";
prin2t "+++ bzw. disjnf bzw. konjnf werden vereinfacht      +++">>
                else
prin2t "+++ deqoptsimp ist off                              +++";
 
 
if not !*dqegradord then
prin2t "+++ dqegradord ist off                              +++">>;
 
 
if !*dqeoptqelim
   then
   <<
     if  !*dqeverbose
         then <<
prin2t "+++ das qe_verfahren wird mit aussagenlogischen     +++";
prin2t "+++        vereinfachungen ausgefuehrt.             +++";
prin2t "+++++++++++++++++++++++++++++++++++++++++++++++++++++++">>;
     ausg := dqe_quantelimopt(phi,diffequaliste)
   >>
   else
   <<
     if  !*dqeverbose
         then <<
prin2t "+++ das qe_verfahren wird ohne aussagenlogischen    +++";
prin2t "+++         vereinfachungen ausgefuehrt.            +++";
prin2t "+++++++++++++++++++++++++++++++++++++++++++++++++++++++">>;
 
       ausg := dqe_quantelim(phi,diffequaliste)
     >>;
 
return ausg;
end;
 
 
 
%%%%%%%%%%%%%%%%   dqe_start2  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                             %
% diese prozedur fuehrt auch wie dqe_start1 die quantoreneli- %
% mination.                                                   %
%                                                             %
% eingabe : eine beliebige formel phi   und                   %
%           eine liste diffequaliste                          %
%                                                             %
% ausgabe : eine positive quantorenfreie formel, die aequi-   %
%           valent zu phi ist.                                %
%                                                             %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
 
symbolic procedure dqe_start2(phi,diffequaliste);
begin scalar ausg;
 
if  !*dqeverbose
    then <<
prin2t "+++++++++++++++++++++++++++++++++++++++++++++++++++++++";
if !*dqeoptsimp then <<
prin2t "+++ dqeoptsimp ist on d.h. die ergebnisse von simplify+";
prin2t "+++ bzw. disjnf bzw. konjnf werden vereinfacht      +++">>
                else
prin2t "+++ deqoptsimp ist off                              +++";
if not !*dqegradord then
prin2t "+++ dqegradord ist off                              +++"
          >>;
 
if !*dqeoptqelim
   then
   <<
     if  !*dqeverbose
         then <<
prin2t "+++ das qe_verfahren wird mit aussagenlogischen     +++";
prin2t "+++        vereinfachungen ausgefuehrt.             +++";
prin2t "+++++++++++++++++++++++++++++++++++++++++++++++++++++++" >>;
 
     ausg := dqe_quantelimopt(phi,diffequaliste)
   >>
   else
   <<
     if  !*dqeverbose
         then <<
prin2t "+++ das qe_verfahren wird ohne aussagenlogischen    +++";
prin2t "+++         vereinfachungen ausgefuehrt.            +++";
prin2t "+++++++++++++++++++++++++++++++++++++++++++++++++++++++">>;
 
     ausg := dqe_quantelim(phi,diffequaliste);
   >>;
 
return ausg;
end;
 
 
 
%%%%%%%%%%%%%%%%   dqe_elim    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                             %
% elim ist eine subroutine fuer die prozeduren exqelim und    %
% allqelim (siehe abschnitt 5.1 in kapitel 5).                %
%                                                             %
% eingabe : eine positive quantorenfreie teilformel phi ,     %
%           eine gebundene variable var und diffequaliste .   %
%                                                             %
% ausgabe : eine positive quantorenfreie formel phi', die     %
%           aequivalen zu  ex var  phi ist .                  %
%                                                             %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
 
symbolic procedure dqe_elim(phi,diffequaliste,var);
begin scalar hilf,ordhilf,erg1,erg2,ausg,zwerg,phi1,hilfvar,phi2,rest,
 hilff,hilfg,ghilf,gradf,gradg,ordf,ordg,redf,initf,const,
 erg21,erg22,erg,phi21,phi22,redhilf,sepf,gghilf,liste,helplist;
 
if !*dqegradord and !*dqeverbose then
prin2t "++++";
 
zwerg := nil;
phi   := dqe_helpelim(phi);
if phi = t or (not phi)
   then ausg := phi
   else
if not cdr phi
   then
   <<hilf := car phi;
 
     if !*dqegradord and !*dqeverbose then
       << ordg := dqe_ord(hilf,var);
          gradg := dqe_grad(hilf,var);
          prin2t "()";
          prin2t list(ordg,gradg)
        >>;
 
     ausg := dqe_neqnullfkt(dqe_termcoefkt(hilf,var)) >>
 
   else
if car phi = 1 and  not cddr phi
   then
   <<hilf  := cadr phi;
 
     if !*dqegradord and !*dqeverbose then
     << ordf := dqe_ord(hilf,var);
        gradf := dqe_grad(hilf,var);
        prin2t list(ordf,gradf);
        prin2t "()"
     >>;
 
     erg   := dqe_termcoefkt( hilf,var);
     hilf  := list('equal,reval car erg,0);
     erg   := dqe_neqnullfkt(cdr erg);
     ausg  := dqe_andorvaleur(list('or,hilf,erg)) >>
 
   else
<<
  hilfg := car phi;
  if (dqe_isconstant hilfg) and not(hilfg = 0)
     then hilfg := 1;
  phi   := cdr phi;
  ordg := dqe_ord(hilfg,var);
  gradg := dqe_grad(hilfg,var);
 
  if not cdr phi
     then
     << hilff := car phi;
        ordf := dqe_ord(hilff,var);
        gradf := dqe_grad(hilff,var);
        if !*dqegradord and !*dqeverbose then
        <<
          prin2t list(ordf,gradf);
          prin2t list(ordg,gradg)
        >>;
 
        if gradf = 0
           then << erg1 := list('equal,reval hilff,0);
                   erg2 := dqe_neqnullfkt(
                           dqe_termcoefkt( hilfg,var));
                   ausg := dqe_andorvaleur(list('and,erg1,erg2)) >>
           else
           <<redf  := dqe_reduktum(hilff,var);
             initf := dqe_initial(hilff,var);
             if redf = 0
                then  phi1 := list('and,list('neq,hilfg,0),
                            list('equal,initf,0))
                else
                << phi1 := dqe_equalnullfkt(
                           dqe_consm(initf,list(redf)));
                   phi1 :=cons('and,cons(list('neq,hilfg,0),phi1))>>;
 
             if ordf > ordg
                then << erg21 := dqe_neqnullfkt(
                                dqe_termcoefkt(hilfg,var));
                        erg22 := dqe_neqnullfkt(
                                 dqe_termcoefkt(initf,var));
                        erg2  :=
                        dqe_andorvaleur(list('and,erg21,erg22))>>
                else
             if ordf = ordg
                then
                << if ordf = 0 then hilfvar := var
                      else hilfvar := list('d,var,ordf);
                   ghilf :=dqe_pseudrest(list('expt,hilfg,gradf),hilff,
                                    hilfvar);
                   erg21 := dqe_neqnullfkt(dqe_termcoefkt(ghilf,var));
                   erg22 := dqe_neqnullfkt(dqe_termcoefkt(initf,var));
                   erg2  := dqe_andorvaleur(list('and,erg21,erg22)) >>
                else
                << const   := reval list('difference,ordg,ordf);
                   hilf    := dqe_diff(hilff,const,diffequaliste);
                   hilfvar := list('d,var,ordg);
                   ghilf   := dqe_pseudrest(hilfg,hilf,hilfvar);
                   if not(dqe_isconstant initf)
                      then ghilf := reval list('times,initf,ghilf);
                   phi21  := list('and,list('neq,ghilf,0),
                                      list('equal,hilff,0)) ;
                   erg21  := dqe_elim(phi21,diffequaliste,var) ;
 
                   if dqe_isconstant initf
                      then gghilf := hilfg
                      else gghilf :=reval list('times,initf,hilfg);
                   sepf := dqe_separante(hilff,var);
                   redhilf := dqe_reduktum(hilf,var);
                   phi22 := dqe_consm(list('equal,sepf,0),
                            dqe_consm(list('equal,redhilf,0),
                            list(list('equal,hilff,0)) ) );
                   phi22  := cons('and,cons(list('neq,gghilf,0),
                                    phi22));
                   erg22  := dqe_elim(phi22,diffequaliste,var) ;
                   erg2 := dqe_andorvaleur(list('or,erg21,erg22))>>;
             erg1 := dqe_elim(phi1,diffequaliste,var);
             ausg := dqe_andorvaleur(list('or,erg1,erg2)) >> >>
 
 
        else
       << phi   := dqe_elimsimplify(phi,zwerg,var);
          zwerg := car phi;
          phi   := cadr phi;
          if not phi
             then
                << if !*dqegradord and !*dqeverbose then
                      << prin2t "()";
                         prin2t list(ordg,gradg)  >>;
 
                    erg  := dqe_neqnullfkt(dqe_termcoefkt( hilfg,var));
                    if zwerg and not cdr zwerg
                     then ausg :=
                           dqe_andorvaleur(list('and,erg,car zwerg))
                     else ausg :=
                           dqe_andorvaleur(list('and,erg,zwerg)) >>
             else
          if not cdr phi
             then << phi  := list('and,list('neq,hilfg,0),
                                       list('equal,car phi,0));
                     erg  := dqe_elim(phi,diffequaliste,var);
                     if zwerg and not cdr zwerg
                      then ausg :=
                           dqe_andorvaleur(list('and,erg,car zwerg))
                      else
                      ausg :=dqe_andorvaleur(list('and,erg,zwerg)) >>
             else
             <<phi   := dqe_listenord(phi,var);
 
               if !*dqegradord and !*dqeverbose then
               << liste := phi; helplist := nil;
                  while liste do
                  << hilf := car liste; liste := cdr liste;
                     helplist := cons( list(dqe_ord(hilf,var),
                         dqe_grad(hilf,var)),helplist) >>;
                   prin2t helplist;
                   prin2t list(ordg,gradg);
               >>;
 
               hilff := car phi;
               initf := dqe_initial(hilff,var);
               redf  := dqe_reduktum(hilff,var);
               ordf  := dqe_ord(hilff,var);
               if redf = 0
                  then
                  << phi1 := dqe_equalnullfkt(
                             dqe_consm(initf,cdr phi));
                  phi1 := cons('and,cons(list('neq,hilfg,0),phi1))>>
                  else
             <<phi1 := dqe_equalnullfkt(
                         dqe_consm(initf,dqe_consm(redf,cdr phi)));
                 phi1 := cons('and,cons(list('neq,hilfg,0),phi1))>>;
 
               if dqe_isconstant initf
                  then ghilf := hilfg
                  else ghilf := reval list('times,initf,hilfg);
               hilf    := cadr phi;
               ordhilf := dqe_ord(hilf,var);
               if ordhilf = 0
                  then hilfvar := var
                  else hilfvar := list('d,var,ordhilf);
 
               if ordhilf = ordf
                  then rest := dqe_pseudrest(hilf,hilff,hilfvar)
                  else
                  << const := reval list('difference,ordhilf,ordf);
                     rest  := dqe_pseudrest(hilf,dqe_diff(hilff,const,
                                   diffequaliste),hilfvar)      >>;
 
               if rest = 0
                  then phi2 := dqe_equalnullfkt(
                          dqe_consm(hilff,cddr phi))
                  else
               phi2 := dqe_equalnullfkt(dqe_consm(rest,
                         dqe_consm(hilff,cddr phi)));
               phi2 := cons('and,cons(list('neq,ghilf,0),phi2));
 
               erg1 := dqe_elim(phi1,diffequaliste,var);
               erg2 := dqe_elim(phi2,diffequaliste,var);
               erg  := dqe_andorvaleur(list('or,erg1,erg2));
 
              if zwerg and not cdr zwerg
               then ausg := dqe_andorvaleur(list('and,erg,car zwerg))
               else ausg :=dqe_andorvaleur(list('and,erg,zwerg)) >> >>
    >>;
return ausg;
end;
 
 
 
 
%%%%%%%%%%%%%%%%    dqe_exqelim    %%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                             %
% exqelim ist eine subroutine fuer die prozedur quantelim     %
% (siehe abschnitt 5.2 in kapitel 5).                         %
%                                                             %
% eingabe : eine positive quantorenfreie formel phi, eine ge- %
%           bundene variable var und diffequaliste .          %
%                                                             %
% ausgabe : eine positive quantorenfreie formel phi', die     %
%           aequivalent zu  ex var  phi ist .                 %
%                                                             %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
 
symbolic procedure dqe_exqelim(phi,diffequaliste,var);
begin scalar hilf,ausg,k,n,timevar,gctimevar,erg;
  ausg := nil;n:= 0; k := 0;
 
if !*dqeverbose   then
<<
prin2t "++nun wird ein existenzquantor eliminiert, also muss zuerst";
prin2t "++die formel in disjunktive normalform transformiert werden.";
prin2t "++die disjunktive normalform von ";
mathprint phi;prin2t "++ist :";
>>;
 
timevar := time();
gctimevar := gctime();
phi := dqe_disjnf(phi);
 
if !*dqeverbose   then
   <<
     timevar := time() - timevar;
     gctimevar := gctime() - gctimevar;
     mathprint phi;
     prin2 timevar;prin2" ms plus ";prin2 gctimevar;prin2t " ms."
   >>;
 
  if  (phi = t) or (not phi) then ausg := phi
      else
  if car phi = 'or
     then
     << phi := cdr phi;
 
        if !*dqeverbose   then
        <<
           n := length(phi);
           prin2 "++ die anzahl der konjunktionen ist "; prin2t n;
           erg := dqe_elimberechnung(phi);
           prin2 "++die gesamte anzahl der atomaren formeln ist ";
           prin2t car erg;
           prin2 "++der ";prin2 cadr erg;
           prin2t "_te disjunktionsglied hat die hoechste";
           prin2 "++ anzahl von atomaren formeln und zwar ";
           prin2t caddr erg;
        >>;
 
        while phi do
        << hilf := car phi; k := k + 1;
 
           if !*dqeverbose   then
           <<
             prin2 "++elimination des quantors ex ";
             prin2 var; prin2 " vor dem ";
             prin2 k;prin2t "-ten konjunktionsglied ";
             mathprint hilf;
           >>;
 
           timevar := time();
           gctimevar := gctime();
 
           hilf := dqe_elim(hilf,diffequaliste,var);
 
           if !*dqeverbose   then
           <<
             timevar := time() - timevar;
             gctimevar := gctime() -gctimevar;
             prin2 "++die aequivalaente zum ";
             prin2 k;prin2t "-ten konjunktionsglied ist : ";
             mathprint hilf;
             prin2 timevar;prin2" ms plus ";
             prin2 gctimevar;prin2t " ms."
           >>;
 
           ausg := dqe_modcons(hilf,ausg);
           phi  := cdr phi >>;
        if length(ausg) = 1 then ausg := car ausg
           else
        if cdr ausg
           then ausg :=  cons('or,ausg) >>
 
     else ausg := dqe_elim(phi,diffequaliste,var);
 
return  ausg;
end;
 
 
 
 
%%%%%%%%%%%%%    dqe_allqelim   %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                             %
% allqelim ist eine subroutine fuer die prozedur quantelim    %
% (siehe abschnitt 5.3 in kapitel 5).                         %
%                                                             %
% eingabe : eine positive quantorenfreie formel phi, eine ge- %
%           bundene variable var und diffequaliste .          %
%                                                             %
% ausgabe : eine positive quantorenfreie formel phi',die      %
%           aequivalent zu  all var  phi ist .                %
%                                                             %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
 
symbolic procedure dqe_allqelim(phi,diffequaliste,var);
begin scalar hilf,ausgb,k,n,timevar,gctimevar,erg;
  ausgb := nil;n := 0; k := 0;
 
if !*dqeverbose
   then
   <<
prin2t "++nun wird ein allquantor eliminiert, also muss zuerst ";
prin2t "++die formel in konjunktive normalform transformiert werden. ";
     prin2t "++die konjunktive normalform von ";
     mathprint phi;prin2t "ist :";
   >>;
timevar := time();
gctimevar := gctime();
 
phi := dqe_konjnf(phi);
 
if !*dqeverbose
   then
   <<
     timevar := time() - timevar;
     gctimevar := gctime() - gctimevar;
     mathprint phi;
     prin2 timevar;prin2" ms plus ";prin2 gctimevar;prin2t " ms."
   >>;
 
if (phi = t) or (not phi)
   then ausgb := phi
   else
if car phi = 'and
   then
   <<phi := cdr phi;
       n := length(phi);
 
     if !*dqeverbose   then
     <<
       prin2 "++die anzahl der disjunktionen ist "; prin2t n;
       erg := dqe_elimberechnung(phi);
       prin2 "++die gesamte anzahl der atomaren formeln ist ";
       prin2t car erg;
       prin2 "++der ";prin2 cadr erg;
       prin2t "_te disjunktionsglied hat die hoechste";
       prin2 " anzahl von atomaren formeln und zwar ";prin2t caddr erg;
     >>;
 
     while phi do
     <<hilf := car phi;k := k + 1;
 
       if !*dqeverbose   then
       <<
         prin2 "++elimination des quantors all ";
         prin2 var; prin2 " vor dem ";
         prin2 k;prin2t "-ten disjunktionsglied ";
         mathprint hilf;
       >>;
 
       timevar := time();
       gctimevar := gctime();
       hilf := dqe_makepositive list('nott,hilf);
       hilf := dqe_elim(hilf,diffequaliste,var);
       hilf := dqe_makepositive list('nott,hilf);
 
       if !*dqeverbose   then
          <<
            timevar := time() - timevar;
            gctimevar := gctime() - gctimevar;
            prin2 "++die aequivalaente zum ";
            prin2 k;prin2t "-ten disjunktionsglied ist : ";
            mathprint hilf;
            prin2 timevar;prin2" ms plus ";
            prin2 gctimevar;prin2t " ms."
          >>;
 
          ausgb  := dqe_modcons(hilf,ausgb);
          phi  := cdr phi >>;
        if length(ausgb) = 1
           then ausgb := car ausgb
           else
        if cdr ausgb
           then ausgb :=  cons('and,ausgb) >>
     else
     << phi := dqe_makepositive list('nott,phi);
        ausgb := dqe_elim(phi,diffequaliste,var) ;
        ausgb := dqe_makepositive list('nott,ausgb) >>;
 
  return  ausgb;
end;
 
 
 
 
%%%%%%%%%%%%%%%%%%   dqe_quantelim   %%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                              %
% quantelim ist die hauptprozedur fuer quantorenelimination    %
% (siehe abschnitt 5.4 des kapitels 5).                        %
%                                                              %
% eingabe : eine beliebige formel phi .                        %
% ausgabe : eine positive quantorenfreie formel phi', die      %
%           aequivalent zu phi ist.                            %
%                                                              %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
 
symbolic procedure dqe_quantelim(phi,diffequaliste);
begin scalar hilf,liste,var,erg,quant,n,k,timevar,gctimevar;
      liste := nil;
      erg   := nil;
         n  := 0;
      timevar := time();
      gctimevar := gctime();
      phi   := dqe_makepositive phi;
      if not dqe_isprenexp phi
         then
         << if not diffequaliste
               then  phi := dqe_makeprenex phi
               else << hilf := dqe_makeprenexmod(phi,diffequaliste);
                       phi  := car hilf;
                       diffequaliste := cadr hilf >> >>;
 
      if !*dqeverbose   then
      <<
        timevar := time() - timevar;
        gctimevar := gctime() - gctimevar;
        prin2t "+++die praenexe form der eingabeformel ist";
        mathprint phi;
        prin2 timevar;prin2" ms plus ";prin2 gctimevar;prin2t " ms.";
      >>;
 
      while  car phi = 'ex  or car phi = 'all  do
         << hilf  := list(car phi,cadr phi);
            liste := cons(hilf,liste);
                n := n + 1;
            phi   := caddr phi >>;
 
      if !*dqeverbose   then
      <<
        prin2t "+++die matrix der eingabeformel ist";
        mathprint phi;
      >>;
 
      erg := phi;
 
      if !*dqeverbose   then
      <<
        prin2 "+++die anzahl der quantoren ist ";mathprint n;
      >>;
 
      if n = 0 then
         <<
           if !*dqeverbose  then
              prin2t "+++die eingabeformel ist quantorenfrei" >>
         else
      if n = 1
         then
        << hilf  := car liste;
           liste := cdr liste;
           quant := car hilf;
           var   := cadr hilf;
 
           if !*dqeverbose   then
           <<
             prin2 "+++es gibt nur den quantor ";
             prin2 quant;prin2 ",";prin2 var;
             prin2t " zu eliminieren.";
           >>;
 
           if quant = 'ex
              then   erg := dqe_exqelim(erg,diffequaliste,var)
              else   erg := dqe_allqelim(erg,diffequaliste,var)
           >>
     else
     << k := 0;
 
       if !*dqeverbose   then
          <<
            prin2 "es gibt ";prin2 n;
            prin2t " quantoren zu eliminieren.";
          >>;
 
     while   liste  do
     << hilf  := car liste;
        liste := cdr liste;
        quant := car hilf;
        var   := cadr hilf;
           k  := k + 1;
 
        if !*dqeverbose   then
        <<
          prin2 " elimination des "; prin2 k;prin2 "-ten quantors ";
          prin2 quant; prin2t var
        >>;
        timevar := time();
        gctimevar := gctime();
 
        if quant = 'ex
           then   erg := dqe_exqelim(erg,diffequaliste,var)
           else  erg := dqe_allqelim(erg,diffequaliste,var);
 
        if !*dqeverbose  then
        <<
          timevar := time() - timevar;
          gctimevar := gctime() - gctimevar;
          prin2 "nach der elimination des ";
          prin2 k;prin2t "-ten quantors";
          prin2t "sieht die quantorenfreie formel, wie folgt, aus: ";
          mathprint erg;
          prin2 timevar;prin2" ms plus ";
          prin2 gctimevar;prin2t " ms.";
        >>;
        >> >>;
 
        if !*dqeverbose  then
prin2t "+++die aequivalaente quantorenfreie formel ist+++: ";
return  erg;
end;
 
 
 
 
%%%%%%%%%%%%%%%%%%  dqe_elimopt  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                             %
% elimopt ist eine subroutine der prozeduren exqelimopt und   %
% allqelimopt. sie arbeitet wie elim aber sie verwendet die   %
% hilfsprozedur simplify (siehe abschnitt 5.5 des kapitels 5).%
%                                                             %
% eingabe : eine positive quantorenfreie teilformel phi ,     %
%           eine gebundene variable var und diffequaliste .   %
%                                                             %
% ausgabe : eine vereinfachte positive quantorenfreie formel  %
%           phi', die aequivalen zu  ex var  phi ist .        %
%                                                             %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

symbolic procedure dqe_elimopt(phi,diffequaliste,var);
   begin scalar nf;
      if !*dqegradord and !*dqeverbose then
      	 prin2t "++++";      
      nf := dqe_helpelim phi;
      if (nf = t) or (not nf) then
	 return nf;
      if not cdr nf then
	 return dqe_elimopt!-neq(nf,diffequaliste,var);
      if car nf = 1 and  not cddr nf then
	 return dqe_elimopt!-oneeq(nf,diffequaliste,var);
      return dqe_elimopt!-regular(nf,diffequaliste,var)
   end;
 
procedure dqe_elimopt!-neq(phi,diffequaliste,var);
   begin scalar res,prod,ordg,gradg;
      prod := car phi;
      if !*dqegradord and !*dqeverbose then <<
	 ordg := dqe_ord(prod,var);
	 gradg := dqe_grad(prod,var);
	 prin2t "()";
	 prin2t {ordg,gradg};
      >>; 
      res := dqe_neqnullfkt dqe_termcoefkt(prod,var);
      res := dqe_simplify res;
      return res
   end;

procedure dqe_elimopt!-oneeq(phi,diffequaliste,var);
   begin scalar equ,ordf,gradf,erg,res;
      equ := cadr phi;
      if !*dqegradord and !*dqeverbose then <<
	 ordf := dqe_ord(equ,var);
	 gradf := dqe_grad(equ,var);
	 prin2t list(ordf,gradf);
	 prin2t "()";
      >>;
      erg := dqe_termcoefkt( equ,var);
      equ := dqe_simplify {'equal,reval car erg,0};   % Warning: Must return eq
      if equ = T then
	 return T;
      erg := cdr erg;
      erg := dqe_neqnullfkt erg ;
      res := dqe_andorvaleur {'or,equ,erg};
      res := dqe_simplify res;
      return res
   end;

procedure dqe_elimopt!-regular(phi,diffequaliste,var);
   begin scalar g,eqs;
      g := car phi;
      eqs := cdr phi;
      if (dqe_isconstant g) and not(g = 0) then
	 g := 1;
      if not cdr eqs then
	 return dqe_elimopt!-regular!-oneeq(g,eqs,diffequaliste,var);
      return dqe_elimopt!-regular1(g,eqs,diffequaliste,var);
   end;

procedure dqe_elimopt!-regular1(g,eqs,diffequaliste,var);
   begin scalar eqs,hilf,ordhilf,erg1,erg2,ausg,zwerg,phi1,hilfvar,phi2,rest;
      scalar hilff,g,ghilf,gradg,ordf,ordg,redf,initf,const;
      scalar erg,weiter;
      scalar liste, helplist,phi;
      ordg  := dqe_ord(g,var);
      gradg := dqe_grad(g,var); 	 
      phi := dqe_elimsimplify(eqs,zwerg,var);
      zwerg := car phi; phi := cadr phi;
      if not zwerg then
	 weiter := t
      else <<
	 if not cdr zwerg then
	    zwerg := dqe_simplify car zwerg
	 else
	    zwerg := dqe_simplify zwerg;
	 if zwerg = nil then
	    weiter := nil
	 else weiter := t
      >>;
      if weiter = nil then
	 ausg := nil
      else <<
	 if not phi then <<
	    if !*dqegradord and !*dqeverbose then <<
	       prin2t "()";
	       prin2t list(ordg,gradg)
	    >>;
	    erg := dqe_neqnullfkt(dqe_termcoefkt( g,var));
	    if zwerg = t then
	       ausg := erg
	    else ausg := dqe_andorvaleur(
	       list('and,erg,zwerg));
	    ausg := dqe_simplify ausg
      	 >> else if not cdr phi then <<
	    phi  := list('and,list('neq,g,0),
	       list('equal,car phi,0));
	    erg  := dqe_elimopt(phi,diffequaliste,var);
	    if zwerg = t then
	       ausg := erg
	    else if erg = t then <<
	       if not zwerg then
	       	  ausg := t
	       else
	       	  ausg := zwerg
	    >> else
	       ausg := dqe_andorvaleur(
	       	  list('and,erg,zwerg));
	    ausg := dqe_simplify ausg
	 >> else <<
	    phi   := dqe_listenord(phi,var); 
	    if !*dqegradord and !*dqeverbose then <<
	       liste := phi; helplist := nil;
	       while liste do <<
		  hilf := car liste; liste := cdr liste;
		  helplist := cons( list(dqe_ord(hilf,var),
		     dqe_grad(hilf,var)),
		     helplist)
	       >>;
	       prin2t helplist;
	       prin2t list(ordg,gradg);
	    >>; 	       
	    hilff := car phi;
	    initf := dqe_initial(hilff,var);
	    redf  := dqe_reduktum(hilff,var);
	    ordf  := dqe_ord(hilff,var);
	    if redf = 0 then
	       phi1 := dqe_equalnullfkt(dqe_consm(initf,cdr phi))
	    else
	       phi1 := dqe_equalnullfkt(dqe_consm(initf,
		  dqe_consm(redf,cdr phi)));
	    phi1 := cons('and,cons(list('neq,g,0),phi1));
	    if dqe_isconstant initf then
	       ghilf := g
	    else
	       ghilf := reval list('times,initf,g);
	    hilf    := cadr phi;
	    ordhilf := dqe_ord(hilf,var);
	    if ordhilf = 0 then
	       hilfvar := var
	    else
	       hilfvar := list('d,var,ordhilf); 	       
	    if ordhilf = ordf then
	       rest := dqe_pseudrest(hilf,hilff,hilfvar)
	    else <<
	       const := reval list('difference,ordhilf,ordf);
	       rest  :=dqe_pseudrest(hilf,dqe_diff(hilff,const,
		  diffequaliste),hilfvar)
	    >>; 	       
	    if rest = 0 then
	       phi2 := dqe_equalnullfkt(
		  dqe_consm(hilff,cddr phi))
	    else
	       phi2 := dqe_equalnullfkt(dqe_consm(rest,
		  dqe_consm(hilff,cddr phi)));
	    phi2 := cons('and,cons(list('neq,ghilf,0),phi2));
	    erg2 := dqe_elimopt(phi2,diffequaliste,var);
	    if erg2 = t then
	       erg := t
	    else <<
	       erg1 := dqe_elimopt(phi1,diffequaliste,var);
	       if erg1 = t then
		  erg := t
	       else
		  erg := dqe_andorvaleur(list('or,erg1,erg2))
	    >>;
	    if zwerg = t then
	       ausg := erg
	    else if erg = t then <<
	       if not zwerg then
		  ausg := t
	       else
		  ausg := zwerg
	    >> else
	       ausg :=dqe_andorvaleur(list('and,erg,zwerg)) ;
	    ausg := dqe_simplify ausg
	 >>
      >>;
      return ausg;
   end;

procedure dqe_elimopt!-regular!-oneeq(g,eqs,diffequaliste,var);
   begin scalar eqs,hilf,erg1,erg2,ausg,phi1,hilfvar;
      scalar hilff,g,ghilf,gradf,gradg,ordf,ordg,redf,initf,const;
      scalar erg21,erg22,phi21,phi22,redhilf,sepf,gghilf;
      ordg  := dqe_ord(g,var);
      gradg := dqe_grad(g,var); 	 
      hilff := car eqs;
      gradf := dqe_grad(hilff,var);
      ordf  := dqe_ord(hilff,var); 	    
      if !*dqegradord and !*dqeverbose then <<
	 prin2t {ordf,gradf};
	 prin2t {ordg,gradg};
      >>; 	    
      if gradf = 0 then <<
	 erg1 := dqe_simplify list('equal,reval hilff,0);
	 if erg1 = nil then
	    ausg := nil
	 else <<
	    erg2 := dqe_neqnullfkt(dqe_termcoefkt( g,var));
	    if erg1 = t then
	       ausg := erg2
	    else
	       ausg := dqe_andorvaleur(list('and,erg1,erg2)) ;
	    ausg := dqe_simplify ausg
	 >>
      >> else <<
	 redf  := dqe_reduktum(hilff,var);
	 initf := dqe_initial(hilff,var);
	 if redf = 0 then
	    phi1 := list('and,list('neq,g,0)
	       , list('equal,initf,0))
	 else <<
	    phi1 :=dqe_equalnullfkt(dqe_consm(initf,list(redf)));
	    phi1 := cons('and,cons(list('neq,g,0),phi1))
	 >>;
	 if ordf > ordg then <<
	    erg21 := dqe_neqnullfkt(
	       dqe_termcoefkt(g,var));
	    erg22 := dqe_neqnullfkt(
	       dqe_termcoefkt(initf,var));
	    erg2  := dqe_simplify
	       dqe_andorvaleur(list('and,erg21,erg22))
	 >> else if ordf = ordg then <<
	    if ordf = 0 then
	       hilfvar := var
	    else
	       hilfvar := list('d,var,ordf);
	    ghilf :=dqe_pseudrest(list('expt,g,gradf),hilff,
	       hilfvar);
	    erg21 := dqe_neqnullfkt(dqe_termcoefkt(ghilf,var));
	    erg22 := dqe_neqnullfkt(dqe_termcoefkt(initf,var));
	    erg2  := dqe_simplify
	       dqe_andorvaleur(list('and,erg21,erg22))
	 >> else <<
	    const   := reval list('difference,ordg,ordf);
	    hilf    := dqe_diff(hilff,const,diffequaliste);
	    hilfvar := list('d,var,ordg);
	    ghilf   := dqe_pseudrest(g,hilf,hilfvar);
	    if not(dqe_isconstant initf) then
	       ghilf := reval list('times,initf,ghilf);
	    phi21  := list('and,list('neq,ghilf,0),
	       list('equal,hilff,0));
	    erg21  := dqe_elimopt(phi21,diffequaliste,var) ;
	    if erg21 = t  then
	       erg2 := erg21
	    else << if dqe_isconstant initf then
	       gghilf := g
	    else gghilf :=
	       reval list('times,initf,g);
	    sepf := dqe_separante(hilff,var);
	    redhilf := dqe_reduktum(hilf,var);
	    phi22 := dqe_consm(list('equal,sepf,0),
	       dqe_consm(list('equal,redhilf,0),
		  list(list('equal,hilff,0)) ) );
	    phi22  := cons('and,cons(list('neq,gghilf,0),
	       phi22));
	    erg22  := dqe_elimopt(phi22,diffequaliste,var) ;
	    erg2 := dqe_andorvaleur(list('or,erg21,erg22))
	    >>
	 >>;
	 if erg2 = t then
	    ausg := t
	 else <<
	    erg1 := dqe_elimopt(phi1,diffequaliste,var);
	    if erg1 = t then
	       ausg := t
	    else
	       ausg := dqe_andorvaleur(list('or,erg1,erg2));
	    ausg := dqe_simplify ausg >>
      >>;
      return ausg;
   end;
 
%%%%%%%%%%%%%%%%%   dqe_exqelimopt   %%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                             %
% exqelimopt ist eine subroutine fuer quantelimopt. sie  ar-  %
% beitet wie exqelim (siehe abschnitt 5.5).                   %
%                                                             %
% eingabe : eine positive quantorenfreie formel phi, eine ge- %
%           junktiver nomalform , eine gebundene variable var %
%           bundene variable var und diffequaliste .          %
%                                                             %
% ausgabe : eine vereinfachte positive quantorenfreie formel  %
%           phi', die aequivalent zu  ex var  phi ist .       %
%                                                             %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
 
symbolic procedure dqe_exqelimopt(phi,diffequaliste,var);
begin scalar hilf,erg,testvar,k,n,timevar,gctimevar,ausg;
  erg := nil;  testvar := t; n := 0;   k := 0;
 
if !*dqeverbose
   then
   <<
 prin2t "++nun wird ein existenzquantor eliminiert, also muss zuerst ";
prin2t "++die formel in disjunktive normalform transformiert werden. ";
     prin2t "++die disjunktive normalform von ";
     mathprint phi; prin2t " ist";
   >>;
timevar := time();
gctimevar := gctime();
 
phi := dqe_disjnf(phi);
 
if !*dqeverbose
   then
   <<
     timevar := time() - timevar;
     gctimevar := gctime() - gctimevar;
     mathprint phi;
     prin2 timevar; prin2 " ms plus ";prin2 gctimevar;prin2t " ms."
   >>;
 
  if (phi = t) or not(phi) then erg := phi
     else
  if car phi = 'or
     then
     << phi := cdr phi; testvar := t;
 
       if !*dqeverbose  then
          <<
            n := length(phi);
            prin2 "++die anzahl der konjunktionen ist "; prin2t n;
            ausg := dqe_elimberechnung(phi);
            prin2 "++die gesamte anzahl der atomaren formeln ist ";
            prin2t car ausg;
            prin2 "++der ";prin2 cadr ausg;
            prin2t "_te disjunktionsglied hat die
            hoechste";
            prin2 " ++anzahl von atomaren formeln und zwar ";
            prin2t caddr ausg;
          >>;
 
        while phi and testvar do
        << hilf := car phi; k := k + 1;
 
          if !*dqeverbose  then
             <<
               prin2 "++elimination des quantors ex";prin2 ",";
               prin2 var; prin2 " vor dem ";
               prin2 k; prin2t "-ten konjunktionsglied ";
               mathprint hilf;
             >>;
          timevar := time();
          gctimevar := gctime();
 
           hilf := dqe_elimopt(hilf,diffequaliste,var);
 
           if !*dqeverbose  then
             <<
               timevar := time() - timevar;
               gctimevar := gctime() - gctimevar;
               prin2 "++ die aequivalaente zum ";
               prin2 k; prin2t "-ten konjunktionsglied ist :";
               mathprint hilf;
               prin2 timevar;prin2 " ms plus ";
               prin2 gctimevar;prin2t " ms."
             >>;
 
           if hilf = t
              then testvar := nil
              else  erg  := dqe_consm(hilf,erg);
           phi  := cdr phi   >>;
 
        if not(testvar) then erg := t
           else
        if length(erg) = 1 then erg := dqe_simplify car erg
           else
        if cdr erg
           then erg := dqe_simplify
                         cons('or,reverse erg) >>
 
     else erg := dqe_elimopt(phi,diffequaliste,var);
 
return  erg;
end;
 
 
 
 
%%%%%%%%%%%%%%%%%  dqe_allqelimopt  %%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                             %
% allqelimopt ist eine subroutine fuer quantelimopt. sie ar-  %
% beitet wie allqelim (siehe abschnitt 5.5).                  %
%                                                             %
% eingabe : eine positive quantorenfreie formel phi, eine ge- %
%           junktiver nomalform , eine gebundene variable var %
%           bundene variable var und diffequaliste .          %
%                                                             %
% ausgabe : eine vereinfachte positive quantorenfreie formel  %
%           phi', die aequivalent zu  all var  phi ist .      %
%                                                             %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
 
symbolic procedure dqe_allqelimopt(phi,diffequaliste,var);
begin scalar hilf,erg,testvar,k,n,timevar,gctimevar,ausg;
  erg := nil; testvar := t; k := 0;
 
if !*dqeverbose
   then
   <<
     prin2t "++nun wird ein allquantor eliminiert, also muss zuerst ";
prin2t "++die formel in konjunktive normalform transformiert werden. ";
     prin2t "++die konjunktive normalform von ";
     mathprint phi;prin2t "ist:"
   >>;
timevar := time();
gctimevar := gctime();
 
phi := dqe_konjnf(phi);
 
if !*dqeverbose
   then
   <<
     timevar := time() - timevar;
     gctimevar := gctime() - gctimevar;
     mathprint phi;
     prin2 timevar; prin2 " ms plus ";prin2 gctimevar;prin2t " ms."
   >>;
 
  if (phi = t) or not(phi) then erg := phi
     else
  if car phi = 'and
     then
     << phi := cdr phi; k := 0;
        n := length(phi);
 
       if !*dqeverbose
          then
          <<
            prin2 "++ die anzahl der disjunktionen ist "; prin2t n;
            ausg := dqe_elimberechnung(phi);
            prin2 "++die gesamte anzahl der atomaren formeln ist ";
            prin2t car ausg;
            prin2 "++der ";prin2 cadr ausg;
            prin2t "_te disjunktionsglied hat die hoechste";
            prin2 " anzahl von atomaren formeln und zwar ";
            prin2t caddr ausg;
          >>;
 
        while phi and testvar do
        <<hilf := car phi; k := k + 1;
 
          if !*dqeverbose then
             <<
               prin2 "elimination des quantors all ";prin2 ",";
               prin2 var; prin2 " vor dem ";
               prin2 k; prin2t "-ten disjunktionsglied ";
               mathprint hilf;
             >>;
          timevar := time();
          gctimevar := gctime();
 
          hilf := dqe_makepositive list('nott,car phi);
          hilf := dqe_elimopt(hilf,diffequaliste,var);
          hilf := dqe_makepositive list('nott,hilf);
 
          if !*dqeverbose  then
            <<
              timevar := time() - timevar;
              gctimevar := gctime() - gctimevar;
              prin2 "++ die aequivalaente zum ";
              prin2 k; prin2t "-ten disjunktionsglied ist :";
              mathprint hilf;
              prin2 timevar;prin2 " ms plus ";
              prin2 gctimevar;prin2t " ms."
              >>;
 
          if hilf = nil
             then testvar := nil
             else  erg  := dqe_consm(hilf,erg);
          phi  := cdr phi    >>;
        if not(testvar) then erg := nil
           else
        if length(erg) = 1 then erg := dqe_simplify car erg
           else
        if cdr erg
           then erg := dqe_simplify
                         cons('and,reverse erg) >>
     else
     << phi := dqe_makepositive list('nott,phi);
        erg := dqe_elimopt(phi,diffequaliste,var) ;
        erg := dqe_makepositive list('nott,erg) >>;
 
  return  erg;
end;
 
 
 
 
%%%%%%%%%%%%%%%%%%%%  dqe_quantelimopt %%%%%%%%%%%%%%%%%%%%%%%%%
%                                                              %
% quantelimopt ist wie quantelim eine hauptprozedur fuer quant-%
% orenelimination mit aussagenlogischen vereinfachungen (siehe %
% abschnitt 5.5 des kapitels 5).                               %
%                                                              %
% eingabe : eine beliebige formel phi .                        %
% ausgabe : eine vereinfachte positive quantorenfreie formel   %
%           phi', die aequivalent zu phi ist.                  %
%                                                              %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
 
symbolic procedure dqe_quantelimopt(phi,diffequaliste);
begin scalar hilf,liste,var,ausg,quant,weiter,k,n,timevar,gctimevar;
      weiter  := t;
           n  := 0;
      liste := nil;
      ausg  := nil;
 
      timevar := time();
      gctimevar := gctime();
 
      phi   := dqe_makepositive phi;
      if not dqe_isprenexp phi
         then
         << if not diffequaliste
               then  phi := dqe_makeprenex phi
               else << hilf := dqe_makeprenexmod(phi,diffequaliste);
                       phi  := car hilf;
                       diffequaliste := cadr hilf >> >>;
 
      if !*dqeverbose  then
         <<
            timevar  := time() - timevar;
            gctimevar := gctime() -  gctimevar;
            prin2t "+++die praenexe form der eingabeformel ist";
            mathprint phi;
            prin2 timevar;prin2" ms plus ";
            prin2 gctimevar;prin2t " ms.";
         >>;
 
      while  car phi = 'ex  or car phi = 'all  do
         << hilf  := list(car phi,cadr phi);
            liste := cons(hilf,liste);
            n     := n + 1;
            phi   := caddr phi >>;
 
     if !*dqeverbose  then
        <<
          prin2t "+++die matrix der eingabeformel ist";
          mathprint phi;
        >>;
 
     ausg := phi;
 
    if !*dqeverbose  then
       << prin2 "+++die anzahl der quantoren ist ";mathprint n >>;
 
    if n = 0 then
         <<
           if !*dqeverbose  then
              prin2t "+++die eingabeformel ist quantorenfrei" >>
              else
  if n = 1 then
     << hilf  := car liste;
        liste := cdr liste;
        quant := car hilf;
        var   := cadr hilf;
 
        if !*dqeverbose then
           <<
             prin2 "+++es gibt nur den quantor ";
             prin2 quant;prin2",";prin2 var;
             prin2t " zu eliminieren.";
           >>;
 
        if quant = 'ex
           then  ausg := dqe_exqelimopt(ausg,diffequaliste,var)
           else  ausg := dqe_allqelimopt(ausg,diffequaliste,var)  ;
           >>
     else
     << k := 0;
 
    if !*dqeverbose then
       <<
          prin2 "+++es gibt ";prin2 n;
          prin2t " quantoren zu eliminieren.";
       >>;
 
     while   liste and weiter do
        << hilf  := car liste;
           liste := cdr liste;
           quant := car hilf;
           var   := cadr hilf;
              k  := k + 1;
 
           if !*dqeverbose
              then
              <<
                prin2 " elimination des ";
                prin2 k;prin2 "-ten quantors ";
                prin2 quant; prin2t var ;
              >>;
           timevar := time();
           gctimevar := gctime();
 
           if quant = 'ex
 
              then  ausg := dqe_exqelimopt(ausg,diffequaliste,var)
              else  ausg := dqe_allqelimopt(ausg,diffequaliste,var);
 
           if !*dqeverbose then
              <<
                timevar := time() - timevar;
                gctimevar := gctime() - gctimevar;
                prin2 "+++nach der elimination des ";
                prin2 k;prin2t "-ten quantors";
         prin2t "sieht die quantorenfreie formel, wie folgt, aus: ";
                mathprint ausg;
                prin2 timevar;prin2" ms plus ";
                prin2 gctimevar;prin2t " ms.";
              >>;
 
           if (ausg = t) or not(ausg)
              then weiter := nil  >> >>;
 
           if !*dqeverbose then
prin2t "+++die aequivalaente vereinfachte quantorenfreie formel ist: ";
     return   ausg;
end;

% part 6

%%%%%%%%%%%%%%% dqe_elimberechnung    %%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                               %
% diese prozedur berechnet die anzahl der atomaren formeln  in  %
% einer positiven quantorenfreien formel, die in disjunktiver   %
% bzw. konjunktiver normalform ist. ausserdem bestimmt sie      %
% den laengesten konjunktions- bzw. disjunktionsglied.          %
%                                                               %
% eingabe: eine positive quantorenfreie formel phi, die in dis- %
%          junktiver bzw. konjunktiver normalform ist.          %
%                                                               %
% ausgabe: eine liste,die aus erg1, erg2 und erg3 besteht.      %
%          erg1 ist anzahl der atomaren formeln in phi.         %
%          erg2 ist der index des in phi vorkommenden  laengen  %
%          gliedes und erg3 ist die anzahl der atomaren formeln %
%          dieses gliedes.                                      %
% sie wird von ex- bzw. allqelim und ex- bzw. allqelimopt       %
% aufgerufen.                                                   %
%                                                               %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


symbolic procedure dqe_elimberechnung(phi);
begin scalar erg,erg1,erg2,erg3,hilf,k;
if phi = t or not phi
   or dqe_isatomarp(phi)
   then
   << erg1 := 1; erg2 := 1; erg3 := 1>>
   else
   << erg1 := 0; erg2 := 0; erg3 := 0; k := 0;
      phi := cdr phi;
      while phi do
      << k := k + 1;
         hilf := car phi; phi := cdr phi;
         hilf := dqe_elimberechnung(hilf);
         erg1 := erg1 + car hilf;
         if car hilf > erg3
            then
            << erg2 := k; erg3 := car hilf>> >> >>;
erg := list(erg1,erg2,erg3);
return erg;
end;


%%%%%%%%%%%%%   dqe_helpsimplify  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                               %
% dqe_helpsimplify ist eine hilfsprozedur fuer dqe_simplify.    %
% sie transformiert jede positive quantorenfreie formel zuerst  %
% in disjuntive normalform. dann fuehrt sie die folgende  ver-  %
% einfachungen durch :                                          %
% 1 fall : die formel von der form (a = 0 and ... and a neq 0)  %
%          wird zu false vereinfacht.                           %
%                                                               %
% 2 fall : die formel von der form (a = 0 or ... or a neq 0)    %
%          wird zu true vereinfacht.                            %
%                                                               %
% 3 fall : die formel von der form (phi and a = 0) or ... or psi%
%          or (phi and a neq 0) wird mit hilfe der prozedur     %
%           dqe_logsimp zu phi or ... or psi vereinfacht.       %
% 4 fall : die formel von der form (phi and a = 0) or ... or psi%
%          or a = 0 wird zu a = 0 or ...or psi (analog fuer     %
%           a neq  0) vereinfacht.                              %
% sie wird von simplify aufgerufen.                             %
%                                                               %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


symbolic procedure dqe_helpsimplify(phi);
begin scalar ausg,hilf,hilfphi,liste1,liste2,weiter;
scalar aliste,kliste;
ausg := nil;
if phi = t or not phi or dqe_isatomarp(phi)
   then ausg := phi
   else
   << phi := dqe_disjnf(phi);
      if car phi = 'and
         then
         %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
         % hier wird a = 0 and ... and a neq 0 zu fasle vereinfacht%
         % phi wird in zwei listen aufgeteilt. liste2 enthaelt die %
         % atomare formeln mit gleichung und liste1 enthaelt die   %
         % atom. formeln mit ungl. . falls ein element der liste1  %
         % aus der liste2 ist, dann ist die ausgabe nil. sonst phi.%
         %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
         << hilfphi := cdr phi; liste1 := nil; liste2 := nil;
            while hilfphi do
            << hilf := car hilfphi; hilfphi := cdr hilfphi;
               if car hilf = 'neq
                  then liste1 := dqe_consm(hilf,liste1)
                  else liste2 := dqe_consm(hilf,liste2) >>;

            weiter := t;
            while liste1 and weiter do
            << hilf := car liste1; liste1 := cdr liste1;
               hilf := dqe_makepositive list('nott,hilf);
               if hilf member liste2
                  then weiter := nil >>;
            if not weiter
               then ausg := nil
               else ausg := phi >>

         else
         << hilfphi := cdr phi; weiter := t; aliste := nil;
            kliste := nil;

            while hilfphi and weiter do
         %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
         % hier wird phi in zwei listen aufgeteilt.                %
         % aliste enthaelt nur die atomaren formeln.               %
         % kliste enthaelt die konjunktionen.                      %
         %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
            << hilf := car hilfphi; hilfphi := cdr hilfphi;
               hilf := dqe_helpsimplify(hilf);
               if hilf = t then weiter := nil
                  else
               if dqe_isatomarp hilf
                  then aliste := dqe_modcons(hilf,aliste)
                  else
               if hilf
                  then kliste := dqe_modcons(hilf,kliste)>>;

            if kliste
               then
         %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
         % hier wird a = 0 or psi and a = 0   zu psi vereinfacht.  %
         % falls ein element der aliste in einem element der kliste%
         % vorkommt,dann wird dieses element aus der aliste enfernt%
         % statt a = 0 and psi nur psi kommt in kliste.            %
         %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
               <<liste1 := aliste;
                 while  liste1 do
                 <<liste2 := kliste;
                   hilf := car liste1;liste1 := cdr liste1;
                   while  liste2 do
                   << if hilf member car liste2
                         then kliste := dqe_sanselem(car liste2,
                                                    kliste);
                      liste2 := cdr liste2 >> >> >>;

            if not weiter then ausg := t
               else
               << hilfphi := aliste;
                  if length(aliste) = 1
                     then aliste := car aliste
                     else
                  if aliste
                     then aliste := cons('or,aliste);

                  liste1 := nil; liste2 := nil;
                  while hilfphi do
         %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
         % hier wird a = 0 or  ... or  a neq 0 zu true vereinfacht.%
         % hilfphi wird in zwei listen aufgeteilt. liste2 enthaelt %
         % atomare  formeln mit gleichung und liste1 enthaelt die  %
         % atom. formeln mit ungl. . falls ein element der liste1  %
         % aus der liste2 ist,dann ist die ausgabe t. sonst hilfphi%
         %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
                  <<hilf := car hilfphi; hilfphi := cdr hilfphi;
                    if car hilf = 'neq
                       then liste1 := dqe_consm(hilf,liste1)
                       else liste2 := dqe_consm(hilf,liste2) >>;

                  weiter := t;
                  while liste1 and weiter do
                  <<hilf := car liste1; liste1 := cdr liste1;
                    hilf := dqe_makepositive list('nott,hilf);
                    if hilf member liste2
                       then weiter := nil >>;

                  if not weiter then ausg := t
                     else
                  if not kliste
                     then ausg := aliste
                     else
                  if not cdr kliste
                     then ausg := dqe_andorvaleur list('or,
                         aliste,car kliste)

                     else
         %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
         % mit hilfe deq_logsimp wird a = 0 and psi or a neq 0 and %
         % psi zu psi vereinfacht.                                 %
         %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
                     << hilfphi := dqe_logsimp(kliste);
                        if not hilfphi
                           then ausg := aliste
                           else
                        if dqe_isatomarp hilfphi
                           then
                           << if not aliste
                                 then ausg := hilfphi
                                 else
                                 <<if dqe_isatomarp aliste
                                      then ausg := list('or,
                                           aliste,hilfphi)
                                      else ausg := dqe_modcons
                                         (hilfphi,aliste);
                                   ausg := dqe_helpsimplify(phi) >>>>
                           else
                        if car hilfphi ='and
                           then ausg := dqe_andorvaleur list('or,
                                  aliste,hilfphi)
                           else
                           <<ausg := dqe_andorvaleur list('or,
                                      aliste,hilfphi);
                             if not(cdr hilfphi = kliste)
                               then ausg := dqe_helpsimplify(ausg)
                            >>
                >> >> >> >>;


return ausg;
end;




%%%%%%%%%%%%%%% dqe_logsimp     %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                               %
% dqe_logsimp ist eine hilfsprozedur von dqe_helpsimplify. mit  %
% hilfe dieser prozedur wird jede positive quantorenfreie formel%
% von der form (phi and a = 0) or... or psi or (phi and a neq 0)%
% zu phi or ... or psi vereinfacht.                             %
%                                                               %
% eingabe : eine liste von konjunktionen.                       %
%                                                               %
% ausgabe : eine liste von konjunktionen mit oben beschriebenen %
%           vereinfachung.                                      %
%                                                               %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


symbolic procedure dqe_logsimp(phi);
begin scalar konjlist,erg,hilf,aliste,liste,weiter,hilfphi;
scalar constant,hilff;
erg := nil; liste := nil;
hilfphi := phi;

while hilfphi do
<< konjlist := cdar hilfphi;constant := car hilfphi;
   hilfphi := cdr hilfphi;
   liste   := hilfphi;
   aliste  := nil;
   while konjlist  do
   << hilf := car konjlist; konjlist := cdr konjlist;
      hilff := dqe_makepositive list('nott,hilf);
      weiter := t;
      while liste and weiter do
      << if hilff member car liste
                  and
            dqe_listequal( dqe_sanselem(car liste,hilff),
                           dqe_sanselem(constant,hilf) )
            then weiter := nil
            else liste := cdr liste >>;
      if weiter
         then
          aliste := dqe_consm(hilf,aliste)
         else
         hilfphi := dqe_sanselem(hilfphi,car liste) ;
      liste  := hilfphi >>;

   if length aliste = 1
      then erg := dqe_consm(car aliste,erg)
      else
   if aliste
      then erg := dqe_consm(cons('and,reverse aliste),erg) >>;

if length erg = 1 then erg := car erg
   else
if erg then erg := cons('or,reverse erg);
return erg;
end;

%%%%%%%%%%%%%%% dqe_listequal     %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                               %
% dqe_listequal testet ob zwei listen die selben elemente haben.%
%                                                               %
% eingabe : zwei listen.                                        %
%                                                               %
% ausgabe : true  falls diese listen dieselbe menge darstellen  %
%           false sonst                                         %
%                                                               %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

 symbolic procedure dqe_listequal(phi,psi);
 begin scalar ausg,weiter;
 ausg := nil; weiter := t;
 if not(length phi = length psi)
    then ausg := nil
    else
    << while phi and weiter do
       << if car phi member psi
             then phi := cdr phi
             else weiter := nil >>;
       if weiter  then ausg := t
                  else ausg := nil >>;
return ausg;
end;


%%%%%%%%%%%%%%% dqe_vorkommen    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                               %
% dqe_vorkommen berechnet,wie oft die atomare formel der form   %
% (elem = 0) oder  not(elem = 0) in einer positiven quantoren-  %
% quantorenfreien formel phi vorkommt.                          %
% (siehe abschnitt 6.1 des kapitels 6)                          %
%                                                               %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


symbolic procedure dqe_vorkommen(elem,phi);
begin scalar erg,hilf;
if phi = t or not phi then erg := 0
   else
if car phi = 'neq or car phi = 'equal
   then
   << if cadr phi = elem  then erg := 1
         else erg := 0>>
   else
   << phi := cdr phi;
      while phi do
      << hilf := dqe_vorkommen(elem,car phi);
         erg  := erg + hilf;
         phi  := cdr phi >> >>;
return erg;
end;


%%%%%%%%%%%%%   dqe_laengefkt     %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                               %
% dqe_laengefkt bestimmt die anzahl der atomaren formeln einer  %
% positiven quantorenfreien formel phi.                         %
% (siehe abschnitt 6.1 des kapitels 6)                          %
%                                                               %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


symbolic procedure dqe_laengefkt(phi);
begin scalar erg,hilf;
erg := 0;
if phi = t or not phi then erg := 0
   else
if car phi = 'equal or car phi = 'neq
   then erg := 1
   else
   << phi := cdr phi;
      while phi do
      << hilf := dqe_laengefkt(car phi);
         erg := erg + hilf;
         phi := cdr phi >> >>;
return erg;
end;




%%%%%%%%%%%%%%% dqe_specneq     %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                               %
% dqe_specneq ist eine hilfsprozedur von dqe_tableau.           %
% (siehe abschnitt 6.1 des kapitels 6)                          %
%                                                               %
% eingabe : eine positive quantorenfreie formel phi und elem.   %
% ausgabe : phi', wobei phi' aus phi entsteht, indem ueberall   %
%           elem = 0 durch false  ersetzt wird und              %
%           not(elem = 0) durch true ersetzt wird und           %
%           durch simplify vereinfacht wird.                    %
%                                                               %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


symbolic procedure dqe_specneq(phi,elem);
begin scalar erg;

erg := dqe_simplify subst(t,list('neq,elem,0),
                subst(nil,list('equal,elem,0),phi));

return erg;
end;

%%%%%%%%%%%%%   dqe_specequal   %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                               %
% dqe_specequal ist eine hilfsprozedur von dqe_tableau.         %
% (siehe abschnitt 6.1 des kapitels 6)                          %
%                                                               %
% eingabe : eine positive quantorenfreie formel phi und elem.   %
% ausgabe : phi', wobei phi' aus phi entsteht, indem ueberall   %
%           elem = 0 durch true  ersetzt wird und               %
%           not(elem = 0) durch false ersetzt wird und mit hilfe%
%           simplify vereinfacht wird.                          %
%                                                               %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


symbolic procedure dqe_specequal(phi,elem);
begin scalar erg;

erg := dqe_simplify subst(t,list('equal,elem,0),
                subst(nil,list('neq,elem,0),phi));

return erg;
end;


%%%%%%%%%%%%%%%    dqe_tableau  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                               %
% dqe_tableau berechnet die tableau-methode fuer elem in der po-%
% tiven quantorenfreien formel phi. diese methode wurde  in     %
% abschnitt 6.1 des kapitels 6 dargestellt.                     %
%                                                               %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


symbolic procedure dqe_tableau(phi,elem);
begin scalar erg;

erg := dqe_simplify(list('or,
         list('and,list('equal,elem,0),dqe_specequal(phi,elem)),
         list('and,list('neq,elem,0),dqe_specneq(phi,elem)) ));
if erg = list('or,list('equal,elem,0),list('neq,elem,0))
   then erg := t;

return erg;
end;



%%%%%%%%%%%    dqe_ltableau           %%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                               %
% dqe_ltableau berechnet mehrere tableau-schritte. sie wurde in %
% abschnitt 6.1 spezifiziert. sie verwendet die obige prozedur  %
% dqe_tableau.                                                  %
%                                                               %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


symbolic procedure dqe_ltableau(phi,varliste);
begin scalar erg,elem;

erg := phi;

while varliste  do
<< elem     := car varliste;
   varliste := cdr varliste;
   erg      := dqe_tableau(erg,elem)>>;

return erg;
end;




%%%%%%%%%%%%%%%  dqe_dknfsimplify %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                   %
% dqe_dknfsimplify vereinfacht eine positive quantorenfreie formel, %
% die in disjunktiver bzw. konjunktiver normal form ist .           %
% dqe_dknfsimplify verwendet die hilfsprozedur dqe_permutationfkt.  %
% (siehe abschnitt 6.2 des kapitels 6)                              %
%                                                                   %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


symbolic procedure dqe_dknfsimplify(phi);
begin scalar erg,hilf,hilff,liste,weiter,symb;
erg := nil;
if (phi = t) or (not phi)
    or dqe_isatomarp(phi)
   then erg := phi
   else
   << symb := car phi;
      phi := cdr phi;
      while phi do
      << hilf := car phi ; phi := cdr phi;
         if (hilf = t) or (not hilf)
              or dqe_isatomarp(hilf)
            then erg := dqe_modcons(hilf,erg)
            else
            << liste := list(cadr hilf);
               hilff := cddr hilf;
               while hilff do
               << liste := dqe_consm(car hilff,liste);
                  hilff := cdr hilff >>;

               if length(liste) = 1
                  then erg := dqe_modcons(car liste,erg)
                  else
                  <<hilf := cons(car hilf,reverse liste);
                    if not erg then erg := list(hilf)
                       else
                    if not(hilf member erg)
                       then
                       << liste := erg; weiter := t;
                          while liste and weiter do
                          << if dqe_listequal(hilf,car liste)
                                then weiter := nil
                                else liste := cdr liste >>;
                          if weiter
                             then erg := dqe_modcons(hilf,erg) >>
                           >> >> >>;
      if length(erg) = 1
         then erg := car erg
         else
      if cdr erg
         then erg:= cons(symb,erg) >>;

return erg;
end;



%%%%%%%%%%%%%%%  dqe_permutationfkt %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                 %
% dqe_permutationfkt ist eine hilfsprozedur fuer dqe_dknfsimplify.%
% sie berechnet alle permutation einer liste.                     %
% (siehe abschnitt 6.2 des kapitels 6)                            %
%                                                                 %
% eingabe: eine liste phi von der form list(a_1,a_2,...,a_n),     %
%          wobei a_i paarweise verschieden sind und sie nur       %
%          atomare formeln oder true oder false seien duerfen.    %
%                                                                 %
% ausgabe: ergliste ist eine liste,die aus der menge der permu-   %
%          tation von der liste phi, falls phi mehr als ein ele-  %
%          enthaelt.                                              %
%          sonst ist ergliste leer.                               %
%                                                                 %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


symbolic procedure dqe_permutationfkt(phi);
begin scalar ergliste,liste,hilf,hilfliste,erghilf;
ergliste :=nil;

if not(phi) or (length(phi) = 1)
   then ergliste := nil
   else
if length(phi) = 2
   then ergliste := list(phi,reverse phi)
   else
   <<liste := phi;

     while liste do
     << hilf := car liste;
        liste := cdr liste;
        hilfliste := dqe_sanselem(phi,hilf);
        hilfliste := dqe_permutationfkt(hilfliste);
        while hilfliste do
        << erghilf := cons(hilf,car hilfliste);
           ergliste := cons(erghilf,ergliste);
           hilfliste := cdr hilfliste  >> >>;

     ergliste := reverse ergliste >>;

return ergliste;
end;



%%%%%%%%%%%%%%%  dqe_sanselem   %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                               %
% dqe_sanselem ist eine hilfsprozedur fuer dqe_permutationfkt . %
% (siehe abschnitt 6.2 des kapitels 6)                          %
%                                                               %
% eingabe: eine liste phi von der form list(a_1,a_2,...,a_n),   %
%          und eine element a.                                  %
%                                                               %
% ausgabe: ergliste ist die liste phi ohne das element a.       %
%                                                               %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


symbolic procedure dqe_sanselem(phi,elem);
begin scalar hilf,erg;
erg := nil;

while phi do
<< hilf := car phi;
   phi  := cdr phi;
   if not(elem = hilf)
      then erg := cons(hilf,erg) >>;

return reverse erg;
end;

% part 7

symbolic procedure dqe_pform f;
if listp f and car f eq '!*sq then prepsq cadr f else f$

endmodule;  % [dcfsfkacem]

end;  % of file
