module groebsea;

% Support of search for reduction polynomials.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%  Search for reduction candidates in a list.

symbolic procedure groebsearchinlist(vev,g);
 % Search for a polynomial in the list 'g',such that the lcm divides
 % vev;'g' is expected to be sorted in descending sequence.
 if null g then nil
 else if buchvevdivides!?(vdpevlmon car g,vev)then car g
 else groebsearchinlist(vev,cdr g);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%  Search list for polynomials;
%  simple variant: mapped to list.
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

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


symbolic procedure groeblistadd(poly,stru);
% Add one polynomial to the tree;
% if this is a simple polynomial(mono or bino), reform
% the list.
 if hcount!* #< 5000 then vdplsortin(poly,stru)
  else vdplsortinreplacing(poly,stru);

symbolic procedure groebstreeadd(poly,stru);
% Map 'groebstreeadd' to 'groeblistadd'.
 groeblistadd(poly,stru);

% symbolic procedure groeblistreconstruct u;
% % Reconstructs a tree from a linear list of polynomials.
%              vdplsort u;

symbolic procedure groebvevdivides!?(e1,e2);
% Look, if 'e1' is a factor of 'e2'.
 if null e1 then t else if null e2 then(if vevzero!? e1 then t else nil)else
 if car e1 #> car e2 then nil else groebvevdivides!?(cdr e1,cdr e2);

% % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % %
% Reforming g, d and g99 when a very simple polynomial was
% found(e.g. a monomial, a binomial).

symbolic procedure groebsecondaryreduction(poly,g,g99,d,gc,
    mode);
 % If poly is a simple polynomial, the polynomials in 'g' and 'g99'
 % are reduced in a second pass. Result is 'g',secondvalue is  'g99'.
 % 'mode' says, that 'g99' has to be modified in place.
 begin scalar break,first,p,pl,rep,rpoly,vev,x;
  mode:=nil;
  secondvalue!*:=g99;thirdvalue!*:=d;fourthvalue!*:=gc;
  vev:=vdpevlmon poly;rpoly:=vdpred poly;
        % Cancel redundant elements in 'g99'.
  for each p in g99 do if buchvevdivides!?(vev,vdpevlmon p)
   then g99:=delete(p,g99);
  if vdplength poly > 2 or  vevzero!? vev then return g;
  if !*groebweak and not vdpzero!? rpoly
   and(groebweaktestbranch!=1(poly,g,d)) then return 'abort;
        !*trgroeb and groebmess50 g;
  pl:=union(g,g99);first:=t;
  while pl and not break do
 <<p:= car pl;pl:=cdr pl;
   if groebprofitsfromvev(p,vev)then
         % Replace by simplified version.
  <<x:=groebnormalform1(p,poly);
    x:=groebsimpcontnormalform x;x:=vdpenumerate x;
    if first then !*trgroeb and groebmess20(poly);
    first:=nil;!*trgroeb and groebmess21(p,x);
    rep:=( p.x).rep;
    if not vdpzero!? x and vevzero!? vdpevlmon x then break:=t;% 1 found.
  >>>>;
  if break then return 'abort;
                           % Reform 'g99'.
  g99:=for each p in g99 collect groebsecondaryreplace(p,rep);
  secondvalue!*:= groebsecondaryremovemultiples g99;
  thirdvalue!*:=d;% Reform 'd'.
  fourthvalue!*:=groebsecondaryremovemultiples   % Reform 'gc'.
  for each y in gc collect groebsecondaryreplace(y,rep);
  g:=for each y in g collect groebsecondaryreplace(y,rep);
         !*trgroeb and groebmess50 g;
  return groebsecondaryremovemultiples g end;

symbolic procedure groebsecondaryremovemultiples g;
 if null g then nil else
  if vdpzero!? car g or member(car g,cdr g)then
   groebsecondaryremovemultiples cdr g else
    car g.groebsecondaryremovemultiples cdr g;

symbolic procedure groebsecondaryreplace(x,rep);
(if y then cdr y else x)where y=atsoc(x,rep);

endmodule;;end;
