module str;  % Routines for structuring expressions.

% Author: Anthony C. Hearn.

% Copyright (c) 1991 The RAND Corporation. All rights reserved.

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


fluid '(!*fort !*nat !*savestructr scountr svar svarlis);

global '(varnam!*);

varnam!* := 'ans;

switch savestructr;

flag('(structr),'intfn);        % To fool the supervisor into printing
                                % results of STRUCTR.

% ***** two essential uses of RPLACD occur in this module.

symbolic procedure structr u;
   begin scalar scountr,fvar,svar,svarlis;
      % SVARLIS is a list of elements of form:
      % (<unreplaced expression> . <newvar> . <replaced exp>);
      scountr :=0;
      fvar := svar := varnam!*;
      if cdr u
        then <<fvar := svar := cadr u; if cddr u then fvar := caddr u>>;
      u := structr1 aeval car u;
      if !*fort then svarlis := reversip!* svarlis
       else if not !*savestructr
        then <<assgnpri(u,nil,'only);
               if not eqcar(u,'mat) then terpri(); % MAT already has eol
               if scountr=0 then return nil
                else <<if null !*nat then terpri();
                       prin2t "   where">>>>;
      if !*fort or not !*savestructr
        then for each x in svarlis do
             <<terpri!* t;
               if null !*fort then prin2!* "      ";
               assgnpri(cddr x,list cadr x,t)>>;
      if !*fort then assgnpri(u,list fvar,t)
       else if !*savestructr
        then return 'list . u .
                        foreach x in svarlis
                           collect list('equal,cadr x,
                                               mkquote cddr x)
   end;

rlistat '(structr);

symbolic procedure structr1 u;
   % This routine considers special case STRUCTR arguments. It could be
   % easily generalized.
   if atom u then u
    else if car u eq 'mat
     then car u .
        (for each j in cdr u collect for each k in j collect structr1 k)
    else if car u eq 'list
     then 'list . for each j in cdr u collect structr1 j
    else if car u eq 'equal then list('equal,cadr u,structr1 caddr u)
    else if car u eq '!*sq
     then mk!*sq(structf numr cadr u ./ structf denr cadr u)
    else if getrtype u then typerr(u,"STRUCTR argument")
    else u;

symbolic procedure structf u;
   if null u then nil
    else if domainp u then u
    else begin scalar x,y;
        x := mvar u;
        if sfp x then if y := assoc(x,svarlis) then x := cadr y
                else x := structk(prepsq!*(structf x ./ 1),
                                  structvar(),x)
%        else if not atom x and not atomlis cdr x
          else if not atom x
             and not(atom car x and flagp(car x,'noreplace))
          then if y := assoc(x,svarlis) then x := cadr y
                else x := structk(x,structvar(),x);
% Suggested patch by Rainer Schoepf to cache powers.
%       if ldeg u = 1
%         then return x .** ldeg u .* structf lc u .+ structf red u;
%       z := retimes exchk list (x .** ldeg u);
%       if y := assoc(z,svarlis) then x := cadr y
%        else x := structk(z, structvar(), z);
%       return x .** 1 .* mystructf lc u .+ mystructf red u
        return x .** ldeg u .* structf lc u .+ structf red u
     end;

symbolic procedure structk(u,id,v);
   begin scalar x;
      if x := subchk1(u,svarlis,id)
        then rplacd(x,(v . id . u) . cdr x)
       else if x := subchk2(u,svarlis)
        then svarlis := (v . id . x) . svarlis
       else svarlis := (v . id . u) . svarlis;
      return id
   end;

symbolic procedure subchk1(u,v,id);
   begin scalar w;
      while v do
       <<smember(u,cddar v)
            and <<w := v; rplacd(cdar v,subst(id,u,cddar v))>>;
         v := cdr v>>;
      return w
   end;

symbolic procedure subchk2(u,v);
   begin scalar bool;
      for each x in v do
       smember(cddr x,u)
          and <<bool := t; u := subst(cadr x,cddr x,u)>>;
      if bool then return u else return nil
   end;

symbolic procedure structvar;
   begin
      scountr := scountr + 1;
      return if arrayp svar then list(svar,scountr)
       else intern compress append(explode svar,explode scountr)
   end;

endmodule;

end;
