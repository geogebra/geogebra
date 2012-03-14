module eqn;   % Support for equations as top level structures.

% Author: Anthony C. Hearn.

% Copyright (c) 1990 The RAND Corporation.  All rights reserved.

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


% At the moment "EQUAL" is the tag for such structures.

% Evalequal is defined in alg/algbool.

fluid '(!*evallhseqp);

switch evallhseqp;

!*evallhseqp := t;   % Default is currently on.

symbolic procedure equalreval u;
   % This definition really needs to know whether we are trying
   % to produce a tagged standard quotient or a prefix form.
   % It would also be more efficient to leave a *SQ form unchanged
   % on the right hand side as shown.  However, it messes up printing.
  (if !*evallhseqp or not atom car u and flagp(caar u,'immediate)
     then list('equal,reval car u,x)
    else list('equal,car u,x))
   where x= reval y % (if eqcar(y,'!*sq) then aeval y else reval y)
             where y=cadr u;

put('equal,'psopfn,'equalreval);

put('equal,'rtypefn,'quoteequation);

put('equal,'i2d,'eqnerr);

symbolic procedure eqnerr u; typerr(u,"equation");

put('equation,'evfn,'evaleqn);

% symbolic procedure evaleqn(u,v);
%    begin scalar op,x;
%       if null cdr u or not eqcar(cadr u,'equal)
%         then rerror(alg,26,"Invalid equation structure");
%      op := car u;
%       if null cddr u
%         then return 'equal . for each j in cdadr u
%          collect if op eq 'eqneval then reval1(j,v) else list(op,j)
%        else if eqcar(caddr u,'equal) or cdddr u
%         then rerror(alg,27,"Invalid equation structure");
%       x := caddr u;
%       return 'equal . for each j in cdadr u collect list(op,j,x)
%   end;

% put('eqneval,'rtypefn,'getrtypecar);

symbolic procedure evaleqn(u,v);
 % This function allows us to perform elementary equation arithmetic
 % combining one equation and scalars by  + - * / ^, and to compute
 % sums and differences of equations. Restriction: the equation must
 % be the leftmost term in the arithmetic expression.
  begin scalar e,l,r,w,op,x,found;
   if (x:=get(u,'avalue)) then u:=cadr x;
   if not !*evallhseqp then
   <<if eqcar(u,'equal) then return equalreval cdr u else
     typerr(u,"algebraic expression when evallhseqp is off")>>;
   op:=car u; w:=cdr u;
   if op='plus or op='difference or op='minus then
   <<for each q in w do
     <<q:=reval q;
       if eqcar(q,'equal)
          then <<l:=cadr q.l; r:=caddr q.r;found:=t>>
          else   <<l:=q.l; r:=q.r>>;
     >>;
     r:=op.reverse r; l:=op.reverse l;
   >>
   else
   << u:=op . for each q in w collect reval q;
      e:=evaleqn1(u,u,nil);
      if e then
      <<l:=subst(cadr e,e,u); r:=subst(caddr e,e,u); found:=t>>;
   >>;
   if not found then rederr
      "failed to locate equal sign in equation processing";
   return {'equal, reval1(l,v), reval1(r,v)}
  end;

symbolic procedure evaleqn1(u,u0,e);
   if atom u then e
    else
   if car u='equal then
     (if e then typerr(u0,"equation expression") else u)
   else evaleqn1(cdr u,u0,evaleqn1(car u,u0,e));

% put(equal,'prifn,'equalpri);

% put('equal,'lengthfn,'eqnlength);

symbolic procedure lhs u;
   % Returns the left-hand-side of an equation.
   lhs!-rhs(u,'cadr);

symbolic procedure rhs u;
   % Returns the right-hand-side of an equation.
   lhs!-rhs(u,'caddr);

symbolic procedure lhs!-rhs(u,op);
 <<if not(pairp u and get(car u,'infix) and cdr u and cddr u
          and null cdddr u)
                then typerr(u,"argument for LHS or RHS");
   apply1(op,u)>>;

flag('(lhs rhs),'opfn);  % Make symbolic operators.


% Explicit substitution code for equations.

symbolic procedure eqnsub(u,v);
   if !*evallhseqp or not atom car u and flagp(caar u,'immediate)
     then 'equal . for each x in cdr v collect subeval1(u,x)
    else list('equal,cadr v,subeval1(u,caddr v));

put('equation,'subfn,'eqnsub);

put('equation,'lengthfn,'eqnlength);

symbolic procedure eqnlength u; length cdr u;

endmodule;

end;
