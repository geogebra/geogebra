%********************************************************************
module simplifications$
%********************************************************************
%  Routines for simplifications, contradiction testing
%  and substitution of functions
%  Author: Andreas Brand   1991 1993 1994
%          Thomas Wolf since 1996

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



symbolic procedure signchange(g)$
%  ensure, that the first term is positive
if pairp g then
 if (car g='MINUS) then cadr g
 else if (car g='PLUS) and (pairp cadr g) and (caadr g='MINUS)
      then reval list('MINUS,g)
      else g
else g$

symbolic procedure simplifyterm(p,ftem)$
%  simplify a single factor p of g=p*q*r*...=0
if (ftem:=smemberl(ftem,p)) then
  if pairp p and member(car p,'(MINUS SQRT QUOTIENT))
  then simplifyterm(cadr p,ftem)
  else if pairp p and (car p='EXPT) then
       if smemberl(ftem,cadr p) then simplifyterm(cadr p,ftem)
                                else 1
  else if member((p:=signchange p),ineq_) then 1
                                                            else p
else if not p or zerop p then 0
                         else 1$

symbolic procedure simp_ineq(p)$
<<p:=reval p$
  while pairp p and member(car p,'(MINUS SQRT QUOTIENT EXPT)) do p:=cadr p$
  p>>$

symbolic procedure may_vanish(p)$
if null p then t else
begin scalar h,hh$
 p:=factored_form simp_ineq(p)$
 if (pairp p) and (car p = 'TIMES) then h:=for each hh in cdr p collect simp_ineq(hh)
                                   else h:=list p$
 while h and (freeoflist(car h,ftem_) or
              member(car h,ineq_) or
              ((pairp car h) and
               (caar h = 'PLUS) and
               member(reval {'MINUS,car h},ineq_))
             ) do h:=cdr h;
 return if h then t
             else nil
end$

symbolic procedure drop_triv_ineq(ineq)$
begin scalar newineq;
 while ineq do <<
  if not numberp car ineq then newineq:=cons(car ineq,newineq);
  ineq:=cdr ineq
 >>$
 return newineq
end$

symbolic procedure contradictioncheck(s,pdes)$
% --> drops factors s in all pdes without asking!!
begin scalar v,p$
if s then
 while pdes do
   <<p:=car pdes$pdes:=cdr pdes$
   v:=get(p,'val)$
   if pairp v and (car v='TIMES) then
     (if member(s,cdr v) then
       <<v:=delete(s,cdr v)$
       update(p,if length v=1 then car v else cons('TIMES,v),
              get(p,'fcts),get(p,'vars),nil,list(0),pdes);
       drop_pde_from_idties(p,pdes,nil)$
       drop_pde_from_properties(p,pdes)$
       for each a in allflags_ do flag1(p,a)$
       >>)
   else if s=v then
    <<raise_contradiction(v,nil)$
    pdes:=nil>>
   >>$
return contradiction_$
end$

symbolic procedure raise_contradiction(g,text)$
<<contradiction_:=t$
  if print_ then
     <<terpri()$if text then write text
                        else write "contradiction : "$
  deprint list g>> >>$

symbolic procedure doedel3 (x)$
begin scalar  xx,kerne,coef,co,fact, xy,summ;
  xx := car x;
  summ := simp 0;
  xy := cadr aeval xx;
  kerne :=  kernels !*q2f xy;
  for each kk in kerne do
  if smemberl(ftem_,kk) then <<
    co := coef := nth(coeffeval list(xx,kk),3);
    co := if atom co then simp co else cadr co;
    if atom coef then fact := simp coef else <<
      coef := fctrf numr cadr coef;
      fact := simp car coef;
      coef := foreach fa in rest coef do fact := multsq(!*p2q fa ,fact);
    >>;
    xy   := addsq(xy,multsq(simp (-1),multsq(co,simp kk)));
    coef := multsq(fact,simp kk);
    summ := addsq(coef,summ) >>;
  summ := addsq(xy,summ);
  return prepsq summ;
%  return list('!*sq,summ,t);  % this is faster but standard quot. form
end$


symbolic procedure simplifypde(g,ftem,tofactor,en)$
%  simplify g=0, en is the name of the equation
begin scalar h,l,ruli,enhi$
 if en and record_hist then enhi:=get(en,'histry_)$
 % if rulelist_ then g:=reval evalwhereexp list(rulelist_,g)$
 ruli:=start_let_rules()$
 g:=reval aeval g$
% g:=doedel3 g$
 stop_let_rules(ruli)$
 if g and not zerop g and not (ftem:=smemberl(ftem,g)) then
   <<raise_contradiction(g,nil)$g:=1>>
 else if pairp g then
  if member(car g,'(EXPT QUOTIENT MINUS SQRT)) then <<
     if enhi then
     if car g='EXPT     then
        put(en,'histry_,reval {'EXPT,enhi,{'QUOTIENT,1,caddr g}}) else
     if car g='QUOTIENT then
        put(en,'histry_,reval {'TIMES,enhi,caddr g}) else
     if car g='MINUS    then put(en,'histry_,reval {'MINUS,enhi}) else
     if car g='SQRT     then put(en,'histry_,reval {'EXPT,enhi,2})$
     g:=simplifypde(cadr g,ftem,tofactor,en) >>
  else if member(car g,'(LOG LN LOGB LOG10)) then <<
     if enhi then
     if (car g='LOG) or
        (car g='LN)  then
        put(en,'histry_,reval {'PLUS,{'EXPT,'E,enhi},-1}) else
     if car g='LOGB  then
        put(en,'histry_,reval {'PLUS,{'EXPT, 2,enhi},-1}) else
     if car g='LOG10 then
        put(en,'histry_,reval {'PLUS,{'EXPT,10,enhi},-1})$
     g:=simplifypde(reval {'PLUS,cadr g,-1},ftem,tofactor,en) >>
  else if tofactor then
   <<if car g='TIMES
     then l:=for each a in cdr g join
                 if numberp a then {a}
                              else cdr err_catch_fac(a)
     else l:=cdr err_catch_fac(g)$
     while l do
       <<if not member(car l,cdr l) then
            h:=union(list simplifyterm(car l,ftem),h)$
       l:=cdr l>>$
     h:=delete(1,h)$
     if null h then
        <<raise_contradiction(g,nil)$
        g:=1>>
     else <<
       if enhi then l:=g;
       if pairp cdr h then g:=cons('TIMES,reverse h)
                      else g:=car h$
       if enhi and (l neq g) then
       put(en,'histry_,reval {'TIMES,{'QUOTIENT,g,l},enhi})$
     >>
   >>$
   return g$
end$

symbolic procedure fcteval(p,less_vars)$
%  looks for a function which can be eliminated
%  if one is found, it is stored in p as (coeff_of_f.f)
%  if less_vars neq nil then the expr. to be substituted
%  must have only fcts. of less vars
%  'to_eval neq nil iff not checked yet for substitution
%                   or if subst. possible
%               i.e. 'to_eval=nil if checked and not possible
%  'fcteval_lin includes subst. with coefficients that do not
%               include ftem functions/constants
%  'fcteval_nca includes subst. with non-vanishing coefficients
%               and therefore no case distinctions (linearity)
%  'fcteval_nli includes subst. with possibly vanishing coefficients
%               and therefore case distinctions (non-linearity)
begin scalar ft,a,b,fl,li,nc,nl,f,cpf,fv,fc$
  if flagp(p,'to_eval) then <<
    b:=get(p,'not_to_eval)$   % functions that replace a derivative
    if (not get(p,'fcteval_lin)) and
       (not get(p,'fcteval_nca)) and
       (not get(p,'fcteval_nli)) then <<
      ft:=get(p,'allvarfcts)$
      if flin_ and (fl:=intersection(ft,flin_)) then <<ft:=fl; fl:=nil>>$
      if null ft then <<
        ft:=get(p,'rational)$
        % drop all functions f from ft for which there is another
        % function which is a function of all variables of f + at
        % least one extra variable
        for each f in ft do <<
          cpf:=get(p,'fcts)$
          fv:=fctargs f$
          while cpf and
                (not_included(fv,fc:=fctargs car cpf) or
                 (length fv >= length fc)                ) do
          cpf:=cdr cpf;
          if null cpf then fl:=cons(f,cpf)
        >>$
        ft:=fl$
      >>$
      if ft then <<
        if (not less_vars)       or
           (not cdr ft)          or
           (zerop get(p,'nvars)) then <<
          % either all functions allowed or only one fnc of all vars
          for each f in ft do
          if (not member(f,b)) and linear_fct(p,f) then <<
            % only linear algebr. fcts
            a:=factored_form reval coeffn(get(p,'val),f,1)$
            if fl:=smemberl(delete(f,get(p,'fcts)),a) then
            if freeofzero(a,fl,get(p,'vars),get(p,'nonrational))
            then nc:=cons(cons(a,f),nc)
            else nl:=cons(cons(a,f),nl)               else
                 li:=cons(cons(a,f),li)
          >>$
          if li then put(p,'fcteval_lin,reverse li); % else
          if nc then put(p,'fcteval_nca,reverse nc); % else
          if nl then put(p,'fcteval_nli,reverse nl);
          if not (li or nc or nl) then remflag1(p,'to_eval)
        >>
      >>$
    >>$
    return (get(p,'fcteval_lin) or
            get(p,'fcteval_nca) or
            get(p,'fcteval_nli)    )
  >>
end$

symbolic procedure freeofzero(p,ft,vl,nrat)$
%   gets p (factorized), if p does not vanish identically
%   nrat is the set of potentially non-rationally occuring functions.
%   If unknown, then to be set = ft
if null ft or numberp p then p
else
begin scalar a,b,h,fr,pri,nonrat$
 pri:=print_$
 print_:=nil$
 for each s in cdr err_catch_fac(p) do
    a:=union(list simplifyterm(s,ft),a)$
 if length a>1 then p:=cons('TIMES,a)$
 while a do
  if null smemberl(ft,car a) or member(signchange(car a),ineq_) then a:=cdr a
  else if pairp cdr
    (b:=union(for each s in
              separ(car a,ft,vl,
                          <<nonrat:=nil;
                            for each h in ft do
                            if not rationalp(car a,h) then
                            nonrat:=cons(h,nonrat);
                            nonrat
                          >>
              ) collect cdr s,nil)) then
      <<fr:=nil$
      while b do if freeofzero(car b,ft,vl,nrat) then <<b:=nil$fr:=t>>
                                                                         else b:=cdr b$
      if fr then a:=cdr a
            else <<a:=nil$p:=nil>> >>
    else <<a:=nil$p:=nil>>$
 print_:=pri$
return p
end$

%symbolic procedure flin_filter(s,preserve_flin,l)$
%if flin_ and preserve_flin and not freeoflist(get(s,'fcts),flin_) then
%begin scalar h$
%  while l do <<
%    if not freeof(flin_,cdar l) then h:=cons(car l,h);
%    l:=cdr l
%  >>$
%  return h
%end                                                              else l$

symbolic procedure get_subst(pdes,l,length_limit,less_vars,no_df)$
%
% get the most simple pde from l which leads to a function substitution
% if less_vars neq nil: the expr. to subst. has only fcts. of less vars
% if no_df neq nil:     the expr. to subst. has no derivatives
begin scalar p,q,h,l1,l2,m,ntms,mdu,ineq_cp,
             n0f,rtn,lcop,fcteval_cop,necount$
  % mdu=(1:lin, 2:nca, 3:nli_lin, 4:nli_nca, 5:nli_nli, 6:nli_nus)

  lcop:=l;
  % drop all equations longer than length_limit
  if length_limit then <<
    while l do
    if get(car l,'length)>length_limit then l:=nil
                                       else <<
      l1:=cons(car l,l1)$
      l:=cdr l
    >>$
    l:=reverse l1
  >>$
  % l is now the list of equations <= length_limit

  % next: substitution only if no_df=nil or
  %       no derivative of any function occurs
  if no_df then <<
    l1:=nil;
    for each s in l do
      <<l2:=get(s,'derivs)$
      while l2 do
         if pairp(cdaar l2) then
            <<l2:=nil;
           l1:=cons(s,l1)>>
        else l2:=cdr l2>>$
    l:=setdiff(l,l1)>>$

  % next: restrict to substitutions, if any,
  %       that have a coefficient without ftem-dependence
  l1:=nil; mdu:=100;
  necount:=0;
  for each s in l do
  if fcteval(s,less_vars) then
  if get(s,'fcteval_lin) then if mdu>1 then <<mdu:=1;l1:=list s>>
                                       else l1:=cons(s,l1)        else
  if (mdu>1) and get(s,'fcteval_nca)                      then
  if mdu>2 then <<mdu:=2;l1:=list s>> else l1:=cons(s,l1) else
  if (mdu>2) and (h:=get(s,'fcteval_nli)) then <<
    if (null get(s,'fct_nli_lin)) and
       (null get(s,'fct_nli_nca)) and
       (null get(s,'fct_nli_nli)) and
       (null get(s,'fct_nli_nus)) then <<
      ineq_cp:=ineq_; ineq_:=nil;
      % partition get(s,'fcteval_nli) into the above 4 cases
      for each l2 in h do <<
        q:=mkeq(car l2,get(s,'fcts),get(s,'vars),allflags_,t,list(0),nil,nil);
        % the pdes-argument in mkeq() is nil to avoid lasting effect on pdes
        necount:=add1 necount$
        fcteval(q,less_vars)$ % less_vars
        if get(q,'fcteval_lin) then
        put(s,'fct_nli_lin,cons(l2,get(s,'fct_nli_lin))) else
        if get(q,'fcteval_nca) then
        put(s,'fct_nli_nca,cons(l2,get(s,'fct_nli_nca))) else
        if get(q,'fcteval_nli) then
        put(s,'fct_nli_nli,cons(l2,get(s,'fct_nli_nli))) else
        put(s,'fct_nli_nus,cons(l2,get(s,'fct_nli_nus)))$
        drop_pde(q,nil,nil)$
        if necount>100 then <<
         clean_prop_list(pdes)$
         necount:=0
        >>
      >>$
      ineq_:=ineq_cp
    >>$
    if              get(s,'fct_nli_lin)                 then
    if mdu>3 then <<mdu:=3;l1:=list s>> else l1:=cons(s,l1) else
    if (mdu>3) and get(s,'fct_nli_nca)                  then
    if mdu>4 then <<mdu:=4;l1:=list s>> else l1:=cons(s,l1) else
    if (mdu>4) and get(s,'fct_nli_nli)                  then
    if mdu>5 then <<mdu:=5;l1:=list s>> else l1:=cons(s,l1) else
    if (mdu>5) and get(s,'fct_nli_nus)                  then
    if mdu>6 then <<mdu:=6;l1:=list s>> else l1:=cons(s,l1)
  >>$
  l:=l1$

  % next: find an equation with as many as possible variables
  %       and few as possible terms for substitution
  m:=-1$
  for each s in l do <<
   l1:=get(s,'nvars);
   if get(s,'starde) then l1:=sub1 l1;
   if l1>m then m:=l1$
  >>$

  while m>=0 do <<
    l1:=l$
    ntms:=10000000$
    while l1 do
    if ((get(car l1,'nvars) -
         if get(car l1,'starde) then 1
                                else 0) = m )   and
       fcteval(car l1,less_vars)                and
       (get(car l1,'terms) < ntms)              then <<
      p:=car l1$
      l1:=cdr l1$
      ntms:=get(p,'terms)$
    >>                             else l1:=cdr l1$
    m:=if p then -1
            else sub1 m
  >>$

  if p then return <<

    fcteval_cop:=if mdu=1 then get(p,'fcteval_lin) else
                 if mdu=2 then get(p,'fcteval_nca) else
                 if mdu=3 then get(p,'fct_nli_lin) else
                 if mdu=4 then get(p,'fct_nli_nca) else
                 if mdu=5 then get(p,'fct_nli_nli) else
                 if mdu=6 then get(p,'fct_nli_nus);

    rtn:={mdu,p,pick_fcteval(pdes,mdu,fcteval_cop)};
    % prevent the substitution of a function<>0
    if rtn and homogen_ and setdiff(ftem_,ineq_) and
       cdr pdes and (get(p,'terms)>1) then <<
      % i.e. not all ftem_ have to be non-zero
      % and it is not the last pde
      n0f:=setdiff(ineq_,setdiff(ineq_,ftem_));
      if freeof(n0f,cdaddr rtn) then rtn
                                else
      if null cdr fcteval_cop then % rtn was the only substitution of this eqn.
      if cdr lcop then             % there are other eqn.s to choose from
      <<h:=get_subst(pdes,delete(p,lcop),length_limit,less_vars,no_df);
        if null h then rtn else h>>
                  else rtn % nil   % no substitution --> changed to rtn
                              else <<
        fcteval_cop:=delete(caddr rtn,fcteval_cop);
        {mdu,p,pick_fcteval(pdes,mdu,fcteval_cop)}
      >>
    >>                                                        else rtn
  >>
end$

symbolic procedure pick_fcteval(pdes,mdu,fctlist)$
if fctlist then
if (not expert_mode) or (length  fctlist = 1) then
% automatic pick of all the possible substitutions
if null cdr fctlist then car fctlist else
if mdu<3 then begin % substitute the function coming first in ftem_
 scalar best;
 best:=car fctlist; fctlist:=cdr fctlist;
 while fctlist do <<
  if which_first(cdr best,cdar fctlist,ftem_) neq cdr best
  then best:=car fctlist;
  fctlist:=cdr fctlist
 >>;
 return best
end      else
begin scalar co,minfinco,minnofinco,finco,nofinco,fctlilen,
      n,maxnopdes,nopdes,f,bestn$
 % 1. find a substitution where the coefficient involves as few as possible functions
 fctlilen:=length fctlist$
 minnofinco:=10000$
 for n:=1:fctlilen do <<
  co:=nth(fctlist,n)$
  finco:=smemberl(ftem_,car co);
  nofinco:=length finco;
  if nofinco<minnofinco then <<minfinco:=list(cons(n,finco));
                               minnofinco:=nofinco>> else
  if nofinco=minnofinco then minfinco:=cons(cons(n,finco),minfinco);
 >>$
 if (length minfinco=1) or (minnofinco>1)
 % if there is only one substitution where the coefficient has a
 % minimal number of ftem_ functions or
 % if the minimal number of functions in any coefficient is >1
 then return nth(fctlist,caar minfinco) % return any ony one of the minimal ones
 else return << % find the one with the ftem_ function that occurs in the
                % fewest equations, to complicate as few as possible equations
  maxnopdes:=1000000;
  for each su in minfinco do <<
   f:=cadr su;
   nopdes:=0;
   for each p in pdes do if not freeof(get(p,'fcts),f) then nopdes:=add1 nopdes;
   if nopdes<maxnopdes then <<maxnopdes:=nopdes;bestn:=car su>>;
  >>$
  nth(fctlist,bestn)
 >>
end                                           else
begin scalar fl,a,h,hh;
 fl:=for each a in fctlist collect cdr a$
 write"Choose a function to be substituted from "$
 listprint(fl)$terpri()$
 hh:=promptstring!*$ promptstring!*:=""$
 repeat h:=termread() until not freeof(fl,h);
 promptstring!*:=hh$
 while h neq cdar fctlist do fctlist:=cdr fctlist;
 return car fctlist
end$

%symbolic procedure ineqsplit(q,ftem)$
%% q into factors and
%% drop quotients
%begin scalar l$
% if pairp q and (car q='QUOTIENT) then q:=cadr q$
% q:=cdr err_catch_fac(q)$
% for each s in q do
%     if smemberl(ftem,s) then
%        <<s:=signchange s$
%        if not member(s,l) then l:=cons(s,l)>>$
%return l$
%end$

%symbolic procedure ineqsubst(new,old,ftem)$
%% tests all q's in ineq_ for subst(new, old,q)=0
%% result: nil, if 0 occurs
%%         otherwise list of the subst(car p,...)
%begin scalar l,a$
%while ineq_ do
% if not my_freeof(car ineq_,old) then
% <<a:=simplifyterm(reval reval subst(new, old,car ineq_),ftem)$
%   if zerop a then
%   <<if print_ then
%     <<terpri()$write "contradiction from the substitution:"$
%       eqprint list('EQUAL,old,new)$
%       write "because of the non-vanishing expression:"$
%       eqprint car ineq_>>$
%     contradiction_:=t$
%     l:=nil$
%     ineq_:=nil>>
%   else
%   <<l:=union(ineqsplit(a,ftem),l)$
%     ineq_:=cdr ineq_>> >>
% else
% <<l:=cons(car ineq_,l)$
%   ineq_:=cdr ineq_>>$
%ineq_:=reverse l$
%end$

symbolic procedure ineqsubst(new,old,ftem,pdes)$
% tests all q's in ineq_ for subst(new, old,q)=0
% result: nil, if 0 occurs
%         otherwise list of the subst(car p,...)
begin scalar l,a,newin$
  l:=ineq_; ineq_:=nil;
  while l do <<
    if freeof(car l,old) then ineq_:=cons(car l,ineq_)
                         else
    <<a:=simplifyterm(reval reval subst(new,old,car l),ftem)$
      if zerop a then
      <<if print_ then
        <<terpri()$write "contradiction from the substitution:"$
          eqprint list('EQUAL,old,new)$
          write "because of the non-vanishing expression:"$
          eqprint car l>>$
        contradiction_:=t$
        l:=list nil$
        ineq_:=nil
      >>         else newin:=cons(a,newin);
    >>;
    l:=cdr l
  >>$
  for each a in newin do addineq(pdes,a)
end$

symbolic procedure do_one_subst(ex,f,a,ftem,vl,level,eqn,pdes)$
% substitute f by ex in a
% pdes used only in drop_pde_from_idties(), to be dropped when pdes_
% will be global
begin scalar l,l1,p,oldstarde,h$
 l:=get(a,'val)$
 oldstarde:=get(a,'starde)$
 if pairp l and (car l='TIMES) then l:=cdr l
                               else l:=list l$
 while l do <<  % for each factor
  if smember(f,car l) then <<
   p:=reval reval subst(ex,f,car l)$
   if not p or zerop p then <<l:=list nil$l1:=list 0>>
                       else <<
    if pairp p and (car p='QUOTIENT) then p:=cadr p$
    %l1:=if fixp(h:=no_of_terms(p)) and (h>max_factor) then cons(p,l1)
    %                                                  else
    h:=err_catch_fac(p);
    l1:=if null h then cons(p,l1)
                  else append(reverse cdr h,l1)
   >>
  >>                  else l1:=cons(car l,l1)$
  l:=cdr l
 >>$
 l:=nil$
 while l1 do <<
  if not member(car l1,cdr l1) then
  l:=union(list simplifyterm(car l1,ftem),l)$
  l1:=cdr l1
 >>$
 l:=delete(1,l)$
 if null l then <<
  if print_ then <<
   terpri()$  % new
   write"Substitution of "$
   fctprint list f$
   if cdr get(eqn,'fcts) then <<
    write " by an expression in "$terpri()$
    fctprint delete(f,get(eqn,'fcts))
   >>$
   write " found in ",eqn," : "$
   eqprint(list('EQUAL,f,ex))
  >>$
  raise_contradiction(get(a,'val),
                      "leads to a contradiction in : ")$
  a:=nil
 >>        else <<
  if pairp cdr l then l:=cons('TIMES,l)
                 else l:=car l$
  if get(a,'level) neq level then
     a:=mkeq(l,ftem,vl,allflags_,nil,list(0),nil,pdes)
  else <<
   p:=get(a,'derivs);
   if p then p:=caar p;
   for each b in allflags_ do flag(list a,b)$
   if null update(a,l,ftem,vl,nil,list(0),pdes) then <<
    drop_pde(a,nil,0)$
    a:=nil
   >>                                      else <<
    % If the leading derivative has changed then drop
    % the 'dec_with and the 'dec_with_rl list.
    l1:=get(a,'derivs);
    if l1 then l1:=caar l1;
    if l1 neq p then <<
     put(a,'dec_with,nil);
     put(a,'dec_with_rl,nil)
    >>;
    drop_pde_from_idties(p,pdes,nil)$
    % nil as second argument for safety, for not knowing better
    drop_pde_from_properties(p,pdes)
   >>
  >>$
  put(a,'level,level)
 >>$
 if oldstarde and not get(a,'starde) then put(a,'dec_with,nil);
 return a$
end$

symbolic procedure do_subst(md,p,l,pde,ftem,forg,vl,plim,keep_eqn)$
% md is the mode of substitution, needed in case of an ISE
% Substitute a function in all pdes
begin scalar f,fl,h,ex,res,slim,too_large,was_subst,
             ruli,ise,cf,vl,nof,stde,partial_subs$
% l:=get(p,'fcteval_lin)$
% if null l then l:=get(p,'fcteval_nca)$
% if null l then l:=get(p,'fcteval_nli)$
% if l then << % l:=car l$
  f:=cdr l$
  cf:=car l$
  if get(p,'starde) then ise:=t;
  slim:=get(p,'length)$
  ruli:=start_let_rules()$
  ex:=reval aeval list('QUOTIENT,
                       list('PLUS,list('MINUS,get(p,'val)),
                                  list('TIMES,cf,f)),
                       cf)$

  %---- specification of substitution in case of expert_mode (user guided)
  if expert_mode then <<
   terpri()$
   write"Enter a list of equations in which substitution should take place."$
   terpri()$
   write"Substitution into the expressions for the original functions and"$
   terpri()$
   write"the inequalities is only done if you select all equations with `;' ."$
   l:=select_from_list(pde,nil)$
   if l then <<
    if not_included(pde,l) then partial_subs:=t
                           else partial_subs:=nil;
    l:=delete(p,l)
   >>;
   if partial_subs then
   if yesp "Should substitutions be done in the inequalities? " then h:=t
                                                                else h:=nil
  >>             else l:=delete(p,pde)$

  %---- substitution in inequalities
  if (not ise) and ((not partial_subs) or h) then ineqsubst(ex,f,ftem,pde)$
  if not contradiction_ then <<

   %--- substitution in forg
   if (not ise) and (not partial_subs) then <<
    fl:=delete(f,smemberl(ftem_,ex))$   % functions occuring in ex
    forg:=for each h in forg collect
          if atom h then
          if f=h then <<put(h,'fcts,fl)$was_subst:=t$list('EQUAL,f,ex)>>
                 else h
                    else
          if (car h='EQUAL) and member(f,get(cadr h,'fcts)) then <<
           was_subst:=t$
           h:=list('EQUAL,cadr h,reval subst(ex,f,caddr h));
           put(cadr h,'fcts,
               smemberl(union(fl,delete(f,get(cadr h,'fcts))),caddr h));
           h
          >>                                                else h$
   >>$
   % The following test depends on the global structure, taken out
   % for the time being:
   %% no substitution in equations which do not include all functions
   %% of all variables in ex
   %h:=nil;
   %fl:=get(p,'allvarfcts);
   %while l do <<
   % if not_included(fl,get(car l,'fcts)) then too_large:=t
   %                                      else h:=cons(car l,h);
   % l:=cdr l
   %>>;
   %l:=h;
   % Do the substitution in all suitable equations

   if ise then <<
    h:=nil;
    vl:=get(p,'vars)$
    fl:=get(p,'fcts)$
    nof:=cdr get(p,'starde)$
    while l do <<
     if (stde:=get(car l,'starde)) and
        (nof<=cdr stde) and
        (not not_included(vl,get(car l,'vars))) and
        (not not_included(fl,get(car l,'fcts))) then h:=cons(car l,h);
     l:=cdr l
    >>$
    l:=h;
   >>$
   while l and not contradiction_ do <<
   if member(f,get(car l,'fcts)) then
    if not expert_mode and plim and (slim*get(car l,'length)>plim)
    then too_large:=t
    else <<
     pde:=eqinsert(do_one_subst(ex,f,car l,ftem,vl,get(p,'level),p,pde),
                   delete(car l,pde))$
     for each h in pde do drop_rl_with(car l,h);
     put(car l,'rl_with,nil);
     for each h in pde do drop_dec_with(car l,h,'dec_with_rl);
     put(car l,'dec_with_rl,nil);
     flag(list car l,'to_int);
     was_subst:=t
    >>$
    l:=cdr l
   >>$
   if print_ and (not contradiction_) and was_subst then <<
    terpri()$write "Substitution of "$
    fctprint list f$
    if cdr get(p,'fcts) then <<
     write " by an "$
     if ise then write"(separable) "$
     write "expression in "$terpri()$
     fctprint delete(f,get(p,'fcts))
    >>$
    write " found in ",p," : "$
    eqprint(list('EQUAL,f,ex))
   >>$
   % To avoid using p repeatedly for substitutions of different
   % functions in the same equations:
   if ise then <<
    put(p,'fcteval_lin,nil);
    put(p,'fcteval_nca,nil);
    put(p,'fcteval_nli,nil);
    remflag1(p,'to_eval)$ % otherwise 'fcteval_??? would be computed again
    md:=md;   % only in order to do something with md if the next
              % statement is commented out
    % if too_large then
    % if md=1 then put(p,'fcteval_lin,list((cf . f))) else
    % if md=2 then put(p,'fcteval_nca,list((cf . f))) else
    %              put(p,'fcteval_nli,list((cf . f)))$
    % could probably unnecessarily be repeated
   >>;
   % delete f and p if not anymore needed
   if (not ise) and
      (not keep_eqn) and
      (not too_large) and
      (not partial_subs) and
      (not contradiction_) then <<
    %if not assoc(f,depl_copy_) then <<
     h:=t;
     for each l in forg do
     if pairp l then if cadr l=f then h:=nil else
                else if l=f then h:=nil;
     if h then drop_fct(f)$
    %>>$
    was_subst:=t$            % in the sense that pdes have been updated
    ftem_:=delete(f,ftem_)$
    pde:=drop_pde(p,pde,0)$
   >>$
%   if was_subst then
   res:=list(pde,forg,p)
   % also if not used to delete the pde if the function to be
   % substituted does not appear anymore
  >>$
  stop_let_rules(ruli)$
% >>$
 if not contradiction_ then return cons(was_subst,res)$
end$

symbolic procedure make_subst(pdes,forg,vl,l1,length_limit,pdelimit,
                              less_vars,no_df,no_cases,lin_subst,
                              min_growth,cost_limit,keep_eqn,sub_fc)$
% make a subst.
% l1 is the list of possible "candidates"
begin scalar p,q,r,l,h,hh,cases_,w,md,tempchng,plim$   % ,ineq,cop,newfdep
  if expert_mode then <<
   write"Which PDE should be used for substitution?"$ terpri()$
   l1:=selectpdes(pdes,1)$
  >>;

  % a fully specified substitution from to_do_list
  if sub_fc and % a specific function sub_fc is to be substituted using a
                % specific equation car l1
     l1 and null cdr l1 then <<
   h:=get(p,'fcteval_lin);
   while h and (sub_fc neq cdar h) do h:=cdr h;
   if h then hh:=1
        else <<
    h:=get(p,'fcteval_nca);
    while h and (sub_fc neq cdar h) do h:=cdr h;
    if h then hh:=2
   >>;
   if h then w:={hh,car l1,car h}
  >>;
  if sub_fc and null w then return nil;

again:
  if (min_growth and (w:=search_subs(pdes,l1,cost_limit,no_cases))) or
     ((null min_growth) and
      (w:=get_subst(pdes,l1,length_limit,less_vars,no_df))) then
  if null !*batch_mode and null expert_mode and confirm_subst and <<
    terpri()$
    write"Proposal: Substitution of  ",cdaddr w$terpri()$
    write"          using equation ",cadr  w,": "$
    if print_ and (get(cadr w,'printlength)<=print_) then print_stars(cadr w)$
    typeeq(cadr  w)$terpri()$
    %write"          with coefficient ",caaddr w$terpri()$
    if car w>2 then write"Case distinctions will be necessary."$terpri()$
    write"Accept? (Enter y or n or s for stopping substitution) "$
    hh:=promptstring!*$ promptstring!*:=""$
    repeat h:=termread() until (h='y) or (h='n) or (h='s);
    promptstring!*:=hh$
    if h='n then <<
      tempchng:=cons(w,tempchng);
      if car w=1 then <<hh:=get(cadr w,'fcteval_lin);
                        hh:=delete(caddr w,hh);
                        put(cadr w,'fcteval_lin,hh)>> else
      if car w=2 then <<hh:=get(cadr w,'fcteval_nca);
                        hh:=delete(caddr w,hh);
                        put(cadr w,'fcteval_nca,hh)>> else
                      <<hh:=get(cadr w,'fcteval_nli);
                        hh:=delete(caddr w,hh);
                        put(cadr w,'fcteval_nli,hh);
        if car w=3 then <<hh:=get(cadr w,'fct_nli_lin);
                          hh:=delete(caddr w,hh);
                          put(cadr w,'fct_nli_lin,hh)
                        >> else
        if car w=4 then <<hh:=get(cadr w,'fct_nli_nca);
                          hh:=delete(caddr w,hh);
                          put(cadr w,'fct_nli_nca,hh)
                        >> else
        if car w=5 then <<hh:=get(cadr w,'fct_nli_nli);
                          hh:=delete(caddr w,hh);
                          put(cadr w,'fct_nli_nli,hh)
                        >> else
        if car w=6 then <<hh:=get(cadr w,'fct_nli_nus);
                          hh:=delete(caddr w,hh);
                          put(cadr w,'fct_nli_nus,hh)
                        >>
                      >>;
      if null hh and
         null get(cadr w,'fcteval_lin) and
         null get(cadr w,'fcteval_nca) and
         null get(cadr w,'fcteval_nli) then remflag1(cadr w,'to_eval)
      % otherwise 'fcteval_lin,... will be reassigned
    >>;
    if (h='s) then l1:=nil;
    if (h='n) or (h='s) then t else nil
  >> then goto again
     else
  if (   car w = 1)                             or
     ((lin_subst=nil)                     and
      ( (car w = 2)                  or
       ((car w > 2)            and
        member(caaddr w,ineq_)     )    )     ) then <<

   if pdelimit and in_cycle({'subst,cdaddr w,get(cadr w,'printlength)})
                                 % function, printlength of equation
   then plim:=nil
   else plim:=pdelimit;
   l:=do_subst(car w,cadr w,caddr w,pdes,ftem_,forg,vl,plim,keep_eqn)$
   if l and null car l then << % not contradiction but not used
    l1:=delete(cadr w,l1);
    if l1 then <<
     pdes:=cadr l;
     forg:=caddr l;
     l:=nil;
     goto again
    >>    else l:=nil
   >>;
   if l then <<
    l:=cdr l;
    add_to_last_steps({'subst,cdaddr w,get(cadr w,'printlength)})
   >>
  >>                                            else
  if (null lin_subst) and (null no_cases) then <<
    md:=car w;   % md = type of substitution, needed in case of ISE
    p:=cadr  w;  % p = the equation
    w:=caddr w;  % w = (coeff . function)
    if pdelimit and in_cycle({'subst,w,get(p,'printlength)}) % (eqn,function)
    then pdelimit:=nil;
    % make an equation from the coefficient
    q:=mkeq(car w,get(p,'fcts),get(p,'vars),allflags_,t,list(0),nil,pdes)$
    % and an equation from the remainder
    r:=mkeq(list('PLUS,get(p,'val),
                 list('TIMES,car w,
                             list('MINUS,cdr w))),
            get(p,'fcts),get(p,'vars),allflags_,t,list(0),nil,pdes)$
    if contradiction_ then <<
      if print_ then <<
       write"Therefore no special investigation whether the "$
       terpri()$
       write"coefficient of a function to be substituted is zero."$
      >>$
      contradiction_:=nil$
      h:=get(q,'val)$
      if pairp h and (car h='TIMES) then ineq_:=union(cdr  h,ineq_)
                                    else ineq_:=union(list h,ineq_)$
      drop_pde(q,nil,nil)$
      drop_pde(r,nil,nil)$
      l:=do_subst(md,p,w,pdes,ftem_,forg,vl,pdelimit,keep_eqn)$

      if l and null car l then << % not contradiction but not used
       l1:=delete(p,l1);
       if l1 then <<
        pdes:=cadr l;
        forg:=caddr l;
        l:=nil;
        goto again
       >>
      >>;
      if l then <<
       l:=cdr l;
       add_to_last_steps({'subst,cdr w,get(p,'printlength)})
      >>

    >>                else <<
%      cop:=backup_pdes(pdes,forg)$
      backup_to_file(pdes,forg,nil)$
      remflag1(p,'to_eval)$
      if print_ then <<
        terpri()$
        write "for the substitution of ",cdr w," by ",p$
        write " we have to consider the case 0=",q,": "$
        eqprint list('EQUAL,0,car w)
      >>$
      pdes:=eqinsert(q,drop_pde(p,pdes,nil))$
      if freeof(pdes,q) then <<
        terpri()$
        write "It turns out that the coefficient of ",cdr w," in ",
              p," is zero due"$
        terpri()$
        write "to other equations. Therefore no substitution is made and"$
        terpri()$
        write "equation ",p," will be updated instead."$
        terpri()$
%        pdes:=car restore_pdes(cop)$
        % cop:=backup_ongoing(l1)$   % not needed here
        h:=restore_backup_from_file(pdes,forg,nil)$
        pdes:= car h;
        forg:=cadr h;
        delete_backup()$
        % restore_ongoing(cop)$      % not needed here
        % cop:=nil;
        update(p,reval list('PLUS,get(p,'val),
                                  list('TIMES,car w,
                                              list('MINUS,cdr w))),
               get(p,'fcts),get(p,'vars),t,list(0),pdes)$
        drop_pde_from_idties(p,pdes,nil)$ % new history is nil as r has no history
        drop_pde_from_properties(p,pdes)$
        drop_pde(q,pdes,nil); % q is not in pdes but nevertheless
        drop_pde(r,pdes,nil); % r is not in pdes but nevertheless
        l:=list(pdes,forg,p)
      >>                else <<
        pdes:=eqinsert(r,pdes)$
        if print_ then <<
          write"The coefficient to be set = 0 in the first subcase is:"$
          %h:=print_all;          print_all:=t;
          hh:=print_;            print_:=300;
          typeeqlist(list q);
          %print_all:=h;
          print_:=hh
        >>$
        to_do_list:=cons(list('subst_level_35,%pdes,forg,vl_,
                              list q),
                         to_do_list)$
        level_:=cons(1,level_)$
        if print_ then print_level(t)$
        h:=get(q,'val)$    % to add it to ineq_ afterwards
        recycle_fcts:=nil$
        l:=if pvm_try() and (null collect_sol)
        then remote_crackmain(pdes,forg) % i.e. l:=nil
        else crackmain(pdes,forg)$
%        for each sol in l do
%        if sol then <<
%          for each f in caddr sol do
%          if hh:=assoc(f,depl!*) then newfdep:=cons(hh,newfdep);
%        >>;

        hh:=restore_and_merge(l,pdes,forg)$
        pdes:= car hh;
        forg:=cadr hh; % was not assigned above as it has not changed probably
        delete_backup()$

        level_:=cons(2,level_)$
        if print_ then <<
          print_level(t)$
          terpri()$
          write "now back to the substitution of ",cdr w," by ",p$
        >>$
%        pdes:=car restore_pdes(cop)$
%        depl!*:=append(depl!*,newfdep);
        addineq(pdes,h);
        % If the value of p was = q*f then q is now dropped, i.e.
        % car w is not anymore the coefficient of f in p
        % --> new determination of car w
        w:=reval coeffn(get(p,'val),cdr w,1) . cdr w;
        drop_pde(q,nil,nil);
        drop_pde(r,nil,nil);

        if contradiction_ or null l then <<
            contradiction_:=nil$
            l:=do_subst(md,p,w,pdes,ftem_,forg,vl,pdelimit,keep_eqn)$

            if l and null car l then << % not contradiction but not used
              l1:=delete(p,l1);
              if l1 then <<
                pdes:=cadr l;
                forg:=caddr l;
                l:=nil;
                goto again
              >>
            >>;
            if l then <<
              l:=cdr l;
              add_to_last_steps({'subst,cdr w,get(p,'printlength)})
            >>

        >>                else <<
          % To avoid a loop the picked w='fcteval_nli is now stored as
          % w='fcteval_nca
          if md>2 then <<
            h:=get(p,'fcteval_nli)$
            if member(w,h) then << % otherwise p had just one term
              % where the non-zero coefficient was a factor which
              % is dropped by now, i.e. no further fix needed.
              % More generally, in addineq() and update_fcteval()
              % the following should be unnecessary by now
              h:=delete(w,h)$
              put(p,'fcteval_nli,h)$
              put(p,'fcteval_nca,cons(w,get(p,'fcteval_nca)))$
              if md=3 then <<
                h:=get(p,'fct_nli_lin)$
                h:=delete(w,h)$
                put(p,'fct_nli_lin,h)$
              >>      else
              if md=4 then <<
                h:=get(p,'fct_nli_nca)$
                h:=delete(w,h)$
                put(p,'fct_nli_nca,h)$
              >>      else
              if md=5 then <<
                h:=get(p,'fct_nli_nli)$
                h:=delete(w,h)$
                put(p,'fct_nli_nli,h)$
              >>      else
              if md=6 then <<
                h:=get(p,'fct_nli_nus)$
                h:=delete(w,h)$
                put(p,'fct_nli_nus,h)$
              >>
            >>
          >>$
          cases_:=t$
%          cop:=nil;     % to save memory
          % no backup of global data
          h:=if pvm_try() and (null collect_sol)
             then remote_crackmain(pdes,forg) % i.e. h:=nil
             else crackmain(pdes,forg)$
          % No recovery of global data because this crackmain will end now too.
          % Because no data are changed, computation could just continue
          % without crackmain() sub-call but then combining the
          % different results would be difficult.
          % No delete_backup() as this has already been done.
          if contradiction_ then contradiction_:=nil
                            else l:=union(h,l)
        >>
      >>
    >>$
  >>$
  if null !*batch_mode and null expert_mode and confirm_subst then
  while tempchng do <<
    w:=car tempchng; tempchng:=cdr tempchng;
    if car w=1 then <<hh:=get(cadr w,'fcteval_lin);
                      hh:=cons(caddr w,hh);
                      put(cadr w,'fcteval_lin,hh)>> else
    if car w=2 then <<hh:=get(cadr w,'fcteval_nca);
                      hh:=cons(caddr w,hh);
                      put(cadr w,'fcteval_nca,hh)>> else
                    <<hh:=get(cadr w,'fcteval_nli);
                      hh:=cons(caddr w,hh);
                      put(cadr w,'fcteval_nli,hh);
      if car w=3 then <<hh:=get(cadr w,'fct_nli_lin);
                        hh:=cons(caddr w,hh);
                        put(cadr w,'fct_nli_lin,hh)>> else
      if car w=4 then <<hh:=get(cadr w,'fct_nli_nca);
                        hh:=cons(caddr w,hh);
                        put(cadr w,'fct_nli_nca,hh)>> else
      if car w=5 then <<hh:=get(cadr w,'fct_nli_nli);
                        hh:=cons(caddr w,hh);
                        put(cadr w,'fct_nli_nli,hh)>> else
      if car w=6 then <<hh:=get(cadr w,'fct_nli_nus);
                        hh:=cons(caddr w,hh);
                        put(cadr w,'fct_nli_nus,hh)>>
                    >>;
    flag1(cadr w,'to_eval)
  >>$

  return if contradiction_  then nil % list(nil,nil)
                            else if cases_ then list l
                                           else l$
end$

symbolic procedure best_fac_pde(pdes)$
% pdes must be pdes for which their 'val property has form {'TIMES,...}
begin scalar p,md,mdgr,mtm,f,dgr,f,tm,bestp;
 md:=1000; mtm:=100000;
 for each p in pdes do <<
  % compute the max degree of any factor
  mdgr:=0$
  for each f in cdr get(p,'val) do <<
   dgr:=pde_degree(f,smemberl(get(p,'rational),f))$
   if dgr>mdgr then mdgr:=dgr
  >>$
  tm:=get(p,'length)$
  if (mdgr<md) or ((mdgr=md) and (tm<mtm)) then
  <<bestp:=p; md:=mdgr; mtm:=tm>>;
 >>;
 return {bestp,md,mtm}
end$

algebraic procedure start_let_rules$
begin scalar ruli;
  lisp (oldrules!*:=nil)$  % to fix a REDUCE bug
  ruli:={};
  let explog_$
  if lisp(userrules_) neq {} then let lisp userrules_$
  if sin(!%x)**2+cos(!%x)**2 neq 1         then <<ruli:=cons(1,ruli);let trig1_>> else ruli:=cons(0,ruli)$
  if cosh(!%x)**2 neq (sinh(!%x)**2 + 1)   then <<ruli:=cons(1,ruli);let trig2_>> else ruli:=cons(0,ruli)$
  if sin(!%x)*tan(!%x/2)+cos(!%x) neq 1    then <<ruli:=cons(1,ruli);let trig3_>> else ruli:=cons(0,ruli)$
  if sin(!%x)*cot(!%x/2)-cos(!%x) neq 1    then <<ruli:=cons(1,ruli);let trig4_>> else ruli:=cons(0,ruli)$
  if cos(2*!%x) + 2*sin(!%x)**2   neq 1    then <<ruli:=cons(1,ruli);let trig5_>> else ruli:=cons(0,ruli)$
  if sin(2*!%x) neq 2*cos(!%x)*sin(!%x)    then <<ruli:=cons(1,ruli);let trig6_>> else ruli:=cons(0,ruli)$
  if sinh(2*!%x) neq 2*sinh(!%x)*cosh(!%x) then <<ruli:=cons(1,ruli);let trig7_>> else ruli:=cons(0,ruli)$
  if cosh(2*!%x) neq 2*cosh(!%x)**2-1      then <<ruli:=cons(1,ruli);let trig8_>> else ruli:=cons(0,ruli)$
  if sqrt(!%x*!%y) neq sqrt(!%x)*sqrt(!%y) then <<ruli:=cons(1,ruli);let sqrt1_>> else ruli:=cons(0,ruli)$
  if sqrt(!%x/!%y) neq sqrt(!%x)/sqrt(!%y) then <<ruli:=cons(1,ruli);let sqrt2_>> else ruli:=cons(0,ruli)$
  return ruli;
end$

algebraic procedure stop_let_rules(ruli)$
begin
  clearrules explog_$
  if (lisp(userrules_) neq {}) and
     (not lisp (zerop reval {'DIFFERENCE,
                             car  cdadr userrules_,
                             cadr cdadr userrules_}))
                    then clearrules lisp userrules_$
  if first ruli = 1 then clearrules sqrt2_$ ruli:=rest ruli$
  if first ruli = 1 then clearrules sqrt1_$ ruli:=rest ruli$
  if first ruli = 1 then clearrules trig8_$ ruli:=rest ruli$
  if first ruli = 1 then clearrules trig7_$ ruli:=rest ruli$
  if first ruli = 1 then clearrules trig6_$ ruli:=rest ruli$
  if first ruli = 1 then clearrules trig5_$ ruli:=rest ruli$
  if first ruli = 1 then clearrules trig4_$ ruli:=rest ruli$
  if first ruli = 1 then clearrules trig3_$ ruli:=rest ruli$
  if first ruli = 1 then clearrules trig2_$ ruli:=rest ruli$
  if first ruli = 1 then clearrules trig1_$ ruli:=rest ruli$
end$


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%  procedures  for finding an optimal substitution  %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

symbolic procedure fbts(a,b)$
% fbts ... first better than second
(cadr   a <= cadr   b) and
(caddr  a <= caddr  b) and
(cadddr a <= cadddr b)$


symbolic procedure list_subs(p,fevl,fli,mdu)$
% p is an equation, fevl a substitution list of p,
% fli is a list of lists (f,p1,p2,..) where
%   f is a function,
%   pi are lists (eqn,nco,nte,mdu) where
%   eqn is an equation that can be used for substituting f
%   nco is the number of terms of the coefficient of f in the eqn
%   nte is the number of terms without f in the eqn
%   mdu is the kind of substitution (1:lin, 2:nca, 3:nli)
begin
 scalar a,f,nco,nte,cpy,cc,ntry;
 for each a in fevl do <<
  f:=cdr a;
  nco:=no_of_terms(car a);
  nte:=get(p,'terms);
  nte:=if nte=1 then 0
                else nte-nco$

  % Is there already any substitution list for f?
  cpy:=fli;
  while cpy and (f neq caar cpy) do cpy:=cdr cpy$
  ntry:={p,nco,nte,mdu}$

  if null cpy then fli:=cons({f,ntry},fli) % no, there was not
              else <<                      % yes, there was one
   cc:=cdar cpy$
   while cc and (null fbts(car cc,ntry)) do cc:=cdr cc$
   if null cc then << % ntry is at least in one criterium better
                      % than a known one
    rplaca(cpy,cons(f,cons(ntry,cdar cpy)));
    cc:=cdar cpy$ % just the list of derivatives with ntry as the first
    while cdr cc do
    if fbts(ntry,cadr cc) then rplacd(cc,cddr cc)
                          else cc:=cdr cc$
   >>
  >>
 >>;
 return fli
end$

algebraic procedure cwrno(n,r)$
% number of terms of (a1+a2+..+an)**r if ai are pairwise prime
% number of combinations of r factors out of n possible factors
% with repititions and without order = (n+r-1 over r)
<<n:=n+r-1;
  % The rest of the procedure computes binomial(n,r).
  if 2*r>n then k:=n-r;
  for i:=1:r product (n+1-i)/i
>>$

symbolic procedure besu(ic1,mdu1,ic2,mdu2)$
% Is the first substitution better than the second?
((mdu1<mdu2) and (ic1<=ic2)) or
((mdu1=mdu2) and (ic1< ic2)) or
% ########## difficult + room for improvement as the decision is
% actually dependent on how precious memory is
% (more memory --> less cases and less time):
((mdu1=2) and (ic1<(ic2+ 4))) or
((mdu1=3) and (ic1<(ic2+25)))$

symbolic procedure search_subs(pdes,sbpdes,cost_limit,no_cases)$
begin
 scalar fli,p,el,f,fpl,dv,drf,d,ffl,hp,ff,nco,be,s,nte,ic,fp,
        rm,mc,subli,mdu,tr_search,h$

 % at first find the list of all functions that could be substituted
 % using one of the equations sbpdes together with
 % a list of such sbpdes, the number of terms in the coeff and
 % the type of substitution

% tr_search:=t$

 for each p in sbpdes do fcteval(p,nil)$

 fp:=sbpdes;
 while fp                                             and
       ((get(car fp,'terms)>2)                     or
        (null (h:=get(car fp,'fcteval_lin)))
       ) do fp:=cdr fp;
 if fp then return {1,car fp,car get(car fp,'fcteval_lin)}$

 for each p in sbpdes do <<
  fli:=list_subs(p,get(p,'fcteval_lin),fli,1)$
  fli:=list_subs(p,get(p,'fcteval_nca),fli,2)$
  if null no_cases then fli:=list_subs(p,get(p,'fcteval_nli),fli,3)$
 >>$

 if tr_search then <<
  write"equations substitution: (eqn, no of coeff. t., no of other t., mdu)"$
  terpri()$
  for each el in fli do <<write el;terpri()>>$
 >>$

 if fli then
 if (null cdr   fli) and  % one function
    (null cddar fli) then % one equation, i.e. no choice
 return <<
  fli:=cadar fli;  % fli is now (eqn,nco,nte,mdu)
  mdu:=cadddr fli;
  {mdu,car fli,car get(car fli,if mdu = 1 then 'fcteval_lin else
                               if mdu = 2 then 'fcteval_nca else
                                               'fcteval_nli)     }
 >>                  else
 % (more than 1 fct.) or (only 1 function and more than 1 eqn.)
 for each el in fli do << % for any function to be substituted
                          % (for the format of fli see proc list_subs)

  f:=car el$ el:=cdr el$
  % el is now a list of possible eqn.s to use for subst. of f

  fpl:=nil$ % fpl will be a list  of lists (p,hp,a1,a2,..) where
            % p is an equation that involves f,
            % hp the highest power of f in p
            % ai are lists {ff,cdr d,nco} where ff is a derivative of f,
            % cdr d its power and nco the number of coefficients
  for each p in pdes do << % for each equation in which f could be subst.
   dv:=get(p,'derivs)$    %   ((fct var1 n1 ...).pow)
   drf:=nil$
   for each d in dv do
   if caar d = f then drf:=cons(d,drf)$
   % drf is now the list of powers of derivatives of f in p

   ffl:=nil$      % ffl will be a list of derivatives of f in p
                  % together with the power of f and number of
                  % terms in the coeff.
   if drf then << % f occurs in this equation and we estimate the increase
    hp:=0$
    for each d in drf do <<
     if cdar d then ff:=cons('DF,car d)
               else ff:=caar d;
     nco:=no_of_terms(coeffn(get(p,'val),ff,cdr d));
     if cdr d > hp then hp:=cdr d$
     ffl:=cons({ff,cdr d,nco},ffl);
    >>
   >>;

   if drf then fpl:=cons(cons(p,cons(hp,ffl)),fpl);
  >>$

  % now all information about all occurences of f is collected and for
  % all possible substitutions of f the cost will be estimated and the
  % cheapest substitution for f will be determined

  be:=nil; % be will be the best equation with an associated min. cost mc
  for each s in el do <<
   % for each possible equation that can be used to subst. for f

   % number of terms of (a1+a2+..+an)**r = n+r-1 over r
   % f = (a1+a2+..+a_nte) / (b1+b2+..+b_nco)
   nco:=cadr s;
   nte:=caddr s;
   ic:= - get(car s,'terms);  % ic will be the cost associated with
                              % substituting f by car s and car s
                              % will be dropped after the substitution
   for each fp in fpl do
   if (car s) neq (car fp) then <<
    rm:=get(car fp,'terms);   % to become the number of terms without f
    hp:=cadr fp;
    ic:=ic - rm;              % as the old eqn. car fp will be replaced

    for each ff in cddr fp do << % for each power of each deriv. of f
     ic:=ic + (caddr ff)*           % number of terms of coefficient of ff
              cwrno(nte,cadr ff)*      % (numerator of f)**(power of ff)
              cwrno(nco,hp - cadr ff); % (denom. of f)**(hp - power of ff)
     rm:=rm - caddr ff;       % caddr ff is the number of terms with ff
    >>;
    % Now all terms containing f in car fp have been considered. The
    % remaining terms are multiplied with (denom. of f)**hp
    ic:=ic + rm*cwrno(nco,hp)
   >>;

   % Is this substitution better than the best previous one?
   if (null be) or besu(ic,cadddr s,mc,mdu) then
   <<be:=car s; mc:=ic; mdu:=cadddr s>>;

  >>;

  % It has been estimated that the substitution of f using the
  % best eqn be has an additional cost of ic terms

  if tr_search and (length el > 1) then <<
   terpri()$
   write"Best substitution for ",f," : ",{ic,f,be,mdu}$
  >>$

  if (null cost_limit) or (ic<cost_limit) then
  subli:=cons({ic,mdu,f,be},subli)$
 >>$

 % Now pick the best substitution
 if subli then <<
  s:=car subli;
  subli:=cdr subli;
  for each el in subli do
  if besu(car el,cadr el,car s,cadr s) then s:=el$

  if tr_search then <<
   terpri()$
   write"Optimal substitution:"$terpri()$
   write"  replace ",caddr s," with the help of ",cadddr s,","$terpri()$
   if car s < 0 then write"  saving ", - car s," terms, "
                else write"  with a cost of ",car s," additional terms, "$
   terpri()$
   write if cadr s = 1 then "  linear substitution" else
         if cadr s = 2 then "  nonlinearity inceasing substitution" else
                            "  with case distinction"  $
  >>$

  el:=get(cadddr s,if (cadr s) = 1 then 'fcteval_lin else
                   if (cadr s) = 2 then 'fcteval_nca else
                                        'fcteval_nli);
  while (caddr s) neq (cdar el) do el:=cdr el;

  return {cadr s,cadddr s,car el}
     % = {mdu   ,p       ,car get(p,'fcteval_???)}
 >>$

end$

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%  procedures  for substitution of a derivative by a new function  %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

symbolic procedure check_subst_df(pdes,forg)$
%  yields a list of derivatives which occur in all
%  pdes and in forg
begin scalar l,l1,l2,n,cp,not_to_substdf$
 if pdes then <<
  for each s in pdes do l:=union(for each a in get(s,'derivs)
                                   collect car a,l)$    % all derivs
  for each s in forg do
   if pairp s then l:=union(for each a in all_deriv_search(s,ftem_)
                                    collect car a,l)$
  l1:=df_min_list(l)$
  l:=nil$
  for each s in l1 do
   if pairp s and not member(car s,not_to_substdf) then <<
    l:=cons(cons('DF,s),l)$
    not_to_substdf:=cons(car s,not_to_substdf)
  >> $

  % Derivatives of functions should only be substituted if the
  % function occurs in at least 2 equations or forg functions
  while l do <<
   n:=0; % counter
   cp:=pdes;
   while cp and (n<2) do <<
    if member(cadar l,get(car cp,'fcts)) then n:=add1 n;
    cp:=cdr cp
   >>;
   cp:=forg;
   while cp and (n<2) do <<
    if (pairp car cp) and (caar cp = 'EQUAL) and
       member(cadar l,get(cadr car cp,'fcts)) then n:=add1 n;
    cp:=cdr cp
   >>;
   if n=2 then l2:=cons(car l,l2);
   l:=cdr l
  >>
 >>$
 return l2$
end$

symbolic procedure df_min_list(dflist)$
%  yields the lowest derivative for each function in the list of
%  deriv. dflist.
%   e.g. dflist='((f x z) (g x) (g) (f y) (h x y) (h x z))
%   ==> result='(f g (h x))
if dflist then
begin scalar l,d,m,lmax$
while dflist do
 <<m:=car dflist$
   dflist:=cdr dflist$
   l:=nil$
   while dflist do
    <<if (d:=df_min(car dflist,m)) then m:=d
                                                            else l:=cons(car dflist,l)$
    dflist:=cdr dflist$
    >>$
   if pairp m and null cdr m then lmax:=cons(car m,lmax)
                             else lmax:=cons(m,lmax)$
   dflist:=l$
 >>$
return lmax$
end$

symbolic procedure df_min(df1,df2)$
%  yields the minimal derivative of d1,d2
%  e.g. df_min('(f x y),'(f x z))='(f x), df_min('(f x z),'(g x))=nil
<<if not pairp df1 then df1:=list df1$
if not pairp df2 then df2:=list df2$
if car df1=car df2 then
  if (df1:=df_min1(cdr df1,cdr df2)) then cons(car df2,df1)
                                     else car df2>>$

symbolic procedure df_min1(df1,df2)$
begin scalar l,a$
while df1 do
 <<a:=car df1$
 if not zerop (a:=min(dfdeg(df1,car df1),dfdeg(df2,car df1))) then
  l:=cons(car df1,l)$
 if a>1 then l:=cons(a,l)$
 df1:=cdr df1$
 if df1 and numberp car df1 then df1:=cdr df1>>$
return reverse l$
end$

symbolic procedure dfsubst_forg(p,g,d,forg)$
% substitute the function d in forg by an integral g
% of the function p
for each h in forg collect
  if pairp h and member(d,get(cadr h,'fcts)) then
          <<put(cadr h,'fcts,
                fctinsert(p,delete(d,get(cadr h,'fcts))))$
          reval subst(g,d,h)>>
  else h$

symbolic procedure expand_INT(p,varlist)$
if null varlist then p
else begin scalar v,n$
  v:=car varlist$
  varlist:=cdr varlist$
  if pairp(varlist) and numberp(car varlist) then
     <<n:=car varlist$
       varlist:=cdr varlist>>
  else n:=1$
  for i:=1:n do p:=list('INT,p,v)$
  return expand_INT(p,varlist)
end$

symbolic procedure substitution_weight(k,l,m,n)$
 % This function computes a weight for an equation to
 % be used for a substitution
 % k .. number of occurences as factor,
 % l .. total degree of factor as homogeneous polynomial,
 % m .. number of appearances in eqns,
 % n .. number of terms
reval {'QUOTIENT,{'TIMES,l,n},{'PLUS,k,m}}$

symbolic procedure rational_less(a,b)$
% a and b are two revalued rational numbers in prefix form
% It returns the boolean value of a<b
if (pairp a) and
   (car a='QUOTIENT) then rational_less(cadr a,reval{'TIMES,caddr a,b}) else
if (pairp b) and
   (car b='QUOTIENT) then rational_less(reval{'TIMES,caddr b,a},cadr b) else
if (pairp a) and (car a='MINUS) then
if (pairp b) and (car b='MINUS) then cadr a > cadr b
                                else not rational_less(cadr a,reval{'MINUS,b})
                                else
if (pairp b) and (car b='MINUS) then
if a<0 then not rational_less(reval{'MINUS,a},cadr b)
       else nil
                                else a<b$

symbolic procedure get_fact_pde(pdes,aim_at_subst)$
% look for pde in pdes which can be factorized
begin scalar p,pv,f,fcl,fcc,h,h1,h2,h3,h4,h5,h6,h7,h8,eql,tr_gf$
 % tr_gf:=t$

 % choose equation that minimizes a weight computed from the
 % weights of its factors,
 % the weight of a factor =
 % (if an atom then number of all equations it occurs
 %  else the number of equations it occurs as a factor)/
 %  the total degree of this factor/
 %  the number of factors of the equation
 % The factor with the highest weight is to be set to 0 first.

 % 1) collecting a list of all suitable equations eql and a list
 %    of all factors of any equation, listing for each factor
 %    in how many equations it appears
 for each p in pdes do <<
  pv:=get(p,'val)$
  if pairp pv and (car pv='TIMES) then <<
   pv:=cdr pv$  % drop 'TIMES to get the list of factors in p

   % increment the counter of appearances of each factor
   h1:=pv$
   while h1 do <<  % for each factor
    f:=car h1; h1:=cdr h1;

    fcc:=fcl$

    % fcl is list of lists
    %   (factor itself,
    %    no of occurences as factor,
    %    total degree of factor as homogeneous polynomial,
    %    number of appearances in eqns)

    while fcc and (caar fcc neq f) do fcc:=cdr fcc$

    if fcc then <<      % factor had already appeared
     h:=cons(f,cons(add1 cadar fcc,cddar fcc));
     rplaca(fcc,h);
    >>     else <<      % factor is new

     % Computing the total degree of the factor
     if homogen_ then <<
      h2:=algebraic find_hom_deg(f)$
      h2:=(cadr h2) + (caddr h2)
     >>          else h2:=1;

     % If it is a function then counting in how many equations it appears
     if atom f then << % count in how many equations f does occur
      h3:=0;           % the counter
      h4:=pdes;
      while h4 do <<
       if not freeof(get(car h4,'fcts),f) then h3:=add1 h3;
       h4:=cdr h4
      >>
     >>        else h3:=1$

     % The number of terms of f:
     h4:=if pairp f and (car f='PLUS) then length cdr f
                                      else 1$

     fcl:=cons({f,1,h2,h3,h4},fcl)
    >>
   >>$    % done for all factors

   % check whether each factor can be used for subst., i.e. whether
   % this equation should be factorized
   if null aim_at_subst then h:=1
                        else <<
    h:=get(p,'split_test);
    if null h then << % check all factors whether they can be used for subst.
     h1:=pv$  % the list of factors in p
     h4:=t$
     % make an equation from the coefficient
     while h1 and h4 do <<
      h3:=mkeq(car h1,get(p,'fcts),get(p,'vars),allflags_,t,list(0),nil,nil)$
      % the last argument is nil to avoid having a lasting effect on pdes
      h1:=cdr h1$
      fcteval(h3,nil)$
      if not(get(h3,'fcteval_lin) or get(h3,'fcteval_nca)) then h4:=nil;
      drop_pde(h3,nil,nil)
     >>$
     h:=if h4 then 1  % p can be splited into substitutable equations
              else 0; % p can not be splited into only " equations
     put(p,'split_test,h)
    >>
   >>;

   % adding the equation to the ones suited for factorization
   if not zerop h then
   eql:=cons(p,eql)

  >>
 >>$  % looked at all factorizable equations

 % Anything worth factorizing?
 if null eql then return nil;

 % Now that it is known how often each factor appears in all equations,
 % each factor can be given a weight and each equation be given a weight

 h2:=nil;    % h2 is the best equation, its weight will be h3 and the
             % factors of the best equation sorted by weight will be h4
             % in the new order they will be set to zero

 for each p in eql do <<
  pv:=cdr get(p,'val)$  % cdr to drop 'TIMES
  h8:=length pv$        % number of factors of p
  h5:=nil;              % the list of factors of p with their weight
  h6:=0;                % the weight of equation p
  while pv do <<
   h:=assoc(car pv,fcl);
   if tr_gf then << write "h assoc= ",h$terpri()>>$
   h7:=substitution_weight(cadr h,caddr h,cadddr h,car cddddr h);
   h5:=cons(cons(h7,car h),h5);
   h6:={'PLUS,h6,h7};
   pv:=cdr pv
  >>$
  if flin_ and not freeoflist(get(p,'fcts),flin_) then
  h6:={'TIMES,10,h6};   % evaluating flin_ functions has lower priority
                        % as they are fewer (in bi-lin alg problems)
  h6:=reval {'TIMES,{'EXPT,2,h8},h6};     % punishment of many factors
  if null h2 or rational_less(h6,h3) then <<
   h2:=p;
   h3:=h6;
   h4:=h5
  >>
 >>$

 % simplifying weights for the rat_idx_sort call
 h4:=rat_idx_sort for each a in h4 collect cons(reval car a,cdr a);

 % Putting the flin_ factor last is bad if this factor comes up in
 % many equations, like in the case of bi-linear systems when at the
 % end only one flin_ function is left being a factor of all equations
 %
 %if flin_ then <<
 % h5:=h4;       % car h5 will be the factor involving flin_ functions
 % while h5 and freeoflist(cdar h5,flin_) do h5:=cdr h5;
 % if h5 then h4:=append(delete(car h5,h4),list car h5)
 %>>$
 put(h2,'val,cons('TIMES,for each a in h4 collect cdr a))$
 return h2
end$


endmodule$

end$

symbolic procedure get_fact_pde(pdes,aim_at_subst)$
% look for pde in pdes which can be factorized
begin scalar p,pv,f,fcl,fcc,h,h1,h2,h3,h4,me,bp,best_fac,
      fewest_factor_pdes,flin_free$
 % collecting all factors in all equations and the equations in
 % which each factor appears {{f1,e_4,e_9},{f2,e_7,e_3},...}
 for each p in pdes do <<
  pv:=get(p,'val)$
  if pairp pv and (car pv='TIMES) then <<
   if null aim_at_subst then h:=1
                        else <<
    h:=get(p,'split_test);
    if null h then << % check all factors whether they ca be used for subst.
     h1:=cdr pv$  % the list of factors in p
     h4:=t$
     % make an equation from the coefficient
     while h1 and h4 do <<
      h3:=mkeq(car h1,get(p,'fcts),get(p,'vars),allflags_,t,list(0),nil,nil)$
      % the last argument is nil to avoid having a lasting effect on pdes
      h1:=cdr h1$
      fcteval(h3,nil)$
      if not(get(h3,'fcteval_lin) or get(h3,'fcteval_nca)) then h4:=nil;
      drop_pde(h3,nil,nil)
     >>$
     h:=if h4 then 1  % p can be splited into substitutable equations
              else 0; % p should not be splited into " equations
     put(p,'split_test,h)
    >>
   >>;
   if not zerop h then <<
    pv:=cdr pv$  % the list of factors in p
    for each f in pv do <<
     % updating how often f has occured as factor
     fcc:=fcl$
     while fcc and (caar fcc neq f) do fcc:=cdr fcc$
     if fcc then <<
      h1:=length pv;
      if null fewest_factor_pdes or (h1=h2) then <<
       fewest_factor_pdes:=cons(p,fewest_factor_pdes)$
       h2:=h1
      >>                                    else
      if h1<h2 then <<
       fewest_factor_pdes:=list p;
       h2:=h1
      >>$

      h:=cons(f,
         if h1=2 then cons(p,cdar fcc) else
         if h1=3 then if cddar fcc then cons(cadar fcc,cons(p,cddar fcc))
                                   else cons(p,cdar fcc)
                 else append(cdar fcc,list p)
             );
      rplaca(fcc,h);
     >>     else fcl:=cons({f,p},fcl);
    >>
   >>
  >>
 >>$

 if flin_ then <<
  % If there is a set flin_ of linear functions whose linearity is to be
  % preserved as long as possible then do not choose a factor of flin_
  % if there is any such factor.
  h:=fcl;
  while h and not freeoflist(caar h,flin_) do h:=cdr h;
  if h then << % There are factors without flin_ functions --> drop all
                % factors with flin_ functions
   h:=fcl;
   fcl:=nil;
   for each p in h do
   if freeoflist(car p,flin_) then fcl:=cons(p,fcl)
  >>
 >>;

 % Selection of the best pair (function . equation)
 % List of priorities:
 % - the factor is of lowest possible degree

 h:=nil;
 h2:=nil;
 while fcl do <<
  h1:=pde_degree(caar fcl,ftem_)$
  if (null h) or (h1=h2) then <<if null h then h2:=h1;
                                h:=cons(car fcl,h)>>   else
  if h1<h2 then <<h2:=h1; h:=list car fcl>>$
  fcl:=cdr fcl
 >>$
 fcl:=h$

 % - the equation has the lowest number of factors, i.e. dropping all
 %   factors that are not also factors to a pde in fewest_factor_pdes
 %   if there is such a PDE left

 if flin_ then <<
  for each p in fewest_factor_pdes do
  if (homogen_ and zerop car get(p,'hom_deg)) or
     freeoflist(get(p,'fcts),flin_) then flin_free:=cons(p,flin_free);
  if flin_free then fewest_factor_pdes:=flin_free
 >>$

 h:=nil;
 for each h1 in fcl do <<
  if not freeoflist(h1,fewest_factor_pdes) then h:=cons(h1,h)$
 >>$
 if h then fcl:=h$

 % keep only factors which occur in the most equations
 % keep for each factor only one equation which has the
 % lowest max degree of its factors and has the fewest
 % number of terms in all its factors
 me:=0;      % the maximum number of equations a factor turns up
 while fcl do <<
  h:=length car fcl$
  if h > me then <<
   me:=h$
   best_fac:=cons(caar fcl,best_fac_pde(cdar fcl))
  >>        else
  if h = me then <<
   % which pde is better: cadr best_fac or best_fac_pde(cdar fcl)?
   bp:=best_fac_pde(cdar fcl)$
   if ( cadr  bp < caddr  best_fac      ) or
      ((cadr  bp = caddr  best_fac) and
       (caddr bp < cadddr best_fac)     ) then
   best_fac:=cons(caar fcl,bp)
  >>;
  fcl:=cdr fcl
 >>$

 %best_fac is now a list of dotted pairs (factor_f . best_eqn_with_f_as_factor)
 return if (null best_fac) or (me=0) then nil
                                     else <<
  h1:=nil; h2:=nil;
  h:=cdr get(cadr best_fac,'val)$
  if flin_ then <<
   for each p in h do if freeoflist(p,flin_) then h1:=cons(p,h1)
                                             else h2:=cons(p,h2);
   h:=append(h1,h2)
  >>$
  put(cadr best_fac,'val,cons('TIMES,cons(car best_fac,
                                          delete(car best_fac,h))))$
  cadr best_fac
 >>
end$

