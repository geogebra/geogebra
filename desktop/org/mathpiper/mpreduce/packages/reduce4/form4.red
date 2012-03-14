module form4;   % Type analysis for REDUCE 4.

% Authors:  Anthony C. Hearn, Eberhard Schruefer.

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


fluid '(!*specification !*specification_reduce !*generate_retracts
        !*instantiate);

switch instantiate,specification;

% If the switch specification is on, all expression are checked for type
% consistency at form-time. For this to work it is necessary that all
% ranks and type relations are set up prior to function definitions.
% In the code below any n_form function is required to return a list
% whose first element is the ceiling type of the formed expression and
% the second element is the formed expression. The toplevel function
% n_form returns only the formed expression.

%!*specification := t;

symbolic procedure n_form u;
   % Car of n_form1 is the ceiling type.  Cadr is a typed expression.
   cadr n_form1(u,!*vars!*);

symbolic procedure n_form1(u,vars);
   begin scalar x,z,ctype,arity_pairs,args,fnc;
      return if atom u
     then if numberp u
            then if fixp u
                    then if u=0
                            then <<z := pckg_type 'zero;
                                   {z, mkquote {z,0}}>>
                          else <<z := pckg_type 'int;
                                 {z,mkquote type_reduce(u,z)}>>
             else {'float,mkquote list('float,u)}
           else if stringp u then {'string,mkquote {'string,u}}
           else if arrayp u then {'array,mkquote {'array,u}}
           else if x := atsoc(u,vars) then {cdr x, u}
