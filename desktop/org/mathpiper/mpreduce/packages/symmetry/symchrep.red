module symchrep;
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



% symchrep.red

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%  functions for representations in iternal structure
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

symbolic procedure mk!_internal(representation);
% transfers the user given representation structure to the
% internal structure
begin
scalar group,elems,generators,repgenerators,g,res;
  group:=get!_group!_out(representation);
  elems:=get!*elements(group);
  generators:=get!*generators(group);
  repgenerators:=mk!_rep!_relation(representation,generators);
  if not(hard!_representation!_check!_p(group,repgenerators)) then
      rederr("this is no representation");
  res:=for each g in elems collect
       list(g,
           mk!_rep!_mat(
                 get!*elem!*in!*generators(group,g),
                 repgenerators)
           );
  return append(list(group),res);
end;

symbolic procedure hard!_representation!_check!_p(group,repgenerators);
% repgenerators -- ((g1,matg1),(g2,matg2),...)
begin
scalar checkp;
  checkp:=t;
  for each relation in get!*generator!*relations(group) do
    if not(relation!_check!_p(relation,repgenerators)) then
        checkp:=nil;
  return checkp;
end;

symbolic procedure relation!_check!_p(relation,repgenerators);
begin
scalar mat1,mat2;
  mat1:=mk!_relation!_mat(car relation, repgenerators);
  mat2:=mk!_relation!_mat(cadr relation, repgenerators);
  return equal!+matrices!+p(mat1,mat2);
end;

symbolic procedure mk!_relation!_mat(relationpart,repgenerators);
begin
scalar mat1,g;
   mat1:=mk!+unit!+mat(get!+row!+nr(cadr car repgenerators));
   for each g in relationpart do
     mat1:=mk!+mat!+mult!+mat(mat1,get!_mat(g,repgenerators));
  return mat1;
end;

symbolic procedure get!_mat(elem,repgenerators);
begin
scalar found,res;
  if elem='id then
    return mk!+unit!+mat(get!+row!+nr(cadr car repgenerators));
  found:=nil;
  while ((length(repgenerators)>0) and (null found)) do
    <<
       if elem = caar repgenerators then
         <<
           res:=cadr car repgenerators;
           found := t;
         >>;
       repgenerators:=cdr repgenerators;
    >>;
  if found then return res else
       rederr("error in get_mat");
end;

symbolic procedure mk!_rep!_mat(generatorl,repgenerators);
% returns the representation matrix (internal structure)
% of a group element represented in generatorl
begin
scalar mat1;
   mat1:=mk!+unit!+mat(get!+row!+nr(cadr(car(repgenerators))));
   for each generator in generatorl do
     mat1:=mk!+mat!+mult!+mat(mat1,
                              get!_rep!_of!_generator(
                                generator,repgenerators)
                             );
   return mat1;
end;

symbolic procedure get!_rep!_of!_generator(generator,repgenerators);
% returns the representation matrix (internal structure)
% of the generator
begin
 scalar found,mate,ll;
  if (generator='id) then return mk!+unit!+mat(
                  get!+row!+nr(cadr(car(repgenerators))));
   found:=nil;
   ll:=repgenerators;
   while (not(found) and (length(ll)>0)) do
      <<
        if (caar(ll)=generator) then
           <<
              found:=t;
              mate:=cadr(car(ll));
           >>;
         ll:=cdr ll;
      >>;
  if found then return mate else
    rederr(" error in get rep of generators");
end;

symbolic procedure get!_group!_in(representation);
% returns the group of the internal data structure representation
begin
  return car representation;
end;

symbolic procedure eli!_group!_in(representation);
% returns the internal data structure representation without group
begin
  return cdr representation;
end;

symbolic procedure get!_rep!_matrix!_in(elem,representation);
% returns the matrix of the internal data structure representation
begin
scalar found,mate,replist;
   found:=nil;
   replist:=cdr representation;
   while (null(found) and length(replist)>0) do
     <<
       if ((caar(replist)) = elem) then
             <<
                mate:=cadr(car (replist));
                found:=t;
             >>;
       replist:=cdr replist;
     >>;
  if found then return mate else
       rederr("error in get representation matrix");
end;

symbolic procedure get!_dimension!_in(representation);
% returns the dimension of the representation (internal data structure)
% output is an integer
begin
   return change!+sq!+to!+int(mk!+trace(get!_rep!_matrix!_in('id,
      representation)));
end;

symbolic procedure get!_rep!_matrix!_entry(representation,elem,z,s);
% get a special value of the matrix representation of group
% get the matrix of this representatiuon corresponding
% to the element elem
% returns the matrix element of row z and column s
begin
  return get!+mat!+entry(
           get!_rep!_matrix!_in(elem,representation),
            z,s) ;
end;

symbolic procedure mk!_resimp!_rep(representation);
begin
scalar group,elem,res;
  group:=get!_group!_in(representation);
  res:=for each elem in get!*elements(group) collect
 list(elem,mk!+resimp!+mat(get!_rep!_matrix!_in(elem,representation)));
  return append(list(group),res);
end;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%  functions for characters in iternal structure
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

symbolic procedure get!_char!_group(char1);
% returns the group of the internal data structure character
begin
  return car char1;
end;

symbolic procedure get!_char!_dim(char1);
% returns the dimension of the internal data structure character
% output is an integer
begin
   return change!+sq!+to!+int(get!_char!_value(char1,'id));
end;

symbolic procedure get!_char!_value(char1,elem);
% returns the value of an element
% of the internal data structure character
begin
scalar found,value,charlist;
   found:=nil;
   charlist:=cdr char1;
   while (null(found) and length(charlist)>0) do
     <<
       if ((caar(charlist)) = elem) then
             <<
                value:=cadr(car (charlist));
                found:=t;
             >>;
       charlist := cdr charlist;
     >>;
  if found then return value else
       rederr("error in get character element");
end;

endmodule;

end;
