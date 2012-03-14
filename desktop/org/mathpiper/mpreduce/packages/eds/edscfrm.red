module edscfrm;

% Coframing structure for EDS

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


Comment. An EDS coframing is stored in a list:

        cfrm    ::= {'!!cfrm!!,cob,crd,drv,rsx}
        cob     ::= list of kernel
        crd     ::= list of kernel
        drv     ::= list of rule
        rsx         ::= list of prefix (mostly !*sq)

The procedure !*a2cfrm allows a number of algebraic quantities to be
turned into coframings. These quantities will be collectively termed
cfrmdef's.

        cfrmdef ::=  cfrm|eds|rlist of pform

endcomment;


fluid '(cfrmcob!* cfrmcrd!* cfrmdrv!* cfrmrsx!* xvars!* kord!*);
global '(!*sqvar!*);


% Type definition


put('cfrm,'tag,'!!cfrm!!);
put('!!cfrm!!,'rtypefn,'quotecfrm);

symbolic procedure quotecfrm u; 'cfrm;


% Evaluation interface


put('cfrm,'evfn,'cfrmeval);

symbolic procedure cfrmeval(u,v);
   % u:prefix, v:bool -> cfrmeval:prefix
   % v is t for reval, nil for aeval. Here it is ignored (and abused as
   % a local variable!).  u is either an id with an avalue whose car has
   % rtype cfrm or a list with rtype cfrm. This routine differs from
   % most evfn's in that the argument list is evaluated prior to calling
   % a cfrmfn.
   if atom u then
      cfrmeval(if flagp(u,'share) then eval u
              else cadr get(u,'avalue),v)
   else if cfrmp u then u
   else if v := get(car u,'cfrmfn) then
      if flagp(car u,'nospread) then cfrmprotect{v,revlis cdr u}
      else cfrmprotect(v . revlis cdr u)
   else rerror(eds,000,{"Illegal operation on coframings"});


symbolic procedure cfrmprotect u;
   % u:prefix -> cfrmprotect:prefix
   % Protected evaluation environment for
   % operations on coframings.
   begin scalar m,ok,od;
         scalar xvars!*;
   % If one of the arguments is cfrm, take the last one
   foreach v in cdr u do if cfrmp v then m := v;
   % Save environment and adjust for cfrm calculation.
   ok := kord!*; od := append(get('d,'kvalue),nil); % copy pairs
   if m then m := setcfrm m;
   u := errorset!*(car u . foreach j in cdr u collect mkquote j,t);
   % Restore environment
   if m then setcfrm m;
   setkorder ok; if od then put('d,'kvalue,od) else remprop('d,'kvalue);
   if errorp u then error1()
   else return car u;
   end;


% Constructors and tests


symbolic procedure mkcfrm u;
   % tag u as cfrm
   '!!cfrm!! . u;


symbolic procedure copycfrm u;
   % copy pairs in u to allow destructive operations
   foreach p in u collect p;


symbolic procedure cfrmp u;
   % u:any -> cfrmp:bool
   eqcar(u,'!!cfrm!!);


symbolic procedure emptycfrm;
   % -> emptycfrm:cfrm
   mkcfrm{{},{},{},{}};


% Global background coframing


put('set_coframing,'psopfn,'setcfrmeval);

symbolic procedure setcfrmeval u;
   % u:{cfrm|nil} -> setcfrmeval:cfrm
   begin scalar m;
   u :=
      if null u or (u = {nil}) then setcfrm emptycfrm()
      else if cfrmp(m := reval car u) then setcfrm m
      else if edsp m then setcfrm eds_cfrm m
      else typerr(u,'cfrm);
   rmsubs();
   return u;
   end;


symbolic procedure setcfrm m;
   % m:cfrm -> setcfrm:cfrm
   % Set up m as background coframing, returning old one.
   % NB. Changes kernel order and let rules.
   begin scalar n;
   n := getcfrm();
   if m = n then return n;
   cfrmswapkord(cfrm_cob m,cfrm_cob n);
   cfrmswaprules(cfrm_drv m,cfrm_drv n);
   cfrmcob!* := cfrm_cob m;
   cfrmcrd!* := cfrm_crd m;
   cfrmdrv!* := cfrm_drv m;
   cfrmrsx!* := (foreach p in cfrm_rsx m collect
                               xpartitop p) where xvars!* = cfrm_crd m;
   return n;
   end;


symbolic procedure cfrmswapkord(new,old);
   % new,old:list of kernel -> cfrmswapkord:list of kernel
   % Swap old for new in kernel ordering.  New kernels come first.
   % Return old kernel ordering.
   setkorder append(new,setdiff(kord!*,append(new,old)));


symbolic procedure cfrmswaprules(new,old);
   % new,old:list of rules -> cfrmswaprules:nil
   % Swap the current rules given by old for those contained in
   % new. Since these rules will be removed before returning to the
   % outside, try to preserve !*sqvar!*. This may cause trouble.
   begin scalar sq;
   if new = old then return;
   sq := !*sqvar!*;
   if old then rule!-list(old,nil);
   if new then rule!-list(new,t);
   !*sqvar!* := sq;
   car !*sqvar!* := t;
   end;


symbolic procedure getcfrm();
   % -> getcfrm:cfrm
   % Get background coframing.
   mkcfrm{cfrmcob!*,cfrmcrd!*,cfrmdrv!*,
                    foreach f in cfrmrsx!* collect !*pf2a f};



% Input interface


put('coframing,'rtypefn,'quotecfrm);
put('coframing,'cfrmfn,'!*a2cfrm);
flag('(coframing),'nospread);

symbolic procedure !*a2cfrm u;
   % u:nil|{cfrmdef}|{xeds}|list of cpt(see below) -> !*a2cfrm:cfrm
   % With no arguments, return the background coframing.  For a cfrm,
   % just return it (this redundancy allows !*a2cfrm to be called from
   % contact etc).  For an eds or xeds, just return the associated
   % coframing(s).  For a list of pforms, deduce the coframing
   % structure required to sustain them.  Otherwise, the coframing is
   % specified by a list of its components.
   if null u then getcfrm()
   else if length u = 1 then
      if cfrmp car u then
         car u
      else if edsp car u then
          eds_cfrm car u
      else if xedsp car u then
          makelist foreach s in getrlist car u collect eds_cfrm s
      else
          !*sys2cfrm !*a2sys car u
   else  !*a2cfrm1 u;


symbolic procedure !*a2cfrm1 u;
   % u:list of cpt -> !*a2cfrm1:cfrm
   %       where cpt is one of
   %       cob - list of 1-form kernel
   %       crd - list of 0-form kernel
   %       rsx - list of prefix inequality
   %       drv - list of rule
   begin scalar cob,crd,drv,rsx;
   % Read through arguments
   foreach l in u do
      if null(l := getrlist indexexpandeval {l}) then nil
      else if eqexpr car l then drv := l
      else if eqcar(car l,'neq) then rsx := l
      else if xdegree car l = 1 then cob := l
      else if xdegree car l = 0 then crd := l
      else rerror(eds,000,"Badly formed coframing");
   % Check correctness of each item and convert to desired type
   cob := foreach k in cob collect
      if xdegree(k := !*a2k k) = 1 then k
      else typerr(k,"cobasis element");
   crd := foreach k in crd collect
      if xdegree(k := !*a2k k) = 0 and
         xvarp k where xvars!* = t then k
      else typerr(k,"coordinate");
   drv := foreach r in drv collect
      if eqexpr r then r
      else typerr(r,"structure equation");
   rsx := foreach f in rsx collect
      if eqcar(f,'neq) then aeval {'difference,cadr f,caddr f}
      else typerr(f,"restriction (only neq allowed)");
   return checkcfrm mkcfrm{cob,crd,drv,rsx};
   end;


symbolic procedure !*sys2cfrm s;
   % s:sys -> !*sys2cfrm:cfrm
   % Return coframing suitable for set of pforms s.  Error if variables
   % of other degrees found explicitly in s. All structure equations are
   % checked for new forms and restrictions.
   begin scalar crd,cob,drv,rsx;
   while s do
      begin scalar new;
      foreach k in kernelspf car s do
         if not(k memq crd or k memq cob) and exformp k then
                  if xdegree k = 0 then
               if assoc(k,depl!*) or eqcar(k,'partdf) or
                  not(xvarp k where xvars!* = t) then % function
                         foreach p in xpows exdfk k do new := !*k2pf p . new
               else
               << crd := k . crd;
                          new := exdfk k . new;
                          if car new neq !*k2pf {'d,k} then
                            drv := {'replaceby,{'d,k},!*pf2a car new} . drv >>
                  else if xdegree k = 1 then
            << cob := k . cob;
               if not exact k then
               << new := exdfk k . new;
                          if car new neq !*k2pf {'d,k} then
                            drv := {'replaceby,{'d,k},!*pf2a car new} . drv
                          else
                            new := cdr new >>
               else if not(cadr k memq crd) then
                  crd := cadr k . crd >>
                  else typerr(k,"0-form or 1-form");
      foreach q in xcoeffs car s do
         if not freeoffl(denr q,crd) then
            rsx := mk!*sq !*f2q denr q . rsx;
      s := append(cdr s,new);
      end;
   return purgecfrm mkcfrm{sort(cob,'termordp),sort(crd,'termordp),drv,
                           rsx}
   end;


% Output interface


put('!!cfrm!!,'prifn,'cfrmprint);
put('!!cfrm!!,'fancy!-reform,'!*cfrm2a);
put('cfrm,'texprifn,'texpricfrm);
%put('cfrm,'prepfn,'!*cfrm2a);


symbolic procedure cfrmprint m;
   % m:cfrm -> cfrmprint:bool
   % if already in external format, use inprint
   maprin !*cfrm2a m;


symbolic procedure !*cfrm2a m;
   % m:cfrm -> !*cfrm2a:prefix
      "coframing" .
         {makelist cfrm_cob m,
          makelist cfrm_crd m,
          makelist foreach r in cfrm_drv m collect !*rule2prefix r,
          makelist foreach f in cfrm_rsx m collect {'neq,reval f,0}};


symbolic procedure !*rule2prefix r;
   car r . foreach a in cdr r collect
      if eqcar(a,'!*sq) then prepsq!* cadr a else a;


symbolic procedure texpricfrm(u,v,w);
   % Have to hide coframing from TRI's makeprefix
   texvarpri('texpriedsop . !*cfrm2a u,v,w);


symbolic procedure texpricfrm(u,v,w);
   % Have to hide the coframing from TRI's makeprefix
   % but not from TRIX's makeprefix.
   texvarpri(
      if get('hodge,'texname) then !*cfrm2a u
      else 'texpriedsop . !*cfrm2a u,v,w);


% Algebraic access to coframing parts


put('cobasis,'rtypefn,'quotelist);
put('cobasis,'listfn,'cobeval);

symbolic procedure cobeval(s,v);
   % s:{any}, v:bool -> cobeval:prefix cob
   % cobeval1 returns true prefix always
   if null v then aeval cobeval1 s else cobeval1 s;


symbolic procedure cobeval1 s;
   % s:{any} -> cobeval1:prefix cob
   % For an eds, returns the cobasis in the ordering used internally.
   if cfrmp(s := reval car s) then
      makelist cfrm_cob s
   else if edsp s then
      makelist edscob s
   else if xedsp s then
      makelist foreach x in cdr s collect makelist edscob x
   else edsparterr(s,"cobasis");


put('coordinates,'rtypefn,'quotelist);
put('coordinates,'listfn,'crdeval);

symbolic procedure crdeval(s,v);
   % s:{any}, v:bool -> crdeval:prefix cob
   % crdeval1 returns true prefix always
   if null v then aeval crdeval1 s else crdeval1 s;


symbolic procedure crdeval1 s;
   % s:{any} -> crdeval1:prefix cob
   if cfrmp(s := reval car s) then
      makelist cfrm_crd s
   else if edsp s then
      makelist cfrm_crd eds_cfrm s
   else if xedsp s then
      makelist foreach x in cdr s collect makelist cfrm_crd eds_cfrm x
   else if rlistp s then
      makelist purge foreach x in getrlist s join
         getrlist allcoords x
   else if null getrtype s then
      allcoords s
   else edsparterr(s,"coordinates");


put('structure_equations,'rtypefn,'quotelist);
put('structure_equations,'listfn,'drveval);

symbolic procedure drveval(s,v);
   % s:{cfrm}|{eds}|{xeds}|{rlist}|{rlist,rlist}, v:bool
   % -> drveval:prefix cob
   reval1(drveval1 s,v);


symbolic procedure drveval1 s;
   % s:{cfrm}|{eds}|{xeds}|{rlist}|{rlist,rlist} -> drveval1:prefix cob
   % Input can be cfrm, eds, xeds, xform or xform + inverse
   if cfrmp car(s := revlis s) then
      makelist cfrm_drv car s
   else if edsp car s then
      makelist cfrm_drv eds_cfrm car s
   else if xedsp car s then
      makelist foreach x in getrlist car s collect
          makelist cfrm_drv eds_cfrm x
   else if rlistp car s and cdr car s and eqexpr cadr car s then
      xformdrveval s
   else edsparterr(s,"structure equations");


put('restrictions,'rtypefn,'quotelist);
put('restrictions,'listfn,'rsxeval);

symbolic procedure rsxeval(s,v);
   % s:{any}, v:bool -> rsxeval:prefix cob
   if cfrmp(s := reval car s) then
      makelist foreach r in cfrm_rsx s collect
          {'neq,reval1(r,v),0}
   else if edsp s then
      makelist foreach r in cfrm_rsx eds_cfrm s collect
          {'neq,reval1(r,v),0}
   else if xedsp s then
      makelist foreach x in cdr s collect
          makelist foreach r in cfrm_rsx eds_cfrm x collect
             {'neq,reval1(r,v),0}
   else edsparterr(s,"restrictions");


symbolic procedure edsparterr(u,v);
   % u:prefix, v:any -> edsparterr:error
   % u is math-printed (with nat off), v is line-printed
   msgpri(nil,u,{"has no",v},nil,t);


symbolic procedure cfrmpart(m,n);
   % m:cfrm, n:int -> cfrmpart:prefix
   if n = 0 then 'coframing
   else if n = 1 then makelist cfrm_cob m
   else if n = 2 then makelist cfrm_crd m
   else if n = 3 then makelist cfrm_drv m
   else if n = 4 then
      makelist foreach r in cfrm_rsx m collect {'neq,r,0}
   else parterr(m,n);

put('!!cfrm!!,'partop,'cfrmpart);


symbolic procedure cfrmsetpart(m,l,r);
   % m:cfrm, l:list of int, r:prefix -> cfrmsetpart:error
   rerror(eds,000,"Part setting disabled on coframing operator");

put('!!cfrm!!,'setpartop,'cfrmsetpart);


% Consistency check, resimplification and cleanup


symbolic procedure checkcfrm m;
   % m:cfrm -> checkcfrm:cfrm
   % Check integrity and completeness of m. Cobasis must be correctly
   % specified, other details (eg missing coordinates, restrictions) can
   % be deduced via !*sys2cfrm. Call via cfrmprotect to install correct
   % structure equations and korder.
   cfrmprotect {'checkcfrm1,m};


symbolic procedure checkcfrm1 m;
   % m:cfrm -> checkcfrm1:cfrm
   % As checkcfrm, but assumes m is background coframing.
   begin scalar n,u,drv;
   m := copycfrm m;
   % Pick up coframing implied by cob/crd
   n := !*sys2cfrm !*a2sys makelist append(cfrm_cob m,cfrm_crd m);
   % Error if cobasis different
   if cfrm_cob n neq cfrm_cob m then
      rerror(eds,000,"Missing cobasis elements");
   % Coordinates and structure equations of n must include those of m,
   % but some restrictions may not be noticed.
   cfrm_rsx n := union(cfrm_rsx m,cfrm_rsx n);
   % Check whether all structure equations are known.
   % Missing coordinate differentials show up as missing cobasis
   % elements.
   drv := foreach d in cfrm_drv n collect cadr d;
   foreach k in cfrm_cob n do
      if not exact k and not member({'d,k},drv) then u := k . u;
   if u then edsverbose("Missing structure equations",reverse u,'cob);
   return purgecfrm n;
   end;


symbolic procedure resimpcfrm s;
   % s:cfrm -> resimpcfrm:cfrm
   begin scalar r;
   r := copycfrm s;
   cfrm_cob r := foreach f in cfrm_cob s collect reval f;
   cfrm_crd r := foreach f in cfrm_crd s collect reval f;
   cfrm_drv r := foreach f in cfrm_drv s collect reval f;
   cfrm_rsx r := foreach f in cfrm_rsx s collect aeval f;
   return if r = s then s else checkcfrm r;
   end;


put('reorder,'psopfn,'reordereval);
   % Can't have an cfrmfn here because we want the external kernel order

symbolic procedure reordereval s;
   % s:{any} -> reordereval:prefix cob
   if cfrmp(s := reval car s) then
      reordercfrm s
   else if edsp s then
      reordereds s
   else if xedsp s then
      makelist foreach x in cdr s collect reordereds x
   else msgpri(nil,nil,"Don't know how to reorder",s,t);


symbolic procedure reordercfrm s;
   % s:cfrm -> reordercfrm:cfrm
   begin scalar r;
   r := copycfrm s;
   cfrm_cob r := sort(cfrm_cob s,'termordp);
   cfrm_crd r := sort(cfrm_crd s,'termordp);
   cfrm_drv r :=
      sort(cfrm_drv s,'(lambda (x y) (termordp (cadr x) (cadr y))));
   cfrm_rsx r := sort(cfrm_rsx s,'ordop);
   return if r = s then s else r;
   end;


put('cleanup,'rtypefn,'getrtypecar);
put('cleanup,'cfrmfn,'cleancfrm);

symbolic procedure cleancfrm m;
   % m:cfrm -> cleancfrm:cfrm
   % Clean up, resimplify and check m.
   begin scalar n;
   n := resimpcfrm m;
   return % eq test here essential!
      if n eq m then checkcfrm m
      else n;
   end;


symbolic procedure purgecfrm m;
   % m:cfrm -> purgecfrm:cfrm
   % Clean up drv and rsx parts of m.
   % Background coframing need not be m.
   begin scalar cfrmcrd!*,cfrmcob!*;
   m := copycfrm m;
   cfrmcob!* := cfrm_cob m;
   cfrmcrd!* := cfrm_crd m;
   cfrm_drv m := purgedrv cfrm_drv m;
   cfrm_rsx m := purgersx cfrm_rsx m;
   return m;
   end;


symbolic procedure purgedrv x;
   % x:drv -> purgedrv:drv
   % Sift through structure equations, checking they are all current.
   % Can't use memq here because lhs's are not evaluated, so kernels may
   % not be unique. Take out d x => d x as well. Should we catch d(0)?
   begin scalar drv,dl,dr,r2;
   foreach r in x do
      if exact(dl := cadr r) and
         (cadr dl member cfrmcob!* or cadr dl member cfrmcrd!*) and
               not(kernp(dr := simp!* caddr r) and dl = mvar numr dr) then
                  if null (r2 := assoc(dl,drv)) then
               drv := (dl . dr) . drv
            else if cdr r2 neq dr and
               resimp cdr r2 neq resimp dr then
               << edsdebug("Inconsistent structure equations",
                     makelist{{'replaceby,dl,mk!*sq dr},
                              {'replaceby,car r2,mk!*sq cdr r2}},
                           'prefix);
                         rerror(eds,000,"Inconsistent structure equations") >>;
   drv := foreach p in reversip drv collect
                   {'replaceby,car p,mk!*sq cdr p};
   return sort(drv,'(lambda (x y) (termordp (cadr x) (cadr y))));
   end;


symbolic procedure purgersx x;
   % x:rsx -> purgersx:rsx
   begin scalar rsx;
   foreach f in reverse purge x do
      rsx := addrsx(numr simp!* f,rsx);
   return rsx;
   end;


symbolic procedure addrsx(x,rsx);
   % x:sf, rsx:rsx -> addrsx:rsx
   % Must reorder before fctrf in case we are handling expressions from
   % another coframing.
   begin
   if not cfrmconstant x and
      not member(mk!*sq !*f2q x,rsx)
   then foreach f in cdr fctrf reorder x do
      if not cfrmconstant car f and
         not member(f := mk!*sq !*f2q car f,rsx)
      then rsx := f . rsx;
   return rsx;
   end;


% Algebraic operations


infix cross;
precedence cross,times;
put('cross,'rtypefn,'getrtypecar);
put('cross,'edsfn,'extendeds);
put('cross,'cfrmfn,'cfrmprod);
flag('(cross),'nospread);
flag('(cross),'nary);


symbolic procedure extendeds u;
   % u:eds.list of cfrmdef -> extendeds:eds
   begin scalar s,jet0;
   % trivial case first
   if null cdr u then return car u;
   s := copyeds car u;
   u := cfrmprod cdr u;
   if jet0 := geteds(s,'jet0) then
      puteds(s,'jet0,
         purgejet0(append(jet0,setdiff(cfrm_crd u,edscrd s)),
                          uniqids indkrns s));
   eds_cfrm s := cfrmprod2(eds_cfrm s,u);
   remkrns s;
   return normaleds purgeeds!* s;
   end$

symbolic procedure purgejet0(crd,idxl);
   begin scalar j,j0;
   idxl := foreach i in flatindxl idxl collect lowerind i;
   foreach c in crd do
   << j := j0;
      while j and not jetprl(c,car j,idxl) do j := cdr j;
      if null j then
         j0 := c . foreach c0 in j0 join
            if not jetprl(c0,c,idxl) then {c0} >>;
   return j0;
   end$

symbolic procedure jetprl(c,c0,idxl);
   if c := splitoffindices(c0,c) then subsetp(cdr c,idxl)$


symbolic procedure cfrmprod u;
   % u:list of cfrmdef -> cfrmprod:cfrm
   % u is non-empty, first line excludes m:xeds
   (if not cfrmp m then typerr(car u,"coframing")
   else if length u = 1 then m
   else cfrmprotect {'cfrmprod2,m,cfrmprod cdr u})
      where m = !*a2cfrm{car u};


symbolic procedure cfrmprod2(m,n);
   % m,n:cfrm -> cfrmprod2:cfrm
   if xnp(cfrm_cob m,cfrm_cob n) or
      xnp(cfrm_crd m,cfrm_crd n)
   then cfrmbprod(m,n)
   else mkcfrm{append(cfrm_cob m,cfrm_cob n),
                    append(cfrm_crd m,cfrm_crd n),
                    append(cfrm_drv m,cfrm_drv n),
                    append(cfrm_rsx m,cfrm_rsx n)}$


symbolic procedure cfrmbprod(m,n);
   % m,n:cfrm -> cfrmbprod:cfrm
   % m and n are cfrm with common elements,
   % result is bundle product.
   begin scalar z,u,v;
   % get common elements
   z := !*a2sys makelist append(
               intersection(cfrm_cob m,cfrm_cob n),
         intersection(cfrm_crd m,cfrm_crd n));
   % generate coframing from each
   setcfrm m; u := !*sys2cfrm z;
   setcfrm n; v := !*sys2cfrm z;
   % check equivalence
   if not equalcfrm(u,v) then
      rerror(eds,000,
         "Cannot form coframing product: overlap cannot be factored");
   % compose as (m/u).n
   return resimpcfrm mkcfrm{
      append(setdiff(cfrm_cob m,cfrm_cob u),cfrm_cob n),
      append(setdiff(cfrm_crd m,cfrm_crd u),cfrm_crd n),
      append(setdiff(cfrm_drv m,cfrm_drv u),cfrm_drv n),
      append(setdiff(cfrm_rsx m,cfrm_rsx u),cfrm_rsx n)};
   end$


put('dim,'simpfn,'simpdim);

symbolic procedure simpdim u;
   % u:{any} -> simpdim:sq
   if cfrmp(u := reval car u) then
      length cfrm_cob u ./ 1
   else if edsp u then
      length edscob u ./ 1
   else edsparterr(u,"dimension");



% Auxiliary routines


Comment.  The following routines are for testing whether an expression
is nowhere zero on a restricted coframing specified by some coordinates
and some expressions assumed not to vanish.  Expressions with unknown
(explicit or implicit) dependence on the coordinates are not nowhere
zero.
endcomment;

symbolic procedure cfrmnowherezero x;
   % x:sf -> cfrmnowherezero:bool
   % Heuristic to test if x is nowhere zero on the coframing described
   % by cfrmcrd!* restricted away from the zeros of the expressions in
   % cfrmrsx!*. This version checks first directly, and then tests (if
   % x can be factorised) whether all the factors are nowhere zero.
   (domainp x or                     % quick exit for constants
   cfrmnowherezero1 xpartitsq(x ./ 1) or       % check x as a whole
   if (x := cdr fctrf x) and (length x > 1 or cdar x > 1) then
   << while x and cfrmnowherezero1 xpartitsq(caar x ./ 1) do
               x := cdr x;
      null x >>)
   where xvars!* = cfrmcrd!*;


symbolic procedure cfrmnowherezero1 x;
   % x:pf -> cfrmnowherezero1:bool
   % Result is t if x is constant or doesn't vanish on restricted space,
   % as tested by substituting x=0 into the expressions in cfrmrsx!* and
   % seeing if one vanishes. If lc x contains an (explicit or implicit)
   % unknown dependence on cfrmcrd!*, result is nil.
   if lpow x = 1 then cfrmconstant numr lc x
   else cfrmviolatesrsx x;


symbolic procedure cfrmconstant x;
   % x:sf -> cfrmconstant:bool
   freeoffl(x,cfrmcrd!*);


symbolic procedure freeoffl(x,v);
   % x:sf, v:list of kernel -> freeoffl:bool
   % freeofl for sf's
   null v or freeoff(x,car v) and freeoffl(x,cdr v);


symbolic procedure freeoff(x,v);
   % x:sf, v:kernel -> freeoff:bool
   % freeof for sf's, using ndepends from EXCALC to handle indexed
   % forms properly
   if domainp x then t
   else if sfp mvar x then
      freeoff(mvar x,v) and freeoff(lc x,v) and freeoff(red x,v)
   else
      not ndepends(mvar x,v) and freeoff(lc x,v) and freeoff(red x,v);


symbolic procedure cfrmviolatesrsx x;
   % x:pf -> cfrmviolatesrsx:bool
   % result is t if x = 0 annihilates at least one of cfrmrsx!*
   begin scalar rsx;
   rsx := cfrmrsx!*; x := {x};
   while rsx and xreduce(car rsx,x) do rsx := cdr rsx;
   return not null rsx; % to give true bool and make trace nicer
   end;

endmodule;

end;
