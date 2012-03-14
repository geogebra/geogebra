module TayBasic;

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


%*****************************************************************
%
%      Functions that implement the basic operations
%       on Taylor kernels
%
%*****************************************************************


exports addtaylor, addtaylor1, invtaylor, invtaylor1, makecoeffpairs,
        makecoeffs, makecoeffs0, multtaylor, multtaylor1,
        multtaylorsq, negtaylor, negtaylor1, quottaylor, quottaylor1$

imports

% from the REDUCE kernel:
        addsq, invsq, lastpair, mvar, multsq, negsq, neq, nth, numr,
        over, quotsq, reversip, subtrsq, union,

% from the header module:
        !*tay2q, common!-increment, get!-degree, invert!-powerlist,
        make!-Taylor!*, multintocoefflist, prune!-coefflist,
        smallest!-increment, subtr!-degrees, subs2coefflist, TayCfPl,
        TayCfSq, TayCoeffList, TayFlags, TayFlagsCombine, TayGetCoeff,
        Taylor!-kernel!-sq!-p, Taylor!:, TayMakeCoeff, TayOrig,
        TayTemplate, TayTpElVars, TpDegreeList, TpNextList,

% from module Tayintro:
        confusion, Taylor!-error, Taylor!-error!*,

% from module Tayutils:
        add!.comp!.tp!., add!-degrees, enter!-sorted, exceeds!-order,
        inv!.tp!., min2!-order, mult!.comp!.tp!., replace!-next,
        taydegree!-strict!<!=;


fluid '(!*taylorkeeporiginal)$

symbolic procedure multtaylorsq (tay, sq);
  %
  % multiplies the s.q. sq into the Taylor kernel tay.
  % value is a Taylor kernel.
  % no check for variable dependency is done!
  %
  if null tay or null numr sq then nil
   else make!-Taylor!* (
              multintocoefflist (TayCoeffList tay, sq),
              TayTemplate tay,
              if !*taylorkeeporiginal and TayOrig tay
                then multsq (sq, TayOrig tay)
               else nil,
              TayFlags tay)$


symbolic smacro procedure degree!-union (u, v);
  union (u, v)$ % works for the moment;

symbolic procedure addtaylor(u,v);
  %
  % u and v are two Taylor kernels
  % value is their sum, as a Taylor kernel
  %
  (if null tp then confusion 'addtaylor
    else addtaylor!*(u,v,car tp))
    where tp := add!.comp!.tp!.(u,v);


symbolic procedure addtaylor!-as!-sq(u,v);
   begin scalar tp;
     return
       if Taylor!-kernel!-sq!-p u and Taylor!-kernel!-sq!-p v and
          (tp := add!.comp!.tp!.(mvar numr u,mvar numr v))
         then !*tay2q addtaylor!*(mvar numr u,mvar numr v,car tp)
        else addsq(u,v)
   end;

symbolic procedure addtaylor!*(u,v,tp);
   make!-Taylor!*
           (cdr z,replace!-next(tp,car z),
            if !*taylorkeeporiginal and TayOrig u and TayOrig v
              then addsq(TayOrig u,TayOrig v)
             else nil,
            TayFlagsCombine(u,v))
       where z := addtaylor1(tp,TayCoeffList u,TayCoeffList v);

symbolic procedure addtaylor1(tmpl,l1,l2);
  %
  % Returns the coefflist that is the sum of coefflists l1, l2.
  %
  begin scalar cff,clist,tn,tp;
    tp := TpDegreeList tmpl;
    tn := TpNextList tmpl;
    %
    % The following is necessary to ensure that the rplacd below
    %  doesn't do any harm to the list in l1.
    %
    clist := for each cc in l1 join
               if not null numr TayCfSq cc
                 then if not exceeds!-order(tn,TayCfPl cc)
                        then {TayMakeCoeff(TayCfPl cc,TayCfSq cc)}
                       else <<tn := min2!-order(tn,tp,TayCfPl cc);nil>>;
    for each cc in l2 do
      if not null numr TayCfSq cc
        then if not exceeds!-order(tn,TayCfPl cc)
               then <<cff := assoc(TayCfPl cc,clist);
                      if null cff then clist := enter!-sorted(cc,clist)
                       else rplacd(cff,addsq(TayCfSq cff,TayCfSq cc))>>
              else tn := min2!-order(tn,tp,TayCfPl cc);
    return tn . subs2coefflist clist
  end;


