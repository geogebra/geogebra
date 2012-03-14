module reval4;   % Support for REDUCE 4 evaluation.

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


% The following selectors and constructors could be smacros.

symbolic procedure type u; car u;

% mkobject -- defined in block4.red.

symbolic procedure value u; cadr u;

symbolic procedure mknovalobj; mkobject(nil,'noval);

symbolic procedure getobject u;
   (if x and type x eq 'generic then n_form print value x else x)
    where x=get(u,'avalue);

symbolic procedure putobject(u,v,w);
   % Store value v for object u of type w.
   put(u,'avalue,mkobject(v,w));

% ---------------------------------------

symbolic procedure xtype(u,v);
   % True if type of u is liftable to type v.
   xtype1(type u,v);

symbolic procedure xtype1(u,v);
   if null type_in_pckgp u then nil
    else u eq v or xtypelist(get(u,'uptree),v);

symbolic procedure xtypelist(u,v);
   u and (xtype1(car u,v) or xtypelist(cdr u,v));

symbolic procedure rapply(u,v);
   % Apply generic operator u to argument list v.
%  type_reduce1 rapply1(u,v);   % Already done by rapply1.
   rapply1(u,v);

symbolic procedure rapply1(u,v);
   begin scalar x,y;
      % Look for named structure (e.g., array or matrix).
      if (x := getobject u) and (y := get(type x,'getfn))
        then return type_reduce1 apply2(y,x,v);
      x := for each j in v collect type j;
      y := type_function(u,x,v);
      if null y then if flagp(u,'opr) then u := eval_generic(u,v) else
        if null cdr x then rederr list(u,"not defined for type",car x) else
        rederr(u . "not defined for types" . x) else
       u := apply(car y,v);
%     if !*specification_reduce then
      u := type_reduce1 u;      % Always reduce to ground type for now.
      if null !*reduce4    % It must have been turned off.
        then return value u
       else return u
   end;

endmodule;

end;
