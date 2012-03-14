module indices;

% Author: Eberhard Schruefer.

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


fluid '(!*exp !*msg !*nat !*sub2 alglist!* fancy!-pos!* fancy!-line!*
        frasc!* subfg!*);

global '(mcond!*);

global '(basisforml!* basisvectorl!* keepl!* naturalframe2coframe
         dbaseform2base2form dimex!* indxl!* naturalvector2framevector
         metricd!* metricu!* coord!* cursym!* detm!* !*nosum
         nosuml!* commutator!-of!-framevectors);

symbolic procedure indexeval(u,v);
   % Toplevel evaluation function for indexed quantities.
   begin scalar v,x,alglist!*;
     v := simp!* u;
     x := subfg!*;
     subfg!* := nil;
     % We don't substitute values here, since indexsymmetries can
     % save us a lot of work.
     v := quotsq(xpndind partitsq(numr v ./ 1,'indvarpf),
                 xpndind partitsq(denr v ./ 1,'indvarpf));
     subfg!* := x;
     % If there are no free indices, we have already the result;
     % otherwise indxlet does the further simplification.
     if numr v and null indvarpf !*t2f lt numr v
        then v := exc!-mk!*sq2 resimp v
      else v := prepsqxx v;
     % We have to convert to prefix here, since we don't have a tag.
     % This is a big source of inefficiency.
     return v
   end;

symbolic procedure exc!-mk!*sq2 u;  %this is taken from matr;
   begin scalar x;
        x := !*sub2;   %since we need value for each element;
        u := subs2 u;
        !*sub2 := x;
        return mk!*sq u
   end;

symbolic procedure xpndind u;
   %performs the implied summation over repeated indices;
   begin scalar x,y;
     y := nil ./ 1;
     a: if null u then return y;
     if null(x := contind ldpf u) then
        y := addsq(multsq(!*f2q ldpf u,lc u),y)
      else for each k in mkaindxc(x,nil) do
        y := addsq(multsq(subcindices(ldpf u,pair(x,k)),lc u),y);
     u := red u;
     go to a
   end;

symbolic procedure subcindices(u,l);
   % Substitutes dummy indices from a-list l into s.f. u;
   % discriminates indices from variables.
   begin scalar alglist!*;
     return if domainp u then u ./ 1
             else addsq(multsq(
                           exptsq(if flagp(car mvar u,'indexvar) then
                                        simpindexvar subla(l,mvar u)
                                   else simp subindk(l,mvar u),ldeg u),
                               subcindices(lc u,l)),
                       subcindices(red u,l))
   end;

symbolic procedure subindk(l,u);
   %Substitutes indices from a-list l into kernel u;
   %discriminates indices from variables;
   car u . for each j in cdr u collect
               if atom j then j
                else if idp car j and get(car j,'dname) then j
                else if flagp(car j,'indexvar) then
                                  car j . subla(l,cdr j)
                else subindk(l,j);

put('form!-with!-free!-indices,'evfn,'indexeval);

put('indexed!-form,'rtypefn,'freeindexchk);

put('form!-with!-free!-indices,'setprifn,'indxpri);

symbolic procedure freeindexchk u;
   if u and indxl!* and indxchk u then 'form!-with!-free!-indices
    else nil;

symbolic procedure indvarp u;
   %typechecking for variables with free indices on prefix forms;
   null !*nosum and indxl!* and
    if eqcar(u,'!*sq) then
       indvarpf numr cadr u or indvarpf denr cadr u
     else freeindp u;

symbolic procedure indvarpf u;
   %typechecking for free indices in s.f.'s;
   if domainp u then nil
    else or(if sfp mvar u then indvarpf mvar u
             else freeindp mvar u,
            indvarpf lc u,indvarpf red u);

symbolic procedure freeindp u;
   begin scalar x;
     return if null u or numberp u then nil
             else if atom u then nil
             else if car u eq '!*sq then freeindp prepsq cadr u
             else if idp car u and get(car u,'dname) then nil
             else if flagp(car u,'indexvar) then indxchk cdr u
             else if (x := get(car u,'indexfun)) then
                                      freeindp apply1(x,cdr u)
             else if car u eq 'partdf then
                     if null cddr u then freeindp cadr u
                      else freeindp cadr u or freeindp caddr u
             else lfreeindp cdr u or freeindp car u
   end;

symbolic procedure lfreeindp u;
   u and (freeindp car u or lfreeindp cdr u);

symbolic procedure indxchk u;
   %returns t if u contains at least one free index;
   begin scalar x,y;
   x := u;
   y := union(indxl!*,nosuml!*);
   a: if null x then return nil;
      if null ((if atom car x
                 then if numberp car x then !*num2id abs car x
                       else car x
                 else if numberp cadar x then !*num2id cadar x
                       else cadar x) memq y)
                then return t;
      x := cdr x;
      go to a
   end;

symbolic procedure indexrange u;
   begin
   if null eqcar(car u,'equal)
      then indxl!* := mkindxl u
    else for each j in u do
           begin scalar names,range;
             names := cadr j;
             range := caddr j;
             if atom names then names := list names
              else if null(car names eq 'list)
                      then rerror(excalc,11,
                                  "badly formed indexrangelist")
                    else names := cdr names;
             if atom range then range := list range
              else if null(car range eq 'list)
                      then rerror(excalc,11,
                                  "badly formed indexrangelist")
                    else range := cdr range;
              range := mkindxl range;
              indxl!* := reverse union(range,reverse indxl!*);
              for each k in names do
                  put(k,'indexrange,range)
            end
    end;

symbolic procedure nosum u;
   <<nosuml!* := union(mkindxl u,nosuml!*); nil>>;

symbolic procedure renosum u;
   <<nosuml!* := setdiff(mkindxl u,nosuml!*); nil>>;

symbolic procedure mkindxl u;
   for each j in u collect if numberp j then !*num2id j
                            else j;

rlistat('(indexrange nosum renosum));

smacro procedure upindp u;
%tests if u is a contravariant index;
   atom revalind u;

symbolic procedure allind u;
   %returns a list of all unbound indices found in standard form u;
   allind1(u,nil);

symbolic procedure allind1(u,v);
   if domainp u then v
    else allind1(red u,allind1(lc u,append(v,allindk mvar u)));

symbolic procedure allindk u;
   begin scalar x;
     return if atom u then nil
             else if flagp(car u,'indexvar) then
                     <<for each j in cdr u do
                         if atom(j := revalind j)
                            then if null(j memq indxl!*)
                                    then x := j . x
                                  else nil
                          else if null(cadr j memq indxl!*)
                                  then x := j . x;
                       reverse x>>
             else if (x := get(car u,'indexfun)) then
                           allindk apply1(x,cdr u)
             else if car u eq 'partdf then
                     if null cddr u then
                        for each j in allindk cdr u collect lowerind j
                      else append(allindk cadr u,
                                  for each j in allindk cddr u collect
                                                lowerind j)
             else append(allindk car u,allindk cdr u)
   end;

symbolic procedure contind u;
   %returns a list of indices over which summation has to be performed;
   begin scalar dnlist,uplist;
     for each j in allind u do
       if upindp j then uplist := j . uplist
        else dnlist := cadr j . dnlist;
     return setdiff(intersection(uplist,dnlist),nosuml!*)
   end;

symbolic procedure mkaindxc(u,bool);
    %u is a list of indices, bool are boolean expressions
    %regulating index-symmetries. Result is a list of lists of
    %all possible index combinations;
    begin scalar r,x;
      r := list u;
      for each k in u do
        if x := getindexr k then r := mappl(x,k,r,bool);
      return r
    end;

symbolic procedure mappl(u,v,w,bool);
   (if null cdr u then x
     else if x then append(x,mappl(cdr u,v,w,bool))
     else mappl(cdr u,v,w,bool))
   where x = chksymmetries!&subst(car u,v,w,bool);

symbolic procedure chksymmetries!&subst(u,v,w,bool);
   if null w then nil
    else ((if x then x . chksymmetries!&subst(u,v,cdr w,bool)
            else chksymmetries!&subst(u,v,cdr w,bool))
           where x = chksymmetries!&sub1(u,v,car w,bool));

symbolic procedure chksymmetries!&sub1(u,v,w,bool);
   (if null bool or indxsymp(x,bool) then x else nil)
   where x = subst(u,v,w);


symbolic procedure getindexr u;
   if memq(u,indxl!*) then nil
    else ((if x then x
            else indxl!*) where x = get(u,'indexrange));

symbolic procedure flatindxl u;
   for each j in u collect if atom j then j else cadr j;

symbolic procedure indexlet(u,v,ltype,b,rtype);
   if flagp(car u,'indexvar) then
      if b then setindexvar(u,v)
       else begin scalar x,y,z,msg;
              msg := !*msg;
              !*msg := nil; %for now.
              u := mvar numr simp0 u;    %is this right?
              z := flatindxl allind !*k2f u;
              for each j in mkaindxc(z,get(car u,'indxsymmetries)) do
                <<let2(x := mvar numr simp0 subla(pair(z,j),u),
                       nil,nil,nil);
                  if y := assoc(x,keepl!*)
                     then keepl!* := delete(y,keepl!*)>>;
              !*msg := msg;
              if basisforml!* and (car u eq caar basisforml!*)
                 and null cddr u
                 then <<naturalframe2coframe := nil;
                        dbaseform2base2form := nil;
                        basisforml!* := nil>>;
              if basisvectorl!* and (car u eq caar basisvectorl!*)
                 and null cddr u
                 then <<naturalvector2framevector := nil;
                        commutator!-of!-framevectors := nil;
                        basisvectorl!* := nil>>;
              y := get(car u,'ifdegree);
              z := assoc(length cdr u,y);
              y := delete(z,y);
              remprop(car u,'ifdegree);
              if y then put(car u,'ifdegree,y)
               else <<remprop(car u,'rtype);
                      remprop(car u,'partitfn);
                      remprop(car u,'indxsymmetries);
                      remprop(car u,'indxsymmetrize);
                      remflag(list car u,'indexvar)>>
             end
    else if subla(frasc!*,u) neq u then
         put(car(u := subla(frasc!*,u)),'opmtch,
             xadd!*((for each j in cdr u collect revalind j) .
                  list(nil . (if mcond!* then mcond!* else t),v,nil),
          get(car u,'opmtch),b))
    else setindexvar(u,v);

put('form!-with!-free!-indices,'typeletfn,'indexlet);

symbolic procedure setindexvar(u,v);
   begin scalar r,s,w,x,y,z,z1,alglist!*;
     x := metricu!* . flagp(car u,'covariant);
     metricu!* := nil; %index position must not be changed here;
     if cdr x then remflag(list car u,'covariant);
     u := simp0 u;
     if red numr u
        or (denr u neq 1) then rerror(excalc,6,"Illegal assignment");
     u := numr u;
     r := cancel(1 ./ lc u);
     u := mvar u;
     metricu!* := car x;
     if cdr x then flag(list car u,'covariant);
     z1 := allind !*k2f u;
     z := flatindxl z1;
    if indxl!* and metricu!* then
      <<z1 := for each j in z1 collect
                if flagp(car u,'covariant)
                   then if upindp j then
                           <<u := car u . subst(lowerind j,j,cdr u);
                             'lower . j>>
                         else cadr j
                 else if upindp j then j
                       else <<u := car u . subst(j,cadr j,cdr u);
                              'raise . cadr j>>;
        u := car u . for each j in cdr u collect revalind j>>
     else z1 := z;
    r := multsq(simp!* v,r);
    w := for each j in mkaindxc(z,get(car u,'indxsymmetries)) collect
      <<x := mkletindxc pair(z1,j);
        s := nil ./ 1;
        y := subfg!*;
        subfg!* := nil;
        for each k in x do
          s := addsq(multsq(car k,subfindices(numr r,cdr k)),s);
        subfg!* := y;
        y := !*q2f simp0 subla(pair(z,j),u);
        mvar y . exc!-mk!*sq2 multsq(subf(if minusf y
                                             then negf numr s
                                      else numr s,nil),
                               invsq subf(multf(denr r,denr s),nil))>>;
      for each j in w do let2(car j,cdr j,nil,t)
    end;

symbolic procedure mkletindxc u;
   %u is a list of dotted pairs. Left part is unbound index and action.
   %Right part is bound index.
   begin scalar r; integer n;
     r := list((1 ./ 1) . for each j in u collect
                            if atom car j then car j else cdar j);
     for each k in u do
       <<n := n + 1;
         if atom car k then
             r := for each j in r collect car j . subindexn(k,n,cdr j)
        else r := mapletind(if caar k eq 'raise then getupper cdr k
                             else getlower cdr k,
                            cdar k,r,n)>>;
     return r
   end;

symbolic procedure subindexn(u,n,v);
   if n=1 then u . cdr v
    else car v . subindexn(u,n-1,cdr v);

symbolic procedure mapletind(u,v,w,n);
   if null u then nil
    else append(for each j in w collect
                 multsq(simp!* cdar u,car j) .
                 subindexn(v . caar u,n,cdr j),
                mapletind(cdr u,v,w,n));

put('form!-with!-free!-indices,'setelemfn,'setindexvar);

symbolic procedure clearfdegree x;
   <<atom x
      and <<remprop(x,'fdegree); remprop(x,'clearfn); remprop(x,'rtype);
            if x memq coord!* then coord!* := delete(x,coord!*)>>;
     let2(x,x,nil,nil); let2(x,x,t,nil)>>;

symbolic procedure subfindices(u,l);
   %Substitutes free indices from a-list l into s.f. u;
   %discriminates indices from variables;
   begin scalar alglist!*;
     return if domainp u then u ./ 1
             else addsq(multsq(if atom mvar u then !*p2q lpow u
                                else if sfp mvar u then
                                   exptsq(subfindices(mvar u,l),ldeg u)
                                else if flagp(car mvar u,'indexvar)
                                        then  exptsq(simpindexvar(
                                            car mvar u .
                                           subla(l,cdr mvar u)),ldeg u)
                                else if car mvar u memq
                                       '(wedge d partdf innerprod
                                         liedf hodge vardf) then
                                   exptsq(simp
                                            subindk(l,mvar u),ldeg u)
                              else !*p2q lpow u,subfindices(lc u,l)),
                       subfindices(red u,l))
   end;

symbolic procedure indxpri1 u;
   begin scalar metricu,il,dnlist,uplist,r,x,y,z;
     metricu := metricu!*;
     metricu!* := nil;
     il := allind !*t2f lt numr simp0 u;
     for each j in il do
          if upindp j
             then uplist := j . uplist
           else dnlist := cadr j . dnlist;
         for each j in intersection(uplist,dnlist) do
             il := delete(j,delete(revalind
                                  lowerind j,il));
         metricu!* := metricu;
     y := flatindxl il;
     r := simp!* u;
     for each j in mkaindxc(y,nil) do
       <<x := pair(y,j);
     z := exc!-mk!*sq2 multsq(subfindices(numr r,x),1 ./ denr r);
        if null(!*nero and (z = 0)) then
         <<maprin list('setq,subla(x,'ns . il),z);
           if not !*nat then prin2!* "$";
           terpri!* t>>>>
       end;

symbolic procedure indxpri(v,u);
   begin scalar x,y,z;
     y := flatindxl allindk v;
     for each j in mkaindxc(y,if coposp cdr v
                                 then get(car v,'indxsymmetries)
                               else nil) do
       <<x := pair(y,j);
         z := aeval subla(x,v);
         if null(!*nero and (z = 0)) then
         <<maprin list('setq,subla(x,v),z);
         if not !*nat then prin2!* "$";
         terpri!* t>>>>
    end;

symbolic procedure coposp u;
   %checks if all indices in list u are either in a covariant or
   %a contravariant position.;
   null cdr u or if atom car u then contposp cdr u
                  else covposp cdr u;

symbolic procedure contposp u;
   %checks if all indices in list u are contravariant;
   null u or (atom car u and contposp cdr u);

symbolic procedure covposp u;
   %checks if all indices in list u are covariant;
   null u or (null atom car u and covposp cdr u);

put('ns,'prifn,'indvarprt);

symbolic procedure simpindexvar u;
   %simplification function for indexed quantities;
   !*pf2sq partitindexvar u;

symbolic procedure partitindexvar u;
   %partition function for indexed quantities;
   begin scalar freel,x,y,z,v,sgn,w;
     x := for each j in cdr u collect
              (if atom k then
                  if numberp k then
                     if minusp k then lowerind !*num2id abs k
                      else !*num2id k
                   else k
                else if numberp cadr k then lowerind !*num2id cadr k
                      else k) where k = revalind j;
     w := deg!*form u;
     if null metricu!* then go to a;
     z := x;
     if null flagp(car u,'covariant) then
        <<while z and (atom car z or
                     null atsoc(cadar z,metricu!*)) do
             <<y := car z . y;
               if null atom car z then freel := cadar z . freel;
               z := cdr z>>;
               if z then <<v := nil;
                           y := reverse y;
                           for each j in getlower cadar z do
                            v := addpf(multpfsq(partitindexvar(car u .
                                   append(y,car j . cdr z)),
                                               simp cdr j),v);
                           return v>>>>
      else
        <<while z and (null atom car z or
                  null atsoc(car z,metricu!*)) do
             <<y := car z . y;
               if atom car z then freel := car z . freel;
               z := cdr z>>;
               if z then <<v := nil;
                           y := reverse y;
                           for each j in getupper car z do
                             v := addpf(multpfsq(partitindexvar(car u .
                                   append(y,lowerind car j . cdr z)),
                                          simp cdr j),v);
                           return v>>>>;
    a: if null coposp x or null get(car u,'indxsymmetries) then
              return if w then mkupf(car u . x)
                      else 1 .* mksq(car u . x,1) .+ nil;
       x := for each j in x collect if atom j then j else cadr j;
       x := indexsymmetrize (car u . x);
       if null x then return;
       if car x = -1 then sgn := t;
       x := cddr x;
       if flagp(car u,'covariant) then
          x := for each j in x collect
                 if j memq freel then j else lowerind j
        else if null metricu!* and null atom cadr u then
          x := for each j in x collect lowerind j
        else
          x := for each j in x collect
                 if j memq freel then lowerind j else j;
       return if w then if sgn then  negpf mkupf(car u . x)
                         else mkupf(car u . x)
               else if sgn then 1 .* negsq mksq(car u . x,1) .+ nil
                     else 1 .* mksq(car u . x,1) .+ nil
    end;

symbolic procedure flatindl u;
   if null u then nil
    else append(car u,flatindl cdr u);

symbolic procedure !*num2id u;
%converts a numeric index to an id;
  %if u = 0 then rerror(excalc,7,"0 not allowed as index") else
   if u<10 then intern cdr assoc(u,
             '((0 . !0) (1 . !1) (2 . !2) (3 . !3) (4 . !4)
               (5 . !5) (6 . !6) (7 . !7) (8 . !8) (9 . !9)))
    else intern compress('!! . explode u);

symbolic procedure revalind u;
   begin scalar x,y,alglist!*,dmode!*;
     x := subfg!*;
     subfg!* := nil;
     u := subst('!0,0,u);
     % The above line is used to avoid the simplifaction of -0 to 0.
     y := prepsq simp u;
     subfg!* := x;
     return y
   end;

symbolic procedure revalindl u;
   for each ind in u collect revalind ind;

endmodule;

end;
