module smacro;  % Support for SMACRO expansion.

% Author: Anthony C. Hearn.

% Copyright (c) 1987 The RAND Corporation.  All rights reserved.

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


%
% This function expands an  invocation of a SMACRO.
%
% Getting this right in all cases seems to be harder than I had expected!
% One simple interpretation of what an SMACRO is is that it represents
% a simple textual expansion, so after
%   smacro procedure f(a,b) E;
% any instance of f(A,B) is expanded to E|a=>A,b=>B using textual
% substitution.
% A different intent for SMACRO is that it marks a procedure to be
% compiled/expanded in-line for performance reasons. The code in Reduce
% up to 3.8 implemented something that was part way between those!
%
% Here are some of the critical cases:
%    smacro procedure f a; ... a ... a...;
%    f(A)  ->   ... A ... A ...                               (a)
%      OR  ->   ((lambda (a) ... a ... a ...) A)              (b)
% The first is what textual expansion suggests, but if the argument A
% is either an expensive-to-evaluate form or has side-effects then
% letting it appear several times within the expansion may be bad either
% for semantics or performance or both. A variation on this arises if the
% formal parameter a does not occur at all within the body of the smacro,
% or is guarded there by an IF, and the actual argument has side-effects.
% Then one version of the expansion WILL evaluate the argument while the
% other will or may not.
%
% Reduce 3.8 uses expansion (a) if either the formal a occurs at most
% once in the body OR (b) the actual argument is one of a limited number
% of sorts of form that can be seen to be side-effect free. For smacros
% with two or more arguments it can lambda-lift just some of the parameters.
%
% Here are some cases where this may cause trouble:
%    smacro procedure f a; 'nothing;
%    ... f(print x) ...                   The print does not happen.
%                                         Maybe that was expected!
%    smacro procedure f a; << if nil then a; if nil then a; nil >>;
%    ... f(print x) ...
% Reduce 3.8 uses expansion (b) and so the print DOES happen.
%
% In these examples I will be using PRINT to stand for something arbitrary
% that may have side effects, might yield different results when called twice
% (including GENSYM and CONS) or might be an expensive computation.
%
%    smacro procedure f(a, b); b . a;
%    ... f(print x, print y) ...
% uses expansion (a) and the prints happen in an order that may be
% unexpected.
%    smacro procedure f(a, b); list(a, b, b);
%    ... f(print x, print y) ...
% uses a lambda at least for b, so y only gets printed once, but probably
% before x.
%
%    smacro procedure set_cdr(a, b); << rplacd(a, b); b >>;
%    ... set_cdr(x, cons(p, q)) ...
%    ... set_cdr(x, cddr x) ...
% if CONS is tagged as side-effect free this does TWO conses and the
% results are almost certainly not what is wanted. And simple inline
% expansion in the second case returns a "wrong" value.
%
%    smacro procedure f(a, b); << a := 1; print b; a := 2; print b >>;
%    ... f(v, v+3) ...
% Oh dear: v+3 is probably not tagged as side-effect free and both a and b
% are used twice in the function body. But there seems to be a clear
% expectation that the firts argument will be textually substituted so that
% the assignments take full effect.
%
%    smacro procedure f a; ... a ... (lambda (a) ...) ...;
% This might arise if a previous smacro used (b) expansion leading to the
% embedded lambda expression, and the names used for formal in the two
% smacros happened to match. If then textual substitution is performed it
% needs to understand the scope rules of nested lambdas and progs. It
% may also need to know that a symbol at the top level of a prog names a
% label not a variable (and ditto (GO x)).
%
%   smacro procedure f x; while a do print (a := cdr a);
%   x := '(1 2 3); f x; print x;
% Depending on expansion style this prints different values at the end.
%
%   smacro procedure increment a; a := a + 1;
% This illustrates a case where it is clear that a direct textual expansion
% is expected. However despite "car x := car x + 1" being accepted syntax the
% order in which things are dons means that "increment (car x)" expands to
% and illegal (setq (car x) (plus (car x) 1)) in Reduce 3.8. And
% increment (getv(x, 2)) becomes ((lambda (a) (setq a (plus a 1))) (getv x 2)).
% because while CAR is tagged as side-effect free GETV is not.
%
% Now by and large these are cases that do not arise too often when smacros
% are used for really simple things and and manually created by people who
% understand what is going on. Well the special treatment in Reduce 3.8 as
% regards how many times a formal is used in the body of the smacro and
% whether the actual argument has side effects suggests that there have been
% problems in individual cases before! But if I try to use the smacro
% mechanism as a generic way of getting in-line compilation I may scan the
% whole source of Reduce and convert small procedures into smacros. And
% then the sorts of issue discussed here bite repeatedly!

