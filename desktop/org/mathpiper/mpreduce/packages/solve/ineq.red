module ineq; % Inequalities and linear optimization.

% Author:       Herbert Melenk <melenk@zib.de>

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


% Driver for solving inequalities and inequality systems.

% Implemented methods:
%
%   -  linear multivariate system
%   -  polynomial/rational univariate inequality and system

% version 2: Jul 2003 Adaptation of the actual REDUCE language stand.
%            Return an isolated equation if only one inequality is
%            entered.

% Common algebraic interface:
%
%     ineq_solve(<ineq/ineqlist> [,<variable/variablelist>])

create!-package('(ineq linineq liqsimp1 liqsimp2 polineq),'(solve));

load!-package'solve;  % Some routines from solve are needed.

fluid'(solvemethods!*);

if not memq('ineqseval,solvemethods!*) then
      solvemethods!*:='ineqseval!*!*.SOlvemethods!*;

if not get('geq,'simpfn) then
    <<mkop'leq; mkop'geq; mkop'lessp; mkop'greaterp>>;

if not get('!*interval!*,'simpfn) then
    <<mkop'!*interval!*;infix !*interval!*;
      put('!*interval!*,'prtch," .. ")>>;

symbolic procedure ineqseval!*!* u;
 % Interface to solve.
  (if null w then nil
    else if w='(failed) then if smemql('(leq geq lessp greaterp),u)
      then w else nil else w)where w=ineqseval u;

symbolic procedure ineqseval!* u;
 % Interface to ineq_solve.
  (if null w or w='(failed) then car u else w)where w=ineqseval u;

put('ineq_solve,'psopfn,'ineqseval!*);

symbolic procedure ineqseval u;
  begin scalar s,s1,v,v1,l,w1,w2,err,ineqp,str;
   integer n;
   s:=reval car u;
   s:=if eqcar(s,'list) then cdr s else {s};
   if cdr u then
   <<v:=reval cadr u;v:=if eqcar(v,'list) then cdr v else {v}>>else
     u:=append(u,{ggvars s});
   % test for linearity, collect variables.
   l:=t;
   s1:=for each q in s join if not err then
   <<if atom q or not memq(car q,'(leq geq lessp greaterp equal))
        then err:=t else
     <<if not(car q eq'equal) then ineqp:=t;
       n:=n#+1;
       str:=str or memq(car q,'(lessp greaterp));
       w1:=simp cadr q; w2:=simp caddr q;
       v1:=union(v1,solvevars{w1,w2});
       if not domainp denr w1 or not domainp denr w2 then l:=nil;
       {numr w1,denr w1,numr w2,denr w2}>>>>;
   if err or not ineqp then return nil;
   if null v then v:=v1;
   l:=l and not nonlnrsys(s1,v);
   if length v1 > length v or not subsetp(v,v1) or not l and cdr v1 then
       return'(failed); % Too many indeterminates in inequality system;
   if l and str then
       return'(failed); % No strict linear system.
   u:=if l then linineqseval u else polineqeval u;
   if null cdr u then u:={'list} else if null cddr u then u:=cadr u;
   return u end;

symbolic procedure ggvars s;
begin scalar v;
 for each u in s do v:=ggvars1(u,v);
 if v then(v:=if null cdr v then car v else 'list.v);
 return v end;

symbolic procedure ggvars1(u,v);
   if not atom u and car u member '(leq geq lessp greaterp equal)
     then ggvars2(cadr u,ggvars2(caddr u,v))
    else nil;

symbolic procedure ggvars2(u,v);
if null u or numberp u or(u eq'i and !*complex)then v
 else if atom u then if u member v then v else u.v
 else if car u memq'(plus times expt difference minus quotient)
  then ggvars3(cdr u,v)
 else if u member v then v else u.v;

symbolic procedure ggvars3(u,v);
if null u then v else ggvars3(cdr u,ggvars2(car u,v));

endmodule;

end;
