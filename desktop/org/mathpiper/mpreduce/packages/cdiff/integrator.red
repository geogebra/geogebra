module integrator;


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

% write"Integrator package for REDUCE, $Revision: 1.0 $"$terpri()$

put('initialize_equations, 'psopfn, 'initialize_equations1)$



global '(cur_eq_set!*)$
cur_eq_set!*:= 'equ$


fluid '(!*coefficient_check)$
!*coefficient_check:=t$
flag('(coefficient_check), 'switch)$


fluid '(!*polynomial_check)$
!*polynomial_check:=nil$
flag('(polynomial_check), 'switch)$


fluid '(!*allow_differentiation)$
!*allow_differentiation:=nil$
flag('(allow_differentiation), 'switch)$



fluid '(listpri_depth!*)$
listpri_depth!*:=40$


algebraic$


lisp procedure initialize_equations1 specification_list;
   begin scalar operator_name,total_used,variable_list,
	 specification,even_used,odd_used,
      constant_operator,bracketname,function_name,function_list;
      if length specification_list<5 then
	 rederr("INITIALIZE_EQUATIONS: wrong number of parameters");
      if not idp(operator_name:=car specification_list)then
	 rederr("INITIALIZE_EQUATIONS: equations operator must be identifier");
      if not fixp(total_used:=
	 reval car(specification_list:=cdr specification_list))
	    or total_used<0 then
	       rederr("INITIALIZE_EQUATIONS: total number of equations must be positive");
      put(operator_name, 'total_used,total_used);
      variable_list:=reval car(
	 specification_list:=cdr specification_list);
      if atom variable_list or car variable_list neq 'list then
	 rederr("INITIALIZE_EQUATIONS: variable list must be algebraic list");
      put(operator_name, 'variable_list,cdr variable_list);

      specification_list:=cdr specification_list;
      specification:=car specification_list;

      if atom specification or length specification neq 4
	 or car specification neq 'list
	    or not idp(constant_operator:=cadr specification)or
	 not fixp(even_used:=reval caddr specification)or
	 not fixp(odd_used:=reval cadddr specification)
	    or even_used<0 or odd_used<0 then

	       msgpri("INITIALIZE_EQUATIONS: invalid declaration of",
		  specification,nil,nil,t);
      put(operator_name, 'constant_operator,constant_operator);
      if(bracketname:=get(constant_operator, 'bracketname))then
	 put(operator_name, 'bracketname,bracketname);

      if get(constant_operator, 'bracketname)then
	 define_used(bracketname,list('list,even_used,odd_used))
      else
      begin
	 put(constant_operator, 'even_used,even_used);
	 put(constant_operator, 'odd_used,odd_used);
      end;

      for each function_specification in cdr specification_list do
      begin

	 if atom function_specification or length function_specification neq 4
	    or car function_specification neq 'list
	       or not idp(function_name:=cadr function_specification)or
	    not fixp(even_used:=reval caddr function_specification)or
	    not fixp(odd_used:=reval cadddr function_specification)
	       or even_used<0 or odd_used<0 then

		  msgpri("INITIALIZE_EQUATIONS: invalid declaration of",
		     function_specification,nil,nil,t);

	 if get(function_name, 'bracketname)then
	    define_used(bracketname,list('list,even_used,odd_used))
	 else
	 begin
	    put(function_name, 'even_used,even_used);
	    put(function_name, 'odd_used,odd_used);
	 end;
	 function_list:=function_name . function_list;
      end;
      put(operator_name, 'function_list,function_list);
   end$


lisp operator use_equations;
lisp procedure use_equations operator_name;
   begin
      if idp operator_name then
	 cur_eq_set!*:=operator_name
      else rederr("USE_EQUATIONS: argument must be identifier");
   end$


lisp operator integrate_equation;
lisp procedure integrate_equation n;
   begin scalar listpri_depth!*,total_used,equation,denominator,
	 solvable_kernel,solvable_kernels,df_list,function_list,present_functions_list,
      variable_list,absent_variables,
      linear_functions_list,constants_list,bracketname,df_terms,df_functions,
      linear_functions,functions_and_constants_list,commutator_functions,
      present_variables,nr_of_variables,integration_variables;
      listpri_depth!*:=200;
      terpri!* t;

      if null(total_used:=get(cur_eq_set!*, 'total_used))or
	 n>total_used then

	    msgpri("INTEGRATE_EQUATIONS: properly initialize",
	       cur_eq_set!*,nil,nil,t);
      if null(equation:=cadr assoc(list(cur_eq_set!*,n),
	 get(cur_eq_set!*, 'kvalue)))then

	    msgpri("INTEGRATE_EQUATION:",list(cur_eq_set!*,n),
	       "is non-existent",nil,t);
      denominator:=denr(equation:=simp!* equation);
      equation:=numr equation;
      if null equation then
      <<write cur_eq_set!*,"(",n,") = 0";terpri!* t;

	 setk(list(cur_eq_set!*,n),0);goto solved>> ;

      df_list:=split_form(equation, '(df));
      if try_a_homogeneous_integration(n,denominator,df_list)then goto solved;


      function_list:=get(cur_eq_set!*, 'function_list);
      present_functions_list:=get_recursive_kernels(equation,function_list);
      variable_list:=get(cur_eq_set!*, 'variable_list);
      absent_variables:=variable_list;
      for each function in present_functions_list do
	 for each variable in
	    ((if depl_entry then cdr depl_entry)
	       where depl_entry=assoc(function,depl!*))do
		  absent_variables:=delete(variable,absent_variables);
      if split_equation_polynomially(n,total_used,equation,absent_variables)then
	 goto solved;

      linear_functions_list:=split_form(car df_list,
	 function_list);
      df_list:=cdr df_list;
      constants_list:=split_form(car linear_functions_list,
	 list get(cur_eq_set!*, 'constant_operator));
      linear_functions_list:=cdr linear_functions_list;
      if(bracketname:=get(cur_eq_set!*, 'bracketname))then

	 if length(df_list)=0 and
	 length(linear_functions_list)=0 then
 	 <<
	    if atom(solvable_kernel:=
	       relation_analysis(!*ff2a(equation,denominator),bracketname))
	    then <<write cur_eq_set!*,"(",n,") is a non-solvable Lie relation";
	       terpri!* t>>
	    else <<write cur_eq_set!*,"(",n,") solved for ";maprin solvable_kernel;
	       terpri!* t;
	       setk(list(cur_eq_set!*,n),0)>> ;
	    goto solved
	 >> ;


      df_terms:=for each df_term in df_list join
	 if member(car cadr car df_term,function_list)
	 then list car df_term;
      for each df_term in df_terms do if not member(cadr
	 df_term,df_functions)then df_functions:=cadr(df_term) . df_functions;
      functions_and_constants_list:=append(linear_functions_list,
	 cdr constants_list);
      linear_functions:=for each linear_function in
	 functions_and_constants_list collect car linear_function;
      if bracketname then commutator_functions:=
	 get_recursive_kernels(car constants_list,
	    get(cur_eq_set!*, 'function_list));;

	    present_variables:=variable_list;
	    for each variable in absent_variables do
	       present_variables:=delete(variable,present_variables);
	    nr_of_variables:=length present_variables;
	    for each kernel in linear_functions do if length

	       ((if depl_entry then cdr depl_entry)
		  where depl_entry=assoc(kernel,depl!*))=nr_of_variables then
		     solvable_kernels:=kernel . solvable_kernels;
	    for each kernel in append(df_functions,commutator_functions)do
	       solvable_kernels:=delete(kernel,solvable_kernels);
	    if solvable_kernels then

 	    <<solvable_kernel:=
	       find_solvable_kernel(solvable_kernels,
		  functions_and_constants_list,denominator);
	       if solvable_kernel then
 	       <<linear_solve_and_assign(!*ff2a(equation,1),solvable_kernel);
		  depl!*:=
		     delete(assoc(solvable_kernel,depl!*),depl!*);

		  successful_message_for(n,"Solved for ",solvable_kernel);
		  goto solved
	       >>
	       else <<not_a_number_message_for(n,"Solving a function",
		     partial_list(solvable_kernels,3));
	       goto solved>>
	    >> ;


	    integration_variables:=present_variables;
	    for each kernel in append(linear_functions,commutator_functions)do
	       for each variable in
		  ((if depl_entry then cdr depl_entry)
		     where depl_entry=assoc(kernel,depl!*))do
			integration_variables:=delete(variable,integration_variables);
	    for each df_function in df_functions do
	       if not (length
		  ((if depl_entry then cdr depl_entry)
		     where depl_entry=assoc(df_function,depl!*))=nr_of_variables) then
			for each variable in
			   ((if depl_entry then cdr depl_entry)
			      where depl_entry=assoc(df_function,depl!*))do
				 integration_variables:=delete(variable,integration_variables);
	    if try_an_inhomogeneous_integration(n,equation,denominator,
	       df_list,df_terms,integration_variables,nr_of_variables)then goto solved;

	    if try_a_differentiation(n,total_used,equation,present_variables,
	       df_terms,linear_functions,commutator_functions)
	    then goto solved;

	    write cur_eq_set!*,"(",n,") not solved";terpri!* t;
   solved:
   end$


lisp procedure successful_message_for(n,action,kernel);
   <<write cur_eq_set!*,"(",n,"): ",action;
      maprin kernel;terpri!*(not !*nat);

      setk(list(cur_eq_set!*,n),0);t>> $


lisp procedure not_a_number_message_for(n,action,kernel);
   <<write"*** ",cur_eq_set!*,"(",n,"): ",action,
	 " failed:";terpri!* t;
	 write"    coefficient not a number for ";
	 maprin kernel;terpri!*(not !*nat);
	 write"    Solvable with 'off coefficient_check'";
	 terpri!* t;t>> $


lisp procedure try_a_homogeneous_integration(n,denominator,df_list);
   begin scalar solvable_kernel,solvable_kernels,df_kernel;
      return
	 if null car df_list and
	 (cdr df_list)and length(cdr df_list)=1
	 then
	    if(solvable_kernel:=find_solvable_kernel(
	       solvable_kernels:=list(car car cdr df_list),
	       cdr df_list,denominator))then
 	       <<df_kernel:=cadr solvable_kernel;
		  setk(df_kernel,homogeneous_integration_of(solvable_kernel));
		  depl!*:=
		     delete(assoc(df_kernel,depl!*),depl!*);

		  successful_message_for(n,"Homogeneous integration of ",solvable_kernel)>>
	    else not_a_number_message_for(n,"Homogeneous integration",
	       car solvable_kernels)
   end$


lisp procedure find_solvable_kernel(kernel_list,kc_list,denominator);
   if !*coefficient_check then
      first_solvable_kernel(kernel_list,kc_list,denominator)
   else car kernel_list$


lisp procedure first_solvable_kernel(kernel_list,kc_list,denominator);
   if kernel_list then
      (if domainp cdr kc_pair or
	 numberp !*ff2a(cdr kc_pair,denominator)
      then car kc_pair
      else first_solvable_kernel(cdr kernel_list,kc_list,denominator))
	 where kc_pair=assoc(car kernel_list,kc_list)$


lisp procedure homogeneous_integration_of df_term;
   begin scalar df_function,function_number,dependency_list,integration_list,
	 coefficient_name,bracketname,even_used,odd_used,
      integration_variable,
      number_of_integrations,solution,new_dependency_list;

      df_function:=cadr df_term;
      if not member(car df_function,get(cur_eq_set!*, 'function_list))
	 or not fixp(function_number:=cadr df_function)
	    or function_number=0 then

	       msgpri("PERFORM_HOMOGENEOUS_INTEGRATION: integration of",
		  df_function,"not allowed",nil,t);
      dependency_list:=
	 ((if depl_entry then cdr depl_entry)
	    where depl_entry=assoc(df_function,depl!*));
      if length dependency_list=1 then
	 coefficient_name:=get(cur_eq_set!*, 'constant_operator)
      else coefficient_name:=car df_function;

      if(bracketname:=get(coefficient_name, 'bracketname))then
      begin even_used:=get(bracketname, 'even_used);
	 odd_used:=get(bracketname, 'odd_used);
      end
      else
      begin
	 even_used:=get(coefficient_name, 'even_used);
	 odd_used:=get(coefficient_name, 'odd_used);
      end;
      integration_list:=cdr cdr df_term;

      if integration_list then integration_variable:=car
	 integration_list else integration_variable:=nil;
      if integration_variable and(integration_list:=cdr integration_list)
	 and fixp car integration_list then
 	 <<number_of_integrations:=car integration_list;
	    integration_list:=cdr integration_list>>
      else number_of_integrations:=1;
      if bracketname then

	 if function_number>0 then
	    (if even_used+number_of_integrations>get(bracketname, 'even_dimension)then
	       change_dimensions_of(bracketname,even_used+number_of_integrations,
		  get(bracketname, 'odd_dimension)))
	 else
	    (if odd_used+number_of_integrations>get(bracketname, 'odd_dimension)then
	       change_dimensions_of(bracketname,get(bracketname, 'even_dimension),
		  odd_used+number_of_integrations));

      solution:=nil ./ 1;
      while integration_variable do
      begin new_dependency_list:=delete(integration_variable,dependency_list);
	 for i:=0:number_of_integrations-1 do
 	 <<solution:=addsq(solution,multsq(
	    if i=0 then 1 ./ 1 else mksq(integration_variable,i),
	    mksq(
	       list(coefficient_name,if function_number>0 then
		  (even_used:=even_used+1)else-(odd_used:=odd_used+1)),1)));
	    if new_dependency_list then
	       depl!*:=(list(coefficient_name,if function_number>0 then even_used
	       else-odd_used) . new_dependency_list) . depl!*;
	 >> ;

	 if integration_list then integration_variable:=car
	    integration_list else integration_variable:=nil;
	 if integration_variable and(integration_list:=cdr integration_list)
	    and fixp car integration_list then
 	    <<number_of_integrations:=car integration_list;
	       integration_list:=cdr integration_list>>
	 else number_of_integrations:=1


      end;
      solution:=mk!*sq subs2 solution;

      if get(coefficient_name, 'bracketname)then
	 define_used(bracketname,list('list,even_used,odd_used))
      else
      begin
	 put(coefficient_name, 'even_used,even_used);
	 put(coefficient_name, 'odd_used,odd_used);
      end;
      return solution
   end$


lisp procedure split_equation_polynomially(n,total_used,equation,
      absent_variables);
      begin scalar polynomial_variables,equations_list;

	 polynomial_variables:=absent_variables;
	 if !*polynomial_check then
	    polynomial_variables:=for each variable in polynomial_variables join
	       if polynomialp(equation,variable)then list(variable);

	 equations_list:=split_non_linear_form(equation,polynomial_variables);
	 if length equations_list>1 then
 	 <<for each pc_pair in cdr equations_list do
	    setk(list(cur_eq_set!*,(total_used:=total_used+1)),
	       mk!*sq((cdr pc_pair) ./ 1));
	    if car equations_list then
	       setk(list(cur_eq_set!*,(total_used:=total_used+1)),
		  mk!*sq((car equations_list) ./ 1));
	    write cur_eq_set!*,"(",n,") breaks into ",
	       cur_eq_set!*,"(",get(cur_eq_set!*, 'total_used)+1,
	       "),...,",cur_eq_set!*,"(",total_used,") by ";
	    maprin partial_list(polynomial_variables,5);
	    terpri!*(not !*nat);

	    setk(list(cur_eq_set!*,n),0);
	    put(cur_eq_set!*, 'total_used,total_used)
	 >> ;
	 if length equations_list>1 then return t


      end$


lisp procedure polynomialp(expression,kernel);
   if domainp expression then t
   else((main_variable=kernel or not depends(main_variable,kernel))and
      polynomialp(lc expression,kernel)and polynomialp(red expression,kernel))
	 where main_variable=mvar expression$


lisp procedure partial_list(printed_list,nr_of_items);
   'list . broken_list(printed_list,nr_of_items)$

lisp procedure broken_list(list,n);
   if list then if n=0 then '(!.!.!.)
   else car list . broken_list(cdr list,n-1)$


lisp procedure check_differentiation_sequence(sequence,variable_list);
   if null sequence then t
   else if fixp car sequence or
      member(car sequence,variable_list)then
	 check_differentiation_sequence(cdr sequence,variable_list)$


lisp procedure try_an_inhomogeneous_integration(n,equation,denominator,
      df_list,df_terms,integration_variables,nr_of_variables);
      begin scalar solvable_kernel,solvable_kernels,forbidden_functions,
	    df_kernel,inhomogeneous_term;

	 for each df_term in df_terms do
 	 <<if length
	    ((if depl_entry then cdr depl_entry)
	       where depl_entry=assoc(cadr df_term,depl!*))=nr_of_variables
		  and(check_differentiation_sequence(cdr cdr df_term,
		     integration_variables)
			or member(cadr df_term,forbidden_functions))
	 then solvable_kernels:=if member(cadr df_term,forbidden_functions)
	 then list(nil,nil)else df_term . solvable_kernels;
	    forbidden_functions:=(cadr df_term) . forbidden_functions>> ;;

	    return
	       if solvable_kernels then
		  if length(solvable_kernels)=1 then
		     if(solvable_kernel:=find_solvable_kernel(solvable_kernels,df_list,denominator))
		     then
			if(inhomogeneous_term:=linear_solve(mk!*sq(equation ./ 1),solvable_kernel))
			   and(not !*polynomial_check or
			      check_polynomial_integration(solvable_kernel,inhomogeneous_term))
			then
 			<<df_kernel:=cadr solvable_kernel;
			   setk(df_kernel,
			      inhomogeneous_integration_of(solvable_kernel,inhomogeneous_term));
			   depl!*:=
			      delete(assoc(df_kernel,depl!*),depl!*);

			   successful_message_for(n,"Inhomogeneous integration of ",solvable_kernel)>>
			else
 			<<write cur_eq_set!*,"(",n,"): Inhomogeneous integration failed: ";
			   terpri!* t;
			   write"inhomogeneous term not polynomial in integration variables";
			   terpri!* t;t>>
		     else not_a_number_message_for(n,"Inhomogeneous integration",
			car solvable_kernels)
		  else <<write cur_eq_set!*,"(",n,"): Inhomogeneous integration failed: ";
		     terpri!* t;
		     write"more terms with maximal dependency";terpri!* t;t>>


      end$

lisp procedure check_polynomial_integration(df_term,integration_term);
   begin scalar numerator,denominator,integration_variables,variable,ok;
      numerator:=numr simp integration_term;
      denominator:=denr simp integration_term;
      integration_variables:=
	 for each argument in cdr cdr df_term join
	    if not fixp argument then list argument;
      ok:=t;
      while ok and integration_variables do
      <<variable:=car integration_variables;
	 ok:=(not depends(denominator,variable)and polynomialp(numerator,variable));
	 integration_variables:=cdr integration_variables
      >> ;
      return ok;
   end$


lisp procedure inhomogeneous_integration_of(df_term,inhomogeneous_term);
   begin scalar df_sequence,integration_variables,int_sequence,
	 variable,nr_of_integrations,integration_terms,solution,
      powers,coefficient,int_factor,solution_term,n,k;
      df_sequence:=cdr cdr df_term;

      while df_sequence do
      <<variable:=car df_sequence;
	 df_sequence:=cdr df_sequence;
	 if df_sequence and fixp car df_sequence then
 	 <<nr_of_integrations:=car df_sequence;
	    df_sequence:=cdr df_sequence>>
	 else nr_of_integrations:=1;
	 integration_variables:=variable . integration_variables;
	 int_sequence:=(variable . nr_of_integrations) . int_sequence
      >> ;
      integration_terms:=split_non_linear_form(numr simp inhomogeneous_term,
	 integration_variables);
      integration_terms:=(nil . car integration_terms) .
	 cdr integration_terms;


      solution:=nil ./ 1;
      for each term in integration_terms do
      <<powers:=car term;coefficient:=cdr term;
	 int_factor:=1;solution_term:=1 ./ 1;
	 for each integration in int_sequence do
 	 <<variable:=car integration;k:=cdr integration;
	    n:=(if power then cdr power else 0)where power=assoc(variable,powers);

	    for i:=1:k do int_factor:=(n+i)*int_factor;
	    solution_term:=multsq(solution_term,mksq(variable,n+k))
	 >> ;
	 solution_term:=multsq(solution_term,coefficient ./ int_factor);
	 solution:=addsq(solution,solution_term)
      >> ;
      solution:=multsq(solution,1 ./ denr simp inhomogeneous_term);
      solution:=mk!*sq subs2 addsq(solution,simp homogeneous_integration_of df_term);
      return solution
   end$


lisp procedure try_a_differentiation(n,total_used,equation,present_variables,
      df_terms,linear_functions,commutator_functions);
      begin scalar differentiations_list,polynomial_order;


	 present_variables:=for each variable in present_variables collect
	    (variable . nil . 0);

	 for each kernel in df_terms do
	    for each variable in
	       ((if depl_entry then cdr depl_entry)
		  where depl_entry=assoc(cadr(kernel),depl!*))do

		     rplacd(entry,kernel . (cddr entry+1))
			where entry=assoc(variable,present_variables);;

			for each kernel in linear_functions do
			   for each variable in
			      ((if depl_entry then cdr depl_entry)
				 where depl_entry=assoc(kernel,depl!*))do

				    rplacd(entry,kernel . (cddr entry+1))
				       where entry=assoc(variable,present_variables);;

				       for each kernel in commutator_functions do
					  for each variable in
					     ((if depl_entry then cdr depl_entry)
						where depl_entry=assoc(kernel,depl!*))do

						   rplacd(entry,nil . (cddr entry+1))
						      where entry=assoc(variable,present_variables);;

						      differentiations_list:=
							 for each entry in present_variables join
							    if cadr entry and cddr entry=1 and
							 (polynomial_order:=get_polynomial_order(
							    linear_solve(mk!*sq(equation ./ 1),cadr entry),car entry))
							    then list(car entry . cadr entry . (polynomial_order+1));
						      return
							 if differentiations_list then
							    if !*allow_differentiation then
 							    <<for each entry in differentiations_list do
							       setk(list(cur_eq_set!*,(total_used:=total_used+1)),
								  mk!*sq simpdf list(mk!*sq(equation ./ 1),
								     car entry,cddr entry));
							       write cur_eq_set!*,"(",n,"): Generation of ",
								  cur_eq_set!*,"(",get(cur_eq_set!*, 'total_used)+1,
								  "),...,",cur_eq_set!*,"(",total_used,
								  ") by differentiation w.r.t. ";
							       terpri!* t;
							       maprin partial_list(for each entry in differentiations_list collect
								  list('list,car entry,cddr entry),10);
							       terpri!*(not !*nat);
							       put(cur_eq_set!*, 'total_used,total_used);
							       t
							    >>
							    else <<
							       write"*** ",cur_eq_set!*,"(",n,
								  "): Generation of new equations by differentiation possible.";
							       terpri!* t;write"    Solvable with 'on allow_differentiation'";
							       terpri!* t;t>>


      end$


lisp procedure get_polynomial_order(expression,variable);
   if not depends(denr(expression:=simp expression),variable)and
      (not !*polynomial_check or polynomialp(numr expression,variable))then
      begin scalar kord!*;
	 setkorder list !*a2k variable;
	 expression:=reorder numr expression;
	 return if mvar expression=variable then ldeg expression else 0;
      end$


algebraic procedure integrate_equations(m,n);
   for i:=m:n do integrate_equation(i)$


lisp operator integrate_exceptional_equation;
lisp procedure integrate_exceptional_equation(n);
   integrate_equation(n)
      where
	 !*coefficient_check=nil,
      !*polynomial_check=nil,
      !*allow_differentiation=t$



lisp operator auto_solve;
lisp procedure auto_solve nr_list;
   begin scalar total,old_total,to_do,unsolved,old_unsolved,stuck;
      total:=old_total:=get(cur_eq_set!*, 'total_used);
      to_do:=if fixp nr_list then list nr_list
      else if car nr_list= 'list then cdr nr_list
      else nr_list;
      while not stuck and to_do do begin
	 for each eq_nr in to_do do
 	 <<integrate_equation eq_nr;
	    if cadr assoc(list(cur_eq_set!*,eq_nr),get(cur_eq_set!*, 'kvalue))neq 0
	    then unsolved:=eq_nr . unsolved>> ;
	 total:=get(cur_eq_set!*, 'total_used);
	 if total=old_total and unsolved and unsolved=old_unsolved then stuck:=t
	 else <<old_unsolved:=unsolved;
	    to_do:=reverse unsolved;
	    unsolved:=nil;
	    to_do:=append(for eq_nr:=old_total+1:total collect eq_nr,to_do);
	    old_total:=total>>
      end;
      if stuck then return 'list . reverse unsolved
      else <<terpri();write"Successful integration of all equations";terpri()>> ;
   end$

lisp operator show_equation;
lisp procedure show_equation n;
   begin scalar equation,total_used,function_list;
      if null(total_used:=get(cur_eq_set!*, 'total_used))or
	 n>total_used then

	    msgpri("SHOW_EQUATION: properly initialize",
	       cur_eq_set!*,nil,nil,t);
      if(equation:=assoc(list(cur_eq_set!*,n),
	 get(cur_eq_set!*, 'kvalue)))then
	 begin
	    equation:=setk(list(cur_eq_set!*,n),aeval cadr equation);
	    varpri(equation,list('setk,mkquote list(cur_eq_set!*,n),
	       mkquote equation), 'only);
	    function_list:=get_recursive_kernels(numr simp equation,
	       get(cur_eq_set!*, 'function_list));
	    if function_list then
 	    <<terpri!* t;write"Functions occurring:";terpri!* t;
	       for each fn in function_list do
 	       <<maprin(fn .
		  ((if depl_entry then cdr depl_entry)
		     where depl_entry=assoc(fn,depl!*)));terpri!*(not !*nat)>>
	    >>
	    else terpri!* nil
	 end
   end$


algebraic procedure show_equations(m,n);
   for i:=m:n do show_equation i$


lisp operator functions_used,put_functions_used,
   equations_used,put_equations_used;


lisp procedure functions_used function_name;
   list('list,get(function_name, 'even_used),get(function_name, 'odd_used))$


lisp procedure put_functions_used(function_name,even_used,odd_used);
   begin
      if not fixp even_used or even_used<0 or
	 not fixp odd_used or odd_used<0 then

	    msgpri("PUT_FUNCTIONS_USED: used functions number invalid",
	       nil,nil,nil,t);
      put(function_name, 'even_used,even_used);
      put(function_name, 'odd_used,odd_used);
   end$


lisp procedure equations_used;
   get(cur_eq_set!*, 'total_used)$


lisp procedure put_equations_used(n);
   if not fixp n or n<0 then

      msgpri("PUT_EQUATIONS_USED: used equation number invalid",nil,nil,nil,t)
   else put(cur_eq_set!*, 'total_used,n)$


lisp operator df_acts_as_derivation_on;

lisp procedure df_acts_as_derivation_on operator_name;
   begin
      put(operator_name, 'dfform, 'df_as_derivation);
   end$


lisp procedure df_as_derivation(kernel,variable,power);
   begin scalar left_part,right_part,argument,derivative;
      if power neq 1 then

	 msgpri("DF_AS_DERIVATION:",kernel,"must occur linearly",nil,t);
      left_part:=list car kernel;
      right_part:=cdr kernel;
      derivative:=nil . 1;
      while right_part do
      <<argument:=car right_part;
	 right_part:=cdr right_part;
	 derivative:=addsq(derivative,
	    simp append(reverse left_part,
	       list('df,argument,variable) . right_part));
	 left_part:=argument . left_part;
      >> ;
      return derivative;
   end$


lisp operator listlength$

lisp procedure listlength l;
   listpri_depth!*:=l$


symbolic procedure listpri l;
   begin
      scalar orig,split,u;
      u:=l;
      l:=cdr l;
      prin2!* get('!*lcbkt!*, 'prtch);
      orig:=orig!*;
      orig!*:=if posn!*<18 then posn!* else orig!*+3;
      if null l then go to b;
      split:=treesizep(l,listpri_depth!*);
   a:  maprint(negnumberchk car l,0);
      l:=cdr l;
      if null l then go to b;
      oprin '!*comma!*;
      if split then terpri!* t;
      go to a;
   b:  prin2!* get('!*rcbkt!*, 'prtch);
      orig!*:=orig;
      return u
   end$

endmodule;
end;


