module tpscomp; % Compile prefix expression into network of
                % communicating power series.

% Authors: Julian Padget & Alan Barnes

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


% The compiler is rule driven by looking for a compilation rule (crule)
% property on the property list of the operator.  If a rule does not
% exist the expression is differentiated to get an expression which is
% amenable to compilation but the process takes care to check for the
% existence of cycles in the derivatives e.g. sine and cosine.
%
% The result is an power series object which can be evaluated by the
% power series evaluator.

%fluid '(unknowns !*exp knownps ps!:max!-order ps!:specials dfdx);
fluid '(unknowns !*exp  knownps ps!:max!-order ps!:specials
        ps!:level ps!:max!-level);

ps!:specials := list('psrev, 'pscomp, 'int);

symbolic (ps!:max!-level:= 20);

symbolic procedure ps!:compile(form,depvar,about);
  if idp form then
          make!-ps!-id(form,depvar,about)
   else  if ps!:numberp form then form
   else if ps!:p form then
      if (ps!:expansion!-point form=about)and(ps!:depvar form=depvar)
        then form
      else ps!:compile(ps!:value form,depvar,about)
   else if memq(rator form, ps!:specials) then
      apply(get(car form,'ps!:crule), list(form,depvar,about))
   else (if dfdx=0 then
           << about:=(rator form).(foreach arg in rands form collect
                           if ps!:p arg then
                              << ps!:find!-order arg;
                                 prepsq ps!:evaluate(arg,0)>>
                           else subst(about,depvar,arg));
              make!-constantps(simp!* about, form, depvar)>>
         else if get(car form,'ps!:crule) then
                apply(get(car form,'ps!:crule),list(form,depvar,about))
         else (if tmp then '!:ps!:  .  cdr tmp
%               else ps!:unknown!-crule(form, depvar, about))
               else ps!:unknown!-crule((car form) .
                                       foreach arg in cdr form collect
                                          ps!:compile(arg,depvar,about),
                                       depvar,about))
                 where tmp = assoc(form,knownps))
      where dfdx=prepsqxx simp!* list('df,ps!:arg!-values form, depvar);

symbolic procedure make!-ps!-id(id,depvar,about);
begin scalar ps;
  ps:=make!-ps(id,id,depvar,about);
  if id=depvar then
      if about='ps!:inf then <<
         ps!:set!-order(ps, -1);
         ps!:set!-terms(ps, list (0 . (1 ./ 1)))>>
      else <<
         about :=  if idp about then !*k2q about
                   else if ps!:numberp about then !*n2f about ./ 1
                   else simp!* about;
         if numr about then <<
            ps!:set!-order(ps, 0);
            ps!:set!-terms(ps, list(0 . about, 1 . (1 ./ 1)))>>
         else <<
            ps!:set!-order(ps, 1);
            ps!:set!-terms(ps, list(0 . (1 ./ 1)))>>
       >>
  else <<
     ps!:set!-order(ps, 0);
     ps!:set!-terms(ps, list(0 .  !*k2q id))>>;
  ps!:set!-last!-term(ps,ps!:max!-order);
  return ps
end;

symbolic procedure make!-constantps(u,v,d);
% u is a constant standard quotient, v is a corresponding prefix form
begin scalar ps;
      ps:=get('tps,'tag) . mkvect 7;
      ps!:set!-order(ps,0);
      ps!:set!-expression(ps, 'psconstant);
      ps!:set!-value(ps, v);
      ps!:set!-last!-term(ps,ps!:max!-order);
      ps!:set!-terms(ps,list(0 . u));
      ps!:set!-depvar(ps,d);
      putv(cdr ps, 7, !*sqvar!*);
      return ps
end;

symbolic procedure make!-ps(form,exp,depvar,about);
begin scalar ps;
      ps:=get('tps,'tag) . mkvect 7;
      ps!:set!-order(ps,0);
      ps!:set!-expression(ps,form);
      ps!:set!-value(ps,exp);
      ps!:set!-depvar(ps,depvar);
      ps!:set!-expansion!-point(ps,about);
      ps!:set!-last!-term(ps,-1);
      putv(cdr ps, 7, !*sqvar!*);
      return ps;
end;

symbolic procedure ps!:plus!-crule(a,d,n);
begin scalar pluses, minuses;
   foreach term in rands a do
      if pairp term and rator term = 'minus  then
           minuses := rand1 term . minuses
      else
           pluses := term . pluses;

   if not null pluses then <<
     if not null cdr pluses then
        pluses := make!-ps('plus . foreach term in pluses collect
                                      ps!:compile(term,d,n),
                           ps!:arg!-values('plus . pluses),d,n)
     else
        pluses := ps!:compile(car pluses,d,n);
     ps!:find!-order pluses>>;

   if not null minuses then <<
     if not null cdr minuses then
        minuses := make!-ps('plus . foreach term in minuses collect
                                      ps!:compile(term,d,n),
                            ps!:arg!-values('plus . minuses),d,n)
     else
        minuses := ps!:compile(car minuses,d,n);
     ps!:find!-order minuses>>;

   if null minuses then
       return pluses
   else if null pluses then
       a:= (make!-ps(ps, ps!:arg!-values ps,d,n)
               where ps = 'minus . list minuses)
   else a:= (make!-ps(ps, ps!:arg!-values ps, d,n)
               where ps = 'difference . list(pluses, minuses));
   ps!:find!-order a;
   return a;
end;

put('plus,'ps!:crule,'ps!:plus!-crule);

symbolic procedure ps!:unary!-crule(a,d,n);
   make!-ps(list(rator a,ps!:compile(rand1 a,d,n)),
                 ps!:arg!-values a,d,n);

symbolic procedure ps!:minus!-crule(a,d,n);
 if ps!:numberp cadr a then
    !:minus cadr a
 else  ps!:unary!-crule(a,d,n);

put('minus,'ps!:crule, 'ps!:minus!-crule);
put('sqrt,'ps!:crule,'ps!:unary!-crule);
put('cbrt,'ps!:crule,'ps!:unary!-crule);

symbolic procedure ps!:binary!-crule(a,d,n);
<<a :=  make!-ps((rator a) . list(ps!:compile(rand1 a,d,n),
                                                          ps!:compile(rand2 a,d,n)),
                      ps!:arg!-values a,d,n);
  ps!:find!-order a;
  a>>;

put('difference,'ps!:crule,'ps!:binary!-crule);

symbolic procedure ps!:nary!-crule(a,d,n);
% called from ps!:times!-crule so args are already power series
<<if null cdddr a then
     a := make!-ps(list(rator a,rand1 a,rand2 a), ps!:arg!-values a,d,n)
  else
     a:= make!-ps(list(rator a,rand1 a,
                       ps!:nary!-crule((rator a) . cddr a,d,n)),
                   ps!:arg!-values a,d,n);
  ps!:find!-order a;
  a>>;


symbolic procedure ps!:times!-crule(a,d,n);
begin scalar prod, variables, constants;
   prod := foreach arg in rands a collect ps!:compile(arg,d,n);
   foreach arg in prod do
      if ps!:numberp arg or
         (not idp cdr arg and ps!:expression arg = 'psconstant) then
           constants := arg . constants
      else variables := arg . variables;
   if not null variables then
      if null cdr variables then
         variables := car variables
      else variables := ps!:nary!-crule('times . variables, d, n);
   if null constants then
      return variables
   else <<
      prod := 1 ./  1;
      foreach arg in constants do
          prod := multsq(prod, if ps!:numberp arg then
                                                     (if arg=0 then nil else arg) ./ 1
                               else ps!:get!-term(arg,0));
      if variables then
         a:= make!-ps(list('psmult, prod, variables),
                      ps!:arg!-values a,d,n)
      else
         return make!-constantps(prod, ps!:arg!-values a, d);
      ps!:find!-order a;
      return a>>;
end;

put('times,'ps!:crule,'ps!:times!-crule);
put('quotient,'ps!:crule,'ps!:quotient!-crule);

symbolic procedure ps!:quotient!-crule(a,d,n);
% forms such as (quotient (expt <x> <y>) (expt <x> <z>)) are
% detected here and transformed into (expt <x>(difference <y> <z>)) to
% help avoid certain essential singularities
begin scalar r1, r2;
  r1 := rand1 a; r2 := rand2 a;
  if eqcar(r1,'expt) and eqcar(r2,'expt) and
        ((rand1 r1)=(rand1 r2)) then
    return ps!:compile(list('expt, rand1 r1,
                            list('difference, rand2 r1, rand2 r2)),
                        d,n);
    r1:=ps!:compile(rand1 a, d, n);
    if (ps!:numberp r1 or
        (not idp cdr r1 and ps!:expression r1 = 'psconstant))
           and eqcar(r2, 'expt) then
     << r2:=ps!:compile(list('expt,rand1 r2,prepsqxx simpminus cddr r2),
                         d,n);
      return
             if onep r1 then r2
        else <<
           a := make!-ps(list('psmult, if ps!:numberp r1 then r1 ./ 1
                                       else ps!:get!-term(r1,0),
                               r2),
                         ps!:arg!-values a,d,n);
           ps!:find!-order a;
             a>>
      >>;
    r2:=ps!:compile(rand2 a, d, n);

    if ps!:numberp r2 or
         (not idp cdr r2 and ps!:expression r2 = 'psconstant) then
      << r2 := if ps!:numberp r2 then 1 ./ r2
               else invsq ps!:get!-term(r2,0);
         a:= make!-ps(list('psmult, r2, r1), ps!:arg!-values a,d,n)>>
    else
         a:= make!-ps(list('quotient, r1, r2), ps!:arg!-values a,d,n);
    ps!:find!-order a;
    return a;
end;

symbolic procedure ps!:int!-crule(a,d,n);
begin scalar r,arg1, psord, intvar;
  intvar := rand2 a;
  if not idp intvar then
    typerr(intvar, "kernel: ps!:int!-crule");
  if depends(intvar, n) then
    rerror(tps,11,
       "Can't integrate series when expansion point is non-constant ");
  arg1:=ps!:compile(prepsqxx simp!* rand1 a,d,n);
  r:= make!-ps(list('int,arg1,intvar), ps!:arg!-values a,d,n);
  psord:= ps!:find!-order arg1;
  if d=intvar then
     if ps!:expansion!-point(arg1) neq 'ps!:inf then
       <<if (psord < 0) and (ps!:evaluate(arg1,-1) neq (nil ./ 1)) then
           rerror(tps,12,"Logarithmic Singularity")>>
     else  % expansion about infinity
         if (psord < 2) and (ps!:evaluate(arg1,1) neq (nil ./ 1)) then
           rerror(tps,13,"Logarithmic Singularity at Infinity");
  ps!:find!-order r;
  return r;
end;

put('int,'ps!:crule,'ps!:int!-crule);

symbolic procedure ps!:log!-crule(a,d,n);
begin scalar r, dfdx, f;
  f := ps!:compile(rand1 a, d, n);
  if ps!:order f neq 0 then
        rerror(tps,14, "Logarithmic Singularity");
  dfdx := ps!:compile(prepsq simp!* list('df, f, d), d, n);
  r := ps!:compile(list('quotient, dfdx, f), d, n);
  r := make!-ps(list('int, r, d), ps!:arg!-values a, d, n);
  ps!:set!-term(r,0, simp!* list('log, prepsq ps!:get!-term(f,0)));
  ps!:find!-order r;
  return r;
end;

put('log,'ps!:crule, 'ps!:log!-crule);

symbolic procedure ps!:arg!-values funct;
  (rator funct) . (foreach arg in rands funct collect
                     if ps!:atom arg then arg
                     else if ps!:p arg then ps!:value arg
                     else ps!:arg!-values arg);

symbolic procedure ps!:unknown!-crule(a,d,n);
% unknowns is an alist structure, the CAR of which is the
% form which was differentiated and the CDR is a dotted pair whose
% CDR is a gensym'ed identifier which is used to build
% the cyclic structures used to represent a recurrence relation.

   (lambda (aval,tmp);
     if (tmp:=assoc(aval, unknowns)) then  '!:ps!: . cdr tmp
     else if ps!:level > ps!:max!-level then
             rerror(tps,15, "Recursion too deep in ps!:unknown!-crule")
     else
        (lambda(dfdx, unknowns);
           (lambda(r, s); <<
              ps!:level:=ps!:level+1;
              %intern s;  %  not needed, but useful for debugging.
              global list s;  % This is definitely needed in UOLISP.
% it is important to set s before recursing to find the power series
% expansion of dfdx as this may involve evaluating s
              set(s,cdr r);
% it is also important to determine the first non-zero term of the
% series (assumed to be of order >= 0) before recursing in case
% the original series is encountered again in the recursion
              ps!:unknown!-term1(r, a);
              dfdx := ps!:compile(dfdx,d,n);
% the next test is intended to detect the case when a function f(x)
% (say) is expanded about a point x=a (say) at which f has a pole or
% essential singuarity, but where the Reduce simplifier returns a
% seemingly well-defined value for f when x=a.
              if ps!:order dfdx < 0 then
                 rerror(tps, 16, "Pole or Logarithmic Singularity");
              ps!:set!-expression(r,list('int, dfdx, d));
              knownps:=(aval . s) . knownps;
              ps!:level:=ps!:level-1;
              r
            >> )
            (make!-ps(nil,aval,d,n), cdar unknowns))
            (ps!:differentiate(a,d), (aval . gensym()) . unknowns)
   )
    (ps!:arg!-values a,nil);

symbolic procedure ps!:unknown!-term1(ps,a);
% There is an implicit assumption that the order of the series >=0 here
begin scalar psord, term, about, infmult, x;
   psord := 0;
   about := ps!:expansion!-point ps;
   x := ps!:depvar ps;
loop:
   term := simp!* ps!:first!-term a;
   ps!:set!-term(ps, psord, term);
   if numr term = nil then <<
      psord := psord+1;
      if psord > ps!:max!-order then
         rerror('tps, 17, list(ps!:value ps,
                                            "has zero expansion to order",
                                psord));
      a := list('quotient, list('df, a, x), psord);
      if about = 'ps!:inf then <<
         if psord = 1 then
            infmult := ps!:compile(list('minus, list('times, x, x)),
                                   x, about);
         a := list('times, infmult, a)>>;
      a := prepsqxx simp!* a;
      go loop>>;
end;

symbolic procedure ps!:first!-term(l);
  if atom l then  l
  else if ps!:p l then
     if ps!:find!-order l < 0 then
          rederr "Possible essential singularity"
     else prepsqxx ps!:get!-term(l,0)
  else
    car l . foreach arg in cdr l collect ps!:first!-term arg;

symbolic procedure ps!:differentiate(a,v);
   (lambda x;
      if eqcar(x,'df) then
         rerror(tps,18,
           list("ps:differentiate: no rule to differentiate function",
                 car a, "when it has", length a - 1, "arguments"))
      else
         x)
   ((lambda (!*exp);
        prepsqxx simp!* list ('df, a, v)) nil);


symbolic procedure ps!:expt!-crule(a,d,n);
% we will assume that forms like (expt (expt <x> <y>) <z>) will
% continue to be transformed by SIMP!* into (expt <x> (times <y> <z>))
% this is very important for the avoidance of essential singularities
%
% If the exponent is equivalent to a rational number there is a
% convenient algorithm for exponentiation. So use it, otherwise
% use a^b = exp(b*log a) and use the algorithm for exp(power-series)
%
begin scalar eflg,exp1,exp2,b,psvalue;
   b := rand1 a;
   if not ps!:p b or constantpsp b then
      eflg := evalequal(b,prepsq simp!* aeval 'e);
   exp1:=rand2 a;
   if (ps!:p exp1 and constantpsp exp1) then exp1:=ps!:value exp1;
   begin scalar alglist!*, dmode!*;
        exp2:=simp!* exp1;
   end;
   psvalue:=ps!:arg!-values a;
   if (atom numr exp2 and atom denr exp2) then
        <<exp1:=numr exp2; exp2:=denr exp2>>
   else return
     << exp2 := ps!:compile(if eflg then exp1
                            else list('times, exp1, list('log,b)), d,n);
        make!-ps(list('exp, exp2), psvalue, d, n)>>;
   b := ps!:compile(b,d,n);
   if exp2=1 then
      if exp1=nil then
         return if ps!:zerop!: b
                then rerror(tps,19,"0**0 formed: ps:expt-crule")
                else 1
      else if exp1=1 then return b
      else if exp1=2 then
         a := make!-ps(list('times,b,b),psvalue,d,n)
      else if exp1 = -1 then
         a:= make!-ps(list('quotient,1,b),psvalue,d,n)
      else a := make!-ps(list('expt,b,exp1,1),psvalue,d,n)
   else a := make!-ps(list('expt,b,exp1,exp2),psvalue,d,n);
   ps!:find!-order a;
   return a;
end;

put('expt,'ps!:crule,'ps!:expt!-crule);

endmodule;

end;
