module tps; % Extendible Power Series.

% Author: Alan Barnes <barnesa@aston.ac.uk>.
% Version 1.54 January 1996.
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



% A power series object is a tagged tuple of the following form:
%
% (:ps: . [<order>,
%          <last-term>,
%          <variable>,
%          <expansion-point>,
%          <value>,
%          <terms>,
%          <ps-expression
%          <simplification flag>])
%
% <order> is the exponent of the first term of the series and is also
%          used to modify the index when accessing components of the
%          series which are addressed by power
%
% <last-term> the power of the last term generated in the series so far
%             used in evaluator when computing new terms
%
% <variable> is the dependent variable of this expansion, needed, in
%          particular, for printing and when combining two series
%
% <expansion!-point> is self-explanatory except that
%          ps!:inf denotes expansion about infinity
%
% <value>  is the originating prefix form which is needed to allow for
%          power series variables appearing inside other power series
%          expressions
%
% <terms>  is an alist containing the terms of the series computed so
%          far, access is controlled using <order> as an index base.
%
% <ps-expression> is a power series object corresponding to the prefix
%          form of which the expansion was requested, the first element
%          of which is the ps!:operator and the rest of which are the
%          ps!:operands which may themselves be pso's
%
% <simplification flag> used to indicate whether power series object
%                       needs re-simplifying = sqvar
%
% In addition we have the following degenerate forms of power series
% object:
%        <number>
%        (!:ps!: . <identifier>)  the value of <identifier> is a vector
%        as above(used in automatically generated recurrence relations)
%        <identifier>   2nd argument of DF, INT etc.
%
% The last two should never appear at top-level in any power series
% object


create!-package('(tps tpscomp tpseval tpsdom tpsfns tpsrev
                  tpssum tpsmisc tpsconv),
                '(contrib tps));

fluid '(ps!:exp!-lim  knownps ps!:level  ps!:max!-order);

% Some structure selectors and referencers.

symbolic smacro procedure rands e;
  cdr e;

symbolic smacro procedure rand1 e;
   cadr e;

symbolic smacro procedure rand2 e;
   caddr e;

symbolic smacro procedure rator e;
  car e;

symbolic smacro procedure ps!:domainp u;
   atom u or (car u neq '!:ps!:) and not listp u;

symbolic smacro procedure ps!:p u;
  pairp u and (car u = '!:ps!:);

symbolic smacro procedure ps!:atom u;
   atom u or (car u neq '!:ps!: and get(car u,'dname));

symbolic smacro procedure ps!:numberp u;
   numberp u or (pairp u and car u neq '!:ps!: and get(car u,'dname));

symbolic procedure constantpsp u;
     ps!:numberp u or ps!:expression u eq 'psconstant;