%          else if (x := get(u,'type)) then mkquote {x,mkquote u}
%               % type_reduce
           else if x := get(u,'avalue)
                then {type x,{'ideval,mkquote u}}
           else {x := pckg_type 'variable,{'ideval,mkquote u}}
    else if not idp car u then typerr(car u,"operator")
    else if (null cdr u and car u neq 'list) or flagp(car u,'non_form)
     then {'non_form,u}
    else if flagp(car u,'non_form) then {'non_form,u}
    else if (x := get(car u,'n_formfn)) then apply2(x,u,vars)
    % See if a direct result can be formed.
    else if x := get(car u,'xform)
     then x . for each j in cdr u collect n_form1(j,vars)
    else <<x := for each j in cdr u collect n_form1(j,vars);
           arity_pairs := for each j in x collect {type j};
           args := for each j in x collect value j;
    % If there is a type constraint, it cannot be resolved in general.
    % The highest type must then be returned as the coarity.
           fnc := type_function2(car u,arity_pairs,args);
           ctype := if null fnc then if !*specification
                       then rederr{"no meaning for",car u,x}
                     else 'generic
                 else cadr fnc;
           if !*generate_retracts then
              args := for each arg in args collect
                        <<x := arity_pairs;
                          arity_pairs := cdr arity_pairs;
 %                         if cdar x and (xxxxx := atsoc(arg,vars))
 %                            then rplacd(xxxxx,cadar x);
                          if cdar x then mkretract(caar x,cadar x,arg)
                           else arg>>;
           if !*instantiate and fnc
%            then {ctype,mk_type_reduce(car fnc . args,ctype)}
             then {ctype,{'type_reduce1,car fnc . args}}
 %           else if !*specification_reduce
 %             then {ctype,{'type_reduce1,
 %                          {'rapply,mkquote car u,'list . args},ctype}}
            else
           {ctype,{'rapply,mkquote car u,'list . args}}>>
   end;



symbolic procedure mk_type_reduce(u,v);
   % We must not call type_reduce when defining a sort constraint for
   % type v, as we would loop otherwise.
   if flagp(v,'defining) then mkquote {v,u}
    else {'type_reduce,u,mkquote v};

symbolic procedure type_function2(fn,typelist,args);
   % Returns disambiguated function symbol for fn.
   % If retracts are necessary, typelist is destructively changed.
   % Type constraints are ignored as we are here only interested
   % in ceiling types and more information can only be derived
   % by formal proofs.
   begin scalar x;
      return if (x := get(fn,'ranks))
                and (x := assoc(length typelist,x))
                and (x := type_assocf(typelist,cdr x,args))
               then x
              else nil
   end;

symbolic procedure type_assocf(typelist,type_assoc_list,args);
   % Determine if there's a match for typelist in type_assoc_list.
   begin scalar x;
     if x :=
        type_assoc1f(car typelist,cdr typelist,type_assoc_list,args)
        then return x
      else if x := atsoc('generic,type_assoc_list)
        then return cdr x
      else return nil
   end;


symbolic procedure type_assoc1f(type,typelist,type_assoc_list,args);
   begin scalar x,y,z;
      if (type_in_pckgp type and
          (x := type_assoc0f(type,type_assoc_list)))
%         or (x := atsoc('generic,type_assoc_list))
        then if null typelist
        then return ceiling_of_constraints cdaddr cdr x
         % We assume termination with the actual name of function here.
       else if y := type_assoc1f(car typelist,cdr typelist,cdr x,args)
               then return y;
      if z := get(car type,'uptree)
        then <<while z and
                 not (x := type_assoc1f(rplaca(type,car z),
                                        typelist,type_assoc_list,args))
                 do z := cdr z; return x>>
       else return nil
   end;

symbolic procedure ceiling_of_constraints u;
   if null u then nil
    else if caar u eq t then cadadr car u
    else ceiling_of_constraints cdr u;

symbolic procedure type_assoc0f(type,type_assoc_list);
   if null type_assoc_list then nil
    else if car type eq caar type_assoc_list  then car type_assoc_list
    else if xtype1(caar type_assoc_list,car type) and !*specification
            then <<lprim {type," -> ",caar type_assoc_list};
                   car type_assoc_list; rplacd(type,{caar type_assoc_list});
                   car type_assoc_list>>
% The above finds a resolution but it might be lower than intended.
% Is the solution to find the closest node or need we to generate all????
    else type_assoc0f(type,cdr type_assoc_list);


flag('(load),'non_form);

put('type,'xform,'type_1);

symbolic procedure type_1 u; list('variable,type u);

symbolic procedure n_formbool(u,vars);
 %%% Should we check if type of u is liftable to bool ???
 %%% Would like to get rid of n_boolvalue*.
   begin scalar x;
     if atom u then if u eq 't then return {'bool,u}
      else if x := atsoc(u,vars)
              then if (cdr x eq 'bool) or (cdr x eq 'generic)
                      then return {'bool,list('n_boolvalue!*,u)}
                    else rederr {"a boolean was expected, but got",cdr x};
     x := n_form1(u,vars);
     if null((type x eq 'bool) or (type x eq 'generic))
        then rederr {"a boolean was expected, but got",type x};
     return {'bool,list('n_boolvalue!*,value x)}
   end;

symbolic procedure n_boolvalue!* u; (v and null(v = 0)) where v=value u;

%  --- COND ---

symbolic procedure n_formcond(u,vars);
   {type x,'cond . value x} where x = n_formcond1(cdr u,vars);

symbolic procedure n_formcond1(u,vars);
   % We need to consider generic a bit more carefully here.
   begin scalar v,eptr,x,restype;
     v := eptr := {nil};
     a: if null u then return {restype,cdr v};
        x := n_form1(cadar u,vars);
        if null restype
           then restype := type x
         else if xtype1(type x,restype) then nil
         else if xtype1(restype,type x) then restype := type x
         else rederr {"types in conditional",type x,"and",restype,
                      "are unrelated"};
        eptr := cdr rplacd(eptr,{{value n_formbool(caar u,vars),
                                  value x}});
        u := cdr u;
        go to a
    end;

put('cond,'n_formfn,'n_formcond);

%  --- LIST ----

symbolic procedure n_formlist(u,vars); % parametrization ??? very crude version
   begin scalar x,y,eltype;
     if null cdr u then return {'empty_list,''(empty_list nil)};
     x := n_form1(cadr u,vars);
     eltype := type x;
     y := value x;
     y := y . for each j in cddr u
                  collect <<x := n_form1(j,vars);
%                           if xtype1(type x,eltype) then nil
%                            else if xtype1(eltype,type x)
%                                    then eltype := type x
%                            else rederr {"types in list",type x,
%                                         "and",eltype,"are unrelated"};
                            value x>>;
     return {'non_empty_list,{'mklistt,'list . y}}
   end;

symbolic procedure mklistt u;   %%% this is not consistent with others
   type_reduce(u,'list);

put('list,'n_formfn,'n_formlist);


%  --- PROGN ---

symbolic procedure n_formprogn(u,vars);
   begin scalar restype,x;
     x := for each j in cdr u
             collect <<restype := n_form1(j,vars);
                       value restype>>;
     return {type restype,'progn . x}
   end;

put('progn,'n_formfn,'n_formprogn);

%  --- SETQ ---

symbolic procedure n_formsetq(u,vars);
   begin scalar x,y,z;
   % Note that target type (car z) is target type of assignment.
   z := n_form1(caddr u,vars);
   if idp cadr u and (x := atsoc(cadr u,vars)) then
     <<if not(cdr x eq 'generic)
          then if xtype1(type z,cdr x) then nil
                else if xtype1(cdr x,type z)
                        then lprim {"assignment is only valid if type of rhs",
                                    type z,"is retractable to",cdr x}
                else rederr {"type of lhs",cdr x,
                             "in assignment is unrelated to ceiling type",
                              type z,"of rhs"};
       return {car z,list('setq,cadr u,cadr z)}>> else
    if not atom cadr u and (x := getobject caadr u)
       and (y := get(type x,'putfn)) then
     return {car z,{y,mkquote x,'list . for each j in cdadr u collect
                        cadr n_form1(j,vars),cadr z}} else
     return {car z,{'rapply,mkquote 'setq,{'list,
                      {'mkobject,mkquote cadr u,mkquote 'variable},
                      cadr z}}}
   end;

put('setq,'n_formfn,'n_formsetq);

endmodule;

end;
