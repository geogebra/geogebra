module symwork;
%
% Symmetry Package
%
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



% symwork.red
% underground functions

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% Boolean functions
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


%symbolic procedure complex!_case!_p();
% returns true, if complex arithmetic is desired
%begin
%  if !*complex then return t else return nil;
%end;

switch outerzeroscheck;

symbolic procedure correct!_diagonal!_p(matrixx,representation,mats);
% returns true, if matrix may be block diagonalized to mats
begin
scalar basis,diag;
   basis:=mk!_sym!_basis (representation);
   diag:= mk!+mat!*mat!*mat(
             mk!+hermitean!+matrix(basis),
             matrixx,basis);
   if equal!+matrices!+p(diag,mats) then return t;
end;


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% functions on data depending on real or complex case
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


symbolic procedure get!_nr!_irred!_reps(group);
% returns number of irreducible representations of group
begin
  if !*complex then
      return get!*nr!*complex!*irred!*reps(group) else
      return get!*nr!*real!*irred!*reps(group);
end;

symbolic procedure get!_dim!_irred!_reps(group,nr);
% returns dimension of nr-th irreducible representations of group
begin
scalar rep;
%   if !*complex then
%        return get!_char!_dim(get!*complex!*character(group,nr)) else
%        return get!_char!_dim(get!*real!*character(group,nr));
   if !*complex then
        rep:= get!*complex!*irreducible!*rep(group,nr) else
        rep:= get!*real!*irreducible!*rep(group,nr);
   return get!_dimension!_in(rep);
end;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%  functions for user given representations
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

symbolic procedure get!_group!_out(representation);
% returns the group identifier given in representation
begin
   scalar group,found,eintrag,repl;
   found:=nil;
   repl:=cdr representation;
   while (not(found) and (length(repl)>1)) do
     <<
        eintrag:=car repl;
        repl:=cdr repl;
        if idp(eintrag) then
          <<
             group:=eintrag;
             found:=t;
          >>;
      >>;
  if found then return group else
  rederr("group identifier missing");
end;

