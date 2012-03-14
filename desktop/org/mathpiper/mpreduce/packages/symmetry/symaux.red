module symaux;  %  Data for symmetry package.

% Author: Karin Gatermann <Gatermann@sc.ZIB-Berlin.de>.

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


CREATE!-PACKAGE('(symaux
                  symatvec
                  symcheck
                  symchrep
                  symhandl
                  sympatch
                  symwork),
                '(contrib symmetry));

load!-package 'matrix;

algebraic(operator @);
algebraic( infix @);
algebraic( precedence @,*);

symbolic procedure give!_groups (u);
% prints the elements of the abstract group
begin
  return mk!+outer!+list(get!*available!*groups());
end;

put('availablegroups,'psopfn,'give!_groups);


symbolic procedure print!_group (groupname);
% prints the elements of the abstract group
begin
scalar g;
  if length(groupname)>1 then rederr("too many arguments");
  if length(groupname)<1 then rederr("group as argument missing");
  g:=reval car groupname;
  if available!*p(g) then
  return alg!:print!:group(g);
end;

put('printgroup,'psopfn,'print!_group);

symbolic procedure print!_generators (groupname);
% prints the generating elements of the abstract group
begin
scalar g;
  if length(groupname)>1 then rederr("too many arguments");
  if length(groupname)<1 then rederr("group as argument missing");
  g:=reval car groupname;
  if  available!*p(g) then
  return alg!:generators(g);
end;

put('generators,'psopfn,'print!_generators);


symbolic procedure character!_table (groupname);
% prints the characters of the group
begin
scalar g;
  if length(groupname)>1 then rederr("too many arguments");
  g:=reval car groupname;
  if available!*p(g) then
  return alg!:characters(g);
end;

put('charactertable,'psopfn,'character!_table);

symbolic procedure character!_nr (groupname);
% prints the characters of the group
begin
scalar group,nr,char1;
  if length(groupname)>2 then rederr("too many arguments");
  if length(groupname)<2 then rederr("group or number missing");
  group:=reval car groupname;
  nr:=reval cadr groupname;
  if not(available!*p(group)) then
     rederr("no information upon group available");
  if not(irr!:nr!:p(nr,group)) then
       rederr("no character with this number");
  if !*complex then
     char1:=get!*complex!*character(group,nr) else
     char1:=get!*real!*character(group,nr);
  return alg!:print!:character(char1);
end;

put('characternr,'psopfn,'character!_nr);

symbolic procedure irreducible!_rep!_table (groupname);
% prints the irreducible representations of the group
begin
scalar g;
  if length(groupname)>1 then rederr("too many arguments");
  if length(groupname)<1 then rederr("group missing");
  g:=reval car groupname;
  if available!*p(g) then
  return alg!:irr!:reps(g);
end;

put('irreduciblereptable,'psopfn,'irreducible!_rep!_table);

symbolic procedure irreducible!_rep!_nr (groupname);
% prints the irreducible representations of the group
begin
scalar g,nr;
  if length(groupname)>2 then rederr("too many arguments");
  if length(groupname)<2 then rederr("group or number missing");
  g:=reval car groupname;
  if not(available!*p(g)) then
     rederr("no information upon group available");
  nr:=reval cadr groupname;
  if not(irr!:nr!:p(nr,g)) then
       rederr("no irreducible representation with this number");
  if !*complex then
       return
    alg!:print!:rep(get!*complex!*irreducible!*rep(g,nr))
       else return
    alg!:print!:rep(get!*real!*irreducible!*rep(g,nr));
end;

put('irreduciblerepnr,'psopfn,'irreducible!_rep!_nr);

symbolic procedure canonical!_decomposition(representation);
% computes the canonical decomposition of the given representation
begin
scalar repr;
   if length(representation)>1 then rederr("too many arguments");
   repr:=reval car representation;
   if representation!:p(repr) then
   return alg!:can!:decomp(mk!_internal(repr));
end;

put('canonicaldecomposition,'psopfn,'canonical!_decomposition);

symbolic procedure sym!_character(representation);
% computes the character of the given representation
begin
scalar repr;
   if length(representation)>1 then rederr("too many arguments");
   if length(representation)<1 then
   rederr("representation list missing");
   repr:=reval car representation;
   if representation!:p(repr) then
   return alg!:print!:character(mk!_character(mk!_internal(repr))) else
     rederr("that's no representation");
end;


put('character,'psopfn,'sym!_character);

symbolic procedure symmetry!_adapted!_basis (arg);
% computes the first part of the symmetry adapted bases of
% the nr-th component
% arg = (representation,nr)
begin
scalar repr,nr,res;
   if length(arg)>2 then rederr("too many arguments");
   if length(arg)<2 then rederr("group or number missing");
   repr:=reval car arg;
   nr:=reval cadr arg;
   if representation!:p(repr) then
         repr:=mk!_internal(repr) else
         rederr("that's no representation");
   if irr!:nr!:p(nr,get!_group!_in(repr)) then
       <<
          if not(null(mk!_multiplicity(repr,nr))) then
             res:= mk!+outer!+mat(mk!_part!_sym!_all(repr,nr))
             else
             res:=nil;
       >> else
        rederr("wrong number of an irreducible representation");
   return res;
end;

put('symmetrybasis,'psopfn,'symmetry!_adapted!_basis);

symbolic procedure symmetry!_adapted!_basis!_part (arg);
% computes the first part of the symmetry adapted bases
% of the nr-th component
% arg = (representation,nr)
begin
scalar repr,nr,res;
   if length(arg)>2 then rederr("too many arguments");
   if length(arg)<2 then rederr("group or number missing");
   repr:=reval car arg;
   nr:=reval cadr arg;
   if representation!:p(repr) then
         repr:=mk!_internal(repr) else
         rederr("that's no representation");
   if irr!:nr!:p(nr,get!_group!_in(repr)) then
       <<
          if not(null(mk!_multiplicity(repr,nr))) then
             res:= mk!+outer!+mat(mk!_part!_sym1(repr,nr))
             else
             res:=nil;
       >> else
        rederr("wrong number of an irreducible representation");
   return res;
end;

put('symmetrybasispart,'psopfn,'symmetry!_adapted!_basis!_part);

symbolic procedure symmetry!_bases (representation);
% computes the complete symmetry adapted basis
begin
scalar repr,res;
   if length(representation)>1 then rederr("too many arguments");
   if length(representation)<1 then rederr("representation missing");
   repr:=reval car representation;
   if representation!:p(repr) then
     <<
         res:= mk!+outer!+mat(mk!_sym!_basis(mk!_internal(repr)));
     >> else
       rederr("that's no representation");
    return res;
end;

put('allsymmetrybases,'psopfn,'symmetry!_bases);

symbolic procedure sym!_diagonalize (arg);
% diagonalizes a matrix with respect to a given representation
begin
scalar repr,matrix1;
   if (length(arg)>2) then rederr("too many arguments");
   if (length(arg)<2) then rederr("representation or matrix missing");
   repr:=reval cadr arg;
   matrix1:=reval (car arg);
   if alg!+matrix!+p(matrix1) then
        matrix1:=mk!+inner!+mat(matrix1)
        else
        rederr("first argument must be a matrix");
   if representation!:p(repr) then
       repr:=mk!_internal(repr) else
       rederr("that's no representation");
   if symmetry!:p(matrix1,repr) then
   return mk!+outer!+mat(mk!_diagonal(
          matrix1,repr)) else
   rederr("matrix has not the symmetry of this representation");
end;

put('diagonalize,'psopfn,'sym!_diagonalize);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% function to add new groups to the database by the user
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

symbolic procedure set!_generators!_group (arg);
% a group is generated by some elements
begin
scalar group, generators,relations,rel;
   if length(arg)>3 then rederr("too many arguments");
   if length(arg)<2 then
      rederr("group identifier or generator list missing");
   group:=reval car arg;
   generators:=reval cadr arg;
   if length(arg)=3 then
          relations:=reval caddr arg else
          relations:=nil;
   if not(idp(group)) then
        rederr("first argument must be a group identifier");
   generators:=mk!+inner!+list(generators);
   if not(identifier!:list!:p(generators)) then
     rederr("second argument must be a list of generator identifiers")
        else set!*generators(group,generators);
   relations:=mk!_relation!_list(relations);
   for each rel in relations do
       if not(relation!:list!:p(group,rel)) then
          rederr("equations in generators are demanded");
   set!*relations(group,relations);
   writepri("setgenerators finished",'only);
end;

put('setgenerators,'psopfn,'set!_generators!_group);

symbolic procedure set!_elements(arg);
% each element<>id of a group has a representation
% as product of generators
% the identity is called id
begin
scalar elemreps,replist,elems,group;
   if length(arg)>2 then rederr("too many arguments");
   if length(arg)<2 then
 rederr("missing group or list with group elements with generators ");
   group:=reval car arg;
   if not(idp(group)) then
        rederr("first argument must be a group identifier");
   elemreps:=reval cadr arg;
   elemreps:=mk!_relation!_list(elemreps);
   for each replist in elemreps do
     if not(generator!:list!:p(group,cadr replist)) then
       rederr("group elements should be represented in generators");
   for each replist in elemreps do
     if not((length(car replist)=1) and idp(caar replist)) then
       rederr("first must be one group element");
   elems:= for each replist in elemreps collect caar replist;
   elems:=append(list('id),elems);
   set!*elems!*group(group,elems);
   set!*elemasgen(group,elemreps);
   writepri("setelements finished",'only);
end;

put('setelements,'psopfn,'set!_elements);

symbolic procedure set!_group!_table (arg);
% a group table gives the result of the product of two elements
begin
scalar table,group,z,s;
   if length(arg)>2 then rederr("too many arguments");
   if length(arg)<2 then
      rederr("missing group or group table as a matrix ");
   group:=reval car arg;
   if not(idp(group)) then
        rederr("first argument must be a group identifier");
   table:=reval cadr arg;
   if alg!+matrix!+p(table) then
       table:=mk!+inner!+mat(table);
   table:=for each z in table collect
        for each s in z collect prepsq(s);
   if group!:table!:p(group,table) then
     <<
        set!*grouptable(group,table);
        set!*inverse(group,mk!*inverse!*list(table));
        set!*group(group,mk!*equiclasses(table));
        set!*storing(group);
     >> else rederr("table is not a group table");
   writepri("setgrouptable finished",'only);
end;

put('setgrouptable,'psopfn,'set!_group!_table);

symbolic procedure set!_real!_rep(arg);
% store the real irreducible representations
begin
scalar replist,type;
   if length(arg)>2 then rederr("too many arguments");
   if length(arg)<2 then
      rederr("representation or type missing");
   replist:=reval car arg;
   type:=reval cadr arg;
   if (not(type= 'realtype) and not(type = 'complextype)) then
       rederr("only real or complex types possible");
   if get!*order(get!_group!_out(replist))=0 then
         rederr("elements of the groups must be set first");
   if representation!:p(replist) then
         replist:=(mk!_internal(replist));
   set!*representation(get!_group!_in(replist),
          append(list(type),cdr replist),'real);
   writepri("Rsetrepresentation finished",'only);
end;

put('Rsetrepresentation,'psopfn,'set!_real!_rep);

symbolic procedure set!_complex!_rep(arg);
% store the complex irreducible representations
begin
scalar replist;
   if length(arg)>1 then rederr("too many arguments");
   if length(arg)<1 then
      rederr("representation missing");
   replist:=reval car arg;
   if get!*order(get!_group!_out(replist))=0 then
         rederr("elements of the groups must be set first");
   if representation!:p(replist) then
         replist:=(mk!_internal(replist));
   set!*representation(get!_group!_in(replist),cdr replist,'complex);
   writepri("Csetrepresentation finished",'only);
end;

put('Csetrepresentation,'psopfn,'set!_complex!_rep);

symbolic procedure mk!_available(arg);
% group is only then made available, if all information was given
begin
scalar group;
   if length(arg)>1 then rederr("too many arguments");
   if length(arg)<1 then
      rederr("group identifier missing");
   group:=reval car arg;
   if check!:complete!:rep!:p(group) then
       set!*available(group);
   writepri("setavailable finished",'only);
end;

put('setavailable,'psopfn,'mk!_available);

symbolic procedure update!_new!_group (arg);
% stores the user defined new abstract group in a file
begin
scalar group;
   if length(arg)>2 then rederr("too many arguments");
   if length(arg)<2 then
      rederr("group or filename missing");
   group:=reval car arg;
   if available!*p(group) then write!:to!:file(group,reval cadr arg);
   writepri("storegroup finished",'only);
end;

put('storegroup,'psopfn,'update!_new!_group);

procedure loadgroups(fname);
% loads abstract groups from a file which was created from a user
% by newgroup and updategroup
begin
  in fname;
  write"group loaded";
end;
endmodule;

end;
