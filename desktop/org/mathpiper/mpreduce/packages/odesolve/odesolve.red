module odesolve$  % Header for ordinary differential equation solver

% Authors: F. J. Wright and M. A. H. MacCallum
% Maintainer: F.J.Wright@maths.qmw.ac.uk, Time-stamp: <14 August 2001>

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


algebraic ODESolve_version := "ODESolve 1.065"$

global '(ODESolve!-subpackages!*)$

% Build needs repeating if this list is changed!

ODESolve!-subpackages!* := '(
   odeintfc   % User interface and condition code (FJW)
   odetop     % Top level ODESolve routines (FJW / MAHM)
   odelin     % Simple linear ODE solvers (MAHM / FJW)
   odespcfn   % Linear special function ODEs (FJW)
   odenon1    % Special form nonlinear ODEs of order 1 (MAHM / FJW)
   odenonn    % Special form nonlinear ODEs of order > 1 (FJW)
   odepatch   % Temporary REDUCE patches and extensions (FJW)
   )$

create!-package('odesolve . ODESolve!-subpackages!*, nil)$

% Modification of the "deg" function.

symbolic procedure deg(u,kern);
   <<u := simp!* u; tstpolyarg(denr u,kern); numrdeg(numr u,kern)>>
   where dmode!* = gdmode!*;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Common variable type declarations and macro definitions

% Switches to select solution form where possible -- off by default.
% Can also be set locally by options to odesolve.
switch odesolve_explicit$               % fully explicit
switch odesolve_expand$                 % expand roots of unity
switch odesolve_full$                   % fully explicit and expanded
switch odesolve_implicit$               % not parametric
switch odesolve_noint$                  % turn off selected integrations
switch noint$                           % turn off integration globally
switch odesolve_verbose$                % display ode & conditions
switch odesolve_basis$                  % output basis as linear ODE solution
switch odesolve_noswap$                 % do not swap variables
switch odesolve_norecurse$              % no recursion => noswap
% The `noswap' and `norecurse' switches are mainly for debugging.
switch odesolve_fast$                   % no heuristics => norecurse
% The `fast' switch disables all non-deterministic solution techniques
% (including most of those for nonlinear ODEs of order > 1).  it is
% useful if ODESolve is used as a service routine, including calling
% it recursively in a hook.  It makes ODESolve 1+ behave like the
% odesolve distributed with REDUCE versions up to and including 3.7,
% and so does not affect the odesolve.tst file.
switch odesolve_check$                  % check solution

%% switch odesolve_load_specfn$  !*odesolve_load_specfn := t$
% If on (the default) then autoload the specfn package if a solution
% is returned that involves special functions.  It can be turned off
% to save resources if ODE solutions will not be further manipulated,
% e.g. if conditions will NOT be imposed.

% Switches controlled by ODESolve:
fluid '(!*evallhseqp !*multiplicities !*div !*intstr
   !*exp !*mcd !*factor !*ifactor !*precise !*fullroots !*trigform)$

% REDUCE global variables manipulated by ODESolve:
fluid '(kord!* depl!*)$

% Common global ODESolve variables:
fluid '(!*odesolve!-solvable!-xy)$

symbolic operator member, delete, !*eqn2a, depends, smember, gensym$

symbolic smacro procedure eqnp u;
   eqcar(u, 'equal)$

symbolic smacro procedure ODESolve!-basisp soln;
   rlistp cadr soln and not eqnp cadadr soln$

% The following two statements are needed in case SOLVE has not been
% loaded before compiling this package.

global '(multiplicities!*)$  share multiplicities!*$

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Tracing support

switch trode$                           % trace the algorithms used
% Assign a numerical value to !*trode for extra algorithm tracing:
share !*trode$

% rlistat is not flagged eval, so for faslout ...
deflist('((traceode rlis) (traceode!* rlis) (traceode1 rlis)), 'stat)$

global '(TraceOde!-InputList)$

symbolic procedure traceode!-print u;
   %% Print sequence of elements and terminate line with linefeed.
   %% Returns nil.
   begin scalar alg;  % non-nil if any algebraic elements to print
      alg := u := revlis nconc(TraceOde!-InputList, u);
      TraceOde!-InputList := nil;
      while alg and atom car alg do alg := cdr alg;
      if alg then <<
         terpri!* t;
         for each el in u do maprin el;
         terpri!* t
      >> else <<
         for each el in u do prin2 el;
         terpri()
      >>
   end$

symbolic procedure traceode u;
   %% Print sequence of elements and terminate line with linefeed.
   %% Returns nil.
   if !*trode then traceode!-print u$

symbolic procedure traceode!* u;
   %% Print line WITHOUT linefeed:
   %% Returns nil.
   if !*trode then
      %% Assignment necessary when TraceOde!-InputList is null:
      begin TraceOde!-InputList := nconc(TraceOde!-InputList, u) end$

symbolic procedure traceode1 u;
   %% Extra tracing -- print line with linefeed:
   %% Returns nil.
   if !*trode = 1 then traceode!-print u$

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Arbitrary constants in solutions:

algebraic operator arbconst$
algebraic (!!arbconst := 0)$

algebraic procedure newarbconst();
   arbconst(!!arbconst := !!arbconst + 1)$


% General utilities:

algebraic procedure ode!-int(y, x);
   %% Currently used only in `ODESolve!-PI' in module `odelin', but
   %% should probably be used more widely, so moved here!
   int(trigsimp y, x)$

algebraic procedure ODESolve!-multi!-int(y, x, m);
   %% Integate y wrt x m times:
   %% REVISE TO INTEGRATE JUST ONCE (cf. trivial n'th order ODEs)?
   if m > 0 then ODESolve!-multi!-int(int(y,x), x, m-1) else y$

%% algebraic procedure odefailure(ode);
%%    <<
%%       %% This message moved to ODESolve!-nonlinear:
%%       %% traceode "This version of ODESOLVE cannot solve ",
%%       %%    "equations of the type given.";
%%       {ode=0}
%%    >>$

algebraic operator odesolve!-df$

endmodule$

end$
