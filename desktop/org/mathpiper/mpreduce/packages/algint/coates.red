module coates;

% Author: James H. Davenport.

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


fluid '(!*tra !*trmin !*galois intvar magiclist nestedsqrts
        previousbasis sqrt!-intvar taylorasslist thisplace
        listofallsqrts listofnewsqrts basic!-listofallsqrts
        basic!-listofnewsqrts gaussiani !*trfield);

global '(coates!-fdi);

exports coates,makeinitialbasis,checkpoles,multbyallcombinations;


symbolic procedure coates(places,mults,x);
begin
  scalar u,tt;
  tt:=readclock();
  u:=coates!-hpfsd(places,mults);
  if !*tra or !*trmin then
    printc  list ('coates,'time,readclock()-tt,'milliseconds);
  return u
  end;


symbolic procedure coates!-real(places,mults);
begin
  scalar thisplace,u,v,save;
  if !*tra or !*trmin then <<
    princ "Find function with zeros of order:";
    printc mults;
    if !*tra then
      princ " at ";
    terpri!*(t);
    if !*tra then
      mapc(places,function printplace) >>;
%  v:=placesindiv places;
    % V is a list of all the substitutors in PLACES;
%  u:=mkunique sqrtsintree(v,intvar,nil);
%  if !*tra then <<
%    princ "Sqrts on this curve:";
%    terpri!*(t);
%    superprint u >>;
%  algnos:=mkunique for each j in places collect basicplace j;
%  if !*tra then <<
%    printc "Algebraic numbers where residues occur:";
%    superprint algnos >>;
  v:=mults;
  for each uu in places do <<
    if (car v) < 0
      then u:=(rfirstsubs uu).u;
    v:=cdr v >>;
  thisplace:=list('quotient,1,intvar);
  if member(thisplace,u)
    then <<
      v:= finitise(places,mults);
      % returns list (places,mults,power of intvar to remove.
      u:=coates!-real(car v,cadr v);
      if atom u
        then return u;
      return multsq(u,!*p2q mksp(intvar,caddr v)) >>;
% It is not sufficient to check the current value of U in FRACTIONAL...
% as we could have zeros over infinity JHD 18/8/86;
  for each uu in places do
    if rfirstsubs uu = thisplace
      then u:=append(u,mapovercar cdr uu);
  coates!-fdi:=fractional!-degree!-at!-infinity u;
% Do we need to blow everything up by a factor of two (or more)
% to avoid fractional powers at infinity?
  if coates!-fdi iequal 1
    then return coatesmodule(places,mults,intvar);
  if !*tra
    then fdi!-print();
  newplace list(intvar . thisplace,
                list(intvar,'expt,intvar,coates!-fdi));
  places := for each j in places collect fdi!-upgrade j;
  save:=taylorasslist;
  u:=coatesmodule(places,
                  for each u in mults collect u*coates!-fdi,
                  intvar);
  taylorasslist:=save;
% u:=fdi!-revertsq u;
% That previous line is junk, I think (JHD 22.8.86)
% just because we blew up the places doesn't mean that
% we should deflate the function, because that has already been done.
  return u
  end;



symbolic procedure coatesmodule(places,mults,x);
begin
  scalar pzero,mzero,u,v,basis,sqrts,magiclist,mpole,ppole;
    % MAGICLIST holds the list of extra unknowns created in JHDSOLVE
    % which must be found in CHECKPOLES (calling FINDMAGIC).
  sqrts:=sqrtsinplaces places;
  if !*tra then <<
    princ "Sqrts on this curve:";
    superprint sqrts >>;
  u:=places;
  v:=mults;
  while u do <<
    if 0<car v
      then <<
        mzero:=(car v).mzero;
        pzero:=(car u).pzero >>
      else <<
        mpole:=(car v).mpole;
        ppole:=(car u).ppole >>;
    u:=cdr u;
    v:=cdr v >>;
  % ***time-hack-2***;
  if previousbasis then basis:=previousbasis
    else basis:=mkvec makeinitialbasis ppole;
  u:=completeplaces(ppole,mpole);
  basis:=integralbasis(basis,car u,cdr u,x);
  basis:=normalbasis(basis,x,0);
  u:=coatessolve(mzero,pzero,basis,nil);
    % The NIL is the list of special constraints needed
    % to force certain poles to occur in the answer.
  if atom u
    then return u;
  v:= checkpoles(list u,places,mults);
  if null v
    then return 'failed;
  if not magiclist
    then return u;
  u:=removecmsq substitutesq(u,v);
  % Apply the values from FINDMAGIC.
  if !*tra or !*trmin then <<
    printc "These values give the function";
    printsq u >>;
  magiclist:=nil;
  if checkpoles(list u,places,mults)
    then return u
    else interr "Inconsistent checkpoles"
  end;



symbolic procedure makeinitialbasis places;
begin
  scalar u;
  u:=multbyallcombinations(list(1 ./ 1),
                           for each u in getsqrtsfromplaces places
         collect !*kk2q u);
  if !*tra then <<
    printc "Initial basis for the space m(x)";
    mapc(u,function printsq) >>;
  return u
  end;

symbolic procedure multbyallcombinations(u,l);
% Produces a list of all elements of u,
% each multiplied by every combination of elements of l.
if null l
  then u
  else multbyallcombinations(nconc(multsql(car l,u),u),cdr l);

symbolic procedure multsql(u,l);
   % Multiplies (!*multsq) each element of l by u.
   if null l then nil else !*multsq(u,car l) . multsql(u,cdr l);

symbolic procedure checkpoles(basis,places,mults);
% Checks that the BASIS really does have all the
%  poles in (PLACES.MULTS).
begin
  scalar u,v,l;
  go to outer2;
outer:
  places:=cdr places;
  mults:=cdr mults;
outer2:
  if null places
    then return if magiclist
                  then findmagic l
                  else t;
  if 0 leq car mults
    then go to outer;
  u:=basis;
inner:
  if null u
    then <<
      if !*tra
        then <<
      princ "The answer from the linear equations did";
      printc " not have the poles at:";
          printplace car places >>;
      return nil >>;
  v:=taylorform xsubstitutesq(car u,car places);
  if taylorfirst v=car mults
    then <<
      if magiclist
        then l:=taylorevaluate(v,car mults) . l;
      go to outer >>;
  if taylorfirst v < car mults
    then interr "Extraneous pole introduced";
  u:=cdr u;
  go to inner
  end;



symbolic procedure coates!-hpfsd(oplaces,omults);
begin
  scalar mzero,pzero,mpole,ppole,fun,summzero,answer,places,mults;
  places:=oplaces;
  mults:=omults;
  % Keep originals in case need to use COATES!-REAL directly.
  summzero:=0;
    % holds the sum of all the mzero's.
  while places do <<
    if 0<car mults
      then <<
        summzero:=summzero + car mults;
        mzero:=(car mults).mzero;
        pzero:=(car places).pzero >>
      else <<
        mpole:=(car mults).mpole;
        ppole:=(car places).ppole >>;
    places:=cdr places;
    mults:=cdr mults >>;
  if summzero > 2 then begin
    % We want to combine a zero/pole pair
    % so as to reduce the total index before calling coates!-real
    % on the remaining zeros/poles.
    scalar nplaces,nmults,f,multiplicity,newpole,sqrts,fz,zfound,mult1;
    sqrts:=getsqrtsfromplaces ppole;
    if !*tra or !*trmin then <<
      princ "Operate on divisor:";
      printc append(mzero,mpole);
      printc "at";
      mapc(pzero,function printplace);
      mapc(ppole,function printplace) >>;
iterate:
    nplaces:=list car pzero;
    multiplicity:=car mzero;
    nmults:=list 1;
    if cdr ppole
      then <<
        nplaces:=(car ppole) . ( (cadr ppole) . nplaces);
        multiplicity:=min(multiplicity,- car mpole,- cadr mpole);
        nmults:=(-1) .((-1) . nmults) >>
      else <<
        nplaces:=(car ppole) . nplaces;
        multiplicity:=min(multiplicity,(- car mpole)/2);
        nmults:=(-2) . nmults >>;
    previousbasis:=nil;
    f:=coates!-real(nplaces,nmults);
    if atom f
      then <<
 if !*tra or !*trmin then
          printc "Failure: must try whole divisor";
 return coates!-real(oplaces,omults) >>;
%    newpole:=removezero(findzeros(f,sqrts),car pzero).
    fz:=findzeros(f,sqrts,2);
    zfound:=assoc(car pzero,fz);
    if not zfound
       then interr list("Didn't seem to find the zero",
                        car pzero,
                        "we looked for");
    if cdr zfound > car mzero
       then interr "We found too many zeros";
    fz:=delete(zfound,fz);
    if !*tra or !*trmin then <<
      printc "Replaced by the pole";
      if fz then prettyprint fz
       else <<terpri(); prin2t "The zero we were already looking for">>;
      princ multiplicity;
      printc " times" >>;
    mult1:=car mzero - multiplicity * cdr zfound;
    if mult1 < 0
 then <<if !*tra then  printc "*** A zero has turned into a pole";
     multiplicity:= car mzero / cdr zfound ;
  mult1:=remainder(car mzero, cdr zfound); >>;
    if mult1=0
      then <<
        mzero:=cdr mzero;
        pzero:=cdr pzero >>
      else rplaca(mzero,mult1);
    if null cdr ppole
      then <<
    if (car mpole + 2*multiplicity)=0
          then <<
            ppole:=cdr ppole;
     mpole:=cdr mpole >>
          else rplaca(mpole,car mpole + 2 * multiplicity) >>
      else <<
    if (cadr mpole + multiplicity)=0
          then <<
            ppole:=(car ppole) . (cddr ppole);
            mpole:=(car mpole) . (cddr mpole) >>
          else rplaca(cdr mpole,cadr mpole + multiplicity);
    if (car mpole + multiplicity)=0
          then <<
            ppole:=cdr ppole;
            mpole:=cdr mpole >>
          else rplaca(mpole,car mpole + multiplicity) >>;
    while fz do <<
      newpole:=caar fz;
      mult1:=multiplicity*(cdar fz);
      if newpole member pzero
        then begin
          scalar m,p;
          while newpole neq car pzero do <<
            m:=(car mzero).m;
            mzero:=cdr mzero;
            p:=(car pzero).p;
            pzero:=cdr pzero >>;
          if mult1 < car mzero then <<
            mzero:=(car mzero - mult1) . cdr mzero;
            mzero:=nconc(m,mzero);
            pzero:=nconc(p,pzero);
            return >>;
          if mult1 > car mzero then <<
            ppole:=newpole.ppole;
            mpole:=(car mzero - mult1) . mpole >>;
          mzero:=nconc(m,cdr mzero);
          pzero:=nconc(p,cdr pzero)
          end
        else if newpole member ppole then begin
          scalar m,p;
          m:=mpole;
          p:=ppole;
          while newpole neq car p do <<
            p:=cdr p;
            m:=cdr m >>;
          rplaca(m,car m - mult1)
          end
        else <<
          mpole:=nconc(mpole,list(-mult1));
          ppole:=nconc(ppole,list newpole) >>;
      fz:=cdr fz >>;
    f:=mk!*sq f;
    if multiplicity > 1
      then answer:=list('expt,f,multiplicity).answer
      else answer:=f.answer;
    summzero:=0;
    for each x in mzero do summzero:=summzero+x;
    if !*tra then <<
      princ "Function is now: ";
      printc append(mzero,mpole);
      printc "at";
      mapc(pzero,function printplace);
      mapc(ppole,function printplace) >>;
    if summzero > 2
      then go to iterate;
    end;
  if pzero or ppole then << % there's something left to do
     fun:=coates!-real(nconc(pzero,ppole),
                       nconc(mzero,mpole));
     if null answer
       then return fun
       else answer:=(mk!*sq fun).answer >>;
  return !*k2q('times.answer);
    % This is not valid, but we hope that it will be unpicked;
    % (e.g. by SIMPLOG) before too much damage is caused.
  end;

% symbolic procedure removezero(l,place);
% if place member l
%   then (lambda u; if null cdr u then car u
%             else interr "Removezero") delete(place,l)
%   else interr "Error in removezeros";

symbolic procedure findzeros(sq,sqrts,nzeros);
% NZEROS is the number of zeros known, a priori, to exist
begin
  scalar u,potentials,answer,n,not!-answer,nz,series;
  u:=denr sqrt2top invsq sq;
  potentials:=for each v in jfactor(u,intvar) collect begin
    scalar w,place;
    w:=makemainvar(numr v,intvar);
    if ldeg w neq 1
      then interr "Can't cope";
    if red w
      then place:=list(intvar,'plus,intvar,prepsq(negf red w ./ lc w))
      else place:=intvar . intvar;
      % This IF .. ELSE .. added JHD 3 Sept 1980.
    return place
    end;
  potentials:=list(intvar,'quotient,1,intvar).potentials;
  for each place in potentials do begin
    scalar slist,nestedsqrts;
    place:=list place;
    newplace place;
    u:=substitutesq(sq,place);
    while involvesq(u,sqrt!-intvar) do begin
      scalar z;
      z:=list list(intvar,'expt,intvar,2);
      place:=nconc(place,z);
      newplace place;
      u:=substitutesq(u,z);
      end;
    slist:=sqrtsinsq(u,intvar);
    for each v in sqrts do
      slist:=union(slist,sqrtsinsq(xsubstitutesq(!*kk2q v,place),
       intvar));
    slist:=sqrtsign(slist,intvar);
    for each s in slist do
      if (n:=taylorfirst (series:=taylorform substitutesq(u,s))) > 0
        then answer:=(append(place,s).n).answer
        else not!-answer:=list(u,place,s,series) . not!-answer;
    return answer;
    end;
  nz:= for each u in answer sum cdr u;
  if nz = nzeros then return answer;
  if nz > nzeros
    then interr "We have too many zeros";
  if !*tra
   then printc "Couldn't find enough zeros of the function: try harder";
  for each v in not!-answer do begin
      scalar !*galois,sqrtsavelist,sublist,s;
      % If we don't reset taylorasslist, then all calculations are
      % dubious!
      taylorasslist:=nil;
      !*galois:=t;
      sqrtsavelist:=listofallsqrts.listofnewsqrts;
      listofnewsqrts:=list mvar gaussiani;
      listofallsqrts:=list((argof mvar gaussiani) . gaussiani);
      series:=cadddr v;
      s:=cdr assoc(taylorfirst series,taylorlist series);
      for each u in sortsqrts(sqrtsinsq(s,nil),nil) do begin
          scalar v,uu;
          uu:=!*q2f simp argof u;
          v:=actualsimpsqrt uu;
          listofallsqrts:=(uu.v).listofallsqrts;
          if domainp v or mvar v neq u then <<
             if !*tra or !*trfield then <<
                printc u;
                printc "re-expressed as";
                printsf v >>;
             basic!-listofnewsqrts := union(sqrtsinsf(v,nil,nil),
                                          basic!-listofnewsqrts);
             basic!-listofallsqrts := car listofallsqrts .
                                          basic!-listofallsqrts;
             v:=prepf v;
             sublist:= (u.v) .sublist;

             sqrtsavelist:=( car listofallsqrts .
                            delete(assoc(uu,car sqrtsavelist),
                                   car sqrtsavelist)).
                            delete(u,cdr sqrtsavelist)>>;
          end;
      listofallsqrts:=car sqrtsavelist;
      listofnewsqrts:=cdr sqrtsavelist;
      if sublist and null numr substitutesq(s,sublist) then <<
         if !*tra or !*trfield
           then printc "a non-zero term has become zero";
         !*galois:=nil;
                  % We'll have done all we wanted, and mustn't confuse
         if (n:=taylorfirst taylorform substitutesq(car v,caddr v)) > 0
            then << answer:= (append(cadr v,caddr v).n) . answer;
                    nz:= nz+n ;
                    if !*tra or !*trfield then
                       printc "that found us a new zero of the function"
                  >>>>;
      end;
  if nz = nzeros then return answer;
  interr "can't find enough zeros";
  end;

endmodule;

end;
