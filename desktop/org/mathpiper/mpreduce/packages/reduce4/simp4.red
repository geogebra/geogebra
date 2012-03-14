module simp4;   % REDUCE 4 extensions for simplification.

% Author:  Anthony C. Hearn.

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


fluid '(zero);

symbolic procedure ideval u;
   % Find true (dynamic) value of id u.
%  (if x then x else list('variable,u)) where x=getobject u;
   kernelvalue u;

symbolic procedure kernelvalue u;
   % Return value of untagged kernel u.
   begin scalar x;
      if null subfg!* then return type_reduce(u,'kernel)
       else if x := assoc(u,wtl!*)
        then return if null car(x := mksq('k!*,cdr x)) then x else % *****
                  type_reduce(multsq(x,!*p2f getpower(car fkern u,1) ./ 1),
                              'ratpol)
       else if atom u
        then <<if null !*nosubs and (x := get(u,'avalue))
                then return if type x memq '(generic scalar) % REDUCE 3 style
                              then simp4 value x else x;
               % Tell system u used as algebraic var.
               if idp u then flag(list u,'used!*);
               return mkobject(u,'variable)>>
       else if null !*nosubs and (x := assoc(u,get(car u,'kvalue)))
         % Old-style kernel value without type.
        then return simp4 cadr x
       else if not('used!* memq cddr (x := fkern u))
        then aconc(x,'used!*);
      return mkobject(car x,'xkernel)
   end;

symbolic procedure eval_generic(fn,u);
   % Evaluate a generic function fn with arguments u.
   % Note: we must use PREPSQXX and not PREPSQ* here, since the REVOP1
   % in SUBS3T uses PREPSQXX, and terms must be consistent to prevent a
   % loop in the pattern matcher.
   begin scalar x,y,z;
    u := for each j in u collect
             if x := get(type j,'prefix_convert)
               then apply1(x,value j) else value j;
    if u and car u=0 and flagp(fn,'odd) and not flagp(fn,'nonzero)
      then return ZERO;
    u := fn . u;
    if flagp(fn,'noncom) then ncmp!* := t;
    if null subfg!* then go to c
     else if (z := value(x := kernelvalue u)) neq u then return x;
    u := z;   % Make sure it's unique.
    if flagp(fn,'linear) and (z := formlnr u) neq u
      then return simp4 z
     else if z := opmtch u then return simp4 z
   ;%else if z := get(car u,'opvalfn) then return apply1(z,u);
    c:  if flagp(fn,'symmetric) then u := fn . ordn cdr u
     else if flagp(fn,'antisymmetric)
      then <<if repeats cdr u then return (nil ./ 1)
          else if not permp(z := ordn cdr u,cdr u) then y := t;
         % The following patch was contributed by E. Schruefer.
         fn := car u . z;
         if z neq cdr u and (z := opmtch fn)
           then return if y then negsq simp4 z else simp4 z;  % ******
         u := fn>>;
    if (flagp(fn,'even) or flagp(fn,'odd))
       and x and minusf numr(x := simp car x)   % ******
     then <<if flagp(fn,'odd) then y := not y;
        u := fn . prepsqxx negsq x . cddr u;
        if z := opmtch u
          then return if y then negsq simp z else simp z>>;
    u := mksq(u,1);
    if y then u := negsq u;
    return type_reduce(u,'ratpol)
   end;

symbolic procedure simp4!* u;
   % This procedure applies REDUCE 3-style rules to a REDUCE 4 expr.
   % It operates similarly to simp!* for scalar expressions.
   % It should disappear eventually.
   begin scalar !*asymp!*,x;
      if (x := type u) memq '(nzint variable zero) then return u
       else if x eq 'xpoly then u := value u ./ 1
       else if x eq 'xratpol then u := value u
       else rederr {"No simplification for type",x};
      u := subs2 u;
      if !*combinelogs then u := clogsq!* u;
      % Must be here, since clogsq!* can upset girationalizesq!:.
      % For defint, it is necessary to turn off girationalizesq - SLK.
      if dmode!* eq '!:gi!: and not !*norationalgi
        then u := girationalize!: u
       else if !*rationalize then u := rationalizesq u
       else u := rationalizei u;
      % If any leading terms have cancelled, a gcd check is required.
      if !*asymp!* and !*rationalize then u := gcdchk u;
      return type_reduce(u,'ratpol)
   end;

symbolic procedure simp4 u; type_reduce(simp u,'ratpol);

put('xpoly,'prefix_convert,'prepf);

put('xratpol,'prefix_convert,'prepsqxx);

% Flag all generic operators.

% However, mapobl isn't defined in CSL.

%  mapobl function
%  (lambda j; if (get(j,'simpfn) eq 'simpiden) then flag(list j,'opr));

flag('(cos sin),'opr);

endmodule;

end;
