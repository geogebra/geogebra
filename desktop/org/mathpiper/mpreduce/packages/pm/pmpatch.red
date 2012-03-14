module pmpatch; % Patches to make pattern matcher run in REDUCE 3.4.

% Author: Kevin McIsaac.
% Changes by Rainer M .Schoepf

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


% remflag('(evenp),'opfn);

% remprop('list,'evfn);

% remprop('list,'rtypefn);

% Redefine LISTEVAL so that the arguments are always returned in prefix
% form.

global '(simpcount!* simplimit!*);

symbolic procedure listeval(u,v);
   <<if (simpcount!* := simpcount!*+1)>simplimit!*
       then <<simpcount!* := 0;
              rederr "Simplification recursion too deep">>;
     u := if atom u
            then listeval(if flagp(u,'share) then eval u
                           else cadr get(u,'avalue),v)
           else car u . for each x in cdr u collect reval1(x,t);
     simpcount!* := simpcount!*-1;
     u>>;


% Allow EXPR as a keyword in patterns.

% remprop('expr,'stat);

% Make REVAL of an equation return a simplified value.

fluid '(substitution);

symbolic procedure equalreval u;
  if null substitution then 'equal . car u . list reval cadr u
   else if evalequal(car u,cadr u) then t
   else 0;

% Define function to prevent simplification of arguments of symbolic
% operators.
% If the i'th element of `list' is `nil' then the i'th argument of `fn'
% is left unsimplified by simp.  If `list' is longer that the argument
% list of `fn' then the extra indicators are ignored.  If `list' is
% shorter than the argument list of `fn' then the remaining arguments
% are simplified, eq nosimp(cat,'(nil T nil)) will cause the 1 and third
% arguments of the functions `cat' to be left un simplified.

symbolic procedure nosimp(fn,list);
  <<put(fn, 'nosimp, list);>>;

symbolic operator nosimp;

flag('(nosimp), 'noval);

symbolic procedure fnreval(u,v,mode);
   % Simplify list u according to list v. If mode is NIL use AEVAL
   % else use REVAL.
   if null u then nil
    else if v eq t then u
    else if null v then for each j in u collect reval1(j ,mode)
    else ((if car v then car u
           else reval1(car u, mode)) . fnreval(cdr u,cdr v,mode));

% Next two routines are changes to module SIMP to add NOSIMP code.

symbolic procedure opfneval u;
   lispeval(car u . for each j in
                  (if flagp(car u,'noval) then cdr u
                  else fnreval(cdr u,get(car u,'nosimp),t))
                            collect mkquote j);

fluid '(ncmp!* subfg!*);

symbolic procedure simpiden u;
   % Convert the operator expression U to a standard quotient.
   % Note: we must use PREPSQXX and not PREPSQ* here, since the REVOP1
   % in SUBS3T uses PREPSQXX, and terms must be consistent to prevent a
   % loop in the pattern matcher.
   begin scalar bool,fn,x,y,z,n;
    fn := car u; u := cdr u;
    if x := valuechk(fn,u) then return x;
    if not null u and eqcar(car u,'list)
      then return mksq(list(fn,aeval car u),1);
    % *** Following line added to add nosimp code.
    x := fnreval(u, get(fn, 'nosimp),nil);
%    x := for each j in cdr u collect aeval j;
    u := for each j in x collect
              if eqcar(j,'!*sq) then prepsqxx cadr j
               else if numberp j then j
               else <<bool := t; j>>;
    if u and car u=0
       and flagp(fn,'odd) and not flagp(fn,'nonzero)
      then return nil ./ 1;
    u := fn . u;
    if flagp(fn,'noncom) then ncmp!* := t;
    if null subfg!* then go to c
     else if flagp(fn,'linear) and (z := formlnr u) neq u
      then return simp z
     else if z := opmtch u then return simp z
     else if z := get(car u,'opvalfn) then return apply1(z,u);
 %    else if null bool and (z := domainvalchk(fn,
 %                for each j in x collect simp j))
 %     then return z;
    c:  if flagp(fn,'symmetric) then u := fn . ordn cdr u
         else if flagp(fn,'antisymmetric)
          then <<if repeats cdr u then return (nil ./ 1)
              else if not permp(z:= ordn cdr u,cdr u) then y := t;
        % The following patch was contributed by E. Schruefer.
        fn := car u . z;
        if z neq cdr u and (z := opmtch fn)
          then return if y then negsq simp z else simp z;
        u := fn>>;
    if (flagp(fn,'even) or flagp(fn,'odd))
       and x and minusf numr(x := simp car x)
     then <<if flagp(fn,'odd) then y := not y;
        u := fn . prepsqxx negsq x . cddr u;
        if z := opmtch u
          then return if y then negsq simp z else simp z>>;
    u := mksq(u,1);
    return if y then negsq u else u
   end;

endmodule;

end;
