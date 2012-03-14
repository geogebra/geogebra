%**********************************************************************
module crackinit$
%**********************************************************************
%  Initialisation
%  Author: Andreas Brand 1993 - 97
%          Thomas Wolf since 1994

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


% variables that are backed up in recursive calls
glob_var:='(
!*batch_mode !*iconic adjust_fnc allflags_ batchcount_ collect_sol
confirm_subst cont_ contradiction_ cost_limit5 % dec_hist dec_hist_list
depl!* done_trafo eqname_ expert_mode explog_ facint_ flin_ % force_sep
fname_ fnew_ freeabs_ freeint_ ftem_ genint_ high_gensep homogen_
idnties_ independence_ ineq_ inter_divint keep_parti last_steps
length_inc lex_df lex_fc lin_problem logoprint_ low_gensep max_gc_elimin
max_gc_fac max_gc_red_len % mem_eff
max_gc_short max_gc_ss max_red_len maxalgsys_ nequ_ new_gensep odesolve_
orderings_ target_limit_0 target_limit_1 target_limit_2 target_limit_3
target_limit_4 poly_only potint_ print_ print_all print_more
proc_list_ pvm_able quick_decoup record_hist recycle_eqns recycle_fcts
repeat_mode safeint_ session_ simple_orderings size_watch solvealg_ stop_
struc_dim struc_eqn subst_0 subst_1 subst_2 subst_3 subst_4 time_ to_do_list
tr_decouple tr_genint tr_gensep tr_main tr_orderings tr_short tr_redlength
userrules_ vl_)$

Comment :

Variables not to be changed interactively are not updated:
   allflags_ current_dir default_proc_list_ full_proc_list_
   lin_test_constmy_gc_counter max_gc_counter prop_list
   one_argument_functions_ reducefunctions_ trig1_ trig2_
   trig3_ trig4_ trig5_ trig6_ trig7_ trig8_

The following are ment to be used continuously:
   size_hist sol_list stepcounter_ level_ nfct_ time_limit limit_time history_

These variables are separately backed up in crutil.red in backup_to_file()
and updated in restore_backup_from_file(), see also restore_and_merge().

history_ is not backed up to accumulate all input also during subcases.
Because function names and function dependencies generated in subcalls
of crack are passed back in the solution that is passed back and on
the other hand the backup depl!* is restored, i.e. the dependencies
of the new functions is dropped, this has to be carried over by
adding their dependencies to the backup depl!*.
$

global_list_integer := '(odesolve_ subst_0 subst_1 subst_2 subst_3
   subst_4 cost_limit5 max_gc_fac max_gc_red_len max_gc_short
   max_gc_ss % dec_hist
   maxalgsys_ nfct_ nequ_ low_gensep high_gensep)$

global_list_ninteger := '(genint_ facint_ new_gensep target_limit_0
   target_limit_1 target_limit_2 target_limit_3 target_limit_4 print_ )$

global_list_number := '(length_inc)$

switch batch_mode$

compiletime global '(groebresmax)$

symbolic operator setcrackflags$
symbolic procedure setcrackflags$
<<
ONE_ARGUMENT_FUNCTIONS_:='(ABS ACOS ACOSD ACOSH ACOT ACOTD ACOTH ACSC
                            ACSCD ACSCH ASEC ASECD ASECH ASIN ASIND ASINH
                           ATAN ATAND ATANH CBRT COS COSD COSH COT
                           COTD COTH CSC CSCD CSCH EXP HYPOT LN
                           LOG LOGB LOG10 SEC SECD SECH SIN SIND SINH SQRT
                           TAN TAND TANH MINUS)$

REDUCEFUNCTIONS_:=append(ONE_ARGUMENT_FUNCTIONS_,
                         '(ATAN2 ATAN2D FACTORIAL PLUS DIFFERENCE DF TIMES
                           QUOTIENT EXPT INT))$

allflags_:='(to_eval to_fullint to_int to_sep to_gensep to_decoup
             to_diff to_under to_symbol)$

prop_list:='(val fcts vars nvars level derivs no_derivs fcteval_lin
             fcteval_nca fcteval_nli fct_nli_lin fct_nli_nca
             fct_nli_nli fct_nli_nus printlength length
             rational nonrational allvarfcts starde dec_with
             dec_with_rl rl_with % dec_info
             histry_ terms orderings partitioned hom_deg split_test
             linear_)$

