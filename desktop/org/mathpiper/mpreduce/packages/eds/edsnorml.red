module edsnormal;

% Converting exterior systems to internal form

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


Comment. The next section contains routines for putting an EDS into
"normal" form. An EDS S is in "normal" form if

***   1) S contains no 0-forms,                *** removed 27/4/95
   2) the 1-forms {theta(a)} in S satisfy
               lc theta(a) = 1
         lpow theta(a) in xpows theta(b) iff a = b.
   3) the 1-forms {omega(i)} in eds_ind S are reduced mod {theta(a)}
      and satisfy
               trc omega(i) = 1
         trpow omega(i) in xpows omega(j) iff i = j
      where trc/trpow mean trailing coefficient/power.
   4) S\{theta(a)} is in normal form mod {theta(a)}.

If S satisfies 1, it is "generated in positive degree". If S satisfies
2, and 3, it is in "solved" form. If S satisfies 4, it is "reduced".

endcomment;

fluid '(cfrmrsx!* !*edssloppy pullback_maps xvars!* kord!*);


symbolic procedure normaleds s;
   % s:eds -> normaleds:eds
   % Bring s into normal form as far as possible.
   if normaledsp s then s
%   else if emptyedsp(s := solvededs s) then s
%   else positiveeds sorteds reducededs s;
   else sorteds reducededs solvededs s;


symbolic procedure normaledsp s;
   % s:eds -> normaledsp:bool
   solvededsp s and
   reducededsp s;
%   null scalarpart eds_sys s;


