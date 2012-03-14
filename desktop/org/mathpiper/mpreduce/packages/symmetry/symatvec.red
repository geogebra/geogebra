module symatvec;

% Symmetry

% Author : Karin Gatermann
%         Konrad-Zuse-Zentrum fuer
%         Informationstechnik Berlin
%         Heilbronner Str. 10
%         W-1000 Berlin 31
%         Germany
%         Email: Gatermann@sc.ZIB-Berlin.de

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



% symatvec.red

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%  functions for matrix vector operations
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

symbolic procedure gen!+can!+bas(dimension);
% returns the canonical basis of R^dimension as a vector list
begin
scalar eins,nullsq,i,j,ll;
   eins:=(1 ./ 1);
   nullsq:=(nil ./ 1);
   ll:= for i:=1:dimension collect
           for j:=1:dimension collect
              if i=j then eins else nullsq;
   return ll;
end;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%  matrix functions
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

symbolic procedure alg!+matrix!+p(mat1);
% returns true if the matrix is a matrix from algebraic level
begin
scalar len,elem;
  if length(mat1)<1 then rederr("should be a matrix");
  if not(car (mat1) = 'mat) then rederr("should be a matrix");
  mat1:=cdr mat1;
  if length(mat1)<1 then rederr("should be a matrix");
  len:=length(car mat1);
  for each elem in cdr mat1 do
    if not(length(elem)=len) then rederr("should be a matrix");
  return t;
end;

symbolic procedure matrix!+p(mat1);
% returns true if the matrix is a matrix in internal structure
begin
scalar dimension,z,res;
  if length(mat1)<1 then return nil;
  dimension:=length(car mat1);
  res:=t;
  for each z in cdr mat1 do
    if not(dimension = length(z)) then res:=nil;
  return res;
end;

symbolic procedure squared!+matrix!+p(mat1);
% returns true if the matrix is a matrix in internal structure
begin
  if (matrix!+p(mat1) and (get!+row!+nr(mat1) = get!+col!+nr(mat1)))
      then return t;
end;

symbolic procedure equal!+matrices!+p(mat1,mat2);
% returns true if the matrices are equal ( internal structure)
begin
scalar s,z,helpp,mathelp,sum,rulesum,rule1,rule2;
   if (same!+dim!+squared!+p(mat1,mat2)) then
       <<
           mathelp:=
             mk!+mat!+plus!+mat(mat1,
                 mk!+scal!+mult!+mat((-1 ./ 1),mat2));
           sum:=(nil ./ 1);
           for each z in mathelp do
                for each s in z do
                  if !*complex then
                     sum:=addsq(sum,multsq(s,mk!+conjugate!+sq s)) else
                     sum:=addsq(sum,multsq(s,s));
      %      print!-sq(sum);
      rulesum:=change!+sq!+to!+algnull(sum);
      if rulesum = 0 then helpp:=t else helpp:=nil;
 %     print!-sq(simp rulesum);
%           if null(numr(simp prepsq(sum))) then helpp:=t
% else helpp:=nil;
       >> else helpp:=nil;
   return helpp;
end;

symbolic procedure get!+row!+nr(mat1);
% returns the number of rows
begin
   return length(mat1);
end;

symbolic procedure get!+col!+nr(mat1);
% returns the number of columns
begin
   return length(car mat1);
end;

symbolic procedure get!+mat!+entry(mat1,z,s);
% returns the matrix element in row z and column s
begin
   return nth(nth(mat1,z),s);
end;

symbolic procedure same!+dim!+squared!+p(mat1,mat2);
% returns true if the matrices are both squared matrices
% of the same dimension
% (internal structur)
begin
  if (squared!+matrix!+p(mat1) and squared!+matrix!+p(mat2) and
       (get!+row!+nr(mat1) = get!+row!+nr(mat1)))
          then return t;
end;

symbolic procedure mk!+transpose!+matrix(mat1);
% returns the transposed matrix (internal structure)
begin
scalar z,s,tpmat1;
  if not(matrix!+p(mat1)) then rederr("no matrix in transpose");
  tpmat1:=for z:=1:get!+col!+nr(mat1) collect
           for s:=1:get!+row!+nr(mat1) collect
                get!+mat!+entry(mat1,s,z);
  return tpmat1
end;

symbolic procedure mk!+conjugate!+matrix(mat1);
% returns the matrix with conjugate elements (internal structure)
begin
scalar z,s,tpmat1;
  if not(matrix!+p(mat1)) then rederr("no matrix in conjugate matrix");
  tpmat1:=for z:=1:get!+row!+nr(mat1) collect
           for s:=1:get!+col!+nr(mat1) collect
              mk!+conjugate!+sq(get!+mat!+entry(mat1,z,s));
  return tpmat1
end;

symbolic procedure mk!+hermitean!+matrix(mat1);
% returns the transposed matrix (internal structure)
begin
   if !*complex then
   return mk!+conjugate!+matrix(mk!+transpose!+matrix(mat1)) else
   return mk!+transpose!+matrix(mat1);
end;

symbolic procedure unitarian!+p(mat1);
% returns true if matrix is orthogonal or unitarian resp.
begin
scalar mathermit,unitmat1;
  mathermit:=mk!+mat!+mult!+mat(mk!+hermitean!+matrix(mat1),mat1);
  unitmat1:=mk!+unit!+mat(get!+row!+nr(mat1));
  if equal!+matrices!+p(mathermit,unitmat1) then return t;
end;

symbolic procedure mk!+mat!+mult!+mat(mat1,mat2);
% returns a matrix= matrix1*matrix2 (internal structure)
begin
scalar dims1,dimz1,dims2,s,z,res,sum,k;
  if not(matrix!+p(mat1)) then rederr("no matrix in mult");
  if not(matrix!+p(mat2)) then rederr("no matrix in mult");
  dims1:=get!+col!+nr(mat1);
  dimz1:=get!+row!+nr(mat1);
  dims2:=get!+col!+nr( mat2);
  if not(dims1 = get!+row!+nr(mat2)) then
     rederr("matrices can not be multiplied");
  res:=for z:=1:dimz1 collect
         for s:=1:dims2 collect
           <<
              sum:=(nil ./ 1);
              for k:=1:dims1 do
               sum:=addsq(sum,
                      multsq(
                       get!+mat!+entry(mat1,z,k),
                       get!+mat!+entry(mat2,k,s)
                             )
                          );
              sum:=subs2 sum where !*sub2=t;
              sum
           >>;
   return res;
end;

symbolic procedure mk!+mat!+plus!+mat(mat1,mat2);
% returns a matrix= matrix1 + matrix2 (internal structure)
begin
scalar dims,dimz,s,z,res,sum;
  if not(matrix!+p(mat1)) then rederr("no matrix in add");
  if not(matrix!+p(mat2)) then rederr("no matrix in add");
  dims:=get!+col!+nr(mat1);
  dimz:=get!+row!+nr(mat1);
  if not(dims = get!+col!+nr(mat2)) then
          rederr("wrong dimensions in add");
  if not(dimz = get!+row!+nr(mat2)) then
          rederr("wrong dimensions in add");
  res:=for z:=1:dimz collect
          for s:=1:dims collect
           <<
            sum:=addsq(
                   get!+mat!+entry(mat1,z,s),
                   get!+mat!+entry(mat2,z,s)
                 );
              sum:=subs2 sum where !*sub2=t;
              sum
           >>;
  return res;
end;

symbolic procedure mk!+mat!*mat!*mat(mat1,mat2,mat3);
% returns a matrix= matrix1*matrix2*matrix3 (internal structure)
begin
scalar res;
  res:= mk!+mat!+mult!+mat(mat1,mat2);
  return mk!+mat!+mult!+mat(res,mat3);
end;

symbolic procedure add!+two!+mats(mat1,mat2);
% returns a matrix=( matrix1, matrix2 )(internal structure)
begin
scalar dimz,z,res;
  if not(matrix!+p(mat1)) then rederr("no matrix in add");
  if not(matrix!+p(mat2)) then rederr("no matrix in add");
  dimz:=get!+row!+nr(mat1);
  if not(dimz = get!+row!+nr(mat2)) then rederr("wrong dim in add");
  res:=for z:=1:dimz collect
      append(nth(mat1,z),nth(mat2,z));
  return res;
end;

symbolic procedure mk!+scal!+mult!+mat(scal1,mat1);
% returns a matrix= scalar*matrix (internal structure)
begin
scalar res,z,s,prod;
  if not(matrix!+p(mat1)) then rederr("no matrix in add");
  res:=for each z in mat1 collect
         for each s in z collect
           <<
              prod:=multsq(scal1,s);
              prod:=subs2 prod where !*sub2=t;
              prod
           >>;
  return res;
end;

symbolic procedure mk!+trace(mat1);
% returns the trace of the matrix (internal structure)
begin
scalar spurx,s;
  if not(squared!+matrix!+p(mat1)) then
          rederr("no square matrix in add");
  spurx :=(nil ./ 1);
  for s:=1:get!+row!+nr(mat1) do
     spurx :=addsq(spurx,get!+mat!+entry(mat1,s,s));
   spurx :=subs2 spurx where !*sub2=t;
  return spurx
end;

symbolic procedure mk!+block!+diagonal!+mat(mats);
% returns a blockdiagonal matrix from
% a list of matrices (internal structure)
begin
  if length(mats)<1 then rederr("no list in mkdiagonalmats");
  if length(mats)=1 then return car mats else
     return fill!+zeros(car mats,mk!+block!+diagonal!+mat(cdr(mats)));
end;

symbolic procedure fill!+zeros(mat1,mat2);
% returns a blockdiagonal matrix from 2 matrices (internal structure)
begin
scalar nullmat1,nullmat2;
  nullmat1:=mk!+null!+mat(get!+row!+nr(mat2),get!+col!+nr(mat1));
  nullmat2:=mk!+null!+mat(get!+row!+nr(mat1),get!+col!+nr(mat2));
  return append(add!+two!+mats(mat1,nullmat2),
                    add!+two!+mats(nullmat1,mat2));
end;

symbolic procedure mk!+outer!+mat(innermat);
% returns a matrix for algebraic level
begin
 scalar res,s,z;
 if not(matrix!+p(innermat)) then rederr("no matrix in mkoutermat");
 res:= for each z in innermat collect
        for each s in z collect
            prepsq s;
 return append(list('mat),res);
end;

symbolic procedure mk!+inner!+mat(outermat);
% returns a matrix in internal structure
begin
   scalar res,s,z;
   res:= for each z in cdr outermat collect
          for each s in z collect
             simp s;
   if matrix!+p(res) then return res else
        rederr("incorrect input in mkinnermat");
end;

symbolic procedure mk!+resimp!+mat(innermat);
% returns a matrix in internal structure
begin
   scalar res,s,z;
   res:= for each z in innermat collect
          for each s in z collect
             resimp s;
   return res;
end;

symbolic procedure mk!+null!+mat(dimz,dims);
% returns a matrix of zeros in internal structure
begin
scalar nullsq,s,z,res;
   nullsq:=(nil ./ 1);
   res:=for z:=1:dimz collect
           for s:=1:dims collect  nullsq;
  return res;
end;

symbolic procedure mk!+unit!+mat(dimension);
% returns a squared unit matrix in internal structure
begin
   return gen!+can!+bas(dimension);
end;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%  vector functions
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

symbolic procedure vector!+p(vector1);
% returns the length of a vector
% vector -- list of sqs
begin
  if length(vector1)>0 then return t;
end;

symbolic procedure get!+vec!+dim(vector1);
% returns the length of a vector
% vector -- list of sqs
begin
  return length(vector1);
end;

symbolic procedure get!+vec!+entry(vector1,elem);
% returns the length of a vector
% vector -- list of sqs
begin
  return nth(vector1,elem);
end;

symbolic procedure mk!+mat!+mult!+vec(mat1,vector1);
% returns a vector= matrix*vector (internal structure)
begin
scalar z;
  return for each z in mat1 collect
           mk!+real!+inner!+product(z,vector1);
end;

symbolic procedure mk!+scal!+mult!+vec(scal1,vector1);
% returns a vector= scalar*vector (internal structure)
begin
scalar entry,res,h;
  res:=for each entry in vector1 collect
     <<
        h:=multsq(scal1,entry);
        h:=subs2 h where !*sub2=t;
        h
     >>;
  return res;
end;

symbolic procedure mk!+vec!+add!+vec(vector1,vector2);
% returns a vector= vector1+vector2 (internal structure)
begin
scalar ent,res,h;
  res:=for ent:=1:get!+vec!+dim(vector1) collect
       <<
         h:= addsq(get!+vec!+entry(vector1,ent),
                get!+vec!+entry(vector2,ent));
         h:=subs2 h where !*sub2=t;
         h
       >>;
  return res;
end;

symbolic procedure mk!+squared!+norm(vector1);
% returns a scalar= sum vector_i^2 (internal structure)
begin
   return mk!+inner!+product(vector1,vector1);
end;

symbolic procedure my!+nullsq!+p(scal);
% returns true, if ths sq is zero
begin
   if null(numr( scal)) then return t;
end;

symbolic procedure mk!+null!+vec(dimen);
% returns a vector of zeros
begin
scalar nullsq,i,res;
   nullsq:=(nil ./ 1);
   res:=for i:=1:dimen collect nullsq;
   return res;
end;

symbolic procedure mk!+conjugate!+vec(vector1);
% returns a vector of zeros
begin
scalar z,res;
   res:=for each z in vector1  collect mk!+conjugate!+sq(z);
   return res;
end;

symbolic procedure null!+vec!+p(vector1);
% returns a true, if vector is the zero vector
begin
    if my!+nullsq!+p(mk!+squared!+norm(vector1)) then
       return t;
end;

symbolic procedure mk!+normalize!+vector(vector1);
% returns a normalized vector (internal structure)
begin
scalar scalo,vecres;
  scalo:=simp!* {'sqrt, mk!*sq(mk!+squared!+norm(vector1))};
  if my!+nullsq!+p(scalo) then
     vecres:= mk!+null!+vec(get!+vec!+dim(vector1)) else
      <<
         scalo:=simp prepsq scalo;
         scalo:=quotsq((1 ./ 1),scalo);
         vecres:= mk!+scal!+mult!+vec(scalo,vector1);
      >>;
  return vecres;
end;

symbolic procedure mk!+inner!+product(vector1,vector2);
% returns the inner product of vector1 and vector2 (internal structure)
begin
scalar z,sum,vec2;
  if not(get!+vec!+dim(vector1) = get!+vec!+dim(vector2)) then
        rederr("wrong dimensions in innerproduct");
  sum:=(nil ./ 1);
  if !*complex then vec2:=mk!+conjugate!+vec(vector2) else
    vec2:=vector2;
  for z:=1:get!+vec!+dim(vector1) do
      sum:=addsq(sum,multsq(
            get!+vec!+entry(vector1,z),
            get!+vec!+entry(vec2,z)
                           )
                );
  sum:=subs2 sum where !*sub2=t;
  return sum;
end;

symbolic procedure mk!+real!+inner!+product(vector1,vector2);
% returns the inner product of vector1 and vector2 (internal structure)
begin
scalar z,sum;
  if not(get!+vec!+dim(vector1) = get!+vec!+dim(vector2)) then
        rederr("wrong dimensions in innerproduct");
  sum:=(nil ./ 1);
  for z:=1:get!+vec!+dim(vector1) do
      sum:=addsq(sum,multsq(
            get!+vec!+entry(vector1,z),
            get!+vec!+entry(vector2,z)
                           )
                );
  sum:=subs2 sum where !*sub2=t;
  return sum;
end;

symbolic procedure mk!+Gram!+Schmid(vectorlist,vector1);
% returns a vectorlist of orthonormal vectors
% assumptions: vectorlist is orthonormal basis, internal structure
begin
scalar i,orthovec,scalo,vectors1;
  orthovec:=vector1;
  for i:=1:(length(vectorlist)) do
     <<
       scalo:= negsq(mk!+inner!+product(orthovec,nth(vectorlist,i)));
       orthovec:=mk!+vec!+add!+vec(orthovec,
          mk!+scal!+mult!+vec(scalo,nth(vectorlist,i)));
     >>;
  orthovec:=mk!+normalize!+vector(orthovec);
  if null!+vec!+p(orthovec) then
     vectors1:=vectorlist else
     vectors1:=add!+vector!+to!+list(orthovec,vectorlist);
  return vectors1
end;

symbolic procedure Gram!+Schmid(vectorlist);
% returns a vectorlist of orthonormal vectors
begin
scalar ortholist,i;
  if length(vectorlist)<1 then rederr("error in Gram Schmid");
  if vector!+p(car vectorlist) then
      ortholist:=nil
        else rederr("strange in Gram-Schmid");
  for i:=1:length(vectorlist) do
        ortholist:=mk!+Gram!+Schmid(ortholist,nth(vectorlist,i));
  return ortholist;
end;

symbolic procedure add!+vector!+to!+list(vector1,vectorlist);
% returns a list of vectors consisting of vectorlist
% and the vector1 at the end
% internal structure
begin
    return append(vectorlist,list(vector1));
end;

symbolic procedure mk!+internal!+mat(vectorlist);
% returns a matrix consisting of columns
% equal to the vectors in vectorlist
% internal structure
begin
  return mk!+transpose!+matrix(vectorlist);
end;

symbolic procedure mat!+veclist(mat1);
% returns a vectorlist consisting of the columns of the matrix
% internal structure
begin
  return mk!+transpose!+matrix(mat1);
end;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% some useful functions
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

symbolic procedure change!+sq!+to!+int(scal1);
% scal1 -- sq which is an integer
% result is a nonnegative integer
begin
  scalar nr;
  nr:=simp!* prepsq scal1;
  if (denr(nr) = 1) then return numr(nr) else
    rederr("no integer in change!+sq!+to!+int");
end;

symbolic procedure change!+int!+to!+sq(scal1);
% scal1 --  integer for example 1 oder 2 oder 3
% result is a sq
begin
  return (scal1 ./ 1);
end;

symbolic procedure change!+sq!+to!+algnull(scal1);
begin
scalar rulesum,storecomp;
           if !*complex then
              <<
                 storecomp:=t;
                 off complex;
              >> else
              <<
                 storecomp:=nil;
              >>;
          rulesum:=evalwhereexp ({'(list (list
 (REPLACEBY
   (COS (!~ X))
   (TIMES
      (QUOTIENT 1 2)
 (PLUS (EXPT E (TIMES I (!~ X))) (EXPT E (MINUS (TIMES I (!~ X))))) ))
 (REPLACEBY
   (SIN (!~ X))
   (TIMES
      (QUOTIENT 1 (times 2 i))
 (difference (EXPT E (TIMES I (!~ X)))
      (EXPT E (MINUS (TIMES I (!~ X))))) ))

))
, prepsq(scal1)});
  rulesum:=reval rulesum;
  if storecomp then on complex;
 % print!-sq(simp (rulesum));
  return rulesum;
end;

symbolic procedure mk!+conjugate!+sq(mysq);
begin
    return conjsq(mysq);
 %   return subsq(mysq,'(( i . (minus i))));
end;

symbolic procedure mk!+equation(arg1,arg2);
begin
  return list('equal,arg1,arg2);
end;

symbolic procedure outer!+equation!+p(outerlist);
begin
    if eqcar(outerlist, 'equal) then return t
end;

symbolic procedure mk!+outer!+list(innerlist);
begin
  return append (list('list),innerlist)
end;

symbolic procedure mk!+inner!+list(outerlist);
begin
   if outer!+list!+p(outerlist) then return cdr outerlist;
end;

symbolic procedure outer!+list!+p(outerlist);
begin
  if eqcar(outerlist, 'list) then return t
end;

symbolic procedure equal!+lists!+p(ll1,ll2);
begin
  return (list!+in!+list!+p(ll1,ll2) and list!+in!+list!+p(ll2,ll1));
end;

symbolic procedure list!+in!+list!+p(ll1,ll2);
begin
  if length(ll1)=0 then return t else
       return (memq(car ll1,ll2) and list!+in!+list!+p(cdr ll1,ll2));
end;

symbolic procedure print!-matrix(mat1);
begin
  writepri (mkquote mk!+outer!+mat(mat1),'only);
end;

symbolic procedure print!-sq(mysq);
begin
  writepri (mkquote prepsq(mysq),'only);
end;

endmodule;

end;
