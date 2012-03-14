module supervf;


% Redistribution and use in source and binary forms, with or without
% modification, are permitted provided that the following conditions
% are met:
%
%    * Redistributions of source code must retain the relevant
%      copyright notice, this list of conditions and the following
%      disclaimer.
%    * Redistributions in binary form must reproduce the above
%      copyright notice, this list of conditions and the following
%      disclaimer in the documentation and/or other materials provided
%      with the distribution.
%
% THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
% "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
% LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
% A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
% OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
% SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
% LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
% DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
% THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
% (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
% OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
%
% % *****************************************************************
%
% Authors: P. Gragert, P.H.M. Kersten, G.H.M. Roelofs, G.F. Post
% University of Twente (Enschede, The Netherlands)
%
% Version and Date:  Version 1.0, 1992.
%
% Maintainer: Raffaele Vitolo
% Dipartimento di Matematica, Universita' del Salento (Lecce, Italy)
% email: raffaele.vitolo@unisalento.it
% web: http://poincare.unisalento.it/vitolo
% ===============================================================


symbolic$
% write"Super vectorfield package for REDUCE, $Revision: 1.1 $"$terpri()$

put('ext, 'simpfn, 'simpiden)$




global '(!*natural_wedges)$
!*natural_wedges:=nil$
flag('(natural_wedges), 'switch)$
put('natural_wedges, 'simpfg,
   '((t(natural_wedges_handler t))(nil(natural_wedges_handler nil))))$



algebraic$

lisp operator super_vectorfield;

lisp procedure super_vectorfield(operator_name,even_variables,odd_variables);
   begin
      scalar odd_dimension;
      if not idp operator_name then
         msgpri("SUPER_VECTORFIELD:",operator_name,
	    "is not an identifier",nil,t);
      put(operator_name, 'simpfn, 'super_der_simp);
      flag(list(operator_name), 'full);
      even_variables:=
         if null even_variables then even_variables
         else if atom even_variables then list even_variables
         else if car even_variables= 'list then cdr even_variables
         else even_variables;
      odd_variables:=
         if null odd_variables then odd_variables
         else if atom odd_variables then list odd_variables
         else if car odd_variables= 'list then cdr odd_variables
         else odd_variables;
      odd_dimension:=0;
      for each kernel in odd_variables do
         if length kernel neq 2 or
	 car kernel neq 'ext or
	 not fixp cadr kernel then
            msgpri("SUPER_VECTORFIELD:",kernel,
	       "not a valid odd variable",nil,t)
         else odd_dimension:=max(odd_dimension,cadr kernel);
      put(operator_name, 'variables,even_variables);
      put(operator_name, 'even_dimension,length even_variables);
      put(operator_name, 'odd_dimension,odd_dimension);
      put(operator_name, 'setkfn, 'setk_super_vectorfield);
      return list('list,length even_variables,odd_dimension);
   end$

lisp operator vectorfield;
lisp procedure vectorfield(operator_name,variables);
   super_vectorfield(operator_name,variables,nil)$


lisp operator add_variables_to_vectorfield;
lisp procedure add_variables_to_vectorfield(operator_name,variables);
   if get(operator_name, 'simpfn)neq 'super_der_simp then

      msgpri("ADD_VARIABLE_TO_VECTORFIELD:",operator_name,
	 "not a vectorfield",nil,t)
   else <<
      variables:=append(get(operator_name, 'variables),if null variables then variables else if atom
	 variables then list variables else if
	    car variables= 'list then cdr variables else variables);
      put(operator_name, 'variables,variables);
      put(operator_name, 'even_dimension,length variables)>> $


lisp operator add_odd_variables_to_vectorfield;
lisp procedure add_odd_variables_to_vectorfield(operator_name,odd_variables);
   if get(operator_name, 'simpfn)neq 'super_der_simp then

      msgpri("ADD_VARIABLE_TO_VECTORFIELD:",operator_name,
	 "not a vectorfield",nil,t)
   else begin scalar odd_dimension;
      odd_variables:=if null odd_variables then odd_variables else if atom
	 odd_variables then list odd_variables else if
	    car odd_variables= 'list then cdr odd_variables else odd_variables;
      odd_dimension:=get(operator_name, 'odd_dimension);

      for each kernel in odd_variables do
	 if length kernel neq 2 or car kernel neq 'ext
	    or not fixp cadr kernel then

	       msgpri("SUPER_VECTORFIELD:",kernel,"not a valid odd variable",nil,t)
	 else odd_dimension:=max(odd_dimension,cadr kernel)

	    ;
      return put(operator_name, 'odd_dimension,odd_dimension);
   end$

lisp procedure merge_lists(x1,x2);
   begin scalar cx1,cx2,lx2,clx2,oddskip,sign;

      sign:=1;
      x1:=reverse x1;
      if x1 then cx1:=car x1 else goto b;
   a:if x2 then cx2:=car x2 else goto b;
      if cx1<cx2 then goto b;
      lx2:=cx2 . lx2;
      oddskip:=not oddskip;
      x2:=cdr x2;
      goto a

	 ;
   b:

      if null x1 then return sign . reversip2(lx2,x2);
      if null lx2 then return sign . reversip2(x1,x2);
      clx2:=car lx2;
      if cx1=clx2 and cx1>0 then return nil;
      if cx1>clx2 then goto b1;

      x2:=clx2 . x2;
      lx2:=cdr lx2;
      oddskip:=not oddskip;
      goto b

	 ;
   b1:

      x2:=cx1 . x2;
      x1:=cdr x1;
      if oddskip and cx1>0 then sign:=-sign;
      if x1 then cx1:=car x1;
      goto b



	 ;
   end$


lisp procedure ext_mult(x1,x2);
   (if null x then nil ./ 1
   else if null cdr x then 1 ./ 1
   else(((!*a2k('ext . cdr x) .^ 1) .* car x) .+ nil) ./ 1)
      where x=merge_lists(cdr x1,cdr x2)$


lisp procedure super_der_simp u;
   if length u=2 then

   begin scalar derivation_name,variables,even_components,odd_components,
	 splitted_numr,splitted_denr;
      derivation_name:=reval car u;
      variables:=get(derivation_name, 'variables);
      u:=simp!* cadr u;

      splitted_numr:=split_form(numr u, '(ext));
      splitted_numr:=
	 (list('ext) . car splitted_numr) . cdr splitted_numr;
      splitted_denr:=split_form(denr u, '(ext));
      splitted_denr:=
	 (list('ext) . car splitted_denr) . cdr splitted_denr;
      even_components:=for i:=1:get(derivation_name, 'even_dimension)collect
	 (nth(variables,i) . split_ext(component, '(ext)))
	    where component=simp!* list(derivation_name,0,i);
      odd_components:=for i:=1:get(derivation_name, 'odd_dimension)collect
	 (i . split_ext(component, '(ext)))
	    where component=simp!* list(derivation_name,1,i)

	       ;
      return subtrsq(
	 quotsq(addsq(even_action(even_components,splitted_numr),
	    odd_action(odd_components,splitted_numr)),denr u ./ 1),
	 quotsq(super_product_sq(even_action(even_components,splitted_denr),
	    numr u ./ 1),
	    multf(denr u,denr u) ./ 1));
   end


   else simpiden u$


lisp procedure split_ext(sq,op_list);
   begin scalar denr_sq,splitted_form;
      denr_sq:=denr sq;
      splitted_form:=split_form(numr sq,op_list);
      return(list('ext) . cancel(car splitted_form ./ denr_sq)) .
	 for each kc_pair in cdr splitted_form collect
	    (car kc_pair . cancel(cdr kc_pair ./ denr_sq))
   end$


lisp procedure even_action(components,splitted_form);
   begin scalar action;
      action:=nil ./ 1;
      for each kc_pair in splitted_form do
	 action:=addsq(action,
	    even_action_sf(components,cdr kc_pair,car kc_pair,1));
      return action;
   end$


lisp procedure even_action_sf(components,sf,ext_kernel,fac);
   begin scalar action;
      action:=nil ./ 1;
      while not domainp sf do
      <<action:=addsq(action,even_action_term(components,lt sf,ext_kernel,fac));
	 sf:=red sf>> ;
      return action;
   end$


lisp procedure even_action_term(components,term,ext_kernel,fac);
   addsq(even_action_pow(components,car term,
      ext_kernel,!*f2q multf(fac,cdr term)),
      even_action_sf(components,cdr term,
	 ext_kernel,multf(fac,!*p2f car term)))$


lisp procedure even_action_pow(components,pow,ext_kernel,fac);
   begin scalar kernel,n,component,derivative,action,active_components;
      kernel:=car pow;n:=cdr pow;

      if(component:=assoc(kernel,components))then
	 return
 	 <<derivative:=if n=1 then 1 ./ 1 else((((kernel .^ n-1) .* n) .+ nil) ./ 1);
	    action:=component_action(component,ext_kernel,derivative);
	    multsq(action,fac)>>

	       ;

      active_components:=find_active_components(kernel,components,nil)

	 ;

      action:=nil ./ 1;
      for each component in active_components do
      <<derivative:=diffp(pow,car component);
	 action:=addsq(action,component_action(component,ext_kernel,derivative))>> ;
      return multsq(action,fac)

	 ;
   end$


lisp procedure component_action(component,ext_kernel,coefficient);
   begin scalar action;
      action:=nil ./ 1;
      for each kc_pair in cdr component do
	 (if numr ext_product then
	    action:=addsq(action,
	       multsq(multsq(ext_product,even_coefficient),coefficient)))
		  where ext_product=ext_mult(car kc_pair,ext_kernel),
	 even_coefficient=cdr kc_pair;
      return action;
   end$


lisp procedure find_active_components(kernel,components,components_found);
   begin
      components_found:=
	 update_components(kernel .
	    ((if depl_entry then cdr depl_entry)where depl_entry=assoc(kernel,depl!*)),
	    components,components_found)$
      if not atom kernel then
	 for each element in kernel do
	    components_found:=find_active_components(element,components,components_found);
      return components_found;
   end$


lisp procedure update_components(dependencies,components,components_found);
   begin scalar component;
      for each kernel in dependencies do
	 if(component:=assoc(kernel,components))
	    and not assoc(kernel,components_found)then
	       components_found:=component . components_found;
      return components_found;
   end$


lisp procedure odd_action(components,splitted_form);
   begin scalar action,sign,derivative,kernel,coefficient,component;
      action:=nil ./ 1;
      for each kc_pair in splitted_form do
      <<kernel:=car kc_pair;
	 coefficient:=!*f2q cdr kc_pair;
	 sign:=t;
	 for each i in cdr kernel do
 	 <<sign:=not sign;
	    derivative:=!*a2k delete(i,kernel);
	    component:=assoc(i,components);
	    action:=addsq(action,
	       component_action(component,derivative,
		  if sign then negsq coefficient else coefficient))
	 >>
      >> ;
      return action;
   end$


lisp procedure setk_super_vectorfield(val,value);
   begin scalar vectorfield,var,variables,i,tuple;
      if length val neq 2 then return let2(val,value,nil,t);
      vectorfield:=car val;
      var:=cadr val;

      tuple:=
	 if not atom var and car var= 'ext and length var=2 then
	    list(1,cadr var)
	 else <<variables:=get(vectorfield, 'variables);i:=1;
	    while variables and var neq car variables do
 	       <<variables:=cdr variables;(i:=i+1)>> ;
	    if null variables then

	       msgpri("SETK_SUPER_VECTORFIELD:",var,
		  "not a valid variable for",vectorfield,t)
	    else list(0,i)>>

	       ;
      return let2(vectorfield . tuple,value,nil,t);
   end$


lisp operator super_product;
lisp procedure super_product(x,y);
   mk!*sq subs2 super_product_sq(simp x,simp y)$

lisp procedure super_product_sq(x,y);
   begin scalar splitted_x,splitted_y,product;
      splitted_x:=split_ext(x, '(ext));
      splitted_y:=split_ext(y, '(ext));
      product:=nil ./ 1;
      for each term_x in splitted_x do
	 for each term_y in splitted_y do
	    product:=addsq(product,
	       multsq(multsq(cdr term_x,cdr term_y),
		  ext_mult(car term_x,car term_y)));
      return product;
   end$


lisp procedure natural_wedges_handler on_off;
   begin scalar save_switch;
      if on_off then
      <<newtok '((!^ !^)super_product);
	 precedence('super_product, 'times);
	 put('ext, 'prifn, 'wedge_print)>>
      else
      <<save_switch:=get('!^, 'switch!*);
	 save_switch:=delete(assoc('!^,car save_switch),car save_switch) . cdr save_switch;
	 put('!^, 'switch!*,save_switch);
	 remprop('ext, 'prifn)>>
   end$



lisp procedure wedge_print ext_kernel;
   if length ext_kernel leq 2 then print_alias ext_kernel
   else inprint('super_product,0,kernels_on_list)
      where kernels_on_list=
	 for each arg in cdr ext_kernel collect list('ext,arg)$

endmodule;

end;


