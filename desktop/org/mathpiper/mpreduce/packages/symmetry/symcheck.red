module symcheck;
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



% symcheck.red

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%  check user input -- used by functions in sym_main.red
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

symbolic procedure representation!:p(rep);
% returns true, if rep is a representation
begin
scalar group,elem,mats,mat1,dim1;
  if length(rep)<0 then rederr("list too short");
  if not(outer!+list!+p(rep)) then rederr("argument should be a list");
  if (length(rep)<2) then rederr("empty list is not a representation");
  group:=get!_group!_out(rep);
  if not(available!*p(group) or storing!*p(group)) then
   rederr("one element must be an identifier of an available group");
  mats:=for each elem in get!*generators(group) collect
        get!_repmatrix!_out(elem,rep);
  for each mat1 in mats do
     if not(alg!+matrix!+p(mat1)) then
        rederr("there should be a matrix for each generator");
  mats:=for each mat1 in mats collect mk!+inner!+mat(mat1);
  for each mat1 in mats do
     if not(squared!+matrix!+p(mat1)) then
        rederr("matrices should be squared");
  mat1:=car mats;
  mats:=cdr mats;
  dim1:=get!+row!+nr(mat1);
  while length(mats)>0 do
    <<
      if not(dim1=get!+row!+nr(car mats)) then
         rederr("representation matrices must have the same dimension");
      mat1:=car mats;
      mats:= cdr mats;
    >>;
  return t;
end;

symbolic procedure irr!:nr!:p(nr,group);
% returns true, if group is a group and information is available
% and nr is number of an irreducible representation
begin
  if not(fixp(nr)) then rederr("nr should be an integer");
  if (nr>0 and nr<= get!_nr!_irred!_reps(group)) then
        return t;
end;

symbolic procedure symmetry!:p(matrix1,representation);
% returns true, if the matrix has the symmetry of this representation
% internal structures
begin
scalar group,glist,symmetryp,repmat;
   group:=get!_group!_in(representation);
   glist:=get!*generators(group);
   symmetryp:=t;
   while (symmetryp and (length(glist)>0)) do
      <<
         repmat:=get!_rep!_matrix!_in(car glist,representation);
         if not (equal!+matrices!+p(
                mk!+mat!+mult!+mat(repmat,matrix1),
                mk!+mat!+mult!+mat(matrix1,repmat)) ) then
                  symmetryp:=nil;
         glist:= cdr glist;
      >>;
  return symmetryp;
end;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%  check functions used by definition of the group
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

symbolic procedure identifier!:list!:p(idlist);
% returns true if idlist is a list of identifiers
begin
  if length(idlist)>0 then
      <<
         if idp(car idlist) then
             return identifier!:list!:p(cdr idlist);
      >> else
      return t;
end;

symbolic procedure generator!:list!:p(group,generatorl);
% returns true if generatorl is an idlist
% consisting of the generators of the group
begin
scalar element,res;
  res:=t;
  if length(generatorl)<1 then
              rederr("there should be a list of generators");
  if length(get!*generators(group))<1 then
              rederr("there are no group generators stored");
  if not(identifier!:list!:p(generatorl)) then return nil;
  for each element in generatorl do
        if not(g!*generater!*p(group,element)) then
            res:=nil;
  return res;
end;

symbolic procedure relation!:list!:p(group,relations);
% relations -- list of two generator lists
begin
  if length(get!*generators(group))<1 then
              rederr("there are no group generators stored");
  return (relation!:part!:p(group,car relations) and
          relation!:part!:p(group,cadr relations))
end;

