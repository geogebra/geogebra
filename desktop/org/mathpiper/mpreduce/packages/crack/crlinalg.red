%********************************************************************
module linalgsys$
%********************************************************************
%  Routines for the memory efficient solution of linear algebraic systems
%  Author: Thomas Wolf
%  December 1998

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


symbolic fluid '(count_tries tr_subsys max_losof matrix_849)$
lisp(tr_subsys:=nil)$

symbolic procedure trian_lin_alg(arglist)$
if not lin_problem then nil else
begin scalar h1,h2,h3,h4,f,fl,newfl,tr_opt,remain_pdes,remain_fl,li,
             total_terms;

 tr_opt:=t;

 % get a list h1 of purely algebraic equation by disregarding the
 % non-algebraic equations
 h2:=car arglist;
 while h2 do <<
  if is_algebraic(car h2) then h1:=cons(car h2,h1);
  h2:=cdr h2
 >>;

% Just for testing spot_over_det():
spot_over_det(h1,nil,nil,nil)$
write "count_tries=",count_tries; terpri()$
return nil;

 % start with reducing the length of all equations as much as possible
 repeat <<
  h2:=alg_length_reduction({h1,nil,vl_,h1});
  % nil for forg which is not used in alg_length_reduction()
  if h2 then h1:=car h2
 >> until contradiction_ or null h2;

 remain_pdes:=h1;
 total_terms:=0;
 for each h2 in remain_pdes do total_terms:=total_terms+get(h2,'terms);

 % fl now becomes a list of lists: ((n1,f1,d11,d12,d13,..),
 % (n2,f2,d21,d22,d23,...),...) where fi are the functions,
 % dij are equation names in which fi occurs and ni is the number of dij
 for each h2 in h1 do fl:=add_equ_to_fl(h2,fl)$

 % newfl is the final newly ordered list of functions
 while fl and null contradiction_ do <<
  % re-order all functions, those occuring in the fewest equations
  % come first
  fl:=idx_sort fl;
  if tr_opt then <<terpri()$write"fl2="$prettyprint fl>>$
  if caar fl = 1 then << % the first function occurs in only one eqn.
   % If a function occurs in only one equation then drop the function
   % and the equation from all functions in fl
   while caar fl leq 1 do <<
    if tr_opt and (caar fl = 1) then <<
     write"equation ",caddar fl," determines ",cadar fl$terpri()
    >>$
    newfl:=cons(cadar fl,newfl);
    fl:=if caar fl = 0 then cdr fl
                       else <<remain_pdes:=delete(caddar fl,remain_pdes);
                              total_terms:=total_terms-get(caddar fl,'terms);
                              fl:=del_equ_from_fl(caddar fl,cdr fl)>>
   >>;
  >>             else << % all remaining functions occur in at least 2 eqn.
   % Find a subsystem of equations that has less or equally many
   % functions as equations
   % ...

   % Find a function which is easiest decoupled/substituted
   %            (e.g. use min-growth-substitution for that)
   remain_fl:=for each h3 in fl collect cadr h3;

   % update 'fcteval_lin for all equations. This is a preparation to
   % find the cheapest substitution
   for each h1 in remain_pdes do <<
    h2:=get(h1,'fcteval_lin)$
    li:=nil;
    if null h2 then << % assign all allowed subst.
     for each f in remain_fl do
     if not freeof(get(h1,'rational),f) then
     li:=cons(cons(reval coeffn(get(h1,'val),f,1),f),li);
    >>        else << % keep only substitutions related to fl-functions
     while h2 do <<
      if not freeof(cdar h2,remain_fl) then li:=cons(car h2,li);
      h2:=cdr h2
     >>
    >>;
    if li then put(h1,'fcteval_lin,reverse li);
   >>;

   % Do the substitution with the lowest upper bound of increase in complexity
   % make_subst(pdes,forg,vl,l1,length_limit,pdelimit,less_vars,no_df,no_cases,
   %            lin_subst,min_growth,cost_limit,keep_eqn)$
   h1:=make_subst(remain_pdes,remain_fl,vl_,remain_pdes,
                  nil,nil,nil,nil,t,t,t,nil,t,nil)$
   if null contradiction_ and h1 then << % update all data
    h2:=caddr h1; % h2 was used for substitution
    h3:=total_terms-get(h2,'terms)$
    remain_pdes:=delete(h2,car h1);
    total_terms:=0;
    for each h4 in remain_pdes do total_terms:=total_terms+get(h4,'terms);
    if tr_opt then <<
     write"equation ",h2," now disregarded"$ terpri()$
     write"growth: ",total_terms-h3," terms"$terpri()$
     write length remain_pdes," remaining PDEs: ",remain_pdes$ terpri()$
    >>$
    fl:=del_equ_from_fl(h2,fl);
    h2:=cadr h1;
    while (not pairp car h2) or (caar h2 neq 'EQUAL) do h2:=cdr h2;
    f:=cadar h2$
    remain_fl:=delete(f,remain_fl);
    if tr_opt then <<
     write length remain_fl," remaining functions: ",remain_fl$ terpri()$
    >>$

    % Drop the entry for function f from fl. h4 is the list of
    % equations with f
    if cadar fl = f then <<h4:=cddar fl;fl:=cdr fl>>
                    else <<
     h3:=fl;
     while cadadr h3 neq f do h3:=cdr h3;
     h4:=cddadr h3;
     rplacd(h3,cddr h3);
    >>;
    % update the appearance of equations in fl in which f was substituted
    for each h3 in h4 do <<
     fl:=del_equ_from_fl(h3,fl);
     if not freeof(remain_pdes,h3) then fl:=add_equ_to_fl(h3,fl)
    >>$

    % Have length reductions become possible through substitution?
    repeat <<
     h2:=alg_length_reduction({remain_pdes,nil,vl_,remain_pdes});
     % nil for forg which is not used in alg_length_reduction()
     if h2 then <<
      % update fl:
      % at first deleting dropped redundand equations from fl
      h3:=setdiff(remain_pdes,car h2);
      for each h4 in h3 do fl:=del_equ_from_fl(h4,fl);
      remain_pdes:=car h2;
      % now updating the entry for the changed equations
      for each h3 in caddr h2 do <<
       fl:=del_equ_from_fl(h3,fl);
       if not freeof(remain_pdes,h3) then fl:=add_equ_to_fl(h3,fl)
      >>
     >>
    >> until contradiction_ or null h2;

   >>    else rederr("make_subst=nil, what now???");
  >>
 >>$
 if newfl neq ftem_ then
 change_fcts_ordering(newfl,car arglist,vl_)
% clear dec_with????
end$

endmodule$

end$



