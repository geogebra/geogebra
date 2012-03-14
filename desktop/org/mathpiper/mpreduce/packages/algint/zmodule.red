module zmodule;

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


fluid '(!*galois
        !*tra
        !*trfield
        !*trmin
        basic!-listofallsqrts
        basic!-listofnewsqrts
        commonden
        gaussiani
        listofallsqrts
        listofnewsqrts
        sqrt!-places!-alist
        taylorasslist);

exports zmodule;
imports !*multf,sqrtsinsql,sortsqrts,simp,!*q2f,actualsimpsqrt,printsf;
imports prepf,substitutesq,printsq,mapply,!*multsq,mkilist;
imports mkvecf2q,mkvec,mkidenm,invsq,multsq,negsq,addsq,gcdn;
imports !*invsq,prepsq;

symbolic procedure zmodule(w);
begin
  scalar reslist,denlist,u,commonden,basis,p1,p2,hcf;
  % w is a list of elements (place.residue)=sq.
  for each v in w do <<
    u:=cdr v;
    reslist:=u.reslist;
    denlist:=(denr u).denlist >>;
  basis:=sqrtsinsql(reslist,nil);
  if null u or null cdr u or !*galois
    then go to nochange;
  reslist:=check!-sqrts!-dependence(reslist,basis);
  denlist:=for each u in reslist
             collect denr u;
nochange:
 commonden:=mapply(function(lambda u,v;
                      multf(u,quotf(v,gcdf(u,v)))),denlist)./1;
  u:=nil;
  for each v in reslist do
    u:=(numr !*multsq(v,commonden)).u;
  reslist:=u;
    % We have effectively reversed RESLIST twice, so it is in correct
    % order.
  u:=bexprn(reslist);
  basis:=car u;
  reslist:=cdr u;
  denlist:=nil;
  while basis do <<
    p1:=reslist;
    p2:=w;
    u:=nil;
    hcf:=0;
    while p1 do <<
      if 0 neq caar p1
        then  <<
          u:=((caar p2).(caar p1)).u;
          hcf:=gcdn(hcf,caar p1) >>;
      p1:=cdr p1;
      p2:=cdr p2 >>;
    if hcf neq 1
     then u:=for each uu in u collect
               (car uu). ( (cdr uu) / hcf);
    denlist:=(prepsq !*multsq(car basis,
                              multsq(!*f2q hcf,!*invsq commonden))
                  .u).denlist;
    basis:=cdr basis;
    reslist := for each j in reslist collect cdr j>>;
  return denlist
  end;


symbolic procedure bexprn(wlist);
begin
  scalar basis,replist,w,w2,w3,p1,p2;
  % wlist is a list of sf.
  w:=reverse wlist;
  replist:=nil;
  while w do <<
    w2:=sf2df car w;
    % now ensure that all elements of w2 are in the basis.
    w3:=w2;
    while w3 do <<
      % caar is the sf,cdar a its coefficient.
      if not member(caar w3,basis)
        then <<
          basis:=(caar w3).basis;
          replist:=mapcons(replist,0) >>;
          % adds car w3 to basis.
      w3:=cdr w3 >>;
    replist:=mkilist(basis,0).replist;
    % builds a new zero representation.
    w3:=w2;
    while w3 do <<
      p1:=basis;
      p2:=car replist;
      %the list for this element.
      while p1 do <<
        if caar w3 = car p1
          then rplaca(p2,cdar w3);
        p1:=cdr p1;
        p2:=cdr p2 >>;
      w3:=cdr w3 >>;
    w:=cdr w >>;
  return mkbasis(basis,replist)
  end;


symbolic procedure mkbasis(basis,reslist);
begin
  scalar row,nbasis,nreslist,u,v;
  basis:=for each u in basis collect !*f2q u;
  % basis is a list of sq's
  % reslist is a list of representations in the form
  % ( (coeff1 coeff2 ...)    ...).
  nreslist:=mkilist(reslist,nil);
  % initialise our list-of-lists.
  trynewloop:
  row := mapovercar reslist;
  reslist := for each j in reslist collect cdr j;
  if obvindep(row,nreslist)
    then u:=nil
    else u:=lindep(row,nreslist);
  if u
    then <<
      % u contains the numbers with which to add this new item into the
      % basis.
      v:=nil;
      while nbasis do <<
        v:=addsq(car nbasis,!*multsq(car basis,car u)).v;
        nbasis:=cdr nbasis;
        u:=cdr u >>;
      nbasis:=reversip v >>
    else <<
      nreslist:=pair(row,nreslist);
      nbasis:=(car basis).nbasis
      >>;
  basis:=cdr basis;
  if basis
   then go to trynewloop;
  return nbasis.nreslist
  end;


symbolic procedure obvindep(row,matrx);
  % True if row is obviously linearly independent of the
  % Rows of the matrix.
begin scalar u;
  if null car matrx
    then return t;
  % no matrix => no dependence.
nexttry:
  if null row
    then return nil;
  if 0 iequal car row
    then go to nouse;
  u:=car matrx;
testloop:
  if 0 neq car u
    then go to nouse;
  u:=cdr u;
  if u
    then go to testloop;
  return t;
