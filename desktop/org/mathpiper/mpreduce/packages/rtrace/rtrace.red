module rtrace$  % Portable REDUCE tracing

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


% $Id: rtrace.red 1.5 1999-03-14 10:16:26+00 fjw Exp $

% Based on rdebug by Herbert Melenk, June 1994

% Portability, enhanced interface and updating by
% Francis J. Wright, Time-stamp: <14 March 1999>

% This package currently implements only entry-exit tracing,
% assignment tracing and rule tracing.  (The latter was portable
% already.)  It does not implement breakpoints or conditional tracing,
% and the variable trprinter!* is not used.  However, it adds tracing
% of inactive rules identified by being assigned to variables
% (trrlid/untrrlid).  This package is completely independent of any
% tracing facilities provided by the underlying Lisp system.

% To do:
%   eval autoload stubs before tracing, e.g. for int.
%   improve showrules
%   return something useful from the trace commands?
%   merge trrl and trrlid?
%   provide more intelligible synonyms?

switch rtrace$  !*rtrace := t$

% When this switch is on then algebraic-mode output is used if
% possible; when it is off then Lisp output is used.

% The following are REDUCE commands that accept a sequence of operator
% names.  The command `rtr int, foo' causes the input and output of
% the underlying procedures, probably simpint and foo, to be traced.
% The command `rtrst int, foo' causes both the I/O and the assignments
% to be traced.  The command `unrtr int, foo' removes all tracing, and
% `unrtrst' is a synonym for `unrtr'.