% Some of the modules in the following list are still experimental.
% The order in which they appear in full_proc_list_ is a proposal for
% the order to appear in proc_list_.
full_proc_list_:='(to_do                      % 1
                   separation                 % 2
                   subst_level_0              % 3
                   subst_level_03             % 4
                   subst_level_05             % 5
                   subst_level_45             % 6
                   quick_integration          % 7
                   factorize_to_substitute    % 8
                   subst_derivative           % 9
                   quick_gen_separation       % 10
                   alg_length_reduction       % 11
                   drop_lin_dep               % 12
                   find_1_term_eqn            % 13
                   trian_lin_alg              % 14
                   subst_level_1              % 15
                   subst_level_3              % 16
                   subst_level_5              % 17
                   subst_level_2              % 18
                   subst_level_33             % 19
                   subst_level_35             % 20
                   subst_level_4              % 21
                   undetlinode                % 22
                   undetlinpde                % 23
                   full_integration           % 24
                   integration                % 25
                   gen_separation             % 26
                   diff_length_reduction      % 27
                   del_redundant_de           % 28
                   idty_integration           % 29
                   decoupling                 % 30
                   add_differentiated_pdes    % 31
                   add_diff_ise               % 32
                   multintfac                 % 33
                   alg_solve_single           % 34
                   alg_solve_system           % 35
                   undo_subst_derivative      % 36
                   change_proc_list           % 37
                   stop_batch                 % 38
                   general_trafo              % 39
                   del_redundant_fc           % 40
                   sub_problem                % 41
                   drop_dep_bi_lin            % 42
                   find_factor_bi_lin         % 43
                   split_into_cases           % 44
                   subst_level_04             % 45
                   first_int_for_ode          % 46
                   factorize_any              % 47
                   gen_separation2            % 48
                   find_and_use_sub_systems12 % 49
                   find_and_use_sub_systems13 % 50
                   find_and_use_sub_systems14 % 51
                   find_and_use_sub_systems15 % 52
                   find_and_use_sub_systems22 % 53
                   find_and_use_sub_systems23 % 54
                   find_and_use_sub_systems24 % 55
                   find_and_use_sub_systems25 % 56
                   high_prio_decoupling       % 57
                   user_defined               % 58
                   alg_groebner               % 59
                   solution_check             % 60
                   find_trafo                 % 61
                  )$

default_proc_list_:='(to_do
                     separation
                     subst_level_0
                     subst_level_03
                     quick_integration
                     factorize_to_substitute
                     factorize_any
                     subst_derivative
                     subst_level_1
                     subst_level_3
                     subst_level_2
                     subst_level_33
                     subst_level_35
                     subst_level_4
                     full_integration
                     gen_separation
                     diff_length_reduction
                     decoupling
                     integration
                     undetlinode
                     add_diff_ise
                     alg_solve_single
                     undo_subst_derivative
                    )$

proc_list_:=default_proc_list_$

