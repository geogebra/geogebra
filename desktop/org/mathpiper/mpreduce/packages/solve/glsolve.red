module glsolve; % Routines for solving a general system of linear
                % equations using Cramer's rule, realized through
                % exterior multiplication.

% Author: Eberhard Schruefer.
% Modifications by:  D. Hartley and R.W. Tucker.

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

fluid '(!*solvesingular vars!*);  % !*solveinconsistent

global '(!!arbint assumptions requirements);

symbolic procedure glnrsolve(u,v);
% glnrsolve(u: list of sf's, v: list of kernels)
% -> (xprs: list of sq's, flg: boolean)
% Adapted by D. Hartley and R.W. Tucker from E. Schruefer's glnrsolve.
% The equations u must be ordered with respect to the kernels v
   begin scalar sgn,x,y,cnds;
     if null u then go to b;
     a: x := !*sf2ex(car u,v);
        if null x then u := cdr u
         else if inconsistency!-chk x then
                 <<cnds := car u . cnds; x := nil; u := cdr u>>;
        if u and null x then go to a;
     b:
     if null u then       % no consistent non-zero equations
       if cnds then go to d
       else return t . {{nil,nil,1}};  % all equations were zero
     if null(u := cdr u) then go to d;
     c: if y := subs2chkex extmult(!*sf2ex(car u,v),x)
          then if inconsistency!-chk y
                  then cnds :=  numr cancel(lc y ./ lc x) . cnds
                  else <<assumptions :=
                            'list . mk!*sq !*f2q lc y .
                        (pairp assumptions and cdr assumptions);
                         x := y>>;
        if (u := cdr u) then go to c;
     d:
     for each j in cnds do
        requirements := 'list . mk!*sq !*f2q j .
                           (pairp requirements and cdr requirements);
     if cnds then return 'inconsistent . nil;
     if setdiff(v,lpow x) and not !*solvesingular then
        return 'singular . {};
     if null red x then return
        t . {{for each j in lpow x collect nil ./ 1,lpow x,1}};
     y := lc x; sgn := evenp length lpow x;
     u := foreach j in lpow x collect
             (if (sgn := not sgn) then negf f else f)
              where f = !*ex2sf innprodpex(delete(j,lpow x),red x);
     return t . {{foreach f in u collect cancel(f ./ y),lpow x,1}};
   end;

symbolic procedure inconsistency!-chk u;
   null u or ((nil memq lpow u) and inconsistency!-chk red u);


endmodule;

end;
