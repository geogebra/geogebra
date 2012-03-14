module hcvctors;

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


% The following set of symbolic procedures allow to manipulate
% indices of vectors in the same way as for lists. Coercion from array
% to vectors is also allowed.
% Module necessary to handle DUMMY.RED
% Only functions available in the algebraic mode are commented in
% the TeX file.

symbolic smacro procedure mkve n;
mkvect(n-1);

symbolic smacro procedure mkve!* n;
% n is an integer
% as mkvect but initialize to 0 instead of nil.
% for general tables, use mkarray1(list(i1,...),'algebraic).
mkarray1(list(n),'algebraic);

symbolic smacro procedure putve(ve,i,elt);
% To identify numerology to the one of lists.
% Use: for i:=1:upbve tri  do putve(tri,i,i); ==> [1 2 3 4]
putv(ve,i-1,elt);

symbolic smacro procedure  venth(u,i);
% To identify numerology to the one of lists.
getv(u,i-1);

symbolic smacro procedure array_to_vect u;
% For the use in the algebraic mode, it may be useful to coerce to
% ARRAYS and vice-versa
% Use: array_to_vect algebraic <array>
  cadr get(u,'avalue);

symbolic procedure mkrandtabl(u,base,ar);
% u is a list of 2 integers which determine the dimensions of the array
% base is integer  or decimal.
% Output is a table of random numbers
if not fixp base and not !*rounded then
                              rederr("ROUNDED should be on") else
begin scalar ve; integer lu;
 lu:=length(u:=alg_to_symb u);
 % if lu > 2 then typerr(u,"one or two integer list");
 ve:=mkarray1(u,'algebraic);
  if lu=1 then
    for i:=1:car u do
       putve(ve,i, if not fixp base then
         mk!*sq((make!:rd random(cdr base)) . 1)
                              else random(base)) else
  if lu=2 then <<
                  for i:=1:car u do putve(ve,i,mkve!* cadr u);
                  for i:=1:car u do for j:=1:cadr u do
                         putve(venth(ve,i),j, if not fixp base then
                               mk!*sq((make!:rd random(cdr base)) . 1)
                                               else random(base))>>
  else return typerr(u,"one or two integer list");
  vect_to_array(list(ve,ar),u);
  return symb_to_alg lengthreval list ar
end;

flag('(mkrandtabl),'opfn);

symbolic procedure upbve u;
% Should be used in FOR ... DO loops.
if null upbv u then 0 else upbv u +1;

% ILLUSTRATION of use of the above macros and function.
%for i:=1:upbve tri do
%        for j:=1:upbve venth(tri,i) do
%               putve(venth(tri,i),j,i*j);

symbolic procedure dimvect u;
% u is a vector or vector of vector or ..
% Gives the dimension of each level.
% Valid only for rectangular patterns.
% May also be used for Young tableaux to get the dimensions of the
% FIRST row and column.
if null u then nil else
  (upbv u + 1) . dimvect ((if not vectorp x then nil
  else x) where x=getv(u,0));

 symbolic procedure index_elt(elt,u);
 % elt is an atom or a number
 % return the position index.
 begin scalar idx; integer ii;
  ii:=1;
  repeat   <<if elt = venth(u,ii) then idx:=ii else nil; ii:=ii+1;>>
  until not null idx or ii=upbve u + 1;
  return idx
 end;

 symbolic procedure vect2list u;
 % Coerce a vector into a list at any level. Suitable for the
 % symbolic mode.
 for i := 0 : upbv u collect
 (if null upbv x then x
       else vect2list x) where x= getv(u,i);

symbolic procedure list_str u;
% generates the list of dimensions for the array construction.
%if not listp u  then
%          rederr "Argument to 'list_str' must be a list"
% it is supposed  to pass the test of homo_lst.
 if not listp car u  then length u  . nil
  else length u  . list_str car u;

symbolic procedure n_first_lst(u,n);
if n=0 then nil else
car u  . n_first_lst(cdr u,n-1);

symbolic procedure homo_lst(u,n);
% n indicates the level of homogeneity.
% u is the list.
% It should be filtered by depth which gives n+1 and
% generated by alg_to_symb <algebraic list>
if not listp u then
  rederr " Argument to 'homo_lst' has not the correct dimension"
else
  if n=0 then  1 else
   begin integer nl;
         scalar su;
   su:=u; nl:=length car su;
  % It is supposed here that car su is also a list.
   su:=cdr su ;
   if null su then 1;
   while su and nl= length car su do su:=cdr su;
   if null su then return
     for each i in u product homo_lst(i,n-1)
   else return 0
end;

symbolic procedure list_to_array(u,n,arr);
% Suitable for the algebraic mode.
% Defines n-dimensional arrays.
begin scalar lu;
lu:=alg_to_symb u;
<<vect_to_array(list(list2vectn(lu,n), arr),
   n_first_lst(list_str lu,n));
   remflag(list arr,'used!*)>>;
end;

flag('(list_to_array,array_to_list),'opfn);

symbolic procedure array_to_list u;
% Transforms an array into a list.
% Suitable for the algebraic mode.
% Works at all levels.
symb_to_alg vect2list array_to_vect u;

symbolic procedure list2vectn(u,n);
if n=1 then list2vect u else
begin scalar ll,x;
 if homo_lst(u,n-1)=1 then ll:=list_str u else
 rerror(alg,1,list(n,"Too large to coerce to an array"));
 x:=mkvect (first ll -1); ll:=cdr ll;
 for i:=1: upbv x +1 do putve(x,i,list2vectn(nth(u,i),n-1));
 return x
end;

symbolic procedure list2vect u; list2vect!*(u,'algebraic);

symbolic procedure list2vect!*(u,v); % replaces list2vect
% Coerce a list into a vector
% v may be either SYMBOLIC or ALGEBRAIC
  begin scalar x;
      x:=mkvect(length u -1);
         for i:=1:upbv x +1 do putve(x,i,
            if v = 'algebraic then symb_to_alg nth(u,i) else nth(u,i));
  return x end;

symbolic procedure vect_to_array(u,dim);
% u is a list (vector, array_id)
<<typechk(cadr u,'array); put(cadr u,'rtype,'array);
   put(cadr u , 'avalue, list('array, car u));
      put(cadr u, 'dimension, dim)>>;

symbolic procedure vectappend(v1,v2);
 if not vectorp v1 then typerr(v1,"vector") else
 if not vectorp v2 then vectappend1(v1,v2) else
  begin scalar new;integer dim;
    new:=mkvect(upbv v1 + upbv v2 +1 );
    dim:=upbv v1 + 1;
    for i:=1:dim do putve(new,i,venth(v1,i));
    for i:=(dim+1):(upbv new + 1) do putve(new,i,venth(v2,i-dim));
   return new
end;

symbolic procedure vectappend1(v1,v2);
   begin scalar new; integer dim;
      new:=mkvect(dim:=upbv v1 +1);
      for i:=1:dim do putve(new,i,venth(v1,i));
      putve(new,dim+1,v2);
  return new end;

symbolic procedure vectadd(v1,v2);
% v1 and v2 are supposed to be two numeric vectors.
% So we use PLUS and not SIMPPLUS.
 if not vectorp v1 or not vectorp v2 then
                                rederr("arguments must be vectors")
  else
    begin scalar vadd;
     vadd:=mkvect upbv v1;
     for i:=1:upbve v1 do putve(vadd,i, venth(v1,i)+venth(v2,i));
     return vadd
  end;

 symbolic procedure setelve(ve,l,val);
 % Sets any elements of ve, at any level to val.
 % Example of use:
 % for i:=1:upbve tri do
 %        for j:=1:upbve venth(tri,i) do
 %               setelve(tri,list(i,j),i+j);
  if null l then nil else
  if null cdr l then putve(ve,car l, val) else
  setelve(venth(ve,car l),cdr l,val);

 symbolic procedure ltrident n;
 % Constructs a lower triangular matrix of unit vectors
   begin scalar a;
    a:=mkve!* n;
    for i:=1:n do
    << putve(a,i,mkve!* i);
         for j:=1:i-1 do putve(venth(a,i), j, 0);
        putve(venth(a,i),i,1);>>;
     return a
   end;

endmodule;

end;