symbolic procedure negtaylor u;
  make!-Taylor!* (
         negtaylor1 TayCoeffList u,
         TayTemplate u,
         if !*taylorkeeporiginal and TayOrig u
           then negsq TayOrig u else nil,
         TayFlags u)$

symbolic procedure negtaylor1 tcl;
  for each cc in tcl collect
    TayMakeCoeff (TayCfPl cc, negsq TayCfSq cc)$

symbolic procedure multtaylor(u,v);
  %
  % u and v are two Taylor kernels,
  % result is their product, as a Taylor kernel.
  %
  (if null tps then confusion 'multtaylor
    else multtaylor!*(u,v,tps))
   where tps := mult!.comp!.tp!.(u,v,nil);

symbolic procedure multtaylor!-as!-sq(u,v);
   begin scalar tps;
     return
       if Taylor!-kernel!-sq!-p u and Taylor!-kernel!-sq!-p v and
          (tps := mult!.comp!.tp!.(mvar numr u,mvar numr v,nil))
         then !*tay2q multtaylor!*(mvar numr u,mvar numr v,tps)
        else multsq(u,v)
   end;

symbolic procedure multtaylor!*(u,v,tps);
   make!-Taylor!*
        (cdr z,replace!-next(car tps,car z),
         if !*taylorkeeporiginal and TayOrig u and TayOrig v
           then multsq (TayOrig u, TayOrig v)
          else nil,
         TayFlagsCombine(u,v))
     where z := multtaylor1(car tps,TayCoeffList u,TayCoeffList v);

symbolic procedure multtaylor1(tmpl,l1,l2);
  %
  % Returns the coefflist that is the product of coefflists l1, l2,
  %  with respect to Taylor template tp.
  %
  begin scalar cff,pl,rlist,sq,tn,tp;
    tp := TpDegreeList tmpl;
    tn := TpNextList tmpl;
    for each cf1 in l1 do
      for each cf2 in l2 do <<
        pl := add!-degrees(TayCfPl cf1,TayCfPl cf2);
        if not exceeds!-order(tn,pl) then <<
          sq := multsq(TayCfSq cf1,TayCfSq cf2);
          if not null numr sq then <<
            cff := assoc(pl,rlist);
            if null cff
              then rlist := enter!-sorted(TayMakeCoeff(pl,sq),rlist)
             else rplacd(cff,addsq(TayCfSq cff,sq))>>>>
         else tn := min2!-order(tn,tp,pl)>>;
    return tn . subs2coefflist rlist
  end;

comment Implementation of Taylor division.
        We use the following algorithm:
        Suppose the numerator and denominator are of the form

                -----                    -----
                \          k             \          l
        f(x) =   >     a  x   ,  g(x) =   >     b  x   ,
                /       k                /       l
                -----                    -----
                k>=k0                    l>=l0

        respectively.  The quotient is supposed to be

                -----
                \          m
        h(x) =   >     c  x   .
                /       m
                -----
                m>=m0

        Clearly: m0 = k0 - l0.  This follows immediately from
        f(x) = g(x) * h(x) by comparing lowest order terms.
        This equation can now be written:

         -----            -----                 -----
         \          k     \             l+m     \              n
          >     a  x   =   >     b  c  x     =   >    b    c  x  .
         /       k        /       l  m          /      n-m  m
         -----            -----                 -----
         k>=k0            l>=l0              m0<=m<=n-l0
                          m>=m0                n>=l0+m0

        Comparison of orders leads immediately to

                  -----
                  \
          a    =   >    b    c   ,  n>=l0+m0 .
           n      /      n-m  m
                  -----
               m0<=m<=n-l0

        We write the last term of the series separately:

                  -----
                  \
          a    =   >    b    c   + b   c     ,  n>=l0+m0 ,
           n      /      n-m  m     l0  n-l0
                  -----
               m0<=m<n-l0

        which leads immediately to the recursion formula

                       /       -----           \
                   1   |       \               |
         c     = ----- | a  -   >     b    c   | .
          n-l0    b    |  n    /       n-m  m  |
                   l0  \       -----           /
                            m0<=m<n-l0

        Finally we shift n by l0 and obtain

                       /          -----              \
                   1   |          \                  |
         c     = ----- | a     -   >     b       c   | .  (*)
          n       b    |  n+l0    /       n-m+l0  m  |
                   l0  \          -----              /
                                 m0<=m<n

        For multidimensional Taylor series we can use the same
        expression if we interpret all indices as appropriate
        multiindices.

        For computing the reciprocal of a Taylor series we use
        the formula (*) above with f(x) = 1, i.e. lowest order
        coefficient = 1, all others = 0;


