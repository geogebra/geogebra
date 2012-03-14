module defintx;  % Code for definite integration using contour methods.

% Author: Stanley L. Kameny <stan_kameny@rand.org>.

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


load_package solve,misc;

fluid '(!*allpoly rdon!* !*norationalgi); switch allpoly;

global '(domainlist!* poles!*);

algebraic <<
logcomplex :=
 {
  log(~x + i) =>
      log(sqrt(x*x+1))+i*atan2(1/sqrt(x*x+1),x/sqrt(x*x+1))
      when repart(x)=x,
  log(~x - i) =>
      log(sqrt(x*x+1))-i*atan2(1/sqrt(x*x+1),x/sqrt(x*x+1))
      when repart(x)=x,
  log(~x + i*~y) =>
      log(sqrt(x*x+y*y))+i*atan2(y/sqrt(x*x+y*y),x/sqrt(x*x+y*y))
      when repart(x)=x and repart(y)=y,
  log(~x - i*~y) =>
      log(sqrt(x*x+y*y))-i*atan2(y/sqrt(x*x+y*y),x/sqrt(x*x+y*y))
      when repart(x)=x and repart(y)=y,
  log(~x/~y) => log(x) - log(y) when repart(y)=y,
  log(sqrt ~x) => (log x)/2,
  log(-1) => i*pi,
  log (-i) => -i*pi/2,
  log i => i*pi/2,
  log(-~x) => i*pi+log x when repart(x)=x and numberp x and x>0,
  log(-i*~x) => -i*pi/2 + log x
       when repart(x)=x and numberp x and x>0,
  log(i*~x) => i*pi/2 + log x  when repart(x)=x and numberp x and x>0
}$

atan2eval :=
 {
  atan2(sqrt 3/2,1/2) => pi/3,
  atan2(-sqrt 3/2,1/2) => -pi/3,
  atan2(sqrt 3/2,-1/2) => 2*pi/3,
  atan2(-sqrt 3/2,-1/2) => -2*pi/3,
  atan2(3/(2*sqrt 3),1/2) => pi/3,      % these shouldn't be needed
  atan2(-3/(2*sqrt 3),1/2) => -pi/3,    % these shouldn't be needed
  atan2(3/(2*sqrt 3),-1/2) => 2*pi/3,   % these shouldn't be needed
  atan2(-3/(2*sqrt 3),-1/2) => -2*pi/3, % these shouldn't be needed
  atan2(1/2,sqrt 3/2) => pi/6,
  atan2(-1/2,sqrt 3/2) => -pi/6,
  atan2(1/2,-sqrt 3/2) => 5*pi/6,
  atan2(-1/2,-sqrt 3/2) => -5*pi/6,
  atan2(1/2,3/(2*sqrt 3)) => pi/6,      % these shouldn't be needed
  atan2(-1/2,3/(2*sqrt 3)) => -pi/6,    % these shouldn't be needed
  atan2(1/2,-3/(2*sqrt 3)) => 5*pi/6,   % these shouldn't be needed
  atan2(-1/2,-3*(2*sqrt 3)) => -5*pi/6, % these shouldn't be needed
  atan2(sqrt 2/2,sqrt 2/2) => pi/4,
  atan2(-sqrt 2/2,sqrt 2/2) => -pi/4,
  atan2(sqrt 2/2,-sqrt 2/2) => 3*pi/4,
  atan2(-sqrt 2/2,-sqrt 2/2) => -3*pi/4,
  atan2(0,-1) => pi,
  atan2(0,1) => 0,
  atan2(1,0) => pi/2,
  atan2(-1,0) => -pi/2
}$

ipower := {i^~n => cos(n*pi/2) + i*sin(n*pi/2),
    (-i)^~n => cos(n*pi/2) - i*sin(n*pi/2)}$

>> $

% We can't set atan2eval rules if rounded or modular (and possibly another mode) is on.

begin scalar oldmode;
      if dmode!* then oldmode := setdmode(dmode!*,nil);
      algebraic let ipower,atan2eval;
      if oldmode then setdmode(oldmode,t)
   end;

%algebraic let logcomplex,atan2eval;

fluid '(!*diffsoln zplist!! poles!# !*msg !*rounded !*complex zlist);
switch diffsoln;

