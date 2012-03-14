module vdpcom;

% Common routines to all vdp mappings.

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


flag('(vdpprintmax),'share);
vdpprintmax:=5;

% Repeat of smacros defined in vdp2dip.

smacro procedure vdppoly u;cadr cddr u;

smacro procedure vdpzero!? u;null u or null vdppoly u;

smacro procedure vdpevlmon u;cadr u;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%  manipulating of exponent vectors
%

symbolic procedure vevnth(a,n);
% Extract nth element from 'a'.
if null a then 0 else if n=1 then car a else vevnth(cdr a,n #- 1);

% Unrolled code for zero test(very often called).
smacro procedure vevzero!? u;
  null u or(car u=0 and vevzero!?1 cdr u);

symbolic procedure vevzero!?1 u;
 null u or(car u=0 and vevzero!? cdr u);

symbolic procedure veveq(vev1,vev2);
 if null vev1 then vevzero!? vev2
 else if null vev2 then vevzero!? vev1
 else(car vev1=car vev2 and vevequal(cdr vev1,vev2));

symbolic procedure vevmaptozero e;
% Generate an exponent vector with same length as e and zeros only.
 vevmaptozero1(e,nil);

symbolic procedure vevmaptozero1(e,vev);
 if null e then vev else vevmaptozero1(cdr e,0 .vev);

symbolic procedure vevmtest!?(e1,e2);
% Exponent vector multiple test.'e1' and 'e2' are compatible exponent
% vectors.vevmtest?(e1,e2) returns a boolean expression.
% True if exponent vector 'e1' is a multiple of exponent
%    vector 'e2', else false.
 if null e2 then t else if null e1 then if vevzero!? e2 then t else nil
 else not(car e1 #< car e2)and vevmtest!?(cdr e1,cdr e2);

symbolic procedure vevlcm(e1,e2);
% Exponent vector least common multiple.'e1' and 'e2' are
% exponent vectors.'vevlcm(e1,e2)' computes the least common
% multiple of the exponent vectors 'e1' and 'e2', and returns
% an exponent vector.
 begin scalar x;
  while e1 and e2 do
  <<x:=(if car e1 #> car e2 then car e1 else car e2).x;
   e1:=cdr e1;e2:=cdr e2>>;
  x:=reversip x;
  if e1 then x:=nconc(x,e1)else if e2 then x:=nconc(x,e2);
  return x end;

symbolic procedure vevmin(e1,e2);
% Exponent vector minima.
 begin scalar x;
  while e1 and e2 do
  <<x:=(if car e1 #< car e2 then car e1 else car e2).x;
   e1:=cdr e1;e2:=cdr e2>>;
  while x and 0=car x do x:=cdr x;% Cut trailing zeros.
  return reversip x end;

symbolic procedure vevsum(e1,e2);
% Exponent vector sum.'e1' and 'e2' are exponent vectors.
% 'vevsum(e1,e2)' calculates the sum of the exponent vectors
% 'e1' and 'e2' componentwise and returns an exponent vector.
 begin scalar x;
  while e1 and e2 do
  <<x:=(car e1 #+ car e2).x;e1:=cdr e1;e2:=cdr e2>>;
   x:=reversip x;
   if e1 then x:=nconc(x,e1)else if e2 then x:=nconc(x,e2);
   return x end;

symbolic procedure vevtdeg u;
% Calculate the total degree of u.
 if null u then 0 else car u #+ vevtdeg cdr u;

symbolic procedure vdptdeg u;
 if vdpzero!? u then 0 else
  max(vevtdeg vdpevlmon u,vdptdeg vdpred u);

symbolic procedure vevdif(ee1,ee2);
% Exponent vector difference.'e1' and 'e2' are exponent
% vectors.'vevdif(e1,e2)' calculates the difference of the
% exponent vectors componentwise and returns an exponent vector.
 begin scalar x,y,break,e1,e2;
  e1:=ee1;e2:=ee2;
  while e1 and e2 and not break do
  <<y:=(car e1 #- car e2);x:=y.x;break:=y #< 0;
   e1:=cdr e1;e2:=cdr e2>>;
  if break or(e2 and not vevzero!? e2)then
  <<print ee1;print ee2;if getd 'backtrace then backtrace();
   return rerror(dipoly,5,"Vevdif, difference would be < 0")>>;
  return nconc(reversip x,e1)end;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% Numbering of polynomials.
%

symbolic procedure vdpenumerate f;
% 'f' is a temporary result.Prepare it for medium range storage
% and sign a number.
 if vdpzero!? f then f else
 <<f:=vdpsave f;
  if not vdpgetprop( f,'number)then
   f:=vdpputprop(f,'number,(pcount!*:=pcount!* #+ 1));f>>;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% SUGAR of polynomials.
%

symbolic procedure gsugar p;
 if !*gsugar then
(( s or
   <<print{"*** missing sugar",p};backtrace();
    s:=vdptdeg p;gsetsugar(p,s);s>>
  )where s= if vdpzero!? p then 0 else vdpgetprop(p,'sugar));

symbolic procedure gsetsugar(p,s);
 !*gsugar and vdpputprop(p,'sugar,s or vdptdeg p)or p;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% Operations on sets of polynomials.
%

symbolic procedure vdpmember(p1,l);
% Test membership of a polynomial in a list of polys.
 if null l then nil else
 if vdpequal(p1,car l)then l else vdpmember(p1,cdr l);

symbolic procedure vdpunion(s1,s2);
% 's1' and 's2' are two sets of polynomials;
% union of the sets using vdpMember as crit.
 if null s1 then s2 else
 if vdpmember(car s1,s2)then vdpunion(cdr s1,s2)
  else car s1.vdpunion(cdr s1,s2);

symbolic procedure vdpintersection(s1,s2);
% 's1' and 's2' are two sets of polynomials;
% intersection of the sets using vdpmember as crit.
 if null s1 then nil else
 if vdpmember(car s1,s2)then car s1.vdpunion(cdr s1,s2)
  else vdpunion(cdr s1,s2);

symbolic procedure vdpsetequal!?(s1,s2);
% Tests if 's1' and 's2' have the same polynomials as members.
 if not(length s1=length s2)then nil
  else vdpsetequal!?1(s1,append(s2,nil));

symbolic procedure vdpsetequal!?1(s1,s2);
% Destroys its second parameter(is therefore copied when called).
 if null s1 and null s2 then t else
 if null s1 or null s2 then nil else
(if hugo then vdpsetequal!?1(cdr s1,groedeletip(car hugo,s2))
  else nil)where hugo=vdpmember(car s1,s2);

symbolic procedure vdpsortedsetequal!?(s1,s2);
% Tests if 's1' and 's2' have the same polynomials as members
% here assuming, that both sets are sorted by the same principles.
 if null s1 and null s2 then t else if null s1 or null s2 then nil else
 if vdpequal(car s1,car s2)then
  vdpsortedsetequal!?(cdr s1,cdr s2)else nil;

symbolic procedure vdpdisjoint!?(s1,s2);
% 's1' and 's2' are two sets of polynomials;
% test that there are no common members.
 if null s1 then t else
 if vdpmember(car s1,s2)then nil else vdpdisjoint!?(cdr s1,s2);

symbolic procedure vdpsubset!?(s1,s2);
 not(length s1 > length s2)and vdpsubset!?1(s1,s2);

symbolic procedure vdpsubset!?1(s1,s2);
% 's1' and 's2' are two sets of polynomials.
% Test if 's1' is subset of 's2'.
 if null s1 then t else
 if vdpmember(car s1,s2)then vdpsubset!?1(cdr s1,s2)else nil;

symbolic procedure vdpdeletemember(p,l);
% Delete polynomial 'p' from list 'l'.
 if null l then nil else
 if vdpequal(p,car l)then vdpdeletemember(p,cdr l)
  else car l.vdpdeletemember(p,cdr l);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% Sorting of polynomials.
%

symbolic procedure vdplsort pl;
% Distributive polynomial list sort.'pl' is a list of
% distributive polynomials.'vdplsort(pl)' returns the
% sorted distributive polynomial list of pl.
 sort(pl,function vdpvevlcomp);

symbolic procedure vdplsortin(p,pl);
% 'p' is a polynomial, 'pl' is a list of polynomials.
% 'p' is inserted into 'pl' at its place determined by vevlcompless?.
% The result is the updated pl.
 if null pl then{p}else<<vdplsortin1(p,pl,pl);pl>>;

symbolic procedure vdplsortin1(p,pl,oldpl);
 if null pl then cdr oldpl:= p.nil
  else if vevcompless!?(vdpevlmon p,vdpevlmon car pl)
   then  vdplsortin1(p,cdr pl,pl)
  else <<cdr pl:=car pl.cdr pl;car pl:=p>>;

symbolic procedure vdplsortinreplacing(po,pl);
% 'po' is a polynomial, 'pl' is a linear list of polynomials(sorted).
% 'po' is inserted into 'pl' at its place determined by 'vevlcompless?'.
% If there is a multiple of the first exponent of a polynomial in 'pl',
% this one is deleted from 'pl'.The result is the updated 'pl'.
% 'opl' is the initial value of 'pl',(initial multiples of 'po' are
% removed);'oopl' a working version of 'opl'.
 begin scalar oopl,opl;if pl then go to bb;
aa : return po.nil;
bb : if vevdivides!?(vdpevlmon po,vdpevlmon car pl)then
  <<pl:=cdr pl;if null pl then go to aa;go to bb>>;
  opl:=pl;
cc : if null pl then
    <<oopl:=lastpair opl;cdr oopl:=po.nil;return opl>>;
  if not(pl eq opl)and vevdivides!?(vdpevlmon po,vdpevlmon car pl)then
   <<if null cdr pl then
       <<oopl:=lastpair1 opl;cdr oopl:=nil;pl:=nil>>
        else <<car pl:=cadr pl;cdr pl:=cddr pl>>; go to cc>>;
  if vevcompless!?(vdpevlmon po,vdpevlmon car pl)then
  <<pl:=cdr pl;go to cc>>;
   cdr pl:=car pl.cdr pl;car pl:=po;% Insert 'po'.
  return opl end;

symbolic procedure lastpair1 opl;
% Determine the last full pair(the 'cdr' non-nil)of the linear list
% 'opl';if the routine is called with 'nil' or cdr='nil',
% return 't'.
 if null opl or null cdr opl then t else
  <<while cddr opl do opl:=cdr opl;opl>>;

symbolic procedure countlastvar(a,m);
% Count the monomials with the last variable of a vdp-polynomial
% 'a';'m' determines, whether the first('m' is true)non-factor
% of the last variable leads to the result '0';if the polynomial has more
% than 25 elements, return '0' if 'm' is false or if the polynomial has
% more than 25 terms(divisible by the last variable).
 begin integer n,nn;a:=vdppoly a;
aa: if atom a then return n;
  nn:=nn #+ 1;if nn #> 25 then return 0;n:=n #+ 1;
  if countlastvar1 dipevlmon a #< 1 then(if m then return 0 else n:=0);
  a:=dipmred a;go to aa end;

symbolic procedure countlastvar1 b;
 begin scalar n;n:=1;
aa : if atom b then return 0 else if n=vdplastvar!* then return car b;
  b:=cdr b;n:=n #+ 1;go to aa end;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% Property lists for polynomials.
%

symbolic procedure vdpputprop(poly,prop,val);
 begin scalar c,p;
  if not pairp poly or not pairp(c:=cdr poly)or not pairp(c:=cdr c)
   or not pairp(c:=cdr c)or not pairp(c:=cdr c)
    then rerror(dipoly,6,
    {"vdpputprop given a non-vdp as 1st parameter",poly,prop,val});
  p:=assoc(prop,car c);
  if p then rplacd(p,val)else rplaca(c,(prop.val).car c);
  return poly end;

symbolic procedure vdpgetprop(poly,prop);
 if null poly then nil  % nil is a legal variant of vdp=0
  else if not eqcar(poly,'vdp)
    then rerror( dipoly,7,
    {"vdpgetprop given a non-vdp as 1st parameter",
      poly,prop})
  else(if p then cdr p else nil)
      where p=assoc(prop,cadr cdddr poly);

symbolic procedure vdpremallprops u;
 begin scalar c;
 if not(not pairp u or not pairp(c:=cdr u)or not pairp(c:=cdr c)
  or not pairp(c:=cdr c)or not pairp(c:=cdr c))
   then rplaca(c,nil);return u end;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% Groebner interface to power substitution.
%

fluid'(!*sub2);

symbolic procedure groebsubs2 q;(subs2 q)where !*sub2=t;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% And a special print.
%

symbolic procedure vdpprintshort u;
 begin scalar m;
  m:=vdpprintmax;vdpprintmax:= 2;vdpprint u;vdpprintmax:=m end;

endmodule;;end;
