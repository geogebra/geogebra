%********************************************************************
module identities$
%********************************************************************
%  Routines for dealing with differential identities
%  Author: Thomas Wolf
%  May 1999

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


symbolic procedure drop_idty(id)$
% recycles a name of an identity
<<setprop(id,nil)$
  recycle_ids:=id . recycle_ids$
  idnties_:=delete(id,idnties_)>>$

symbolic procedure new_id_name$
% provides a name for a new identity
begin scalar id$
 if null recycle_ids then <<
  id:=mkid(idname_,nid_)$
  nid_:=add1 nid_$
 >>                  else <<
  id:=car recycle_ids$
  recycle_ids:=cdr recycle_ids
 >>$
 setprop(id,nil)$
 return id
end$

symbolic procedure replace_idty$
begin scalar p,ps,ex$
 ps:=promptstring!*$
 promptstring!*:=""$
 terpri()$
 write "If you want to replace an identity then ",
       "type its name, e.g. id_2 <ENTER>."$
 terpri()$
 write "If you want to add an identity then type `new_idty' <ENTER>. "$
 p:=termread()$
 if (p='NEW_IDTY) or member(p,idnties_) then
  <<terpri()$write "Input of a value for "$
  if p='NEW_IDTY then write "the new identity."
                 else write p,"."$
  terpri()$
  write "You can use names of other identities, e.g. 3*id_2 - df(id_1,x); "$
  terpri()$
  write "Terminate the expression with ; or $ : "$
  terpri()$
  ex:=termxread()$
  for each a in idnties_ do ex:=subst(get(a,'val),a,ex)$
  if p neq 'NEW_IDTY then drop_idty(p)$
  new_idty(reval ex,nil,nil)$

  terpri()$write car idnties_$
  if p='NEW_IDTY then write " is added"
                 else write " replaces ",p$
 >>
 else <<terpri()$
        write "An identity ",p," does not exist! (Back to previous menu)">>$
 promptstring!*:=ps$
end$


symbolic procedure trivial_idty(pdes,idval)$
if zerop idval or
   (pdes and
    null smemberl(pdes,search_li(idval,'DF))) % identity is purely alg.
   then t else nil$

symbolic procedure new_idty(idty,pdes,simp)$
% idty is the value of a new differential identity between equations
begin scalar id,idcp$
 if simp then idty:=simplifypde(idty,pdes,t,nil)$
 if not trivial_idty(pdes,idty) then <<
  idcp:=idnties_$
  while idcp and (get(car idcp,'val) neq idty) do idcp:=cdr idcp;
  if null idcp then <<
   id:=new_id_name();
   put(id,'val,idty)$
   flag1(id,'to_subst)$
   flag1(id,'to_int)$
   idnties_:=cons(id,idnties_)
  >>
 >>
end$

symbolic procedure show_id$
begin scalar l,n$
 terpri()$
 l:=length idnties_$
 write if l=0 then "No" else l,
 if l=1 then " identity." else " identities"$
 if l=0 then terpri()
        else <<
  n:=1;
  for each l in reverse idnties_ do <<
   terpri()$
   algebraic write n,")  ",l," :  0 = ",lisp(get(l,'val));
   n:=add1 n;
   if print_all then <<
    terpri()$write "   to_int     : ",flagp(l,'to_int)$
    terpri()$write "   to_subst   : ",flap(l,'to_subst)$
   >>
  >>
 >>
end$

symbolic procedure del_red_id(pdes)$
begin scalar oldli,pl,s,idty,news,succ,p,l$ % ,r,newr$
 if idnties_ then <<
  oldli:=idnties_$
  while oldli do
  if not flagp(car oldli,'to_subst) then oldli:=cdr oldli
                                    else <<
   idty:=get(car oldli,'val)$
   pl:=smemberl(pdes,idty)$

   for each p in pl do l:=union(get(p,'vars),l)$
   if l then l:=length l else l:=0$

   pl:=setdiff(pl,search_li(idty,'DF));
   % now all pdes in pl are redundand, drop the longest
   if null pl then remflag1(car oldli,'to_subst)
              else <<
    drop_idty(car oldli);
    % find the longest equation s of those with the most variables
    s:=nil;
    while pl do <<
     if (null get(car pl,'starde)                     ) and
        (get(car pl,'nvars)=l                         ) and
        (null(% flagp(s,'to_int) or
              % flagp(s,'to_fullint)  or
              % flagp(s,'to_sep) or
              % flagp(s,'to_gensep) or
              % flagp(s,'to_decoup) or
              flagp(s,'to_eval))                      ) and
        ((null s                                 ) or
         (get(car pl,'nvars)>get(s,'nvars)       ) or
         ((get(car pl,'nvars)=get(s,'nvars)) and
          (get(car pl,'terms)>get(s,'terms))     )    ) then s:=car pl;
     pl:=cdr pl
    >>;
    if null s then remflag1(car oldli,'to_subst)
              else <<
     if print_ then <<
      write "Equation ",s," is dropped as it is a consequence of others: "$
      algebraic write "0 = ",lisp(idty)$
     >>$
     % assuming s occurs linearly:
     pl:=coeffn(idty,s,1)$
     news:=reval {'QUOTIENT,{'DIFFERENCE,{'TIMES,pl,s},idty},pl};
     %for each r in idnties_ do
     %if not freeof(get(r,'val),s) then <<
     % newr:=reval subst(news,s,get(r,'val));
     % newr:=simplifypde(newr,pdes,t,nil)$
     % put(r,'val,newr)$
     % flag1(r,'to_subst)$
     % flag1(r,'to_int)$
     %>>$
     succ:=t$
     pdes:=drop_pde(s,pdes,news)$
     oldli:=cdr oldli
    >>
   >>
  >>
 >>;
 if succ then return pdes
end$

symbolic procedure del_redundant_de(argset)$
begin scalar pdes;
 if pdes:=del_red_id(car argset) then return {pdes,cadr argset}$
end$

symbolic procedure write_id_to_file(pdes)$
begin scalar s,p,h,pl,ps$
 if idnties_ then <<
  ps:=promptstring!*$
  promptstring!*:=""$
  write"Please give the name of the file in double quotes"$terpri()$
  write"without `;' : "$
  s:=termread()$
  out s;
  off nat$

  write"load crack$"$terpri()$
  write"lisp(nequ_:=",nequ_,")$"$terpri()$
  write"off batch_mode$"$terpri()$
  write"list_of_variables:="$
  algebraic write lisp cons('LIST,vl_)$

  write"list_of_functions:="$
  algebraic write lisp cons('LIST,pdes)$

  for each h in pdes do
  if pl:=assoc(h,depl!*) then
  for each p in cdr pl do
  algebraic write "depend ",lisp h,",",lisp p$

  write"list_of_equations:="$
  algebraic write
  lisp( cons('LIST,for each h in idnties_ collect get(h,'val)));

  terpri()$ write"solution_:=crack(list_of_equations,{},"$
  terpri()$ write"                 list_of_functions,"$
  terpri()$ write"                 list_of_variables)$"$
  terpri()$
  terpri()$
  write"end$"$terpri()$
  shut s;
  on nat;
  promptstring!*:=ps$
 >>
end$

symbolic procedure remove_idl$
<<for each h in idnties_ do setprop(h,nil);
  idnties_:=nil>>$

symbolic procedure start_history(pdes)$
begin scalar l,ps$
 ps:=promptstring!*$
 promptstring!*:=""$
 write"For recording the history of equations all currently"$ terpri()$
 write"recorded histories would be deleted as well as all"$ terpri()$
 write"present decoupling information, i.e. `dec_with'"$ terpri()$
 write"would be set to nil. Please confirm (y/n). "$
 l:=termread()$
 if (l='y) or (l='Y) then <<
  record_hist:=t;
  for each l in pdes do put(l,'histry_,l)$
  for each l in pdes do put(l,'dec_with,nil)$
 >>;
 promptstring!*:=ps$
end$

symbolic procedure stop_history(pdes)$
<<record_hist:=nil;
  for each l in pdes do put(l,'histry_,l)>>$

%  write"Do you want to delete all dec_with information? (y/n) "$
%  l:=termread()$
%  if (l='y) or (l='Y) then
%  for each l in pdes do put(l,'dec_with,nil)$

symbolic procedure idty_integration(argset)$
begin scalar l,pdes,idcp;
 pdes:=car argset;
 idcp:=idnties_;
 while idcp do
 if not flagp(car idcp,'to_int) then idcp:=cdr idcp else
 if l:=integrate_idty(car idcp,pdes,%cadr argset,
                      ftem_,vl_) then <<
  pdes:=l;idcp:=nil>>                                      else <<
  remflag1(car idcp,'to_int);
  idcp:=cdr idcp;
 >>;
 if l then return {pdes,cadr argset}
end$

symbolic procedure integrate_idty(org_idty,allpdes,%forg,
                                  fl,vl)$
% idty is a differential identity between equations
% allpdes, fl, vl are lisp lists of equation names, functions and variables
% ways to optimize: use conlaw instead of the initial intcurrent2
%                   use more general methods to take advantage of
%                   non-conservation laws
if idnties_ then
begin scalar cl,ncl,vlcp,xlist,eql,a,f,newpdes,ftem_bak,
             nx,dl,l,k,ps,idty,pdes,extrapdes,newidtylist$ %nclu
 if null org_idty then
 if null cdr idnties_ then org_idty:=car idnties_
                      else <<
  show_id()$
  ps:=promptstring!*$
  promptstring!*:=""$
  write"Which of the identities shall be integrated? (no) "$
  k:=length(idnties_);
  repeat
   l:=termread()
  until (fixp l) and (0<l) and (l<=k);
  org_idty:=nth(idnties_,k+1-l)$
  promptstring!*:=ps
 >>$

 idty:=reval num reval get(org_idty,'val)$
 if trivial_idty(allpdes,idty) then return nil$

 pdes:=smemberl(allpdes,idty)$
 a:=all_deriv_search(idty,pdes)$
 xlist:=smemberl(vl,a)$
 cl:=intcurrent3(idty,cons('LIST,pdes),cons('LIST,xlist))$
 % intcurrent3 is only successful if only 2 derivatives found
 if (not zerop caddr cl) and inter_divint then
 cl:=intcurrent2(idty,cons('LIST,pdes),cons('LIST,xlist))$
 if zerop caddr cl then <<
  cl:=cdadr cl;
  vlcp:=xlist;
  xlist:=nil;
  while vlcp do <<
   if not zerop car cl then <<
    ncl:=cons(car cl,ncl);
    xlist:=cons(car vlcp,xlist)
   >>;
   cl:=cdr cl;
   vlcp:=cdr vlcp
  >>;
%  ncl:=reverse ncl;
%  xlist:=reverse xlist;

  cl:=ncl;

%  % Now try to get a divergence in less differentiation variables.
%  % Each component of the divergence is tried to be written as
%  % a divergence in the other (right) variables
%  while ncl do <<
%   a:=intcurrent2(car ncl,cons('LIST,pdes),cons('LIST,cdr xlist))$
%   if not zerop caddr a then <<
%    cl:=cons(car ncl,cl);       ncl:=cdr ncl;
%    vlcp:=cons(car xlist,vlcp); xlist:=cdr xlist
%   >>                   else <<
%    % It was possible to integrate car ncl to div(cdadr a,cdr xlist).
%    % distribute {'DF,car a,car xlist} to the divergence of cdr ncl
%    ncl:=cdr ncl;
%    a:=cdadr a;
%    nclu:=nil;
%    while ncl do <<
%     nclu:=cons(reval {'PLUS,car ncl,{'DF,car a,car xlist}}, nclu);
%     ncl:=cdr ncl;
%     a:=cdr a
%    >>;
%    ncl:=reverse nclu;
%    xlist:=cdr xlist
%   >>
%  >>$
%  ncl:=cl;
%  xlist:=vlcp;
  nx:=length xlist;
  while pdes do <<
   ncl:=subst(get(car pdes,'val),car pdes,ncl);
   pdes:=cdr pdes
  >>$
  ftem_bak:=ftem_;
  eql:=int_curl(reval cons('LIST,ncl),  cons('LIST,fl),
                      cons('LIST,xlist),cons('LIST,varslist(ncl,ftem_,vl)) )$
  % eql has the form {'LIST,reval cons('LIST,resu),cons('LIST,neweq)}
  if (null eql) or (null cdadr eql) or (zerop cadadr eql) then return nil;
  eql:=cdr eql;
  if print_ then <<
   ncl:=for i:=1:nx collect {'DF,nth(cl,i),nth(xlist,i)};
   ncl:=if cdr ncl then cons('PLUS,ncl)
                   else car ncl;
   terpri()$
   write"The identity "$
   % mathprint idty$
   mathprint reval ncl;
   write"can be integrated to "$terpri()$
   deprint(cdar eql)$
  >>$
  if nx < 3 then a:='y else
  if (null inter_divint) or !*batch_mode then <<
   a:='n;
   if print_ then <<
    write"The integrated divergence is not used because it ",
          "has more than 2 terms and"$    terpri()$
    if !*batch_mode then write"`inter_divint' is nil."
                    else write"batch_mode is on."$
   >>$
   terpri()
  >>                   else <<
   ps:=promptstring!*$
   promptstring!*:=""$
   write"Shall this integration be used? (y/n) "$
   repeat a:=termread() until (a='y) or (a='n);
   promptstring!*:=ps
  >>;
  if a='n then <<
   a:=setdiff(ftem_,ftem_bak);
   for each f in a do drop_fct(f)$
   ftem_:=ftem_bak
  >>      else <<
   % the extra conditions from the generalized integration:
   extrapdes:=cdadr eql$
   eql:=cdar eql; % eql are now the integrated curl conditions
   drop_idty(org_idty)$
   while eql do <<
    if not zerop car eql then <<
     a:=mkeq(car eql,ftem_,vl,allflags_,nil,list(0),nil,allpdes);
     newpdes:=cons(a,newpdes);
    >>;
    eql:=cdr eql;
   >>;
   newpdes:=reverse newpdes;
   % formulate the new identities
   for i:=1:nx do <<
    idty:=nth(cl,i);
    if nx=1 then a:=car newpdes
            else <<
     % at first sum over df(q^{ji},j),  j<i
     l:=i-1;
     dl:=nx-2;
     a:=for j:=1:(i-1) collect <<
      k:=l;
      l:=l+dl;
      dl:=sub1 dl;
      {'DF,nth(newpdes,k),nth(xlist,j)}
     >>;
     a:=if null a then 0 else
        if cdr a then cons('PLUS,a)
                 else car a;
     idty:={'PLUS,idty,a};
     % then sum over -df(q^{ij},j), j>i
     if i=1 then l:=1
            else l:=k+nx-i+1;
     a:=for j:=(i+1):nx collect <<
      k:=l;
      l:=l+1;
      {'DF,nth(newpdes,k),nth(xlist,j)}
     >>;
     a:=if null a then 0 else
        if cdr a then cons('PLUS,a)
                 else car a;
    >>$
    newidtylist:=cons({'DIFFERENCE,idty,a},newidtylist);
   >>;
   eql:=nil;
   for each a in extrapdes do <<
    a:=mkeq(a,ftem_,vl,allflags_,t,list(0),nil,allpdes);
    allpdes:=eqinsert(a,allpdes);
    to_do_list:=cons(list('subst_level_35,%allpdes,forg,vl_,
                          list a),
                     to_do_list);
    eql:=cons(a,eql)
   >>;
   if print_ then <<
    write"Integration gives: "$
    listprint(newpdes)$terpri()$
    if eql then <<
     write"with extra conditions: "$
     listprint(eql)
    >>$
   >>;
   for each a in newpdes do allpdes:=eqinsert(a,allpdes)$
   % now that allpdes is updated:
   for each a in newidtylist do new_idty(a,allpdes,t)$
   return allpdes
  >>
 >>
end$

symbolic procedure sortpermuli(a)$
% a is a list of numbers to be sorted and the exchanges of neighbours
% are to be counted
begin scalar flp,conti,newa;
 repeat <<
  newa:=nil;
  conti:=nil;
  while cdr a do
  if car a < cadr a then <<newa:=cons(car a,newa); a:=cdr a>>
                    else <<
   conti:=t;
   flp:=not flp;
   newa:=cons(cadr a,newa);
   a:=cons(car a,cddr a);
  >>$
  newa:=cons(car a,newa);
  a:=reverse newa
 >> until null conti;
 return flp . a
end$

symbolic procedure curlconst(xlist,vl)$
% generates a list q^ij=r^ijk,_k with r^ijk totally antisymmetric
% in the order q^(n-1)n,...
% xlist is the list of xi,xj,xk
% vl is the list of all variables new functions should depend on

begin scalar n,qli,i,j,k,qij,a,flp,f,resu,qlicp$
 n:=length xlist$
 for i:=1:(n-1) do
 for j:=(i+1):n do << % generation of r^ijk,k
  qij:=nil;
  for k:=1:n do
  if (k neq i) and (k neq j) then <<
   a:=sortpermuli({i,j,k});
   flp:=car a;
   a:=cdr a;
   qlicp:=qli;
   while qlicp and (caar qlicp neq a) do qlicp:=cdr qlicp;
   if qlicp then f:=cdar qlicp
            else <<
    f:=newfct(fname_,vl,nfct_);
    nfct_:=add1 nfct_;
    ftem_:=fctinsert(f,ftem_);
    qli:=cons(a . f,qli)
   >>;
   f:={'DF,f,nth(xlist,k)};
   if flp then f:={'MINUS,f};
   qij:=cons(f,qij)
  >>$
  if null qij then <<qij:=newfct(fname_,setdiff(vl,xlist),nfct_);
                     nfct_:=add1 nfct_;
                     ftem_:=fctinsert(qij,ftem_)>>
              else
  if cdr qij then qij:=reval cons('PLUS,qij)
             else qij:=car qij;
  resu:=cons(qij,resu)
 >>$
 return resu
end$

symbolic procedure updt_curl(h2,rmdr,fl,done_xlist,x,cdrxlist,n,k)$
% a subroutine of int_curl
begin scalar i,h4,h5,h6,h7,rmdr,y,pint,succ$
 if (not zerop reval reval {'DF,rmdr,x}) then <<
  if print_ then <<terpri()$write"No success."$terpri()>>$
  succ:=nil
 >>                                      else <<
  succ:=t;
  if done_xlist then << % there is one computed curl component to be updated
   % integration wrt done_xlist
   h7:=intcurrent2(rmdr,fl,cons('LIST,done_xlist));
   rmdr:=caddr h7;
   h7:=cdadr h7;
   % update the already computed h2-components with the new h7-comp.
   h4:=nil;
   h5:=-1;
   for i:=1:(k-1) do <<
    h5:=add1 h5;
    for h6:=1:(n-k) do <<h4:=cons(car h2,h4);h2:=cdr h2>>;
    h4:=cons({'DIFFERENCE,car h2,car h7},h4);
    h2:=cdr h2;
    h7:=cdr h7;
    for h6:=1:h5 do <<h4:=cons(car h2,h4);h2:=cdr h2>>
   >>;
   h2:=reverse h4;
  >>$
  % now generalized integration of the remainder
  if zerop rmdr then pint:=cons(0,nil)
                else <<
   y:=if cdrxlist then car cdrxlist
                  else car done_xlist;
   fnew_:=nil$
   pint:=partint(rmdr,fl,vl_,y,genint_);
   % genint is max number of new terms
   if null pint then succ:=nil
                else for each h4 in fnew_ do ftem_:=fctinsert(h4,ftem_)
  >>
 >>;
 return if null succ then nil
                     else cons(h2,pint)
 % pint=cons(generalized integral of rmdr,list of new eqn)
end$


symbolic procedure int_curl(pli,fl,xlist,vl)$
% given a vector p^i satisfying p^i,_i=0, find q^{ij}=-q^{ji}
% such that p^i=q^{ij},j
% car result: (q^{12}, q^{13},.., q^{1n}, q^{23},.., q^{2n},.., q^{(n-1)n})
%             each q^{ij} comes with r^{ijk},k
% cdr result: list of new conditions in fewer variables
% works only if identically satisfied, not modulo some equations
% vl is the list of all relevant variables
% during computation is h2 =
% (q^{kn},.., q^{k(k+1)},q^{(k-1)n},.., q^{(k-1)k},..,
%  q^{2n},.., q^{23},    q^{1n},.., q^{13},q^{12}}     )
begin scalar h1,h2,h3,resu,newpli,xcp,done_xlist,n,k,ok,neweq,ftem_bak$
 % conversion from algebraic mode lists to lisp lists:
 pli:=cdr pli$ xlist:=cdr xlist$ vl:=cdr vl; xcp:=xlist$
 n:=length(xlist);
 k:=0;
 ok:=t;
 ftem_bak:=ftem_;
 if n=1 then return {'LIST,reval cons('LIST,pli),{'LIST}}$

 while cdr pli and ok do <<
  k:=add1 k;
  % the integration has to be done first wrt cdr xlist. The resulting
  % curl will be used to change the remining pli to be integrated
  h3:=intcurrent2(reval car pli,fl,cons('LIST,cdr xlist));
  pli:=cdr pli;
  h1:=cdadr h3;
  h3:=reval reval caddr h3;
  % h3 now = the remainder of the integration wrt cdr xlist

  if not zerop h3 then <<
   % here the integration wrt the done_xlist. These curl updates will
   % not be used to update pli, because df(h3,car xlist)=0 is assumed
   h3:=updt_curl(h2,h3,fl,done_xlist,car xlist,cdr xlist,n,k)$
   if null h3 then ok:=nil
              else <<      % generalized integration of the remainder
    neweq:=append(cddr h3,neweq);
    h2:=car h3;
    h1:=cons({'PLUS,car h1,cadr h3},cdr h1);
    % because of cdr xlist neq nil here q^{k(k+1)} is updated
   >>
  >>$

  if ok then << % In the first round h2 is naturally nil --> use ok for test
   % append (q^{kn},.., q^{k(k+1)}) and h2
   h2:=append(reverse h1,h2);
   % update the remaining pli to be integrated
   newpli:=nil;
   while h1 do <<
    newpli:=cons({'PLUS,{'DF,car h1,car xlist},car pli},newpli);
    h1:=cdr h1;
    pli:=cdr pli
   >>;
   pli:=reverse newpli
  >>;
  done_xlist:=cons(car xlist,done_xlist);
  xlist:=cdr xlist
 >>$
 if ok then <<
  pli:=reval car pli;
  % to get the remainder of the last component of pli integrated
  if pli neq 0 then <<
   k:=k+1;
   h3:=updt_curl(h2,pli,fl,done_xlist,car xlist,nil,n,k)$
   if null h3 then ok:=nil
              else <<
    neweq:=append(cddr h3,neweq);
    h2:=car h3;
    h2:=cons({'DIFFERENCE,car h2,cadr h3},cdr h2)
    % because of null xlist here car h2=q^{n-1,n} is updated
   >>$
  >>
 >>;
 if null ok then << % drop all new functions
  h1:=setdiff(ftem_,ftem_bak);
  for each h2 in h1 do drop_fct(h2)$
  ftem_:=ftem_bak
 >>         else <<
  h1:=curlconst(xcp,vl)$
  while h1 do <<
   resu:=cons({'PLUS,car h2,car h1},resu);
   h1:=cdr h1;
   h2:=cdr h2;
  >>$
  return {'LIST,reval cons('LIST,resu),cons('LIST,neweq)}
 >>
end$

endmodule$

end$
