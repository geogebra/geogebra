%********************************************************************
module underdetde$
%********************************************************************
%  Routines for the solution of underdetermiined ODEs and PDEs
%  Author: Thomas Wolf
%  August 1998, February 1999

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


symbolic procedure undetlinode(arglist)$
% parametric solution of underdetermined ODEs
begin scalar l,l1,p,pdes,forg,s$
 pdes:=car arglist$
 forg:=cadr arglist$
 if expert_mode then <<l1:=selectpdes(pdes,1);flag(l1,'to_under)>>
                else l1:=cadddr arglist$
 while l1 do
 if null (p:=get_ulode(l1)) then l1:=nil
                            else <<
  l:=underode(p,pdes)$
  p:=car p$
  if null l then <<remflag1(p,'to_under);l1:=delete(p,l1)>>
            else <<
   if print_ then <<
    write"Parametric solution of the underdetermined ODE ",p$
    terpri();
    write"giving the new ODEs "$
    s:=l;
    while s do <<
     write car s;
     s:=cdr s;
     if s then write ","
    >>$
    terpri()
   >>$
   pdes:=drop_pde(p,pdes,nil)$
   for each s in l do pdes:=eqinsert(s,pdes)$
   l:=list(pdes,forg)$
   l1:=nil;
  >>
 >>$
 return l$
end$

symbolic procedure undetlinpde(arglist)$
% parametric solution of underdetermined PDEs
begin scalar l,l1,p,pdes,forg$
 pdes:=car arglist$
 forg:=cadr arglist$
 if expert_mode then <<l1:=selectpdes(pdes,1);flag(l1,'to_under)>>
                else l1:=cadddr arglist$
 while l1 do
 if null (p:=get_ulpde(l1)) then l1:=nil
                            else <<
  l:=underpde(p,pdes)$  % l has to be the list of new pdes
  p:=car p$
  if null l then <<remflag1(p,'to_under);l1:=delete(p,l1)>>
            else <<
   pdes:=drop_pde(p,pdes,nil)$
   for each s in l do pdes:=eqinsert(s,pdes)$
   l:=list(pdes,forg)$
   l1:=nil;
  >>
 >>$
 return l$
end$

symbolic procedure get_ulode(pdes)$
begin scalar h,best_ulode;
 for each p in pdes do
 if flagp(p,'to_under) then
 if null (h:=ulodep(p)) then remflag1(p,'to_under)
                        else
 if ((null best_ulode) or (car h < car best_ulode)) then best_ulode:=h;
 return if best_ulode then cdr best_ulode
                      else nil
end$

symbolic procedure get_ulpde(pdes)$
begin scalar h,best_ulpde;
 for each p in pdes do
 if flagp(p,'to_under) then
 if null (h:=ulpdep(p)) then remflag1(p,'to_under)
                        else
 if ((null best_ulpde) or (car h < car best_ulpde)) then best_ulpde:=h;
 return if best_ulpde then cdr best_ulpde
                      else nil
end$

symbolic procedure ulodep(p)$
begin
 scalar tr_ulode,drvs,ulode,allvarf,minord,dv,f,x,h,found,minordf,totalorder$
 % minord and minordf are currently not needed later on

% tr_ulode:=t;

 % Is it an underdetermined linear ODE for the allvarfcts?
 drvs:=get(p,'derivs)$
 ulode:=t$
 allvarf:=get(p,'allvarfcts);
 if tr_ulode then <<
  write"allvarf=",allvarf$
  terpri()$
 >>$

 minord:=1000;
 if not (allvarf and cdr allvarf) then ulode:=nil
                                  else % at least two allvar-fcts
 while ulode and drvs do <<
  dv:=caar drvs;
  f:=car dv$
  if tr_ulode then <<
   write"car drvs=",car drvs,"  dv=",dv,"  f=",f,
        "  member(f,allvarf)=",member(f,allvarf)$
   terpri()$
  >>$
  if member(f,allvarf) then
  if (cdar drvs neq 1) or % not linear
                          % x is already determined and it is not x:
     (cdr dv and ((x and (x neq cadr dv)                  ) or
                          % there are other differentiation variables:
                  ((cddr dv) and ((not fixp caddr dv) or
                                  cdddr dv               ))    )
     ) then <<            % no ODE:
   ulode:=nil;
   if tr_ulode then <<
    write"new ulode=",ulode$
    terpri()$
   >>$
  >>   else               % can be an ODE
  if null cdr dv then     % f has no derivatives
  if not member(f,found) then ulode:=nil % no ODE --> substitition
                         else % f has already been found with a
                              % consequently higher x-derivative
                 else     % this is an x-derivative of f
  if null x then <<       % x had not yet been determined
   if tr_ulode then <<
    write"null x"$
    terpri()$
   >>$
   found:=cons(f,found)$
   x:=cadr dv;
   minordf:=car dv;
   if null cddr dv then minord:=1
                   else minord:=caddr dv;
   totalorder:=minord
  >>        else                  % x has already been determined
  if not member(f,found) then <<  % only leading derivatives matter
   found:=cons(f,found)$          % and the first deriv. of f is leading
   if null cddr dv then h:=1
                   else h:=caddr dv;
   totalorder:=totalorder+h;
   if h<minord then <<
    minord:=h;
    minordf:=car dv
   >>
  >>                     else
                       else % not member(f,allvarf)
  if null x or              % then there are only derivatives
                            % of non-allvarfcts left
     member(x,fctargs f) then ulode:=nil; % no x-dependent non-allvarfcts
  if tr_ulode then <<
   write"found=",found,"  minord=",minord,"  minordf=",minordf$
   terpri()$
  >>$

  drvs:=cdr drvs;
 >>$

 if tr_ulode then <<
  write"ulode=",ulode$
  terpri()$
 >>$
 return if ulode then {totalorder,p,x,minord,minordf}
                 else nil
end$ % of ulodep

symbolic procedure ulpdep_(p)$
begin
 scalar tr_ulpde,drvs,drv,ulpde,allvarf,allvarfcop,
        vld,vl,v,pde,fn,f,no_of_drvs,no_of_tms,ordr,maxordr,parti$
%tr_ulpde:=t;

 % Is it an underdetermined linear PDE for the allvarfcts?
 drvs:=get(p,'derivs)$
 ulpde:=t$
 allvarf:=get(p,'allvarfcts);
 if tr_ulpde then <<
  write"allvarf=",allvarf$
  terpri()$
 >>$

 if not (allvarf and cdr allvarf) then ulpde:=nil
                                  else << % at least two allvar-fcts
  allvarfcop:=allvarf$
  no_of_tms:=0; % total number of terms of all diff operators
  vl:=get(p,'vars)$

  while ulpde and allvarfcop do <<
   % extracting the differential operator for car allvarfcop
   pde:=get(p,'val);
   fn:=car allvarfcop;     allvarfcop:=cdr allvarfcop;
   for each f in allvarf do
   if f neq fn then pde:=subst(0,f,pde);
   pde:=reval pde;
   % Is pde linear in fn?
   if not lin_check(pde,{fn}) then <<
    if tr_ulpde then <<write"not linear in ",f$terpri()>>$
    ulpde:=nil
   >>                         else <<
    % add up the number of terms
    no_of_tms:=no_of_tms + no_of_terms(pde)$

    % What are all variables in pde? This is needed to test later
    % whether they are disjunct from all variables from another
    % diff. operator
    vld:=nil;
    for each v in vl do if not freeof(pde,v) then vld:=cons(v,vld);

    % What is the number of derivatives of fn?
    % What order is the highest derivative of fn?
    no_of_drvs:=0;
    for each drv in drvs do
    if fn=caar drv then <<
     ordr:=absdeg(cdar drv);
     if (no_of_drvs=0) or (ordr>maxordr) then maxordr:=ordr;
     no_of_drvs:=add1 no_of_drvs;
    >>;

    % collect the differential operators with properties in parti
    parti:=cons({pde,fn,vld,no_of_drvs,maxordr},parti);
   >>
  >>;
  if no_of_tms neq get(p,'terms) then <<
   if tr_ulpde then <<
    write"not a lin. homog. PDE"$terpri()
   >>$
   ulpde:=nil; % not a lin. homog. PDE
  >>$
  if tr_ulpde then <<
   write"parti="$
   prettyprint parti$
  >>$
 >>$
 return if null ulpde then nil
                      else parti
end$

symbolic procedure ulpdep(p)$
begin
 scalar tr_ulpde,drvs,drv,ulpde,parti,pde,
        difop1,difop2,commu,disjun,totalcost$
%tr_ulpde:=t;
 % Is it an underdetermined linear PDE for the allvarfcts?
 drvs:=get(p,'derivs)$
 ulpde:=ulpdep_(p)$
 if ulpde then <<
  parti:=ulpde$ ulpde:=t$
  % Find a differential operator pde in parti
  pde:=nil;
  for each difop1 in parti do <<
   commu:=t;
   % which does commute with all other diff. operators
   for each difop2 in parti do
   if (cadr difop1 neq cadr difop2) and
      (not zerop reval {'DIFFERENCE,subst(car difop1,cadr difop2,car difop2),
                        subst(cadr difop1,cadr difop2,
                              subst(car difop2,cadr difop1,car difop1))})
   then <<
    commu:=nil;
    if tr_ulpde then <<
     write"no commutation of:"$terpri()$
     prettyprint difop1$
     write"and "$terpri()$
     prettyprint difop2
    >>
   >>$
   % and is variable-disjunct with at least one other diff. operator
   if commu then <<
    disjun:=nil;
    for each difop2 in parti do
    if (cadr difop1 neq cadr difop2) and
       freeoflist(caddr difop1,caddr difop2) then disjun:=t;
    if disjun then
    if null pde then pde:=difop1 else
    if (  car cddddr difop1) < (car cddddr pde) or   % minimal maxord
       (((car cddddr difop1) = (car cddddr pde)) and % minimal number of terms
        ((cadddr difop1) < (cadddr pde))             ) then pde:=difop1
   >>
  >>;
  if null pde then ulpde:=nil
 >>;

 if tr_ulpde then <<
  write"ulpde=",ulpde$
  terpri()$
 >>$
 % as a first try we take as cost for the PDE p the sum of all orders
 % of all derivatives of all functions in p
 totalcost:=0;
 for each drv in drvs do totalcost:=totalcost+absdeg(cdar drv);

 return if ulpde then {totalcost,p,pde,parti}
                 else nil
end$ % of ulpdep

symbolic procedure min_ord_f(ode,allvarf,vl)$
begin scalar minord,minordf,newallvarf,f,h,tr_ulode$
% tr_ulode:=t;
 % does any function not occur anymore?
 % Which function does occur with lowest derivative: minord, minordf?
 minord:=1000;
 minordf:=nil;
 newallvarf:=nil;
 for each f in allvarf do <<
  h:=ld_deriv_search(ode,f,vl)$
  if tr_ulode then <<
   write"ld_deriv_search(",f,")=",h$
   terpri()$
  >>$
  if not zerop cdr h then <<  % otherwise f does not occur anymore in ode
   newallvarf:=cons(f,newallvarf)$
   h:=car h$
   h:=if null h then 0 else
      if null cdr h then 1 else cadr h$ % asuming that car h = x
   if h<minord then <<minord:=h;minordf:=f>>
  >>
 >>$
 return {minord,minordf,newallvarf}
end$

symbolic procedure underode(pchar,pdes)$
% pchar has the form {p,x,minord,minordf}
begin
 scalar tr_ulode,p,x,allvarf,orgallvarf,ode,noallvarf,vl,
        minord,minordf,adj,f,h,newf,sol,sublist,rtnlist$

% tr_ulode:=t;

 p      :=car  pchar;
 x      :=cadr pchar;
 minord :=caddr pchar;
 minordf:=cadddr pchar;

 allvarf:=get(p,'allvarfcts);
 orgallvarf:=allvarf;

 ode:=get(p,'val);
 noallvarf:=length(allvarf);
 vl:=get(p,'vars);
 while (minord>0) and
       (length(allvarf)=noallvarf) do <<
  if tr_ulode then <<
   write "x=",x,"  minord=",minord,"  minordf=",minordf,
         "  allvarf=", allvarf$
   terpri()$
  >>$
  repeat <<
   adj:=intpde(ode,allvarf,vl,x,t);
   if tr_ulode then <<
    write"adj=",adj$
    terpri()$
   >>$

   h:=nil;
   for each f in allvarf do if not freeof(cadr adj,f) then h:=cons(f,h);
   if null h then  % exact ode --> should do better then what is done now
   ode:=reval {'TIMES,x,ode};
  >> until h;

  minordf:=cadr min_ord_f(ode,h,vl)$

  % a new function (potential) is needed:
  newf:=newfct(fname_,vl,nfct_)$
  nfct_:=add1 nfct_;

  if tr_ulode then <<
   algebraic write"eqn=",{'LIST,{'PLUS,{'DF,newf,x},lisp cadr adj}}$
   algebraic write"var=",{'LIST,minordf                      }
  >>$
  sol:=cadr solveeval list({'LIST,{'PLUS,{'DF,newf,x},cadr adj}},
                           {'LIST,minordf                      } );
  allvarf:=delete(minordf,allvarf)$
  allvarf:=cons(newf,allvarf)$
  % assuming that there is exacly one solution to the lin. alg. equation
  if tr_ulode then <<
   terpri()$
   write"sol=",sol$
   terpri()$
  >>$
  sublist:=cons(sol,sublist)$
  ode:=reval num reval
       {'PLUS,newf,{'MINUS,subst(caddr sol,cadr sol,car adj)}}$
  if tr_ulode then algebraic(write"ode=",ode)$

  h:=min_ord_f(ode,allvarf,vl)$
  minord:=car h; minordf:=cadr h; allvarf:=caddr h;

  if minord=0 then
  sublist:=cons(cadr solveeval list({'LIST,ode},{'LIST,minordf}),sublist)$

  if tr_ulode then <<
   write"allvarf=",allvarf,"  minord=",minord,"  minordf=",minordf$
   terpri()$
  >>$

 >>$

 if (minord neq 0) and (not zerop ode) then rtnlist:=list ode;
 ode:=nil;
 if tr_ulode then <<write"rtnlist=", rtnlist;terpri()>>$
 if tr_ulode then algebraic(write"sublist=",cons('LIST,sublist));
 while sublist do <<
  if member(cadar sublist,orgallvarf) then
  rtnlist:=cons(reval num reval {'PLUS,cadar sublist,
                                 {'MINUS,caddar sublist}},rtnlist)$
  sublist:=cdr reval cons('LIST,
                          subst(caddar sublist,cadar sublist,cdr sublist))$
  if tr_ulode then algebraic(write"sublist=",cons('LIST,sublist))
 >>$

 allvarf:=smemberl(allvarf,rtnlist)$
 if tr_ulode then <<
  write"allvarf=",allvarf$
  terpri()$
 >>$
 for each h in allvarf do ftem_:=fctinsert(h,ftem_)$
 if tr_ulode then algebraic(write"rtnlist=",cons('LIST,rtnlist));
 h:=for each h in rtnlist collect
    mkeq(h,union(get(p,'fcts),allvarf),vl,allflags_,t,list(0),nil,pdes)$
 if print_ then terpri()$
 return h
end$

symbolic procedure underpde(pchar,pdes)$
% pchar has the form {p,difop,parti} where p is the name of the equation,
% difop is the main differential operator in p and parti is a partition
% of p, i.e. the list of all differential operators. Each differential
% operator has the form
%  {pde,fn,vld,no_of_drvs,maxordr}
% where pde are all terms in p with fn, vld is a list of all variables
% in pde, no_of_dervs is the number of different derivatives of fn,
% maxord is the maximal order of derivatives of fn (order of pde)

begin
 scalar tr_ulpde,ldo,parti,fn,lcond,difop,h,fl,eqlist,vl$
% has to return list of new pde just like in underode
% tr_ulpde:=t;
 ldo:=cadr pchar;
 parti:=caddr pchar$
 vl:=get(car pchar,'vars)$
 fn:=cadr ldo$
 lcond:={fn}$
 if tr_ulpde then <<
  write"ldo="$prettyprint parti$
  write"parti="$prettyprint parti
 >>$
 for each difop in parti do
 if cadr difop neq fn then <<
  h:=newfct(fname_,vl,nfct_)$
  nfct_:=add1 nfct_$
  if print_ then terpri()$
  fl:=cons(h,fl)$
  eqlist:=cons(cons({cadr difop,h},
                    reval {'DIFFERENCE,cadr difop,subst(h,fn,car ldo)}),
               eqlist)$
  lcond:=cons(subst(h,cadr difop,car difop),lcond)
 >>$
 eqlist:=cons(cons(append(get(car pchar,'fcts),fl),
                   cons('PLUS,lcond)),eqlist)$
 if tr_ulpde then <<
  write"eqlist="$prettyprint eqlist$
 >>$
 h:=for each h in eqlist collect
 mkeq(cdr h,car h,get(car pchar,'vars),allflags_,t,list(0),nil,pdes)$
 if print_ then terpri()$
 return h
end$

endmodule$

end$

