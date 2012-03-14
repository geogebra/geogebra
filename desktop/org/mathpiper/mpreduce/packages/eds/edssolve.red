module edssolve;

% Specialised solvers for EDS

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


Comment. The EDS solve routines are interfaces to the general REDUCE
solver, presenting the results in a form more useful in EDS. The most
primitive is edssolve, which basically just turns off arbvars and
reorganises the answer, and the most sophisticated is edssolvegraded,
which tries to solve the principal part first if the equations are
semilinear. This is useful since the principal part is often less
complex than the rest. Solutions are returned as rmaps, giving
information about both the solution and its applicability.

endcomment;


fluid '(!*edsverbose !*edsdebug !*arbvars !*varopt !*groebopt
        !*solveinconsistent depl!* cfrmcrd!* cfrmrsx!* xvars!*
        !*edssloppy !*edsdisjoint);


symbolic procedure edssolvegraded(xl,vl,rsx);
   % xl:list of sq, vl:list of list of kernel, rsx:rsx
   % -> edssolvegraded:list of (t.rmap)|(nil.list of sq)
   (begin scalar fl,z;
   z := foreach l in vl join append(l,{});
   if vl and semilinearsql(xl,car vl,if !*edssloppy then car vl else z)
     then return edssolvesemi(xl,vl,rsx);
   foreach l on vl do
      foreach y in car l do foreach ll in cdr l do
          foreach x in ll do depend1(y,x,t);
   xl := edssolve(xl,z);
   fl := {};
   xl := foreach s in xl join
      if null car s  then <<fl := s . fl; nil>>
      else if not member(0,z := pullbackrsx(rsx,cadr s)) then
          {{cadr s,purgersx append(z,caddr s)}};
   return append(reverse fl,foreach s in edsdisjoin xl collect t . s);
   end) where depl!* = depl!* ;


symbolic procedure semilinearsql(xl,vl,kl);
   % xl:list of sq, vl,kl:list of kernel -> semilinearsql:bool
   null xl
      or semilinearsq(car xl,vl,kl) and semilinearsql(cdr xl,vl,kl);


symbolic procedure semilinearsq(x,vl,kl);
   % x:sq, vl,kl:list of kernel -> semilinearsq:bool
   % True if x is linear in vl with coefficients independent of kl.
   % Assumes that kl includes vl.
   semilinearpf0(xpartitsq x,vl,kl) where xvars!* = vl;


symbolic procedure semilinearpf0(f,vl,kl);
   % f:pf, vl,kl:list of kernel  -> semilinearpf0:bool
   % Works when xvars!* allows 0-forms as well - used in solvegraded.
   % True if x is linear in vl with coefficients independent of kl.
   % Assumes that kl includes vl.
   null f or
      (lpow f = 1 and
       freeoffl(denr lc f,vl) and   % because vl might occur inside
                                    % operators
       freeoffl(numr lc f,vl)
                 or
       length wedgefax lpow f = 1 and
       freeoffl(denr lc f,kl) and   % because vl might occur inside
                                    % operators
       freeoffl(numr lc f,kl)) and
      semilinearpf0(red f,vl,kl);


symbolic procedure edssolvesemi(xl,vl,rsx);
   % xl:list of sq, vl:list of list of kernel, rsx:rsx
   % -> edssolvesemi:list of (t.rmap)|(nil.list of sq)
   % xl is known to be semilinear
   begin scalar sl,nl;
   xl := edspartsolve(xl,car vl);
   foreach x in car xl do
      if not(car x memq cadr xl) then
         sl := (car x . mk!*sq subs2 subsq(simp!* cdr x,caddr xl)) . sl
      else if numr
      << x := addsq(negsq !*k2q car x,simp!* cdr x);
         x := subs2 subsq(x,caddr xl) >>
      then nl := x . nl;
   sl := !*map2rmap reversip sl;
   if 0 member (rsx := pullbackrsx(rsx,car sl)) then return nil;
   rsx := purgersx append(rsx,cadr sl); sl := car sl;
   if null nl then return {t . {sl,rsx}};
   xl := edssolvegraded(nl,cdr vl,rsx);
   return foreach s in xl collect
      if null car s then
         nil . append(cdr s,foreach x in sl collect
                                             addsq(negsq !*k2q car x,simp!* cdr x))
      else
         t . {append(pullbackmap(sl,cadr s),cadr s),caddr s};
   end;


