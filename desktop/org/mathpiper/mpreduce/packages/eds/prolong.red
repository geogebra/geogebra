module prolong;

% Prolonged systems, tableaux

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



fluid '(!*edsverbose !*edsdebug !*arbvars !*varopt !*groebopt
        !*solveinconsistent depl!* !*edssloppy pullback_maps);


% Grassmann bundle variety


put('grassmann_variety,'rtypefn,'getrtypecar);
put('grassmann_variety,'edsfn,'grassmannvariety);
flag('(grassmannvariety grassmannvarietysolution
       grassmannvarietytorsion),'hidden);

symbolic procedure grassmannvariety s;
   % s:eds -> grassmannvariety:eds
   % Reduced Grassmann contact system together with Grassmann variety
   % conditions as one system with 0-forms
   begin scalar p,g,s0,v;
   if g := geteds(s,'grassmannvariety) then return g;
   s0 := closure s;
   g := gbsys s0;
   p := solvedpart pfaffpart eds_sys s0;
   % reduction in next lines ok since lpows g = prlkrns s0
   foreach f in setdiff(eds_sys s0,p) do
      v := union(foreach q in xcoeffs xreduce(f,eds_sys g) collect
                    1 .* q .+ nil,v);
   g := augmentsys(g,append(v,p));
   puteds(s,'grassmannvariety,g);
   return g;
   end;


% Prolongation


put('prolong,'rtypefn,'quoteeds);
put('prolong,'edsfn,'prolongeds);


symbolic procedure prolongeds s;
   % s:eds -> prolongeds:xeds
   begin
   pullback_maps := makelist {};
   return
      if not edsp s  then typerr(s,'eds)
      else mkxeds makelist
               foreach x in prolong s join
            if cdr x then {cdr x};
   end;


symbolic procedure prolong s;
   % s:eds -> prolong:list of tag.eds
   % where tag is one of
   %       prolonged       s was prolonged
   %       reduced         s was reduced
   %    failed          couldn't solve Grassmann variety conditions (eds
   %                    is {Grassmann system with variety conditions})
   %    inconsistent    prolongation is inconsistent
   % eds_ind s is preserved by prolong. Note the
   % heuristic to eliminate independent variables is incomplete.
   begin scalar g,v,s1;
   g := copyeds grassmannvariety s;
   updkordl edscob g;
   eds_sys g := setdiff(eds_sys g,scalarpart eds_sys g);
   v := decomposegrassmannvariety s;
   return if null v then
   << edsverbose("Prolongation inconsistent",nil,nil);
      eds_sys g := {!*k2pf 1};
      {'inconsistent . g} >>
   else foreach strata in v join
      if car strata = 'failed then
      << edsverbose("Prolongation failed - solution variety:",
                    cdr strata,'sq);
         {'failed .
            augmentsys(g,foreach q in cdr strata collect 1 .* q .+ nil)}
       >>
      else if car strata = 'base then
      << edsverbose("Reduction using new equations:",cdr strata,'rmap);
         pullback_maps := append(pullback_maps,{!*rmap2a cdr strata});
         s1 := edscall pullback0(s,cdr strata);
          if scalarpart eds_sys s1 then
            s1 := edscall positiveeds s1;
         if emptyedsp s1 then {'inconsistent . s1}
         else if edsp s1 then {'reduced . s1}
         else foreach s2 in getrlist s1 collect 'reduced . s2 >>
      else if car strata = 'fibre then
      << if cadr strata then
            edsverbose("Prolongation using new equations:",
                       cdr strata,'rmap)
         else
            edsverbose("Prolongation (no new equations)",nil,nil);
         pullback_maps := append(pullback_maps,{!*rmap2a cdr strata});
               s1 := edscall pullback0(g,cdr strata);
         {'prolonged . s1} >>;
   end;


symbolic procedure decomposegrassmannvariety s;
   % s:eds -> decomposegrassmannvariety:list of tag.value
   % where tag.value is one of
   %       'fibre.rmap             s can be prolonged
   %       'base.rmap              s must be reduced
   %    'failed . list of sq    couldn't solve Grassmann variety
   %                            conditions
   %    'inconsistent.nil      Grassmann variety empty
   begin scalar g,v,c,b;
   g := grassmannvariety s;
   c := reverse setdiff(edscrd g,edsindcrd g);
   c := edsgradecoords(c,geteds(g,'jet0));
   % Allow for case where g has no fibre coordinates (s has finite type)
   if null setdiff(edscrd g,edscrd s) then c := {} . c;
   if semilinearp s then
      if v := grassmannvarietytorsion s then
         if null(v := edssolvegraded(v,cdr c,cfrm_rsx eds_cfrm s))
         then return {'inconsistent . nil}
         else return foreach m in v collect
            if car m then 'base . cdr m
            else 'failed . cdr m
      else if v := partsolvegrassmannvariety s then return
               {'fibre . !*map2rmap
            foreach x in car v join if not(car x memq cadr v) then
               {car x . mk!*sq subsq(simp!* cdr x,caddr v)}}
      else errdhh "Bad solution to semilinear system"
   else % not semilinearp s
   << v := foreach f in scalarpart eds_sys g collect lc f;
      if null(v := edssolvegraded(v,c,cfrm_rsx eds_cfrm s))
      then return {'inconsistent . nil}
      else return foreach m in v collect
         if null car m then 'failed . cdr m
         else if (b := foreach s in cadr m join
                                       if not memq(car s,car c) then {s}) then
            'base . {b,for each p in caddr m
                          join if freeofl(p,car c) then {p}}
         else 'fibre . cdr m; >>;
   end;


% Special routines for semilinear systems


symbolic procedure partsolvegrassmannvariety s;
   % s:eds -> partsolvegrassmannvariety:{map,list of kernel,map}
   % Partly solves the variety equations for a linear system s.
   % The "solution" is returned as from edspartsolve.
   begin scalar v,c;
   if v := geteds(s,'grassmannvarietysolution) then return v;
   v := grassmannvariety s;
   c := reverse setdiff(edscrd v,edscrd s);
   v := foreach f in scalarpart eds_sys v collect lc f;
   v := edspartsolve(v,c);
   puteds(s,'grassmannvarietysolution,v);
   return v;
   end;


put('dim_grassmann_variety,'simpfn,'dimgrassmannvarietyeval);

symbolic procedure dimgrassmannvarietyeval u;
   % u:{eds}|{eds,sys} -> dimgrassmannvarietyeval:sq
   if length u < 1 or length u > 2 then
      rerror(eds,000,
             "Wrong number of arguments to dim_grassmann_variety")
   else if edsp car(u := revlis u) then
      edscall dimgrassmannvariety(car u,if cdr u then !*a2sys cadr u)
         ./ 1
   else typerr(car u,"EDS");


symbolic procedure dimgrassmannvariety(s,x);
   % s:eds, x:sys -> dimgrassmannvariety:int
   begin scalar v,c;
   if not quasilinearp s then
      if null x then
               rerror(eds,000,"Integral element required for nonlinear EDS")
      else
               s := linearise(s,x);
   v := grassmannvariety s;
   c := length setdiff(edscrd v,edscrd s);
   % Treat quasilinear and semilinear systems the same
   % Will storing the solution etc cause trouble for q-l. s?
   v := partsolvegrassmannvariety s;
   c := c - foreach x in car v sum
                     if car x memq cadr v then 0 else 1;
   return c;
   end;


put('torsion,'rtypefn,'quotelist);
put('torsion,'listfn,'torsioneval);

symbolic procedure torsioneval(u,v);
   % u:{eds}, v:bool -> torsioneval:rlist
   if not edscall semilinearp(u := reval car u) then
      rerror(eds,000,"TORSION available for semi-linear systems only")
   else
      makelist for each q in edscall grassmannvarietytorsion u
                  collect !*q2a1(q,v);


symbolic procedure grassmannvarietytorsion s;
   % s:eds -> grassmannvarietytorsion:list of sf
   begin scalar v;
   if v := geteds(s,'grassmannvarietytorsion) then return v;
   v := partsolvegrassmannvariety s;
   v := foreach x in car v join
                 if car x memq cadr v and numr
                 << x := addsq(negsq !*k2q car x,simp!* cdr x);
              x := subsq(x,caddr v) >>
                 then {x};
   puteds(s,'grassmannvarietytorsion,v);
   return v;
   end;

endmodule;

end;
