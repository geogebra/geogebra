module package4;   % Package support for REDUCE 4.

% Author:  Eberhard Schruefer.

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


remd 'package; % Don't interfere with existing.

fluid '(curr_pckg!*);

% Packages are stored on the property list of their names
% with their package type as indicator. The first element
% of the property is the base of the package, the second is
% an a-list associating types with their internal types
% and the last element is the package body.

symbolic procedure read_package;
   % Reader for a package (assertions) unit.
   begin scalar pckgtyp,pckg,body,tmp,x;
     pckgtyp := cursym!*;
     body := pckg := read_package_name();
     tmp := cdr pckg;
     if (x := scan()) eq 'extends
        then <<flag('(by),'delim);
               x := xread t;
               remflag('(by),'delim); scan();
               tmp := cdr rplacd(tmp,{{'extends,x}})>>;
     a: if x eq 'package
           then lprie {"Nested package definition not allowed"};
        x := xread1 t;
        if null atom x and (car x memq '(endpackage endassertions))
           then return pckgtyp . body;
        tmp := cdr rplacd(tmp,{x});
        x := scan();
        go to a
   end;

symbolic procedure read_package_name;
   % reader for package header. Takes care of package typed
   % arguments.
   begin scalar n,x,y,z;
     n := scan();
     if null(scan() eq '!*lpar!*) then return {n,nil};
     a: x := scan();
        if not idp x then typerr(x,"parameter");
        y := scan();
        if y eq '!*colon!* then go to assertion;
     b: if y eq '!*comma!* then progn(z := x . z, go to a)
         else if y eq '!*rpar!*
                 then if scan() eq '!*semicol!*
                         then return n . {reversip(x . z)};
     assertion:
      x := x . read_type();
      y := cursym!*;
      go to b
   end;


put('package,'stat,'read_package);
put('assertions,'stat,'read_package);
put('endpackage,'stat,'endstat);
put('endassertions,'stat,'endstat);

symbolic procedure n_form_package(u,vars);
   begin scalar pckg_typ,pckg_name,pckg_parms,enr,mi,x;
     pckg_typ := car u;
     u := cdr u;
     pckg_name := car u; % must do more if parametrized.
   %%-- add check if pckg_name already exists.
     u := cdr u;
     pckg_parms := car u;
   %%-- the execution of the next statement should probably be defered
   %%   until the package parameters are bound. Otherwise we need to
   %%   have all assertions defined prior the definition of the
   %%   parametrized package using it.
   %%   What tags are we going to use?
     if pckg_parms then
        mi := for each arg in pckg_parms collect
                <<x := copy_assertions(form_pckg_expr cdr arg,
                                       append(explode car arg,
                                              explode cdr arg));
                  mk_new_base_env(car arg,x);
                  x>>;
     u := cdr u;
     if null u then rederr{"empty package",pckg_name};
     enr := if null eqcar(car u,'extends)
               then form_extends_pckg(pckg_name,nil,u)
     % The above line causes a base package to be interpreted
     % as extending an empty package. We might want to have this
     % a combine operation.
             else form_extends_pckg(pckg_name,form_pckg_expr cadar u,cdr u);
     % Now we must update the appropriate env.
     if pckg_parms
        then (if pckg_typ eq 'package
                then <<x := pckg_base enr;
                       for each j in pckg_parms do
                           x := delete(car j,x);
                       mk_new_param_env(pckg_name,x . cdr enr,mi);
                       rm_base_env pckg_parms>>
              else if pckg_typ eq 'assertions
                then <<"to be filled in">>)
      else mk_new_base_env(pckg_name,(pckg_name . car enr) . cdr enr);
    %%-- still need assertions
     enr := cdr enr;
     if enr and eqcar(car enr,'types) then enr := cdr enr;
     if null pckg_parms and (pckg_typ eq 'package)
        then return {'noval,'progn . for each j in enr
                                       collect cadr n_form1(j,vars)};
     return mknovalobj()
   end;

symbolic procedure types u; nil;
flag('(types),'non_form);

symbolic procedure mk_new_base_env(u,v);
   <<if pckg_base v then put(u,'package_dag,cdr pckg_base v);
     put(u,'base_pckg,v)>>;

symbolic procedure get_pckg u;
   get(u,'base_pckg);

symbolic procedure mk_new_assertion_env(u,v);
   put(u,'assertion_pckg,v);

symbolic procedure mk_new_param_env(u,v,w);
   put(u,'param_pckg,{v,w});

symbolic procedure rm_base_env u;
   for each m in u do remprop(car m,'base_pckg);

symbolic procedure pckg_base u; car u;

symbolic procedure copy_assertions(u,tag);
   begin scalar x,y,sl;
     for each base in pckg_base u do
       if (x := atsoc('types,get(base,'base_pckg)))
          then y := append(cddr x,y);
     sl := mk_retag_sl(y,u,tag);
     return {nil,retag_package(cdr u,sl)}
   end;

symbolic procedure mk_retag_sl(u,v,tag);
   % u contains the package tags that are to be preserved.
   begin scalar x,y,z;
     if x := atsoc('types,v) then x := cddr x;
     tag := append(tag,'(!:));
     for each el in x do
       if null rassoc(cdr el,u)
          then y := (cdr el . <<put(z := mk_prefix(tag,
                                car el),'package_orig,tag); z>>) . y;
     return y
   end;

symbolic procedure retag_package(u,sl);
   % Copies a package (without its base) and retags types
   % according to the a-list sl.
   if null u then nil
    else if eqcar(car u,'types) or
            eqcar(car u,'subtyperels) or
            eqcar(car u,'ranks)
            then subla(sl,car u) . retag_package(cdr u, sl)
    else if eqcar(car u,'procedure)
            then (caar u . cadar u . subla(sl,caddar u) .
                  car cdddar u . subla(sl,cadr cdddar u) .
                  cddr cdddar u) . retag_package(cdr u, sl);


put('package,'n_formfn,'n_form_package);
put('assertions,'n_formfn,'n_form_package);
put('package,'formfn,'n_form_package);    %just for test under 3.x
put('assertions,'formfn,'n_form_package); %just for test under 3.x


symbolic procedure form_pckg_expr u;
   if atom u then get_pckg u
    else if car u eq 'plus
      then pckg_combine for each arg in cdr u
                          collect form_pckg_expr arg
      else mk_parametrized_pckg u;

symbolic procedure pckg_combine u;
   begin scalar base,body,typs;
 %% All we actually need to return is the new base and an
 %% empty extend. We have to see if this is sufficiently
 %% efficient...
     base := caar u;
     typs := cadar u;
     for each pckg in cdr u do
       <<base := union(car pckg,base);
         typs := union(cadr pckg,typs);
         >>;
     return {base,typs,body}
   end;

symbolic procedure form_extends_pckg(p,u,v);
   % New types and ops must get a package prefix.
   % Type-relations must accordingly be translated.
   % Op-definitions must be adjusted to new environment.
   % Take care of private (hidden) types.
   begin scalar prefix,ta,x;
    prefix := append(explode p,'(!:));
    if ta := atsoc('types,v)
       then for each typl on cdr ta do
              rplaca(typl,car typl .
               <<put(x := mk_prefix(prefix,car typl),'package_orig,p);
                 x>>);
    ta := cdr ta;
%% - check if we can really do this destructively. Would like to
%    get rid of it anyhow.
    if x := atsoc('types,u) then nconc(ta,cdr x);
   % we still need to lookup the type names for explicitly mentioned
   % types from the imported packages base.
    if x := atsoc('subtyperels,v) then
       rplacd(x,subla(ta,cdr x));
    if x := atsoc('ranks,v) then
       rplacd(x,subla(ta,cdr x));
    for each j in v do
      if eqcar(j,'procedure)
         then <<rplaca(cddr j,subla(ta,caddr j));
                rplaca(cdddr j,cadddr j . p);
                rplaca(cddddr j,subla(ta,car cddddr j))>>;
    return (if u then pckg_base u else nil) . v
   end;

symbolic procedure mk_prefix(u,v);
   intern compress append(u,explode v);


%% -- views

symbolic procedure read_view;
   % very primitive reader for views
   begin scalar target,source;
     target := scan();
     if null(scan() eq 'as) then lprie {"keyword 'as' expected"};
     source := scan();
     if null(scan() eq 'with) then lprie {"keyword 'with' expected"};
     return {'view,target,source,xread t}
   end;

symbolic procedure bind_param_package(pckg,views);
   begin scalar x,y,z,sl;
     x := get(pckg,'param_pckg);
%     sl :=
     z := retag_package(car x,sl);
     y := pckg_combine for each v in views collect cadr v;
     return pkg_base car x . y
   end;

%%--

symbolic procedure pckg_geq(u,v);
   (u eq v) or pckg_geql(get(u,'package_dag),v);

symbolic procedure pckg_geql(u,v);
   u and (pckg_geq(car u,v) or pckg_geql(cdr u,v));

symbolic procedure op_in_pckgp u;
   pckg_geq(curr_pckg!*,u);

symbolic procedure type_in_pckgp u;
   pckg_geq(curr_pckg!*,get(u,'package_orig));

symbolic procedure pckg_type u;
  %%% very provisional.....
   begin scalar x,y;
     if null curr_pckg!* then return u;
     x := atsoc('types,get(curr_pckg!*,'base_pckg));
     if null x then return u;
     y := assoc(u,cdr x);
     return if y then cdr y else u
   end;

symbolic procedure pckg_op_chk u;
   if null u then nil
    else if null curr_pckg!* then u
    else if op_in_pckgp u then u
    else nil;


endmodule;

end;
