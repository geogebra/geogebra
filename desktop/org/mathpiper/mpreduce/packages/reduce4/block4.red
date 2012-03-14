module block4;   % REDUCE 4 block statement and related operators.

% Author: Anthony C. Hearn.

% Copyright (c) 1995 RAND.  All rights reserved.

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


fluid '(blockrestype!*);

remprop('return,'xform);  %%% has this property in the original version.

symbolic procedure n_formblock(u,vars);
   {type x,'prog . append(n_initprogvars cadr u,value x)}
     where x =  n_formprog1(cddr u,append(cadr u,vars));

symbolic procedure n_initprogvars u;
   begin scalar x,y,z;
    a: if null u then return(reversip!* x . reversip!* y)
       else if (z := get(caar u,'initvalue!*))  % variable
          or (z := get_type_initvalue cdar u)   % type
        then y := mksetq(caar u,mkquote z) . y
       else y := mksetq(caar u,mkquote ideval nil) . y;
      x := caar u . x;
      u := cdr u;
      go to a
   end;

symbolic procedure get_type_initvalue u;
   if atom u then get(u,'initvalue!*) else get(car u,'initvalue!*);

symbolic procedure mkobject(u,v);
   % Makes an object with value u and type v.
   % Next line is a kludge that needs to be fixed up in the poly code.
   if null u and v eq 'poly then list('zero,0) else
   list(v,u);

put('int,'initvalue!*,mkobject(0,'zero));

put('list,'initvalue!*,mkobject(nil,'list));

put('poly,'initvalue!*,mkobject(0,'zero));

symbolic procedure n_formprog(u,vars);
   {type x,'prog . cadr u . value x}
    where x = n_formprog1(cddr u,n_pairvars(cadr u,vars));

symbolic procedure n_formprog1(u,vars);
   % left out the 'modefn' stuff from  above.
   begin scalar eptr,v,blockrestype!*;
     v := eptr := {nil};
     a: if null u
           then return {if blockrestype!* then blockrestype!*
                         else 'noval,cdr v};
        if null car u then nil
         else if atom car u then eptr := cdr rplacd(eptr,{car u})
         else eptr := cdr rplacd(eptr,cdr n_form1(car u,vars));
        u := cdr u;
        go to a
    end;

symbolic procedure n_formreturn(u,vars);
   begin scalar x;
     x := n_form1(cadr u,vars);
     if null blockrestype!*
        then blockrestype!* := type x
      else if xtype1(type x,blockrestype!*) then nil
      else if xtype1(blockrestype!*,type x)
              then blockrestype!* := type x
      else rederr {"block return types",type x,"and",
                   blockrestype!*,"are unrelated"};
     return {type x, 'return . cdr x}
   end;

put('return,'n_formfn,'n_formreturn);


put('rblock,'n_formfn,'n_formblock);

% symbolic procedure decl u;
%    begin scalar varlis,w;
%     a:  if cursym!* eq '!*semicol!* then go to c
%          else if cursym!* eq 'local and !*reduce4 then nil
%          else if not flagp(cursym!*,'type) then return varlis
%          else typerr(cursym!*,"local declaration");
%         w := cursym!*;
%         scan();
%         if not !*reduce4 and cursym!* eq 'procedure
%           then return procstat1 w;
% %       varlis := append(varlis,pairvars(remcomma xread1 nil,nil,w));
%         varlis := append(varlis,read_param_list nil);
%         if not(cursym!* eq '!*semicol!*) or null u then symerr(nil,t);
%     c:  scan();
%         go to a
%    end;

symbolic procedure read_type;
   % This is a very restricted parser for type expressions.
   begin scalar y,z;
      y := scan();
   a: z := scan();
      if z eq '!*lpar!* then <<y := list(y,xread 'paren); go to a>>
       else if null(z memq '(!*comma!* !*rpar!* !*semicol!* mapped_to))
        then <<y := if atom y then list(y,z) else aconc(y,z);
               go to a>>;
      return y
   end;

symbolic procedure read_param_list u;
   % Read a parameter list, with or without parentheses.
   % Used only when reduce4 is on.
   begin scalar lparp,x,y,z;
      x := cursym!*;
      if not(x eq '!*lpar!*) then go to b;
      lparp := t;
  a:  x := scan();
  b:  if not idp x then typerr(x,"parameter");
      y := scan();
      if y eq '!*colon!* then go to typed
%      else x := x . if u then u
%                     else if not !*reduce4 then 'scalar else 'generic;
       else x := x . 'generic;
  c:  if y eq '!*comma!* then progn(z := x . z, go to a)
       else if y eq '!*rpar!*
        then if lparp
              then if scan() eq '!*semicol!* then return reversip(x . z)
             else if cursym!* eq '!*colon!* and null u
              then return reversip(x . z)
                     else typerr(cursym!*,"delimiter")
              else rerror(rlisp,100,"Too many right parentheses")
       else if y eq '!*semicol!*
        then if lparp
               then rerror(rlisp,101,"Too few right parentheses")
       else return reversip(x . z);
  typed:
      x := x . read_type();
      y := cursym!*;
      go to c
   end;

flag('(local),'type);

endmodule;

end;
