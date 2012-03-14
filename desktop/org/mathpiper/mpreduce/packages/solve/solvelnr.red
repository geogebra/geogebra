module solvelnr; % Code for solving a general system of linear eqns.

% Authors: Anthony C. Hearn and Eberhard Schruefer.
% Modifications by: David Hartley.

% Based on code by David R. Stoutemyer modified by Donald R. Morrison.

% Copyright (c) 1993 RAND.  All rights reserved.

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


% The number of equations and the number of unknowns are arbitrary.
% I.e. the system can be under- or overdetermined.

fluid '(!*cramer !*exp !*solvesingular asymplis!* wtl!*
        !*arbvars !*trsparse !*varopt bareiss!-step!-size!*);

% switch solveinconsistent;

% !*solveinconsistent := t; % Default value.

symbolic procedure solvelnrsys(exlis,varlis);
   % exlis: list of sf, varlis: list of kernel
   % -> solvelnrsys: tagged solution list
   % Check the system for sparsity, then decide whether to use the
   % Cramer or Bareiss method.  Using the Bareiss method on sparse
   % systems, 4-step elimination seems to be faster than 2-step.
   % The Bareiss code is not good at handling surds at the moment,
   % hence exptexpflistp test.
   begin scalar w,x;
   if w := solvesparsecheck(exlis,varlis) then exlis := w
     else exlis := exlis . varlis;
%  There used to be a bug in quotfexf!*1 that required exptexpflistp.
%  This shouldn't be needed now.
%  if null !*cramer and null exptexpflistp car exlis
   if null !*cramer
      and null errorp(x :=
           errorset2{'solvebareiss,mkquote car exlis, mkquote cdr exlis}
                      where bareiss!-step!-size!* = if w then 4 else 2)
     then exlis := car x
    else exlis := solvecramer(car exlis,cdr exlis);
   return solvesyspost(exlis,varlis)
   end;

symbolic procedure exptexpflistp u;
   %  True if any of u contains an expt kernel.
   u and (exptexpfp car u or exptexpflistp cdr u);

symbolic procedure exptexpfp u;
   % True if u contains an expt kernel.
   not domainp u
      and ((eqcar(x,'expt) or exptexpfp lc u or exptexpfp red u)
           where x = mvar u);

symbolic procedure solvesyspost(exlis,varlis);
   % exlis: tagged solution list, varlis: list of kernel
   %  -> solvesyspost: tagged solution list
   % Insert arbitrary constants and present
   % solutions in same order as in varlis.
   % Also reorders expressions to prevailing kernel order.
   car exlis . foreach s in cdr exlis collect
      if car s and null cadr s then s else
      begin scalar arbvars,z;
      if !*arbvars or (null cadr s and length varlis = 1) then
         arbvars := foreach v in setdiff(varlis,cadr s) collect
                       v . mvar makearbcomplex()
      else
         varlis := intersection(varlis,cadr s);
      z := pair(cadr s,sublis(arbvars,car s));
      z := append(z,foreach p in arbvars collect car p . !*k2q cdr p);
      return {foreach v in varlis collect reordsq cdr atsoc(v,z),
              varlis,caddr s};
      end;

symbolic procedure solvecramer(exlis,varlis);
   % exlis: list of sf, varlis: list of kernel
   % -> solvecramer: tagged solution list
   % Just a different name at the moment.
   glnrsolve(exlis,varlis);

symbolic procedure solvesparsecheck(sys,vl);
   % sys: list of sf, vl: list of kernel
   % -> solvesparsecheck: nil or {list of sf,list of kernel}
   % This program checks for a sparse linear system. If the
   % system is sparse enough, it returns (exlis.varlis) reordered
   % such that a maximum triangular upper diagonal form is
   % established. Otherwise the result is NIL.
   begin scalar vl1,xl,sys1,q,x,y;
         integer sp;

   % First collect a list vl1 where each variable is followed
   % by the number of equations where it occurs, and then
   % by the number of other variables which occur in these
   % equations (connectivity). At the same time, collect a measure
   % of the sparsity.
   sp:=0;
   vl1:= for each x in vl collect x . 0 . nil;
   foreach q in sys do
      foreach x in (xl := intersection(topkerns q,vl)) do
       <<y := assoc(x,vl1);
         cdr y := (cadr y + 1) . union(xl,cddr y);
         sp := sp + 1>>;
   foreach p in vl1 do
      cddr p := length cddr p - 1; % could drop the -1

   % Drop out if density > 80%
   if sp > length sys * length vl * 0.8 then
    <<if !*trsparse then prin2t "System is not very sparse";
      return nil>>;

   % If varopt is on, sort variables first by least occurrences and then
   % least connectivity, but allow dependency to override.
   % Reset kernel order and reorder equations.
   if !*trsparse then
     solvesparseprint("Original sparse system",sys,vl);

   if !*varopt then
   << vl1 := sort(vl1,function solvevarordp);
      vl1 := foreach x in vl1 collect car x;
      vl1 := solvevaradjust vl1;
      foreach k in reverse vl1 do updkorder k;
      sys := for each q in sys collect reorder q >>
   else vl1 := foreach x in vl1 collect car x;

   % Next sort equations in ascending order of their first variable
   % and then descending order of the next variable.
   sys1:= (nil . nil) . foreach x in vl1 collect x . nil;
   foreach q in sys do
      <<if domainp q or not member(mvar q,vl1) then y := assoc(nil,sys1)
        else y := assoc(mvar q,sys1);
        cdr y := q . cdr y>>;
   foreach p in cdr sys1 do
      if cdr p then cdr p := sort(cdr p, function solvesparsesort);

   % Finally split off a leading diagonal system and push the remaining
   % equations down.
   sys := nconc(foreach p in sys1 join if cdr p then {cadr p},
                reversip foreach p in sys1 join if cdr p then cddr p);
   if !*trsparse then
      solvesparseprint("Variables and/or equations rearranged",sys,vl1);
   return sys . vl1
   end;

symbolic procedure solvevarordp(x,y);
   cadr x < cadr y or
   cadr x = cadr y and cddr x < cddr y;

symbolic procedure solvevaradjust u;
   % u:list of kernel -> solvevaradjust:list of kernel
   % Adjust ordering of u to respect dependency ordering by recursively
   % putting variables with no dependencies last
   begin scalar v,y;
   if null u then return nil;
   v := foreach x in u join
              << y := assoc(x,depl!*);
            if null y or null xnp(cdr y,u) then {x} >>;
   return nconc(solvevaradjust setdiff(u,v),v)
   end;

symbolic procedure solvesparseprint(text,sys,vl);
   <<terpri(); prin2t text;
     for each e in sys do
       << e := topkerns e;
          for each x in vl do
          if memq(x,e) then prin2 "*"  else prin2 "-";
          terpri()>>>>;

symbolic procedure topkerns u;
   % u:sf -> topkerns:list of kernel
   % kernels in top level of u
   if domainp u then nil else mvar u . topkerns red u;

symbolic procedure solvesparsesort(x,y);
   % x,y: sf -> solvesparsesort: bool
   if domainp x then nil
   else if domainp y then t
   else if mvar x = mvar y then solvesparsesort(red y,red x)
   else if ordop(mvar x,mvar y) then t
   else nil;

endmodule;

end;
