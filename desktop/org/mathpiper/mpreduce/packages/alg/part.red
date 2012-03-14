module part;  % Access and updates parts of an algebraic expression.

% Author: Anthony C. Hearn.

% Copyright (c) 1991 RAND. All rights reserved.

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


fluid '(!*intstr);

symbolic procedure revalpart u;
   if null u then rerror(alg,201,"part called without arguments")
    else begin scalar !*intstr,bool,expn,v,z;
      !*intstr := t;   % To make following result in output form.
      expn := if (z := getrtype car u) eq 'list then listeval0 car u
               else reval car u;
      !*intstr := nil;
      v := cdr u;
      while v do
         begin scalar x,y,ishold;
%@@@ Debugging print here
%          princ "part ["; prin expn; princ "] : "; print car v;
%@@@ End of debug print
           ishold := eqcar(expn, '!*hold);
           if atom expn then <<parterr2(expn,car v); bool := t>>
            else if not numberp(x := reval car v)
             then msgpri("Invalid argument",car v,"to part",nil,t)
% As of July 2011 I can only see 'partop properties set in the
% eds package (on !!cfrm!! and !!eds!! and in the taylor package
% on Taylor!*.
            else if (y := get(car expn,'partop))
             then return <<expn := apply2(y,expn,x); v := cdr v>>
            else if x=0
              then return <<expn :=
                              (if (getrtype w eq 'list) and (z := 'list)
                                 then listeval0 w
                                else if z eq 'list
                                 then <<!*intstr := t; w := reval w;
                                        !*intstr := z := nil; w>>
                                else w) where w = car expn;
                           v := nil>>
            else if x<0 then <<x := -x; y := reverse cdr expn>>
            else y := cdr expn;
           if ishold then <<
              y := car y;
              if atom y then <<parterr2(expn,car v); bool:=t>>
              else y := cdr y >>;
           if bool then nil
            else if length y<x then <<parterr2(expn,car v); bool := t>>
            else if ishold then <<
                expn := nth(y, x);
                if not atom expn and
                   not eqcar(expn, '!*hold) then expn := list('!*hold, expn) >>
            else expn := (if (getrtype w eq 'list) and (z := 'list)
                            then listeval0 w
                           else if z eq 'list
                            then <<!*intstr := t; w := reval w;
                                   !*intstr := z := nil; w>>
                           else w) where w = nth(y,x);
           v := if bool then nil else cdr v
        end;
      return if bool then 0 else reval expn
   end;

symbolic procedure parterr2(u,v);
   <<msgpri("Expression",u,"does not have part",v,nil); 0>>;

put('part,'psopfn,'revalpart);

flag('(part),'immediate);

symbolic procedure revalsetpart u;
   % Simplifies a SETPART expression.
   if null cdr u then rerror(alg,201,"part called without arguments")
    else begin scalar !*intstr,x,y;
      x := reverse cdr u;
      !*intstr := t;
      y := reval car u;
      !*intstr := nil;
      return  revalsetp1(y,reverse cdr x,reval car x)
   end;

symbolic procedure revalsetp1(expn,ptlist,rep);
   if null ptlist then rep
    else if atom expn then parterr(expn,car ptlist)
    else begin scalar x,y;
      if not numberp(x := reval car ptlist)
             then msgpri("Invalid argument",car ptlist,"to part",nil,t)
       else return
        if y := get(car expn,'setpartop) then apply3(y,expn,ptlist,rep)
         else if x=0 then rep . cdr expn
         else if x<0
          then car expn .
                reverse ssl(reverse cdr expn,
                            -x,cdr ptlist,rep,expn . car ptlist)
         else car expn . ssl(cdr expn,x,cdr ptlist,
                             rep,expn . car ptlist)
   end;

symbolic procedure ssl(expn,indx,ptlist,rep,rest);
   if null expn then parterr(car rest,cdr rest)
    else if indx=1 then revalsetp1(car expn,ptlist,rep) . cdr expn
    else car expn . ssl(cdr expn,indx-1,ptlist,rep,rest);

put('part,'rtypefn,'rtypepart);

symbolic procedure rtypepart u;
   if getrtypecar u then 'yetunknowntype else nil;

% symbolic procedure rtypepart(u);
%   if null cdr u then getrtypecar u
%    else begin scalar x,n;
%       x := car u;
%       if idp x then <<x := get(car u,'avalue);
%                       if x then x := cadr x>>;
%       if eqcar(x,'list) and numberp (n := aeval cadr u)
%         then return rtypepart(nth(cdr x,n) . cddr u)
%    end;

% put('part,'setqfn,'(lambda (u v w) (setpart!* u v w)));

put('setpart!*,'psopfn,'revalsetpart);

symbolic procedure arglength u;
   begin scalar !*intstr,x;
      if null u then return 0;
      !*intstr := t;
      x := reval u;
      return if atom x then -1 else length cdr x
   end;

flag('(arglength),'opfn);

flag('(arglength),'noval);

put('partlength,'psopfn,'partlengthreval);

symbolic procedure partlengthreval u;
   (if atom expn then 0 else length cdr expn)
      where expn = (if getrtype car u eq 'list then listeval0 car u
                     else reval car u) where !*intstr = t;

% "unhold" undoes the effect of "hold", and I define it here because
% this file (in procedure revalpart) has a major fraction of the
% code that supports "hold".

algebraic procedure unhold u; u where !*hold(~x) => x;

endmodule;

end;
