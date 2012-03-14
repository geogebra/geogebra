module pullback;

% Pullback transformations

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


Comment. Data structure:

        map     ::= list of kernel . prefix

endcomment;

fluid '(xvars!* kord!* subfg!*);
global '(!*sqvar!*);

fluid '(cfrmcob!* cfrmcrd!* cfrmdrv!* cfrmrsx!*);


% Type coersions


symbolic procedure !*a2map u;
   % u:list of equation -> !*a2map:map
   % should remove x=x entries
   unrollmap for each j in getrlist u collect
      if eqexpr j then !*a2k lhs j . rhs j
      else rerror(eds,000,"Incorrectly formed pullback map");


symbolic procedure !*map2a u;
   % u:map -> !*map2a:prefix
   makelist foreach p in u collect {'equal,car p,cdr p};


% Pullback


put('pullback,'rtypefn,'getrtypecar);
put('pullback,'cfrmfn,'pullbackcfrm);
put('pullback,'edsfn,'pullbackeds);
put('pullback,'listfn,'pullbacklist);
put('pullback,'simpfn,'simppullback);


symbolic procedure pullbackcfrm(m,x);
   % m:cfrm, x:list of equation|inequality -> pullbackcfrm:cfrm
   % pullback m using map x
   begin
   x := !*a2rmap x;
   m := car pullbackcfrm1(m,car x);
   return if cadr x then cfrmprotect{'restrictcfrm1,m,{{},cadr x}}
           else m
   end;


symbolic procedure pullbackeds(s,x);
   % s:eds, x:list of equation|inequality -> pullback:eds
   % pulls back s using rmap x
   pullback0(s,!*a2rmap x);


symbolic procedure pullbacklist(u,v);
   % u:{prefix list of prefix ,prefix list of equation|inequality}, v:bool
   % -> pullbacklist:prefix list of prefix
   begin scalar x;
   x := car !*a2rmap reval cadr u; % throw away rsx
   u := reval car u;
   return
      makelist foreach f in cdr u join
               if (f := !*pf2a1(pullbackpf(xpartitop f,x),v)) neq 0 then {f};
   end;


symbolic procedure simppullback u;
   % u:{prefix,prefix list of equation} -> simppullback:sq
   (if degreepf f < 0 then rerror(eds,000,"Cannot pull back vectors")
   else !*pf2sq repartit pullbackpf(f,x))
      where f = xpartitop car u,
            x = car !*a2rmap reval cadr u;


symbolic procedure pullbackcfrm1(m,x);
   % m:cfrm, x:map -> pullbackcfrm1:{cfrm,{cob,map}}
   % Pull back coframing m. Also returns extended map showing which
   % cobasis elements have been eliminated in case of ambiguity (e.g.
   % anholonomic cobases).
   begin scalar n,cfrmcrd!*,cfrmrsx!*;
   if null x then return m;
   m := copycfrm m;
   x := unrollmap x;
   % Get source coframing (or subcoframing thereof)
   n := !*map2srccfrm x;
   %%if xnp(foreach p in x collect car p,cfrm_crd n) then
   %%   rerror(eds,000,"Recursive map in pullback");
   % New coordinates (ordering here critical)
   cfrm_crd m := rightunion(cfrm_crd m,cfrm_crd n);
   cfrm_crd m := setdiff(cfrm_crd m,foreach p in x collect car p);
   % Pull back rsx and check (ordering here critical)
   cfrm_rsx m := rightunion(cfrm_rsx m,cfrm_rsx n);
   cfrm_rsx m := pullbackrsx(cfrm_rsx m,x);
   if 0 member cfrm_rsx m then
      rerror(eds,000,
             "Map image not within target coframing in pullback");
   % Get target cobasis, and differentiate appropriate part of map
   % Need to use new coframing's coordinates
   cfrmcrd!* := cfrm_crd m;
   cfrmrsx!* := (foreach p in cfrm_rsx m collect
                               xpartitop p) where xvars!* = cfrmcrd!*;
   x := !*map2cotangent x;
   if not subsetp(car x,cfrm_cob m) then
      rerror(eds,000,
             "Map image not within target coframing in pullback");
   % New cobasis (ordering here critical)
   cfrm_cob m := rightunion(cfrm_cob m,cfrm_cob n);
   cfrm_cob m := setdiff(cfrm_cob m,car x);
   % Pullback derivatives (ordering here critical)
   cfrm_drv m := rightunion(cfrm_drv m,cfrm_drv n);
   cfrm_drv m := pullbackdrv(cfrm_drv m,cadr x);
   return {purgecfrm m,x};
   end;


symbolic procedure unrollmap x;
   % x:map -> unrollmap:map
   % Straighten out recursive maps. Designed to work only for weakly
   % reduced maps (ie row-echelon form).
   begin scalar r,z,cfrmcrd!*; integer c;
   cfrmcrd!* := foreach p in x collect car p;
   %%%edsdebug("Unroll input",x,'map);
   while x and (c := c+1) < 20 do
      begin
      foreach p in x do
      << r := simp!* cdr p;
               if cfrmconstant numr r and cfrmconstant denr r then
            z := p . z >>;
      x := pullbackmap(setdiff(x,z),append(x,z));
      %%%edsdebug("Recursive part",x,'map);
      end;
   if x then rerror(eds,000,"Recursive map");
   return z;
   end;


symbolic procedure !*map2srccfrm x;
   % x:map -> !*map2srccfrm:cfrm
   % Determine a possible source coframing for map x by
   % inspecting the rhs of each rule.
   !*sys2cfrm foreach p in x collect
          (1 .* simp!* cdr p .+ nil);


symbolic procedure !*map2cotangent x;
   % x:map -> !*map2cotangent:{cob,map}
   % Differentiate map x and determine which cobasis elements are
   % eliminated (ambiguous for anholonomic frames). Also returns
   % differentiated map.
   begin scalar f,old,xl;
   foreach p in x do
   << f := xpows exdfk car p;
      if length f > 1 or car f neq {'d,car p} then xl := p . xl
      else old := car f . old >>;
   if xl then x := exdfmap(xl,x);
   old := append(old,for each p in x
                        join if xdegree car p = 1 then {car p});
   edsdebug("Cobasis elements eliminated",old,'cob);
   return {old,x};
   end;


symbolic procedure exdfmap(xl,x);
   % xl,x:map -> exdfmap:map
   % produce substitution for differentials in xl from those for scalars
   % x is the whole map, xl is usually only a subset
   begin scalar f,old,y,ok;
   ok := updkordl {};
   foreach p in xl do
   << f := exdfk car p;
      old := union(xpows f,old);
      if red f or lpow f neq {'d,car p} then
         f := pullbackpf(f,x);
      y := addpf(f,negpf pullbackpf(xpartitop{'d,cdr p},x)) . y >>;
   edsdebug("Possibilities for elimination",old,'cob);
   y := solvepfsys1(y,old);
   if cadr y then
      rerror(eds,000,"Cannot determine suitable coframe for pullback");
   setkorder ok;
   return
      append(x,
                   foreach f in car y collect
                 lpow f . mk!*sq !*pf2sq negpf xreorder!* red f);
   end;


symbolic procedure pullbackdrv(d,x);
   % d:drv, x:map -> pullbackdrv:drv
   (foreach r in d collect
      {car r,cadr r,mk!*sq subsq(simp!* caddr r,x)})
          ; %%% where subfg!*=nil; %%% Why?


symbolic procedure pullbackmap(p,x);
   % p:map, x:map -> pullbackmap:map
   % substitute map x into map p
   foreach s in p collect car s . mk!*sq subsq(simp!* cdr s,x);


symbolic procedure pullback0(s,x);
   % s:eds, x:rmap -> pullback0:eds
   % restricts and pulls back s using rmap x
   if emptyedsp(s := pullback(s,car x)) then s
   else if cadr x then edscall restrict(s,{{},cadr x})
   else s;


symbolic procedure pullback(s,x);
   % s:eds, x:map -> pullback:eds
   % Pulls back s using map x.
   begin scalar prl,cob,m;
   if null x then return s;
   % Get some information about s
   prl := prlkrns s; cob := edscob s;
   % Pullback coframing, and get cotangent space info
   m := pullbackcfrm1(eds_cfrm s,x);
   x := cadr m; m:= car m;
   % Setting coframe here reduces need to reorder later. If some
   % cobasis elements are eliminated, the forms in sys and ind may be
   % out of order, but this doesn't seem to matter since these will be
   % replaced anyway.
   setcfrm m;
   % Fix flags first (need to test using old sys/ind)
   s := purgeeds!* s; % copies s
   if not subsetp(cfrm_cob m,cob) then
      rempropeds(s,'jet0);
   if subsetp(car x,prl) and % try to avoid re-solving
      null xnp(prl,foreach f in pfaffpart eds_sys s join xpows f) and
      null xnp(prl,foreach f in eds_ind s join xpows f) then
      remtrueeds(s,'reduced)
   else
      remtrueeds(s,'solved);
   foreach f in {'solved,'pfaffian,'quasilinear,'closed} do
      remfalseeds(s,f);
   rempropeds(s,'involutive);
   % Form new eds
   eds_sys s := foreach f in eds_sys s join
      if f := pullbackpf(f,cadr x) then {f};
   eds_ind s := foreach f in eds_ind s join
      if f := pullbackpf(f,cadr x) then {f}
      else <<edsverbose(
                "Pullback inconsistent with independence condition",
                nil,nil);>>;
   cfrm_cob m := append(setdiff(cfrm_cob m,i),i) where i=indkrns s;
   eds_cfrm s := m;
   if not subsetp(cfrm_cob m,cob) then % probably need to reorder
   << setcfrm m;
      eds_sys s := xreordersys eds_sys s;
      eds_ind s := xreordersys eds_ind s; >>;
   remkrns s;
   return normaleds s;
   end;


symbolic procedure pullbackpf(f,x);
   % f:pf, x:map -> pullbackpf:pf
   % pulls back f using map x
   % should watch out for partdf's
   % This version assumes x introduces no new xvars in coefficients.
   % Done using two routines to reduce subs2 checking.
   subs2pf pullbackpf1(f,x);


symbolic procedure pullbackpf1(f,x);
   % f:pf, x:map -> pullbackpf1:pf
   if f then
      addpf(multpfsq(pullbackk(lpow f,x),subsq(lc f,x)),
            pullbackpf1(red f,x));


symbolic procedure pullbackk(k,x);
   % k:lpow pf, x:map -> pullbackk:pf
   % need xreorder here because subf returns unordered wedge kernels
   xreorder xpartitsq subf(!*k2f k,x);


symbolic procedure pullbacksq(q,x);
   % q:sq, x:map -> pullbacksq:pf
   xpartitsq subsq(q,x);


endmodule;

end;
