%********************************************************************
module decoupling$
%********************************************************************
%  Routines for decoupling de's
%  Author: Andreas Brand untill 1995,
%          updates and extensions by Thomas Wolf
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


symbolic procedure which_deriv(p,q)$
%  yields a list of variables and orders
%  such that one gets at least q by differentiating p w.r.t. the vars
%  p,q: lists of variables and orders
begin scalar l,n,a$
while q do
   if (a:=member(car q,p)) then
      <<q:=cdr q$
      if q and numberp(car q) then
               <<n:= car q$
         q:=cdr q>>
      else n:=1$
      n:=n-(if pairp cdr a and numberp cadr a then cadr a else 1)$
      if n>0 then
               <<l:=cons(car a,l)$
         if n>1 then l:=cons(n,l)>> >>
   else
      <<l:=cons(car q,l)$
      q:=cdr q$
      if q and numberp(car q) then
         <<l:=cons(car q,l)$
         q:=cdr q>> >>$
return append(reverse l,q)$
end$

symbolic procedure dec_ld_info(p,q,simpp,simpq,f,vl,rl)$
%  gets leading derivatives of f in p and q wrt. vars order vl
%  and the lists of variables and orders for differentiation
begin scalar s,l,l1,l1d,l2,l2d,vl1,vl2,d1,d2,ld1,ld2,wd1,wd2,
             caar_ld,found$
 %
 % if (p has more variables than q) or
 %    (f is not leading function of p)
 % => simpp = t => p must be simplified with (deriv.s of) q
 % if (q has more variables than p) or
 %    (f is not leading function of q)
 % => simpq = t => q must be simplified with (deriv.s of) p
 %
 % vl1 holds the list of _ordered_ variables of p
 % vl2 holds the list of _ordered_ variables of q
 %
 % list all powers of derivatives of f in p as l1 and in q as l2
 %

 if simpp and simpq then return nil$

 vl1:=intersection(vl,get(p,'vars))$
 vl2:=intersection(vl,get(q,'vars))$

 % collect all powers of all derivatives of f
 %
 for each a in get(p,'derivs) do if caar a=f then l1:=cons(a,l1)$
 l1:=sort_derivs(reverse l1,list f,vl1)$
 %
 % l1 is a list of _all_ derivatives of f in p _sorted_ stored as a
 % dotted pair, e.g. ((f x 2 y) . 5) would be f_{xxy}^5, or more
 % generally ((f_1 . power) (f_2 . power) ... )
 %
 %terpri()$write "l10=",l1$
 %
 % keep only highest power of each derivative in l1
 l:=nil$
 for each a in l1 do if not member(cdar a,l) then <<
  l:=cons(cdar a,l)$
  l1d:=cons(list(cdar a,absdeg(cdar a),cdr a),l1d)
 >>$
 %
 % cdar a is the list of derivatives so we are making sure that our
 % list l1 has no repetitions
 %
 l1 :=reverse l$   % e.g. l1  = ( (x 2 y)       (x y 2)      ...)
 l1d:=reverse l1d$ % e.g. l1d = (((x 2 y),3,1) ((x y 2),3,2) ...)

 %
 % The above now applies but with q and l2 instead of p and l1
 %
 % collect all powers of all derivatives of f
 %
 for each a in get(q,'derivs) do if caar a=f then l2:=cons(a,l2)$
 l2:=sort_derivs(reverse l2,list f,vl2)$
 %terpri()$write "l20=",l2$
 % keep only highest power of each derivative in l2
 l:=nil$
 for each a in l2 do if not member(cdar a,l) then <<
  l:=cons(cdar a,l)$
  l2d:=cons(list(cdar a,absdeg(cdar a),cdr a),l2d)
 >>$
 l2 :=reverse l$   % e.g. l2 =  ( (x 2 y)       (x y 2)      ...)
 l2d:=reverse l2d$ % e.g. l2d = (((x 2 y),3,1) ((x y 2),3,2) ...)

 % At this point we have two lists, l1d and l2d resp. containing the
 % sorted list of all derivatives of the function f in p and q
 % together with their highest power

 % At first we note the leading derivative in l1d with its power
 % and check whether there is a derivative in l2d which has in no variable
 % a lower derivative or and either has a higher derivative in at least
 % one variable, or is not of lower degree.

 if not simpp then <<
   %p may be differentiated and q be substituted or new equ. added
   caar_ld:=caar l1d$
   d1:=cadar  l1d$
   d2:=caddar l1d$
   l:=l2d$
   while l and ((d1<cadar l) or ((d1=cadar l) and (d2<=caddar l))) do
   <<s:=which_deriv(caar_ld,caar l)$
     %
     % which_deriv(a,b) takes two lists of derivatives and returns how
     % often you need to diff. a in order to get at least the
     % derivatives in b.
     % e.g. which_deriv((x 2 y), (x y 2)) returns y
     %
     if (absdeg s + d1)=cadar l then <<ld2:=caar l$ found:=t$ l:=nil>>
       % At this point we compare the degree of the highest
       % derivative of l1 + number of diff. in order to get the
       % leading deriv. of l2 (aliased to l)
                                else l:=cdr l
   >>
 >>$

 if simpq and null found then return nil;

 % Now, either l is nil and ld2 = leading deriv. of l2 (i.e. highest
 % deriv. of f in q) [this is the case in which leading deriv. in l2
 % can be obtained by diff. of the leading deriv. in l1] OR
 % ld2 is nil and l contains the rest of the deriv. of l2 except the
 % leading one [in this case we _cannot_ obtain the leading deriv. in
 % l2 by diff. the leading deriv. in l1].

 if (not ld2) and (not simpq) then <<
   %
   % We cannot get to the leading deriv. in l2 by diff. of leading
   % deriv. in l1.
   % We now try the opposite way, we try to diff. something in l2 to
   % get into l1.
   %
   caar_ld:=caar l2d$
   d1:=cadar  l2d$
   d2:=caddar l2d$
   l:=l1d$
   found:=nil$
   while l and ((d1<cadar l) or ((d1=cadar l) and (d2<=caddar l))) do
   <<s:=which_deriv(caar_ld,caar l)$
     if (absdeg s + d1)=cadar l then <<ld1:=caar l$ found:=t$ l:=nil>>
                                else l:=cdr l
   >>
 >>$

 if simpp and null found then return nil;

 % We now have either ld2 non-nil, i.e. we can get to leading derv. in
 % l2 by differentiation of terms in l1 OR we have ld1 non-nil in
 % which case we have the opposite situation. If neither are non-nil
 % then we have to cross-differentiate to get the ld to match.
 %
 % What we return is
 %
 % ( (s ld(l1)) (nil ld(l2)) )         [ld2 non-nil] or
 % ( (nil ld(l1)) (s ld(l2)) )         [ld1 non-nil] or
 % ( (v ld(l1)) (w ld(l2)) )           [both ld1 _and_ ld2 nil]
 %
 % where v and w are the required diff. to get to ld2 and ld1 resp.
 % and s is the required diff. for the non-nil cases.
 %
 % It is to be interpreted as:
 %
 % Either "diff. ld(l1) by s to get ld(l2)" or
 %               "diff. ld(l2) by s to get ld(l1)" or
 %               "diff. ld(l1) by wd1 and ld(l2) by wd2 to get the
 %               ld's to match".
 %

 return
 if ld2 then cons(cons(s,caar l1d),cons(nil,ld2)) else
 if ld1 then cons(cons(nil,ld1),cons(s,caar l2d)) else <<
   wd1:=which_deriv(caar l1d,caar l2d)$
   wd2:=which_deriv(caar l2d,caar l1d)$
   if (simpq and wd2) or
      (simpp and wd1) or
      (rl and wd1 and wd2) then nil
   else cons(cons(wd1,caar l1d),
             cons(wd2,caar l2d))
 >>
end$

symbolic procedure diffeq(f,sd,r)$
% input of how often equation r is to be differentiated
% sd is the resulting derivative that is to be substituted
% with another equation, eg   sd=(x,2,y)
begin scalar rdif,rd,contradic,a,ad,b,bd,resu,must_be_subst$
  terpri()$
  write"How often is equation ",r," to be differentiated?"$
  terpri()$
  write"(just `;' for no differentiation or, for example, `x,y,2;' ): "$
  rdif:=termlistread()$
  rd:=get(r,'derivs)$
  while rd and null contradic do <<
    a:=caar rd;  % only the differentiations, not the degree
    rd:=cdr rd$
    if f=car a then <<
      ad:=cdr a$
      if cdr a then a:=cons('DF,a)
               else a:=car a; % a is now the function/full derivative
      if null rdif then b:=a else
                        b:=reval cons('DF,cons(a,rdif));
      if pairp b then bd:=cddr b
                 else bd:=nil$
      % There must not result a derivative from differentiating
      % equation r which is a derivative of sd
      if zerop b then <<
        write "The function ",f," differentiated that way gives zero."$
        contradic:=t$
      >>         else
      if (null which_deriv(bd,sd)) and
               which_deriv(sd,bd)  then
      if null rdif then must_be_subst:=b
                   else <<
        contradic:=t$ % sd,r,rdif are not compatible
        terpri()$
        write"This differentiation of equation ",r,
             " will generate a derivative ",b$
        terpri()$
        write" which is a derivative of the derivative to be eliminated."$
        terpri()$
      >>                      else
      if bd = sd then resu:={r,rdif,ad}
    >>
  >>$
  return if contradic or null resu then nil
                                   else resu . must_be_subst
end$

symbolic procedure read_sub_diff(p,q)$
begin scalar ps,s,l0,l,m0,m1,f,sd,info_p,info_q,contradic,let_conflict$
  ps:=promptstring!*$
  promptstring!*:=""$
  terpri()$
  write"What is the derivative to be eliminated? "$
  write"(e.g.  df(f,x,y,2); or f; ) "$terpri()$
  l0:=termxread()$
  l:=reval l0$
  % tests whether the input l is ok
  if null l then return nil else
  if not pairp l then if l0 neq l then let_conflict:=t else
                 else % pairp l
  if car l neq 'DF then if car l0 neq 'DF then <<
    write"Not a derivative!"$ terpri()$
    return nil
  >>                                      else let_conflict:=t
                   else
  if cadr l neq cadr l0 then let_conflict:=t
                        else <<
   m0:=cddr l0; m1:=cddr l;
   while m1 and null let_conflict do
   if fixp car m1 then m1:=cdr m1 else <<
    if no_of_v(car m1,m1) neq no_of_v(car m1,m0) then let_conflict:=t;
    m1:=cdr m1
   >>
  >>$
  if let_conflict then <<
   write "Due to a LET-rule in operation this elimination ",
         "is not possible."$terpri()$
   write "To delete a LET-rule use 'cr'."$terpri()$
   return nil
  >>$

  if pairp l then <<f:=cadr l;sd:=cddr l>>
             else <<f:=l;sd:=nil>>$
  info_p:=diffeq(f,sd,p)$
  if info_p then info_q:=diffeq(f,sd,q)$

  promptstring!*:=ps$
  return
  if info_p and info_q then <<
    if null cadar info_p and cadar info_q then s:=p else
    if null cadar info_q and cadar info_p then s:=q else
    if cadar info_p and cadar info_q then s:=nil else <<
      terpri()$
      write"Which equation is to be substituted? Input ",p," or ",q,": "$
      repeat
      s:=reval termread()
      until (s=p) or (s=q)
    >>$
    if s=p and cdr info_q then <<
      contradic:=t$
      terpri()$
      write"The derivative ",cdr info_q," would enter ",p$
      terpri()$
      write" which is a derivative of the derivative to be substituted."$
    >>$
    if s=q and cdr info_p then <<
      contradic:=t;
      terpri()$
      write"The derivative ",cdr info_p," would enter ",q$
      terpri()$
      write" which is a derivative of the derivative to be substituted."$
    >>$
    if contradic then nil
                 else {car info_p,car info_q,l,s,nil} . 1
    % returns the same kind of result as dec_info
  >>                   else nil
end$

symbolic procedure dec_info(p,q,f,vl,rl,ordering)$
% yields information for a decouple reduction step
% i.e. ((info_1
%        info_2
%        deriv_to_eliminate
%        equ_to_be_subst
%        whether_one_equation_must_be_substituted % important for elim. techn.
%       ).num_value)
% where num_value is a measure of cost, e.g.
% result has form (((e4 (x 2 y) (y z))
%                   (e5 (z) (x 2 y 2)) (df f x 2 y 2 z) nil nil) . num_value)
% Criteria:   a) the function f must depend on all vars
%             b) the function and all their derivatives must occur
%                polynomially
begin scalar a,b,l,l1,info,m,n,fp,fq,fpmq,fqmp,s,lenp,lenq,dp,dq,
             simpp,simpq,let_conflict$
  %
  % 'length is the property containing the expression length
  %
  if expert_mode then return read_sub_diff(p,q)$

  lenp:=get(p,'length)$
  lenq:=get(q,'length)$
  if rl and ((lenp*lenq)>max_red_len) then return nil;

  a:=get(p,'vars); b:=get(q,'vars);
  simpp:=(null get(p,'allvarfcts)) or (f neq caaar get(p,'derivs))$
  simpq:=(null get(q,'allvarfcts)) or (f neq caaar get(q,'derivs))$
         % star-equn. or f is not leading function
  l:=dec_ld_info(p,q,simpp,simpq,f,vl,rl)$
  if not l then <<
    add_both_dec_with(ordering,p,q,rl)$
    return nil
  >>$
  %
  % l:= dec_ld_info(p,q,f,vl,rl) returns a list of lists, from which
  % a := caar l sets a to be the differentiations required to get
  % the ld(p) w.r.t. f to match that of ld(q) w.r.t. f,
  % b := caadr l sets b to be the differentiations required to get
  % the ld(q) w.r.t. f to match that of ld(q) w.r.t. f.
  %
  % l1 := cadadr l sets l1 to be the derivative in q which we
  % eliminate, similarly l is the derivative in p which we elim.
  %
  a:=caar l$             % a are the differentiations of p
  b:=cadr l$             % b are the differentiations of q
  if struc_eqn and
     ((a and b and (not freeof(algebraic struc_done,f))) or
      % no integrab. cond.s for functions in struc_done
      ((get(p,'no_derivs)>0) and (get(q,'no_derivs)=0))  or
      ((get(p,'no_derivs)=0) and (get(q,'no_derivs)>0))
      % not using algebr. conditions to simplify diff. cond.
     ) then return nil;
  l1:=cddr l$
  l:=cdar l$
  % Test whether there is a let-rule in operation which changes the
  % target derivative
  if (null a) and (null l) then
  if f neq reval f then let_conflict:=t
                   else    else <<
   m:=reval cons('DF,cons(f,append(l,a)));
   if (not pairp m) or
      (car m neq 'DF) or
      (cadr m neq f) then let_conflict:=t
                     else <<
    m:=cddr m$
    while m and null let_conflict do
    if fixp car m then m:=cdr m else <<
     if (no_of_v(car m,a)+no_of_v(car m,l)) neq
         no_of_v(car m,m)                       then let_conflict:=t;
     m:=cdr m
    >>
   >>
  >>$
  if let_conflict then <<
   if print_ then <<
    write "Due to a let-rule in operation equations ",
          p,",",q," will not be paired."$terpri()$
   >>$
   add_both_dec_with(ordering,p,q,rl)$
   return nil
  >>$

  % s is the equation to be substituted
  if a and not b then s:=q                 % p will be diff.
  else if b and not a then s:=p            % q will be diff.
  else if not (a or b) then                % no diff., only reduction
  if struc_eqn and l and l1 then <<
    % 2 structural equations, both with one or more derivatives
    % --> equation with more derivatives is substituted
    % The case below would work, only this may need fewer substitutions
    m:=get(p,'no_derivs)$
    n:=get(q,'no_derivs)$
    if m>n then s:=p else
    if m<n then s:=q else
%    if cons(f,l ) neq caar get(q,'derivs) then s:=q else
%    if cons(f,l1) neq caar get(p,'derivs) then s:=p else
    if get(p,'length)>get(q,'length) then s:=p
                                     else s:=q
  >>           else <<
    dp:=get(p,'derivs)$
    dq:=get(q,'derivs)$
    repeat <<
      s:=total_less_dfrel(car dp,car dq,ftem_,vl)$
      dp:=cdr dp$
      dq:=cdr dq
    >> until (s neq 0) or (null dp) or (null dq)$
    if (s=t) or ((null dp) and dq) then s:=q
                                   else s:=p
  >>$

  fp:=get(p,'allvarfcts)$ % functions of all vars in p
  fq:=get(q,'allvarfcts)$ % functions of all vars in q

  % If a pde will be replaced by a pde with more fcts of all vars
  % then this pairing will have a lowered priority
  fqmp:=length setdiff(fq,fp);
  fpmq:=length setdiff(fp,fq);
  if nil then
  if tr_decouple then << terpri()$
    write"p=",p," q=",q," s=",s," lfp=",length fp,
         " lfq=",length fq," lfu=",length union(fp,fq),
         " fqmp=",fqmp," fpmq=",fpmq
  >>$
  m:=(1.5^absdeg(a)*lenp+1.5^absdeg(b)*lenq)*
     (length union(fp,fq))**20$
  if nil then
  if tr_decouple then write" m2=",m;
  if s then <<
    % the equation s will be replaced by the new one
    % --> if (null struc_eqn) and fcteval(s,nil) then m:=m*10**7;
    % The above line has been commented out because fcteval takes
    % much time the first time it is called and substitutions
    % are to be done before decoupling anyway
    if (s=q) and (lenp>lenq) then m:=(m*lenp)/lenq else
    if (s=p) and (lenq>lenp) then m:=(m*lenq)/lenp;
    if (s=p) and (fqmp>0) then m:=m*10**(2*fqmp) else
    if (s=q) and (fpmq>0) then m:=m*10**(2*fpmq);
    if struc_eqn then
    if ((a and is_algebraic(p)) or
        (b and is_algebraic(q))    ) then m:=m*10**100 else
    if is_algebraic(p) and is_algebraic(q) then m:=m/10**5;
  >>   else
  % Enlarge m because extra equation is generated (temp. idea)
  m:=m*10$
  % Non-linearity in largest derivative not taken care of.
  if nil then
  if tr_decouple then write" m3=",m;
  info:=cons(list(list(p,a,l),
                  list(q,b,l1),
                  if (null a) and
                     (null l) then f
                              else reval cons('DF,cons(f,append(l,a))),
                  s,
                  simpp or simpq
                 ),
             m)$
  return info$
end$

%symbolic procedure dec_put_info(l,rl)$
%% l has form ((e4 (x 2 y) (y z))
%%             (e5 (z) (x 2 y 2)) (df f x 2 y 2 z) nil)
%% puts informations for decouple reduction step
%% result: ((df f x 2 y 2 z) e4 e5 nil)
%if l then
%begin scalar f$
%  put(caar l,'dec_info,cadar l)$     % saves (x 2 y) for e4
%  put(caadr l,'dec_info,cadadr l)$   % saves (z)     for e5
%  if (cadar l) and (cadadr l) then << % if both eq. are diff.
%    f:=caddr l;
%    if pairp f then f:=cadr f;
%    add_both_dec_with(f,caar l,caadr l,rl)$
%  >>$
%  return list(caddr l,caar l,caadr l,cadddr l)$
%end$

% symbolic procedure dec_put_info(f,l)$
% % put informations for decouple reduction step
% % result: (deriv_to_eliminate pde_1 pde_2)
% if l then
% begin scalar a,b$
%    put(caar l,'dec_info,cadar l)$
%    a:=get(caar l,'dec_with)$
%    b:=assoc(f,a)$
%    a:=delete(b,a)$
%    if b then b:=cons(f,cons(caadr l,cdr b))
%         else b:=list(f,caadr l)$
%    put(caar l,'dec_with,cons(b,a))$
%    put(caadr l,'dec_info,cadadr l)$
%    a:=get(caadr l,'dec_with)$
%    b:=assoc(f,a)$
%    a:=delete(b,a)$
%    if b then b:=cons(f,cons(caar l,cdr b))
%         else b:=list(f,caar l)$
%    put(caadr l,'dec_with,cons(b,a))$
% return list(caddr l,caar l,caadr l)$
% end$

%% symbolic procedure dec_info_leq(p,q)$
%% %  relation "<=" for decouple informations
%% if p and q then
%%    if not (cadar car p and cadadr car p) then
%%       if not (cadar car q and cadadr car q) then (cdr p<=cdr q)
%%                                     else p
%%    else if cadar car q and cadadr car q then (cdr p<=cdr q)
%%                                 else nil
%% else p$

symbolic procedure dec_info_leq(p,q)$
%  relation "<=" for decouple informations
if p and q then (cdr p<=cdr q)
else if p then p
else q$

symbolic procedure dec_and_fct_select(pdes,vl,rl,hp,ordering)$
% select 2 pdes for decoupling
% if rl then one pde must be simplified with the help of
% another one and reduce its length
% if hp then only high priority decouplings (eqns with max 3-4 functions)
begin scalar min,f,l,l1,l2,done_pdes,car_pdes,len,
      d_car_pdes,val_car_pdes,val_p,d_p,w1,w2,rtn,f_in_flin,allvarfl$
  while pdes and null rtn do <<
    car_pdes:=car pdes;
    allvarfl:=get(car_pdes,'allvarfcts);
    if expert_mode or
       (flagp(car_pdes,'to_decoup) and allvarfl and
        ((null hp) or (length(allvarfl)<4))         ) then
    <<f:=caaar get(car_pdes,'derivs)$
      if not flin_ or (f_in_flin:=not freeof(flin_,f)) or
         (     homogen_ and (zerop car get(car_pdes,'hom_deg))) or
         (null homogen_ and freeoflist(get(car_pdes,'fcts),flin_)) then <<
       % initializations for the special case of car_pdes:  0=df(f,...)
       len:=get(car_pdes,'printlength)$
       if (null record_hist) and (len=1) then <<
         val_car_pdes:=get(car_pdes,'val);
         if (pairp val_car_pdes) and
            (car val_car_pdes = 'DF) and
            (cadr val_car_pdes = f)    then
         d_car_pdes:=cddr val_car_pdes else
         if val_car_pdes=f then d_car_pdes:=nil
                           else len:=1000
       >>$
       l:=assoc(ordering,get(car_pdes,'dec_with))$
       if rl then l:=append(l,assoc(ordering,get(car_pdes,'dec_with_rl)))$
       % unchecked pairings
       for each p in cdr pdes do
% It should be possible that f is leading function in car_pdes but not
% in others
%       if not flin_ or f_in_flin or
%          (     homogen_ and (zerop car get(p,'hom_deg))) or
%          (null homogen_ and freeoflist(get(p,'fcts),flin_)) then
       if expert_mode or
          (flagp(p,'to_decoup) and
           member(f,get(p,'rational)) and
           ((null hp) or
            (length(union(allvarfl,get(p,'allvarfcts)))<4)) and
           ((not member(p,l)) or
            ((not member(car_pdes,assoc(ordering,get(p,'dec_with)))) and
             ((null rl) or
              (not member(car_pdes,assoc(ordering,get(p,'dec_with_rl)))))
            )
           )
          )
       then
       % If both equations consist of a derivative of f only then
       % instant decision possible
       if (null record_hist) and (len=1) and (get(p,'printlength)=1) then <<
         val_p:=get(p,'val);
         d_p:=0$
         if (pairp val_p) and
            (car val_p = 'DF) and
            (cadr val_p = f)    then
         d_p:=cddr val_p else
         if val_p=f then d_p:=nil$
         if not zerop d_p then <<
           w1:=which_deriv(d_p,d_car_pdes)$
           w2:=which_deriv(d_car_pdes,d_p)$
           if w1 and w2 then add_both_dec_with(ordering,car_pdes,p,rl) else
           % returns eg. ((e5 nil (x 2 y 2 z)) (e4 (x 2 y) (y z))
           %              (df f x 2 y 2 z) e5)
           if null w1 then rtn:={{p,nil,d_p},{car_pdes,w2,d_car_pdes},
                                 val_p,p}
                       else rtn:={{p,w1,d_p},{car_pdes,nil,d_car_pdes},
                                 val_car_pdes,car_pdes}
         >>
       >>                                     else
       % The general case
       <<l1:=dec_info(car_pdes,p,f,vl,rl,ordering)$
         if expert_mode and null l1 then <<pdes:={nil};done_pdes:=nil;l2:=nil>>
                                    else
         if expert_mode or
            (quick_decoup and l1 and cadddr car l1 and
             ((null struc_eqn)                    or
              ((null is_algebraic(car_pdes)) and
               (null is_algebraic(p       ))     )  ))
         then rtn:=car l1
         else if l1 then l2:=cons(l1,l2)
       >>$
       % Check pairings where f is *not* the leading function in
       % car done_pdes. This has not been checked when this pairing
       % was tested before.
       if null rtn then
       for each p in done_pdes do
% It should be possible that f is leading function in car_pdes but not
% in others
%       if not flin_ or f_in_flin or
%          (     homogen_ and (zerop car get(p,'hom_deg))) or
%          (null homogen_ and freeoflist(get(p,'fcts),flin_)) then
       if flagp(p,'to_decoup) and
          member(f,get(p,'rational))       and
          ((null hp) or
           (length(union(allvarfl,get(p,'allvarfcts)))<4)) and
          ((not member(p,l)) or
           ((not member(car_pdes,assoc(ordering,get(p,'dec_with)))) and
            ((null rl) or
             (not member(car_pdes,assoc(ordering,get(p,'dec_with_rl)))))
           )
          )                                and
          ((null get(p,'allvarfcts)) or
           (f neq car get(p,'allvarfcts)))
       then <<l1:=dec_info(car_pdes,p,f,vl,rl,ordering)$
              if expert_mode and null l1 then
              <<pdes:={nil};done_pdes:=nil;l2:=nil>>
                                         else
              if quick_decoup and l1 and cadddr car l1 and
                 ((null struc_eqn)                    or
                  ((null is_algebraic(car_pdes)) and
                   (null is_algebraic(p       ))     )  )
              then rtn:=car l1
              else if l1 then l2:=cons(l1,l2)
       >>
      >>

    >>$
    done_pdes:=cons(car_pdes,done_pdes)$
    pdes:=cdr pdes
  >>$
  if rtn then return rtn$

  %--- l2 is the list of possible pairings of 2 equations
  %--- pick one of these pairings
  l1:=nil;
  %--- l1 is the list of equations which still can be reduced
  %--- and where f=car get(equ,'allvarfcts), i.e. equations
  %--- which must not be used for generating new equations
  %
  %--- each l in l2 has the form
  %--- (((e4 (x 2 y) (y z)) (e5 (z) (x 2 y 2))
  %---  (df f x 2 y 2 z) nil nil) . num_value)
  for each l in l2 do <<
    f:=caddar l;
    if pairp f then f:=cadr f;
    if (caaar l = cadddr car l) and  % if caaar  l will be subst.
       get(caaar l,'allvarfcts) and
       (f=car get(caaar l,'allvarfcts))
    then l1:=union(list(caaar l),l1);
    if (caadar l = cadddr car l) and % if caadar l will be subst.
       get(caadar l,'allvarfcts) and
       (f=car get(caadar l,'allvarfcts))
    then l1:=union(list(caadar l),l1);
  >>;
  %--- Test that no new equation will be generated from an
  %--- equation from which the leading derivative can still be
  %--- reduced
  for each l in l2 do
  if ((cadaar l = nil)                              or
      (cadr cadar l = nil)                          or
      (freeof(l1,caaar  l) and freeof(l1,caadar l))    ) and
     dec_info_leq(l,min)
  then min:=l;
  if min then <<
   l:=car min$
   if (cadar l) and (cadadr l) then << % if both eq. are diff.
     f:=caddr l;
     if pairp f then f:=cadr f;
     add_both_dec_with(ordering,caar l,caadr l,rl)$
   >>$
   return l     % dec_put_info(car min,rl)$
  >>
end$

symbolic procedure err_catch_elimin(p,ltp,dgp,q,ltq,dgq,x,once)$
begin scalar h,bak,kernlist!*bak,kord!*bak,bakup_bak;
 bak:=max_gc_counter$ max_gc_counter:=my_gc_counter+max_gc_elimin;
 kernlist!*bak:=kernlist!*$
 kord!*bak:=kord!*$
 bakup_bak:=backup_;backup_:='max_gc_elimin$
 h:=errorset({'elimin,mkquote p,mkquote ltp,mkquote dgp,
                      mkquote q,mkquote ltq,mkquote dgq,
                      mkquote x,mkquote once},nil,nil)
    where !*protfg=t;
 kernlist!*:=kernlist!*bak$
 kord!*:=kord!*bak;
 erfg!*:=nil;
 max_gc_counter:=bak;
 backup_:=bakup_bak;
 return if errorp h then nil
                    else car h
end$

symbolic procedure elimin(p,ltp,dgp,q,ltq,dgq,x,once)$
% returns {resulting_eqn, multiplier_of_ddpcp, multiplier_of_ddqcp}
begin
 scalar dgs,s,flg,quoti,lts,
        fcpp,fcqp,fcsp,
        fcpq,fcqq,fcsq$

 if dgp > dgq then << flg:=t; dgs:=dgq >>
              else dgs:=dgp$

 fcpp:=1; fcpq:=0;
 fcqq:=1; fcqp:=0;

 while dgs neq 0 do <<
  quoti:=reval{'QUOTIENT,ltp,ltq};
  s:=reval{'PLUS,{'TIMES,p,{'DEN,quoti}},
                 {'MINUS,{'TIMES,q,{'NUM,quoti}}}}$
  lts:=reval{'LTERM,s,x}$
  dgs:=reval{'DEG,lts,x}$
  fcsp:=reval{'PLUS,{'TIMES,fcpp,{'DEN,quoti}},
                    {'MINUS,{'TIMES,fcqp,{'NUM,quoti}}}}$
  fcsq:=reval{'PLUS,{'TIMES,fcpq,{'DEN,quoti}},
                    {'MINUS,{'TIMES,fcqq,{'NUM,quoti}}}}$

  if flg=t then <<
   p:=s;
   ltp:=lts;
   dgp:=dgs;
   fcpp:=fcsp;
   fcpq:=fcsq;
   if dgq>dgp then flg:=nil
  >>       else <<
   q:=s;
   ltq:=lts;
   dgq:=dgs;
   fcqp:=fcsp;
   fcqq:=fcsq;
   if dgp>dgq then flg:=t
  >>$
  if once then dgs:=0
 >>;
 quoti:=err_catch_gcd(fcsp,fcsq);
 return {reval{'QUOTIENT,s   ,quoti},
         reval{'QUOTIENT,fcsp,quoti},
         reval{'QUOTIENT,fcsq,quoti} }
end$  % elimin

symbolic procedure dec_new_equation(l,rl)$
% l has form ((e4 (x 2 y) (y z)) (e5 (z) (x 2 y 2)) (df f x 2 y 2 z) nil nil)
% This means: e4 has df(f,y,z) and is differ. wrt. xxy
%             e5 has df(f,x,2,y,2) and is diff. wrt. z
%             to eliminate df(f,x,2,y,2,z),
%             nil is substituted,
%             substitution is not essential
begin scalar ld,f,ip,iq,s,nvl,lcop,p,ddp,ddpcp,ldp,ltp,dgp,pfac,
                                   q,ddq,ddqcp,ldq,ltq,dgq,qfac,h,once$
  % ddpcp will be the name of the equation, e.g.  e4
  % p     at first the value of the equation, later df(p,ip)
  % ddp   will be the history value of the equation
  % ip    is a list of differentiations to be done with p
  % ldp   is the leading derivative in p
  % ltp   at first the lead. term of p then is the leading term in df(p,ip)
  % dgp   at first highpow of ldp in p then highpow of df(ldp,ip) in ltp
  % lcop  is the coefficient of ldp**dgp in ltp
  % pfac  an overall factor of p that has been dropped but that may vanish

  % similar with q

  ld:=caddr l$
  f:=if pairp ld then cadr ld
                 else ld$
  ip:=cadar l$
  iq:=cadadr l$
  s:=cadddr l$
  once:=car cddddr l$
  ddp:=caar l$  ddpcp:=ddp$
  ddq:=caadr l$ ddqcp:=ddq$
  p:=get(ddp,'val)$
  q:=get(ddq,'val)$
  if record_hist then <<
   nvl:=get(ddp,'nvars)$
   if get(ddq,'nvars)<nvl then nvl:=get(ddq,'nvars)$
   if s=ddp then ddp:=get(ddp,'histry_) else
   if s=ddq then ddq:=get(ddq,'histry_)
  >>$

  if print_ and ((null rl and tr_decouple) or
                 (     rl and tr_redlength)  ) then
  <<terpri()$write "  first pde ",caar l,": "$
    typeeq caar l$
    if ip then write "is diff. wrt. ",ip,","
          else write "is not differentiated,"$
    write "  second pde ",caadr l,": "$
    typeeq caadr l$
    if iq then write "is diff. wrt. ",iq," "
          else write "is not differentiated, "$
    write"to eliminate "$mathprint ld$
  >>$

  if atom ld then ldp:=ld else <<
   ldp:=cadr ld;
   if caddar l then ldp:=cons('DF,cons(ldp,caddar l))
  >>;
  ltp:=reval{'LTERM,p,ldp};
  dgp:=reval{'DEG,ltp,ldp};
  pfac:=1:
  if ip then <<
    lcop:=reval{'QUOTIENT,ltp,{'EXPT,ldp,dgp}}$
    if (dgp=1) and (not fixp lcop) then <<
      p:=reval cons('DF,cons({'QUOTIENT,{'DIFFERENCE,p,ltp},lcop},ip));
      if record_hist then
      ddp:=reval cons('DF,cons({'QUOTIENT,ddp,lcop},ip));
      h:=reval{'DEN,p}$     % the new lcop
      pfac:=reval {'QUOTIENT,lcop,h}$
      if may_vanish(pfac) and (s=ddqcp) then s:=nil;
      ltp:={'TIMES,ld,h}$
      p:=reval{'PLUS,ltp,{'NUM,p}}$
      if record_hist then ddp:=reval {'TIMES,ddp,h}$
    >>                             else <<
      % p:=cons('DF,cons(p,ip));
      if record_hist then
      ddp:=cons('DF,cons(ddp,ip));
      % ltp:=cons('DF,cons(ltp,ip))
      dgp:=1;
      ltp:={'TIMES,{'DF,p,ldp},cons('DF,cons(ldp,ip))};
      p:=cons('DF,cons(p,ip));
    >>
  >>$

  if atom ld then ldq:=ld else <<
   ldq:=cadr ld;
   if caddar cdr l then ldq:=cons('DF,cons(ldq,caddar cdr l))
  >>;
  ltq:=reval{'LTERM,q,ldq};
  dgq:=reval{'DEG,ltq,ldq};
  qfac:=1:
  if iq then <<
    lcop:=reval{'QUOTIENT,ltq,{'EXPT,ldq,dgq}}$
    if (dgq=1) and (not fixp lcop) then <<
      q:=reval cons('DF,cons({'QUOTIENT,{'DIFFERENCE,q,ltq},lcop},iq));
      if record_hist then
      ddq:=cons('DF,cons({'QUOTIENT,ddq,lcop},iq));
      h:=reval{'DEN,q}$     % the new lcop
      qfac:=reval {'QUOTIENT,lcop,h}$
      if may_vanish(qfac) and (s=ddpcp) then s:=nil;
      ltq:={'TIMES,ld,h}$
      q:=reval{'PLUS,ltq,{'NUM,q}}$
      if record_hist then ddq:=reval {'TIMES,ddq,h}$
    >>                             else <<
      % q:=cons('DF,cons(q,iq));
      if record_hist then
      ddq:=cons('DF,cons(ddq,iq));
      % ltq:=cons('DF,cons(ltq,iq))
      dgq:=1;
      ltq:={'TIMES,{'DF,q,ldq},cons('DF,cons(ldq,iq))};
      q:=cons('DF,cons(q,iq));
    >>
  >>$

  % l:=list(caar l,caadr l)$
  % if iq then q:=simplifypde(reval cons('DF,cons(q,iq)),ftem,nil,nil (?))$
  % if ip then p:=simplifypde(reval cons('DF,cons(p,ip)),ftem,nil,nil (?))$

  % h:=reval !*q2a simpresultant list(p,q,ld)$
  return if (l:=err_catch_elimin(p,ltp,dgp,q,ltq,dgq,ld,once)) then
         list(l,s,ddpcp,ddqcp,ddp,ddq,ld,pfac,qfac)            else nil$
end$ % of dec_new_equation

symbolic procedure dec_reduction(h,pdes,ftem,%forg,
                                 vl,rl,ordering)$
% do a reduction step or a cross differentiation either
% h is the result of dec_new_equation() and has the structure
%   list(elimin(p,ltp,dgp,q,ltq,dgq,ld),s,ddpcp,ddqcp,ddp,ddq,ld,pfac,qfac)$
% if rl then one pde must be simplified with the help of
% another one and reduce its length
begin scalar %p,q,ld,a,s,ip,iq,f,dwsa,dwla,dwlb,el,h,
             %ldp,ldq,ltp,ltq,dgp,dgq,lcop,ddp,ddq,ddpcp,ddqcp,len,nvl$
             s,p,q,ddp,ddq,ld,len,a,ip,iq,pfac,qfac$

 s:=cadr h$
 p:=caddr h$
 q:=cadddr h$
 ddp:=nth(h,5)$
 ddq:=nth(h,6)$
 ld:=nth(h,7)$
 pfac:=nth(h,8)$
 qfac:=nth(h,9)$
 h:=car h$

 % If an equation is to be substituted then the new system must
 % be sufficient after replacing one equations through another one.
 % --> the replaced equation must not have been multiplied with
 % possibly vanishing factors

 if s and (null rl) and % (sufficient_decouple) and
    % for rl=t already checked
    (((s=p) and may_vanish(cadr  h)) or
     ((s=q) and may_vanish(caddr h))    ) then s:=nil$

 % tracing comments

 if (null rl and tr_decouple) or
    (     rl and tr_redlength) then <<
  terpri()$
  write p," (resp its derivative) is multiplied with"$terpri()$
  algebraic write lisp if qfac=1 then cadr h
                                 else {'TIMES,qfac,cadr  h}$
  write q," (resp its derivative) is multiplied with"$terpri()$
  algebraic write lisp if pfac=1 then caddr h
                                 else {'TIMES,pfac,caddr h}$
 >>$

 % If an equation is used for a substitution of a derivative which is
 % not a leading derivative and the length of the equation is
 % increased then drop the new equation

 if (null rl) and % for rl=t the length comparison is already done
    (null expert_mode) and % not explicitly ordered by user
    (car h) and s and ((null struc_eqn) or (atom ld))
 then <<
  len:=no_of_terms(car h);
  if pairp(ld) and (car ld = 'DF) then ld:=cdr ld
                                  else ld:=list ld;
  if ((s=p) and
      (ld neq caar get(p,'derivs)) and
      (len>get(p,'terms))) or
     ((s=q) and
      (ld neq caar get(q,'derivs)) and
      (len>get(q,'terms))) then
  return <<
   if print_ then <<
    write"The tried reduction of a non-leading derivative"$terpri()$
    write"would have only increased the equation's length."$terpri()
   >>$
   add_both_dec_with(ordering,p,q,rl);
   list(nil)
  >>;
  if cdr ld then ld:=cons('DF,ld)
            else ld:=car ld;
 >>$

 % the case of a resulting identity 0=0

 if car h then
 if zerop car h and null rl then <<
  % for rl=t the case that the multipliers contain ftem_ has already
  % been checked
  if print_ then <<terpri()$write" An identity 0=0 results.">>$

  if null ip and null iq and null s and
   % if s<>nil then multipliers can not be ftem_ dependent
   (!*batch_mode or
    (batchcount_>=stepcounter_)) then << % i.e. only if batch_mode
   a:=proc_list_;
   % Have already all normal factorizations be tried?
   while a and (car a neq 8) and (car a neq 30) do a:=cdr a;
   if a and car a = 8 then
   to_do_list:=cons(list('factorize_any,%pdes,forg,vl_,
                    list <<a:=get(p,'val);
                           if (pairp a) and (car a = 'TIMES) then p
                                                              else q
                          >>),
                    to_do_list);
   a:=nil;
  >>;
  add_both_dec_with(ordering,p,q,rl);
 >>             else <<
  a:=mkeq(if pfac neq 1 then if qfac neq 1 then {'TIMES,pfac,qfac,car h}
                                           else {'TIMES,pfac,     car h}
                        else if qfac neq 1 then {'TIMES,     qfac,car h}
                                           else                   car h ,
          ftem,vl,allflags_,t,list(0),
          reval {'PLUS,{'TIMES,cadr  h,ddp},
                       {'TIMES,caddr h,ddq} },pdes)$
  if print_ and ((null rl and tr_decouple ) or
                 (     rl and tr_redlength)    ) then
  <<terpri()$mathprint reval {'EQUAL,a,get(a,'histry_)}>>
 >>$

 if record_hist and
    (car h) and (zerop car h) then
 new_idty({'PLUS,{'TIMES,cadr  h,ddp},{'TIMES,caddr h,ddq} }, pdes, t);
 % also if car h is not identical 0 but with less variables.
 % It still can be the case that some functions of fewer variables
 % still depend on all the differentiation variables of the divergence
 % Then integration of the curl is not done(possible?)

 % The following lines have been commented out 9.9.2001 as the
 % cycle-test with dec_hist_list is too crude. It is necessary to
 % record which method (decoupling or length-reduction-decoupling or
 % shortening) leads to a repetition, or better just to check only
 % when doing length-reduction because straightforward decoupling
 % should be done anyway.

 %if a and (null s) and member(get(a,'val),dec_hist_list) then <<
 %  drop_pde(a,nil,nil)$
 %  add_both_dec_with(ordering,car l,cadr l,rl);
 %  if print_ and tr_decouple then <<
 %    terpri()$write "the resulting pde would lead to a cycle"$
 %  >>
 %>>                                                      else <<

 if print_ then <<
  write"Eliminate "$
  mathprint ld$
  write"from ",if ip then cons('DF,cons(p,ip))
                     else p, " and ",
               if iq then cons('DF,cons(q,iq))
                     else q, ". "$
  if a then <<
   if s then <<
    write s,": "$terpri()$
    typeeq s;
    write"is replaced by ",a,": "
   >>   else write a," is added: "$
   terpri()$
   typeeq a
  >>   else
  if s then <<write s," is deleted.";terpri()>>$
 >>$
 if null s then add_both_dec_with(ordering,p,q,rl)
           else <<      % reduction, s is the equation to be replaced
  % The following was commented out as in_cycle() is to take care of
  % preventing cycles, l had the value which is now the input of
  % dec_new_equation()
  %
  % l:=delete(s,l)$
  %
  % if not (ip or iq) then <<
  %  % The equations wrt which s has already been decoupled
  %  % are to be listed under dec_with wrt to the equation
  %  % of both that is kept which is car l
  %  % purpose: These decouplings should not be done again
  %  % with car l as this may result in a cycle
  %  dwsa:=get(    s,'dec_with)$
  %  dwla:=get(car l,'dec_with)$
  %  for each el in dwsa do <<
  %   % el are the different orderings, if more than one are
  %   % in use then something must be changed probably
  %   dwlb:=assoc(car el,dwla)$
  %   dwla:=delete(dwlb,dwla)$
  %   if dwlb then dwlb:=cons(car el,union(cdr el,cdr dwlb))
  %           else dwlb:=el$
  %   dwla:=cons(dwlb,dwla)
  %  >>$
  %  put(car l,'dec_with,dwla)$
  % >>$

  % The following was taken out some time ago (now 9.9.2001)
  % because it probably prevented a complete computation
  % % If other than the leading derivatives are reduced then
  % % the new equ. a must inherit 'dec_with from equ. s
  % if a and get(a,'derivs) and
  %    (car get(a,'derivs) = car get(s,'derivs)) then <<
  %  dwsa:=get(s,'dec_with)$
  %  put(a,'dec_with,dwsa)$
  % >>$

  % The following has been taken out with the dec_hist_list test above
  % if dec_hist>0 then <<
  %  if length dec_hist_list>dec_hist then
  %     dec_hist_list:=cdr dec_hist_list$
  %  dec_hist_list:=reverse cons(get(s,'val),reverse dec_hist_list)$
  % >>$

  drop_pde(s,if a then cons(a,pdes) else pdes,nil)

 >>$
 %>>$ commented out together with code from above
 return list(a)
 % a is either a new equation or nil if s has beed reduced to an identity
end$ % of dec_reduction

symbolic procedure dec_fct_check(a,l)$
% checks, if a function occurs in only one pde
begin scalar ft,n$
  ft:=get(a,'fcts)$
  while ft and l do
    <<if flagp(car l,'to_decoup) then
        ft:=setdiff(ft,get(car l,'fcts))$
    l:=cdr l>>$
  n:=get(a,'nvars)$
  while ft and (n<=length fctargs(car ft)) do ft:=cdr ft$
  if ft then remflag1(a,'to_decoup)$
  return ft$
end$

symbolic procedure check_cases_for_symbol(l)$
% l has form ((e4 (x 2 y) (y z)) (e5 (z) (x 2 y 2)) (df f x 2 y 2 z) nil nil)
% This means: e4 has df(f,y,z) and is differ. wrt. xxy
%             e5 has df(f,x,2,y,2) and is diff. wrt. z
%             to eliminate df(f,x,2,y,2,z),
%             nil is substituted,
%             substitution is not essential
begin scalar s,h,lde,sy$
 % Is a case-distinction to be made about whether the symbol
 % is zero or non-zero?
 s:=cadddr l;
 if lin_problem or (null s) or
    (not pairp caddr l) or
    (null flagp(if s=caar l then caadr l
                            else caar l,'to_symbol))
 then return nil % no case-distinction
 else <<
  if s=caar l then h:=cadr l else h:=car l$ % h are the data of the lower
                                            % priority (e.g. order) equation
  if null cadr h then return nil else <<    % lower order equ. is not diff.
   remflag1(car h,'to_symbol)$
   lde:=if null caddr h then cadr caddr l    % lead. deriv. is algebraic
                        else cons('DF,cons(cadr caddr l,caddr h));
   % lde is the leading derivative in the lower priority equation
   sy:=reval {'DF,get(car h,'val),lde};
   return
   if freeofzero(sy,get(car h,'fcts),get(car h,'vars),get(car h,'nonrational))
   % if not may_vanish(sy)
   then nil
   else <<to_do_list:=cons(list('split_into_cases,sy),to_do_list);
    if print_ then <<
     write"To reduce the leading derivative"$terpri()$
     mathprint caddr l$
     write"in ",s," using ",car h," a case distinction will be made."$terpri()
    >>$
    t
   >>
  >>
 >>
end$

symbolic procedure dec_one_step(pdes,ftem,%forg,
                                vl,hp,ordering)$
% do one decouple step for 2 pdes from l, differentiate them
% and add the new pde or replace an original one
begin scalar l0,l1,l2,l,ruli$ %,f$
 l:=pdes;
 if not expert_mode then l0:=l
                    else <<
  l0:=selectpdes(l,2)$
  drop_dec_with(car  l0,cadr l0,nil)$
  drop_dec_with(cadr l0,car  l0,nil)$
 >>$
 ruli:=start_let_rules()$
again:
 l1:=dec_and_fct_select(l0,vl,nil,hp,ordering)$
 if null l1 then l:=nil else
 if check_cases_for_symbol(l1) then return l else
 % i.e. dec_one_step was successful even if nothing
 % happened just to continue with to_do
 if null (l2:=dec_new_equation(l1,nil)) then
 <<l:=nil;         % e.g. elimin too slow --> err_catch
   add_both_dec_with(ordering,caar l1,caadr l1,nil)$
   goto again
 >>                                     else
 if null (l2:=dec_reduction(l2,pdes,ftem,%forg,
                        vl,nil,ordering)) then l:=nil else <<
  for each a in cdddr l1 do
  if get(a,'val)=nil then l:=delete(a,l)$
  for each a in l2 do if a then <<
   l:=eqinsert(a,l)$
%  % equations which are added and are still to be reduced and still
%  % contain the function to be decoupled shall not be integrated:
%  if null cadddr l1 then <<% no equation was reduced only a new one is added
%   f:=if pairp caddr l1 then cadr caddr l1 % leading deriv. was a deriv
%                        else caddr l1;     % leading deriv. was a function
%   if not freeof(get(a,'fcts),f) then <<
%    remflag1(a,'to_int)$
%    remflag1(a,'to_fullint)
%   >>$
  >>
  % the following breaks the ordering
  % for each a in l2 do dec_fct_check(a,l)$
 >>$
 stop_let_rules(ruli);
 % if anything has changed then l must be the new pde-list
 return l$
end$

symbolic procedure dec_try_to_red_len(pdes_to_choose_from,vl,ordering)$
begin scalar l1,l2,p,q,s$
again:
 l1:=dec_and_fct_select(pdes_to_choose_from,vl,t,nil,ordering)$
 if l1 then <<
  if in_cycle({27,get(caar l1,'printlength),get(caadr l1,'printlength),
              caddr l1,get(cadddr l1,'printlength),
              length get(cadddr l1,'fcts)}) then <<
   add_both_dec_with(ordering,caar l1,caadr l1,t)$
   goto again;
  >>;
  l2:=dec_new_equation(l1,t)$
  % possible length measures to use:
  %    put(equ,'length,pdeweight(val,ftem))$
  %    put(equ,'printlength,delength val)$
  %    put(equ,'terms,no_of_terms(val))$
  p:=caar   l1$
  q:=caadr  l1$
  s:=cadddr l1$
%  if ( no_of_terms(caar l2)   >
%       get(cadddr l1,'terms)       ) or  % * length_inc
%  disadvantage of 'terms: a big product is one term
  if (null l2) or
     ( pdeweight(caar l2,ftem_) >
       get(cadddr l1,'length)        ) or  % * length_inc
     ((s=p) and may_vanish( cadar l2)) or
     ((s=q) and may_vanish(caddar l2)) then <<
   l2:=nil;
   add_both_dec_with(ordering,p,q,t);
   last_steps:=cdr last_steps; % last_steps had already been updated
                               % in add_to_last_steps() in in_cycle()
   goto again
  >>
 >>;
 return l2
end$

symbolic procedure err_catch_red_len(a1,a2,a3)$
begin scalar h,bak,kernlist!*bak,kord!*bak,bakup_bak;
 bak:=max_gc_counter$ max_gc_counter:=my_gc_counter+max_gc_red_len;
 kernlist!*bak:=kernlist!*$
 kord!*bak:=kord!*$
 bakup_bak:=backup_;backup_:='max_gc_red_len$
 h:=errorset({'dec_try_to_red_len,mkquote a1,mkquote a2,mkquote a3},nil,nil)
    where !*protfg=t;
 kernlist!*:=kernlist!*bak$
 kord!*:=kord!*bak;
 erfg!*:=nil;
 max_gc_counter:=bak;
 backup_:=bakup_bak;
 return if errorp h then nil
                    else car h
end$

symbolic procedure dec_and_red_len_one_step(pdes,ftem,%forg,
                                            vl,ordering)$
% do one length-reducing decouple step for 2 pdes from l,
% differentiate at most one and replace the other one which must
% become shorter, the one replaced must not be multiplied with a
% potentially zero factor
begin scalar l,l1,l2,l3,ruli$ %,f$
  l:=pdes;
  if not expert_mode then l1:=l
                     else <<
    l1:=selectpdes(l,2)$
    drop_dec_with(car  l1,cadr l1,t)$
    drop_dec_with(cadr l1,car  l1,t)$
  >>$
  ruli:=start_let_rules()$
again:
  l2:=err_catch_red_len(l1,vl,ordering)$

  if null l2 then return nil;

  if (l3:=dec_reduction(l2,pdes,ftem,%forg,
                        vl,t,ordering)) then <<
   l:=delete(cadr l2,l)$
   for each a in l3 do if a then <<
    l:=eqinsert(a,l)$
%   % equations which are added and are still to be reduced and still
%   % contain the function to be decoupled shall not be integrated:
%   if null cadddr l1 then <<% no equation was reduced only a new one is added
%    f:=if pairp caddr l1 then cadr caddr l1 % leading deriv. was a deriv
%                         else caddr l1;     % leading deriv. was a function
%    if not freeof(get(a,'fcts),f) then <<
%     remflag1(a,'to_int)$
%     remflag1(a,'to_fullint)
%    >>$
%   >>
   >>$
   % the following breaks the ordering
   % for each a in l3 do dec_fct_check(a,l)$
  >>                                             else
  <<last_steps:=cdr last_steps;
    if not expert_mode then <<l1:=l;goto again>>
  >>;
  stop_let_rules(ruli);
  % if anything has changed then l must be the new pde-list
  return l$
end$

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%  procedures for decoupling of similar pde                        %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%symbolic procedure rel_length_diff(p,q)$
%% print length difference in pro cent
%(abs(get(p,'length)-get(q,'length))*100)/
%   (get(p,'length)+get(q,'length))$

%symbolic procedure nearly_same(p,q)$
%begin scalar lp,lq$
% lp:=get(p,'fcts)$
% lq:=get(q,'fcts)$
% if null setdiff(get(p,'allvarfcts),get(q,'allvarfcts)) and
%    null setdiff(get(q,'allvarfcts),get(p,'allvarfcts)) and
%    ((length setdiff(lp,lq)+length setdiff(lq,lp))*100<
%    (length lp+length lq)*same_fcts) then
%    <<lp:=get(p,'derivs)$
%      lq:=get(q,'derivs)$
%      if (length setdiff(lp,lq)+length setdiff(lq,lp))*100<
%         (length lp+length lq)*same_derivs then return t>>$
%end$

%symbolic procedure get_same_pdes(pdes)$
%begin scalar l,n,res$
% while pdes do
%  <<l:=cdr pdes$
%  while l do
%    if (n:=rel_length_diff(car pdes,car l))<=same_length then
%       if nearly_same(car pdes,car l) then
%          <<res:=list(car pdes,car l)$
%            l:=nil>>
%       else l:=cdr l
%    else if n>5*same_length then l:=nil
%                            else l:=cdr l$
%  if res then pdes:=nil
%         else pdes:=cdr pdes
%  >>$
% return res$
%end$

endmodule$

end$
