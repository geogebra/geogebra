module xcrit;

% Critical pairs, critical values

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


Comment. Critical pairs are stored as

        crit_pr ::= {key, type, pf, pf}
        key     ::= mon
        type    ::= 'spoly_pair | 'wedge_pair | 'xcomm_pair

endcomment;


fluid '(xvarlist!* zerodivs!* xtruncate!* !*twosided);


symbolic procedure critical_pairs(q,p,c);
   % q,p:list of pf, c:xset -> critical_pairs:xset
   % add critical pairs for new poly's q to existing xset c,
   % which is based on old poly's p.
   begin scalar f;
   foreach l on q do
     begin
     f := car l;
     foreach g in cdr l do
       (if pr then add_item(pr,c)) where pr = make_spoly_pair(f,g);
     foreach g in p do
       (if pr then add_item(pr,c)) where pr = make_spoly_pair(f,g);
     foreach x in zerodivs!* do
       (if pr then add_item(pr,c)) where pr = make_wedge_pair(x,f);
     foreach x in if !*twosided then xvarlist!* do
       (if pr then add_item(pr,c)) where pr = make_xcomm_pair(x,f);
     end;
   return c;
   end;


symbolic procedure remove_critical_pairs(G,P);
   % G:list of pf, P:xset -> remove_critical_pairs:xset
   % Remove critical pairs for old poly's G from existing xset P.
   <<if G then remove_items(P,G); P>>;


symbolic procedure make_spoly_pair(f,g);
   % f,g:pf -> make_spoly_pair:crit_pr|nil
   % construct critical pair (spoly) for f and g in canonical order
   % return nil if simple criteria fail
   if pfordp(g,f) then make_spoly_pair(g,f) else
   and(t,
       red f or red g,
       not triviallcm(l,xval f,xval g),
       not xdegreecheck mknwedge l,
       {l,'spoly_pair,f,g})
   where l = xlcm(xval f,xval g);


symbolic procedure triviallcm(l,p,q);
   % l,p,q:mon -> triviallcm:bool
   % l is xlcm(p,q), result is t if l = p . q
   xdiv(p,l) = q;


symbolic procedure xdegreecheck u;
   % u:lpow pf -> xdegreecheck:bool
   % result is t if degree of u exceeds truncation
   % degree in graded GB's
   xtruncate!* and xdegree u > xtruncate!*;


symbolic procedure make_wedge_pair(x,f);
   % x:kernel, f:pf -> make_wedge_pair:crit_pr|nil
   % construct critical pair (wedge) for x and f
   % return nil if simple criteria fail
   and(!*twosided and not xtruncate!* or x memq xval f,
       not overall_factor(x,f),
       not xdegreecheck mknwedge l,
       {l,'wedge_pair,!*k2pf x,f})
   where l = xlcm({x,x},xval f);


symbolic procedure overall_factor(x,f);
   % x:kernel,f:pf -> overall_factor:bool
   null f or x memq xval f and overall_factor(x,red f);


symbolic procedure make_xcomm_pair(x,f);
   % x:kernel, f:pf -> make_xcomm_pair:crit_pr|nil
   % construct critical pair (commutator) for x and f
   % return nil if simple criteria fail
   and(!*twosided,
       not xtruncate!*,       % left ideal = right ideal if homogeneous.
       {xval f,'xcomm_pair,!*k2pf x,f});


symbolic procedure critical_element pr;
   % pr:crit_pr -> critical_element:pf
   % calculate a critical element for pr
   apply1(pr_type pr,pr);


symbolic procedure spoly_pair pr;
   % pr:crit_pr -> spoly_pair:pf
   % calculate a critical element for pr
   begin scalar l,f,g;
   f := pr_lhs pr; g := pr_rhs pr;
   l := xkey pr;
   f := wedgepf(!*k2pf mknwedge xdiv(xval f,l),f); % left multiplication
   g := wedgepf(!*k2pf mknwedge xdiv(xval g,l),g); % left multiplication
   return addpf(multpfsq(f,lc g),negpf multpfsq(g,lc f)); % normalise?
   end;


symbolic procedure wedge_pair pr;
   % pr:crit_pr -> wedge_pair:pf
   % calculate a critical element for pr
   if !*twosided and not xdiv(xval pr_lhs pr,xval pr_rhs pr) then
     wedgepf(wedgepf(pr_lhs pr,pr_rhs pr),pr_lhs pr) % split cofactor
   else wedgepf(pr_lhs pr,pr_rhs pr);


symbolic procedure xcomm_pair pr;
   % pr:crit_pr -> xcomm_pair:pf
   % calculate a critical element for pr
   addpf(wedgepf(pr_lhs pr,pr_rhs pr),
         if evenp xdegreemon xval pr_rhs pr
            then wedgepf(pr_rhs pr,negpf pr_lhs pr)
            else wedgepf(pr_rhs pr,pr_lhs pr));

endmodule;

end;
