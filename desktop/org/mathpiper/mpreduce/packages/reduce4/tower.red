module tower;    % Set up type hierarchy.

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


fluid '(!*protfg curr_pckg!*);

!*argnochk := nil;

symbolic procedure type_reduce(u,v);
   % Returns minimum type, value pair of expression u of type v.
   type_reduce1 {v,u};
   %type_reduce2(u,v,get(v,'typetree));

symbolic procedure type_reduce2(u,v,w);
   if null w then mkobject(u,v)  % no further reduction possible.
    else if null cdr w then type_reduce(u,car w)
                     % it must be at least this type.
    else (if x and (x := apply2(x,u,v)) then type_reduce(x,car w)
           else type_reduce2(u,v,cdr w))
          where x=(type_in_pckgp car w and get(car w,'boolfn));

symbolic procedure delrelatedtype(u,v);
   if null v then nil
    else if xtype1(car v,u) then cdr v
    else car v . delrelatedtype(u,cdr v);

symbolic procedure type_reduce5 u;
   begin scalar x,y,z,!*protfg; !*protfg := t;
     if null(x := get(type u,'!*nullary!*)) then return u;
     z := get(type u,'typetree);
     a: if null x then return mkobject(value u,car z);
        if null errorp (y := errorset({'apply1,mkquote x,
                                       mkquote u},nil,nil)) and car y
           then <<x := apply1(caar y,u);
                  return type_reduce1 x>>;
        for each j in cdaddr x do
          z := delrelatedtype(cadr cadadr j,z);
        return if null z then u else mkobject(value u,car z);
        %x := cdr x;
        go to a
   end;

symbolic procedure type_reduce1 u;
   begin scalar x,y;
     if null type_in_pckgp type u or
        null(x := get(type u,'!*nullary!*)) then return u;
     if y := apply1(x,u)
           then <<x := apply1(car y,u);
                  return type_reduce1 x>>;
     return u
   end;



symbolic procedure type_function(fn,typelist,args);
   begin scalar x;
      return if (x := get(fn,'ranks))
                and (x := assoc(length typelist,x))
                and (x := type_assoc(typelist,cdr x,args))
               then x
              else nil
   end;

symbolic procedure type_assoc(typelist,type_assoc_list,args);
   % Determine if there's a match for typelist in type_assoc_list.
   begin scalar x;
    if x := type_assoc1(car typelist,cdr typelist,type_assoc_list,args)
       then return x
     else if x := atsoc('generic,type_assoc_list) then return cdr x
     else return nil
   end;


symbolic procedure type_assoc1(type,typelist,type_assoc_list,args);
   begin scalar x,y,z;
      if (type_in_pckgp type and
          (x := type_assoc0(type,type_assoc_list)))
%        or (x := atsoc('generic,type_assoc_list))
        then if null typelist
                then return pckg_op_chk
                       (if atom cadr x then
                          if atom caddr x then cdr x
                        else if z := constraint_apply(cdr x,args)
                              then z else nil)
         % We assume termination with the actual name of function here.
       else if y := type_assoc1(car typelist,cdr typelist,cdr x,args)
               then return y;
      if z := get(type,'uptree)
        then <<while z and
                 not (x := type_assoc1(car z,typelist,type_assoc_list,args))
                 do z := cdr z; return x>>
       else return nil
   end;

symbolic procedure constraint_apply(u,v); apply(u,v);


symbolic procedure type_assoc0(type,type_assoc_list);
   assoc(type,type_assoc_list);


symbolic procedure check_type(u,t_type);
   % Checks that bottom type of u is compatible with a target type.
   % Returns u if no error.
   if xtype1(type u,t_type) then u
    else rederr {"Ceiling target type",t_type,
                 "is unrelated to result type",
                                  type u};

symbolic procedure get_disambop(name,arity,coarity);
   begin scalar x;
      return if (x := get(name,'ranks))
                and (x := assoc(length arity,x))
                and (x := disambop_assoc(arity,cdr x,coarity))
               then x
              else nil
   end;

symbolic procedure disambop_assoc(typelist,type_assoc_list,coarity);
   begin scalar x;
     if x :=
        disambop_assoc1(car typelist,cdr typelist,type_assoc_list,coarity)
        then return x
      else if x := atsoc('generic,type_assoc_list) then return cdr x
      else return nil
   end;


symbolic procedure disambop_assoc1(type,typelist,type_assoc_list,coarity);
   begin scalar x,y,z;
      if (type_in_pckgp type and (x := assoc(type,type_assoc_list)))
%        or (x := atsoc('generic,type_assoc_list))
        then if null typelist
                then return pckg_op_chk
                       (%if atom cadr x then
                        %  if atom caddr x then cdr x
                        %else
                        if z := assoc_coarity(coarity,cdr cadddr x)
                              then z else nil)
         % We assume termination with the actual name of function here.
       else if y := disambop_assoc1(car typelist,cdr typelist,cdr x,coarity)
               then return y;
      if z := get(type,'uptree)
        then <<while z and
                 not (x := disambop_assoc1(car z,typelist,
                                           type_assoc_list,coarity))
                 do z := cdr z; return x>>
       else return nil
   end;

symbolic procedure assoc_coarity(coarity,u);
   begin
     a: if null u then return nil;
        if cadar cdadar u eq coarity then return caar cdadar u;
        u := cdr u;
        go to a
   end;

symbolic procedure coercable(u,v);
   % True if type u is coercable to type v (without transformation).
   u=v or coercablelis(u,get(v,'typetree));

symbolic procedure coercablelis(u,v);
   v and (coercable(u,car v) or coercablelis(u,cdr v));

endmodule;

end;