nouse:
  row:=cdr row;
  matrx:=cdr matrx;
  go to nexttry
  end;


symbolic procedure sf2df sf;
if null sf
   then nil
   else if numberp sf
    then (1 . sf).nil
    else begin
      scalar a,b,c;
      a:=sf2df lc sf;
      b:=(lpow sf .* 1) .+ nil;
      while a do <<
        c:=(!*multf(caar a,b).(cdar a)).c;
        a :=cdr a >>;
      return nconc(c,sf2df red sf)
      end;





symbolic procedure check!-sqrts!-dependence(sql,sqrtl);
% Resimplifies the list of SQs SQL,
% allowing for all dependencies among the
% sqrts in SQRTl.
begin
  scalar !*galois,sublist,sqrtsavelist,changeflag;
  sqrtsavelist:=listofallsqrts.listofnewsqrts;
  listofnewsqrts:=list mvar gaussiani;
  listofallsqrts:=list((argof mvar gaussiani) . gaussiani);
  !*galois:=t;
  for each u in sortsqrts(sqrtl,nil) do begin
    scalar v,uu;
    uu:=!*q2f simp argof u;
    v:=actualsimpsqrt uu;
    listofallsqrts:=(uu.v).listofallsqrts;
    if domainp v or mvar v neq u
      then <<
        if !*tra or !*trfield then <<
           printc u;
           printc "re-expressed as";
           printsf v >>;
        v:=prepf v;
        sublist:=(u.v) . sublist;
        changeflag:=t >>
    end;
  if changeflag then <<
    sql:=for each u in sql collect
           substitutesq(u,sublist);
    taylorasslist:=nil;
    sqrt!-places!-alist:=nil;
    basic!-listofallsqrts:=listofallsqrts;
    basic!-listofnewsqrts:=listofnewsqrts;
    if !*tra or !*trmin then <<
      printc "New set of residues are";
      mapc(sql,function printsq) >> >>
    else <<
      listofallsqrts:=car sqrtsavelist;
      listofnewsqrts:=cdr sqrtsavelist >>;
  return sql
  end;



symbolic procedure lindep(row,matrx);
  begin
    scalar m,m1,n,u,v,inverse,rowsinuse,failure;
    % Inverse is the answer from the "gaussian elimination"
    % we are doing.
    % Rowsinuse has nil for rows with no "awkward" non-zero entries.
    m1:=length car matrx;
    m:=isub1 m1;
    n:=isub1 length matrx;
    % n=length row.
    row:=mkvecf2q row;
    matrx:=mkvec for each j in matrx collect mkvecf2q j;
    inverse:=mkidenm m1;
    rowsinuse:=mkvect m;
    failure:=t;
    % initialisation complete.
    for i:=0 step 1 until n do begin
      % try to kill off i'th elements in each row.
      u:=nil;
      for j:=0 step 1 until m do <<
        % try to find a  pivot element.
        if  (null u) and
            (null getv(rowsinuse,j)) and
            (numr getv(getv(matrx,i),j))
          then u:=j >>;
      if null u
        then go to nullu;
      putv(rowsinuse,u,t);
      % it is no use trying this again ---
      % u is our pivot element.
      if u iequal m
        then go to nonetokill;
      for j:=iadd1 u step 1 until m do
        if numr getv(getv(matrx,i),j)
          then <<
            v:=negsq multsq(getv(getv(matrx,i),j),
                            invsq getv(getv(matrx,i),u));
            for k:=0 step 1 until m1 do
              putv(getv(inverse,k),j,
                addsq(getv(getv(inverse,k),j),
                  multsq(v,getv(getv(inverse,k),u))));
            for k:=0 step 1 until n do
              putv(getv(matrx,k),j,
                addsq(getv(getv(matrx,k),j),
                  multsq(v,getv(getv(matrx,k),u)))) >>;
      % We have now pivoted throughout matrix.
    nonetokill:
      % now do the same in row if necessary.
      if null numr getv(row,i)
        then go to norowop;
      v:=negsq multsq(getv(row,i),
                 invsq getv(getv(matrx,i),u));
      for k:=0 step 1 until m1 do
        putv(getv(inverse,k),m1,
          addsq(getv(getv(inverse,k),m1),
            multsq(v,getv(getv(inverse,k),u))));
      for k:=0 step 1 until n do
        putv(row,k,addsq(getv(row,k),
                     multsq(v,getv(getv(matrx,k),u))));
      u:=nil;
      for k:=0 step 1 until n do
        if numr getv(row,k)
          then u:=t;
      % if u is null then row is all 0.
      if null u
        then <<
          n:=-1;
          failure:=nil >>;
    norowop:
      if !*tra then <<
        princ "At end of cycle";
        printc row;
        printc matrx;
        printc inverse >>;
      return;
    nullu:
      % there is no pivot for this u.
      if numr getv(row,i)
        then n:=-1;
        % this element cannot be killed.
      end;
    if failure
      then return nil;
    v:=nil;
    for i:=0 step 1 until m do
      v:=(negsq getv(getv(inverse,m-i),m1)).v;
    return v
    end;

endmodule;

end;
