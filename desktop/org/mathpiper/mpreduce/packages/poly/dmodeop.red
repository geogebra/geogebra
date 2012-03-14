module dmodeop;  % Generic operators for domain arithmetic.

% Author: Anthony C. Hearn.

% Copyright (c) 1991 The RAND Corporation.  All rights reserved.

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


% internal dividef;

fluid '(!*noequiv);    % !*convert

% switch convert;

% !*convert := t;

symbolic procedure !:difference(u,v);
   if null u then !:minus v else if null v then u
    else if u=v then nil
    else if atom u and atom v then u-v
    else dcombine(u,v,'difference);

symbolic procedure !:divide(u,v);
   % Returns a dotted pair of quotient and remainder of non-invertable
   % domain element U divided by non-invertable domain element V.
   % Note that a zero is returned as NIL.
   if null u then nil . nil
    else if null v then rerror(poly,202,"zero divisor")
    else if atom u and atom v then dividef(u,v)
    else dcombine(u,v,'divide);

symbolic procedure dividef(m,n);
   ((if car x=0 then nil else car x) . if cdr x=0 then nil else cdr x)
   where x=divide(m,n);

symbolic procedure !:expt(u,n);
   % Raises domain element U to integer power N.  Value is a domain
   % element.
   if null u then if n=0 then rerror(poly,11,"0/0 formed") else nil
    else if n=0 then 1
    else if n=1 then u
    else if u=1 then 1
    else if n<0
     then !:recip !:expt(if not fieldp u then mkratnum u else u,-n)
    else if atom u then u**n
%    Moved into the exponentiation method of !:mod!:
%    else if car u eq '!:mod!:
%     then (lambda x; if x=0 then nil else if x=1 then 1 else car u . x)
%          general!-modular!-expt(cdr u,n)
    else begin scalar v,w,x;
      if x := get(car u,'expt)
         then return apply2(x,u,n);
         % There was a special exponentiation method.
      v := apply1(get(car u,'i2d),1);   % unit element.
      x := get(car u,'times);
   a: w := n;
      if w-2*(n := n/2) neq 0 then v := apply2(x,u,v);
      if n=0 then return v;
      u := apply2(x,u,u);
      go to a
   end;

symbolic procedure !:gcd(u,v);
  if null u then v else if null v then u
   else if atom u and atom v then gcdn(u,v)
   else if fieldp u or fieldp v then 1
   else dcombine(u,v,'gcd);

% symbolic procedure !:i2d u;

symbolic procedure !:minus u;
   % U is a domain element. Value is -U.
   if null u then nil
    else if atom u then -u
    else (if x then apply1(x,u) else dcombine(u,-1,'times))
         where x=get(car u,'minus);

symbolic procedure !:minusp u;
   if atom u then minusp u else apply1(get(car u,'minusp),u);

symbolic procedure !:onep u;
   if atom u then onep u else apply1(get(car u,'onep),u);

symbolic procedure !:plus(u,v);
   if null u then v else if null v then u
    else if atom u and atom v
     then (if w=0 then nil else w) where w=u+v
    else dcombine(u,v,'plus);

% symbolic procedure !:prep u;

% symbolic procedure !:print u;

symbolic procedure !:quotient(u,v);
   if null u or u=0 then nil
    else if null v or v=0 then rerror(poly,12,"Zero divisor")
    else if atom u and atom v
     % We might also check that remainder is zero in integer case.
     then if null dmode!* then u/v else
        (if atom recipv then u*recipv else dcombine(u,recipv,'times))
                where recipv=!:recip v
    else dcombine(u,v,'quotient);

symbolic procedure !:recip u;
   % U is an invertable domain element. Value is 1/U.
   begin
      if numberp u
        then if abs u=1 then return u
       else if null dmode!* or dmode!* memq '(!:rd!: !:cr!:)
          then return !:rn2rd mkrn(1,u)
       else u := apply1(get(dmode!*,'i2d),u);
      return (if not atom x and car x='!:rn!: then !:rn2rd x else x)
         where x=dcombine(1,u,'quotient)
   end;

symbolic procedure !:rn2rd x;
   % Convert rn to rd in dmodes rd and cr if roundall is on.
   if !*roundall and !*rounded then !*rn2rd x else x;

symbolic procedure !:times(u,v);
   % We assume neither u nor v can be 0.
   if null u or null v then nil
    else if atom u and atom v then u*v
    else dcombine(u,v,'times);

symbolic procedure !:zerop u;
   if null u or u=0 then t
    else if atom u then nil
    else apply1(get(car u,'zerop),u);

symbolic procedure fieldp u;
   % U is a domain element. Value is T if U is invertable, NIL
   % otherwise.
   not atom u and flagp(car u,'field);

symbolic procedure gettransferfn(u,v);
   % This may be unnecessary.  If dmodechk has been called, then all
   % transfer functions should be defined.
   (if x then x else dmoderr(u,v)) where x=get(u,v);

symbolic procedure dcombine(u,v,fn);
   % U and V are domain elements, but not both atoms (integers).
   % FN is a binary function on domain elements;
   % Value is the domain element representing FN(U,V)
   % or pair of domain elements representing divide(u,v).
   <<u := if atom u
        then apply2(get(car v,fn),apply1(get(car v,'i2d),u),v)
       else if atom v
        then apply2(get(car u,fn),u,apply1(get(car u,'i2d),v))
       else if car u eq car v then apply2(get(car u,fn),u,v) else
      % convert anything to :ps: but not the reverse;
      % convert real to complex, never the reverse;
      % also convert rn or crn to rd or cr but not the reverse:
      % hence crn or gi plus rd requires that *both* convert to cr.
      (<<if (not(x and atom x)
             or (get(du,'cmpxfn) and not get(dv,'cmpxfn)
             or du memq dl and not(dv memq dl)) and dv neq '!:ps!:)
              % extra test added above by Alan Barnes to ensure
              % result is :ps: if either operand is a :ps:
            and not flagp(dv,'noconvert) then
           % convert v -> u but may first have to convert u.
            <<if du memq dml and dv eq '!:rd!:
                 or du eq '!:rd!: and dv memq dml then
                 <<u := apply1(get(du,'!:cr!:),u); du := '!:cr!:>>
              else if du eq '!:rn!: and dv eq '!:gi!:
                 or du eq '!:gi!: and dv eq '!:rn!: then
                 <<u := apply1(get(du,'!:crn!:),u); du := '!:crn!:>>;
              v := apply1(get(dv,du),v); x := get(du,fn)>>
            else <<u := apply1(x,u); x := get(dv,fn)>>;
         apply2(x,u,v)>>
       where x=get(du,dv),dml='(!:crn!: !:gi!:),dl='(!:rd!: !:cr!:))
       where du=car u,dv=car v;
       if !*rounded and !*roundall and not atom u then
        % atom test added by Alan Barnes in case a power series
        % operation has already produced an integer.
       int!-equiv!-chk
         if (v := car u) eq '!:rn!: and cddr u neq 1 then !*rn2rd u
         else if v eq '!:crn!: and (cdadr u neq 1 or cdddr u neq 1)
            then !*crn2cr u
         else u
       else if fn eq 'divide then   % Modified by Francis Wright.
          int!-equiv!-chk car u . int!-equiv!-chk cdr u
       else int!-equiv!-chk u>>;

symbolic procedure int!-equiv!-chk u;
   % U is a domain element. If U can be converted to 0, result is NIL,
   % if U can be converted to 1, result is 1,
   % if U is a rational or a complex rational and can be converted to
   % an integer, result is that integer,
   % if *convert is on and U can be converted to an integer, result
   % is that integer. Otherwise, U is returned.
   % In most cases, U will be structured.
   if !*noequiv then u
    else begin scalar x;
           if atom u then return if u=0 then nil else u
            else if apply1(get(car u,'zerop),u) then return nil
            else if apply1(get(car u,'onep),u) then return 1
%           else if null !*convert then return u
            else if (x := get(car u,'intequivfn)) and (x := apply1(x,u))
             then return if x=0 then nil else x
            else return u
      end;

% symbolic procedure minuschk u;
%    if eqcar(u,'minus)
%       and (numberp cadr u
%          or not atom cadr u and idp caadr u and get(caadr u,'dname))
%      then !:minus cadr u
%     else u;

endmodule;

end;
