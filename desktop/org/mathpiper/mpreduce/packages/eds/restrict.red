module restrict;

% Restrict to a subset of a coframing

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


Comment. Data structures:

        rsx     ::= list of prefix (usually !*sq)
        rmap    ::= {map,rsx}

Restrictions are store in rmap's, where the second part gives the
restrictions to the coframing.

endcomment;

fluid '(xvars!* kord!* subfg!*);
global '(!*sqvar!*);

fluid '(cfrmcob!* cfrmcrd!* cfrmdrv!* cfrmrsx!*);


% Type coersions


symbolic procedure !*a2rmap u;
   % u:list of equation/inequality -> !*a2rmap:rmap
   % should remove x=x entries
   begin scalar map,rsx;
   for each j in getrlist u do
      if eqexpr j then
      << map := (!*a2k lhs j . rhs j) . map;
         rsx := addrsx(denr simp!* cdar map,rsx) >>
      else if eqcar(j := reval j,'neq) then
         rsx := addrsx(numr subtrsq(simp!* cadr j,simp!* caddr j),rsx)
      else typerr(j,"either equality or inequality");
   map := unrollmap map;
   rsx := pullbackrsx(rsx,map);
   return {map,rsx};
   end;


symbolic procedure !*rmap2a u;
   % u:rmap -> !*rmap2a:prefix
   makelist append(foreach p in car u collect {'equal,car p,cdr p},
                                foreach p in cadr u collect {'neq,p,0});


symbolic procedure !*map2rmap x;
   % x:map -> !*map2rmap:rmap
   % Pick up denominators in x
   begin scalar rsx;
   for each s in x do
      rsx := addrsx(reorder denr simp!* cdr s,rsx);
   return {x,reversip rsx};
   end;


% Restrict


if not operatorp 'neq then
   mkop 'neq; % make it an algebraic operator so it can be reval'd


put('restrict,'rtypefn,'getrtypecar);
put('restrict,'cfrmfn,'restrictcfrm);
put('restrict,'edsfn,'restricteds);
put('restrict,'listfn,'restrictlist);
put('restrict,'simpfn,'simprestrict);


symbolic procedure restrictcfrm(m,x);
   % m:cfrm, x:list of equation/inequality -> restrictcfrm:cfrm
   % restricts m using rmap x
   restrictcfrm1(m,!*a2rmap x);


symbolic procedure restricteds(s,x);
   % s:eds, x:list of equation/inequality -> restrict:eds
   % restricts s using rmap x
   restrict(s,!*a2rmap x);


symbolic procedure restrictlist(u,v);
   % u:{prefix list of prefix,prefix list of equation/inequality}, v:bool
   % -> restrictlist:prefix list of prefix
   begin scalar x;
   x := car !*a2rmap reval cadr u;
   u := reval car u;
   return
      makelist foreach f in cdr u join
               if (f := !*pf2a1(restrictpf(xpartitop f,x),v)) neq 0 then {f};
   end;


symbolic procedure simprestrict u;
   % u:{prefix,prefix list of equation/inequality} -> simprestrict:sq
   % just ignores inequalities
   !*pf2sq repartit restrictpf(f,x)
      where f = xpartitop car u,
            x = car !*a2rmap reval cadr u;


symbolic procedure restrictcfrm1(m,x);
   % m:cfrm, x:rmap -> restrictcfrm:cfrm
   begin scalar kl,rl;
   if null car x and null cadr x then return m;
   m := copycfrm m;
   kl := union(!*map2cob car x,!*rsx2cob cadr x);
   % Get rsx restrictions from denominators of map part
   rl := purge foreach p in car x join
                  if not cfrmconstant(p := denr simp!* cdr p) then
                {mk!*sq !*f2q p};
   % Put all rsx together and restrict
   rl := append(cfrm_rsx m,append(cadr x,rl));
   cfrm_rsx m := pullbackrsx(rl,car x);
   if not subsetp(kl,cfrm_cob m) or 0 member cfrm_rsx m then
      rerror(eds,000,
             "Map image not within target coframing in restrict");
   % Restrict derivatives
   cfrm_drv m := restrictdrv(cfrm_drv m,car x);
   return purgecfrm m;
   end;


symbolic procedure !*map2cob x;
   % x:map -> !*map2cob:cob
   % Collect all 1-form variables in map x.
   begin scalar f,kl;
   foreach p in x do
   << f := simp!* cdr p;
      f := foreach k in union(kernels denr f,kernels numr f) join
              if exformp k then xpows exdfk k;
      f := append(xpows exdfk car p,f);
      kl := union(f,kl) >>;
   return kl;
   end;


symbolic procedure !*rsx2cob x;
   % x:rsx -> !*rsx2cob:cob
   % Collect all 1-form variables in restrictions x.
   begin scalar f,kl;
   foreach p in x do
   << f := simp!* p;
      f := foreach k in union(kernels denr f,kernels numr f) join
              if exformp k then xpows exdfk k;
      kl := union(f,kl) >>;
   return kl;
   end;


symbolic procedure restrictdrv(d,x);
   % d:drv, x:map -> restrictdrv:drv
   (foreach r in d collect
      {car r,cadr r,mk!*sq restrictsq(simp!* caddr r,x)})
          ; %%% where subfg!*=nil; %%% Why?


symbolic procedure restrictsq(q,x);
   % q:sq, x:map -> restrictsq:sq
    !*pf2sq restrictpf(xpartitsq q,x);


symbolic procedure restrict(s,x);
   % s:eds, x:rmap -> restrict:eds
   % restricts s using rmap x
   begin
   if null car x and null cadr x then return s;
   % Do coframing first (spot errors faster)
   s := copyeds s;
   eds_cfrm s := restrictcfrm1(eds_cfrm s,x);
   % Fix flags
   s := purgeeds!* s;
   foreach f in {'solved,'pfaffian,'quasilinear,'closed} do
      remfalseeds(s,f);
   rempropeds(s,'involutive);
   remkrns s;
   % Form new eds
   eds_sys s := foreach f in eds_sys s join
      if f := restrictpf(f,car x) then {f};
   eds_ind s := foreach f in eds_ind s join
      if f := restrictpf(f,car x) then {f}
      else <<edsverbose(
                "Restriction inconsistent with independence condition",
                nil,nil);>>;
   return normaleds s;
   end;


symbolic procedure restrictpf(f,x);
   % f:pf, x:map -> restrictpf:pf
   % restricts f using map x
   % should watch out for partdf's
   if null f then nil
   else if null x then f        % doesn't check let rules
   else (if numr c then lpow f .* c .+ restrictpf(red f,x)
         else restrictpf(red f,x)) where c = subsq(lc f,x);


symbolic procedure pullbackrsx(rsx,x);
   % rsx:rsx, x:map -> pullbackrsx:rsx
   foreach p in rsx collect mk!*sq subf(numr simp!* p,x);

endmodule;

end;
