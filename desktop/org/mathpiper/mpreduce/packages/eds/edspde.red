module edspde;

% PDE interface to EDS

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


fluid '(xvars!* kord!* depl!* dependencies);
global '(indxl!*);


put('pde2eds,'rtypefn,'quoteeds);
put('pde2eds,'edsfn,'pde2eds);
flag('(pde2eds),'nospread);

symbolic procedure pde2eds u;
   % u:{pde:list of equations|expressions, [dep,ind:list of variables]}
   % -> pde2eds:eds
   % Assumes all non-kernel variables are indexed 0-forms.
   begin scalar pde,dep,ind,vars,fns,s,map;
   % Analyse PDE and convert to jet notation
   pde := pde2jet u; dep := cadr pde; ind := caddr pde;
   fns := cadddr pde; pde := getrlist car pde;
   vars := append(ind,foreach p in dep collect car p);
   % Save dependencies in shared variable "dependencies"
   dependencies := makelist purge
      foreach k in append(fns,vars) join
          if k := atsoc(lid k,depl!*) then {makelist k};
   % All variables must be dependency-free, and all functions have
   % fdomains.  Functions without dependencies must depend on all
   % independent variables.
   foreach k in vars do
   << k := lid k; % get leading id
      foreach x in atsoc(k,depl!*) do depend1(k,x,nil);
      remflag({k},'impfun); >>;
   foreach k in fns do
   << k := lid k; % get leading id
      foreach x in setdiff(atsoc(k,depl!*),ind) do depend1(k,x,nil);
      if null atsoc(k,depl!*) then foreach x in ind do depend1(k,x,t);
      flag({k},'impfun); >>;
   % Construct contact system
   vars := {};
   foreach p in dep do
      if s := assoc(cdr p,vars) then cdr s := car p . cdr s
      else vars := (cdr p . {car p}) . vars;
   s := partialcontact(makelist foreach l in vars collect
                 makelist l,makelist ind);
   % Decide what to pullback or augment
   map := makelist foreach x in pde join
                   if eqexpr x and kernp simp!* cadr x then {x};
   pde := makelist foreach x in setdiff(pde,map) collect !*eqn2a x;
   % Finished
   if pde then s := edscall augmenteds(s,pde);
   if map then s := edscall pullbackeds(s,map);
   return s;
   end;


put('partial_contact,'rtypefn,'quoteeds);
put('partial_contact,'edsfn,'partialcontact);

symbolic procedure partialcontact(vars,ind);
   % vars:rlist of (degree . rlist of kernel), ind:rlist of kernel
   % -> partialcontact:eds
   begin scalar s,jet,ord,sys;
   vars := foreach l in getrlist vars collect getrlist l;
   vars := sort(vars,function(lambda(x,y); car x > car y));
   ind := !*a2cfrm{makelist getrlist ind};
   s := mkeds{{},foreach f in cfrm_cob ind collect !*k2pf f,ind,nil};
   puteds(s,'sqvar,!*sqvar!*);
   foreach f in {'solved,'reduced,'quasilinear,'pfaffian,'involutive} do
      flagtrueeds(s,f);
   while vars do
   << jet := !*a2cfrm{makelist cdar vars};
      eds_cfrm s := cfrmprod2(eds_cfrm s,jet);
      puteds(s,'jet0,append(geteds(s,'jet0),cdar vars));
      ord := if cdr vars then caar vars - caadr vars else caar vars;
      for i:=1:ord do   % gbsys doesn't produce redundant mixed partials
      << sys := eds_sys s;
         s := edscall gbsys s;
               eds_sys s := append(sys,eds_sys s) >>;
      vars := cdr vars >>;
   return s;
   end;


put('pde2jet,'rtypefn,'quotelist);
put('pde2jet,'listfn,'pde2jeteval);

symbolic procedure pde2jeteval(u,v);
   reval1(car pde2jet revlis u,v);


symbolic procedure pde2jet u;
   % u:{pde:list of equations|expressions, [dep,ind:list of variables]}
   % -> pde2jet:{pde:rlist of prefix, dep:list of kernel . int,
   %             ind:list of kernel, fns:list of kernel}
   begin scalar dep1,ind1,drv,ind,dep,fns,idxs,rlb,!*evallhseqp;
   if length u neq 1 and length u neq 3 then
      rerror(eds,000,"Wrong number of arguments to pde2jet");
   if length u > 1 then
   << dep1 := foreach v in getrlist cadr u collect !*a2k v;
         ind1 := foreach v in getrlist caddr u collect !*a2k v >>;
   on evallhseqp;
   % Collect all derivatives and possible dependent variables
   foreach x in getrlist car u do drv := union(edsdfkernels x,drv);
   edsdebug("Derivatives and functions found",drv,'cob);
   % Scan to distinguish dependent and independent variables and get
   % orders
   ind := edspdescan drv; dep := car ind; ind := cadr ind;
   % If there are explicit variable lists given, pick out functions not
   % in dependent variables, add any dependent variable which did not
   % occur, and likewise for independent variables
   if length u > 1 then
   << if not subsetp(ind,ind1) then
         rerror(eds,000,
                "Less independent variables given than occur in PDE");
      ind := ind1;
      foreach k in ind do dep := delasc(k,dep);
      fns := setdiff(foreach p in dep collect car p,dep1);
      foreach k in fns do dep := delasc(k,dep);
      foreach k in dep1 do if not atsoc(k,dep) then
         dep := (k . 0) . dep; >>;
   % Sort variables
   dep := sort(dep,function ordopcar);
   ind := sort(ind,function ordop);
   fns := sort(fns,function ordop);
   edsdebug("Dependent variables and orders",
            makelist foreach p in dep
               collect {'equal,car p,cdr p},'prefix);
   edsdebug("Independent variables",ind,'cob);
   edsdebug("Other functions",fns,'cob);
   % All variables and functions must be 0-forms.
   foreach k in append(fns,ind) do if not exformp k then mkform!*(k,0);
   foreach k in dep do if not exformp car k then mkform!*(car k,0);
   % All dependent variables and functions with dependencies must be
   % impfuns
   %% flag(foreach k in fns join if atsoc(k,depl!*) then {k},'impfun);
   %% flag(foreach p in dep join if atsoc(car p,depl!*) then
   %%    {car p},'impfun);
   % Get indices and fix index names (cf. gbsys)
   idxs := uniqids ind;
   if not subsetp(idxs,indxl!*) then % indexrange is an rlist
      apply1('indexrange,{{'equal,gensym(),makelist idxs}});
   idxs := pair(ind,idxs);
   % Construct relabelling list
   foreach k in drv do
      if eqcar(k,'df) or eqcar(k,'partdf) then
         if cadr k memq fns then
            rlb := {'equal,k,!*df2partdf k} . rlb
         else
            rlb := {'equal,k,!*df2jet(k,idxs)} . rlb;
   edsdebug("Relabelling list",makelist rlb,'prefix);
   return {subeval{makelist rlb,car u},dep,ind,fns};
   end;


symbolic procedure edspdescan u;
   % u:list of kernel-> edspdescan:{list of kernel . int,list of kernel}
   % Look for dependent and independent variables and order of
   % differentials in u. All variables which are not differentiated wrt
   % are considered dependent. All non-indexed variables are broken up
   % and the arguments are scanned instead.
   begin scalar dep,ind,k,p;
   while u do
   << k := car u; u := cdr u;
      if eqcar(k,'partdf) or eqcar(k,'df) then
      << k := cadr k . edsdfexpand cddr k;
         ind := union(cdr k,ind);
         if (p := atsoc(car k,dep)) then
            cdr p := max(cdr p,length cdr k)
         else
            dep := (car k . length cdr k) . dep >>
      else if not atsoc(k,dep) then
         if atom k or (xvarp k where xvars!* = t) then
            dep := (k . 0) . dep
               else
            foreach v in cdr k do u := union(edsdfkernels v,u) >>;
   foreach k in ind do dep := delasc(k,dep);
   return {dep,ind};
   end;


symbolic procedure edsdfkernels x;
   % x:prefix -> edsdfkernels:list of kernel
   % Returns all kernels in x which could be differentiable
   if eqexpr x then
      union(edsdfkernels cadr x,edsdfkernels caddr x)
   else
   << x := simp!* x;
      foreach k in union(kernels numr x,kernels denr x) join
         if eqcar(k,'df) or eqcar(k,'partdf) or
            assoc(k,depl!*) or exformp k then {k} >>;


symbolic procedure !*df2jet(u,idxs);
   % u:df or partdf kernel, idxs:alist of kernel . id -> !*df2jet:kernel
   begin scalar v,ixl;
   u := cdr u;
   if atom(v := car u) then v := {v};
   ixl := sublis(idxs,edsdfexpand cdr u);
   ixl := foreach j in sort(ixl,'indtordp) collect lowerind j;
   u := car fkern append(v,ixl);
   return mkform!*(u,0);
   end;


symbolic procedure !*df2partdf u;
   % u:df kernel -> !*df2partdf:kernel
   car fkern('partdf . cadr u . edsdfexpand cddr u);


symbolic procedure edsdfexpand u;
   % u:list of (id|posint) -> edsdfexpand:list of id
   % take list of derivatives used by df and partdf operators and
   % expand any repeat counts to explicitly repeat derivatives
   if null u then nil
   else if cdr u and fixp cadr u then
      nconc(nlist(car u,cadr u),edsdfexpand cddr u)
   else
      car u . edsdfexpand cdr u;


symbolic operator mkdepend;
symbolic procedure mkdepend u;
   % u:rlist of rlists -> nil
   foreach v in getrlist u do
      if v := getrlist v then
      << depl!* := v . delasc(car v,depl!*);
         if exformp car v or flagp(car v,'indexvar) then
             flag({car v},'impfun) >>;

endmodule;

end;
