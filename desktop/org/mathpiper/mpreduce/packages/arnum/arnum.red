module arnum;  % Support for algebraic rationals.

% Author: Eberhard Schruefer.

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


create!-package('(arnum arinv),'(contrib arnum));

fluid '(!*bezout);

global '(domainlist!* arbase!* arvars!* repowl!* curdefpol!*
     !*acounter!* !*extvar!* reexpressl!*);

!*acounter!* := 0;    %counter for number of extensions;

!*bezout := t;

!*extvar!* := 'a;     %default print character for primitive element;

fluid '(!*arnum dmode!* !*exp !*chk!-reducibility !*reexpress
        !*arinv !*arquot !*arq alglist!*);

global '(timer timef);

switch arnum; % chk!-reducibility;

timer:=timef:=0;

domainlist!*:=union('(!:ar!:),domainlist!*);

% Definition of DEFPOLY changed by F. Kako.

symbolic procedure defpoly u;
   begin
     if null(dmode!* eq '!:ar!:) then on 'arnum;
     for each j in u do
         (if eqexpr j then
          if cadr j=0 then defpoly1 caddr j
           else if caddr j=0 then defpoly1 cadr j
           else rerror(arnum,1,list(cadr j,"=",caddr j,
              "  is not a proper defining polynomial"))
          else defpoly1 j)
   end;

symbolic procedure defpoly1 u;
   begin scalar x,alglist!*;
      x := aeval u;
      if x = 0 then mkextension u else mkextension x
   end;

rlistat '(defpoly);

symbolic procedure mkextension u;
   if null curdefpol!* then initalgnum u
    else begin scalar !*exp;
        !*exp := t;
        primitive!_elem !*a2f u
     end;

symbolic procedure initalgnum u;
   begin scalar dmode!*,alglist!*,!*exp,x;
     !*exp := t;
     arbase!* := nil;
     u := numr simp0 u;
     if x := not!_in!_extension u then u := x
      else return;
     if lc u neq 1 then u := monicize u;
     % rederr("defining polynomial must be monic");
     curdefpol!* := u;
     for j:=0:(ldeg u-1) do
         arbase!* := (if j=0 then 1
                       else mksp(mvar u,j)) . arbase!*;
     arvars!* := mvar u . arvars!*;
     mk!-algebraic!-number!-vars list mvar u;
     repowl!* := lpow u . negf red u
   end;

symbolic procedure put!-current!-representation(u,v);
   put(u,'currep,v);

symbolic procedure get!-current!-representation u;
   get(u,'currep);

symbolic procedure mkdar u;
   %puts any algebraic number domain element into its tagged form.
   %updated representations (through field extension) are accessed here;
   ((if x then x else '!:ar!: . !*k2f u) ./ 1)
    where x = get!-current!-representation u;

symbolic procedure release u;
   %Undeclares elements of list u to be algebraic numbers;
   for each j in u do
     if atom j then remprop(j,'idvalfn)
      else clear1 {!*a2k j};

symbolic procedure mk!-algebraic!-number!-vars u;
   %Declares elements of list u to be algebraic numbers;
   for each j in u do
     if atom j then put(j,'idvalfn,'mkdar)
      else setk(!*a2k j,mk!*sq mkdar j);

symbolic procedure uncurrep u;
   for each j in u do remprop(j,'currep);

symbolic procedure update!-extension u;
   %Updates representation of elements in list u;
    for each j in u do
       ((x and put(j,'currep,numr simp prepf cdr x))
      where x = get(j,'currep));

symbolic procedure express!-in!-arvars u;
   %u is an untagged rational number. Result is equivalent algebraic
   %number expressed in input variables.
   rerror(arnum,2,"Switch reexpress not yet implemented");
%  begin scalar x;
%    for each j in reexpressl!* do
%        x := extmult(extadd(...,j),x);
%    return solve!-for!-arvars x
%  end;

symbolic procedure mkreexpressl;
   %Sets up the homogenous part of the system to be solved for
   %expressing a primitive element expression in terms of the
   %input variables.
   reexpressl!* := nil;
%  begin scalar x;
%


put('reexpress,'simpfg,'((t (mkreexpressl))
             (nil (setq reexpressl!* nil))));

%*** tables for algebraic rationals ***;

flag('(!:ar!:),'field);
put('arnum,'tag,'!:ar!:);
put('!:ar!:,'dname,'arnum);
put('!:ar!:,'i2d,'!*i2ar);
%put('!:ar!:,'!:rn!:,'ar2rn);
put('!:ar!:,'!:rd!:,'arconv);
put('!:ar!;,'!:cr!:,'arconv);
put('!:ar!:,'!:mod!:,'arconv);
put('!:ar!:,'minusp,'arminusp!:);
put('!:ar!:,'zerop,'arzerop!:);
put('!:ar!:,'onep,'aronep!:);
put('!:ar!:,'plus,'arplus!:);
put('!:ar!:,'difference,'ardifference!:);
put('!:ar!:,'times,'artimes!:);
put('!:ar!:,'quotient,'arquotient!:);
put('!:ar!:,'factorfn,'arfactor!:);
put('!:ar!:,'rationalizefn,'arrationalize!:);
put('!:ar!:,'prepfn,'arprep!:);
put('!:ar!:,'intequivfn,'arintequiv!:);
put('!:ar!:,'prifn,'arprn!:);
put('!:rn!:,'!:ar!:,'rn2ar);
flag('(!:ar!:),'ratmode);

symbolic procedure rn2ar u;
   '!:ar!: . if cddr u=1 then cadr u else u;

symbolic procedure ar2rn u;
   if cadr u eq '!:rn!: then cdr u
    else if numberp cdr u then '!:rn!: . (cdr u . 1)
    else rerror(arnum,3,list "Conversion to rational not possible");

symbolic procedure !*i2ar u;
   '!:ar!: . u;

symbolic procedure arconv u;
   rerror(arnum,4,list("Conversion between current extension and",
               get(car u,'dname),"not possible"));


symbolic procedure arminusp!: u;
   minusf cdr u;

symbolic procedure arzerop!: u;
   null cdr u;

symbolic procedure aronep!: u;
   cdr u=1;

symbolic procedure arintequiv!: u;
   if numberp cdr u then cdr u
    else if (cadr u eq '!:rn!:) and (cdddr u=1) then caddr u
    else nil;

smacro procedure mkar u;
 '!:ar!: . u;

symbolic procedure arplus!:(u,v);
   begin scalar dmode!*,!*exp;
     !*exp := t;
     return mkar addf(cdr u,cdr v)
   end;

symbolic procedure ardifference!:(u,v);
   begin scalar dmode!*,!*exp;
     !*exp := t;
     return mkar addf(cdr u,negf cdr v)
   end;

symbolic procedure artimes!:(u,v);
   begin scalar dmode!*,!*exp;
     !*exp := t;
     return mkar reducepowers multf(cdr u,cdr v)
   end;

symbolic procedure arquotient!:(u,v);
   begin scalar r,s,y,z,dmode!*,!*exp;
     !*exp := t;
     if domainp cdr v then
          return mkar multd(<<dmode!* := '!:rn!:;
                              s := !:recip cdr v;
                              dmode!* := nil;
                              s>>,cdr u);
     if !*arinv then
    return mkar reducepowers multf(cdr u,arinv cdr v);
     if !*arquot then return mkar arquot(cdr v,cdr u);
     if !*arq then return mkar reducepowers multf(u,arquot1 v);
     r := ilnrsolve(mkqmatr cdr v,mkqcol cdr u);
     z := arbase!*;
     dmode!* := '!:rn!:;
     for each j in r do
         s := addf(multf(int!-equiv!-chk car j,
                       <<y := if atom car z then 1 else !*p2f car z;
                         z := cdr z; y>>),s);
     return mkar s
    end;

symbolic procedure arfactor!: v;
   if domainp v then list v
    else if null curdefpol!* then factorf v
    else
   begin scalar w,x,y,z,aftrs,ifctr,ftrs,mva,mvu,
         dmode!*,!*exp;
     timer:=timef:=0;
     !*exp := t;
     mva := mvar curdefpol!*;
     mvu := mvar v;
     ifctr := factorft numr(v := fd2q v);
     dmode!* := '!:ar!:;
     w := if denr v neq 1 then mkrn(car ifctr,denr v)
           else car ifctr;
     for each f in cdr ifctr do
         begin scalar l;
           y := numr subf1(car f,nil);
           if domainp y then <<w := multd(y,w); return>>;
           y := sqfrnorm y;
           dmode!* := nil;
           ftrs := factorft car y;
           dmode!* := '!:ar!:;
           if cadr y neq 0 then
              l := list(mvu . prepf addf(!*k2f mvu,
                                      negf multd(cadr y,!*k2f mva)));
           y := cddr y;
           for each j in cdr ftrs do
              <<x := gcdf!*(car j,y);
                y := quotf!*(y,x);
                z := if l then numr subf1(x,l) else x;
                x := lnc ckrn z;
                z := quotf(z,x);
                w := multf(w,exptf(x,cdr f));
                aftrs := (z . cdr f) . aftrs>>
         end;
      %print timer; print timef;
      return w . sort!-factors aftrs
    end;

symbolic procedure afactorize u;
   begin scalar ftrs,x,!*exp; integer n;
     !*exp := t;
     if cdr u then <<off 'arnum; defpoly cdr u>>;
     x := arfactor!: !*a2f car u;
     ftrs := (0 . mk!*sq(car x ./ 1)) . nil;
     for each j in cdr x do
       for k := 1:cdr j do
           ftrs := ((n := n+1) . mk!*sq(car j ./ 1)) . ftrs;
     return multiple!-result(ftrs,nil)
   end;


put('algeb!_factorize,'psopfn,'afactorize);

symbolic procedure arprep!: u;                         %u;
   prepf if !*reexpress then express!-in!-arvars cdr u
      else cdr u;

%symbolic procedure simpar u;
%('!:ar!: . !*a2f car u) ./ 1;

%put('!:ar!:,'simpfn,'simpar);


symbolic procedure arprn!: v;
   ( if atom u or (car u memq '(times expt)) then maprin u
     else <<prin2!* "(";
            maprin u;
        prin2!* ")" >>) where u = prepsq!*(cdr v ./ 1);


%*** utility functions ***;

symbolic procedure monicize u;
   %makes standard form u monic by the appropriate variable subst.;
   begin scalar a,mvu,x;
         integer n;
     x := lc u;
     mvu := mvar u;
     n := ldeg u;
     !*acounter!* := !*acounter!* + 1;
     a := intern compress append(explode !*extvar!*,
                 explode !*acounter!*);
     u := multsq(subf(u,list(mvu . list('quotient,a,x))),
                 x**(n-1) ./ 1);
     mk!-algebraic!-number!-vars list mvu;
     put!-current!-representation(mvu,
                  mkar(a to 1 .* ('!:rn!: . 1 . x)
                       .+ nil));
     terpri();
     prin2 "defining polynomial has been monicized";
     terpri();
     maprin prepsq u;
     terpri!* t;
     return !*q2f u
   end;


symbolic procedure polynorm u;
   begin scalar dmode!*,x,y;
         integer n;
     n := ldeg curdefpol!*;
     x := fd2q u;
     y := resultantft(curdefpol!*,numr x,mvar curdefpol!*);
     dmode!* := '!:ar!:;
     return if denr x = 1 then y
         else !*q2f multsq(y ./ 1,1 ./ (denr x)**n)
   end;

symbolic procedure resultantft(u,v,w);
   resultant(u,v,w);

symbolic procedure factorft u;
   begin scalar dmode!*; return fctrf u end;

symbolic procedure fd2q u;
   %converts a s.f. over ar to a s.q. over the integers;
   if atom u then u ./ 1
    else if car u eq '!:ar!: then fd2q cdr u
    else if car u eq '!:rn!: then cdr u
    else addsq(multsq(!*p2q lpow u,fd2q lc u),fd2q red u);

symbolic procedure sqfrnorm u;
   begin scalar l,norm,y; integer s;
     y := u;
     if algebnp u then go to b;
     a: s := s-1;
        l := list(mvar u . prepf
          addf(!*k2f mvar u,multd(s,!*k2f mvar curdefpol!*)));
        y := numr subf1(u,l);
        if null algebnp y then go to a;
     b: norm := polynorm y;
        if not ar!-sqfrp norm then go to a;
     return norm . (s . y)
   end;

symbolic procedure algebnp u;
   if atom u then nil
    else if car u eq '!:ar!: then t
    else if domainp u then nil
    else algebnp lc u or algebnp red u;

symbolic procedure ar!-sqfrp u;
   % This is same as sqfrp in gint module.
   domainp gcdf!*(u,diff(u,mvar u));

symbolic procedure primitive!_elem u;
   begin scalar a,x,y,z,newu,newdefpoly,olddefpoly;
     if x := not!_in!_extension u then u := x
      else return;
     !*acounter!* := !*acounter!* + 1;
     a := intern compress append(explode !*extvar!*,
                 explode !*acounter!*);
     x := sqfrnorm u;
     newdefpoly := !*q2f subf(car x,list(mvar car x . a));
     olddefpoly := curdefpol!*;
     newu := !*q2f subf(cddr x,list(mvar car x . a));
     rmsubs();
     release arvars!*;
     begin scalar !*chk!-reducibility;
       initalgnum prepf newdefpoly end;
     y := gcdf!*(numr simp prepf newu,olddefpoly);
     arvars!* := mvar car x . arvars!*;
     mk!-algebraic!-number!-vars arvars!*;
     put!-current!-representation(mvar olddefpoly,
                  z := quotf!*(negf red y,lc y));
     put!-current!-representation(mvar car x,
                  addf(mkar !*k2f a,
                       multf(!*n2f cadr x,z)));
     rmsubs();
     update!-extension arvars!*;
     terpri!* t;
     prin2!* "*** Defining polynomial for primitive element:";
     terpri!* t;
     maprin prepf curdefpol!*;
     terpri!* t
   end;

symbolic procedure not!_in!_extension u;
   %We still need a criterion which branch to choose;
   %Isolating intervals would do;
   begin scalar ndp,x; integer cld;
     if null !*chk!-reducibility then return u;
     cld := ldeg u;
     ndp := u;
     x := if curdefpol!* then arfactor!: u
           else factorf u;
     for each j in cdr x do
         if ldeg car j < cld then
            <<ndp := car j;
              cld := ldeg ndp>>;
     if cld=1 then <<mk!-algebraic!-number!-vars list mvar u;
                     arvars!* := mvar u . arvars!*;
                     put!-current!-representation(mvar u,
                               quotf!*(negf red ndp,lc ndp));
                     return nil>>
      else return ndp
   end;

symbolic procedure split!_field1(u,v);
   % Determines the minimal splitting field for u.
   begin scalar a,ftrs,mvu,q,x,y,z,roots,bpoly,minpoly,newminpoly,
                polys,newfactors,dmode!*,!*exp,!*chk!-reducibility;
         integer indx,lcu,k,n,new!_s;
    off 'arnum;  %crude way to clear previous extensions;
    !*exp := t;
    u := !*q2f simp!* u;
    mvu := mvar u;
    lcu := lc u;
    if lcu neq 1
       then u := !*q2f multsq(subf(u,list(mvu .
                                          list('quotient,mvu,lcu))),
                              lcu**(ldeg u - 1) ./ 1);
    indx := 1;
    polys := (1 . u) . polys;
    !*acounter!* := !*acounter!* + 1;
    a := intern compress append(explode !*extvar!*,
            explode !*acounter!*);
    minpoly := newminpoly := numr subf(u,list(mvu . a));
    dmode!* := '!:ar!:;
    mkextension prepf minpoly;
    roots := mkar !*k2f  a . roots;
     b: polys := for each j in polys collect
            if indx=car j then
               car j . quotf!*(cdr j,
                    addf(!*k2f mvu,negf car roots))
             else j;
        k := 1;
        indx := 0;
        for each j in polys do
            begin scalar l;
              x := sqfrnorm cdr j;
              if cadr x neq 0 then
                 l := list(mvu . prepf addf(!*k2f mvu,
                                         negf multd(cadr x,!*k2f a)));
              z := cddr x;
              dmode!* := nil;
              ftrs := cdr factorf car x;
              dmode!* := '!:ar!:;
              for each qq in ftrs do
                <<y := gcdf!*(z,q:=car qq);
                  if ldeg q > ldeg newminpoly then
                     <<newminpoly := q;
                       new!_s := cadr x;
                       indx := k;
                       bpoly := y>>;
                  z := quotf!*(z,y);
                  if l then y := numr subf(y,l);
                  if ldeg y=1 then
                     roots := quotf(negf red y,lc y) . roots
                   else <<newfactors:=(k . y) . newfactors;
                          k:=k+1>>>>
            end;
        if null newfactors then
       <<terpri();
         prin2t "*** Splitting field is generated by:";
         terpri();
         maprin prepf newminpoly;
         terpri!* t;
             n := length roots;
             return multiple!-result(
                      for each j in roots collect
                        (n := n-1) . mk!*sq(cancel(j ./ lcu)),v)>>;
    !*acounter!* := !*acounter!* + 1;
    a := intern compress append(explode !*extvar!*,
                    explode !*acounter!*);
        newminpoly := numr subf(newminpoly,list(mvu . a));
        bpoly := numr subf(bpoly,list(mvu . a));
        rmsubs();
        release arvars!*;
        initalgnum prepf newminpoly;
        x := gcdf!*(minpoly,numr simp prepf bpoly);
    mk!-algebraic!-number!-vars arvars!*;
    put!-current!-representation(mvar minpoly,
                     z := quotf!*(negf red x,lc x));
        rmsubs();
        roots := addf(mkar !*k2f a,multf(!*n2f new!_s,z)) .
                      for each j in roots collect numr subf(cdr j,nil);
        polys := for each j in newfactors collect
                     car j . numr simp prepf cdr j;
        newfactors := nil;
        minpoly := newminpoly;
        go to b
  end;

symbolic procedure split!-field!-eval u;
   begin scalar x;
     if length u > 2
       then rerror(arnum,5,
                  "Split!_field called with wrong number of arguments");
     x := split!_field1(car u,if cdr u then cadr u else nil);
     dmode!* := '!:ar!:;
     %The above is necessary for working with the results.
     return x
  end;

put('split!_field,'psopfn,'split!-field!-eval);

symbolic procedure arrationalize!: u;
   %We should actually factorize the denominator first to
   %make sure that the result is in lowest terms. ????
   begin scalar x,y,z,dmode!*;
     if domainp denr u then return quotf(numr u,denr u) ./ 1;
     if null algebnp denr u then return u;
     x := polynorm numr fd2q denr u;
     y := multsq(fd2q multf(numr u,quotf!*(x,denr u)),1 ./ x);
     dmode!* := '!:ar!:;
     x := numr subf(denr y,nil);
     y := numr subf(numr y,nil);
     z := lnc x;
     return quotf(y,z) ./ quotf(x,z)
   end;

%put('rationalize,'simpfn,'rationalize); its now activated by a switch.
put('polynorm,'polyfn,'polynorm);

%*** support functions ***;

comment the function ilnrsolve and others are identical to the
    %ones in matr except they work only on integers here;
        %there should be better algorithms;


symbolic procedure reducepowers u;
   %reduces powers with the help of the defining polynomial;
   if domainp u or (ldeg u<pdeg car repowl!*) then u
    else if ldeg u=pdeg car repowl!* then
             addf(multf(cdr repowl!*,lc u),red u)
    else reducepowers
     addf(multf(multpf(mvar u .** (ldeg u-pdeg car repowl!*),lc u),
              cdr repowl!*),red u);

symbolic procedure mkqmatr u;
   %u is an ar domainelement, result is a matrix form which
   %needs to be inverted for calculating the inverse of ar;
   begin scalar r,x,v,w;
     v := mkqcol u;
     for each k in cdr reverse arbase!* do
       <<w := reducepowers multpf(k,u);
         v := for each j in arbase!* collect
                <<r := ((if atom j then ratn w
                          else if domainp w then 0 . 1
                          else if j=lpow w then
                                  <<x:=ratn lc w; w:=cdr w; x>>
                          else 0 . 1) . car v);
                  v := cdr v;
                  r>>>>;
     return v
   end;

symbolic procedure mkqcol u;
   %u is an ar domainelement result is a matrix form
   %representing u as a coefficient matrix of the ar base;
   begin scalar x,v;
     v := for each j in arbase!* collect
             if atom j then list ratn u
              else if domainp u then list(0 . 1)
              else if j=lpow u then <<x:=list ratn lc u; u:=cdr u; x>>
               else list(0 . 1);
     return v
   end;

symbolic procedure ratn u;
   if null u then 0 . 1
    else if atom u then u . 1
    else if car u eq '!:rn!: then cdr u
    else rerror(arnum,6,"Illegal domain in :ar:");

symbolic procedure inormmat u;
   begin integer y; scalar z;
%    x := 1;
     for each v in u do
       <<y := 1;
         for each w in v do y := ilcm(y,denr w);
         z := (for each w in v
                 collect numr w*y/denr w) . z>>;
     return reverse z
   end;

symbolic procedure ilcm(u,v);
   if u=0 or v=0 then 0
    else if u=1 then v
    else if v=1 then u
    else u*v/gcdn(u,v);

symbolic procedure ilnrsolve(u,v);
   %u is a matrix standard form, v a compatible matrix form;
   %value is u**(-1)*v;
   begin integer n;
     n := length u;
     v := ibacksub(ibareiss inormmat ar!-augment(u,v),n);
     u := ar!-rhside(car v,n);
     v := cdr v;
    return for each j in u collect
              for each k in j collect mkrn(k,v)
    end;

symbolic procedure ar!-augment(u,v);
   % Same as augment in bareiss module.
   if null u then nil
    else append(car u,car v) . ar!-augment(cdr u,cdr v);


symbolic procedure ar!-rhside(u,m);
   % Same as rhside in bareiss module.
   if null u then nil else pnth(car u,m+1) . ar!-rhside(cdr u,m);

 symbolic procedure ibareiss u;
   %as in matr but only for integers;
   begin scalar ik1,ij,kk1,kj,k1j,k1k1,ui,u1,x;
    integer k,k1,aa,c0,ci1,ci2;
    aa:= 1;
    k:= 2;
    k1:=1;
    u1:=u;
    go to pivot;
    agn: u1 := cdr u1;
    if null cdr u1 or null cddr u1 then return u;
    aa:=nth(car u1,k);              %aa := u(k,k);
    k:=k+2;
    k1:=k-1;
    u1:=cdr u1;
    pivot:  %pivot algorithm;
    k1j:= k1k1 := pnth(car u1,k1);
    if car k1k1 neq 0 then go to l2;
    ui:= cdr u1;                    %i := k;
    l:   if null ui then return nil
     else if car(ij := pnth(car ui,k1))=0
      then go to l1;
    l0:  if null ij then go to l2;
    x:= car ij;
    rplaca(ij,-car k1j);
    rplaca(k1j,x);
    ij:= cdr ij;
    k1j:= cdr k1j;
    go to l0;
    l1:  ui:= cdr ui;
    go to l;
    l2:  ui:= cdr u1;                    %i:= k;
    l21: if null ui then return; %if i>m then return;
    ij:= pnth(car ui,k1);
    c0:= car k1k1*cadr ij-cadr k1k1*car ij;
    if c0 neq 0 then go to l3;
    ui:= cdr ui;                    %i:= i+1;
    go to l21;
    l3:  c0:= c0/aa;
    kk1 := kj := pnth(cadr u1,k1);  %kk1 := u(k,k-1);
    if cdr u1 and null cddr u1 then go to ev0
     else if ui eq cdr u1 then go to comp;
    l31: if null ij then go to comp;     %if i>n then go to comp;
    x:= car ij;
    rplaca(ij,-car kj);
    rplaca(kj,x);
    ij:= cdr ij;
    kj:= cdr kj;
    go to l31;
    %pivoting complete;
     comp:
    if null cdr u1 then go to ev;
    ui:= cddr u1;                   %i:= k+1;
     comp1:
    if null ui then go to ev;       %if i>m then go to ev;
    ik1:= pnth(car ui,k1);
    ci1:= (cadr k1k1*car ik1-car k1k1*cadr ik1)/aa;
    ci2:= (car kk1*cadr ik1-cadr kk1*car ik1)/aa;
    if null cddr k1k1 then go to comp3;%if j>n then go to comp3;
    ij:= cddr ik1;                  %j:= k+1;
    kj:= cddr kk1;
    k1j:= cddr k1k1;
     comp2:
    if null ij then go to comp3;
    rplaca(ij,(car ij*c0+car kj*ci1+car k1j*ci2)/aa);
    ij:= cdr ij;
    kj:= cdr kj;
    k1j:= cdr k1j;
    go to comp2;
     comp3:
    ui:= cdr ui;
    go to comp1;
     ev0:if c0=0 then return;
     ev: kj := cdr kk1;
    x := cddr k1k1;                 %x := u(k-1,k+1);
    rplaca(kj,c0);
     ev1:kj:= cdr kj;
    if null kj then go to agn;
    rplaca(kj,(car k1k1*car kj-car kk1*car x)/aa);
    x := cdr x;
    go to ev1
    end;

 symbolic procedure ibacksub(u,m);
    begin scalar ij,ijj,ri,uj,ur; integer i,jj,summ,detm,det1;
    %n in comments is number of columns in u;
    if null u then rerror(arnum,7,"Singular matrix");
    ur := reverse u;
    detm := car pnth(car ur,m);             %detm := u(i,j);
    if detm=0 then rerror(arnum,8,"Singular matrix");
    i := m;
     rows:
    i := i-1;
    ur := cdr ur;
    if null ur then return u . detm;
         %if i=0 then return u . detm;
    ri := car ur;
    jj := m+1;
    ijj:=pnth(ri,jj);
     r2: if null ijj then go to rows;    %if jj>n then go to rows;
    ij := pnth(ri,i);               %j := i;
    det1 := car ij;                 %det1 := u(i,i);
    uj := pnth(u,i);
    summ := 0;                      %summ := 0;
     r3: uj := cdr uj;                   %j := j+1;
    if null uj then go to r4;       %if j>m then go to r4;
    ij := cdr ij;
    summ := summ+car ij*nth(car uj,jj);
         %summ:=summ+u(i,j)*u(j,jj);
    go to r3;
     r4: rplaca(ijj,(detm*car ijj-summ)/det1);
         %u(i,j):=(detm*u(i,j)-summ)/det1;
    jj := jj+1;
    ijj := cdr ijj;
    go to r2
    end;

initdmode 'arnum;

put('arnum,'simpfg,
      '((t (setdmode (quote arnum) t))
    (nil (setdmode (quote arnum) nil) (release arvars!*)
         (uncurrep arvars!*) (setq curdefpol!* nil)
         (setq arvars!* nil))));

endmodule;

end;
