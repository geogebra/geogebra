module depend; % Defining and checking expression dependency.

% Author: Anthony C. Hearn.
% Modifications by: Francis J. Wright <F.J.Wright@qmw.ac.uk>.

% Copyright (c) 1996 The RAND Corporation. All rights reserved.

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


fluid '(alglist!* depl!* frlis!*);

% DEPL* is a list of dependencies among kernels.

symbolic procedure depend u;
   depend0(u,t);

symbolic procedure nodepend u;
   <<rmsubs(); depend0(u,nil)>>;

rlistat '(depend nodepend);

%symbolic procedure depend0(u,bool);
%   % We need to include both <id> and <id>_ in the list to provide for
%   % ROOT_OF expressions.
%   <<for each x in cdr u do depend1(car u,x,bool);
%     if idp car u
%       then (for each x in cdr u do depend1(y,x,bool))
%             where y=intern compress append(explode car u,'(!! !_))>>;

symbolic procedure depend1(u,v,bool);
   begin scalar y,z;
      u := !*a2k u;
      v := !*a2k v;
      if u eq v then return nil;
      y := assoc(u,depl!*);
%     if y then if bool then rplacd(y,union(list v,cdr y))
%                else if (z := delete(v,cdr y)) then rplacd(y,z)
      if y then if bool
                 then depl!*:= repasc(car y,union(list v,cdr y),depl!*)
                 else if (z := delete(v,cdr y))
                  then depl!* := repasc(car y,z,depl!*)
                 else depl!* := delete(y,depl!*)
       else if null bool
         then lprim list(u,"has no prior dependence on",v)
       else depl!* := list(u,v) . depl!*
   end;

symbolic procedure depends(u,v);
   if null u or numberp u or numberp v then nil
    else if u=v then u
    else if atom u and u memq frlis!* then t
      %to allow the most general pattern matching to occur;
    else if (lambda x; x and ldepends(cdr x,v)) assoc(u,depl!*)
     then t
    else if not atom u and idp car u and get(car u,'dname) then
        (if depends!-fn then apply2(depends!-fn,u,v) else nil)
           where (depends!-fn = get(car u,'domain!-depends!-fn))
    else if not atom u
      and (ldepends(cdr u,v) or depends(car u,v)) then t
    else if atom v or idp car v and get(car v,'dname) then nil
    % else dependsl(u,cdr v);
    else nil;

symbolic procedure ldepends(u,v);
   % Allow for the possibility that U is an atom.
   if null u then nil
    else if atom u then depends(u,v)
    else depends(car u,v) or ldepends(cdr u,v);

symbolic procedure dependsl(u,v);
   v and (depends(u,car v) or dependsl(u,cdr v));

symbolic procedure freeof(u,v);
   not(smember(v,u) or v member assoc(u,depl!*));

symbolic operator freeof;

flag('(freeof),'boolean);

% infix freeof;

% precedence freeof,lessp;   %put it above all boolean operators;

% This following code, by Francis J. Wright, enhances the depend and
% nodepend commands.  If the first argument is an (algebraic) LIST
% then change the dependency for each element of it, i.e.

%   (no)depend {y1, y2, ...}, x1, x2, ...  maps to
%   (no)depend y1, x1, x2, ...;  (no)depend y2, x1, x2, ...; ...

% Also allow a sequence of such dependence sequences, where the
% beginning of each new sequence is indicated by a LIST of one or more
% dependent variables.

symbolic procedure depend0(u, bool);
   % u = y,x1,x2,..., {yy1,yy2,...},xx1,xx2,...,  OR
   % u = {y1,y2,...},x1,x2,..., {yy1,yy2,...},xx1,xx2,...,
 <<alglist!* := nil . nil;  % We need to clear cache.
   while u do
      begin scalar v;
         % Make v point to the next dependent variable list or nil.
         v := cdr u;
         while v and not rlistp car v do v := cdr v;
         for each y in (if rlistp car u then cdar u else {car u}) do
            begin scalar x;
               x := u;
               while not((x := cdr x) eq v) do depend1(y,car x,bool);
               if idp y
                 then <<y := intern compress append(explode y,'(!! !_));
                        x := u;
                        while not((x := cdr x) eq v) do
                           depend1(y,car x,bool)>>
            end;
         u := v
      end>>;

endmodule;

end;
