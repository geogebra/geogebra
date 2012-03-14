%********************************************************************
module equivalence$
%********************************************************************
%  Routines for testing equivalence of solutions
%  Author: Thomas Wolf
%  1996
%
% $Id: crequsol.red,v 1.2 1998/04/28 21:32:11 arrigo Exp $
%

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


algebraic procedure extrfun(sol)$
% unpacking the free functions from the list of free functions
% together with their dependencies
for each f in third sol collect <<
 for each h in rest f do depend first f,h;
 first f
>>$


algebraic procedure equivsol(sol1,sol2,vl)$
% checking equivalence of two solutions of crack, that have been
% `completed' with the procedure `completesol' before.
% The evaluated functions should have identical names in both
% solutions. None of both solutions should contain any unsolved
% equations.
% vl is a list of all independent variables in both solutions.

begin
 scalar f1,f2,s1,s2,ff2,f,h,s,v,mm_,oldtime;
 symbolic fluid '(time_);
 if (first sol1 neq {}) or
    (first sol2 neq {}) or
    (length second sol1 neq length second sol2) or
    (length third  sol1 neq length third  sol2) then return nil;

 f1:=extrfun(sol1);
 f2:=extrfun(sol2);

 % substituting the names of free functions in sol2 to avoid
 % name clashes
 s2:=second sol2;
 ff2:=for each f in f2 collect <<
  h:=lisp gensym();
  s2:=sub(f=h,s2);
  s:=fargs(f);
  for each v in s do depend h,v;
  h
 >>;

 % conditions of equivalence of both solutions
 s1:=for each s in second sol1 collect (lhs s - rhs s);
 s1:=sub(s2,s1);
 lisp<<oldtime:=time_;time_:=nil>>;
 h:=crack(s1,{},f1,vl);
 lisp(time_:=oldtime);

 % is there a regular relation beetween the free functions
 % of sol1 and sol2?
 if h={} or (length h neq 1) then return nil
                             else h:=first h;
 if (first h neq {}) or (third h neq {}) then return nil;
 s2:=second h;
 h:=length f1;
 matrix m__(h,h);
 for f:=1:h do for s:=1:h do
 m__(f,s):=df(rhs part(s2,f),part(ff2,s));

 % cleaning dependencies
 for each h in ff2 do <<
  s:=fargs(h);
  for each v in s do nodepend h,v;
 >>;

 return if det m__=0 then nil
                     else t
end$

algebraic procedure completesol(sol)$
% substitutes in a list of solutions of crack the list of free or yet
% unevaluated functions by a list of lists containing each function
% and the variables they depend on. This is useful to save solutions
% in files.
list(first sol,
     second sol,
     for each f in third sol collect cons(f,fargs f),
     if length sol>3 then part(sol,4)
                     else {}
    )$

endmodule$
end$