symbolic procedure quottaylor(u,v);
  %
  % Divides Taylor series u by Taylor series v.
  % Like invtaylor, it depends on ordering of the coefficients
  %  according to the degree of the expansion variables (lowest first).
  %
  (if null tps then confusion 'quottaylor
    else quottaylor!*(u,v,tps))
   where tps := mult!.comp!.tp!.(u,v,t);

symbolic procedure quottaylor!-as!-sq(u,v);
   begin scalar tps;
     return
       if Taylor!-kernel!-sq!-p u and Taylor!-kernel!-sq!-p v and
          (tps := mult!.comp!.tp!.(mvar numr u,mvar numr v,t))
         then !*tay2q quottaylor!*(mvar numr u,mvar numr v,tps)
        else quotsq(u,v)
   end;

symbolic procedure quottaylor!*(u,v,tps);
   make!-Taylor!*(
      cdr z,replace!-next(car tps,car z),
      if !*taylorkeeporiginal and TayOrig u and TayOrig v
        then quotsq (TayOrig u, TayOrig v)
       else nil,
      TayFlagsCombine(u,v))
    where z := quottaylor1(car tps,TayCoeffList u,TayCoeffList v);

symbolic procedure quottaylor1(tay,lu,lv);
  %
  % Does the real work, called also by the expansion procedures.
  % Returns the coefflist.
  %
  Taylor!:
  begin scalar clist,il,lminu,lminv,aminu,aminv,ccmin,coefflis,tp,tn;
    tp := TpDegreeList tay;
    tn := TpNextList tay;
    lu := prune!-coefflist lu;
    if null lu then return tn . nil;
    lminu := TayCfPl car lu;
    for each el in cdr lu do
      if not null numr TayCfSq el then
        lminu := taydegree!-min2(lminu,TayCfPl el);
    aminu := if lminu neq TayCfPl car lu then TayGetCoeff(lminu,lu)
              else TayCfSq car lu;
    lv := prune!-coefflist lv;
    if null lv then Taylor!-error!*('not!-a!-unit,'quottaylor);
    il := common!-increment(smallest!-increment lu,
                            smallest!-increment lv);
    aminv := TayCfSq car lv;   % first element is that of lowest degree
    lminv := TayCfPl car lv;
    for each cf in cdr lv do
      if not taydegree!-strict!<!=(lminv,TayCfPl cf)
        then Taylor!-error('not!-a!-unit,'quottaylor);
    ccmin := subtr!-degrees(lminu,lminv);
    clist := {TayMakeCoeff(ccmin,quotsq(aminu,aminv))};
    coefflis := makecoeffs(ccmin,tn,il);
    if null coefflis then return tn . clist;
    for each cc in cdr coefflis do begin scalar sq;
      sq := subtrsq(TayGetCoeff(add!-degrees(cc,lminv),lu),
                    addcoeffs(clist,lv,ccmin,cc));
      if exceeds!-order(tn,cc)
        then tn := min2!-order(tn,tp,cc)
       else if not null numr sq
        then clist := TayMakeCoeff(cc,quotsq(sq,aminv)) . clist;
     end;
    return tn . subs2coefflist reversip clist
  end;

symbolic procedure taydegree!-min2(u,v);
  %
  % (TayPowerList, TayPowerList) -> TayPowerList
  %
  % returns minimum of both powerlists
  %
  for i := 1 : length u collect begin scalar l1,l2;
    l1 := nth(u,i);
    l2 := nth(v,i);
    return
      for j := 1 : length l1 collect
        Taylor!: min2(nth(l1,j),nth(l2,j))
  end;

symbolic procedure makecoeffshom(cmin,lastterm,incr);
  if null cmin then '(nil)
   else Taylor!:
     for i := 0 step incr until lastterm join
       for each l in makecoeffshom(cdr cmin,lastterm - i,incr) collect
              (car cmin + i) . l;

symbolic procedure makecoeffshom0(nvars,lastterm,incr);
  if nvars=0 then '(nil)
   else Taylor!:
     for i := 0 step incr until lastterm join
       for each l in makecoeffshom0(nvars - 1,lastterm - i,incr) collect
             i . l;

