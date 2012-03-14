module xgroeb;

% GB calculation

% Authors: David Hartley and Philip A Tuckey

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


fluid '(!*xfullreduce !*trxideal !*twosided !*trxmod
        xpolylist!* xvarlist!* zerodivs!* xtruncate!*
        xdegreelist!*);

global '(dimex!*);


put('xideal,'rtypefn,'quotelist);
put('xideal,'listfn,'xideallist);


symbolic procedure xideallist(u,v);
   % u:list of prefix,v:bool -> xideallist:prefix
   % Syntax is xideal({poly,...} [,{var,...}] [,degree])
   begin scalar x,y;
   xtruncate!* := nil;       % don't truncate GB
   if atom u
     then rerror(xideal,0,"Wrong number of arguments to xideal");
   if eqcar(x := aeval car u,'list) then
    <<x := cdr x;
      u := cdr u>>
   else typerr(car u,'list);
   if u and eqcar(y := reval car u,'list) then
    <<xvars y;                       % partition variables
      u := cdr u>>;
   if u then
      if fixp(y := reval car u) then
       <<xtruncate!* := y;       % truncation degree
         u := cdr u>>
      else typerr(y,"truncation degree");
   if u then rerror(xideal,0,"Wrong number of arguments to xideal");
   x := xidealpf for each f in x join if f := xpartitop f then {f};
   return makelist for each g in x collect !*q2a1(!*pf2sq repartit g,v);
   end;


symbolic procedure xidealpf p;
   % p:list of pf -> xidealpf:list of pf
   xideal0 storexvars p
      where xvarlist!* = {}, xdegreelist!* = {};


symbolic procedure storexvars p;
   % p:list of pf -> storexvars:list of pf
   % Result is identical to input. Side-effects are to store all pform
   % variables in xvarlist!*, all zero divisors in zerodivs!*, and check
   % whether input is homogeneous in degree or in conflict with dimex!*.
   begin
   xvarlist!* := nil;
   foreach f in p do % collect all variables present in p
    <<if xtruncate!* and not xhomogeneous f then
       <<lprim "inhomogeneous input - truncation not possible";
         xtruncate!* := nil>>;
      xvarlist!* := union(allxvars f,xvarlist!*)>>;
   xvarlist!* := sort(xvarlist!*,'worderp);
   xdegreelist!*
      := (1 . 0) . foreach k in xvarlist!* collect k . xdegree k;
   zerodivs!*:= foreach v in xvarlist!* join if oddp xdegree v then {v};
   if fixp dimex!* and dimex!* < foreach v in xvarlist!* sum xdegree v
     then rerror(xideal,0,
             "too many independent p-forms in XIDEAL (check SPACEDIM)");
   return p;
   end;


symbolic procedure allxvars f;
   % f:pf -> allxvars:list of <kernel>
   if null f or lpow f = 1 then nil
   else append(wedgefax lpow f,allxvars red f);


symbolic procedure xideal0 F;
   % F:list of pf -> xideal0:list of pf
   % GB algorithm
   begin scalar G,F0,P;
   if !*trxideal then xprint_basis("Input Basis",F);
   if !*xfullreduce then F := weak_xautoreduce1(F,{});
   if !*trxideal and not xequiv(F,xpolylist!*) then
      xprint_basis("New Basis",F);
   P := critical_pairs(F,{},empty_xset());
   while not empty_xsetp P do
      begin scalar cp,k;
      cp := remove_least_item P;
      if !*trxideal then xprint_pair cp;
      if not xriterion_1(cp,F,P) and not xriterion_2(cp,zerodivs!*,P)
        then if k := weak_xreduce(critical_element cp,F) then
            if lpow k = 1 then % quick exit for trivial ideal
             <<P := empty_xset();
               F := {xregister(!*k2pf 1,cp)}>>
            else
             <<k := xregister(xnormalise k,cp);
               G := if !*xfullreduce then weak_xautoreduce1({k},F)
                     else k . F;
               F0 := intersection(F,G);
               P := remove_critical_pairs(setdiff(F,F0),P);
               if !*trxideal and not xequiv(G,xpolylist!*) then
                  xprint_basis("New Basis",G);
               P := critical_pairs(setdiff(G,F0),F0,P);
               F := G>>
         else if !*trxideal and not !*trxmod then writepri(0,'last);
      end;
   return if !*xfullreduce then xautoreduce1 F
          else reversip sort(F,'pfordp);
   end;


symbolic procedure xriterion_1(cp,G,P);
   if null G then nil
   else if pr_type cp neq 'spoly_pair then nil
   else x neq pr_lhs cp
    and x neq pr_rhs cp
    and xdiv(xval x,xkey cp)
    and (null pr or not find_item(pr,P)
           where pr = make_spoly_pair(x,pr_lhs cp))
    and (null pr or not find_item(pr,P)
           where pr = make_spoly_pair(x,pr_rhs cp))
    and <<if !*trxideal then writepri("criterion 1 hit",'last); t>>
    or  xriterion_1(cp,cdr G,P) where x = car G;


symbolic procedure xriterion_2(cp,G,P);
   % G = zerodivs!* at the start
   % I don't believe this ever returns t for our case
   if null G then nil
   else if pr_type cp neq 'wedge_pair then nil
   else !*k2pf x neq pr_lhs cp
    and xdiv({x,x},xkey cp)
    and (null pr or not find_item(pr,P)
           where pr = make_wedge_pair(x,pr_rhs cp))
    and <<if !*trxideal then writepri("criterion 2 hit",'last); t>>
    or  xriterion_2(cp,cdr G,P) where x = car G;


% The remaining procedure are for tracing and debugging


symbolic procedure xequiv(F,G);
   % F,G:list of pf -> xequiv:bool
   % true if F and G have equal contents, possibly reordered
   length F = length G and sublistp(F,G);


symbolic procedure xregister(k,pr);
   % k:pf, pr:crit_pr -> xregister:pf
   % returns k unchanged
   % xpolylist!* updated as side-effect
   begin
   eval {mkid('xregister_,pr_type pr)};
   if !*trxideal then
    <<xpolylist!* := append(xpolylist!*,{k});
      writepri(mkquote{'equal,{'xpoly,xpolyindex k},
                              preppf k},'last)>>;
   return k;
   end;


symbolic procedure xregister_spoly_pair; nil; % Just for counting calls.
symbolic procedure xregister_wedge_pair; nil;
symbolic procedure xregister_xcomm_pair; nil;


symbolic procedure xprint_basis(s,p);
   % s:string, p:list of pf -> xprint_basis:nil
   % Prints heading s, followed by basis p.
   % xpolylist!* updated as a side-effect. Used for tracing.
   begin
   xpolylist!* := p;
   writepri(s,'only);
   foreach f in p do
      mathprint {'equal,{'xpoly,xpolyindex f},preppf f};
   end;



symbolic procedure xpolyindex x;
   length(x member reverse xpolylist!*);


symbolic procedure xprint_pair cp;
   begin
   writepri(mkquote pr_type cp,'first);
   if pr_type cp = 'spoly_pair then
      writepri(mkquote makelist {xpolyindex pr_lhs cp,
                                 xpolyindex pr_rhs cp},
               nil)
   else if pr_type cp = 'wedge_pair then
      writepri(mkquote makelist {lpow pr_lhs cp,
                                 xpolyindex pr_rhs cp},
               nil)
   else
      writepri(mkquote makelist {lpow pr_lhs cp,
                                 xpolyindex pr_rhs cp},
               nil);
   writepri(" -> ",nil);
   end;


endmodule;

end;
