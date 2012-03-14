%********************************************************************
module simpsolution$
%********************************************************************
%  Routines for simplifying expressions by changing free functions
%  and constants of integration
%  Author: Thomas Wolf
%  Nov 1993

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


symbolic operator dropredundant$

symbolic procedure dropredundant(ex,fl,vl,unequ)$
comment
 All arguments are algebraic, ex is the list of expressions or
 equations from which the right side is taken, fl is the list of
 functions to be sorted out, vl the list of all extra independent variables,
 not already included in fl.
 returns algebraic list of redundant functions/const.=0, new EX, new FL;
begin scalar a;
 vl:=union(reverse argset cdr fl,cdr vl)$
 if null ftem_ then ftem_:=cdr fl$
 a:=dropredund(list(list(nil),cdr ex,cdr fl,cdr unequ),nil,vl);
 return if a then list('LIST,cons('LIST,car    a),
                             cons('LIST,caddr  a),
                             cons('LIST,cadddr a) )
             else nil
end$

symbolic procedure del_redundant_fc(arglist)$
% prepares a call of dropredund() from within a crack run
begin
 scalar p,f,fli,nofl,fred,redu,dropped,oldpdes,newpdes,newpval,bak,prolibak$
 bak:=backup_pdes(car arglist,cadr arglist)$
 prolibak:=proc_list_;
 proc_list_:=default_proc_list_;
 if null ftem_ then ftem_:=cadr arglist$

 for each f in cadr arglist do
 if not pairp f then nofl:=cons(f,nofl) else
 if car f='EQUAL then fli:=cons(f,fli)$
 fred:=setdiff(ftem_,nofl);
 if not !*batch_mode then <<
  write"Which functions shall be checked for redundancy? "$
  f:=select_from_list(fred,nil)$
  if f then <<nofl:=append(nofl,setdiff(fred,f)); fred:=f>>;
 >>$

 redu:=
 dropredund({for each p in car  arglist collect get(p,'val),
             fli,
             fred,
             ineq_},
            nofl,vl_);
 oldpdes:=car restore_pdes(bak)$
 proc_list_:=prolibak;
 if redu and car redu then <<
  for each f in car redu do <<
   dropped:=cons(cadr f,dropped)$
   % none of the dropped functions is in forg so all can be put in:
   drop_fct(cadr f)
  >>$
  ftem_:=setdiff(ftem_,dropped);
  newpval:=cadr redu$
  while newpval do <<
   newpdes:=if get(car oldpdes,'val) = car newpval
            then eqinsert(car oldpdes,newpdes) % cons(car oldpdes,newpdes)
            else <<for each f in allflags_ do flag1(car oldpdes,f)$
                   p:=update(car oldpdes,car newpval,get(car oldpdes,'fcts),
                             get(car oldpdes,'vars),t,{0},newpdes)$
                   if null p then <<
                    drop_pde_from_idties(car oldpdes,car arglist,nil);
                    drop_pde_from_properties(car oldpdes,car arglist)
                   >>        else eqinsert(p,newpdes) % cons(p,newpdes)
                 >>;
   newpval:=cdr newpval;
   oldpdes:=cdr oldpdes
  >>$

  % delete the dropped functions in the property list of the forg functions
  for each f in dropped do
  for each p in caddr redu do <<
   if (pairp p) and (car p='EQUAL) then
   put(cadr p,'fcts,delete(f,get(cadr p,'fcts)))
  >>$

  return {newpdes,append(caddr redu,setdiff(nofl,setdiff(nofl,cadr arglist)))}
  % appending only those nofl elements which have been in forg=cadr arglist
 >>
end$

symbolic procedure dropredund(a,nofl,vl)$
begin
 scalar sol,arbit,fl,el1,el2,el3,corres,b,b1,b2,condi,oldcon,
        redund,flstart,fldrop,flnew,newfu,fnew_,newcorres,unequ,
        vlf,vla,potold,newnewfu,todelete,nofl_arbit,ineq_bak,vl_bak,
        ftem_bak,proc_list_bak,session_old,adjust_fnc_bak,level_bak,
        collect_sol_bak$%,printold,batch_mode_old
 % a has the structure of one solution of CRACK in symbolic mode,
 % makes a copy of the free constants and functions in the solution
 % nofl is a list of functions not to be modified or dropped
 % sets to zero the difference between the old solution and the new
 % with replaced constants and functions and finds the non-essential
 % returns  cons(list of redundant fnc./cons=0,new solution)
 if cadr  a then if length cadr  a > 0 then
 if caddr a then if length caddr a > 0 then <<
  %printold:=print_;
  %print_:=nil;
  %batch_mode_old:=!*batch_mode;
  %!*batch_mode:=t;
  if not !*batch_mode then <<
   write"-------------------------------------------------------------"$
   terpri()$
   write" A new CRACK computation starts to find redundand functions. "$
   terpri()$
  >>$
  for each el1 in append(car a,cadr a) do
  if el1 then b1:=cons(if pairp el1 then
                       if car el1 = 'EQUAL then caddr el1
                                           else el1
                                    else el1             ,b1);
  % b1 is the list of expressions to be invariant
  b2:=b1;
  % arbit is the list of original free functions in the input solution
  arbit:=caddr a;

  % flstart is the list of functions which can be gauged and which
  %         turn up in the invariant expressions including the
  %         duplicates of these functions
  % fldrop  is the list of functions which can be dropped and do not
  %         turn up
  % todelete is a list of all new duplicate-functions
  % b2      is a duplicate of the list of invariant expressions b1
  % flnew   is a duplicate of todelete
  % fl      is the list of all functions
  % corres  is a list of correspondences of functions and their dupl.
  for each el1 in arbit do
  if not freeof(nofl,el1) then nofl_arbit:=cons(el1,nofl_arbit)
                          else
  if not my_freeof(b1,el1) then <<
   flstart:=cons(el1,flstart);
   el2:=newfct(fname_,fctargs(el1),nfct_)$
   todelete:=cons(el2,todelete);
   nfct_:=add1 nfct_$
   b2:=subst(el2,el1,b2);
   flnew:=cons(el2,flnew);
   fl:=cons(el1,cons(el2,fl));
   corres:=cons((el1 . el2),corres);
  >>                       else
  fldrop:=cons(el1,fldrop);

  % condi is the set of conditions: difference of related expressions=0
  while b1 do <<
   condi:=cons(reval list('PLUS,car b1,list('MINUS,car b2)),condi);
   b1:=cdr b1;
   b2:=cdr b2
  >>;
  b1:=nil;b2:=nil;
  fnew_:=nil;
  potold:=potint_;
  potint_:=nil;
  session_old:=session_;
  orderings_:=mkvect(1)$
  putv(orderings_,0,list(vl,fl,'default_ordering_function))$
  ineq_bak:=ineq_;    ineq_:=nil;  % temporarily
  vl_bak  :=vl_;      vl_  :=vl;
  ftem_bak:=ftem_;
  level_bak:=level_;  level_:=nil;
  for each b in flnew do ftem_:=fctinsert(b,ftem_)$
  if not freeof(proc_list_,'stop_batch) then <<
   proc_list_bak:=proc_list_;
   proc_list_:=delete('stop_batch,proc_list_)
  >>;
  adjust_fnc_bak:=adjust_fnc; adjust_fnc:=nil;
  collect_sol_bak:=collect_sol$collect_sol:=t$
  b:=crackmain(mkeqlist(condi,fl,vl_,allflags_,t,
                        orderings_prop_list_all(),nil),fl);
  collect_sol:=collect_sol_bak$
  adjust_fnc:=adjust_fnc_bak;  level_:=level_bak;
  ineq_:=ineq_bak;    vl_:=vl_bak;    ftem_:=ftem_bak$ % temporarily
  if proc_list_bak then proc_list_:=proc_list_bak;
  % a solution without inequalities
  %            without remaining equations
  % where each right hand side contains at least one fl-function
  for each b1 in b do
  if (not cadddr b1) and (not car b1) then <<
   el1:=t;
   for each el2 in cadr b1 do % for each computed assignment
   if (pairp el2) and
      (car el2='EQUAL) and
      (null smemberl(fl,caddr el2)) and
      (null smemberl(caddr b1,caddr el2)) then el1:=nil;
   if el1 then b2:=cons(b1,b2);
  >>$
  potint_:=potold;
  session_:=session_old;
  %print_:=printold;
  %!*batch_mode:=batch_mode_old;
  if not !*batch_mode then <<
   terpri()$
   write" The CRACK computation to find redundand functions finished."$terpri()$
   write"------------------------------------------------------------"$terpri()$
  >>$
  if null b2 then return <<
   for each el1 in append(todelete,fldrop) do
   drop_fct(el1)$ % depl!*:=delete(assoc(el1,depl!*),depl!*)$
   if null fldrop then nil
                  else <<

    redund:=for each el1 in fldrop collect list('EQUAL,el1,0);

    oldcon:=car a;
    for each el1 in fldrop do oldcon:=subst(0,el1,oldcon);
    oldcon:=for each el1 in oldcon collect reval el1;

    sol:=cadr a;
    for each el1 in fldrop do sol:=subst(0,el1,sol);
    sol:=for each el1 in sol collect reval el1;

    unequ:=cadddr a;
    for each el1 in fldrop do unequ:=subst(0,el1,unequ);
    unequ:=for each el1 in unequ collect reval el1;

    list(redund,oldcon,sol,union(nofl_arbit,flstart),unequ)
   >>
  >>         else b:=car b2;
  arbit:=caddr b;        % arbit are the free functions of the CRACK run
                         % newfu are the solved functions
  for each el1 in cadr b do
  if not((pairp el1       ) and
         (car el1 = 'EQUAL)     ) then arbit:=cons(el1,arbit)
                                  else newfu:=cons(el1,newfu)$
  oldcon:=car a;
  sol:=cadr a;
  unequ:=cadddr a;

  % flstart are the remaining essential free functions
  % redund are the functions to be dropped, they are set to 0 in
  % the old solution
  for each el1 in corres do
  if member(car el1,arbit) and member(cdr el1,arbit) then <<
   redund:=cons(list('EQUAL,car el1,0),redund);
   fldrop:=cons(car el1,fldrop);
   % the function and its copy are both not essential
   oldcon:=for each el2 in oldcon collect reval subst(0,car el1,el2);
   sol:=for each el2 in sol collect <<
    if (pairp el2) and (car el2='EQUAL) then
    put(cadr el2,'fcts,delete(car el1,get(cadr el2,'fcts)));
    reval subst(0,car el1,el2)
   >>$
   unequ:=for each el2 in unequ collect reval subst(0,car el1,el2);
   arbit:=delete(car el1,arbit);
   arbit:=delete(cdr el1,arbit);
   fl:=delete(car el1,fl);
   fl:=delete(cdr el1,fl);
   flstart:=delete(car el1,flstart);
   flnew:=delete(cdr el1,flnew);
   newfu:=subst(0,car el1,newfu);
   newfu:=subst(0,cdr el1,newfu);
  >>                                                 else
  newcorres:=cons(el1,newcorres);

  % Eliminate from all equations the flnew function in terms of
  % the corresponding flstart function and possibly other terms
  % newnewfu becomes a list of substitutions of new functions
  % by expressions in old functions.
  while newfu do <<
   el1:=car newfu; % el1: evaluated function = expression
   el2:=cadr el1;  % el2: evaluated function
   b:=newcorres;   % the remaining correspondences
   while b and (el2 neq cdar b) do b:=cdr b;
   if b then       % el2 = cdar b is a new function
   if (not freeof(el1,caar b)) then newnewfu:=cons(el1,newnewfu)
                               else <<
    % The right hand side ex1 of equation el1: el2=ex1 does not
    % contain the old function, say f, which corresponds to the
    % new function el2
    % --> search for an equation car el3 in newfu of the form
    % f = ex2, then add el2=ex1+f-ex2 to newnewfu
    el3:=newfu;
    while el3 and (cadar el3 neq caar b) do el3:=cdr el3;
    if el3 then <<
     newnewfu:=cons(list('EQUAL,el2,reval list('PLUS,caddr el1,cadar el3,
                                         list('MINUS,caddar el3)     )),
                    newnewfu);
     newfu:=delete(car el3,newfu)
    >>     else newnewfu:=nil;  % means later that it can not be treated
   >>   else <<    % el2 is an old function
    % like in the case above, only that in order to add equations of
    % the form new_fct = expr in old_fcts can be added to newnewfu,
    % the equations has to be solved for new_fct
    b:=newcorres;  % the correspondences of the remaining functions
    while el2 neq caar b do b:=cdr b; % caar b is now el2 (old function)
    if (not freeof(el1,cdar b)) then  % image function of el2 is in el1
    % solving el1 for the image function cdar b of el2
    newnewfu:=cons(list('EQUAL,cdar b,reval list('PLUS,cdar b,el2,
                                           list('MINUS,caddr el1))
                       ),newnewfu)
                                else <<
     % add an equ. to el1 with (the pri-image function of el2) = ...
     el3:=newfu;
     while el3 and (cadar el3 neq cdar b) do el3:=cdr el3;
     if el3 then <<
      newnewfu:=cons(list('EQUAL,cdar b,
                          reval list('PLUS,caddar el3,cadr el1,
                               list('MINUS,caddr el1)      )),
                     newnewfu);
      newfu:=delete(car el3,newfu)
     >>     else newnewfu:=nil;  % means later that it can not be treated
    >>
   >>;
   newfu:=cdr newfu
  >>;
  newfu:=newnewfu;

  % test, whether each new function has exactly one substitution
  % and no new function appears on a rhs
  if length flnew = length newfu then
  while newnewfu and freeoflist(caddar newnewfu,flnew) do
  newnewfu:=cdr newnewfu;

  if newfu and (not newnewfu) then <<
   % now the conditions have really been solved for the new
   % functions, no new function is on the rhs

   % arbit are all free old and new functions after the above CRACK-run
   % fl are all functions at the start of the above CRACK-run
   % flnew are all remaining new functions
   % flstart are all the old functions
   % new arbit: all functions which came in only through the
   %            last CRACK run
   arbit:=setdiff(setdiff(union(arbit,fl),flnew),flstart);

   % rewriting the substitutions as: old fct = expr in old fcts
   newfu:=
   for each el1 in newfu collect <<
    b:=cadr el1;     % b is a new function
    el2:=newcorres;  % caar el2 the corresponding old function
    while b neq cdar el2 do el2:=cdr el2;
    list('EQUAL,caar el2,reval caddr el1)
   >>;

   % Specifying the functions in arbit which are free to get as many
   % as possible functions flstart to zero
   arbit:=fctsort(arbit)$  % to use the functions with most variables first
   for each el1 in arbit do <<
    vla:=fctargs el1; % variables of the function to be eliminated
    el2:=newfu;
    while el2 do
    if freeof(car el2,el1) then el2:=cdr el2
                           else <<
     vlf:=fctargs cadar el2;
     if (null not_included(vla,vlf)) and
        (null not_included(vlf,vla)) then <<
      % cadar el2 is a function that shall be made to zero
      % by a choice for el1

      % It is checked whether the arbitrary function el1 occurs only
      % linearly algebraically, so that it can be computed by
      % solving equation car el2
      b:=lderiv(caddar el2,el1,vla);
      if cdr b=1 then << % success!! cadar el2 can be set to zero!
       if (car b neq el1) and print_ then <<terpri()$
        write" It is assumed that the equation:";terpri()$
        deprint cddar el2 ;
        write" has always a solution in ",el1;terpri()$
        write" functions: ";
        el3:=append(flstart,arbit);
        b:=nil;
        while el3 do <<
         if not freeof(cddar el2,car el3) then b:=cons(car el3,b);
         el3:=cdr el3
        >>;
        fctprint b; b:=nil
       >>;
       redund:=cons(list('EQUAL,cadar el2,0),redund);
       fldrop:=cons(cadar el2,fldrop);
       oldcon:=for each el3 in oldcon collect reval subst(0,cadar el2,el3);
       sol:=for each el3 in sol collect <<
        if (pairp el3) and (car el3='EQUAL) then
        put(cadr el3,'fcts,delete(cadar el2,get(cadr el3,'fcts)));
        reval subst(0,cadar el2,el3)
       >>$
       unequ:=for each el3 in unequ collect reval subst(0,cadar el2,el3);
       flstart:=delete(cadar el2,flstart);
       newfu:=delete(car el2,newfu);
       el2:=nil;
      >>
     >>;
     if el2 then el2:=cdr el2
    >>
   >>;

   % substituting all remaining functions arbit in the substitutions
   % newfu to zero which are not already specified
   for each el1 in arbit do newfu:=subst(0,el1,newfu);
  >>;
  if fldrop and print_ then <<
   terpri()$
   write"non-essential dropped constant(s) or function(s): ";
   fctprint fldrop
  >>$
  for each el1 in append(todelete,fldrop) do
  depl!*:=delete(assoc(el1,depl!*),depl!*)$
  return
  if null fldrop then nil
                 else list(redund,oldcon,sol,union(nofl_arbit,flstart),unequ)
 >>
end$

symbolic operator ncontent$
symbolic procedure ncontent p$
% Return numeric content of expression p
% based on simpnprimitive in ezgcd.
<< p := simp!* p;
%   if polyzerop(numr p) then 0 else
   if p=('NIL . 1) then 0 else
   mk!*sq(numeric!-content numr p ./ numeric!-content denr p)
>>$

algebraic procedure absorbconst(exlist,flist)$
% absorbing numerical factors into free constants/functions of flist
% if the list of expressions in exlist is known to be linear in flist
% returns an algebraic list of substitutions to be done
begin
  scalar e1,e2,n,n1,n2,nu,sb,cs1,cs2,!*rational_bak;
  !*rational_bak:=!*rational;
  if !*rational then algebraic(off rational)$
  sb:={};
  for each e1 in flist do <<
    n1:=nil;
    % to make a change of sign at least one equation
    % must demand it which is cs1=t
    % and no equation must forbit it which is cs2=nil
    cs1:=nil; cs2:=t;
    for each e2 in exlist do <<
      n:=coeffn(e2,e1,1);
      if n neq 0 then <<
        % if at least one equation does not demand a change of
        % sign then no change of sign is made

        if (numberp n) and (n<0) then cs1:=t
                                 else
        if lisp pairp reval algebraic n then <<
          if part(n,0)='MINUS then cs1:=t
                              else
          if part(n,0)='QUOTIENT then
          <<nu:=part(n,1);
            if lisp( pairp reval algebraic nu) and
               (part(nu,0)='MINUS) then cs1:=t
                                   else
            if (numberp nu) and (nu<0) then cs1:=t
                                       else cs2:=nil
          >>                     else cs2:=nil
        >>;
        n:=ncontent(n);
        if n1=nil then <<n1:=num n; n2:=den n>>
                  else <<
          n1:=gcd(n1,num(n));
          n2:=n2*den(n)/gcd(n2,den(n))
        >>
      >>
    >>;
    if n1 and ((n1 neq 1) or (n2 neq 1)) then <<
      if cs1 and cs2 then n2:=-n2;
      sb:=cons(e1=e1*n2/n1 , sb);
    >>                                   else
    if cs1 and cs2 then sb:=cons(e1=-e1, sb);
  >>;
  if !*rational_bak then algebraic(on rational);
  return if sb={} then nil else sb
end$ % of absorbconst

algebraic procedure drop_const(oldsoln, vars, additive)$
comment
  oldsoln is the output of a CRACK call. In all solutions functions
  which are independent of all elements of vars are dropped from
  the list of free functions/constants and
  - set to zero if additive=t and they are additive or
  - set to 1 if additive=nil and they are multiplicative;

begin
  scalar soln, sl, fncn, h1, h2, newfl, vcopy, constnt, v, fcopy,f1,co,
         mcdold;
  soln := {};
  mcdold:=lisp !*mcd$
  off mcd;
  while oldsoln neq {} do <<
    sl := first oldsoln; oldsoln := rest oldsoln;
    fncn := second sl;
    h1 := third sl;
    newfl:={};
    for each h2 in h1 do <<
              % is h2 constant ?
      vcopy := vars;
      constnt := t;
      while constnt and (vcopy neq {}) do <<
        v := first vcopy; vcopy := rest vcopy;
        if not my_freeof(co,v) then constnt := nil
      >>;
      if constnt then
      if (not my_freeof(first sl, h2)) or my_freeof(fncn, h2)
      then constnt := nil;
      if constnt then <<
          % is the coefficient of h2 constant in all solved expressions
          % and occurs h2 only linear ?
        fcopy := fncn;
        while constnt and (fcopy neq {}) do <<
          f1 := rhs first fcopy; fcopy := rest fcopy;
          on mcd;
          co:=coeffn(f1,h2,1);
          if (not my_freeof(co,h2))                               or
             (     additive  and (not my_freeof(f1 - co*h2, h2))) or
             ((not additive) and ((f1 - co*h2) neq 0))
          then constnt := nil;
          off mcd;
          if constnt and additive then <<
            vcopy := vars;
            while constnt and (vcopy neq {}) do <<
              v := first vcopy; vcopy := rest vcopy;
              if not my_freeof(co,v) then constnt := nil
            >>
          >>
        >>
      >>;
      if constnt then if additive then fncn := sub(h2=0, fncn)
                                  else fncn := sub(h2=1, fncn)
                 else newfl := cons(h2, newfl)
    >>;
    soln := cons({first sl, fncn, newfl}, soln)
  >>;
  if mcdold then on mcd;
  return soln
end$ % of drop_const

algebraic procedure sol_define$
<<%********* In this procedure are the statements that specify the solution
%  %Example: Test whether s=hhh-y**2/t**2,  u=y/t eine Loesung it
%  %         wobei hhh=hhh(t)
%  depend hhh,t$
%  % returned is a list of vanishing expressions defining the solution
%  % to be tested, for example:
%  {s-(hhh-y**2/t**2),u-y/t}
>>$

symbolic procedure solution_check(arglist)$
% arglist=(pdes,forg,vl_,pdes)

% This procedure tests whether a solution that is defined in
% an external procedure sol_define() is still contained in the
% general solution of the currend system under investigation

begin scalar pdes,forg,a,fsub,solu,l1,batch_bak,session_bak$
 pdes:=car arglist$
 forg:=cadr arglist$
 batch_bak:=!*batch_mode$
 !*batch_mode:=t$
 backup_to_file(pdes,forg,nil)$  % with all pdes deleted
 solu:=algebraic sol_define()$
 for each a in forg do
 if (pairp a) and (car a='EQUAL) then fsub:=cons(a,fsub)$
 solu:=if null fsub then algebraic solu
                    else <<
  fsub:=cons('LIST,fsub);
  algebraic (sub(lisp fsub,solu))
 >>$

 level_:=cons(1,level_)$
 if print_ then <<
   print_level(t)$
   terpri()$write "CRACK is now called to check whether a given"$
   terpri()$write "solution is included in the current system."
 >>;
 % further necessary step to call crackmain():
 recycle_fcts:=nil$  % such that functions generated in the sub-call
                     % will not clash with existing functions
 pdes:=append(mkeqlist(cdr solu,ftem_,vl_,allflags_,t,list(0),pdes),pdes)$
 session_bak:=session_$
 session_:=nil$ % to prevent the saving of the solution of this side comput.
 l1:=crackmain(pdes,forg)$
 session_:=session_bak$
 if l1 and not contradiction_ then write"+++++ Solution IS included."
                              else write"+++++ Solution is NOT included."$
 terpri()$
 contradiction_:=nil$
 l1:=restore_backup_from_file(pdes,forg,nil)$
 % not needed: pdes:=car l1;  forg:=cadr l1;
 delete_backup()$
 !*batch_mode:=batch_bak
end$

endmodule$

end$
