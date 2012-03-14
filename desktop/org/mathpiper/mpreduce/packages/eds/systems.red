module systems;

% Operations on exterior differential systems

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


fluid '(kord!* xtruncate!* !*arbvars !*edssloppy cfrmcob!*);
global '(indxl!*);


symbolic procedure copyeds s;
   % s:eds -> copyeds:eds
   % Copy s to allow destructive operations using selectors
   append(s,{});



put('augment,'rtypefn,'getrtypecar);
put('augment,'edsfn,'augmenteds);

symbolic procedure augmenteds(s,u);
   % s:eds, u:prefix sys -> augmenteds:eds
   begin
   u := makelist getrlist u;
   u := !*a2sys u;
   s := augmentsys(s,u);
   foreach f in {'pfaffian,'closed,'quasilinear,'involutive} do
      rempropeds(s,f);
   return checkeds s; % removes all hidden flags, adds new rsx
   end;


symbolic procedure augmentsys(s,u);
   % s:eds, u:sys -> augmentsys:eds
   % Augment system by adding new forms, using ordering of s on input in
   % final sort. Doesn't change flags or check integrity.
   begin scalar c;
   s := copyeds s;
   eds_sys s := sortsys(union(u,eds_sys s),edscob s);
   return s;
   end;


put('quasilinear,'psopfn,'quasilineareval);

symbolic procedure quasilineareval s;
   % s:{eds} -> quasilineareval:0 or 1
   if edsp(s := reval car s) then
      if knowntrueeds(s,'quasilinear) or
               not knownfalseeds(s,'quasilinear) and
               edscall quasilinearp s then 1 else 0
   else typerr(s,'eds);


symbolic procedure quasilinearp s;
   % s:eds -> quasilinearp:bool
   % Test whether (closure of) system is quasilinear
   knowntrueeds(s,'quasilinear) or
      not knownfalseeds(s,'quasilinear) and
      if not normaledsp s then
               rerror(eds,000,{"System not in normal form in quasilinearp"})
      else if null scalarpart eds_sys s and
              quasilinearsys(nonpfaffpart eds_sys closure s,prlkrns s)
       then << flagtrueeds(s,'quasilinear); t >>
      else
            << flagfalseeds(s,'quasilinear); nil >>;


symbolic procedure quasilinearsys(s,prl);
   % s:sys, prl:list of 1-form kernel -> quasilinearsys:bool
   % Systems with 0-forms are non-linear by definition here.
   null cadr lineargens(s,{},prl);


symbolic procedure lineargenerators s;
   % s:eds -> lineargenerators:eds
   % Makes linearly generated part of s explicitly linear.
   begin scalar p;
   p := pfaffpart eds_sys s;
   p := append(p,append(car q,cadr q))
                 where q = lineargens(setdiff(eds_sys s,p),{},prlkrns s);
   if p = eds_sys s then return s;
   s := copyeds s;
   eds_sys s := p;
   return sorteds s;
   end;


symbolic procedure lineargens(s,g,prl);
   % s,g:sys, prl:list of 1-form kernel -> lineargens:{sys,sys}
   % g is a GB for a linear system, s is fully reduced wrt g. Returns as
   % linear a set of generators as possible.  For a linear system,
   % returns a linear set of generators.  Recursively checks if
   % non-linear part of s can be reduced mod linear part + g to give a
   % linear system.  Systems with 0-forms are non-linear by definition
   % here.
   begin scalar w,xtruncate!*; integer d;
   foreach f in s do
   << d := max(d,degreepf f);
      if degreepf f neq 0 and quasilinearpf(f,prl) then
          w := f . w >>;
   w := reversip w;
   s := setdiff(s,w);
   if null s then return {w,{}};
   if null w then return {{},s};
   xtruncate!* := d;
   g := xidealpf append(g,w);
   s := foreach f in s join
           if f := xreduce(f,g) then {f};
   return {append(w,car p),cadr p} where p = lineargens(s,g,prl);
   end;


symbolic procedure quasilinearpf(f,p);
   % f:pf, p:list of 1-form kernel -> quasilinearpf:bool
   % result is t if f is at most linear in p
   if null f then t
   else length intersection(wedgefax lpow f,p) <= 1
        and quasilinearpf(red f,p);


put('semilinear,'psopfn,'semilineareval);

symbolic procedure semilineareval s;
   % s:{eds} -> semilineareval:0 or 1
   if edsp(s := reval car s) then
      if edscall semilinearp s then 1 else 0
   else typerr(s,'eds);


symbolic procedure semilinearp s;
   % s:eds -> semilinearp:bool
   % Test whether (closure of) system is semilinear
   if not normaledsp s then nil
   else if !*edssloppy then edscall quasilinearp s
   else semilinearsys(nonpfaffpart eds_sys edscall closure s,prlkrns s);


symbolic procedure semilinearsys(s,prl);
   % s:sys, prl:list of 1-form kernel -> semilinearsys:bool
   % 0-forms are non-linear by definition here.
   null s or
      degreepf car s neq 0 and
      semilinearpf(car s,prl) and
      semilinearsys(cdr s,prl);


symbolic procedure semilinearpf(f,p);
   % f:pf, p:list of 1-form kernel -> semilinearpf:bool
   % Works when xvars!* allows 0-forms as well - used in solvegraded.
   % result is t if f is at most linear in p with constant coefficient
   null f or
      (l = 0 or
                l = 1 and
          cfrmconstant numr lc f and
          cfrmconstant denr lc f
            where l = length foreach k in wedgefax lpow f join
                                                   if k memq p then {k}) and
      semilinearpf(red f,p);


put('pfaffian,'psopfn,'pfaffianeval);

symbolic procedure pfaffianeval s;
   % s:{eds} -> pfaffianeval:0 or 1
   if edsp(s := reval car s) then
      if knowntrueeds(s,'pfaffian) or
               not knownfalseeds(s,'pfaffian) and
               edscall pfaffian s then 1 else 0
   else typerr(s,'eds);


symbolic procedure pfaffian s;
   % s:eds -> pfaffian:bool
   knowntrueeds(s,'pfaffian) or
      not knownfalseeds(s,'pfaffian) and
      if not normaledsp s then
               rerror(eds,000,{"System not in normal form in pfaffian"})
      else if pfaffsys eds_sys s then
      << flagtrueeds(s,'pfaffian); t>>
      else
      << flagfalseeds(s,'pfaffian); nil>>;


symbolic procedure pfaffsys s;
   % s:sys -> pfaffsys:bool
   % Systems with 0-forms are non-Pfaffian by definition here.
   begin scalar p,xtruncate!*; integer d;
   if scalarpart s then return nil;
   foreach f in s do
   << d := max(d,degreepf f);
      if degreepf f = 1 then p := f . p >>;
   s := setdiff(s,p);
   if null s then return t;
   if null p then return nil;
   xtruncate!* := d;
   p := xidealpf foreach f in p collect xreduce(exdfpf f,p);
   while s and null xreduce(car s,p) do s := cdr s;
   return null s;
   end;


put('closure,'edsfn,'closure);
put('closure,'rtypefn,'getrtypecar);

symbolic procedure closure s;
   % s:eds -> closure:eds
   begin scalar p,sys,s0; integer d;
   if knowntrueeds(s,'closed) then return s;
   if s0 := geteds(s,'closure) then return s0;
%%%   if not normaledsp s then
%%%      rerror(eds,000,{"System not in normal form in closure"});
%%%   if scalarpart eds_sys s then
%%%      rerror(eds,000,{"Closure with 0-forms not yet implemented"});
   if scalarpart eds_sys s then
      lprim {"0-forms in closure: result may not be closed"};
   d := length eds_ind s;
   p := solvedpart eds_sys s;
   sys := foreach f in eds_sys s join
             if degreepf f < d and
                (f := xreduce(xreorder exdfpf f,p)) then {f};
   if null sys then return <<flagtrueeds(s,'closed); s>>;
   s0 := augmentsys(s,sys);
   if pfaffpart sys then rempropeds(s0,'solved);
   flagtrueeds(s0,'closed);
   s0 := normaleds s0;        % might add 0-forms or become inconsistent
   return if emptyedsp s0 then s0
      else if scalarpart eds_sys s0 then s0
      else <<puteds(s,'closure,s0); s0>>;
   end;

flag('(closure),'hidden);


% symbolic operator closed;
% symbolic procedure closed u;
%    % u:eds|rlist of prefix|prefix -> closed:bool
%    % True if u is a closed eds, a closed system of forms or a closed
%    % form
%    if edsp u then
%       knowntrueeds(u,'closed) or edscall closededs u
%    else if rlistp u then
%       closedsys foreach f in getrlist u collect xpartitop f
%    else null exdfpf xpartitop u;

% flag('(closed),'boolean);

% symbolic procedure closededs s;
%    % s:eds -> closededs:bool
%    knowntrueeds(s,'closed) or
%    if closedsys eds_sys s then
%    << flagtrueeds(s,'closed); t>>;



put('closed,'psopfn,'closedeval);

symbolic procedure closedeval s;
   % s:{eds} -> closedeval:0 or 1
   if edsp(s := reval car s) then
      if knowntrueeds(s,'closed) or
               not knownfalseeds(s,'closed) and
         edscall closed s then 1 else 0
   else if rlistp s then
      if closedsys foreach f in getrlist s collect xpartitop f then 1
      else 0
   else if null exdfpf xpartitop s then 1 else 0;


symbolic procedure closed s;
   % s:eds -> closed:bool
   knowntrueeds(s,'closed) or
      not knownfalseeds(s,'closed) and
      if closedsys eds_sys s then
      << flagtrueeds(s,'closed); t>>
      else
      << flagfalseeds(s,'closed); nil>>;


symbolic procedure closedsys s;
   % s:sys -> closedsys:bool
   begin scalar p,xtruncate!*; integer d;
   foreach f in s do
   << d := max(d,1 + degreepf f);
      f := xreduce(exdfpf f,s);
      if f then p := f . p >>;
   if null p then return t;
   xtruncate!* := d;
   s := xidealpf s;
   while p and null xreduce(car p,s) do p := cdr p;
   return null p;
   end;


symbolic operator frobenius;
symbolic procedure frobenius u;
   % u:eds|rlist of prefix|prefix -> frobenius:bool
   % True if u is an eds or list of forms generated by 1-forms
   % satisfying the Frobenius test
   if edsp u then
      null nonpfaffpart eds_sys u and
      null nonpfaffpart eds_sys edscall closure u
   else if rlistp u then
      frobeniussys foreach f in getrlist u collect xpartitop f
   else rerror(eds,000,"Invalid argument to frobenius");

flag('(frobenius),'boolean);


symbolic procedure frobeniussys s;
   % s:sys -> frobeniussys:bool
   begin scalar p;
   p := pfaffpart s;
   s := union(foreach f in p collect exdfpf f,setdiff(s,p));
   p := xautoreduce p;
   while s and null xreduce(car s,p) do s := cdr s;
   return null s;
   end;


put('cauchy_system,'rtypefn,'quotelist);
put('cauchy_system,'listfn,'evalcauchysys);

symbolic procedure evalcauchysys(u,v);
   % u:{prefix}, v:bool -> evalcauchysys:rlist
   if xedsp(u := reval car u) then
      evalcartansys({edscall closure u},v)
   else if rlistp u then
      evalcartansys({append(u,foreach f in cdr u collect aeval{'d,f})},v)
   else
      evalcartansys({makelist {u,aeval{'d,u}}},v);


put('cartan_system,'rtypefn,'quotelist);
put('cartan_system,'listfn,'evalcartansys);

symbolic procedure evalcartansys(u,v);
   % u:{prefix}, v:bool -> evalcartansys:rlist
   if xedsp(u := reval car u) then
      if edsp u then !*sys2a1(edscall cartansyseds u,v)
      else makelist for each s in cdr u
                       collect !*sys2a1(edscall cartansyseds u,v)
   else if rlistp u then
      !*sys2a1(cartansys !*a2sys u,v)
   else
      !*sys2a1(cartansyspf xpartitop u,v);


symbolic procedure cartansys u;
   % u:list of pf -> cartansys:list of pf
   begin scalar xtruncate!*;
   xtruncate!* := eval('max.foreach f in u collect degreepf f);
   xtruncate!* := xtruncate!* - 1;
   u := xidealpf u;
   return reversip xautoreduce purge foreach f in u join cartansyspf f;
   end;


symbolic procedure cartansyspf f;
   % f:pf -> cartansyspf:list of pf
   begin scalar x,p,q,z;
   if degreepf f = 1 then return {f};
   while f do
      begin
      p := wedgefax lpow f;
      foreach k in p do
         if not((q := delete(k,p)) member z) then
         << z := q . z;
            x := xcoeff(f,q) . x >>;
      f := red f;
      end;
   return reverse xautoreduce purge x;
   end;


symbolic procedure cartansyseds s;
   % s:eds -> cartansyseds:sys
   cartansys eds_sys s;


put('linearise,'edsfn,'lineariseeds);
put('linearise,'rtypefn,'quoteeds);
put('linearize,'edsfn,'lineariseeds);
put('linearize,'rtypefn,'quoteeds);
flag('(linearise linearize),'nospread);

symbolic procedure lineariseeds u;
   % u:{eds[,rlist]} -> lineariseeds:eds
   begin scalar x;
   if null u or length u > 2 then
      rerror(eds,000,{"Wrong number of arguments to linearise"});
   if cdr u then x := !*a2sys cadr u;
   if nonpfaffpart x then typerr(cadr u,"integral element");
   return edscall linearise(car u,x);
   end;


symbolic procedure linearise(s,x);
   % s:eds, x:sys -> linearise:eds
   % x is an integral element of s, result is linearisation of s at x
   % in original cobasis.
   if quasilinearp s then lineargenerators s else
   begin scalar xx,q,prl;
   s := copyeds closure s;
   x := xreordersys x;
   q := nonpfaffpart eds_sys s;
   prl := prlkrns s;
   % pick out those products which occur
   xx := purge foreach f in q join
                  foreach k in xpows f join
               nonlinfax intersection(wedgefax k,prl);
   % form the relevant poducts from x
   x := pair(lpows x,x);
   xx := foreach pr in xx collect
                  wedgepf(cdr atsoc(car pr,x),cdr atsoc(cadr pr,x));
   % reduce the system mod x^x
   eds_sys s := append(setdiff(eds_sys s,q),
                      foreach f in q join if f := xreduce(f,xx) then {f});
   flagtrueeds(s,'quasilinear);
   return s;
   end;


symbolic procedure nonlinfax l;
   % l:list of kernel -> nonlinfax:list of list of 2 kernel
   % Collect elements of l pairwise, discarding any odd element.
   if length l > 1 then {car l,cadr l} . nonlinfax cddr l;

%% symbolic procedure linearise(s,x);
%%    % s:eds, x:sys -> linearise:eds
%%    % x is an integral element of s, result is linearisation of s at x
%%    % in original cobasis.
%%    % NB Changes background coframing.
%%    if quasilinearp s then lineargenerators s else
%%    begin scalar s1;
%%    s1 := linearise0(s,x);
%%    x := cadr s1;
%%    s1 := car s1;
%%    return xformeds0(s1,x,setdiff(edscob s,edscob s1));
%%    end;
%%
%%
%% symbolic procedure linearise0(s,x);
%%    % s:eds, x:sys -> linearise0:{eds,xform}
%%    % x is an integral element of s, result is linearisation of s at x
%%    % in a cobasis based on x, together with transform required for
%%    % original cobasis. The structure equations are NOT updated.
%%    % NB Changes background coframing.
%%    begin scalar c,y,prl;
%%    c := foreach f in x collect mkform!*(intern gensym(),1);
%%    x := pair(c,x);
%%    y := invxform x;
%%    s := copyeds closure s;
%%    s := tmpind xformeds0(s,y,c);
%%    x := append(x,cadr s);
%%    s := car s;
%%    prl := prlkrns s;
%%    eds_sys s := foreach f in eds_sys s join
%%                               if degreepf f < 2 then {f}
%%                  else if inhomogeneouspart(f,prl) then
%%                     typerr(!*sys2a foreach p in x collect cdr p,
%%                            "integral element")
%%                  else if f := linearpart(f,prl) then {f};
%%    flagtrueeds(s,'quasilinear);
%%    return {s,x};
%%    end;


put('one_forms,'rtypefn,'quotelist);
put('one_forms,'listfn,'oneformseval);

symbolic procedure oneformseval(u,v);
   % u:{xeds|rlist}, v:bool -> oneformseds:rlist
   if edsp(u := reval car u) then
      !*sys2a1(pfaffpart eds_sys u,v)
   else if xedsp u then
      makelist foreach s in getrlist u collect
         !*sys2a1(pfaffpart eds_sys s,v)
   else
      makelist foreach f in getrlist u join
         if reval{'exdegree,f}=1 then {f};


put('zero_forms,'rtypefn,'quotelist);
put('zero_forms,'listfn,'zeroformseval);
put('nought_forms,'rtypefn,'quotelist);
put('nought_forms,'listfn,'zeroformseval);

symbolic procedure zeroformseval(u,v);
   % u:{xeds|rlist}, v:bool -> zeroformseval:rlist
   if edsp(u := reval car u) then
      !*sys2a1(scalarpart eds_sys u,v)
   else if xedsp u then
      makelist foreach s in getrlist u collect
         !*sys2a1(scalarpart eds_sys s,v)
   else
      makelist foreach f in getrlist u join
         if reval{'exdegree,f}=0 then {f};

endmodule;

end;