% I hope these comments will help anybody writing their own smacros. I
% MAY introduce a new keyword, say
%   inline procedure f(x); ...;
% with unambiguous call-by-value semantics, but meanwhile in any automatic
% conversion from procedure to smacro the issues here need to be thought
% about.    ACN September 2010.

symbolic procedure applsmacro(u,vals,name);
   % U is smacro body of form (lambda <varlist> <body>), VALS is
   % argument list, NAME is name of smacro.
   begin scalar body,remvars,varlist,w;
      varlist := cadr u;
      body := caddr u;
      if length varlist neq length vals
        then rerror(rlisp,15,list("Argument mismatch for SMACRO",name));
      if no!-side!-effect!-listp vals or one!-entry!-listp(varlist,body)
        then return subla!-q(pair(varlist,vals),body)
       else if length varlist>1
        then <<w := for each x in varlist collect (x . gensym());
               body := subla!-q(w,body);
               varlist := for each x in w collect cdr x>>;
      for each x in vals do
         <<if no!-side!-effectp x or one!-entryp(car varlist,body)
             then body := subla!-q(list(car varlist . x),body)
            else remvars := aconc(remvars,car varlist . x);
           varlist := cdr varlist>>;
      if null remvars then return body
       else <<w := list('lambda,
                         for each x in remvars collect car x,
                         body) .
                    for each x in remvars collect cdr x;
%             if not eqcar(cadr w,'setq)
%               then <<prin2 "*** smacro: "; print cdr w>>;
              return w>>
   end;

symbolic procedure no!-side!-effectp u;
   if atom u then numberp u or (idp u and not(fluidp u or globalp u))
    else if car u eq 'quote then t
    else if flagp(car u,'nosideeffects)
     then no!-side!-effect!-listp cdr u
    else nil;

symbolic procedure no!-side!-effect!-listp u;
   null u or no!-side!-effectp car u and no!-side!-effect!-listp cdr u;

% This list USED to have CONS in it, which would grant expansion of
% smacros the right to duplicate expressions with CONS in them - and
% firstly that would waste memory, and (worse) it causes bugs when
% in the presence of RPLACA and RPLACD.  (ACN, Sept 2010)

flag('(car cdr
       caar cadr cdar cddr
% The expansion code is willing to duplicate expressions that use things
% flagged as side-effect free. I am not certain whether the following
% are sensible to duplicate calls of...
       caaar caadr cadar caddr cdaar cdadr cddar cdddr
       ),'nosideeffects);

% Here are some more things that do not have side effects.

flag('(not null atom eq numberp fixp floatp eqcar),'nosideeffects);

symbolic procedure one!-entryp(u,v);
   % determines if id U occurs less than twice in V.
   if atom v then t
    else if smemq(u,car v)
     then if smemq(u,cdr v) then nil else one!-entryp(u,car v)
    else one!-entryp(u,cdr v);

symbolic procedure one!-entry!-listp(u,v);
   null u or one!-entryp(car u,v) and one!-entry!-listp(cdr u,v);

% This function is (also) defined in alg/general.red but is put here
% because it is needed early(ish) in the bootstrap process.

symbolic procedure delasc(u,v);
  begin scalar w;
     while v do
      <<if atom car v or u neq caar v then w := car v . w; v := cdr v>>;
     return reversip w
  end;

% I have updated subla!-q to let it cope better with nested scoped. At
% present I have not allowed for name clashed between parameters and the
% names of PROG labels,

symbolic procedure subla!-q(u,v);
% u is an association list of substitutions, as in
%     ((name1 . value1) (name2 . value2) ...)
% and v is a bit of Lisp code. Perform the substitutions throughout
% the code, but NOT within quoted items (QUOTE literal) and NOT in
% a manner that messes up embedded bindings. This latter is
% an enhancement to the code as of September 2010 to resolve issues
% that arose when trying to use many more smacros then before.
   begin scalar x;
        if null u or null v then return v
         else if atom v
                 then return if x:= atsoc(v,u) then cdr x else v
         else if car v eq 'quote or car v eq 'go then return v
         else if (eqcar(v, 'lambda) or eqcar(v, 'prog)) and
                 not atom cdr v then <<
            x := cadr v;  % (LAMBDA x . body) or (PROG x . body)
% Now the key line - discard the bindings that get hidden.
% Right now there is a residual bug in that labels in a PROG are subject
% to substitution when they should not be! I will worry about that at some
% later stage - maybe.
            for each xx in x do u := delasc(xx, u);
            x := (subla!-q(u,car v) . subla!-q(u,cdr v));
            return x >>
         else return (subla!-q(u,car v) . subla!-q(u,cdr v))
   end;


put('smacro,'macrofn,'applsmacro);


endmodule;

end;
