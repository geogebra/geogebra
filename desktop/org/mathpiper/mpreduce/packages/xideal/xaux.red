module xaux;

% Auxiliary functions for XIDEAL

% Author: David Hartley

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



Comment.  The routines in EXCALC sometimes use a new type, here called
wedgepf, internally.  It has the same structure as a pf, but the powers
are lists of factors in an implicit wedge product.  The WEDGE tag may
or may not be present.  A pf, typically a 0- or 1-form, can be
converted to this type using mkunarywedge.  More general routines for
converting pf <-> wedgepf are provided here.

It is not necessary for the WEDGE kernels passed to the EXCALC product
routines to be unique (and the output is not), hence two conversions
lpow wedgepf -> lpow pf are given below: mkuwedge constructs a unique
kernel, while mknwedge may be non-unique.  The results of the product
routine wedgepf defined here are unique.

endcomment;


symbolic procedure !*wedgepf2pf f;
   % f:wedgepf -> !*wedgepf2pf:pf
   if null f then nil
   else mkuwedge lpow f .* lc f .+ !*wedgepf2pf red f;


symbolic procedure !*pf2wedgepf f;
   % f:wedgepf -> !*pf2wedgepf:pf
   if null f then nil
   else wedgefax lpow f .* lc f .+ !*pf2wedgepf red f;


