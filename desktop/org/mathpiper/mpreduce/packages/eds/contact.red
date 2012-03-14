module contact;

% Contact systems on jet bundles and Grassmann bundles

% Author: David Hartley

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



global '(indxl!* !*sqvar!*);

put('contact,'rtypefn,'quoteeds);
put('contact,'edsfn,'contact);
flag('(contact),'nospread);


symbolic procedure contact u;
   % u:{int,cfrm|rlist of prefix,cfrm|rlist of prefix[,props]}
   % -> contact:eds
   % Contact system for jet bundle of order ord
   % over bundle with base coframing bas and fibre coframing jet
   begin scalar ord,bas,jet,props,s,m,sys;
   if length u < 3 or length u > 4 then
      rerror(eds,000,"Wrong number of arguments to contact");
   ord := car u;
   if not fixp ord or ord < 0 then
      typerr(ord,"non-negative integer");
   bas := !*a2cfrm{car(u := cdr u)};
   jet := !*a2cfrm{car(u := cdr u)};
   props := if cdr u then foreach x in getrlist cadr u collect
                     if not idp cadr x then
                  rerror(eds,000,"Badly formed properties in EDS")
               else cadr x .
                   if rlistp caddr x then cdr indexexpandeval{caddr x}
                  else caddr x;
   m := cfrmprod2(bas,jet);
   s := mkeds{{},
              foreach f in cfrm_cob bas collect !*k2pf f,
              m,
              props};
   puteds(s,'jet0,uniqvars cfrm_cob jet);
   puteds(s,'sqvar,!*sqvar!*);
   foreach f in {'solved,'reduced,'quasilinear,'pfaffian,'involutive} do
      flagtrueeds(s,f);
   if allexact cfrm_cob m then
      for i:=1:ord do   % gbsys doesn't produce redundant mixed partials
      << sys := eds_sys s;
         s := edscall gbsys s;
               eds_sys s := append(sys,eds_sys s) >>
   else
      for i:=1:ord do   % have to allow for structure constants
         s := edscall prolongeds s;
   return s;
   end;


symbolic procedure gbsys s;
   % s:eds -> gbsys:eds
   % Refine test for flg argument to gbcoords
   begin scalar prl,dep,ind,jet,jet0,sys,cob,idxs,x,crd,m;
   if not normaledsp s then
      rerror(eds,000,{"System not in normal form"});
   % Get information about s
   ind := indkrns s; idxs := uniqids ind;
   prl := prlkrns s; jet := uniqvars prl;
   cob := edscob s;
   jet0 := geteds(s,'jet0) or jet;
   % Generate new index names if necessary
   if not subsetp(idxs,indxl!*) then % indexrange is an rlistat
      apply1('indexrange,{{'equal,gensym(),makelist idxs}});
   % Generate new coordinates
   jet := gbcoords(jet,idxs,jet0,allexact cob);
   % New contact forms
   sys := foreach pr in pair(prl,jet) collect
             car pr .* (1 ./ 1) .+
                negpf zippf(eds_ind s,
                            for each c in cdr pr collect !*k2q c);
   % Compile coordinate and cobasis lists in correct order
   foreach j in jet do crd := union(j,crd);
   prl := foreach c in crd collect
             if (x := lpow exdfk c) =  {'d,c} then x
             else errdhh{"Bad differential",x,"from",{'d,c},"in gbsys"};
   prl := reversip setdiff(prl,cob);
   dep := setdiff(cob,ind);
   cob := append(dep,append(prl,ind));
   crd := reversip setdiff(crd,edscrd s);
   crd := append(edscrd s,crd);
   % Update coframing
   m := copycfrm eds_cfrm s;
   cfrm_cob m := cob;
   cfrm_crd m := crd;
   % Update eds
   s := copyeds s;
   eds_sys s := sys;
   eds_cfrm s := m;
   puteds(s,'jet0,jet0);
   foreach f in {'solved,'reduced,'quasilinear,'pfaffian} do
      flagtrueeds(s,f);
   flagfalseeds(s,'closed);
   rempropeds(s,'involutive);
   s := purgeeds!* s;
   remkrns s;
   return s;
   end;


symbolic procedure gbcoords(prlvars,indids,jet0,flg);
   % prlvars:list of kernel, indids:list of id, jet0:list of kernel,
   % flg:bool
   % -> gbcoords:matrix of kernel
   % constructs coordinates for fibre of Grassmann bundle
   % index symmetries???
   foreach c in prlvars collect
      begin scalar x; integer n;
      % split c into {base,indices} using jet0
      if jet0 eq prlvars then
         c := {splitoffindices(c,c)}
      else
         c := foreach c0 in jet0 join
                 if c0 := splitoffindices(c0,c) then {c0};
      if length c neq 1 then
         errdhh {"Name conflict in gbcoords:",length c,"matches"}
      else c := car c;
      n := length car c + length cdr c; % actually, cdar c + cdr c + 1
      if (x := get(caar c,'ifdegree)) and (x := assoc(n,x)) and cdr x
         then errdhh {"Degree conflict in gbcoords:",
                      append(car c,nil.cdr c)}
         else mkform!*(append(car c,nil.cdr c),0);
      return foreach i in indids collect
         begin scalar x;
         x := if (jet0 neq prlvars) and flg then foreach j in
                 sort(i . flatindxl cdr c,'indtordp)
                 collect lowerind j
              else append(cdr c,{lowerind i});
         x := car fkern append(car c,x);
         if reval x neq x then typerr(x,"free coordinate");
         return x;
         end;
      end;


symbolic procedure splitoffindices(u,v);
   % u,v:kernel -> splitoffindices:nil or kernel.list of id
   % v is an indexed variable, u is a variable
   % if v is obtained from u by adding indices,
   % return base.indices otherwise nil
   % Rules:             a,a     -> {a}.{}
   %                     a,{a,i..}       -> {a}.{i..}
   %               {a,i..},{a,i..} -> {a,i..}.{}
   %           {a,i..},{a,i..,j..}     -> {a,i..}.{j..}
   %                     otherwise       -> nil
   if atom u then
      if u = v then {u}.{}
      else if pairp v and car v = u then {u}.cdr v
      else nil
   else if pairp v and car v = car u then
      if null cdr u then u.cdr v
      else (if x then u.cdr x)
            where x = splitoffindices(cdr u,cdr v);


symbolic procedure indtordp(u,v);
   % a total ordering for indices
   begin scalar x;
        x := indxl!*;
    a:  if null x then return orderp(u,v)
         else if u eq car x then return t
         else if v eq car x then return;
        x := cdr x;
        go to a
   end;


symbolic procedure uniqids u;
   % u:list of kernel -> uniqids:list of id
   % returns id's suitable for use as indices
   % if elements of u are indexed pforms with the same base,
   % we can use the indices, otherwise artificial names are
   % constructed (if excalc allowed non-atomic index names, we
   % wouldn't need to contrive id's).
   begin scalar x;
   x := foreach i in u collect indexid i;
   if memq(nil,x)
      or not allequal sublis(pair(x,nlist(nil,length x)),u)
      then x := foreach i in u collect pformid i;
   if repeats x then errdhh "Name conflict in uniqids";
   return x;
   end;


symbolic procedure indexid u;
   % u:kernel -> indexid:id or nil
   % returns the index on a single-index kernel, else nil
   (if x and length x = 1 then car x)
    where x = flatindxl indexlist u;


symbolic procedure indexlist u;
   % u:kernel -> indexlist:list of kernel
   % returns list of ALL indices in u, free or not
   % based on allindk
   if atom u then nil
   else if get(car u,'rtype) = 'indexed!-form then
      for each j in cdr u collect revalind j
   else if get(car u,'indexfun) then
      indexlist apply1(get(car u,'indexfun),cdr u)
   else if car u eq 'partdf then
      if null cddr u then
         for each j in indexlist cdr u collect revalind lowerind j
      else append(indexlist cadr u,
                  for each j in indexlist cddr u
                    collect revalind lowerind j)
   else append(indexlist car u,indexlist cdr u);


symbolic procedure pformid u;
   % u:kernel -> pformid:id
   % constructs an id for the pform variable in u
   (if atom x then x
    else intern compress foreach a in flatindxl x join explode a)
    where x = pformvar u;


symbolic procedure uniqvars u;
   % u:list of kernel -> uniqvars:list of kernel
   % extracts pform variables from u, checking for repeats
   if repeats(u := foreach k in u collect pformvar k)
      then errdhh "Name conflict in uniqvars"
      else u;


symbolic procedure pformvar u;
   % u:kernel -> pformvar:kernel
   % extracts pform variable from u
   if atom u or get(car u,'rtype) = 'indexed!-form then u
   else if car u memq '(d hodge partdf)
           and null cddr u then pformvar cadr u
   else errdhh {"No unique variable in ",u};

endmodule;

end;