put('lift,'edsfn,'positiveeds);
put('lift,'rtypefn,'getrtypecar);

symbolic procedure positiveeds s;
   % s:eds -> positiveeds:xeds
   % Bring s into positive form as far as possible.
   begin scalar v,c,s1;
   v := foreach f in scalarpart eds_sys s collect lc f;
   if null v then return s;
   edsverbose("Solving 0-forms",nil,nil);
   eds_sys s := setdiff(eds_sys s,v);
   c := reverse setdiff(edscrd s,edsindcrd s);
   c := edsgradecoords(c,geteds(s,'jet0));
   v := edssolvegraded(v,c,cfrm_rsx eds_cfrm s);
   s := purgexeds makelist
      if null v then
               << edsverbose("System inconsistent",nil,nil); {}>>
      else foreach strata in v collect
          if null car strata then
               << edsverbose("Couldn't solve 0-forms",cdr strata,'sq);
            strata := foreach q in cdr strata collect 1 .* q .+ nil;
            augmentsys(s,strata) >>
         else
         << edsverbose("New equations:",cadr strata,'map);
%%%         pullback_maps:= append(pullback_maps,{!*rmap2a cdr strata});
            s1 := pullback0(s,cdr strata);           % might add 0-forms
            if null scalarpart eds_sys s1 then s1
            else edscall positiveeds s1 >>;          % so go round again
   return s;
   end;


flag('(reduced solved),'hidden); % non-printing and purgeable


symbolic procedure reducededs s;
   % s:eds -> reducededs:eds
   % Bring s into reduced form as far as possible.
   % Changes background coframing.
   if knowntrueeds(s,'reduced) then s else
   begin scalar m,p,q;
   m := setcfrm eds_cfrm!* s;
   p := solvedpart pfaffpart eds_sys s;
   q := foreach f in setdiff(eds_sys s,p) join
                 if f := xreduce(f,p) then
               {if cfrmnowherezero numr lc f then xnormalise f else f};
   eds_sys s := append(p,q);
   flagtrueeds(s,'reduced);
   return s;
   end;


symbolic procedure reducededsp s;
   % s:eds -> reducededsp:bool
   knowntrueeds(s,'reduced);


symbolic procedure solvededs s;
   % s:eds -> solvededs:eds
   % Bring s into solved form as far as possible.
   % Local variables:
   %    m  - coframing for s
   %    n  - external background coframing
   %    p  - solved part of 1-forms in s
   %    q  - unsolved part of 1-forms in s
   %    z  - 0-forms picked up from 1-forms in s
   %    i  - independence 1-forms
   %    ik - independent kernels (cf indkrns)
   %    dk - dependent kernels (cf depkrns)
   %    pk - principal kernels (cf prlkrns)
   %    kl - cobasis (cf edscob)
   if knowneds(s,'solved) then s else
   begin scalar m,n,p,q,z,i,ik,dk,pk,kl;
   m := copycfrm eds_cfrm!* s;
   % Set up coframing and initial ordering
   i := xautoreduce eds_ind s;   % check if indkrns are obvious
   i := if !*edssloppy or singleterms i then reverse lpows i else {};
   kl := append(setdiff(cfrm_cob m,i),i);
   cfrm_cob m := kl;
   n := setcfrm m;
   % Put 1-forms into solved form as far as possible
   edsdebug("Solving Pfaffian subsystem",nil,nil);
   q := solvepfsys1(pfaffpart eds_sys s,
                                 if !*edssloppy then setdiff(cfrm_cob m,i));
   p := car q;
   dk := lpows p;
   % Put independence 1-forms into solved form mod p
   edsdebug("Solving independence forms",nil,nil);
   i := solvepfsys1(foreach f in eds_ind s join
                                           if f := xreduce(xreorder f,p) then {f},
                      if !*edssloppy then i);
   if length eds_ind s > length car i + length cadr i then
      return <<edsverbose("System inconsistent",nil,nil);
               setcfrm n;
                emptyeds()>>;
   ik := lpows car i;
   % Check for f(i)*omega(i) 1-forms from q
   q := foreach f in cadr q join
                 if xreduce(f := xreorder f,car i) then {f}
           else
            <<z := union(foreach w in ik join
                                        if w := xcoeff(f,wedgefax w) then {w},
                         z);
              nil>>;
   if z then edsverbose("New 0-form conditions detected",z,'sys);
   % Set final ordering
   pk := setdiff(kl,append(dk,ik));
   kl := append(dk,append(pk,ik)); % dep > prl > ind
   updkordl kl;
   % Construct final eds
   m := copycfrm eds_cfrm s;
   s := copyeds s;
   eds_sys s :=
      xreordersys append(z,append(p,append(q,nonpfaffpart eds_sys s)));
   eds_ind s := xreordersys append(car i,cadr i);
   cfrm_cob m := kl;
   eds_cfrm s := m;
   if !*edssloppy then eds_cfrm s := updatersx eds_cfrm s;
   % Fix flags
   if q or cadr i then flagfalseeds(s,'solved)
   else flagtrueeds(s,'solved);
   rempropeds(s,'reduced);
   if z then remtrueeds(s,'closed);
   remkrns s;
   setcfrm n;
   return s;
   end;


symbolic procedure xreordersys p;
   % p:sys -> xreordersys:sys
   foreach f in p collect xreorder f;


symbolic procedure solvededsp s;
   % s:eds -> solvededsp:bool
   knowntrueeds(s,'solved);


symbolic procedure reordereds s;
   % s:eds -> reordereds:eds
   % Reorder s according to current kernel order as far as possible.
   begin scalar r,k;
   r := copyeds s;
   k := rightunion(kord!*,edscob r);
   eds_sys r := sortsys(xreordersys eds_sys r,k);
   eds_ind r := sortsys(xreordersys eds_ind r,k);
   eds_cfrm r := reordercfrm eds_cfrm r;
   return if r = s then s else normaleds r;
   end;


symbolic procedure sorteds s;
   % s:eds -> sorteds:eds
   begin scalar k;
   s := copyeds s;
   k := edscob s;
   eds_sys s := sortsys(eds_sys s,k);
   eds_ind s := sortsys(eds_ind s,k);
   return s;
   end;


symbolic procedure sortsys(s,c);
   % s:sys, c:cob -> sortsys:sys
   % sort forms by degree, should add some more stuff.
   reversip sort(s,function pfordp)
      where kord!* = reverse c;

endmodule;

end;