symbolic macro procedure rtr fns;
   %% Trace the procedures underlying the named operators.
   rtr!*('rtrace, fns)$

symbolic macro procedure unrtr fns;
   %% Untrace the procedures underlying the named operators.
   rtr!*('unrtrace, fns)$

symbolic macro procedure rtrst fns;
   %% Traceset the procedures underlying the named operators.
   rtr!*('rtraceset, fns)$

symbolic macro procedure unrtrst fns;
   %% Untrace the procedures underlying the named operators.
   rtr!*('unrtrace, fns)$

flag('(rtr rtrst unrtr unrtrst), 'noform)$
deflist('((rtr rlis) (rtrst rlis) (unrtr rlis) (unrtrst rlis)), 'stat)$

symbolic procedure rtr!*(trfn, fns);
   {trfn, mkquote for each fn in cdr fns collect
      get(fn, 'simpfn) or get(fn, 'psopfn) or fn}$

%% The following are Lisp functions that accept quoted lists of
%% procedure names, cf. traceset in
%% /reduce/lisp/csl/cslbase/compat.lsp:
symbolic procedure rtrace L;
   mapcar(L, function rtrace1)$
symbolic procedure unrtrace L;
   mapcar(L, function unrtrace1)$

fluid '(!*rtrace!-setq)$
% !*comp is pre-declared, to be fluid in CSL and global in PSL!

symbolic procedure rtraceset L;
   mapcar(L, function rtrace1) where !*rtrace!-setq = t$

symbolic procedure rtrace1(name);
   %% Trace or traceset the specified procedure.
   %% name must be quoted when called!
   begin scalar defn, args, !*redefmsg;
      if null(defn := getd name) then return
         write "***** ", name, " not yet defined! ";
      if !*comp then <<
         write "Portable tracing does not work reliably with the";
         write " switch `comp' on, so it has been turned off! ";
         off comp
      >>;
      if eqcar(defn, 'expr) and eqcar(cdr defn, 'lambda) then
         %% cf. traceset in /reduce/lisp/csl/cslbase/compat.lsp
         if eqcar(cadddr defn, 'run!-rtraced!-procedure) then return
            if  flagp(name, 'rtraced!-setq) eq !*rtrace!-setq
               %% i.e. both true or both false
            then write "*** ", name, " already traced! "
            else re!-rtrace1(name)
         else args := caddr defn
      else <<
         if !*rtrace!-setq then <<
            write "*** ", name,
               " must be interpreted for portable assignment tracing! ";
            terpri();
            write "*** Tracing arguments and return value only.";
            terpri();
            !*rtrace!-setq := nil
         >>;
         if (args := get(name, 'number!-of!-args)) then <<
            args := for i := 1 : args collect mkid('!A!r!g, i);
            write "*** ", name, " is compiled: ",
               "portable tracing may not show recursive calls! ";
            terpri();
         >> else <<
            write "***** ", name,
               " must be interpreted for portable tracing! ";
            terpri();
            return
         >>;
      >>;
      if !*rtrace!-setq then <<
         defn := subst('rtraced!-setq, 'setq,
            subst('rtraced!-setk, 'setk, defn));
         flag({name}, 'rtraced!-setq)
      >> else % in case procedure has been redefined:
         remflag({name}, 'rtraced!-setq);
      put(name, 'rtraced!-procedure, defn);
      return eval {'de, name, args,
         {'run!-rtraced!-procedure, mkquote name, mkquote args,
            'list . args}}
   end$

symbolic procedure re!-rtrace1(name);
   %% Toggle trace/traceset of named procedure.
   %% name must be quoted when called!
   begin scalar defn;
      defn := get(name, 'rtraced!-procedure);
      if !*rtrace!-setq then <<
         defn := subst('rtraced!-setq, 'setq,
            subst('rtraced!-setk, 'setk, defn));
         flag({name}, 'rtraced!-setq)
      >> else <<
         defn := subst('setq, 'rtraced!-setq,
            subst('setk, 'rtraced!-setk, defn));
         remflag({name}, 'rtraced!-setq)
      >>;
      put(name, 'rtraced!-procedure, defn);
      write "*** Trace mode of ", name, " changed.";
      return name
   end$

symbolic procedure unrtrace1(name);
   %% Remove all tracing.
   %% name must be quoted when called!
   begin scalar defn, !*redefmsg;
      if (defn := remprop(name, 'rtraced!-procedure)) then <<
         defn := subst('setq, 'rtraced!-setq,
            subst('setk, 'rtraced!-setk, defn));
         putd(name, car defn, cdr defn);
      >>;
      remflag({name}, 'rtraced!-setq);
      return name
   end$

fluid '(rtrace!-depth)$  rtrace!-depth := 0$

global '(rtrout!*)$
% Default is nil, meaning trace to the terminal.

rlistat '(rtrout)$
symbolic procedure rtrout files;
   <<
      if rtrout!* then close(rtrout!*);
      rtrout!* := if car files then open(car files, 'output);
      nil
   >>$

symbolic procedure run!-rtraced!-procedure(name, argnames, args);
   (begin scalar old!-handle, result;
      result := cdr get(name, 'rtraced!-procedure);
      old!-handle := wrs rtrout!*;
      write "Enter (", rtrace!-depth, ") ", name;  terpri();
      for each arg in args do <<
         write "   ", car argnames, ":  ";
         rtrace!-print arg;
         argnames := cdr argnames
      >>;
      wrs old!-handle;
      %% result := apply(cdr get(name, 'rtraced!-procedure), args);
      result :=
         errorset!*({'apply, mkquote result, mkquote args}, nil);
      if errorp result then rederr EMSG!* else result := car result;
      wrs rtrout!*;
      write "Leave (", rtrace!-depth, ") ", name, " = ";
      rtrace!-print result;
      wrs old!-handle;
      return result
   end) where rtrace!-depth = add1 rtrace!-depth$

symbolic procedure rtrace!-print arg;
   if !*rtrace then rdbprint arg else print arg$

symbolic procedure rtrace!-setq!-print arg;
   begin scalar old!-handle;
      old!-handle := wrs rtrout!*;
      rtrace!-print arg;
      wrs old!-handle;
      return arg
   end$

symbolic macro procedure rtraced!-setq u;
   %% For symbolic assignments.
   %% Must avoid evaluating the lhs of the assignment, and evaluate
   %% the rhs only once in case of side effects (such as a gensym).
   begin scalar left, right, old!-handle;
      left := cadr u;
      right := caddr u;
      old!-handle := wrs rtrout!*;
      write left, " := ";
      wrs old!-handle;
      %% Handle nested setq calls carefully:
      return if eqcar(right, 'rtraced!-setq) then
         {'setq, left, right}
      else
         {'rtrace!-setq!-print, {'setq, left, right}}
   end$

symbolic procedure rtraced!-setk(left, right);
   %% For algebraic assignments.
   begin scalar old!-handle;
      old!-handle := wrs rtrout!*;
      write left, " := ";  rtrace!-print right;
      wrs old!-handle;
      return setk(left, right)
   end$

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% The following is based closely on PSL code by
% Herbert Melenk, June 1994

% assgnpri autoloads and is defined in /reduce/src/mathpr/mprint.red
% writepri is defined in terms of assgnpri.

%------------------------------------------------------------------
% Print algebraic expressions by REDUCE printer.

fluid '(trlimit)$
share trlimit$
trlimit := 5$

symbolic procedure rdbprint u;
   % Try to print an expression u as algebraic expression rather than
   % LISP internal style.
   << algpri1(u,0);  assgnpri("",nil,'last) >> where !*nat = nil$

symbolic procedure algpri1(u,n);
   begin scalar r;
      n := n+1;
      if (r := algpriform u) then return algpri2 r;
      algpri2 "[";
      while u do
         if atom u then << algpri2 ".";  algpri2 u;  u := nil >>
         else <<
            algpri1(car u,n);
            u := cdr u;  n := n+1;
            if pairp u then algpri2 ",";
            if n > trlimit then << algpri2 " ...";  u := nil >>
         >>;
      algpri2 "]";
   end$

symbolic procedure algpriform u;
   % is expression printable in algebraic mode?
   if atom u then u else
      if get(car u,'prifn) or get(car u,'pprifn) then u else
         if eqcar(u,'!*sq) then prepsq cadr u else
            if is!-algebraic!? u then u else
               if get(car u,'prepfn) then prepf u else
                  if is!-sform!? u then prepf u else
                     if is!-sfquot!? u then prepsq u$

symbolic procedure is!-algebraic!? u;
   atom u or get(car u,'dname)
      or (get(car u,'simpfn) or get(car u,'psopfn))
         and algebraic!-args cdr u$

symbolic procedure algebraic!-args u;
   null u or is!-algebraic!? car u and algebraic!-args cdr u$

symbolic procedure is!-sform!? u;
   if atom u then t else
      if get(car u,'dname) then t else
         pairp car u and pairp caar u and
            (is!-algebraic!? mvar u or is!-sform!? mvar u)
               and fixp ldeg u and ldeg u>0
                  and is!-sform!? lc u and is!-sform!? red u$

symbolic procedure is!-sfquot!? u;
   pairp u and is!-sform!? numr u and is!-sform!? denr u$

symbolic procedure algpri2 u;
   assgnpri(u,nil,nil)$                 % where !*nat=nil;

%------------------------------------------------------------------
% RULE Trace

symbolic procedure rdbprin2 u;
   algpri1(u,0) where !*nat = nil$

symbolic procedure rule!-trprint!* u;
   begin scalar r;
      rdbprin2 "Rule ";
      rdbprin2 car u; %name
      if cadr u then << rdbprin2 "."; rdbprin2 cadr u >>;
      rdbprin2 ": ";
      rdbprin2 caddr u;
      rdbprin2 " => ";
      rdbprint(r := cadddr u);
      return reval r
   end$

put('rule!-trprint,'psopfn,'rule!-trprint!*)$


%% FJW: Redefine put!-kvalue and put!-avalue in module forall to
%% prevent them detecting spurious recursive simplification errors.
%% Rebind the variable !*recursiveerror to nil to turn off recursive
%% simplification error checking.

fluid '(!*recursiveerror)$
symbolic(!*recursiveerror := t)$

begin scalar !*redefmsg;

   symbolic procedure put!-kvalue(u,v,w,x);
      % This definition is needed to allow p(2) := sqrt(1-p^2).
      if !*recursiveerror and
         (if eqcar(x,'!*sq) then sq_member(w,cadr x) else smember(w,x))
      then recursiveerror w
      else put(u,'kvalue,aconc(v,{w,x}));

   symbolic procedure put!-avalue(u,v,w);
      % This definition allows for an assignment such as a := a 4.
      if v eq 'scalar
      then if eqcar(w,'!*sq) and sq_member(u,cadr w)
      then recursiveerror u
      else if !*reduce4 then putobject(u,w,'generic)
      else put(u,'avalue,{v,w})
      else if !*recursiveerror and smember(u,w) then recursiveerror u
      else put(u,'avalue,{v,w});

end$


fluid '(trace!-rules!*)$

symbolic procedure trrl w;
   for each u in w do
   begin scalar name, rs, rsx, n, !*recursiveerror;
      rs := reval u;
      if idp u then <<
         %% FJW: Take care not to trace a traced rule:
         if assoc(u, trace!-rules!*) then <<
            %% terpri();
            write "*** rules for ", u, " already traced! ";
            terpri();
            return
         >>;
         name := u;
         if rs=u then                   % unassigned operator name?
            rs := showrules u;
         if atom rs or car rs neq 'list or null cdr rs then
            rederr {"could not find rules for", u}
      >> else <<
         name := intern gensym();       % MUST be interned for later use!!!
         prin2 "*** using name ";
         prin2 name;
         prin2t " for rule list"
      >>;
      if eqcar(rs,'list) then
         << rs := cdr rs; n := 1 >>     % rule list
      else
         << rs := {rs}; n := nil >>;    % single rule
      rsx := trrules1(name,n,rs);
      trace!-rules!* := {name,rs,rsx} . trace!-rules!*;
      %% FJW: Should do this only if rules already in effect (?):
      %% algebraic clearrules ('list.rs);
      clearrules ('list . rs);
      %% algebraic let ('list.rsx);
      let ('list . rsx);
      return name
   end$

symbolic procedure trrlid w;
   %% FJW: Trace unset rules assigned to variables.
   for each u in w do
   begin scalar rs, rsx, name, !*recursiveerror;
      if not idp u then typerr(u, "rule list identifier");
      %% Take care not to trace a traced rule:
      if assoc(u, trace!-rules!*) then <<
         %% terpri();
         write "*** rules for ", u, " already traced! ";
         terpri();
         return
      >>;
      rs := reval u;
      if atom rs or car rs neq 'list or null cdr rs then
         typerr(u, "rule list identifier");
      rs := cdr rs;
%%       %% Convert u to string to avoid apparent recursive application
%%       %% of the rule (and prepend "id "):
%%       name := compress(append('(!" i d ! ),append(explode u,'(!"))));
      name := u;
      rsx := trrules1(name, 1, rs);
      trace!-rules!* := {u, rs, rsx} . trace!-rules!*;
      setk0(u, 'list.rsx)  % algebraic assignment to atom u
   end$

put('trrl,'stat,'rlis)$
put('trrlid,'stat,'rlis)$

symbolic procedure trrules1(name, n, rs);
   begin scalar rl, nrl, rh, lh;
      rl := car rs;  rs := cdr rs;
      if atom rl or not memq(car rl, '(replaceby equal)) then
         typerr(rl, 'rule);
      lh := cadr rl;  rh := caddr rl;
      %% Ignore "constant rules" like log(e) => 1:
      if constant_exprp lh then go to a;
      rh := if eqcar(rh, 'when) then
         {'when, {'rule!-trprint, name, n, lh, cadr rh}, caddr rh}
      else {'rule!-trprint,name, n, lh, rh};
   a: nrl := {car rl, lh, rh};
      return if null rs then {nrl} else
         nrl . trrules1(name, n+1, rs)
   end$

symbolic procedure untrrl u;
   begin scalar w, v;
      for each r in u do <<
         w := if idp r then
            assoc(r, trace!-rules!*) or % rule (list) name
            assoc(showrules r, trace!-rules!*) % operator name
         else                           % explicit rule (list)
            assoc!!2(if eqcar(r,'list) then cdr r else r . nil,
               trace!-rules!*);
         if w then <<
            %% The `let' and `clearrules' commands have peculiar
            %% properties, so the following explicit assignments to
            %% `v' are necessary!
            v := 'list . caddr w;  clearrules v;
            v := 'list . cadr w;   let v;
            trace!-rules!* := delete(w, trace!-rules!*)
         >>
         else write "*** rule ", r, " not found"
      >>
   end$

symbolic procedure assoc!!2(u, v);
   %% Finds key U in second element of an element of alist V, and
   %% returns that element or NIL.
   if null v then nil
   else if u = cadar v then car v
   else assoc!!2(u, cdr v)$

symbolic procedure untrrlid u;
   %% FJW: Untrace inactive rules assigned to variables.
   begin scalar w;
      for each r in u do <<
         if not idp r then typerr(r, "rule list identifier");
         w := assoc(r, trace!-rules!*);
         if w then <<
            setk0(r, 'list.cadr w);  % algebraic assignment to atom r
            trace!-rules!* := delete(w, trace!-rules!*)
         >>
      >>
   end$

put('untrrl,'stat,'rlis)$
put('untrrlid,'stat,'rlis)$

% Make 'rule!-trprint invisible when printed.

put('rule!-trprint, 'prifn,
   function(lambda(u); maprin car cddddr u))$

put('rule!-trprint, 'fancy!-prifn,
   function(lambda(u); fancy!-maprin car cddddr u))$

endmodule$

end$
