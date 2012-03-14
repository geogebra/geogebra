%*********************************************************************
module structural_eqns$
%*********************************************************************
%  Routines for solving a system of structural equations
%  Author: Thomas Wolf
%  1998
%
% $Id: crstruc.red $
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


%--> Necessary assignments in the application code in algebraic mode:
%
% lisp(struc_eqn:=t)$
% struc_done:={all functions for which the corresponding
%              structural equations have already satisfied their
%              integrability conditions}$

symbolic procedure ini_struc$
begin scalar done$

 done:=algebraic struc_done$

 % check initial data
 if (not pairp done) or
    (car done neq 'LIST) then
 rederr("struc_done not properly initialized!")
                               else done:=cdr done$

 % In order for structural equations to be used for formulating
 % integrability conditions and not for substitutions, we need a
 % `total differential order' ordering where the differential order
 % has highest priority higher than the lex. order of functions
 lex_fc:=nil$

 % Although only first order derivatives occur in the structural
 % equations, we will specify to order derivatives by their
 % differential order and not lexicographically.
 lex_df:=nil$

 quick_decoup:=t$ % To do the first reduction found, not looking
                  % for other reductions or integrability conditions

 proc_list_:='(
              subst_level_03
              alg_length_reduction
              decoupling
              subst_level_05
              change_proc_list
             )$

 lisp(adjust_fnc:=t)$

end$

symbolic procedure change_proc_list(arglist)$
begin scalar fcts;
 proc_list_:='(
              to_do
              subst_level_05
              separation
              quick_integration
              full_integration
              integration
              subst_derivative
              subst_level_4
              undetlinode
              gen_separation
              decoupling
              diff_length_reduction
              undo_subst_derivative
             )$
 struc_eqn:=nil$
 fcts:=union(cadr arglist,setdiff(ftem_,cadr arglist))$
 if print_ then <<
  terpri()$
  write"The priority list of procedures is changed. The new one is:"$
  priproli(proc_list_)$
  terpri()$ write"The current situation:"$
  print_statistic(car arglist,fcts);
  print_pdes(car arglist);
  print_ineq(ineq_);
  struc_dim:=length fcts;
 >>;
 return arglist
end$

endmodule$
end$

