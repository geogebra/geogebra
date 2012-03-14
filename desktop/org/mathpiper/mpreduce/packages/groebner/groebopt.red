module groebopt;


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

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%   optimization of the sequence of variables
%

% Optimization of variable sequence;the theoretical background can be found
% in  Boege/Gebauer/Kredel,J.Symb.Comp(1986)I,83-98
% Techniques modfied to the following algorithm
%
% x > y if
%  x appears in a higher power than y
%       or
%  the highest powers are equal, but x appears more often with that power.
%
% An explicit dependency DEPENDS X,Y will supersede the optimality.

symbolic procedure vdpvordopt(w,vars);
 % w : list of polynomials(standard forms),vars: list of variables;
 % returns(w . vars), both reorderdered
 begin scalar c;vars:=sort(vars,'ordop);
  c:=for each x in vars collect x . 0 . 0;
  for each poly in w do vdpvordopt1(poly,vars,c);
  c:=sort(c,function vdpvordopt2);
  intvdpvars!*:=for each v in c collect car v;
  vars:=vdpvordopt31 intvdpvars!*;
  if !*trgroeb then
  <<prin2 " optimized sequence of kernels : ";prin2t vars>>;
  return(for each poly in w collect reorder poly). vars end;

symbolic procedure vdpvordopt1(p,vl,c);
 if null p then 0 else
 if domainp p or null vl then 1 else
 if mvar p neq car vl then vdpvordopt1(p,cdr vl,c)else
 begin scalar var,pow,slot;integer n;
  n:=vdpvordopt1(lc p,cdr vl,c);
  var:=mvar p;pow:=ldeg p;slot:=assoc(var,c);
  if pow #> cadr slot then
  <<rplaca(cdr slot,pow);rplacd(cdr slot,n)>>
  else rplacd(cdr slot,n #+ cddr slot);
  return n #+ vdpvordopt1(red p,vl,c)end;

symbolic procedure vdpvordopt2(sl1,sl2);
% Compare two slots from the power table .
<<sl1:=cdr sl1;sl2:=cdr sl2;
 car sl1 #< car sl2 or car sl1 = car sl2 and cdr sl1 #< cdr sl2>>;

symbolic procedure vdpvordopt31 u;
% ' u ' : list of variables;
% returns ' u ' reordered to respect dependency ordering .
begin scalar v,y;if null u then return nil;
 v:=foreach x in u join
  <<y:=assoc(x,depl!*);if null y or null xnp(cdr y,u)then { x }>>;
 return nconc(vdpvordopt31 setdiff(u,v), v)end;

endmodule;;end;
