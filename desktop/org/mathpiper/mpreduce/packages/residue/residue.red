module residue; % Calculation of residues

% Author: Wolfram Koepf
% Version 1.0, April 1995

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


% needs taylor package for execution.

remflag('(load_package),'eval);

load_package taylor;

create!-package('(residue),'(contrib misc));

fluid '(!*taylor!-max!-precision!-cycles!*);

% enlarging recursion depth
symbolic(!*taylor!-max!-precision!-cycles!* := 20);

% polynomials and rational functions
% by Winfried Neun

symbolic procedure PolynomQQQ (x);

(if fixp xx then 1 else
 if not onep denr (xx := cadr xx) then NIL
 else begin scalar kerns,kern,aa,var,fform,mvv,degg;

 fform := sfp  mvar  numr xx;
 var := reval cadr x;
 if fform then << xx := numr xx;
    while (xx neq 1) do
     << mvv :=  mvar  xx;
        degg := ldeg  xx;
        xx   := lc  xx;
        if domainp mvv then <<if not freeof(mvv,var) then
                << xx := 1 ; kerns := list list('sin,var) >> >> else
        kerns := append ( append (kernels mvv,kernels degg),kerns) >> >>
   else kerns := kernels !*q2f xx;

 aa: if null kerns then return 1;
     kern := first kerns;
     kerns := cdr kerns;
     if not(eq (kern, var)) and depends(kern,var)
                then return NIL else go aa;
end) where xx = aeval(car x);

put('PolynomQQ,'psopfn,'polynomQQQ);

symbolic procedure ttttype_ratpoly(u);
  ( if fixp xx then 1 else
        if not eqcar (xx , '!*sq) then nil
          else polynomQQQ(list(mk!*sq(numr cadr xx ./ 1),reval cadr u))
            and polynomQQQ(list(mk!*sq(denr cadr xx ./ 1),reval cadr u))
 ) where xx = aeval(car u);

flag ('(type_ratpoly),'boolean);

put('type_ratpoly,'psopfn,'ttttype_ratpoly);

symbolic procedure type_ratpoly(f,z);
    ttttype_ratpoly list(f,z);

% Calculation of residues,
% by Wolfram Koepf
algebraic procedure residue(f,x,a);
begin
scalar tmp,numerator,denominator,numcof,dencof;
  if not freeof(f,factorial) then rederr("not yet implemented");
  if not freeof(f,gamma) then rederr("not yet implemented");
  if not freeof(f,binomial) then rederr("not yet implemented");
  if not freeof(f,pochhammer) then rederr("not yet implemented");
  tmp:=taylortostandard(taylor(f,x,a,0));
  if a=infinity then tmp:=-sub(x=1/x,tmp);
  if PolynomQQ(tmp,x) then return(0);
  if part(tmp,0)=taylor then rederr("taylor fails");
  if not type_ratpoly(tmp,x) then return(nil);
  tmp:=sub(x=x+a,tmp);
  numerator:=num(tmp);
  denominator:=den(tmp);
  if numerator=0 or deg(denominator,x)<1 then return(0) else
    <<
    numcof:=coeffn(numerator,x,deg(denominator,x)-1);
    if numcof=0 then return(0);
    if freeof(denominator,x) then dencof:=denominator
      else dencof:=lcof(denominator,x);
  return(numcof/dencof);>>
end$

% Calculation of the pole order of a meromorphic function,
% by Wolfram Koepf
algebraic procedure poleorder(f,x,a);
begin
  scalar tmp,denominator;
  if not freeof(f,factorial) then rederr("not yet implemented");
  if not freeof(f,gamma) then rederr("not yet implemented");
  if not freeof(f,binomial) then rederr("not yet implemented");
  if not freeof(f,pochhammer) then rederr("not yet implemented");
  tmp:=taylortostandard(taylor(f,x,a,0));
  if a=infinity then tmp:=-sub(x=1/x,tmp);
  if PolynomQQ(tmp,x) then return(0);
  denominator:=den(tmp);
  return(deg(denominator,x));
end$

endmodule;

end;