symbolic procedure get!_repmatrix!_out(elem,representation);
% returns the representation matrix of elem given in representation
% output in internal structure
begin
scalar repl,found,matelem,eintrag;
   found:=nil;
   repl:= cdr representation;
   while (null(found) and (length(repl)>0)) do
     <<
        eintrag:=car repl;
        repl:=cdr repl;
        if eqcar(eintrag,'equal) then
           <<
              if not(length(eintrag) = 3) then
                       rederr("incomplete equation");
              if (cadr(eintrag) = elem) then
                <<
                   found:=t;
                   matelem:=caddr eintrag;
                >>;
           >>;
     >>;
   if found then return matelem else
        rederr("representation matrix for one generator missing");
end;

symbolic procedure mk!_rep!_relation(representation,generators);
% representation in user given structure
% returns a list of pairs with generator and its representation matrix
% in internal structure
begin
scalar g,matg,res;
  res:=for each g in generators collect
     <<
        matg:= mk!+inner!+mat(get!_repmatrix!_out(g,representation));
        if not(unitarian!+p(matg)) then
             rederr("please give an orthogonal or unitarian matrix");
        list(g,matg)
      >>;
  return res;
end;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% functions which compute, do the real work, get correct arguments
%                    and use get-functions from sym_handle_data.red
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

symbolic procedure mk!_character(representation);
% returns the character of the representation (in internal structure)
% result in internal structure
begin
scalar group,elem,char;
   group:=get!_group!_in(representation);
   char:= for each elem in get!*elements(group) collect
             list(elem,
                  mk!+trace(get!_rep!_matrix!_in(
                              elem,representation)
                           )
                  );
   char:=append(list(group),char);
   return char;
end;

symbolic procedure mk!_multiplicity(representation,nr);
% returns the multiplicity of the nr-th rep. in representation
% internal structure
begin
scalar multnr,char1,group;
     group:=get!_group!_in(representation);
  if !*complex then
     char1:=mk!_character(get!*complex!*irreducible!*rep(group,nr))
         else
     char1:=mk!_character(get!*real!*irreducible!*rep(group,nr));
  multnr:=char!_prod(char1,mk!_character(representation));
% complex case factor 1/2 !!
     if (not(!*complex) and
         (get!*real!*comp!*chartype!*p(group,nr))) then
         multnr:=multsq(multnr,(1 ./ 2));
     return change!+sq!+to!+int(multnr);
end;


symbolic procedure char!_prod(char1,char2);
% returns the inner product of the two characters as sq
begin
scalar group,elems,sum,g,product;
  group:=get!_char!_group(char1);
  if not(group = get!_char!_group(char2))
      then rederr("no product for two characters of different groups");
  if not (available!*p(group)) and not(storing!*p(group)) then
       rederr("strange group in character product");
  elems:=get!*elements(group);
  sum:=nil ./ 1;
  for each g in elems do
    <<
      product:=multsq(
        get!_char!_value(char1,g),
        get!_char!_value(char2,get!*inverse(group,g))
                     );
      sum:=addsq(sum,product);
    >>;
  return quotsq(sum,change!+int!+to!+sq(get!*order(group)));
end;

symbolic procedure mk!_proj!_iso(representation,nr);
% returns the projection onto the isotypic component nr
begin
scalar group,elems,g,charnr,dimen,mapping,fact;
  group:=get!_group!_in(representation);
  if not (available!*p(group)) then
       rederr("strange group in projection");
  if not(irr!:nr!:p(nr,group)) then
       rederr("incorrect number of representation");
  elems:=get!*elements(group);
  if !*complex then
      charnr:=
          mk!_character(get!*complex!*irreducible!*rep(group,nr))
  else
      charnr:=mk!_character(get!*real!*irreducible!*rep(group,nr));
  dimen:=get!_dimension!_in(representation);
  mapping:=mk!+null!+mat(dimen,dimen);
  for each g in elems do
    <<
      mapping:=mk!+mat!+plus!+mat(
              mapping,
              mk!+scal!+mult!+mat(
        get!_char!_value(charnr,get!*inverse(group,g)),
        get!_rep!_matrix!_in(g,representation)
                                 )
                                 );
    >>;
  fact:=quotsq(change!+int!+to!+sq(get!_char!_dim(charnr)),
         change!+int!+to!+sq(get!*order(group)));
  mapping:=mk!+scal!+mult!+mat(fact,mapping);
% complex case factor 1/2 !!
  if (not(!*complex) and
    (get!*real!*comp!*chartype!*p(group,nr))) then
       mapping:=mk!+scal!+mult!+mat((1 ./ 2),mapping);
  return mapping;
end;

symbolic procedure mk!_proj!_first(representation,nr);
% returns the projection onto the first vector space of the
% isotypic component nr
begin
scalar group,elems,g,irrrep,dimen,mapping,fact,charnr,irrdim;
  group:=get!_group!_in(representation);
  if not (available!*p(group)) then
       rederr("strange group in projection");
  if not(irr!:nr!:p(nr,group)) then
       rederr("incorrect number of representation");
  elems:=get!*elements(group);
  if !*complex then
      irrrep:=get!*complex!*irreducible!*rep(group,nr) else
      irrrep:=get!*real!*irreducible!*rep(group,nr);
  dimen:=get!_dimension!_in(representation);
  mapping:=mk!+null!+mat(dimen,dimen);
  for each g in elems do
    <<
      mapping:=mk!+mat!+plus!+mat(
              mapping,
              mk!+scal!+mult!+mat(
        get!_rep!_matrix!_entry(irrrep,get!*inverse(group,g),1,1),
        get!_rep!_matrix!_in(g,representation)
                                 )
                                 );
    >>;
  irrdim:=get!_dimension!_in(irrrep);
  fact:=quotsq(change!+int!+to!+sq(irrdim),
         change!+int!+to!+sq(get!*order(group)));
  mapping:=mk!+scal!+mult!+mat(fact,mapping);
% no special rule for real irreducible representations of complex type
  return mapping;
end;

symbolic procedure mk!_mapping(representation,nr,count);
% returns the mapping from V(nr 1) to V(nr count)
% output is internal matrix
begin
scalar group,elems,g,irrrep,dimen,mapping,fact,irrdim;
  group:=get!_group!_in(representation);
  if not (available!*p(group)) then
       rederr("strange group in projection");
  if not(irr!:nr!:p(nr,group)) then
       rederr("incorrect number of representation");
  elems:=get!*elements(group);
  if !*complex then
      irrrep:=get!*complex!*irreducible!*rep(group,nr) else
      irrrep:=get!*real!*irreducible!*rep(group,nr);
  dimen:=get!_dimension!_in(representation);
  mapping:=mk!+null!+mat(dimen,dimen);
  for each g in elems do
    <<
      mapping:=mk!+mat!+plus!+mat(
              mapping,
              mk!+scal!+mult!+mat(
        get!_rep!_matrix!_entry(irrrep,get!*inverse(group,g),1,count),
        get!_rep!_matrix!_in(g,representation)
                                 )
                                 );
    >>;
  irrdim:=get!_dimension!_in(irrrep);
  fact:=quotsq(change!+int!+to!+sq(irrdim),
         change!+int!+to!+sq(get!*order(group)));
  mapping:=mk!+scal!+mult!+mat(fact,mapping);
% no special rule for real irreducible representations of complex type
  return mapping;
end;

symbolic procedure mk!_part!_sym (representation,nr);
% computes the symmetry adapted basis of component nr
% output matrix
begin
scalar unitlist, veclist2, mapping, v;
  unitlist:=gen!+can!+bas(get!_dimension!_in(representation));
  mapping:=mk!_proj!_iso(representation,nr);
  veclist2:= for each v in unitlist collect
             mk!+mat!+mult!+vec(mapping,v);
  return mk!+internal!+mat(Gram!+Schmid(veclist2));
end;

symbolic procedure mk!_part!_sym1 (representation,nr);
% computes the symmetry adapted basis of component V(nr 1)
% internal structure for in and out
% output matrix
begin
scalar unitlist, veclist2, mapping, v,group;
  unitlist:=gen!+can!+bas(get!_dimension!_in(representation));
  group:=get!_group!_in (representation);
  if (not(!*complex) and
       get!*real!*comp!*chartype!*p(group,nr)) then
     <<
        mapping:=mk!_proj!_iso(representation,nr);
     >> else
        mapping:=mk!_proj!_first(representation,nr);
  veclist2:= for each v in unitlist collect
             mk!+mat!+mult!+vec(mapping,v);
  veclist2:=mk!+resimp!+mat(veclist2);
  return mk!+internal!+mat(Gram!+Schmid(veclist2));
end;

symbolic procedure mk!_part!_symnext (representation,nr,count,mat1);
% computes the symmetry adapted basis of component V(nr count)
% internal structure for in and out -- count > 2
% bas1 -- internal matrix
% output matrix
begin
scalar veclist1, veclist2, mapping, v;
  mapping:=mk!_mapping(representation,nr,count);
  veclist1:=mat!+veclist(mat1);
  veclist2:= for each v in veclist1 collect
      mk!+mat!+mult!+vec(mapping,v);
  return mk!+internal!+mat(veclist2);
end;

symbolic procedure mk!_sym!_basis (representation);
% computes the complete symmetry adapted basis
% internal structure for in and out
begin
scalar nr,anz,group,dimen,mats,matels,mat1,mat2;
   group:=get!_group!_in(representation);
   anz:=get!_nr!_irred!_reps(group);
   mats:=for nr := 1:anz join
     if not(null(mk!_multiplicity(representation,nr))) then
     <<
       if get!_dim!_irred!_reps(group,nr)=1 then
           mat1:=mk!_part!_sym (representation,nr)
        else
           mat1:=mk!_part!_sym1 (representation,nr);
       if (not(!*complex) and
              get!*real!*comp!*chartype!*p(group,nr)) then
           <<
              matels:=list(mat1);
           >> else
           <<
              if get!_dim!_irred!_reps(group,nr)=1 then
                <<
                   matels:=list(mat1);
                >> else
                <<
                   matels:=
                  for dimen:=2:get!_dim!_irred!_reps(group,nr) collect
                        mk!_part!_symnext(representation,nr,dimen,mat1);
                   matels:=append(list(mat1),matels);
                >>;
           >>;
       matels
     >>;
  if length(mats)<1 then rederr("no mats in mk!_sym!_basis");
  mat2:=car mats;
  for each mat1 in cdr mats do
      mat2:=add!+two!+mats(mat2,mat1);
  return mat2;
end;

symbolic procedure mk!_part!_sym!_all (representation,nr);
% computes the complete symmetry adapted basis
% internal structure for in and out
begin
scalar group,dimen,matels,mat1,mat2;
   group:=get!_group!_in(representation);
   if get!_dim!_irred!_reps(group,nr)=1 then
      mat1:=mk!_part!_sym (representation,nr)
      else
        <<
           mat1:=mk!_part!_sym1 (representation,nr);
       if (not(!*complex) and
              get!*real!*comp!*chartype!*p(group,nr)) then
           <<
              mat1:=mat1;
           >> else
           <<
              if get!_dim!_irred!_reps(group,nr)>1 then
                << matels:=
                  for dimen:=2:get!_dim!_irred!_reps(group,nr) collect
                     mk!_part!_symnext(representation,nr,dimen,mat1);
                  for each mat2 in matels do
                   mat1:=add!+two!+mats(mat1,mat2);
                >>;
           >>;
        >>;
  return mat1;
end;

symbolic procedure mk!_diagonal (matrix1,representation);
% computes the matrix in diagonal form
% internal structure for in and out
begin
scalar nr,anz,mats,group,mat1,diamats,matdia,dimen;
   group:=get!_group!_in(representation);
   anz:=get!_nr!_irred!_reps(group);
   mats:=for nr := 1:anz join
     if not(null(mk!_multiplicity(representation,nr))) then
     <<
       if get!_dim!_irred!_reps(group,nr)=1 then
           mat1:=mk!_part!_sym (representation,nr)
        else
           mat1:=mk!_part!_sym1 (representation,nr);
%        if (not(!*complex) and
%              get!*real!*comp!*chartype!*p(group,nr)) then
%                mat1:=add!+two!+mats(mat1,
%                          mk!_part!_symnext(representation,nr,2,mat1));
        matdia:= mk!+mat!*mat!*mat(
                  mk!+hermitean!+matrix(mat1),matrix1,mat1
                              );
        if (not(!*complex) and
              get!*real!*comp!*chartype!*p(group,nr)) then
           <<
             diamats:=list(matdia);
           >> else
           <<
             diamats:=
                  for dimen:=1:get!_dim!_irred!_reps(group,nr) collect
                     matdia;
            >>;
        diamats
     >>;
  mats:=mk!+block!+diagonal!+mat(mats);
  if !*outerzeroscheck then
    if not(correct!_diagonal!_p(matrix1,representation,mats)) then
       rederr("wrong diagonalisation");
  return mats;
end;

endmodule;

end;
