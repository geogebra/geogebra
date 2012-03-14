module tools21;


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

%write "Algebraic operator tools for REDUCE, Version 2.1 (August 14, 1996)"$terpri()$


algebraic$

lisp procedure get_first_kernel(form,oplist);
   gfk(form,oplist,nil)$

lisp procedure gfk(form,oplist,l);
   if l or domainp form then l
   else gfk(red form,
      oplist,
      gfk(lc form,
	 oplist,
	 if not atom x and member(car x,oplist) then x else l))
            where x=mvar form$


lisp operator get_kernel;

lisp procedure get_kernel(exprss,oplist);
   gfk(numr simp!* exprss,
      if null oplist then nil
      else if atom oplist then list oplist
      else if car oplist= 'list then cdr oplist
      else oplist,
      nil)$



lisp procedure get_all_kernels(form,oplist);
   gak(form,oplist,nil)$

lisp procedure gak(form,oplist,l);
   if domainp form then l
   else gak(red form,
      oplist,
      gak(lc form,
	 oplist,
	 if not atom x and
	 member(car x,oplist) and
	 not member(x,l) then l:=aconc(l,x)
	 else l))
            where x=mvar form$


lisp operator get_kernels;

lisp procedure get_kernels(exprss,oplist);
   'list . gak(numr simp!* exprss,
      if null oplist
      then nil
      else if atom oplist
      then list oplist
      else if car oplist= 'list
      then cdr oplist
      else oplist,nil)$


lisp procedure get_recursive_kernels(form,oplist);
   grk(form,oplist,nil)$

lisp procedure grk(form,oplist,l);
   if domainp form
   then l else grk(red form,oplist,
      grk(lc form,oplist,

	 if not atom x
	 then begin scalar y;
	    for each arg in cdr x
	       do <<y:=simp arg;
		  l:=grk(numr y,oplist,grk(denr y,oplist,l))>> ;
	    return if member(car x,oplist)and not member(x,l)
	    then x . l else l end
	 else l

	    ))
	       where x=mvar form$


lisp operator get_deep_kernels;

lisp procedure get_deep_kernels(exprss,oplist);
   'list . (grk(numr val,
      if null oplist
      then nil
      else if atom oplist
      then list oplist
      else if car oplist= 'list
      then cdr oplist
      else oplist,
      grk(denr val,
	 if null oplist
	 then nil
	 else if atom oplist
	 then list oplist
	 else if car oplist= 'list
	 then cdr oplist
	 else oplist,nil)))
	    where val=simp!* exprss$


lisp procedure tlo(form,l);
   if domainp form then l
   else tlo(red form,
      tlo(lc form,if not atom x and not member(car x,l)
      then car(x) . l else l))
	 where x=mvar form$



