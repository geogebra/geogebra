module xideal;
%
%                               XIDEAL V2.4
%
%
% Authors:        David Hartley
%                GMD - German National Research Center
%                      for Information Technology
%                D-53754 St Augustin
%                Germany
%
%                email:   David.Hartley@gmd.de
%
%
%               Philip A Tuckey
%                Laboratoire de Physique Mol\'eculaire,
%                Universit\'e de Franche-Comt\'e,
%                25030 Besan\c{}con,
%                France
%
%                email:   pat@rs1.univ-fcomte.fr
%
%
% Description:   Tools for calculations with ideals of polynomials in
%               exterior algebra. Uses Groebner basis algorithms
%               described in D Hartley and P A Tuckey, "A direct
%               characterisation of Groebner bases in Clifford and
%               Grassmann algebras", Preprint MPI-Ph/93-96 1993, and J
%               Apel "A relationship between Groebner bases of ideals
%               and vector modules of G-algebras", Contemp
%               Math 131(1992)195.
%
% Requires:      REDUCE 3.6 patched to 25 Apr 96 or later
%
% Created:          5/8/92  V0      as ideal.red
%
% Modified:      4/3/94  V1      Renamed xideal.red
%                               Compiles independently
%                               Converted right reduction and spolys to
%                               left
%                               Added graded lexicographical ordering
%                               Enabled non-graded ideals
%                               Fixed trivial ideal bug
%                               Removed subform
%                               Renamed xtrace -> xstats
%               1/12/94 V2      Enable 2-sided ideals
%                               Enable p-forms with p >= 0
%               8/12/95  Added subs2 checking in reduction
%               19/1/96 V2.2    Added subs2 checking in xrepartit
%                               Added resimp before subs2
%                               Fixed rtypes of operators
%               16/4/96 V2.3    Added exvars and excoeffs
%
%
% Algebraic mode entry points
%
% xorder k;
%  establishes the term order, where k is one of lex, gradlex (graded by
%  number of factors in term) or deglex (graded by exterior degree of
%  term.)
%
% xvars U,V,W,...;
%  declares which degree 0 kernels are to be regarded as polynomial
%  variables (rest are coefficient parameters). U,V,W can be variables
%  or lists of variables. xvars nil, restores the default, in which all
%  declared 0-forms are polynomial variables.
%
% xideal(S) xideal(S,V,r) or xideal(S,r)
%  calculates an exterior Groebner basis for the list of generator S,
%  with optional 0-form variables V, optionally up to degree r.
%
% xmodideal(F,S) or F xmodideal S
%  reduces F with respect to an exterior Groebner basis for the list of
%  generators S. F may be either a single exterior form,
%  or a list of forms.
%
% xmod(F,S) or F xmod S
%  reduces F with respect to the set of exterior polynomials S, which is
%  not necessarily a Groebner basis. F may be either a single
%  exterior form, or a list of forms. This routine can be used in
%  conjunction with xideal to produce the same effect as xmodideal:
%         F xmodideal S = F xmod xideal(S,exdegree F).
%
% xauto(S)
%  autoreduces the polynomials in S.
%
% exvars(F)
%  returns polynomials variables (as defined by xvars) from F
%
% excoeffs(F)
%  returns polynomials coefficients (as defined by xvars) from F
%
% Switches
%
% xfullreduce   - Allows reduced Groebner bases to be calculated
%                    (default ON)
% trxideal       - Trace spoly and wedge poly production (default OFF)
% trxmod - Trace reduction to normal form (default OFF)
%
% ======================================================================

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


% Need EXCALC loaded first.

load_package 'excalc;

create!-package('(

     xideal  % Header module
     xgroeb  % GB calculation
     xreduct % Normal form algorithms
     xcrit   % Critical pairs, critical values
     xpowers % Powers, including div relation and lcm.
     xstorage        % Storage and retrieval of critical pairs and polynomials.
     xaux    % Auxiliary functions for XIDEAL
     xexcalc % Modifications to Eberhard Schruefer's excalc

                ),'(contrib xideal));

% Switches

fluid '(!*xfullreduce !*trxideal !*twosided !*trxmod);

switch xfullreduce,trxideal,twosided,trxmod;

!*xfullreduce   := t;    % whether to autoreduce GB
!*trxideal      := nil;  % display new polynomials added to GB
!*twosided      := nil;  % construct GB for two-sided ideal
!*trxmod        := nil;  % display reduction chains


% Global variables

fluid '(xvars!* xtruncate!* xvarlist!* xdegreelist!* zerodivs!*
        xpolylist!*);

xvars!*         := t;    % list of variables to include in partition
xtruncate!*     := nil;  % degree at which to truncate GB
xvarlist!*      := {};   % variables in current problem
xdegreelist!*   := {};   % a-list of degrees of variables
zerodivs!*      := {};   % odd degree variables
xpolylist!*     := {};   % internal list for debugging only

% Macros used in other modules

smacro procedure xkey pr;
  car pr;

smacro procedure pr_type pr;
  cadr pr;

smacro procedure pr_lhs pr;
  caddr pr;

smacro procedure pr_rhs pr;
  cadddr pr;

smacro procedure empty_xset;
  '!*xset!* . nil;

smacro procedure empty_xsetp c;
  null cdr c;

smacro procedure xset_item c;
  car c;


% Macros from other packages for compilation

smacro procedure ldpf u;                   % from excalc
   %selector for leading standard form in patitioned sf;
   caar u;

smacro procedure !*k2pf u;                 % from excalc
   u .* (1 ./ 1) .+ nil;

smacro procedure negpf u;                  % from excalc
   multpfsq(u,(-1) ./ 1);

smacro procedure get!*fdeg u;                      % from excalc
   (if x then car x else nil) where x = get(u,'fdegree);

smacro procedure get!*ifdeg u;                     % from excalc
   (if x then cdr x else nil)
    where x = assoc(length cdr u,get(car u,'ifdegree));

endmodule;
end;
