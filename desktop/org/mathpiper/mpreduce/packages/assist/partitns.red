module partitns;

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


% definitions of particular tensors.

global '(dimex!* sgn!*  signat!* spaces!* numindxl!* pair_id_num!*);

fluid('(dummy_id!* g_dvnames epsilon!*));

% epsilon!* keeps track of the various epsilon tensors
% which may be defined when onespace is OFF
% It is a list pairs (<space-name> . <name>)

switch exdelt; % default is OFF

switch onespace;

!*onespace:=t;  % working inside a unique space is the default.

flag(list('delta,'epsilon,'del,'eta,'metric), 'reserved); % they are keywords.

symbolic flag(list('make_partic_tens),'opfn);

symbolic procedure make_partic_tens(u,v);
% u is a bare identifier (free of properties)
% the result is T(rue) when it suceeds to create
% the properties of being a particular tensor on u.
% can be trivially generalized to other tensors.
 if v memq {'delta,'eta,'epsilon,'del,'metric} then
 <<
    if get(u,'avalue)
% or (get(u,'reserved) and null flagp(u,'tensor))
     or getrtype u  or (gettype u eq 'procedure) or
     % is this necessary?
     (u memq list('sin,'cos,'tan,'atan,'acos,'asin,'df,'int))  then
        rerror(cantens,5,list(u,"may not be defined as tensor"))
     else
    if flagp(u,'tensor) then
      <<lpri {"*** Warning:", u,"redefined as particular tensor"};
        remprop(u,'kvalue);
        remprop(u,'simpfn);
        remprop(u,'bloc_diagonal);
        remflag(list u,'generic);
      >>;
% the 'name' indicator allows to find
% the name chosen for a particular tensor from the keyword
% associated to it.
% Only ONE tensor of type 'delta' and 'eta' are allowed so:
  (if x and v memq {'delta,'eta,'del} then rem_tensor1 x)where x=get(v,'name);
   make_tensor(u,nil);  % contains the action of rem_tensor
   put(u,'partic_tens, if v = 'delta then 'simpdelt
                        else
                       if v = 'eta then 'simpeta
                        else
                       if v = 'epsilon then 'simpepsi
                        else
                       if v = 'del then 'simpdel
                        else
                       if v= 'metric then 'simpmetric);
   if null !*onespace and v = 'epsilon
        then
          if epsilon!*
                then <<put(v,'name,u);
                       lpri {"*** Warning:", u,"MUST belong to a space"};>>
               else nil;
    put(v,'name, u);
   if v memq {'metric,'delta} then <<flag(list u,'generic);
                                         make_bloc_diagonal u>>;
   t
 >>
      else "unknown keyword";



symbolic procedure find_name u;
% find the name of a particular tensor whose keyword is u.
% Must still be extended for u=epsilon
(if null x then
      rerror(cantens,6,{" no name found for", list u})
 else x)where x=get(u,'name);

% **** Simplification functions for particular tensors


symbolic procedure simpdelt (x,varl);
% x is is a list {<tensor> indices}
% for instance (tt a (minus b)) for tt(a,-b)
% varl is the set of variables {v1,v2, ...}
% result is the simplified form of the Dirac delta function if varl is nil
% and cdr x is nil.
 If varl and null cdr x then !*k2f(car x . varl . nil) else
  if null varl or null cdr varl then
 begin scalar delt,ind,y,yv,yc;
  delt := car x; ind:= cdr x;
  y:=split_cov_cont_ids ind;
 if (length car y * length cadr y) neq 1 then
   rerror(cantens,7, "bad choice of indices for DELTA tensor");
  yv:=caar y;
  yc:=caadr y;
 % The conditional statement below can be suppressed if
 % 'wholespace' can be defined with an indexrange.
% if get(delt,'belong_to_space) eq  'wholespace then
%    if get_sign_space('wholespace) = 0 then
%      if yv='!0 or yc ='!0 then
%        rerror(cantens,2,"bad value of indices for DELTA tensor");
 if !*id2num yv and !*id2num yc then return
     if  yv=yc  then 1
      else  0
  else
  if !*onespace then return
       if yv eq yc then dimex!*
       else !*k2f(delt . append(cadr y,lowerind_lst car y))
  else return
   if null get(yv,'space) and yv eq yc then
      if  assoc('wholespace,spaces!*) then !*k2f get_dim_space 'wholespace
       else "not meaningful"
    else
   if  yv eq yc then  !*k2f space_dim_of_idx yv
     else  !*k2f(delt . append(cadr y,lowerind_lst car y))
 end
else "not meaningful";

symbolic procedure simpdel u;
% u is the list {<del-name> <covariant indices>
% <contravariant indices>}
% when 'DEL' is used by the system through simpepsi,
% indices are already ordered and, when 'canonical' is entered,
% they are again ordered after contractions. So ordering is
% necessary only if the user enters it from the start.
% in spite of this, the procedure is made to order them
% in all cases. REFINEMENTS to avoid that are possible.
% returns a standard form.
  begin scalar del,ind,x,idv,idc,idvn,idcn,bool,spweight;
        integer free_ind,tot_ind,dim_space;
   del:= car u;
   ind:=cdr u;
   spweight:=1;
 % though it is antisymmetric separately with respect to the cov
 % and cont indices we do not declare it as such for the time being.
   x:=split_cov_cont_ids ind;
   idv:= car x; idc:=cadr x;
   if length idv neq length idc then
      rerror(cantens,7, "bad choice of indices for DEL tensor")
    else
     if null !*onespace then
       if null symb_ids_belong_same_space!:(
                     append(idv,idc),nil) then
        rerror(cantens,7, "all indices should belong to the SAME space")
    else
   if repeats idv or repeats idc then return 0
    else
   if length idc =1 then return
      apply2('simpdelt, find_name('delta) . append(lowerind_lst idv,idc),nil);
  % here we shall start to find the dummy indices which are internal
  % to 'del' as in the case del(a,b,a1..an, -a,-b,-c1, ...-cn) which
  % can be simplified to del(a1,...an,-c1, ...,-cn)*polynomial in the
  % space-dimension or a number if N_space=number
  % first arrange each list so that dummy indices are at the beginning
  % of idv and idc.
    idv:=for each y in idv collect  %au lieu de idvn
                     if null !*id2num y and memq(y,idc) then list('dum,y)
                      else y;
    idc:=for each y in idc collect
                     if null !*id2num y and memq(y,car x) then list('dum,y)
                      else y;
    if permp!:(idvn:=ordn idv,idv)=permp!:(idcn:=ordn idc,idc) then bool:=t;
    % the form of these new lists is ((dum a) (dum b) ..ak..) etc ...
   % 1. they contain only  numeric indices:
      if num_indlistp append(idvn,idcn) then
                       return simpdelnum(idvn,idcn,bool);
   % 2. some indices are symbolic:
       tot_ind:=length idvn;
   %  dummy indices can be present:
      idv:=splitlist!:(idvn,'dum); % if no dummy indices, it is nil.
       free_ind:=tot_ind - length idv;
    % now search the space in which we are working.
      dim_space:= if idv then     %% since, may be, no dummy indices
                       if null spaces!* then  dimex!*
                                else !*k2f space_dim_of_idx cadar idv;
      for i:=free_ind : (tot_ind -1) do
              <<spweight:=multf(addf(dim_space,negf !*n2f i),spweight);
                 idvn:=cdr idvn; idcn:=cdr idcn;
              >>;
       spweight:=!*a2f reval prepf spweight;
      if null idvn then
          return
          if bool then spweight
           else negf spweight;
    % left indices can again be all numeric indices
      if num_indlistp append(idvn,idcn) then
                        return
              multf(spweight,simpdelnum(idvn,idcn,bool));
    % 3. There is no more internal dummy indices, so
        return
%      if !*exdelt then
%             if bool then
%           multf(spweight,extract_delt(del,idvn,idcn,1))
%              else  negf multf(spweight,extract_delt(del,idvn,idcn,1))
%        else
      if !*exdelt then
             if bool then
           multf(spweight,extract_delt(del,idvn,idcn,'full))
              else negf multf(spweight,extract_delt(del,idvn,idcn,'full))
        else
      if length idvn=1 then
            if bool then
            multf(spweight,
                 !*k2f(find_name('delta) . append(lowerind_lst idvn,idcn)))
             else
              negf multf(spweight,
                !*k2f(find_name('delta) . append(lowerind_lst idvn,idcn)))
      else
         if bool then
           multf(spweight,!*k2f(del . append(lowerind_lst idvn ,idcn)))
          else
           multf(spweight,negf
                 !*k2f(del . append(lowerind_lst idvn , idcn)))
 end;


symbolic procedure simpdelnum(idvn,idcn,bool);
% simplification of 'DEL' when all indices are numeric.
 if idvn=idcn then
          if bool then 1
           else   -1
  else 0;

symbolic procedure extract_delt(del,idvn,idcn,depth);
% we  deal with already ordered lists. Numeric indices
% come first like (!1 !2 a). So, extraction is done from
% the left because the result simplify more.
 if length idcn =1 then
    apply2(function simpdelt,
         get('delta,'name) . lowerind car idvn . car idcn . nil,nil)
  else
      begin scalar uu,x,ind;
         ind:=car idcn;
         idcn:=cdr idcn;
         if depth =1 then
               for i:=1:length idvn do
               <<x:=multf(exptf(-1,i-1),
                    multf(apply2(function simpdelt,
                    get('delta,'name) . (ind . list lowerind nth(idvn,i)),nil),
                     !*q2f mksq((if length idvn=2 then get('delta,'name)
                                  else del) . append(idcn,
                                       lowerind_lst  remove(idvn,i)),1)
                          )
                         );
                   uu:=addf(x,uu)
                 >>
              else
             if depth='full then
                  for i:=1:length idvn do
                  <<x:= multf(exptf(-1,i-1),
                       multf(apply2(function simpdelt,
                       get('delta,'name) . (ind . list lowerind nth(idvn,i)),nil),
                       extract_delt(del,remove(idvn,i),idcn,depth)
                            )
                            );
                   uu:=addf(x,uu)
                >>;
       return uu
      end;


symbolic procedure idx_not_member_whosp u;
% u is an index
(if x then x neq 'wholespace) where x=get(u,'space);

symbolic procedure ids_not_member_whosp u;
% U is a list of indices.
 if null u then t
  else
 if idx_not_member_whosp car u then ids_not_member_whosp cdr u
   else nil;

symbolic procedure simpeta u;
% u is a list {<tensor> indices}
% for instance tt(a b) or tt(a -b) or tt(-a,-b)
% result is the simplified form of the Minkowski metric tensor.
  if (!*onespace and signat!*=0)
   then msgpri(nil,nil,
           "signature must be defined equal to 1 for ETA tensor",nil,t)
   else
  if
   (null !*onespace and null get_sign_space get(car u,'belong_to_space))
     then
    msgpri(nil,nil,
           "ETA tensor not properly assigned to a space",nil,nil)
      else
 begin scalar eta,ind,x;
  eta := car u; ind:= cdr u;
  flag(list eta,'symmetric);
  x:=split_cov_cont_ids ind;
  if car x  and  cadr x  then return
     apply2('simpdelt,find_name('delta) . ind,nil);
 %  Now BOTH indices are up or down, so
  x:=if null car x then cadr x else car x;
  if length x neq 2 then
   rerror(cantens,8, "bad choice of indices for ETA tensor");
  x:=for each y in x collect !*id2num y;
  return if numlis x then num_eta x
         else
  if !*onespace then !*k2f(eta . ordn ind)
  else
  if ids_not_member_whosp {car ind,cadr ind} and
        get(car ind,'space) neq get(cadr ind,'space) then 0
  else !*k2f(eta . ordn ind)
  end;


symbolic procedure num_eta u;
% u is the list of covariant or contravariant indices of ETA.
 if car u = cadr u then
       if car u = 0 then sgn!*
       else  negf sgn!*
 else 0;


symbolic procedure simpepsi u;
% Simplification procedure for the epsilon tensor.
 begin scalar epsi,ind,x,spx,bool;
  epsi := car u;
  % spx is the space epsi belongs to.
  % so we can define SEVERAL epsi tensors.
  spx:= get(epsi,'belong_to_space); % In case several spaces are used.
                                    % otherwise it is nil
  ind:= cdr u;
  flag(list epsi,'antisymmetric);
  x:=split_cov_cont_ids ind;
  if  null car x then x:='cont . cadr x
     else
     if null cadr x then  x:= 'cov . car x
     else
   x:= 'mixed . append(car x, cadr x);
 % If the space has a definite dimension we must take care of the number
 % of indices:
 (if fixp y and y neq length cdr x then
   rerror(cantens,9,
             list("bad number of indices for ", list car u," tensor"))
  )where y= if spx then get_dim_space spx
              else (if fixp z then z)where z=wholespace_dim '?;
  if repeats x then return 0;
%  if null !*onespace then one must verify that all
%  indices belong to the same space as epsi.
   if null !*onespace and spx then
    if null ind_same_space_tens(cdr u,car u) then
      rerror(cantens,9, list("some indices are not in the space of",epsi));
 return
  if car x  eq 'mixed or not num_indlistp cdr x then
   begin scalar xx,xy;
    xx:=ordn ind;
    bool:=permp!:(xx,ind);
    if car x eq 'mixed then
            <<xy:=cont_before_cov ind;
                 if null permp!:(xy,xx) then bool:=not bool>>;
    return if bool then
                     !*k2f(epsi . if car x eq 'mixed then
                                  xy else xx)
   else negf !*k2f(epsi . if car x eq 'mixed then
                                  xy else xx)
   end
   else
  % cases where all indices are numeric ones must be handled separately
  % Take the case where either no space is defined or declared. Then
  % space is euclidean.
  % look out ! spx is EUCLIDEAN by default. To avoid it, use
  % 'make_tensor_belong_space'.
  if !*onespace or null spx  then
          if signat!* =0 then num_epsi_euclid(x)
              else
          if signat!* =1 then num_epsi_non_euclid (epsi,x)
            else nil
   else
  if  null get_sign_space spx or get_sign_space spx=0
                                            then  num_epsi_euclid (cdr x)
  else
  if  get_sign_space spx =1 then num_epsi_non_euclid (epsi,x)
  else
  "undetermined signature or signature bigger then 1";
 end;


symbolic procedure num_epsi_non_euclid(epsi,ind);
% epsi is the name of the epsilon tensor
% ind is the list (cont n1 n2  nk) or (cov n1 n2 .. nk)
% result is either 0 OR +- (epsi 0 1 2 .... k))
% i.e. in terms of contravariant indices.
% So, in case of covariant indices we must take care of the
% product eta(0,0)*... *eta(spx,spx) and the convention
% sgn!* enters the game.
 begin scalar x;
 x:=ordn cdr ind;
 return if car ind eq 'cont then
             (if y then y
               else  if permp!:(x,cdr ind) then !*k2f(epsi . x)
                        else negf !*k2f(epsi . x))where
                                             y=!*q2f match_kvalue(epsi,x,nil)
           else
           if car ind eq 'cov then
                 if sgn!* = 1  then
                      if evenp length cdr x then
                        (if y then y
                           else  if permp!:(x,cdr ind) then !*k2f(epsi . x)
                                  else negf !*k2f(epsi . x))where
                                            y=!*q2f match_kvalue(epsi,x,nil)
                       else
                       (if y then negf y
                         else if permp!:(x,cdr ind) then negf !*k2f(epsi . x)
                        else  !*k2f(epsi . x))where
                                            y=!*q2f match_kvalue(epsi,x,nil)
                  else
                 if sgn!* =-1 then
                      (if y then negf y
                       else if permp!:(x,cdr ind) then negf !*k2f(epsi . x)
                              else !*k2f(epsi . x))where
                                            y=!*q2f match_kvalue(epsi,x,nil)
                 else nil
           else nil;
 end;

flag({'show_epsilons},'opfn);

symbolic procedure show_epsilons();
(if null x then {'list}
  else 'list . for each y in x collect
      list('list,mk!*sq !*k2q car y,mk!*sq !*k2q cdr y))where x=epsilon!*;


symbolic procedure match_kvalue(te,ind,varl);
% te is a tensor, result is nil or a standard form.
% Must return a standard quotient.
(if x then  simp!* cadr x)where
                          x= if varl then
                              assoc(te . varl . ind,get(te,'kvalue))
                              else assoc(te . ind,get(te,'kvalue));


symbolic procedure num_epsi_euclid(ind);
% ind is the list (i1, ...,in), therefore
% here epsi(1,2,  n)=1=epsi(-1,-2, ... -n)
  begin scalar x;
    x:=ordn ind;
  return if permp!:(x,ind) then 1
          else -1
  end;


symbolic procedure simpmetric(u,var);
% generic definition of the metric tensor
% covers the possibility of several spaces.
% may depend of any number of variables if needed.
% 'var' is {x1, .. xn}.
% receives an SF and sends back an SQ.
% CORRECTED
 begin scalar g,ind,x;
  if x:=opmtch u then return  simp x;
   g:=car u; ind:=cdr u;
  flag(list g,'symmetric);
   x:=split_cov_cont_ids ind;
  if car x  and  cadr x  then return
     apply2('simpdelt,find_name('delta) . ind,nil) ./ 1;
 %  Now BOTH indices are up or down, so
  x:=if null car x then cadr x else car x;
  if length x neq 2 then
   rerror(cantens,10, "bad choice of indices for a METRIC tensor");
   % case of numeric indices.
     x:=for each y in x collect !*id2num y;
   return if numlis x then
             if !*onespace then
                if x:= match_kvalue(g,ordn ind,var) then x
                 else !*k2f(g . if var then var . ordn ind
                                   else ordn ind) ./ 1
             else mult_spaces_num_metric(g,ind,var) ./ 1
         else
  if !*onespace then
      if x:= match_kvalue(g,ordn ind,var) then x
       else !*k2f(g . if var then var . ordn ind
                        else ordn ind) ./ 1
   else
  if get(car ind,'space) neq get(cadr ind,'space) then 0
   else
  if x:= match_kvalue(g,ordn ind,var) then x
     else !*k2f(g . if var then var . ordn ind
                     else ordn ind) ./ 1
 end;


symbolic procedure mult_spaces_num_metric(g,ind,var);
% g, is the name of the metric tensor
% ind its numeric indices (both covariant or contravariant)
  begin scalar x,y;
   x:=if pairp car ind then raiseind_lst ind else ind;
   return
   if numindxl!* and null numids2_belong_same_space(car x,cadr x,g) then  0
    else
   if y:= match_kvalue(g,if var then var . ordn ind
                                   else ordn ind,var) then y
    else !*k2f(g . if var then var . ordn ind
                     else ordn ind)
  end;

endmodule;

end;
