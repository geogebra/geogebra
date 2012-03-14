module crack;   % Top level CRACK module.
% (May require more than one run to compile using Win32-PSL.)

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


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                   %
%   CRACK Version  1 Dec 2002                                       %
%                                                                   %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

comment

Title: CRACK
Authors:

        Thomas Wolf
        Department of Mathematics
        Brock University,
        500 Glenridge Avenue, St.Catharines,
        Ontario, Canada L2S 3A1
        email: twolf@brocku.ca

        until 1997:

        Andreas Brand
        Institut fuer Informatik
        Friedrich Schiller Universitaet Jena
        07740 Jena,  Germany
        email: maa@hpux.rz.uni-jena.de

Abstract:
CRACK is a package for solving overdetermined systems of ordinary or
partial differential equations (ODEs, PDEs). Examples of programs
using CRACK are LIEPDE (for determining point and higher order
symmetries), APPLYSYM (to compute symmetry and similarity variables
for given point symmetries (symmetry reduction)) and CONLAW (for
determining first integrals for ODEs or conservation laws for PDEs).
For more details see the manual CRACK.TEX and the manuals of the
other packages;

% The following additions by FJW are to support CRACK under CSL as
% they are defined in PSL.

fluid '(promptstring!*)$

!#if (null(getd 'setprop))
symbolic procedure setprop(U, L);
   %% Store item L as the property list of U.
   %% FJW: Defined (but NOT flagged lose) in PSL only.
   %% FJW: A crude implementation for CSL.
   %% Note that in CSL flags are properties with value t.
   << for each p in plist U do remprop(U, car p);
      for each p in L do put(U, car p, cdr p) >>$
!#endif

% The following smacro definitions MUST be in this header file!

!#if (null(getd 'flag1))
symbolic smacro procedure flag1(U, V);
   %% The identifier U is flagged V.
   %% FJW: Defined and flagged lose in PSL only.
   %% FJW: This implementation based on the PSL manual.
   flag({U}, V)$
!#endif

!#if (null(getd 'remflag1))
symbolic smacro procedure remflag1(U, V);
   %% Remove V from the property list of identifier U.
   %% FJW: Defined and flagged lose in PSL only.
   %% FJW: This implementation based on the PSL manual.
   remflag({U}, V)$
!#endif

global '(!*iconic)$

symbolic fluid '(!*allowdfint_bak !*dfprint_bak !*exp_bak !*ezgcd_bak
!*fullroots_bak !*gcd_bak !*mcd_bak !*nopowers_bak !*ratarg_bak
!*rational_bak !*batch_mode abs_ adjust_fnc allflags_ batchcount_ backup_
collect_sol confirm_subst cont_ contradiction_ cost_limit5 current_dir
% dec_hist dec_hist_list
default_proc_list_ do_recycle_eqn do_recycle_fnc done_trafo eqname_
expert_mode explog_ facint_ flin_ force_sep fname_ fnew_ freeabs_
freeint_ ftem_ full_proc_list_ gcfree!* genint_ glob_var
global_list_integer global_list_ninteger global_list_number
high_gensep homogen_ history_ idname_ idnties_ independence_ ineq_
inter_divint keep_parti last_steps length_inc level_ lex_df lex_fc
limit_time lin_problem lin_test_const logoprint_ low_gensep
max_gc_counter max_gc_elimin max_gc_fac max_gc_red_len max_gc_short
max_gc_ss max_red_len maxalgsys_ mem_eff my_gc_counter nequ_
new_gensep nfct_ nid_ odesolve_ old_history one_argument_functions_
orderings_ poly_only potint_ print_ print_all print_more proc_list_
prop_list pvm_able quick_decoup record_hist recycle_eqns recycle_fcts
recycle_ids reducefunctions_ repeat_mode safeint_ session_
simple_orderings size_hist size_watch sol_list solvealg_ stepcounter_
stop_ struc_dim struc_eqn subst_0 subst_1 subst_2 subst_3 subst_4
target_limit_0 target_limit_1 target_limit_2 target_limit_3
target_limit_4 time_ time_limit to_do_list tr_decouple tr_genint
tr_gensep tr_main tr_orderings tr_redlength tr_short trig1_ trig2_
trig3_ trig4_ trig5_ trig6_ trig7_ trig8_ userrules_ vl_)$

!#if (getd 'packages_to_load)  % Load support packages, but not when compiling:
     packages_to_load ezgcd,odesolve,factor,int,algint,matrix,groebner;
     if getd('pvm_mytid) then  % Load PVM support
     packages_to_load pvm,reducepvm;
!#else                         % for REDUCE 3.6
     apply1('load_package, '(ezgcd odesolve factor int algint matrix groebner));
!#endif


!#if (get 'applysym 'folder)     % Means that mkpckge is being used.
   create!-package('(
      crack
      crdec
      crinit        %  initialisation and help
      crmain        %  main module
      crsep         %  separation module
      crgensep      %  generalized separation module
      crint         %  integration of pde's module
      crsimp        %  simplification and substitution module
      crutil        %  procedures used in several modules
      crsimpso      %  simplification of the results
      crequsol      %  equivalence of solutions
      crshort       %  reductions in length
      crorder       %  orderings support
      crstruc       %  special module for structural eqn.
      crunder       %  param. solution of underdet. lin. DEs
      crlinalg      %  simpl. and sol. of lin. alg. systems
      crsubsys      %  identifying and solving subsystems
      crtrafo       %  point transformations module
      crident       %  working with identities
      crhomalg      %  working with bilinear algebraic systems
      crpvm         %  interface for PVM
%     crintfix
      crstart
      ), nil);

!#else
in crdec!.red$         %  decouple module
in crinit!.red$        %  initialisation and help
in crmain!.red$        %  main module
in crsep!.red$         %  separation module
in crgensep!.red$      %  generalized separation module
in crint!.red$         %  integration of pde's module
in crsimp!.red$        %  simplification and substitution module
in crutil!.red$        %  procedures used in several modules
in crsimpso!.red$      %  simplification of the results
in crequsol!.red$      %  equivalence of solutions
in crshort!.red$       %  reductions in length
in crorder!.red$       %  orderings support
in crstruc!.red$       %  special module for structural eqn.
in crunder!.red$       %  param. solution of underdet. lin. DEs
in crlinalg!.red$      %  simpl. and sol. of lin. alg. systems
in crsubsys!.red$      %  identifying and solving subsystems
in crtrafo!.red$       %  point transformations module
in crident!.red$       %  working with identities
in crhomalg!.red$      %  working with bilinear algebraic systems
in crpvm!.red$         %  working parallel on PVM
!#if (equal version!* "REDUCE 3.6")
in crintfix!.red$      %  patch for the integration
!#endif

setcrackflags()$

!#endif

endmodule;

end$