symbolic procedure relation!:part!:p(group,relationpart);
% relations -- list of two generator lists
begin
scalar generators,res,element;
  res:=t;
  generators:=get!*generators(group);
  if length(generators)<1 then
              rederr("there are no group generators stored");
  if length(relationpart)<1 then
              rederr("wrong relation given");
  if not(identifier!:list!:p(relationpart)) then return nil;
  generators:=append(list('id),generators);
  for each element in relationpart do
       if not(memq(element,generators)) then res:=nil;
  return res;
end;

symbolic procedure group!:table!:p(group,gtable);
% returns true, if gtable is a group table
% gtable - matrix in internal representation
begin
scalar row;
  if not(get!+mat!+entry(gtable,1,1) = 'grouptable) then
  rederr("first diagonal entry in a group table must be grouptable");
  for each row in gtable do
       if not(group!:elemts!:p(group,cdr row)) then
            rederr("this should be a group table");
  for each row in mk!+transpose!+matrix(gtable) do
       if not(group!:elemts!:p(group,cdr row)) then
            rederr("this should be a group table");
  return t;
end;

symbolic procedure group!:elemts!:p(group,elems);
% returns true if each element of group appears exactly once in the list
begin
   return equal!+lists!+p(get!*elements(group),elems);
end;

symbolic procedure check!:complete!:rep!:p(group);
% returns true if sum ni^2 = grouporder and
%                 sum realni = sum complexni
begin
scalar nr,j,sum,dime,order1,sumreal,chars,complexcase;
    nr:=get!*nr!*complex!*irred!*reps(group);
    sum:=(nil ./ 1);
    for j:=1:nr do
       <<
         dime:=change!+int!+to!+sq( get!_dimension!_in(
                get!*complex!*irreducible!*rep(group,j)));
         sum:=addsq(sum,multsq(dime,dime));
       >>;
    order1:=change!+int!+to!+sq(get!*order(group));
    if not(null(numr(addsq(sum,negsq(order1))))) then
        rederr("one complex irreducible representation missing or
                    is not irreducible");
    sum:=(nil ./ 1);
    for j:=1:nr do
       <<
         dime:=change!+int!+to!+sq( get!_dimension!_in(
                get!*complex!*irreducible!*rep(group,j)));
         sum:=addsq(sum,dime);
       >>;
    chars:=for j:=1:nr collect
            get!*complex!*character(group,j);
    if !*complex then
      <<
        complexcase:=t;
      >> else
      <<
        complexcase:=nil;
        on complex;
      >>;
    if not(orthogonal!:characters!:p(chars)) then
          rederr("characters are not orthogonal");
    if null(complexcase) then off complex;
    nr:=get!*nr!*real!*irred!*reps(group);
    sumreal:=(nil ./ 1);
    for j:=1:nr do
       <<
         dime:=change!+int!+to!+sq( get!_dimension!_in(
                get!*real!*irreducible!*rep(group,j)));
         sumreal:=addsq(sumreal,dime);
       >>;
    chars:=for j:=1:nr collect
            get!*real!*character(group,j);
    if not(orthogonal!:characters!:p(chars)) then
          rederr("characters are not orthogonal");
    if not(null(numr(addsq(sum,negsq(sumreal))))) then
  rederr("list real irreducible representation incomplete or wrong");
  return t;
end;

symbolic procedure orthogonal!:characters!:p(chars);
% returns true if all characters in list are pairwise orthogonal
begin
scalar chars1,chars2,char1,char2;
  chars1:=chars;
  while (length(chars1)>0) do
    <<
      char1:=car chars1;
      chars1:=cdr chars1;
      chars2:=chars1;
         while (length(chars2)>0) do
            <<
               char2:=car chars2;
               chars2:=cdr chars2;
               if not(change!+sq!+to!+algnull(
                      char!_prod(char1,char2))=0)
                  then rederr("not orthogonal");
            >>;
    >>;
  return t;
end;

symbolic procedure write!:to!:file(group,filename);
begin
scalar nr,j;
if not(available!*p(group)) then rederr("group is not available");
out filename;
rprint(list
   ('off, 'echo));
rprint('symbolic);
rprint(list
   ('set!*elems!*group ,mkquote group,mkquote get!*elements(group)));
rprint(list
   ('set!*generators, mkquote group,mkquote get!*generators(group)));
rprint(list
   ('set!*relations, mkquote group,
       mkquote get!*generator!*relations(group)));
rprint(list
   ('set!*grouptable, mkquote group,mkquote get(group,'grouptable)));
rprint(list
   ('set!*inverse, mkquote group,mkquote get(group,'inverse)));
rprint(list
   ('set!*elemasgen, mkquote group
          ,mkquote get(group,'elem!_in!_generators)));
rprint(list
   ('set!*group, mkquote group,mkquote get(group,'equiclasses)));

nr:=get!*nr!*complex!*irred!*reps(group);
   for j:=1:nr do
     <<
       rprint(list
   ('set!*representation, mkquote group,
           mkquote cdr get!*complex!*irreducible!*rep(group,j),
            mkquote 'complex));
     >>;
nr:=get!*nr!*real!*irred!*reps(group);
   for j:=1:nr do
     <<
       rprint(list
   ('set!*representation, mkquote group,
           mkquote get(group,mkid('realrep,j)),mkquote 'real));
     >>;
rprint(list(
    'set!*available,mkquote group));
rprint('algebraic);
rprint('end);
shut filename;
end;


symbolic procedure mk!_relation!_list(relations);
% input: outer structure : reval of {r*s*r^2=s,...}
% output: list of pairs of lists
begin
scalar twolist,eqrel;
  if not(outer!+list!+p(relations)) then
       rederr("this should be a list");
  twolist:=for each eqrel in mk!+inner!+list(relations) collect
       change!_eq!_to!_lists(eqrel);
  return twolist;
end;

symbolic procedure change!_eq!_to!_lists(eqrel);
begin
 if not(outer!+equation!+p(eqrel)) then
    rederr("equations should be given");
 return list(mk!_side!_to!_list(reval cadr eqrel),
             mk!_side!_to!_list(reval caddr eqrel));
end;

symbolic procedure mk!_side!_to!_list(identifiers);
begin
scalar i;
  if idp(identifiers) then return list(identifiers);
  if eqcar(identifiers,'Plus) then rederr("no addition in this group");
  if eqcar(identifiers,'EXPT) then
     return for i:=1:(caddr identifiers) collect (cadr identifiers);
  if eqcar(identifiers,'TIMES) then
     rederr("no multiplication with * in this group");
if eqcar(identifiers,'!@) then
     return append(mk!_side!_to!_list(cadr identifiers),
                  mk!_side!_to!_list(caddr identifiers));
end;


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%  pass to algebraic level
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

symbolic procedure alg!:print!:group(group);
% returns the group element list in correct algebraic mode
begin
  return mk!+outer!+list(get!*elements(group));
end;

symbolic procedure alg!:generators(group);
% returns the generator list of a group in correct algebraic mode
begin
  return append(list('list),get!*generators(group));
end;

symbolic procedure alg!:characters(group);
% returns the (real od complex) character table
% in correct algebraic mode
begin
scalar nr,i,charlist,chari;
  nr:=get!_nr!_irred!_reps(group);
  charlist:=for i:=1:nr collect
     if !*complex then
        get!*complex!*character(group,i) else
        get!*real!*character(group,i);
  charlist:= for each chari in charlist collect
        alg!:print!:character(chari);
  return mk!+outer!+list(charlist);
end;

symbolic procedure alg!:irr!:reps(group);
% returns the (real od complex) irr. rep. table
% in correct algebraic mode
begin
scalar repi,reps,nr,i;
  nr:=get!_nr!_irred!_reps(group);
  reps:=for i:=1:nr collect
     if !*complex then
        get!*complex!*irreducible!*rep(group,nr) else
        get!*real!*irreducible!*rep(group,i);
  reps:= for each repi in reps collect
        alg!:print!:rep(repi);
  return mk!+outer!+list(reps);
end;

symbolic procedure alg!:print!:rep(representation);
% returns the representation in correct algebraic mode
begin
scalar pair,repr,group,mat1,g;
  group:=get!_group!_in(representation);
  repr:=eli!_group!_in(representation);
  repr:= for each pair in repr collect
      <<
          mat1:=cadr pair;
          g:=car pair;
          mat1:=mk!+outer!+mat(mat1);
          mk!+equation(g,mat1)
      >>;
  repr:=append(list(group),repr);
  return mk!+outer!+list(repr)
end;

symbolic procedure alg!:can!:decomp(representation);
% returns the canonical decomposition in correct algebraic mode
% representation in internal structure
begin
scalar nr,nrirr,ints,i,sum;
   nrirr:=get!_nr!_irred!_reps(get!_group!_in(representation));
   ints:=for nr:=1:nrirr collect
       mk!_multiplicity(representation,nr);
   sum:=( nil ./ 1);
   ints:= for i:=1:length(ints) do
      sum:=addsq(sum,
             multsq(change!+int!+to!+sq(nth(ints,i)),
                   simp mkid('teta,i)
                  )
                );
   return mk!+equation('teta,prepsq sum);
end;

symbolic procedure alg!:print!:character(character);
% changes the character from internal representation
% to printable representation
begin
scalar group,res,equilists;
  group:=get!_char!_group(character);
  res:=get!*all!*equi!*classes(group);
  res:= for each equilists in res collect
         mk!+outer!+list(equilists);
  res:= for each equilists in res collect
          mk!+outer!+list( list(equilists,
            prepsq get!_char!_value(character,cadr equilists)));
  res:=append(list(group),res);
  return mk!+outer!+list(res);
end;

endmodule;

end;
