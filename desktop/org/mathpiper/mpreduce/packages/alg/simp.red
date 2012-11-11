module simp; % Functions to convert prefix forms into canonical forms.

% Author: Anthony C. Hearn.

% Modifications by: J.H. Davenport, F. Kako, S. Kameny, E. Schruefer and
%                   Francis J. Wright.

% Copyright (c) 1998, Anthony C. Hearn.  All rights reserved.

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


fluid '(!*allfac !*div);

fluid '(!*asymp!* !*complex !*exp !*gcd !*ifactor !*keepsqrts !*mcd
        !*mode !*modular !*notseparate !*numval !*precise !*precise_complex
        !*rationalize !*reduced !*resimp !*sub2 !*uncached alglist!* dmd!*
        dmode!* varstack!* !*combinelogs !*expandexpt !*msg frlis!* subfg!*
        !*norationalgi factorbound!* ncmp!* powlis1!* !*nospurp
        !*ncmp);

global '(!*match
         den!*
%        exptl!*   No-one else refers to this variable - just slows us
         initl!*
         mul!*
         simpcount!*
         simplimit!*
         tstack!*
         ws);

switch expandexpt; % notseparate;

!*expandexpt := t;

% The NOTSEPARATE switch inhibits an expression such as x^(4/3) to
% become x*x^(1/3).  At the present time, no one is using this.

factorbound!* := 10000;   % Limit for factoring with IFACTOR off.

% !*KEEPSQRTS uses SQRT rather than EXPT for square roots.
% Normally set TRUE in the integrator, false elsewhere.

