%###############################################################
%
% FILE:    supp.red
% AUTHOR:  graebe
% CREATED: 2/2002
% PURPOSE: Interface for the extended GEO syntax to Reduce
% VERSION: $Id: supp.red,v 1.1 2002/12/26 16:27:22 compalg Exp $


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

algebraic procedure geo_simplify u; u;
algebraic procedure geo_normal u; u;
algebraic procedure geo_subs(a,b,c); sub(a=b,c);

algebraic procedure geo_gbasis(polys,vars);
  begin
  setring(vars,{},lex);
  setideal(uhu,polys);
  return gbasis uhu;
  end;

algebraic procedure geo_groebfactor(polys,vars,nondeg);
  begin
  setring(vars,{},lex);
  return groebfactor(polys,nondeg);
  end;

algebraic procedure geo_normalf(p,polys,vars);
  begin
  setring(vars,{},lex);
  return p mod polys;
  end;

algebraic procedure geo_eliminate(polys,vars,elivars);
  begin
  setring(vars,{},lex);
  return eliminate(polys,elivars);
  end;

algebraic procedure geo_solve(polys,vars);
  solve(polys,vars);

algebraic procedure geo_solveconstrained(polys,vars,nondegs);
  begin scalar u;
  setring(vars,{},lex);
  u:=groebfactor(polys,nondegs);
  return for each x in u join solve(x,vars);
  end;

algebraic procedure geo_eval(con,sol);
  for each x in sol collect sub(x,con);

end;


