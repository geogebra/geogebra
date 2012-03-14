module fixsubf;

% Author: James H. Davenport.

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


fluid '(!*nosubs asymplis!* dmode!* ncmp!*);

% The standard version of SUBF messes with the order of variables before
% calling SUBF1, something we can't afford, so we define a new version.

symbolic procedure algint!-subf(a,b); algint!-subf1(a,b);

symbolic procedure algint!-subsq(u,v);
   !*multsq(algint!-subf(numr u,v),!*invsq algint!-subf(denr u,v));

symbolic procedure algint!-subf1(u,l);
   %U is a standard form,
   %L an association list of substitutions of the form
   %(<kernel> . <substitution>).
   %Value is the standard quotient for substituted expression.
   %Algorithm used is essentially the straight method.
   %Procedure depends on explicit data structure for standard form;
   if domainp u
     then if atom u then if null dmode!* then u ./ 1 else simpatom u
          else if dmode!* eq car u then !*d2q u
          else simp prepf u
    else begin integer n; scalar kern,m,w,x,xexp,y,y1,z;
        z := nil ./ 1;
    a0: kern := mvar u;
        if m := assoc(kern,asymplis!*) then m := cdr m;
    a:  if null u or (n := degr(u,kern))=0 then go to b
         else if null m or n<m then y := lt u . y;
        u := red u;
        go to a;
    b:  if not atom kern and not atom car kern then kern := prepf kern;
        if null l then xexp := if kern eq 'k!* then 1 else kern
         else if (xexp := algint!-subsublis(l,kern)) = kern
                   and not assoc(kern,asymplis!*)
          then go to f;
    c:  w := 1 ./ 1;
        n := 0;
        if y and cdaar y<0 then go to h;
        if (x := getrtype xexp) then typerr(x,"substituted expression");
        x := simp!* xexp;
        % SIMP!* here causes problem with HE package in subf,
        % but we probably need the extra power of simp!*
        x := reorder numr x ./ reorder denr x;
        % needed in case substitution variable is in XEXP;
        if null l and kernp x and mvar numr x eq kern then go to f
         else if null numr x then go to e;   %Substitution of 0;
        for each j in y do
         <<m := cdar j;
           w := !*multsq(!*exptsq(x,m-n),w);
           n := m;
           z := !*addsq(!*multsq(w,algint!-subf1(cdr j,l)),z)>>;
    e:  y := nil;
        if null u then return z
         else if domainp u then return !*addsq(algint!-subf1(u,l),z);
        go to a0;
    f:  sub2chk kern;
        for each j in y do
           z := !*addsq(!*multsq(!*f2q !*p2f car j,
                                 algint!-subf1(cdr j,l)),z);
        go to e;
    h:  %Substitution for negative powers;
        x := simprecip list xexp;
    j:  y1 := car y . y1;
        y := cdr y;
        if y and cdaar y<0 then go to j;
    k:  m := -cdaar y1;
        w := !*multsq(!*exptsq(x,m-n),w);
        n := m;
        z := !*addsq(!*multsq(w,algint!-subf1(cdar y1,l)),z);
        y1 := cdr y1;
        if y1 then go to k else if y then go to c else go to e
     end;

symbolic procedure algint!-subsublis(u,v);
   begin scalar x;
      return if x := assoc(v,u) then cdr x
              else if atom v then v
              else if car v eq '!*sq then
                      list('!*sq,algint!-subsq(cadr v,u),caddr v)
%    Previous two lines added by JHD 7 July 1982.
%    without them, CDRs in SQ expressions buried inside;
%    !*SQ forms are lost;
              else if x := get(car v,'subfunc) then apply2(x,u,v)
              else for each j in v collect algint!-subsublis(u,j)
   end;

put('int,'subfunc,'algint!-subsubf);

symbolic procedure algint!-subsubf(l,expn);
   %Sets up a formal SUB expression when necessary;
   begin scalar x,y;
      for each j in cddr expn do
         if (x := assoc(j,l)) then <<y := x . y; l := delete(x,l)>>;
      expn := sublis(l,car expn)
                 . for each j in cdr expn
                       collect algint!-subsublis(l,j);
        %to ensure only opr and individual args are transformed;
      if null y then return expn;
      expn := aconc!*(for each j in reversip!* y
                     collect list('equal,car j,aeval cdr j),expn);
      return mk!*sq if l then algint!-simpsub expn
                     else !*p2q mksp('sub . expn,1)
   end;

symbolic procedure algint!-simpsub u;
   begin scalar !*nosubs,w,x,z;
    a:  if null cdr u
          then <<if getrtype car u or eqcar(car u,'equal)
                   then typerr(car u,"scalar");
                 u := simp!* car u;
                 z := reversip!* z;   % to put replacements in same
                                      % order as input.
                 return quotsq(algint!-subf(numr u,z),
                               algint!-subf(denr u,z))>>;
        !*nosubs := t;  % We don't want left side of eqns to change.
        w := reval car u;
        !*nosubs := nil;
        if getrtype w eq 'list
          then <<u := append(cdr w,cdr u); go to a>>
         else if not eqexpr w then errpri2(car u,t);
        x := cadr w;
        if null getrtype x then x := !*a2k x;
        z := (x . caddr w) . z;
        u := cdr u;
        go to a;
   end;

endmodule;

end;
