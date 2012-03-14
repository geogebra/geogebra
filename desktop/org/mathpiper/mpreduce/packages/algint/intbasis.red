module intbasis;

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


fluid '(!*tra !*trmin excoatespoles intvar previousbasis taylorasslist
        taylorvariable);

exports completeplaces,completeplaces2,integralbasis;


symbolic procedure deleteplace(a,b);
if null b
  then nil
  else if equalplace(a,car b)
    then cdr b
    else (car b).deleteplace(a,cdr b);


symbolic procedure completeplaces(places,mults);
begin
  scalar current,cp,cm,op,om,ansp,ansm;
  if null places then return nil;       %%% ACH
loop:
  current:=basicplace car places;
  while places do <<
    if current = (basicplace car places)
      then <<
        cp:=(car places).cp;
        cm:=(car mults ).cm >>
      else <<
        op:=(car places).op;
        om:=(car mults ).om >>;
    places:=cdr places;
    mults:=cdr mults >>;
  cp:=completeplaces2(cp,cm,sqrtsinplaces cp);
  ansp:=append(car cp,ansp);
  ansm:=append(cdr cp,ansm);
  places:=op;
  mults:=om;
  cp:=op:=cm:=om:=nil;
  if places
    then go to loop
    else return ansp.ansm
  end;


symbolic procedure completeplaces2(places,mults,sqrts);
  % Adds extra places with multiplicities of 0 as necessary.
begin scalar b,p;
  sqrts:=sqrtsign(sqrts,intvar);
  b:=basicplace car places;
  p:=places;
  while p do <<
    if not(b = (basicplace car p))
      then interr "Multiple places not supported";
    sqrts:=deleteplace(extenplace car p,sqrts);
    p:=cdr p >>;
  mults:=nconc(nlist(0,length sqrts),mults);
  places:=nconc(mappend(sqrts,b),places);
  return places.mults
  end;


symbolic procedure intbasisreduction(zbasis,places,mults);
begin
  scalar i,m,n,v,w,substn,basis;
  substn:=list(intvar.intvar);
    % The X=X substitution.
  n:=upbv zbasis;
  basis:=copyvec(zbasis,n);
  taylorvariable:=intvar;
  v:=sqrtsinplaces places;
  for i:=0:n do
    w:=union(w,sqrtsinsq(getv(basis,i),intvar));
  m:=intersection(v,w);  % Used to be INTERSECT
  v:=setdiff(v,m);
  w:=setdiff(w,m);
  for each u in v do <<
    if !*tra or !*trmin then <<
      prin2t u;
      prin2t "does not occur in the functions";
      mapvec(basis,function printsq) >>;
    m:=!*q2f simp argof u;
    i:=w;
    while i and not quotf(m,!*q2f simp argof car i)
      do i:=cdr i;
    if null i
      then interr
         "Unable to find equivalent representation of branches";
    i:=car i;
    w:=delete(i,w);
    places:=subst(i,u,places);
    if !*tra or !*trmin then <<
      prin2t "replaced by";
      prin2t i >> >>;
  if (length places) neq (iadd1 n) then <<
   if !*tra
      then prin2t "Too many functions";
    basis := shorten!-basis basis;
    n:=upbv basis >>;
  m:=mkvect n;
  for i:=0:n do
    putv(m,i,cl6roweval(basis.i,places,mults,substn));
reductionloop:
  if !*tra then <<
    prin2t "Matrix before a reduction step:";
    mapvec(m,function prin2t) >>;
  v:=firstlinearrelation(m,iadd1 n);
  if null v
    then return replicatebasis(basis,(iadd1 upbv zbasis)/(n+1));
  i:=n;
  while null numr getv(v,i) do
    i:=isub1 i;
  w:=nil ./ 1;
  for j:=0:i do
    w:=!*addsq(w,!*multsq(getv(basis,j),getv(v,j)));
  w:=removecmsq multsq(w,1 ./ !*p2f mksp(intvar,1));
  if null numr w
    then <<
      mapvec(basis,function printsq);
      prin2t iadd1 i;
      interr "Basis collapses" >>;
  if !*tra then <<
    princ "Element ";
    princ iadd1 i;
    prin2t " of the basis replaced by ";
    if !*tra then
      printsq w >>;
  putv(basis,i,w);
  putv(m,i,cl6roweval(basis.i,places,mults,substn));
  goto reductionloop
  end;


symbolic procedure integralbasis(basis,places,mults,x);
begin
  scalar z,save,points,p,m,princilap!-part,m1;
  if null places
    then return basis;
  mults := for each u in mults collect min(u,0);
  % this makes sure that we impose constraints only on
  % poles, not on zeroes.
  points:=removeduplicates(for each j in places collect basicplace j);
  if points = list(x.x)
    then basis:=intbasisreduction(basis,places,mults)
    else if cdr points
      then go complex
      else <<
        substitutevec(basis,car points);
        if !*tra then <<
          prin2t "Integral basis reduction at";
          prin2t car points >>;
        basis:=intbasisreduction(basis,
                              for each j in places collect extenplace j,
                              mults);
        substitutevec(basis,antisubs(car points,x)) >>;
