module compactf; % Algorithms for compacting algebraic expressions.

% Author: Anthony C. Hearn.

% Copyright (c) 1991 The RAND Corporation.  All Rights Reserved.

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


fluid '(frlis!* mv!-vars!*);

global '(!*trcompact);

switch trcompact;

% Interface to REDUCE simplifier.

put('compact,'simpfn,'simpcompact);

symbolic procedure simpcompact u;
   begin scalar bool;
      if null u or null cdr u
       then rerror(compact,1,
                   list("Wrong number of arguments to compact"));
      if null !*exp then <<rmsubs(); bool := !*exp := t>>;
      u := errorset!*(list('simpcompact1,mkquote u),nil);
      if bool then !*exp := nil;
      if errorp u then rerror(compact,2,"Compact error");
      return car u
   end;

symbolic procedure simpcompact1 u;
   begin scalar v,x,y,w;
      v := simp!* car u;
      u := cadr u;
      if idp u
        then if eqcar(x := get(u,'avalue),'list)
               then u := cadr x
              else typerr(u,"list")
       else if getrtype u eq 'list then u := cdr u
       else typerr(u,"list");
      u := for each j in u collect
      << w:=t;
         if eqcar(j,'equal) or eqcar(j,'replaceby) then
         << if eqcar(y:=caddr j,'when) then
            <<w:=compactbool formbool(caddr y,nil,'algebraic);
              y:=cadr y>>;
           j:= {'difference,cadr j,y}>>;
          % propagate free variables.
         if(y:=compactfmatch2 j) then
         <<j:=sublis(for each x in y collect x.cadr x,j);
           j:=sublis(for each x in y collect cadr x.x,j)>>;
       j.w>>;
      for each j in u do v := compactsq(v,simp!* car j,cdr j);
      return v
   end;

symbolic procedure compactbool w;
    % Reform condtion w for later evaluation and substitution.
    % Without this reform (list (quote ~)(quote x)) would not
    % be substituted by subst('(((~ x).y)..)... .
   if atom w then w else
   if eqcar(w,'list) and cdr w and cadr w='(quote !~) then
       {'quote,{'!~,cadr caddr w}} else
   compactbool car w . compactbool cdr w;

% True beginning of compacting routines.

symbolic procedure compactsq(u,v,c);
   % U is a standard quotient, v a standard quotient for equation v=0.
   % Result is a standard quotient for u reduced wrt v=0.
   begin
      if denr v neq 1
        then msgpri("Relation denominator",prepf denr v,"discarded",
                    nil,nil);
      v := numr v;
      return multsq(compactf(numr u,v,c) ./ 1,
                    1 ./ compactf(denr u,v,c))
   end;

symbolic procedure compactf(u,v,c);
   % U is a standard form, v a standard form for an equation v=0.
   % C is a condition for applying v.
   % Result is a standard form for u reduced wrt v=0.
   begin scalar x; integer n;
      if !*trcompact
       then <<prin2t "*** Arguments on entering compactf:";
              mathprint mk!*sq !*f2q u;
              mathprint mk!*sq !*f2q v>>;
      while x neq u do <<x := u; u := compactf0(u,v,c); n := n+1>>;
      if !*trcompact and n>2
        then <<prin2 " *** Compactf looped ";prin2 n; prin2t " times">>;
      return u
   end;

symbolic procedure compactf0(u,v,c);
 begin scalar x,y,w;
   x := kernels u;
   y := kernels v;
   if not smemq('!~,v) then return compactf1(u,v,x,y);
   for each p in compactfmatch(x,y) do
    if p and not smemq('!~,w:=sublis(p,c)) and eval w and
        not smemq('!~,w:=numr subf(v,p)) then
       u:=compactf1(u,w,x,kernels w);
   return u;
 end;

symbolic procedure compactfmatch(x,y);
 % Finds all possible matches between free variables in
 % kernels of list x and pattern list y, including incomplete,
 % inconsistent and the empty match.
   if null x or null y then '(nil) else
   begin scalar y1,z,r;
    z:=compactfmatch(x,cdr y);
    if not smemq('!~,car y) then return z;
    y1:=car y; y:= cdr y;
    r:=for each x1 in x join
     for each w in compactfmatch1(x1,y1) join
      for each q in compactfmatch(delete(x1,x),sublis(w,y)) collect
       union(w,q);
    return union(r,z);
    end;

symbolic procedure compactfmatch1(x,y);
   if car y = '!~ then {{y.x}} else
   if pairp x and car x=car y then
     mcharg(cdr x,cdr y,car y)
         where frlis!* =nconc(compactfmatch2 y,frlis!*);

symbolic procedure compactfmatch2 y;
   if atom y then nil else
   if car y = '!~ then {y} else
   append(compactfmatch2(car y),compactfmatch2(cdr y));

symbolic procedure compactf1(u,v,x,y);
   begin scalar z;
    %  x := kernels u;
    %  y := kernels v;
      z := intersection(x,y);                 % find common vars.
      if null z then return u;
%  Unfortunately, it's too expensive in space to generate all perms.
%  as in this example:
%       l:={-c31*c21+c32*c22+c33*c23+c34*c24=t1};
%       x:= -c31*c21+c32*c22+c33*c23+c34*c24;
%       compact(x,l);     % out of heap space
%     for each j in permutations z do u := compactf11(u,v,x,y,j);
      return compactf11(u,v,x,y,z)
%     return u
   end;

symbolic procedure compactf11(u,v,x,y,z);
   begin scalar w;
      if domainp u then return u;
      y := append(z,setdiff(y,z));            % vars in eqn.
      x := append(setdiff(x,z),y);            % all vars.
      x := setkorder x;
      u := reorder u;                         % reorder expressions.
      v := reorder v;
      z := comfac!-to!-poly comfac u;
      u := quotf(u,z);
      u := remchkf(u,v,y);
      w := compactf2(u,mv!-reduced!-coeffs sf2mv(v,y),y);
      if termsf w < termsf u then u := w;
      % Now reduce z (required, e.g. for compact(u1*(h0+h1),{h0+h1=z1}))
      if not kernlp z
        then <<z := remchkf(z,v,y);
               w := compactf2(z,mv!-reduced!-coeffs sf2mv(v,y),y);
               if termsf w < termsf z then z := w>>;
      u := multf(z,u);
      setkorder x;
      u := reorder u;
      if !*trcompact
       then <<prin2t "*** Value on leaving compactf11:";
              mathprint mk!*sq !*f2q u>>;
      return u
   end;

symbolic procedure remchkf(u,v,vars);
   % This procedure returns u after checking if a smaller remainder
   % results after division by v.  It is potentially inefficient, since
   % we check all the way down the list, term by term.  However, the
   % process terminates when we no longer have any relevant kernels.
   (if domainp x or null intersection(kernels u,vars) then x
     else lt x .+ remchkf(red x,v,vars))
   where x=remchkf1(u,v);

symbolic procedure remchkf1(u,v);
   begin integer n;
      n := termsf u;
      v := xremf(u,v,n);
      if null v or termsf(v := car v)>=n then return u
       else if !*trcompact then prin2t "*** Remainder smaller";
      return v
   end;

symbolic procedure xremf(u,v,m);
   % Returns the quotient and remainder of U divided by V, or NIL if
   % the number of terms in the remainder exceeds M.
   % The goal is to keep terms u+terms z<=m.
   % There is some slop in the count, so one must check sizes on
   % leaving.
   begin integer m1,m2,n; scalar x,y,z;
        if domainp v then return list cdr qremd(u,v);
        m2 := termsf u;
    a:  if m<= 0 then return nil
         else if domainp u then return list addf(z,u)
         else if mvar u eq mvar v
          then if (n := ldeg u-ldeg v)<0 then return list addf(z,u)
                else <<x := qremf(lc u,lc v);
                y := multpf(lpow u,cdr x);
                m := m+m1;
                z := addf(z,y);
                m1 := termsf z;
                m := m-m1+m2;
                u := if null car x then red u
                        else addf(addf(u,multf(if n=0 then v
                                        else multpf(mvar u .** n,v),
                                        negf car x)), negf y);
                m2 := termsf u;
                m := m-m2;
                go to a>>
         else if not ordop(mvar u,mvar v) then return list addf(z,u);
        m := m+m1;
        x := xremf(lc u,v,m);
        if null x then return nil;
        z := addf(z,multpf(lpow u,car x));
        m1 := termsf z;
        m := m-m1;
        u := red u;
        go to a
   end;

symbolic procedure compactf2(u,v,vars);
  % U is standard form for expression, v for equation. W is ordered
  % list of variables in v. Result is a compacted form for u.
    if domainp u then u
     else if mvar u memq vars then compactf3(u,v,vars)
     else lpow u .* compactf2(lc u,v,vars) .+ compactf2(red u,v,vars);

symbolic procedure compactf3(u,v,vars);
   begin scalar mv!-vars!*;
      mv!-vars!* := vars;
      return mv2sf(mv!-compact(sf2mv(u,vars),v,nil),vars)
   end;

endmodule;

end;
