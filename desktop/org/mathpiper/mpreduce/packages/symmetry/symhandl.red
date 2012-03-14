module symhandl;
%
% Symmetry Package
%
% Author: Karin Gatermann
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


%  symhandl.red

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% functions to get the stored information of groups
% booleans first
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

symbolic procedure available!*p(group);
% returns true, if the information
% concerning irreducible representations
% of the group are in this database
begin
   if not(idp(group)) then rederr("this is no group identifier");
   return flagp(group,'available);
end;

symbolic procedure storing!*p(group);
% returns true, if the information concerning generators
% and group elements
% of the group are in this database
begin
   return flagp(group,'storing);
end;

symbolic procedure g!*element!*p(group,element);
% returns true, if element is an element of the abstract group
begin
   if memq(element,get!*elements(group)) then return t else return nil;
end;

symbolic procedure g!*generater!*p(group,element);
% returns true, if element is a generator of the abstract group
begin
  if memq(element,get!*generators(group)) then return t else return nil;
end;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% operators for abstract group
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

symbolic procedure get!*available!*groups;
% returns the available groups as a list
begin
  return get('availables,'groups);
end;

symbolic procedure get!*order(group);
% returns the order of group as integer
begin
  return length(get!*elements(group));
end;

symbolic procedure get!*elements(group);
% returns the abstract elements of group
% output list of identifiers
begin
scalar ll;
  return get(group,'elems);
end;

symbolic procedure get!*generators(group);
% returns a list abstract elements of group which generates the group
begin
   return get(group,'generators);
end;

symbolic procedure get!*generator!*relations(group);
% returns a list with relations
% which are satisfied for the generators of the group
begin
   return get(group,'relations);
end;

symbolic procedure get!*product(group,elem1,elem2);
% returns the element elem1*elem2  of  group
begin
scalar table,above,left;
    table:=get(group,'grouptable);
    above:= car table;
    left:=for each row in table collect car row;
    return get!+mat!+entry(table,
                give!*position(elem1,left),
                give!*position(elem2,above));
end;

symbolic procedure get!*inverse(group,elem);
% returns the inverse element of the element elem in group
% invlist = ((g1,g2,..),(inv1,inv2,...))
begin
scalar invlist;
    invlist:=get(group,'inverse);
    return nth(cadr invlist,give!*position(elem,car invlist));
end;

symbolic procedure give!*position(elem,ll);
begin
scalar j,found;
j:=1; found:=nil;
    while (null(found) and (j<=length(ll))) do
       <<
          if (nth(ll,j)=elem) then found:=t else j:=j+1;
       >>;
    if null(found) then rederr("error in give position");
    return j;
end;

symbolic procedure get!*elem!*in!*generators(group,elem);
% returns the element representated by the generators of group
begin
scalar ll,found,res;
    ll:=get(group,'elem!_in!_generators);
    if (elem='id) then return list('id);
    found:=nil;
    while (null(found) and (length(ll)>0)) do
      <<
         if (elem=caaar ll) then
           <<
              res:=cadr car ll;
              found:=t;
           >>;
          ll:=cdr ll;
      >>;
  if found then return res else
       rederr("error in get!*elem!*in!*generators");
end;

symbolic procedure get!*nr!*equi!*classes(group);
% returns the number of equivalence classes of group
begin
    return length(get(group,'equiclasses));
end;

symbolic procedure get!*equi!*class(group,elem);
% returns the equivalence class of the element elem  in  group
begin
scalar ll,equic,found;
    ll:=get(group,'equiclasses);
    found:=nil;
    while (null(found) and (length(ll)>0)) do
      <<
         if memq(elem,car ll) then
           <<
              equic:=car ll;
              found:=t;
           >>;
          ll:=cdr ll;
      >>;
  if found then return equic;
end;

symbolic procedure get!*all!*equi!*classes(group);
% returns the equivalence classes of the element elem  in  group
% list of lists of identifiers
begin
    return get(group,'equiclasses);
end;


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% functions to get information of real irred. representation of group
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

symbolic procedure get!*nr!*real!*irred!*reps(group);
% returns number of real irreducible representations of group
begin
  return get(group,'realrepnumber);
end;

symbolic procedure get!*real!*character(group,nr);
% returns the nr-th real character of the group group
begin
   return mk!_character(get!*real!*irreducible!*rep(group,nr));
end;

symbolic procedure get!*real!*comp!*chartype!*p(group,nr);
% returns true if the type of the real irreducible rep.
% of the group is complex
begin
  if eqcar( get(group,mkid('realrep,nr)) ,'complextype) then return t;
end;

symbolic procedure get!*real!*irreducible!*rep(group,nr);
% returns the real nr-th irreducible matrix representation of group
begin
  return mk!_resimp!_rep(append(list(group),
    cdr get(group,mkid('realrep,nr))));
end;


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% functions to get information of
%  complex irreducible representation of group
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


symbolic procedure get!*nr!*complex!*irred!*reps(group);
% returns number of complex irreducible representations of group
begin
  return get(group,'complexrepnumber);
end;

symbolic procedure get!*complex!*character(group,nr);
% returns the nr-th complex character of the group group
begin
   return mk!_character(get!*complex!*irreducible!*rep(group,nr));
end;

symbolic procedure get!*complex!*irreducible!*rep(group,nr);
% returns the complex nr-th irreduciblematrix representation of group
begin
  return mk!_resimp!_rep(append(list(group),
      get(group,mkid('complexrep,nr))));
end;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%  set information upon group
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

symbolic procedure set!*group(group,equiclasses);
%
begin
  put(group,'equiclasses,equiclasses);
end;

symbolic procedure set!*elems!*group(group,elems);
%
begin
  put(group,'elems,elems);
end;

symbolic procedure set!*generators(group,generators);
%
begin
  put(group,'generators,generators);
end;

symbolic procedure set!*relations(group,relations);
%
begin
  put(group,'relations,relations);
end;

symbolic procedure set!*available(group);
begin
scalar grouplist;
  flag(list(group),'available);
  grouplist:=get('availables,'groups);
  grouplist:=append(grouplist,list(group));
  put('availables,'groups,grouplist);
end;

symbolic procedure set!*storing(group);
begin
  flag(list(group),'storing);
end;

symbolic procedure set!*grouptable(group,table);
%
begin
  put(group,'grouptable,table);
end;

symbolic procedure set!*inverse(group,invlist);
% stores the inverse element list in group
begin
  put(group,'inverse,invlist);
end;

symbolic procedure set!*elemasgen(group,glist);
%
begin
  put(group,'elem!_in!_generators,glist);
end;

symbolic procedure set!*representation(group,replist,type);
%
begin
scalar nr;
  nr:=get(group,mkid(type,'repnumber));
  if null(nr) then nr:=0;
  nr:=nr+1;
  put(group,mkid(mkid(type,'rep),nr),replist);
  set!*repnumber(group,type,nr);
end;

symbolic procedure set!*repnumber(group,type,nr);
%
begin
  put(group,mkid(type,'repnumber),nr);
end;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%  functions to build information upon group
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

symbolic procedure mk!*inverse!*list(table);
% returns ((elem1,elem2,..),(inv1,inv2,..))
begin
scalar elemlist,invlist,elem,row,column;
  elemlist:=cdr(car (mk!+transpose!+matrix(table)));
  invlist:=for each elem in elemlist collect
    <<
      row:=give!*position(elem,elemlist);
      column:=give!*position('id,cdr nth(table,row+1));
      nth(cdr(car table),column)
    >>;
  return list(elemlist,invlist);
end;

symbolic procedure mk!*equiclasses(table);
% returns ((elem1,elem2,..),(inv1,inv2,..))
begin
scalar elemlist,restlist,s,r,tt,ts;
scalar rows,rowt,columnt,columnr,equiclasses,equic,firstrow;
  elemlist:=cdr(car (mk!+transpose!+matrix(table)));
  restlist:=elemlist;
  firstrow:=cdr car table;
  equiclasses:=nil;
  while (length(restlist)>0) do
     <<
        s:=car restlist;
        rows:=give!*position(s,elemlist);
        equic:=list(s);
        restlist:=cdr restlist;
        for each tt in elemlist do
         <<
           columnt:=give!*position(tt,firstrow);
           rowt:=give!*position(tt,elemlist);
           ts:=get!+mat!+entry(table,rows+1,columnt+1);
           columnr:=give!*position(ts,cdr nth(table,rowt+1));
           r:=nth(firstrow,columnr);
           equic:=union(equic,list(r));
           restlist:=delete(r,restlist);
         >>;
     equiclasses:=append(equiclasses,list(equic));
     >>;
  return equiclasses;
end;

endmodule;

end;
