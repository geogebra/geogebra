%********************************************************************
module shortening$
%********************************************************************
%  Routines for algebraically combining de's to reduce their length
%  Author: Thomas Wolf
%  Jan 1998
%
%  $Id: crshort.red,v 1.4 1998/04/28 21:36:27 arrigo Exp $
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


symbolic procedure alg_length_reduction(arglist)$
% Do one length-reducing combination of two equations
begin scalar pdes,l,l1$ %,cpu,gc$
%  cpu:=time()$ gc:=gctime()$
 pdes:=car arglist$
 if expert_mode then l:=selectpdes(pdes,2)
                else l:=pdes$
 !*rational_bak  := cons(!*rational,!*rational_bak)$
 if !*rational then algebraic(off rational)$
 if struc_eqn then <<
  while l do <<
   if is_algebraic(car l) then l1:=cons(car l,l1);
   l:=cdr l
  >>$
  l:=reverse l1
 >>$
 if l and cdr l and (l1:=err_catch_short(l,caddr arglist,pdes)) then
      <<for each a in cdr l1 do pdes:=drop_pde(a,pdes,nil)$
        for each a in car l1 do
        if a then pdes:=eqinsert(a,pdes)$
        for each a in car l1 do
        if a then dec_fct_check(a,pdes)$
        l:=nil;
        for each a in car l1 do if a then l:=cons(a,l);
        l:=list(pdes,cadr arglist,l)>>
 else l:=nil$
 %if print_ and !*time then <<
 % write " time : ", time() - cpu,
 %       " ms    GC time : ", gctime() - gc," ms "
 %>>$
 if !*rational neq car !*rational_bak then
 if !*rational then algebraic(off rational) else algebraic(on rational)$
 !*rational_bak:= cdr !*rational_bak$
 return l$
end$

%-------------------