symbolic procedure mkuwedge u;
   % u:list of kernel -> mkuwedge:lpow pf
   % result is a unique kernel
   if cdr u then car fkern('wedge . u) else car u;


symbolic procedure mknwedge u;
   % u:list of kernel -> mknwedge:lpow pf
   % result is a non-unique kernel
   if cdr u then 'wedge . u else car u;


symbolic procedure wedgefax u;
   % u:lpow pf -> wedgefax:list of kernel
   if eqcar(u,'wedge) then cdr u else {u};


symbolic procedure wedgepf(u,v);
   % u,v:pf -> wedgepf:pf
   !*wedgepf2pf wedgepf2(u,!*pf2wedgepf v);


Comment.  The list xvars!* is used to decide which 0-form kernels are
counted as parameters and which as variables ("xvars") in partitioned
pf's.  The xvars statement allows this list to be set.

endcomment;


fluid '(xvars!*);

rlistat '(xvars);

symbolic procedure xvars u;
   % u:list of prefix -> xvars:nil
   begin
   xvars!* := if u = {nil} then t else xvarlist u;
   end;


symbolic procedure xvarlist u;
   % u:list of prefix -> xvarlist:list of kernel
   % recursively evaluate and expand lists
   for each x in u join
      if eqcar(x := reval x,'list) then xvarlist cdr x
      else {!*a2k x};


symbolic procedure xpartitsq u;
   % u:sq -> xpartitsq:pf
   % Leaves unexpanded structure if possible
   (if null x then nil
    else if domainp x then 1 .* u .+ nil
    else addpf(if sfp mvar x then
                  wedgepf(xexptpf(xpartitsq(mvar x ./ 1),ldeg x),
                          xpartitsq cancel(lc x ./ y))
               else if xvarp mvar x then
                  wedgepf(xexptpf(xpartitk mvar x,ldeg x),
                          xpartitsq cancel(lc x ./ y))
               else
                  multpfsq(xpartitsq cancel(lc x ./ y),
                           !*p2q lpow x),
               xpartitsq(red x ./ y)))
    where x = numr u, y = denr u;


symbolic procedure xpartitk k;
   % k:kernel -> xpartitk:pf
   % k is an xvar. If k is not a variable (eg a wedge product)
   % then its arguments may need reordering if they've been through subf1.
   if memqcar(k,'(wedge partdf)) then
      (if j=k then !*k2pf k else xpartitop j) where j=reval k
   else !*k2pf k;


symbolic procedure xpartitop u;
   xpartitsq simp!* u;


symbolic procedure xexptpf(u,n);
   % u:pf,n:posint -> xexptpf:pf
   if n = 1 then u
   else wedgepf(u,xexptpf(u,n-1));


symbolic procedure xvarp u;
   % u:kernel -> xvarp:bool
   % Test for exterior variables: p-forms (incl. p=0) and vectors
   % xvars!* controls whether 0-forms are included: if t, then all
   % 0-forms are included, otherwise only those in xvars!*.  Forms of
   % degree other than 0 are always included.  If xvars!* contains x,
   % then sin(x) is not an xvar (unless explicitly listed) since it is
   % algebraically independent.
   % Should the last line be exformp u?
   if xvars!* neq t then
      xdegree u neq 0 or u memq xvars!*
   else if atom u then
      get(u,'fdegree)
   else if flagp(car u,'indexvar) then
      assoc(length cdr u,get(car u,'ifdegree))
   else
      car u memq '(wedge d partdf hodge innerprod liedf);


symbolic operator excoeffs;
symbolic procedure excoeffs u;
   begin scalar x;
   u := 1 .+ xpartitop u;
   while (u := red u) do
      x := mk!*sq lc u . x;
   return makelist reverse x;
   end;


symbolic operator exvars;
symbolic procedure exvars u;
   begin scalar x;
   u := 1 .+ xpartitop u;
   while (u := red u) do
      x := lpow u . x;
   return makelist reverse x;
   end;


% Various auxilliary functions


symbolic procedure xdegree f;
   % f:prefix -> xdegree:int
   % This procedure gives the degree of a homogeneous form (deg!*form in
   % excalc returns nil for 0-forms). Behaves erratically with
   % inhomogeneous forms.
   (if null x then 0 else x) where x = deg!*form f;



symbolic procedure xhomogeneous f;
   % f:pf ->  xhomogeneous:int or nil
   % Result is degree of f if homogeneous, otherwise nil.
   if null f then 0
   else if null red f then xdegree lpow f
   else (if d = xhomogeneous red f then d) where d = xdegree lpow f;


symbolic procedure xmaxdegree f;
   % f:pf -> xmaxdegree:int
   % Returns the maximum degree among the terms of f
   if null f then 0
   else max(xdegree lpow f,xmaxdegree red f);


symbolic procedure xnormalise f;
   % f:pf -> xnormalise:pf
   % rescale f so that the leading coefficient is 1
   if null f then nil
   else if lc f = (1 ./ 1) then f
   else multpfsq(f,invsq lc f);


symbolic procedure subs2pf f;
   % f:pf -> subs2pf:pf
   % Power check for pf. Only leading term is guaranteed correct.
   if f then
      (if numr c then lpow f .* c .+ red f else subs2pf red f)
         where c = subs2 resimp lc f;


symbolic procedure subs2pf!* f;
   % f:pf -> subs2pf!*:pf
   % Power check for pf. All terms guaranteed correct.
   if f then
    (if numr c then lpow f .* c .+ subs2pf!* red f else subs2pf!* red f)
         where c = subs2 resimp lc f;


% Partitioned form printing


symbolic procedure !*pf2a f;
   % f:pf -> !*pf2a:!*sq prefix
   % Returns 0-form ^ 0-form to 0-form * 0-form.
   mk!*sq !*pf2sq repartit f;


symbolic procedure !*pf2a1(f,v);
   % f:pf, v:bool -> !*pf2a1:prefix
   % !*sq prefix if v null, else true prefix.
   % Returns 0-form ^ 0-form to 0-form * 0-form.
   !*q2a1(!*pf2sq repartit f,v);


symbolic procedure preppf f;
   % f:pf -> preppf:prefix
   % produce a partitioned prefix form
   if null(f := preppf0 f) then 0
   else if length f = 1 then car f
   else 'plus . f;


symbolic procedure preppf0 f;
   % f:pf -> preppf0:list of prefix
   % produce a list of prefix terms
   % prepsq!* takes out over minus signs
   if null f then nil
   else preppf1(lpow f,prepsq!* lc f) . preppf0 red f;


symbolic procedure preppf1(k,c);
   % k:lpow pf, c:prefix -> preppf1:prefix
   % extract an overall minus sign, and expand an overall product
   if k = 1 then c
   else if c = 1 then k
   else if eqcar(c,'minus) then {'minus,preppf1(k,cadr c)}
   else if eqcar(c,'times) then append(c,{k})
   else if eqcar(c,'quotient) and eqcar(cadr c,'minus) then
      preppf1(k,{'minus,{'quotient,cadr cadr c,caddr c}})
   else {'times,c,k};


symbolic procedure printpf f;
   % f:pf -> printpf:nil
   % A simple printing routine for use in tracing
   mathprint preppf f;

endmodule;

end;
