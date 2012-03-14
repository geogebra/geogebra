module numint; % compute a numerical integral

% Author: H. Melenk,

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


     % Version March 1993

     % strategy for univariate integrals:
     %
     % 1. look for exact antiderivative, use that if it is
     %          bounded in the interval,
     %
     % 2. try Chebyshev fit,
     %
     % 3. use M. Wulkow's adaptive multilevel quadrature.

% fixed a problem in finding the right accuracy
% in the adaptive multilevel quadrature, W. Neun, August 1997

fluid '(!*noequiv accuracy!* singularities!*);
global '(iterations!* !*trnumeric);

symbolic procedure intrdeval u;
     % Interface function.
  begin scalar e,vars,y,p,singularities!*,imode,r,protfg!*,
          m,w;
    u := for each x in u collect reval x;
    u := accuracycontrol(u,3,20);
    e :=car u; u :=cdr u;
      % variables and interval's.
    % Allow for int(f,x,low,upp).
    if length u = 3 and (atom car u or not(caar u eq 'equal))
      then u := {{'equal,car u,'!*interval!* . cdr u}}
     else if eqcar(car u,'list) then
      u := for each x in cdar u collect reval x;
    for each x in u do
     <<if not eqcar(x,'equal) then typerr(x,"interval bounds");
       imode := 'infinity;
       if not !*rounded then setdmode('rounded,w:=!*rounded:=t);
       y:=revalnuminterval(caddr x,imode);
       if w then setdmode('rounded,w:=!*rounded:=nil);
       imode := t; % inf only for univariate cases.
       vars:=cadr x.vars;
       p:=(cadr x. car y. cadr y).p>>;
    m:=!*msg;
    if !*trnumeric then !*msg:=nil else protfg!* := t;
    r:= intrd0(e,vars,p);
    erfg!* := nil; !*msg:=m;
    r:=reval r where dmode!* = '!:rd!:;
    return r;
  end;

put('num_int,'psopfn,'intrdeval);

symbolic procedure intrd0 (e,vars,p);
  if cdr vars then intrd2(e,vars,p) else
     % Univariate case.
   begin scalar fcn,x,lo,hi;
    fcn := e;
    x := car vars;
    lo := car cdar p; hi := cdr cdar p;
    return intrd1(fcn,x,lo,hi,0);
  end;

symbolic procedure intrd1(fcn,x,lo,hi,n);
 % Recursively decompose infinite intervals; map -inf to +inf;
 % transform integral over [a,inf] to integral over [0,1].
   begin scalar w,r1,r2,res;
    n := n+1;
    if !*trnumeric then
    <<writepri(n,'first); writepri(" level integrate :",nil);
      writepri(mkquote fcn,nil); writepri(" over ",nil);
      writepri(mkquote mkinterval(lo,hi),'last);
    >>;
    if evalgreaterpx(lo,hi) then
    <<w:=lo;lo:=hi;hi:=w;fcn:={'minus,fcn}>>;

     % Map infinite intervals to finite ones.

     % case 1: [-inf,hi]

    if lo=minus!-infinity!* then
     if evalgreaterpx(hi,-1) then

     << % case 1.1: hi>-1 : [-inf,-1] + [-1,hi]
       r1 := intrd1(fcn,x,-1,hi,n);
       r2 := intrd1(subst({'minus,x},x,fcn),x,1,'infinity,n);
       res := reval {'plus,r1,r2};
     >> else

     << % case 1.2: hi <= -1
        res:= intrd1(subst({'minus,x},x,fcn),
                x,reval{'minus,lo},'infinity,n);
     >>;

     if res then goto fin;

     % case 2: [low .. inf] with -inf < low
    if hi='infinity then
     if evalgreaterpx(1,lo) then

     << % case 2.1: lo < 1: [lo,1] + [1,inf]
       r1 := intrd1(fcn,x,lo,1,n);
       r2 := intrd1(inttrans(fcn,1,x),x,0,1,n);
       res:= reval {'plus,r1,r2};
     >> else

     << % case 2.2: 1<=lo
        res := intrd1(inttrans(fcn,lo,x),x,0,1,n);
     >>;

     if res then goto fin;

     % case 3: finite interval
    res := intrd1a(fcn,x,lo,hi,{x.lo.hi});

 fin:
    if !*trnumeric then
    <<writepri(n,'first); writepri(" level integral :",nil);
      writepri(mkquote res,'last)>>;
    return res;
   end;

symbolic procedure inttrans(u,a,x);
  reval inttrans1(u,a,x);

algebraic procedure inttrans1(u,a,x);
  % Transform function to integrate. The transformation
  % maps integral over [a,inf] to [0,1] when a > 0.
  % Here used only for a>1.
  <<u:=sub(x=a/(1-!&t),u)*a /(a-!&t)^2;
    sub(!&t=x,u)>>;

symbolic procedure intrd1a(fcn,x,lo,hi,p);
   begin scalar u,w,acc,uu,loo,hii,oldmode,cbound;
     integer ord;
    if evalgreaterp(hi,lo) then <<loo:=lo; hii:=hi>>
      else <<loo:=hi; hii:=loo>>;
     % first attempt: look for bounded antiderivative.
    u := reval{'int,reval fcn,x} where dmode!*=nil,!*rounded=nil;
    oldmode:=switch!-mode!-rd nil;
    w:= errorset(
        {'boundseval,mkquote{u, {'equal,x,{'!*interval!*, loo, hii}}}},
                             nil,nil) where !*msg=nil;
    switch!-mode!-rd oldmode;

    if not smemq('int,u) and not errorp w then
      <<
        if !*trnumeric then prin2t "Using bounded antiderivative";
        oldmode:=switch!-mode!-rd nil;
        u:=simp u;
        w:= prepsq subtrsq(subsq(u,{x . hi}),subsq(u,{x . lo}));
        switch!-mode!-rd oldmode;
        return w;
      >>;
    if !*trnumeric then prin2t "No bounded antiderivative found";

    % second attempt: look for a Chebyshev fit.
    w := nil;
    oldmode:=switch!-mode!-rd nil;
    acc := !:!:quotient(1,expt(10,accuracy!*));
    cbound := dm!: (acc /(hii - loo));
    ord := 20;
  chebloop:
    u := chebcoeffs(fcn,x,loo,hii,ord);
    uu := reverse u;
    if int!-chebconverges(uu,acc,cbound) then
    <<
      u:=chebint(u,nil,loo,hii);
      w:=aeval{'difference,chebeval(u,nil,loo,hii,hi),
                           chebeval(u,nil,loo,hii,lo)};
    >>;
    if null w and ord<60 then <<ord:=ord+20; goto chebloop>>;
    switch!-mode!-rd oldmode;
    if w then
    <<if !*trnumeric then
       << prin2 "Using Chebyshev approximation of order ";
          prin2t ord>>;
      return w;
    >>;
    if !*trnumeric then
    <<
       prin2t "No usable Chebyshev approximation found";
       prin2t "Starting adaptive multilevel quadrature";
    >>;
    return intrd2 (fcn,{x.lo.hi},p);
  end;

symbolic procedure int!-chebconverges(u,acc,ab);
  % Test the (reverse) Chebyshev coefficients u for convergence.
  % acc: current accuracy (epsilon), ab: absolute upper bound for
  % trailing coefficient(s).
  begin scalar mx;
    mx := int!-chebmax(u,nil);
    dm!:(mx := mx*acc);
    return dm!:(abs car u < mx
        and abs cadr u < mx and abs caddr u < mx
        and abs car u < ab and abs cadr u < ab
        and abs caddr u < ab);
  end;

symbolic procedure  int!-chebmax(u,mx);
   if null u then mx else
   int!-chebmax(cdr u,if dm!:(w>mx) then w else mx)
          where w=dm!: abs car u;


% ----------------- adaptive multilevel quadrature

symbolic procedure intrd2 (e,vars,p);
   begin scalar acc,r,oldmode,!*noequiv;
    vars := nil;
    oldmode:=switch!-mode!-rd nil;
    acc := !:!:quotient(1,expt(10,accuracy!*));
    e := reval e;
    r := if null cdr p then intrduni(e,p,acc) else
          intrdmulti(e,p,acc);
    switch!-mode!-rd oldmode;
    return r;
  end;

symbolic procedure intevaluate1(e,x,v);
  (if atom a then <<singularities!*:=v.singularities!*; 0>>
     else car a)where a=evaluate!*(e,list x,list v);

symbolic procedure intevaluate(e,x,v);
  (if atom a then <<singularities!*:=v.singularities!*; 0>>
     else car a)where a=evaluate!*(e,x,v);

symbolic procedure intrduni (e,p,acc);
   % univariate integral.
 dm!:
  begin scalar lo,hi,x,u,eps,i,il,int;
     integer n,k;
    x := car p; p:= cdr p;
   % evaluate the interval.
    lo := cadr x; hi := cddr x; x := car x;
    lo:=force!-to!-dm lo; hi:=force!-to!-dm hi;
    if hi=lo then return force!-to!-dm nil;
      % initial interval;
    il := list intrdmkinterval(e,x,
               (lo.intevaluate1(e,x,lo)),
               (hi.intevaluate1(e,x,hi)),1);
    int:=car lastpair car il;
  loop:
    k:=add1 k; n:=add1 n;
    if remainder(n,25)=0 then intrdmess(n,il);
   % divide the interval with the biggest local error;
    i:=car il; il:=cdr il; u:=intrdintersect(e,x,i);
    if null u then
        <<il:=i.il;
          intrdabortmsg(list car cadr i,list x,intcollecteps il);
           goto ready>>;
    for each q in u do il := intrdsortin(q,il);
    if k<5 then goto loop;

   % control variation of result
   %% if n=5 then   % W. Neun.
    int:=intcollectint il; % adjust accuracy
    k:=0; eps := intcollecteps il;
    if eps > (acc * abs int) then goto loop;
  ready:
    return intcollectint il;
  end;

symbolic procedure intrdabortmsg(pt,vars,eps);
   <<writepri(
"requested accuracy cannot be reached within iteration limit",'only);
     writepri("critical area of function near to ",'first);
     writepri(mkquote('list . for each u in pair(vars,pt)
       collect list('equal,car u,prepf cdr u)),'last);
     writepri("current error estimate: ",'first);
     writepri(mkquote prepf eps,'last);
   >>;

symbolic procedure intcollectint il;
 dm!: <<for each i in cdr il do r:= car lastpair i + r; r>>
          where r=car lastpair(car il);

symbolic procedure intcollecteps il;
 dm!: <<for each i in cdr il do r:= car i + r; r>>
          where r=car(car il);

symbolic procedure intrdsortin(u,l);
   % sort interval u into l such that l is sorted with descending car.
 dm!:( if null l then list u else
       if car u < caar l then car l . intrdsortin(u,cdr l) else
       u . l);

symbolic procedure intrdintersect(e,x,i);
  begin scalar d,plo,pmi,phi,lev;
    i:= cdr i;
    plo := car i; i := cdr i;
    pmi := car i; i := cdr i;
    phi := car i; i := cdr i;
    d   := car i; dm!:(d:=d/2);
    lev := cadr i +1;
    if lev>iterations!* then return nil;
    return list (intrdmkinterval(e,x,plo,pmi,lev) ,
                 intrdmkinterval(e,x,pmi,phi,lev) );
   end;

symbolic procedure intrdmkinterval(e,x,lo,hi,lev);
  dm!: begin scalar eps,it,is,mid,mi,flo,fhi,fmid,d,u;
    d := car hi- car lo;
    mid := (car hi+ car lo)/2;
    flo := cdr lo; fhi := cdr hi;
    fmid:=intevaluate1(e,x,mid);
    mi:=mid.fmid;
    if u:=intrdunisingular(lo,mi,hi) then
         <<flo:=car u; fmid:=cadr u; fhi:=caddr u>>;
    it := d*(flo+ 2fmid + fhi) / 4;         % order 1 integral;
    is := d*(4*flo + 16*fmid + 4*fhi)/24;   %simpson integral;
    eps:= abs(is-it);
                                            % interval record:
    return list (eps,                       % local error contrib
                 lo, mi, hi,                % the 3 points
                 d, lev, is);               % length and simpson.
  end;

symbolic procedure intrdunisingular(lo,mi,hi);
  % local extraploation for singular points.
  if singularities!* then
   begin scalar slo,smi,shi,fl,fm,fh,u;
    slo:=member(car lo,singularities!*);
    smi:=member(car mi,singularities!*);
    shi:=member(car hi,singularities!*);
    if not slo and not smi and not shi then return nil;
    if slo and smi and shi then rederr "too many singularities";
    fl:=cdr lo; fm:=cdr mi; fh:=cdr hi;
    dm!:( u := 2*(fl+fm+fh) );
    return list(if slo then u else fl,
                if smi then u else fm,
                if shi then u else fh);
  end;

symbolic procedure intrdmulti(e,p,acc);
  % multivariate integral.
 dm!:
  begin scalar vars,u,eps,i,il,int;
     integer n,k,dim;
    if smemq('infinity,p) then
      rederr "no support for infinity in multivariate integrals";
    dim:=length p;
    il:=intrdmkinitcube(e,p);
    vars := car il; il:= cdr il;
  loop:
    k:=add1 k; n:=add1 n;
    if remainder(n,25)=0 then intrdmess(n,il);
   % divide the simplex with the biggest local error;
    i:=car il; il:=cdr il; u:=intrdrefinecube(e,vars,i);
    if null u then
        <<il:=i.il;
          intrdabortmsg(car cadr i,vars,intcollecteps il);
           goto ready>>;
    for each q in u do il := intrdsortin(q,il);
    if k<5 then goto loop;

   % control variation of result
   % if n=5 then  % W. Neun.
    int:=intcollectint il; % adjust accuracy
    k:=0; eps := intcollecteps il;
    if eps> (acc * abs int) then goto loop;
  ready:
    return intcollectint il;
  end;

symbolic procedure intrdmkinitcube(e,p);
  % make initial element.
   begin scalar vol,vars,il,lo,hi,x,y;
    vol:=1;
    for each z in p do
    <<vars:=car z.vars;
      x:=force!-to!-dm cadr z;
      y:=force!-to!-dm cddr z;
      lo:=x.lo; hi:=y.hi;
      vol:= dm!:( abs(x-y)*vol );
    >>;
    lo:=lo.intevaluate(e,vars,lo);
    hi:=hi.intevaluate(e,vars,hi);
    il:=list intrdmkcube(e,vars,lo,hi,vol,1);
    return vars.il;
  end;

symbolic procedure intrdrefinecube(e,vars,i);
 dm!:  % divide cube into 2.
  begin scalar plo,phi,lo,hi,nlo,nhi,vol,x,y,xnew;
     integer m,lev;
    i:=cdr i;
    plo:=car i; i:=cdr i; phi:=car i; i:=cdr i;
    vol:= car i / 2; lev := add1 cadr i;
    if lev > iterations!* then return nil;
    lo:=car plo; hi:=car phi;
      % select longest edge of interval;
    m := 1; x := car hi-car lo;
    for j:=2:length lo do
      if x<(y:=(nth(hi,j)-nth(lo,j))) then<<m:=j;x:=y>>;
    nlo := append(lo,nil); nhi := append(hi,nil);
    xnew:=(nth(hi,m) + nth(lo,m)) /2 ;
    nth(nlo,m):=xnew; nth(nhi,m):=xnew;
    nlo := nlo.intevaluate(e,vars,nlo);
    nhi := nhi.intevaluate(e,vars,nhi);
    return list(
      intrdmkcube(e,vars,plo,nhi,vol,lev),
      intrdmkcube(e,vars,nlo,phi,vol,lev));
  end;

symbolic procedure intrdmkcube(e,vars,lo,hi,vol,lev);
   % make a fresh cube record.
  dm!: begin scalar u,eps,it,is,mid,fmi,flo,fhi;
    flo:= cdr lo; fhi:= cdr hi;
    mid:=list!+list(car lo,car hi); mid:=scal!*list(1/2,mid);
    fmi:= intevaluate(e,vars,mid);
    if u:=intrdunisingular(lo,mid.fmi,hi) then
         <<flo:=car u; fmi:=cadr u; fhi:=caddr u>>;
    is:=flo+fhi;
    it:=is*vol/2; is:=(2*fmi+is)*vol/4;
    eps := abs(is-it);
    return list(eps,lo,hi,vol,lev,is);
  end;

symbolic procedure intrdmess(n,il);
  if !*trnumeric then
   <<writepri(2*n,'first); writepri(" intervals, integral=",nil);
     writepri(mkquote prepf intcollectint il,nil);
     writepri(", error estimate=",nil);
     writepri(mkquote prepf intcollecteps il,nil);
   >>;

symbolic procedure prinxt l;
  begin integer i;
    for each x in l do
     if not eqcar(x,'!:rd!:) then prin2 x else
     if atom cdr x then prin2 x else
       <<prin2 (float cadr x *expt(10.0,cddr x));
         tab (i:=20+i)>>;
     terpri();
   end;

endmodule;

end;
