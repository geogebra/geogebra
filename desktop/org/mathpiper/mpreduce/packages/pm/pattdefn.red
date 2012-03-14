module pattdefn; %Notational conveniences and low level routines for the
                 % UNIFY code.

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


fluid('(freevars op r p i upb
        identity expand acontract mcontract comb count symm ))$

% Binding routines.  These would be more efficient with a more direct
% mechanism.

symbolic procedure bind(u, v);         %push the value of v onto the
   put(u,'binding,v.get(u,'binding))$   %binding stack of u

symbolic procedure binding(u);              %Top most binding on stack
   (lambda x; if x then car x) get(u,'binding)$

symbolic procedure unbind(u);                  %pop binding off stack
   put(u,'binding, cdr get(u,'binding))$

symbolic procedure newenv(u);           % Mark a new environment.
   bind(u, 'unbound)$                   % Give UNIFY lexical scoping.

symbolic procedure restorenv(u);        % Should include error checks?
   unbind(u)$

symbolic procedure pm!:free(u);       % Is u a pm unbound free variable?
   binding(u) eq 'unbound$

symbolic procedure bound(u);           % Is u a pm bound free variable?
   (lambda x;  x and (x neq 'unbound)) binding u;

symbolic procedure meq(u,v);
 (lambda x;
%    (if (x and (x neq 'unbound)) then x else u) eq meval v )
     (if (x and (x neq 'unbound)) then x else u) = v)
          binding u;

% This has been fixed.
% symbolic procedure meval(u);
%    if eqcar(u,'minus) and numberp cadr u then -cadr u else u;


% Currently Mval does nothing.  It should be defined so that nosimp
% functions are handled properly.  By leaving it out the PM will not
% dynamically change pattern it is working on.  I.e.,
% m(f(1,2,3+c),f(?a,?b,?a+?b+?c)) will now return True.  If the code
% commented out is restored then this will give the expected result.
% However m(f(1_=natp 1),f(?a_=natp ?a)), where natp(?x) :- t, will not
% work.

symbolic procedure mval(u); u;
%===>   if not atom u then (reval bsubs(car u)) . cdr u
%===>   else bsubs u;

symbolic procedure bsubs(u);
   % Replaces free atoms by their bindings.  Would be nice to mark
   % expressions that no longer contain bunbound free variables
   if null u then u
   else if atom u then if bound(u) then binding u else u
   else for each j in u collect bsubs j;

symbolic procedure ident(op);
get(op,'identity)$

symbolic procedure genp(u);
   atom u and (get(u,'gen) or mgenp(u))$

symbolic procedure mgenp(u);
   atom u and get(u,'mgen)$

symbolic procedure suchp u;             %Is this a such that condition?
   not atom u and car u eq 'such!-that$

% False if any SUCH conditions are in wich all free variable are bound
% does not simplify to T.  Should we return free expressions partially
% simplified?

symbolic procedure chk u;
null u or u eq t or
(lambda x;
   if freexp(x) then
      (lambda y; if null y then nil
                 else if y eq t then list x
                 else x.y) chk(cdr u)
   else if reval(x) eq t then chk(cdr u) else nil) bsubs car u$

symbolic procedure findnewvars u;
   if atom u then if genp u then list u else nil
    else for each j in u conc findnewvars j;

symbolic procedure freexp u;
   if atom u then pm!:free u else freexp car u or freexp cdr u;

symbolic procedure genexp u;
   if atom u then genp u else genexp car u or genexp cdr u;

endmodule;

end;
