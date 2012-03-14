module coatesid;

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


fluid '(!*tra intvar magiclist taylorasslist taylorvariable);

exports coatessolve,vecprod,coates!-lineq;

imports !*invsq,!*multsq,negsq,!*addsq,swap,check!-lineq,non!-null!-vec,
 printsq,sqrt2top,mapvec,mksp,vecsort,addsq,mkilist,mkvec,mapply,
 taylorformp,xsubstitutesq,taylorform,taylorevaluate,multsq,
 invsq,removecmsq;

symbolic procedure coatessolve(mzero,pzero,basis,normals);
  begin scalar m,n,rightside,nnn;
    % The following is needed in int(-4x^3/(sqrt(4x^4+1)-4x^4-1),x).
      if null mzero then return 'failed;
    % if null normals
    %   then normals:=list mkilist(basis,1 ./ 1);
    %     This provides the default normalisation,
    %     viz merely a de-homogenising constraint;
    % No it doesn't - JHD May 1983 and August 1986.
    % This may be precisely the wrong constraint, as can be seen from
    % the example of SQRT(X**2-1).  Fixed 19/8/86 to amend COATESMATRIX
    % to insert a normalising constraint if none is provided.
      nnn:=max(length normals,1);
      basis:=mkvec basis;
      m:=coatesmatrix(mzero,pzero,basis,normals);
      n:=upbv m;
      rightside:=mkvect n;
      for i:=0:n do putv(rightside,n-i,(if i<nnn then 1 else nil) ./ 1);
      n:=coates!-lineq(m,rightside);
      if n eq 'failed then return 'failed;
      n:=removecmsq vecprod(n,basis);
      if !*tra then <<
        printc "Answer from linear equation solving is ";
        printsq n >>;
      return n
  end;



symbolic procedure coatesmatrix(mzero,pzero,basis,normals);
% NORMALS is a list of the normalising constraints
% that we must apply.  Thypically, this is NIL, and we have to
% invent one - see the code IF NULL NORMALS ...
begin
  scalar ans,n1,n2,j,w,save,nextflag,save!-taylors,x!-factors,
     normals!-ok,temp;
  save!-taylors:=mkvect isub1 length pzero;
  save:=taylorasslist;
  normals!-ok:=nil;
  n1:=upbv basis;
  n2:=isub1 mapply(function plus2,mzero) + max(length normals,1);
    % the number of constaints in all (counting from 0).
  taylorvariable:=intvar;
  if !*tra then <<
    printc "Basis for the functions with precisely the correct poles";
    mapvec(basis,function printsq) >>;
  ans:=mkvect n2;
  for i:=0:n2 do
    putv(ans,i,mkvect n1);
  for i:=0:n1 do begin
    scalar xmz,xpz,k;
    xmz:=mzero;
    k:=j:=0;
    xpz:=pzero;
    while xpz do <<
      newplace basicplace car xpz;
      if nextflag
        then w:=taylorformp list('binarytimes,
     getv(save!-taylors,k),
     getv(x!-factors,k))
 else if not !*tra
          then w:=taylorform xsubstitutesq(getv(basis,i),car xpz)
          else begin
            scalar flg,u,slists;
            u:=xsubstitutesq(getv(basis,i),basicplace car xpz);
            slists:=extenplace car xpz;
            for each w in sqrtsinsq(u,intvar) do
              if not assoc(w,slists)
                then flg:=w.flg;
            if flg then <<
          printc "The following square roots were not expected";
              mapc(flg,function superprint);
          printc "in the substitution";
              superprint car xpz;
              printsq getv(basis,i) >>;
            w:=taylorform xsubstitutesq(u,slists)
            end;
      putv(save!-taylors,k,w);
      k:=iadd1 k;
      for l:=0 step 1 until isub1 car xmz do <<
        astore(ans,j,i,taylorevaluate(w,l));
        j:=iadd1 j >>;
      if null normals and j=n2 then <<
 temp:=taylorevaluate(w,car xmz);
 astore(ans,j,i,temp);
 % The defaults normalising condition is that the coefficient
 % after the last zero be a non-zero.
 % Unfortunately this too may fail (JHD 21.3.87) - check for it later
 normals!-ok:=normals!-ok or numr temp >>;
      xpz:=cdr xpz;
      xmz:=cdr xmz  >>;
    nextflag:=(i < n1) and
       (getv(basis,i) = multsq(!*kk2q intvar,getv(basis,i+1)));
    if nextflag and null x!-factors then <<
      x!-factors:=mkvect upbv save!-taylors;
      xpz:=pzero;
      k:=0;
      xmz:=invsq !*kk2q intvar;
      while xpz do <<
 putv(x!-factors,k,taylorform xsubstitutesq(xmz,car xpz));
        xpz:=cdr xpz;
        k:=iadd1 k >> >>
    end;
  if null normals and null normals!-ok then <<
     if !*tra
       then printc "Our default normalisation condition was vacuous";
     astore(ans,n2,n1,1 ./ 1)>>;
  while normals do <<
    w:=car normals;
    for k:=0:n1 do <<
      astore(ans,j,k,car w);
      w:=cdr w >>;
    j:=iadd1 j;
    normals:=cdr normals >>;
  tayshorten save;
  return ans
  end;