load_package int;

% put('defint,'psopfn,'defint0);

symbolic procedure defint0 u;
   begin
      scalar rdon!*,!*msg,c,!*noneglogs,fac,
         !*combinelogs,!*rationalize;
      if not getd 'solvesq then load_package solve;
      if length u neq 4 then rederr
        "defint called with wrong number of args";
      c := !*complex; off complex; % since complex on won't work here!
    %  on complex;  % this causes trouble here, so it was moved into
                    % defint11s after splitfactors has operated!
      !*noneglogs := t;
      algebraic (let logcomplex); %,atan2eval);
      fac := !*factor; on factor;
      u := errorset2 {'defint1,mkquote u} where !*norationalgi = t;
      if errorp u then <<u := 'failed; go to ret>> else u := car u;
      off factor;
      if !*rounded then
       % if approximate answer, eliminate infinitessimal real or
       % imaginary part.
          (<<off complex;
            if domainp numr u and denr u = 1 then
              (if evallessp({'times,{'abs,prepsq im},eps},
                 {'abs,prepsq rl})
                  then u := rl else
               if evallessp({'times,{'abs,prepsq rl},eps},
                 {'abs,prepsq im})
                  then u := addsq(u,negsq rl));
            u := mk!*sq u;
            if rdon!* then off rounded;off complex; go to ret2>>
          where rl=repartsq u,im=impartsq u,eps=10.0^(2-precision 0));
      !*rationalize := t;
      u := aeval prepsq u;
      on complex;
      u := simp!* u;
   %   u := evalletsub2({'(logcomplexs),
   %      {'simp!*,{'prepsq,mkquote u}}},nil);
   %   if errorp u then error(99,list("error during log simp"))
   %      else u := car u;
 ret: if fac then on factor;
      off complex;
      algebraic (clearrules logcomplex); %,atan2eval);
      if u neq 'failed then u := prepsq u;
      off complex; on combinelogs;
      if u neq 'failed then u := aeval u;
ret2: if c then on complex;
      return u end;

symbolic procedure defint1 u; defint11s(car u,cadr u,caddr u,cadddr u);

symbolic procedure defint11s(exp,var,llim,ulim);
  % removes from integrand any factors independent of var, and passes
  % the dependent factors to defint11.  Based on FACTOR being on.
   <<% off complex; % not needed here since complex is off already.
     exp := splitfactors(simp!* aeval exp,var);
     on complex; % at this point it is safe to turn complex on.
     multsq(simp!* car exp,
        defint11(cadr exp,var,llim,ulim,t))>>;

symbolic procedure fxinfinity x;
  if x eq 'infinity then 'inf
   else if x = '(minus infinity) then 'minf else x;

symbolic procedure defint11(exp,var,llim,ulim,dtst);
  if evalequal(llim := fxinfinity llim, ulim := fxinfinity ulim)
    or evalequal(exp,0) then nil ./ 1 else
  begin scalar !*norationalgi,r,p,q,poles,rlrts,cmprts,q1;
     scalar m,n;
     if ulim = 'minf or llim = 'inf then
        return defint11({'minus,exp},var,ulim,llim,dtst);
     exp := simp!* exp;
    % Now the lower limit must be 'minf or a finite value,
    % and the upper limit must be 'inf or a finite value. There are
    % four cases:
    % Upper limit is 'inf.  Convert lower limit to zero if necessary,
    % and use methods for infinite integrals.
     if ulim = 'inf then
        <<if not(llim member '(0 minf)) then
            <<exp := subsq(exp,{var . {'plus,var,llim}}); llim := 0>>;
          go to c0>>;
    % lower limit is 'minf.  Convert this case to upper limit 'inf.
     if llim = 'minf then
        <<off complex;
          exp := reval prepsq subsq(exp,{var . {'minus,var}});
          llim := reval {'minus,ulim};
          on complex;
          return defint11(exp,var,llim,'inf,dtst)>>;
    % Both limits are finite, so check for indef integral and
    % substitute values if it exists; else check for special forms with
    % finite limits, try substitutions, or abort.
     r := simpint {prepsq exp,var};
     if eqcar(prepsq r,'int) then go to c1;
     p := errorset2 list('subsq, mkquote r, mkquote {var . ulim});
     q := errorset2 list('subsq, mkquote r, mkquote {var . llim});
     if errorp(p) or errorp (q) then <<
        p:= simplimit list('limit!- ,mk!*sq(r),var,ulim);
        q:= simplimit list('limit!+ ,mk!*sq(r),var,llim); >>
     else <<p := car p; q := car q>>;
     return q1 := addsq(p,negsq q);
 c1: rederr "special forms for finite limits not implemented";
 c0: r := exp; p := numr r; q := denr r;
  %   if not polynomp(q,var) then
   %     rederr "only polynomial denominator implemented";
     m := degreeof(p,var); n := degreeof(q,var);
     if smemql('(failed infinity),m) or smemql('(failed infinity),n)
       then return error(99, 'failed);
    % Note that degreeof may return a fraction or a general complex
    % quantity.
     if not evalgreaterp(prepsq addsq(repartsq n,negsq repartsq m),1)
        then go to div;
    % this is the point at which special cases can be tested.
     if (q1 := specformtestint(q,p,var,llim,ulim)) then return q1;
    % beyond here, only rational functions are handled.
     if not (m := sq2int m) or not (n := sq2int n) then
       <<write "this irrational function case not handled"; terpri();
         error(99,'failed)>>;
     if n - m < 2 then go to div;
     if dtst and !*diffsoln then
        if (q1 := diffsol(q,p,m,n,var,llim,ulim)) then return q1;
     off factor; !*norationalgi := nil;
     poles := getpoles(q,var,llim);
     rlrts := append(car poles,cadr poles); cmprts := caddr poles;
     !*norationalgi := t;
     q1 := difff(q,var); q := q ./ 1;  p := p ./ 1;
     return if llim = 0 then defint2(p,q,q1,var,rlrts,cmprts)
        else defint3(p,q,q1,var,rlrts,cmprts);
div: % write "integral diverges"; terpri();
     error(99,'failed) end;

symbolic procedure zpsubsq x;
   subsq(x,for each v in zplist!! collect (v . 0));

symbolic procedure degreeof(p,var);
   % p is a standard form.
   % Note that limit returns "failed" as a structure, not an id.
   % Also, the limit code has problems with bessels at the present time.
  % if smemq('besseli,p) then !*k2q 'failed else
  if smemql ('(besselj besselk bessely besseli),p) then !*k2q 'failed else
  (if null car de then de else
    <<%if d then onoff(d := get(d,'dname),nil);
      if d then setdmode(get(d,'dname),nil);
      p := simp!*
              limit(list('quotient,list('times,var, prepsq de),prepf p),
                    var,'infinity);
      if d then setdmode(d,t); p>>)
    where d=dmode!*,de=difff(p,var);

symbolic procedure genminusp x;
   if domainp x then !:minusp x else !:minusp topeval prepf x;

symbolic procedure sq2int x;
  (if null numr impartsq x and denr y=1
     then if null z then 0 else if numberp z then z else nil)
   where z=numr y where y=repartsq x;

symbolic procedure topeval u;
  <<if not r then on rounded; if not c then on complex;
    u := numr simp!* aeval u;
    if not r then off rounded;if not c then off complex; u>>
  where r=!*rounded,c=!*complex,!*msg=nil;

symbolic procedure firstatom x;
   if atom x then x else firstatom car x;

symbolic procedure valueof u;
   (if firstatom x neq 'root_of then x) where x=caar u;

symbolic procedure rdsolvesq u;
   solvesq(subf(numr simp!* cadr x,list((caddr x) . caadr u)),
        caadr u,caddr u)
   where x=caaaar caar u;

symbolic procedure defint2(p,q,q1,var,rlrts,cmprts);
  % Does the actual computation of integral with limits 0, inf.
  % Pertinent poles and their orders have been found previously.
     begin scalar int;
error(99,"THIS IS WRONG, Stanley!!");
        p := simp!* aeval{'times,{'log,{'minus,var}},prepsq p};
        int := nil ./ 1;
        for each r in append(rlrts,cmprts) do
           int := addsq(int,residuum(p,q,q1,var,prepsq car r,cdr r));
        return negsq int end;

symbolic procedure defint3(p,q,q1,var,rlrts,cmprts);
  % Does the actual computation of integral with limits minf, inf.
  % Pertinent poles and their orders have been found previously.
     begin scalar int,int2;
        int := int2 := nil ./ 1;
        for each r in cmprts do
           int := addsq(int,residuum(p,q,q1,var,prepsq car r,cdr r));
        int := addsq(int,int);
        for each r in rlrts do
           int2 := addsq(int2,residuum(p,q,q1,var,prepsq car r,cdr r));
        int := addsq(int,int2);
        return multsq(simp!* '(times pi i),int) end;

symbolic procedure diffsqn(sq,var,n);
   <<if n>0 then for j := 1:n do sq := diffsq(sq,var); sq>>;


symbolic procedure polypwrp(exp,var);
   begin scalar pol,fl; integer s,pwr;
     if eqcar(exp,'expt) then
       <<pol := cadr exp; if (pwr := caddr exp) <2 then return nil;
         if atom pol then
           if var eq pol then s := 1 else return nil
         else if not eqcar(pol,'plus) then return nil
         else for each p in cdr pol do s := max(s,termvpwr(p,var));
         return if s = 0 then nil else {pol,s,pwr}>>
     else if eqcar(exp,'times) then
       <<exp := for each p in cdr exp collect polypwrp(p,var);
         for each p in exp do
           <<if null p then fl := t;
             if not fl then pwr := gcdn(pwr,caddr p)>>;
         if fl then return nil;
         s := (for each p in exp sum cadr p * caddr p)/pwr;
         pol := 'times .
           for each p in exp collect {'expt,car p,caddr p/pwr};
         return {pol,s,pwr}>> end;

symbolic procedure termvpwr(p,var);
   if freeof(p,var) then 0
   else if atom p then 1
   else if eqcar(p,'expt) and cadr p = var and
     numberp caddr p then caddr p
   else if eqcar(p,'times) then for each q in cdr p sum termvpwr(q,var)
   else 0;

symbolic procedure diffsol(q,p,mm,nn,var,llim,ulim);
  % p is numerator   q is denom    mm is deg p   nn is deg q
   (q := polypwrp(prepf q,var)) and
   begin scalar n,s,m,r,zplist!!;
     n := mm; s := cadr q; m := caddr q;
    % if s, the power of the base polynomial, > 4 then the base
    % polynomial won't be solved, and this approach won't work!
    % However, for s > 2, the approach is impractical, because the
    % roots of the zp!! polynomial are too complicated, so in the
    % following, s is tested s > 2.
     if s > 2 or m*s neq nn or nn - n <= 2 then return nil;
     r := (n+2)/s; if r*s < n+2 then r := r+1;
     if m = r then return nil;
     q := {'plus,car q,'zp!!}; zplist!! := '(zp!!);
     q := numr simp!*{'expt,q,r};
     nn :=(-1)^(m - r)*factorial(r - 1) ./ factorial(m - 1);
     p := defint11(prepsq(p ./ q),var,llim,ulim,nil);
     p := zpsubsq diffsqn(p,'zp!!,m - r);
     return multsq(nn,p) end;

symbolic procedure residuum(p,q,q1,var,pole,m);
   if m=1 then subsq(quotsq(p,q1),list(var . pole))
   else
   begin integer n;
     q1 := nil;
     for each r in poles!* do
       <<n := cdr r; r := prepsq car r;
         if not evalequal(pole,r)
           then q1 := {'expt,{'difference,var,r},n} . q1>>;
     n := ((lc numr simp!* prepsq q) where !*factor=nil);
     q1 := 'times . (n . q1);
     return if ((lt numr simp!* prepsq q =
       lt numr simp!*{'times,{'expt,{'difference,var,pole},m},q1})
          where !*factor=nil)
       then
           <<q := quotsq(p,simp!* q1);
             q := diffsqn(q,var,m - 1);
             q := subsq(q,{var . pole});
             q := if null numr q
               then q else quotsq(q,factorial(m -1) ./ 1)>>
         else q1 := simp!* (p := limit(
           prepsq
             quotsq(diffsqn(multsq(quotsq(p,q),
                 simp!* {'expt,{'difference,var,pole},m}),var,m - 1),
               factorial(m - 1) ./ 1),var,pole)) end;

symbolic procedure splitfactors(u,var);
  % returns a list of two factors:
  % independent of var and dependent on var.
   begin scalar n,d,ni,nd,di,dd,fli,fld;
     n := prepf numr u;
     if n=0 then return {0,0};
     d := prepf denr u;
     ni := nd := di := dd := 1;
     if simptermp n then
       <<if freeof(n,var) then ni := n else nd := n; go to d>>;
     for each x in cdr n do
         if freeof(x,var) then ni := if ni = 1 then list x
              else <<fli := t; x . ni>>
            else nd := if nd = 1 then list x else <<fld := t; x . nd>>;
     ni := fleshoutt(ni,fli); nd := fleshoutt(nd,fld);
     fli := fld := nil;
  d: if simptermp d then
       <<if freeof(d,var) then di := d else dd := d; go to ret>>;
     for each x in cdr d do
         if freeof(x,var) then di := if di = 1 then list x
              else <<fli := t; x . di>>
            else dd := if dd = 1 then list x else <<fld := t; x . dd>>;
     di := fleshoutt(di,fli); dd := fleshoutt(dd,fld);
ret: return {fleshout(ni,di),fleshout(nd,dd)} end;

symbolic procedure simptermp x;
   atom x or ((y = 'minus and simptermp cadr x or y neq 'times)
     where y=car x);

symbolic procedure fleshout(n,d); if d = 1 then n else {'quotient,n,d};

symbolic procedure fleshoutt(n,d);
   if n = 1 then n else if d then 'times . n else car n;

symbolic procedure specformtestint(den,num,var,llim,ulim);
  % This tests for defint(x^(p-1)/(a*x^n+b)^m,x,0,inf) with
  % m,n,p positive integers, p/n not integer and m>(p/n) and either
  %    a*b>0   or   {a*b<0,m=1}.
  % Since splitfactors has removed all factors which do not depend upon
  % var, both num and den are either 1 or products of terms which
  % depend upon var.
   begin scalar a,b,d,m,n,p,q1,q,k,z,ff;
     den := prepf den; num := prepf num;
     if not(llim=0) and ulim='inf then go to t2;
    % This is the test for defint(y**(q-1)/(a*y**n +b)**m,y,0,inf);
    % which is converted to defint(x**(p-1)/(x+z)**m,x,0,inf);
    % the next test is assumed to be accessed at label t2.
     if num = 1 then q1 := 0
       else if (q1 := varpwrtst(num,var))=0 then go to t2;
     if atom den then go to t2
       else if not eqcar(den,'times) then
        %only (a*y**n+b)**m term in den.
         if (k := aynbmtst(den,var)) then go to sep4 else go to t2
       else if length den neq 3 then go to t2;
    % the denominator is the product of 2 terms, one of which must be
    % y**q and the other an aynbm form like the previous case.
     d := cdr den;
     if not((k := aynbmtst(cadr d,var))
            and eqcar(q := varpwrtst(car d,var),'quotient)
            or
            (k := aynbmtst(car d,var))
            and eqcar(q := varpwrtst(cadr d,var),'quotient))
              then go to t2;
sep4: n := caddr k; if not numberp n then go to t3;
     q := prepsq simp!* {'plus,1,q1,{'minus,q}};
     p := prepsq simp!* {'quotient,q,n};
     m := cadddr k; if not numberp m or m<1 then go to t3;
     a := car k;
     b := cadr k;
     z := prepsq simp!* {'quotient,b,a};
       if numr impartsq simp!* z then go to t2;
     ff := prepsq simp!* aeval {'quotient,1,{'times,n,{'expt,a,m}}};
    % there are two different cases:
    %  z > 0 and m >repart p >0  m >= 1
    %  z < 0 and m=1 (Cauchy principal value)
     if evalgreaterp(z,0) then if
       not (evalgreaterp((k := prepsq repartsq simp!* p),0) and
         evalgreaterp(m,k))
       then go to t3
     else
      <<k := prepsq simp!* aeval
          {'times,{'expt,-1,m+1},'pi,{'expt,z,{'difference,p,m}}};
        n := 1;
        for c := 1:(m-1) do
          n := prepsq simp!* aeval {'times,n,{'difference,p,c}};
        q := prepsq simp!* aeval
          {'times,{'factorial,m-1},{'sin,{'times,p,'pi}}};
        return simp!* aeval {'quotient,{'times,k,n,ff},q}>>;
    if m neq 1 then go to t3;
    write "Cauchy principal value"; terpri();
    k := prepsq simp!* aeval
      {'minus,{'expt,{'quotient,-1,z},{'difference,1,p}}};
    q := prepsq simp!* aeval
      {'times,ff,{'quotient,'pi,{'times,a,n}},{'cot,{'times,'pi,p}}};
    return simp!* aeval {'times,k,q};
t3: return nil; % most (if not all) of these are divergence cases.
t2: return specformtestint2(den,num,var,llim,ulim) end;

symbolic procedure aynbmtst(exp,var);
  % test for form (a*y**n+b)**m (or degenerate forms of this) and
  % extract parameters a,n,b,m.  b qnd m are required to be present.
  % car exp = 'expt or else m=1.
   begin scalar a,b,m,n;
      if not eqcar(exp,'expt) then <<m := 1; goto a2>>;
      m := caddr exp;
      exp := cadr exp;
  a2: if not eqcar(exp,'plus) or length exp neq 3 then return nil;
      b := caddr exp;
      if eqcar(cadr exp,'times) then
         <<exp := cdadr exp;
           if length exp neq 2 or not(
             numberp (a := car exp)
               and (n := varpwrtst(cadr exp,var)) neq 0
             or
             numberp (a := cadr exp)
               and (n := varpwrtst(car exp,var)) neq 0)
             then return nil>>
        else
          <<a := 1;
            if (n := varpwrtst(cadr exp,var)) = 0 then return nil>>;
      return {a,b,n,m} end;

fluid '(!*fullpoly); switch fullpoly;

symbolic procedure getpoles(q,var,llim);
   begin scalar poles,rt,m,rlrt,cmprt,rtv,rtvz,cpv,prlrts,nrlrts,rlrts,
       cmprts,!*multiplicities,!*fullroots,!*norationalgi;
     off factor; !*norationalgi := poles!* := nil;
     !*multiplicities := t;
    if !*fullpoly then !*fullroots := t;
    % if !*allpoly = 'all then
    %   <<on rounded; rdon!* := t; write "test mode"; terpri()>>;
     poles := solvesq(simp!* prepf q,var,1);
     !*norationalgi := t;
 lp: if null poles then go to int;
     rt := car poles; poles := cdr poles; m := caddr rt;
     rlrt := cmprt := nil;
     if (rtv := valueof rt) then
        <<poles!* := (rtv . m) . poles!*;
          rtvz := zpsubsq rtv; rt := car impartsq rtvz;
          if null rt or
            (rt := topevalsetsq prepf rt) and evalequal(0,prepsq rt)
            then rlrt := rtv else cmprt := rtv;
          if llim = 0 then
             <<if rlrt then
                <<if null car rtvz then go to div
                  else if not genminusp car rtvz then
                     <<if m > 1 then go to div else cpv := t;
                       prlrts := (rlrt . m) . prlrts>>
                     else nrlrts := (rlrt . m) . nrlrts>>
               else cmprts := (cmprt . m) . cmprts; go to lp>>
          else
             <<if rlrt then
                  <<if m > 1 then go to div else cpv := t;
                    rlrts := (rlrt . m) . rlrts>>
               else if not genminusp car impartsq rtvz then
                  cmprts := (cmprt . m) . cmprts>>;
          go to lp>>;
una: if !*rounded then rederr "unable to find poles approximately";
     if not !*allpoly then <<write
        "Denominator degree > 4.  Approx integral requires ON ALLPOLY";
        terpri(); error(99,"failed")>>
        else <<write "approximate integral only"; terpri()>>;
     on rounded; rdon!* := t;
     if valueof car(rt := rdsolvesq rt) then
        <<poles := append(rt,poles); go to lp>>;
     go to una;
div: % write "integral diverges"; terpri();
     error(99,'failed);
int: if cpv then <<write "Cauchy principal value"; terpri()>>;
     return if llim=0 then {prlrts,nrlrts,cmprts}
         else {rlrts,nil,cmprts} end;

symbolic procedure specformtestint2(den,num,var,llim,ulim);
  % This checks for defint(x^k*R(x),x,0 inf) where {k != 0,-1<k<1} and
  % limit+(x^(k+1)*R(x),x,0)=limit(x^(k+1)*R(x),x,inf)=0 where R is
  % a rational function with no poles of order >1 on positive real axis.
   begin scalar d,k,k1,m,p,p1,q,q1,poles,prpoles,s1,s2;
     if not(llim=0) and ulim='inf then go to t2;
     p1 := polanalyz(num,var);
     k1 := polanalyz(den,var);
     if not (p1 or k1) then go to t2;
     k := prepsq simp!* aeval {'difference,p1,k1};
     if numberp k or not(evalgreaterp(k,-1) and evalgreaterp(1,k))
        then go to t2;%<==  this was t3 but caused problem!
     if (d := dmode!*) then onoff(d := get(d,'dname),nil);
     m := prepsq simp!* aeval {'quotient,{'times,var,num},den};
     if numr simp!* limit!+(m,var,0)
       or numr simp!* limit(m,var,'infinity) then go to t3;
     if d then onoff(d,t);
    % all tests met, except for finding poles of den.
    % move irrational factor to numerator, changing the sign of var.
     p := simp!* aeval {'times,num,
        {'expt,var,{'times,-1,p1}},{'expt,{'minus,var},k}};
    % note that p in general has a non-trivial denominator.
    % now remove irrational factor from denominator, leaving polynomial.
       q := simp!* aeval {'times,den,{'expt,var,{'times,-1,k1}}};
       q1 := diffsq(q,var);
     poles := getpoles(numr q,var,llim);
     prpoles := car poles; poles := append(cadr poles,caddr poles);
     s1 := s2 := nil ./ 1;
     k1 := {'times,'pi,{'plus,k,1}};
     if poles then
       <<for each r in poles do
           s1 := addsq(s1,residuum(p,q,q1,var,prepsq car r,cdr r));
         s1 := {'quotient,{'times,'pi,prepsq s1},{'sin,k1}}>>
       else s1 := 0;
     if prpoles then
       <<for each r in prpoles do
           s2 := addsq(s2,residuum(p,q,q1,var,prepsq car r,cdr r));
         s2 := {'times,'pi,prepsq s2,{'cot,k1}}>>
       else s2 := 0;
     return simp!* aeval {'difference,s1,s2};
 t2: return nil;  % replace by call to next test.
 t3: % write "integral diverges"; terpri();
     error(99,'failed)  end;

symbolic procedure polanalyz(exp,var);
  % test for fractional power of var in exp.
   if poltstp exp then
   ((if eqcar(
     exp := varpwrtst(if eqcar(ex2,'times) then cadr ex2 else ex2,var),
      'quotient) then exp else 0)
    where ex2=if eqcar(exp,'minus) then cadr exp else exp);

symbolic procedure poltstp exp;
   atom exp and exp or car exp member domainlist!* or
     car exp member '(plus times quotient minus expt sqrt) and
   begin scalar fg;
     for each c in cdr exp do if not poltstp c then fg := t;
     return null fg end;

symbolic procedure evalmax(a,b);
   if numberp a and numberp b then max(a,b)
     else if evalgreaterp(a,b) then a else b;

symbolic procedure evalplus(a,b);
   if numberp a and numberp b then a+b
   else prepsq simp!* aeval {'plus,a,b};

symbolic procedure varpwrtst(exp,var);
   if atom exp then if exp = var then 1 else 0
   else if car exp eq 'minus then varpwrtst(cadr exp,var)
   else if car exp member '(plus,difference) then
     (<<for each c in cdr exp do q := evalmax(q,varpwrtst(c,var)); q>>
      where q=0)
   else if eqcar(exp,'expt) then
     prepsq simp!* aeval{'times,varpwrtst(cadr exp,var),caddr exp}
   else if eqcar(exp,'sqrt) then
     prepsq simp!* aeval{'times,varpwrtst(cadr exp,var),{'quotient,1,2}}
   else if eqcar(exp,'times) then
     (<<for each c in cdr exp do q := evalplus(q,varpwrtst(c,var)); q>>
      where q=0)
   else 0;

endmodule;

end;
