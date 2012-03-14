module showrule; % Display rules for an operator.

% Author: Herbert Melenk, ZIB, Berlin. E-mail: melenk@zib.de.
% Copyright (c) 1992 ZIB Berlin. All rights reserved.

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


% Modified by: Francis J. Wright <F.J.Wright@Maths.QMW.ac.uk>
% Time-stamp: <10 November 1998>

% $Id: showrule.red 1.2 1998-11-10 08:33:09+00 fjw Exp $

global '(!*match);

fluid '(asymplis!* powlis!*);

% All let-rules for an operator are collected as rule set.

% Usage in algebraic mode:
%  e.g. SHOWRULES SIN;
% The rules for exponentiation can be listed by
%       SHOWRULES EXPT;

symbolic procedure showrules opr;
   begin scalar r;
     r := showruleskvalue opr;
     r:=append(r,showrulesopmtch opr);
     r:=append(r,showrules!*match opr);
     r:=append(r,showrulesdfn opr);
     if opr eq 'expt then
        <<r:=append(r,showrulespowlis!*());
          r:=append(r,showrulespowlis1!*());
          r:=append(r,showrulesasymplis!*())>>
     else
        %% FJW: Show rules for powers of opr:
        <<r:=append(r,showrulespowlis!*opr opr);
          r:=append(r,showrulespowlis1!*opr opr);
          r:=append(r,showrulesasymplis!*opr opr)>>;
       return 'list.r;
   end;

