module edsaux;

% Miscellaneous support functions

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


fluid '(!*edsverbose !*edsdebug);


% Operations on eds

% Storing these kernel lists seems to have <1% effect on speed.

symbolic procedure indkrns s;
   % s:eds -> indkrns:list of lpow pf
   % independent kernels - leading basis forms in eds_ind s
%   geteds(s,'indkrns) or
%   puteds(s,'indkrns,
      foreach f in eds_ind s join
            if tc(f := trterm f) = (1 ./ 1) then {tpow f}; %);


symbolic procedure depkrns s;
   % s:eds -> depkrns:list of lpow pf
   % dependent kernels - a basis for edscob s/eds_ind s
   % OK?
%   geteds(s,'depkrns) or
%   puteds(s,'depkrns,
      foreach f in  eds_sys s join
            if lc f = (1 ./ 1) and degreepf f = 1 then {lpow f}; %);


symbolic procedure prlkrns s;
   % s:eds -> prlkrns:list of lpow pf
   % principal kernels - a basis for the non-linear part of s
%   geteds(s,'prlkrns) or
%   puteds(s,'prlkrns,
      setdiff(edscob s,append(indkrns s,depkrns s)); %);


symbolic procedure remkrns s;
   % s:eds -> remkrns:nil
   foreach p in {'indkrns,'depkrns,'prlkrns} do rempropeds(s,p);


% Operations on sys


symbolic procedure degreepart(s,d);
   % s:sys, d:int -> degreepart:sys
   foreach f in s join if degreepf f = d then {f};


symbolic procedure scalarpart s;
   % s:sys -> scalarpart:sys
   degreepart(s,0);


symbolic procedure pfaffpart s;
   % s:sys -> pfaffpart:sys
   degreepart(s,1);


symbolic procedure nonpfaffpart s;
   % s:sys -> nonpfaffpart:sys
   foreach f in s join if degreepf f neq 1 then {f};


symbolic procedure solvedpart s;
   % s:sys -> solvedpart:sys
   foreach f in s join if f and lc f = (1 ./ 1) then {f};


symbolic procedure lpows s;
   % s:list of pf -> lpows:list of lpow pf
   % returns the leading kernels in s
   foreach f in s collect lpow f;


symbolic procedure singleterms s;
   % s:list of pf -> singleterms:bool
   % true if each form in s is a single term
   null s or null red car s and singleterms cdr s;


symbolic procedure !*a2sys u;
   % u:prefix list -> !*a2sys:list of pf
   if rlistp u then
      foreach f in cdr indexexpandeval{u} collect xpartitop f
   else typerr(u,'list);


symbolic procedure !*sys2a u;
   % u:list of pf -> !*sys2a:prefix list
   makelist foreach f in u collect !*pf2a f;


symbolic procedure !*sys2a1(u,v);
   % u:list of pf -> !*sys2a:prefix list
   % !*sys2a with choice of !*sq or true prefix
   makelist foreach f in u collect !*pf2a1(f,v);


% Operations on pf


symbolic procedure xpows f;
   % f:pf -> xpows:list of lpow pf
   if f then lpow f . xpows red f;


symbolic procedure xcoeffs f;
   % f:pf -> xcoeffs:list of sq
   if f then lc f . xcoeffs red f;


symbolic procedure degreepf f;
   % f:pf -> degreepf:int
   % assumes f homogeneous
   % could replace with xdegree from XIDEAL2
   if null f then 0
   else (if null x then 0
   else if fixp x then x
   else rerror('eds,130,"Non-integral degree not allowed in EDS"))
   where x = deg!*form lpow f;


symbolic procedure xreorder f;
   % f:pf -> xreorder:pf
   if f then
      addpf(multpfsq(xpartitop lpow f,reordsq lc f),
            xreorder red f);


symbolic procedure xreorder!* f;
   % f:pf -> xreorder!*:pf
   % Like xreorder when it is known that only the order between terms
   % has changed (and not within terms).
   if f and red f then
      addpf(lt f .+ nil,xreorder!* red f)
   else f;


symbolic procedure xrepartit f;
   % f:pf -> xrepartit:pf
   if f then
      addpf(wedgepf(xpartitsq subs2 resimp lc f,xpartitop lpow f),
            xrepartit red f);


symbolic procedure xrepartit!* f;
   % f:pf -> xrepartit!*:pf
   % Like xrepartit when xvars!* hasn't changed.
   if f then
      addpf(multpfsq(xpartitop lpow f,subs2 resimp lc f),
            xrepartit!* red f);


symbolic procedure trterm f;
   % f:pf -> trterm:lt pf
   % the trailing term in f
   if null red f then lt f else trterm red f;


symbolic procedure linearpart(f,p);
   % f:pf, p:list of 1-form kernel -> linearpart:pf
   % result is the part of f of degree 1 in p
   if null f then nil
   else if length intersection(wedgefax lpow f,p) = 1 then
      lt f .+ linearpart(red f,p)
   else linearpart(red f,p);


symbolic procedure inhomogeneouspart(f,p);
   % f:pf, p:list of 1-form kernel -> inhomogeneouspart:pf
   % result is the part of f of degree 0 in p
   if null f then nil
   else if length intersection(wedgefax lpow f,p) = 0 then
      lt f .+ inhomogeneouspart(red f,p)
   else inhomogeneouspart(red f,p);


symbolic procedure xcoeff(f,c);
   % f:pf, c:pffax -> xcoeff:pf
   if null f then nil
   else
      begin scalar q,s;
      q := xdiv(c,wedgefax lpow f);
      if null q then return xcoeff(red f,c);
      q := mknwedge q;
      if append(q,c) = lpow f then s := 1 % an easy case
      else s := numr lc wedgepf(!*k2pf q,!*k2pf mknwedge c);
      return q .* (if s = 1 then lc f else negsq lc f)
               .+ xcoeff(red f,c);
      end;


symbolic procedure xvarspf f;
   % f:pf -> xvarspf:list of kernel
   if null f then nil
   else
      union(foreach k in
               append(wedgefax lpow f,
               append(kernels numr lc f,
                      kernels denr lc f)) join if xvarp k then {k},
            xvarspf red f);


symbolic procedure kernelspf f;
   % f:pf -> kernelspf:list of kernel
   % breaks up wedge products
   if null f then nil
   else
      union(append(wedgefax lpow f,
            append(kernels numr lc f,
                          kernels denr lc f)),
            kernelspf red f);


symbolic procedure mkform!*(u,p);
   % u:prefix, p:prefix (usually int) -> mkform!*:prefix
   % putform with u returned, and covariant flag removed
   begin
   putform(u,p);
   return u;
   end;


% Operations on lists


symbolic procedure purge u;
   % u:list -> purge:list
   % remove repeated elements from u, leaving last occurence only
   if null u then nil
   else if car u member cdr u then purge cdr u
   else car u . purge cdr u;


symbolic procedure rightunion(u,v);
   % u,v:list -> rightunion:list
   % Like union, but appends v to right. Ordering of u and v preserved
   % (last occurence of each element in v used).
   append(u,foreach x on v join
      if not(car x member u) and not(car x member cdr x) then {car x});


symbolic procedure sublisq(u,v);
   % u:a-list, v:any -> sublisq:any
   % like sublis, but leaves structure untouched where possible
   if null u or null v then v
   else
      begin scalar x,y;
      if (x := atsoc(v,u)) then return cdr x;
      if atom v then return v;
      y := sublisq(u,car v) . sublisq(u,cdr v);
      return if y = v then v else y;
      end;


symbolic procedure ordcomb(u,n);
   % u:list, n:int -> ordcomb:list of list
   % List of all combinations of n distinct elements from u.
   % Order of u is preserved: ordcomb(u,1) = mapcar(u,'list)
   % which is not true for comb, from which this is copied.
   begin scalar v; integer m;
   if n=0 then return {{}}
   else if (m:=length u-n)<0 then return {}
   else for i := 1:m do
    <<v := nconc!*(v,mapcons(ordcomb(cdr u,n-1),car u));
      u := cdr u>>;
   return nconc!*(v,{u})
   end;


symbolic procedure updkordl u;
   % u:list of kernel -> updkordl:list of kernel
   % list version of updkorder
   % kernels in u will have highest precedence, order of
   % other kernels unchanged
   begin scalar v,w;
   v := kord!*;
   w := append(u,setdiff(v,u));
   if v=w then return v;
   kord!* := w;
   alglist!* := nil . nil;        % Since kernel order has changed.
   return v
   end;


symbolic procedure allequal u;
   % u:list of any -> allequal:bool
   % t if all elements of u are equal
   if length u < 2 then t
   else car u = cadr u and allequal cdr u;


symbolic procedure allexact u;
   % u:list of kernel -> allexact:bool
   % t if all elements of u are exact pforms
   if null u then t
   else exact car u and allexact cdr u;


symbolic procedure coords u;
   % u:list of kernel -> coords:list of kernel
   % kernels in u are 1-forms, returns coordinates involved
   % THIS SHOULD GO
   foreach k in u join if exact k then {cadr k};


symbolic procedure zippf(pfl,sql);
   % pfl:list of pf, sql:list of sq
   % -> zippf:pf
   % Multiply elements of pfl by corresponding elements of sql, and add
   % results together. If trailing nulls on pfl or sql can be omitted.
   if null pfl or null sql then nil
   else if numr car sql and car pfl then
      addpf(multpfsq(car pfl,car sql),zippf(cdr pfl,cdr sql))
   else
      zippf(cdr pfl,cdr sql);


% EDS tracing and debugging


symbolic procedure errdhh u;
% Special error call for errors that shouldn't happen
rerror(eds,999,"Internal EDS error -- please contact David Hartley
*****" . if atom u then {u} else u);


symbolic procedure edsverbose(msg,v,type);
   % msg:atom or list, v:various, type:id|nil
   % -> edsverbose:nil
   % type gives the type of v, one of
   %    nil  - v is empty
   %    'sf  - v is a list of sf
   %    'sq  - v is a list of sq
   %    'sys - v is a list of pf
   %    'cob - v is a list of kernel
   %    'map - v is a map (list of kernel.prefix)
   %    'xform - v is an xform (list of kernel.pf)
   %    'prefix- v is prefix
   if !*edsverbose or !*edsdebug then
   begin
   if atom msg then msg := {msg};
   lpri msg; terpri();
   if null type then nil
   else if type = 'sf then
      foreach f in v do edsmathprint prepf f
   else if type = 'sq then
      foreach f in v do edsmathprint prepsq f
   else if type = 'sys then
      foreach f in v do edsmathprint preppf f
   else if type = 'cob then
      edsmathprint makelist v
   else if type = 'map then
      foreach p in v do edsmathprint {'equal,car p,cdr p}
   else if type = 'rmap then
   << foreach p in car v do edsmathprint {'equal,car p,cdr p};
      foreach p in cadr v do edsmathprint {'neq,p,0} >>
   else if type = 'xform then
      foreach p in v do edsmathprint {'equal,car p,preppf cdr p}
   else if type = 'prefix then
      edsmathprint v
   else errdhh{"Unrecognised type",type,"in  edsverbose"};
   end;


symbolic procedure edsdebug(msg,v,type);
   % msg:string, v:various, type:id|nil
   % -> edsdebug:nil
   % Like edsverbose, just for debugging.
   if !*edsdebug then edsverbose(msg,v,type);


symbolic procedure edsmathprint f;
   % f:prefix -> nil
   % Similar to mathprint, except going in at writepri,
   % so TRI package picks it up too.
   <<writepri(mkquote f,'only); terpri!* t; >>;

endmodule;

end;
