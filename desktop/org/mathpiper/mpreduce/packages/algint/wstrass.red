module wstrass;

% Author: James H. Davenport.

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


fluid '(!*exp
        !*gcd
        !*mcd
        !*structure
        !*uncached
        !*keepsqrts        % Forced SIMP to keep square roots around
        !*tra
        !*trmin
        intvar
        listofallsqrts
        listofnewsqrts
        magiclist
        previousbasis
        sqrt!-intvar
        sqrtflag
        sqrts!-in!-integrand
        taylorasslist
        taylorvariable
        thisplace
        zlist);

global '(coates!-fdi);

exports simpwstrass,weierstrass!-form;

imports gcdn,sqrtsinplaces,
        makeinitialbasis,mkvec,completeplaces,integralbasis,
        normalbasis,mksp,multsq,xsubstitutesq,taylorform,taylorevaluate,
        coatessolve,checkpoles,substitutesq,removecmsq,printsq,interr,
        terpri!*,printplace,finitise,fractional!-degree!-at!-infinity,
        !*multsq,fdi!-print,fdi!-upgrade,fdi!-revertsq,simp,newplace,
        xsubstitutep,sqrtsinsq,removeduplicates,!*exptf,!*multf,
        !*multsq,!*q2f,mapvec,upbv,coates!-lineq,addsq,!*addsq;