put( 'top_level_operators, 'psopfn, 'top_level_operators)$

lisp procedure top_level_operators u;
   'list . union(tlo(numr sq_form,nil),tlo(denr sq_form,nil))
      where sq_form=simp!* car u$


lisp operator write_defs;
lisp procedure write_defs(opr);
   begin for each el in get(opr, 'kvalue)do
      assgnpri(value,list car el, 'only)
	 where value=aeval cadr el
   end$


lisp operator reassign_defs;
lisp procedure reassign_defs(opr);
   begin scalar kvalue_list;
      kvalue_list:=for each el in get(opr, 'kvalue)collect car el;
      for each kernel in kvalue_list do
	 setk(kernel,aeval kernel);
   end$


lisp operator used;
lisp procedure used opkern;
   length get(opkern, 'klist)$

lisp operator known;
lisp procedure known opkern;
   length get(opkern, 'kvalue)$


put( 'clear_op, 'stat, 'rlis)$

lisp procedure clear_op kernel_list;
   for each kernel in kernel_list do
      if atom kernel then
      <<for each property in prop(kernel)do
	 if not atom property then remprop(kernel,car property);
	 remflag(list kernel, 'full);
	 rmsubs()>>
      else begin scalar op_name,key,entry;
	 op_name:=car kernel;
	 key:=for each i in cdr kernel collect reval i;
	 entry:=na_get(get(op_name, 'na_values),key);
	 if null entry or null cdr entry then
	    msgpri("CLEAR_OP:",kernel,"not found",nil,nil)
	 else rplacd(entry,nil);
      end$



lisp put( 'operator_representation, 'psopfn, 'opr_representation)$
lisp procedure opr_representation l;
   operator_representation(reval car l,reval cadr l,
      if length l>2 then reval caddr l else list( 'list))$

lisp procedure operator_representation(int_opr,even_list,odd_list);
   begin scalar n_even,n_odd;
      if get(int_opr, 'alias_vector)then
	 rederr("OPERATOR_REPRESENTATION: first call CLEAR_OPERATOR_REPRESENTATION");

      even_list:=cdr even_list;
      odd_list:=cdr odd_list;
      if not get(int_opr, 'simpfn)then put(int_opr, 'simpfn, 'simpiden);
      n_even:=0;
      for each el in even_list do
      <<if not atom el then

	 msgpri("OPERATOR_REPRESENTATION:",el,"not an atom",nil,t);
	 setk(el,list(int_opr,n_even:=n_even+1));
	 put(el, 'newnam,list(int_opr,n_even))>> ;
      n_odd:=0;
      for each el in odd_list do
      <<if not atom el then

	 msgpri("OPERATOR_REPRESENTATION:",el,"not an atom",nil,t);
	 setk(el,list(int_opr,n_odd:=n_odd-1));
	 put(el, 'newnam,list(int_opr,n_odd))>> ;
      n_odd:=-n_odd;

      if not get(int_opr, 'prifn)then put(int_opr, 'prifn, 'print_alias);
      put(int_opr, 'alias_vector,n_odd . n_even .
	 list2vector append(reverse odd_list,nil . even_list));
   end$


lisp procedure print_alias l;
   begin scalar i,n_odd,n_even,alias_vector;
      alias_vector:=get(car l, 'alias_vector);
      if alias_vector then <<n_odd:=car alias_vector;
	 n_even:=cadr alias_vector;
	 alias_vector:=cddr alias_vector>> ;
      if null alias_vector or length l>2 or not fixp(i:=cadr l)or
	 i<-n_odd or i>n_even then

 	 <<prin2!* car l;
	    prin2!*"(";
	    obrkp!*:=nil;
	    if cdr l then inprint( '!*comma!*,0,cdr l);
	    obrkp!*:=t;
	    prin2!*")">>


      else maprin getv(alias_vector,i+n_odd)
   end$


lisp operator construct_alias_print;
lisp procedure construct_alias_print(int_opr,even_list,odd_list);
   begin scalar n_even,n_odd;
      even_list:=cdr even_list;odd_list:=cdr odd_list;
      n_even:=length even_list;n_odd:=length odd_list;

      if not get(int_opr, 'prifn)then put(int_opr, 'prifn, 'print_alias);
      put(int_opr, 'alias_vector,n_odd . n_even .
	 list2vector append(reverse odd_list,nil . even_list));
   end$


lisp put( 'add_to_operator_representation, 'psopfn, 'add_to_opr_representation)$

lisp procedure add_to_opr_representation l;
   add_to_operator_representation(reval car l,reval cadr l,
      if length l>2 then reval caddr l else list( 'list))$

lisp procedure add_to_operator_representation(int_opr,even_list,odd_list);
   begin scalar n_even,n_odd,old_list,alias_vector;
      if not get(int_opr, 'alias_vector)
      then rederr("ADD_TO_OPERATOR_REPRESENTATION: first call OPERATOR_REPRESENTATION");
      alias_vector:=get(int_opr, 'alias_vector);
      n_even:=cadr alias_vector;
      n_odd:=-car alias_vector;
      alias_vector:=cddr alias_vector;
      old_list:=for i:=0:upbv alias_vector
	 collect getv(alias_vector,i);

      even_list:=cdr even_list;
      odd_list:=cdr odd_list;
      for each el in even_list
	 do <<if not atom el
	 then
	    msgpri("ADD_TO_OPERATOR_REPRESENTATION:",el,"not an atom",nil,t);
	 setk(el,list(int_opr,n_even:=n_even+1));
	 put(el, 'newnam,list(int_opr,n_even))
	 >> ;
      for each el in odd_list
	 do <<if not atom el
	 then
	    msgpri("ADD_TO_OPERATOR_REPRESENTATION:",el,"not an atom",nil,t);
	 setk(el,list(int_opr,n_odd:=n_odd-1));
	 put(el, 'newnam,list(int_opr,n_odd))>> ;
      n_odd:=-n_odd;

      put(int_opr, 'alias_vector,n_odd . n_even .
	 list2vector append(reverse odd_list,append(old_list,even_list)));
   end$


lisp operator clear_operator_representation;
lisp procedure clear_operator_representation int_opr;
   begin scalar alias_vector,n_odd,n_even,kernel;
      if(alias_vector:=get(int_opr, 'alias_vector))then
      <<n_odd:=car alias_vector;n_even:=cadr alias_vector;
	 alias_vector:=cddr alias_vector;
	 for i:=-n_odd:n_even do if i neq 0 then

 	 <<kernel:=getv(alias_vector,n_odd+i);
	    remprop(kernel, 'newnam);
	    remprop(kernel, 'avalue)>> ;
	 clear_alias_print int_opr>>
   end$

lisp operator clear_alias_print;
lisp procedure clear_alias_print int_opr;
   begin
      remprop(int_opr, 'prifn);
      remprop(int_opr, 'alias_vector)
   end$


lisp procedure split_f(form,oplist,fact,kc_list);
   if null form then kc_list
   else if domainp form then
      addf(multf(fact,form),
	 car kc_list) . cdr kc_list
   else if not atom mvar form and member(car mvar form,oplist)then
      if ldeg form neq 1 or get_first_kernel(lc form,oplist)then

	 msgpri("SPLIT_F: expression not linear w.r.t.",
 	    'list . oplist,nil,nil,t)
      else split_f(red form,oplist,fact,
	 update_kc_list(kc_list,mvar form,multf(fact,lc form)))
   else split_f(red form,oplist,fact,
      split_f(lc form,oplist,
	 multf(fact,!*p2f lpow form),kc_list))$


lisp procedure split_form(form,oplist);
   split_f(form,oplist,1,nil . nil)$

lisp procedure list_assoc(car_exprn,a_list);
   if null a_list then a_list else if caar a_list=car_exprn then a_list
   else list_assoc(car_exprn,cdr a_list)$

lisp procedure update_kc_list(kc_list,kernel,coefficient);
   (if rest_list then <<rplaca(rest_list,caar rest_list . addf(cdar
      rest_list,coefficient));kc_list>> else
	 car kc_list . (kernel . coefficient) . cdr kc_list)
	    where rest_list=list_assoc(kernel,cdr kc_list)$


put( 'operator_coeff, 'psopfn, 'operator_coeff_1)$

lisp procedure operator_coeff_1 u;
   if length u neq 2 then rederr("OPERATOR_COEFF: wrong number of arguments")
   else operator_coeff(car u,reval cadr u)$


lisp procedure operator_coeff(exprn,oplist);
   begin scalar numr_ex,denr_ex,kc_list;
      oplist:=
	 if null oplist
	 then nil
	 else if atom oplist
	 then list oplist
	 else if car oplist= 'list
	 then cdr oplist
	 else oplist;
      exprn:=simp!* exprn;numr_ex:=numr exprn;denr_ex:=denr exprn;
      if gfk(denr_ex,oplist,nil)
      then rederr("OPERATOR_COEFF: denominator not independent of operator(s)");
      kc_list:=split_form(numr_ex,oplist);
      return 'list . !*ff2a(car kc_list,denr_ex) .
	 for each kc_pair in cdr kc_list collect
	    list( 'list,car kc_pair,!*ff2a(cdr kc_pair,denr_ex));
   end$


lisp procedure dump_operators(form,oplist,fact);
   if null form then nil
   else if domainp form then multf(fact,form)
   else if not atom mvar form and member(car mvar form,oplist)then
      dump_operators(red form,oplist,fact)
   else
      addf(dump_operators(red form,oplist,fact),
	 dump_operators(lc form,oplist,multf(fact,!*p2f lpow form)))$


put( 'independent_part, 'psopfn, 'independent_part_1)$

lisp procedure independent_part_1 u;
   if length u neq 2 then rederr("INDEPENDENT_PART: wrong number of arguments")
   else independent_part(car u,reval cadr u)$

lisp procedure independent_part(exprn,oplist);
   begin scalar numr_ex,denr_ex;
      oplist:=
	 if null oplist
	 then nil
	 else if atom oplist
	 then list oplist
	 else if car oplist= 'list
	 then cdr oplist
	 else oplist;
      exprn:=simp!* exprn;numr_ex:=numr exprn;denr_ex:=denr exprn;
      if gfk(denr_ex,oplist,nil)
      then rederr("INDEPENDENT_PART: denominator not independent");
      return !*ff2a(dump_operators(numr_ex,oplist,1),denr_ex);
   end$


lisp procedure split_non_linear_f(form,var_list,multi_power,fact,pc_list);
   if null form
   then pc_list
   else if domainp form
   then if multi_power
   then update_kc_list(pc_list,multi_power,multf(fact,form))
   else addf(multf(fact,form),car pc_list) . cdr pc_list
   else split_non_linear_f(red form,var_list,multi_power,fact,
      if(not atom mvar form and member(car mvar form,var_list))
	 or member(mvar form,var_list)
      then split_non_linear_f(lc form,var_list,
	 append(multi_power,list lpow form),fact,pc_list)
      else split_non_linear_f(lc form,var_list,multi_power,
	 multf(fact,!*p2f lpow form),pc_list))$


lisp procedure split_non_linear_form(form,kernel_list);
   split_non_linear_f(form,kernel_list,nil,1,nil . nil)$


put( 'multi_coeff, 'psopfn, 'multi_coeff_1)$

lisp procedure multi_coeff_1 u;
   if length u neq 2 then rederr("MULTI_COEFF: wrong number of arguments")
   else multi_coeff(car u,reval cadr u)$

lisp procedure multi_coeff(exprn,kernel_list);
   begin scalar numr_ex,denr_ex,pc_list;
      kernel_list:=
	 if null kernel_list
	 then nil
	 else if atom kernel_list
	 then list kernel_list
	 else if car kernel_list= 'list
	 then cdr kernel_list
	 else kernel_list;
      exprn:=simp!* exprn;
      numr_ex:=numr exprn;denr_ex:=denr exprn;
      for each generator in kernel_list do if depends(denr_ex,generator)
      then
	 msgpri("MULTI_COEFF: expression is not polynomial w.r.t. ",
 	    'list . kernel_list,nil,nil,t);
      pc_list:=split_non_linear_form(numr_ex,kernel_list);
      return 'list . !*ff2a(car pc_list,denr_ex) .
	 for each pc_pair in cdr pc_list collect
	    list( 'list,convert_multi_power car pc_pair,!*ff2a(cdr pc_pair,denr_ex));
   end$


lisp procedure convert_multi_power multi_power;
   'times . for each power in multi_power collect
      if cdr power=1 then car power else list( 'expt,car power,cdr power)$


lisp procedure split_arguments(arg_list,oplist,splitted_list);
   if null arg_list then splitted_list
   else split_arguments(cdr arg_list,oplist,
      multf(denr first_arg,car splitted_list) .
	 split_form(numr first_arg,oplist) .
	    cdr splitted_list)where first_arg=simp!* car arg_list$

lisp procedure split_non_linear_arguments(arg_list,oplist,splitted_list);
   if null arg_list then splitted_list
   else split_non_linear_arguments(cdr arg_list,oplist,
      multf(denr first_arg,car splitted_list) .
	 split_non_linear_form(numr first_arg,oplist) .
	    cdr splitted_list)where first_arg=simp!* car arg_list$



lisp procedure split_operator u;
   split_arguments(cdr u,get(car u, 'oplist),1 . nil)$

lisp procedure split_non_linear_operator u;
   split_non_linear_arguments(cdr u,get(car u, 'oplist),1 . nil)$



lisp procedure process_arg_stack(arg_stack,op_name,arg_list,fact);
   if null arg_stack then multsq(!*f2q fact,
      apply1(get(op_name, 'resimp_fn),op_name . arg_list))
   else process_comp_list(car arg_stack,cdr arg_stack,op_name,arg_list,fact)$

lisp procedure process_non_linear_arg_stack(arg_stack,op_name,arg_list,fact);
   if null arg_stack
   then multsq(!*f2q fact,apply1(get(op_name, 'resimp_fn),
      op_name . for each power_set in arg_list
	 collect if power_set=1
	 then power_set
	 else convert_multi_power power_set))
   else process_non_linear_comp_list(car arg_stack,cdr arg_stack,op_name,arg_list,fact)$


lisp procedure process_comp_list(comp_list,arg_stack,op_name,arg_list,fact);
   addsq(process_independent_part(car comp_list,arg_stack,op_name,arg_list,fact),
      process_components(cdr comp_list,arg_stack,op_name,arg_list,fact))$

lisp procedure process_non_linear_comp_list(comp_list,arg_stack,op_name,arg_list,fact);
   addsq(process_non_linear_independent_part(car comp_list,arg_stack,op_name,arg_list,fact),
      process_non_linear_components(cdr comp_list,arg_stack,op_name,arg_list,fact))$

lisp procedure process_independent_part(independent_part,arg_stack,
      op_name,arg_list,fact);
   if null independent_part then nil . 1
   else
      process_arg_stack(arg_stack,op_name,1 . arg_list,multf(fact,independent_part))$

lisp procedure process_non_linear_independent_part(independent_part,arg_stack,
      op_name,arg_list,fact);
   if null independent_part then nil . 1
   else
      process_non_linear_arg_stack(arg_stack,op_name,1 . arg_list,multf(fact,independent_part))$



lisp procedure process_components(comp_list,arg_stack,op_name,arg_list,fact);
   if null comp_list then nil . 1
   else
      addsq(process_components(cdr comp_list,arg_stack,op_name,arg_list,fact),
	 process_arg_stack(arg_stack,op_name,caar comp_list . arg_list,
	    multf(fact,cdar comp_list)))$

lisp procedure process_non_linear_components(comp_list,arg_stack,op_name,arg_list,fact);
   if null comp_list then nil . 1
   else
      addsq(process_non_linear_components(cdr comp_list,arg_stack,op_name,arg_list,fact),
	 process_non_linear_arg_stack(arg_stack,op_name,caar comp_list . arg_list,
	    multf(fact,cdar comp_list)))$



lisp procedure build_sum(op_name,arg_stack);
   process_arg_stack(arg_stack,op_name,nil,1)$

lisp procedure build_non_linear_sum(op_name,arg_stack);
   process_non_linear_arg_stack(arg_stack,op_name,nil,1)$


lisp procedure simp_multilinear u;
   quotsq(build_sum(car u,cdr splitted_list),!*f2q car splitted_list)
      where splitted_list=split_operator u$

lisp procedure simp_multimorph u;
   quotsq(build_non_linear_sum(car u,cdr splitted_list),!*f2q car splitted_list)
      where splitted_list=split_non_linear_operator u$



put( 'multilinear, 'stat, 'rlis)$

put( 'multimorph, 'stat, 'rlis)$

lisp procedure multilinear u;
   for each decl in u do
   begin scalar op_name,resimp_fn;
      if length decl neq 2 and length decl neq 3 then

	 msgpri(nil,decl,"invalid multilinear declaration",nil,t);
      if not idp(op_name:=car decl)then

	 msgpri(nil,op_name,"invalid as operator",nil,t);
      put(op_name, 'oplist,
	 if null cadr decl
	 then nil
	 else if atom cadr decl
	 then list cadr decl
	 else if car cadr decl= 'list
	 then cdr cadr decl
	 else cadr decl);
      if(length decl=3 and(resimp_fn:=caddr decl))or
	 (resimp_fn:=get(op_name, 'resimp_fn))or
	 (resimp_fn:=get(op_name, 'simpfn))then put(op_name, 'resimp_fn,resimp_fn)
      else put(op_name, 'resimp_fn, 'simpiden);
      put(op_name, 'simpfn, 'simp_multilinear);
      flag(list(op_name), 'full);
   end$


lisp procedure multimorph u;
   for each decl in u do
   begin scalar op_name,resimp_fn;
      if length decl neq 2 and length decl neq 3 then

	 msgpri(nil,decl,"invalid multimorph declaration",nil,t);
      if not idp(op_name:=car decl)then

	 msgpri(nil,op_name,"invalid as operator",nil,t);
      put(op_name, 'oplist,
	 if null cadr decl
	 then nil
	 else if atom cadr decl
	 then list cadr decl
	 else if car cadr decl= 'list
	 then cdr cadr decl
	 else cadr decl);
      if(length decl=3 and(resimp_fn:=caddr decl))or
	 (resimp_fn:=get(op_name, 'resimp_fn))or
	 (resimp_fn:=get(op_name, 'simpfn))then put(op_name, 'resimp_fn,resimp_fn)
      else put(op_name, 'resimp_fn, 'simpiden);
      put(op_name, 'simpfn, 'simp_multimorph);
      flag(list(op_name), 'full);
   end$


put( 'linear_solve, 'psopfn, 'linear_solve_1)$

lisp procedure linear_solve_1 u;
   if length u neq 2 then
      rederr("LINEAR_SOLVE: wrong number of arguments")
   else linear_solve(car u,!*a2k cadr u)$


lisp procedure linear_solve(exprn,kernel);
   begin scalar kord!*,form;

      exprn:=fctrf numr simp!* exprn;
      exprn:=if domainp car exprn then cdr exprn else(car exprn . 1) . cdr exprn;
      form:=for each factor in exprn join
	 if depends(factor,kernel)then list factor;
      if length form=1 then form:=numr car form else

	 msgpri("LINEAR_SOLVE: expression not linear with respect to",
	    kernel,nil,nil,t);
      setkorder list kernel;
      form:=reorder form;
      if(mvar form=kernel)and(ldeg form=1)and
	 not depends(lc form,kernel)and not depends(red form,kernel)then
	    return !*ff2a(negf red form,lc form)
      else
	 msgpri("LINEAR_SOLVE: expression not linear with respect to",
	    kernel,nil,nil,t);
   end$


put( 'linear_solve_and_assign, 'psopfn, 'linear_solve_and_assign_1)$

lisp procedure linear_solve_and_assign_1 u;
   if length u neq 2 then
      rederr("LINEAR_SOLVE_AND_ASSIGN: wrong number of arguments")
   else linear_solve_and_assign(car u,cadr u)$

lisp procedure linear_solve_and_assign(exprn,kernel);
   setk(krnl,linear_solve(exprn,krnl))
      where krnl=!*a2k kernel$



put( 'solvable_kernels, 'psopfn, 'solvable_kernels_1)$

lisp procedure solvable_kernels_1 u;
   if length u neq 3 then
      rederr("SOLVABLE_KERNELS: wrong number of arguments")
   else solvable_kernels(car u,cadr u,caddr u)$


lisp procedure list_merge(element,merge_list);
   if member(element,merge_list)then merge_list else element .
      merge_list$

lisp procedure mk_kernel_list(form,k_oplist,c_oplist,forbidden,kernel_list);
   if domainp form then kernel_list
   else(
      if not atom kernel then
	 mk_kernel_list(red form,k_oplist,c_oplist,forbidden,
	    mk_kernel_list(lc form,k_oplist,c_oplist,
	       if member(car kernel,c_oplist)then t else forbidden,
	       if member(car kernel,k_oplist)then
		  if not forbidden and ldeg form=1 and
	       not get_first_kernel(lc form,c_oplist)then
		  list_merge(kernel,car kernel_list) . cdr kernel_list
		  else
		     car kernel_list . list_merge(kernel,cdr kernel_list)
	       else kernel_list))
      else mk_kernel_list(red form,k_oplist,c_oplist,forbidden,
	 mk_kernel_list(lc form,k_oplist,c_oplist,forbidden,kernel_list))
	    )where kernel=mvar form$


lisp procedure solvable_kernels(exprn,k_oplist,c_oplist);
   begin scalar form,kernel_list,forbidden_kernels;
      form:=numr simp!* exprn;
      k_oplist:=
	 if null k_oplist
	 then nil
	 else if atom k_oplist
	 then list k_oplist
	 else if car k_oplist= 'list
	 then cdr k_oplist
	 else k_oplist;
      c_oplist:=
	 if null c_oplist
	 then nil
	 else if atom c_oplist
	 then list c_oplist
	 else if car c_oplist= 'list
	 then cdr c_oplist
	 else c_oplist;
      kernel_list:=mk_kernel_list(form,k_oplist,c_oplist,nil,nil . nil);
      forbidden_kernels:=cdr kernel_list;
      kernel_list:=car kernel_list;
      for each kernel in forbidden_kernels do kernel_list:=delete(kernel,kernel_list);
      return 'list . kernel_list;
   end$



lisp procedure na_get(na_list,key);
   if na_list then
      if null key then car na_list
      else(if na_assoc then na_get(cdr na_assoc,cdr key))
	 where na_assoc=assoc(car key,cdr na_list)$

lisp procedure na_put(na_list,key,value);
   if null key then car rplaca(na_list,value)
   else(if na_assoc then na_put(cdr na_assoc,cdr key,value)
   else na_put(cdadr rplacd(na_list,list(car key,nil) . cdr na_list),
      cdr key,value))
	 where na_assoc=assoc(car key,cdr na_list)$


put( 'na_operator, 'stat, 'rlis)$

lisp procedure na_operator u;
   for each decl in u do
   begin
      if not atom decl then
	 msgpri(nil,decl,"invalid na_operator declaration",nil,t);
      put(decl, 'na_values,list(nil));
      put(decl, 'simpfn, 'simp_na_op);
      put(decl, 'setkfn, 'setk_na_op);
      put(decl, 'mksqsubfn, 'mksqsub_na_op);
      put(decl, 'fkernfn, 'fkern_na_op);
      put(decl, 'clearfn, 'clear_na_op);
      put(decl, 'prepsq!*fn, 'prepsq!*_get_kernels);
      flag(list(decl), 'full);
   end$


fluid '(!_na_krnl_);

lisp procedure simp_na_op kernel;
   begin scalar op_name,key,entry;
      op_name:=car kernel;
      key:=for each i in cdr kernel collect reval i;
      return
	 if(entry:=na_get(get(op_name, 'na_values),key))and cdr entry
	 then simp cdr entry
	 else(mksq(op_name . key,1)where !_na_krnl_=list entry);
   end$


lisp procedure setk_na_op(kernel,value);
   begin scalar op_name,key,entry;
      op_name:=car kernel;key:=cdr kernel;
      return
	 if(entry:=na_get(get(op_name, 'na_values),key))then
	    cdr rplacd(entry,value)
	 else cdr na_put(get(op_name, 'na_values),key,
	    list(op_name . key,nil) . value);
   end$


lisp procedure mksqsub_na_op kernel;
   if !_na_krnl_ then
      (car !_na_krnl_ and cdar !_na_krnl_ and list(kernel,cdar !_na_krnl_))
   else
   begin scalar op_name,key,entry;
      op_name:=car kernel;key:=cdr kernel;
      if(entry:=na_get(get(op_name, 'na_values),key))and cdr entry then
	 return list(kernel,cdr entry);
   end$


lisp procedure fkern_na_op kernel;
   begin scalar op_name,key,entry;
      op_name:=car kernel;key:=cdr kernel;
      return
	 if(entry:=if !_na_krnl_ then car !_na_krnl_
	 else na_get(get(op_name, 'na_values),key))then
	    if car entry then car entry
	    else car rplaca(entry,list(kernel,nil))
	 else car na_put(get(op_name, 'na_values),key,list(kernel,nil) . nil);
   end$


lisp procedure prepsq!*_get_kernels(u,op_name);
   ordn get_all_kernels(numr u,op_name)$



endmodule;

end;

