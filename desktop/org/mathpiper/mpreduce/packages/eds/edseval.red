module edseval;

% Definition and manipulation of eds structure for exterior systems

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


Comment. A simple exterior differential system is stored in a list:

        eds     ::= {'!!eds!!,sys,ind,cfrm,props}
        sys     ::= list of pf
        ind     ::= list of pf
        cfrm    ::= cfrm
        props   ::= alist of id.atom|id.list of prefix

More generally, exterior differential systems are stored as algebraic
lists, with a single-element list represented by a simple eds.

              xeds   ::= eds | 'list . list of xeds

endcomment;


fluid '(cfrmcob!* cfrmcrd!* cfrmdrv!* cfrmrsx!* xvars!* kord!*);
global '(!*sqvar!*);


% Type definition


put('eds,'tag,'!!eds!!);
put('!!eds!!,'rtypefn,'quoteeds);

symbolic procedure quoteeds u; 'eds;


if not(get('list,'rtypefn) memq {'quotelist,'edsorlist}) then
   lprim {"Changing list rtypefn from",get('list,'rtypefn)};

put('list,'rtypefn,'edsorlist);

symbolic procedure edsorlist u;
   % u:list of prefix -> edsorlist:'eds|'list
   % Gives rtype eds to an rlist of eds.
   if u and getrtype car u = 'eds then 'eds
   else 'list;


% Evaluation interface


put('eds,'evfn,'edseval);

symbolic procedure edseval(u,v);
   % u:prefix, v:bool -> edseval:prefix
   % v is t for reval, nil for aeval. Here it is ignored (and abused as
   % a local variable!).  u is either an id with an avalue whose car has
   % rtype eds or a list with rtype eds. This routine differs from most
   % evfn's in that the argument list is evaluated prior to calling an
   % edsfn. This is because the predicted result type of eds might be
   % wrong (it might give an xeds). If this happens, reval is called
   % again.
   if atom u then
      edseval(if flagp(u,'share) then eval u
              else cadr get(u,'avalue),v)
   else if edsp u then resimpeds!* u
   else if xedsp u then
      mkxeds makelist foreach s in mkxeds0 u collect resimpeds!* s
   else if v := get(car u,'edsfn) then
      mkxeds makelist foreach f in edsexpand revlis cdr u collect
         if flagp(car u,'nospread) then edsprotect{v,f}
               else edsprotect(v . f)
   else rerror(eds,000,{"Illegal operation on EDS"});


symbolic procedure resimpeds!* s;
   % s:eds -> resimpeds!*:eds
   % Resimplify s iff sqvar is nil
   if v and car v where v = geteds(s,'sqvar) then s
   else resimpeds s;


symbolic procedure edsexpand u;
   % u:list of prefix -> edsexpand:list of list of prefix
   % Input is an argument list, result is a list of argument lists.
   % All xeds in the argument list are distributed in the result,
   % which contains only simple eds.
   if null u then {u}
   else if not xedsp car u then
      foreach w in edsexpand cdr u collect car u . w
   else foreach s in mkxeds0 car u join
      foreach w in edsexpand cdr u collect s . w;


symbolic procedure edsexpand u;
   % u:list of prefix -> edsexpand:list of list of prefix
   % Input is an argument list, result is a list of argument lists.
   % The first xeds in the argument list is distributed in the result,
   % which contains only simple eds.
   if null u or not xedsp car u then {u}
   else foreach s in mkxeds0 car u collect s . cdr u;


symbolic procedure edsprotect u;
   % u:prefix -> edsprotect:prefix
   % Protected evaluation environment for operations on exterior
   % systems. Like cfrmprotect, but removes base coordinates and order
   % cobasis.
   begin scalar m,ok,od;
         scalar xvars!*;
   % If one of the arguments is eds, take the first one
   foreach v in cdr u do if null m and edsp v then m := v;
   % Save environment and adjust for eds calculation.
   ok := kord!*; od := append(get('d,'kvalue),nil); % copy pairs
   if m then m := setcfrm eds_cfrm!* m;
   u := errorset!*(car u . foreach j in cdr u collect mkquote j,t);
   % Restore environment
   if m then setcfrm m;
   setkorder ok; if od then put('d,'kvalue,od) else remprop('d,'kvalue);
   if errorp u then error1()
   else return car u;
   end;


symbolic procedure eds_cfrm!* s;
   % s:eds -> eds_cfrm!*:cfrm
   % Coframing for s but with base coordinates removed from list.
   begin scalar m;
   m := copycfrm eds_cfrm s;
   cfrm_crd m := setdiff(cfrm_crd m,edsindcrd s);
   return m;
   end;


symbolic procedure edscob s;
   % s:eds -> edscob:cob
   % Cobasis ordering for s: dep > prl > ind.
   cfrm_cob eds_cfrm s;


symbolic procedure edscrd s;
   % s:eds -> edscrd:list of kernel
   cfrm_crd eds_cfrm s;


symbolic procedure edsindcrd s;
   % s:eds -> edsindcrd:list of kernel
   % Tries to determine independent coordinates in s. Can go wrong with
   % anholonomic systems.
   begin scalar i,j;
   i := indkrns s;
   j := foreach k in i join
                 if exact k then {cadr k};
   if length j = length i then return j;
   j := append(j,foreach c in setdiff(edscrd s,j) join
                              if lpow exdfk c memq i then {c});
   if length j = length i then return j;
   %edsdebug("Can't determine independent coordinates - guessing",nil,
   %         nil);
   return
      if length j > length i then
               reverse pnth(reverse j,1 + length j - length i)
      else j;
   end;


put('list,'edsfn,'listeds);
flag('(list),'nospread);

symbolic procedure listeds u;
   % u:list of eds -> listeds:rlist of eds
   makelist u;


% Constructors and tests


symbolic procedure mkeds u;
   % tag u as eds
   '!!eds!! . u;


symbolic procedure mkxeds u;
   % u:xeds -> mkxeds:xeds
   % take possibly nested xeds's and produce a flat list, or an eds
   if length(u := mkxeds0 u) = 1 then car u
   else makelist u;


symbolic procedure mkxeds0 u;
   % u:xeds|eds -> mkxeds0:list of eds
   % take possibly nested xeds's and produce a flat list
   if edsp u then {u}
   else if rlistp u then
      foreach v in cdr u join mkxeds0 v
   else typerr(u,'eds);


symbolic procedure emptyeds;
   % -> emptyeds:eds
   mkeds{{!*k2pf 1},{},emptycfrm(),{}};


symbolic procedure emptyedsp s;
   % s:eds -> emptyedsp:bool
   !*k2pf 1 member eds_sys s;


symbolic procedure edsp u;
   % u:any -> edsp:bool
   eqcar(u,'!!eds!!);


symbolic procedure xedsp u;
   % u:any -> xedsp:bool
   edsp u or
   rlistp u and cdr u and xedsp cadr u;


symbolic procedure purgexeds s;
   % s:xeds -> purgexeds:xeds
   % Remove all empty eds's from s (except perhaps one)
   begin
   s := foreach s0 in mkxeds0 s join
                if not emptyedsp s0 then {s0};
   return
      if null s then emptyeds()
      else if length s = 1 then car s
      else makelist s;
   end;


% Input interface


put('eds,'rtypefn,'quoteeds);
put('eds,'edsfn,'!*a2eds);
flag('(eds),'nospread);

symbolic procedure !*a2eds s;
   % s:eds -> !*a2eds:xeds
   % Argument syntax:
   %    eds(sys,ind[,cfrm][,props])
   begin scalar sys,ind,cfrm,props;
   if length s < 2 or length s > 4 then
      rerror(eds,000,{"Wrong number of arguments to EDS"});
   sys := !*a2sys car s;
   if rlistp cadr s then ind := !*a2sys cadr s
   else if getrtype cadr s then typerr(cadr s,"independence form")
   else if null(ind := xdecomposepf xpartitop cadr s) then
       typerr(cadr s,"independence form (not decomposable)");
   foreach l in cddr s do
      if cfrmp l then cfrm := l
      else if rlistp l and edspropsp cdr l then props := cdr l
      else rerror(eds,000,"Badly formed EDS");
   ind := foreach f in ind collect
      if degreepf f = 1 then f
      else typerr(f,"independence 1-form");
   if null cfrm then cfrm := !*sys2cfrm append(sys,ind);
   props := foreach x in props collect
                     if not idp cadr x then
                  rerror(eds,000,"Badly formed properties in EDS")
               else cadr x .
                   if rlistp caddr x then
                     revlis cdr indexexpandeval{caddr x}
                  else caddr x;
   s := mkeds{sys,ind,cfrm,props};
   return edscall checkeds s;
   end;


symbolic procedure edspropsp u;
   % u:any -> edspropsp:bool
   % Tests if u is candidate for property list (ie a list of eqn)
   null u or eqexpr car u and edspropsp cdr u;


% Output interface


put('!!eds!!,'prifn,'edsprint);
put('!!eds!!,'fancy!-reform,'!*eds2a);
put('eds,'texprifn,'texprieds);
%put('eds,'prepfn,'!*eds2a);


symbolic procedure edsprint s;
   % s:eds -> edsprint:bool
   % if already in external format, use inprint
   maprin !*eds2a s;


symbolic procedure !*eds2a s;
   % s:eds -> !*eds2a:prefix
   edscall !*eds2a1 s;


symbolic procedure !*eds2a1 s;
   % s:eds -> !*eds2a1:prefix
   if !*nat then
      "EDS" . {makelist for each f in eds_sys s
                           collect preppf repartit f,
               if eds_ind s then
                  mknwedge foreach f in eds_ind s
                              collect preppf repartit f
               else makelist nil}
   else
      "eds" . {makelist for each f in eds_sys s
                           collect preppf repartit f,
               if eds_ind s then
                  mknwedge foreach f in eds_ind s
                              collect preppf repartit f
               else makelist nil,
               !*cfrm2a eds_cfrm s,
               edsproperties s};


% The next bit is just temporary till TRI is fixed

%% symbolic procedure texprieds(u,v,w);
%%    % Have to hide the EDS from TRI's makeprefix
%%    if edsp u then
%%       texvarpri('texpriedsop . !*eds2a u,v,w)
%%    else
%%       texvarpri(makelist foreach s in cdr u collect
%%        'texpriedsop . !*eds2a s,v,w);


symbolic procedure texprieds(u,v,w);
   % Have to hide the EDS from TRI's makeprefix
   % but not from TRIX's makeprefix.
   if edsp u then
      texvarpri(
         if get('hodge,'texname) then !*eds2a u
         else 'texpriedsop . !*eds2a u,v,w)
   else
      texvarpri(makelist foreach s in getrlist u collect
         if get('hodge,'texname) then !*eds2a s
         else 'texpriedsop . !*eds2a s,v,w);



put('texpriedsop,'simpfn,'simptexpriedsop);

symbolic procedure simptexpriedsop u;
   % don't do anything to u, treat it as a kernel
   % this is all to get around makeprefix in TRI
   !*k2q u;


% Algebraic access to eds parts


put('system,'formfn,'formsystem);

symbolic procedure formsystem(u,v,mode);
   % distinguish between system(string) and system(eds).
   begin scalar x;
   x := formlis(cdr u,v,mode);
   return if mode = 'symbolic then
      'system . x
   else if x and stringp car x then
      'list . mkquote 'system . x
   else %if x and eqcar(car x,'quote) and getrtype eval car x = 'eds
        % then
    'list . mkquote 'systemeds . x;
   end;

put('systemeds,'rtypefn,'quotelist);
put('systemeds,'listfn,'syseval);

symbolic procedure syseval(s,v);
   % s:{xeds}, v:bool -> syseval:prefix sys
   if not xedsp(s := reval car s) then typerr(s,'eds)
   else if edsp s then
      !*sys2a1(eds_sys s,v)
   else makelist foreach x in cdr s collect
      !*sys2a1(eds_sys x,v);


put('independence,'rtypefn,'quotelist);
put('independence,'listfn,'indeval);

symbolic procedure indeval(s,v);
   % s:{xeds}, v:bool -> indeval:prefix ind
   if not xedsp(s := reval car s) then typerr(s,'eds)
   else if edsp s then
      makelist foreach f in eds_ind s collect !*pf2a1(f,v)
   else makelist foreach x in cdr s collect
      makelist foreach f in eds_ind x collect !*pf2a1(f,v);


put('properties,'rtypefn,'quotelist);
put('properties,'listfn,'propertieseval);

symbolic procedure propertieseval(s,v);
   % s:{xeds}, v:bool -> propertieseval:prefix list of list
   % ignore v argument
   if not xedsp(s := reval car s) then typerr(s,'eds)
   else if edsp s then edsproperties s
   else makelist foreach x in cdr s collect edsproperties x;


symbolic procedure edsproperties s;
   % s:eds -> edsproperties:prefix list of list
   makelist foreach p in eds_props s join
      if not flagp(car p,'hidden) then
         {{'equal,car p,if pairp cdr p then makelist cdr p else cdr p}};


put('eds,'lengthfn,'edslength);

symbolic procedure edslength s;
   % s:eds -> edslength:int
   if edsp s then 1
   else length cdr s;


symbolic procedure edspart(s,n);
   % s:eds, n:int -> edspart:prefix
   if n = 0 then 'eds
   else if n = 1 then !*sys2a eds_sys s
   else if n = 2 then !*sys2a eds_ind s
   else if n = 3 then eds_cfrm s
   else if n = 4 then edsproperties s
   else parterr(s,n);

put('!!eds!!,'partop,'edspart);


symbolic procedure edssetpart(s,l,r);
   % s:eds, l:list of int, r:prefix -> edssetpart:error
   rerror(eds,000,"Part setting disabled on EDS operator");

put('!!eds!!,'setpartop,'edssetpart);


symbolic procedure mapeds(fn,s);
   % Map function for eds
   begin
   s := copyeds s;
   eds_sys s := foreach f in eds_sys s collect
                                xpartitop apply1(fn,!*pf2a f);
   eds_ind s := foreach f in eds_ind s collect
                                xpartitop apply1(fn,!*pf2a f);
   return edscall checkeds s;
   end;

put('!!eds!!,'mapfn,'mapeds);


% Consistency check, resimplification and cleanup


symbolic procedure checkeds s;
   % s:eds -> checkeds:eds
   % Check EDS actually resides on coframing, and bring to normal form.
   begin scalar m,n;
   s := purgeeds s; % remove all hidden properties
   % Pick up coframing for sys/ind
   n := !*sys2cfrm append(eds_ind s,eds_sys s);
   % Check this against given coframing, if any.
   m := copycfrm eds_cfrm s;
   if not subsetp(cfrm_crd n,cfrm_crd m) then
      rerror(eds,000,
             "EDS not expressed in terms of coframing coordinates");
   if not subsetp(cfrm_cob n,cfrm_cob m) then
      rerror(eds,000,"EDS not expressed in terms of coframing cobasis");
   % Add any restrictions or structure equations picked up
   cfrm_rsx m := union(cfrm_rsx n,cfrm_rsx m);
   cfrm_drv m := union(cfrm_drv n,cfrm_drv m);
   eds_cfrm s := purgecfrm m;
   puteds(s,'sqvar,!*sqvar!*);
   return normaleds s;
   end;


symbolic procedure resimpeds s;
   % s:eds -> resimpeds:eds
   begin scalar r,ok;
   r := copyeds s;
   ok := cfrmswapkord(edscob r,{});
   eds_sys r := foreach f in eds_sys r collect xrepartit!* f;
   eds_ind r := foreach f in eds_ind r collect xrepartit!* f;
   eds_cfrm r := resimpcfrm eds_cfrm r;
   % next line no good, because sqvar is changed in closure etc
   %eds_props r := foreach p in eds_props r collect
   %                              car p . reval cdr p;
   if revlis geteds(r,'jet0) neq geteds(s,'jet0) then
      rempropeds(r,'jet0);
   setkorder ok;
   if r = s then
   << puteds(s,'sqvar,!*sqvar!*);
      return s >>;
   return edscall checkeds r;
   end;

flag('(sqvar),'hidden); % so it doesn't ever get printed


put('cleanup,'rtypefn,'getrtypecar);
put('cleanup,'edsfn,'cleaneds);

symbolic procedure cleaneds s;
   % s:eds -> cleaneds:eds
   begin scalar r,j;
   s := copyeds s;
   j := geteds(s,'jet0);
   eds_props s := {}; % remove ALL properties except jet0
   if j then puteds(s,'jet0,j);
   r := resimpeds s;
   return  % eq test here essential!
      if r eq s then edscall checkeds s
      else r;
   end;


symbolic procedure purgeeds s;
   % s:eds -> purgeeds:eds
   % Remove all hidden flags and properties
   begin
   s := copyeds s;
   eds_props s := foreach p in eds_props s join
      if not flagp(car p,'hidden) then {p};
   return s;
   end;


symbolic procedure purgeeds!* s;
   % s:eds -> purgeeds!*:eds
   % Remove most hidden flags and properties.
   begin
   s := copyeds s;
   eds_props s := foreach p in eds_props s join
      if car p memq {'solved,'reduced,'sqvar} or
         not flagp(car p,'hidden)
      then {p};
   return s;
   end;


% Operations on eds property list


symbolic procedure puteds(s,k,v);
   % s:eds, k:id, v:any -> puteds:any
   if not edsp s then
      errdhh {"Attempt to do puteds on",s,"which is not an EDS"}
   else if not idp k then
      errdhh {"Attempt to do puteds with",k,"which is not an id"}
   else
      begin scalar p;
      if p := assoc(k,eds_props s) then
         eds_props s := (k.v) . delete(p,eds_props s)
      else
         eds_props s := (k.v) . eds_props s;
      return v;
      end;


symbolic procedure rempropeds(s,k);
   % s:eds, k:id -> rempropeds:any
   if not edsp s or not idp k then nil
   else
      begin scalar p;
      if p := assoc(k,eds_props s) then
         eds_props s := delete(p,eds_props s);
      return if p then cdr p;
      end;


symbolic procedure geteds(s,k);
   % s:any, k:id -> geteds:any
   if not edsp s or not idp k then nil
   else (if p then cdr p) where p = assoc(k,eds_props s);


% Ternary logic flags


symbolic procedure flagtrueeds(s,k);
   % s:eds, k:id -> flagtrueeds:nil
   <<puteds(s,k,1);>>;


symbolic procedure knowntrueeds(s,k);
   % s:eds, k:id -> knowntrueeds:bool
   geteds(s,k) = 1;


symbolic procedure remtrueeds(s,k);
   % s:eds, k:id -> remtrueeds:nil
   <<if knowntrueeds(s,k) then rempropeds(s,k);>>;


symbolic procedure flagfalseeds(s,k);
   % s:eds, k:id -> flagfalseeds:nil
   <<puteds(s,k,0);>>;


symbolic procedure knownfalseeds(s,k);
   % s:eds, k:id -> knownfalseeds:bool
   geteds(s,k) = 0;


symbolic procedure remfalseeds(s,k);
   % s:eds, k:id -> remfalseeds:nil
   <<if knownfalseeds(s,k) then rempropeds(s,k);>>;


symbolic procedure knowneds(s,k);
   % s:eds, k:id -> knowneds:bool
   geteds(s,k);

endmodule;

end;