join:
  save:=taylorasslist;
  % we will not need te taylorevaluates at gensym.
  z:=gensym();
  places:=mapcons(places,x.list('difference,x,z));
  z:=list(x . z);
%  basis:=intbasisreduction(basis,
%                          places,
%                          nlist(0,length places),
%                          x,z);
  taylorasslist:=save;
  % ***time-hack-2***;
  if not excoatespoles
    then previousbasis:=copyvec(basis,upbv basis);
    % Save only if in COATES/FINDFUNCTION, not if in EXCOATES.
  return basis;
complex:
  while points do <<
    p:=places;
    m:=mults;
    princilap!-part:=m1:=nil;
    while p do <<
    if (car points) = (basicplace car p)
      then <<
        princilap!-part:=(extenplace car p).princilap!-part;
        m1:=(car m).m1 >>;
      p:=cdr p;
      m:=cdr m >>;
    substitutevec(basis,car points);
    if !*tra then <<
      prin2t "Integral basis reduction at";
      prin2t car points >>;
    basis:=intbasisreduction(basis,princilap!-part,m1);
    substitutevec(basis,antisubs(car points,x));
    points:=cdr points >>;
  go to join
  end;


symbolic procedure cl6roweval(basisloc,places,mults,x!-alpha);
% Evaluates a row of the matrix in Coates lemma 6.
begin
  scalar i,v,w,save,basiselement,taysave,mmults,flg;
  i:=isub1 length places;
  v:=mkvect i;
  taysave:=mkvect i;
  i:=0;
  basiselement:=getv(car basisloc,cdr basisloc);
  mmults:=mults;
  while places do <<
    w:=substitutesq(basiselement,car places);
    w:=taylorform substitutesq(w,x!-alpha);
      % The separation of these 2 is essential since the x->x-a
      % must occur after the places are chosen.
    save:=taylorasslist;
    if not flg
      then putv(taysave,i,w);
    w:=taylorevaluate(w,car mmults);
    tayshorten save;
    putv(v,i,w);
    i:=iadd1 i;
    flg:=flg or numr w;
    mmults:=cdr mmults;
    places:=cdr places >>;
  if flg
    then return v;
    % There was a non-zero element in this row.
  save:=0;
loop:
  save:=iadd1 save;
  mmults:=mults;
  i:=0;
  while mmults do <<
    w:=taylorevaluate(getv(taysave,i),save + car mmults);
    flg:=flg or numr w;
    mmults:=cdr mmults;
    putv(v,i,w);
    i:=iadd1 i >>;
  if not flg
    then go to loop;
    % Another zero row.
  putv(car basisloc,cdr basisloc,multsq(basiselement,
                                        1 ./ !*p2f mksp(intvar,save)));
  return v
  end;


symbolic procedure replicatebasis(basis,n);
if n = 1
  then basis
  else if n = 2
    then begin
      scalar b,sqintvar,len;
      len:=upbv basis;
      sqintvar:=!*kk2q intvar;
      b:=mkvect(2*len+1);
      for i:=0:len do <<
        putv(b,i,getv(basis,i));
        putv(b,i+len+1,multsq(sqintvar,getv(basis,i))) >>;
      return b
      end
    else interr "Unexpected replication request";


symbolic procedure shorten!-basis v;
begin
  scalar u,n,sfintvar;
  sfintvar:=!*kk2f intvar;
  n:=upbv v;
  for i:=0:n do begin
    scalar uu;
    uu:=getv(v,i);
    if not quotf(numr uu,sfintvar)
      then u:=uu.u
    end;
  return mkvec u
  end;


endmodule;

end;


% ***time-hack-1***;


% This is the version of CL6ROWEVAL which does not attempt to
% make multiple steps.  See $IMPLEM, item 2.

symbolic procedure cl6roweval(basiselement,places,mults,x!-alpha);
% Evaluates a row of the matrix in Coates lemma 6.
begin
  scalar i,v,w,save;
  v:=mkvect isub1 length places;
  i:=0;
  basiselement:=getv(car basiselement,cdr basiselement);
  while places do <<
    w:=substitutesq(basiselement,car places);
    w:=substitutesq(w,x!-alpha);
      % The separation of these 2 is essential since the x->x-a
      % must occur after the places are chosen.
    save:=taylorasslist;
    w:=taylorevaluate(taylorform w,car mults);
    tayshorten save;
    putv(v,i,w);
    i:=iadd1 i;
    mults:=cdr mults;
    places:=cdr places >>;
  return v
  end;

endmodule;

end;