symbolic procedure printmatrix(ans,n2,n1);
if !*tra
  then <<
    printc "Equations to be solved:";
    for i:=0:n2 do begin
      if null getv(ans,i)
        then return;
      princ "Row number ";
      princ i;
      for j:=0:n1 do
        printsq getv(getv(ans,i),j)
      end >>;



symbolic procedure vecprod(u,v);
begin
  scalar w,n;
  w:=nil ./ 1;
  n:=upbv u;
  for i:=0:n do
    w:=!*addsq(w,!*multsq(getv(u,i),getv(v,i)));
  return w
  end;



symbolic procedure coates!-lineq(m,rightside);
begin
  scalar nnn,n;
  nnn:=desparse(m,rightside);
  if nnn eq 'failed
    then return 'failed;
  m:=car nnn;
  if null m
    then <<
      n:=cddr nnn;
      goto vecprod >>;
  rightside:=cadr nnn;
  nnn:=cddr nnn;
  n:=check!-lineq(m,rightside);
  if n eq 'failed
    then return n;
  n:=jhdsolve(m,rightside,non!-null!-vec nnn);
  if n eq 'failed
    then return n;
  for i:=0:upbv n do
    if (m:=getv(nnn,i))
      then putv(n,i,m);
vecprod:
  for i:=0:upbv n do
    if null getv(n,i) then putv(n,i,nil ./ 1);
  return n
  end;



symbolic procedure jhdsolve(m,rightside,ignore);
% Returns answer to m.answer=rightside.
% Matrix m not necessarily square.
begin
  scalar ii,n1,n2,ans,u,row,swapflg,swaps;
  % The SWAPFLG is true if we have changed the order of the
  % columns and need later to invert this via SWAPS.
  n1:=upbv m;
  for i:=0:n1 do
    if (u:=getv(m,i))
      then (n2:=upbv u);
  printmatrix(m,n1,n2);
  swaps:=mkvect n2;
  for i:=0:n2 do
    putv(swaps,i,n2-i);
    % We have the SWAPS vector, which should be a vector of indices,
    % arranged like this because VECSORT sorts in decreasing order.
  for i:=0:isub1 n1 do begin
    scalar k,v,pivot;
  tryagain:
    row:=getv(m,i);
    if null row
      then go to interchange;
    % look for a pivot in row.
    k:=-1;
    for j:=0:n2 do
      if numr (pivot:=getv(row,j))
        then <<
          k:=j;
          j:=n2 >>;
    if k neq -1
      then goto newrow;
    if numr getv(rightside,i)
      then <<
        m:='failed;
    i:=sub1 n1; %Force end of loop.
        go to finished >>;
    % now interchange i and last element.
interchange:
    swap(m,i,n1);
    swap(rightside,i,n1);
    n1:=isub1 n1;
    if i iequal n1
      then goto finished
      else goto tryagain;
  newrow:
    if i neq k
      then <<
        swapflg:=t;
        swap(swaps,i,k);
      % record what we have done.
        for l:=0:n1 do
          swap(getv(m,l),i,k) >>;
    % place pivot on diagonal.
    pivot:=sqrt2top negsq !*invsq pivot;
    for j:=iadd1 i:n1 do begin
      u:=getv(m,j);
      if null u
        then return;
      v:=!*multsq(getv(u,i),pivot);
      if numr v then <<
        putv(rightside,j,
     !*addsq(getv(rightside,j),!*multsq(v,getv(rightside,i))));
        for l:=0:n2 do
   putv(u,l,!*addsq(getv(u,l),!*multsq(v,getv(row,l)))) >>
      end;
  finished:
    end;
  if m eq 'failed
    then go to failed;
    % Equations were inconsistent.
  while null (row:=getv(m,n1)) do
    n1:=isub1 n1;
  u:=nil;
  for i:=0:n2 do
    if numr getv(row,i)
      then u:='t;
  if null u
    then if numr getv(rightside,n1)
      then go to failed
      else n1:=isub1 n1;
      % Deals with a last equation which is all zero.
  if n1 > n2
    then go to failed;
    % Too many equations to satisfy.
  ans:=mkvect n2;
  n2:=n2 - ignore;
  ii:=n1;
  for i:=0 step 1 until n1 do
    if null getv(m,i)
      then ii:=iadd1 ii;
  if ii < n2 then <<
    if !*tra then <<
      printc "The equations do not completely determine the functions";
      printc "Matrix:";
      mapvec(m,function superprint);
      printc "Right-hand side:";
      superprint rightside;
      printc list("Adding new symbols for ", iadd1 ii," ... ",n2) >>;