symbolic procedure edssolve(xl,vl);
   % xl:list of sq, vl:list of kernel -> edssolve: list of tag.value
   % where tag.value is one of
   %        t.rmap          an explicit solution
   %        nil.list of sq    a (partial) system which couldn't be solved
   % This is an interface to the REDUCE solve routines, for use by other
   % eds routines. It switches off arbvars and returns the result in a
   % form more easily used in eds. If the system is inconsistent, an
   % empty list {} is returned.
   begin scalar kl,sl,msg;
         scalar !*arbvars;  % stop arbcomplex's being generated
   msg := {"Solving",length xl,"equations in",length vl,"variables"};
   foreach q in xl do kl := union(kernels numr q,kl);
   kl := expanddependence kl;
   vl := intersection(vl,kl);        % must preserve order of vl
   msg := append(msg,{{length vl,"present"}}); % {a,b} prints as (a b)
   edsdebug(msg,nil,nil);
   sl := edssolvesys(foreach q in xl collect numr q,vl);
   if eqcar(sl,'inconsistent) then
      sl := {}
   else if eqcar(sl,'failed) or eqcar(sl,nil) then
      sl := {nil . xl}
   else if eqcar(sl,t) then
      sl := foreach s in cdr sl join
               if not explicitsolutionp s then
            {nil . foreach pr in pair(cadr s,car s) collect
               addsq(negsq !*k2q car pr,cdr pr)}
               else % need to reorder return value from edssolvesys!
            {t . !*map2rmap pair(cadr s,for each q in car s
                                           collect mk!*sq reordsq q)}
   else errdhh{"Unexpected result from solvesys:",sl};
   return sl;
   end;


symbolic procedure expanddependence kl;
   % kl: list of kernel -> expanddependence:list of kernel
   % expand kl recursively to include all kernels explicitly appearing
   % as operator arguments. Should we check implicit dependence also?
   if null kl then nil
   else if atomf car kl then
      car kl . expanddependence cdr kl
   else % must be operator, so add all arguments to list
      car kl . expanddependence append(cdar kl,cdr kl);


symbolic procedure edssolvesys(xl,vl);
   % xl:list of sf, vl:list of kernel -> edssolvesys: list of tag.value
   % where tag.value is given in solve.red. This just calls solvesys.
   solvesys(xl,vl);


symbolic procedure explicitsolutionp s;
   % s:solve solution -> explicitsolutionp:bool
   not smember('root_of,s) and not smember('one_of,s);


symbolic procedure edspartsolve(xl,vl);
   % x:list of sq,
   % vl:list of kernel -> edspartsolve:{map,list of kernel,map}
   % Solves the equations xl for the variables vl by splitting into
   % homogeneous and inhomogeneous parts.  The results are only
   % guaranteed for linear equations whose coefficient are independent
   % of the inhomogeneous parts.  The solution is returned in terms of
   % some temporary variables representing the inhomogeneous parts.
   % The temporary variables are given in the original variables by the
   % third return value.
   (begin scalar al,il,l;
%        scalar depl!*;       % preserve dependencies
   xl := splitlinearequations(xl,vl);
   xl := foreach p in xl collect
                  if null numr cdr p then
               car p
            else if (l := mk!*sq cdr p member il) then
               addsq(car p,!*k2q nth(al,1 + length il - length l))
            else
            << al := intern gensym() . al;
               il := mk!*sq cdr p . il;
               addsq(car p,!*k2q car al) >>;
   al := reversip al; il := reversip il;
   foreach y in vl do
      foreach z in al do depend1(y,z,t);
   xl := edssolve(xl,append(vl,al));
   if length xl neq 1 or null car(xl := car xl) then
      errdhh{"Bad solution in edspartsolve",xl};
   return {cadr xl,al,pair(al,il)};
   end) where depl!* = depl!*;     % preserve dependencies


symbolic procedure splitlinearequations(v,c);
   % v:list of sq, c:list of kernel
   % -> splitlinearequations:{list of sq.sq}
   % Splits rational expressions in v into homogeneous and
   % inhomogeneous parts wrt variables in c.
   begin scalar ok,g,h;
         scalar !*exp;
   !*exp := t;
   ok := setkorder c;
   v := foreach q in v collect
        << g := h := nil;
                 while not domainp f and mvar f memq c do
                    << g := lt f . g; f := red f >>;
           foreach u in g do h := u .+ h;
                 cancel(h ./ d) . cancel(f ./ d) >>
         where f = reorder numr q,
               d = reorder denr q;
   setkorder ok;
   return foreach p in v collect reordsq car p . reordsq cdr p;
   end;


symbolic procedure edsgradecoords(crd,jet0);
   % crd,jet0:list of kernel -> edsgradecoords:list of list of kernel
   % grade coordinates according to jet order, highest jet first
   if null jet0 then {crd}
   else
      begin scalar u;
      foreach c in crd do
         begin scalar j0,c0;
         j0 := jet0;
         while j0 and null(c0 := splitoffindices(car j0,c)) do
            j0 := cdr j0;
         if j0 := assoc(length c0,u) then nconc(j0,{c})
         else u := (length c0 . {c}) . u;
         end;
      u := sort(u,function(lambda x,y; car x > car y));
      return foreach v in u collect cdr v;
      end;



Comment. The routine solvepfsys tries to bring a system into solved form
in the current environment specified by cfrmcrd!* and cfrmrsx!*.

endcomment;

symbolic procedure solvepfsys sys;
   % sys:sys -> solvepfsys:{sys,sys}
   % Bring sys into solved form as far as possible.
   solvepfsys1(sys,{});

symbolic procedure solvepfsys1(sys,vars);
   % sys:sys, vars:list of lpow pf -> solvepfsys1:{sys,sys}
   % Solve sys for vars. If vars is {} then solve for anything. Kernel
   % ordering changed so that solved set comes ahead of rest. Ordering
   % within solved set and others is unchanged. The solved part is
   % returned sorted in decreasing order of lpow.  NBB. Kernel ordering
   % changed!!
   begin scalar ok,sl,nl;
   % save old kernel ordering
   ok := updkordl nil;
   nl := foreach f in sys collect subs2pf!* f;
   % First try for constant coefficients
   if nl then
      begin
%      nl := solvepfsyseasy(nl,vars,'domainp);
      nl := solvepfsyseasy(nl,vars,'cfrmconstant);
      if car nl then
          edsdebug("Solved with constant coefficients:",car nl,'sys);
      if cadr nl then
          edsdebug("Unsolved with constant coefficients:",cadr nl,'sys);
      sl := append(sl,car nl);
      nl := cadr nl;
      end;
   % If that's not enough, try for nowhere-zero coefficients
   if nl then
      begin
      nl := solvepfsyseasy(nl,vars,'cfrmnowherezero);
      if car nl then
          edsdebug("Solved with nonzero coefficients:",car nl,'sys);
      if cadr nl then
          edsdebug("Unsolved with nonzero coefficients:",cadr nl,'sys);
      sl := append(sl,car nl);
      nl := cadr nl;
      end;
   % If that's not enough, try Cramer's rule.
   if nl then
      begin
      nl := solvepfsyshard(nl,vars);
      if car nl then
          edsdebug("Solved with Cramer's rule:",car nl,'sys);
      if cadr nl then
          edsdebug("Unsolved with Cramer's rule:",cadr nl,'sys);
      sl := append(sl,car nl);
      nl := cadr nl;
      end;
   % Fix up kernel ordering and back-substitute solved forms
   setkorder ok;
   updkordl lpows sl;
   sl := xautoreduce1 xreordersys sl;
   nl := xreordersys nl;
   % Block structure may have messed up order of solved kernels
   sl := sortsys(sl,ok);
   updkordl lpows sl;
   return {sl,nl};
   end;


symbolic procedure solvepfsyseasy(sys,vars,test);
   % sys:sys, vars:list of lpow pf,
   % test:function -> solvepfsyseasy:{sys,sys}
   % Recursively bring sys into weakly solved form as far as possible
   % just by changing kernel ordering and normalising.  The solved part
   % is in normalised upper triangular form, and korder is completely
   % messed up - results must be reordered under lpows solved part.
   begin scalar sl,nl,kl;
   kl := purge foreach f in sys join xsolveables(f,test);
   kl := sort(if vars then intersection(kl,vars) else kl,'termordp);
   if null kl then return {{},sys};
   updkordl kl;
   foreach f in sys do
      if (f := xreduce(xreorder f,sl)) and apply1(test,numr lc f) then
         sl := xnormalise f . sl
      else if f then
         nl := f . nl;
   sl := reversip sl; % sl in upper triangular form
   nl := reversip foreach f in nl join
      if f := subs2pf!* xreduce(f,sl) then {f};
   if null sl or null nl then return {sl,nl};
   nl := solvepfsyseasy(nl,vars,test);
   return {append(sl,car nl),cadr nl};
   end;


symbolic procedure xsolveables(f,test);
   % f:pf, test:function -> xsolveables:list of lpow pf
   % All powers in f whose coefficient numerators satisfy test
   if null f then nil
   else if apply1(test,numr lc f) then lpow f . xsolveables(red f,test)
   else xsolveables(red f,test);


symbolic procedure solvepfsyshard(sys,vars);
   % sys:sys, vars:list of lpow pf -> solvepfsyshard:{sys,sys}
   % Bring sys into weakly solved form by Cramer's rule.  The solved
   % part is in normalised upper triangular form, and korder is
   % completely messed up - results must be reordered under lpows
   % solved part.  Allowing !*edssloppy to work here for the first time
   % minimises the number of restrictions added.
   begin scalar sl,nl,kl,w;
   if null sys then return {{},{}};
   if vars then updkordl vars;
   w := !*k2pf 1;
   foreach f in sys do
      if f := subs2pf wedgepf(if vars then xreorder f else f,w) then
         if null vars or subsetp(wedgefax lpow f,vars) then
            w := f
         else
         << if degreepf f neq 1 then
               f := xcoeff(f,intersection(wedgefax lpow f,vars));
            nl := multpfsq(f,invsq lc w) . nl >>; % exact divisions
   kl := xsolveables(w,'cfrmnowherezero);
   if null kl and !*edssloppy then kl := xpows w;
   if vars then while kl and not subsetp(wedgefax car kl,vars) do
         kl := cdr kl;
   if null kl or (car kl = 1) then return {{},sys};
   kl := wedgefax car kl;
   if !*edssloppy then
      cfrmrsx!* := xpartitsq(numr lc xcoeff(w,kl) ./ 1) . cfrmrsx!*
                                     where xvars!* = cfrmcrd!*;
   sl := foreach k in kl collect xcoeff(w,delete(k,kl));
   updkordl kl;
   return {foreach f in sl collect xnormalise xreorder f,
                 foreach f in nl collect xrepartit!* f};
   end;

endmodule;

end;