put('ifactor,'simpfg,'((t (rmsubs))));

put('alglist!*,'initl,'(cons nil nil));

put('simpcount!*,'initl,0);

initl!* := union('(alglist!* simpcount!*),initl!*);

simplimit!* := 1000;

symbolic procedure noncom u;
   % Declare vars u to be noncom.
   <<rmsubs(); for each j in u do noncom1 j>>;

symbolic procedure noncom1 u;
   <<!*ncmp := t; flag(list u,'noncom)>>;

put('noncom,'stat,'rlis);

symbolic procedure simp!* u;
   begin scalar !*asymp!*,x;
        if eqcar(u,'!*sq) and caddr u and null !*resimp
          then return cadr u;
        x := mul!* . !*sub2;    % Save current environment.
        mul!* := nil;
        u:= simp u;
        if !*nospurp then mul!* := union(mul!*,'(isimpq));
        for each j in mul!* do u:= apply1(j,u);
        mul!* := car x;
        u := subs2 u;
        if !*combinelogs then u := clogsq!* u;
        % Must be here, since clogsq!* can upset girationalizesq!:.
        % For defint, it is necessary to turn off girationalizesq - SLK.
        if dmode!* eq '!:gi!: and not !*norationalgi
          then u := girationalize!: u
          else if !*rationalize then u := rationalizesq u
         else u := rationalizei u;
        !*sub2 := cdr x;
        % If any leading terms have cancelled, a gcd check is required.
        if !*asymp!* and !*rationalize then u := gcdchk u;
        return u
   end;

symbolic procedure rationalizei u;
   % Remove overall factor of i in denominator.
   begin scalar v,w;
      if domainp (v := denr u) or not smemq('i,v) then return u;
      v := reordsq u where kord!* = 'i . kord!*;
      return if lpow (w := denr v) = '(i . 1) and null red w
               then negf multf(!*k2f 'i,reorder numr v) ./ reorder lc w
              else u
   end;

symbolic procedure subs2 u;
   begin scalar xexp,v,w,x;
    if null subfg!* then return u
     else if !*sub2 or powlis1!* then u := subs2q u;
    u := exptchksq u;
    x := get('slash,'opmtch);
    if null (!*match or x) or null numr u then return u
     else if null !*exp
      then <<xexp:= t; !*exp := t; v := u; w := u := resimp u>>;
    u := subs3q u;
    if xexp then <<!*exp := nil; if u=w then u := v>>;
    if x then u := subs4q u;
    return u
   end;

% car alglist!* is a table, inspected here in simp and set (only) in
% !*ssave, which in turn is only ever called form here. In forall.red
% there is a call that removes items from the table. The only
% other constraint on it is that NIL must represent an empty table.
% The value stored against a key is always a CONS, and specifically is
% never NIL.
%
% Items should only ever be added to the list if there are not already
% present. This means that the order of items in the table is not
% important. There are, across the Reduce tests, about as many searches
% as there are additions to this table.
%
% The initial implementation was a simple association list. That works
% well provided it remains short! However for instance the liepde test
% script leads to a table with over 4000 entries, so other large calculations
% may be bad too. But of course an over-heavy-handed implementation might
% also cause pain! To help me understand this I will make all access to this
% table abstract via small procedures here.


!#if (and (memq 'csl lispsystem!*) (not (memq 'vsl lispsystem!*)))

% With CSL I have hash tables and I am fairly confident both that for
% cases where alglist!* becomes long they are a significant win and that
% in other cases they are as close to cost-neutral as I can measure.

!#if nil % Version for testing purposes only. Will be removed soon!

% The code here is for use while I test the hash table idea - it keeps
% a hash table AND the old style association list and checks that they
% deliver the same answers!

smacro procedure add_to_alglist(key, val, l);
<<
  if null l then l := nil . mkhash(10, 3, 2.0);
  puthash(key, cdr l, val);
  ((key . val) . car l) . cdr l >>;

smacro procedure search_alglist(key, l);
  if null l then nil
  else begin
    scalar al, ha;
    al := assoc(key, car l);
    if al then al := cdr al;
    ha := gethash(key, cdr l);
    if not (al = ha) then <<
       printc "+++++ HO HO HO alglist search messup +++++";
       print key;
       print l;
       print al;
       print ha;
       g_key := key;
       g_hash := cdr l;
       g_al := car l;
       g_from_hash := ha;
       g_from_al := al;
       error(99, "broken") >>;
    return ha
  end;

symbolic procedure delete_from_alglist(key, l);
  if null l then nil
  else <<
    remhash(key, cdr l); (delasc(key, car l) . cdr l) >>;

!#else

% If I just cache EVERYTHING then alglist can end up huge. This may be
% bad in general since it keep stuff that may be stale in memory forever.
% Also in CSL for HUGE calculations it can lead to exceeding the maximum
% capacity of my hash tables where I believe there is a case that can
% lead to crashes. So when the cache has had a certain number of entries
% inserted I will just clear it. The effect will be to lead to some
% recomputation at that stage. alglist_limit!* sets the limit, and
% I expect that only truly large computations will even trigger it. A
% really keen person wanting to tune behaviour here could go
% "lisp alglist_limit!* := nnnn;" for suitable nnnn and experiment to
% see just what works best for them.

global '(alglist_count!* alglist_limit!*);
alglist_count!* := 0;
alglist_limit!* := 1000000;

smacro procedure add_to_alglist(key, val, l);
<<
  if null l or alglist_count!* > alglist_limit!* then <<
     l := mkhash(10, 3, 2.0);
     alglist_count!* := 0 >>;
  puthash(key, l, val);
  alglist_count!* := add1 alglist_count!*;
  l
>>;

smacro procedure search_alglist(key, l);
  if null l then nil
  else gethash(key, l);

symbolic procedure delete_from_alglist(key, l);
  if null l then nil
  else << remhash(key, l); l >>;

!#endif

!#else

% With PSL I maintain the previous association-list model, albeit now
% lifted by a level of abstraction.

smacro procedure add_to_alglist(key, val, l);
  (key . val) . l;

smacro procedure search_alglist(key, l);
  begin
    scalar r;
    r := assoc(key, l);
    if null r then return r
    else return cdr r
  end;

symbolic procedure delete_from_alglist(key, l);
  delasc(key, l);

!#endif


symbolic procedure simp u;
   (begin scalar x,y;
    % This case is sufficiently common it is done first.
    if fixp u
      then if u=0 then return nil ./ 1
            else if not dmode!* then return u ./ 1
            else nil
     else if u member varstack!* then recursiveerror u;
    varstack!* := u . varstack!*;
    if simpcount!*>simplimit!*
      then <<simpcount!* := 0;
             rerror(alg,12,"Simplification recursion too deep")>>
     else if eqcar(u,'!*sq) and caddr u and null !*resimp
      then return cadr u
     else if null !*uncached and (x := search_alglist(u,car alglist!*))
      then return <<if car x then !*sub2 := t; cdr x>>;
    simpcount!* := simpcount!*+1; % undone by returning through !*SSAVE.
    if atom u then return !*ssave(simpatom u,u)
     else if not idp car u or null car u
      then if atom car u then typerr(car u,"operator")
            else if idp caar u and (x := get(caar u,'name))
             then return !*ssave(u,u)     %%% not yet correct
            else if eqcar(car u,'mat)
                and numlis(x := revlis cdr u) and length x=2
             then return !*ssave(simp nth(nth(cdar u,car x),cadr x),u)
            else errpri2(u,t)
     else if flagp(car u,'opfn)
      then if null(y := getrtype(x := opfneval u))
             then return !*ssave(simp_without_resimp x,u)
            else if y eq 'yetunknowntype and null getrtype(x := reval x)
             then return simp x
            else typerr(u,"scalar")
     else if x := get(car u,'psopfn)
      then if getrtype(x := apply1(x,cdr argnochk u))
             then typerr(u,"scalar")
        else if x=u then return !*ssave(!*k2q x,u)
        else return !*ssave(simp_without_resimp x,u)
     % Note in above that the psopfn MUST return a *sq form,
     % otherwise an infinite recursion occurs.
     else if x := get(car u,'polyfn)
      then return
        <<argnochk u;
          !*ssave(!*f2q lispapply(x,
                            for each j in cdr u collect !*q2f simp!* j),
                  u)>>
     else if get(car u,'opmtch)
        and not(get(car u,'simpfn) eq 'simpiden)
        and (x := opmtchrevop u)
      then return !*ssave(simp x,u)
     else if x := get(car u,'simpfn)
      then return !*ssave(apply1(x,
                                 if x eq 'simpiden or flagp(car u,'full)
                                   then argnochk u
                                 else cdr argnochk u),
                          u)
     else if (x := get(car u,'rtype)) and (x := get(x,'getelemfn))
      then return !*ssave(simp apply1(x,u),u)
     else if flagp(car u,'boolean) or get(car u,'infix)
      then typerr(if x := get(car u,'prtch) then x else car u,
              "algebraic operator")
     else if flagp(car u,'nochange)
      then return !*ssave(simp lispeval u,u)
     else if get(car u,'psopfn) or get(car u,'rtypefn)
      then typerr(u,"scalar")
     else <<redmsg(car u,"operator");
        mkop car u;
        varstack!* := delete(u,varstack!*);
        return !*ssave(simp u,u)>>;
   end) where varstack!* = varstack!*;

symbolic procedure opmtchrevop u;
   % The following structure is designed to make index mu; p1.mu^2;
   % work.  It also introduces a redundant revlis in most cases.
   if null !*val or smemq('cons,u) then opmtch u
    else opmtch(car u . revlis cdr u);

symbolic procedure simp_without_resimp u;
   simp u where !*resimp := nil;

put('array,'getelemfn,'getelv);

put('array,'setelemfn,'setelv);

symbolic procedure getinfix u;
   %finds infix symbol for U if it exists;
   begin scalar x; return if x := get(u,'prtch) then x else u end;

symbolic procedure !*ssave(u,v);
   % We keep !*sub2 as well, since there may be an unsubstituted
   % power in U.
  begin
    if not !*uncached then
      rplaca(alglist!*, add_to_alglist(v, (!*sub2 . u), car alglist!*));
    simpcount!* := simpcount!*-1;
    return u
  end;

symbolic procedure numlis u;
   null u or (numberp car u and numlis cdr u);

symbolic procedure simpatom u;
%  if null u then typerr("NIL","algebraic identifier")
   if null u then nil ./ 1   % Allow NIL as default 0.
    else if numberp u
     then if u=0 then nil ./ 1
           else if not fixp u then ('!:rd!: . cdr fl2bf u) ./ 1
             % we assume that a non-fixp number is a float.
           else if dmode!* eq '!:mod!: and current!-modulus = 1
            then nil ./ 1
           else if flagp(dmode!*,'convert) and u neq 1 % Don't convert 1
            then !*d2q apply1(get(dmode!*,'i2d),u)
           else u ./ 1
    else if stringp u then typerr(list("String",u),"identifier")
    else if flagp(u,'share) then
      <<(if x eq u then mksq(u,1) else simp x) where x=lispeval u>>
    else begin scalar z;
      if z := get(u,'idvalfn) then return apply1(z,u)
       else if !*numval and dmode!* and flagp(u,'constant)
          and (z := get(u,dmode!*))
          and not errorp(z := errorset!*(list('lispapply,mkquote z,nil),
                         nil))
        then return !*d2q car z
       else if getrtype u then typerr(u,'scalar)
       else return mksq(u,1) end;

flag('(e pi),'constant);

symbolic procedure mkop u;
   begin scalar x;
    if null u then typerr("Local variable","operator")
     else if (x := gettype u) eq 'operator
      then lprim list(u,"already defined as operator")
    % Allow a scalar to also be an operator.
     else if x and not(x memq '(fluid global procedure scalar))
      then typerr(u,'operator)
%    else if u memq frlis!* then typerr(u,"free variable")
     else put(u,'simpfn,'simpiden)
   end;

symbolic procedure operatorp u;
    gettype u eq 'operator;

symbolic procedure simpcar u;
   simp car u;

put('quote,'simpfn,'simpcar);

symbolic procedure share u;
   begin scalar y;
      for each v in u do
         if not idp v then typerr(v,"id")
         else if flagp(v,'share) then nil
         else if flagp(v,'reserved) or v eq 't then rsverr v
         else if (y := getrtype v) and y neq 'list
            then rerror(alg,13,list(y,v,"cannot be shared"))
         else
          % if algebraic value exists, transfer to symbolic.
         <<if y then remprop(v,'rtype);
           if y := get(v,'avalue)
             then <<setifngfl(v,cadr y); remprop(v,'avalue)>>
          % if no algebraic value but symbolic value, leave unchanged.
            else if not boundp v then setifngfl(v,v);
          % if previously unset, set symbolic self pointer.
           flag(list v,'share)>>
   end;

symbolic procedure boundp u;
   % Determines if the id u has a value.
   % NB:  this function must be redefined in many systems (e.g., CL).
   null errorp errorset!*(u,nil);

symbolic procedure setifngfl(v,y);
   <<if not globalp v then fluid list v; set(v,y)>>;

rlistat '(share);

flag('(ws !*mode),'share);

flag('(share),'eval);


% ***** SIMPLIFICATION FUNCTIONS FOR EXPLICIT OPERATORS - EXP *****

symbolic procedure simpexpon u;
   % Exponents must not use non-integer arithmetic unless NUMVAL is on,
   % in which case DOMAINVALCHK must know the mode.
   simpexpon1(u,'simp!*);

symbolic procedure simpexpon1(u,v);
   if !*numval and (dmode!* eq '!:rd!: or dmode!* eq '!:cr!:)
     then apply1(v,u)
    else begin scalar dmode!*,alglist!*; return apply1(v,u) end;

symbolic procedure simpexpt u;
   % We suppress reordering during exponent evaluation, otherwise
   % internal parts (as in e^(a*b)) can have wrong order.
   begin scalar expon;
      expon := simpexpon carx(cdr u,'expt) where kord!*=nil;
      % We still need the right order, else
      % explog := {sqrt(e)**(~x*log(~y)/~z) => y**(x/z/2)};
      % on ezgcd,gcd; let explog; fails.
      expon := simpexpon1(expon,'resimp);
      return simpexpt1(car u,expon,nil)
   end;

symbolic procedure simpexpt1(u,n,flg);
   % FLG indicates whether we have done a PREPSQ SIMP!* U or not: we
   % don't want to do it more than once.
   begin scalar !*allfac,!*div,m,x,y;
      if onep u then return 1 ./ 1;
      !*allfac := t;
      m := numr n;
      if m=1 and denr n=1 then return simp u;
     % this simplifies e^(n log x) -> x^n  for all n,x.
      if u eq 'e and domainp denr n and not domainp m and ldeg m=1
         and null red m and eqcar(mvar m,'log) then return
            simpexpt1(prepsq!* simp!* cadr mvar m,lc m ./ denr n,nil);
      if not domainp m or not domainp denr n
        then return simpexpt11(u,n,flg);
      x := simp u;
      if null m
        then return if null numr x then rerror(alg,14,"0**0 formed")
                     else 1 ./ 1;
      % We could use simp!* here, except it messes up the handling of
      % gamma matrix expressions.
%     if denr x=1 and not domainp numr x and not(denr n=1)
%       then <<y := sqfrf numr x;
%%      then <<y := fctrf numr x;
%%              if car y=1 then y := cdr y
%%               else if minusp car y then y := {1};
%              if length y>1 then return simpexptfctr(y,n)>>;
      return if null numr x
               then if domainp m and minusf m
                      then rerror(alg,15,"Zero divisor")
                     else nil ./ 1
              else if atom m and denr n=1 and domainp numr x
                 and denr x=1
               then if atom numr x and m>0 then !*d2q(numr x**m)
                     else <<x := !:expt(numr x,m) ./ 1;
                            %remove rationals where possible.
                            if !*mcd then resimp x else x>>
              else if y := domainvalchk('expt,list(x,n)) then y
              else if atom m and denr n=1
               then <<if not(m<0) then exptsq(x,m)
                       else if !*mcd then invsq exptsq(x,-m)
                       else multf(expf(numr x,m),mksfpf(denr x,-m))
                               ./ 1>>     % This uses OFF EXP option.
                      % There may be a pattern matching problem though.
     % We need the subs2 in the next line to take care of power and
     % product simplification left over from the call of simp on u.
              else simpexpt11(if flg then u else prepsq!* subs2!* x,n,t)
   end;

symbolic procedure simpexptfctr(u,n);
   begin scalar x;
     x := 1 ./ 1;
     for each j in u do
         x:= multsq(simpexpt1(prepf car j,multsq(cdr j ./ 1,n),nil),x);
     return x
   end;

symbolic procedure simpexpt11(u,n,flg);
   % Expand exponent to put expression in canonical form.
   begin scalar x;
      return if !*precise_complex then simpexpt2(u,n,flg)
              else if domainp denr n
                 or not(car(x := qremf(numr n,denr n)) and cdr x)
               then simpexpt2(u,n,flg)
              else multsq(simpexpt1(u,car x ./ 1,flg),
                          simpexpt1(u,cdr x ./ denr n,flg))
   end;

symbolic procedure simpexpt2(u,n,flg);
   % The "non-numeric exponent" case.  FLG indicates whether we have
   % done a PREPSQ SIMP!* U or not: we don't want to do it more than
   % once.
   begin scalar m,n,x,y;
    if u=1 then return 1 ./ 1;
%  The following is now handled in mkrootsq.
%    else if fixp u and u>0 and (u<factorbound!* or !*ifactor)
%      and (length(x := zfactor u)>1 or cdar x>1)
%     then <<y := 1 ./ 1;
%            for each j in x do
%               y := multsq(simpexpt list(car j,
%                                         prepsq multsq(cdr j ./ 1,n)),
%                           y);
%            return y>>;
    m:=numr n;
    if pairp u then <<
     if car u eq 'expt and null !*precise_complex
      then <<n:=multsq(m:=simp caddr u,n);
             if !*precise
               and numberp numr m and evenp numr m
%               and numberp numr n and not evenp numr n
               then u := list('abs,cadr u)
              else u := cadr u;
             return simpexpt1(u,n,flg)>>
     else if car u eq 'sqrt and not !*keepsqrts
      then return simpexpt2(cadr u, multsq(1 ./ 2,n),flg)
     % We need the !*precise check for, say, sqrt((1+a)^2*y*z).
     else if car u eq 'times and not !*precise and not !*modular
      then <<x := 1 ./ 1;
             for each z in cdr u do x := multsq(simpexpt1(z,n,flg),x);
             return x>>
        % For a product under *precise we isolate positive factors.
     else if car u eq 'times and (y:=split!-sign cdr u) and car y
%             and null !*precise_complex
      then <<x := simpexpt1(retimes append(cadr y,cddr y),n,flg);
             for each z in car y do x := multsq(simpexpt1(z,n,flg),x);
             return x>>
     else if car u eq 'quotient
%    The next lines did not allow, e.g., sqrt(a/b) => sqrt(a)/sqrt(b).
%    when precise is on and there is a risk of
%    E.g., sqrt(a/b) neq sqrt(a)/sqrt(b) when a=1, b=-1.
%    We allow however the denominator to be a positive number.
        and (not !*precise
%               or alg_constant_exptp(cadr u,n)
%               or alg_constant_exptp(caddr u,n)
                or posnump caddr u and posnump prepsq n
            )
      then <<if not flg and !*mcd then
                return simpexpt1(prepsq simp!* u,n,t);
             n := prepsq n;
             return quotsq(simpexpt{cadr u,n},simpexpt{caddr u,n})>>
     % Special case of (-expression)^(1/2).
%    else if car u eq 'minus
%            and (n = '(1 . 2) or n = '((!:rd!: . 0.5) . 1)
%                 or n = '((!:rd!: 5 . -1) . 1)
%                 or n = '((!:rn!: 1 . 2) . 1))
%     then return simptimes list('i,list('expt,cadr u,prepsq n))>>;
%    else if car u eq 'minus and numberp m and denr n=1
%     then return multsq(simpexpt list(-1,m),
%                simpexpt list(cadr u,m))>>;
     else if car u eq 'minus and not !*precise and not(cadr u = 1)
      then return (multsq(simpexpt list(-1,expon),
                 simpexpt list(cadr u,expon))) where expon=prepsq n>>;
    if null flg
      then <<% Don't expand say e and pi, since whole expression is not
             % numerical.
            if null(dmode!* and idp u and get(u,dmode!*))
               then u := prepsq simp!* u;
             return simpexpt1(u,n,t)>>
     else if numberp u and zerop u then return nil ./ 1
     else if not numberp m then m := prepf m;
    n := prepf denr n;
    if m memq frlis!* and n=1 then return list ((u . m) . 1) . 1;
       % "power" is not unique here.
    if !*mcd or not numberp m or n neq 1
      or atom u or denr simp!* u neq 1 then return simpx1(u,m,n)
      else return mksq(u,m)  % To make pattern matching work.
   end;

symbolic procedure posnump u;
   % True if u is a positive number. Test is naive but correct.
   if atom u then (numberp u and u>0) or u memq '(e pi)
    else if car u memq '(expt plus quotient sqrt times)
     then posnumlistp cdr u
    else nil;

symbolic procedure posnumlistp u;
   null u or posnump car u and posnumlistp cdr u;

% symbolic procedure alg_constant_exptp(u,v);
%    % U an expression, v a standard quotient.
%    alg_constantp u and alg_constantp car v and alg_constantp cdr v;

% symbolic procedure alg_constantp u;
%    % True if u is an algebraic constant whose surd is unique.
%    if atom u then numberp u
%    else if car u memq
%        '(difference expt plus minus quotient sqrt times)
%      then alg_constant_listp cdr u
%     else nil;

% symbolic procedure alg_constant_listp u;
%    null u or alg_constantp car u and alg_constant_listp cdr u;

put('expt,'simpfn,'simpexpt);

symbolic procedure split!-sign u;
  % U is a list of factors. Split into positive, negative
  % and unknown sign part. Nil if no sign is known.
  begin scalar p,n,w,s;
    for each f in u do
      if 1=(s:=sign!-of f) then p:=f.p else if -1=s then n:=f.n
          else w:=f.w;
    if null p and null n then return nil;
    return p.n.w;
  end;

symbolic procedure conv2gid(u,d);
   if null u or numberp u or eqcar(u,'!:gi!:) then d
    else if domainp u
     then if eqcar(u,'!:crn!:) then lcm(d,lcm(cdadr u,cdddr u))
           else if eqcar(u,'!:rn!:) then lcm(d,cddr u) else d
    else conv2gid(lc u,conv2gid(red u,d));

symbolic procedure conv2gi2 u;
   if null u then u
   else if numberp u then u * den!*
   else if eqcar(u,'!:gi!:) then '!:gi!:.((den!**cadr u).(den!**cddr u))
   else if eqcar(u,'!:crn!:)
    then <<u := cdr u;
           u:= '!:gi!: . ((den!*/cdar u*caar u).(den!*/cddr u*cadr u))>>
   else if eqcar(u,'!:rn!:) then den!*/cddr u*cadr u
   else if domainp u then rerror(alg,16,list("strange domain",u))
   else lpow u .* conv2gi2(lc u) .+ conv2gi2(red u);

symbolic procedure simpx1(u,m,n);
   % U,M and N are prefix expressions.
   % Value is the standard quotient expression for U**(M/N).
   % FLG is true if we have seen a "-" in M.
    begin scalar flg,x,z;
      % Check for imaginary result.
      if eqcar(u,'!*minus!*)
         then if m=1 and fixp n and remainder(n,2)=0
             or n=1 and eqcar(m,'quotient) and cadr m=1 and fixp caddr m
               and remainder(caddr m,2)=0
                 then return multsq(simp list('expt,'i,
                                              list('quotient,1,n/2)),
                          simpexpt list(cadr u,list('quotient,m,n)))
      % and for negative result.
                else if m=1 and fixp n          % n must now be odd.
                 then return negsq
                          simpexpt list(cadr u,list('quotient,m,n));
    if numberp m and numberp n
       or null(smemqlp(frlis!*,m) or smemqlp(frlis!*,n))
      then go to a;
    % exptp!* := t;
    return mksq(list('expt,u,if n=1 then m
                   else list('quotient,m,n)),1);
    a:
    if numberp m then
        if minusp m then <<m := -m; go to mns>>
           else if fixp m then
                   if fixp n then <<
                      if flg then m := -m;
                      z := m;
                      if !*mcd and (fixp u or null !*notseparate)
                        then <<z := z-n*(m := m/n);
                               if z<0 then <<m := m-1; z := z+n>>>>
                       else m := 0;
                      x := simpexpt list(u,m);
                      if z=0 then return x
                      else if n=2 and !*keepsqrts
                       then <<x := multsq(x,apply1(get('sqrt,'simpfn),
                                                   list u));
                              % z can be 1 or -1. I'm not sure if other
                              % values can occur.
                              if z<0 then <<x := invsq x; z := -z>>;
                              return exptsq(x,z)>>
      % Note the indirect call: the integrator rebinds this property.
      % JHD understands this interaction - don't change without
      % consulting him.  Note that, since KEEPSQRTS is true, SIMPSQRT
      % won't recurse on SIMPEXPT1.
                      else return
                              multsq(x,exptsq(simprad(simp!* u,n),z))>>
                   else <<z := m; m := 1>>
                else z:=1
     else if atom m then z:=1
     else if car m eq 'minus then <<m := cadr m; go to mns>>
     else if car m eq 'plus and !*expandexpt then <<
         z := 1 ./ 1;
         for each x in cdr m do
             z := multsq(simpexpt list(u,
                         list('quotient,if flg then list('minus,x)
                                               else x,n)),
                           z);
         return z >>
%%   else if car m eq 'times and fixp cadr m and numberp n
%%    then <<
%%      z := gcdn(n,cadr m);
%%      n := n/z;
%%      z := cadr m/z;
%%      m := retimes cddr m >>
%% BEGIN modification by Francis J. Wright:
     else if car m eq 'times and fixp cadr m
      then <<
        if numberp n
          then <<z := gcdn(n,cadr m); n := n/z; z := cadr m/z>>
         else z := cadr m;
        % retimes seems to me to be overkill here, so try just ...
        m := if cdddr m then 'times . cddr m else caddr m>>
%% END   modification by FJW.
     else if car m eq 'quotient and n=1 and !*expandexpt
      then <<n := caddr m; m := cadr m; go to a>>
     else z := 1;
     if idp u and not flagp(u,'used!*) then flag(list u,'used!*);
        if u = '(minus 1)
               and n=1
               and null numr simp list('difference,m,'(quotient 1 2))
         then <<u := simp 'i; return if flg then negsq u else u>>;
    u := list('expt,u,if n=1 then m else list('quotient,m,n));
    return mksq(u,if flg then -z else z); %U is already in lowest terms;
    mns: %if numberp m and numberp n and !*rationalizeflag
     %  then return multsq(simpx1(u,n-m,n),invsq simp u) else
    % return invsq simpx1(u,m,n)
    if !*mcd then return invsq simpx1(u,m,n);
    flg := not flg;
    go to a;
   end;

symbolic procedure expf(u,n);
   %U is a standard form. Value is standard form of U raised to
   %negative integer power N. MCD is assumed off;
   %what if U is invertable?;
   if null u then nil
    else if u=1 then u
    else if atom u then mkrn(1,u**(-n))
    else if domainp u then !:expt(u,n)
    else if red u then mksp!*(u,n)
    else if ldeg u memq frlis!*
     then car fkern {'expt,mvar u,ldeg u} .** n .* expf(lc u,n) .+ nil
    else (lambda x; if x>0 and sfp mvar u
             then multf(exptf(mvar u,x),expf(lc u,n))
            else mvar u .** x .* expf(lc u,n) .+ nil)
     (ldeg u*n);

% ******* The "radical simplifier" section ******

symbolic procedure simprad(u,n);
   % Simplifies radical expressions.
   if !*reduced then multsq(radfa(numr u,n),invsq radfa(denr u,n))
     else begin scalar iflag,x,y,z;
       if !*rationalize then << % Move all radicands into numerator.
          y:=list(denr u,1); % A partitioned expression.
          u:=multf(numr u, exptf(denr u,n-1)) ./ 1 >>
         else y := radf(denr u,n);
       if n=2 and minusf numr u % Should this be 'evenp n'?
         then <<iflag := t; x := radf(negf numr u,n)>>
        else x := radf(numr u,n);
       z := simp list('quotient,retimes cdr x, retimes cdr y);
       if domainp numr z and domainp denr z
      % This test allows transformations like sqrt(2/3)=>sqrt(2)/sqrt(3)
      % whereas we really don't want to do this for symbolic elements
      % since we can introduce paradoxes that way.
         then z := multsq(mkrootsq(prepf numr z,n),
                          invsq mkrootsq(prepf denr z,n))
        else <<if iflag
                 then <<iflag := nil; % Absorb the "i" in square root.
                        z := negsq z>>;
               z := mkrootsq(prepsq z,n)>>;
       z := multsq(multsq(if !*precise and evenp n
                            then car x ./ 1   % mkabsf0 car x
                           else car x ./ 1, 1 ./ car y), z);
       if iflag then z := multsq(z,mkrootsq(-1,2));
       return z
   end;

symbolic procedure radfa(u,n);
   begin scalar x,y;
      x := fctrf u;
      if numberp car x then x := append(zfactor car x,cdr x)
       else x := (car x ./ 1) . cdr x;
      y := 1 ./ 1;
      for each j in x do y := multsq(y,radfb(car j,cdr j,n));
      return y
   end;

symbolic procedure radfb(u,m,n);
   begin scalar x,y;
      x := radf(u,n);
    % if !*precise and evenp n then y := mkabsf0 car x ./ 1 else
      y := exptf(car x,m) ./ 1;
      return multsq(exptsq(mkrootlsq(cdr x,n),m),y)
   end;

symbolic procedure mkrootlsq(u,n);
   % U is a list of prefix expressions, N an integer.
   % Value is standard quotient for U**(1/N);
   % NOTE we need the REVAL call so that PREPSQXX is properly called on
   % the argument for consistency with the pattern matcher.  Otherwise
   % for all x,y let sqrt(x)*sqrt(y)=sqrt(x*y); sqrt(30*(l+1))*sqrt 5;
   % goes into an infinite loop.
   if null u then !*d2q 1
    else if null !*reduced then mkrootsq(reval retimes u,n)
    else mkrootlsq1(u,n);

symbolic procedure mkrootlsq1(u,n);
   if null u then !*d2q 1
    else multsq(mkrootsq(car u,n),mkrootlsq1(cdr u,n));

symbolic procedure mkrootsq(u,n);
   % U is a prefix expression, N an integer.
   % Value is a standard quotient for U**(1/N).
   if u=1 then !*d2q 1
    else if n=2 and (u= -1 or u= '(minus 1)) then simp 'i
    else if eqcar(u,'expt) and fixp caddr u and null !*precise_complex
     then exptsq(mkrootsq(cadr u,n),caddr u)
    else begin scalar x,y;
            if fixp u and not minusp u
                      and (length(x :=
                            zfactor1(u,u<factorbound!* or !*ifactor))>1
                           or cdar x>1)
              then return mkrootsql(x,n);
            x := if n=2 then mksqrt u
                  else list('expt,u,list('quotient,1,n));
            if y := opmtch x then return simp y
             else return mksq(x,1)
         end;

symbolic procedure mkrootsql(u,n);
   if null u then !*d2q 1
    else if cdar u>1
     then multsq(exptsq(mkrootsq(caar u,n),cdar u),mkrootsql(cdr u,n))
    else multsq(mkrootsq(caar u,n),mkrootsql(cdr u,n));


comment The following four procedures return a partitioned root
    expression, which is a dotted pair of integral part (a standard
    form) and radical part (a list of prefix expressions). The whole
    structure represents U**(1/N);

symbolic procedure check!-radf!-sign(rad,result,n);
   % Changes the sign of result if result**n = -rad. rad and result are
   % s.f.'s, n is an integer.
   (if evenp n and s = -1 or
       not evenp n and numberp s and
        ((numberp s1 and s neq s1)
           where s1 = reval {'sign,mk!*sq !*f2q rad})
      then negf result
     else result)
    where s = reval{'sign,mk!*sq !*f2q result};

symbolic procedure radf(u,n);
   % U is a standard form, N a positive integer. Value is a partitioned
   % root expression for U**(1/N).
   begin scalar ipart,rpart,x,y,z,!*gcd,!*mcd;
      if null u then return list u;
      !*gcd := !*mcd := t;  % mcd cannot be off in this code.
      ipart := 1;
      z := 1;
      while not domainp u do
     <<y := comfac u;
       if car y
         then <<x := if !*precise_complex then 0 . pdeg car y
                      else divide(pdeg car y,n);
            if car x neq 0
              then ipart := multf(
                   if evenp car x
                      then !*p2f(mvar u .** car x)
%                   else if !*precise
%                    then !*p2f mksp(numr
%                    then exptf(numr
%                                    simp list('abs,if sfp mvar u
%                                                     then prepf mvar u
%                                                    else mvar u),
                    else check!-radf!-sign(!*p2f(mvar u .** pdeg car y),
                                           !*p2f(
                            (if !*precise and evenp n then {'abs,mvar u}
                              else mvar u)
                                                  .** car x),
                                           n),
                    ipart);
            if cdr x neq 0
              then rpart := mkexpt(sfchk mvar u,cdr x) . rpart>>;
       x := quotf(u,comfac!-to!-poly y);   % We need *exp on here.
       u := cdr y;
       if !*reduced and minusf x
         then <<x := negf x; u := negf u>>;
       if flagp(dmode!*,'field) then
          <<y := lnc x;
                if y neq 1 then <<x := quotf(x,y); z := multd(y,z)>>>>;
       if x neq 1
         then <<x := radf1(if !*precise_complex or !*modular then {x .^ 1} 
                            else sqfrf x,n);
                y := car x;
                if y neq 1 then
                   <<if !*precise and evenp n
                       then y := !*kk2f {'abs,prepf y};
                     ipart := multf(y,ipart)>>;
                rpart := append(rpart,cdr x)>>>>;
      if u neq 1
    then <<x := radd(u,n);
           ipart := multf(car x,ipart);
           rpart := append(cdr x,rpart)>>;
      if z neq 1
    then if !*numval
        and (y := domainvalchk('expt,
                       list(!*f2q z,!*f2q !:recip n)))
           then ipart := multd(!*q2f y,ipart)
          else rpart := prepf z . rpart;  % was aconc(rpart,z).
      return ipart . rpart
   end;

symbolic procedure radf1(u,n);
   %U is a form_power list, N a positive integer. Value is a
   %partitioned root expression for U**(1/N);
   begin scalar ipart,rpart,x;
      ipart := 1;
      for each z in u do
     <<x := divide(cdr z,n);
       if not(car x=0)
            then ipart := multf(
                 check!-radf!-sign(!*p2f z,exptf(car z,car x),n),ipart);
          if not(cdr x=0)
            then rpart := mkexpt(prepsq!*(car z ./ 1),cdr x)
                   . rpart>>;
      return ipart . rpart
   end;

symbolic procedure radd(u,n);
   %U is a domain element, N an integer.
   %Value is a partitioned root expression for U**(1/N);
   begin scalar bool,ipart,x;
      if not atom u then return list(1,prepf u);
%      then if x := integer!-equiv u then u := x
%            else return list(1,prepf u);
      if u<0 and evenp n then <<bool := t; u := -u>>;
      x := nrootnn(u,n);
      if bool then if !*reduced and n=2
             then <<ipart := multd(car x,!*k2f 'i);
                x := cdr x>>
            else <<ipart := car x; x := -cdr x>>
       else <<ipart := car x; x := cdr x>>;
      return if x=1 then list ipart else list(ipart,x)
   end;

% symbolic procedure iroot(m,n);
%    %M and N are positive integers.
%   %If M**(1/N) is an integer, this value is returned, otherwise NIL;
%   begin scalar x,x1,bk;
%      if m=0 then return m;
%      x := 10**iroot!-ceiling(lengthc m,n);   %first guess;
%   a: x1 := x**(n-1);
%      bk := x-m/x1;
%      if bk<0 then return nil
%       else if bk=0 then return if x1*x=m then x else nil;
%      x := x - iroot!-ceiling(bk,n);
%      go to a
%   end;

 symbolic procedure iroot(n,r);
    % N, r are integers; r >= 1.  If n is an exact rth power then its
    % rth root is returned, otherwise NIL.
    begin scalar tmp;
       tmp := irootn(n,r);
       return if tmp**r = n then tmp else nil
  end;

symbolic procedure iroot!-ceiling(m,n);
   %M and N are positive integers. Value is ceiling of (M/N) (i.e.,
   %least integer greater or equal to M/N);
   (lambda x; if cdr x=0 then car x else car x+1) divide(m,n);

symbolic procedure mkexpt(u,n);
   if n=1 then u else list('expt,u,n);

% The following definition is due to Eberhard Schruefer.

symbolic procedure nrootn(n,x);
   % N is an integer, x a positive integer. Value is a pair
   % of integers r,s such that r*s**(1/x)=n**(1/x).
   begin scalar fl,r,s,m,signn;
     r := 1;
     s := 1;
     if n<0 then <<n := -n; if evenp x then signn := t else r := -1>>;
     fl := zfactor n;
     for each j in fl do
         <<m := divide(cdr j,x);
           r := car j**car m*r;
           s := car j**cdr m*s>>;
     if signn then s := -s;
     return r . s
   end;

% symbolic procedure nrootn(n,x);
%   % N is an integer, X a positive integer. Value is a pair
%   % of integers I,J such that I*J**(1/X)=N**(1/X).
%   begin scalar i,j,r,signn;
%      r := 1;
%      if n<0 then <<n := -n; if evenp x then signn := t else r := -1>>;
%      j := 2**x;
%      while remainder(n,j)=0 do <<n := n/j; r := r*2>>;
%      i := 3;
%      j := 3**x;
%      while j<=n do
%         <<while remainder(n,j)=0 do <<n := n/j; r := r*i>>;
%           if remainder(i,3)=1 then i := i+4 else i := i+2;
%           j := i**x>>;
%      if signn then n := -n;
%      return r . n
%   end;

switch precise_complex;

put('precise_complex,'simpfg,'((t nil) (nil (rmsubs))));

% ***** simplification functions for other explicit operators *****

symbolic procedure simpiden u;
   % Convert the operator expression U to a standard quotient.
   % Note: we must use PREPSQXX and not PREPSQ* here, since the REVOP1
   % in SUBS3T uses PREPSQXX, and terms must be consistent to prevent a
   % loop in the pattern matcher.
   begin scalar bool,fn,x,y,z;
    fn := car u; u := cdr u;
    % Allow prefix ops with names of symbolic functions.
    if (get(fn,'!:rn!:) or get(fn,'!:rd!:)) and (x := valuechk(fn,u))
      then return x;
    % Keep list arguments in *SQ form.
    if u and eqcar(car u,'list) and null cdr u
      then return mksq(list(fn,aeval car u),1);
    x := for each j in u collect aeval j;
    u := for each j in x collect
              if eqcar(j,'!*sq) then prepsqxx cadr j
               else if numberp j then j
               else <<bool := t; j>>;
%   if u and car u=0 and (flagp(fn,'odd) or flagp(fn,'oddreal))
    if u and car u=0 and flagp(fn,'odd)
         and not flagp(fn,'nonzero)
      then return nil ./ 1;
    u := fn . u;
    if flagp(fn,'noncom) then ncmp!* := t;
    if null subfg!* then go to c
     else if flagp(fn,'linear) and (z := formlnr u) neq u
      then return simp z
     else if z := opmtch u then return simp z;
 %   else if z := get(car u,'opvalfn) then return apply1(z,u);
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
%    if (flagp(fn,'even) or flagp(fn,'odd))
%       and x and minusf numr(x := simp car x)
%     then <<if flagp(fn,'odd) then y := not y;
%   if (flagp(fn,'even) or flagp(fn,'odd) or flagp(fn,'oddreal)
%          and x and not_imag_num car x)
    if (flagp(fn,'even) or flagp(fn,'odd))
         and x and minusf numr(x := simp car x)
     then <<if not flagp(fn,'even) then y := not y;
        u := fn . prepsqxx negsq x . cddr u;
        if z := opmtch u
          then return if y then negsq simp z else simp z>>;
    u := mksq(u,1);
    return if y then negsq u else u
   end;

switch rounded;

symbolic procedure not_imag_num a;
 % Tests true if a is a number that is not a pure imaginary number.
 % Rebinds sqrtfn and *keepsqrts to make integrator happy.
   begin scalar !*keepsqrts,!*msg,!*numval,dmode,sqrtfn;
      dmode := dmode!*;
      !*numval := t;
      sqrtfn := get('sqrt,'simpfn);
      put('sqrt,'simpfn,'simpsqrt);
      on rounded,complex;
      a := resimp simp a;
      a := numberp denr a and domainp numr a and numr repartsq a;
      off rounded,complex;
      if dmode then onoff(get(dmode,'dname),t);
      put('sqrt,'simpfn,sqrtfn);
      return a
   end;

flagop even,odd,nonzero;

symbolic procedure domainvalchk(fn,u);
   begin scalar x;
      if (x := get(dmode!*,'domainvalchk)) then return apply2(x,fn,u);
      % The later arguments tend to be smaller ...
      u := reverse u;
  a:  if null u then return valuechk(fn,x)
       else if denr car u neq 1 then return nil;
      x := mk!*sq car u . x;
      u := cdr u;
      go to a
   end;

symbolic procedure valuechk(fn,u);
   begin scalar n;
      if (n := get(fn,'number!-of!-args)) and
         length u neq n and
         not flagp(fn, 'variadic)
         or not n and
            u and
            cdr u and
            (get(fn,'!:rd!:) or get(fn,'!:rn!:))
       then <<
          if !*strict_argcount then
              rerror(alg,17,list("Wrong number of arguments to",fn))
          else lprim list("Wrong number of arguments to", fn) >>; 
      u := opfchk!!(fn . u);
      if u then return znumrnil
          ((if eqcar(u,'list) then list((u . 1) . 1) else u) ./ 1)
   end;

symbolic procedure znumrnil u; if znumr u then nil ./ 1 else u;

symbolic procedure znumr u;
   null (u := numr u) or numberp u and zerop u
   or not atom u and domainp u and
     (y and apply1(y,u) where y=get(car u,'zerop));

symbolic procedure opfchk!! u;
   begin scalar fn,fn1,sf,sc,int,ce; fn1 := fn := car u; u := cdr u;
     % first save fn and check to see whether fn is defined.
     % Integer functions are defined in !:rn!:,
     % real functions in !:rd!:, and complex functions in !:cr!:.
      fn := if flagp(fn,'integer) then <<int := t; get(fn,'!:rn!:)>>
         else if !*numval and dmode!* memq '(!:rd!: !:cr!:)
            then get(fn,'!:rd!:);
      if not fn then return nil;
      sf := if int then 'simprn
         else if (sf := get(fn,'simparg)) then sf else 'simprd;
     % real function fn is defined.  now check for complex argument.
      if int or not !*complex then go to s; % the simple case.
     % mode is complex, so check for complex argument.
     % list argument causes a slight complication.
      if eqcar(car u,'list)
         then if (sc := simpcr revlis cdar u) and eqcar(sc,nil)
            then go to err else go to s;
      if not (u := simpcr revlis u) then return nil
     % if fn1 = 'expt, then evaluate complex function only; else
     % if argument is real, evaluate real function, but if error
     % occurs, then evaluate complex function.
      else if eqcar(u,nil) or
          fn1 eq 'expt and rd!:minusp caar u then u := cdr u
         else <<ce := cdr u; u := car u; go to s>>;
     % argument is complex or real function failed.
     % now check whether complex fn is defined.
 evc: if fn := get(fn1,'!:cr!:) then go to a;
 err: rerror(alg,18,list(fn1,"is not defined as complex function"));
   s: if not (u := apply1(sf, revlis u)) then return nil;
   a: u := errorset!*(list('apply,mkquote fn,mkquote u),nil);
      if errorp u then
         if ce then <<u := ce; ce := nil; go to evc>> else return nil
       else return if int then intconv car u else car u
   end;

symbolic procedure intconv x;
   if null dmode!* or dmode!* memq '(!:rd!: !:cr!:) then x
   else apply1(get(dmode!*,'i2d),x);

symbolic procedure simpcr x;
 % Returns simprd x if all args are real, else nil . "simpcr" x.
  if atom x then nil else
   <<(<<if not errorp y then z := car y;
        y := simplist x where dmode!* = '!:cr!:;
        if y then z . y else z>>)
   where z=nil,y=errorset!*(list('simprd,mkquote x),nil)>>;

symbolic procedure simprd x;
   % Converts any argument list that can be converted to list of rd's.
   if atom x then nil else <<simplist x where dmode!* = '!:rd!:>>;

symbolic procedure simplist x;
   begin scalar fl,c; c := get(dmode!*,'i2d);
     x := for each a in x collect (not fl and
        <<if null (a := mconv numr b) then a := 0;
          if numberp a then a := apply1(c,a)
             else if not(domainp a and eqcar(a,dmode!*)) then fl := t;
          if not fl and
             (numberp(b := mconv denr b) and (b := apply1(c,b))
             or domainp b and eqcar(b,dmode!*))
                then apply2(get(dmode!*,'quotient),a,b) else fl := t>>
        where b=simp!* a);
    if not fl then return x
   end;

symbolic procedure mconv v; <<dmconv0 dmode!*; mconv1 v>>;

symbolic procedure dmconv0 dmd;
   dmd!* := if null dmd then '!:rn!:
        else if dmd eq '!:gi!: then '!:crn!: else dmd;

symbolic procedure dmconv1 v;
   if null v or eqcar(v,dmd!*) then v
   else if atom v then if flagp(dmd!*,'convert)
      then apply1(get(dmd!*,'i2d),v) else v
   else if domainp v then apply1(get(car v,dmd!*),v)
   else lpow v .* dmconv1(lc v) .+ dmconv1(red v);

symbolic procedure mconv1 v;
   if domainp v then drnconv v
   else lpow v .* mconv1(lc v) .+ mconv1(red v);

symbolic procedure drnconv v;
   if null v or numberp v or eqcar(v,dmd!*) then v else
   <<(if y and atom y then apply1(y,v) else v)
   where y=get(car v,dmd!*)>>;

% Absolute Value Function.

symbolic procedure simpabs u;
   if null u or cdr u then mksq('abs . revlis u, 1)  % error?.
    else begin scalar x;
      u := car u;
      if numberp u then return abs u ./ 1
       else if x := sign!-abs u then return x;
      u := simp!* u;
      return if null numr u then nil ./ 1
        else quotsq(simpabs1 numr u, simpabs1 denr u);
    end;

symbolic procedure simpabs1 u;
   % Currently abs(sqrt(2)) does not simplify, whereas it clearly
   % should simplify to just sqrt(2).  The facts that abs(i) -> 1 and
   % abs(sqrt(-2)) -> abs(sqrt(2)) imply that REDUCE regards abs as
   % the complex modulus function, in which case I think it is always
   % correct to commute abs and sqrt.  However, I will do this only if
   % the result is a simplification.  FJW, 18 July 1998
   begin scalar x,y,w;
      x:=prepf u; u := u ./ 1;
      if eqcar(x,'minus) then x:=cadr x;
      % FJW: abs sqrt y -> sqrt abs y if abs y simplifies.
      if eqcar(x,'sqrt) then
         return !*kk2q if eqcar(y:=reval('abs.cdr x), 'abs)
         then {'abs, x} else {'sqrt, y};
%%    if eqcar(x,'times) and (y:=split!-sign cdr x) then
%%    <<w:=simp!* retimes car y; u:=quotsq(u,w);
%%      if cadr y then
%%       <<y:=simp!* retimes cadr y; u:=quotsq(u,y);
%%         w:=multsq(negsq y,w)>>
%%    >>;
      if eqcar(x,'times) then
      begin scalar abslist, noabs;
         for each fac in cdr x do
            % FJW: abs sqrt y -> sqrt abs y if abs y simplifies.
            if eqcar(fac,'sqrt)
               and not eqcar(y:=reval('abs.cdr fac), 'abs)
            then noabs := {'sqrt, y} . noabs
            else abslist := fac . abslist;
         abslist := reversip abslist;
         if noabs then
            u := quotsq(u, noabs := simp!*('times . reversip noabs));
         if (y:=split!-sign abslist) then
         <<w:=simp!* retimes car y; u:=quotsq(u,w);
            if cadr y then
            <<y:=simp!* retimes cadr y; u:=quotsq(u,y);
               w:=multsq(negsq y,w)>>;
            if noabs then w := multsq(noabs, w)
         >>
         else w := noabs
      end;
      if numr u neq 1 or denr u neq 1 then
         u:=quotsq(mkabsf1 absf numr u,mkabsf1 denr u);
      if w then u:=multsq(w,u);
      return u
   end;

%symbolic procedure rd!-abs u;
%   % U is a prefix expression.  If it represents a constant, return the
%   % abs of u.
%   (if !*rounded or not constant_exprp u then nil
%    else begin scalar x,y,dmode!*;
%           setdmode('rounded,t) where !*msg := nil;
%           x := aeval u;
%           if evalnumberp x
%             then if null !*complex or 0=reval {'impart,x}
%                    then y := if evalgreaterp(x,0) then u
%                               else if evalequal(x,0) then 0
%                               else {'minus,u};
%           setdmode('rounded,nil) where !*msg := nil;
%           return if y then simp y else nil
%       end) where alglist!*=alglist!*;

symbolic procedure sign!-abs u;
   % Sign based evaluation of abs - includes the above rd!-abs
   % method as sub-branch.
    <<if not numberp n then nil else
      simp if n<0 then {'minus,u} else if n=0 then 0 else u
      >> where n=sign!-of u;

symbolic procedure constant_exprp u;
   % True if u evaluates to a constant (i.e., number).
   if atom u
     then numberp u or flagp(u,'constant) or u eq 'i and idomainp()
    else (flagp(car u,'realvalued)
             or flagp(car u,'alwaysrealvalued)
             or car u memq '(plus minus difference times quotient)
             or get(car u,'!:rd!:)
             or !*complex and get(car u,'!:cr!:))
          and not atom cdr u
          and constant_expr_listp cdr u;

symbolic procedure constant_expr_listp u;
   % True if all members of u are constant_exprp.
   % U can be a dotted pair as well as a list.
   if atom u
     then null u or numberp u or flagp(u,'constant)
        or u eq 'i and idomainp()
    else constant_exprp car u and constant_expr_listp cdr u;

symbolic procedure mkabsf0 u; simp{'abs,mk!*sq !*f2q u};

symbolic procedure mkabsf1 u;
   if domainp u then mkabsfd u
    else begin scalar x,y,v;
           x := comfac!-to!-poly comfac u;
           u := quotf1(u,x);
           y := split!-comfac!-part x;
           x := cdr y;
           y := car y;
           if positive!-sfp u then <<y := multf(u,y); u := 1>>;
           u := multf(u,x);
           v := lnc y;
           y := quotf1(y,v);
           v := multsq(mkabsfd v,y ./ 1);
           return if u = 1 then v
                   else multsq(v,simpiden list('abs,prepf absf u))
        end;

symbolic procedure mkabsfd u;
   if null get('i,'idvalfn) then absf u ./ 1
    else (simpexpt list(prepsq nrm,'(quotient 1 2))
          where nrm = addsq(multsq(car us,car us),
                             multsq(cdr us,cdr us))
          where us = splitcomplex u);

symbolic procedure positive!-sfp u;
   if domainp u
      then if get('i,'idvalfn)
              then !:zerop impartf u and null !:minusp repartf u
            else null !:minusp u
    else positive!-powp lpow u and positive!-sfp lc u
         and positive!-sfp red u;

symbolic procedure positive!-powp u;
   not atom car u and caar u memq '(abs norm);

% symbolic procedure positive!-powp u;
%    % This definition allows for the testing of positive valued vars.
%    if atom car u then flagp(car u, 'positive)
%     else ((if x then apply2(x,car u,cdr u) else nil)
%           where x = get(caar u,'positivepfn));

symbolic procedure split!-comfac!-part u;
   split!-comfac(u,1,1);

symbolic procedure split!-comfac(u,v,w);
   if domainp u then multd(u,v) . w
    else if red u then
       if positive!-sfp u then multf(u,v) . w
        else v . multf(u,w)
    else if mvar u eq 'i then split!-comfac(lc u,v,w)
    else if positive!-powp lpow u
      then split!-comfac(lc u,multpf(lpow u,v),w)
    else split!-comfac(lc u,v,multpf(lpow u,w));

put('abs,'simpfn,'simpabs);

symbolic procedure simpdiff u;
   <<ckpreci!# u; addsq(simpcar u,simpminus cdr u)>>;

put('difference,'simpfn,'simpdiff);

symbolic procedure simpminus u;
   negsq simp carx(u,'minus);

put('minus,'simpfn,'simpminus);

symbolic procedure simpplus u;
   begin scalar z;
     if length u=2 then ckpreci!# u;
     z := nil ./ 1;
  a: if null u then return z;
     z := addsq(simpcar u,z);
     u := cdr u;
     go to a
   end;

put('plus,'simpfn,'simpplus);

symbolic procedure ckpreci!# u;
   % Screen for complex number input.
   !*complex
      and (if a and not b then ckprec2!#(cdar u,cadr u)
            else if b and not a then ckprec2!#(cdadr u,car u))
            where a=timesip car u,b=timesip cadr u;

symbolic procedure timesip x; eqcar(x,'times) and 'i memq cdr x;

symbolic procedure ckprec2!#(im,rl);
   % Strip im and rl to domains.
   <<im := if car im eq 'i then cadr im else car im;
     if eqcar(im,'minus) then im := cadr im;
     if eqcar(rl,'minus) then rl := cadr rl;
     if domainp im and domainp rl and not(atom im and atom rl)
        then ckprec3!#(!?a2bf im,!?a2bf rl)>>;

remflag('(!?a2bf),'lose);   % Until things stabilize.

symbolic smacro procedure make!:ibf (mt, ep);
   '!:rd!: . (mt . ep);

symbolic smacro procedure i2bf!: u; make!:ibf (u, 0);

symbolic procedure !?a2bf a;
   % Convert decimal or integer to bfloat.
   if atom a then if numberp a then i2bf!: a else nil
    else if eqcar(a,'!:dn!:) then a;

symbolic procedure ckprec3!#(x,y);
  % if inputs are valid, check for precision increase.
   if x and y then
   precmsg max(length explode abs cadr x+cddr x,
               length explode abs cadr y+cddr y);

symbolic procedure simpquot q;
   (if null numr u
      then if null numr v then rerror(alg,19,"0/0 formed")
            else rerror(alg,20,"Zero divisor")
     else if dmode!* memq '(!:rd!: !:cr!:) and domainp numr u
        and domainp denr u and domainp denr v
            and !:onep denr u and !:onep denr v
      then (if null numr v then nil else divd(numr v,numr u)) ./ 1
     else <<q := multsq(v,simprecip cdr q);
            if !*modular and null denr q
              then rerror(alg,201,"Zero divisor");
            q>>)
   where v=simpcar q,u=simp cadr q;

put('quotient,'simpfn,'simpquot);

symbolic procedure simprecip u;
   if null !*mcd then simpexpt list(carx(u,'recip),-1)
    else invsq simp carx(u,'recip);

put('recip,'simpfn,'simprecip);

symbolic procedure simpset u;
  begin scalar x;
     x := prepsq simp!* car u;
     if null x % or not idp x
       then typerr(x,"set variable");
     let0 list(list('equal,x,mk!*sq(u := simp!* cadr u)));
     return u
  end;

put ('set, 'simpfn, 'simpset);

symbolic procedure simpsqrt u;
   if u=0 then nil ./ 1 else
   if null !*keepsqrts
     then simpexpt1(car u, simpexpon '(quotient 1 2), nil)
    else begin scalar x,y;
       x := xsimp car u;
       return if null numr x then nil ./ 1
               else if denr x=1 and domainp numr x and !:minusp numr x
                then if numr x=-1 then simp 'i
                      else multsq(simp 'i,
                                  simpsqrt list prepd !:minus numr x)
               else if y := domainvalchk('sqrt,list x) then y
               else simprad(x,2)
     end;

symbolic procedure xsimp u; expchk simp!* u;

symbolic procedure simptimes u;
   begin scalar x,y;
    if null u then return 1 ./ 1;
    if tstack!* neq 0 or null mul!* then go to a0;
    y := mul!*;
    mul!* := nil;
    a0: tstack!* := tstack!*+1;
    x := simpcar u;
    a:    u := cdr u;
    if null numr x then go to c
     else if null u then go to b;
    x := multsq(x,simpcar u);
    go to a;
    b:    if null mul!* or tstack!*>1 then go to c;
    x:= apply1(car mul!*,x);
    alglist!* := nil . nil;   % since we may need MUL!* set again.
    mul!*:= cdr mul!*;
    go to b;
    c:    tstack!* := tstack!*-1;
    if tstack!* = 0 then mul!* := y;
    return x;
   end;

put('times,'simpfn,'simptimes);

symbolic procedure resimp u;
   % U is a standard quotient.
   % Value is the resimplified standard quotient.
   resimp1 u where varstack!*=nil;

symbolic procedure resimp1 u;
   begin
      u := quotsq(subf1(numr u,nil),subf1(denr u,nil));
      !*sub2 := t;
      return u
   end;

symbolic procedure simp!*sq u;
   if cadr u and null !*resimp then car u else resimp1 car u;

put('!*sq,'simpfn,'simp!*sq);

endmodule;

end;