symbolic procedure simpwstrass u;
begin
  scalar intvar,sqrt!-intvar,taylorvariable,taylorasslist;
  scalar listofallsqrts,listofnewsqrts;
  scalar sqrtflag,sqrts!-in!-integrand,tt,u;
  scalar !*keepsqrts,!*exp,!*gcd,!*mcd,!*structure,!*uncached;
  !*keepsqrts:=t;     % Else nothing will work
  !*exp := !*gcd := !*mcd := !*uncached := t;
  !*structure := nil;  % Algebraic code certainly wants this off:
                       % keeping it on inhibits sqrt(z)^2 -> z
  tt:=readclock();
  sqrtflag:=t;
  taylorvariable:=intvar:=car u;
  sqrt!-intvar:=mvar !*q2f simpsqrti intvar;
  u:=for each v in cdr u
            collect int!-simp v;
  sqrts!-in!-integrand:=sqrtsinsql(u,intvar);
  u:=errorset!*('(weierstrass!-form sqrts!-in!-integrand),t);
  if atom u
    then return u
    else u:=car u;
  printc list('time,'taken,readclock()-tt,'milliseconds);
  printc "New x value is:";
  printsq car u;
  u:=cdr u;
  printc "New y value is:";
  printsq car u;
  u:=cdr u;
  printc "Related by the equation";
  printsq car u;
  return car u
  end;
put('wstrass,'simpfn,'simpwstrass);

symbolic procedure weierstrass!-form sqrtl;
begin
  scalar sqrtl2,u,x2,x1,vect,a,b,c,d,lhs,rhs;
  if !*tra or !*trmin then <<
    printc "Find Weierstrass form for elliptic curve defined by:";
    for each u in sqrtl do
      printsq simp u >>;
  sqrtl2:=sqrts!-at!-infinity sqrtl;
  sqrtl2:=append(car sqrtl2,
                 for each u in cdr sqrtl2 collect
                   u.u);
          % one of the places lying over infinity
          % (after deramification as necessary).
  x2:=coates!-wstrass(list sqrtl2,list(-3),intvar);
    % Note that we do not multiply by the MULTIPLICITY!-FACTOR
    % since we genuinely want a pole of order -3 irrespective
    % of any ramification problems.
  if !*tra then <<
    printc "Function with pole of order 3 (x2) is:";
    printsq x2 >>;
  x1:=coates!-wstrass(list sqrtl2,list(-2),intvar);
  if !*tra then <<
    printc "Function with pole of order 2 (x1) is:";
    printsq x1 >>;
  vect := mkvec list(1 ./ 1,
                  x1,
                  x2,
                  !*multsq(x1,x1),
                  !*multsq(x2,x2),
                  !*multsq(x1,!*multsq(x1,x1)),
                  !*multsq(x1,x2));
  u:=!*lcm!*(!*exptf(denr x1,3),!*multf(denr x2,denr x2)) ./ 1;
  for i:=0:6 do
    putv(vect,i,!*q2f !*multsq(u,getv(vect,i)));
  if !*tra then <<
    printc "List of seven functions in weierstrass!-form:";
    mapvec(vect,function printsf) >>;
  vect := wstrass!-lineq vect;
  if vect eq 'failed then
    interr "Linear equation solving failed in Weierstrass";
% printsq(addsq(getv(vect,0),addsq(!*multsq(getv(vect,1),x1),
%               addsq(!*multsq(getv(vect,2),x2),
%                     addsq(!*multsq(getv(vect,3),!*multsq(x1,x1)),
%                          addsq(!*multsq(getv(vect,4),!*multsq(x2,x2)),
%                             addsq(!*multsq(getv(vect,5),exptsq(x1,3)),
%                                       !*multsq(getv(vect,6),
%                                               !*multsq(x1,x2)))))))));
  x2:=!*addsq(!*multsq(!*multsq(2 ./ 1,getv(vect,4)),x2),
              !*addsq(!*multsq(x1,getv(vect,6)),
                    getv(vect,2)));
  putv(vect,4,!*multsq(-4 ./ 1,getv(vect,4)));
  a:=!*multsq(getv(vect,4),getv(vect,5));
  b:=!*addsq(!*multsq(getv(vect,6),getv(vect,6)),
             !*multsq(getv(vect,3),getv(vect,4)));
  c:=!*addsq(!*multsq(2 ./ 1,!*multsq(getv(vect,2),getv(vect,6))),
             !*multsq(getv(vect,1),getv(vect,4)));
  d:=!*addsq(!*multsq(getv(vect,2),getv(vect,2)),
             !*multsq(getv(vect,0),getv(vect,4)));
  lhs:=!*multsq(x2,x2);
  rhs:=!*addsq(d,!*multsq(x1,
                          !*addsq(c,!*multsq(x1,
                                          !*addsq(b,!*multsq(x1,a))))));
  if lhs neq rhs then <<
    printsq lhs;
    printsq rhs;
    interr "Previous two unequal - consistency failure 1" >>;
  u:=!*lcm!*(!*lcm!*(denr a,denr b),!*lcm!*(denr c,denr d));
  if u neq 1 then <<
    % for now use u**2 whereas we should be using the least
    % square greater than u**2 (does it really matter).
    x2:=!*multsq(x2,u ./ 1);
    u:=!*multf(u,u) ./ 1;
    a:=!*multsq(a,u);
    b:=!*multsq(b,u);
    c:=!*multsq(c,u);
    d:=!*multsq(d,u) >>;
  if (numr b) and not (quotf(numr b,3)) then <<
    % multiply all through by 9 for the hell of it.
    x2:=multsq(3 ./ 1,x2);
    u:=9 ./ 1;
    a:=multsq(a,u);
    b:=multsq(b,u);
    c:=multsq(c,u);
    d:=multsq(d,u) >>;
  x2:=!*multsq(x2,a);
  x1:=!*multsq(x1,a);
  c:=!*multsq(a,c);
  d:=!*multsq(!*multsq(a,a),d);
  lhs:=!*multsq(x2,x2);
  rhs:=!*addsq(d,!*multsq(x1,!*addsq(c,!*multsq(x1,!*addsq(b,x1)))));
  if lhs neq rhs then <<
    printsq lhs;
    printsq rhs;
    interr "Previous two unequal - consistency failure 2" >>;
  b:=quotf(numr b,3) ./ 1;
  x1:=!*addsq(x1,b);
  d:=!*addsq(d,!*addsq(multsq(2 ./ 1,!*multsq(b,!*multsq(b,b))),
                       negsq !*multsq(c,b)));
  c:=!*addsq(c,multsq((-3) ./ 1,!*multsq(b,b)) );
% b:=nil ./ 1;   % not used again.
  if !*tra then <<
    printsq x2;
    printsq x1;
    printc "with coefficients";
    printsq c;
    printsq d;
    rhs:=!*addsq(d,
                 !*addsq(!*multsq(c,x1),
                         !*multsq(x1,!*multsq(x1,x1)) ));
    lhs:=!*multsq(x2,x2);
    if lhs neq rhs then <<
      printsq lhs;
      printsq rhs;
      interr "Previous two unequal - consistency failure 3" >> >>;
    return weierstrass!-form1(c,d,x1,x2)
   end;

symbolic procedure !*lcm!*(u,v); !*multf(u,quotf(v,gcdf(u,v)));

symbolic procedure weierstrass!-form1(c,d,x1,x2);
 begin scalar b,u;
  u:=gcdf(numr c,numr d);
    % We will reduce by anything whose square divides C
    % and whose cube divides D.
  if not numberp u then begin
    scalar cc,dd;
    u:=jsqfree(u,mvar u);
    u:=cdr u;
    if null u
      then return;
      % We found no repeated factors.
    for each v in u do
      for each w in v do
        while (cc:=quotf(numr c,multf(w,w))) and
              (dd:=quotf(numr d,exptf(w,3)))
          do <<
            c:=cc ./ 1;
            d:=dd ./ 1;
            x1:=!*multsq(x1,1 ./ w);
            x2:=!*multsq(x2,1 ./ multf(w,simpsqrt2 w)) >>;
    u:=gcdn(algint!-numeric!-content numr c,
            algint!-numeric!-content numr d)
    end;
  b:=2;
 while not(b*b > u) do begin
    scalar nc,nd,uu;
    nc:=0;
    while cdr(uu:=divide(u,b))=0 do <<
      nc:=nc+1;
      u:=car uu >>;
    if nc < 2
      then go to next;
    uu:=algint!-numeric!-content numr d;
    nd:=0;
    while cdr(uu:=divide(uu,b))=0 do <<
      nd:=nd+1;
      uu:=car uu >>;
    if nd < 3
      then go to next;
    nc:=min(nc/2,nd/3);
      % re-normalise by b**nc.
    uu:=b**nc;
    c:=multsq(c,1 ./ (uu**2));
    d:=multsq(d,1 ./ (uu**3));
    x1:=multsq(x1,1 ./ uu);
    x2:=multsq(x2,1 ./ (uu*b**(nc/2)) );
    if not evenp nc
      then x2:=!*multsq(x2,!*invsq simpsqrti b);
next:
    b:=nextprime(b)
    end;
  u:=!*kk2q intvar;
  u:=!*addsq(!*addsq(d,multsq(c,u)),exptsq(u,3));
  if !*tra or !*trmin then <<
    printc "Standard form is y**2 = ";
    printsq u >>;
  return list(x1,x2,simpsqrtsq u)
  end;

symbolic procedure sqrts!-at!-infinity sqrtl;
begin
  scalar inf,hack,sqrtl2,repeating;
  hack:=list list(intvar,'expt,intvar,2);
  inf:=list list(intvar,'quotient,1,intvar);
  sqrtl2:=list sqrt!-intvar;
  while sqrt!-intvar member sqrtl2 do <<
    if repeating
      then inf:=append(inf,hack);
    newplace inf;
    sqrtl2:=for each v in sqrtl conc
      sqrtsinsq(xsubstitutep(v,inf),intvar);
    repeating:=t >>;
  sqrtl2:=removeduplicates sqrtl2;
  return inf.sqrtl2
  end;

symbolic procedure coates!-wstrass(places,mults,x);
begin
  scalar thisplace,u,finite!-hack,save,places2,mults2;
  if !*tra or !*trmin then <<
    princ "Find function with zeros of order:";
    printc mults;
    if !*tra then
      princ " at ";
    terpri!*(t);
    if !*tra then
      mapc(places,function printplace) >>;
%  finite!-hack:=placesindiv places;
    % FINITE!-HACK is a list of all the substitutors in PLACES;
%  u:=removeduplicates sqrtsintree(finite!-hack,x,nil);
%  if !*tra then <<
%    princ "Sqrts on this curve:";
%    terpri!*(t);
%    superprint u >>;
%  algnos:=removeduplicates for each j in places collect basicplace j;
%  if !*tra then <<
%    printc "Algebraic numbers where residues occur:";
%    superprint algnos >>;
  finite!-hack:= finitise(places,mults);
    % returns list (places,mults,power of x to remove.
  places2:=car finite!-hack;
  mults2:=cadr finite!-hack;
  finite!-hack:=list(places,mults,caddr finite!-hack);
  coates!-fdi:=fractional!-degree!-at!-infinity u;
  if coates!-fdi iequal 1
    then return !*multsq(wstrassmodule(places2,mults2,x,finite!-hack),
                         !*p2q mksp(x,caddr finite!-hack));
  if !*tra
    then fdi!-print();
  newplace list(intvar . thisplace,
                list(intvar,'expt,intvar,coates!-fdi));
  places2 := for each j in places2 collect fdi!-upgrade j;
  save:=taylorasslist;
  u:=wstrassmodule(places2,
                   for each u in mults2 collect u*coates!-fdi,
                   x,finite!-hack);
  taylorasslist:=save;
  u:=fdi!-revertsq u;
  return !*multsq(u,!*p2q mksp(x,caddr finite!-hack))
  end;

symbolic procedure wstrassmodule(places,mults,x,finite!-hack);
begin
  scalar pzero,mzero,u,v,basis,sqrts,magiclist,mpole,ppole;
    % MAGICLIST holds the list of extra unknowns created in JHDSOLVE
    % which must be found in CHECKPOLES (calling FINDMAGIC).
  sqrts:=sqrtsinplaces places;
  if !*tra then <<
    princ "Sqrts on this curve:";
    superprint sqrts >>;
  u:=places;
  v:=mults;
  while u do <<
    if 0<car v
      then <<
        mzero:=(car v).mzero;
        pzero:=(car u).pzero >>
      else <<
        mpole:=(car v).mpole;
        ppole:=(car u).ppole >>;
    u:=cdr u;
    v:=cdr v >>;
  basis:=mkvec makeinitialbasis ppole;
  u:=completeplaces(ppole,mpole);
  basis:=integralbasis(basis,car u,cdr u,x);
  basis:=normalbasis(basis,x,0);
  u:=coatessolve(mzero,pzero,basis,force!-pole(basis,finite!-hack));
    % This is the list of special constraints needed
    % to force certain poles to occur in the answer.
 previousbasis:=nil;
  if atom u
    then return u;
  v:= checkpoles(list u,places,mults);
  if null v
    then return 'failed;
  if not magiclist
    then return u;
  u:=removecmsq substitutesq(u,v);
  % Apply the values from FINDMAGIC.
  if !*tra or !*trmin then <<
    printc "Function is";
    printsq u >>;
  magiclist:=nil;
  if checkpoles(list u,places,mults)
    then return u
    else interr "Inconsistent checkpoles"
  end;

symbolic procedure force!-pole(basis,finite!-hack);
begin
  scalar places,mults,u,ans;
  places:=car finite!-hack;
  mults:=cadr finite!-hack;
  finite!-hack:=caddr finite!-hack;
  u:=!*p2q mksp(intvar,finite!-hack);
  basis:=for each v in basis collect multsq(u,v);
  while places do <<
    u:=for each v in basis collect
       taylorevaluate(taylorform xsubstitutesq(v,car places),
                      car mults);
    mults:=cdr mults;
    places:=cdr places;
    ans:=u.ans >>;
  return ans
  end;

symbolic procedure wstrass!-lineq vect;
begin
  scalar zlist,powlist,m,rightside,v;
  scalar zero,one;
  zero:=nil ./ 1;
  one:=1 ./ 1;
  for i:=0:6 do
    zlist:=varsinsf(getv(vect,i),zlist);
  zlist:=intvar . findzvars(zlist,nil,intvar,nil);
  for i:=0:6 do
    putv(vect,i,f2df getv(vect,i));
  for i:=0:6 do
    for each u in getv(vect,i) do
      if not ((tpow u) member powlist)
        then powlist:=(tpow u).powlist;
  m:=for each u in powlist collect begin
    scalar v;
    v:=mkvect 6;
    for i:=0:6 do
      putv(v,i,(lambda u;
                  if null u
                    then zero
                    else tc u)
                 assoc(u,getv(vect,i)));
    return v
    end;
  v:=mkvect 6;
  for i:=0:6 do
    putv(v,i,zero);
  putv(v,4,one);
    % we know that coefficient e is non-zero.
  m:=mkvec (v.m);
  v:=upbv m;
  rightside:=mkvect v;
  putv(rightside,0,one);
  for i:=1:v do
    putv(rightside,i,zero);
  return coates!-lineq(m,rightside)
  end;

% This is same as NUMERIC!-CONTENT in the EZGCD module, but is included
% here so that that module doesn't need to be loaded.

symbolic procedure algint!-numeric!-content form;
 %Find numeric content of non-zero polynomial.
   if domainp form then abs form
   else if null red form then algint!-numeric!-content lc form
   else begin
     scalar g1;
     g1 := algint!-numeric!-content lc form;
     if not (g1=1)
       then g1 := gcddd(g1,algint!-numeric!-content red form);
     return g1
   end;

endmodule;

end;