symbolic procedure makecoeffs(plmin,dgl,il);
  %
  % plmin the list of the smallest terms, dgl the degreelist
  % of the largest term, il the list of increments.
  % It returns an ordered list of all index lists matching this
  % requirement.
  %
  Taylor!:
  if null plmin then '(nil)
   else for each l1 in makecoeffs(cdr plmin,cdr dgl,cdr il) join
        for each l2 in makecoeffshom(
                         car plmin,
                         car dgl - get!-degree car plmin - car il,
                         car il)
                 collect (l2 . l1);

symbolic procedure makecoeffs0(tp,dgl,il);
  %
  % tp is a Taylor template,
  % dgl a next list (m1 ... ),
  % il the list of increments (i1 ... ).
  % It returns an ordered list of all index lists matching the
  % requirement that for every element ni: 0 <= ni < mi and ni is
  % a multiple of i1
  %
  Taylor!:
  if null tp then '(nil)
   else for each l1 in makecoeffs0(cdr tp,cdr dgl,cdr il) join
        for each l2 in makecoeffshom0(length TayTpElVars car tp,
                                      car dgl - car il,
                                      car il)
                 collect (l2 . l1);

symbolic procedure makecoeffpairs1(plmin,pl,lmin,il);
  Taylor!:
  if null pl then '((nil))
   else for each l1 in makecoeffpairs1(
                         cdr plmin,
                         cdr pl,cdr lmin,cdr il) join
     for each l2 in makecoeffpairshom(car plmin,
                                      car pl,car lmin,- car il)
           collect (car l2 . car l1) . (cdr l2 . cdr l1)$

symbolic procedure makecoeffpairs(plmin,pl,lmin,il);
  reversip cdr makecoeffpairs1(plmin,pl,lmin,il);

symbolic procedure makecoeffpairshom(clow,chigh,clmin,inc);
  if null clmin then '((nil))
   else Taylor!:
    for i := car chigh step inc until car clow join
      for each l in makecoeffpairshom(cdr clow,cdr chigh,cdr clmin,inc)
          collect (i . car l) . ((car chigh + car clmin - i) . cdr l);

symbolic procedure addcoeffs(cl1,cl2,pllow,plhigh);
  begin scalar s,il;
    s := nil ./ 1;
    il := common!-increment(smallest!-increment cl1,
                            smallest!-increment cl2);
    for each p in makecoeffpairs(pllow,plhigh,caar cl2,il) do
        s := addsq(s,multsq(TayGetCoeff(car p,cl1),
                            TayGetCoeff(cdr p,cl2)));
    return s
%    return for each p in makecoeffpairs(ccmin,cc,caar cl2,dl) addsq
%             multsq(TayGetCoeff(car p,cl1),TayGetCoeff(cdr p,cl2));
  end;

symbolic procedure invtaylor u;
  %
  % Inverts a Taylor series expansion,
  % depends on ordering of the coefficients according to the
  %  degree of the expansion variables (lowest first)
  %
  if null u then confusion 'invtaylor
   else begin scalar tps;
     tps := inv!.tp!. u;
     return make!-Taylor!*(
               invtaylor1(car tps,TayCoeffList u),
               car tps,
               if !*taylorkeeporiginal and TayOrig u
                then invsq TayOrig u
                 else nil,
               TayFlags u);
   end;

symbolic procedure invtaylor1(tay,l);
  %
  % Does the real work, called also by the expansion procedures.
  % Returns the coefflist.
  %
  Taylor!:
  begin scalar clist,amin,ccmin,coefflis,il;
    l := prune!-coefflist l;
    if null l then Taylor!-error!*('not!-a!-unit,'invtaylor);
    amin := TayCfSq car l;  % first element must have lowest degree
    ccmin := TayCfPl car l;
    for each cf in cdr l do
      if not taydegree!-strict!<!=(ccmin,TayCfPl cf)
        then Taylor!-error('not!-a!-unit,'invtaylor);
    il := smallest!-increment l;
    ccmin := invert!-powerlist ccmin;
    clist := {TayMakeCoeff(ccmin,invsq amin)};
    coefflis := makecoeffs(ccmin,TpNextList tay,il);
    if not null coefflis
      then for each cc in cdr coefflis do begin scalar sq;
             sq := addcoeffs(clist,l,ccmin,cc);
             if not null numr sq
               then clist := TayMakeCoeff(cc,negsq quotsq(sq,amin))
                               . clist;
     end;
    return subs2coefflist reversip clist
  end;

endmodule;

end;
