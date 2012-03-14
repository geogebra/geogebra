module genpurfn;

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


smacro procedure be_last u;
delete(lastcar u, u);

% VARIOUS GENERAL PURPOSE FUNCTIONS

% 1. Generalization of functions already defined in the REDUCE kernel.

symbolic procedure rmkidnew(u);
if null u or null (u:=reval car u) then gensym() else mkid(u,gensym());

put('mkidnew,'psopfn,'rmkidnew); % Usage mkidnew() or mkidnew(<id>).

symbolic procedure list_to_ids l;
if atom l then rederr "argument for list_to_ids must be a list"
else
intern compress for each i in cdr l join explode i;

flag('(list_to_ids),'opfn);

symbolic procedure simpsetf u;
% generalizes the function "set" to kernels.
  begin scalar x;
     x := simp!* car u;
if not kernp x  or fixp (!*q2a x) then
                           typerr(!*q2a x,"setvalue kernel") else
      x:=!*q2a x;
     let0 list(list('equal,x,mk!*sq(u := simp!* cadr u)));
     return u
  end;

put ('setvalue, 'simpfn, 'simpsetf);

newtok '((!= !=) setvalue ! !=!=! );

infix ==;

flag('(prin2 ) ,'opfn); % To make it available in the alg. mode.


% 2. New functions closely related to existing ones.

symbolic procedure oddp u$
% Tests if integer U is odd. Is also defined in EXCALC;
not evenp u;

flag('(oddp),'boolean);

symbolic procedure followline(n)$
%It allows to go to a new line at the position  given by the integer N.
<< terpri()$ spaces(n)>>$

flag('(followline ) ,'opfn);

% 3. New general purpose functions.

% 3.1 To handle indexed identifiers.

symbolic procedure charnump!: x;
 if x member list('!0,'!1,'!2,'!3,'!4,'!5,'!6,'!7,'!8,'!9) then t ;

symbolic procedure charnump u;
if null u then t else charnump!: car u and charnump cdr u;

 symbolic procedure detidnum u;
 % Allows one to extract the index  from the identifier u.
 if idp u then
   begin scalar uu;
      if length(uu:= cdr explode u) =1 then go to l1
      else
        while not charnump uu do uu:=cdr uu;
   l1: uu:= compress uu;
      if fixp uu then return uu end;

flag('(detidnum),'opfn);

symbolic procedure dellastdigit u;
% Strips an integer from its last digit.
if fixp u then compress reverse cdr reverse explode u
else typerr(u,"integer");

flag('(dellastdigit),'opfn);

% 3.2 Random number generator.

symbolic procedure randomlist(n,trial);
% This procedure gives a list of trials in number "trial" of
% random numbers between 0 and n. For the algorithm see KNUTH vol. 2.
'list . lisp for j:=1:trial collect random n;

flag('(randomlist),'opfn);

% 3.3 Combinatorial functions, symmetry and sorting.

symbolic procedure transpose(l,i,j);
% i,j are integers, l is a list.
% DESTROYS the initial list.
 begin scalar tmp;
 tmp:=nth(l,i);
 nth(l,i):=nth(l,j);
 nth(l,j):=tmp;
 return l
end;

algebraic procedure combnum(n,nu)$
% Number of combinations of n objects nu to nu.
if nu>n then
rederr "second argument cannot be bigger than first argument"
else  factorial(n)/factorial(nu)/factorial(n-nu)$

symbolic procedure cyclicpermlist l;
% Gives all cyclic permutations of elements of the list l.
if atom l then nil else
 begin scalar x; integer le;
 l:=cdr l;
 le:=length l;
 x:= ('list . l) . x;
 for i:=2:le do x:=('list . (l:=append(cdr l,list car l))) . x;
 return 'list .  reversip x
end;

flag('(cyclicpermlist),'opfn);

symbolic procedure rpermutation u;
if not baglistp(u:=reval car u) then
   nil else if null cdr u then 'list . nil  else
 begin scalar x,prf$ prf:=car u$
    u:=cdr u$
    x:=for each j in  u
    conc  mapcons(permutations delete(j,u),j)$
    x:=for each j in x collect prf . j$
    return prf . x end;

put('permutations,'psopfn,'rpermutation);

symbolic procedure perm_to_num(nindl,indl);
% INPUT : 'indl' : a list of indices.
%         'nindl' : a permutation of 'indl'.
% OUTPUT : an INTEGER (between 0 and (indl)!-1 ) in one-to-one
%          correspondence with 'nindl' for the given 'indl'.
begin integer ln,fln,r,num,pos;
 nindl:=cdr nindl;
 if (ln:=length nindl)= 1 then return num;
 fln:=rnfactorial!* mkratnum ln;
 while ln>=1 do <<
                  << r:=rposition list(lastcar nindl,indl);
                    nindl:=for each j in be_last nindl collect
                       <<pos:=rposition list(j,indl);
                          if pos>r then nth(cdr indl,pos-1) else j
                       >>;
                    fln:=fln/ln; num:=num + (ln-r)*fln;
                   >>;
       ln:=ln-1 >>;
return num
end;

symbolic procedure num_to_perm(num,indl);
% Does the reverse job. num is an INTEGER. indl is a list of numbers.
% Constructs the corresponding permutation list starting from indl.
begin integer rk,j,f,m,lst; scalar nindl;
indl:=cdr indl;
rk:=length indl;
f:=rnfactorial!* mkratnum rk;
while rk>=1 do <<
                  <<f:=f/rk; m:=rnfloor!* mkratnum(num/f);
                    num:=num-m*f; j:=rk-m;
                    lst:=nth(indl,j); indl:=remove(indl,j);
                    nindl:=lst . nindl>>;
                  rk:=rk-1
                >>;
return 'list . nindl
end;

flag('(perm_to_num num_to_perm),'opfn);

symbolic procedure !:comb(u)$
begin scalar x,prf; integer n;
 if length u neq 2 then
      rederr "combinations called with wrong number of arguments";
 x:=reval car u ;  if not baglistp x then return nil ;
 prf :=car x; x:=cdr x; n:=reval cadr u;
 return prf . (for each j in comb(x,n) collect prf . j)
end;

put('combinations,'psopfn,'!:comb);

put('symmetrize,'simpfn,'simpsumsym);

flag('(symmetrize),'listargp);

symbolic procedure simpsumsym(u);
% The use is SYMMETRIZE(LIST(A,B,...J),operator,perm_function)
% or SYMMETRIZE(LIST(LIST(A,B,C...)),operator,perm_function).
% Works both for OPFN and symbolic procedure functions.
% Does not yet allow odd permutations.
if length u neq 3 then rederr("3 arguments required for symmetrize")
else
begin scalar uu,x,res,oper,fn,bool,boolfn; integer n;
  fn:= caddr u;
  if not(gettype fn eq 'procedure) then typerr(fn,"procedure");
  uu:=(if flagp(fn,'opfn) then <<boolfn:=t; reval x>>
          else cdr reval x) where x=car u;
  n:=length uu;
  oper:=cadr u;
   if not idp oper then typerr(oper,"operator") else
   if null flagp(oper,'opfn) then
     if null get(oper,'simpfn) then put(oper,'simpfn,'simpiden);
     flag(list oper, 'listargp);
  x:=if listp  car uu and not boolfn then
                <<bool:=t;apply1(fn, cdar uu)>> else
     if boolfn and listp cadr uu then
                <<bool:=t;apply1(fn,cadr uu)>> else
                        apply1(fn,uu);
  if flagp(fn,'opfn) then x:=alg_to_symb x;
  n:=length x -1;
  if not bool then <<
 res:=( oper . car x) .** 1 .* 1 .+ nil;
 for i:=1:n do << uu:=cadr x; aconc(res,(oper . uu) .** 1 .* 1 );
                                       delqip(uu,x);>>;
           >>
  else
 << res:=(oper . list('list .
      for each i in car x collect mk!*sq simp!* i)) .** 1 .* 1 .+ nil;
    for i:=1:n do << uu:=cadr x;
    aconc(res,(oper . list('list .
          for each i in uu collect mk!*sq simp!* i)) .** 1 .* 1 );
     delqip(uu,x);>>;
  >>;
  if get(oper,'opmtch) or flagp(oper,'opfn) then
        res:=resimp( res ./ 1) else res:=res ./ 1;
return  res
end;

symbolic procedure sortnumlist l;
% Procedure valid only for list of integers.
% Returns the sorted list without destroying l.
'list . (if length x < 10 then bubblesort1 x else
   quicksort_i_to_j(x,1,length x)) where x=cdr l ;

flag('(sortnumlist),'opfn);

symbolic procedure sortlist(l,fn);
if numlis cdr l  then
      if fn eq 'lessp  then sortnumlist l else
      if fn eq 'geq then
      ( 'list . (reverse(if length x <10 then bubblesort1 x else
               quicksort_i_to_j(x,1,length x))) where x=cdr l) else
                             nil else
'list . bubsort1(cdr l,fn);

flag('(sortlist),'opfn);

symbolic procedure bubblesort1 l;
% Elements of l are supposed to be numbers.
begin integer ln;
ln:=length l;
for i:=1:ln do
    for j:=i+1:ln do
           if i neq j and nth(l,i)>nth(l,j) then
            transpose(l,i,j) else nil;
return l
end;

symbolic procedure bubsort1(l,fn);
% Elements of l are numbers or identifiers.
% fn is any ordering function.
begin integer ln;
ln:=length l;
for i:=1:ln do
    for j:=i+1:ln do
           if i neq j and
                apply2(fn,nth(l,j),nth(l,i)) then
            transpose(l,i,j) else nil;
return l
end;

symbolic procedure find_pivot_index(l,i,j);
% l is the list, i and  j are integers.
begin scalar key; integer k;
key:=nth(l,i);
k:=i+1;
a: if k=j+1 then return -1;
if nth(l,k) > key then return k else
if nth(l,k) < key then return i;
 k:=k+1; go to a
end;

symbolic procedure partition(l,i,j,pivot);
% Writes l, all elements less than  pivot to the left
% and elements greater or equal to the right of pivot.
% returns the new pivot.
begin integer le,ri;
le:=i; ri:=j;
a: if le>ri then return le;
     transpose(l,le,ri);
while nth(l,le) < pivot do le:=le+1;
   while nth(l,ri) >= pivot do ri:=ri-1;
   go to a
end;

symbolic procedure quicksort_i_to_j(l, i,j);
begin integer k,pi;
pi:=find_pivot_index(l,i,j);
return if pi neq -1 then
        <<pi:=nth(l,pi); k:=partition(l,i,j,pi);
          quicksort_i_to_j(l,i,k-1);quicksort_i_to_j(l,k,j);l>>
        else l
end;

symbolic procedure algsort(u,v);
% Based on the PSL sort function.
% May replace all the above functions.
symb_to_alg sort(alg_to_symb u,v);

symbolic operator algsort;

% 4. Functions to check various properties of objects in a list and extract
%    them.

symbolic procedure checkproplist1(l,fn);
% Checks if the list l has the property defined by the function fn.
% fn should preferably be  'function <name_function>'.
 if null l then t else
 if fn eq 'numberp then
   if apply1(function evalnumberp, car l) then checkproplist1(cdr l,fn)
    else  nil else
 if fn eq 'floatp then
    if atom car l then nil else
    if apply1(function floatp, cdar l ) then checkproplist1(cdr l,fn)
    else nil else
 if get(fn,'number!-of!-args)=1  then
    if apply1(fn,car l) then checkproplist1(cdr l,fn)
    else  nil else
 if get(fn,'number!-of!-args)=2 then
    if apply(fn,list(car l,cadr l)) then checkproplist1(cdr l,fn)
 else nil;

symbolic procedure checkproplist(l,fn);
% fn may be the name of a function or the expression 'function <name
if atom l then rederr("First argument must be a list") else
 checkproplist1(cdr l,fn);

flag('(checkproplist),'boolean);

symbolic procedure extractlist1(l,fn);
% fn is a boolean function. Result is a new list which contains the
% elements satisfying the fn selection criteria.
 if null l then nil
 else
  if fn eq 'numberp then
          if apply1(function evalnumberp,car l) then
                         car l . extractlist1(cdr l,fn)
          else extractlist1(cdr l,fn)
  else
  if fn eq 'floatp then
          if atom car l then extractlist1(cdr l,fn) else
          if apply1(function floatp, cdar l)
               then car l . extractlist1(cdr l,fn)
          else extractlist1(cdr l,fn)
  else
  if apply1(fn,car l) then  car l . extractlist1(cdr l,fn)
     else extractlist1(cdr l,fn);

symbolic procedure extractlist(l,fn);
% The message will be issued only when number!-of!-args is used.
 (if x and x > 1 then
   rederr("UNARY boolean function required as argument") else
 'list . extractlist1(cdr l,fn)) where x=get(fn,'number!-of!-args);

flag('(extractlist),'opfn);

% 5. Flags and properties in the ALGEBRAIC mode.

symbolic procedure putflag(u,flg,b)$
% Allows one to put or erase any FLAG on the identifier U.
% U is an idf or a list of idfs, FLAG is an idf, B is T or 0.
if not idp u and not null baglistp u then
              <<for each x in cdr u do putflag(x,flg,b)$ t>>
 else      if idp u and b eq t then
            <<flag(list u, flg)$
              !:flaglis:=union(list list(u, flg),!:flaglis)$ flg>>
 else      if idp u and b equal 0 then
            <<remflag( list u, flg)$ !:delete(u,nil,flg)$>>
 else rederr "*** VARIABLES ARE (idp OR list of flags, T or 0).";

symbolic procedure putprop(u,prop,val,b)$
% Allows to put or erase any PROPERTY on the object U
% U is an idf or a list of idfs, B is T or 0$
if not idp u and baglistp u then
              <<for each x in cdr u do putprop(x,prop,val,b)$ t>>
 else      if idp u and b eq t then
            <<put(u, prop,val)$
              !:proplis:=union(list list(u,prop,val),!:proplis)$ u>>
 else      if idp u and b equal 0 then
            <<remprop( u, prop)$  !:delete(u,prop,val)$ >>
 else rederr "*** VARIABLES ARE (idp OR list of idps, T or 0).";

flag('(putflag putprop),'opfn)$

symbolic procedure rdisplayprop(u)$
% U is the idf whose properties one wants to display.Result is a
% list which contains them$
begin scalar x,val,aa$ x:=reval car u; val:=reval cadr u;
for each j in !:proplis do if car j eq x and cadr j eq val
                          then aa:=('list . cdr j) . aa;
return if length aa =1 then first aa else 'list . aa
end;

put('displayprop,'psopfn,'rdisplayprop)$

put('displayflag,'psopfn,'rdisplayflag)$

symbolic procedure rdisplayflag(u)$
% U is the idf whose properties one wants to display.Result is a
% list which contains them$
begin scalar x,aa$ x:=reval car u;
 for each j in !:flaglis do if car j=x then aa:=cons(cadr j,aa)$
 return 'list . aa end;

symbolic procedure clrflg!: u;
for each x in !:flaglis do
            if u eq car x then putflag(car x,cadr x,0) ;

symbolic procedure clearflag u;
% If u equals "all" all flags are eliminated.
% If u is a1,a2,a3.....an flags of these identifiers are eliminated.
if null cdr u and car u eq 'all then for each x in !:flaglis
          do putflag (car x,cadr x,0) else
 if null cdr u then clrflg!: car u else
                for each  y in u do clrflg!: y;

symbolic procedure clrprp!: u;
for each x in !:proplis do
          if u eq car x then putprop(car x,cadr x,caddr x,0);

symbolic procedure clearprop u;
% If u equals "all" all properties are eliminated.
% If u is a1,a2,a3...an properties of these identifiers are eliminated.
if null cdr u and car u eq 'all then for each x in !:proplis
          do putprop(car x,cadr x,caddr x,0) else
if null cdr u then clrprp!: car u else
                for each  y in u do clrprp!: y;

put('clearflag,'stat,'rlis);

put('clearprop,'stat,'rlis);

endmodule;

end;
