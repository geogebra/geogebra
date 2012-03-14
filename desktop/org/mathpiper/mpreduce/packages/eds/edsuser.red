module edsuser;

% Miscellaneous user functions

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



fluid '(alglist!* subfg!* !*arbvars xvars!*);

global '(indxl!*);


put('index_expand,'rtypefn,'quotelist);
put('index_expand,'listfn,'indexexpandeval0);

symbolic procedure indexexpandeval0(u,v);
   % u:list of prefix, v:bool -> indexexpandeval0:prefix list
   % kludge to add v argument to index_expand listfn
   makelist foreach p in getrlist indexexpandeval u collect reval1(p,v);


symbolic procedure indexexpandeval u;
   % u:list of prefix -> indexexpandeval:prefix list
   if length u neq 1 then
      rerror(eds,000,"Wrong number of arguments to index_expand")
   else if rlistp(u := reval car u) then
      makelist purge foreach x in cdr u join cdr indexexpandeval {x}
   else makelist indexexpand u;


symbolic procedure indexexpand u;
   % u:prefix -> indexexpand:list of prefix
   % u has always been reval'd, so there is no need to expand
   % summations.
   if eqexpr u then indexexpandeqn u else
   if boolexpr u then indexexpandbool u else
   begin scalar i,v,alglist!*;
   u := simp!* u;
   % Expand free indices (put them into some order for safety)
   i := idsort purge if numr u and not domainp numr u then
                              flatindxl allind !*t2f lt numr u;
   v := foreach j in mkaindxc(i,nil) join
                 if numr(j := subfreeindices(numr u,pair(i,j))) then
              {absf numr j ./ denr j};  % nprimitive too?
   return for each q in purge v collect mk!*sq multsq(q,1 ./ denr u);
   end;


symbolic procedure indexexpandeqn u;
   % u:rule|equation -> indexexpandeqn:list of rule|equation
   begin scalar i,v,lh,rh;
               scalar alglist!*;
   % Expand free indices on lhs (put them into some order for safety)
   lh := reval cadr u where subfg!* = nil; % avoid let rules
   i := idsort purge flatindxl allindk lh;
   rh := aeval caddr u;
   v := foreach j in mkaindxc(i,nil) join
      if j := subfreeindeqn({car u,lh,rh},pair(i,j)) then {j};
   % Remove duplicates
   i := {};
   v := foreach r in v join
      if not(cadr r member i) then
               << i := cadr r . i; {r} >>;
   return v;
   end;


symbolic procedure subfreeindeqn(u,l);
   % u:rule|equation, l:alist -> subfreeindeqn:rule|equation|nil
   % Make index substitution l in u. Only index symmetry simplifications
   % are allowed, so the lhs can either vanish (nil returned) or acquire
   % an overall sign (sign transferred to rhs).
   begin scalar lh,rh;
   lh := subfreeindk(cadr u,l);
   if null atomf lh then lh := revop1 lh; % gets done in rule!-list
                                          % anyway
   lh := reval lh where subfg!* = nil; % avoid let rules;
   if lh = 0 then return nil;
   rh := simp!* caddr u;
   rh := quotsq(subfreeindices(numr rh,l),subfreeindices(denr rh,l));
   if eqcar(lh,'minus) then
   << lh := cadr lh;
      rh := negsq rh >>;
   return {car u,lh,mk!*sq rh};
   end;


symbolic procedure boolexpr u;
   % u:any -> boolexpr:bool
   not atom u and flagp(car u,'boolean);


symbolic procedure indexexpandbool u;
   % u:prefix -> indexexpandbool:list of prefix
   begin scalar i,v,alglist!*;
   % Expand free indices on lhs (put them into some order for safety)
   i := idsort purge flatindxl allindk u;
   v := foreach j in mkaindxc(i,nil) collect
           car u .
              foreach a in cdr u collect reval subfreeindk(a,pair(i,j));
   return purge v;
   end;


symbolic procedure subfreeindices(u,l);
   % u:sf, l:a-list -> subfreeindices:sq
   % Discriminates indices from variables, modified from EXCALC's
   % subfindices to go inside operators other than EXCALC's.
   begin scalar alglist!*;
   return
      if domainp u then u ./ 1
      else
         addsq(
            multsq(if atom mvar u then
                             !*p2q lpow u
                          else if sfp mvar u then
                      exptsq(subfreeindices(mvar u,l),ldeg u)
                   else
                      exptsq(simp subfreeindk(mvar u,l),ldeg u),
                   subfreeindices(lc u,l)),
            subfreeindices(red u,l))
   end;


symbolic procedure subfreeindk(u,l);
   % u:kernel, l:a-list -> subfreeindk:kernel
   % Extends subindk to indexed variables
   if atom u then u
   else if flagp(car u,'indexvar) then
      car u . subla(l,cdr u)
   else subindk(l,u);


put('linear_divisors,'rtypefn,'quotelist);
put('linear_divisors,'listfn,'lineardivisors);

symbolic procedure lineardivisors(u,v);
   % u:{prefix}, v:bool -> lineardivisors:prefix list
   makelist foreach f in lineardivisorspf xpartitop reval car u collect
      !*pf2a1(f,v);


symbolic procedure lineardivisorspf f;
   % f:pf -> lineardivisorspf:list of pf
   begin scalar x,g,v;
   foreach p in xpows f do x := union(wedgefax p,x);
   foreach k in x do
   << v := intern gensym() . v;
      g := addpf(k .* !*k2q car v .+ nil,g)>>;
   x := edssolve(xcoeffs wedgepf(g,f),v);
   if length x neq 1 or caar x neq t then
      errdhh "Bad solve result in lineardivisorspf";
   x := cadar x;
   v := updkordl v;
   g := numr subf(numr !*pf2sq g,x);
   x := {};
   while g do
   << x := xpartitsq(lc g ./ 1) . x;
      g := red g >>;
   setkorder v;
   return reverse xautoreduce x;
   end;


symbolic procedure xdecomposepf f;
   % f:pf -> xdecomposepf:list of pf
   begin scalar x;
   x := lineardivisorspf f;
   if length x = degreepf f then return reverse x;
   end;


put('exfactors,'rtypefn,'quotelist);
put('exfactors,'listfn,'exfactors);

symbolic procedure exfactors(u,v);
   % u:{prefix}, v:bool -> exfactors:prefix list
   makelist foreach f in xfactorspf xpartitop reval car u collect
      !*pf2a1(f,v);


symbolic procedure xfactorspf f;
   % f:pf -> xfactorspf:list of pf
   begin scalar x;
   x := lineardivisorspf f;
   f := xreduce(f,foreach g in x collect
                                  addpf(1 .* (-1 ./ 1) .+ nil,g));
   return
      if f = !*k2pf 1 then reverse x
      else f . reverse x;
   end;


symbolic operator exact;
symbolic procedure exact u;
   % u:prefix -> exact:bool
   % True if u is an exact pform kernel
   eqcar(u,'d);

flag('(exact),'boolean);


put('derived_system,'rtypefn,'getrtypecar);
put('derived_system,'edsfn,'deriveeds);
put('derived_system,'listfn,'derivelist);

symbolic procedure derivelist(u,v);
   % u:{xeds|rlist}, v:bool -> derivelist:rlist
   !*sys2a1(derive !*a2sys reval car u,v) where xvars!* = nil;


symbolic procedure deriveeds s;
   % s:eds -> deriveeds:eds
   begin
   s := copyeds s;
   if pfaffian s then
      eds_sys s := derive pfaffpart eds_sys s
   else rerror(eds,000,"non-Pfaffian system in derived_system");
   return s;
   end;


symbolic procedure derive s;
   % s:sys -> derive:sys
   begin scalar c,f;
   if null s then s;
   s := xautoreduce s;
   c := for each f in s collect
               if degreepf f = 1 then intern gensym()
         else rerror(eds,000,"non-Pfaffian system in derived_system");
   f := zippf(s,foreach k in c collect !*k2q k);
   s := edssolve(xcoeffs xreduce(exdfpf f,s),c);
   if length s neq 1 or null caar s then
      errdhh{"Bad solve result in derive:",s};
   s := cadr car s;
   f := pullbackpf(f,s);
   c := setdiff(c,foreach m in s collect car m);
   f := xrepartit f where xvars!* = c;
   return for each x in reverse c collect xcoeff(f,{x});
   end;


symbolic procedure allcoords f;
   % f:prefix -> allcoords:list of kernel
   % Pick up 0-form kernels in f
   makelist purge
      foreach k in (xvarspf xpartitop f where xvars!* = t) join
               if xdegree k = 0 and
            not assoc(k,depl!*) and
             not eqcar(k,'partdf) then {k}
         else if (xdegree k = 1) and exact k then {cadr k};

endmodule;

end;
