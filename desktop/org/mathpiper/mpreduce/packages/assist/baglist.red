module baglist$

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


global('(!:flaglis !:proplis));

fluid '(!*!:avoid);

% 1. Functions which works on LIST_like objects.

symbolic procedure flattens1 x;
% ll; ==> ((A B) ((C D) E))
% flattens1 ll; (A B C D E)
if atom x then list x else
if cdr x then append(flattens1 car x, flattens1 cdr x) else
   flattens1 car x;

algebraic procedure frequency lst;
% gives a LIST of PAIRS  {{el1,freq1} ...{eln,freqn}}.
% Procedure created by E. Schruefer.
   <<clear count!?!?; operator count!?!?; frequency1 lst>>;

algebraic procedure frequency1 lst;
if lst = {} then {}
else begin scalar r,el;
      el := first lst;
      if numberp count!?!? el
      then <<count!?!? el := count!?!? el + 1; r:=frequency1 rest lst>>
       else r :=
        {el,count!?!? el} . <<count!?!? el := 1; frequency1 rest lst>>;
        return r
       end;

symbolic procedure sequences n;
% Works properly, both in the symbolic and in the algebraic mode.
if !*mode eq 'symbolic then sequsymb n else
algebraic sequalg n;

flag('(sequences),'opfn);

symbolic procedure sequsymb n;
% Corresponds to the one below in the symbolic mode.
if n=1 then list(list(0),list(1))
 else for each s in sequsymb (n-1) conc list(0 . s,1 . s);

algebraic procedure sequalg n;
% Gives the list  {{0,0 ...,0},{0,0, ..., 1}, ...{1,1, ..., 1}}
% "conc" used in an explicit way.
   if n = 1 then {{0},{1}}
    else for each s in sequalg(n - 1) conc {0 . s,1 . s};

algebraic procedure split(u,v);
% split(list(a,b,c),list(1,1,1)); ==> {{A},{B},{C}}
% split(bag(a,b,c,d),list(1,1,2)); ==> {{A},{B},{C,D}}
% etc.
if symbolic baglistp u and symbolic baglistp v then
 begin scalar x;
    return for each n in v collect
             for i := 1:n collect
                 <<x := u.1; u := rest u; x>>
    end
 else lisp rederr(list(u,v,": must be lists or bags"));

symbolic procedure extremum(l,fn);
% Gives the extremum of elements in list l with respect
% to an ordering function fn. It may be ORDP etc ..
if atom l then l else
(if null x then nil else
 maximum3(x ,cadr l,fn))where x=cdr l;

flag('(extremum),'opfn);

symbolic procedure maximum3(l,m,fn);
if null l then m else
if apply2(fn,car l,m) then maximum3(cdr l,car l,fn) else
 maximum3(cdr l, m,fn);

symbolic procedure rmklis u$
begin scalar s,ss;integer n;
if length u = 2  then
<<s:=reval car u; n:=reval cadr u;
if car s eq 'list then
  ss:=append(s,cdr rmklis(list(n+1-length s))) else nil>> else
if length u=1 then
 <<n:=reval car u; for j:=1:n do s:=0 . s; ss:='list . s>>
else nil;
return ss end;

put('mklist,'psopfn,'rmklis);

symbolic procedure algnlist(u,n);
% Puts n copies of the list or bag u  in a list .
symb_to_alg nlist(alg_to_symb u,n);

flag('(algnlist),'opfn);

symbolic procedure insert_keep_order(u,l,fn);
% The message for the number of args works only if number!-of!-args
% is used.
(if x and x=1  or atom l then
typerr(list(l,fn),"list and binary function")
else 'list . insert_keep_order1(u,cdr l,fn)) where
                                          x=get(fn,'number!-of!-args);

flag('(insert_keep_order),'opfn);

symbolic procedure insert_keep_order1(u,l,fn);
% u is any object, l is a list and fn is a BINARY boolean function.
if null l then list u else
if apply(fn,list(u,car l)) then u . l else
car l . insert_keep_order1(u,cdr l,fn);

symbolic procedure merge_list(l1,l2,fn);
% fn is a binary boolean function
'list . merge_list1(cdr l1,cdr l2,fn);

flag('(merge_list),'opfn);

symbolic procedure merge_list1(l1,l2,fn);
% Returns the (physical) merge of the two sorted lists l1 and l2.
% Example of use :
% l1:=list(1,2,3)$ l2:=list(1,4,5)$
% merge(l1,l2,'lessp); ==> (1 1 2 3 4 5)
% l1 and l2 are destroyed
% This is complementary to the function INSERT_KEEP_ORDER
  if null l1 then l2
  else if null l2 then l1
  else if apply2(fn,car l1,car l2) then
                               rplacd(l1,merge_list1(cdr l1,l2,fn))
  else rplacd(l2,merge_list1(l1,cdr l2,fn));

% 2. Introduction of BAG-like objects.

put('bag,'simpfn,'simpiden);

flag('(bag),'bag)$  % the default bag

flag('(bag),'reserved)$

symbolic (!:flaglis:=union(list list('bag,'bag),!:flaglis))$

symbolic procedure !:delete(u,prop,val)$
if prop then
for each x in !:proplis do if x=list(u,prop,val)
              then !:proplis:=delete(x,!:proplis) else nil else
for each x in !:flaglis do if x=list(u,val)
              then !:flaglis:=delete(x,!:flaglis);

symbolic procedure !:bagno u; u eq 'list or flagp(u,'boolean);

symbolic procedure !:bagyes u; getd u or
                  gettype u member list('tvector,'vector) or
                  flagp( u,'opfn) or
                  get(u,'simpfn) or get(u,'psopfn) or
                  get(u,'fdegree) or get(u,'ifdegree);

symbolic procedure simpbagprop u$
% gives the bag property to ident. or baglike-list of identifiers U
% V is T if one creates the property or 0 if one destroys it.
% Use is bagprop(<list of atoms>,T or 0)
% Makes tests to avoid giving this property to an unsuitable object.
   begin scalar id,bool;
   id:= car u; bool:= if  cadr u eq t then t;
   if listp id then
    << for each x in id do simpbagprop list(x,bool) $
     return bool>> else
   if  idp id and bool=t then
         if !:bagno id then typerr (id,"BAG") else
         if !:bagyes id then <<flag(list id,'bag),go to l1>> else
         <<put(id,'simpfn,'simpiden)$ flag(list id,'bag)$ go to l1>>
   else
   if  idp id and not bool  then
   <<remflag(list id,'bag); go to l1>>
   else  rederr("BAD ARGUMENT for bagprop");
l1: if bool then !:flaglis:=union(list list(id,'bag),!:flaglis)
      else !:delete(id,nil,'bag) end;

symbolic procedure putbag u;
% gives the bag property to ident. or baglike-list of identifiers u
% V is T to create the bag property.
simpbagprop list(u,t);

symbolic procedure clearbag u;
% destroys the bag property of the identifier or the baglike-list u
simpbagprop list(u,0);

rlistat '(putbag clearbag);

symbolic procedure bagp(u)$
% test of the baglike property of U$
not atom u and flagp(car u ,'bag)$

flag('(bagp),'boolean);

symbolic procedure nbglp(u,n)$
%Function which determines if U is not a bag at the level N.
% Used in DEPTH.
if n=0 then not baglistp u else
if atom u or not bglp!:!: car u  then nil else
begin scalar uu$ uu:= u$
 l1:  uu:=cdr uu$
    if null uu then return t$
    if nbglp(car uu,n-1) then  go to l1 else
          return nil end$

symbolic procedure bglp!:!: u;
if not atom u then bglp!:!: car u else
     if (flagp(u,'bag) or u eq 'list) then t else nil;

symbolic procedure baglistp u;
% This function is supposed to act on a prefix simplified expression.
not atom u and ( car u eq 'list  or flagp(car u,'bag));

symbolic procedure nul!: u; baglistp u and null cdr u;

flag('(baglistp nul!:),'boolean);

symbolic procedure alistp u$
% Not for use in algebraic mode.
if null u then t else
(not atom car u) and alistp cdr u;

symbolic procedure abaglistp u;
% For use in algebraic mode. Recognizes when a bag-like object
% contains bags which themselves contain two and only two objects.
if null baglistp u or null baglistp cadr u then nil else
     begin;
l1:  u:=cdr u;
    if null u then return t ;
     if length car u <3 then return nil else go to l1 end;

flag('(abaglistp),'boolean);

% 3. Definitions of operations on lists and bags.

symbolic procedure rexplis u;
% THIS PROCEDURE GENERALIZES BAGLIST TO ANY OBJECT AND GIVES A LIST OF
% THE ARGUMENTS OF U.
if atom ( u:=reval car u) then nil else
% if kernp mksq(u,1) then 'list . cdr u ;
if kernp mksq(u,1) then
  'list . for each i in cdr u collect mk!*sq simp!* i ;

put('kernlist,'psopfn,'rexplis);

symbolic procedure rlisbag u$
begin scalar x,prf;
x:=reval car u; prf :=reval cadr u;
if atom x then return nil else
<<simpbagprop list(prf,t) ; x:=prf . cdr x>>;
return x end;

put('listbag,'psopfn,'rlisbag);

symbolic procedure rfirst li;
  if bagp( li:=reval car li) then
  if null cdr li then car li . nil else  car li . cadr li . nil else
  if car li neq 'list then typerr(li,"list or bag")
  else if null cdr li then parterr(li,1)
  else cadr li;

put('first,'psopfn,'rfirst);

symbolic procedure rsecond li;
 if bagp( li:=reval car li) then
      if null cdr li or null cddr li then  car li . nil
      else  car li . caddr li . nil
  else if car li neq 'list then typerr(li,"list or bag")
  else if null cdr li or null cddr li then parterr(li,2)
  else caddr li;

put('second,'psopfn,'rsecond);

symbolic procedure rthird li;
  if bagp( li:=reval car li) then
    if null cdr li  or null cddr li or null cdddr li
       then car li . nil else  car li . cadddr li . nil
    else if car li neq 'list then typerr(li,"list or bag")
  else if null cdr li or null cddr li or null cdddr li
    then parterr(li,3)
    else cadddr li;

symbolic procedure rrest li;
if bagp( li:=reval car li) then
  if null cdr li then li . nil else  car li . cddr li  else
if car li neq 'list then typerr(li,"list or bag")
else 'list . if null (li:=cdr li) then li else cdr li;

put('rest,'psopfn,'rrest);

symbolic procedure rreverse u;
<<u:=reval car u;
if bagp u then car u . reverse cdr u
else if car u neq 'list  then typerr(u,"list or bag")
else 'list . reverse cdr u>>;

put('reverse,'psopfn,'rreverse);

symbolic procedure rlast u;
<<u:=reval car u;
if bagp u then if null cdr u then u else
    car u . lastcar cdr u . nil
 else if car u neq 'list  then typerr(u,"list or bag")
 else  if null cdr u then nil
 else lastcar cdr u>>;

put('last,'psopfn,'rlast);

symbolic procedure rdc u;
if null cdr u then nil else car u . rdc cdr u;

symbolic procedure rbelast u;
<< u:=reval car u;
    if baglistp u then
        if null cdr u then u else
        car u . rdc cdr u   else
    typerr(u, "list or bag")>>;

put('belast,'psopfn,'rbelast);

symbolic procedure rappend u;
   begin scalar x,y;
    if length u neq 2 then rederr("append has TWO arguments");
       x:=reval car u;
       y:=reval cadr u;
    if baglistp x and baglistp y  then
                      return car x . append(cdr x,cdr y)
    else typerr(list(x,y),"list or bag")
   end ;

put('append,'psopfn,'rappend);

symbolic procedure rappendn u;
% This append function works for any number of arguments and all
% types of kernels. Output is always a LIST.
   begin scalar x,y;
      x:= revlis u;
      y:=for each i in x collect mkquote if atom i then
         rederr("arguments must be kernels or lists") else cdr i;
      x:=  eval expand(y,'append);
      return 'list .  x
end ;

put('appendn,'psopfn,'rappendn);

%symbolic procedure rcons u;
% Dans ASSIST.RED
% This procedure does not work perfectly well when the package
% HEPHYS is entered because ISIMPA is applied by reval1 on the
% result of RCONS. When it is given by (BAG (LIST A B) C D) it gives
% the output BAG({A,B}) erasing C and D ! It is due to the fact that
% ISIMP1 and ISIMP2 do not accept SQ forms for identifiers.
% So avoid inputs like list(a,b).bag(c,d) when HEPHYS is loaded.
% begin scalar x,y,z;
%      if (y := getrtypeor(x := revlis u)) eq 'hvector
%        then return if get('cons,'opmtch) and (z:=opmtch('cons . x))
%                      then reval z
%                    else prepsq simpdot x
%      if (y := getrtypeor(x := revlis u)) eq 'hvector
%    then return if get('cons,'opmtch) and (z := opmtch('cons . x))
%                   then reval z
%                 else prepsq subs2 simpdot x
%      else if getrtype(y:=cadr x) eq 'list
%             then return  'list . car x . cdadr x
%      else if bagp y
%             then return   z:=car y . car x . cdr y
%       else if fixp y
%   then return z:= if get('rcons,'cleanupfn) then 'bag . revalpart u
%                                else revalpart u
%       else typerr(x,"list or bag")
%   end;

%symbolic procedure isimpa(u,v);
%   if eqcar(u,'list) then u else
%   if eqcar(u,'bag) then cdr u else !*q2a1(isimpq simp u,v);

symbolic procedure rcons u;
% Dans ASSIST.RED
% This procedure does not work perfectly well when the package
% HEPHYS is entered because ISIMPA is applied by reval1 on the
% result of RCONS. When it is given by (BAG (LIST A B) C D) it gives
% the output BAG({A,B}) erasing C and D ! It is due to the fact that
% ISIMP1 and ISIMP2 do not accept SQ forms for identifiers.
% So avoid inputs like list(a,b).bag(c,d) when HEPHYS is loaded.
 begin scalar x,y,z;
      if (y := getrtypeor(x := revlis u)) eq 'hvector
    then return if get('cons,'opmtch) and (z := opmtch('cons . x))
                   then reval z
                 else prepsq subs2 simpdot x
      else if getrtype(y:=cadr x) eq 'list
             then return  'list . car x . cdadr x
      else if bagp y
             then return   z:=car y . car x . cdr y
      else if fixp y then
         <<if get('rcons,'cleanupfn) then !*!:avoid :=t; return revalpart u>>
       else typerr(x,"list or bag")
   end;

%remflag('(isimpa),'lose);
%
% The version here is now in hephys so this copy is no longer needed
%
%symbolic procedure isimpa(u,v);
% if eqcar(u,'list) or !*!:avoid or (atom u and get(u,'rtype) eq 'hvector)
%   then <<!*!:avoid:=nil; u>>
% else !*q2a1(isimpq simp u,v);
%
%flag('(isimpa),'lose);

put('cons,'setqfn,'(lambda (u v w) (setpart!* u v w)));

put('cons,'psopfn,'rcons);

symbolic procedure lengthreval u;
   begin scalar v,w;
      if length u neq 1
        then rederr "LENGTH called with wrong number of arguments"
       else if idp car u and arrayp car u
        then return 'list . get(car u,'dimension)
       else if bagp (u:=reval car u)
        then return  length cdr u;
      v := aeval u;
      if (w := getrtype v) and (w := get(w,'lengthfn))
        then return apply1(w,v)
       else if atom v then return 1
       else if not idp car v or not(w := get(car v,'lengthfn))
        then typerr(u,"length argument")
       else return apply1(w,cdr v)
   end;

put('length,'psopfn,'lengthreval);

put('size,'psopfn,'lengthreval);

symbolic procedure rremove u;
% Allows one to remove the element n of bag u.
% First argument is a bag or list, second is an integer.
 if length u neq 2 then
       rederr("remove called with wrong number of arguments") else
 begin scalar x;integer n;
 x:=reval car u; n:=reval cadr u;
 if baglistp x  then return car x . remove(cdr x,n) else
 typerr(u, "list or bag")
 % rederr(" first argument is a list or a bag, second is an integer")
end;

put('remove,'psopfn,'rremove);

symbolic procedure rdelete u;
begin scalar x,y;
x:=reval car u; y:=reval cadr u;
if baglistp y then return delete(x,y) end;

put('delete,'psopfn,'rdelete);

% Use is delete(<any>,<bag or list>)

symbolic procedure delete_all(ob,u);
'list . del_all_obj(ob,cdr u);

flag('(delete_all),'opfn);

symbolic procedure del_all_obj(ob,u);
% Deletes from list u ALL objects ob
if null u then nil else
if car u = ob then del_all_obj(ob,cdr u) else
  car u . del_all_obj(ob,cdr u);

symbolic procedure rmember u;
% First argument is anything, second argument is a bag or list.
begin scalar x,y$
  x:=reval car u;
  y:=reval cadr u;
 if baglistp y then
              if (x:=member(x,cdr y))
              then return car y . x else return nil
else typerr(y,"list or bag") end;

put('member,'psopfn,'rmember);

% INPUT MUST BE " member (any , < bag OR list> ) ".

symbolic procedure relmult u;
if length u neq 2 then
      rederr("elmult called with wrong number of arguments") else
begin scalar x,y; integer n;
   x:=reval car u;  % It is the object the multiplicity of which one
                  % wants to compute.
   y:=reval cadr u; % IT IS THE list OR bag
 if x=y then return 1 else
 if baglistp y then
            <<y:=cdr y;
             while not null (y:=member(x,y)) do <<y:=cdr y;n:=n+1>>>>
         else typerr(y,"list or bag");
 return n end;

put('elmult,'psopfn,'relmult);

% Use is  " elmult (any , < bag OR list> ) " .

symbolic procedure rpair u$
begin scalar x,y,prf$
 if length u neq 2 then
      rederr("pair called with wrong number of arguments");
 x:=reval car u; y:=reval cadr u$
 if not (baglistp x and baglistp y) then
                  rederr("arguments must be lists or bags") else
 prf:=car x;x:=cdr x; y:=cdr y;
 y:=pair(x,for each j in y collect list j);
 return y:=prf . for each j in y collect prf . j  end;

put('pair,'psopfn,'rpair);

symbolic procedure delpair(elt,u);
'list .
  for each j in  delasc(elt,for each i  in cdr u collect cdr i)
                                                  collect 'list . j ;

flag('(delpair),'opfn);

symbolic procedure depth!: u;
   if not atom u and (car u eq 'list or flagp(car u,'bag))
      then 1 + (if cdr u then depth!: cadr u else 0)
    else 0;

symbolic procedure rdepth(u)$
% Use is depth(<BAG or LIST>).
begin scalar x; integer n;
 x := reval car u;
 if nbglp(x,n:=depth!: x) then
     return n else return "bag or list of unequal depths" end;

put('depth,'psopfn,'rdepth);

symbolic procedure rinsert u;
% Use is insert(<any>, <list or bag>, <integer>).
begin scalar x,bg,bbg,prf; integer n;
   bg:=reval cadr u; n:=reval caddr u;
 if not baglistp bg then typerr(bg,"list or bag") else
 if n<=0 then rederr("third argument must be positive an integer") else
 if (n:=n+1) > length bg then return append(bg,x:=list reval car u);
   prf:=car bg; x:=reval car u;
   for i:=3:n do <<bg:=cdr bg; bbg:=car bg . bbg>>;
   bbg:=reverse bbg;
  return bbg:=prf . append(bbg,cons(x,cdr bg))
 end;

put('insert,'psopfn,'rinsert);

% Use is : insert(<any>, <list or bag>, <integer>).

symbolic procedure rposition u$
% Use is position(<any>,<LIST or BAG>).
begin scalar el,bg; integer n;
el:=if null !*exp then reval resimp simp!* car u else reval car u;
if not baglistp (bg:=reval cadr u) then typerr(bg," list or bag");
n:=length( bg:=cdr bg);
if (bg:=member(el,bg))
             then return (n:=n+1-length bg)  else
 msgpri(nil,el,"is not present in list or bag",nil,nil) end;

put('position,'psopfn,'rposition);

% **********

% The functions below, when applied to objects containing SEVERAL bag
% prefixes have a rule to select them in the output object when this
% one is itself a bag: the first level prefix has priority over all
% other prefixes and will be selected, when needed, as the envelope
% of the output.

symbolic procedure !:assoc u;
if length u neq 2 then
      rederr("asfirst called with wrong number of arguments") else
 begin scalar x,y,prf;
  x:=reval car u; y:=reval cadr u;
 if null baglistp y then typerr(y,"list or bag");
  prf:=car y; y:=cdr y;
 if null alistp y then typerr(y, "association list") else
      y:=for each j in y collect cdr j;
 return  if null (y:=assoc(x,y)) then nil else prf . y   end;

put('asfirst,'psopfn,'!:assoc);

% Use is : asfirst(<key>,<a-list> | <a-bag>)

symbolic procedure !:rassoc u;
if length u neq 2 then
      rederr("assecond called with wrong number of arguments") else
begin scalar x,y,prf;
 x:=reval car u; y:=reval cadr u;
 if null baglistp y then typerr(y,"list or bag");
  prf:=car y; y:=cdr y;
 if null alistp y then typerr(y, "association list") else
     y:=for each j in y collect cdr j;
 return  if null (y:=rassoc(list x,y)) then nil else prf . y   end;

put('assecond,'psopfn,'!:rassoc);

% Use is : assecond(<key>,<a-list>|<a-bag>)

symbolic procedure !:assoc2 u;
if length u neq 2 then
      rederr("asrest called with wrong number of arguments") else
begin scalar x,y,prf;
  x:=reval car u; y:=reval cadr u;
 if null baglistp x or null baglistp y then
   typerr(list(x,y),"list or bag");
  prf:=car y; y:=cdr y; x:=cdr x;
 if null alistp y then typerr(y, "association list") else
     y:=for each j in y collect cdr j;
 return  if null (y:=assoc2(x,y)) then nil else prf . y   end;

put('asrest,'psopfn,'!:assoc2);

% Use is : asrest(<key>,<a-list>|<a-bag>)

symbolic procedure lastassoc!*(u,v);
% Use is :
% aslast(<key as a last element>,<a-list>|<a-bag>)
% Finds the sublist in which u is the last element in the
% compound list  or bag v, or nil if it is not found.
   if null v then nil
    else begin scalar vv; vv:=car v;
          while length vv > 1  do vv:=cdr vv;
          if u = car vv then return car v
    else return lastassoc!*(u,cdr v) end;

symbolic procedure !:lassoc u;
if length u neq 2 then
      rederr("aslast called with wrong number of arguments") else
begin scalar x,y,prf;
  x:=reval car u; y:=reval cadr u;
 if null baglistp y then typerr(y,"list or bag");
  prf:=car y; y:=cdr y;
 if null alistp y then typerr(y, "association list") else
      y:=for each j in y collect cdr j;
 return  if null (y:=lastassoc!*(x,y)) then nil else prf . y   end;

put('aslast,'psopfn,'!:lassoc);

symbolic procedure rasflist u;
% Use is :
% asflist(<key as a first element>,<a-list>|<a-bag>)
% This procedure gives the LIST (or BAG) associated with the KEY con-
% tained in the first argument. The KEY is here the FIRST element
% of each sublist contained in the association list .
if length u neq 2 then
      rederr("ASFLIST called with wrong number of arguments") else
begin scalar x,y,prf,res,aa;
 x:=reval car u; y:=reval cadr u; prf:=car y;
 if null cdr y then return y;
 for each j in cdr y do if car j neq prf then
 rederr list("prefix INSIDE the list or bag neq to",prf);
  l1: aa:=!:assoc(list(x,y));
     if not aa then return prf . reverse res;
     res:=aa . res;
     y:=delete(aa,y);
     go to l1;
                end$

put('asflist,'psopfn,'rasflist);

symbolic procedure rasslist u;
% Use is :
% asslist(<key as the second element>,<a-list>|<a-bag>)
if length u neq 2 then
      rederr("ASSLIST called with wrong number of arguments") else
begin scalar x,y,prf,res,aa;
 x:=reval car u; y:=reval cadr u; prf:=car y;
 if null cdr y then return y;
 for each j in cdr y do if car j neq prf then
 rederr list("prefix INSIDE the list or bag neq to",prf);
  l1: aa:=!:rassoc(list(x,y));
     if not aa then return prf . reverse res;
     res:=aa . res;
     y:=delete(aa,y);
     go to l1;
                end$

put('asslist,'psopfn,'rasslist);

symbolic procedure !:sublis u;
% Use is :
% restaslist(<bag-like object containing keys>,<a-list>|<a-bag>)
% Output is a list containing the values associated to the selected
% keys.
if length u neq 2 then
    rederr("restaslist called with wrong number of arguments") else
begin scalar x,y,yy,prf;
  x:=reval car u;
  y:=reval cadr u; prf:=car y;
 if null baglistp y then typerr(y,"list or bag") else
 if null alistp (y:=cdr y) then typerr(y," association list or bag")
 else  y:=for each j in y collect cdr j;
 if baglistp x then <<x:=cdr x; x:=for each j in x collect
                                            if  assoc(j,y) then j>>;
  y:=sublis(y,x); if atom y then yy:=list y else
  for each j in y do if not null j then yy:=j . yy;
  yy:=reverse yy;
  return  prf . for each j in yy collect
                     if atom j then prf . j . nil else prf . j$
        end$

put('restaslist,'psopfn,'!:sublis);

% Use is :
% restaslist(<bag-like object containing keys>,<a-list>|<a-bag>)
% Output is a list containing the values associated to the selected
% keys.
% ******* End of functions which may change bag- or list- prefixes.

% FOR SUBSTITUTION OF IDENTIFIERS IT IS CONVENIENT TO USE :

symbolic procedure !:subst u;
 reval subst(reval car u,reval cadr u,reval caddr u);

put('substitute,'psopfn,'!:subst);

% Use is : substitute(<newid>,<oldid>,<in any>).
% May serve to transform ALL bags into lists or vice-versa.

symbolic procedure !:repla u;
if length u neq 2 then
      rederr("repfirst called with wrong number of arguments") else
begin scalar x,y,prf;
  y:=reval car u; x:= reval cadr u;
 if null baglistp x then typerr(x,"list or bag");
  prf:= car x; x:=cdr x;
  return prf . rplaca(x,y) end;

put('repfirst,'psopfn,'!:repla);

% Use is : repfirst(<any>, <bag or list>);

symbolic procedure !:repld u;
% Use is : represt(<any>, <bag or list>);
begin scalar x,y,prf;
 if length u neq 2 then
       rederr("replast called with wrong number of arguments");
  y:=reval car u; x:= reval cadr u;
 if null baglistp x then typerr(u,"list or bag");
  prf:= car x; x:=cdr x;
  return prf . rplacd(x,list y) end;

put('represt,'psopfn,'!:repld);


% 4. Functions for SETS.

symbolic procedure !:union u$
begin scalar x,y,prf;
 if length u neq 2 then
       rederr("union called with wrong number of arguments");
  x:=reval car u; y:=reval cadr u;
 if not setp y then rederr "second argument to UNION must be a set";
 if baglistp x and baglistp y then
 <<prf:=car y; y:=prf . union(cdr x,cdr y)>> else return nil;
  return y end;

put('union,'psopfn,'!:union);

symbolic procedure setp u;
null repeats u;

flag('(setp),'boolean);

symbolic procedure rmkset u;
begin scalar x,prf$
  x:=reval car u; prf:=car x;
 if baglistp x then return prf . list2set cdr x
end;

put('mkset,'psopfn,'rmkset);

symbolic procedure !:setdiff u$
begin scalar x,y,prf;
 if length u neq 2 then
       rederr("diffset called with wrong number of arguments");
  x:=reval car u; y:=reval cadr u;
 if baglistp x and baglistp y then
    <<prf:=car y; y:=prf . setdiff(cdr x,cdr y)>> else return nil;
 return y end;

put('diffset,'psopfn,'!:setdiff);

symbolic procedure !:symdiff u$
begin scalar x,y,prf;
 if length u neq 2 then
       rederr("symdiff called with wrong number of arguments");
  x:=reval car u; y:=reval cadr u; prf:=car x;
 if setp x and setp y then return
    prf . append(setdiff(x:=cdr x,y:=cdr y),setdiff(y,x))
end;

put('symdiff,'psopfn,'!:symdiff);

symbolic procedure !:xn u$
begin scalar x,y;
  if length u neq 2 then
      rederr("intersect called with wrong number of arguments");
    x:=reval car u; y:=reval cadr u;
  if setp x and setp y then return car x . intersection(cdr x,cdr y)
 end;

put('intersect,'psopfn,'!:xn);

endmodule;

end;