symbolic procedure showruleskvalue opr;
  for each rule in get(opr,'kvalue) collect
   begin scalar pattern, vars, target;
      pattern := car rule;
      vars := selectletvars pattern;
      vars := arbvars vars;
      pattern := subla(vars,pattern);
      target := cadr rule;
      target := subla(vars,target);
      return mkrule(nil,pattern,target)
   end;

symbolic procedure showonerule(test,pattern,target);
   % central routine produces one rule.
   begin scalar vars;
      vars := selectletvars pattern;
      vars := arbvars vars;
      pattern := subla(vars,pattern);
      test := subla(vars,test);
      target := subla(vars,target);
      test := simpletsymbolic test;
      if test=t then test:=nil;
      %% target := simpletsymbolic target;
      %% FJW: mangles lists in target, e.g. for hypergeometric, but
      %% not applying simpletsymbolic might not be the right fix!
      return mkrule(test,pattern,target)
   end;

symbolic procedure showrulesopmtch opr;
   for each rule in get(opr,'opmtch) collect
      showonerule(cdadr rule,opr . car rule,caddr rule);

symbolic procedure showrulesdfn opr;
   append(showrulesdfn1 opr, showrulesdfn2 opr);

symbolic procedure showrulesdfn1 opr;
   for i:=1:5 join showrulesdfn1!*(opr,i);

symbolic procedure showrulesdfn1!*(opr,n);
   % simple derivatives
   begin scalar dfn,pl,rule,pattern,target;
    dfn:=dfn_prop(for j:=0:n collect j);
    if(pl:=get(opr,dfn)) then return
     for j:=1:n join
      if (rule:=nth(pl,j)) then
      <<  pattern := car rule;
          pattern := {'df,opr . pattern,nth(pattern,j)};
          target := cdr rule;
          {showonerule(nil,pattern,target)}
      >>;
   end;

symbolic procedure mkrule(c,a,b);
  <<b:=strip!~ b; c:=strip!~ c;
    {'replaceby,separate!~ a,if c then {'when,b,c} else b}>>;

symbolic procedure strip!~ u;
   if null u then u else
   if idp u then
     (if eqcar(w,'!~) then intern compress cdr w else u)
         where w=explode2 u
   else if atom u then u
   else if car u = '!~ then strip!~ cadr u
   else strip!~ car u . strip!~ cdr u;

symbolic procedure separate!~ u;
   if null u or u='!~ then u else
   if idp u then
     (if eqcar(w,'!~) then {'!~,intern compress cdr w} else u)
         where w=explode2 u
   else if atom u then u
   else separate!~ car u . separate!~ cdr u;


symbolic procedure showrulesdfn2 opr;
   % collect possible rules from df
   for each rule in get('df,'opmtch) join
      if eqcar(caar rule,opr) then
    {showonerule(cdadr rule,'df . car rule,caddr rule)};

symbolic procedure showrules!*match opr;
  for each rule in !*match join if smember(opr,rule) then
   begin scalar pattern,target,test,p1,p2;
       pattern := car rule;
       p1 := car pattern;
       p2 := cadr pattern;
       pattern := list('times,prepsq !*p2q p1,
                                prepsq !*p2q p2);
       test := cdadr rule;
       target := caddr rule;
       return {showonerule(test,pattern,target)}
     end;

symbolic procedure showrulespowlis!*();
 for each rule in powlis!* collect
   begin scalar pattern,target;
      pattern := list ('expt,car rule,cadr rule);
      target := cadddr rule;
      return mkrule(nil,pattern,target);
   end;

symbolic procedure showrulespowlis1!*();
 for each rule in powlis1!* collect
   begin scalar pattern,target,test,p1,p2;
      pattern := car rule;
      p1 := car pattern;
      p2 := cdr pattern;
      pattern := list ('expt, p1, p2);
      test := cdadr rule;
      target := caddr rule;
      return showonerule(test,pattern,target);
    end;

symbolic procedure showrulesasymplis!*();
   for each rule in asymplis!* collect
      mkrule(nil,{'expt,car rule,cdr rule},0);

symbolic procedure showrulespowlis!*opr opr;
   %% FJW: Pick rules in powlis!* for operator opr:
   for each rule in powlis!* join
      if eqcar(car rule, opr) then
      begin scalar pattern,target;
         pattern := list ('expt,car rule,cadr rule);
         target := cadddr rule;
         return mkrule(nil,pattern,target) . nil
      end;

symbolic procedure showrulespowlis1!*opr opr;
   %% FJW: Pick rules in powlis1!* for operator opr:
   for each rule in powlis1!* join
      if eqcar(caar rule, opr) then
      begin scalar pattern,target,test,p1,p2;
         pattern := car rule;
         p1 := car pattern;
         p2 := cdr pattern;
         pattern := list ('expt, p1, p2);
         test := cdadr rule;
         target := caddr rule;
         return showonerule(test,pattern,target) . nil
       end;

symbolic procedure showrulesasymplis!*opr opr;
   %% FJW: Pick rules in asymplis!* for operator opr:
   for each rule in asymplis!* join
      if eqcar(car rule, opr) then
         mkrule(nil,{'expt,car rule,cdr rule},0) . nil;

symbolic procedure selectletvars u;
     if null u then nil else
     if memq(u,frlis!*) then {u} else
     if atom u then nil else
     union (selectletvars car u, selectletvars cdr u);

symbolic procedure simpletsymbolic u;
    if atom u then u else
    if car u eq 'quote then simpletsymbolic cadr u else
    if car u memq '(aeval reval revalx boolvalue!*) then
       if needs!-lisp!-tag cadr u
         then {'symbolic,simpletsymbolic cadr u}
        else simpletsymbolic cadr u
     else
    if car u eq 'list then simpletsymbolic cdr u else
    if isboolfn car u then simpletsymbolic (isboolfn car u . cdr u)
       else simpletsymbolic car u . simpletsymbolic cdr u;

symbolic procedure needs!-lisp!-tag u;
   if numberp u then nil else
   if atom u then t else
   if car u memq '(aeval reval revalx boolvalue!* quote) then nil else
   if car u eq 'list then needs!-lisp!-tag1 cdr u
   else if car u eq 'cons then
        needs!-lisp!-tag cadr u or needs!-lisp!-tag caddr u
   else t;

symbolic procedure needs!-lisp!-tag1 u;
  if null u then nil else
    needs!-lisp!-tag car u or needs!-lisp!-tag1 cdr u;

fluid '(bool!-functions!*);

bool!-functions!* :=
  for each x in {'equal,'greaterp,'lessp,'geq,'leq,'neq,'numberp}
      collect get(x,'boolfn).x;

symbolic procedure isboolfn u;
    if idp u and (u:=assoc(u,bool!-functions!*)) then cdr u;

symbolic procedure arbvars vars;
  for each var in vars collect
         var . {'!~, intern compress cddr explode var};

symbolic operator showrules;

endmodule;

end;
