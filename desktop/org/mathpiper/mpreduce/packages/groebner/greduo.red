module greduo;
% Compute 'greduce' with several orders for the minimal polynomial.

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


global'(gorder gorders greduce_result);

share gorder;

share gorders;

share greduce_result;

if null gorders then gorders:='(list revgradlex gradlex lex);

symbolic procedure greduce!-orders!-eval u;
% 'Greduce_orders(p,g)'; the result is the (minimal) reduction of 'p'
% corresponding to the global variable '*orders', eventually '0'.
begin scalar b,g,l,o,p,r,rr,s,ss,v,x;
 l:=length u;
 if 2>l or 3<l then
  rederr('groe4,1,"groe4 must have 2 or 3 parameters.");
 p:=reval car u;u:=cdr u;
 if eqexpr p then p:=!*eqn2a p;
 g:=reval car u;u:=cdr u;
 if not eqcar(g,'list) then
  rederr('groe4,2,"groe4: 2nd parameter must be a list.");
 for each gg in cdr g do
  if null x and eqexpr gg then x:=t;
 if x then
  g:='list.for each gg in cdr g collect
   if eqexpr gg then !*eqn2a gg else gg;
 if u then<<v:=reval car u;
  if not eqcar(v,'list) then
  rederr('groe4,3,"groe4: 3rd par. must be a list (or it must be omitted).")>>;
 v:='list.groebnervars(cdr g,v);
 for each oo in cdr gorders do
  if null b then
  <<o:=oo;oo:=if eqcar(oo,'list)then cdr oo else oo.nil;torder(v.oo);
    rr:=greduceeval{p,g};ss:=greduce!-orders!-size rr;
    if null r or ss<s then <<gorder:=o;r:=rr;s:=ss;greduce_result:=rr>>;
    if rr=0 then b:=t>>;return r end;

put('greduce_orders,'psopfn,'greduce!-orders!-eval);

symbolic procedure greduce!-orders!-size p;
% Compute the size of the polynomial 'p'.
if atom p then 1 else
if eqcar(p,'expt)then(1+greduce!-orders!-size cadr p+2*x
  where x=if fixp caddr p and caddr p>1 and caddr p<30 then caddr p
          else 5*greduce!-orders!-size caddr p)else
 for each x in p sum greduce!-orders!-size x;

endmodule;;end;
