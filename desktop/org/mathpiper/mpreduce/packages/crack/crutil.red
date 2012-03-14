%********************************************************************
module utilities$
%********************************************************************
%  Routines for finding leading derivatives and others
%  Author: Andreas Brand 1990 1994
%          Thomas Wolf since 1994

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


%%%%%%%%%%%%%%%%%%%%%%%%%
%  properties of pde's  %
%%%%%%%%%%%%%%%%%%%%%%%%%

symbolic procedure drop_dec_with(de1,de2,rl)$
% drop de1 from the 'dec_with or 'dec_with_rl list of de2
% currently for all orderings
begin scalar a,b,c$
  a:=if rl then get(de2,'dec_with_rl)
           else get(de2,'dec_with)$
  for each b in a do << % for each ordering b
    b:=delete(de1,b);
    if length b>1 then c:=cons(b,c);
  >>;
  if rl then put(de2,'dec_with_rl,c)
        else put(de2,'dec_with   ,c)
end$

symbolic procedure add_dec_with(ordering,de1,de2,rl)$
% add (ordering de1) to 'dec_with or 'dec_with_rl of de2
begin scalar a,b$
  a:=if rl then get(de2,'dec_with_rl)
           else get(de2,'dec_with)$
  b:=assoc(ordering,a)$
  a:=delete(b,a)$
  if b then b:=cons(ordering,cons(de1,cdr b))
       else b:=list(ordering,de1)$
  if rl then put(de2,'dec_with_rl,cons(b,a))
        else put(de2,'dec_with   ,cons(b,a))$
end$

symbolic procedure add_both_dec_with(ordering,de1,de2,rl)$
% add (ordering de1) to 'dec_with or 'dec_with_rl of de2  and
% add (ordering de2) to 'dec_with or 'dec_with_rl of de1
begin
  add_dec_with(ordering,de1,de2,rl)$
  add_dec_with(ordering,de2,de1,rl)$
end$

symbolic procedure drop_rl_with(de1,de2)$
% drop de1 from the 'rl_with list of de2
put(de2,'rl_with,delete(de1,get(de2,'rl_with)))$

symbolic procedure add_rl_with(de1,de2)$
% add de1 to 'rl_with of de2 and vice versa
<<put(de2,'rl_with,cons(de1,get(de2,'rl_with)))$
  put(de1,'rl_with,cons(de2,get(de1,'rl_with)))>>$

symbolic procedure prevent_simp(v,de1,de2)$
% it is df(de1,v) = de2
% add dec_with such that de2
% will not be simplified to 0=0
begin scalar a,b$
  % a:=get(de1,'fcts)$
  a:=list(0);   % all orderings for which de1 is used (-->ord)
  for each b in a do if member(v,fctargs(b)) then
  <<add_dec_with(b,de2,de1,nil);add_dec_with(b,de2,de1,t)>>;
  % a:=get(de2,'fcts)$
  a:=list(0);   % all orderings for which de2 is used (-->ord)
  for each b in a do if member(v,fctargs(b)) then
  <<add_dec_with(b,de1,de2,nil);add_dec_with(b,de1,de2,t)>>;
end$

symbolic procedure termread$
begin scalar val, !*echo;  % Don't re-echo tty input
 if not null old_history then <<
  val:=car old_history$
  if print_ then <<write"old input: ",val$terpri()>>$
  old_history:=cdr old_history
 >>                      else <<
  rds nil; wrs nil$         % Switch I/O to terminal
  val := read()$
  if ifl!* then rds cadr ifl!*$  %  Resets I/O streams
  if ofl!* then wrs cdr ofl!*$
 >>$
 history_:=cons(val,history_)$
 return val
end$

symbolic procedure termxread$
begin scalar val, !*echo;  % Don't re-echo tty input
 if not null old_history then <<
  val:=car old_history$
  if print_ then <<write"old input: ",val$terpri()>>$
  old_history:=cdr old_history
 >>                      else <<
  rds nil; wrs nil$         % Switch I/O to terminal
  val := xread(nil)$
  if ifl!* then rds cadr ifl!*$  %  Resets I/O streams
  if ofl!* then wrs cdr ofl!*$
 >>$
% history_:=cons(compress(append(explode val,list('$))),history_)$
 history_:=cons(val,history_)$
 return val
end$

symbolic procedure termlistread()$
begin scalar l;
  l:=termxread()$
  if (not null l) and
     ((atom l) or
      (pairp l and (car l neq '!*comma!*)))
  then l:=list('!*comma!*,l);
  if l and ((not pairp l) or (car l neq '!*comma!*)) then
  <<terpri()$write"Error: not a legal list of elements.";terpri()$
    l:=nil>>
  else if pairp l then l:=cdr l; % dropping '!*comma!*
  return l
end$

symbolic procedure mkeqlist(vallist,ftem,vl,flaglist,simp_flag,orderl,pdes)$
%  make a list of equations
%    vallist:  list of expressions
%    ftem:     list of functions
%    vl:       list of variables
%    flaglist: list of flags
%    orderl:   list of orderings where the equations are valid
%    pdes:     list of all equations by name to update inequalities
%              within update()
begin scalar l1$
 for each a in vallist do
     l1:=eqinsert(mkeq(a,ftem,vl,flaglist,simp_flag,orderl,
                       nil,append(l1,pdes)),l1)$
return l1
end$

symbolic procedure mkeq(val,ftem,vl,flaglist,simp_flag,orderl,hist,pdes)$
%  make a new equation
%    val:      expression
%    ftem:     list of functions
%    vl:       list of variables
%    flaglist: list of flags
%    orderl:   list of orderings where the equation is valid
%    hist:     the history of val
%    pdes:     list of all equations by name to update inequalities
%              within update()
%  If the new equation to be made is only to exist temporarily then
%  call mkeq with pdes=nil to avoid lasting effects of the temprary pde.
%
begin scalar s$
 s:=new_pde()$
 if record_hist and hist then put(s,'histry_,reval hist)$
 for each a in flaglist do flag1(s,a)$
 if not update(s,val,ftem,vl,simp_flag,orderl,pdes) then
   <<drop_pde(s,nil,nil)$
   s:=nil>>$
 if record_hist and null hist and s then put(s,'histry_,s)$
 return s
end$

symbolic procedure no_of_derivs(equ)$
begin scalar h,dl;
 h:=0;
 dl:=get(equ,'derivs);
 while dl do <<
  if (pairp caar dl) and (cdaar dl) then h:=add1 h;
  dl:=cdr dl
 >>;
 return h
end$

symbolic procedure update(equ,val,ftem,vl,simp_flag,orderl,pdes)$
%  update the properties of a pde
%    equ:      pde
%    val:      expression
%    ftem:     list of functions
%    vl:       list of variables
%    orderl:   list of orderings where the equation is valid
%    pdes:     to call addineq at end
%  *** important ***:
%  call afterwards also drop_pde_from_idties(p,pdes,pval) and
%                       drop_pde_from_properties()
%  if this is now a new equation
begin scalar l$
  if val and not zerop val then <<
    %ftem:=reverse union(smemberl(ftem,val),nil)$
    ftem:=sort_according_to(smemberl(ftem,val),ftem_)$
    put(equ,'terms,no_of_terms(val))$
    if simp_flag then <<
      % the following test to avoid factorizing big equations
      val:=% if get(equ,'terms)>max_factor then simplifypde(val,ftem,nil,equ)
           %                               else
           simplifypde(val,ftem,t,equ)$
      if val and not zerop val then <<
        ftem:=reverse union(smemberl(ftem,val),nil)$
        put(equ,'terms,no_of_terms(val))$
      >>
    >>$
  >>$
  depl!*:=delete(assoc(reval equ,depl!*),depl!*)$
  if val and not zerop val then <<
    put(equ,'val,val)$
    put(equ,'fcts,ftem)$
    for each v in vl do
      if not my_freeof(val,v) then l:=cons(v,l)$
    vl:=sort_according_to(l,vl_);
    put(equ,'vars,vl)$
    if vl then
    depl!*:=cons(cons(equ,vl),depl!*)$ % needed in expressions in idnties_
    put(equ,'nvars,length vl)$
    put(equ,'level,level_)$
    put(equ,'derivs,sort_derivs(all_deriv_search(val,ftem),ftem,vl))$
    if struc_eqn then put(equ,'no_derivs,no_of_derivs(equ));
    put(equ,'fcteval_lin,nil)$
    put(equ,'fcteval_nca,nil)$
    put(equ,'fcteval_nli,nil)$
    put(equ,'fct_nli_lin,nil)$
    put(equ,'fct_nli_nca,nil)$
    put(equ,'fct_nli_nli,nil)$
    put(equ,'fct_nli_nus,nil)$
%    put(equ,'terms,no_of_terms(val))$
    put(equ,'length,pdeweight(val,ftem))$
    put(equ,'printlength,delength val)$
    put(equ,'rational,nil)$
    put(equ,'nonrational,nil)$
    put(equ,'allvarfcts,nil)$
    put(equ,'orderings,orderl)$     % Orderings !
    for each f in reverse ftem do
      if rationalp(val,f) then
         <<put(equ,'rational,cons(f,get(equ,'rational)))$
           if fctlength f=get(equ,'nvars) then
              put(equ,'allvarfcts,cons(f,get(equ,'allvarfcts)))>>
      else put(equ,'nonrational,cons(f,get(equ,'nonrational)))$
%    put(equ,'degrees,          % too expensive
%     if linear_pr then cons(1,for each l in get(equ,'rational)
%                              collect (l . 1))
%                  else fct_degrees(val,get(equ,'rational))    )$
    put(equ,'partitioned,nil)$
    put(equ,'starde,stardep(ftem,vl))$
    flag1(equ,'to_eval)$
    if (l:=get(equ,'starde)) then
       <<%remflag1(equ,'to_eval)$
       remflag1(equ,'to_int)$
       remflag1(equ,'to_fullint)$
       if simp_flag and (zerop cdr l) then flag1(equ,'to_sep)$
       % remflag1(equ,'to_diff)
       >>
    else remflag1(equ,'to_gensep)$
    if get(equ,'starde) and
       (zerop cdr get(equ,'starde)      ) % or (get(equ,'length)<=gensep_))
       then
       else remflag1(equ,'to_sep)$
    if get(equ,'nonrational) then
       <<%remflag1(equ,'to_decoup)$
       if not freeoflist(get(equ,'allvarfcts),get(equ,'nonrational)) then
          remflag1(equ,'to_eval)>>$
%    if not get(equ,'allvarfcts) then remflag1(equ,'to_eval)$
    if (not get(equ,'rational)) or
       ((l:=get(equ,'starde)) and (cdr l = 0)) then remflag1(equ,'to_eval)$
    if homogen_ then <<
      l:=cdr algebraic find_hom_deg(lisp val);
      put(equ,'hom_deg,l)$
%      if car l=1 then << % i.e. linear in flin_
%        l:=get(equ,'derivs);
%        while l and (null linf or (length linf < 3)) do <<
%          if not freeoflist(car l,flin_) then <<
%            linf:=cons(car l,linf);
%            if member(car l,ineq_) then fd1:=car l
%          >>;
%          l:=cdr l
%        >>;
%        if linf and (length linf = 2) and fd1 then <<<<
%          if NON-ZERO(coeffn(get(equ,'val),fd1,1)) then <<
%            fd2:=car delete(fd1,linf);
%  braucht pdes, was nicht vorhanden ist
%            addineq(pdes,fd2);
%            addineq(pdes,coeffn(get(equ,'val),fd2,1))
%          >>
%        >>
%      >>
    >>$
    put(equ,'split_test,nil)$
    put(equ,'linear_,if lin_problem then t
                                    else lin_check(val,ftem))$
    new_ineq_from_pde(equ,pdes);
    return equ
  >>
end$

symbolic procedure new_ineq_from_pde(equ,pdes)$
% currently only effective for equations with 2 terms
% If one term of the equation is non-zero then the sum of the
% remaining terms has to be non-zero too
if pdes and null lin_problem and (get(equ,'terms)=2) % >1)
then begin scalar valu;
 valu:=get(equ,'val);
 if not (pairp valu and (car valu='PLUS)) then valu:=reval valu;
 if not (pairp valu and (car valu='PLUS)) then write"err in update"
                                          else
%    for each l in cdr valu do
%    if null may_vanish l then addineq(pdes,reval{'DIFFERENCE,valu,l})
 if null may_vanish cadr  valu then addineq(pdes,caddr valu) else
 if null may_vanish caddr valu then addineq(pdes,cadr  valu)
end$

symbolic procedure fct_degrees(pv,ftem)$
% ftem are to be the rational functions
begin
 scalar f,l,ll,h,degs$
 if den pv then pv:=num pv$
 for each f in ftem do <<
  l:=gensym()$
  ll:=cons((f . l),ll)$
  pv:=subst({'TIMES,l,f},f,pv)$
 >>$
 pv:=reval pv$
 for each l in ll do <<
  degs:=cons((car l . deg(pv,cdr l)),degs)$
 >>;
 h:=cdar ll$
 for each l in cdr ll do pv:=subst(h,cdr l,pv)$
 pv:=reval pv$
 return cons(deg(pv,h),degs)
end$

symbolic procedure pde_degree(pv,ftem)$
% ftem are to be the rational functions
begin
 scalar f,h$
 if den pv neq 1 then pv:=num pv$
 h:=gensym()$
 for each f in ftem do pv:=subst({'TIMES,h,f},f,pv)$
 pv:=reval pv$
 return deg(pv,h)
end$

symbolic procedure dfsubst_update(f,der,equ)$
%  miniml update of some properties of a pde
%    equ:      pde
%    der:      derivative
%    f:        f new function
begin scalar l$
  for each d in get(equ,'derivs) do
    if not member(cadr der,car d) then l:=cons(d,l)
    else
      <<l:=cons(cons(cons(f,df_int(cdar d,cddr der)),cdr d),l)$
      put(equ,'val,
          subst(reval cons('DF,caar l),reval cons('DF,car d),
                get(equ,'val)))>>$
  put(equ,'fcts,subst(f,cadr der,get(equ,'fcts)))$
  put(equ,'allvarfcts,subst(f,cadr der,get(equ,'allvarfcts)))$
  if get(equ,'allvarfcts) then flag(list equ,'to_eval)$
%  This would reactivate equations which resulted due to
%  substitution of derivative by a function.
%  8.March 98: change again: the line 3 lines above has been reactivated
  put(equ,'rational,subst(f,cadr der,get(equ,'rational)))$
  put(equ,'nonrational,subst(f,cadr der,get(equ,'nonrational)))$
  put(equ,'derivs,sort_derivs(l,get(equ,'fcts),get(equ,'vars)))$
  return equ
end$

symbolic procedure eqinsert(s,l)$
% l is a sorted list
if not (s or get(s,'val)) or zerop get(s,'length) or member(s,l) then l
else if not l then list s
else begin scalar l1,n$
 l1:=proddel(s,l)$
 if car l1 then
  <<n:=get(s,'length)$
    l:=cadr l1$
    l1:=nil$
    while l and (n>get(car l,'length)) do
     <<l1:=cons(car l,l1)$
       l:=cdr l>>$
    l1:=append(reverse l1,cons(s,l))$
  >>
 else if l1 then l1:=cadr l1  % or reverse of it
            else l1:=l$
 return l1$
end$

symbolic procedure not_included(a,b)$
% meaning: not_all_a_in_b = setdiff(a,b)
% Are all elements of a also in b? If yes then return nil else t
% This could be done with setdiff(a,b), only setdiff
% copies expressions and needs extra memory whereas here we only
% want to know one bit (included or not)
begin scalar c$
 c:=t;
 while a and c do <<
  c:=b;
  while c and ((car a) neq (car c)) do c:=cdr c;
  % if c=nil then car a is not in b
  a:=cdr a;
 >>;
 return if c then nil
             else t
end$

symbolic procedure proddel(s,l)$
% delete all pdes from l with s as factor
% delete s if it is a consequence of any known pde from l
begin scalar l1,l2,l3,n,lnew,pdes,s_hist$
 if pairp(lnew:=get(s,'val)) and (car lnew='TIMES) then lnew:=cdr lnew
                                                   else lnew:=list lnew$
 n:=length lnew$
 pdes:=l$
 while l do <<
  if pairp(l1:=get(car l,'val)) and (car l1='TIMES) then l1:=cdr  l1
                                                    else l1:=list l1$
  if n<length l1 then     % s has less factors than car l
    if not_included(lnew,l1) then
    l2:=cons(car l,l2)    % car l is not a consequ. of s
                             else
    <<l3:=cons(car l,l3); % car l is a consequ. of s
      drop_pde(car l,nil,{'QUOTIENT,{'TIMES,s,get(car l,'val)},get(s,'val)})
    >>
  else <<
    if null not_included(l1,lnew) then % s is a consequence of car l
    <<if print_ then <<terpri()$write s," is a consequence of ",car l,".">>$
      % one could stop here but continuation can still be useful
      if null s_hist then s_hist:={'QUOTIENT,
                                   {'TIMES,car l,get(s,'val)},
                                   get(car l,'val)            }$
    >>$
    % else
    if null l3 or (car l3 neq car l) then l2:=cons(car l,l2)$
  >>;
  l:=cdr l
 >>$
 if print_ and l3 then <<
  listprint l3$
  if cdr l3 then write " are consequences of ",s
            else write " is a consequence of ",s;
  terpri()$
 >>$
 if s_hist then <<drop_pde(s,nil,s_hist);s:=nil>>$
 return list(s,reverse l2)$
end$


symbolic procedure myprin2l(l,trenn)$
if l then
   <<if pairp l then
        while l do
          <<write car l$
          l:=cdr l$
          if l then write trenn>>
   else write l>>$

symbolic procedure print_stars(s)$
begin scalar b,star,pv$
 pv:=get(s,'val)$
 if (pairp pv) and (car pv='TIMES) then pv:=t else pv:=nil$
 star:=get(s,'starde)$
 if star or pv then <<
  write "("$
  if pv then write"#"$
  if star then for b:=1:(1+cdr star) do write"*"$
  write")"$
 >>$
end$

symbolic procedure typeeq(s)$
%  print equation
 if (null print_) or (get(s,'printlength)>print_) then begin scalar a,b$
   print_stars(s);
   write " ",(a:=get(s,'terms))," terms"$
   if a neq (b:=get(s,'length)) then write", ",b," factors"$
   write", with derivatives"$
   if get(s,'starde) then <<
    write": "$ terpri()$
    print_derivs(s,nil)$
   >>   else <<
    write" of functions of all variables: "$ terpri()$
    print_derivs(s,t)$
   >>
 end                     else
 mathprint list('EQUAL,0,get(s,'val))$

symbolic procedure print_derivs(p,allvarf)$
begin scalar a,d,dl,avf;
 dl:=get(p,'derivs)$
 if allvarf then <<
  avf:=get(p,'allvarfcts);
  for each d in dl do
  if not freeoflist(d,avf) then a:=cons(d,a);
  dl:=reverse a
 >>$
 dl:=for each d in dl collect <<
  a:=if null cdar d then caar d
                    else cons('DF,car d);
  if cdr d=1 then a else {'EXPT,a,cdr d}
 >>$
 mathprint cons('! ,dl)
end$

symbolic procedure typeeqlist(l)$
%  print equations and their property lists
<<terpri()$
for each s in l do
 <<terpri()$
 write s," : "$
 if not print_all then typeeq(s)
                  else
 if (null print_) or (get(s,'printlength)>print_) then
 <<write get(s,'terms)," terms"$terpri()>>        else
 mathprint list('EQUAL,0,get(s,'val))$
 if print_all then
    <<        write "   derivs     : "$
    terpri()$print_derivs(s,nil)$
%   if struc_eqn then <<
%    terpri()$write "   no_derivs  : ",get(s,'no_derivs)$
%   >>$
             write "   terms      : ",get(s,'terms)$
    terpri()$write "   fcts       : ",get(s,'fcts)$
    terpri()$write "   vars       : ",get(s,'vars)$
    terpri()$write "   nvars      : ",get(s,'nvars)$
    terpri()$write "   length     : ",get(s,'length)$
    terpri()$write "   printlength: ",get(s,'printlength)$
    terpri()$write "   level      : ",get(s,'level)$
    terpri()$write "   allvarfcts : ",get(s,'allvarfcts)$
    terpri()$write "   rational   : ",get(s,'rational)$
    terpri()$write "   nonrational: ",get(s,'nonrational)$
    terpri()$write "   degrees    : ",get(s,'degrees)$
    terpri()$write "   starde     : ",get(s,'starde)$
    terpri()$write "   fcteval_lin: ",get(s,'fcteval_lin)$
    terpri()$write "   fcteval_nca: ",get(s,'fcteval_nca)$
    terpri()$write "   fcteval_nli: ",get(s,'fcteval_nli)$
    terpri()$write "   fct_nli_lin: ",get(s,'fct_nli_lin)$
    terpri()$write "   fct_nli_nca: ",get(s,'fct_nli_nca)$
    terpri()$write "   fct_nli_nli: ",get(s,'fct_nli_nli)$
    terpri()$write "   fct_nli_nus: ",get(s,'fct_nli_nus)$
    terpri()$write "   rl_with    : ",get(s,'rl_with)$
    terpri()$write "   dec_with   : ",get(s,'dec_with)$
    terpri()$write "   dec_with_rl: ",get(s,'dec_with_rl)$
%    terpri()$write "   dec_info   : ",get(s,'dec_info)$
    terpri()$write "   to_int     : ",flagp(s,'to_int)$
    terpri()$write "   to_fullint : ",flagp(s,'to_fullint)$
    terpri()$write "   to_sep     : ",flagp(s,'to_sep)$
    terpri()$write "   to_gensep  : ",flagp(s,'to_gensep)$
    terpri()$write "   to_decoup  : ",flagp(s,'to_decoup)$
    terpri()$write "   to_drop    : ",flagp(s,'to_drop)$
    terpri()$write "   to_eval    : ",flagp(s,'to_eval)$
    terpri()$write "   to_diff    : ",flagp(s,'to_diff)$
    terpri()$write "   to_under   : ",flagp(s,'to_under)$
    terpri()$write "   to_symbol  : ",flagp(s,'to_symbol)$
    terpri()$write "   not_to_eval: ",get(s,'not_to_eval)$
    terpri()$write "   used_      : ",flagp(s,'used_)$
    terpri()$write "   orderings  : ",get(s,'orderings)$
    terpri()$write "   histry_    : ",get(s,'histry_)$
    terpri()$write "   partitioned: ",if get(s,'partitioned) then
                                      "not nil"              else
                                      "nil"$
    terpri()$write "   split_test : ",get(s,'split_test)$
    terpri()$write "   linear_    : ",get(s,'linear_)$
    if homogen_ then <<
      terpri()$write "   hom_deg    : ",get(s,'hom_deg)
    >>$
    terpri()>>
 >> >>$

symbolic procedure rationalp(p,f)$
%  tests if p is rational in f and its derivatives
not pairp p
or
((car p='QUOTIENT) and
 polyp(cadr p,f) and polyp(caddr p,f))
or
((car p='EQUAL) and
 rationalp(cadr p,f) and rationalp(caddr p,f))
or
polyp(p,f)$

symbolic procedure ratexp(p,ftem)$
if null ftem then t
             else if rationalp(p,car ftem) then ratexp(p,cdr ftem)
                                           else nil$

symbolic procedure polyp(p,f)$
%  tests if p is a polynomial in f and its derivatives
%    p: expression
%    f: function
if my_freeof(p,f) then t
else
begin scalar a$
if atom p then a:=t
else
if member(car p,list('EXPT,'PLUS,'MINUS,'TIMES,'QUOTIENT,'DF)) then
                                        %  erlaubte Funktionen
        <<if (car p='PLUS) or (car p='TIMES) then
                <<p:=cdr p$
                while p do
                    if a:=polyp(car p,f) then p:=cdr p
                                         else p:=nil>>
        else if (car p='MINUS) then
                a:=polyp(cadr p,f)
        else if (car p='QUOTIENT) then
                <<if freeof(caddr p,f) then a:=polyp(cadr p,f)>>
        else if car p='EXPT then        %  Exponent
                <<if (fixp caddr p) then
                   if caddr p>0 then a:=polyp(cadr p,f)>>
        else if car p='DF then          %  Ableitung
                if (cadr p=f) or freeof(cadr p,f) then a:=t>>
else a:=(p=f)$
return a
end$

symbolic procedure starp(ft,n)$
%  yields T if all functions from ft have less than n arguments
begin scalar b$
  while not b and ft do                % searching a fct of all vars
  if fctlength car ft=n then b:=t
                        else ft:=cdr ft$
  return not b
end$

symbolic procedure stardep(ftem,vl)$
%  yields: nil, if a function (from ftem) in p depends
%               on all variables (from vl)
%          cons(v,n) otherwise, with v being the list of variables
%               which occur in a minimal number of n functions
begin scalar b,v,n$
  if starp(ftem,length vl) then
  <<n:=sub1 length ftem$
    while vl do                % searching var.s on which depend a
                               % minimal number of functions
    <<if n> (b:=for each h in ftem sum
                                   if member(car vl,fctargs h) then 1
                                                               else 0)
      then <<n:=b$v:=list car vl>>       % a new minimum
      else if b=n then v:=cons(car vl,v)$
      vl:=cdr vl>> >>$
  return if v then cons(v,n)             % on each varible from v depend n
                                           % functions
              else nil
end$

%symbolic procedure no_of_sep_var(ftem)$
%% assuming ftem are all functions from an ise
%% How many are there indirectly separable variables?
%% If just two then the new indirect separation is possible
%begin scalar v,vs$
%  vl:=argset(ftem);
%  for each f in ftem do
%  vs:=union(setdiff(vl,fctargs f),vs)$
%  return vs
%end$

symbolic operator parti_fn$
symbolic procedure parti_fn(fl,el)$
% fl ... alg. list of functions, el ... alg. list of equations
% partitions fl such that all functions that are somehow dependent on
% each other through equations in el are grouped in lists,
% returns alg. list of these lists
begin
 scalar f1,f2,f3,f4,f5,e1,e2;
 fl:=cdr fl;
 el:=cdr el;
 while fl do <<
  f1:=nil;         % f1 is the sublist of functions depending on each other
  f2:=list car fl; % f2 ... func.s to be added to f1, not yet checked
  fl:=cdr fl;
  while f2 and fl do <<
   f3:=car f2; f2:=cdr f2;
   f1:=cons(f3,f1);
   for each f4 in
   % smemberl will be all functions not registered yet that occur in
   % an equation in which the function f3 occurs
   smemberl(fl,    % fl ... the remaining functions not known yet to depend
            <<e1:=nil;  % equations in which f3 occurs
              for each e2 in el do
              if smember(f3,e2) then e1:=cons(e2,e1);
              e1
            >>
           )        do <<
    f2:=cons(f4,f2);
    fl:=delete(f4,fl)
   >>
  >>;
  if f2 then f1:=append(f1,f2);
  f5:=cons(cons('LIST,f1),f5)
 >>;
 return cons('LIST,f5)
end$

symbolic procedure plot_dependencies(pdes)$
begin scalar ps,fl$
  ps:=promptstring!*$ promptstring!*:=""$
  fl:=ftem_;
  if flin_ and yesp
  "Shall only functions from the linear list flin_ be considered? "
  then fl:=setdiff(fl,setdiff(fl,flin_))$
  promptstring!*:=ps$
  plot_dep_matrix(pdes,fl)
end$

symbolic procedure plot_dep_matrix(pdes,allf)$
begin scalar f,ml,lf,fl,h,lh,lc,n,m,h;
  terpri()$
  write "Horizontally: function names (each vertical),  ",
        "Vertically: equation indices"$
  terpri()$

  ml:=0;                % the maximal length of all variable names
  lf:=length allf$
  for each f in reverse allf do <<
    h:=explode f;
    lh:=length h;
    if lh>ml then ml:=lh;
    lc:=cons(h,lc);
  >>$

  % print the variable names
  for n:=1:ml do <<
    terpri()$ write"     "$
    for m:=1:lf do write <<
      h:=nth(lc,m);
      if n>length h then " "
                    else nth(nth(lc,m),n)
    >>
  >>$

  m:=add1 add1 ml;
  terpri()$terpri()$
  for each p in pdes do
  if m>=0 then <<
   h:=explode p;
   for n:=3:length h do write nth(h,n);
   for n:=(sub1 length(h)):5 do write" "$
   fl:=get(p,'fcts);
   if (not get(p,'fcteval_lin)) and
      (not get(p,'fcteval_nca)) and
      (not get(p,'fcteval_nli)) then fcteval(p,nil)$ % for writing "s"
   for each f in allf do
   if freeof(fl,f) then write" " else
   if solvable_case(p,f,'fcteval_lin) or
      solvable_case(p,f,'fcteval_nca) then write"s"
                                             else write"+"$
   terpri()$
   m:=add1 m$
   if m=23 then if not yesp "Continue ?" then m:=-1
                                         else m:=0
  >>$
end$

symbolic procedure solvable_case(p,f,case)$
begin scalar fe;
 fe:=get(p,case);
 while fe and (cdar fe neq f) do fe:=cdr fe$
 return fe
end$

%symbolic procedure lin_check(pde,fl)$
%<<for each f in fl do pde:=subst({'TIMES,lin_test_const,f},f,pde);
%  freeof(reval {'QUOTIENT,pde,lin_test_const},lin_test_const)
%>>$

symbolic procedure lin_check(pde,fl)$
% This needs pde to have prefix form.
begin scalar a,f;
 a:=pde;
 for each f in fl do a:=err_catch_sub(f,0,a);
 return
 if a then <<
  for each f in fl do pde:=subst({'TIMES,lin_test_const,f},f,pde);
  freeof(reval {'QUOTIENT,{'DIFFERENCE,pde,a},lin_test_const},lin_test_const)
 >>   else nil
end$

symbolic procedure plot_non0_coeff_ld(s)$
begin scalar dv,dl,dlc,dr,fdl,avf;
 write " Leading derivatives with non-zero symbol: "$terpri()$
 dv:=get(s,'derivs);
 avf:=get(s,'allvarfcts);
 while dv do <<
  dr:=caar dv; dv:=cdr dv;
  if member(car dr,avf) then <<
   dlc:=dl;
   while dlc and ((caar dlc neq car dr) or
                  which_deriv(car dlc,dr)  ) do dlc:=cdr dlc;
   if null dlc then dl:=cons(dr,dl);
   % which_deriv(a,b) takes two lists of derivatives and returns how
   % often you need to diff. a in order to get at least the
   % derivatives in b. e.g. which_deriv((x 2 y), (x y 2)) returns y
  >>
 >>;
 for each dr in dl do <<
  dr:=if null cdr dr then car dr
                     else cons('DF,dr);
  if get(s,'linear_) or
     freeofzero(reval {'DF,get(s,'val),dr},get(s,'fcts),
                get(s,'vars),get(s,'nonrational)) then
  fdl:=cons(dr,fdl)
 >>;
 mathprint cons('! ,fdl)
end$

%%%%%%%%%%%%%%%%%%%%%%%%%
%  leading derivatives  %
%%%%%%%%%%%%%%%%%%%%%%%%%

symbolic procedure listrel(a,b,l)$
%   a>=b  w.r.t list l; e.g. l='(a b c) ->  a>=a, b>=c
member(b,member(a,l))$

symbolic procedure abs_dfrel(p,q,vl)$
% returns t if derivative of p is lower than derivative of q
%         0         "             equal          "
%        nil        "             higher         "
% p,q  : derivatives or functions from ftem like ((f x 2 y z 3) . 2)
% ftem : list of fcts
% vl   : list of vars
begin scalar a$
return
if lex_df then dfrel2(p,q,vl) else
if zerop (a:=absdeg(cdar p)-absdeg(cdar q)) then dfrel2(p,q,vl)
else a<0$
end$

symbolic procedure mult_derivs(a,b)$
% multiplies deriv. of a and b
% a,b list of derivs of the form ((fct var1 n1 ...).pow)
begin scalar l$
 return
  if not b then a
  else if not a then b
  else
   <<
   for each s in a do
      for each r in b do
        if car s=car r then l:=union(list cons(car r,plus(cdr r,cdr s)),l)
                       else l:=union(list(r,s),l)$
   l>>$
end$

symbolic procedure all_deriv_search(p,ftem)$
%  yields all derivatives occuring polynomially in a pde  p
begin scalar a$
if not pairp p then
 <<if member(p,ftem) then a:=list cons(list p,1)>>
else
<<if member(car p,'(PLUS QUOTIENT EQUAL)) then
    for each q in cdr p do
          a:=union(all_deriv_search(q,ftem),a)
  else if car p='MINUS then a:=all_deriv_search(cadr p,ftem)
  else if car p='TIMES then
    for each q in cdr p do
          a:=mult_derivs(all_deriv_search(q,ftem),a)
  else if (car p='EXPT) and numberp caddr p then
      for each b in all_deriv_search(cadr p,ftem) do
          <<if numberp cdr b then
               a:=cons(cons(car b,times(caddr p,cdr b)),a)>>
  else if (car p='DF) and member(cadr p,ftem) then a:=list cons(cdr p,1)
>>$
return a
end$

symbolic procedure abs_ld_deriv(p)$
if get(p,'derivs) then reval cons('DF,caar get(p,'derivs))$

symbolic procedure abs_ld_deriv_pow(p)$
if get(p,'derivs) then cdar get(p,'derivs)
                  else 0$

symbolic procedure which_first(a,b,l)$
if null l then nil else
if a = car l then a else
if b = car l then b else which_first(a,b,cdr l)$


symbolic procedure total_less_dfrel(a,b,ftem,vl)$
% = 0 if a=b, =t if a<b, = nil if a>b
begin scalar fa,ad,al,bl$
  fa:=caar a$
  return
  if a=b then 0 else
  if lex_fc then % lex. order. of functions has highest priority
  if fa=caar b then
  if (ad:=abs_dfrel(a,b,vl))=0 then             % power counts
  if cdr a < cdr b then t
                   else nil
                               else
  if ad then t
        else nil
               else
  if fa=which_first(fa,caar b,ftem) then nil
                                    else t
            else % order. of deriv. has higher priority than fcts.
                 % number of variables of functions has still higher priority
  if (al:=fctlength fa) > (bl:=fctlength caar b) then nil
                                                 else
  if bl>al then t
           else
  if (ad:=abs_dfrel(a,b,vl))=0 then
  if fa=caar b then
  if cdr a < cdr b then t
                   else nil
               else
  if fa=which_first(fa,caar b,ftem) then nil
                                    else t
                               else
  if ad then t
        else nil
end$

symbolic procedure sort_derivs(l,ftem,vl)$
% yields a sorted list of all derivatives in l
begin scalar l1,l2,a$
 return
 if null l then nil
           else <<
  a:=car l$
  l:=cdr l$
  while l do <<
     if total_less_dfrel(a,car l,ftem,vl) then l1:=cons(car l,l1)
                                          else l2:=cons(car l,l2)$
     l:=cdr l
  >>$
  append(sort_derivs(l1,ftem,vl),cons(a,sort_derivs(l2,ftem,vl)))>>
end$

symbolic procedure dfmax(p,q,vl)$
%   yields the higher derivative
%   vl list of variables e.g. p=((x 2 y 3 z).2), q=((x y 4 z).1)
%                             df(f,x,2,y,3,z)^2, df(f,x,y,4,z)
if dfrel(p,q,vl) then q
                 else p$

symbolic procedure dfrel(p,q,vl)$
%   the relation "p is lower than q"
%   vl list of vars e.g. p=((x 2 y 3 z).2), q=((x y 4 z).1)
if cdr p='infinity then nil
else if cdr q='infinity then t
else begin scalar a$
 return
  if lex_df then dfrel1(p,q,vl) else
  if zerop(a:=absdeg(car p)-absdeg(car q)) then dfrel1(p,q,vl)
                                           else if a<0 then t
                                                       else nil
end$

symbolic procedure diffrelp(p,q,v)$
% gives  t  when p "<" q
%       nil when p ">" q
%        0  when p  =  q
%   p, q Paare (liste.power), v Liste der Variablen
%   liste Liste aus Var. und Ordn. der Ableit. in Diff.ausdr.,
%   power Potenz des Differentialausdrucks
if cdr p='infinity then nil
else if cdr q='infinity then t
     else dfrel1(p,q,v)$

symbolic procedure dfrel1(p,q,v)$
% p,q like ((f x 2 y z 3) . 2)
if null v then
%   if cdr p = t then if cdr q = t then 0 else nil
%                else if cdr q = t then t else
   if cdr p>cdr q then nil else         %  same derivatives,
   if cdr p<cdr q then t   else 0       %  considering powers
   % for termorderings of non-linear problems the last 2 lines
   % have to be extended
else begin
        scalar a,b$
        a:=dfdeg(car p, car v)$
        b:=dfdeg(car q, car v)$
        return if a<b then t
        else   if b<a then nil
        else dfrel1(p,q,cdr v)  %  same derivative w.r.t car v
end$

symbolic procedure dfrel2(p,q,v)$
% p,q like ((f x 2 y z 3) . 2)
if null v then 0
else begin
        scalar a,b$
        a:=dfdeg(car p, car v)$
        b:=dfdeg(car q,car v)$
        return if a<b then t
        else   if b<a then nil
        else dfrel2(p,q,cdr v)  %  same derivative w.r.t car v
end$

symbolic procedure absdeg(p)$
if null p then 0
else eval cons('PLUS,for each v in p collect if fixp(v) then sub1(v)
                                                        else 1)$

symbolic procedure maxderivs(numberlist,deriv,varlist)$
if null numberlist then
 for each v in varlist collect dfdeg(deriv,v)
else begin scalar l$
 for each v in varlist do
  <<l:=cons(max(car numberlist,dfdeg(deriv,v)),l)$
  numberlist:=cdr numberlist>>$
 return reverse l
end$

symbolic procedure dfdeg(p,v)$
%   yields order of deriv. wrt. v$
%   e.g p='(x 2 y z 3), v='x --> 2
if null(p:=member(v,p)) then 0
else if null(cdr p) or not fixp(cadr p)
        then 1                          %  v without order
        else cadr p$                    %  v with order

symbolic procedure lower_deg(p,v)$
%  reduces the order of the derivative p wrt. v by one
%   e.g p='(x 2 y z 3), v='z --> p='(x 2 y z 2)
%   e.g p='(x 2 y z 3), v='y --> p='(x 2 z 3)
%  returns nil if no v-derivative
begin scalar newp$
 while p and (car p neq v) do <<newp:=cons(car p,newp);p:=cdr p>>$
 if p then
 if null(cdr p) or not fixp(cadr p) then p:=cdr p
                                    else <<
  newp:=cons(sub1 cadr p,cons(car p,newp));
  p:=cddr p
 >> else newp:=nil;
 while p do <<newp:=cons(car p,newp);p:=cdr p>>$
 return reverse newp
end$

symbolic procedure df_int(d1,d2)$
begin scalar n,l$
return
 if d1 then
  if d2 then
   <<n:=dfdeg(d1,car d1)-dfdeg(d2,car d1)$
   l:=df_int(if cdr d1 and numberp cadr d1 then cddr d1
                                           else cdr d1 ,d2)$
   if n<=0 then l
   else if n=1 then cons(car d1,l)
   else cons(car d1,cons(n,l))
   >>
  else d1$
end$

symbolic procedure linear_fct(p,f)$
begin scalar l$
 l:=ld_deriv(p,f)$
 return ((car l=f) and (cdr l=1))
end$

% not used anymore:
%
%symbolic procedure dec_ld_deriv(p,f,vl)$
%%  gets leading derivative of f in p wrt. vars order vl
%%  result: derivative , e.g. '(x 2 y 3 z)
%begin scalar l,d,ld$
% l:=get(p,'derivs)$
% vl:=intersection(vl,get(p,'vars))$
% while caaar l neq f do l:=cdr l$
% ld:=car l$l:=cdr l$
% % --> if lex_df then dfrel1() else
% d:=absdeg(cdar ld)$
% while l and (caaar l=f) and (d=absdeg cdaar l) do
%   <<if dfrel1(ld,car l,vl) then ld:=car l$
%   l:=cdr l>>$
% return cdar ld$
%end$

symbolic procedure ld_deriv(p,f)$
%  gets leading derivative of f in p
%  result: derivative + power , e.g. '((DF f x 2 y 3 z) . 3)
begin scalar l$
 return if l:=get(p,'derivs) then
    <<while caaar l neq f do l:=cdr l$
      if l then cons(reval cons('DF,caar l),cdar l)>>
 else cons(nil,0)$
end$

symbolic procedure ldiffp(p,f)$
%  liefert Liste der Variablen + Ordnungen mit Potenz
%  p Ausdruck in LISP - Notation, f Funktion
ld_deriv_search(p,f,fctargs f)$

symbolic procedure ld_deriv_search(p,f,vl)$
%  gets leading derivative of function f in expr. p w.r.t
%  list of variables vl
begin scalar a$
if p=f then a:=cons(nil,1)
else
<<a:=cons(nil,0)$
if pairp p then
  if member(car p,'(PLUS TIMES QUOTIENT EQUAL)) then
     <<p:=cdr p$
       while p do
         <<a:=dfmax(ld_deriv_search(car p,f,vl),a,vl)$
           if cdr a='infinity then p:=nil
                              else p:=cdr p
         >>
     >>
  else if car p='MINUS then a:=ld_deriv_search(cadr p,f,vl)
  else if car p='EXPT then
     <<a:=ld_deriv_search(cadr p,f,vl)$
       if numberp cdr a then
          if numberp caddr p
          then a:=cons(car a,times(caddr p,cdr a))
          else if not zerop cdr a
            then a:=cons(nil,'infinity)
            else if not my_freeof(caddr p,f)
                    then a:=cons(nil,'infinity)
     >>
  else if car p='DF then
           if cadr p=f then a:=cons(cddr p,1)
           else if my_freeof(cadr p,f)
                then a:=cons(nil,0)                       %  a constant
                else a:=cons(nil,'infinity)
  else if my_freeof(p,f) then a:=cons(nil,0)
                       else a:=cons(nil,'infinity)
>>$
return a
end$

symbolic procedure lderiv(p,f,vl)$
%  fuehrende Ableitung in LISP-Notation mit Potenz (als dotted pair)
begin scalar l$
l:=ld_deriv_search(p,f,vl)$
return cons(if car l then cons('DF,cons(f,car l))
                  else if zerop cdr l then nil
                          else f
                ,cdr l)
end$

symbolic procedure splitinhom(q,ftem,vl)$
% Splitting the equation q into the homogeneous and inhom. part
% returns dotted pair qhom . qinhom
begin scalar qhom,qinhom,denm;
  vl:=varslist(q,ftem,vl)$
  if pairp q and (car q = 'QUOTIENT) then
  if starp(smemberl(ftem,caddr q),length vl) then
  <<denm:=caddr q; q:=cadr q>>        else return (q . 0)
                                     else denm:=1;

  if pairp q and (car q = 'PLUS) then q:=cdr q
                                 else q:=list q;
  while q do <<
   if starp(smemberl(ftem,car q),length vl) then qinhom:=cons(car q,qinhom)
                                                             else qhom  :=cons(car q,qhom);
   q:=cdr q
  >>;
  if null qinhom then qinhom:=0
                 else
  if length qinhom > 1 then qinhom:=cons('PLUS,qinhom)
                       else qinhom:=car qinhom;
  if null qhom then qhom:=0
               else
  if length qhom   > 1 then qhom:=cons('PLUS,qhom)
                       else qhom:=car qhom;
  if denm neq 1 then <<qhom  :=list('QUOTIENT,  qhom,denm);
                       qinhom:=list('QUOTIENT,qinhom,denm)>>;
  return qhom . qinhom
end$

symbolic procedure search_den(l)$
% get all denominators and arguments of LOG,... anywhere in a list l
begin scalar l1$
if pairp l then
 if car l='quotient then
  l1:=union(cddr l,union(search_den(cadr l),search_den(caddr l)))
 else if member(car l,'(log ln logb log10)) then
  if pairp cadr l and (caadr l='QUOTIENT) then
   l1:=union(list cadadr l,search_den(cadr l))
  else l1:=union(cdr l,search_den(cadr l))
 else for each s in l do l1:=union(search_den(s),l1)$
return l1$
end$

symbolic procedure zero_den(l,ftem,vl)$
begin scalar cases$
 l:=search_den(l)$
 while l do <<
  if not freeofzero(car l,ftem,vl,ftem) then cases:=cons(car l,cases);
  l:=cdr l
 >>$
 return cases
end$

symbolic procedure forg_int(forg,fges)$
for each ex in forg collect
 if pairp ex and pairp cadr ex then forg_int_f(ex,smemberl(fges,ex))
                             else ex$

symbolic procedure forg_int_f(ex,fges)$
% try to integrate expr. ex of the form df(f,...)=expr.
begin scalar p,h,f$
 p:=caddr ex$
 f:=cadadr ex$
 if pairp p and (car p='PLUS)
    then p:=reval cons('PLUS,cons(list('MINUS,cadr ex),cdr p))
    else p:=reval list('DIFFERENCE,p,cadr ex)$
 p:=integratepde(p,cons(f,fges),nil,nil,nil)$
 if p and (car p) and not cdr p then
    <<h:=car lderiv(car p,f,fctargs f)$
    p:=reval list('PLUS,car p,h)$
    for each ff in fnew_ do
      if not member(ff,ftem_) then ftem_:=fctinsert(ff,ftem_)$
    ex:=list('EQUAL,h,p)>>$
 return ex
end$

symbolic operator total_alg_mode_deriv$
symbolic procedure total_alg_mode_deriv(f,x)$
begin scalar tdf$ %,u,uli,v,vli$
 tdf:={'DF,f,x}$
% explicit program for chain rule of differentiation which is not used
% as currently f=f(u), u=u(x) gives df(f**2,x)=2*f*df(f,x)
%
% for each u in depl!* do
% if not freeof(cdr u,x) then uli:=cons(car u,uli)$
% for each u in uli do <<
%  vli:=nil$
%  for each v in depl!* do
%  if not freeof(cdr v,u) then vli:=cons(car v,vli)$
%  algebraic ( tdf:=tdf+df(f,v)*df(v,u)*df(u,x) )$
% >>$
 return reval tdf
end$

symbolic procedure no_of_v(v,l)$
% v is a variable name, l a list of derivatives like (x 2 y z 3)
% it returns the order of v-derivatives
<<while l and car l neq v do l:=cdr l;
  if null l then 0 else
  if null cdr l or not fixp cadr l or (cadr l = 1) then 1 else
  cadr l
>>$

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%  general purpose procedures  %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

symbolic procedure memberl(a,b)$
%  member for a list
if a and b then
if member(car a,b) then cons(car a,memberl(cdr a,b))
                   else memberl(cdr a,b)$

symbolic procedure smemberl(fl,ex)$
%  smember for a list
if fl and ex then
if smember(car fl,ex) then cons(car fl,smemberl(cdr fl,ex))
                      else smemberl(cdr fl,ex)$

symbolic operator my_freeof$
symbolic procedure my_freeof(u,v)$
%  a patch for FREEOF in REDUCE 3.5
not(smember(v,u)) and freeofdepl(depl!*,u,v)$

lisp flag('(my_freeof),'BOOLEAN)$

symbolic procedure freeoflist(l,m)$
%   liefert t, falls kein Element aus m in l auftritt
if null m then t
else if freeof(l,car m) then freeoflist(l,cdr m)
                        else nil$

symbolic procedure freeofdepl(de,u,v)$
if null de then t
else if smember(v,cdar de) and smember(caar de,u) then nil
else freeofdepl(cdr de,u,v)$

symbolic procedure fctins(f,flen,ftem)$
if null ftem then list f else
if fctlength car ftem < flen then cons(f,ftem)
else cons(car ftem,fctinsert(f,cdr ftem))$

symbolic procedure fctinsert(f,ftem)$
% isert a function f in the function list ftem
if freeof(ftem,f) then fctins(f,fctlength f,ftem)
                  else ftem$

symbolic procedure newfct(id,l,nfct)$
begin scalar f$
 % Only in the top level function names may be recycled otherwise
 % name clashes occur when passing back solutions with new functions
 % of integration but old used names
 if (null level_) and (id=fname_) and recycle_fcts then <<
  f:=car recycle_fcts$
  recycle_fcts:=cdr recycle_fcts
 >>                              else
 f:=mkid(id,nfct)$
 depl!*:=delete(assoc(f,depl!*),depl!*)$
 %put(f,'simpfn,'simpiden)$
 %if pairp l then f:=cons(f,l)$
 if pairp l then depl!*:=cons(cons(f,l),depl!*)$
 if print_ then
   <<terpri()$
   if pairp l then
     <<write "new function: "$
     fctprint list f>>
   else
     write "new constant: ",f>>$
 return f$
end$

symbolic procedure drop_fct(f)$
% check before that f is not one of the forg functions!
% check dropping f also from ftem_
<<if do_recycle_fnc then recycle_fcts:=f . recycle_fcts$
  depl!*:=delete(assoc(reval f,depl!*),depl!*)$
>>$

symbolic procedure varslist(p,ftem,vl)$
begin scalar l$
ftem:=argset smemberl(ftem,p)$
for each v in vl do
  if not my_freeof(p,v) or member(v,ftem) then l:=cons(v,l)$
return reverse l$
end$

symbolic procedure var_list(pdes,forg,vl)$
begin scalar l,l1$
for each p in pdes do l:=union(get(p,'vars),l)$
for each v in vl do
  if member(v,l) or not my_freeof(forg,v) then l1:=cons(v,l1)$
return reverse l1$
end$

symbolic procedure fctlist(ftem,pdes,forg)$
begin scalar fges,l$
 for each p in pdes do l:=union(get(p,'fcts),l)$
 for each f in ftem do
     if not freeof(forg,f) or member(f,l) then fges:=fctinsert(f,fges)$
 for each f in forg do
     if not pairp f and not member(f,fges) then fges:=fctinsert(f,fges)$
 for each f in l do
     if not member(f,fges) then fges:=fctinsert(f,fges)$
 l:=setdiff(ftem,fges);
 for each f in l do drop_fct(f)$
 return fges$
end$

symbolic operator fargs$
symbolic procedure fargs f$
cons('LIST,fctargs f)$

symbolic procedure fctargs f$
%  arguments of a function
if (f:=assoc(f,depl!*)) then cdr f$

symbolic procedure fctlength f$
%  number of arguments
length fctargs f$

symbolic procedure fctsort(l)$
% list sorting
begin scalar l1,l2,l3,m,n$
return
if null l then nil
else
<<n:=fctlength car l$
  l2:=list car l$
  l:=cdr l$
  while l do
   <<m:=fctlength car l$
   if m<n then l1:=cons(car l,l1)
   else if m>n then l3:=cons(car l,l3)
   else l2:=cons(car l,l2)$
   l:=cdr l>>$
  append(fctsort reverse l3,append(reverse l2,fctsort reverse l1))>>
end$

symbolic procedure listprint(l)$
%   print elements of a lisp list
if pairp l then <<
 write car l$
 for each v in cdr l do <<write ",",v>>
>>$

symbolic procedure fctprint1(f)$
%   print a function
begin scalar vl;
if f then
 if pairp f then <<
  write car f$
  if pairp cdr f then <<
   for each a in vl_ do
   if not freeof(cdr f,a) then vl:=cons(a,vl);
   write "("$
%   listprint cdr f$
   listprint append(setdiff(cdr f,vl),reverse vl)$
   write ")">>
  >>
 else write f$
end$

symbolic procedure fctprint(fl)$
%   Ausdrucken der Funktionen aus fl
begin scalar l,f,a,n,nn$
  n:=0$
  while fl do <<
    f:=car fl$
    fl:=cdr fl$
    if pairp f then
      if car f='EQUAL then
        <<n:=no_of_terms caddr f$
        if (null print_) or (n>print_) then
          <<terpri()$write cadr f,"= expr. with ",n," terms"$
          if (l:=get(cadr f,'fcts)) then
             <<write " in "$
             myprin2l(l,", ")>>$
          terpri()>>
        else mathprint f$
        n:=0>>
      else
         <<if n = 4 then
              <<terpri()$n:=0>>$
         fctprint1 f$
         if fl then write ", "$
         n:=add1 n>>
   else <<
     nn:=reval {'PLUS,4,length explode f,
                for each a in fctargs f sum add1 length explode a};
     if nn+n > 79 then <<terpri()$n:=0>>$
     l:=assoc(f,depl!*)$
     fctprint1 if l then l
                    else f$
     if fl then write ", "$
     n:=nn+n>>
  >>$
end$

symbolic procedure deprint(l)$
%   Ausdrucken der Gl. aus der Liste l
if l and print_ then for each x in l do eqprint list('EQUAL,0,x)$

symbolic procedure eqprint(e)$
%   Ausdrucken der Gl. e
if print_ then
 begin scalar n$
 n:=delength e$
 if n>print_ then
        <<write %"expr. with ",
                n," factors in ",
                no_of_terms(e)," terms"$
          terpri()
        >>
 else
        mathprint e$
end$

symbolic procedure print_level(neu)$
if print_ and level_ then <<
 terpri()$
 if neu then write "New level : "
        else write "Current level : "$
 for each m in reverse level_ do write m,"."$
>>$

symbolic procedure print_statistic(pdes,fcts)$
if print_ then begin
 integer j,k,le,r,s$
 scalar n,m,p,el,fl,vl,pl$

 %--- printing the stats of equations:
 if pdes then <<
  terpri()$write "number of equations : ",length pdes$
  terpri()$write "total no of terms   : ",
  j:=for each p in pdes sum get(p,'terms)$
  k:=for each p in pdes sum get(p,'length)$
  if k neq j then <<terpri()$write "total no of factors :  ",k>>$

  while pdes do <<
   j:=0;
   el:=nil;
   for each p in pdes do <<
    vl:=get(p,'vars);
    if vl then le:=length vl
          else le:=0;
    if ((j=0) and null vl) or
       (j=le) then el:=cons(p,el)
              else if j<le then <<
     j:=le;
     el:=list(p)
    >>
   >>;
   pdes:=setdiff(pdes,el);
   if el then <<
    n:=length el$
    terpri()$write n," equation"$
    if n>1 then write"s"$write" in ",j," variable"$
    if j neq 1 then write"s"$
    write": "$

    if struc_eqn then el:=sort_deriv_pdes(el)$
    repeat <<
     if struc_eqn then <<
      pl:=first el; el:=cdr el;
      terpri()$
      write length cdr pl," equations with ",car pl," derivative",
            if car pl = 1 then ":" else "s:"$
      pl:=cdr pl
     >>           else <<pl:=el;el:=nil>>;
     terpri()$
     k:=0;
     while pl do <<
      if (k geq 70) then <<k:=0;terpri()>>$
      k:=k+4+length explode car pl + length explode get(car pl,'terms)$
      write car pl,"(",get(car pl,'terms)$
      r:=get(car pl,'val)$
      if (s:=get(car pl,'starde)) then <<
       for r:=1:(1+cdr s) do write"*"$
       k:=k+1+cdr s;
      >>$
      if (pairp r) and (car r='TIMES) then write"#"$
      if flin_ and freeoflist(r,flin_) then write"a"$
      write")"$
      pl:=cdr pl$
      if pl then write","$
     >>;

    >> until null el;

   >>$
   j:=add1 j;
  >>
 >>
 else <<terpri()$write "no equations">>$
 %--- printing the stats of functions:
 for each f in fcts do if not pairp f then fl:=cons(f,fl)$
 if fl then <<
  fl:=fctsort reverse fl$
  m:=fctlength car fl$
  while m>=0 do <<
   n:=0$
   el:=nil;
   while fl and (fctlength car fl=m) do <<
    n:=add1 n$
    el:=cons(car fl,el)$
    fl:=cdr fl
   >>$
   if n>0 then
   if m>0 then <<
    terpri()$
    write n," function"$
    if n>1 then write"s"$
    write" with ",m," argument",if m>1 then "s : "
                                       else "  : "
   >>     else <<
    terpri()$
    write n," constant"$
    if n>1 then write"s"$
    write" : "
   >>$
   k:=0;
   while el do <<
    if k=10 then <<k:=0;terpri()>>
            else k:=add1 k$
    write car el$
    el:=cdr el$
    if el then write","$
   >>$
   m:=if fl then fctlength car fl
            else -1
  >>
 >>    else <<terpri()$write "no functions or constants">>$
 terpri()$
end$

symbolic procedure get_statistic(pdes,fcts)$
 % returns:    {time(),
 %              stepcounter_,
 %              number of pdes,
 %              number of terms,
 %              length of pdes,
 %              {{no of eq, no of var in eq}, ...}
 %              {{no of fc, no of var in fc}, ...}
 %             }
if contradiction_ then "contradiction" else
begin
 integer j,le$
 scalar n,p,el,fl,vl,li,stats$

 stats:={for each p in pdes sum get(p,'length),
         for each p in pdes sum get(p,'terms),
         length pdes,
         stepcounter_,
         time()}$

 %--- the statistics of equations:
 while pdes do <<
  % j is number of variables and el the list of equations
  j:=0;
  el:=nil;
  for each p in pdes do <<
   vl:=get(p,'vars);
   if vl then le:=length vl
         else le:=0;
   if ((j=0) and null vl) or
      (j=le) then el:=cons(p,el)
             else if j<le then <<
    j:=le;
    el:=list(p)
   >>
  >>;
  pdes:=setdiff(pdes,el);
  li:=cons({length el,j},li)
  % length el equations in j variables
 >>;
 stats:=cons(li,stats)$
 li:=nil;

 %--- the statistics of functions:
 for each f in fcts do if not pairp f then fl:=cons(f,fl)$
 if fl then <<
  fl:=fctsort reverse fl$
  j:=fctlength car fl$
  while j>=0 do <<
   n:=0$
   while fl and (fctlength car fl=j) do <<n:=add1 n$ fl:=cdr fl>>$
   li:=cons({n,j},li)$
   % n functions of j variables
   j:=if fl then fctlength car fl
            else -1
  >>
 >>$

 return reverse cons(li,stats)
end$

symbolic procedure sort_deriv_pdes(pdes)$
begin scalar max_no_deri,cp,pl,res$
 max_no_deri:=0;
 cp:=pdes;
 while cp do <<
  if get(car cp,'no_derivs)>max_no_deri then
  max_no_deri:=get(car cp,'no_derivs);
  cp:=cdr cp
 >>;
 repeat <<
  pl:=nil;
  cp:=pdes;
  while cp do <<
   if get(car cp,'no_derivs)=max_no_deri then pl:=cons(car cp,pl);
   cp:=cdr cp
  >>$
  if pl then res:=cons(cons(max_no_deri,reverse pl),res)$
  pdes:=setdiff(pdes,pl);
  max_no_deri:=if zerop max_no_deri then nil
                                    else sub1(max_no_deri);
 >> until (null max_no_deri) or (null pdes);
 return res
end$

symbolic procedure print_pdes(pdes)$
% print all pdes up to some size
begin scalar pl,ps,n,pdecp$
  terpri()$
  if pdes then <<
   if (null !*batch_mode) and
      (batchcount_<stepcounter_) and
      (cdr pdes) then << % if more than one pde
    write"What is the maximal number of terms of equations to be shown? "$
    ps:=promptstring!*$
    promptstring!*:=""$
    terpri()$n:=termread()$
    promptstring!*:=ps$
    for each pl in pdes do
    if get(pl,'terms)<=n then pdecp:=cons(pl,pdecp);
    pdecp:=reverse pdecp;
   >>          else pdecp:=pdes$

   write "equations : "$
   if struc_eqn then <<
    pl:=sort_deriv_pdes(pdecp)$
    while pl do <<
     terpri()$
     write length cdar pl," equations with ",caar pl," derivatives:"$
     typeeqlist(cdar pl)$
     pl:=cdr pl
    >>
   >>           else typeeqlist(pdecp)
  >>      else <<write "no equations"$ terpri()>>$
end$

symbolic procedure print_ineq(ineqs)$
% print all ineqs
begin scalar a,b,c$
 terpri()$
 if ineqs then <<
  terpri()$write "non-vanishing expressions: "$
  for each a in ineqs do if pairp a then c:=cons(a,c)
                                    else b:=cons(a,b);
  listprint b;terpri()$
  for each a in c do eqprint a
 >>
end$

symbolic procedure print_fcts2(pdes,fcts)$
% print all fcts and vars
begin scalar dflist,dfs,f,p,cp,h,hh,ps,showcoef$

for each h in fcts do if not pairp h then hh:=cons(h,hh);
 ps:=promptstring!*$ promptstring!*:=""$
% write "Which function out of "$terpri()$
% listprint(hh)$
% write"? "$terpri()$
% write"Enter the function name only or ; for all functions."$terpri()$
%
% h:=termread();
% if h neq '!; and not_included(list h,hh) then <<
%  write"This is not a function in the list."$
%  terpri();
%  h:=nil
% >>;
% if null h then return nil;
% if h='!; then fcts:=hh
%          else fcts:=list h;

 fcts:=select_from_list(hh,nil)$
 pdes:=select_from_list(pdes,nil)$

 write"Do you want to see the coefficients of all derivatives in all equations"$
 terpri()$
 write"in factorized form which may take relatively much time? y/n"$
 terpri()$
 repeat
  h:=termread()
 until (h='y) or (h='n);
 if h='n then showcoef:=nil else showcoef:=t;

 promptstring!*:=ps$

 while fcts do
 if pairp car fcts then fcts:=cdr fcts
                   else <<
  f:=car fcts;  fcts:=cdr fcts;
  dflist:=nil;
  for each p in pdes do if not freeof(get(p,'fcts),f) then <<
   dfs:=get(p,'derivs);
   while dfs do <<
    if caaar dfs=f then <<
     cp:=dflist;
     while cp and (caar cp neq caar dfs) do cp:=cdr cp;
     if cdaar dfs then h:=cons('DF,caar dfs)
                  else h:=caaar dfs;
     if showcoef then
     if null cp then dflist:=cons({caar dfs,{'LIST,p,
                                             factorize coeffn(get(p,'val),h,1)}},
                                  dflist)
                else rplaca(cp,cons(caar cp,
                                    cons({'LIST,p,
                                          factorize coeffn(get(p,'val),h,1)},
                                         cdar cp)))
               else
     if null cp then dflist:=cons({caar dfs,p},dflist)
                else rplaca(cp,cons(caar cp,cons(p,cdar cp)))
    >>;
    dfs:=cdr dfs
   >>;
  >>;
  while dflist do <<
   dfs:=car dflist;dflist:=cdr dflist;
   if cdar dfs then h:=cons('DF,car dfs)
               else h:=caar dfs;
   if showcoef then algebraic <<write h,": ",lisp cons('LIST,cdr dfs)>>
               else <<write h,": "$ print cdr dfs$ terpri()>>
  >>;
 >>;
end$

symbolic procedure print_fcts(fcts,vl)$
% print all fcts and vars
<<if fcts then
         <<terpri()$write "functions : "$
           fctprint(fcts);
           terpri()$write "with ",
           for each p in fcts sum no_of_terms(p)," terms">>$
 if vl then
         <<terpri()$write "variables : "$
         fctprint(vl)>>$
>>$

symbolic procedure print_pde_fct_ineq(pdes,ineqs,fcts,vl)$
% print all pdes, ineqs and fcts
if print_ then begin$
 print_pdes(pdes)$
 print_ineq(ineqs)$
 print_fcts(fcts,vl)$
 print_statistic(pdes,fcts)
end$

symbolic procedure no_of_terms(d)$
if not pairp d then if (null d) or (zerop d) then 0
                                             else 1 else
if car d='PLUS then length d - 1            else
if car d='EQUAL then no_of_terms(cadr  d) +
                     no_of_terms(caddr d)   else
if (car d='MINUS) or (car d='QUOTIENT) then
   no_of_terms(cadr d)                 else
if car d='EXPT then
if (not fixp caddr d) or (caddr d < 2) then 1 else
% number of terms of (a1+a2+..+an)**r = n+r-1 over r
begin scalar h,m,q$
 m:=no_of_terms(cadr d)-1;
 h:=1;
 for q:=1:caddr d do h:=h*(m+q)/q;
 return h
end            else
if car d='TIMES then begin scalar h,r;
 h:=1;
 for each r in cdr d do h:=h*no_of_terms(r);
 return h
end             else 1$

symbolic procedure delength(d)$
%   Laenge eines Polynoms in LISP - Notation
if not pairp d then
 if d then 1
      else 0
else
if (car d='PLUS) or (car d='TIMES) or (car d='QUOTIENT)
   or (car d='MINUS) or (car d='EQUAL)
   then for each a in cdr d sum delength(a)
else 1$

symbolic procedure pdeweight(d,ftem)$
%   Laenge eines Polynoms in LISP - Notation
if not smemberl(ftem,d) then 0
else if not pairp d then 1
else if (car d='PLUS) or (car d='TIMES) or (car d='EQUAL)
        or (car d='QUOTIENT) then
         for each a in cdr d sum pdeweight(a,ftem)
else if (car d='EXPT) then
        if numberp caddr d then
         caddr d*pdeweight(cadr d,ftem)
                           else
        pdeweight(caddr d,ftem)+pdeweight(cadr d,ftem)
else if (car d='MINUS) then pdeweight(cadr d,ftem)
else 1$

symbolic procedure desort(l)$
% sort expressions hat are the elements of the list l by size
for each a in idx_sort for each b in l collect cons(delength b,b)
collect cdr a$

symbolic procedure idx_sort(l)$
% All elements of l have a numerical first element and are sorted
% by that number
begin scalar l1,l2,l3,m,n$
return
if null l then nil
else
<<n:=caar l$
  l2:=list car l$
  l:=cdr l$
  while l do
   <<m:=caar l$
   if m<n then l1:=cons(car l,l1)
   else if m>n then l3:=cons(car l,l3)
   else l2:=cons(car l,l2)$
   l:=cdr l>>$
  append(idx_sort(l1),append(l2,idx_sort(l3)))>>
end$

symbolic procedure rat_idx_sort(l)$
% All elements of l have a rational number first element
% and are sorted by that number
% The rational number has to be reval-ed !
begin scalar l1,l2,l3,m,n$
return
if null l then nil
else
<<n:=caar l$
  l2:=list car l$
  l:=cdr l$
  while l do
   <<m:=caar l$
   if rational_less(m,n) then l1:=cons(car l,l1)
   else if rational_less(n,m) then l3:=cons(car l,l3)
   else l2:=cons(car l,l2)$
   l:=cdr l>>$
  append(rat_idx_sort(l1),append(l2,rat_idx_sort(l3)))>>
end$

symbolic procedure argset(ftem)$
%  List of arguments of all functions in ftem
if ftem then union(reverse fctargs car ftem,argset(cdr ftem))
        else nil$

symbolic procedure no_fnc_of_v$
begin scalar vl,v,nofu,f,nv$
  % How many functions do depend on each variable?
  vl:=argset(ftem_)$
  for each v in vl do <<
    nofu:=0;  % the number of functions v occurs in
    for each f in ftem_ do
    if not freeof(fctargs f,v) then nofu:=add1 nofu$
    nv:=cons((v . nofu),nv)$
  >>$
  return nv
end$

procedure push_vars(liste)$
for each x in liste collect
if not boundp x then x else eval x$ % valuecell x$

symbolic procedure backup_pdes(pdes,forg)$
%  make a backup of all pdes
begin scalar allfl$
 return
 list(push_vars glob_var,
      for each p in pdes collect
      list(p,
           for each q in prop_list collect cons(q,get(p,q)),
           <<allfl:=nil;
             for each q in allflags_ do
             if flagp(p,q) then allfl:=cons(q,allfl);
             allfl>>),
      for each f in forg collect
           if pairp f then cons(f,get(cadr f,'fcts))
                      else cons(f,get(     f,'fcts)),
      for each id in idnties_ collect
      list(id,get(id,'val),flagp(id,'to_int),flagp(id,'to_subst))
     )
end$

%symbolic procedure backup_pdes(pdes,forg)$
%%  make a backup of all pdes
%begin scalar cop$
% cop:=list(nequ_,
%           for each p in pdes collect
%               list(p,
%                    for each q in prop_list collect cons(q,get(p,q)),
%                    for each q in allflags_ collect if flagp(p,q) then q),
%           for each f in forg collect
%               if pairp f then cons(cadr f,get(cadr f,'fcts))
%                          else cons(f,get(f,'fcts)),
%           ftem_,
%           ineq_,
%           recycle_ens,
%           recycle_fcts)$
% return cop
%end$

symbolic procedure pop_vars(liste,altewerte)$
foreach x in liste do <<set (x,car altewerte);
                        altewerte := cdr altewerte>>$

symbolic procedure restore_pdes(bak)$
%  restore all data: glob_var, pdes, forg
begin scalar pdes,forg$

  % recover values of global variables
  pop_vars(glob_var,car bak)$
  % recover the pdes
  for each c in cadr bak do <<
    pdes:=cons(car c,pdes)$
    for each s in cadr  c do put(car c,car s,cdr s)$
    for each s in caddr c do flag1(car c,s)
  >>$

  % recover the properties of forg
  for each c in caddr bak do <<
    forg:=cons(car c,forg)$
    if pairp car c then put(cadar c,'fcts,cdr c)
  >>$

  % recover the properties of idnties_
  if cdddr bak then
  for each c in cadddr bak do <<
    put(car c,'val,cadr c);
    if caddr c then flag1(car c,'to_int)
               else if flagp(car c,'to_int) then remflag(car c,'to_int);
    if caddr c then flag1(car c,'to_subst)
               else if flagp(car c,'to_subst) then remflag(car c,'to_subst);
  >>$

  return {reverse pdes,reverse forg}$
end$

%symbolic procedure restore_pdes(cop)$
%%  restore the pde list cop
%%  first element must be the old value of nequ_
%%  the elements must have the form (p . property_list_of_p)
%begin scalar pdes$
%  % delete all new produced pdes
%  for i:=car cop:sub1 nequ_ do setprop(mkid(eqname_,i),nil)$
%  nequ_:=car cop$
%  for each c in cadr cop do
%    <<pdes:=cons(car c,pdes)$
%    for each s in cadr c do
%        put(car c,car s,cdr s)$
%    for each s in caddr c do
%        if s then flag1(car c,s)$
%    >>$
%  for each c in caddr cop do
%    put(car c,'fcts,cdr c)$
%  ftem_:=cadddr cop$
%  ineq_:=car cddddr cop$
%  recycle_eqns:= cadr cddddr cop$
%  recycle_fcts:= caddr cddddr cop$
%  return reverse pdes$
%end$

%symbolic procedure copy_from_backup(copie)$
%%  restore the pde list copie
%%  first element must be the old value of nequ_
%%  the elements must have the form (p . property_list_of_p)
%%  at least recycle_eqns should not work with this procedure
%begin scalar l,pdes,cop$
%  cop:=cadr copie$
%  l:=cop$
%  for i:=1:length cop do
%    <<pdes:=cons(mkid(eqname_,nequ_),pdes)$
%    setprop(car pdes,nil)$
%    nequ_:=add1 nequ_>>$
%  pdes:=reverse pdes$
%  for each p in pdes do
%    <<cop:=subst(p,caar l,cop)$
%    l:=cdr l>>$
%  for each c in cop do
%    <<for each s in cadr c do
%        put(car c,car s,cdr s)$
%    for each s in caddr c do
%        if s then flag1(car c,s)$
%    >>$
%  for each c in caddr copie do
%    put(car c,'fcts,cdr c)$
%  ftem_:=cadddr copie$
%  return pdes$
%end$

symbolic procedure deletepde(pdes)$
begin scalar s,l,ps$
 ps:=promptstring!*$
 promptstring!*:=""$
 terpri()$
 write "Equations to be deleted: "$
 l:=select_from_list(pdes,nil)$
 promptstring!*:=ps$
 for each s in l do
 if member(s,pdes) then pdes:=drop_pde(s,pdes,nil)$
 return pdes
end$

symbolic procedure new_pde()$
begin scalar s$
 if null car recycle_eqns then <<
  s:=mkid(eqname_,nequ_)$
  nequ_:=add1 nequ_$
 >>                       else <<
  s:=caar recycle_eqns$
  recycle_eqns:=(cdar recycle_eqns) . (cdr recycle_eqns)
 >>$
 setprop(s,nil)$
 return s
end$

symbolic procedure drop_pde_from_properties(p,pdes)$
begin
 for each q in pdes do if q neq p then <<
  drop_dec_with(p,q,t)$
  drop_dec_with(p,q,nil)$
  drop_rl_with(p,q)
 >>
end$

symbolic procedure drop_pde_from_idties(p,pdes,pval)$
% to be used whenever the equation p is dropped or changed and
% afterwards newly characterized in update,
% pval is the new value of p in terms of the previous value,
% if this is unknown then pval=nil
% to be done before setprop(p,nil)
begin scalar q,newidval,idnt$
 for each q in pdes do if q neq p then
 if not freeof(get(q,'histry_),p) then
 put(q,'histry_, if null pval then q
                              else subst(pval,p,get(q,'histry_)))$

 if record_hist and (getd 'show_id) then <<
  idnt:=idnties_$
  while idnt do <<
   if not freeof(get(car idnt,'val),p) then
   if null pval then drop_idty(car idnt)
                else <<
    % Once pdes_ is available as global variable then simplify 'val
    % before put()
    newidval:=reval subst(pval,p,get(car idnt,'val))$
    if trivial_idty(pdes,newidval) then drop_idty(car idnt)
                                   else <<
     put(car idnt,'val,newidval);
     flag1(car idnt,'to_subst)$
     flag1(car idnt,'to_int)
    >>
   >>;
   idnt:=cdr idnt
  >>;
  if pval and not zerop pval and (p neq get(p,'histry_)) then <<
   idnt:=reval num reval {'PLUS,get(p,'histry_),{'MINUS,pval}}$
   if not zerop idnt then
   new_idty(idnt,pdes,if pdes then t else nil)
  >>
 >>
end$

symbolic procedure drop_pde(p,pdes,pval)$
% pval is the value of p in terms of other equations,
% if pval=nil then unknown
% pdes should be a list of all currently used pde-names
begin
 if do_recycle_eqn then
 recycle_eqns:=(car recycle_eqns) . union({p},cdr recycle_eqns)$
 depl!*:=delete(assoc(reval p,depl!*),depl!*)$
 drop_pde_from_idties(p,pdes,pval)$
 setprop(p,nil)$
 return delete(p,pdes)
end$

symbolic procedure change_pde_flag(pdes)$
begin scalar p,ty,h$
 repeat <<
  terpri()$
  write "From which PDE do you want to change a ",
        "flag or property, e.g. e_23?"$
  terpri()$
  p:=termread()$
 >> until not freeof(pdes,p)$
 terpri()$
 write"Type in one of the following flags that is to be flipped "$
 terpri()$
 write"(e.g. to_int <ENTER>): "$
 terpri()$terpri()$
 write allflags_;
 terpri()$terpri()$
 write"or type in one of the following properties that is to be changed"$
 terpri()$
 write"(e.g. vars <ENTER>): "$
 terpri()$terpri()$
 write prop_list;
 terpri()$terpri()$
 ty:=termread()$
 if member(ty,allflags_) then <<
  if flagp(p,ty) then remflag1(p,ty)
                 else flag1(p,ty)$
  write"The new value of ",ty,": ",flagp(p,ty)$
 >>                      else
 if member(ty,prop_list) then <<
  terpri()$
  write"current value: ",get(p,ty)$
  terpri()$
  write"new value (e.g. (x y z) ENTER): "$
  terpri()$
  h:=termread()$
  put(p,ty,h)$
  write"The new value of ",ty,": ",get(p,ty)$
 >>                      else write"Input not recognized."$
 terpri()$
end$

symbolic procedure restore_backup_from_file(pdes,forg,nme)$
begin scalar ps,s,p,echo_bak$
  if nme=t then <<
    ps:=promptstring!*$
    promptstring!*:=""$
    terpri()$
    write"Please give the name of the file in double quotes"$terpri()$
    write"without `;' : "$
    s:=termread()$
  >>     else
  if nme then s:=nme
         else s:=level_string(session_)$
  % infile s$
  echo_bak:=!*echo; semic!*:='!$; in s$ !*echo:=echo_bak;
  %-- cleaning up:
  for each p in pdes do setprop(p,nil)$
  for each p in forg do if pairp p then put(cadr p,'fcts,nil)$
  %-- assigning the new values:
  promptstring!*:=ps$
  size_hist   :=car backup_; backup_:=cdr backup_;
  stepcounter_:=car backup_; backup_:=cdr backup_;
  level_      :=car backup_; backup_:=cdr backup_;
  nfct_       :=car backup_; backup_:=cdr backup_;
  time_limit  :=car backup_; backup_:=cdr backup_;
  limit_time  :=car backup_; backup_:=cdr backup_;
  history_    :=car backup_; backup_:=cdr backup_;
  s:=restore_pdes(backup_)$
  backup_:=nil;
  orderings_:=car orderings_;
  return s
end$

symbolic procedure level_string(s)$
 << for each m in reverse level_ do s := if s then bldmsg("%w%d.",s,m)
                                               else bldmsg("%d.",m  );
 s>>;

symbolic procedure backup_to_file(pdes,forg,nme)$
begin scalar s,ps$ %,levelcp$
  if nme=t then <<
    ps:=promptstring!*$
    promptstring!*:=""$
    terpri()$
    write"Please give the name of the file in double quotes"$terpri()$
    write"without `;' : "$
    s:=termread()$
    promptstring!*:=ps$
  >>     else
  if nme then s:=nme
         else s:=level_string(session_)$
  out s;
  off nat$
  orderings_:=list orderings_;
  write"off echo$"$
  write "backup_:='"$terpri()$
  print cons(size_hist,cons(stepcounter_,cons(level_,cons(nfct_,
        cons(time_limit,cons(limit_time,cons(history_,
        backup_pdes(pdes,forg))))))))$
  write"$"$terpri()$
  write "end$"$terpri()$
  shut s;
  on nat;
end$

symbolic procedure delete_backup$
begin scalar s$
 s:=level_string(session_);
 delete!-file s;
end$

symbolic procedure restore_and_merge(soln,pdes,forg)$
% pdes, forg are cleaned up
% one could just use restore_pdes without assigning bak
% but then it would not be stored in a file, such that
% rb can reload the file
begin scalar bak,newfdep,sol,f,h$

 % store ongoing global values in bak
 newfdep:=nil$
 for each sol in soln do
 if sol then <<
   for each f in caddr sol do
   if h:=assoc(f,depl!*) then newfdep:=cons(h,newfdep);
 >>;
 bak:=append(list(size_hist,stepcounter_,level_,nfct_,
                  time_limit,limit_time,history_),
             newfdep);

 h:=restore_backup_from_file(pdes,forg,nil)$

 size_hist   :=car bak; bak:=cdr bak;
 stepcounter_:=car bak; bak:=cdr bak;
 level_      :=car bak; bak:=cdr bak;
 nfct_       :=car bak; bak:=cdr bak;
 time_limit  :=car bak; bak:=cdr bak;
 limit_time  :=car bak; bak:=cdr bak;
 history_    :=car bak; bak:=cdr bak;
 depl!*      :=append(depl!*,bak);
 return h
end$

symbolic procedure write_in_file(pdes,forg)$
begin scalar p,pl,s,h,ps,wn,vl,ftem$
  ps:=promptstring!*$
  promptstring!*:=""$
  terpri()$
  write "Enter a list of equations, like e2,e5,e35; from: "$terpri()$
  listprint(pdes)$
  terpri()$write "To write all equations just enter ; "$terpri()$
  repeat <<
    s:=termlistread()$
    h:=s;
    if s=nil then pl:=pdes else <<
      pl:=nil;h:=nil$
      if (null s) or pairp s then <<
        for each p in s do
        if member(p,pdes) then pl:=cons(p,pl);
        h:=setdiff(pl,pdes);
      >> else h:=s;
    >>;
    if h then <<write "These are no equations: ",h,"   Try again."$terpri()>>$
  >> until null h$
  write"Shall the name of the equation be written? (y/n) "$
  repeat s:=termread()
  until (s='y) or (s='Y) or (s='n) or (s='N)$
  if (s='y) or (s='Y) then wn:=t$
  write"Please give the name of the file in double quotes"$terpri()$
  write"without `;' : "$
  s:=termread()$
  out s;
  off nat$

  write"load crack$"$terpri()$
  write"lisp(nfct_:=",nfct_,")$"$terpri()$
  if wn then write"lisp(nequ_:=",nequ_,")$"$terpri()$
  write"off batch_mode$"$terpri()$
  for each p in pl do <<h:=get(p,'vars);if h then vl:=union(h,vl)>>$
  write"list_of_variables:="$
  algebraic write lisp cons('LIST,vl)$

  for each p in pl do <<h:=get(p,'fcts);if h then ftem:=union(h,ftem)>>$
  write"list_of_functions:="$
  algebraic write lisp cons('LIST,ftem)$

  for each h in ftem do
  if assoc(h,depl!*) then <<
    p:=pl;
    while p and freeof(get(car p,'val),h) do p:=cdr p;
    if p then <<
      write "depend ",h$
      for each v in cdr assoc(h,depl!*) do <<
        write ","$print v
      >>$
      write "$"$terpri()$

%      for each v in cdr assoc(h,depl!*) do
%      algebraic write "depend ",lisp h,",",lisp v$
    >>
  >>$
  if wn then <<
    for each h in pl do algebraic (write h,":=",lisp (get(h,'val)))$
    write"list_of_equations:="$
    algebraic write lisp( cons('LIST,pl) )
  >>    else <<
    write"list_of_equations:="$
    algebraic write lisp( cons('LIST,
       for each h in pl collect get(h,'val)) )$
  >>$

  write"list_of_inequalities:="$
  algebraic write lisp( cons('LIST,ineq_))$

  terpri()$ write"solution_:=crack(list_of_equations,"$
  terpri()$ write"                 list_of_inequalities,"$
  terpri()$ write"                 list_of_functions,"$
  terpri()$ write"                 list_of_variables)$"$
  terpri()$

  for each h in forg do <<
   terpri()$
   if pairp h and (car h = 'EQUAL) then
   algebraic
   write lisp(cadr  h)," := sub(second first solution_,",
         lisp(caddr h),")"
  >>$
  terpri()$
  write"end$"$terpri()$terpri()$
  write"These data were produced with the following input:"$terpri()$terpri()$
  write"lisp( old_history := "$terpri()$
  write"'",reverse history_,")$"$terpri()$
  shut s;
  on nat;
  promptstring!*:=ps$
end$

symbolic procedure give_low_priority(pdes,f)$
% It assumes that f is element of ftem_.
% It assumes that if f is element of flin_ then flin_ functions
% come first in each group of functions with the same number
% of independent variables.
% If f is element of flin_ then f is put at the end of the flin_
% functions with equally many variables but before the first functions
% that occur in ineq_ in order to change ftem_ as little as possible
% not to invalidate previous decoupling.

begin scalar ftemcp,ano,h,s,fli$
 ftemcp:=ftem_$
 while ftemcp and (car ftemcp neq f) do <<
  h:=cons(car ftemcp,h)$
  ftemcp:=cdr ftemcp
 >>$
 % Is there an element of the remaining ftemcp with the same no of
 % variables and that is not in ineq_ ?

 if ftemcp then <<
  ftemcp:=cdr ftemcp;
  ano:=fctlength f$
  if member(f,flin_) then fli:=t$
  while ftemcp do
  if (ano > (fctlength car ftemcp)) or
     (fli and (not member(car ftemcp,flin_))) then ftemcp:=nil else <<
   h:=cons(car ftemcp,h)$
   ftemcp:=cdr ftemcp$
   if not member(car h,ineq_) then <<
    while ftemcp and
          (ano = (fctlength car ftemcp)) and
          (not member(car ftemcp,ineq_)) and
          ((not fli) or member(car ftemcp,flin_)) do <<
     h:=cons(car ftemcp,h)$
     ftemcp:=cdr ftemcp
    >>$

    if print_ or tr_orderings then <<
     write"The lexicographical ordering of unknowns is changed"$
     terpri()$
     write"because ",f," has to be non-zero, giving ",f," a low priority."$
     terpri()$
     write "Old ordering: "$
     s:=ftem_;
     while s do <<write car s$ s:=cdr s$ if s then write",">>$
     terpri()$
     write "New ordering: "$
     s:=append(reverse h,cons(f,ftemcp));
     while s do <<write car s$ s:=cdr s$ if s then write",">>$
     terpri()$

    >>$
    change_fcts_ordering(append(reverse h,cons(f,ftemcp)),pdes,vl_)$
    ftemcp:=nil
   >>  % of not member(car h,ineq_)
  >>   % of ano > (fctlength car ftemcp)
 >>    % of ftemcp
end$

symbolic procedure addineq(pdes,newineq)$
% it assumes newineq involves functions of ftem_
begin scalar h1,h2,h3$
 newineq:=simp_ineq(newineq);
 h1:=cdr err_catch_fac(newineq)$          % h1 is a lisp list of factors
 if null cdr h1 then <<
  h2:=signchange(car h1);                 % only one factor
  if not member(h2,ineq_) then <<ineq_:=cons(h2,ineq_);h3:=cons(h2,h3)>>
 >>             else for each h2 in h1 do <<
  h2:=signchange(h2);
  if (not freeoflist(h2,ftem_)) and
     (not member(h2,ineq_)) then <<ineq_:=cons(h2,ineq_);h3:=cons(h2,h3)>>
 >>$

 if print_ and h3 then <<
  write"The list of inequalities got extended."$terpri()
 >>$
 % h3 is the list of all new non-zero expressions
 % if any one of these expressions is an element of ftem_ then it
 % should get a low priority in the lexicographical ordering for
 % non-linear problems
 if flin_ and null lin_problem then % maybe also for flin_=nil
 for each h2 in h3 do
 if atom h2 and member(h2,ftem_) then give_low_priority(pdes,h2);
 % h2 gets a low priority so that it is eliminated late in decoupling
 % to be available as non-zero coefficient as long as possible to
 % allow substitutions of other functions without case-distinctions

 if pdes then for each h2 in h3 do update_fcteval(pdes,h2);

 % If one term of the equation is non-zero then the sum of the
 % remaining terms has to be non-zero too
 if h3 and pdes then for each h2 in pdes do
 if get(h2,'terms)=2 then new_ineq_from_pde(h2,pdes)

end$

% symbolic procedure drop_factor(h,pro)$
% % This procedure drops a factor h or its negative from an expression pro
% begin scalar hs,newpro,mi;
%  hs:=signchange(h);
%  if pairp pro and (car pro='MINUS) then <<pro:=cadr pro; mi:=t>>;
%  if pro = h  then newpro:= 1 else
%  if pro = hs then newpro:=-1 else
%  if pairp pro and (car pro = 'TIMES) then
%  if member(h ,pro) then newpro:=reval delete(h ,pro) else
%  if member(hs,pro) then newpro:=reval list('MINUS,delete(hs,pro));
%  if mi and newpro then newpro:=reval list('MINUS,newpro)
%  return newpro
% end$

symbolic procedure update_fcteval(pdes,newineq)$
begin scalar p,pv,ps,hist,h1,h2$
 for each p in pdes do <<
  pv:=get(p,'val)$
  if pairp pv and (car pv='TIMES) and member(newineq,pv) then <<
   pv:=reval {'QUOTIENT,pv,newineq};
   if record_hist then hist:=reval {'QUOTIENT,get(p,'histry_),newineq}$
   update(p,pv,get(p,'fcts),get(p,'vars),t,list(0),pdes)$
   drop_pde_from_idties(p,pdes,hist)$
   drop_pde_from_properties(p,pdes)
  >> else <<
   ps:=get(p,'fcteval_nli)$
   while ps and
     <<h1:=caar ps;
       h2:=signchange(h1);
       (not ((newineq=h1              ) or
             (pairp h1            and
              (car h1 = 'TIMES)   and
               member(newineq,h1)     )    )) and
       (not ((newineq=h2              ) or
             (pairp h2            and
              (car h2 = 'TIMES)   and
               member(newineq,h2)     )    ))
     >> do ps:=cdr ps;
   if ps then << % We would have to check whether apart from the
    % new non-zero factor, the other factors can vanish for
    % specific values of ftem_ or not. Instead of programming
    % this again here we simply change flags to re-compute all
    % fct... properties later when a substitution is to be done.
    flag1(p,'to_eval)$
    put(p,'fcteval_lin,nil)$
    put(p,'fcteval_nca,nil)$
    put(p,'fcteval_nli,nil)$
    put(p,'fct_nli_lin,nil)$
    put(p,'fct_nli_nca,nil)$
    put(p,'fct_nli_nli,nil)$
    put(p,'fct_nli_nus,nil)$
   >>
  >>
 >>$
end$

symbolic procedure addfunction(ft)$
begin scalar f,ff,l,ps,ok$
 ps:=promptstring!*$
 promptstring!*:=""$
 ff:=mkid(fname_,nfct_)$
 repeat <<
  ok:=t;
  terpri()$
  write "What is the name of the new function?"$
  terpri()$
  write "If the name is ",fname_,"+digits then use ",ff,". Terminate with <ENTER>: "$
  f:=termread()$
  if f=ff then nfct_:=add1 nfct_
          else if member(f,ft) then <<
   terpri()$
   write"Choose another name. ",f," is already in use."$
   ok:=nil
  >>$
 >> until ok;
 depl!*:=delete(assoc(f,depl!*),depl!*)$
 terpri()$
 write "Give a list of variables ",f," depends on, for example x,y,z;  "$
 terpri()$
 write "For constant ",f," input a `;'  "$
 l:=termxread()$
 if (pairp l) and (car l='!*comma!*) then l:=cdr l;
 if pairp l then depl!*:=cons(cons(f,l),depl!*) else
 if l then depl!*:=cons(list(f,l),depl!*)$
 ft:=fctinsert(f,ft)$
 ftem_:=fctinsert(f,ftem_)$
 promptstring!*:=ps$
 return (ft . f)
end$

symbolic procedure newinequ(pdes)$
begin scalar ps,ex$
 ps:=promptstring!*$
 promptstring!*:=""$
 write "Input of a value for the new non-vanishing expression."$
 terpri()$
 write "You can use names of pds, e.g. 3*e_12 - df(e_13,x) + 8; "$
 terpri()$
 write "Terminate the expression with ; or $ : "$
 terpri()$
 ex:=termxread()$
 for each a in pdes do ex:=subst(get(a,'val),a,ex)$
 terpri()$
 promptstring!*:=ps$
 addineq(pdes,ex)$
end$

symbolic procedure replacepde(pdes,ftem,vl)$
begin scalar p,q,ex,ps,h,newfct,again$
 ps:=promptstring!*$ promptstring!*:=""$
 repeat <<
  terpri()$
  write "Is there a"$
  if again then write" further"$
  write" new function in the changed/new PDE that"$
  terpri()$
  write "is to be calculated (y/n)? "$
  p:=termread()$
  if (p='y) or (p='Y) then <<
   h:=addfunction(ftem)$
   ftem:=car h$
   if cdr h then newfct:=cons(cdr h,newfct)
  >>;
  again:=t
 >> until (p='n) or (p='N)$
 terpri()$
 write "If you want to replace a pde then type its name, e.g. e_23 <ENTER>."$
 terpri()$
 write "If you want to add a pde then type `new_pde' <ENTER>. "$
 p:=termread()$
 if (p='NEW_PDE) or member(p,pdes) then
  <<terpri()$write "Input of a value for "$
  if p='new_pde then write "the new pde."
                else write p,"."$
  terpri()$
  write "You can use names of other pds, e.g. 3*e_12 - df(e_13,x); "$
  terpri()$
  write "Terminate the expression with ; or $ : "$
  terpri()$
  ex:=termxread()$
  for each a in pdes do ex:=subst(get(a,'val),a,ex)$
  terpri()$
  write "Do you want the equation to be"$terpri()$
%  write "- left completely unchanged"$
%  terpri()$
%  write "  (e.g. to keep the structure of a product to "$
%  terpri()$
%  write "   investigate subcases)                        (1)"$
%  terpri()$
  write "- simplified (e.g. e**log(x) -> x) without"$
  terpri()$
  write "  dropping non-zero factors and denominators"$
  terpri()$
  write "  (e.g. to introduce integrating factors)       (1)"$
  terpri()$
  write "- simplified completely                         (2) "$
  h:=termread()$
%  if h=2 then ex:=reval ex$
%  if h<3 then h:=nil
%         else h:=t$
  if h=1 then <<ex:=reval ex$h:=nil>>
         else h:=t$
  if p neq 'NEW_PDE then
  pdes:=drop_pde(p,pdes,{'QUOTIENT,{'TIMES,p,ex},get(p,'val)})$
  q:=mkeq(ex,ftem,vl,allflags_,h,list(0),nil,pdes)$
  % A new equation with a new function appearing linear and only
  % algebraically can only have the purpose of a transformation
  % in which case the new equation should not be solved for the
  % new function as this would just mean dropping the new equation:
  if (p='NEW_PDE) and newfct then
  put(q,'not_to_eval,newfct)$
  terpri()$write q$
  if p='NEW_PDE then write " is added"
                else write " replaces ",p$
  pdes:=eqinsert(q,pdes)>>
 else <<terpri()$
        write "A pde ",p," does not exist! (Back to previous menu)">>$
 promptstring!*:=ps$
 return list(pdes,ftem)
end$

symbolic procedure select_from_list(liste,n)$
begin scalar ps,s$
 ps:=promptstring!*$
 promptstring!*:=""$
 terpri()$
 if n then write"Pick ",n," from this list:"
      else write"Pick from this list"$
 terpri()$
 listprint(liste)$write";"$terpri()$
 if null n then <<
  write"a sublist and input it in the same form. Enter ; to choose all."$
  terpri()$
 >>$
 s:=termlistread()$
 if n and n neq length s then <<
  write "Wrong number picked."$terpri()$
  s:=nil;
 >>                      else
 if null s then s:=liste else
 if not_included(s,liste) then <<
  write setdiff(s,liste)," is not allowed."$terpri()$
  s:=nil;
 >>;
 promptstring!*:=ps$
 return s
end$

symbolic procedure selectpdes(pdes,n)$
% interactive selection of n pdes
% n may be an integer or nil. If nil then the
% number of pdes is free.
if pdes then
begin scalar l,s,ps,m$
 ps:=promptstring!*$
 promptstring!*:=""$
 terpri()$
 if null n then <<
  write "How many equations do you want to select? "$terpri()$
  write "(number <ENTER>) : "$terpri()$
  n:=termread()$
 >>$
 write "Please select ",n," equation"$
 if n>1 then write "s"$write " from: "$
 write pdes$ % fctprint pdes$
 terpri()$
 m:=0$
 s:=t$
 while (m<n) and s do
  <<m:=add1 m$
  if n>1 then write m,". "$
  write "pde: "$
  s:=termread()$
  while not member(s,pdes) do
   <<write "Error!!! Please select a pde from: "$
   write pdes$ % fctprint pdes$
   terpri()$if n>1 then write m,". "$
   write "pde: "$
   s:=termread()>>$
  if s then
   <<pdes:=delete(s,pdes)$
   l:=cons(s,l)>> >>$
 promptstring!*:=ps$
 return reverse l$
end$

symbolic operator nodepnd$
symbolic procedure nodepnd(fl)$
for each f in cdr fl do
depl!*:=delete(assoc(reval f,depl!*),depl!*)$

symbolic procedure err_catch_readin$
begin scalar h,mode_bak,echo_bak$
 mode_bak:=!*mode; % as the _stop_ file has to start with 'lisp;'
 echo_bak:=!*echo; semic!*:='!$;
 if filep "_stop_" then <<
   h:= errorset({'in,mkquote {"_stop_"}},nil,nil)
       where !*protfg=t >>
 else h := '(nil);
 !*echo:=echo_bak; semic!*:='!; ;
 erfg!*:=nil; !*mode:=mode_bak$
 return not errorp h
end$

symbolic procedure err_catch_solve(eqs,fl)$
% fl='(LIST x y z);    eqs='(LIST expr1 expr2 .. )
begin scalar h$
 h:=errorset({'solveeval,mkquote{eqs, fl}},nil,nil)
 where !*protfg=t;
 erfg!*:=nil;
 return if errorp h then nil
                    else cdar h    % cdr for deleting 'LIST
end$

symbolic operator err_catch_sub$
symbolic procedure err_catch_sub(h2,h6,h3)$
% do sub(h2=h6,h3) with error catching
begin scalar h4,h5;
 h4 := list('EQUAL,h2,h6);
 h5:=errorset({'subeval,mkquote{reval h4,
                                reval h3 }},nil,nil)
     where !*protfg=t;
 erfg!*:=nil;
 return if errorp h5 then nil
                     else car h5
end$

symbolic operator err_catch_int$
symbolic procedure err_catch_int(h2,h3)$
% do int(h2,h3) with error catching
begin scalar h5;
 h5:=errorset({'simpint,mkquote{reval h2,
                                reval h3 }},nil,nil)
     where !*protfg=t;
 erfg!*:=nil;
 return if errorp h5 then nil
                     else
 if not freeof(car h5,'INT) then nil
                            else prepsq car h5
end$

symbolic procedure beforegcsystemhook()$
my_gc_counter:=add1 my_gc_counter$

symbolic procedure aftergcsystemhook()$
if my_gc_counter > max_gc_counter then <<
 if print_ % and print_more (User must know that not all is computed.)
  then <<
  write "Stop of a subroutine."$terpri()$
  write "Number of garbage collections exceeds ",backup_,".";
  terpri()$
 >>$
 rederr "Heidadeife "
>>$

symbolic operator err_catch_fac$
symbolic procedure err_catch_fac(a)$
begin scalar h,bak,kernlist!*bak,kord!*bak,bakup_bak;
 bak:=max_gc_counter;
 max_gc_counter:=my_gc_counter+max_gc_fac;
 kernlist!*bak:=kernlist!*$
 kord!*bak:=kord!*$
 bakup_bak:=backup_;backup_:='max_gc_fac$
 h:=errorset({'reval,list('FACTORIZE,mkquote a)},nil,nil)
    where !*protfg=t;
 kernlist!*:=kernlist!*bak$
 kord!*:=kord!*bak;
 erfg!*:=nil;
 max_gc_counter:=bak;
 backup_:=bakup_bak;
 return if errorp h then {'LIST,a}
                    else car h
end$

symbolic operator err_catch_gcd$
symbolic procedure err_catch_gcd(a,b)$
begin scalar h,bak,kernlist!*bak,kord!*bak,bakup_bak;
 bak:=max_gc_counter;
 max_gc_counter:=my_gc_counter+max_gc_fac;
 kernlist!*bak:=kernlist!*$
 kord!*bak:=kord!*$
 bakup_bak:=backup_;backup_:='max_gc_fac$
 h:=errorset({'reval, mkquote list('GCD,mkquote a,mkquote b)},nil,nil)
    where !*protfg=t;
 kernlist!*:=kernlist!*bak$
 kord!*:=kord!*bak;
 erfg!*:=nil;
 max_gc_counter:=bak;
 backup_:=bakup_bak;
 return if errorp h then 1
                    else car h
end$

%symbolic operator err_catch_fac$
%symbolic procedure err_catch_fac(a)$
%begin scalar h,bak,bakup_bak;
% bak:=max_gc_counter;
% max_gc_counter:=my_gc_counter+max_gc_fac;
% bakup_bak:=backup_;backup_:='max_gc_fac$
% h:=errorset({'reval,list('FACTORIZE,mkquote a)},nil,nil)
%    where !*protfg=t;
% erfg!*:=nil;
% max_gc_counter:=bak;
% backup_:=bakup_bak;
% return if errorp h then {'LIST,a}
%                    else car h
%end$

symbolic procedure factored_form(a)$
% a is expected to be in prefix form
begin scalar b;
 if (pairp a) and (car a = 'PLUS) then <<
  b:=err_catch_fac a$
  if b and (length b > 2) then a:=cons('TIMES,cdr b)
 >>;
 return a
end$

symbolic lispeval '(putd 'countids 'expr
          '(lambda nil (prog (nn) (setq nn 0)
                (mapobl (function (lambda (x) (setq nn (plus2 nn 1)))))
                                  (return nn))))$

symbolic operator low_mem$
% if garbage collection recovers only 500000 cells then backtrace
% to be used only on workstations, not PCs i.e. under LINUX, Windows

symbolic procedure newreclaim()$
   <<oldreclaim();
     if (known!-free!-space() < 500000 ) then backtrace()
   >>$

symbolic procedure low_mem()$
if not( getd 'oldreclaim) then <<
    copyd('oldreclaim,'!%reclaim);
    copyd('!%reclaim,'newreclaim);
>>$

symbolic operator polyansatz$
symbolic procedure polyansatz(ev,iv,fn,degr,homo)$
% ev, iv are algebraic mode lists
% generates a polynomial in the variables ev of degree degr
% with functions of the variables iv
% if homo then a homogeneous polynomial
begin scalar a,fi,el1,el2,f,fl,p,pr;
 a:=reval list('EXPT,cons('PLUS,if homo then cdr ev
                                        else cons(1,cdr ev)),degr)$
 a:=reverse cdr a$
 fi:=0$
 iv:=cdr iv$
 for each el1 in a collect <<
  if (not pairp el1) or
     (car el1 neq 'TIMES) then el1:=list el1
                          else el1:=cdr el1;
  f:=newfct(fn,iv,fi);
  fi:=add1 fi;
  fl:=cons(f,fl)$
  pr:=list f$
  for each el2 in el1 do
  if not fixp el2 then pr:=cons(el2,pr);
  if length pr>1 then pr:=cons('TIMES,pr)
                 else pr:=car pr;
  p:=cons(pr,p)
 >>$
 p:=reval cons('PLUS,p)$
 return list('LIST,p,cons('LIST,fl))
end$

symbolic operator polyans$
symbolic procedure polyans(ordr,dgr,x,y,d_y,fn)$
% generates a polynom
% for i:=0:dgr sum fn"i"(x,y,d_y(1),..,d_y(ordr-1))*d_y(ordr)**i
% with fn as the function names and d_y as names or derivatives of y
% w.r.t. x
begin scalar ll,fl,a,i,f$
    i:=sub1 ordr$
    while i>0 do
          <<ll:=cons(list(d_y,i),ll)$
          i:=sub1 i>>$
    ll:=cons(y,ll)$
    ll:=reverse cons(x,ll)$
    fl:=nil$
    i:=0$
    while i<=dgr do
    <<f:=newfct(fn,ll,i)$
      fl:=(f . fl)$
      a:=list('PLUS,list('TIMES,f,list('EXPT,list(d_y,ordr),i)),a)$
      i:=add1 i>>$
    return list('list,reval a,cons('list,fl))
end$ % of polyans

symbolic operator sepans$
symbolic procedure sepans(kind,v1,v2,fn)$
% Generates a separation ansatz
% v1,v2 = lists of variables, fn = new function name + index added
% The first variable of v1 occurs only in one sort of the two sorts of
% functions and the remaining variables of v1 in the other sort of
% functios.
% The variables of v2 occur in all functions.
% Returned is a sum of products of each one function of both sorts.
% form: fn1(v11;v21,v22,v23,..)*fn2(v12,..,v1n;v21,v22,v23,..)+...
% the higher "kind", the more general and difficult the ansatz is
% kind = 0 is the full case
begin scalar n,vl1,vl2,h1,h2,h3,h4,fl$
  if cdr v1 = nil then <<vl1:=cdr v2$vl2:=cdr v2>>
                  else <<vl1:=cons(cadr v1,cdr v2)$
                         vl2:=append(cddr v1,cdr v2)>>$
  return
  if kind = 0 then <<vl1:=append(cdr v1,cdr v2)$
                     h1:=newfct(fn,vl1,'_)$
                     list('LIST,h1,list('LIST,h1))>>
  else
  if kind = 1 then <<h1:=newfct(fn,vl1,1)$
                     list('LIST,h1,list('LIST,h1))>>
  else
  if kind = 2 then <<h1:=newfct(fn,vl2,1)$
                     list('LIST,h1,list('LIST,h1))>>
  else
  if kind = 3 then <<h1:=newfct(fn,vl1,1)$
                     h2:=newfct(fn,vl2,2)$
                     list('LIST,reval list('PLUS,h1,h2),
                          list('LIST,h1,h2))>>
  else
  if kind = 4 then <<h1:=newfct(fn,vl1,1)$
                     h2:=newfct(fn,vl2,2)$
                     list('LIST,reval list('TIMES,h1,h2),
                          list('LIST,h1,h2))>>
  else
  if kind = 5 then <<h1:=newfct(fn,vl1,1)$
                     h2:=newfct(fn,vl2,2)$
                     h3:=newfct(fn,vl1,3)$
                     list('LIST,reval list('PLUS,list('TIMES,h1,h2),h3),
                          list('LIST,h1,h2,h3))>>
  else
  if kind = 6 then <<h1:=newfct(fn,vl1,1)$
                     h2:=newfct(fn,vl2,2)$
                     h3:=newfct(fn,vl2,3)$
                     list('LIST,reval list('PLUS,list('TIMES,h1,h2),h3),
                          list('LIST,h1,h2,h3))>>
  else
  if kind = 7 then <<h1:=newfct(fn,vl1,1)$
                     h2:=newfct(fn,vl2,2)$
                     h3:=newfct(fn,vl1,3)$
                     h4:=newfct(fn,vl2,4)$
                     list('LIST,reval list('PLUS,
                          list('TIMES,h1,h2),h3,h4),
                          list('LIST,h1,h2,h3,h4))>>
  else
% ansatz of the form FN = FN1(v11,v2) + FN2(v12,v2) + ... + FNi(v1i,v2)
  if kind = 8 then <<n:=1$ vl1:=cdr v1$ vl2:=cdr v2$
                    fl:=()$
                     while vl1 neq () do <<
                       h1:=newfct(fn,cons(car vl1,vl2),n)$
                       vl1:=cdr vl1$
                       fl:=cons(h1, fl)$
                       n:=n+1
                     >>$
                     list('LIST, cons('PLUS,fl), cons('LIST,fl))>>


  else
                   <<h1:=newfct(fn,vl1,1)$
                     h2:=newfct(fn,vl2,2)$
                     h3:=newfct(fn,vl1,3)$
                     h4:=newfct(fn,vl2,4)$
                     list('LIST,reval list('PLUS,list('TIMES,h1,h2),
                                                 list('TIMES,h3,h4)),
                          list('LIST,h1,h2,h3,h4))>>
end$ % of sepans

%
% Orderings support!
%
% change_derivs_ordering(pdes,fl,vl) changes the ordering of the
% list of derivatives depending on the current ordering (this
% is detected "automatically" by sort_derivs using the lex_df flag to
% toggle between total-degree and lexicographic.
%
symbolic procedure change_derivs_ordering(pdes,fl,vl)$
begin scalar p, dl;
   for each p in pdes do <<
      if tr_orderings then <<
         terpri()$
         write "Old: ", get(p,'derivs)$
      >>$
      dl := sort_derivs(get(p,'derivs),fl,vl)$
      if tr_orderings then <<
         terpri()$
         write "New: ", dl$
      >>$
      put(p,'derivs,dl)$
      put(p,'dec_with,nil)$    % only if orderings are not
      % investigated in parallel (-->ord)
      put(p,'dec_with_rl,nil)  % only if orderings are not ..
   >>$
   return pdes
end$

symbolic procedure sort_according_to(r,s)$
% all elements in r that are in s are sorted according to their order in s
begin scalar ss,h;
 for each ss in s do
 if member(ss,r) then h:=cons(ss,h);
 return reverse h
end$

symbolic procedure change_fcts_ordering(newli,pdes,vl)$
begin scalar s$
 ftem_ := newli$
 for each s in pdes do put(s,'fcts,sort_according_to(get(s,'fcts),ftem_))$
 pdes := change_derivs_ordering(pdes,ftem_,vl)$
 if tr_orderings then <<
  terpri()$
  write "New functions list: ", ftem_$
 >>
end$

symbolic procedure check_history(pdes)$
begin scalar p,q,h$
 for each p in pdes do <<
  h:=get(p,'histry_);
  for each q in pdes do
  h:=subst(get(q,'val),q,h)$
  if algebraic((lisp(get(p,'val)) - h) neq 0) then <<
   write"The history value of ",p," is not correct!"$
   terpri()
  >>
 >>
end$

symbolic procedure check_globals$
% to check validity of global variables at start of CRACK
begin scalar flag, var$

 % The integer variables
 foreach var in global_list_integer do
 if not fixp eval(var) then <<
  terpri()$
  write var, " needs to be an integer: ", eval(var)," is invalid"$
  flag := var
 >>$

 % Now for integer variables allowed to be nil
 foreach var in global_list_ninteger do
 if not fixp eval(var) and eval(var) neq nil then <<
  terpri()$
  write var, " needs to be an integer or nil: ",
  eval(var)," is invalid"$
  flag := var
 >>$

 % Finally variables containing any number
 foreach var in global_list_number do
 if not numberp eval(var) then <<
  terpri()$
  write var, " needs to be a number: ", eval(var)," is invalid"$
  flag := var
 >>$

 return flag
end$

symbolic procedure search_li(l,care)$
% Find the cadr of all sublists which have 'care' as car (no nesting)
if pairp l then
if car l = care then {cadr l}
                else begin
 scalar a,b,resul;
 for each a in l do
 if b:=search_li(a,care) then resul:=union(b,resul);
 return resul
end$

symbolic procedure search_li2(l,care)$
% Find all sublists which have 'care' as car (no nesting)
if pairp l then
if car l = care then list l
                else begin
 scalar a,b,resul;
 for each a in l do
 if b:=search_li2(a,care) then resul:=union(b,resul);
 return resul
end$

symbolic operator backup_reduce_flags$
symbolic procedure backup_reduce_flags$
% !*nopowers   = t  to have output of FACTORIZE like in Reduce 3.6
% !*allowdfint = t  moved here from crintfix, to enable simplification
%                   of derivatives of integrals
begin
 !*dfprint_bak   := cons(!*dfprint,!*dfprint_bak)$
 !*exp_bak       := cons(!*exp,!*exp_bak)$
 !*ezgcd_bak     := cons(!*ezgcd,!*ezgcd_bak)$
 !*fullroots_bak := cons(!*fullroots,!*fullroots_bak)$
 !*gcd_bak       := cons(!*gcd,!*gcd_bak)$
 !*mcd_bak       := cons(!*mcd,!*mcd_bak)$
 !*ratarg_bak    := cons(!*ratarg,!*ratarg_bak)$
 !*rational_bak  := cons(!*rational,!*rational_bak)$

 if null !*dfprint   then algebraic(on  dfprint)$
 if null !*exp       then algebraic(on  exp)$
 if null !*ezgcd     then algebraic(on  ezgcd)$
 if null !*fullroots then algebraic(on  fullroots)$
 if      !*gcd       then algebraic(off gcd)$
 if null !*mcd       then algebraic(on  mcd)$
 if null !*ratarg    then algebraic(on  ratarg)$
 if null !*rational  then algebraic(on  rational)$

 !#if (neq version!* "REDUCE 3.6")
  !*nopowers_bak   := cons(!*nopowers,!*nopowers_bak)$
  !*allowdfint_bak := cons(!*allowdfint,!*allowdfint_bak)$
  if null !*nopowers   then algebraic(on nopowers)$
  if null !*allowdfint then algebraic(on allowdfint)$
 !#endif

end$

symbolic operator recover_reduce_flags$
symbolic procedure recover_reduce_flags$
begin

 if !*dfprint neq car !*dfprint_bak then
 if !*dfprint then algebraic(off dfprint) else algebraic(on dfprint)$
 !*dfprint_bak:= cdr !*dfprint_bak$

 if !*exp neq car !*exp_bak then
 if !*exp then algebraic(off exp) else algebraic(on exp)$
 !*exp_bak:= cdr !*exp_bak$

 if !*ezgcd neq car !*ezgcd_bak then
 if !*ezgcd then algebraic(off ezgcd) else algebraic(on ezgcd)$
 !*ezgcd_bak:= cdr !*ezgcd_bak$

 if !*fullroots neq car !*fullroots_bak then
 if !*fullroots then algebraic(off fullroots) else algebraic(on fullroots)$
 !*fullroots_bak:= cdr !*fullroots_bak$

 if !*gcd neq car !*gcd_bak then
 if !*gcd then algebraic(off gcd) else algebraic(on gcd)$
 !*gcd_bak:= cdr !*gcd_bak$

 if !*mcd neq car !*mcd_bak then
 if !*mcd then algebraic(off mcd) else algebraic(on mcd)$
 !*mcd_bak:= cdr !*mcd_bak$

 if !*ratarg neq car !*ratarg_bak then
 if !*ratarg then algebraic(off ratarg) else algebraic(on ratarg)$
 !*ratarg_bak:= cdr !*ratarg_bak$

 if !*rational neq car !*rational_bak then
 if !*rational then algebraic(off rational) else algebraic(on rational)$
 !*rational_bak:= cdr !*rational_bak$

 !#if (neq version!* "REDUCE 3.6")
  if !*nopowers neq car !*nopowers_bak then
  if !*nopowers then algebraic(off nopowers) else algebraic(on nopowers)$
  !*nopowers_bak:= cdr !*nopowers_bak$
  if !*allowdfint neq car !*allowdfint_bak then
  if !*allowdfint then algebraic(off allowdfint) else algebraic(on allowdfint)$
  !*allowdfint_bak:= cdr !*allowdfint_bak$
 !#endif
end$

algebraic procedure maklist(ex)$
% making a list out of an expression if not already
if lisp(atom algebraic ex) then {ex} else
if lisp(car algebraic ex neq 'LIST) then ex:={ex}
                                    else ex$

symbolic procedure add_to_last_steps(h)$
if length last_steps < 20 then last_steps:=cons(h,last_steps) else
last_steps:=cons(h,reverse cdr reverse last_steps)$

symbolic procedure same_steps(a,b)$
if (car a = car b            ) and
   ((a = b) or
    ((car a neq 'subst) and
     (car a neq   27  ) and
     (car a neq   11  )     )) then t
                               else nil$

symbolic procedure in_cycle(h)$
begin scalar cpls1,cpls2,n,cycle;
 cpls1:=last_steps$
 if car h='subst then <<
  n:=0$
  while cpls1 do <<
   if same_steps(h,car cpls1) then n:=add1 n;
   cpls1:=cdr cpls1
  >>$
  cycle:=
  if n>2 then << % the subst. had been done already >=3 times
   write"A partial substitution has been repeated too often."$ terpri()$
   write"It will now be made rigorously."$ terpri()$
   t
  >>     else nil
  % add_to_last_steps(h) is done outside for substitutions as it is not
  % clear at this stage whether the substitution will be performed
 >>                         else <<
  n:=1$
  while cpls1 and (not same_steps(h,car cpls1)) do
  <<n:=add1 n;cpls1:=cdr cpls1>>$
  if null cpls1 or
    ((reval {'PLUS,n,n})>length last_steps) then cycle:=nil
                                            else <<
   cpls1:=cdr cpls1;
   cpls2:=last_steps$
   while (n>0) and same_steps(car cpls2,car cpls1) do
   <<cpls1:=cdr cpls1;cpls2:=cdr cpls2;n:=sub1 n>>$
   if (n=0) and print_ then <<
     write if car h = 11 then "An algebraic length reduction" else
           if car h = 27 then "A length reducing simplification" else
                              "A step",
           " was prevented"$    terpri()$
     write"to avoid a cycle."$  terpri()$
   >>$
   cycle:=if n>0 then nil else t
  >>;
  if null cycle then add_to_last_steps(h)$
 >>;
 return cycle
end$

endmodule$

%********************************************************************
module solution_handling$
%********************************************************************
%  Routines for storing, retrieving, merging and displaying solutions
%  Author: Thomas Wolf Dec 2001

symbolic procedure save_solution(eqns,assigns,freef,ineq,file_name)$
% input data are algebraic mode
% eqns    .. list of remaining unsolved equations
% assigns .. list of computed assignments of the form `function = expression'
% freef   .. list of list of functiones either free or in eqns
% ineq    .. list of inequalities
begin scalar s,levelcp,h,p,conti$
  if file_name then s:=file_name
               else <<
   s:=session_;
   levelcp:=reverse level_;
   while levelcp do <<setq(s,bldmsg("%w%d.",s,car levelcp));
                      levelcp:=cdr levelcp>>;
   s:=explode s;
   s:=compress cons(car s,cons('s,cons('o,cdddr s)))$
  >>$

  sol_list:=union(list s,sol_list)$

  out s;
  write"off echo$ "$
  write"backup_:='("$terpri()$

  for each h in freef do
  if p:=assoc(h,depl!*) then conti:=cons(p,conti);

  % The first sub-list is a  list of dependencies, like ((f x y) (g x))
  write"% A list of dependencies, like ((f x y) (g x))"$terpri()$
  print conti$write" "$terpri()$

  % The next sublist is a list of unsolved equations
  write"% A list of unsolved equations"$terpri()$
  print eqns$write" "$terpri()$

  % The next sublist is a list of assignments
  write"% A list of assignments"$terpri()$
  print assigns$write" "$terpri()$

  % The next sublist is a list of free or unresolved functions
  write"% A list of free or unresolved functions"$terpri()$
  print freef$write" "$terpri()$

  % The next sublist is a list of non-vanishing expressions
  write"% A list of non-vanishing expressions"$terpri()$
  print ineq$terpri()$

  write")$"$

  % shorter but less clear:  print list(conti,eqns,freef,ineq)$

  write "end$"$terpri()$
  shut s;

  return s
end$

symbolic procedure print_indexed_list(li)$
begin scalar a,h$
 terpri()$
 h:=0$
 for each a in li do <<
  h:=add1 h;
  write"[",h,"]";terpri()$
  mathprint a
 >>
end$

symbolic procedure merge_two(s1,sol1,s2,sol2,absorb)$
% Is sol1 a special case of sol2 ?
% If yes, return the new generalized solution sol2 with one less inequality.
% If absorb then modify s2 and sol2 if s1 can be absorbed

begin scalar eli_2,singular_eli,regular_eli,a,b,cond2,sb,remain_sb,
             singular_sb,regular_sb,c2,remain_c2,remain_num_c2,h,
             try_to_sub,try_to_sub_cp,num_sb,num_sb_quo,singular_ex,
             singular_ex_cp,ineq2,ine,ineqnew,ineqdrop,tr_merge,
             extra_par_in_s1,gauge_of_s2,gauge_of_s2_cp,did_trafo,n,
             remain_c2_cp,dropped_assign_in_s2,new_assign_in_s2$

 % tr_merge:=t$
 if tr_merge then <<write"s1=",s1$terpri()$
                    write"s2=",s2$terpri()$
                    write"*** sol1 ***:"$print_indexed_list(caddr sol1)$
                    write"*** sol2 ***:"$print_indexed_list(caddr sol2)$
                    write"free param in sol1: ",cadddr sol1$terpri()$
                    write"free param in sol2: ",cadddr sol2$terpri()>>$
 % At first we list all lhs y_i in assignments y_i=... in sol2
 eli_2:=setdiff(for each a in caddr sol2 collect cadr a,cadddr sol2);

 % We use setdiff because of assignments, like a6=a6 in sol2
 % where a6 is a free parameter.

 % writing assignments of solution 2 as expressions to vanish
 cond2:=for each a in caddr sol2
        collect {'PLUS,cadr a,{'MINUS,caddr a}};

 % Do all substitutions a=... from sol1 for which
 % there is an assignment a=... in sol2 and
 % collecting the others as remain_sb

 cond2:=cons('LIST,cond2);
 sb:=caddr sol1; % all assignments of solution 1
 while sb do <<
  a:=car sb; sb:=cdr sb;
  if member(cadr a,eli_2) then <<
   eli_2:=delete(cadr a,eli_2)$
   cond2:=err_catch_sub(cadr a,caddr a,cond2)
  >>                      else remain_sb:=cons(a,remain_sb)
 >>$
 eli_2:=append(eli_2,cadddr sol2)$
 % eli_2 is now the list of new sol2 parameters

 % At this stage any sol2 conditions either become singular or zero.
 % The singular ones are collected in remain_c2.

 % The same again, now taking only numerators
 remain_c2:=cond2;

 cond2:=cdr cond2;
 c2:=nil$
 h:=0$
 while cond2 and (null c2 or zerop c2) do <<
  c2:=car cond2;
  h:=add1 h;
  if tr_merge then <<write"[",h,"]"$terpri()$mathprint c2>>$

  % Is the numerator of c2 fulfilled by assignments of solution 1?
  sb:=remain_sb;               % all remaining assignments of solution 1
  while sb and c2 and not zerop c2 do <<
   a:=car sb; sb:=cdr sb;
   c2:=algebraic(num(lisp(c2)));
   if tr_merge then b:=c2;
   c2:=err_catch_sub(cadr a,caddr a,c2);
   if tr_merge and (b neq c2) then <<
    write"Sub: ";mathprint a$
    if c2 then <<write"c2="$mathprint c2>>
          else <<write"singular result"$terpri()>>
   >>
  >>$
  if null c2 then remain_num_c2:=cons(car cond2,remain_num_c2);
  cond2:=cdr cond2
 >>$

 if c2 and not zerop c2 then return nil; % sol1 is not special case of sol2
 if remain_num_c2 then << % can only occur if there were singular subst.
  write"Even substitutions in the numerator is giving "$terpri()$
  write"singularities like for log(0)."$                terpri()$
  return nil
 >>$

 write"Substitutions in numerators give all zero"$terpri()$

 % At this stage in the program either all is satisfied (remain_c2 = nil)
 % or only singular solution 2 assignments remain (remain_c2 <> nil)
 % but which vanish if numerators are taken.

 % We now want to find a different order of substitutions, especially
 % substituting for the free parameter functions of solution 2
 % based on remain_sb to be done in remain_c2.

 % At first we sort all solution 1 assignments into regular_sb and singular_sb.
 % remain_c2 is not changed in this
 cond2:=remain_c2;
 sb:=remain_sb;               % all remaining assignments of solution 1
 while sb do <<
  a:=car sb; sb:=cdr sb;
  h:=err_catch_sub(cadr a,caddr a,cond2);
  if null h then singular_sb:=cons(a,singular_sb)
            else regular_sb:=cons(a,regular_sb)
 >>$
 if tr_merge then <<terpri()$
                    write"regular_sb: "$mathprint cons('LIST,regular_sb)>>$
 if tr_merge then <<write"singular_sb: "$mathprint cons('LIST,singular_sb)>>$

 if singular_sb then <<
  write"Substitutions lead to singularities."$terpri()$
  write"Solution ",s2," has to be transformed."$terpri()
 >>$

 % We now make a list of vanishing expressions based on singular_sb
 % which when replaced by 0 in remain_c2 give singularities
 singular_ex:=for each a in singular_sb
              collect reval {'PLUS,cadr a,{'MINUS,caddr a}};
 if tr_merge then <<
  write"The following are expressions which vanish due to sol1 and"$
  terpri()$
  write"which lead to singularities when used for substitutions in sol2"$
  terpri()$
  mathprint cons('LIST,singular_ex)
 >>$

 if tr_merge then <<
  write"The following are all free parameters in sol2 for which there are"$
  terpri()$
  write"substitutions in sol1"$ terpri()$
 >>$
 singular_eli:=for each a in singular_sb collect cadr a;
 regular_eli:=for each a in regular_sb collect cadr a;
 if tr_merge then <<terpri()$
                    write"singular_eli: "$mathprint cons('LIST,singular_eli)>>;
 if tr_merge then <<write"regular_eli: "$mathprint cons('LIST,regular_eli)>>;

 % Before continuing we want to check whether the supposed to be more special
 % solution sol1 has free parameters which are not free parameters in the more
 % general solution sol2. That can cause problems, i.e. division through 0
 % and non-includedness when in fact sol1 is included in sol2.

 extra_par_in_s1:=setdiff(cadddr sol1,cadddr sol2);
 if tr_merge then <<write"Param in sol1 and not in sol2: ",extra_par_in_s1;
                    terpri()>>$

 for each a in extra_par_in_s1 do <<
  h:=caddr sol2$
  while h and cadar h neq a do h:=cdr h;
  if null h then write"ERROR, there must be an assignment of a in sol2!"
            else <<
   if tr_merge then <<
    write"Assignment in ",s2," of a variable that is a free parameter in ",
         s1," :"$
    terpri()$
    mathprint car h$
   >>$
   dropped_assign_in_s2:=cons(car h,dropped_assign_in_s2);
   gauge_of_s2:=cons(algebraic(num(lisp({'PLUS,cadr car h,
                                         {'MINUS,caddr car h}}))),
                     gauge_of_s2)
  >>
 >>$

 gauge_of_s2:=cons('LIST,gauge_of_s2);

 if tr_merge then <<write"gauge_of_s2="$mathprint gauge_of_s2>>$

 % We should not do all regular substitutions in gauge_of_s2 (tried that)
 % because some of them may set variables to zero which limits the
 % possibilities of doing transformations of remain_c2

 % We now search for a substitution based on one of the equations
 % gauge_of_s2. The substitution is to be performed on remain_c2.

 % One sometimes has to solve for regular_eli as singular_eli
 % might appear only non-linearly.
 % try_to_sub:=append(regular_eli,singular_eli);
 try_to_sub:=append(singular_eli,regular_eli);
 n:=1;
 repeat <<
  did_trafo:=nil;
  gauge_of_s2_cp:=cdr gauge_of_s2;
  while gauge_of_s2_cp do <<
   sb:=reval car gauge_of_s2_cp$
   gauge_of_s2_cp:=cdr gauge_of_s2_cp$
   if not zerop sb then <<
    try_to_sub_cp:=try_to_sub;
    if tr_merge then <<write"next relation to be used: 0="$mathprint sb$
                       write"try_to_sub=",try_to_sub$terpri()>>$
    h:=err_catch_fac(sb);
    if h then <<
     sb:=nil;
     h:=cdr h;
     while h do <<
      if pairp cadar h then sb:=cons(cadar h,sb);
      h:=cdr h;
     >>
    >>$

    % From the next condition 0=sb we drop all factors which are
    % single variables which set to zero would be a limitation
    if tr_merge then <<write"After dropping single variable factors ",
                             length sb," factor(s) remain"$terpri()>>$
    sb:=reval cons('TIMES,cons(1,sb));
    if tr_merge then <<write"New relation used for substitution: sb="$
                       mathprint sb$terpri()>>$

    while try_to_sub_cp do <<
     a:=car try_to_sub_cp; try_to_sub_cp:=cdr try_to_sub_cp;
     if tr_merge then <<write"try to sub next: ",a$terpri()>>$
     if not freeof(sb,a) and lin_check(sb,{a}) then <<
      num_sb:=reval {'DIFFERENCE, sb,{'TIMES,a,coeffn(sb,a,1)}};
      if tr_merge then <<write"num_sb="$mathprint num_sb>>$
      singular_ex_cp:=singular_ex;
      while singular_ex_cp do <<
       if tr_merge then <<write"car singular_ex_cp=",car singular_ex_cp$
                          terpri()>>$
       % Search for an expression causing a singular substitution
       % which is a factor of the substituted expression for a
       num_sb_quo:=reval {'QUOTIENT,num_sb,car singular_ex_cp};
       if tr_merge then <<write"num_sb_quo="$mathprint num_sb_quo>>$
       % if (not pairp num_sb_quo) or
       %    (car num_sb_quo neq 'QUOTIENT) then <<
       if t then <<
        eli_2:=delete(a,eli_2);
        % i.e. num_sb is a multiple of one of members of singular_ex, HURRAY!
        % Do the substitution in remain_c2
        b:=cadr solveeval list(sb,a)$
        h:=err_catch_sub(cadr b,caddr b,remain_c2);
        if tr_merge and null h then <<
         write"Trafo "$mathprint b$write" was singular."$ terpri()
        >>$
        if h then <<
         gauge_of_s2:=err_catch_sub(cadr b,caddr b,gauge_of_s2);
         gauge_of_s2:=cons('LIST, for each gauge_of_s2_cp in cdr gauge_of_s2
                           collect algebraic(num(lisp(gauge_of_s2_cp))));
         gauge_of_s2_cp:=nil$
         new_assign_in_s2:=cons(b,new_assign_in_s2);
         did_trafo:=t$
         write"In order to avoid a singularity when doing substitutions"$
         terpri()$
         write"the supposed to be more general solution was transformed using:"$
         terpri()$
         mathprint b$
         if tr_merge then <<write"The new gauge_of_s2: "$
                            mathprint gauge_of_s2>>$

         remain_c2:=h;
         h:=append(regular_sb,singular_sb);
         while h and a neq cadar h do h:=cdr h;
         if h then remain_c2:=append(remain_c2,list {'DIFFERENCE,caddar h,caddr b});
         if tr_merge then <<write"remain_c2="$print_indexed_list(cdr remain_c2)>>$
         singular_ex_cp:=nil;
         try_to_sub:=delete(a,try_to_sub);
         try_to_sub_cp:=nil;
         n:=n+1
        >>   else singular_ex_cp:=cdr singular_ex_cp
       >>                                else singular_ex_cp:=cdr singular_ex_cp
      >>    % while singular_ex_cp
     >>    % if car try_to_sub_cp passes first test
    >>$   % while try_to_sub_cp
   >>    % if not zerop sb
  >>$   % while gauge_of_s2_cp
 >> until (did_trafo=nil)$

 if tr_merge then <<
  write"After completing the trafo the new list of parameters of"$
  terpri()$
  write"sol2 is: ",eli_2$terpri()$
  write"sol1 has free parameters: ",cadddr sol1$terpri()
 >>$

 if not_included(cadddr sol1,eli_2) then return <<
  write"Something seems wrong in merge_sol(): after the transformation of"$
  terpri()$
  write"sol2, all free parameters of sol1 should be free parameters of sol2."$
  terpri();
  nil
 >>                                else <<
  if tr_merge then <<
   write"All free parameters of sol1 are free parameters of sol2"$
   terpri()
  >>
 >>$


 % Now all in remain_c2 has to become zero by using first substitutions
 % from regular_sb and substituting parameters from sol2 such that
 % the substituted expression has one of the singular_ex as factor.

 % We seek global substitutions, i.e. substitutions based on sol1
 % which satisfy all sol2 conditions and not for each sol2 condition a
 % different set of sol1 based substitutions. Therefore substitutions
 % are done in the whole remain_c2.

 % try_to_sub are free parameters in sol2 that are contained in
 % regular_eli and which are therefore not in singular_eli and not free
 % parameters in sol1. They are to be substituted next because sol1 is
 % obviously singularity free, so we have to express sol2 in the same
 % free parameters, so we have to substitute for the free parameters fo
 % sol2 which are not free parameters of sol1. But we must not use the
 % same substitutions regular_sb which substitute for them as they lead
 % to singular substitutions afterwards.

% try_to_sub:=memberl(cadddr sol2,regular_eli);
%
% write"try_to_sub=",try_to_sub$terpri()$
%
% % We now search for a substitution in regular_sb which leads to a
% % substitution of a member of try_to_sub, say p, ...
% b:=regular_sb;
% for each sb in b do <<
%  sb_cp:=algebraic(num(lisp({'PLUS,cadr sb,{'MINUS,caddr sb}})));
%  try_to_sub_cp:=delete(cadr sb,try_to_sub); % ... but the substitution
%                                             % does not originally
%                                             % have the form p=...  .
%  while try_to_sub_cp do <<
%   a:=car try_to_sub_cp; try_to_sub_cp:=cdr try_to_sub_cp;
%   if not freeof(sb_cp,a) and lin_check(sb_cp,{a}) then <<
%    num_sb:={'DIFFERENCE, sb_cp,{'TIMES,a,coeffn(sb_cp,a,1)}};
%
%    singular_ex_cp:=singular_ex;
%    while singular_ex_cp do <<
%     % Search for an expression causing a singular substitution
%     % which is a factor of the substituted expression for a
%     num_sb_quo:=reval {'QUOTIENT,num_sb,car singular_ex_cp};
%     if (not pairp num_sb_quo) or
%        (car num_sb_quo neq 'QUOTIENT) then <<
%      % i.e. num_sb is a multiple of one of members of singular_ex, HURRAY!
%      % Do the substitution in remain_c2
%      h:=err_catch_sub(cadr sb,caddr sb,remain_c2);
%      if h then <<
%       write"In order to avoid a singularity when doing substitutions"$
%       terpri()$
%       write"the supposed to be more general solution was transformed:"$
%       terpri()$
%       mathprint sb$
%       remain_c2:=h;
%       singular_ex_cp:=nil;
%       regular_sb:=delete(sb,regular_sb);
%       try_to_sub:=delete(a,try_to_sub);
%       try_to_sub_cp:=nil;
%      >>   else singular_ex_cp:=cdr singular_ex_cp
%     >>                                else singular_ex_cp:=cdr singular_ex_cp
%    >>    % while singular_ex_cp
%   >>    % if car try_to_sub_cp passes first test
%  >>$   % while try_to_sub_cp
% >>$   % for each sb

 % Do the remaining regular_sb
 sb:=append(regular_sb,singular_sb); % all remaining assignments of solution 1
 while sb and remain_c2 do <<
  a:=car sb; sb:=cdr sb;
  remain_c2_cp:=remain_c2$
  remain_c2:=err_catch_sub(cadr a,caddr a,remain_c2);
  if tr_merge then
  if null remain_c2 then
  <<write"The following subst. was singular: "$mathprint a>>
                    else <<
   write"Remaining substitution: ";mathprint a$
   %write"remain_c2="$mathprint remain_c2
  >>
 >>$

 if null remain_c2 then remain_c2:=remain_c2_cp
                   else remain_c2_cp:=remain_c2;

 % Drop all zeros.
 remain_c2_cp:=cdr remain_c2_cp$
 while remain_c2_cp and zerop car remain_c2_cp do
 remain_c2_cp:=cdr remain_c2_cp;

 if remain_c2_cp then <<     % s1 is NOT a special case of s2

  remain_c2_cp:=remain_c2$
  if tr_merge then <<write"remain_c2="$
                     print_indexed_list(cdr remain_c2_cp)>>$

  % Is there a contradiction of the type that the equivalence of two
  % assignments, a8=A (from sol1), a8=B (from sol2) requires 0=A-B
  %  which got transformed into an expression C which is built only
  % from free parameters of sol1 and therefore should not vanish?

  h:=cadddr sol1; % all free parameters in sol1
  while h and <<
   if tr_merge then write"Substitution of ",car h," by: "$
   repeat <<     % find a random integer for the free parameter
    a:=1+random(10000);   % that gives a regular substitution
    if tr_merge then <<write a$terpri()>>$
    a:=err_catch_sub(car h,a,remain_c2_cp)
   >> until a;
   remain_c2_cp:=a;
   while a and ((not numberp car a) or (zerop car a)) do a:=cdr a;
   not a
  >> do h:=cdr h;

  if h then return <<
   write"In the following S1 stands for ",s1,"and S2 stands for ",s2," . ",
        "Solution S1 fulfills all conditions of solution S2 when conditions",
        "are made denominator free. But, after rewriting solution S2 so that",
        "all free parameters of solution S1 are also free parameters of S2",
        "then the new solution S2 now requires the vanishing of an expression",
        "in these free parameters which is not allowed by S1. Therefore S1",
        "is not a special case of S2."$
   nil
  >>$

  if tr_merge and remain_c2_cp then
  <<write"remain_c2_cp after subst = "$mathprint cons('LIST,remain_c2)>>$
  write"Solution ",s1," is not less restrictive than solution"$terpri()$
  write s2," and fulfills all conditions of solution ",s2," ."$terpri()$
  write"But it was not possible for the program to re-formulate solution "$
  terpri()$ write s2," to include both solutions in a single set of"$terpri()$
  write"assignments without vanishing denominators. :-( "$
  terpri()$
  return nil

 >>              else return <<  % return the new s2 as s1 IS a special case of s2

  % Which inequality is to be dropped?
  ineq2:=car cddddr sol2$

  while ineq2 do <<
   ine:=car ineq2;
   % ine should not have denominators, so no extra precautions for substitution:
   for each a in caddr sol1 do ine:=reval(subst(caddr a,cadr a,ine));
   if not zerop reval ine then ineqnew:=cons(car ineq2,ineqnew)
                          else ineqdrop:=cons(car ineq2,ineqdrop)$
   ineq2:=cdr ineq2
  >>$

  if absorb then <<
   % delete the redundant solution
   sol_list:=delete(s1,sol_list); % system bldmsg ("rm %s",s1);

   % transform the general solution if that was necessary and
   % updating the list of free parameters
   h:=cons('LIST,caddr sol2);
   b:=cadddr sol2;
   if tr_merge then <<
    write"h0="$print_indexed_list(h)$
    write"dropped_assign_in_s2="$print_indexed_list(dropped_assign_in_s2)$
    write"new_assign_in_s2="$print_indexed_list(new_assign_in_s2)$
   >>$
   for each a in dropped_assign_in_s2 do
   <<h:=delete(a,h);b:=cons(reval cadr a,b)>>$
   if tr_merge then <<write"h1="$print_indexed_list(h)>>$
   for each a in reverse new_assign_in_s2 do if h then <<
    b:=delete(reval cadr a,b)$
    if tr_merge then <<write"a=",a$terpri()$write"h2="$print_indexed_list(h)>>$
    h:=err_catch_sub(cadr a,caddr a,h);
    if h then h:=reval append(h,list a)
   >>$
   if null h then
   write"A seemingly successful transformation of ",s2,
        "went singular when performing the transformation ",
        "finally on the whole solution."
             else     % save the generalized solution
   save_solution(cadr sol2,cdr h,b,ineqnew,s2)$
  >>;

  if absorb and null h then nil
                       else <<
   % report the merging
   if null ineqdrop then <<
    write"Strange: merging ",s1," and ",s2," without dropping inequalities!"$
    terpri()$
    write"Probably ",s2," had already been merged with ",s1,
         " or similar before."$ terpri()
   >>               else
   if print_ then <<
    write"Solution ",s2," includes ",s1," by dropping "$
    if length ineqdrop = 1 then write"inequality"
                           else write"inequalities"$terpri()$
    for each ine in ineqdrop do mathprint ine
   >>;
   s2 % the more general solution
  >>
 >>
end$

symbolic operator merge_sol$
symbolic procedure merge_sol$
begin scalar s,sol_cp,sl1,sl2,s1,s2,s3,sol1,sol2,echo_bak$

 if null session_ then ask_for_session() else <<
  write "Do you want to merge solutions computed in this session,"$
  terpri()$
  if not yesp "i.e. since loading CRACK the last time? " then
  ask_for_session()
 >>$

 % reading in sol_list
 setq(s,bldmsg("%w%w",session_,"sol_list"));
 in s;

 % % At fist sort sol_list by the number of free unknowns
 % for each s1 in sol_list do <<
 %  in s1;
 %  s2:=if null cadddr backup_ then 0 else length cadddr backup_;
 %  if cadr backup_ then s2:=s2 - length cadr backup_;
 %  sol_cp:=cons((s2 . s1),sol_cp)
 % >>$
 % sol_cp:=idx_sort(sol_cp)$
 % while sol_cp do <<sl1:=cons(cdar sol_cp,sl1);sol_cp:=cdr sol_cp>>$

 sol_cp:=sol_list$
 sl1:=sol_cp$

 if sl1 then
 while sl1 and cdr sl1 do <<
  s1:=car sl1; sl1:=cdr sl1;
  %infile s1;
  echo_bak:=!*echo; semic!*:='!$; in s1$ !*echo:=echo_bak;
  sol1:=backup_;
  if print_ then <<write"Comparing ",s1," with:"$terpri()>>$

  sl2:=sl1;
  while sl2 do <<
   s2:=car sl2; sl2:=cdr sl2;
   %infile s2$
   echo_bak:=!*echo; semic!*:='!$; in s2$ !*echo:=echo_bak;
   sol2:=backup_;
   if print_ then <<write"  ",s2$terpri()>>$

   if (null car sol1) and (null car sol2) then % algebraic problem
   if length cadddr sol1 <
      length cadddr sol2 then s3:=merge_two(s1,sol1,s2,sol2,t) else
   if length cadddr sol1 >
      length cadddr sol2 then s3:=merge_two(s2,sol2,s1,sol1,t) else
   <<s3:=merge_two(s1,sol1,s2,sol2,t)$
    if s3 then <<
     write"Strange: ",s1," is contained in ",s2$terpri()$
     write"but both have same number of free unknowns!"$terpri()$
     write"One of them has probably undergone earlier merging"$
     terpri()$
    >>
   >>                                     else
   if null (s3:=merge_two(s1,sol1,s2,sol2,t)) then
   s3:=merge_two(s2,sol2,s1,sol1,t);
   if s3=s1 then sl1:=delete(s2,sl1) else % not to pair s2 later
   if s3=s2 then sl2:=nil                 % to continue with next element in sl1
  >>
 >>;

 save_sol_list()
end$

symbolic procedure save_sol_list$
begin scalar s$
 setq(s,bldmsg("%w%w",session_,"sol_list"));
 out s;
 write"off echo$ "$ terpri()$
 write"sol_list:='"$
 print sol_list$write"$"$terpri()$
 write "end$"$terpri()$
 shut s
end$

symbolic procedure ask_for_session$
begin scalar ps$
 ps:=promptstring!*$
 promptstring!*:="Name of the session in double quotes: "$
 terpri()$session_:=termread()$
 promptstring!*:=ps
end$

symbolic operator pri_sol$
symbolic procedure pri_sol(sin,assgn,crout,html,solcount,fname,prind)$
% print the single solution sin
begin scalar a,b,sout$
 in sin$

 if html then <<
  setq(sout,bldmsg("%w%w%d%w",fname,"-s",solcount,".html"));
  out sout;

  write"<html>"$terpri()$
  terpri()$
  write"<head>"$terpri()$
  write"<meta http-equiv=""Content-Type"" content=""text/html;"$terpri()$
  write"charset=iso-8859-1"">"$terpri()$
  write"<title>Solution ",solcount," to problem ",prind,"</title>"$terpri()$
  write"</head>"$terpri()$
  terpri()$
  write"<BODY TEXT=""#000000"" BGCOLOR=""#FFFFFF"">"$terpri()$
  terpri()$
  write"<CENTER><H2>Solution ",solcount," to problem ",prind,"</H2>"$terpri()$
  write"<HR>"$terpri()$
  if cadr backup_ then <<write"<A HREF=""#1"">Remaining equations</A> | "$
                         terpri()>>$
  write"<A HREF=""#2"">Expressions</A> | "$terpri()$
  write"<A HREF=""#3"">Parameters</A> | "$terpri()$
  write"<A HREF=""#4"">Relevance</A> | "$terpri()$
  write"<A HREF=",prind,".html>Back to problem ",prind,"</A> "$
  write"</CENTER>"$terpri()$
  terpri()
 >>$
 for each a in car backup_ do
 for each b in cdr a do
 algebraic(depend(lisp(car a),lisp b));
 backup_:=cdr backup_;
 terpri()$
 if html then write"<!-- "$
 write">>>=======>>> SOLUTION ",sin," <<<=======<<<"$
 if html then write" --> "$
 terpri()$terpri()$

 if assgn or html then <<

  if car backup_ then <<
   if html then <<
    write"<HR><A NAME=""1""></A><H3>Equations</H3>"$terpri()$
    write"The following unsolved equations remain:"$terpri()$
    write"<pre>"$
   >>      else write"Equations:"$
   for each a in car backup_ do mathprint {'EQUAL,0,a}$
   if html then <<write"</pre>"$terpri()>>
  >>$
  if html then <<
   write"<HR><A NAME=""2""></A><H3>Expressions</H3>"$terpri()$
   write"The solution is given through the following expressions:"$terpri()$
   write"<pre>"$terpri()$
   for each a in cadr backup_ do mathprint a$
   write"</pre>"$terpri()
  >>      else <<
   b:=nil;
   for each a in cadr backup_ do
   if not zerop caddr a then b:=cons(a,b);
   print_fcts(b,nil)$
  >>$
  terpri()$

  if html then <<
   write"<HR><A NAME=""3""></A><H3>Parameters</H3>"$terpri()$
   write"Apart from the condition that they must not vanish to give"$terpri()$
   write"a non-trivial solution and a non-singular solution with"$terpri()$
   write"non-vanishing denominators, the following parameters are free:"$terpri()$
   write"<pre> "$
   fctprint caddr backup_;
   write"</pre>"$terpri()
  >>      else <<
   write length caddr backup_," free unknowns: "$ listprint caddr backup_;
   print_ineq(cadddr backup_)$
  >>
 >>$

 if html then <<
  write"<HR><A NAME=""4""></A><H3>Relevance for the application:</H3>"$
  terpri()$
  % A text for the relevance should be generated in crack_out()
%  write"The solution given above tells us that the system {u_s, v_s}"$
%  terpri()$
%  write"is a higher order symmetry for the lower order system {u_t,v_t}"$
%  terpri()$
%  write"where u=u(t,x) is a scalar function, v=v(t,x) is a vector"$
%  terpri()$
%  write"function of arbitrary dimension and f(..,..) is the scalar"$
%  terpri()$
%  write"product between two such vectors:"$
%  terpri()$
  write"<pre>"
 >>$
 if crout or html then <<
  algebraic (
  crack_out(lisp cons('LIST,car backup_),
           lisp cons('LIST,cadr backup_),
           lisp cons('LIST,caddr backup_),
           lisp cons('LIST,cadddr backup_) ))$
 >>$
 if html then <<
  write"</pre>"$terpri()$
  write"<HR>"$terpri()$
  write"</body>"$terpri()$
  write"</html>"$terpri()$
  shut sout
 >>
end$

symbolic operator print_all_sol$
symbolic procedure print_all_sol$
begin scalar s,a,assgn,crout,natbak,print_more_bak,fname,solcount,
             html,prind,ps$

 write"This is a reminder for you to read in any file CRACK_OUT.RED"$
 terpri()$
 write"with a procedure CRACK_OUT() in case that is necessary to display"$
 terpri()$
 write"results following from solutions to be printed."$
 terpri()$ terpri()$

 if null session_ then ask_for_session() else <<
  write "Do you want to print solutions computed in this session,"$
  terpri()$
  if not yesp "i.e. since loading CRACK the last time? " then
  ask_for_session()$
  terpri()
 >>$

 % reading in sol_list
 setq(s,bldmsg("%w%w",session_,"sol_list"));
 in s;

 natbak:=!*nat$ print_more_bak:=print_more$ print_more:=t$
 if yesp "Do you want to generate an html file for each solution? "
 then <<
  html:=t$
  solcount:=0$
  terpri()$
  write "What is the file name (including the path)"$
  terpri()$
  write "that shall be used (in double quotes) ? "$
  terpri()$
  write "(A suffix '-si'  will be added for each solution 'i'.) "$
  ps:=promptstring!*$
  promptstring!*:=""$
  fname:=termread()$terpri()$
  write "What is a short name for the problem? "$
  prind:=termread()$
  promptstring!*:=ps$
  terpri()$
 >> else <<
  if yesp "Do you want to see the computed value of each function? "
  then assgn:=t$
  if yesp "Do you want procedure `crack_out' to be called? " then <<
   crout:=t;
   if flin_ and homogen_ then
   if yesp "Do you want to print less (e.g. no symmetries)? "
   then print_more:=nil$
   if not yesp
   "Do you want natural output (no if you want to paste and copy)? "
   then !*nat:=nil$
  >>$
 >>$
 for each a in sol_list do <<
  if html then solcount:=add1 solcount$
  pri_sol(a,assgn,crout,html,solcount,fname,prind)$
 >>$
 !*nat:=natbak;
 print_more:=print_more_bak
end$

symbolic procedure sol_in_list(set1,set2,sol_list2)$
begin scalar set2cp,s1,s2,found,sol1,sol2,same_sets,echo_bak$
 while set1 do <<
  s1:=car set1; set1:=cdr set1;
  %infile s1;
  echo_bak:=!*echo; semic!*:='!$; in s1$ !*echo:=echo_bak;
  sol1:=backup_;
  set2cp:=set2$
  found:=nil$
  while set2cp and not found do <<
   s2:=car set2cp; set2cp:=cdr set2cp;
   %infile s2;
   echo_bak:=!*echo; semic!*:='!$; in s2$ !*echo:=echo_bak;
   sol2:=backup_;
   found:=merge_two(s1,sol1,s2,sol2,nil)$
  >>;
  if not found then <<
   same_sets:=nil;
   if print_ then <<
    write"Solution ",s1," is not included in ",sol_list2$
    terpri()
   >>
  >>
 >>$
 return same_sets
end$

symbolic operator same_sol_sets$
symbolic procedure same_sol_sets$
begin scalar session_bak,set1,set2,sol_list1,sol_list2,echo_bak$
 session_bak:=session_;
 write"Two sets of solutions are compared whether they are identical."$

 write"What is the name of the session that produced the first set of solutions?"$
 terpri()$
 write"(CRACK will look for the file `sessionname'+`sol_list'.)"$terpri()$
 ask_for_session()$

 % reading in sol_list
 setq(sol_list1,bldmsg("%w%w",session_,"sol_list"));
 %infile sol_list1;
 echo_bak:=!*echo; semic!*:='!$; in sol_list1$ !*echo:=echo_bak;
 set1:=sol_list$

 write"What is the name of the session that produced the second set of solutions?"$
 terpri()$
 ask_for_session()$

 % reading in sol_list
 setq(sol_list2,bldmsg("%w%w",session_,"sol_list"));
 %infile sol_list2;
 echo_bak:=!*echo; semic!*:='!$; in sol_list2$ !*echo:=echo_bak;
 set2:=sol_list$

 session_:=session_bak$

 % 1. Check that all solutions in set1 are included in set2.

 sol_in_list(set1,set2,sol_list2)$
 sol_in_list(set2,set1,sol_list1)$

end$

% some PSL specific hacks

!#if (memq 'psl lispsystem!*)
  symbolic procedure delete!-file(fi);
   if memq('unix,lispsystem!*) then
        system bldmsg("rm '%s'",fi) else
        system bldmsg("del ""%s""",fi);
 !#endif

endmodule$

end$

