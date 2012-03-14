module bounds;   % Upper and lower bound of a
% multivariate functional expression in a n-dimensional interval.

% Author: H. Melenk, ZIB, Berlin

% Copyright (c): ZIB Berlin 1991, all rights resrved

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


put('bounds,'psopfn,'boundseval);
put('bounds!-rd,'psopfn,'boundsevalrd);
put('bounds,'numericfn,'bounds!-rd);

fluid '(boundsdb!* !*msg boundspolynomialdb!*
       !*boundsfullcheck boundsknown!* boundsvars!*);

symbolic procedure boundsevalrd u;
   begin scalar dmode!*;
     setdmode('rounded,t);
     return boundseval u;
   end;

symbolic procedure boundseval u;
 (begin scalar db,y,l,f, boundsvars!*;
    u := for each x in u collect reval x;
    f := car u; u := cdr u;
    if u and eqcar(car u,'list) then
      u := for each x in cdar u collect reval x;
    for each x in u do
    <<if not eqcar(x,'equal) then typerr(x,"domain description");
      y := revalnuminterval(caddr x,nil);
      l:=list(simp car y,simp cadr y);
      boundsvars!*:=append(boundsvars!*,{cadr x});
      db := (cadr x . minsq l . maxsq l).db>>;
    u := boundseval1(f,db);
    return mkinterval(prepsq car u,prepsq cdr u);
  end) where !*roundbf=!*roundbf;

symbolic procedure boundserr(a,b);
    if not !*msg then error(99,'bounds) else
    if a then typerr(a,b) else rederr b;

symbolic procedure boundseval0 (f,db,!*msg);
  % internal calling convention:
  %  f            algebraic expression,
  %  db           list of (x . lo . hi), where
  %               lo and hi represent the current interval for
  %               variable x as standard quotients.
  %  result is a pair of standard quotients representing
  %               the bounds for f.
   <<boundsvars!*:=for each x in db collect car x;
     boundseval1(f,db)>>;

symbolic procedure boundseval1(u,boundsdb!*);
    <<if u member boundsknown!* then !*boundsfullcheck := nil else
      << !*boundsfullcheck := t;
        if dmode!* = '!:rd!: then boundsknown!* := u.boundsknown!*;
      >>;
      boundseval2 u>>;

symbolic procedure boundseval2 u;
  % Main driver for bounds evaluation
  if adomainp u then <<u:=simp u;u.u>> else
  boundspolynomial u or
  begin scalar v,w,fcn;
    if (v:=assoc(u,boundsdb!*))then return cdr v;
    if idp u and (fcn:=get(u,dmode!*)) then
      <<v:=apply(fcn,nil); v:=(v ./1).(v ./1); goto done>>;
    if atom u then goto err;
    if car u='expt and fixp caddr u and caddr u>0 then
    <<v:=boundsexpt!-int(boundseval2 cadr u,caddr u);
      goto done>>;
    w := for each y in cdr u collect boundseval2 y;
    v:= if cdr w and cddr w and (fcn:=get(car u,'boundsevaln))
           then apply1(fcn,w)
         else if length w>2 then t
         else if (fcn:=get(car u,'boundseval1))
          then apply2(fcn,car u,car w)
         else if (fcn:=get(car u,'boundseval))
          then if null cdr w then apply1(fcn,car w)
                else apply2(fcn,car w,cadr w)
         else if cdr w then t
         else boundsoperator(car u,car w);
    if v=t then goto err2;
    if null v or
       not domainp numr car v or not domainp denr car v or
       not domainp numr cdr v or not domainp denr cdr v then goto err;
     % cache result for later usage.
  done:
    boundsdb!*:= (u.v).boundsdb!*;
    return v;
err: boundserr(nil,"unbounded in range");
err2: boundserr(nil,"bounds confusion");
  end;

symbolic procedure boundsoperator(fcn,u);
  % general external interface: function flagged decreasing,
  % increasing of explicit bounds given.
   if flagp(fcn,'increasing) then boundsincreasingfn(fcn,u) else
   if flagp(fcn,'decreasing) then boundsdecreasingfn(fcn,u) else
   if get(fcn,'upperbound) and get(fcn,'lowerbound) then
       simp get(fcn,'lowerbound) . simp get(fcn,'upperbound)
    else nil;

symbolic procedure boundsplus u;
   if null cdr u then car u else
   boundsplus2(car u,boundsplus cdr u);

symbolic procedure boundsplus2(u,v);
    addsq(car u,car v) . addsq(cdr u,cdr v);

put('plus,'boundsevaln,'boundsplus);
put('plus,'boundseval,'boundsplus2);

symbolic procedure boundsdifference(x,y);
   subtrsq(car x,cdr y).subtrsq(cdr x,car y);

put('difference,'boundseval,'boundsdifference);

symbolic procedure boundsminus(u);
   negsq cdr u . negsq car u;

put('minus,'boundseval,'boundsminus);

symbolic procedure boundstimes u;
   if null cdr u then car u else
    boundstimes2(car u,boundstimes cdr u);

symbolic procedure boundstimes2(x,y);
  begin scalar z;
      % first handle simple cases
   if not minusf numr car x and not minusf numr car y then
        return multsq(car x,car y) . multsq(cdr x,cdr y);
   if minusf numr cdr x and minusf numr cdr y then
        return multsq(cdr x,cdr y) . multsq(car x,car y);
   z:=list(multsq(car x,car y), multsq(car x,cdr y),
           multsq(cdr x,car y), multsq(cdr x,cdr y));
   return minsq z . maxsq z;
  end;

symbolic procedure minsq l;
  begin scalar x;
   x := car l;
   for each y in cdr l do
    if minusf numr subtrsq(y,x) then x:=y;
   return x;
  end;

symbolic procedure maxsq l;
  begin scalar x;
   x:= car l;
   for each y in cdr l do
    if minusf numr subtrsq(x,y) then x:=y;
   return x;
  end;

symbolic procedure sqgreaterp(x,y);
     minusf numr subtrsq(y,x);

put('times,'boundsevaln,'boundstimes);
put('times,'boundseval,'boundstimes2);

symbolic procedure boundsexp u;
   boundsexpt(b . b,u) where b = simp 'e;

put('exp,'boundseval,'boundsexp);

symbolic procedure boundsexpt!-int(b,ex);
  % Form x ** n. Look for even exponents.
  begin scalar hi,lo,bh,bl;
    bl := exptsq(car b,ex); bh := exptsq(cdr b,ex);
    if not evenp ex then return bl.bh;
    lo := minusf numr cdr b;
    hi := not minusf numr car b;
    return
        if hi then bl.bh else  % both had been positive
        if lo then bh.bl else  % both had been negative: invert
             (nil ./ 1) . maxsq list(bl,bh);
   end;

symbolic procedure boundsexpt!-const(b,ex);
  % Form x ** constant, including fractional exponents.
  begin scalar l,n,m;
    n := '(nil . 1);
    if sqgreaterp(n,ex) then
        return boundsquotient('(1 . 1),boundsexpt!-const(b,negsq ex));
    if denr ex = 1 and
      (fixp(m:=numr ex)
         or eqcar(m,'!:rd!:)
            and null addf(numr ex,negf(m := reval{'round,m})))
           then return boundsexpt!-int(b,m);
    if sqgreaterp(n,car b) then
        boundserr(nil,"fractional exponent with negative base");
    l := list(bounds!-evalfcn2('expt,car b,ex),
              bounds!-evalfcn2('expt,cdr b,ex));
    return minsq l . maxsq l;
  end;

symbolic procedure boundsexpt(b,e);
   if car e=cdr e then boundsexpt!-const(b,car e) else
  % Form x ** y. x>0 only.
  % Either monotonous growing or falling: pick extremal points.
  begin scalar l,n;
    n:='(nil . 1);
    if sqgreaterp(n,car b) then return nil;
    l := list(bounds!-evalfcn2('expt,car b,car e),
              bounds!-evalfcn2('expt,car b,cdr e),
              bounds!-evalfcn2('expt,cdr b,car e),
              bounds!-evalfcn2('expt,cdr b,cdr e));
    return minsq l . maxsq l;
  end;

symbolic procedure boundsprepsq u; prepsq car u . prepsq cdr u;

put('expt,'boundseval,'boundsexpt);

symbolic procedure boundsquotient(n,d);
  begin scalar l;
    if boundszero d then boundserr(nil,"unbounded in range");
    l := list(quotsq(car n,car d),quotsq(car n,cdr d),
              quotsq(cdr n,car d),quotsq(cdr n,cdr d));
    return minsq l . maxsq l;
  end;

symbolic procedure boundszero u;
  % test: 0 in interval u.
   not(minusf numr car u = minusf numr cdr u) or
   boundszero1 numr car u or boundszero1 numr cdr u;

symbolic procedure boundszero1 f;
  % test standard form f for zero.
   null f or zerop f or pairp f and apply(get(car f,'zerop),{f});

put('quotient,'boundseval,'boundsquotient);

symbolic procedure boundsabs u;
 if evalgreaterp(prepsq car u,0) then u else
 if evalgreaterp(0,prepsq cdr u) then negsq cdr u . negsq car u else
  (nil ./1) . maxsq list (negsq car u,cdr u);

put('abs,'boundseval,'boundsabs);

symbolic procedure boundsincreasingfn(fn,u);
  % Bounds for an increasing function. Out-of -range problems will
  % be detected either by the function evaluation or finally by
  % the main driver if the result is not an interval with numeric
  % bounds.
  bounds!-evalfcn1(fn,car u) . bounds!-evalfcn1(fn,cdr u);

put('log,'boundseval1,'boundsincreasingfn);
put('asin,'boundseval1,'boundsincreasingfn);
put('atan,'boundseval1,'boundsincreasingfn);
put('sqrt,'boundseval1,'boundsincreasingfn);

symbolic procedure boundsdecreasingfn(fn,u);
       boundsincreasingfn(fn,cdr u.car u);

put('acos,'boundseval1,'boundsdecreasingfn);
put('acot,'boundseval1,'boundsdecreasingfn);

symbolic procedure boundssincos(fcn,n);
   % Reason if one of the turn points (n*pi) is in the
   % range. If yes, include the corresponding value 1 or -1.
   % Otherwise compute the range spanned by the end points.
  begin scalar lo,hi,!1pi,!2pi,!3pi,l,m;
     lo := car n; hi := cdr n;
     <<setdmode('rounded,t);
       !1pi := simp 'pi;
       setdmode('rounded,nil);
     >> where dmode!* =nil;
     if not domainp numr !1pi then goto trivial;
     !2pi := addsq(!1pi,!1pi); !3pi := addsq(!1pi,!2pi);
       % convert sin to cos
     if fcn = 'sin then <<m :=multsq(!1pi, -1 ./ 2);
         lo := addsq(lo,m); hi := addsq(hi,m)>>;
     m := negsq multsq(!2pi,fixsq quotsq(lo,!2pi));
       % move interval to main interval
     lo:=addsq(lo,m); hi:=addsq(hi,m);
     if minusf numr lo then
     <<lo := addsq(lo,!2pi); hi := addsq(hi,!2pi)>>;
     if null numr lo or sqgreaterp(hi,!2pi) then l:= (1 ./ 1) . l;
     if (sqgreaterp(!1pi,lo) and  sqgreaterp(hi,!1pi))
      or(sqgreaterp(!3pi,lo) and  sqgreaterp(hi,!3pi))
       then l := (-1 ./ 1) . l;
     if l and cdr l then goto trivial;
     l:=bounds!-evalfcn1('cos,lo) .bounds!-evalfcn1('cos,hi).l;
     if smemq('cos,l) then goto trivial;
     return minsq l . maxsq l;
 trivial:
     return ((-1)./1) . (1 ./ 1);
  end;

symbolic procedure fixsq u;
         bounds!-evalfcn1('fix,u);

symbolic procedure bounds!-evalfcn1(fcn,u);
   (if domainp numr u and denr u=1
         and (x:=valuechk(fcn,list numr u))
     then x else simp list(fcn,prepsq u)) where x=nil;

symbolic procedure bounds!-evalfcn2(fcn,u,v);
   (if domainp numr u and denr u=1 and
       domainp numr v and denr v=1
         and (x:=valuechk(fcn,list(numr u,numr v)))
     then x else simp list(fcn,prepsq u,prepsq v)) where x=nil;

symbolic procedure agreaterp(u,v);
   (lambda x;
    if not atom denr x or not domainp numr x
      then error(99,"number")
     else numr x and !:minusp numr x)
        simp!* list('difference,v,u);

symbolic procedure boundssin u;
       boundssincos('sin,u);

symbolic procedure boundscos u;
       boundssincos('cos,u);

put('sin,'boundseval,'boundssin);
put('cos,'boundseval,'boundscos);

symbolic procedure boundstancot(fcn,n);
  begin scalar lo,hi,x,ppi;
     lo := car n; hi := cdr n;
     ppi := rdpi!*() ./ 1;
     if sqgreaterp(subtrsq(lo,hi),ppi) then goto no;
     lo:=bounds!-evalfcn1(fcn,lo);
     hi:=bounds!-evalfcn1(fcn,hi);
     if fcn='cot then <<x:=lo;lo:=hi;hi:=x>>;
     if not sqgreaterp(lo,hi) then return lo.hi;
no:  return boundserr(nil,"unbounded in range");
  end;


symbolic procedure boundstan u;
    boundstancot('tan,u);

symbolic procedure boundscot u;
    boundstancot('cot,u);

put('tan,'boundseval,'boundstan);
put('cot,'boundseval,'boundscot);

symbolic procedure boundsmaxmin u;
  if null cdr u then car u else
       boundsmaxmin2(car u,boundsmaxmin cdr u);

symbolic procedure boundsmaxmin2(x,y);
  (if sqgreaterp(car x,car y) then car y else car x).
  (if sqgreaterp(cdr x,cdr y) then cdr x else cdr y);

put('max,'boundsevaln,'boundsmaxmin);
put('min,'boundsevaln,'boundsmaxmin);
put('max,'boundseval,'boundsmaxmin2);
put('min,'boundseval,'boundsmaxmin2);

% for univariate polynomials we look at the extremal points and the
% interval ends. Assuming that the same expression will be investigated
% with different intervals, we store the knowledge about the polynomial.

symbolic procedure boundspolynomial e;
 dm!: begin scalar x,u,lo,hi,d,v,l;
    if dmode!* neq '!:rd!: then return nil;
    if(u:=assoc(e,boundspolynomialdb!*)) then goto old;
    if null !*boundsfullcheck or
       null(x:=boundspolynomialtest e) then return nil;
    d := realroots{reval{'df,e,x}};
    u := x. for each s in cdr d collect
      <<d:=numr simp caddr s;
        d . evaluate(e,{x},{d})>>;
    u:=e.u;
    boundspolynomialdb!*:=u.boundspolynomialdb!*;
 old:
    x:=cadr u; u :=cddr u;
    v := assoc(x,boundsdb!*);
    if null v then return nil;
    lo:=numr cadr v; hi:=numr cddr v;
    l:={evaluate(e,{x},{lo})./1 , evaluate(e,{x},{hi}) ./1};
    for each p in u do
      if lo<car p and car p<hi then l:= (cdr p./1).l;
    return minsq l . maxsq l;
  end;

symbolic procedure boundspolynomialtest e;
 (pairp r and car r) where r=boundspolynomialtest1(e,nil);

symbolic procedure boundspolynomialtest1(e,x);
   if adomainp e then x or t else
   if atom e then boundspolynomialtestvariable(e,x) else
   if car e eq 'expt then
       fixp caddr e and
          boundspolynomialtestvariable(cadr e,x)
      else
   if car e eq 'minus then boundspolynomialtest1(cadr e,x)
      else
   if car e  eq 'plus or car e eq 'times then
    begin scalar r;
      e:=cdr e; r:=t;
      while e and r do
      <<r:=boundspolynomialtest1(car e,x);
        if r neq t then x:=r; e:=cdr e>>;
      return x or r;
    end
      else boundspolynomialtestvariable(e,x);

symbolic procedure boundspolynomialtestvariable(e,x);
  if x and e=car x then x else
  if x then nil else
  if member(e, boundsvars!*) then {e};

%--------------------------------------------------------------
%
% support for compilation
%
%--------------------------------------------------------------

fluid '(boundsdb!* boundscompv!* boundscompcode!*);

symbolic procedure boundscompile(e,v);
  % compile bounds evaluation function for expression e,
  % input intervals v.
  begin scalar boundsdb!*,boundscompv!*,boundscompcode!*,u;
    boundsdb!*:=for each x in v collect x.x;
    u:=boundscompile1(e,nil);
    return
     {'lambda,v,
      'prog . boundscompv!* .
         append(reversip boundscompcode!*,{{'return,u}})};
  end;

symbolic procedure boundscompile1(u,flag);
  % Main driver for bounds compilation
  if adomainp u then <<u:=simp u;mkquote(u.u)>> else
  begin scalar v,w,fcn,var;
    if (v:=assoc(u,boundsdb!*))then return cdr v;
    if idp u and (fcn:=get(u,dmode!*)) then
      <<v:=apply(fcn,nil); v:=mkquote((v ./1).(v ./1)); goto done>>;
    if atom u then goto err1;
    if car u='expt and fixp caddr u and caddr u>0 then
    <<v:={'boundsexpt!-int,boundscompile1(cadr u,t),caddr u};
      goto done>>;
    w := for each y in cdr u collect boundscompile1(y,t);
    v:= if length w>2 and (fcn:=get(car u,'boundsevaln))
              then {fcn,'list.w} else
        if length w>2 then t else
        if (fcn:=get(car u,'boundseval1))
               then {fcn,mkquote car u,car w} else
        if (fcn:=get(car u,'boundseval))
               then if null cdr w then {fcn,car w}
                     else {fcn,car w,cadr w}
         else if cdr w then t
         else {'boundsoperator,car u,car w};
  done:
    if v=t then goto err2;
    if not flag then return v;
    var:=gensym(); boundscompv!* := var.boundscompv!*;
    boundscompcode!*:={'setq,var,v}.boundscompcode!*;
     % cache result for later usage.
    boundsdb!*:= (u.var).boundsdb!*;
    return var;
err1: typerr(u,"bounded value");
err2: typerr(u,"expression to be compiled for bounds");
  end;

endmodule;

end;
