module hephys;   % Support for high energy physics calculations.

% Author: Anthony C. Hearn.

% Generalizations for n dimensional vector and gamma algebra by
% Gastmans, Van Proeyen and Verbaeten, University of Leuven, Belgium.

% Copyright (c) 1991 The RAND Corporation. All rights reserved.

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


create!-package('(hephys),'(appl));

fluid '(!*nospurp !*sub2 ncmp!* ndims!*);

global '(defindices!* indices!* mul!* ndim!*);

defindices!* := nil; % Deferred indices in N dim calculations.

indices!* := nil; % List of indices in High Energy Physics
                  % tensor expressions.
ndim!* := 4;      % Number of dimensions in gamma algebra.

% *********************** SOME DECLARATIONS *************************

deflist ('((cons simpdot)),'simpfn);

flag('(cons),'symmetric);   % Since CONS is used in algebraic mode
                            % only for High Energy Physics expressions.

put('vector,'stat,'rlis);

% put('vector,'formfn,'formvector);

%symbolic procedure formvector(u,vars,mode);
%   if mode eq 'algebraic
%     then list('vector1,'list . formlis(cdr u,vars,'algebraic))
%    else u;

symbolic procedure vector u; vector1 u;

symbolic procedure vector1 u;
   for each x in u do
      begin scalar y;
         if not idp x or (y := gettype x) and y neq 'hvector
           then typerr(if y then {y,x} else x,"high energy vector")
          else put(x,'rtype,'hvector)
      end;

put('hvector,'fn,'vecfn);

put('hvector,'evfn,'veval);

put('g,'simpfn,'simpgamma);

noncom g;

symbolic procedure index u;
   begin vector1 u; rmsubs(); indices!* := union(indices!*,u) end;

symbolic procedure remind u;
   begin indices!* := setdiff(indices!*,u) end;

symbolic procedure mass u;
   if null car u then rerror(hephys,1,"No arguments to MASS")
    else <<for each x in u do put(cadr x,'rtype,'hvector);
           for each x in u do put(cadr x,'mass,caddr x)>>;

symbolic procedure getmas u;
   (if x then x else rerror(hephys,2,list(u,"has no mass")))
    where x=get(u,'mass);

symbolic procedure vecdim u;
   begin ndim!* := car u end;

symbolic procedure mshell u;
   begin scalar x,z;
    a:  if null u then return let0 z;
        x := getmas car u;
        z := list('equal,list('cons,car u,car u),list('expt,x,2)) . z;
        u := cdr u;
        go to a
   end;

symbolic procedure nospur u;
   <<rmsubs(); !*nospurp := t; flag(u,'nospur)>>;

symbolic procedure spur u;
   <<rmsubs(); remflag(u,'nospur); remflag(u,'reduce)>>;

rlistat '(vecdim index mass mshell remind nospur spur vector);


% ******** FUNCTIONS FOR SIMPLIFYING HIGH ENERGY EXPRESSIONS *********

symbolic procedure veval(u,v);
   begin scalar z;
      for each x in nssimp(u,'hvector) do
       <<if null cdr x then rerror(hephys,3,"Missing vector")
          else if cddr x
           then msgpri("Redundant vector in",cdr x,nil,nil,t);
         z := aconc!*(z,retimes(prepsq car x . cdr x))>>;
      return replus z
   end;

symbolic procedure vmult u;
   begin scalar z;
        z := list list(1 . 1);
    a:  if null u then return z;
        z := vmult1(nssimp(car u,'hvector),z);
        if null z then return;
        u := cdr u;
        go to a
   end;

symbolic procedure vmult1(u,v);
   begin scalar z;
        if null v then return;
    a:  if null u then return z
         else if cddar u
          then msgpri("Redundant vector in",cdar u,nil,nil,t);
        z := nconc!*(z,for each j in v collect
                          multsq(car j,caar u) . append(cdr j,cdar u));
        u := cdr u;
        go to a
   end;

symbolic procedure simpdot u;
   mkvarg(u,function dotord);

symbolic procedure dotord u;
   <<if xnp(u,indices!*) and not ('isimpq memq mul!*)
           then mul!* := aconc!*(mul!*,'isimpq) else nil;
        if 'a memq u
          then rerror(hephys,4,
                      "A represents only gamma5 in vector expressions")
         else mksq('cons . ord2(car u,carx(cdr u,'dot)),1)>>;

symbolic procedure mkvarg(u,v);
   begin scalar z;
        u := vmult u;
        z := nil ./ 1;
    a:  if null u then return z;
        z := addsq(multsq(apply1(v,cdar u),caar u),z);
        u := cdr u;
        go to a
   end;

symbolic procedure simpgamma u;
   if null u or null cdr u
       then rerror(hephys,5,"Missing arguments for G operator")
    else begin scalar z;
        if not ('isimpq memq mul!*) then mul!*:= aconc!*(mul!*,'isimpq);
        ncmp!* := t;
        z := nil ./ 1;
        for each j in vmult cdr u do
           z := addsq(multsq(mksq('g . car u . cdr j,1),car j),z);
        return z
   end;

symbolic procedure simpeps u;
   mkvarg(u,function epsord);

symbolic procedure epsord u;
   if repeats u then nil ./ 1 else mkepsq u;

symbolic procedure mkepsk u;
   % U is of the form (v1 v2 v3 v4).
   % Value is <sign flag> . <kernel for EPS(v1,v2,v3,v4)>.
   begin scalar x;
        if xnp(u,indices!*) and not('isimpq memq mul!*)
          then mul!* := aconc!*(mul!*,'isimpq);
        x := ordn u;
        u := permp(x,u);
        return u . ('eps . x)
   end;

symbolic procedure mkepsq u;
   (lambda x; (lambda y; if null car x then negsq y else y)
                 mksq(cdr x,1))
        mkepsk u;


% ** FUNCTIONS FOR SIMPLIFYING VECTOR AND GAMMA MATRIX EXPRESSIONS **

symbolic smacro procedure mkg(u,l);
   % Value is the standard form for G(L,U).
   mksf('g . l . u);

symbolic smacro procedure mka l;
   % Value is the standard form for G(L,A).
   mksf list('g,l,'a);

symbolic smacro procedure mkgamf(u,l);
   mksf('g . (l . u));

symbolic procedure mkg1(u,l);
   if not flagp(l,'nospur) then mkg(u,l) else mkgamf(u,l);

symbolic smacro procedure mkpf(u,v);
   multpf(u,v);

symbolic procedure mkf(u,v);
   multf(u,v);

symbolic procedure multd!*(u,v);
   if u=1 then v else multd(u,v);     % onep

symbolic smacro procedure addfs(u,v);
   addf(u,v);

symbolic smacro procedure multfs(u,v);
   % U and V are pseudo standard forms.
   % Value is pseudo standard form for U*V.
   multf(u,v);

put('rcons,'cleanupfn,'isimpa);

fluid '(!*!:avoid);

% I have moved the definition from assist/baglist.red into here in the
% hope that it will stilll remain valid in this context. Doing so avoids
% having a function redefinition and use of the 'lose flag and following
% potential confusion.

symbolic procedure isimpa(u,v);
   if eqcar(u,'list) or
      !*!:avoid or
      (atom u and get(u, 'rtype) eq 'hvector) then <<
      !*!:avoid := nil;
      u >>
    else !*q2a1(isimpq simp u,v);

symbolic procedure isimpq u;
   begin scalar ndims!*;
      ndims!* := simp ndim!*;
      if denr ndims!* neq 1
        then <<!*sub2 := t;
               ndims!* := multpf(mksp(list('recip,denr ndims!*),1),
                                 numr ndims!*)>>
       else ndims!* := numr ndims!*;
   a: u := isimp1(numr u,indices!*,nil,nil,nil) ./ denr u;
      if defindices!*
        then <<indices!* := union(defindices!*,indices!*);
               defindices!* := nil;
               go to a>>
       else if null !*sub2 then return u
       else return resimp u
   end;

symbolic procedure isimp1(u,i,v,w,x);
   if null u then nil
    else if domainp u
       then if x then multd(u,spur0(car x,i,v,w,cdr x))
             else if v
              then rerror(hephys,6,"Unmatched index" . mapovercar v)
             else if w then multfs(emult w,isimp1(u,i,v,nil,x))
             else u
    else addfs(isimp2(car u,i,v,w,x),isimp1(cdr u,i,v,w,x));

symbolic procedure isimp2(u,i,v,w,x);
   begin scalar z;
        if atom (z := caar u) then go to a
         else if car z eq 'cons and xnp(cdr z,i)
            then return dotsum(u,i,v,w,x)
         else if car z eq 'g
          then go to b
         else if car z eq 'eps then return esum(u,i,v,w,x);
    a:  return mkpf(car u,isimp1(cdr u,i,v,w,x));
    b:  z := gadd(appn(cddr z,cdar u),x,cadr z);
        return isimp1(multd!*(nb car z,cdr u),i,v,w,cdr z)
   end;

symbolic procedure nb u;
   if u then 1 else -1;

symbolic smacro procedure mkdot(u,v);
   % Returns a standard form for U . V.
   mksf('cons . ord2(u,v));

symbolic procedure dotsum(u,i,v,w,x);
   begin scalar i1,n,u1,u2,v1,y,z,z1;
        n := cdar u;
        if not (car (u1 := cdaar u) member i) then u1 := reverse u1;
        u2 := cadr u1;
        u1 := car u1;
        v1 := cdr u;
        if n=2 then go to h
         else if n neq 1 then typerr(n,"index power");
    a:  if u1 member i then go to a1
         else if null (z := mkdot(u1,u2)) then return nil
         else return mkf(z,isimp1(v1,i1,v,w,x));
    a1: i1 := delete(u1,i);
        if u1 eq u2 then return multf(ndims!*,isimp1(v1,i1,v,w,x))
         else if not (z := bassoc(u1,v)) then go to c
         else if u2 member i then go to d;
        if u1 eq car z then u1 := cdr z else u1 := car z;
        go to e;
    c:  if z := memlis(u1,x)
            then return isimp1(v1,
                              i1,
                              v,
                              w,
                              subst(u2,u1,z) . delete(z,x))
         else if z := memlis(u1,w)
            then return esum((('eps . subst(u2,u1,z)) . 1) . v1,
                             i1,
                             v,
                             delete(z,w),
                             x)
         else if u2 member i and null y then go to g;
        return isimp1(v1,i,(u1 . u2) . v,w,x);
    d:  z1 := u1;
        u1 := u2;
        if  z1 eq car z then u2 := cdr z else u2 := car z;
    e:  i := i1;
        v := delete(z,v);
        go to a;
    g:  y := t;
        z := u1;
        u1 := u2;
        u2 := z;
        go to a1;
    h:  if u1 eq u2 then rerror(hephys,7,
                                "2 invalid as repeated index power");
        i := i1 := delete(u1,i);
        u1 := u2;
        go to a
   end;

symbolic procedure mksf u;
   % U is a non-unique kernel.
   % Value is a (possibly substituted) standard form for U.
   begin scalar x;
        x := mksq(u,1);
        if denr x=1 then return numr x;
        !*sub2 := t;
        return !*p2f mksp(u,1)
   end;


% ********* FUNCTIONS FOR SIMPLIFYING DIRAC GAMMA MATRICES **********

symbolic procedure gadd(u,v,l);
   begin scalar w,x; integer n;
        n := 0;                 % Number of gamma5 interchanges.
        if not (x := atsoc(l,v)) then go to a;
        v := delete(x,v);
        w := cddr x;            % List being built.
        x := cadr x;            % True if gamma5 remains.
    a:  if null u then return (evenp n . (l . x . w) . v)
         else if car u eq 'a then go to c
         else w := car u . w;
    b:  u := cdr u;
        go to a;
    c: if ndims!* neq 4
         then rerror(hephys,8,"Gamma5 not allowed unless vecdim is 4");
       x := not x;
        n := length w + n;
        go to b
   end;


% ***** FUNCTIONS FOR COMPUTING TRACES OF DIRAC GAMMA MATRICES *******

symbolic procedure spur0(u,i,v1,v2,v3);
   begin scalar l,w,i1,kahp,n,z;
      l := car u;
      n := 1;
      z := cadr u;
      u := reverse cddr u;
      if z then u := 'a . u; % Gamma5 remains.
      if null u then go to end1
       else if null flagp(l,'nospur)
        then if car u eq 'a and (length u<5 or hevenp u)
                  or not(car u eq 'a) and not hevenp u
               then return nil
              else if null i then <<w := reverse u; go to end1>>;
    a:
      if null u then go to end1
       else if car u member i
        then if car u member cdr u
               then <<if car u eq cadr u
                        then <<i := delete(car u,i);
                               u := cddr u;
                               n := multf(n,ndims!*);
                               go to a>>;
                      kahp := t;
                      i1 := car u . i1;
                      go to a1>>
              else if car u member i1 then go to a1
              else if z := bassoc(car u,v1)
               then <<v1 := delete(z,v1);
                      if w then i := delete(car w,i);
                      u := other(car u,z) . cdr u;
                      go to a>>
              else if z := memlis(car u,v2)
               then return if flagp(l,'nospur)
                                and null v1
                                and null v3
                                and null cdr v2
                             then mkf(mkgamf(append(reverse w,u),l),
                                      multfs(n,mkepsf z))
                            else multd!*(n,
                                         isimp1(spur0(
           l . (nil . append(reverse u,w)),nil,nil,delete(z,v2),v3),
                                                i,v1,list z,nil))
              else if z := memlis(car u,v3)
               then if ndims!*=4
                      then return spur0i(u,delete(car u,i),v1,v2,
                                         delete(z,v3),l,n,w,z)
                     else <<indices!* := delete(car u,indices!*);
                            i := delete(car u,i);
                            if not(car u memq defindices!*)
                              then defindices!* :=
                                    car u . defindices!*;
                            go to a1>>
              else rerror(hephys,9,list("Unmatched index",car u));
    a1:
      w := car u . w;
      u := cdr u;
      go to a;
    end1:
      if kahp
        then if ndims!*=4
               then <<z := multfs(n,kahane(reverse w,i1,l));
                      return isimp1(z,setdiff(i,i1),v1,v2,v3)>>
              else z := spurdim(w,i,l,nil,1)
       else z := spurr(w,l,nil,1);
      return if null z then nil
              else if get('eps,'klist) and not flagp(l,'nospur)
               then isimp1(multfs(n,z),i,v1,v2,v3)
              else multfs(z,isimp1(n,i,v1,v2,v3))
   end;

symbolic procedure spur0i(u,i,v1,v2,v3,l,n,w,z);
   begin scalar kahp,i1;
      if flagp(l,'nospur) and flagp(car z,'nospur)
        then rerror(hephys,10,
                    "NOSPUR on more than one line not implemented")
       else if flagp(car z,'nospur) then kahp := car z;
      z := cdr z;
      i1 := car z;
      z := reverse cdr z;
      if i1 then z := 'a . z;
      i1 := nil;
      <<while null (car u eq car z) do
           <<i1 := car z . i1; z := cdr z>>;
        z := cdr z;
        u := cdr u;
        if flagp(l,'nospur)
          then <<w := w . (u . (i1 . z));
                 i1 := car w;
                 z := cadr w;
                 u := caddr w;
                 w := cdddr w>>;
        w := reverse w;
        if null ((null u or not eqcar(w,'a)) and (u := append(u,w)))
          then <<if not hevenp u then n :=  - n;
                 u := 'a . append(u,cdr w)>>;
        if kahp then l := kahp;
        z :=
         mkf(mkg(reverse i1,l),
             multf(brace(u,l,i),multfs(n,mkg1(z,l))));
        z := isimp1(z,i,v1,v2,v3);
        if null z or (z := quotf(z,2)) then return z
         else errach list('spur0,n,i,v1,v2,v3)>>
   end;

symbolic procedure spurdim(u,i,l,v,n);
   begin scalar w,x,y,z,z1; integer m;
    a:  if null u
          then return if null v then n
                else if flagp(l,'nospur) then multfs(n,mkgamf(v,l))
                else multfs(n,sprgen v)
         else if not(car u memq cdr u)
          then <<v := car u . v; u := cdr u; go to a>>;
        x := car u;
        y := cdr u;
        w := y;
        m := 1;
    b:  if x memq i then go to d
         else if not(x eq car w) then go to c
         else if null(w := mkdot(x,x)) then return z;
        if x memq i then w := ndims!*;
        return addfs(mkf(w,spurdim(delete(x,y),i,l,v,n)),z);
    c:  z1 := mkdot(x,car w);
        if car w memq i
          then z := addfs(spurdim(subst(x,car w,remove(y,m)),
                                  i,l,v,2*n),z)
         else if z1
          then z := addfs(mkf(z1,spurdim(remove(y,m),i,l,v,2*n)),z);
        w := cdr w;
        n := -n;
        m := m+1;
        go to b;
   d:   while not(x eq car w) do
         <<z:= addfs(spurdim(subst(car w,x,remove(y,m)),i,l,v,2*n),z);
           w := cdr w;
           n := -n;
           m := m+1>>;
        return addfs(mkf(ndims!*,spurdim(delete(x,y),i,l,v,n)),z)
   end;

symbolic procedure appn(u,n);
   if n=1 then u else append(u,appn(u,n-1));

symbolic procedure other(u,v);
   if u eq car v then cdr v else car v;

symbolic procedure kahane(u,i,l);
   % The Kahane algorithm for Dirac matrix string reduction.
   % Ref: Kahane, J., Journ. Math. Phys. 9 (1968) 1732-1738.
   begin scalar p,r,v,w,x,y,z; integer k,m;
        k := 0;
%   mark:
        if eqcar(u,'a) then go to a1;
    a:  p := not p;             % Vector parity.
        if null u then go to d else if car u member i then go to c;
    a1: w := aconc!*(w,car u);
    b:  u := cdr u;
        go to a;
    c:  y := car u . p;
        z := (x . (y . w)) . z;
        x := y;
        w := nil;
        k := k+1;
        go to b;
    d:  z := (nil . (x . w)) . z;
        % Beware ... end of string has opposite convention.
%   pass2:
        m := 1;
    l1: if null z then go to l9;
        u := caar z;
        x := cadar z;
        w := cddar z;
        z := cdr z;
        m := m+1;
        if null u then go to l2
         else if (car u eq car x) and exc(x,cdr u) then go to l7;
        w := reverse w;
        r := t;
    l2: p := not exc(x,r);
        x := car x;
        y := nil;
    l3: if null z
          then rerror(hephys,11,"Unmatched index" .
                           if y then if not atom cadar y then cadar y
                                   else if not atom caar y then caar y
                                      else nil
                             else nil)
          else if (x eq car (i := cadar z)) and not exc(i,p)
           then go to l5
          else if (x eq car (i := caar z)) and exc(i,p) then go to l4;
        y := car z . y;
        z := cdr z;
        go to l3;
    l4: x := cadar z;
        w := appr(cddar z,w);
        r := t;
        go to l6;
    l5: x := caar z;
        w := append(cddar z,w);
        r := nil;
    l6: z := appr(y,cdr z);
        if null x then go to l8
         else if not eqcar(u,car x) then go to l2;
    l7: if w and cdr u then w := aconc!*(cdr w,car w);
        v := multfs(brace(w,l,nil),v);  % v := ('brace . l . w) . v;
        go to l1;
    l8: v := mkg(w,l);                  % v := list('g . l . w);
        z := reverse z;
        k := k/2;
        go to l1;
    l9: u := 2**k;
        if not evenp(k-m) then u := - u;
        return multd!*(u,v)             % return 'times . u . v;
   end;

symbolic procedure appr(u,v);
   if null u then v else appr(cdr u,car u . v);

symbolic procedure exc(u,v);
   if null cdr u then v else not v;

symbolic procedure brace(u,l,i);
   if null u then 2
    else if xnp(i,u) or flagp(l,'nospur)
     then addf(mkg1(u,l),mkg1(reverse u,l))
    else if car u eq 'a
       then if hevenp u then addfs(mkg(u,l),
                                 negf mkg('a . reverse cdr u,l))
             else mkf(mka l,spr2(cdr u,l,2,nil))
    else if hevenp u then spr2(u,l,2,nil)
    else spr1(u,l,2,nil);

symbolic procedure spr1(u,l,n,b);
   if null u then nil
    else if null cdr u then multd!*(n,mkg1(u,l))
    else begin scalar m,x,z;
               x := u;
               m := 1;
          a:   if null x then return z;
               z:= addfs(mkf(mkg1(list car x,l),
                              if null b then spurr(remove(u,m),l,nil,n)
                               else spr1(remove(u,m),l,n,nil)),
                         z);
               x := cdr x;
               n :=  - n;
               m := m+1;
               go to a
    end;

symbolic procedure spr2(u,l,n,b);
   if null cddr u and null b then multd!*(n,mkdot(car u,cadr u))
    else (lambda x; if b then addfs(spr1(u,l,n,b),x) else x)
       addfs(spurr(u,l,nil,n),
             mkf(mka l,spurr(append(u,list 'a),l,nil,n)));

symbolic procedure hevenp u;
   null u or not hevenp cdr u;

symbolic procedure bassoc(u,v);
   if null v then nil
    else if u eq caar v or u eq cdar v then car v
    else bassoc(u,cdr v);

symbolic procedure memlis(u,v);
   if null v then nil
    else if u member car v then car v
    else memlis(u,cdr v);

symbolic procedure spurr(u,l,v,n);
   begin scalar w,x,y,z,z1; integer m;
    a:  if null u then go to b
         else if car u member cdr u then go to g;
        v := car u . v;
        u := cdr u;
        go to a;
    b:  return if null v then n
         else if flagp(l,'nospur) then multd!*(n,mkgamf(v,l))
         else multd!*(n,sprgen v);
    g:  x := car u;
        y := cdr u;
        w := y;
        m := 1;
    h:  if not(x eq car w) then go to h1
         else if null(w:= mkdot(x,x)) then return z
         else return addfs(mkf(w,spurr(delete(x,y),l,v,n)),z);
    h1: z1 := mkdot(x,car w);
        if z1 then z:= addfs(mkf(z1,spurr(remove(y,m),l,v,2*n)),z);
        w := cdr w;
        n :=  - n;
        m := m+1;
        go to h
   end;

symbolic procedure sprgen v;
   begin scalar x,y,z;
        if not (car v eq 'a) then return sprgen1(v,t)
         else if null (x := comb(v := cdr v,4)) then return nil
         else if null cdr x then go to e;
    c:  if null x then return multpf('i to 1,z);
        y := mkepsf car x;
        if asign(car x,v,1)=-1 then y := negf y;
        z := addf(multf(y,sprgen1(setdiff(v,car x),t)),z);
    d:  x := cdr x;
        go to c;
    e:  z := mkepsf car x;
        go to d
   end;

symbolic procedure asign(u,v,n);
   if null u then n else asign(cdr u,v,asign1(car u,v,-1)*n);

symbolic procedure asign1(u,v,n);
   if u eq car v then n else asign1(u,cdr v,-n);

symbolic procedure sprgen1(u,b);
   if null u then nil
    else if null cddr u then (lambda x; if b then x else negf x)
                                mkdot(car u,cadr u)
    else begin scalar w,x,y,z;
               x := car u;
               u := cdr u;
               y := u;
          a:   if null u then return z
                else if null(w:= mkdot(x,car u)) then go to c;
               z := addf(multf(w,sprgen1(delete(car u,y),b)),z);
          c:   b := not b;
               u := cdr u;
               go to a
    end;

% ****************** FUNCTIONS FOR EPSILON ALGEBRA ******************


put('eps,'simpfn,'simpeps);

symbolic procedure mkepsf u;
   (lambda x; (lambda y; if null car x then negf y else y) mksf cdr x)
        mkepsk u;

symbolic procedure esum(u,i,v,w,x);
   begin scalar y,z,z1;
      z := car u;
      u := cdr u;
      if cdr z neq 1
       then u := multf(exptf(mkepsf cdar z,cdr z-1),u);
      z := cdar z;
  a:  if repeats z then return nil;
  b:  if null z then return isimp1(u,i,v,reverse y . w,x)
       else if car z member i
        then <<if z1 := bassoc(car z,v)
                 then <<v := delete(z1,v);
                        i := delete(car z,i);
                        z := append(reverse y,other(car z,z1) . cdr z);
                        y := nil;
                        go to a>>
                else if z1 := memlis(car z,w)
                 then <<z := append(reverse y,z);
                        y := intersection(i,intersection(z,z1));
                        return isimp1(multfs(emult1(z1,z,y),u),
                                      setdiff(i,y),
                                      v,
                                      delete(z1,w),
                                      x)>>>>;
      y := car z . y;
      z := cdr z;
      go to b
   end;

symbolic procedure emult u;
   if null cdr u then mkepsf car u
    else if null cddr u then emult1(car u,cadr u,nil)
    else multfs(emult1(car u,cadr u,nil),emult cddr u);

symbolic procedure emult1(u,v,i);
   (lambda (x,y);
         (lambda (m,n);
               if m=4 then 24*n
                else if m=3 then multd(6*n,mkdot(car x,car y))
                else multd!*(n*(if m = 0 then 1 else m),
                           car detq maplist(x,
                             function (lambda k;
                               maplist(y,
                                 function (lambda j;
                                   mkdot(car k,car j) . 1))))))
            (length i,
             (lambda j; nb if permp(u,append(i,x)) then not j else j)
                permp(v,append(i,y))))
      (setdiff(u,i),setdiff(v,i));

endmodule;

end;
