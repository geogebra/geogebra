module rankstat;

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


fluid '(curmodule!*);

%symbolic procedure module_stat;
%   begin scalar cursym,module_name,module_parametrization,module_body;
%     module_name := scan();
%     scan();
%     if cursym!* eq '![ then <<flag('(!]),'delim);
%                               module_parametrization := xread t;
%                               remflag('(!]),'delim); scan()>>;
%     if null(cursym!* eq '!*lcbkt!*) then symerr('module,nil);
%     scan();
%     loop: module_body := aconc(module_body, xread1  'group);
%     cursym := cursym!*;
%     scan();
%     if cursym eq '!*rcbkt!*
%       then return 'theory . module_name . module_parametrization .
%                             module_body
%      else go to loop
%   end;

%put('module,'stat,'module_stat);

symbolic procedure type_stat;
   <<flag(x,'typeid); 'types . x>>
     where x =  remcomma xread nil;

put('types,'stat,'type_stat);

%%%% There should be at least a function to check if a newly added type
%%%% relation produces a cycle.

symbolic procedure subtype_rels_stat;
   'subtyperels . remcomma xread 'lambda;

put('subtypes,'stat,'subtype_rels_stat);


symbolic procedure n_formsubtyperels(u,vars);
   {'noval,'progn . aconc(for each j in cdr u
                              conc n_formsubtyperels1(j,vars),
                          ''(noval nil))};

symbolic procedure n_formsubtyperels1(u,vars);
   if null eqcar(u,'lessp) then nil
    else append(n_formsubtyperels2(if atom caddr u then {caddr u}
                                    else caddr u,cadr u,vars),
                n_formsubtyperels1(cadr u,vars));

symbolic procedure n_formsubtyperels2(u,v,vars);
   for each j in u conc
    begin scalar x;
      x := if atom v then {v}
            else if car v eq 'lessp
                    then if atom caddr v then {caddr v}
                          else flat_typel caddr v
                  else flat_typel v;
      return {'put,mkquote j,''typetree,
                   {'union,{'get,mkquote j,''typetree},
                    mkquote x}} .
             for each k in x collect
              {'put,mkquote k,''uptree,
                   {'union,{'get,mkquote k,''uptree},
                    mkquote {j}}}
    end;

symbolic procedure flat_typel u;
   car u . if null atom cadr u then flat_typel cadr u else cdr u;


put('subtyperels,'n_formfn,'n_formsubtyperels);
flag('(subtyperels),'always_nform);

newtok '((!- !>) mapped_to); % For now. Should only be active inside the stat.

symbolic procedure ranks_stat;
   begin scalar props,oper,arity,coarity,ranks1;
     %scan();
     loop:
     oper := xread 'for;
     if atom oper then oper := {'!*nullary!*,oper};
     flag('(mapped_to),'delim);
     arity := xread nil;
     if atom arity then arity := {arity}
      else arity := cdr arity;
     remflag('(mapped_to),'delim);
     coarity := xread 'group;
     if atom coarity then props := nil
      else if car coarity eq 'when
                then <<props := 'when . cddr coarity;
                       coarity := cadr coarity>>
              else if cadr coarity eq 'symmetric
                      then <<coarity := car coarity;
                             props := 'symmetric>>
              else <<prin2 "unimplemented property: ";
                     prin2t cadr coarity;
                     coarity := car coarity>>;
     ranks1 := aconc(ranks1,list(oper,arity,coarity,props));
     if cursym!* eq '!*comma!* then go to loop;
     return 'ranks . ranks1
   end;

put('ranks,'stat,'ranks_stat);

symbolic procedure n_formranks(u,vars);
  %%%%% Function stubs are generated to provide default constructors.
  %%%%% Should those functions remove the type tag of the args?
  %%%%% The function stubs are overwritten later in case of non-constructors
  %%%%% or by handcrafted constructors.
   begin scalar r,e_ptr,rnk,arity,coarity,props,op,opvars,z,n;
     r := {'noval,e_ptr := {'progn}};
     a: u := cdr u;
        if null u then <<rplacd(e_ptr,{''(noval nil)}); return r>>;
        rnk := car u;
        op := caar rnk;
        opvars := cdar rnk;
        arity := cadr rnk;
        coarity := caddr rnk;
        rnk := cdddr rnk;
        props := car rnk;
        if cdr rnk then rnk := cadr rnk else rnk := nil;  % Used by INSTALL.
        if op eq '!*nullary!* then
          <<flag(arity,'defining); % Disable forming a type_reduce on arity.
            if eqcar(props,'when) then
               props := {form1(cadr props,
                                   append(pair(opvars,arity),vars),'symbolic)}
             else rederr "unconditional constraint not supported";
            remflag(arity,'defining);
            props := {'(x1),subla(pair(opvars,'((value x1))),props)};
            z := mk_nullaryfns(arity,coarity,props);
            e_ptr := cdr rplacd(e_ptr,{car z});
            e_ptr := cdr rplacd(e_ptr,cdr z);
            go to a>>;

        if props eq 'symmetric
           then <<put(op,'symmetricfn,arity); props := nil>>;
        if eqcar(props,'when) then
           props := {{'value,value n_form1(cadr props,
                                   append(pair(opvars,arity),vars))}}
         else props := {t};
        n := 0;
        z := for each j in opvars
               collect j . intern compress append(explode 'x,
                                                  explode(n := n + 1));
        props := subla(z,{opvars,props});
        for each rankfn in mk_rankfns(op,coarity,arity,props,rnk) do
          e_ptr := cdr rplacd(e_ptr,{rankfn});
        go to a
   end;

put('ranks,'n_formfn,'n_formranks);

flag('(ranks),'always_nform);

symbolic procedure mk_rankfns(op,coarity,arity,props,altop);
   % Symmetry is currently restricted to binary operators.
   % Altop is used by INSTALL.
   begin scalar x,disambop,disambop2,rankfns; integer n; n := 0;
     disambop := mkrankedname(op,arity,if caadr props eq t then nil
                                        else coarity);
     x := for each j in arity
              collect intern compress append(explode j,
                                             explode(n := n + 1));
     rankfns := {'de,disambop,x,
%                'list . (if op then mkquote coarity . {
%                               'list . mkquote op . x}
%                          else mkquote coarity . {'value . x})}
                 {'mkobject,
                   if altop then altop . for each j in x collect {'value,j}
                   % This is to allow for compilation of
                   % ranks u := v : {kernel,poly} -> poly
                   else if op eq 'setq
                     then 'set . for each j in x collect {'value,j}
                    else if op then op . for each j in x collect {'value,j}
                    else 'value . x,
                  mkquote coarity}}
                   . rankfns;
     rankfns := {'addrank0,mkquote op,mkquote arity,
                  mkquote({car props,
                           append(cadr props,
                                 {mkquote {disambop,coarity}})})} . rankfns;
     if null symmetricp(op . arity) then return rankfns;
     if length arity neq 2
        then rederr "only binary symmetric functions are supported";
     if (car arity eq cadr arity) and (caadr props eq t)
        then return rankfns;
     disambop2 := mkrankedname(op,reverse arity,
                               if null(caadr props eq t) then
                                  if car arity eq cadr arity
                      then intern compress append(explode '!!,explode coarity)
                                                  else coarity
                                else nil);
     rankfns := {'de,disambop2,reverse x,disambop . x} . rankfns;
     rankfns := {'addrank0,mkquote op,mkquote reverse arity,
                 mkquote({car props,append(if car arity eq cadr arity
                                              then cadr props
                    else subla(pair(car props,reverse car props),cadr props),
                        {mkquote {disambop2,coarity}})})} . rankfns;
     return rankfns
   end;

symbolic procedure symmetricp u;  % temporary hack.
   (x and ((xtype1(cadr u,car x) and xtype1(caddr u,cadr x))
           or (xtype1(caddr u,car x) and xtype1(cadr u,cadr x))))
   where x = get(car u,'symmetricfn);

symbolic procedure mk_nullaryfns(arity,coarity,props);
   begin scalar x,disambop,n;  n := 0;
     disambop := mkrankedname(nil,arity,coarity);
     x := for each j in arity
              collect intern compress append(explode j,
                                             explode(n := n + 1));
     return {{'addnullary,mkquote car arity,  %mkquote coarity,
              mkquote({car props,
                       append(cadr props,
                             {mkquote {disambop,coarity}})})},
             {'de,disambop,x,
                 'list . mkquote coarity . {'value . x}}}
  end;

% Support for "install" form of definition.

put('install,'stat,'installstat);

symbolic procedure installstat;
   begin scalar mode,oprname,x,y;
        mode := 'generic;  % Default target mode.
        oprname := scan();
        if null idp oprname
          then <<typerr(oprname,"install name"), go to c>>;
        scan();
        x := errorset!*(list('read_param_list,nil),nil);
        if errorp x then go to c;
        x := car x;
        if cursym!* eq '!*colon!* then mode := read_type();
        if null(cursym!* eq 'mapped_to) then go to c;
        y := scan();
        if not(scan() eq '!*semicol!*) then go to c;
%       return list('install,oprname,x,mode,y);
        return {'ranks,{oprname . for each j in x collect car j,
                        for each j in x collect cdr j,mode,nil,y}};
    c:  errorset!*('(symerr (quote install) t),nil)
   end;

endmodule;

end;

% Not needed now.

symbolic procedure n_forminstall(u,vars);
   begin scalar body,mode,name,oldname,truename,typelist,varlis;
        u := cdr u;
        name := car u;
        varlis := cadr u;
        u := cddr u;
        mode := car u;
        oldname := cadr u;
        typelist := for each j in varlis collect cdr j;
        varlis := for each j in varlis collect car j;
        body := oldname . for each j in varlis collect list('value,j);
%       body := {'type_reduce,body,mkquote mode};
        body := {'mkobject,body,mkquote mode};
        truename := name;
        name := mkrankedname(name,typelist,nil);
        body := list('de,name,varlis,body);
        body := list('progn,
                     list('addrank,mkquote truename,mkquote typelist,
                           mkquote mode),
                     body,{'mkobject,mkquote truename,mkquote 'variable});
        return body
   end;

put('install,'n_formfn,'n_forminstall);

% ---------------------

symbolic procedure equations_stat;
   begin scalar x,equations,typed_vars;
     x := scan();
     x := scan();
     s: if flagp(typeid := cursym!*,'typeid)
           then <<typed_vars := nconc(typed_vars,
                              for each var in remcomma xread 'lambda
                                  collect var . (typeid . var));
               scan(); go to s>>;
     ne: x := xread1 'group;
     if null atom caddr x and caaddr x eq 'when
        then x := {{car x,cadr x,cadr caddr x,'when . cddr caddr x}}
      else x := {x,nil};
     equations := aconc(equations,x);
     if cursym!* eq '!*comma!* then <<scan(); go to ne>>;
     return 'equations . typed_vars . equations
  end;

put('equations,'stat,'equations_stat);


symbolic procedure form_module(u,vars,mode);
   begin scalar theo,parametrization,sequations,x;
     u := cdr u;
     theo := car u; curtheo!* := theo;
     u := cdr u;
     parametrization := car u;
     u := cdr u;
     for each j in u do
       if car j eq 'is
          then ((if null x
                    then rederr list("theory",caadr j,"not defined")
                  else u := theory_merge(delete(j,u),
                                         textual_sub(cdadr j,x)))
                where x = get(caadr j,'uninterpreted_theory));
     terpri();
     prettyprint u; terpri();
     put(theo,'uninterpreted_theory,u);
     if caar u eq 'types then u := cdr u;
     if caar u eq 'subtype_rels
        then put_sub_type_rels(cdar u,theo);
     while u and (caar u neq 'operations)
         do u := cdr u;
     put_ranks cdar u;
     if null(u := cdr u) then return ;
     if eqcar(car u,'equations)
        then sequations := for each equation in cddar u collect
          if caar equation eq 'replaceby then
          {'replaceby,tag_equation(type_qual(cadar u,cadar equation)),
                      tag_equation(type_qual(cadar u,caddar equation)),
                      if null car cdddar equation then nil
                       else type_qual(cadar u,car cdddar equation)}
          else {'equal,tag_equation(type_qual(cadar u,cadar equation)),
                       tag_equation(type_qual(cadar u,caddar equation))};
     put(theo,'equations,sequations);
     terpri(); prettyprint sequations
   end;

put('theory,'formfn,'form_theory);

symbolic procedure type_qual(u,v); subla(u,v);


symbolic procedure tag_equation(u);
   if null u then nil
    else if null atom u and flagp(car u,'typeid) then u
    else if null atom car u and flagp(caar u,'typeid) then u
    else ((coarity_op(car u,
                      for each j in x
                        collect if atom car j
                                   then car j else caar j) . x)
           where x = for each operand in cdr u
                         collect tag_equation(operand));


symbolic procedure type_eq(u,v);
   if null u and null v then t
    else if car u eq caar v then type_eq(cdr u,cdr v)
    else nil;

symbolic procedure coarity_op(op,arity);
   ((if null x then rederr list(op,"has no definitions")
     else if y then cadr y . op
     else cadr find_closest_arity(x,arity). op)
    where y = assoc_arity(arity,x))
    where x = get(op,'ranks);

symbolic procedure assoc_arity(arity,r);
   if null r then nil
    else if type_eq(arity,caar r) then car r
    else assoc_arity(arity,cdr r);

symbolic procedure find_closest_arity(x,arity);
   begin scalar hs1,hs2,y,coar;
     if length arity = 1
      then <<hs1 := find_upper_types(car arity,
                                     get(curtheo!*,'type_rels));
             hs1 := car hs1;
             while hs1 and null (coar := assoc_arity({car hs1},x))
                do hs1 := cdr hs1;
             return coar>>
      else if length arity = 2
      then <<hs1 := find_upper_types(car arity,
                                     get(curtheo!*,'type_rels));
             hs2 := find_upper_types(cadr arity,
                                     get(curtheo!*,'type_rels));
             hs1 := car arity . car hs1; hs2 := cadr arity . car hs2;
             while hs1 and null coar do
               <<y := hs2;
                 while y and null(coar := assoc_arity({car hs1,car y},x))
                    do y := cdr y;
                 hs1 := cdr hs1>>;
             if null coar then write "couldn't find subtype ";
             return coar>>
     else write "currently only up to binary functions are considered";
  end;

symbolic procedure textual_sub(p,theo);
   subla(for each j in p collect cadr j . caddr j,theo);

symbolic procedure theory_merge(theo1,theo2);
   begin scalar x,y,theo;
     x := assoc('equations,theo1); y := assoc('equations,theo2);
     if x and y
        then theo := {car x . merge_equations(x,y)}
      else if x then theo := {x}
      else if y then theo := {y};
     for each j in '(operations using is subtype_rels types)
       do <<x := assoc(j,theo1); y := assoc(j,theo2);
            if x and y then theo := (car x . union(cdr x,cdr y)) . theo
             else if x then theo := x . theo
             else if y then theo := y . theo>>;
     return theo
   end;

symbolic procedure merge_equations(equat1,equat2);
   begin scalar typeid1,typeid2,eqlis1,eqlis2,x,newv; integer n;
     typeid1 := cadr equat1;
     typeid2 := cadr equat2;
     eqlis1 := cddr equat1;
     eqlis2 := cddr equat2;
     for each typevar in typeid1 do
       if x := atsoc(car typevar,typeid2)
          then if cdr typevar = cdr x then nil
            else <<newv := intern compress append(explode car x,
                                                  explode( n := n + 1));
                   typeid2 := subla({car x . newv},typeid2);
                   eqlis2  := subla({car x . newv},eqlis2)>>;
    return union(typeid1,typeid2) . append(eqlis1,eqlis2)
  end;