%    FOR I:=IADD1 ii:N2 DO <<
%      U:=GENSYM();
%      MAGICLIST:=U.MAGICLIST;
%      PUTV(ANS,I,!*KK2Q U) >>;
    if !*tra then printc "If in doubt consult an expert">>;
  % now to do the back-substitution.
  % Note that the matrix is not necessarily square,
  % but that we have ensured that the non-square (underdetermiined)
  % parts are at the right
  for i:=n1 step -1 until 0 do begin
    row:=getv(m,i);
    if null row
      then return;
%    WHILE NULL NUMR GETV(ROW,II) DO II:=ISUB1 II;
    ii:=0;
    while null numr getv(row,ii) do ii:=iadd1 ii;
    u:=getv(rightside,i);
    for j:=iadd1 ii:n2 do <<
      if null getv(ans,j) then <<
        magiclist:=gensym().magiclist;
        putv(ans,j,!*kk2q car magiclist) >>;
      u:=!*addsq(u,!*multsq(getv(row,j),negsq getv(ans,j)))
      >>;
    putv(ans,ii,!*multsq(u,sqrt2top !*invsq getv(row,ii)));
%    II:=ISUB1 II;
    end;
  if swapflg
    then vecsort(swaps,list ans);
  return ans;
failed:
  if !*tra then printc "Unable to force correct zeroes";
  return 'failed
  end;



symbolic procedure desparse(matrx,rightside);
begin
  scalar vect,changed,n,m,zero,failed;
  zero := nil ./ 1;
  n:=upbv matrx;
  m:=upbv getv(matrx,0);
  vect := mkvect m;
  % for i:=0:m do putv(vect,i,zero);   %%% initialize - ach
  changed:=t;
  while changed and not failed do begin
    changed:=nil;
    for i:=0:n do
      if changed or failed
    then i:=n   % and hence quit the loop.
        else begin
          scalar nzcount,row,pivot;
          row:=getv(matrx,i);
          if null row
            then return;
          nzcount:=0;
          for j:=0:m do
            if numr getv(row,j)
              then <<
                nzcount:=iadd1 nzcount;
                pivot:=j >>;
          if nzcount = 0
            then if null numr getv(rightside,i)
              then return putv(matrx,i,nil)
              else return (failed:='failed);
          if nzcount > 1
            then return nil;
          nzcount:=getv(rightside,i);
          if null numr nzcount
            then <<
              putv(vect,pivot,zero);
       go to was!-zero >>;
   nzcount:=!*multsq(nzcount,!*invsq getv(row,pivot));
          putv(vect,pivot,nzcount);
          nzcount:=negsq nzcount;
          for i:=0:n do
            if (row:=getv(matrx,i))
              then if numr (row:=getv(row,pivot))
  then putv(rightside,i,!*addsq(getv(rightside,i),
      !*multsq(row,nzcount)));
was!-zero:
          for i:=0:n do
            if (row:=getv(matrx,i))
              then putv(row,pivot,zero);
          changed:=t;
          putv(matrx,i,nil);
          swap(matrx,i,n);
          swap(rightside,i,n);
          end;
    end;
  if failed
    then return 'failed;
  changed:=t;
  for i:=0:n do
    if getv(matrx,i)
      then changed:=nil;
  if changed
    then matrx:=nil;
    % We have completely solved the equations by these machinations.
  return matrx.(rightside . vect)
  end;


symbolic procedure astore(a,i,j,val);
   putv(getv(a,i),j,val);

endmodule;

end;