symbolic procedure err_catch_short(a1,vl,pdes)$
begin scalar h,bak,kernlist!*bak,kord!*bak,mi,newp,p1,bakup_bak;
 bak:=max_gc_counter$ max_gc_counter:=my_gc_counter+max_gc_short;
 kernlist!*bak:=kernlist!*$
 kord!*bak:=kord!*$
 bakup_bak:=backup_; backup_:='max_gc_short$
 h:=errorset({'shorten_pdes,mkquote a1},nil,nil)
    where !*protfg=t;
 kernlist!*:=kernlist!*bak$
 kord!*:=kord!*bak;
 erfg!*:=nil;
 max_gc_counter:=bak;
 backup_:=bakup_bak;
 return
 if (errorp h) or (caar h=nil) then nil
                               else <<
  mi:=caar h;
  newp:=cdar h;
  h:=nil;
  p1:=0;
  for each pc in cdr newp do p1:=p1+get(pc,'terms);
  mi:=(<<h:=for each pc in car newp collect
            if zerop pc then <<nequ_:=add1 nequ_;nil>> else
               mkeq(pc,fctsort union(get(caddr mi,'fcts),
                                     get(cadddr mi,'fcts)),
                    vl,allflags_,t,list(0),nil,pdes);
         for each pc in h do if pc then p1:=p1-get(pc,'terms);
         h
       >> . cdr newp);
  if print_ then <<
   if tr_short then <<
    for each h in cdr newp do <<write h,": "$typeeq h>>$
    for each h in car mi do if null h then
    <<write "This gives identity 0=0."$terpri()>>
                                      else
    <<write h,": "$typeeq h>>$
   >>$
   write "shortening by ",p1," term"$
   if p1 neq 1 then write"s"$
   terpri()$
  >>;
  for each pc in cdr newp do drop_pde(pc,nil,nil);
  mi
 >>
end$

%-------------------

symbolic procedure is_algebraic(p)$
% checks whether the leading derivative is algebraic
% if true and if lex_fc:=nil then all allvar functions turn up
% only algebraically
begin scalar h;
 h:=get(p,'derivs)$
 return
 if null h then t
           else <<h:=caar h;
  if (pairp h) and (cdr h) then nil
                           else t
 >>
end$

%-------------------

symbolic procedure shorten_pdes(des)$
begin scalar mi,p1,p1rl,p1le,pc,pcc,newp,
             l0,l1,l2,l3,l4,version,p1_is_alg$ %,valcp$
 if pairp des and pairp cdr des then <<
  version:=1;
  repeat <<
   % find the pair of pdes not yet reduced with each other
   % with the lowest product of their number of terms % printlength's
   mi:=nil;
   pc:=des;
   while cdr pc do <<
    p1:=car pc;pc:=cdr pc;
    if flagp(p1,'to_eval) %and
 %     ((get(p1,'terms)>1) or <<
 %       valcp:=get(p1,'val);
 %       if car valcp='!*sq then valcp:=reval valcp;
 %       freeof(valcp,'PLUS)
 %      >>)
     then <<
     p1rl:=get(p1,'rl_with);
     p1le:=get(p1,'terms);
     l1:=get(p1,'derivs);
     l0:=length l1;
     if struc_eqn then p1_is_alg:=is_algebraic(p1)$
     pcc:=pc;
     while pcc do
     if flagp(car pcc,'to_eval   ) and
%        ((get(car pcc,'terms)>1) or <<
%          valcp:=get(car pcc,'val);
%          if car valcp='!*sq then valcp:=reval valcp;
%          freeof(valcp,'PLUS)
%        >>) and
        ((not member(car pcc, p1rl                )) or
         (not member(p1     ,get(car pcc,'rl_with)))    ) and
        ((null struc_eqn)      or
         p1_is_alg             or
         (is_algebraic(car pcc) and
          (p1le=get(car pcc,'terms))
         )                       ) and
        <<l2:=get(car pcc,'derivs)$
          if version=1 then <<
           l3:=length(setdiff(l1,l2))$
           if (l0>l3               ) and  % necessary requirement
              (( null mi      ) or
               (( car mi) > l3)    ) then t
                                     else nil
          >>           else <<
           l3:=length(setdiff(l1,setdiff(l1,l2)))$
           l4:=length(union(l1,l2))$
           if (l3>0) and
              ((null mi) or
               ((((car mi)*l4) > ((cadr mi)*l3)))) then t
                                                   else nil
          >>
        >>
     then <<mi:=list(l3,l4,p1,car pcc);
            if ((version  =  1) and (l3= 0)) or
               ((version neq 1) and (l3=l4)) then <<pcc:=nil;pc:={nil}>>
                                             else pcc:=cdr pcc
          >>
     else pcc:=cdr pcc;
    >>
   >>$
   if mi then <<
    newp:=shorten(caddr mi,cadddr mi);
    if null newp then add_rl_with(caddr mi,cadddr mi)
   >>
  >> until (null mi) or newp; % if not possible then already returned with nil
 >>;

 return (mi . newp)
end$

%-------------------

symbolic procedure partition_1(l,la)$
% l is an equation,
% returning (l1 . l2) where
% l1=partitioning of equation l into ((lpow1.lc1),(lpow2.lc2),...)
% l2=(lpow1,lpow2,...)
% This works currently only for l that are linear in elem. of la
begin scalar l1,l3;
 l:=reorder !*a2f l;
 while pairp l and member(l3:=car lpow l,la) do <<
  l1:=cons((l3 . !*f2a lc l), l1)$
  l:= red l;
 >>;
 return if l then (append(l1,list(1 . !*f2a l)) .
                   append(la,list(1))) % inhomogeneous case
             else (l1 . la)            %   homogeneous case
end$

%-------------------

symbolic procedure partition_2(de,l)$
% dropping from de all parts that can not be matched by the other
% equation, a list of ftem-functions and their derivatives from
% the other equation is l
begin scalar newde,dropped,n;
 % dropped is the number of terms that can not be matched and
 % which are therefore dropped
 dropped:=0$
 while de do <<
  n:=no_of_terms cdar de$
  if member(caar de,l) then newde:=cons(cons(n,car de),newde)
                       else dropped:=dropped+n;
  de:=cdr de
 >>;
 return (dropped . newde)
end$

%-------------------

symbolic procedure strip(d)$
begin
 scalar h;
 d:= if not pairp d then list d else
     if car d='QUOTIENT then cadr d else
     if car d = 'PLUS then cdr d
                      else list(d)$
 return
 for each h in d collect !*a2f h
end$

%-------------------

symbolic procedure shorten(de1,de2)$
% shorten the two pdes with each other
% returns a dotted pair, where car is a list of the values of new pdes
% and cdr is a list of names of pdes to be dropped
begin scalar a,b,h,r,s,cp,l1,l2,l1ul2,l1ml2,l2ml1,l1il2,oldorder,
      de1p,de2p,termsof1,termsof2,flip,n1,n2,ql,maxcancel,
      take_first,non_linear,homo,de2pnew,tr_short_local;
 non_linear:=t;
 % take_first:=t;
 % "=t is not so radical, --> eqn.s are longer and in total it is slower

 % tr_short_local:=t;
 if tr_short_local then deprint list(get(de1,'val),get(de2,'val))$

 if homogen_ and (1=car get(de1,'hom_deg)      )
             and (1=car get(de2,'hom_deg)      )
             and ((cadr get(de1,'hom_deg)) neq
                  (cadr get(de2,'hom_deg))     ) then homo:=t;
 if non_linear and null homo then <<
  a:=sort_partition(de1,nil,get(de1,'fcts),nil)$
  b:=sort_partition(de2,nil,get(de2,'fcts),nil)$
  if tr_short_local then <<
   write"a=",a$ terpri()$
   write"b=",b$ terpri()$
  >>;
  de1p:=nil;
  de2p:=nil;
  for each h in a do <<
   s:=car h;
   cp:=b;
   % Does s occur in b?
   while cp and (s neq caar cp) do cp:=cdr cp;
   if cp then <<
    r:=if (pairp s) or (numberp s) then gensym() else s;
    %--- dropping the ftem-depending factors once at the beginning
    de1p:=cons(cons(cadr h,
                    cons(r,
                         reval list('QUOTIENT,
                                    if cadr h>1 then cons('PLUS,caddr h)
                                                else caaddr h,
                                    s)
                        )),
               de1p);
    de2p:=cons(cons(cadar cp,
                    cons(r,
                         reval list('QUOTIENT,
                                    if cadar cp>1 then cons('PLUS,caddar cp)
                                                  else car caddar cp,
                                    s)
                        )),
               de2p);
%    %--- not dropping the ftem-depending factors
%    de1p:=cons(cons(cadr h,cons(r,if cadr h>1 then cons('PLUS,caddr h)
%                                              else caaddr h )),de1p);
%    de2p:=cons(cons(cadar cp,cons(r,if cadar cp>1 then cons('PLUS,caddar cp)
%                                                  else car caddar cp )),de2p);
    if tr_short_local then <<
     write"de1p=",de1p$terpri()$
     write"de2p=",de2p$terpri()$
    >>
   >>
  >>
 >>   else <<

  de1p:=get(de1,'val)$
  de2p:=get(de2,'val)$

  if homo then <<  % multiplication with flin_ functions is forbidden
   a:=get(de1,'derivs)$
   h:=nil$
   while a do <<
    if not freeoflist(car a,flin_) then h:=cons(car a,h);
    a:=cdr a
   >>
  >>      else h:=get(de1,'derivs)$
  l1:=for each a in h collect
      if length car a = 1 then caar a else cons('DF,car a)$ % all derivs of de1

  if homo then <<  % multiplication with flin_ functions is forbidden
   a:=get(de2,'derivs)$
   h:=nil$
   while a do <<
    if not freeoflist(car a,flin_) then h:=cons(car a,h);
    a:=cdr a
   >>
  >>      else h:=get(de2,'derivs)$
  l2:=for each a in h collect
      if length car a = 1 then caar a else cons('DF,car a)$ % all derivs of de2

  l1ml2:=setdiff(l1,l2);    % l1 - l2
  l2ml1:=setdiff(l2,l1);    % l2 - l1
  l1il2:=setdiff(l1,l1ml2); % intersection
  l1ul2:=union(l1,l2);      % union
  if tr_short_local then <<
   write"before substitution:"$terpri()$
   write"l1=",l1$ terpri()$
   write"l2=",l2$ terpri()$
   write"de1p=",de1p$terpri()$
   write"de2p=",de2p$terpri()$
   write"l1ml2=",l1ml2$terpri()$
   write"l2ml1=",l2ml1$terpri()$
   write"l1il2=",l1il2$terpri()$
   write"l1ul2=",l1ul2$terpri()$
  >>;

  % substituting derivatives by a new variable to become kernels
  for each a in l1ml2 do if pairp a then <<
   b:=gensym()$
   l1:=subst(b,a,l1)$
   l1ul2:=subst(b,a,l1ul2)$
   de1p:=subst(b,a,de1p)
  >>$
  for each a in l2ml1 do if pairp a then <<
   b:=gensym()$
   l2:=subst(b,a,l2)$
   l1ul2:=subst(b,a,l1ul2)$
   de2p:=subst(b,a,de2p)
  >>$
  for each a in l1il2 do if pairp a then <<
   b:=gensym()$
   l1:=subst(b,a,l1)$
   l2:=subst(b,a,l2)$
   l1ul2:=subst(b,a,l1ul2)$
   de1p:=subst(b,a,de1p)$
   de2p:=subst(b,a,de2p)
  >>$
  if tr_short_local then <<
   write"after substitution:"$terpri()$
   write"l1=",l1$ terpri()$
   write"l2=",l2$ terpri()$
   write"de1p=",de1p$terpri()$
   write"de2p=",de2p$terpri()$
   write"l1ml2=",l1ml2$terpri()$
   write"l2ml1=",l2ml1$terpri()$
   write"l1il2=",l1il2$terpri()$
   write"l1ul2=",l1ul2$terpri()$
  >>;

  %--- writing both equations as polynomials in elements of l1ul2
  oldorder:=setkorder l1ul2;
  de1p:=partition_1(de1p,l1); l1:=cdr de1p; de1p:=car de1p;
  de2p:=partition_1(de2p,l2); l2:=cdr de2p; de2p:=car de2p;
  setkorder oldorder;

  %--- l1,l2 can now have the element 1 in case of inhomogeneous de's
  l1ul2:=nil;
  l1il2:=nil;

  %--- Partitioning each equation into 2 parts, one part that can
  %--- be matched by the other equation and one that can not.

  % de1p:=partition_2(de1p,l2)$ dropped1:=car de1p; de1p:=cdr de1p;
  % de2p:=partition_2(de2p,l1)$ dropped2:=car de2p; de2p:=cdr de2p;
  de1p:=cdr partition_2(de1p,l2)$
  de2p:=cdr partition_2(de2p,l1)$
 >>$

 if (null de1p) or (null de2p) then return nil;

 termsof1:=no_of_terms get(de1,'val)$
 termsof2:=no_of_terms get(de2,'val)$

 if tr_short_local then <<
  write"---------"$terpri()$
  write"de1:",de1," with ",termsof1," terms"$terpri()$
  a:=de1p;
  while a do <<
   write "caar =",caar a;terpri()$
   write "cadar=",cadar a;terpri()$
   write "cddar=", algebraic write lisp cddar a;terpri()$
   a:=cdr a;
  >>;terpri()$
  write"de2:",de2," with ",termsof2," terms"$terpri()$
  a:=de2p;
  while a do <<
   write "caar =",caar a;terpri()$
   write "cadar=",cadar a;terpri()$
   write "cddar=",algebraic write lisp cddar a;terpri()$
   a:=cdr a;
  >>;terpri()$
 >>;

 % One can do a stronger restriction: The maximum that can be
 % canceled is sum of min of terms of the de1p,de2p sublists
 % corresponding to the coefficients of different ftem functions/deriv.

 a:=de1p; b:=de2p; n2:=nil;
 while a do <<
  n1:=if (caar a)<(caar b) then caar a else caar b;
  % n1 is min of terms of the coefficients of the same ftem function/der.
  n2:=cons(2*n1,n2);
  a:=cdr a; b:=cdr b;
 >>$

 % maxcancel is the maximal number of cancellations in all the
 % remaining runs of short depending on the current run.

 maxcancel:=list(0);
 n1:=0;
 while n2 do <<
  n1:=n1+car n2;
  n2:=cdr n2;
  maxcancel:=cons(n1,maxcancel);
 >>;

 if ((car maxcancel)<termsof1) and
    ((car maxcancel)<termsof2) then return nil;

 if homo and (cadr get(de1,'hom_deg)<cadr get(de2,'hom_deg)) then flip:=nil else
 if homo and (cadr get(de1,'hom_deg)>cadr get(de2,'hom_deg)) then flip:= t  else
 if (termsof1<termsof2) or
    (struc_eqn and
     (n1=n2)   and
     (null is_algebraic(de2))
    ) then flip:=nil
      else flip:=t;

 if flip then <<
  a:=de1p; de1p:=de2p; de2p:=a;
  n1:=termsof2;
  n2:=termsof1
 >>      else <<
  n1:=termsof1;
  n2:=termsof2
 >>;

 if (n1=1) and (length de1p = 1)
    and ((atom  cddar de1p) or (caddar de1p neq 'PLUS)) then <<
  % one equation has only a single term which is not a product of sums
  a:=cadar de1p;    % e.g. g0030
  b:=de2p;
  while b and (cadar b neq a) do b:=cdr b;
  if tr_short_local then <<
   write"one is a 1-term equation"$terpri()$
   write"a=",a$terpri()$
   write"b=",b$terpri()$
   write"de1p.1=",de1p$terpri()$
   write"de2p.1=",de2p$terpri()$
  >>$
  a:=if null b then nil  % that term does not turn up in other equation
               else <<   % it does turn up --> success
    de1p:=cddar de1p;
    de2p:=cddar b;
    if tr_short_local then <<
     write"de1p.2=",de1p$terpri()$
     write"de2p.2=",de2p$terpri()$
    >>$
    if homo then <<
     if pairp de2p and car de2p='PLUS then de2p:= cdr de2p
                                      else de2p:=list de2p;
     for each a in de2p do <<
      r:=algebraic(a/de1p);         % otherwise already successful
      if freeoflist(algebraic den r,ftem_) then
      de2pnew:=cons(r,de2pnew)
     >>;
     de2p:=if null de2pnew then <<b:=nil;nil>> else
           if cdr de2pnew then cons('PLUS,de2pnew)
                          else car de2pnew;
     de1p:=1
    >>;
    de2p % does only matter whether nil or not
  >>
 >>      else <<
  repeat << % one shortening
   if tr_short_local then <<write"cadar de1p=",cadar de1p$terpri()>>$

   b:=short(ql,strip cddar de1p,strip cddar de2p,n1,
            2*(caar de1p),car maxcancel-cadr maxcancel,
            cadr maxcancel,take_first,homo)$

   % take_first:=car b; b:=cdr b; % to activate see end of short()
   if b then <<
    ql:=car b;
    a:=cdr b;
    if a and take_first then <<     % the result
      de1p:=!*f2a car a;
      de2p:=!*f2a cdr a;
    >>   else <<
      de1p:=cdr de1p;
      de2p:=cdr de2p;
    >>;
    maxcancel:=cdr maxcancel;
   >>   else a:=nil;
  >> until (null b)         or % failure
           a and take_first or % success
           null de1p$          % the case of exact balance
  if b and (null take_first) then <<
   % search of the best shortening
   r:=0;               % highest number of saved terms so far
   de1p:=nil;          % numerator   of the best quotient so far
   de2p:=nil;          % denominator of the best quotient so far
   while ql do <<
    s:=cdar ql;
    while s do <<
     cp:=car s;
     h:=cdar cp;       % nall in short()
     while cdr cp do <<
      if (cdadr cp)+h>r then <<
       r:=(cdadr cp)+h;
       de1p:=!*f2a caar cp;
       if caaadr cp neq 1 then de1p:=reval {'TIMES,de1p,caaadr cp};
       de2p:=!*f2a caar ql;
       if cdaadr cp neq 1 then de2p:=reval {'TIMES,de2p,cdaadr cp};
      >>;
      rplacd(cp,cddr cp)
     >>;
     s:=cdr s;
    >>;
    ql:=cdr ql;
   >>
  >>
 >>;
 return
 if null b or (take_first and null a) then nil
                          else <<  % numerator and denominator are de1p, de2p

  %--- computing the shorter new equation
  if flip then <<a:=get(de2,'val);  b:=get(de1,'val)>>
          else <<a:=get(de1,'val);  b:=get(de2,'val)>>$
  ql:=if termsof1>termsof2 then de1
                           else de2;
  if print_ then <<
   if null car recycle_eqns then n1:=mkid(eqname_,nequ_)
                            else n1:=caar recycle_eqns$
   if tr_short then
   algebraic write"The new equation ",n1," = ",
                  de2p*(if flip then de2 else de1) -
                  de1p*(if flip then de1 else de2),"  replaces  "
  >>$
  a:=reval list('PLUS,
                list('MINUS,
                     if de1p=1 then b
                               else list('TIMES,de1p,b)),
                if de2p=1 then a
                          else list('TIMES,de2p,a)       )$

  if in_cycle(cons(11,if flip then {
     get(de2,'printlength),length get(de2,'fcts),de2p,
     get(de1,'printlength),length get(de1,'fcts),de1p}
                              else {
     get(de1,'printlength),length get(de1,'fcts),de1p,
     get(de2,'printlength),length get(de2,'fcts),de2p}))
  then nil
  else (list a . list(ql))
 >>
end$

%-------------------

symbolic procedure clean_num(qc,j)$
begin
 scalar qc1,nall$
 return
 if 2*(cdaar qc)<=j then t else <<
  qc1:=car qc;  % the representative and list to proportional factors
  nall:=cdar qc1;
  while cdr qc1 do
  if (cdadr qc1)+nall<=j then rplacd(qc1,cddr qc1)
                         else qc1:=cdr qc1;
  if qc1=car qc then t else nil  % whether empty or not after cleaning
 >>
end$

%--------------------

symbolic procedure clean_den(qc,j)$
begin
 scalar qcc$
 qcc:=qc$
 while cdr qc do
 if clean_num(cdr qc,j) then rplacd(qc,cddr qc)
                        else qc:=cdr qc$
 return null cdr qcc % Are there any numerators left?
end$

%--------------------

symbolic procedure short(ql,d1,d2,n1,n1_now,max_save_now,
                         max_save_later,take_first,homo)$
begin
 % d1,d2 are two subexpressions of two expressions with n1,n2 terms
 % ql is the list of quotients
 % drp is the number of terms dropped as they can not cancel anything
 % dne is the number terms of d1 already done, including those dropped
 % mi is the minimum of n1,n2
 % homo=t then non-linear equations --> must check that d2 is not
 %        multiplied with ftem_ dependent factor
 scalar nall,d1cop,d2cop,m,j,e1,q,qq,qc,dcl,nu,preqc,ldcl,lnu,mi,tr_short_local;

%tr_short_local:=t;
 mi:=n1;
 m:=0;
 nall:=0;
 d1cop:=d1;
 % n1_now is the maximum number of terms cancelling each other
 % in this run of short based on 2*(number of remaining terms of d1
 % still to check).
 % max_save_now is the maximum number of cancellations based
 % on 2*min(terms of d1, min terms of d2)
 j:=if n1_now<max_save_now then n1_now
                           else max_save_now$
 % The following j-value is the minimal number of cancellations
 % of a quotient by now in order to lead to a reduction.
 % mi is the minimal umber of cancelled terms at the end = number
 % of terms of the shorter equation.
 % max_save_later is the maximal number of cancelling terms in all
 % later runs of short.
 j:=mi-j-max_save_later$
 repeat <<                             % for each term of d1
  n1_now:=n1_now-2;
  e1:=car d1cop; d1cop:=cdr d1cop;
  d2cop:=d2;
  while d2cop and (nall+m<=n1) do <<   % for each term of d2
   q:=cancel(e1 ./ car d2cop);         % otherwise already successful
   d2cop:=cdr d2cop;
   %--- dropping a numerical factors
   dcl:=cdr q;        % dcl is the denominator of the current quotient
   if numberp dcl then <<ldcl:=dcl;dcl:=1>>
                  else <<
    ldcl:=dcl;
    repeat ldcl:=lc ldcl until numberp ldcl$% or car ldcl = '!:RN!:$
    dcl:=car cancel(dcl ./ ldcl)
   >>;
   nu:=car q;         % nu is the numerator of the current quotient
   if numberp nu then <<lnu:=nu;nu:=1>> else
   if homo and not freeoflist(nu,ftem_) then nu:=nil
                                        else <<
    lnu:=nu;
    repeat lnu:=lc lnu until numberp lnu$% or car ldcl = '!:RN!:$
    nu:=car cancel(nu ./ lnu)
   >>;
   if (lnu>1000000000) or (ldcl>1000000000) then
   if tr_short then <<
    write" Num. factors grew too large in shortening."$
    terpri()
   >>          else                         else
   if nu then <<

    % - ql is a list of denominator classes: (dcl1 dcl2 dcl3 ...)
    % - each denominator class dcli is a dotted pair (di . nclist) where
    %   - di is the denominator and
    %   - nclist is a list of numerator classes.
    %     Each numerator class is a list with
    %     - first element: (ncl . n) where ncl is the numerator
    %       up to a rational numerical factor and n is the number of
    %       occurences of ncl (up to a rational numerical factor)
    %     - further elements: (nfi . ni) where nfi is the numerical
    %       proportionality factor and ni the number of occurences
    %       of this factor

    %---- search for the denominator class
    qc:=ql;
    while qc and (dcl neq caar qc) do qc:=cdr qc;

    if null qc then     % denominator class not found
    if j <= 0 then      % add denominator class, here nall,m are not
                        % assigned as it would only play a role if
                        % one equation had only one term but that
                        % is covered as special case
    ql:=cons((dcl . list(list((nu . 1),((lnu . ldcl) . 1)))), ql)
              else      % too late to add this denominator
               else <<  % denominator class has been found

     %---- now search of the numerator class
     qc:=cdar qc;      % qc is the list of numerator classes nclist
     while qc and (nu neq caaar qc) do <<preqc:=qc; qc:=cdr qc>>;

     if null qc then   % numerator class not found
     if j leq 0 then   % add numerator class
     rplacd(preqc,list(list((nu . 1),((lnu . ldcl) . 1))) )
                  else % too late to add this numerator
                else <<% numerator class found
      nall:=cdaar qc + 1;   % increasing the total number of occur.
      rplacd(caar qc,nall);

      %---- now search for the numerical factor
      qq:=(lnu . ldcl);
      qc:=cdar qc;
      while qc and (qq neq caar qc) do <<preqc:=qc;qc:=cdr qc>>;
      if null qc then << % numerical factor not found
       m:=1;            rplacd(preqc,list((qq . 1)))
      >>         else <<
       m:=add1 cdar qc$ rplacd(car qc,m)
      >>
     >> % numerator class found
    >>  % denominator class found
   >>   % not (homo and ftem_ - dep. factor for d2)
  >>$   % all terms of d2
  j:=if n1_now<max_save_now then n1_now
                            else max_save_now$
  j:=mi-j-max_save_later$
  if j>0 then <<
   while ql and clean_den(car ql,j) do ql:=cdr ql;
   if ql then <<
    qc:=ql;
    while cdr qc do
    if clean_den(cadr qc,j) then rplacd(qc,cddr qc)
                            else qc:=cdr qc
   >>
  >>;
  if tr_short_local then <<
   terpri();write length ql," denominators";
  >>;
  % If there is only one quotient left and no new one can be added
  % (because of j>0) then take_first:=t
  % The following lines need only be un-commented but a test
  % showed no speed up, only slight slowing down
  %
  % if (null take_first)    and
  %    (j > 0)              and % no new quotients will be added
  %    ql and (null cdr ql) and % only one denominator class
  %    (null cddar ql)      and % only one numerator class in cdar ql
  %                             % the numerator class is cadar ql
  %    (1=cdar cadar ql)   then take_first:=t
 >>    % all terms of d1
 until (null d1cop) or                  % everything divided
       (take_first and (nall+m>n1)) or  % successful: saving > cost
       ((j > 0) and (null ql))$         % all quotients are too rare --> end
 return
 % cons(take_first,
      if ((j > 0) and (null ql)) then nil
                                 else
      if m+nall<=mi then (ql . nil)
                    else (ql . q)
 %     )
end$ % of short


symbolic procedure drop_lin_dep(arglist)$
% drops linear dependent equations
begin scalar pdes,tr_drop,p,cp,incre,newpdes,m,h,s,r,a,v,
             vli,indp,indli,conli,mli,success$
 % the pdes are assumed to be sorted by the number of terms,
 % shortest come first
 % vli is the list of all `independent variables' v in this lin. algebra
 %     computation, i.e. a list of all different products of powers of
 %     derivatives of ftem functions and constants
 %     format: ((product1, v1, sum1),(product2, v2, sum2),...)
 %     where sumi is the sum of all terms of all equations multiplied
 %     with the multiplier of that equation
 % indli is a list marking whether equations are necessarily lin
 %     indep. because they involve a `variable' v not encountered yet
 % mli is the list of multipliers of the equations
 pdes:=car arglist$
 % tr_drop:=t$
 if pdes and cdr pdes then <<
  while pdes do <<
   p:=car pdes; pdes:=cdr pdes; newpdes:=cons(p,newpdes);
   m:=gensym()$
   a:=sort_partition(p,nil,get(p,'fcts),nil);
   if tr_drop then <<write "new eqn:"$prettyprint a;
    write"multiplier for this equation: m=",m$terpri()
   >>$
   indp:=nil;
   for each h in a do <<
    s:=car h;
    % Does s occur in vli?
    % Adding the terms multiplied with the multiplier to the corresp. sum
    cp:=vli;
    while cp and (s neq caar cp) do cp:=cdr cp;
    if tr_drop then <<
     write"searched for: s=",s$terpri();
     if cp then write"found: car cp=",car cp$terpri()$
    >>$
    if cp then <<r:=cadar cp;
                 incre:=reval {'QUOTIENT,
                               {'TIMES,m,r,
                                if cadr h>1 then cons('PLUS,caddr h)
                                            else caaddr h},
                               s};
                 rplaca(cddar cp,cons(incre,caddar cp))
               >>
          else <<r:=if (pairp s) or (numberp s) then gensym() else s;
                 indp:=s;
                 incre:=reval {'QUOTIENT,
                               {'TIMES,m,r,
                                if cadr h>1 then cons('PLUS,caddr h)
                                            else caaddr h},
                               s};
                 vli:=cons({s,r,{incre}},vli)
               >>;
    if tr_drop then <<
     write"corresponding symbol: r=",r$terpri()$

     write"upd: incre=",incre$terpri()$
     write"vli="$prettyprint vli
    >>$
   >>;
   mli:=cons(m,mli);
   indli:=cons(indp,indli)
  >>$

  % Formulating a list of conditions
  while vli do <<
   v:=caddar vli; vli:=cdr vli;
   conli:=cons(if cdr v then cons('PLUS,v)
                        else car v,
               conli)
  >>;

  % Now the investigation of linear independence
  pdes:=nil;  % the new list of lin. indep. PDEs
  while cdr newpdes do <<
   if tr_drop then <<
    terpri()$
    if car indli then write"lin. indep. without search of ",car newpdes,
                           " due to the occurence of ",car indli
                 else write"lin. indep. investigation for ",car newpdes$
   >>;
   if car indli then pdes:=cons(car newpdes,pdes)
                else <<
    s:=cdr solveeval {cons('LIST,subst(1,car mli,conli)),cons('LIST,cdr mli)};
    if s then <<drop_pde(car newpdes,nil,nil)$  % lin. dep.
                success:=t$
                if print_ then <<
                 terpri()$
                 write"Eqn. ",car newpdes,
                      " has been dropped due to linear dependence."$
                >>
              >>
         else <<pdes:=cons(car newpdes,pdes);   % lin. indep.
                if tr_drop then <<
                 terpri()$
                 write"Eqn. ",car newpdes," is lin. indep."$
                >>
              >>;
   >>;
   newpdes:=cdr newpdes;
   indli:=cdr indli;
   conli:=subst(0,car mli,conli);
   mli:=cdr mli
  >>;
  pdes:=cons(car newpdes,pdes)
 >>;
 return if success then list(pdes,cadr arglist)
                   else nil
end$

symbolic procedure find_1_term_eqn(arglist)$
% checks whether a linear combination of the equations can produce
% an equation with only one term
if not lin_problem then nil else
begin scalar pdes,tr_drop,p,cp,incre,m,h,s,r,a,v,
             vli,indp,indli,conli,mli,mpli,success,
             sli,slilen,maxlen,newconli,newpdes,newp,fl,vl$
%tr_drop:=t$
 if tr_drop then terpri()$
 pdes:=car arglist$
 newpdes:=pdes$
%---------------------------------
% if struc_eqn then <<
%  cp:=pdes;
%  while cp do <<
%   if is_algebraic(car cp) then r:=cons(car cp,r)
%                           else s:=cons(car cp,s);
%   cp:=cdr cp
%  >>;
%  r:=nil;
%  s:=nil;
% >>$
% Drop all PDEs which have at least two derivs which no other have
%---------------------------------
 if pdes and cdr pdes then <<
  while pdes do <<
   p:=car pdes; pdes:=cdr pdes;
   m:=gensym()$
   if tr_drop then <<terpri()$write"multiplier m=",m$terpri()>>$
   a:=sort_partition(p,nil,get(p,'fcts),nil);
   for each h in a do <<
    s:=car h;
    % Does s occur in vli?
    % Adding the terms multiplied with the multiplier to the corresp. sum
    cp:=vli;
    while cp and (s neq caar cp) do cp:=cdr cp;
    if tr_drop then <<
     write"searched for: s=",s$terpri();
     if cp then <<write"found: car cp=",car cp$terpri()$>>
    >>$
    if cp then <<r:=cadar cp;
                 incre:=reval {'QUOTIENT,
                               {'TIMES,m,%r,
                                if cadr h>1 then cons('PLUS,caddr h)
                                            else caaddr h},
                               s};
                 rplaca(cddar cp,cons(incre,caddar cp))
               >>
          else <<r:=if (pairp s) or (numberp s) then gensym() else s;
                 indp:=s;
                 incre:=reval {'QUOTIENT,
                               {'TIMES,m,%r,
                                if cadr h>1 then cons('PLUS,caddr h)
                                            else caaddr h},
                               s};
                 vli:=cons({s,r,{incre}},vli)
               >>;
    if tr_drop then <<
     write"corresponding symbol: r=",r$terpri()$

     write"upd: incre=",incre$terpri()$
     write"vli="$prettyprint vli
    >>$
   >>;
   mli:=cons(m,mli);
   mpli:=cons((m . p),mpli);
   indli:=cons(indp,indli)
  >>$

  % Formulating a list of conditions
  while vli do <<
   sli:=cons(caar vli,sli);
   v:=caddar vli; vli:=cdr vli;
   conli:=cons(if cdr v then cons('PLUS,v)
                        else car v,
               conli)
  >>;

  % Now the investigation of the existence of equations with only one
  % term
  slilen:=length sli;
  mli  :=cons('LIST,  mli);
  conli:=cons('LIST,conli);
  if tr_drop then <<
   write"sli=",sli$terpri()$
   algebraic(write"mli=",mli)$
   algebraic(write"conli=",conli)$
   write"mpli=",mpli$terpri()$
  >>;
  for h:=1:slilen do <<             % for each possible single term
   newp:=car sli;sli:=cdr sli;
   pdes:=newpdes;
   while pdes and
         ((get(car pdes,'terms)>1) or
          (not zerop reval {'DIFFERENCE,get(car pdes,'val),newp})) do
   pdes:=cdr pdes;
   if null pdes then <<
    cp:=conli;
    for s:=1:h do cp:=cdr cp;
    rplaca(cp,reval {'PLUS,1,car cp});
    if tr_drop then <<
     write"h=",h$terpri()$
     algebraic(write"new conli=",conli)$
    >>;

    s:=cdr solveeval {conli,mli};
    if (null s) and tr_drop then <<write"no success"$terpri()>>$
    if s then <<                     % found 1-term equation
     if null success then
     for each p in newpdes do <<
      fl:=union(get(p,'fcts),fl);
      vl:=union(get(p,'vars),vl)
     >>$
     success:=t$
     maxlen:=0$
     s:=cdar s; % first solution (lin. system), dropping 'LIST

     % now find the equation to be replaced by the 1-term-equation
     % find the non-vanishing m in s, such that the corresponding p in
     % mpli has a maximum number of terms
     while s do <<
      if caddar s neq 0 then <<
       r:=cadar s;
       cp:=mpli;
       while caar cp neq r do cp:=cdr cp;
       if get(cdar cp,'terms)>maxlen then <<
        p:=cdar cp;              % p will be the equation to be replaced
        m:=r;
        maxlen:=get(p,'terms);
       >>
      >>$
      s:=cdr s
     >>$

     % Now replacing the old equation p by the new 1-term equation in conli:
     r:=0;
     newconli:=nil$
     while conli do <<
      v:=subst(0,m,car conli)$
      conli:=cdr conli$
      if r=h then <<
       v:=reval {'PLUS,{'TIMES,m,newp},v}$
      >>$
      newconli:=cons(v,newconli);
      r:=add1(r)
     >>$
     conli:=reverse newconli$

     % the new equation:
     newp:=mkeq(newp,fl,vl,allflags_,t,list(0),nil,nil);
     % last argument is nil as no new inequalities can result
     % if new equation has only one term
     newpdes:=cons(newp,newpdes);
     if print_ then <<
      terpri()$
      write"The new equation ",newp$ typeeq newp$
      write" replaces ",p$           typeeq p$
     >>;
     drop_pde(p,nil,nil)$
     newpdes:=delete(p,newpdes);

     % update of mpli:
     mpli:=subst(newp,p,mpli)$

     if tr_drop then <<
      write"mpli=",mpli$terpri()$
     >>;

    >>;                        % end of successful find
    cp:=conli;
    for s:=1:h do cp:=cdr cp;
    rplaca(cp,reval {'PLUS,-1,car cp});
   >>                          % if the 1-term PDE is not already known
  >>$                          % for each possible single term

 >>;
 return if success then list(newpdes,cadr arglist)
                   else nil
end$

endmodule$

end$

% moegliche Verbesserungen:
% - auch subtrahieren, wenn 0 Gewinn (Zyklus!)
% - kann Zyklus mit decoupling geben
% - evtl erst alle Quotienten bestimmen, dann die Heuristik:
%   . erst wo nur die kleinere Gleichung mit ftem-Funktionen multipliziert
%     wird
%   . wo nur die kleinere Gleichung multipliziert wird
%   . alle Quotienten werden auf Hauptnenner gebracht und der mit der
%     groessten Potenz mit der die kuerzere Gleichung multipliziert wird,
%     ist der erste
% - Erweiterung auf mehrere Gleichungen
% - counter example:
%   0 = +a+b+c
%   0 =   -b  +d+e
%   0 =     -c-d  +f
%   0 = -a      -e-f
%   combining any 2 gives a longer one
%   the sum of all 4 is even zero.
% - In order not to run into a cycle with decouple one could use
%   dec_hist_list but that costs memory.


