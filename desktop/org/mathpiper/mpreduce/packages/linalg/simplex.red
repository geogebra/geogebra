module simplex;

% added a patch by Thomas Sturm and Andreas Dolzmann for Simplex1
% WN 21-Oct-1999
%**********************************************************************%
%                                                                      %
% Computation of the optimal value of an objective function given a    %
% number of linear inequalities using the SIMPLEX algorithm.           %
%                                                                      %
% Author: Matt Rebbeck, June 1994.                                     %
%                                                                      %
% Many of the ideas were taken from "Linear Programming" by            %
%                                                 M.J.Best & K. Ritter %
%                                                                      %
% Minor changes: Herbert Melenk, Jan 1995                              %
%                                                                      %
%   replacing first, second etc. by car, cadr                          %
%   converted big smacros to ordinary procedures                       %
%                                                                      %
%**********************************************************************%

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


if not get('leq,'simpfn) then
<<
   algebraic operator <=;
   algebraic operator >=;
>>;

symbolic smacro procedure smplx_prepsq u;
  %
  % If u in (!*sq) standard quotient form then get !:rd!: part.
  %
  if atom u then u
  else if car u = 'minus and atom cadr u then u
  else if car u = 'minus and caadr u = '!*sq then {'minus,car cadadr u}
  else if car u = 'minus and caadr u = '!:rd!: then u
  else if car u = '!:rd!: then u
  else if car u = '!*sq then prepsq cadr u;



symbolic smacro procedure fast_row_dim(in_mat);
  %
  % Finds row dimension of a matrix with no error checking.
  %
  length in_mat #- 1;



symbolic smacro procedure fast_column_dim(in_mat);
  %
  % Finds column dimension of a matrix with no error checking.
  %
  length cadr in_mat;



symbolic smacro procedure fast_stack_rows(in_mat,row_list);
  %
  % row_list is always an integer in simplex.
  %
  'mat.{nth(cdr in_mat,row_list)};




symbolic smacro procedure fast_getmat(matri,i,j);
  %
  % Get matrix element (i,j).
  %
  fast_unchecked_getmatelem list(matri,i,j);



symbolic smacro procedure fast_my_letmtr(u,v,y);
  rplaca(pnth(nth(cdr y,car my_revlis cdr u),cadr my_revlis cdr u),v);



put('simplex,'psopfn,'simplex1);

symbolic procedure simplex1(input);
  %
  % The simplex problem is:
  %
  % min {c'x | Ax = b, x>=0},
  %
  % where Ax = b describes the linear equations and c is the function
  % that is to be optimised.
  %
  % This code implements the basic phaseI-phaseII revised simplex
  % algorithm. (phaseI checks for feasibility and phaseII finds the
  % optimal solution).
  %
  % A general idea of tha algorithm is as follows:
  %
  % 1: create phase 1 data.
  %
  %    Add slack and artificial variables to equations to create matrix
  %    A1. The initial basis (ib) consists of the numbers of the columns
  %    relating to the artificial variables. The basic feasible solution
  %    (xb) (if one exists) is  B^(-1)*b where b is the r.h.s. of the
  %    equations. Throughout, cb denotes the columns of the objective
  %    matrix corresponding to ib.
  %    This data goes to the revised simplex algorithm(2).
  %
  % 2: revised simplex:
  %
  %    step 1: Computation of search direction sb.
  %
  %    Compute u = -(B^(-1))'*cb, the smallest index k, and vk s.t.
  %
  %               vk = min{c(i) + A(i)'u | i not elt of ib}.
  %
  %    If vk>=0, stop with optimal solution xb = B^(-1)*b.
  %    If vk<0, set sb = B^(-1)*A(k) and go to step 2.
  %
  %    step 2: Computation of maximum feasible step size Ob.
  %
  %    If sb<=0 then rederr "Problem unbounded from below".
  %    If (sb)(i) >0 for at least one i, compute Ob and the smallest
  %      index l s.t.
  %
  %              (xb)(l)       { (xb)(i) |                      }
  %         Ob = ------- = min { ------  | all i with (sb)(i)>0 },
  %              (sb)(l)       { (sb)(i) |                      }
  %
  %    and go to step 3.
  %
  %    step3: Update.
  %
  %    Replace B^(-1) with [phiprm((B^(-1)',A(k),l)]', xb with B^(-1)*b
  %    and the l'th elt in ib with k. Compute cb'xb and go to step 1.
  %
  % 3: If we get this far (ie: we are feasible) then apply revised
  %    simplex to A (equations with slacks added), and the new xb,
  %    Binv, and ib.
  %
  %
  % Further details and far more advanced algorithms can be found
  % in almost any linear programming book. The above was adapted from
  % "Linear Programming"  M.J.Best and K. Ritter. To date, the code
  % contains no anti_cycling algorithm.
  %
  begin
    scalar max_or_min,objective,equation_list,tmp,A,b,obj_mat,X,A1,
           phase1_obj,ib,xb,Binv,simp_calc,phase1_obj_value,big,sum,
           stop,work,I_turned_rounded_on,ans_list,optimal_value;
    integer m,n,k,i,ell,no_coeffs,no_variables,equation_variables;
    max_or_min := reval car input;
    objective := reval cadr input;
    equation_list := normalize!-equationl reval caddr input;  % <--- patch
    if not !*rounded then << I_turned_rounded_on := t; on rounded; >>;

    if (max_or_min neq 'max) and (max_or_min neq 'min) then rederr
     "Error in simplex(first argument): must be either max or min.";

    if pairp equation_list and car equation_list = 'list then
     equation_list := cdr equation_list
     else rederr "Error in simplex(third argument}: must be a list.";

    % get rid of any two (or more) equal equations! Probably makes
    % things faster in general.
    tmp := unique_equation_list(equation_list);
    equation_list := car tmp;
    equation_variables := cadr tmp;

    % If r.h.s. and l.h.s. of inequalities are both <0 then multiply
    % by -1.
    equation_list := make_equations_positive(equation_list);

    % If there are variables in the objective function that are not in
    % the equation_list then add these variables to the equation list.
    % (They are added as variable>=0).
    equation_list :=
     add_not_defined_variables(objective,equation_list,
                               equation_variables);

    tmp := initialise(max_or_min,objective,equation_list);
    A := car tmp;
    b := cadr tmp;
    obj_mat := caddr tmp;
    X := cadddr tmp;
    % no_variables is the number of variables in the input equation
    % list. this is used in make_answer_list.
    no_variables := car cddddr tmp;

    % r.h.s. (i.e. b matrix) must be positive.
    tmp := check_minus_b(A,b);
    A := car tmp;
    b := cadr tmp;

    m := fast_row_dim(A);
    n := no_coeffs := fast_column_dim(A);

    tmp := create_phase1_A1_and_obj_and_ib(A);
    A1 := car tmp;
    phase1_obj := cadr tmp;
    ib := caddr tmp;
    xb := copy_mat(b);
    Binv := fast_make_identity(fast_row_dim(A));
    % Phase 1 data is now ready to go...

    simp_calc := simplex_calculation(phase1_obj,A1,b,ib,Binv,xb);

    phase1_obj_value := car simp_calc;
    xb := cadr simp_calc;
    Binv := cadddr(simp_calc);
    if get_num_part(phase1_obj_value) neq 0
     then rederr "Error in simplex: Problem has no feasible solution.";

    % Are any artificials still basic?
    for ell:=1:m do if nth(ib,ell) <= n then <<>>
    else
    <<
      % so here, an artificial is basic in row ell.
      big := -1;
      k := 0;
      stop := nil;
      i := 1;
      while i<=n and not stop do
      <<
        sum := get_num_part(smplx_prepsq(fast_getmat(reval
                {'times,fast_stack_rows(Binv,ell),
                 fast_augment_columns(A,i)},1,1)));
        if abs(sum) leq big then stop := t
        else
        <<
          big := abs(sum);
          k := i;
        >>;
        i := i+1;
      >>;
      if big geq 0 then <<>>
      else rederr {"Error in simplex: constraint",k," is redundant."};
      work := fast_augment_columns(A,k);
      Binv := phiprm(Binv,work,ell);
      nth(ib,ell) := k;
    >>;

    % Into Phase 2.
    simp_calc := simplex_calculation(obj_mat,A,b,ib,Binv,xb);
    optimal_value := car simp_calc;
    xb := cadr simp_calc;
    ib := caddr simp_calc;
    ans_list := make_answer_list(xb,ib,no_coeffs,X,no_variables);
    if I_turned_rounded_on then off rounded;
    if max_or_min = 'max then
     optimal_value := my_reval{'minus,optimal_value};
    return {'list,optimal_value,'list.ans_list};
  end;

flag('(simplex1),'opfn);

procedure normalize!-equationl(eql);
  if eqcar(eql , 'list) then
   'list . for each equ in cdr eql collect normalize!-equation equ
  else eql;

procedure normalize!-equation(equ);
   begin scalar lhs,b;
      lhs := numr subtrsq(simp cadr equ,simp caddr equ);
      b := negf abssummand lhs;
      return {car equ,prepf addf(lhs,b),prepf b}
   end;

procedure abssummand(f);
   if domainp f then f else if red f then abssummand red f;

symbolic procedure unique_equation_list(equation_list);
  %
  % Removes repititions in input. Also returns coeffecients in equation
  % list.
  %
  begin
    scalar unique_equation_list,coeff_list;
    for each equation in equation_list do
    <<
      if not intersection({equation},unique_equation_list) then
      <<
        unique_equation_list := append(unique_equation_list,{equation});
        coeff_list := union(coeff_list,get_coeffs(cadr equation));
      >>;
    >>;
    return {unique_equation_list,coeff_list};
  end;



symbolic procedure make_equations_positive(equation_list);
  %
  % If r.h.s. and l.h.s. of inequality are <0 then mult. both sides by
  % -1.
  %
  for each equation in equation_list collect
   if pairp cadr equation and caadr equation = 'minus and
      pairp caddr equation and caaddr equation = 'minus
    then {car equation,my_minus(cadr equation),my_minus(caddr equation)}
     else equation;



symbolic procedure add_not_defined_variables
                    (objective,equation_list,equation_variables);
  %
  % If variables in the objective have not been defined in the
  % inequalities(equation_list) then add them. They are added as
  % variable >= 0.
  %
  begin
    scalar obj_variables;
    obj_variables := get_coeffs(objective);
    if length obj_variables = length equation_variables then
     return equation_list;
    for each variable in obj_variables do
    <<
      if not intersection({variable},equation_variables) then
      <<
        prin2
         "*** Warning: variable ";
        prin2 variable;
        prin2t " not defined in input. Has been defined as >=0.";
       equation_list := append(equation_list,{{'geq,variable,0}});
      >>;
    >>;
    return equation_list;
  end;



symbolic procedure initialise(max_or_min,objective,equation_list);
  %
  % Creates A (with slack variables included), b (r.h.s. of equations),
  % the objective matrix (obj_mat) and X s.t. AX=b and
  % obj_mat * X = objective function.
  % Also returns the number of equations in the equation_list so we know
  % where to stop making answers in make_answer_list.
  %
  begin
    scalar more_init,A,b,obj_mat,X;
    integer no_variables;
    if max_or_min = 'max then
     objective := reval{'times,objective,-1};
    more_init := more_initialise(objective,equation_list);
    A := car more_init;
    b := cadr more_init;
    obj_mat := caddr more_init;
    X := cadddr more_init;
    no_variables := car cddddr more_init;
    return {A,b,obj_mat,X,no_variables};
  end;



symbolic procedure more_initialise(objective,equation_list);
  begin
    scalar objective,equation_list,non_slack_variable_list,obj_mat,
           no_of_non_slacks,tmp,variable_list,slack_equations,A,b,X;
    non_slack_variable_list :=
     get_preliminary_variable_list(equation_list);
    no_of_non_slacks := length non_slack_variable_list;
    tmp := add_slacks_to_equations(equation_list);
    slack_equations := car tmp;
    b := cadr tmp;
    variable_list := union(non_slack_variable_list,caddr tmp);
    tmp := get_X_and_obj_mat(objective,variable_list);
    X := car tmp;
    obj_mat := cadr tmp;
    A := simp_get_A(slack_equations,variable_list);
    return {A,b,obj_mat,X,no_of_non_slacks};
  end;



symbolic procedure check_minus_b(A,b);
  %
  % The algorithm requires the r.h.s. (i.e. the b matrix) to contain
  % only positive entries.
  %
  begin
    for i:=1:row_dim(b) do
    <<
      if get_num_part( reval getmat(b,i,1) ) < 0 then
      <<
        b := mult_rows(b,i,-1);
        A := mult_rows(A,i,-1);
      >>;
    >>;
    return {A,b};
  end;



symbolic procedure create_phase1_A1_and_obj_and_ib(A);
  begin
    scalar phase1_obj,A1,ib;
    integer column_dim_A1,column_dim_A,row_dim_A1,i;
    column_dim_A := fast_column_dim(A);
    % Add artificials to A.
    A1 := fast_matrix_augment({A,fast_make_identity(fast_row_dim(A))});
    column_dim_A1 := fast_column_dim(A1);
    row_dim_A1 := fast_row_dim(A1);
    phase1_obj := mkmatrix(1,fast_column_dim(A1));
    for i:=column_dim_A+1:fast_column_dim(A1) do
     fast_setmat(phase1_obj,1,i,1);
    ib := for i:=column_dim_A+1:fast_column_dim(A1) collect i;
    return {A1,phase1_obj,ib};
  end;



symbolic procedure simplex_calculation(obj_mat,A,b,ib,Binv,xb);
  %
  % Applies the revised simplex algorithm. See above for details.
  %
  begin
    scalar rs1,sb,rs2,rs3,u,continue,obj_value;
    integer k,iter,ell;
    obj_value := compute_objective(get_cb(obj_mat,ib),xb);
    while continue neq 'optimal do
    <<
      rs1 := rstep1(A,obj_mat,Binv,ib);
      sb := car rs1;
      k := cadr rs1;
      u := caddr rs1;
      continue := cadddr rs1;
      if continue neq 'optimal then
      <<
        rs2 := rstep2(xb,sb);
        ell := cadr rs2;
        rs3 := rstep3(xb,obj_mat,b,Binv,A,ib,k,ell);
        iter := iter + 1;
        Binv := car rs3;
        obj_value := cadr rs3;
        xb := caddr rs3;
      >>;
    >>;
    return {obj_value,xb,ib,Binv};
  end;



symbolic procedure get_preliminary_variable_list(equation_list);
  %
  % Gets all variables before slack variables are added.
  %
  begin
    scalar variable_list;
    for each equation in equation_list do
     variable_list := union(variable_list,get_coeffs(cadr equation));
    return variable_list;
  end;



symbolic procedure add_slacks_to_equations(equation_list);
  %
  % Takes list of equations (=, <=, >=) and adds required slack
  % variables. Also returns all the rhs integers in a column matrix,
  % and a list of the added slack variables.
  %
  begin
    scalar slack_list,rhs_mat,slack_variable,slack_variable_list;
    integer i,row;
    rhs_mat := mkmatrix(length equation_list,1);
    row := 1;
    for each equation in equation_list do
    <<
      if not numberp reval caddr equation then
      <<
        prin2 "***** Error in simplex(third argument): ";
        rederr "right hand side of each inequality must be a number";
      >>
      else fast_setmat(rhs_mat,row,1,caddr equation);

      row := row+1;

      %
      % Put in slack/surplus variables where required.
      %
      if car equation = 'geq then
      <<
        i := i+1;
        slack_variable := mkid('sl_var,i);
        equation := {'plus,{'minus,mkid('sl_var,i)},cadr equation};
        slack_variable_list := append(slack_variable_list,
                                      {slack_variable});
      >>
      else if car equation = 'leq then
      <<
        i := i+1;
        slack_variable := mkid('sl_var,i);
        equation := {'plus,mkid('sl_var,i),cadr equation};
        slack_variable_list := append(slack_variable_list,
                                      {slack_variable});
      >>
      else if car equation = 'equal then equation := cadr equation
      else
      <<
        prin2 "***** Error in simplex(third argument):";
        rederr "inequalities must contain either >=, <=, or =.";
      >>;

      slack_list := append(slack_list,{equation});
    >>;
    return {slack_list,rhs_mat,slack_variable_list};
  end;

flag('(add_slacks_to_list),'opfn);



symbolic procedure simp_get_A(slack_equations,variable_list);
  %
  % Extracts the A matrix from the slack equations.
  %
  begin
    scalar A,slack_elt,var_elt;
    integer row,col,length_slack_equations,length_variable_list;
    length_slack_equations := length slack_equations;
    length_variable_list := length variable_list;
    A := mkmatrix(length slack_equations,length variable_list);
    for row := 1:length_slack_equations do
    <<
      for col := 1:length_variable_list do
      <<
        slack_elt := nth(slack_equations,row);
        var_elt := nth(variable_list,col);
        fast_setmat(A,row,col,smplx_prepsq(
                               algebraic coeffn(slack_elt,var_elt,1)));
      >>;
    >>;
    return A;
  end;



symbolic procedure get_X_and_obj_mat(objective,variable_list);
  %
  % Converts the variable list into a matrix and creates the objective
  % matrix. This is the matrix s.t. obj_mat * X = objective function.
  %
  begin
    scalar X,obj_mat;
    integer i,length_variable_list,tmp;
    length_variable_list := length variable_list;
    X := mkmatrix(length_variable_list,1);
    obj_mat := mkmatrix(1,length_variable_list);
    for i := 1:length variable_list do
    <<
      fast_setmat(X,i,1,nth(variable_list,i));
      tmp := nth(variable_list,i);
      fast_setmat(obj_mat,1,i,algebraic coeffn(objective,tmp,1));
    >>;
    return {X,obj_mat};
  end;



symbolic procedure get_cb(obj_mat,ib);
  %
  % Gets hold of the columns of the objective matrix that are pointed
  % at in ib.
  %
  fast_augment_columns(obj_mat,ib);



symbolic procedure compute_objective(cb,xb);
  %
  % Objective value := cb * xb (both are matrices)
  %
  fast_getmat(reval {'times,cb,xb},1,1);



symbolic procedure rstep1(A,obj_mat,Binv,ib);
  %
  % Implements step 1 of the revised simplex algorithm.
  % ie: Computation of search direction sb.
  %
  % See above for details. (comments in simplex).
  %
  begin
    scalar u,sb,sum,i_in_ib;
    integer i,j,m,n,k,vkmin;
    m := fast_row_dim(A);
    n := fast_column_dim(A);
    u := mkmatrix(m,1);
    sb := mkmatrix(m,1);
    %  Compute u.
    u := reval {'times,{'minus,algebraic (tp(Binv))},
                algebraic tp(symbolic get_cb(obj_mat,ib))};
    k := 0;
    vkmin := 10^10;
    i := 1;
    for i:=1:n do
    <<
      i_in_ib := nil;
      % Check if i is in ib.
      for j:=1:m do
      <<
        if i = nth(ib,j) then i_in_ib := t;
      >>;
      if not i_in_ib then
      <<
        sum := specrd!:plus(smplx_prepsq(fast_getmat(obj_mat,1,i)),
                two_column_scalar_product(fast_augment_columns(A,i),u));
        % if i is not in ib.
        %sum := fast_getmat(obj_mat,1,i);
        %for p:=1:m do
        %<<
          %sum := reval
          % {'plus,sum,{'times,fast_getmat(A,p,i),fast_getmat(u,p,1)}};
        %>>;
        if get_num_part(sum) geq get_num_part(vkmin) then <<>>
        else
        <<
          vkmin := sum;
          k := i;
        >>;
      >>;
    >>;
    % Do we need a tolerance here?
    if get_num_part(vkmin) < 0 then
    <<
      % Form sb.
      for i:=1:m do
      <<
        sum := 0;
        for j:=1:m do sum := specrd!:plus(sum,
         specrd!:times(fast_getmat(Binv,i,j),fast_getmat(A,j,k)));
        fast_setmat(sb,i,1,sum);
      >>;
      return {sb,k,u,nil};
    >>
    else return {sb,k,u,'optimal};
  end;



symbolic procedure rstep2(xb,sb);
  %
  % step 2: Computation of maximum feasible step size Ob.
  %
  % see above for details. (comments in simplex).
  %
  begin
    scalar ratio;
    integer ell,sigb;
    sigb := 1*10^30;
    for i:=1:fast_row_dim(sb) do
    <<
      if get_num_part(my_reval fast_getmat(sb,i,1)) leq 0 then <<>>
      else
      <<
        ratio := specrd!:quotient(smplx_prepsq(fast_getmat(xb,i,1)),
                                  smplx_prepsq(fast_getmat(sb,i,1)));
        if get_num_part(ratio) geq get_num_part(sigb) then <<>>
        else
        <<
          sigb := ratio;
          ell := i;
        >>;
      >>;
    >>;
    if ell= 0 then
     rederr "Error in simplex: The problem is unbounded.";
    return {sigb,ell};
  end;



symbolic procedure rstep3(xb,obj_mat,b,Binv,A,ib,k,ell);
  %
  % step3: Update.
  %
  % see above for details. (comments in simplex).
  %
  begin
    scalar work,Binv;
    work := fast_augment_columns(A,k);
    Binv := phiprm(Binv,work,ell);
    xb := reval{'times,Binv,b};
    nth(ib,ell) := k;
    obj_mat := compute_objective(get_cb(obj_mat,ib),xb);
    return {Binv,obj_mat,xb};
  end;



symbolic procedure phiprm(Binv,D,ell);
  %
  % Replaces B^(-1) with [phi((B^(-1)',A(k),l)]'.
  %
  begin
    scalar sum,temp;
    integer m,j;
    m := fast_column_dim(Binv);
    sum := scalar_product(fast_stack_rows(Binv,ell),D);
%    if get_num_part(sum) = 0 then
%     rederr
%{"Error in simplex: new matrix would be singular. Inner product = 0."};
    if not zerop get_num_part(sum) then
     sum := specrd!:quotient(1,sum);
    Binv := fast_mult_rows(Binv,ell,sum);
    for j:=1:m do
    <<
      if j = ell then <<>>
      else
      <<
        temp := fast_getmat(reval{'times,fast_stack_rows(Binv,j),D},
                            1,1);
        Binv := fast_add_rows(Binv,ell,j,{'minus,temp});
      >>;
    >>;
    return Binv;
  end;



symbolic procedure make_answer_list(xb,ib,no_coeffs,X,no_variables);
  %
  % Creates a list of the values of the variables at the optimal
  % solution.
  %
  begin
    scalar x_mat,ans_list;
    integer i;
    x_mat := mkmatrix(1,no_coeffs);
    i := 1;
    for each elt in ib do
     <<
       if fast_getmat(xb,i,1) neq 0 then
        fast_setmat(x_mat,1,elt,fast_getmat(xb,i,1)); i := i+1;
     >>;
     ans_list := for i:=1:no_variables collect
                 {'equal,my_reval fast_getmat(X,i,1),
                 get_num_part(my_reval fast_getmat(x_mat,1,i))};
    return ans_list;
  end;



% Speed functions


symbolic procedure fast_add_rows(in_mat,r1,r2,mult1);
  %
  % Replaces row2 (r2) by mult1*r1 + r2 without messing around.
  %
  begin
    scalar new_mat,fast_getmatel;
    integer i,coldim;
    coldim := fast_column_dim(in_mat);
    new_mat := copy_mat(in_mat);
    if (my_reval mult1) = 0 then return new_mat;
    for i:=1:coldim do
    <<
      if not((fast_getmatel :=my_reval fast_getmat(new_mat,r1,i)) = 0)
      then fast_setmat(new_mat,r2,i,specrd!:plus(specrd!:times(
      smplx_prepsq(mult1),smplx_prepsq(fast_getmatel)),smplx_prepsq(
      fast_getmat(in_mat,r2,i))));
    >>;
    return new_mat;
  end;



symbolic procedure fast_augment_columns(in_mat,col_list);
  %
  % Quickly augments columns of in_mat specified in col_list.
  %
  if atom col_list then 'mat.for i:=1:fast_row_dim(in_mat)
                              collect {fast_getmat(in_mat,i,col_list)}
   else 'mat.for each row in cdr in_mat
              collect for each elt in col_list collect nth(row,elt);


symbolic procedure fast_matrix_augment(mat_list);
  %
  % As in linear_algebra package but doesn't produce !*sq output.
  %
  begin
  scalar ll,new_list;
    if length mat_list = 1 then return mat_list
     else
     <<
       new_list := {};
       for i:=1:fast_row_dim(car mat_list) do
       <<
         ll := {};
         for each mat1 in mat_list do ll := append(ll,nth(cdr mat1,i));
         new_list := append(new_list,{ll});
       >>;
       return 'mat.new_list;
     >>;
  end;



symbolic procedure fast_setmat(matri,i,j,val);
  %
  % Set matrix element (i,j) to val.
  %
  fast_my_letmtr(list(matri,i,j),val,matri);



symbolic procedure fast_unchecked_getmatelem u;
  nth(nth(cdr car u,cadr u),caddr u);



symbolic procedure fast_mult_rows(in_mat,row_list,mult1);
  %
  % In simplex row_list is always an integer.
  %
  begin
    scalar new_list,new_row;
    integer row_no;
    row_no := 1;
    for each row in cdr in_mat do
    <<
      if row_no neq row_list then new_list := append(new_list,{row})
      else
      <<
        new_row := for each elt in row collect
                    my_reval{'times,mult1,elt};
        new_list := append(new_list,{new_row});
      >>;
      row_no := row_no+1;
    >>;
    return 'mat.new_list;
  end;



symbolic procedure fast_make_identity(sq_size);
  %
  % Creates identity matrix.
  %
  'mat. (for i:=1:sq_size collect
           for j:=1:sq_size collect if i=j then 1 else 0);



symbolic procedure two_column_scalar_product(col1,col2);
  %
  % Calculates tp(col1)*col2.
  %
  % Uses sparsity efficiently.
  %
  begin
    scalar sum;
    sum := 0;
    for i:=1:length cdr col1 do
    <<
      if car nth(cdr col1,i)=0 or car nth(cdr col2,i)=0 then <<>>
       else
       sum := specrd!:plus(sum,specrd!:times(smplx_prepsq(
                           car nth(cdr col1,i)),smplx_prepsq(
                           car nth(cdr col2,i))));
    >>;
    return sum;
  end;



symbolic procedure scalar_product(row,col);
  %
  % Calculates row*col.
  %
  % Uses sparsity efficiently.
  %
  begin
    scalar sum;
    sum := 0;
    for i:=1:length cadr row do
    <<
      if nth(cadr row,i)=0 or car nth(cdr col,i)=0 then <<>>
       else
       sum := specrd!:plus(sum,
                           specrd!:times(smplx_prepsq(nth(cadr row,i)),
                                     smplx_prepsq(car nth(cdr col,i))));
    >>;
    return sum;
  end;


endmodule;  % simplex.

end;
