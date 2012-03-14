module transfrm;

% Cobasis transformations

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

        xform   ::= list of 1-form kernel . 1-form pf

endcomment;


fluid '(xvars!* kord!* subfg!*);
global '(!*sqvar!*);

fluid '(cfrmcob!* cfrmcrd!* cfrmdrv!* cfrmrsx!* !*edssloppy);


% Type coersions


symbolic procedure !*a2xform u;
   % u:list of equation -> !*a2xform:xform
   % Turn off subfg!* to stop let rules being applied.
   % should remove x=x entries
   for each j in getrlist u collect
      if eqexpr j then !*a2k lhs j . xpartitop rhs j where subfg!* = nil
      else rerror(eds,000,"Incorrectly formed transform");


symbolic procedure !*xform2map x;
   % x:xform -> !*xform2map:map
   foreach p in x collect
      car p . mk!*sq !*pf2sq cdr p;


symbolic procedure !*map2xform u;
   % u:map -> !*map2xform:xform
   % Turn off subfg!* to stop let rules being applied.
   (for each x in u collect
      car u . xpartitop cadr u) where subfg!* = nil;


symbolic procedure !*xform2drv x;
   % x:xform -> !*xform2drv:drv
   % Turn off subfg!* to stop let rules being applied.
   (foreach p in x collect
      {'replaceby,car p,!*pf2a cdr p}) where subfg!* = nil;


symbolic procedure !*xform2sys x;
   % x:xform -> !*xform2sys:sys
   foreach p in x collect addpf(!*k2pf car p,negpf cdr p);


% Transform


put('transform,'rtypefn,'getrtypecar);
put('transform,'cfrmfn,'transformcfrm);
put('transform,'edsfn,'transformeds);


symbolic procedure transformcfrm(m,x);
   % m:cfrm, x:list of equation -> transformcfrm:cfrm
   % Transform m using map x.
   checkcfrm xformcfrm(m,!*a2xform x);


symbolic procedure transformeds(s,x);
   % s:eds, x:list of equation -> transform:eds
   % pulls back s using map x
   xformeds(s,!*a2xform x);


symbolic procedure xformcfrm(m,x);
   % m:cfrm, x:xform -> xformcfrm:cfrm
   % Apply transform x to m, where x may be either old=f(new,old) or
   % new=f(old).
   xformcfrm1(m,car u,cadr u,caddr u)
      where u = getxform(x,cfrm_cob m);


symbolic procedure xformcfrm1(m,x,y,new);
   % m:cfrm, x,y:xform, new:cob -> xformcfrm1:cfrm
   % Apply transform x to m, where x is old=f(new,old), y is x inverse,
   % and new gives the new cobasis elements.
   begin scalar p,z;
   m := copycfrm m;
   z := pair(foreach p in x collect car p,new);
   cfrm_cob m :=            % replace old by new in-place
      foreach k in cfrm_cob m collect % sublis here destroys kernels
                                if p := atsoc(k,z) then cdr p else k;
   cfrm_crd m :=        % retain all old coordinates (may appear in eds)
       reverse union(foreach k in new join if exact k then {cadr k},
                          reverse cfrm_crd m);
   cfrm_drv m :=      % add new differentials and structure equations
      append(xformdrv(cfrm_drv m,x),
      append(!*xform2drv foreach p in x join if exact car p then {p},
             structeqns(y,x)));
   if !*edssloppy then m := updatersx m; % invxform may have added new
                                         % rsx
   m := purgecfrm m;
   return m;
   end;


symbolic procedure xformcfrm0(m,x,new);
   % m:cfrm, x:xform, new:cob -> xformcfrm0:cfrm
   % Cut down version of xformcfrm1 which doesn't update structure
   % equations. Useful when following operations are purely algebraic.
   begin scalar p,z;
   m := copycfrm m;
   z := pair(foreach p in x collect car p,new);
   cfrm_cob m :=            % replace old by new in-place
      foreach k in cfrm_cob m collect % sublis here destroys kernels
                                if p := atsoc(k,z) then cdr p else k;
   cfrm_crd m :=        % retain all old coordinates (may appear in eds)
       reverse union(foreach k in new join if exact k then {cadr k},
                          reverse cfrm_crd m);
   if !*edssloppy then m := updatersx m; % invxform may have added new
                                         % rsx
   m := purgecfrm m;
   return m;
   end;


symbolic procedure xformdrv(d,x);
   % d:drv, x:xform -> xformdrv:drv
   % Apply xform to drv. Must suppress active rules, for example if d a
   % => d b is active and x = {d b => d a}, then after applying x, it
   % will be undone immediately.
   pullbackdrv(d,!*xform2map x) where subfg!* = nil;


symbolic procedure updatersx m;
   % m:cfrm -> updatersx:cfrm
   % Reinstall restrictions in s from global variable, typically
   % after solvepfsys when !*edssloppy is t.
   begin
   m := copycfrm m;
   cfrm_rsx m := foreach f in purge cfrmrsx!* collect !*pf2a f;
   return m;
   end;


symbolic procedure xformeds(s,x);
   % s:eds, x:xform -> xformeds:eds
   % Apply transform x to m, where x may be either old=f(new,old) or
   % new=f(old).
   % possibly changes kernel order
   xformeds1(s,car u,cadr u,caddr u)
      where u = getxform(x,edscob s);



symbolic procedure xformeds1(s,x,y,new);
   % s:eds, x,y:xform, new:cob -> xformeds1:eds
   % Apply transform x to m, where x is old=f(new,old), y is x inverse,
   % and new gives the new cobasis elements.  Changes background
   % coframing.
   begin scalar k;
   s := copyeds s;
   % Transform coframing
   eds_cfrm s := xformcfrm1(eds_cfrm s,x,y,new);
   % Make sure old get eliminated (and add new to kord!* for safety)
   k := updkordl append(foreach p in x collect car p,new);
   x := !*xform2sys x;
   % Transform rest of eds
   eds_sys s := foreach f in eds_sys s collect xreduce(xreorder f,x);
   eds_ind s := foreach f in eds_ind s collect xreduce(xreorder f,x);
   remkrns s;
   s := purgeeds!* s;
   rempropeds(s,'jet0);
   foreach f in {'solved,'reduced} do rempropeds(s,f);
   setkorder k;
   s := normaleds s; % Refine this a bit?
   setcfrm eds_cfrm!* s;
   return s;
   end;


symbolic procedure xformeds0(s,x,new);
   % s:eds, x:xform, new:cob -> xformeds0:eds
   % Cut down version of xformeds1 which doesn't care about structure
   % equations (some are lost). Useful when following operations are
   % purely algebraic. Changes background coframing.
   begin scalar k;
   s := copyeds s;
   % Transform coframing (ignore structure equations)
   eds_cfrm s := xformcfrm0(eds_cfrm s,x,new);
   % Make sure old get eliminated (and add new to kord!* for safety)
   k := updkordl append(foreach p in x collect car p,new);
   x := !*xform2sys x;
   % Transform rest of eds
   eds_sys s := foreach f in eds_sys s collect xreduce(xreorder f,x);
   eds_ind s := foreach f in eds_ind s collect xreduce(xreorder f,x);
   remkrns s;
   s := purgeeds!* s;
   rempropeds(s,'jet0);
   foreach f in {'solved,'reduced} do rempropeds(s,f);
   setkorder k;
   s := normaleds s; % Refine this a bit?
   setcfrm eds_cfrm!* s;
   return s;
   end;


symbolic procedure getxform(x,cob);
   % x:xform, cob:cob -> getxform:{xform,xform,cob}
   % Analyse transform x, which may be either old=f(new,old) or
   % new=f(old). The sense is established by cob, which contains the old
   % cobasis. Return value is {x,y,new} where x is in the sense old =
   % f(new,old), and y is the inverse of x (ie new = f(old)).  The
   % inverse y is calculated only if x is old = f(new,old) and there
   % are anholonomic forms in new.
   begin scalar old,new,y;
   foreach p in x do
   << new := union(xpows cdr p,new);
      old := car p . old >>;
   if not xnp(old,cob) then  % x is new=f(old), must invert
   << y := x; x := invxform x;
      new := old; old := foreach p in x collect car p >>;
   new := sort(setdiff(new,cob),'termordp);
   edsdebug("New cobasis elements...",new,'cob);
   edsdebug("... replacing old cobasis elements",old,'cob);
   if length new neq length old or not subsetp(old,cob) then
      rerror(eds,000,"Bad cobasis transformation");
   if not allexact new and null y then
      y := invxform x; % for structure equations
   return {x,y,new};
   end;


% Structure equations


symbolic procedure xformdrveval u;
   % u:{rlist,rlist} or {rlist} -> xformdrveval:rlist
   begin scalar x,y,k;
   y := !*a2xform car u;
   x := if cdr u then !*a2xform cadr u else invxform y;
   k := updkordl foreach p in x collect car p;
   y := structeqns(y,x);
   setkorder k;
   return makelist y;
   end;

symbolic procedure xformdrveval u;
   % u:{rlist,rlist} or {rlist} -> xformdrveval:rlist
   begin scalar x,y,xvars!*;
   y := !*a2xform car u;
   x := if cdr u then !*a2xform cadr u else invxform y;
   y := structeqns(y,x);
   return makelist y;
   end;


symbolic procedure structeqns(y,x);
   % y,x:xform -> structeqns:list of rule
   % y is the inverse of x, and d lhs x are known.
   % Returns rules for d lhs y.
   begin scalar ok;
   ok := updkordl foreach p in x collect car p;
   x := !*xform2sys x;
   y := foreach p in y join
                 if not exact car p then
              {{'replaceby,
                {'d,car p},
                    !*pf2a xreduce(exdfpf cdr p,x)}};
   setkorder ok;
   return y;
   end;

symbolic procedure structeqns(y,x);
   % y,x:xform -> structeqns:list of rule
   % y is the inverse of x, and d lhs x are known.
   % Returns rules for d lhs y.
   begin scalar ok;
   ok := updkordl sort(foreach p in x collect car p,function ordop);
   x := !*xform2sys x;
   y := foreach p in y join
                 if not exact car p then
              {{'replaceby,
                {'d,car p},
                    !*pf2a xreduce(exdfpf cdr p,x)}};
   setkorder ok;
   return y;
   end;

% Inverting tranformations


put('invert,'rtypefn,'quotelist);
put('invert,'listfn,'inverteval);

symbolic procedure inverteval(u,v);
   % u:{prefix list of eqn} -> inverteval:{prefix list of eqn}
   % u is unevaluated.
   makelist foreach p in invxform !*a2xform(u := reval car u) collect
      {'equal,car p,!*pf2a1(cdr p,v)};


symbolic procedure invxform x;
   % x:xform -> invxform:xform
   % Returns inverse transformation. Selects kernels to eliminate based
   % on prevailing order
   begin scalar old,y,k, subfg!*;
   subfg!* := nil;
   foreach p in x do old := union(xpows cdr p,old);
   old := sort(old,'termordp);
   % ensure old eliminated, and add new to kord!* for safety
   k := updkordl append(old,foreach p in x collect car p);
   edsdebug("Inverting transform",nil,nil);
   y := solvepfsys1(!*xform2sys x,old);       % invert transformation
   if cadr y then
      rerror(eds,000,"Cobasis transform could not be inverted");
   setkorder k;
   return foreach f in car y collect
      lpow f . negpf xreorder red f;
   end;


symbolic procedure tmpind s;
   % s:eds -> tmpind:{eds,xform}
   % Returns s with eds_ind s all kernels, transforming to a new
   % cobasis if necessary.  Second return value is nil if no change
   % made, or the list of transformation relations.  Structure
   % equations are not transformed, so s should be closed first if
   % necessary.  NB.  Background coframing changed.
   begin scalar new,x;
   if singleterms eds_ind s then return {s,nil};
   new := foreach f in eds_ind s collect mkform!*(intern gensym(),1);
   x := invxform pair(new,eds_ind s);
   updkordl foreach p in x collect car p;
   return {xformeds0(s,x,new),x};
   end;

endmodule;

end;
