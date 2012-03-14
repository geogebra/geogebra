module contrtns;

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


global '(dimex!* sgn!*  signat!* spaces!* numindxl!* pair_id_num!*) ;


lisp (pair_id_num!*:= '((!0 . 0) (!1 . 1) (!2 . 2) (!3 . 3) (!4 . 4)
                        (!5 . 5) (!6 . 6) (!7 . 7) (!8 . 8) (!9 . 9)
                        (!10 . 10) (!11 . 11) (!12 . 12) (!13 . 13)));

fluid('(dummy_id!* g_dvnames epsilon!* !*distribute));

% g_dvnames is a vector.


switch onespace;

!*onespace:=t;  % working inside a unique space is the default.

fluid('(indxl_tens!* dummy_id!* g_dvnames)); % g_dvnames is a vector.

% This module contains the procedures which enhances the
% capabilities of 'canonical' which is the master function of DUMMY.RED.
% That function is now able to make tensor-like expressions contractions
% and to find the normal form of an expression containing "tensors"
% and derivatives of these and of operators.

% auxiliary functions to canonical:

symbolic procedure no_dum_varp u;
% u is the mvar of a msf
% returns t if the indices are all variables or if
% no indices.
% this is a variation on 'nodum_varp' which should still
% be improved.
 if null cdr u or (splitlist!:(cdr u,'list)=cdr u)  then t
   else nil;

%symbolic procedure no_dum_varp u;
% u is the mvar of a msf
% returns t if the indices are all variables
% or if
% covariant and contravariant indices are the same.
% this is a variation on 'nodum_varp' which should still
% be improved.
% it was aimed to avoid elimination of powers for traces but
% it does not work because they are treated as operators
% in sep-tens_from_other
% if null cdr u  or (splitlist!:(cdr u,'list)=cdr u)  then t
%   else
%     begin scalar ll;
%      ll:= splitlist!:(cdr u,'list);
%     if ll then
%       <<ll:=car ll;
%          ll:= for each y in split_cov_cont_ids cdr u collect ordn delete(ll,y)>>
%      else
%        ll:=for each y in split_cov_cont_ids cdr u collect ordn y;
%   if car ll = cadr ll then return t
% end;


symbolic procedure sep_tens_from_other u;
% u is a standard form which describes a monomial.
% output is list(<ordered list of tensor kernels>,<standard form without tensors>)
% does NOT change ordering since multiplication is not necessarily
% commutative.
 begin scalar mv,tel,other,y;
     other:= !*n2f 1;
  l: if numberp u then return list(reversip tel, multf(other,!*n2f u))
      else
     if atom mvar u  then other:=multf(other,!*p2f lpow u)
      else
     << if y:=get(car mvar u, 'Translate1) then
          << u:=fullcopy u; (mvar u:= apply1(y,mvar u)) >>;
%        if tensorp mvar u then tel:=mvar u . tel
%          else  other :=multf(other,!*p2f lpow u)>>;
         if tensorp(mv:=mvar u) then
            if null no_dum_varp mv
                  or flagp(car mv,'noncom)  then tel:=mvar u  . tel
             else other :=multf(other,!*p2f lpow u)
         else  other :=multf(other,!*p2f lpow u)

      >>;
      u:= lc u;
      go to l;
end;


symbolic procedure all_index_lst u;
% u is a list of tensor kernels.
% output is the list of all indices
% example:
% cc:= car sep_tens_from_other bb;
% ((te r b (minus c)) (te r (minus s) (minus d)) (te (minus r) c d))
% gives (r b (minus c) r (minus s) (minus d) (minus r) c d)
  if null u then nil
   else   append(
                 ((if listp car y and caar y = 'list then cdr y
                  else y ) where y=cdar u),
                                           all_index_lst cdr u);


symbolic procedure del_affin_tens u;
% u is a list of tensor kernels
 if null u then nil
  else
 if affinep car u then  del_affin_tens cdr u
  else car u . del_affin_tens cdr u;

symbolic procedure dv_canon_covcont(sf);
% for Riemanian spaces, places contravariant dummy indices first
% in place.
   if domainp sf then sf
    else
 begin scalar tenslist,idlist,dummyid;
   dummyid:=dummy_id!*;
   tenslist:=car sep_tens_from_other(sf); % get tensor list;
%   y:=del_affin_tens y;
   if null tenslist then return restorealldfs sf;
   idlist:=all_index_lst tenslist; %get list of all indexes;
    for each z in tenslist do
      if (get(car z,'partic_tens)='simpdel) or affinep z then
        for each y in cdr z do
          dummyid:=delete(raiseind!: y, dummyid);
    for each z in idlist do
       if atom z then
          (if z memq dummyid
            % first dummy index is high. no more to do with it.
              then dummyid:=delete(z,dummyid))
       else if careq_minus z and memq(cadr z,dummyid) then
          % first dummy index is low, change this.
           << sf:=subst(list('minus,cadr z),cadr z,sf);
              dummyid:=delete(cadr z,dummyid)>>;
  return restorealldfs sf;
end;

symbolic procedure cov_contp(u,v);
% u and v are  lists of  tensors indices
% verify if one has expressions of the form
% (a,b,c,...) and ((minus a')(minus b')(minus c')...)
% for u and v or for v and u.
% IMPORTANT for epsilon products.
 cov_lst_idsp u and cont_lst_idsp v
 or cont_lst_idsp u and cov_lst_idsp v;

symbolic procedure belong_to_spacep(u,sp);
% u is a list of indices
% sp is the name of a space
% t if ALL INDICES belong to sp.
% I do not think it is still needed. ****
 if null u  or sp = 'wholespace then t
  else
 if get(car u,'space) eq sp  then belong_to_spacep (cdr u,sp);

symbolic procedure extract_tens(tel,sp_tens);
% tel is a list of tensor kernels as given by the car of the
% output of 'sep_tens_from_other'
% sp_tens is the name of a special tensor
% result is a list of these tensors found in tel
  if null tel then nil
  else
  if caar tel = sp_tens then
                 car tel . extract_tens(cdr tel,sp_tens)
   else extract_tens(cdr tel,sp_tens);


symbolic procedure treat_dummy_ids(sf);
% manage all dummy indices by interfacing with dummy.red
% Creates bags of ids belonging to same space, and them call
% the simplification procedure form dummy.
 if !*onespace
  then
    begin scalar user_g_dvnames,res;
      user_g_dvnames:=g_dvnames;
      dummy_nam dummy_id!*;
      res:=dv_canon_monomial sf;
      g_dvnames:=user_g_dvnames;
      return if g_dvnames then dv_canon_covcont dv_canon_monomial res
               else dv_canon_covcont res;
    end
  else
    begin scalar res,partit_space_lst,idxl,sp,user_g_dvnames,bool;
      partit_space_lst:=nil;
      user_g_dvnames:=g_dvnames;
      partit_space_lst:=for each y in spaces!* collect car y . nil;
    % Put each index with the ones belonging to same space
      for each z in dummy_id!* do
        if sp:=space_of_idx z then
    % dummy indices which have not been declared to belong to a (sub)space
    % are assumed to belong to 'wholespace'
    % and no error statement is generated iff 'wholespace' has been defined.
             if idxl:=assoc(sp,partit_space_lst) then
                              cdr idxl:=  z . cdr idxl
               else  rerror(cantens,14,
                 list("Index ",z," does not belong to a defined space"));

      res:=sf;
      for each z in partit_space_lst do
        if (idxl:=cdr z)
          then <<bool:=t; dummy_nam idxl;
                  res:=dv_canon_monomial(res)>>;
        if not bool then res:=dv_canon_monomial res; %% added
        g_dvnames:=user_g_dvnames;
      return if g_dvnames then dv_canon_covcont dv_canon_monomial res
               else dv_canon_covcont res;
    end;


%
% the dummy user procedure modified to perform tens calculations
%

symbolic procedure canonical sq;
  begin scalar sf, denom, !*distribute;
   sq := simp!* car sq;
   denom := denr sq;
   on distribute;
   sf := distri_pol numr sq;
  % Check coherence of dummy and free indices and generate dummy_id!*..
  %% simplify the whole thing, and return
  return simp!*( {'!*sq,
     canonical1(sf, cadr check_ids(sf)) ./ denom, nil} );
  end;

symbolic procedure canonical1 (sf, dumlist);
  begin scalar dummy_id!*, res;
   dummy_id!*:=dumlist;
  % WE MUST BE SURE THAT FURTHER SIMPLIFICATIONS WILL
  % NOT REPLACE AN ST BY SEVERAL ST's
  % IF RULES ARE APPLIED THEY  SHOULD HAVE ACTED BY NOW.
  %  IF SEVERAL TENSORS ARE OF THE EPSI KIND THEY MUST ANALYZED
  %  AND, POSSIBLY, REPLACED BY 'DEL' OR EXPANSIONS OF IT.
  %  FOR INSTANCE e(-a,-b)*e(c,d)=
  %  del(-a,c)*delt(-b,d) - del(-a,d)*delt(-b,c)
  %  then we must generate a SUM of standard forms
  % This is HERE that products of epsilon tensors should be dealt with
  % => SIMPEPSE.RED.
  % Epsi simplification.
   while not domainp sf do
   << res:=addf(res,simpepsi_mon_expr(lt sf .+ nil));
      sf:=red sf;
    >>;
   sf:= distri_pol addf(res,sf);
     res:=nil;
  while not domainp(sf) do
  <<
   (if length car y >=2
      then res:= addf(res,dv_canon_tensor y)
        else  res := addf(res, treat_dummy_ids(lt sf .+ nil)))
                                 where y=sep_tens_from_other (lt sf .+ nil);
    sf:=red sf;
   >>;
   clearallnewids();
  % Now add the domainp term:
   return
     res := addf(res,sf);
  end;

symbolic procedure tensor_has_dummy_idx(dum,te);
% dum is a list of dummy indices
% te is a tensor in prefix form.
% T(rue) if one of the indices of te belongs to dum.
 if null dum then nil
 else
 if smember(car dum, te) then t
 else tensor_has_dummy_idx(cdr dum,te);

symbolic procedure tens_list_is_generic tel;
% tel is a list of tensors
% output is T(rue) if ALL tensors are generic
 if null tel then t else
 if null get(caar tel,'partic_tens) then tens_list_is_generic cdr tel;


symbolic procedure mk_delta_first tel;
% input is a list of tensor kernels.
% output is an equivalent list with
% all delta-like tensors placed first
% and eta-like tensors second.
 begin scalar x,y,z;
  x:=extract_tens(tel,get('delta,'name));
  z:=setdiff(tel,x);
  y:=extract_tens(z,get('eta,'name));
  z:=setdiff(z,y);
 return append(x,append(y,z))
 end;


symbolic procedure dv_canon_tensor u;
% u is list(<list of tensor kernels>,<standard form without tensors>)
% output is a standard form given to dv_canon_monomial.
% First take the list of tensor kernels and make the contractions
% if necessary.
  begin scalar x,tel,tel_dum,tel_free,notens;
     tel:=car u; tel_free:=!*n2f 1; notens:=cadr u;
 % replace the list tel by tel_dum
 % where tel_dum contains tensors with dummy indices.
 % and put the rest in tel_free
     for each y in tel do
       if tensor_has_dummy_idx(dummy_id!*,y) then tel_dum:=y . tel_dum
       else tel_free:=multf(!*k2f y,tel_free);
   tel_dum:=tel_dum; % to restitute the order
 % now tel_dum must eventually be transformed by contractions.
 % Two cases appear:
 % all tensors in tel_dum are generic:
  return
   if tens_list_is_generic tel_dum then
    <<x:=!*n2f 1;
       if tel_dum then tel_dum:=for each y in tel_dum collect !*k2f y;
      while tel_dum do <<
                     x:=multf(car tel_dum, x);tel_dum:=cdr tel_dum;
                       >>;
      multf(restorealldfs tel_free,treat_dummy_ids multf(x,notens))
     >>
 % one or several tensors are particular ones:
  else
  % simptensexpr must output a standard form.
          multf(restorealldfs tel_free,
            treat_dummy_ids multf(simptensexpr(
              mk_delta_first tel_dum,dummy_id!*,1),notens));
 end;

symbolic procedure simptensexpr(tel,dum,i);
% tel is the list of tensor kernels
% dum is the associated list of dummy variable
% output should be the standard form of the contracted tensors.
 begin scalar res;
  res:=!*n2f 1;
 return
      if numberp tel then  !*n2f tel
      else
      if atom tel  or length tel=1 then !*k2f car tel
      else
      if i>=length tel + 1 then
          <<for each i in tel do res:=multf(res,!*k2f i);res>>
      else
     (if y memq list('simpdelt,'simpeta,'simpmetric)
      then simpdeltetaexpr(tel,dum,i)
      else simptensexpr(tel,dum,i+1)
%  here the epsi tensors should NOT be considered
%  since they are already simplified.
     )where y=get(car nth(tel,i),'partic_tens);
 end;


symbolic procedure simpdeltetaexpr(tel,dum,i);
% output is the result of contraction of the ith tensor
% with the other ones.
% tensor with the other-ones (at least one is present).
% The SAME procedure appears to be valid for BOTH 'delta' and 'eta'.
 begin scalar itel,rtel,res,old,new;
 % itel is delta tensor kernel.
 % rtel is the list of the other tensors
 % res is the new  list of kernels.
   itel:=nth(tel,i);
  if (id_switch_variance cadr itel) neq caddr itel
                   and intersection(flatindxl cdr itel,dum)  then
  << rtel:=remove(tel,i);
    % let  us identify where the dummy index in itel is:
    % and define substitution variables:
      if (old:=raiseind!: cadr itel) memq dum
        then << old:=id_switch_variance cadr itel; new:=caddr itel >>
        else << old:=id_switch_variance caddr itel; new:=cadr itel >>;
      res:=subst(new,old,rtel);
      return simptensexpr(res,dum,i)
   >>
     else return simptensexpr(tel,dum,i+1);
 end;


symbolic procedure select_epsi_pairs ep;
% result is a list of PAIRS of contractible (to DEL)
% epsilon-pairs.
% if there are 3 or more epsilons of a given kind,
% they are eliminated. So contractions will NOT be done.
% to allow for this, generalize THIS procedure.
% the problem however is which two among the three of
% should we choose.
 if null ep then nil
  else
 (if length x = 2 and cov_contp(cdar x,cdadr x) then
                  x .  select_epsi_pairs cdr ep
   else select_epsi_pairs cdr ep) where x=car ep;


symbolic procedure mk_eps_lst tkl;
% tkl is a list of tensor kernels
% extract the list of contractible epsilon pairs from tkl
% and substracts them from tkl.
% returns list(<epsilon pair list>,<new tkl>) or nil.
 begin scalar eps_lst;
   eps_lst:= if !*onespace and get('epsilon,'name) then
                list extract_tens(tkl,find_name('epsilon))
               else
             if epsilon!* then
                  for each i in epsilon!* collect extract_tens(tkl,car i)
              else nil;
   eps_lst:=select_epsi_pairs eps_lst;
   if null eps_lst then
  return list(nil,tkl);
   for each j in eps_lst do tkl:=setdiff(tkl,j);
  return list(eps_lst,tkl)
 end;

symbolic procedure get_sign_space!: u;
 if null u then signature '? else
  get_sign_space u;

symbolic procedure epsi_to_del(ep);
% ep is a list of contractible epsilon pairs.
% returns a standard form which represents the product of
% the DEL-like objects
% First  task: replace all eps-products by DEL-like objects
% taking properly into account the space signature.
% Second task: reconstruct the SF-product.
 if null ep then nil
   else
  begin scalar del_prd,x,y;
  % del_prd is the SF which results from application of SIMPDEL
   del_prd:=!*n2f 1;
  for each j in ep do
   <<x:=all_index_lst j;
     if get_sign_space!:(if y:=assoc(caar j,epsilon!*) then cdr y
                             else nil) = 1 then
          del_prd:=multf(negf apply1('simpdel,find_name('del) . x), del_prd)
      else  del_prd:=multf(apply1('simpdel,find_name('del) . x), del_prd)>>;
   return del_prd
  end;

symbolic procedure simpepsi_mon_expr msf;
% msf  is a monomial standard form.
% result is a NEW STANDARD FORM after simplifications on epsilon products
% presently, we limit simplification to the case of TWO epsilons
% for each defined space .
% since more general products are usually not encountered.
  if domainp msf then msf
   else
    begin scalar tens_msf,notens,x,del_prd;
     % First  see if some simplifications are possible.
       tens_msf:=sep_tens_from_other msf;
       notens:=cadr tens_msf;
       notens:=if notens then notens else !*n2f 1;
       tens_msf:=car tens_msf;
       if null tens_msf then return msf;
     % we have to extract relevant epsilon products from tens_msf
     % and construct the DEL-like product
       x:=mk_eps_lst tens_msf;
       tens_msf:=reverse cadr x;
     % function epsi_to_del returns an SF
       del_prd:= epsi_to_del car x;
     % we do the product of DEL-like tensors and operators.
       x:=if del_prd then multf(del_prd,notens)
              else notens;
       for each j in tens_msf do x:=multf(!*k2f j,x);
       % returns tne new SF which is NO LONGER a monomial.
       return x
     end;


endmodule;

end;
