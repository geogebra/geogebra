module ncenv; % Non-communtative polynomial ring environment.

% This module organizes an environment for computing with
% non-commutative polynomials in algebraic mode, and an embedding
% for non-commutative Groebner bases.

% Author: H. Melenk, ZIB Berlin, J. Apel, University of Leipzig

% Copyright: Konrad-Zuse-Zentrum Berlin, 1994

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


share ncpi!-brackets!*,ncpi!-comm!-rules!*,ncpi!-name!-rules!*;

algebraic operator nc!*;
algebraic noncom nc!*;

put('nc!*,'prifn,'pri!-nc!*);
put('nc!*,'dipprifn,'dippri!-nc!*);

symbolic procedure pri!-nc!* u;
   prin2!*(w and cdr w or u) where w=assoc(u,ncpi!-names!*);

symbolic procedure dippri!-nc!* u;
   dipprin2(w and cdr w or u) where w=assoc(u,ncpi!-names!*);

symbolic procedure ncpi!-setup u;
  begin scalar vars,al,b,b0,f,m,rs,rn,na,rh,lh,s,x,y,w,!*evallhseqp;
   if (w:=member('left,u)) or (w:=member('right,u)) then
   <<u:=delete(car w,u);!*ncg!-right:=car w='right>>;
   if length u > 2 then rederr "illegal number of arguments";
   if ncpi!-name!-rules!* then
      algebraic clearrules ncpi!-comm!-rules!*,ncpi!-name!-rules!*;
   u:=sublis(ncpi!-names!*,u);
   for each x in cdr listeval(car u,nil) collect
   <<x:=reval x;y:={'nc!*,mkid('!_,x)}; na:=(y.x).na;
     rn:={'replaceby,x,y}.rn; al:=(x.y).al;vars:=append(vars,{y})>>;
   ncpi!-names!*:=na;
   ncpi!-name!-rules!*:='list.rn;
   m:=for i:=1:length vars -1 join
     for j:=i+1:length vars collect nth(vars,i).nth(vars,j);
   if cdr u then ncpi!-brackets!*:=listeval(cadr u,nil);
   if null ncpi!-brackets!* then rederr "commutator relations missing";
   for each b in cdr ncpi!-brackets!* do
   <<b0:=sublis(al,b);
     w:= eqcar(b0,'equal) and (lh:=cadr b0) and (rh:=caddr b0)
      and eqcar(lh,'difference) and (f:=cadr lh) and (s:=caddr lh)
      and eqcar(f,'times) and eqcar(s,'times)
      and (x:=cadr f) and (y:=caddr f) and member(x,vars) and member(y,vars)
      and x=caddr s and y=cadr s;
     if not w then typerr(b,"commutator relation");
     % Invert commutator if necessary.
     if member(x.y,m) then <<w:=x;x:=y;y:=w; rh:={'minus,rh}>>;
     m:=delete(y.x,m);
     rs:={'replaceby,{'times,x,y},{'plus,{'times,y,x},rh}}.rs>>;
    % Initialize non-commutative distributive Polynomials.
   ncdsetup!*{'list.vars,'list.rs};
   apply('korder,{vars});
   apply('order,{vars});
    % Rules for commutating objects.
   for each c in m do
     rs:={'replaceby,{'times,cdr c,car c},{'times,car c,cdr c}}.rs;
   ncpi!-comm!-rules!*:='list.rs;
   algebraic let ncpi!-comm!-rules!*,ncpi!-name!-rules!* end;

put('nc_setup,'psopfn,'ncpi!-setup);

symbolic procedure nc_cleanup();
   <<algebraic clearrules ncpi!-comm!-rules!*,ncpi!-name!-rules!*;
     algebraic korder nil;algebraic order nil>>;

put('nc_cleanup,'stat,'endstat);

endmodule;;end;
