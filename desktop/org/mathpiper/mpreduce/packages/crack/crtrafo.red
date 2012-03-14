%********************************************************************
module transform$
%********************************************************************
%  Routines for performing transformations
%  Author: Thomas Wolf
%  March 1999
%

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


symbolic procedure input_trafo$
begin scalar ulist,vlist,u,v,ylist,xlist,yslist,xslist,oldprompt,xl2,
             notallowed,full_simplify$
 oldprompt:=promptstring!*$
 promptstring!*:=""$

 write"Under the following conditions this program performs arbitrary"$
 terpri()$
 write"transformations."$
 terpri()$terpri()$

 write"If not only variables but also functions are transformed then it"$
 terpri()$
 write"is assumed that all new functions depend on the same new variables"$
 terpri()$
 write"and that all old functions depend on the same old variables."$
 terpri()$ terpri()$

 write"For these procedures to be applicable the old functions and variables"$
 terpri()$
 write"must be given explicitly in terms of the new ones, not involving"$
 terpri()$
 write"unspecified functions of the new ones. Also the differential "$
 terpri()$
 write"equations to be transformed must contain the old independent and"$
 terpri()$
 write"dependent variables and their partial derivatives explicitly."$
 terpri()$

% write"Hint: Splitting a single transformation involving many"$
% terpri()$
% write"variables into many transformations involving each only few"$
% terpri()$
% write"variables speeds the whole transformation up."$
% terpri()$

 terpri()$
 write"Give a list of new functions, e.g. `u1,u2,u3;' in the order to"$
 terpri()$
 write"be used to sort dervatives. If there are no new functions enter ;"$
 terpri()$
 ulist := termlistread()$

 terpri()$
 write"Give a list of all new variables, e.g. 'v1,v2,v3;' in the order to"$
 terpri()$
 write"be used to sort derivatives. If there are no new variables enter ;"$
 terpri()$
 vlist := termlistread()$

 if ulist then <<
  for each u in ulist do
  for each v in vlist do depend u,v$

  terpri()$
  write"Give a list of all substitutions of old functions in terms of"$
  terpri()$
  write"new functions and new variables, e.g. y1=2*u1+u2*v2, y2=u2-u1*v1;"$
  terpri()$
  write"If there are no substitutions of old functions enter ;"$
  terpri()$
  yslist := termlistread()$

  % Check whether all old functions do depend on the same variables
  ylist:=for each u in yslist collect cadr u$
  xlist:=fctargs car ylist$
  for each u in cdr ylist do <<
   xl2:=fctargs u$
   if not_included(xlist,xl2) or
      not_included(xl2,xlist) then <<
    notallowed:=t$
    terpri()$
    write"Functions ",car ylist,",",u," do not depend on the same variables!"$
   >>
  >>
 >>$

 if notallowed then return nil;

 if vlist then <<
  terpri()$
  write"Give a list of all substitutions of old variables in terms of"$
  terpri()$
  write"new functions and new variables, e.g. x1=v1-v2*u2, x2=3*v2+v1*u1;"$
  terpri()$
  xslist := termlistread()$
 >>$

 terpri()$
 write"Shall the transformed equation be fully simplified,"$
 terpri()$
 write"i.e. redundand non-vanishing factors be dropped y/n : "$
 full_simplify:=termread()$
 if (full_simplify='n) then full_simplify:=nil;
 terpri()$

 % Dependence of the new dependent variables on old independent variables
 % which are not transformed
 for each v in xslist do xlist:=setdiff(xlist,list cadr v);
 for each u in ulist do
 for each v in xlist do depend u,v$

 % Also non-changing old variables must enter the transformation as
 % partial derivatives wrt them will have a different meaning
 vlist:=append(vlist,xlist)$
 for each v in xlist do xslist:=cons({'EQUAL,v,v},xslist)$

 % If a test is necessary that all old variables are replaced then do
 % if (not not_included(ftem_,newli)) and
 %    (not not_included(newli,ftem_)) then
 promptstring!*:=oldprompt$

 if print_ then <<
  write"The transformation:"$terpri()$
  if vlist then <<
   write"The new variables: "$
   listprint(vlist)$terpri()
  >>;
  if ulist then <<
   write"The new functions: "$
   listprint(ulist)$terpri()
  >>;
  if xslist then <<
   write"The old variables expressed:"$terpri()$
   mathprint cons('LIST,xslist)
  >>;
  if yslist then <<
   write"The old functions expressed:"$terpri()$
   mathprint cons('LIST,yslist)
  >>;
 >>;

 return {'LIST,cons('LIST,ulist),
               cons('LIST,vlist),
               cons('LIST,yslist),
               cons('LIST,xslist),
               full_simplify }
end$

%----------------------------

symbolic procedure adddep(xlist)$
% all functions depending on old variables get a dependency on
% the new variables
% xlist is a lisp list ((x1,v1,v4,v5),(x2,v2,v3,v4),...)
begin scalar newdep,xs,dp;
 for each xs in xlist do <<
  newdep:=nil$
  while depl!* do <<
   dp:=car depl!*;
   depl!*:=cdr depl!*;
   if not freeof(dp,car xs) then
   dp:=cons(car dp,union(cdr xs,cdr dp))$
   newdep:=cons(dp,newdep);
  >>;
  depl!*:=reverse newdep;
 >>;
end$

%----------------------------

symbolic procedure dropdep(xlist)$
% xlist is a lisp list
begin scalar x,dp,newdep$
 for each x in xlist do <<
  newdep:=nil$
  while depl!* do <<
   dp:=car depl!*;
   depl!*:=cdr depl!*;
   if not freeof(dp,x) then
   dp:=delete(x,dp)$
   newdep:=cons(dp,newdep);
  >>;
  depl!*:=reverse newdep
 >>;
end$

%----------------------------

%symbolic operator TransfoDf$
symbolic procedure TransfoDf(dy,yslist,xlist,vlist)$
% - dy is the derivative to be transformed
% - yslist is a list of already computed substitutions for the old
%   functions and their derivatives
% - xlist is a list of the old variables
% - vlist is a list of the new variables
% All parameters are in prefix form.
% yslist,xlist,vlist are lisp lists
% returns cons(substitution for dy, complete list of substitutions)
begin
 scalar cpy,x,dym1,m,n,newdy,v$
 cpy:=yslist$
 while cpy and
       (dy neq cadar cpy) do cpy:=cdr cpy;
 return
 if not null cpy then cons(car cpy,yslist)          else  % found rule
 if not pairp dy then cons({'EQUAL,dy,dy},yslist) else << % no dy-rule
  % dym1 is one lower x-derivative than dy
  if ( length dy = 3      ) or
     ((length dy = 4) and
      (cadddr dy = 1)     ) then <<x:=caddr dy;dym1:=cadr dy>>
                            else <<
   cpy:=reverse dy;
   dym1:=reverse
   if not numberp car cpy then <<x:= car cpy;  cdr cpy >> else
   if (car cpy = 1)       then <<x:=cadr cpy; cddr cpy >> else
   if (car cpy = 2)       then <<x:=cadr cpy;  cdr cpy >> else
                               <<x:=cadr cpy; cons(sub1 car cpy,
                                                   cdr cpy)>>
  >>;
  yslist:=TransfoDf(dym1,yslist,xlist,vlist);
  dym1:=car yslist;      % dym1 is now a substitution rule for dym1 above
  dym1:=caddr dym1;      % dym1 is now the expression to be substituted
  yslist:=cdr yslist;    % the new substitution list
  % computation of the subst. rule for dy
  m:=1;
  while xlist and (x neq car xlist) do <<m:=add1 m; xlist:=cdr xlist >>$
  if null xlist then newdy:=reval {'DF,dym1,x}
                else <<
   n:=0;
   for each v in vlist do <<
    n:=add1 n;
    if not zerop algebraic(Dv!/Dx(n,m)) then
    newdy:=cons({'TIMES,{'DF,dym1,v},algebraic(Dv!/Dx(n,m))},
                newdy)$
    % {'DF,dym1,v} is the full total derivative as it should be
    % provided all functions depend directly on v (as stored in depl!*)
    % or they do not depend on v but not like f(u(v)) with an
    % unspecified f(u)
   >>;
   newdy:=if cdr newdy then reval cons('PLUS,newdy)
                       else if newdy then reval car newdy
                                     else 0
  >>$

  % return the new subst. rule and the new yslist
  cons({'EQUAL,dy,newdy},cons({'EQUAL,dy,newdy},yslist))
 >>
end$ % of TransfoDf

%----------------------------

symbolic procedure Do_Trafo(arglist,x)$
begin
 scalar yslist,xslist,ulist,vlist,xlist,ylist,m,n,ovar,nvar,e1,e2,e3,
        x,detpd,pdes,hval,trfo,newforg,newineq_,drvs,full_simplify$
        %dyx!/duv,Dv!/Dx

 algebraic <<
  % input of the transformation
  ulist :=first x$ x:=rest x$
  vlist :=first x$ x:=rest x$
  yslist:=first x$
  xslist:=second x$
  full_simplify:=third x$ x:=nil$
 >>$

 % update of depl!*
 xlist:=
 for each e1 in cdr xslist collect <<
  x:=caddr e1$
  e3:=nil;
  for each e2 in cdr vlist do
  if not freeof(x,e2) then e3:=cons(e2,e3);
  cons(cadr e1,e3)
 >>$
 adddep(xlist)$

 algebraic <<
  % checking regularity of the transformation
  m:=length(xslist); n:=length(yslist)+m;
  clear dyx!/duv,Dv!/Dx;
  matrix dyx!/duv(n,n);
  matrix Dv!/Dx(m,m);
  ovar:=append(yslist,xslist);
  nvar:=append(ulist,vlist);
  n:=0;
  for each e1 in ovar do <<
   n:=n+1;m:=0;
   for each e2 in nvar do <<
    m:=m+1;
    dyx!/duv(m,n):=df(rhs e1,e2)
   >>
  >>;

  detpd:=det(dyx!/duv);
  if detpd=0 then <<write"The proposed transformation is not regular!"$
                    return nil>>;
  clear dyx!/duv;
  % computation of Dv/Dx:=(Dx/Dv)^(-1)
  n:=0;
  for each e1 in xslist do <<
   n:=n+1;m:=0;
   for each e2 in vlist do <<
    m:=m+1;
    Dv!/Dx(n,m):=total_alg_mode_deriv(rhs e1,e2)
                % It is assumed that ulist does depend on vlist
   >>
  >>;
  Dv!/Dx:=Dv!/Dx**(-1);
 >>$
 xslist:=cdr xslist$
 yslist:=cdr yslist$
 vlist :=cdr vlist$
 ulist :=cdr ulist$

 % update of global data ftem_, vl_
 if ulist then <<
  for each e1 in yslist do ftem_:=delete(cadr e1,ftem_);
  for each e1 in  ulist do ftem_:=fctinsert(e1,ftem_)$
 >>$

 xlist:=for each e1 in xslist collect cadr e1$
 for each e1 in xlist do vl_:=delete(e1,vl_);
 vl_:=append(vl_,vlist)$

 ylist:=for each e1 in yslist collect cadr e1$

 % update of the pdes
 pdes:=car arglist$
 for each e1 in pdes do <<
  hval:=get(e1,'val)$
  drvs:=append(search_li2(hval,'DF),ylist)$
  for each e3 in drvs do <<
   trfo:=TransfoDf(e3,yslist,xlist,vlist)$
   hval:=subst(caddar trfo,cadar trfo,hval);
   yslist:=cdr trfo
  >>$
  for each e2 in xslist do
  if not freeof(hval,cadr e2) then
  hval:=subst(caddr e2,cadr e2,hval);
  put(e1,'val,hval);
 >>$

 % update of forg
 for each e1 in cadr arglist do
 if (pairp e1) and (car e1 = 'EQUAL) then <<
  hval:=caddr e1;
  drvs:=append(search_li2(hval,'DF),ylist)$
  for each e3 in drvs do <<
   trfo:=TransfoDf(e3,yslist,xlist,vlist)$
   hval:=subst(caddar trfo,cadar trfo,hval);
   yslist:=cdr trfo
  >>$
  for each e2 in xslist do
  if not freeof(hval,cadr e2) then
  hval:=subst(caddr e2,cadr e2,hval);
  hval:=reval hval;

  newforg:=cons({'EQUAL,cadr e1,hval},newforg)$
  e2:=nil;
  for each e3 in ftem_ do
  if not freeof(hval,e3) then e2:=cons(e3,e2);
  put(cadr e1,'fcts,e2)
 >>                                  else
 if not freeof(ylist,e1) then <<
  e3:=yslist;
  while e3 and cadar e3 neq e1 do e3:=cdr e3$
  if e3 then newforg:=cons(car e3,newforg)
        else newforg:=cons(e1,newforg)
 >>                                  else
 newforg:=cons(e1,newforg);

 % update of ineq_
 newineq_:=nil;
 for each e1 in ineq_ do <<
  drvs:=append(search_li2(e1,'DF),ylist)$
  for each e3 in drvs do <<
   trfo:=TransfoDf(e3,yslist,xlist,vlist)$
   e1:=subst(caddar trfo,cadar trfo,e1);
   yslist:=cdr trfo
  >>$
  for each e2 in xslist do
  if not freeof(e1,cadr e2) then
  e1:=subst(caddr e2,cadr e2,e1);
  newineq_:=cons(reval e1,newineq_)
 >>$
 ineq_:=nil;
 for each e1 in newineq_ do addineq(pdes,e1);

 xlist:=nil;
 for each e1 in xslist do
 if cadr e1 neq caddr e1 then xlist:=cons(cadr e1,xlist);
 dropdep(xlist)$

 for each e1 in pdes do <<
  for each e2 in allflags_ do flag1(e1,e2)$
  update(e1,get(e1,'val),ftem_,vl_,full_simplify,list(0),pdes)$
  drop_pde_from_idties(e1,pdes,nil);
  drop_pde_from_properties(e1,pdes)
 >>$

 % cleanup
 algebraic clear Dv!/Dx;
 return {pdes,newforg,vl_}

end$ % of Do_Trafo

%----------------------------

symbolic procedure Find_Trafo(arglist)$
begin
 scalar dli,avf,f,ps,sol,pde,pdes,forg,batch_bak,print_bak,vlist,
        xslist,vl,h1,h2,h3,h4,trtr,eligfncs,eligpdes,epdes,remain,
        maxvno;
% trtr:=t$
 ps:=promptstring!*$
 promptstring!*:=""$
 pdes:=car arglist$
 % If there are functions of fewer variables then transformations can
 % make them to functions of more variables which can add solutions.
 % One could first compute the transformation and then check whether
 % there is an ftem_ function which has an enlarged set of dependent
 % variables and in this case either drops the transformation or one
 % adds extra conditions df(f,y)=0 (where d/dy is to be transformed)
 % for these functions. Instead a preliminary simpler routs is taken
 % in the following, ftem_ may contain only constants or functions
 % of the same number of variables.

 maxvno:=0;
 h1:=ftem_;
 while h1 and <<
  h3:=fctlength car h1$
  if h3=0 then t else
  if maxvno=0 then <<maxvno:=h3;t>> else
  if h3=maxvno then t else nil
 >> do h1:=cdr h1;
 if h1 then return <<
  if print_ then <<
   write"Non-constant functions of fewer variables prevent"$terpri()$
   write"the application of this technique."$terpri()
  >>$
  nil
 >>$
 if trtr then <<write"111"$terpri()>>$

 % Find eligible PDEs
 while pdes do <<
  pde:=car pdes;pdes:=cdr pdes;
  if get(pde,'nvars)=maxvno then <<
   eligfncs:=nil;
   avf:=get(pde,'allvarfcts)$
   if avf and null cdr avf then <<
    % There must only be one function of all variables because
    % the other one would be part of the inhomogeneity and
    % derivatives of this function would give errors in quasilinpde
    % when the differentiation variable becomes a function in the
    % characteristic ODE system and substitutions are done where
    % the function is substituted by an expression that has been
    % computed. But also if no derivatives occur, crack is strictly
    % speaking not able to deal with funtions of functions.
    % Therefore only one function apart from constants is allowed.
    f:=car avf;
    dli:=get(pde,'derivs);
    h1:=t; h2:=0;  % h2 counts the first order derivatives of f
    while dli and h1 do
    if (not pairp caar dli) or
       (caaar dli neq f) then dli:=cdr dli else
    if null cdaar dli then dli:=cdr dli else % f algebraic
    if null cddaar dli then <<h2:=add1 h2;dli:=cdr dli>>
                       else h1:=nil;
    if null dli and (h2 > 1) then eligfncs:=cons(f,eligfncs)

   >>$
   if eligfncs then <<
    eligpdes:=cons(cons(pde,eligfncs),eligpdes);
    epdes:=cons(pde,epdes)
   >>
  >>
 >>$
 if trtr then <<write"222"$terpri()>>$
 if null epdes then return nil;

 if expert_mode then pde:=selectpdes(epdes,1)
                else <<
 if trtr then <<write"333"$terpri()>>$

  % Find PDEs with min number of allvar functions
  h2:=10000;
  for each h1 in epdes do <<
   h3:=length get(h1,'allvarfcts);
   if h3<h2 then <<h2:=h3;remain:={h1}>> else
   if h3=h2 then remain:=cons(h1,remain);
  >>;
  epdes:=remain;
  if trtr then <<write"444"$terpri()>>$

  % Find PDEs with max number of variables
  h2:=0;
  for each h1 in epdes do <<
   h3:=get(h1,'nvars);
   if h3>h2 then <<h2:=h3;remain:={h1}>> else
   if h3=h2 then remain:=cons(h1,remain);
  >>;
  epdes:=remain;
  if trtr then <<write"555"$terpri()>>$

  % Find shortest of these PDEs
  h2:=10000;
  for each h1 in epdes do <<
   h3:=get(h1,'terms);
   if h3<h2 then <<h2:=h3;remain:={h1}>> else
   if h3=h2 then remain:=cons(h1,remain);
  >>;
  epdes:=remain;
  if trtr then <<write"666"$terpri()>>$

  pde:=car epdes$   % One could select further the one with the
                    % fewest variables involved in the transformation
  while eligpdes and caar eligpdes neq pde do eligpdes:=cdr eligpdes;
  f:=cadar eligpdes;

 >>$
 if trtr then <<write"777"$terpri()>>$
 if null pde then return nil;
 if trtr then <<write"888"$terpri()>>$

 if print_ then <<
  write"Finding a transformation to integrate the 1st order PDE ",pde,":"$
  terpri()$
 >>$

 print_bak:=print_;       print_:=nil$
 batch_bak:=!*batch_mode; !*batch_mode:=t$
 pdes:=car arglist$
 forg:=cadr arglist$

 h1:=level_string(session_)$
 h1:=bldmsg("%s%s.",h1,"qlp")$
 backup_to_file(pdes,forg,h1)$ % moved before again:, should be ok
 if trtr then <<write"999"$terpri()>>$
 sol:=reval algebraic(quasilinpde(lisp(get(pde,'val)),f,
                                  lisp(cons('LIST,get(pde,'vars)))))$
 restore_backup_from_file(pdes,forg,h1)$
 delete!-file h1;
 if trtr then <<write"000"$terpri()>>$
 if trtr then <<write"sol0="$mathprint sol$terpri()>>$

 !*batch_mode:=batch_bak$ print_:=print_bak$
 if null sol or null cdr sol or null cdadr sol then return nil$
 sol:=cadr sol;

 h1:=cdr sol;
 for each h2 in h1 do
 if not freeof(h2,f) then sol:=delete(h2,sol);
 % One could use lin_check(h2,{f}) to test linearity if needed

 h1:=cdr sol;
 if trtr then <<write"f=",f$terpri()$
  write"h1=",h1$terpri()$
  write"sol0="$mathprint sol
 >>$

 % make a list of all variables occuring in these expressions
 % these are all the variables to occur in the transformation
 h2:=get(pde,'vars)$
 for each f in h2 do if member(f,sol) then h2:=delete(f,h2);

 % Keep only the algebraic expressions, drop the single variables
 for each f in h1 do if atom f then sol:=delete(f,sol);
 if trtr then <<write"sol1="$mathprint sol>>$

 % find the variable for which the algebraic expressions are
 % most easily solved
 if trtr then <<write"h2=",h2$terpri()>>$
 if trtr then <<write"xslist=",xslist$terpri()>>$
 xslist:=err_catch_solve(sol,cons('LIST,h2))$
 if null xslist then return <<
  write"REDUCE was not able to solve"$mathprint sol$
  write"for one of "$listprint(h2);
  nil
 >>             else xslist:=cdr reval car xslist$
 if trtr then <<write"xslist=",xslist$terpri()>>$

 h3:=nil;
 while xslist do <<
  f:=car xslist; xslist:=cdr xslist;

  if (car f='EQUAL) and
     ((pairp caddr f) and
      (caaddr f = 'ARBCOMPLEX)) then <<
   h2:=delete(cadr f,h2);
   h3:=cons(cadr f,h3);
   xslist:=subst(1,caddr f,xslist)
  >>
 >>$
 if trtr then <<write"h3new=",h3$terpri()$
                write"h2new=",h2$terpri()>>$

 for each f in get(pde,'vars) do
 if not freeof(sol,f) and
    not member(f,h3)  and
    not member(f,h2)  then h3:=cons(f,h3);
 if trtr then <<write"sol2=",sol$terpri()>>$
 sol:=append(sol, h3);
 if trtr then <<write"sol3=",sol$terpri()>>$

 %terpri()$
 %write"Give a list of ",length cdr sol," new variable names, like:  u,v,w;"$
 %terpri()$
 %write"one for each of the expressions in the following list:"$
 %mathprint sol;
 %promptstring!*:="list of new variable names: "$
 %vlist:=termlistread()$
 %promptstring!*:=ps$

 h3:=append(h2,h3);
 h4:=h3;
 vlist:=for each f in h3 collect mkid(f,'!%);
 if trtr then <<write"vlist=",vlist$terpri()>>$

 h1:=vlist$
 if trtr then <<write"sol-2="$mathprint sol$terpri()>>$
 sol:=cdr sol;
 h2:=sol;
 while sol do <<
  vl:=cons({'DIFFERENCE,car sol,car h1},vl);
  sol:=cdr sol;
  h1:=cdr h1;
  h4:=cdr h4;
 >>$
 while h4 do <<
  vl:=cons({'DIFFERENCE,car h4,car h1},vl);
  h1:=cdr h1;
  h4:=cdr h4
 >>$

 if trtr then <<write"2nd SOLVE: vl="$mathprint cons('LIST,vl)$terpri()$
                write"h3=",h3>>$
 xslist:=err_catch_solve(cons('LIST,vl),cons('LIST,h3))$
 if null xslist then return <<
  write"REDUCE was not able to solve"$mathprint cons('LIST,vl)$
  write"for the variables "$listprint(h3);
  nil
 >>             else xslist:=car xslist$

 if trtr or print_ then <<
  write"The following variable transformation expresses variables"$
  terpri()$
  listprint(h3);
  write"  through variables  "$
  listprint(vlist); write" :"$terpri()$
  for each f in cdr xslist do mathprint f
 >>$

 h3:=for each h1 in vl collect {'EQUAL,caddr h1,cadr h1};
 done_trafo:=cons('LIST,cons(cons('LIST,h3),cdr done_trafo));

 return Do_Trafo(arglist,{'LIST,{'LIST},cons('LIST,vlist),
                          {'LIST},xslist,t %full_simplify
                         });

end$ % of Find_Trafo

%----------------------------

symbolic procedure General_Trafo(arglist)$
% Doing a transformation for all data relevant in CRACK
% Tramsformation rule for partial derivatives d using total
% derivatives D:
%
%          /   p \ -1
%   d     |  Dx   |    D
%   --- = |  ---  |  * ---
%     p   |    i  |      i
%   dx     \ Dv  /     Dv
%
begin
 scalar x;
 x:=input_trafo()$
 if null x then return
 <<terpri()$write"No proper input --> no transformation"$nil>>$
 return Do_Trafo(arglist,x)
end$

%----------------------------

endmodule$

end$
