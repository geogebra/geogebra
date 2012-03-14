module opmtch; % Functions that apply basic pattern matching rules.

% Author: Anthony C. Hearn.
% Modifications by: Winfried Neun.

% Copyright (c) 2000 Anthony C. Hearn. All rights reserved.

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


fluid '(!*uncached frlis!* matchlength!* subfg!*);

matchlength!* := 5; % Maximum number of arguments checked in matching.

share matchlength!*;

% Operator // for extended quotient match to be used only in the
% lhs of a rule.

newtok '((!/ !/) slash);

mkop 'slash;

infix slash;

precedence slash, quotient;

% put('slash,'simpfn, function(lambda(u); typerr("//",'operator)));

symbolic procedure emtch u;
   if atom u then u else (lambda x; if x then x else u) opmtch u;

symbolic procedure opmtch u;
   begin scalar q,x,y,z;
        if null(x := get(car u,'opmtch)) then return nil
         else if null subfg!* then return nil  % null(!*sub2 := t).
         else if (null !*uncached) and (q := assoc(u,cdr alglist!*))
                 then return cdr q;
%WN%     else if q := assoc(u,cdr alglist!*) then return cdr q;
        z := for each j in cdr u collect emtch j;
    a:  if null x then go to c;
        y := mcharg(z,caar x,car u);
    b:  if null y then <<x := cdr x; go to a>>
         else if lispeval subla(car y,cdadar x)
          then <<q := subla(car y,caddar x); go to c>>;
        y := cdr y;
        go to b;
    c:  if not !*uncached then rplacd(alglist!*,(u . q) . cdr alglist!*);
%WN% c:  rplacd(alglist!*,(u . q) . cdr alglist!*);
        return q
   end;

symbolic procedure mcharg(u,v,w);
  <<if atsoc('minus,v) then mcharg1(reform!-minus(u,v),v,w) else
    if v and eqcar(car v,'slash) then
      for each f in reform!-minus2(u,v) join mcharg1(car f,cdr f,w)
    else mcharg1(u,v,w)>>;

symbolic procedure mcharg1(u,v,w);
   % Procedure to determine if an argument list matches given template.
   % U is argument list of operator W, V is argument list template being
   % matched against.  If there is no match, value is NIL,
   % otherwise a list of lists of free variable pairings.
   if null u and null v then list nil
    else begin integer m,n;
        m := length u;
        n := length v;
        if flagp(w,'nary) and m>2
          then if m<=matchlength!* and flagp(w,'symmetric)
                             then return mchcomb(u,v,w)
                else if n=2 then <<u := cdr mkbin(w,u); m := 2>>
                else return nil;   % We cannot handle this case.
        return if m neq n then nil
                else if flagp(w,'symmetric) then mchsarg(u,v,w)
                else if mtp v then list pair(v,u)
                else mcharg2(u,v,list nil,w)
   end;

symbolic procedure reform!-minus(u,v);
  % Convert forms (quotient (minus a) b) to (minus (quotient a b))
  % if the corresponding pattern in v has a top level minus.
    if null v or null u then u else
      ((if eqcar(car v,'minus) and eqcar(c,'quotient)
           and eqcar(cadr c,'minus)
          then {'minus,{'quotient,cadr cadr c,caddr c}} else c)
                      . reform!-minus(cdr u,cdr v))
                               where c=car u;

symbolic procedure reform!-minus2(u,v);
 % Prepare an extended quotient match; v is a pattern with leading "//".
 % Create for a form (quotient a b) a second form
 %  (quotient (minus a) (minus b)) if b contains a minus sign.
   if null u or not eqcar(car u,'quotient) then nil else
  <<v := ('quotient . cdar v) . cdr v;
   if not smemq('minus,caddar u) then {u.v}
     else
   {u . v,
    ({'quotient,reval {'minus,cadar u},reval {'minus,caddar u}} . cdr u)
                . v}>>;

symbolic procedure mchcomb(u,v,op);
   begin integer n;
      n := length u - length v +1;
      if n<1 then return nil
      else if n=1 then return mchsarg(u,v,op)
      else if not smemqlp(frlis!*,v) then return nil;
% The expansion of "for each ... join" is careful not to scan the
% list that is being generated from the start at each step and so is
% not a terrible cost issue.
      return for each x in comb(u,n) join
           if null ncmp!* then mchsarg((op . x) . setdiff(u,x),v,op)
           else (if null y then nil
                 else mchsarg((op . x) . car y,
                              if cdr y then reverse v else v,op))
             where y = mchcomb2(x,u,nil,nil,nil)
   end;

symbolic procedure mchcomb2(u,v,w,bool1,bool2);
   % Determines if v can be removed from u according to noncom rules,
   % and whether remaining terms must be on the left (t) or right (nil).
   if null u
     then reversip2(w,v) . bool2
   % (bool2 or null noncomlistp v and noncomlistp w and 'ok)
    else if car u = car v
           then if noncomp car u then mchcomb2(cdr u,cdr v,w,t,bool2)
                 else mchcomb2(cdr u,cdr v,w,bool1,bool2)
    else if noncomp car u
     then if bool1 then nil
           else mchcomb2(u,cdr v,car v . w,t,if bool2 then bool2 else t)
    else mchcomb2(u,cdr v,car v . w,bool1,bool2);

%- symbolic procedure comb(u,n);
%-    % Value is list of all combinations of N elements from the list U.
%-    begin scalar v; integer m;
%-         if n=0 then return list nil
%-          else if (m:=length u-n)<0 then return nil
%-          else for i := 1:m do
%-           <<v := nconc!*(v,mapcons(comb(cdr u,n-1),car u));
%-             u := cdr u>>;
%-         return u . v
%-    end;

symbolic procedure comb(u,n);
% Value is list of all combinations of N elements from the list U.
% This new version does not return the combinations in the same order,
% but I really hope that does not matter. It avoids the use of
% nconc that repeatedly tagged items on the end of what could end up
% a very long list, and so for long lists u its costs should be
% smaller.
  begin
    scalar v, w;
    integer m;
    if n=0 then return list nil
    else if (m:=length u-n)<0 then return nil;
    for i := 1:m do <<
      w := comb(cdr u, n-1);
      for each q in w do v := ((car u) . q) . v;
      u := cdr u>>;
    return u . v
  end;


symbolic procedure mcharg2(u,v,w,x);
   % Matches compatible list U of operator X against template V.
   begin scalar y;
        if null u then return w;
        y := mchk(car u,car v);
        u := cdr u;
        v := cdr v;
        return for each j in y
           join mcharg2(u,updtemplate(j,v,x),msappend(w,j),x)
   end;

symbolic procedure msappend(u,v);
   % Mappend u and v with substitution.
   for each j in u collect append(v,sublis(v,j));

symbolic procedure updtemplate(u,v,w);
   begin scalar x,y;
      return for each j in v collect
        if (x := subla(u,j)) = j then j
         else if (y := reval!-without(x,w)) neq x then y
         else x
   end;

symbolic procedure reval!-without(u,v);
   % Evaluate U without rules for operator V.  This avoids infinite
   % recursion with statements like
   % for all a,b let kp(dx a,kp(dx a,dx b)) = 0; kp(dx 1,dx 2).
   begin scalar x;
      x := get(v,'opmtch);
      remprop(v,'opmtch);
      u := errorset!*(list('reval,mkquote u),t);
      put(v,'opmtch,x);
      if errorp u then error1() else return car u
   end;

symbolic procedure mchk(u,v);
  % Extension to optional arguments for binary forms suggested by
  % Herbert Melenk.
   if u=v then list nil
    else if eqcar(u,'!*sq) then mchk(prepsqxx cadr u,v)
    else if eqcar(v,'!*sq) then mchk(u,prepsqxx cadr v)
    else if atom v
           then if v memq frlis!* then list list (v . u) else nil
    else if atom u      % Special check for negative number match.
     then if numberp u and u<0 and eqcar(v,'minus)
          then mchk(list('minus,-u),v) else mchkopt(u,v)
       % "difference" may occur in a pattern like (a - b)^~n.
    else if car v = 'difference then
       mchk(u,{'plus,cadr v,{'minus,caddr v}})
    else if get(car u,'dname) or get(car v,'dname) then nil
    else if car u eq car v then mcharg(cdr u,cdr v,car u)
    else if car v memq frlis!*    % Free operator.
      then for each j in mcharg(subst(car u,car v,cdr u),
                                subst(car u,car v,cdr v),
                                car u)
               collect (car v . car u) . j
    else if car u eq 'minus then mchkminus(cadr u,v)
    else mchkopt(u,v);

symbolic procedure mchkopt(u,v);
 % Check whether the pattern v is a binary form with an optional
 % argument.
   (if o then mchkopt1(u,v,o)) where o=get(car v,'optional);

symbolic procedure mchkopt1(u,v,o);
  begin scalar v1,v2,w;
    if null (w:=cdr v) then return nil; v1:=car w;
    if null (w:=cdr w) then return nil; v2:=car w;
    if cdr w then return nil;
    return
     if flagp(v1,'optional) then
      for each r in mchk(u,v2) collect (v1.car o) . r
     else if flagp(v2,'optional) then
      for each r in mchk(u,v1) collect (v2.cadr o) . r
     else nil;
   end;

put('plus,'optional,'(0 0));
put('times,'optional,'(1 1));
put('quotient,'optional,
     '((rule_error "fraction with optional numerator") 1));
put('expt,'optional,
     '((rule_error "exponential with optional base")  1));

symbolic procedure rule_error u;
  rederr{"error in rule:",u,"illegal"};

symbolic operator rule_error;

% The following function pushes a minus sign into a term.
% E.g. a + ~~y*~z matches
%                         y   z
%     (a + b)             1   b
%     (a - b)            -1   b
%     (a -3b)            -3   b
%                         b  -3
%     (a - b*c)          -b   c
%                         c  -b
%
% For products, the minus is assigned to a numeric coefficient or
% an artificial factor (-1) is created. For quotients the minus is
% always put in the numerator.

symbolic procedure mchkminus(u,v);
  if not(car v memq '(times quotient)) then nil else
  if atom u or not(car u eq car v) then
    if car v eq 'times then mchkopt1(u,v,'((minus 1)(minus 1)))
        else mchkopt({'minus,u},v)
  else if numberp cadr u or pairp cadr u and get(caadr u,'dname)
        or car v eq 'quotient
     then mcharg({'minus,cadr u}.cddr u,cdr v,car v)
  else mcharg('(minus 1).cdr u,cdr v,'times);

symbolic procedure mkbin(u,v);
   if null cddr v then u . v else list(u,car v,mkbin(u,cdr v));

symbolic procedure mtp v;
   null v or (car v memq frlis!* and not(car v member cdr v)
       and mtp cdr v);

symbolic procedure mchsarg(u,v,w);
   %  From ACH: I don't understand why I put in the following reversip,
   %  since it causes the least direct match to come back first.
   reversip!* if mtp v and (W NEQ 'TIMES OR noncomfree u)
     then for each j in noncomperm v collect pair(j,u)
    else for each j in noncomperm u join mcharg2(j,v,list nil,w);

symbolic procedure noncomfree u;
   if idp u then not flagp(u,'noncom)
    else atom u or noncomfree car u and noncomfree cdr u;

symbolic procedure noncomperm u;
   % Find possible permutations when non-commutativity is taken into
   % account.
   if null u then list u
    else for each j in u join
       (if x eq 'failed then nil else mapcons(noncomperm x,j))
        where x=noncomdel(j,u);

symbolic procedure noncomdel(u,v);
   if null NONCOMP!* u then delete(u,v) else noncomdel1(u,v);

symbolic procedure noncomdel1(u,v);
   begin scalar z;
   a: if null v then return reversip!* z
       else if u eq car v then return reversip2(z,cdr v)
       else if NONCOMP!* car v then return 'failed;
      z := car v . z;
      v := cdr v;
      go to a
   end;

symbolic procedure NONCOMP!* u;
   noncomp u or eqcar(u,'expt) and noncomp cadr u;

flagop antisymmetric,symmetric;

flag ('(plus times),'symmetric);

endmodule;

end;
