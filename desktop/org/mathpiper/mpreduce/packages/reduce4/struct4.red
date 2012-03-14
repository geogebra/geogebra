module structure; % REDUCE 4 support for indexed structures.

% Author: Anthony C. Hearn.

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


% These definitions are very careful about bounds checking. Appropriate
% optimizations in a given system might really speed things up.

symbolic procedure n_formstructure(u,vars,type);
   begin scalar x;
      u := cadr u;
      x := for each x in cdr u collect n_formstructure1(x,vars,type);
      return mkobject(n_structurefn(x,type),'noval)
   end;

symbolic procedure n_formstructure1(u,vars,type);
   begin scalar x;
      if not idp car u
      then typerr(car u,compress append(explode type, explode2 "! name"));
      x := for each j in cdr u collect lispeval cadr n_form1(j,vars);
      x :=  for each j in x collect
                if coercable(type j,'int) then value j
                 else typerr(value j,'int);
     return car u . x
   end;



symbolic procedure n_structurefn(u,type);
   <<for each x in u do n_structurefn1(x,type); mkquote mknovalobj()>>;

symbolic procedure n_structurefn1(u,type);
   begin scalar y;
      if flagp(type,'zeroelementp)
        then y := add1lis for each z in cdr u collect lispeval z
       else y := for each z in cdr u collect lispeval z;
      putobject(car u,mkn_structure(y,type),type);
      put(car u,'dimension,y)
   end;

symbolic procedure mkn_structure(u,type);
   % U is a list of positive integers representing structure bounds.
   % Value is a structure.
   if null u then mkobject(0,'zero)
%   else if type eq 'matrix then mkn_matrix u
    else begin integer n; scalar x;
      n := car u - 1;
      x := mkvect n;
      for i:=0:n do putv(x,i,mkn_structure(cdr u,type));
      return x
   end;

symbolic procedure getell(u,v);
   getell1(value u,for each x in v collect value x);

symbolic procedure getell1(u,v);
      if null v then u else getell1(getv(u,car v),cdr v);

symbolic procedure setell(u,v,w);
   setell1(value u,v,w);

symbolic procedure setell1(u,v,w);
   if null v then rederr "Structure confusion" else
   if null cdr v then putv(u,int_check car v,w) else
   setell1(getv(u,int_check car v),cdr v,w);

symbolic procedure int_check u;
   if coercable(type u,'int) then value u else typerr(value u,'int);

% Arrays.

flag('(array),'zeroelementp);

put('array,'n_formfn,'n_formarray);

symbolic procedure n_formarray(u,vars);
   n_formstructure(u,vars,'array);

put('array,'getfn,'getell);

put('array,'putfn,'setell);

endmodule;

end;
