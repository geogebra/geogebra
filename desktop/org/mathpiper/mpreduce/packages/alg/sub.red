module sub; % Functions for substituting in standard forms.

% Author: Anthony C. Hearn.

% Copyright (c) 1991 RAND.  All rights reserved.

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


fluid '(!*nosqrts asymplis!* dmode!* errmsg!* ncmp!* sublist!* wtl!*);

% Evaluation interface.

symbolic procedure subeval u;
   % This baroque definition is needed to handle an infinite loop in
   % expressions like
   % sub(x = root_of(2log(sqrt(y^2 + 1) + 1) + 2log(sqrt(y^2 + 1) - 1)
   %                - 2log(sqrt(y^2 + 1) + 1)*a - a^2 + 4y^2 + 4,y,tag),
   %     2sqrt(x^2 + 1));
   % arising from the rule for root_of in solve/solve1.red.
   begin scalar sublist!*,x;
      put('sub,'psopfn,'subeval0);
      x := errorset2{'subeval0,mkquote u};
      put('sub,'psopfn,'subeval);
      if errorp x
        then if errmsg!* then rederr errmsg!* else rederr 'sub;
      return car x
   end;

symbolic procedure subeval0 u;
   % This is the general evaluator for SUB forms.  All but the last
   % argument are assumed to be substitutions.  These can either be
   % an explicit rule with a lhs and rhs separated by an equal sign,
   % a list of such rules, or something that evaluates to this.
   begin scalar x,y,z,ns;
   % Check for spurious substitutions.
   while cdr u do <<if not eqcar(car u,'equal) then x := car u . x
                     else if not(cadar u = (y := reval caddar u))
                      then x := {caar u,cadar u,y} . x;
                    u := cdr u>>;
   if null x then return car u else u := reversip2(x,u);
   % Separate assignments from expression.
      if u member sublist!* then return mk!*sq !*p2q mksp('sub . u,1)
       else sublist!* := u . sublist!*;
      if null(u and cdr u)
       then rederr "SUB requires at least 2 arguments"; % F.J. Wright.
      (while cdr u do
          <<x := reval car u;
            if getrtype x eq 'list then u := append(cdr x,cdr u)
             else <<if not eqexpr x then errpri2(car u,t);
                    y := cadr x;
                    if null getrtype y then y := !*a2kwoweight y;
                    if getrtype caddr x then ns := (y . caddr x) . ns
                     else z := (y . caddr x) . z;
                    u := cdr u>>>>) where !*evallhseqp=nil;
      x := aeval car u;
%     Next line only makes sense if an nssubfn existed (which it
%     currently doesn't.  However, subeval2 suffers from the problem
%     that its evaluation is sequential.
%     if ns then x := subeval2(ns,x);
      return subeval1(append(ns,z),x)
   end;

symbolic procedure subeval1(u,v);
   begin scalar y,z;
      while u and caar u = cdar u do u := cdr u;
      if null u then return v
       else if y := getrtype v
        then if z := get(y,'subfn) then return apply2(z,u,v)
              else rerror(alg,23,
                            list("No substitution defined for type",y));
        u := subsq(simp v,u);
        u := subs2 u where !*sub2 = t;   % Make sure powers are reduced.
        return mk!*sq u
   end;

% symbolic procedure subeval2(u,v);
   % This function handles sub rules that have a non *sq rhs.
   % The corresponding substitution functions are keyed by the
   % rtype in an alist stored as a property nssubfn on the rtype
   % of the expression in which the substitutions are to be carried out.
   % Substitutions are made sequentially.
%   begin scalar x,y,z;
%     for each s in u do
%       <<if null(x := getrtype v) then x := '!*sq;
%         y := getrtype cdr s;
%         if (z := get(x,'nssubfn)) and (z := atsoc(y,z))
%            then v := apply2(cdr z,s,v)
%          else v := subeval1(list s,v)>>;
%%         else rerror(alg,23,
%%            {"No substitution defined for type",y," into type ",x})>>;
%     return v
%   end;

put('sub,'psopfn,'subeval);


% Explicit substitution code for scalar expressions.

symbolic procedure subsq(u,v);
   % We need to use subs2!* to avoid say (I^4-2I^2-1)/(I^2-1) => I^2-1
   % instead of a 0/0 error.
      begin scalar x;
      x := subf(numr u,v);
      u := subf(denr u,v);
      if null numr subs2!* u
       then if null numr subs2!* x then rerror(alg,201,"0/0 formed")
             else rerror(alg,201,"Zero divisor");
      return quotsq(x,u)
   end;

symbolic procedure subs2!* u;
   (subs2 u) where !*sub2=!*sub2;

symbolic procedure subf(u,l);
   % In REDUCE 3.4, this procedure used to rebind *nosqrts to T.
   % However, this can introduce two representations of a sqrt in the
   % same calculation.  For now then, this rebinding is removed.
   begin scalar alglist!*,x,y,z;
   % Domain may have changed, so next line uses simpatom.
      if domainp u then return !*d2q u
       else if ncmp!* and noncomexpf u then return subf1(u,l);
      x := reverse intersection(for each y in l collect car y,
                                kernord(u,nil));
      x := setkorder x;
      u := subf1(reorder u,l);
%     if powlis1!* then u := subs2q u;
      % The subf code does not combine expts completely, e.g.,
      % sub(ll=2l,df(1/(1+exp(-z/(2l))),z) - df(1/(1+exp(-z/(ll))),z));
      while not(u member z)
        and (atsoc('expt,kernels numr u) or atsoc('expt,kernels denr u))
        and not((y := prepsq u) member varstack!*)
         do <<z := u . z; u := simp y>>;
      setkorder x;
      return reorder numr u ./ reorder denr u
   end;

symbolic procedure noncomexpf u;
   not domainp u
      and (noncomp mvar u or noncomexpf lc u or noncomexpf red u);

%%% SUBF1 changed so that domain elements are resimplified during a call
%%%       to RESIMP even if their tags are the same as dmode*.
%%%       This happens only if the domain is flagged

symbolic procedure subf1(u,l);
   % U is a standard form,
   % L an association list of substitutions of the form
   % (<kernel> . <substitution>).
   % Value is the standard quotient for substituted expression.
   % Algorithm used is essentially the straight method.
   % Procedure depends on explicit data structure for standard form.
   if null u then nil ./ 1
    else if domainp u
     then if atom u then if null dmode!* then u ./ 1 else simpatom u
%          else if dmode!* eq car u then !*d2q u
          else if dmode!* eq car u and
                  not flagp(dmode!*, 'resimplify) then !*d2q u
          else simp prepf u
    else begin integer n; scalar kern,l1,m,varstack!*,v,w,x,x1,xexp,y,y1,z;
        % Leaving varstack!* unchanged can make the simplifier think
        % there is a loop.
        z := nil ./ 1;
    a0: kern := mvar u;
        v := nil;
        if assoc(kern,l) and (v := assoc(kern,wtl!*)) then v := cdr v;
        if m := assoc(kern,asymplis!*) then m := cdr m;
    a:  if null u or (n := degr(u,kern))=0 then go to b
         else if null m or n<m then y := wtchk(lt u,v) . y;
        u := red u;
        go to a;
    b:  % Check for trivial substitions.
        l1 := nil;
        while l do
           <<if caar l neq cdar l then l1 := car l . l1; l := cdr l>>;
        l := reversip l1;
        if not atom kern and not atom car kern then kern := prepf kern;
        if null l then xexp := if kern eq 'k!* then 1 else kern
         else if (xexp := subsublis(l,kern)) = kern
                   and not assoc(kern,asymplis!*)
          then go to f;
    c:  w := 1 ./ 1;
        n := 0;
        % Make sure exponent is not a variable at this point.
        if y and minusp cdaar y then go to h;
        if (x := getrtype xexp) eq 'yetunknowntype
          then x:= getrtype(xexp:= eval!-yetunknowntypeexpr(xexp,nil));
        if x and not(x eq 'list)
         then typerr(list(x,xexp),"substituted expression");
        % At this point we are simplifying the expression that is
        % substituted.  Ideally, this should be done in the order
        % environment that existed when entering SUB.  However, to avoid
        % the many code changes that would imply, we make sure
        % substituted expression is evaluated in a standard order.
        % Note also that SIMP!* here causes problem with HE package --
        % We also can't use powlis1!* here, since then match x=0,x^2=1;
        % will match all powers of x to zero!
        v := setkorder nil;
        x := simp xexp;
        setkorder v;
        x := reordsq x;
        % Needed in case substitution variable is in XEXP.
        if null l and kernp x and mvar numr x eq kern then go to f
         else if null numr x then go to e;   %Substitution of 0;
        x1 := x;
        for each j in y do
         <<m := cdar j;
           if m memq frlis!*
            then <<x := simpexpt list(prepsq x1,m); m := 1>>;
           w := multsq(subs2 exptsq(x,m-n),w);
           n := m;
           z := addsq(multsq(w,subf1(cdr j,l)),z)>>;
    e:  y := nil;
        if null u then return z
         else if domainp u then return addsq(subf1(u,l),z);
        go to a0;
    f:  sub2chk kern;
        for each j in y do z := addsq(multpq(car j,subf1(cdr j,l)),z);
        go to e;
    h:  % Substitution for negative powers.
        x := simprecip list xexp;
    j:  y1 := car y . y1;
        y := cdr y;
        if y and cdaar y<0 then go to j;
    k:  m := -cdaar y1;
        w := multsq(subs2 exptsq(x,m-n),w);
        n := m;
        z := addsq(multsq(w,subf1(cdar y1,l)),z);
        y1 := cdr y1;
        if y1 then go to k else if y then go to c else go to e
     end;

symbolic procedure wtchk(u,wt);
   % If a weighted variable is substituted for, we need to remove the
   % weight of that variable in an expression.
   if null wt then u
    else (if null x then errach list("weight confusion",u,wt)
           else lt x)
         where x=quotf(u .+ nil ,!*p2f('k!* .**(wt*tdeg u)));

symbolic procedure subsublis(u,v);
   % NOTE: This definition assumes that with the exception of *SQ and
   % domain elements, expressions do not contain dotted pairs.
   begin scalar x;
      x := if x := assoc(v,u) then cdr x
              % allow for case of sub(sqrt 2=s2,atan sqrt 2).
              else if eqcar(v,'sqrt)
                 and (x := assoc(list('expt,cadr v,'(quotient 1 2)),u))
               then cdr x
              else if atom v then v
              else if not idp car v
               then for each j in v collect subsublis(u,j)
              else if x := get(car v,'subfunc) then apply2(x,u,v)
              else if get(car v,'dname) then v
              else if car v eq '!*sq then subsublis(u,prepsq cadr v)
              else for each j in v collect subsublis(u,j);
      % Could there be other cases here apart from equal?
      % This code does not seem to be necessary with current
      % subeval0 checks.
%     return if eqcar(x,'equal) then x else prepsq!* simp x
      return x
   end;

symbolic procedure subsubf(l,expn);
   % Sets up a formal SUB expression when necessary.
   begin scalar x,y;
      % This code does not seem to be necessary with current
      % subeval0 checks.
%     for each j in l do if car j neq (y := prepsq!* simp!* cdr j)
%                          then x := (car j . y) . x;
%     l := reversip x;
%     if null l then return expn;
%     y := nil;
      for each j in cddr expn do
         if (x := assoc(j,l)) then <<y := x . y; l := delete(x,l)>>;
      expn := sublis(l,car expn)
                 . for each j in cdr expn collect subsublis(l,j);
        %to ensure only opr and individual args are transformed;
      if null y then return expn;
      expn := aconc!*(for each j in reversip!* y
                     collect list('equal,car j,aeval cdr j),expn);
      return if l then subeval expn
              else mk!*sq !*p2q mksp('sub . expn,1)
   end;


% Explicit substitution code for lists.

symbolic procedure listsub(u,v);
   makelist for each x in cdr v collect subeval1(u,x);

put('list,'subfn,'listsub);

put('int,'subfunc,'subsubf);

put('df,'subfunc,'subsubf);

endmodule;

end;