% in case crident.red is not distributed:
if not getd 'show_id then <<
 full_proc_list_:=setdiff(full_proc_list_,
                          '(del_redundant_de idty_integration));
 proc_list_     :=setdiff(     proc_list_,
                          '(del_redundant_de idty_integration))
>>$

!*batch_mode:=t$   % running crack in batchmode
expert_mode:=nil$  % "half automatic" when running crack in non batch mode
repeat_mode:=nil$  % "repeat mode" when running crack in non batch mode
if not fixp nfct_ then
nfct_:=1$          % index of new functions and constants initialized
nequ_:=1$          % index of new equations initialized
nid_:=1$           % index of new identities initialized
fname_:='c_$       % name of new functions and constants (integration)
eqname_:='e_$      % name of new equations
idname_:='id_$     % name of new identities
level_:=nil$       % actual level of crack recursion
cont_:=nil$        % interactive user control for integration or
                   % substitution of large expressions is disabled
independence_:=nil$% interactive control of linear independence disabled
genint_:=15$       % if =nil then generalized integration disabled
                   % else the maximal number of new functions and extra
                   % equations due to generalized integration
facint_:=1000$     % =nil then no search for integrating factors
                   % otherwise max product terms*kernels for investigation
potint_:=t$        % allowing `potential integration'
safeint_:=t$       % uses only solutions of ODEs with non-vanishing denom.
freeint_:=t$       % Do only integrations if expl. part is integrable
freeabs_:=t$       % Allow only solutions of ODEs without ABS()
odesolve_:=100$    % maximal length of a de (number of terms) to be
                   % integrated as ode
low_gensep:=6$     % max. size of expressions to separate in a
                   % generalized way with higher priority
high_gensep:=300$  % min. size of expressions to separate in a
                   % generalized way with higher priority
new_gensep:=nil$   % whether or not a new form of gensep should be used
subst_0:=2$        % maximal length of an expression to be substituted
subst_1:=8$        %
subst_2:=10^3$     %
subst_3:=20$       %
subst_4:=10^3$     %
cost_limit5:=100$  % maximal number of extra terms generated by a subst.
my_gc_counter:=0$  % initialization of my_gc_counter
max_gc_short:=40$  % maximal number of garbage collections during shortening
max_gc_ss:=10$     % maximal number of garbage collections during
                   % search of sub_systems
max_gc_counter:=100000000$% max. number of garbage collections
max_gc_red_len:=30$% maximal number of garbage collections during
                   % length reduction
max_gc_fac:=15$    % maximal number of garbage collections during factorization
max_gc_elimin:=15$ % maximal number of garbage collections during
                   % elimination in decoupling
max_red_len:=1000000$  % max product of lengths of equations to be length
                   % reduced with the decouling procedure
target_limit_0:=nil$   % maximal product length(pde)*length(subst_expr)
target_limit_1:=10^3$  % nil=no length limit
target_limit_2:=10^4$  %
target_limit_3:=10^3$  %
target_limit_4:=nil$   %
length_inc:=1.0$   % factor by which the length of an expression may
                   % grow during decoupling
tr_main:=nil$      % Trace main procedure
tr_gensep:=nil$    % Trace generalized separation
tr_genint:=nil$    % Trace generalized integration
tr_decouple:=nil$  % Trace decoupling process
tr_redlength:=nil$ % Trace length reduction
tr_orderings:=nil$ % Trace orderings stuff
tr_short:=nil$     % Trace the algebraic shortening
homogen_:=nil$     % =t if all equations are homogeneous -> hom_deg is assigned
solvealg_:=nil$    % Use SOLVE for algebraic equations
print_more:=t$     % Print more informations about the pdes
print_all:=nil$    % Print all informations about the pdes
logoprint_:=t$     % print logo for crack call
poly_only:=nil$    % all equations are polynomials only
time_:=nil$        % print the time needed for running crack
print_:=12$        % maximal length of an expression to be printed
%dec_hist:=0$       % length of pde history list during decoupling
maxalgsys_:=20$    % max. number of equations to be solved in specialsol
adjust_fnc:=nil$   % if t then free constants/functions are scaled and
                   % redundant ones are droped to simplify the result
orderings_:=nil$   % Stores the orderings list, nil initially
simple_orderings:=t$ % Turn off orderings support except for trivial case
lex_df:=nil$       % if t then use lex. instead of tot. degree ordering
                   % of derivatives
lex_fc:=t$         % if t then lex. ordering of functions has higher
                   % priority than any ordering of derivatives
collect_sol:=t$    % whether solutions found shall be collected and
                   % returned together at the end or not (to save memory)
struc_eqn:=nil$    % whether the equations has the form of structural eqn.
quick_decoup:=nil$ % whether decoupling should be done faster with less
                   % care for saving memory
idnties_:=nil$     % list of identities resulting from reductions and
                   % integrability conditions
if getd 'show_id then
record_hist:=t   else
record_hist:=nil;  % whether the history of equations is to be recorded
keep_parti:=nil$   % whether for each equation a copy in partitioned form
                   % is to be stored to speed up several simplifications
size_watch:=nil$   % whether before each computational step the size
                   % of the system shall be recorded in size_hist
inter_divint:=nil$ % whether the integration of divergence identities
                   % with more than 2 differentiation variables shall
                   % be confirmed interactively
do_recycle_eqn:=t$ % whether equation names shall be recycled or not
                   % (saves memory but is less clear when determining
                   % histry_ in terms of original equations)
do_recycle_fnc:=nil$ % whether function names shall be recycled or not
                   % (saves memory but is less clear to follow)
old_history:=nil$  % old_history is interactive input to be read from
                   % this list
confirm_subst:=nil$% whether substitutions and the order of subcase
                   % investigations has to be confirmed
mem_eff:=t$        % whether to be memory efficient even if slower
force_sep:=nil$    % whether direct separation should be forced even
                   % if functions occur in the supposed to be linear
                   % independent explicit expressions (for non-lin. prob.)
flin_:=nil$        % a list of functions occuring only linearly in an
                   % otherwise non-linear problem. This matters in a
                   % factorization when factors with functions of flin_
                   % are considered last.
last_steps:=nil$   % a list of the last steps to avoid cycles
if null lin_test_const then lin_test_const:=gensym()$
                   % a global fixed constant to check linearity
lin_problem:=nil$  % whether the full problem is linear or not
time_limit:=nil$   % whether a time limit limit_time is set after
                   % which batch-mode is interrupted to interactive mode
limit_time:=0$     % = time()+how-much-more-time-allowed-in-batch-mode
if memq ('psl, lispsystem!*) then random_init()       % only if under PSL
                             else random_new_seed(time() + 10)$
session_:=explode date()$
session_:=
   for each c in session_ collect
    (if c memq '(!: ! ) then '!- else c);
session_ := compress session!_;    % proposed by ACN
%%session_:=reverse cons(car session_,cdr cddddr reverse session_)$
%%if cadr session_ = '!  then session_:=cons(car session_,cddr session_)$
%%session_:=compress session_$
setq(session_,bldmsg("%w%d-%w","bu",random 1000,session_))$
                   % name of the session, used to generate filename
                   % for backup when case splitting
if print_all then <<write"The name of this new session is: """,session_,""""$terpri()>>$
random_new_seed(1)$
setq(groebresmax,2000);
pvm_activate()$ % initialize pvm_able and current_dir
% if getd('pvm_mytid) then <<pvm_able:=t;current_dir:=pwd()>>
%                     else <<pvm_able:=nil;current_dir:=nil>>$
!*iconic:=nil$     % whether new processes in parallelization
                   % should appear as icons
done_trafo:={'LIST}$   % a list of backtransformations of done transformations

put('to_do,'description,
    list("Hot list of urgent steps"))$
put('subst_level_0,'description,
    list("Substitution",
         if subst_0 then " of <=",subst_0,if subst_0 then " terms",
         if target_limit_0 then " in <=",target_limit_0,if target_limit_0 then " terms",
         ", only fcts. of less vars., no cases"))$
put('subst_level_03,'description,
    list("Substitution",
         if subst_0 then " of <=",subst_0,if subst_0 then " terms",
         if target_limit_0 then " in <=",target_limit_0,if target_limit_0 then " terms",
         ", alg. expressions, no cases"))$
put('subst_level_04,'description,
    list("Substitution",
         if subst_1 then " of <=",subst_1,if subst_1 then " terms",
         if target_limit_1 then " in <=",target_limit_1,if target_limit_1 then " terms",
         ", alg. expressions, no cases"))$
put('subst_level_05,'description,
    list("Substitution",
         if subst_4 then " of <=",subst_4,if subst_4 then " terms",
         if target_limit_0 then " in <=",target_limit_0,if target_limit_0 then " terms",
         ", alg. expressions, no cases"))$
put('subst_level_1,'description,
    list("Substitution",
         if subst_1 then " of <=",subst_1,if subst_1 then " terms",
         if target_limit_1 then " in <=",target_limit_1,if target_limit_1 then " terms",
         ", fcts. of less vars."))$
put('subst_level_2,'description,
    list("Substitution",
         if subst_2 then " of <=",subst_2,if subst_2 then " terms",
         if target_limit_0 then " in <=",target_limit_0,if target_limit_0 then " terms",
         ", fcts. of less vars., no cases"))$
put('subst_level_3,'description,
    list("Substitution",
         if subst_3 then " of <=",subst_3,if subst_3 then " terms",
         if target_limit_3 then " in <=",target_limit_3,if target_limit_3 then " terms"))$
put('subst_level_33,'description,
    list("Substitution",
         if subst_4 then " of <=",subst_4,if subst_4 then " terms",
         if target_limit_4 then " in <=",target_limit_4,if target_limit_4 then " terms",
         " only linear expressions, f-indep. coeff."))$
put('subst_level_35,'description,
    list("Substitution",
         if subst_4 then " of <=",subst_4,if subst_4 then " terms",
         if target_limit_4 then " in <=",target_limit_4,if target_limit_4 then " terms",
         ", no cases"))$
put('subst_level_4,'description,
    list("Substitution",
         if subst_4 then " of <=",subst_4,if subst_4 then " terms",
         if target_limit_4 then " in <=",target_limit_4,if target_limit_4 then " terms"))$
put('subst_level_45,'description,
    list("Substitution",
         ", minimal growth",
         if cost_limit5 then ", with max ",cost_limit5,
         if cost_limit5 then " add. terms",
         ", no cases"))$
put('subst_level_5,'description,
    list("Substitution",
         if subst_4 then " of <=",subst_4,if subst_4 then " terms",
         if target_limit_4 then " in <=",target_limit_4,if target_limit_4 then " terms",
         ", minimal growth"))$
put('subst_derivative,'description,
    list("Substitution of derivatives by new functions"))$
put('undo_subst_derivative,'description,
    list("Undo Substitutions of derivatives by new functions"))$
put('factorize_to_substitute,'description,
    list("Factorization to subcases leading to substitutions"))$
put('factorize_any,'description,
    list("Any factorization"))$
put('separation,'description,
    list("Direct separation"))$
put('quick_integration,'description,
    list("Integration of a first order de with at",
         " most two terms."))$
put('full_integration,'description,
    list("Integration of a pde such that",
         " a function can be subst."))$
put('integration,'description,
    list("Any integration"))$
put('multintfac,'description,
    list("Find an integrating factor for a set of pde's"))$
put('diff_length_reduction,'description,
    list("Length reducing decoupling steps"))$
put('decoupling,'description,
    list("Do one decoupling step"))$
put('quick_gen_separation,'description,
    list("Indirect separation of <",low_gensep," or >",
         high_gensep," terms"))$
put('gen_separation,'description,
    list("Indirect separation of equations of any size"))$
put('gen_separation2,'description,
    list("Alternative indirect separation of non-lin equations"))$
put('add_differentiated_pdes,'description,
    list("Differentiate pdes with nonlinear leading derivs"))$
put('alg_length_reduction,'description,
    list("Algebraic length reduction of equations"))$
put('alg_solve_single,'description,
    list("Solving an algebraic equation."))$
put('alg_solve_system,'description,
    list("Solving equations for fnct.s or deriv.s algebraically"))$
put('stop_batch,'description,
    list("Stop batch mode"))$
put('undetlinode,'description,
    list("The parametric solution of underdetermined ODE"))$
put('undetlinpde,'description,
    list("The parametric solution of underdetermined PDE"))$
put('change_proc_list,'description,
    list("Changing the list of priorities"))$
put('drop_lin_dep,'description,
    list("Find and drop linear dependent general equations"))$
put('drop_dep_bi_lin,'description,
    list("Find and drop linear dependent bi-linear equations"))$
put('find_factor_bi_lin,'description,
    list("Find factorizable bi-linear equations"))$
put('find_1_term_eqn,'description,
    list("Find a linear dependent equation with only 1 term"))$
put('trian_lin_alg,'description,
    list("Triangularize a linear algebraic system"))$
put('general_trafo,'description,
    list("An interactive general transformation"))$
put('del_redundant_fc,'description,
    list("Drop redundant functions and constants"))$
put('sub_problem,'description,
    list("Solve a subset of equations first"))$
put('del_redundant_de,'description,
    list("Delete redundant equations"))$
put('idty_integration,'description,
    list("Integrate an identity"))$
put('add_diff_ise,'description,
    list("Differentiate indirectly separable equations"))$
put('split_into_cases,'description,
    list("Consider a given expression to be zero and non-zero"))$
put('first_int_for_ode,'description,
    list("Find symmetries and then first integrals for an ODE"))$
put('find_and_use_sub_systems12,'description,
    list("Find sub-systems with 2 non-flin_ functions"))$
put('find_and_use_sub_systems13,'description,
    list("Find sub-systems with 3 non-flin_ functions"))$
put('find_and_use_sub_systems14,'description,
    list("Find sub-systems with 4 non-flin_ functions"))$
put('find_and_use_sub_systems15,'description,
    list("Find sub-systems with 5 non-flin_ functions"))$
put('find_and_use_sub_systems22,'description,
    list("Find sub-systems with 2 flin_ functions"))$
put('find_and_use_sub_systems23,'description,
    list("Find sub-systems with 3 flin_ functions"))$
put('find_and_use_sub_systems24,'description,
    list("Find sub-systems with 4 flin_ functions"))$
put('find_and_use_sub_systems25,'description,
    list("Find sub-systems with 5 flin_ functions"))$
put('high_prio_decoupling,'description,
    list("Do one high priority decoupling step"))$
put('user_define,'description,
    list("Perform a user defined operation"))$
put('alg_groebner,'description,
    list("Computation of the algebraic Groebner basis"))$
put('solution_check,'description,
    list("Check whether a given solution is contained"))$
put('find_trafo,'description,
    list("Find a transformation to integrate a 1st order PDE"))$
  ini_let_rules()
>>$

algebraic procedure ini_let_rules$
begin
  explog_:= {
  cot(~x) => 1/tan(x),
%  e**(~x+~y) => e**x*e**y,
%  sqrt(e)**(~x+~y) => sqrt(e)**x*sqrt(e)**y,
%  e**((~x+~y)/~z) => e**(x/z)*e**(y/z),
%  sqrt(e)**((~x+~y)/~z) => sqrt(e)**(x/z)*sqrt(e)**(y/z),

  e**~x*e**~y =>  e**(x+y),
  sqrt(e)**~x*sqrt(e)**~y => sqrt(e)**(x+y),
  e**(~x/~z)*e**(~y/~z) => e**((x+y)/z),
  sqrt(e)**(~x/~z)*sqrt(e)**(~y/~z) => sqrt(e)**((x+y)/z),

  sqrt(e)**(log(~y)/~x) => y**(1/x/2),
  sqrt(e)**(-log(~y)/~x) => y**(-1/x/2),
  sqrt(e)**(~x*log(~y)/~z) => y**(x/z/2),
  sqrt(e)**(-~x*log(~y)/~z) => y**(-x/z/2),
  sqrt(e)**((~x*log(~y))/~z) => y**(x/z/2),
  e**(log(~y)/~x) => y**(1/x),
  e**(~x*log(~y)/~z) => y**(x/z),
  e**((~x*log(~y))/~z) => y**(x/z),
  int(df(~y,~x)/~y,~x) => log(y) } $

  lisp(userrules_:={'LIST})$    % LET rules defined by the user
  abs_  :={abs(~x)     => x}$

  trig1_:={sin(~x)**2  => 1-cos(x)**2}$
%  trig1_:={cos(~x)**2  => 1-sin(x)**2}$
  trig2_:={cosh(~x)**2 => (sinh(x)**2 + 1)}$
  trig3_:={tan(~x/2)   => (1-cos(x))/sin(x)}$
  trig4_:={cot(~x/2)   => (1+cos(x))/sin(x)}$
  trig5_:={cos(2*~x)   => 1-2*sin(x)**2}$
  trig6_:={sin(2*~x)   => 2*cos(x)*sin(x)}$
  trig7_:={sinh(2*~x)  => 2*sinh(x)*cosh(x)}$
  trig8_:={cosh(2*~x)  => 2*cosh(x)**2-1}$
  sqrt1_:={sqrt(~x*~y) => sqrt(x)*sqrt(y)}$
  sqrt2_:={sqrt(~x/~y) => sqrt(x)/sqrt(y)}$
end$

% The following procedure is PSL specific and has to be COMPILED!

fluid '(datebuffer);

symbolic procedure random_init()$
<<external_time(datebuffer)$
  random_new_seed(wand(wgetv(datebuffer,0),65535))
>>$

%symbolic procedure randomhack()$
% wand(external_time datebuffer,255)$

%random_new_seed ( 100 * lisp randomhack() + 27)$

endmodule$

end$
