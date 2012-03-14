module pmintrfc;  % Interface for pattern matcher.

% Author: Kevin McIsaac.
% Changes by Rainer M. Schoepf 1991.

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


% For some reason, this doesn't like being compiled as a module.

% REDUCE syntax for pattern matching.
%
% ?a
%   This is an ordinary pattern matching variable. It can any value.
%
% ??a
%   This is a segment pattern variable. I can take any value as does ?a
%   or a set of values.
%
% ?a_=cond
%   ?a can only be matched is the condition does not evaluate to false
%
% exp1 -> exp2
%   exp1 is replaced by exp2
%
% exp1 --> exp2
%   exp1 is replaced by exp2, RHS is quoted. exp2 is simplified after
%   replacement
%
% M(exp,pat)
%   Returns a list of replacements for pm variables in pat such that pat
%   and exp are equal. Where defined the properties of symmetry, assoc-
%   iativity and the identity element are used to match the expressions.
%
% S(exp,rep,rpt:1,depth:Inf) or S(exp,{rep1,rep2,...},rpt:1,depth:Inf)
%  The lhs of rep is matched against exp and subexpressions of exp.
%  When a match is found the replacements for pm variables in rhs are
%  substituted into the lhs and the resultant expression is used as a
%  replacement.  This is done to a maximum (tree) depth of dept, with a
%  maximum number of repeats rpt, to a (tree) depth of dept.

% S(exp,rep,depth:Inf) or S(exp,{rep1,rep2,...},depth:Inf)
%   Shorthand notation for S with Inf number of rpt's
%
% exp1 :- exp2
%   exp1 is  added  to a  global  list of  automatic  replacements. Most
%   specific  rules are ordered  before less specific  rules.  If a rule
%   already exists the the rule is replaced unless exp2 is null in which
%   case the rule is deleted.
%
% exp1 ::- exp2
%   as above except the RHS is quoted.
%
fluid '(!*trpm rpt subfg!* substitution varstack!*);

switch trpm;