symbolic procedure ps!:getv(ps,i);
   if eqcar(ps,'!:ps!:) then
        if idp cdr ps then getv(eval cdr ps,i)
        else getv(cdr ps,i)
   else rerror(tps,1,list("PS:GETV: not a ps",ps));

symbolic procedure ps!:putv(ps,i,v);
     if eqcar(ps,'!:ps!:) then
        if idp cdr ps then putv(eval cdr ps,i,v)
        else putv(cdr ps,i,v)
     else rerror(tps,2,list("PS:PUTV: not a ps",ps));

symbolic procedure ps!:order ps;
   if ps!:atom ps then 0
   else ps!:getv(ps,0);

symbolic smacro procedure ps!:set!-order(ps,n);
   ps!:putv(ps,0,n);

symbolic procedure ps!:last!-term ps;
   if ps!:atom ps then ps!:max!-order
   else  ps!:getv(ps,1);

symbolic (ps!:max!-order:= 2147483647);
% symbolic here seems to be essential in Cambridge Lisp systems

symbolic smacro procedure ps!:set!-last!-term (ps,n);
   ps!:putv(ps,1,n);

symbolic procedure ps!:depvar ps;
   if ps!:atom ps then nil
   else ps!:getv(ps,2);

symbolic smacro procedure ps!:set!-depvar(ps,x);
   ps!:putv(ps,2,x);

symbolic procedure ps!:expansion!-point ps;
   if ps!:atom ps then nil
   else ps!:getv(ps,3);

symbolic smacro procedure ps!:set!-expansion!-point(ps,x);
   ps!:putv(ps,3,x);

symbolic procedure ps!:value ps;
   if ps!:atom ps then if ps then ps else 0
   else ps!:getv(ps,4);

symbolic smacro procedure ps!:set!-value(ps,x);
   ps!:putv(ps,4,x);

symbolic smacro procedure ps!:terms ps;
   if ps!:atom ps then list (0 . ( ps . 1))
   else ps!:getv(ps,5);

symbolic smacro procedure ps!:set!-terms(ps,x);
   ps!:putv(ps,5,x);

symbolic procedure ps!:expression ps;
   if ps!:atom ps then ps
   else ps!:getv(ps,6);

symbolic smacro procedure ps!:set!-expression(ps,x);
   ps!:putv(ps,6,x);

symbolic smacro procedure ps!:operator ps;
   car ps!:getv(ps,6);

symbolic smacro procedure ps!:operands ps;
   cdr ps!:getv(ps,6);

symbolic procedure ps!:get!-term(ps,i);
    (lambda(psorder, pslast);
       if i<psorder then nil ./ 1
       else if i>pslast then nil
       else begin scalar term;
               term:=assoc(i-psorder, ps!:terms ps);
               return if term then cdr term
                      else nil ./ 1;
            end)
    (ps!:order ps, ps!:last!-term ps);

symbolic procedure ps!:set!-term(ps,n,x);
% it is only safe to set terms of order >= order of series
% and order > last!-term of series, otherwise mathematical
% inconsistencies could arise.
% Value of last!-term now updated automatically by this procedure
begin scalar psorder, terms;
   psorder := ps!:order ps;
   if n < psorder then
      rerror(tps,3,list (n, "less than the order of ", ps))
   else if n <= ps!:last!-term ps then
      rerror(tps,4,list (n, "less than power of last term of ", ps));
   terms := ps!:terms ps;
   if atom x or (numr x neq nil) then
      % atom test is relevant only for ps!:generating!-series
      if terms then nconc(terms,list((n-psorder).x))
      else ps!:set!-terms(ps,list((n-psorder).x))
   else if n=psorder then
      ps!:set!-order(ps,n+1);
   ps!:set!-last!-term(ps,n);
end;

symbolic operator pstruncate;

symbolic procedure pstruncate(ps,n);
<< n := ieval n;
   ps := prepsqxx simp!* ps;
   if ps!:numberp ps then
       if n geq 0 then
         if atom ps then ps else apply1(get(car ps, 'prepfn), ps)
       else 0
   else if ps!:p ps then
      prep!:ps(ps, n)
   else typerr(ps, "power series:  truncate")
>>;

put('psexplim, 'simpfn, 'simppsexplim);

symbolic (ps!:exp!-lim := 6); % default depth of expansion
% symbolic here seems to be essential in Cambridge Lisp systems

symbolic procedure simppsexplim u;
 begin integer n;
    n:=ps!:exp!-lim;
    if u then ps!:exp!-lim := ieval carx(u,'psexplim);
    return (if n=0 then nil ./ 1 else n ./ 1);
 end;

symbolic procedure simpps a;
    if length a = 3 then apply('simpps1,a)
    else rerror(tps,5,
          "Args should be <FORM>,<depvar>, and <point>:  simpps");

put('ps,'simpfn,'simpps);

symbolic procedure simpps1(form,depvar,about);
  if form=nil then
     rerror(tps,6,"Args should be <FORM>,<depvar>, and <point>: simpps")
  else if not kernp simp!* depvar then
     typerr(depvar, "kernel:  simpps")
  else if  smember(depvar,(about:=prepsqxx simp!* about)) then
     rerror(tps,7,"Expansion point depends on depvar:  simpps")
  else
     begin scalar knownps, ps!:level;
        ps!:level := 0;
        return ps!:compile(ps!:presimp form, depvar,
                           if about='infinity then 'ps!:inf else about)
                    ./ 1
     end;

put('psterm,'simpfn,'simppsterm);

symbolic procedure simppsterm a;
  if length a=2 then apply('simppsterm1, a)
  else
     rerror(tps,8,
            "Args should be of form <power series>,<term>: simppsterm");

symbolic procedure simppsterm1(p,n);
 << n := ieval n;
    p := prepsqxx simp!* p;
    if ps!:numberp p then
       if n neq 0 or p=0 then nil ./ 1 else p ./ 1
    else if ps!:p p then
       << ps!:find!-order p; ps!:evaluate(p,n)>>
    else typerr(p, "power series:  simppsterm1")
 >>;

put('psorder,'simpfn,'simppsorder);
put('pssetorder,'simpfn,'simppssetorder);

symbolic procedure simppsorder u;
  << u := prepsqxx simp!* carx(u,'psorder);
     if ps!:numberp u then
        if u=0 then !*k2q 'undefined else nil ./ 1
     else if ps!:p u then
        !*n2f ps!:find!-order u ./ 1
     else typerr(u,"power series:  simppsorder")
  >>;

symbolic procedure simppssetorder u;
  (lambda (psord,ps);
    if not ps!:p ps then typerr(ps,"power series: simppssetorder")
    else if not fixp psord then
        typerr(psord, "integer: simppssetorder")
    else <<u := ps!:order ps;
           ps!:set!-order(ps,psord);
           (if u=0 then nil else u) ./ 1>>)
  (prepsqxx simp!* carx(cdr u,'pssetorder), prepsqxx simp!* car u);

put('psexpansionpt,'simpfn,'simppsexpansionpt);

symbolic procedure simppsexpansionpt u;
  << u:=prepsqxx simp!* carx(u,'psexpansionpt);
     if ps!:numberp u then !*k2q 'undefined
     else if ps!:p u then
        (lambda about;
                if about neq 'ps!:inf then
                   if about then simp!* about else !*k2q 'undefined
                else !*k2q 'infinity )
        (ps!:expansion!-point u)
     else typerr(u,"power series:  simppsexpansionpt")
  >>;

put('psdepvar,'simpfn,'simppsdepvar);

symbolic procedure simppsdepvar u;
  << u := prepsqxx simp!* carx(u,'psdepvar);
     if ps!:numberp u then !*k2q 'undefined
     else if ps!:p u then
            if (u:=ps!:depvar u) then !*k2q u else !*k2q 'undefined
     else typerr(u,"power series:  simppsdepvar")
  >>;

put('psfunction,'simpfn,'simppsfunction);

symbolic procedure simppsfunction u;
  << u := prepsqxx simp!* carx(u,'psfunction);
     if ps!:numberp u then u ./ 1
     else if ps!:p u then simp!* ps!:value u
     else typerr(u,"power series:  simppsfunction")
  >>;

symbolic procedure ps!:presimp form;
 if (pairp form) and
    ((rator form = 'expt) or (rator form = 'int)) then
        list(rator form, prepsort rand1 form, prepsort rand2 form)
 else prepsort form;

symbolic procedure prepsort u;
  % Improves log handling if logsort is defined.  S.L. Kameny.
  if getd 'logsort then logsort u else prepsqxx simp!* u;

symbolic procedure !*pre2dp u;
begin scalar x;
    u:=simp!* u;
    return  if fixp denr u then
               if denr u = 1 and domainp(x := numr u) then x
            else if fixp numr u then mkrn(numr u, denr u)
end;

flag('(!:ps!:),'full);

put('!:ps!:, 'simpfn, 'simp!:ps!:);

symbolic procedure simp!:ps!: ps;
      simp!:ps1 ps ./ 1;

symbolic procedure simp!:ps1 ps;
 if atom ps or car ps neq '!:ps!: or idp cdr ps then ps
 else if car getv(cdr ps,7) and null !*resimp then ps
 else
    begin scalar terms, simpfn, ex;
      ex := ps!:expression ps;
      if (pairp ex and rator ex ='psgen) then
           simpfn:= 'simp!:ps1
      else simpfn:= 'resimp;
      terms:=ps!:terms ps;
      % next operation depends on fact that terms are stored in an
      % association list
      ps!:set!-terms(ps,
                     foreach term in terms
                       collect (car term . apply1(simpfn, cdr term)));
      if atom ex or rator ex = 'ps!:summation then nil
      else<<ps!:set!-expression(ps,
                      (rator ex) . mapcar(cdr ex, 'simp!:ps1));
            ps!:set!-value(ps,prepsqxx simp!* ps!:value ps)>>;
      putv(cdr ps,7,!*sqvar!*);
      return ps
    end;

put('pschangevar,'simpfn,'simppschangevar);

symbolic procedure simppschangevar u;
  (lambda (newvar, ps, oldvar);
     if not ps!:p ps then
        typerr(ps,"power series: simppschangevar")
     else if not kernp newvar then
        typerr(prepsqxx newvar, "kernel: simppschangevar")
     else
        <<oldvar:= ps!:depvar ps;
          newvar:= prepsqxx newvar;
          if smember(newvar,ps!:value ps) and newvar neq oldvar then
               rerror(tps,9,"Series depends on new variable")
          else if oldvar then
             <<ps!:set!-depvar(ps,newvar);
               ps!:set!-value(ps, subst(newvar,oldvar,ps!:value ps));
               ps ./ 1
             >>
          else rerror(tps,10,"Can't change variable of constant series")
        >>)
   (simp!* carx(cdr u,'pschangevar), prepsqxx simp!* car u,nil);

endmodule;

end;
