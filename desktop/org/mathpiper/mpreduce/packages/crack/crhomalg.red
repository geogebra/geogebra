%********************************************************************
module homalgsys$
%********************************************************************
%  Routines for the efficient solution of homogeneous algebraic systems
%  Author: Thomas Wolf
%  May 2000

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


% extra global variables:
symbolic fluid '(tr_hom_alg)$

lisp(tr_hom_alg:=t)$

algebraic procedure bi_lin_expt(p)$
if p=1 then 0 else
if  arglength(p)<2 then 1 else
if (arglength(p)=2) and (part(p,0)=EXPT) then part(p,2)
                                         else write"error!"$

algebraic procedure find_hom_deg(p)$
% returns {dg1,dg2} where dg1 is the degree of     flin_ functions
%                         dg2 is the degree of non-flin_ functions
begin scalar tm,f$
 % take first term only
 if (arglength(p)<0) or (part(p,0) neq plus) then tm:=p
                                             else tm:=part(p,1)$
 l1:=lisp gensym()$
 l2:=lisp gensym()$
 sb:=append(for each f in lisp(cons('LIST,              flin_ )) collect f=f*l1,
            for each f in lisp(cons('LIST,setdiff(ftem_,flin_))) collect f=f*l2)$
 tm:=sub(sb,tm)/tm$

 if freeof(tm,l1) then <<
  dg1:=0;
  dg2:=bi_lin_expt(tm);
 >>               else
 if freeof(tm,l2) then <<
  dg1:=bi_lin_expt(tm);
  dg2:=0;
 >>               else
 if (arglength(tm)=2) and (part(tm,0)=TIMES) then
 if freeof(part(tm,1),l2) then <<dg1:=bi_lin_expt(part(tm,1));
                                 dg2:=bi_lin_expt(part(tm,2)) >>
                          else <<dg1:=bi_lin_expt(part(tm,2));
                                 dg2:=bi_lin_expt(part(tm,1)) >>
                                             else write"error3";
 % The next two lines are a test whether the equation is still
 % homogeneous. This is currently commented out to save time.
 % tm:=sub(sb,p)/l1**dg1/l2**dg2$
 % if tm neq p then write"INHOMOGENEOUS!"$

 return {dg1,dg2}
end$

symbolic operator make_hom_ansatz$
symbolic procedure make_hom_ansatz(f_1,f_2,d1,d2)$
begin scalar ans,ans1,ans2,h,fl,rply;

 if null f_1 then ans1:=1 else
 if null cdr f_1 then ans1:={'EXPT,car f_1,d1} else
 ans1:={'EXPT,cons('PLUS,f_1),d1}$

 if null f_2 then ans2:=1 else
 if null cdr f_2 then ans2:={'EXPT,car f_1,d2} else
 ans2:={'EXPT,cons('PLUS,f_2),d2}$

 ans:=reval {'TIMES,ans1,ans2}$

 return
 if (not pairp ans) or (car ans neq 'PLUS) then <<
  h:=gensym();
  {'LIST,{'TIMES,h,ans},{'LIST,h}}
 >>                                        else <<
  ans:=cdr ans$
  for each f in ans do <<
   h:=gensym()$
   fl:=cons(h,fl)$
   rply:=cons({'TIMES,h,f},rply)
  >>$
  {'LIST,cons('PLUS,rply),cons('LIST,fl)}
 >>
end$

symbolic procedure bi_lin_sep(p,fl_1,fl_2)$
% separation of p wrt all fl_1, fl_2
begin scalar fl_1,fl_2,f1,f2,f,su,pcp,sepli,cnd;

 for each f1 in fl_1 do <<
  if print_ then write "Separation wrt. ",f1,". "$
  su:=nil;
  for each f in fl_1 do
  su:=cons(if f=f1 then {'EQUAL,f,1}
                   else {'EQUAL,f,0},su);
  su:=cons('LIST,su);
  pcp:=algebraic(sub(su,p));
  if print_ then <<write "Substitution done."$terpri()>>$

  for each f2 in fl_2 do <<
   su:=nil;
   for each f in fl_2 do
   su:=cons(if f=f2 then {'EQUAL,f,1}
                    else {'EQUAL,f,0},su);
   su:=cons('LIST,su);
   cnd:=algebraic(sub(su,pcp));
   if pairp cnd and car cnd='MINUS then cnd:=cadr cnd;
   sepli:=union(list cnd,sepli);

  >>$
 >>$
 return cons('LIST,sepli)
end$

symbolic procedure bi_lin_eqn_lin_comb(pdes)$
% generates a linear combination of all pdes
% returns also list of unknown coefficients
begin scalar p,fl,rs,h$
 if print_ then <<
  write "Formulating a linear combination of all equations."$
  terpri()
 >>$
 for each p in pdes do
 <<h:=p$  % gensym()$
   fl:=cons(h,fl)$
   rs:=cons({'TIMES,h,get(p,'val)},rs)
 >>$
 rs:=cons('PLUS,rs)$
 return {'LIST,reval rs,cons('LIST,fl)}
end$

symbolic procedure drop_dep_bi_lin(arglist)$
begin scalar pdes,cnd,fl,f,cndcp,c,linde,again,fl_1,fl_2$
 pdes:=car arglist$
 again:=t$
 fl_1:=flin_;
 fl_2:=setdiff(ftem_,flin_);
 if pdes and cdr pdes then
 repeat <<
  again:=nil$
  cnd:=bi_lin_eqn_lin_comb(pdes)$
  fl:=caddr cnd$
  cnd:=bi_lin_sep(cadr cnd,fl_1,fl_2)$
  if print_ then <<write"Now solving the linear system."$terpri()>>$
  !!arbint:=0;
  cnd:=cadr solveeval list(cnd,fl)$

  for f:=1:!!arbint do <<
   cndcp:=cnd;
   for c:=1:!!arbint do
   if c neq f then cndcp:=algebraic(sub(arbcomplex(c)=0,cndcp));
   cndcp:=cdr cndcp;

   while cndcp and
         ((zerop caddar cndcp) or
          (not freeof(linde,cadar cndcp))) do cndcp:=cdr cndcp;
   if null cndcp then <<
    write"The computation to find redundant equations has to be done again."$
    terpri()
   >>            else linde:=cons(reval cadar cndcp,linde);
  >>$
  if null linde then write"No equations deleted."
                else write"Deleted redundant equations: ",linde$
  terpri()$
  for each f in linde do pdes:=drop_pde(f,pdes,nil)$
  if again then <<
   write"This computation has to be repeated"$
   terpri()
  >>
 >> until null again;

 return cons(pdes,cdr arglist)
end$

symbolic procedure find_factor_bi_lin(arglist)$
begin scalar ps,h,pdes,fc,rhs,lhs,lhsfl,cnd,cndcp,fl,fl_1,fl_2,
      hdg,dg1,dg2,indx1,indx2,again$

 write"Before starting to determine factorizable equations with "$terpri()$
 write"a given factor, all redundant equations have to be dropped."$terpri()$
 write"Has this already been done? (y/n) "$

 ps:=promptstring!*$
 promptstring!*:=""$
 repeat h:=termread() until (h='y) or (h='n)$
 If h='n then arglist:=drop_dep_bi_lin(arglist)$
 terpri()$

 fl_1:=flin_;
 fl_2:=setdiff(ftem_,flin_);
 pdes:=car arglist$
 write"Start of determining factorizable equations."$terpri()$
 repeat <<

  write"Give a factor of the equations to be found: (terminate with ; ) "$
  terpri()$
  fc:=termxread()$

  if null rhs then <<
   rhs:=bi_lin_eqn_lin_comb(pdes)$
   fl:=caddr rhs;  % alg mode list
   rhs:=cadr rhs;  % expression
  >>$

  hdg:=cdr algebraic find_hom_deg(fc)$
  dg1:=1-car  hdg$
  dg2:=1-cadr hdg$
  lhs:=make_hom_ansatz(fl_1,fl_2,dg1,dg2)$
  lhsfl:=caddr lhs;
  lhs:=cadr lhs$

  cnd:=bi_lin_sep(algebraic(fc*lhs-rhs),fl_1,fl_2)$
  if print_ then <<write"Now solving the linear system."$terpri()>>$
  !!arbint:=0;
  cnd:=cadr solveeval list(cnd,cons('LIST,append(cdr lhsfl,cdr fl)))$
  lhs:=algebraic(sub(cnd,lhs));

  for indx1:=1:!!arbint do <<
   cndcp:=lhs;
   for indx2:=1:!!arbint do
   if indx2 neq indx1 then cndcp:=algebraic(sub(arbcomplex(indx2)=0,cndcp))
                      else cndcp:=algebraic(sub(arbcomplex(indx2)=1,cndcp))$
   if not zerop cndcp then <<
    cndcp:={'TIMES,fc,cndcp}$

    pdes:=eqinsert(h:=mkeq(cndcp,ftem_,vl_,allflags_,t,list(0),nil,pdes),pdes)$
    if h and not freeof(pdes,h) then <<

% Drop one redundant equation

     write"New equation: ",h$ mathprint get(h,'val)$
     rhs:=nil;
    >>
   >>
  >>;
  write"Do you want to find further factorizable equations ",
       "with other factors? (y/n) "$
  repeat h:=termread() until (h='y) or (h='n)$
  If h='y then again:=t
          else again:=nil$
 >> until null again$

 promptstring!*:=ps$
 return cons(pdes,cdr arglist)
end$

endmodule$

end$