put('m,'psopfn,'mx);

symbolic procedure mx u; pm_m1(reval car u,reval cadr u);

symbolic procedure pm_m1(exp, temp);
   begin scalar substitution, mmatch, count, freevars;
      count := 0;
      freevars := idsort union(findnewvars temp,nil);
      substitution := if freevars then freevars else t;
      for each j in freevars do newenv j;
      mmatch := amatch(temp, exp, t, nil);
      for each j in freevars do restorenv j;
      if mmatch then return
         if freevars then 'list . for each j in pair(freevars, mmatch)
                                      collect list('rep, car j, cdr j)
         else t
   end;

symbolic procedure fixreplist(repset);
   % Check that repset is properly formed and add multi-generic
   % variables to assoc functions.
   begin scalar replist;
      if car(repset) memq '(rep repd) then replist := list(repset)
       else replist := cdr repset;
      replist := for each rep in replist collect fixrep(rep);
      return replist
   end;

Comment It is necessary to replace all free variables by unique ones
        in order to avoid confusion during the superset operation.
        To this end we generate replace them by special gensyms
        before putting them in the rules database.  This is not
        visible to the user;

fluid '(pm!:gensym!-count!*);

symbolic (pm!:gensym!-count!* := 0);

symbolic procedure pm!:gensym;
  compress ('!? . '!_ .
    explode (pm!:gensym!-count!* := pm!:gensym!-count!* + 1));

fluid '(freevarlist!*);

symbolic procedure make!-unique!-freevars form;
  if atom form then
    if get(form,'gen) then begin scalar x;
      x := atsoc (form, freevarlist!*);
      if null x then << x := (form . pm!:gensym());
                        put (cdr x, 'gen, t);
                        freevarlist!* := x .  freevarlist!*>>;
      return cdr x
     end
     else form
    else for each x in form collect make!-unique!-freevars x;

symbolic procedure fixrep(repl);
   << (repl := make!-unique!-freevars repl) where freevarlist!* := nil;
   % Should check if the extra multi-generic variables are required.
   if flagp(caadr repl,'assoc) then
      if flagp(caadr repl,'symmetric) then
         list(car repl,append(cadr repl,list('!?!?!;)),
            list(caadr repl,caddr repl,'!?!?!;))
      else
         list(car repl,caadr(repl) .
              ('!?!?!^ . append(cdadr repl,list('!?!?!;))),
            list(caadr repl,'!?!?!^,caddr repl,'!?!?!;))
   else repl >>;


put('s,'psopfn,'sx);

symbolic procedure sx arg;
   % Fill in args for s0. Default: repeat 1, depth Inf.
   reval
      s0(reval car arg, reval cadr arg,
            if cddr arg then reval caddr arg  else 1,
            if cddr arg and cdddr arg then reval car cdddr arg
             else 'inf);

put('si,'psopfn,'si!-x);

symbolic procedure si!-x arg;
   % Fill in args for s0. Default: repeat Inf, depth Inf.
   reval
      s0(reval car arg,reval cadr arg, 'inf,
            if cddr arg then reval caddr arg  else 'inf);

symbolic procedure s0(exp, repset,rpt,depth);
   % Breadth first search.  Rpt is passed as a fluid.
   if length repset <= 1 or not memq(car repset,'(rep repd list))
     then exp
    else if (depth neq 'inf and depth < 0)
          or (rpt neq 'inf and rpt <=0)  or atom(exp) then exp
    else sbreadth(exp,fixreplist repset,depth) ;

symbolic procedure sbreadth(exp,replist,depth);
   % Substitute a set of replacements into  the root expression until
   % expression stops changing, then recurse on all the sub expressions.
   <<exp:= sroot(exp,replist);
     if (depth neq 'inf and depth <= 0)
        or (rpt neq 'inf and rpt <=0)  or atom(exp) then exp
      else ssbreadth(exp,replist,
                     if depth neq 'inf then depth-1 else depth)>>;

symbolic procedure ssbreadth(exp,replist,depth);
   begin scalar newexp, new,  reps;
      if (depth neq 'inf and depth < 0)
          or (rpt neq 'inf and rpt <= 0)  or atom(exp) then return exp;
      repeat
      begin
         new := nil;
         reps := replist;
       a:   exp := reval for each subexp in exp collect
               << newexp := sroot1(subexp,car reps) ;
                  new := new or (subexp neq newexp);
                  newexp
               >>;
         if not (new or null(reps := cdr reps)) then go to a;
      end
      until(atom exp or not new);
      return
         if (depth neq 'inf and depth <= 0)
             or (rpt neq 'inf and rpt <= 0)  or atom(exp) then exp
         else for each subexp in exp collect
               ssbreadth(subexp,replist,
                         if depth neq 'inf then depth-1 else depth)
   end;


put('sd,'psopfn,'sdx);

symbolic procedure sdx arg;
   % Fill in args for sd0. Default: repeat 1, depth inf.
   reval
      sd0(reval car arg,reval  cadr arg,
            if cddr arg then reval caddr arg  else 1,
            if cddr arg and cdddr arg then reval car cdddr arg
             else 'inf);

put('sdi,'psopfn,'sdi);

symbolic procedure sdi arg;
   % Fill in args for sd0. Default: repeat Inf, depth Inf.
   reval
      sd0(reval car arg,reval  cadr arg, 'inf,
            if cddr arg then reval caddr arg  else 'inf);

symbolic procedure sd0(exp, repset,rpt,depth);
   % Depth first search.
   if length repset <= 1 or not memq(car repset,'(rep repd list))
     then exp
    else if (depth neq 'inf and depth < 0)
       or (rpt neq 'inf and rpt <= 0)  or atom(exp) then exp
    else sdepth(exp,fixreplist repset,depth) ;

symbolic procedure sdepth(exp,replist,depth);
   <<exp:= sroot(exp,replist);
     if (depth neq 'inf and depth <= 0)
        or (rpt neq 'inf and rpt <= 0)  or atom(exp) then exp
      else car(exp) . for each subterm in cdr exp collect
                sdepth(subterm,replist,
                       if depth neq 'inf then depth-1 else depth)>>;

symbolic procedure sroot(exp,replist);
   % Substitute a set of replacements into a root expression until the
   % expression stops changing.  When a replacement succeeds the
   % substitution process restarts on the new expression at the
   % beginning of the replacement list.
   begin scalar oldexp, reps;
      if  (rpt neq 'inf and rpt <=0)  or atom(exp) then return exp;
      repeat
       begin
         oldexp := exp;
         reps := replist;
       a:   exp := sroot1(exp,car reps);
         if not(exp neq oldexp or null(reps := cdr reps)) then go to a;
         if exp neq oldexp then exp := reval exp
       end
      until(atom exp or exp eq oldexp);
      return exp;
   end;

symbolic procedure sroot1(exp,rep);
   % Try to substitute a single replacement into a root expression once
   % only.
   begin scalar freevars,substitution,mmatch;
      if (rpt neq 'inf and rpt <=0)  or
           atom(exp) or (car(exp) neq caadr(rep)) then return exp;
      freevars := union(findnewvars cadr rep,nil);
      substitution := caddr rep;
      for each j in freevars do newenv j;
      if !*trpm then <<write("Trying rule  "); rprint(rep);
                       write("against      "); rprint(exp)>>;
      mmatch := amatch(cadr rep, exp, t,nil);
      if !*trpm
        then <<if mmatch then <<write("producing    ");
                              rprint(mmatch := embed!-null!-fn mmatch)>>
                else <<write("failed"); terpri()>>;
               terpri()>>;
      for each j in freevars do restorenv j;
      return if mmatch then
             << if (rpt neq 'inf) then rpt := rpt - 1;
                embed!-null!-fn mmatch>>
             else exp
   end;

symbolic procedure embed!-null!-fn u;
   if atom u then u
    else for each j in u conc
       if atom j then list(j)
        else if car j eq 'null!-fn then embed!-null!-fn cdr j
        else list(embed!-null!-fn j);

algebraic operator null!-fn;

% Code for printing null-fn(a,b,...) as [a,b,...]. Modeled on LIST code.

put('null!-fn,'prifn,'null!-fn!-pri);

fluid '(orig!* posn!*);

symbolic procedure null!-fn!-pri l;
   % This definition is basically that of INPRINT, except that it
   % decides when to split at the comma by looking at the size of
   % the argument.
   (begin scalar split,u;
      u := l;
      l := cdr l;
      prin2!* "[";
      orig!* := if posn!*<18 then posn!* else orig!*+3;
      if null l then go to b;
      split := treesizep(l,40);   % 40 is arbitrary choice.
   a: maprint(negnumberchk car l,0);
      l := cdr l;
      if null l then go to b;
      oprin '!*comma!*;
      if split then terpri!* t;
      go to a;
   b: prin2!* "]";
      return u
   end)
    where orig!* := orig!*;

% Assignments and automatic replacements.

symbolic operator rset;

symbolic procedure rset(temp,exp);
   % Add new rule to rule list. If RHS is null then delete rule.
   if atom temp then setk(temp,exp)
    else begin scalar oldsubfg!*,varstack!*;
             %rebind subfg. Don't do this do that(yuck..lisp..)
             % rebind varstack!* since the template is simplified again
      oldsubfg!* := subfg!*; subfg!* := nil;
      temp := reval temp;
      put(car temp,'opmtch,
          rinsert(fixrep('rset . list(temp,exp)),
                  get(car temp,'opmtch)));
      subfg!* := oldsubfg!*;
      return exp
    end;

symbolic operator rsetd;

symbolic procedure rsetd(temp,exp);
   % Delayed version.
   if atom temp then 'hold . setk(temp,exp)
    else 'hold . list
      begin scalar oldsubfg!*,varstack!*;
           %rebind subfg. Don't do this do that(yuck..lisp..)
         oldsubfg!* := subfg!*; subfg!* := nil;
         temp := reval temp;
         put(car temp,'opmtch,
             rinsert(fixrep('rsetd . list(temp,exp)),
                     get(car temp,'opmtch)));
         subfg!* := oldsubfg!*;
         return exp
      end;

symbolic procedure rinsert(rule,rulelist);
% Insert rule in rule list so that most specific rules are found first.
% Use super-set idea, due to Grief.  If an equivalent rule exits then
% replace with new rule.  A new rule will be placed as far down the rule
% list as possible If the RHS of rule is nil then delete the rule.

if null rulelist or not atom caar rulelist then rule . rulelist
   else
     (lambda ss;
       if ss eq 'equal then
          if cadr rule then rule . cdr(rulelist)
          else cdr(rulelist)
       else if ss eq 't then rule . rulelist
       else car(rulelist) . rinsert(rule,cdr rulelist))
     superset(cadar rulelist,cadr rule);

symbolic procedure superset(temp1,temp2);
   begin scalar mmatch;
      mmatch := pm_m1(temp2,temp1);
      return(
         if null mmatch then nil
         else if mmatch eq 't then 'equal
         else if not bound2gen(cdr mmatch) then t
         else if null (mmatch := pm_m1(temp1,temp1)) then  t
         else 'equal)
   end;

symbolic procedure bound2gen(replist);
   % True if all Generic variables are bound to generic variables.
   null replist or (genp(caddar replist) and bound2gen(cdr replist));

symbolic operator arep;

symbolic procedure arep(replist);
   % Add the replacements in replist to the list of automatically
   % applied replacements.
   if atom replist then replist
    else if car replist eq 'rep
     then list('rset ,cadr replist,caddr replist)
    else if car replist eq 'repd
     then list('rsetd,cadr replist,caddr replist)
    else if car replist eq 'list then
%    '!*set!* . for each rep in cdr replist collect arep(rep)
     'list . for each rep in cdr replist collect arep(rep)
    else nil;

symbolic operator drep;

symbolic procedure drep(replist);
   % Delete the replacements in replist from the list of automatically
   % applied replacements.
   if atom replist then replist
    else if car replist eq 'rep  then list('rset ,cadr replist,nil)
    else if car replist eq 'repd then list('rsetd,cadr replist,nil)
    else if car replist eq 'list then
%     '!*set!*.for each rep in cdr replist collect Drep(rep)
     'list . for each rep in cdr replist collect drep(rep)
    else nil;

symbolic procedure opmtch(exp);
   begin scalar oldexp, replist, rpt;
      rpt := 'inf;
      replist := get(car exp, 'opmtch);
      if null(replist) or null subfg!* then return nil;
      oldexp := exp;
      repeat
      exp := if (atom caar replist) then sroot1(exp, car replist)
             else oldmtch(exp,car replist)
      until (exp neq oldexp or null(replist := cdr replist));
      return if exp eq oldexp then nil else exp
   end;

symbolic procedure oldmtch(exp,rule);
   begin scalar x, y;
      y := mcharg(cdr exp, car rule,car exp);
      while (y and null x) do
      <<x := if eval subla(car y,cdadr rule)
               then subla(car y,caddr rule);
        y := cdr y>>;
      return if x then x else exp
   end;

put('!?,'gen,t);

put('!?!?!;,'mgen,t);

put('!?!?!$,'mgen,t);

put('!?!?!^,'mgen,t);

symbolic operator prop!-alg;

newtok '((!_) prop!-alg);

symbolic procedure prop!-alg(f);
   begin scalar x;
      x := prop f;
      while x do <<prin2(car x); prin2("  "); print(cadr x); print(" ");
                   x := cddr x>>
   end;

symbolic operator preceq;

symbolic procedure preceq(u,v);
   % Give u same precedence as v.
   <<put(u,'op,get(v,'op));
     put(u,'infix,get(v,'infix));>>;

newtok '((!: !- )    rset);
newtok '((!: !: !- ) rsetd);
newtok '((!- !>)    rep);
newtok '((!- !- !>) repd);
newtok '((!_ !=) such!-that);

flag ('(such!-that), 'spaced);  % _ adjacent to symbols causes problems.

algebraic;

infix :-;
nosimp(:-,'(t nil));
%precedence :-,:=;  %can't do this

infix ::-;
nosimp(::-,'(t t));
precedence rsetd,rset;

infix ->;
precedence ->,rsetd;

infix -->;
nosimp(-->,'(nil t));
precedence -->,->;

infix _=;
nosimp(_=,'(nil t));
precedence _=,-->;

operator hold;
nosimp(hold,t);
flag('(rset rsetd rep repd such!-that), 'right);
preceq(rsetd,rset);
preceq(-->,->);

flag('(plus times expt),'assoc);

endmodule;

end;
