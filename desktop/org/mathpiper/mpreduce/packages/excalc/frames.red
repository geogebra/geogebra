module frames;

% Author: Eberhard Schruefer;

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


global '(basisforml!* basisvectorl!* keepl!* naturalframe2coframe
         dbaseform2base2form dimex!* indxl!* naturalvector2framevector
         metricd!* metricu!* coord!* cursym!* detm!*
         commutator!-of!-framevectors);

fluid '(alglist!* indl kord!* subfg!*);   % indl needed by Common Lisp.

symbolic procedure coframestat;
   begin scalar framel,metric;
     flag('(with),'delim);
     framel := cdr rlis();
     remflag('(with),'delim);
     if cursym!* eq '!*semicol!* then go to a;
     if scan() eq 'metric then metric := xread t
      else if cursym!* eq 'signature then metric := rlis()
      else symerr('coframe,t);
     a: cofram(framel,metric)
   end;

put('coframe,'stat,'coframestat);


%put('cofram,'formfn,'formcofram);

symbolic procedure cofram(u,v);
   begin scalar alglist!*;
     rmsubs();
     u := for each j in u collect
              if car j eq 'equal then cdr j else list j;
     putform(caar u,1);
     basisforml!* := for each j in u collect !*a2k car j;
     indxl!* := for each j in basisforml!* collect cadr j;
     dimex!* := length u;
     basisvectorl!* := nil;
     if null v then
          metricd!* := nlist(1,dimex!*)
      else if car v eq 'signature
                          then if dimex!* neq length cdr v
                          then rerror(excalc,12,
                          "Dimension of coframe and metric are inconsistent.")
            else metricd!* := for each j in cdr v collect aeval j;
     if null v or (car v eq 'signature) then
       <<detm!* := simp car metricd!*;
         for each j in cdr metricd!* do
             detm!* := multsq(simp j,detm!*);
           detm!* := mk!*sq detm!*;
           metricu!* := metricd!*:= pair(indxl!*,for each j in
                           pair(indxl!*,metricd!*) collect list j)>>
      else mkmetric v;
     if flagp('partdf,'noxpnd) then remflag('(partdf),'noxpnd);
     putform('eps . indxl!*,0);
     put('eps,'indxsymmetries,
         list list('lambda,'(indl),list('tot!-asym!-indp,
                   list('evlis,mkquote for j := 1:dimex!* collect
                                        list('nth,'indl,j)))));
     put('eps,'indxsymmetrize,
         list list('lambda,'(indl),list('asymmetrize!-inds,
                   mkquote(for j := 1: dimex!* collect j),'indl)));
     flag('(eps),'covariant);
     setk('eps . for each j in indxl!* collect lowerind j,1);
     if null cdar u then return;
     keepl!* := append(for each j in u collect
                         !*a2k car j . cadr j,keepl!*);
     coframe1 for each j in u collect cadr j
  end;

symbolic procedure coframe1 u;
   begin scalar osubfg,scoord,v,y,w;
     osubfg := subfg!*;
     subfg!* := nil;
     v := for each j in u collect
            <<y := partitop j;
                   scoord := pickupcoords(y,scoord);
              y>>;
     if null atom car scoord
        then <<remflag({caar scoord},'covariant);
               scoord := for each j in scoord
                             collect mvar numr lc partitop j;
               v := for each j in u collect partitop j>>;
     if length scoord neq dimex!*
       then rerror(excalc,3,"badly formed basis");
     w := !*pf2matwrtcoords(v,scoord);
     naturalvector2framevector := v;
     subfg!* := nil;
     naturalframe2coframe := pair(scoord,
          for each j in lnrsolve(w,for each k in basisforml!*
                                       collect list !*k2q k)
              collect mk!*sqpf partitsq!* car j);
     subfg!* := osubfg;
     coord!* := scoord;
     dbaseform2base2form := pair(basisforml!*,
          for each j in v collect mk!*sqpf repartit exdfpf j)
   end;

symbolic procedure pickupcoords(u,v);
   %u is a pf, v a list. Picks up vars in exdf and declares them as
   %zero forms.
   if null u then v
    else if null eqcar(ldpf u,'d)
      then rerror(excalc,4,"badly formed basis")
    else if null v then <<putform(cadr ldpf u,0);
                          pickupcoords(red u,cadr ldpf u . nil)>>
    else if ordop(cadr ldpf u,car v)
      then if cadr ldpf u eq car v
              then pickupcoords(red u,v)
            else <<putform(cadr ldpf u,0);
                   pickupcoords(red u,cadr ldpf u . v)>>
    else pickupcoords(red u,car v . pickupcoords(!*k2pf ldpf u,cdr v));

symbolic procedure !*pf2matwrtcoords(u,v);
   if null u then nil
    else !*pf2colwrtcoords(car u,v) . !*pf2matwrtcoords(cdr u,v);

symbolic procedure !*pf2colwrtcoords(u,v);
   if null v then nil
    else if u and (cadr ldpf u eq car v)
            then lc u . !*pf2colwrtcoords(red u,cdr v)
    else (nil ./ 1) . !*pf2colwrtcoords(u,cdr v);

symbolic procedure coordp u;
   u memq coord!*;

symbolic procedure mkmetric u;
   begin scalar x,y,z,okord;
     putform(list(cadr u,nil,nil),0);
     put(cadr u,'indxsymmetries,
         '((lambda (indl) (tot!-sym!-indp
                             (evlis '((nth indl 1)
                                      (nth indl 2)))))));
     put(cadr u,'indxsymmetrize,
         '((lambda (indl) (symmetrize!-inds '(1 2) indl))));
     flag(list cadr u,'covariant);
     okord := kord!*;
     kord!* := basisforml!*;
     x := simp!* caddr u;
     y := indxl!*;
     metricu!* := t; %to make simpindexvar work;
     for each j in indxl!* do
       <<for each k in y do
           setk(list(cadr u,lowerind j,lowerind k),0);
         y := cdr y>>;
     for each j on partitsq(x,'basep) do
       if ldeg ldpf j = 2 then
           setk(list(cadr u,lowerind cadr mvar ldpf j,
                            lowerind cadr mvar ldpf j),
                mk!*sq lc j)
        else
           setk(list(cadr u,lowerind cadr mvar ldpf j,
                            lowerind cadr mvar lc ldpf j),
                mk!*sq multsq(lc j,1 ./ 2));
     kord!* := okord;
     x := for each j in indxl!* collect
            for each k in indxl!* collect
               simpindexvar list(cadr u,lowerind j,lowerind k);
         z := subfg!*;
     subfg!* := nil;
     y := lnrsolve(x,generateident length indxl!*);
         subfg!* := z;
     metricd!* := mkasmetric x;
     metricu!* := mkasmetric y;
     detm!* := mk!*sq detq x
   end;

symbolic procedure mkasmetric u;
   for each j in pair(indxl!*,u) collect
        car j . begin scalar w,z;
                  w := indxl!*;
                  for each k in cdr j do
                    <<if numr k then
                         z := (car w . mk!*sq k) . z;
                         w := cdr w>>;
                  return z
                 end;

symbolic procedure frame u;
   begin scalar y;
     putform(list(car u,nil),-1);
     flag(list car u,'covariant);
     basisvectorl!* :=
         for each j in indxl!* collect !*a2k list(car u,lowerind j);
     if null dbaseform2base2form then return;
     commutator!-of!-framevectors :=
       for each j in pickupwedges dbaseform2base2form collect
         list(cadadr j,cadadr cdr j) . mk!*sqpf mkcommutatorfv(j,
                                                 dbaseform2base2form);
     y := pair(basisvectorl!*,
               naturalvector2framevector);
     naturalvector2framevector := for each j in coord!* collect
                                      j . mk!*sqpf mknat2framv(j,y)
   end;

symbolic procedure pickupwedges u;
   pickupwedges1(u,nil);

Symbolic procedure pickupwedges1(u,v);
   if null u then v
    else if null cdar u then pickupwedges1(cdr u,v)
    else if null v then pickupwedges1((caar u . red cdar u) . cdr u,
                                      ldpf cdar u . nil)
    else if ldpf cdar u memq v
            then pickupwedges1(if red cdar u
                                  then (caar u . red cdar u) . cdr u
                                else cdr u,v)
          else   pickupwedges1(if red cdar u
                                  then (caar u . red cdar u) . cdr u
                                else cdr u,ldpf cdar u . v);

symbolic procedure mkbasevector u;
   !*a2k list(caar basisvectorl!*,lowerind u);

symbolic procedure mkcommutatorfv(u,v);
   if null v then nil
    else addpf(mkcommutatorfv1(u,mkbasevector cadaar v,cdar v),
               mkcommutatorfv(u,cdr v));

symbolic procedure mkcommutatorfv1(u,v,w);
   if null w then nil
    else if u eq  ldpf w
            then v .* negsq simp!* lc w .+ nil
    else if ordop(u,ldpf w) then nil
    else mkcommutatorfv1(u,v,red w);

symbolic procedure mknat2framv(u,v);
   if null v then nil
    else addpf(mknat2framv1(u,caar v,cdar v),mknat2framv(u,cdr v));

symbolic procedure mknat2framv1(u,v,w);
   if null w then nil
    else if u eq cadr ldpf w
            then v .* lc w .+ nil
    else if ordop(u,cadr ldpf w) then nil
    else mknat2framv1(u,v,red w);

symbolic procedure dualframe u;
   rerror(excalc,5,"Dualframe no longer supported - use frame instead");

symbolic procedure riemannconx u;
   riemconnection car u;

put('riemannconx,'stat,'rlis);

smacro procedure mkbasformsq u;
   mksq(list(caar basisforml!*,u),1);

symbolic procedure riemconnection u;
   %calculates the riemannian connection and stores it in u;
   begin
     putform(list(u,nil,nil),1);
     flag(list u,'covariant);
     put(u,'indxsymmetries,
         '((lambda (indl) (tot!-asym!-indp (evlis '((nth indl 1)
                                                    (nth indl 2)))))));
     put(u,'indxsymmetrize,
         '((lambda (indl) (asymmetrize!-inds '(1 2) indl))));

     for each j in indxl!* do
       for each k in indxl!* do if (j neq k) and indordp(j,k) then
                                 setk(list(u,lowerind j,lowerind k),0);
     riemconpart1 u;
     riemconpart2 u;
     riemconpart3 u
    end;

symbolic procedure riemconpart1 u;
   begin scalar covbaseform,indx1,indx2,indx3,varl,w,z;
     for each l in dbaseform2base2form do
       <<covbaseform := partitindexvar list(caar l,
                                            lowerind cadar l);
         for each j on cdr l do
         <<varl := cdr ldpf j;
           indx1 := cadar varl;
           indx2 := cadadr varl;
           for each y on covbaseform do
             <<w := list(u,lowerind indx1,lowerind indx2);
               z := multsq(-1 ./ 2,!*pf2sq multpfsq(lt y .+ nil,
                                                    simp!* lc j));
               setk(w,mk!*sq addsq(z,mksq(w,1)));
               indx3 := cadr ldpf y;
               z := multsq(-1 ./ 2,multsq(lc y,simp!* lc j));
               if indx1 neq indx3 then
                  if indordp(indx1,indx3) then
                     <<w := list(u,lowerind indx1,lowerind indx3);
                       setk(w,mk!*sq addsq(multsq(z,mkbasformsq indx2),
                                           mksq(w,1)))>>
                else
                     <<w := list(u,lowerind indx3,lowerind indx1);
                       setk(w,mk!*sq addsq(multsq(negsq z,
                                      mkbasformsq indx2),mksq(w,1)))>>;
               if indx2 neq indx3 then
                  if indordp(indx2,indx3) then
                     <<w := list(u,lowerind indx2,lowerind indx3);
                       setk(w,mk!*sq addsq(multsq(negsq z,
                                       mkbasformsq indx1),mksq(w,1)))>>
                else
                     <<w := list(u,lowerind indx3,lowerind indx2);
                       setk(w,mk!*sq addsq(multsq(z,
                                       mkbasformsq indx1),mksq(w,1)))>>
      >>>>>>
   end;


symbolic procedure riemconpart2 u;
   begin scalar dgkl,indx1,indx2,varl,w,z;
     if null(dgkl := mkmetricconx2 metricd!*)
        then return;
     for each j in dgkl do
       for each y on cdr j do
         <<varl := ldpf y;
           indx1 := cadar varl;
           indx2 := cadadr varl;
           w := list(u,lowerind indx1,lowerind indx2);
           z := multsq(-1 ./ 2,multsq(!*k2q car j,lc y));
           setk(w,mk!*sq addsq(z,mksq(w,1)))>>
    end;

symbolic procedure mkmetricconx2 u;
   if null u then nil
    else (if x then (ldpf mkupf list(caar basisforml!*,caar u) . x)
                     . mkmetricconx2 cdr u
           else mkmetricconx2 cdr u)
          where x = mkmetricconx21 cdar u;

symbolic procedure mkmetricconx21 u;
   if null u then nil
    else addpf(wedgepf2(exdf0 simp!* cdar u,
               !*k2pf list ldpf mkupf list(caar basisforml!*,caar u)),
               mkmetricconx21 cdr u);

symbolic procedure riemconpart3 u;
   begin scalar dg,dgk,dgkl,w,x,z;
     if null (dg := mkmetricconx3 metricd!*)
        then return;
     remprop(u,'indxsymmetries);
     remprop(u,'indxsymmetrize);
     for each j in indxl!* do
       <<if dg and (dgk := atsoc(j,dg))
            then dgk := cdr dgk
          else dgk := nil;
         for each k in indxl!* do
             if indordp(j,k) then
             <<w := list(u,lowerind j,lowerind k);
               x := if j eq k then nil ./ 1 else mksq(w,1);
               if dgk and (dgkl := atsoc(k,dgk))
                  then dgkl := cdr dgkl
                else dgkl := nil ./ 1;
               z := multsq(1 ./ 2,dgkl);
               setk(w,mk!*sq addsq(z,x));
               w := list(u,lowerind k,lowerind j);
               setk(w,mk!*sq addsq(z,negsq x))>>>>
   end;

symbolic procedure mkmetricconx3 u;
   if null u then nil
    else ((if x then (caar u . x) . mkmetricconx3 cdr u
            else mkmetricconx3 cdr u)
           where x = mkmetricconx31 cdar u);

symbolic procedure mkmetricconx31 u;
   if null u then nil
    else ((if x then (caar u . x) . mkmetricconx31 cdr u
            else mkmetricconx31 cdr u)
           where x = !*pf2sq exdf0 simp!* cdar u);

symbolic procedure basep u;
   if domainp u then nil
    else or(if sfp mvar u then basep mvar u
             else eqcar(mvar u,caar basisforml!*),
            basep lc u,basep red u);


symbolic procedure wedgefp u;
   if domainp u then nil
    else or(if sfp mvar u then wedgefp mvar u
             else eqcar(mvar u,'wedge),
            wedgefp lc u,wedgefp red u);

endmodule;

end;
