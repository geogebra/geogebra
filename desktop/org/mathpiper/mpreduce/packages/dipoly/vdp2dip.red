module vdp2dip;

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


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% interface for Virtual Distributive Polynomials(VDP)
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% "Distributive representation" with respect to a given set of
% variables(" vdpvars ")means for a polynomial, that the polynomial
% is regarded as  a sequence of monomials, each of which is a
% product of a " coefficient " and of some powers of the variables.
% This internal representation is very closely connected to the
% standard external(printed)representation of a polynomial in
% REDUCE if nothing is factored out. The monomials are logically
% ordered by a term order mode based on the ordering which is
% given bye the sequence " vdpvars ";with respect to this ordering
% the representation of a polynomial is unique. The " highest " term
% is the car one. Monomials are represented by their coefficient
%(" vbc ")and by a vector of the exponents(" vev ")(in the order
% corresponding to the vector vars). The distributive representation
% is good for those algorithms,which base their decisions on the
% complete ledading monomial: this representation guarantees a
% fast and uniform access to the car monomial and to the reductum
%(the cdr of the polynomial beginning with the cadr monomial).
% The algorithms of the Groebner package are of this type. The
% interface defines the distributive polynomials as abstract data
% objects via their acess functions. These functions map the
% distributive operations to an arbitrary real data structure
%(" virtual "). The mapping of the access functions to an actual
% data structure is restricted only by the demand,that the typical
% " distributive operations " be efficient. Additionally to the
% algebraic value a VDP object has a property list. So the algorithms
% using the VDP interface can assign name - value - pairs to individual
% polynomials. The interface is defined by a set of routines which
% create and handle the distributive polynomials. In general the
% first letters of the routine name classifies the data its works on:
%
%   vdp...      complete virtual polynomial objects
%   vbc...      virtual base coefficients
%   vev...      virtual exponent vectors
%
% 0. general control
%
%   vdpinit(dv)initialises the vdp package for the variables
%              given in the list 'dv'. vdpinit modifies the
%              torder and returns the prvevious torder as its
%              result. 'vdpinit' sets the global variable
%              'vdpvars!*'.
%
% 1. Conversion
%
%   a2vdp      Algebraic(prefix)to vdp.
%   f2vdp      Standard form to vdp.
%   a2vbc      Algebraic(prefix)to vbc.
%   vdp2a      Vdp to algebraic(prefix).
%   vdp2f      Vdp to standard form.
%   vbc2a      Vbc to algebraic(prefix).
%
% 2. Composing/decomposing
%
%   vdpfmon    Make a vdp from a vbc and an vev.
%   vdpmoncomp Add a monomial(vbc and vev)to the front of a vdp.
%   vdpappendmon Add a monomial(vbc and vev)to the bottom of a vdp.
%   vdpmonadd  Add a monomial(vbc and vev)to a vdp,not yet
%              knowing the place of the insertiona.
%   vdpappendvdp Concat two vdps.
%
%   vdplbc     Extract leading vbc.
%   vdpevlmon  Extract leading vev.
%   vdpred     Reductum of vdp.
%   vdplastmon Last monomial of polynomial.
%   vevnth     Nth element from exponent vector.
%
% 3. Testing
%
%   vdpzero?    Test vdp = 0.
%   vdpredzero!? Test rductum of vdp = 0.
%   vdpone?     Test vdp = 1.
%   vevzero?    Test vev =(0 0 ... 0).
%   vbczero?    Test vbc = 0.
%   vbcminus?   Test vbc <= 0(not decidable for algebraic vbcs).
%   vbcplus?    Test vbc >= 0(not decidable for algebraic vbcs).
%   vbcone!?    Test vbc = 1.
%   vbcnumberp  Test vbc is a numeric value.
%   vevdivides? Test if vev1 < vev2 elementwise.
%   vevlcompless?  Test ordering vev1 < vev2.
%   vdpvevlcomp    Calculate ordering vev1 / vev1 : -1, 0 or +1.
%   vdpequal   Test vdp1 = vdp2.
%   vdpmember  Member based on " vdpequal ".
%   vevequal   Test vev1 = vev2.
%
% 4. Arithmetic
%
% 4.1 Vdp arithmetic
%
%  vdpsum       vdp + vdp
%               Special routines for monomials : see above(2.).
%  vdpdif       vdp - vdp.
%  vdpprod      vdp * vdp.
%  vdpvbcprod   vbc * vdp.
%  vdpdivmon    vdp /(vbc,vev) divisability presumed.
%  vdpcancelvev Substitute all multiples of monomial(1,vev)in vdp by 0.
%  vdlLcomb1    vdp1 *(vbc1,vev1)+ vdp2 *(vbc2,vev2).
%  vdpcontent   Calculate gcd over all vbcs.
%
% 4.2 Vbc arithmetic
%
%  vbcsum       vbc1 + vbc2.
%  vbcdif       vbc1 - vbc2.
%  vbcneg       - vbc.
%  vbcprod      vbc1 * vbc2.
%  vbcquot      vbc1 / vbc2     Divisability assumed if domain = ring.
%  vbcinv       1 / vbc         Only usable in field.
%  vbcgcd       gcd(vbc1,vbc2)  Only usable in Euclidean field.
%
% 4.2 Vev arithmetic
%
% vevsum        vev1 + vev2 Elementwise.
% vevdif        vev1 - vev2 Elementwise.
% vevtdeg       Sum over all exponents.
% vevzero       Generate a zero vev.
%
% 5. Auxiliary
%
% vdpputprop   Assign indicator - value - pair to vdp.
%              The property " number " is used for printing.
% vdpgetprop   Read value of indicator from vdp.
% vdplsort     Sort list of polynomials with respect to ordering.
% vdplsortin   Sort a vdp into a sorted list of vdps.
% vdpprint     Print a vdp together with its number.
% vdpprin2t    Print a vdp " naked ".
% vdpprin3t    Print a vdp with closing ";".
% vdpcondense  Replace exponent vectors by equal objects from
%              global list dipevlist!* in order to save memory.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% RECORD STRUCTURE
%
% A virtual polynomial here is a record(list) with the entries
%  ('vdp < vdpevlmon > < vdplbc > < form > < plist >)
%
%    ' vdp        A type tag;
%     < vdpevlmon > the exponents of the variables in the
%                 leading monomial;the positions correspond to
%                 the positions in vdpvars!*. Trailing zeroes
%                 can be omitted.
%
%     < lcoeff >  The " coefficient " of the leading monomial,which
%                 in general is a standard form.
%
%     < form >    The complete polynomial,e.g. as REDUCE standard form.
%
%     < plist >   An asso list for the properties of the polynomial.
%
% The components should not be manipulated only via the interface
% functions and macros,so that application programs remain
% independent from the internal representation.
% The only general assumption made on < form > is,that the zero
% polynomial is represented as NIL. That is the case e. g. for both,
% REDUCE standard forms and DIPOLYs.
%
% Conventions for the usage:
% -------------------------
%
%    vdpint has to be called prveviously to all vdp calls. The list of
%    vdp paraemters is passed to vdpinit. The value of vdpvars!*
%    and the current torder must remain unmodfied afterwards.
%    usual are simple id's,e.g.
%
% Modifications to vdpvars!* during calculations
% ----------------------------------------------
%
% This mapping of vdp operations to standard forms offers the
% ability to enlarge vdpvars during the calculation in order
% to add new(intermediate)variables. Basis is the convention,
% that exponent vectors logically have an arbitrary number
% of trailing zeros. All routines processing exponent vectors
% are able to handle varying length of exponent vectors.
% A new call to vdpinit is necessary.
%
% During calculation vdpvars may be enlarged(new variables
% suffixed)without needs to modify existing polynomials;only
% korder has to be set to the new variable sequence.
% modifications to the sequence in vdpvars requires a
% new call to vdpinit  and a reordering of exisiting
% polynomials,e.g. by
%          vdpint newvdpvars;
%          f2vdp vdp2f p1;f2vdp vdp2f p2;.....

% Modification 14.9.2004:
% ----------------------
% Test parmeter expresssions (including the parameters of the coefficient
% functions) for the non-occurrence of groebner variables.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% DECLARATION SECTION
%
%  This module must be present during code generation for modules
%  using the vdp - sf interface.

global '(vdpprintmax groebmonfac);

flag('(vdpprintmax),'share);

% Basic internal constructor of vdp-record:

smacro procedure makevdp(vbc,vev,form);
 {'vdp,vev,vbc,form,nil};

% Basic selectors(conversions):

smacro procedure vdppoly u;cadr cddr u;

smacro procedure vdplbc u;caddr u;

smacro procedure vdpevlmon u;cadr u;

% Basic tests:

smacro procedure vdpzero!? u;null u or null vdppoly u;

smacro procedure vevzero!? u;
 null u or(car u=0 and vevzero!?1 cdr u);

smacro procedure vdpone!? p;
 not vdpzero!? p and vevzero!? vdpevlmon p;

% Manipulating of exponent vectors.

smacro procedure vevdivides!?(vev1,vev2);vevmtest!?(vev2,vev1);

smacro procedure vevzero();vevmaptozero1(vdpvars!*,nil);

smacro procedure vdpnumber f;vdpgetprop(f,'number);

% The code for checkpointing is factored out.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% Interface for DIPOLY polynomials as records(objects).
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%

flag('(vdpprintmax),'share);

symbolic procedure dip2vdp u;
 % Is used when u can be empty.
 (if dipzero!? uu then makevdp(a2bc 0,nil,nil)
                    else makevdp(diplbc uu,dipevlmon uu,uu))
           where uu=if !*groebsubs then dipsubs2 u else u;

% Some simple mappings:

smacro procedure makedipzero();nil;

symbolic procedure vdpredzero!? u;dipzero!? dipmred vdppoly u;

symbolic procedure vdplastmon u;
 % Return bc. ev of last monomial of u.
  begin u:=vdppoly u;
    if dipzero!? u then return nil;
    while not dipzero!? u and not dipzero!? dipmred u do u:=dipmred u;
    return diplbc u.dipevlmon u end;

symbolic procedure vbczero!? u;bczero!? u;

symbolic procedure vbcnumber u;
 if pairp u and numberp car u and 1=cdr u then cdr u else nil;

symbolic procedure vbcfi u;bcfd u;

symbolic procedure a2vbc u;a2bc u;

symbolic procedure vbcquot(u,v);bcquot(u,v);

symbolic procedure vbcneg u;bcneg u;

symbolic procedure vbcabs u;if vbcminus!? u then bcneg u else u;

symbolic procedure vbcone!? u;bcone!? u;

symbolic procedure vbcprod(u,v);bcprod(u,v);

% Initializing vdp - dip polynomial package.
symbolic procedure vdpinit2 vars;
 begin scalar oldorder;vdpcleanup();
  oldorder:=kord!*;
  if null vars then rerror(dipoly,8,"vdpinit: vdpvars not set");
  vdpvars!*:=dipvars!*:=vars;torder2 vdpsortmode!*;
  return oldorder end;

symbolic procedure vdpcleanup();dipevlist!*:={nil};

symbolic procedure vdpred u;
 begin scalar r,s;r:=dipmred vdppoly u;
  if dipzero!? r then return makevdp(nil ./ nil,nil,makedipzero());
  r:=makevdp(diplbc r,dipevlmon r,r);
  if !*gsugar and(s:=vdpgetprop(u,'sugar))then gsetsugar(r,s);
  return r end;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%  Coefficient handling;here we assume that coefficients are
%  standard quotients.
%

symbolic procedure vbcgcd(u,v);
begin scalar x;
 if not vbcsize(u,-100)or not vbcsize(v,-100)
  then return '(1 . 1);
  x:=if denr u=1 and denr v=1 then
   if fixp numr u and fixp numr v then gcdn(numr u,numr v) ./ 1
            else gcdf!*(numr u,numr v)./ 1
    else 1 ./ 1;
  return x end;

symbolic procedure vbcsize(u,n);
 if n #> -1 then nil
  else if atom u then n
  else begin n:=vbcsize(car u,n #+ 1);
  if null n then return nil;return vbcsize(cdr u,n)end;

% Cofactors: compute(q,v)such that q*a=v*b.

symbolic procedure vbc!-cofac(bc1,bc2);
% Compute base coefficient cofactors.
<<if vbcminus!? bc1 and vbcminus!? bc2 then gcd:=vbcneg gcd;
 vbcquot(bc2,gcd). vbcquot(bc1,gcd)>>
  where gcd=vbcgcd(bc1,bc2);

symbolic procedure vev!-cofac(ev1,ev2);
% Compute exponent vector cofactors.
(vevdif(lcm,ev1).vevdif(lcm,ev2))
 where lcm=vevlcm(ev1,ev2);

% The following functions must be redefinable.

symbolic procedure vbcplus!? u;(numberp v and v > 0)where v=numr u;

symbolic procedure bcplus!? u;(numberp v and v > 0)where v=numr u;

symbolic procedure vbcminus!? u;(numberp v and v < 0)where v=numr u;

symbolic procedure vbcinv u;bcinv u;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%  Conversion between forms, vdps and prefix expressions.
%

% Prefix to vdp.
symbolic procedure a2vdp u;
 if u=0 or null u then makevdp(nil./1,nil,makedipzero())
  else(makevdp(diplbc r,dipevlmon r,r)where r=a2dip u);

% Vdp to prefix.
symbolic procedure vdp2a u;dip2a vdppoly u;

symbolic procedure vbc2a u;bc2a u;

% Form to vdp.
symbolic procedure f2vdp u;
 if u=0 or null u then makevdp(nil./1,nil,makedipzero())
  else(makevdp(diplbc r,dipevlmon r,r)where r=f2dip u);

% Vdp to form.
symbolic procedure vdp2f u;dip2f vdppoly u;

% Vdp from monomial.
symbolic procedure vdpfmon(coef,vev);
 begin scalar r;r:=makevdp(coef,vev,dipfmon(coef,vev));
  if !*gsugar then gsetsugar(r,vevtdeg vev);return r end;

% Add a monomial to a vdp in front(new vev and coeff).
symbolic procedure vdpmoncomp(coef,vev,vdp);
 if vdpzero!? vdp then vdpfmon(coef,vev)
  else if vbczero!? coef then vdp
  else makevdp(coef,vev,dipmoncomp(coef,vev,vdppoly vdp));

% Add a monomial to the end of a vdp(vev remains unchanged).
symbolic procedure vdpappendmon(vdp,coef,vev);
 if vdpzero!? vdp then vdpfmon(coef,vev)
  else if vbczero!? coef then vdp
  else makevdp(vdplbc vdp,vdpevlmon vdp,dipsum(vdppoly vdp,dipfmon(coef,vev)));

% Add monomial to vdp;place of new monomial still unknown.
symbolic procedure vdpmonadd(coef,vev,vdp);
 if vdpzero!? vdp then vdpfmon(coef,vev)else
(if c=1 then vdpmoncomp(coef,vev,vdp)else
  if c=-1 then makevdp(vdplbc vdp,vdpevlmon vdp,
                               dipsum(vdppoly vdp,dipfmon(coef,vev)))
  else vdpsum(vdp,vdpfmon(coef,vev))
)where c=vevcomp(vev,vdpevlmon vdp);

symbolic procedure vdpzero();a2vdp 0;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%  Comparing of exponent vectors:
%

symbolic procedure vdpvevlcomp(p1,p2);dipevlcomp(vdppoly p1,vdppoly p2);

symbolic procedure vevilcompless!?(e1,e2);1=evilcomp(e2,e1);

symbolic procedure vevilcomp(e1,e2);evilcomp(e1,e2);

symbolic procedure vevcompless!?(e1,e2);1=evcomp(e2,e1);

symbolic procedure vevcomp(e1,e2);evcomp(e1,e2);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%  Routines traversing the " coefficients ";
%
% CONTENT of a vdp:
% The content is the gcd of all coefficients.

symbolic procedure vdpcontent d;
 if vdpzero!? d then a2bc 0 else
 <<d:=vdppoly d;dipnumcontent(dipmred d,diplbc d)>>;

symbolic procedure vdpcontent1(d,c);dipnumcontent(vdppoly d,c);

symbolic procedure dipnumcontent(d,c);
 if bcone!? c or dipzero!? d then c
  else dipnumcontent(dipmred d,vbcgcd(c,diplbc d));

symbolic procedure dipcontenti p;
% The content is a pair of the lcm of the coefficients and the
% exponent list of the common monomial factor.
 if dipzero!? p then 1 else
(if dipzero!? rp then diplbc p.
 (if !*groebrm then dipevlmon p else nil)
   else dipcontenti1(diplbc p, if !*groebrm then dipevlmon p else nil,rp))
     where rp=dipmred p;

symbolic procedure dipcontenti1(n,ev,p1);
 if dipzero!? p1 then n.ev
  else begin scalar nn;nn:=vbcgcd(n,diplbc p1);
 if ev then ev:=dipcontevmin(dipevlmon p1,ev);
 if bcone!? nn and null ev then return nn.nil
  else return dipcontenti1(nn,ev,dipmred p1)end;

% CONTENT and MONFAC(if groebrm is on).
symbolic procedure vdpcontenti d;
 vdpcontent d.if !*groebrm then vdpmonfac d else nil;

symbolic procedure vdpmonfac d;dipmonfac vdppoly d;

symbolic procedure dipmonfac p;
% Exponent list of the common monomial factor.
 if dipzero!? p or not !*groebrm then evzero()
  else(if dipzero!? rp then dipevlmon p
  else dipmonfac1(dipevlmon p,rp))where rp=dipmred p;

symbolic procedure dipmonfac1(ev,p1);
 if dipzero!? p1 or evzero!? ev then ev
  else dipmonfac1(dipcontevmin(ev,dipevlmon p1),dipmred p1);

% vdpcoeffcientsfromdomain?
symbolic procedure vdpcoeffcientsfromdomain!? w;
 dipcoeffcientsfromdomain!? vdppoly w;

symbolic procedure dipcoeffcientsfromdomain!? w;
 if dipzero!? w then t else
(if bcdomain!? v then dipcoeffcientsfromdomain!? dipmred w
     else nil)where v=diplbc w;

symbolic procedure vdplength f;diplength vdppoly f;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%  Polynomial operations:
%             coefficient normalization and reduction of monomial factors.
%

symbolic procedure vdpequal(p1,p2);
 p1 eq p2
 or(n1 and n1=n2   % number comparison is faster most times
 or dipequal(vdppoly p1,vdppoly p2)
  where n1=vdpgetprop(p1,'number),n2=vdpgetprop(p2,'number));

symbolic procedure dipequal(p1,p2);
 if dipzero!? p1 then dipzero!? p2 else if dipzero!? p2 then nil
  else diplbc p1=diplbc p2 and evequal(dipevlmon p1,dipevlmon p2)
   and dipequal(dipmred p1,dipmred p2);

symbolic procedure evequal(e1,e2);
% Test equality with variable length exponent vectors.
 if null e1 and null e2 then t
  else if null e1 then evequal('(0),e2)
  else if null e2 then evequal(e1,'(0))
  else 0=(car e1 #- car e2)and evequal(cdr e1,cdr e2);

symbolic procedure vdplcm p;diplcm vdppoly p;

symbolic procedure vdprectoint(p,q);dip2vdp diprectoint(vdppoly p,q);

symbolic procedure vdpsimpcont(p);
 begin scalar r,q;q:=vdppoly p;
  if dipzero!? q then return p;r:=dipsimpcont q;
  p:=dip2vdp cdr r;% the polynomial
  r:=car r;      % the monomial factor if any
  if not evzero!? r and(dipmred q or evtdeg r>1)
   then vdpputprop(p,'monfac,r);return p end;

symbolic procedure dipsimpcont(p);
 if !*vdpinteger or not !*groebdivide  then dipsimpconti p else dipsimpcontr p;

% Routines for integer coefficient case:
% calculation of contents and dividing all coefficients by it.

symbolic procedure dipsimpconti p;
% Calculate the contents of p and divide all coefficients by it.
 begin scalar co,lco,res,num;
 if dipzero!? p then return nil.p;co:=bcfd 1;
 co:=if !*groebdivide then dipcontenti p
   else if !*groebrm then co.dipmonfac p else co.nil;
 num:=car co;
 if not bcplus!? num then num:=bcneg num;
 if not bcplus!? diplbc p then num:=bcneg num;
 if bcone!? num and cdr co=nil then return nil.p;
 lco:=cdr co;
 if groebmonfac neq 0 then lco:=dipcontlowerev cdr co;
 res:=p;
 if not(bcone!? num and lco=nil)then res:=dipreduceconti(p,num,lco);
 if null cdr co then return nil.res;
 lco:=evdif(cdr co,lco);
 return(if lco and not evzero!? evdif(dipevlmon res,lco)
            then lco else nil).res end;

symbolic procedure vdpreduceconti(p,co,vev);
% Divide polynomial p by monomial from co and vev.
 vdpdivmon(p,co,vev);

% Divide all coefficients of p by cont.

symbolic procedure dipreduceconti(p,co,ev);
 if dipzero!? p then makedipzero()
  else dipmoncomp(bcquot(diplbc p,co),
                    if ev then evdif(dipevlmon p,ev)
                     else dipevlmon p,dipreduceconti(dipmred p,co,ev));

% Routines for rational coefficient case:
% calculation of contents and dividing all coefficients by it

symbolic procedure dipsimpcontr p;
% Calculate the contents of p and divide all coefficients by it.
 begin scalar co,lco,res;
  if dipzero!? p then return nil.p;
  co:=dipcontentr p;
  if bcone!? diplbc p and co=nil then return nil.p;
  lco:=dipcontlowerev co;res:=p;
  if not(bcone!? diplbc p and lco=nil)then
   res:=dipreducecontr(p,bcinv diplbc p,lco);
  return(if co then evdif(co,lco)else nil).res end;

symbolic procedure dipcontentr p;
% The content is the exponent list of the common monomial factor.
(if dipzero!? rp then (if !*groebrm then dipevlmon p else nil)
   else dipcontentr1(if !*groebrm then dipevlmon p else nil,rp))
    where rp=dipmred p;

symbolic procedure dipcontentr1(ev,p1);
 if dipzero!? p1 then ev
  else begin
        if ev then ev:=dipcontevmin(dipevlmon p1,ev);
        if null ev then return nil
         else return dipcontentr1(ev,dipmred p1)end;

% Divide all coefficients of p by cont.

symbolic procedure dipreducecontr(p,co,ev);
 if dipzero!? p then makedipzero()
  else dipmoncomp(bcprod(diplbc p,co),if ev then evdif(dipevlmon p,ev)
                         else dipevlmon p,dipreducecontr(dipmred p,co,ev));

symbolic procedure dipcontevmin(e1,e2);
% Calculates the minimum of two exponents;if one is shorter, trailing
% zeroes are assumed.
% e1 is an exponent vector.e2 is a list of exponents
 begin scalar res;
  while e1 and e2 do
  <<res:=(if ilessp(car e1,car e2)then car e1 else car e2).res;
     e1:=cdr e1;e2:=cdr e2>>;
  while res and 0=car res do res:=cdr res;
  return reversip res end;

symbolic procedure dipcontlowerev e1;
% Subtract a 1 from those elements of an exponent vector which
% are greater  than 1.
% e1 is a list of exponents,the result is an exponent vector.
 begin scalar res;
  while e1 do
  <<res:=(if igreaterp(car e1,0)then car e1 - 1 else 0).res;e1:=cdr e1>>;
   while res and 0=car res do res:=cdr res;
   if res and !*trgroebs then
   <<prin2 " ***** exponent reduction : ";prin2t reverse res>>;
   return reversip res end;

symbolic procedure dipappendmon(dip,bc,ev);append(dip,dipfmon(bc,ev));

smacro procedure dipnconcmon(dip,bc,ev);nconc(dip,dipfmon(bc,ev));

smacro procedure dipappenddip(dip1,dip2);append(dip1,dip2);

smacro procedure dipnconcdip(dip1,dip2);nconc(dip1,dip2);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%  Basic polynomial arithmetic:
%

symbolic procedure vdpsum(d1,d2);
 begin scalar r;
  r:=dip2vdp dipsum(vdppoly d1,vdppoly d2);
  if !*gsugar then gsetsugar(r,max(gsugar d1,gsugar d2));return r end;

symbolic procedure vdpdif(d1,d2);
 begin scalar r;
  r:=dip2vdp dipdif(vdppoly d1,vdppoly d2);
  if !*gsugar then gsetsugar(r,max(gsugar d1,gsugar d2));return r end;

symbolic procedure vdpprod(d1,d2);
 begin scalar r;
  r:= dip2vdp dipprod(vdppoly d1,vdppoly d2);
  if !*gsugar then gsetsugar(r,gsugar d1 + gsugar d2);return r end;

% % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % %
%
%  Linear combination: the Buchberger workhorse.
%
% LCOMB1: calculate mon1 * vdp1 + mon2 * vdp2.

symbolic procedure vdpilcomb1(d1,vbc1,vev1,d2,vbc2,vev2);
 begin scalar r;
  r:=
 dip2vdp dipilcomb1(vdppoly d1,vbc1,vev1,vdppoly d2,vbc2,vev2);
  if !*gsugar then gsetsugar(r,max(gsugar d1 + vevtdeg vev1,
                                   gsugar d2 + vevtdeg vev2));return r end;

symbolic procedure dipilcomb1(p1,bc1,ev1,p2,bc2,ev2);
% Same as dipILcomb, exponent vectors multiplied in already.
begin scalar gcd;
 gcd:=!*gcd;
return
 begin scalar ep1,ep2,sl,res,sum,z1,z2,p1new,p2new,
  lptr,bptr,c,!*gcd;
  !*gcd:=if vbcsize(bc1,-100)and vbcsize(bc2,-100)then gcd;
  z1:=not evzero!? ev1;z2:=not evzero!? ev2;
  p1new:=p2new:=t;
  lptr:=bptr:=res:=makedipzero();
 loop:
  if p1new then
  <<if dipzero!? p1 then return if dipzero!? p2 then res else
                     dipnconcdip(res,dipprod(p2,dipfmon(bc2,ev2)));
     ep1:=dipevlmon p1;
     if z1 then ep1:=evsum(ep1,ev1);
     p1new:=nil>>;
   if p2new then
   <<if dipzero!? p2 then
              return dipnconcdip(res,dipprod(p1,dipfmon(bc1,ev1)));
       ep2:=dipevlmon p2;
       if z2 then ep2:=evsum(ep2,ev2);
       p2new:=nil>>;
    sl:=evcomp(ep1,ep2);
    if sl=1 then
    <<if !*gcd and not vbcsize(diplbc p1,-100)then !*gcd:=nil;
       c:=bcprod(diplbc p1,bc1);
       if not bczero!? c then
       <<lptr:=dipnconcmon(bptr,c,ep1);
          bptr:=dipmred lptr>>;
       p1:=dipmred p1;p1new:=t;
    >> else if sl=-1 then
     <<if !*gcd and not vbcsize(diplbc p2,-100)then !*gcd:=nil;
        c:=bcprod(diplbc p2,bc2);
        if not bczero!? c then
        <<lptr:=dipnconcmon(bptr,c,ep2);bptr:=dipmred lptr>>;
           p2:=dipmred p2;p2new:=t>>
        else
         <<if !*gcd and(not vbcsize(diplbc p1,-100)or
                      not vbcsize(diplbc p2,-100)) then !*gcd:=nil;
             sum:=bcsum(bcprod(diplbc p1,bc1),
                            bcprod(diplbc p2,bc2));
             if not bczero!? sum then
             <<lptr:=dipnconcmon(bptr,sum,ep1);
                bptr:=dipmred lptr>>;
              p1:=dipmred p1;p2:=dipmred p2;p1new:=p2new:=t>>;
        if dipzero!? res then <<res:=bptr:=lptr>>;% initial
        goto loop end;end;

symbolic procedure vdpvbcprod(p,a);
(if !*gsugar then gsetsugar(q,gsugar p)else q)
       where q=dip2vdp dipbcprod(vdppoly p,a);

symbolic procedure vdpdivmon(p,c,vev);
(if !*gsugar then gsetsugar(q,gsugar p)else q)
       where q=dip2vdp dipdivmon(vdppoly p,c,vev);

symbolic procedure dipdivmon(p,bc,ev);
% Divides a polynomial by a monomial;
% we are sure that the monomial ev is a factor of p.
 if dipzero!? p then makedipzero()
  else dipmoncomp(bcquot(diplbc p,bc),evdif(dipevlmon p,ev),
                        dipdivmon(dipmred p,bc,ev));

symbolic procedure vdpcancelmvev(p,vev);
(if !*gsugar then gsetsugar(q,gsugar p)else q)
  where q=dip2vdp dipcancelmev(vdppoly p,vev);

symbolic procedure dipcancelmev(f,ev);
% Cancels all monomials in f which are multiples of ev
 dipcancelmev1(f,ev,makedipzero());

symbolic procedure dipcancelmev1(f,ev,res);
 if dipzero!? f then res
  else if evmtest!?(dipevlmon f,ev)then dipcancelmev1(dipmred f,ev,res)
     else dipcancelmev1(dipmred f,ev,
       %                 dipappendmon(res,diplbc f,dipevlmon f));
                         dipnconcmon(res,diplbc f,dipevlmon f));

% Some prehistoric routines needed in resultant operation

symbolic procedure vevsum0(n,p);
% Exponent vector sum version 0 . n is the length of vdpvars!*.
% p is a distributive polynomial.
  if vdpzero!? p then vevzero1 n else vevsum(vdpevlmon p,vevsum0(n,vdpred p));

symbolic procedure vevzero1 n;
% Returns the exponent vector power representation
% of length n for a zero power.
 begin scalar x;for i:=1:n do x:=0 . x;return x end;

symbolic procedure vdpresimp u;
% if domain changes,the coefficients have to be resimped
 dip2vdp dipresimp vdppoly u;

symbolic procedure dipresimp u;
 if null u then nil else
(for each x in u collect
     <<toggle:=not toggle;
      if toggle then simp prepsq x else x>>)where toggle = t;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% printing of polynomials
%

symbolic procedure vdpprin2t u;<<vdpprint1(u,nil,9999);terpri()>>;

symbolic procedure vdpprin3t u;<<vdpprint1(u,nil,9999);prin2t ";">>;

symbolic procedure vdpprint u;<<vdpprin2 u;terpri()>>;

symbolic procedure vdpprin2 u;
 <<(if x then <<prin2 " P(";prin2 x;
      if s then <<prin2 " / ";prin2 s>>;prin2 "):  ">>)
    where x=vdpgetprop(u,'number),s= vdpgetprop(u,'sugar);
   vdpprint1(u,nil,vdpprintmax)>>;

symbolic procedure vdpprint1(u,v,max);vdpprint1x(vdppoly u,v,max);

symbolic procedure vdpprint1x(u,v,max);
% Prints a distributive polynomial in infix form.
% U is a distributive form. V is a flag which is true if a term
% has preceded current form
% max limits the number of terms to be printed
  if dipzero!? u then if null v then dipprin2 0 else nil
    else if max=0 then   % maximum of terms reached
     <<terpri();prin2 " ### etc(";
       prin2 diplength u;prin2 " terms)### ";terpri()>>
    else begin scalar bool,w;
       w:=diplbc u;
       if bcminus!? w then<<bool:=t;w:=bcneg w>>;
       if bool then dipprin2 " - " else if v then dipprin2 " + ";
      (if not bcone!? w or evzero!? x then<<bcprin w;dipevlpri(x,t)>>
         else dipevlpri(x,nil))
           where x=dipevlmon u;
       vdpprint1x(dipmred u,t,max - 1)end;

symbolic procedure dipprin2 u;<<if posn()>69 then terprit 2;prin2 u>>;

symbolic procedure vdpsave u;u;

% switching between term order modes

symbolic procedure torder2 u;dipsortingmode u;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% additional conversion utilities

% conversion dip to standard form / standard quotient

symbolic procedure dip2f u;
(if denr v neq 1 then
  <<print u;
     rerror(dipoly,9,
                 " Distrib . poly . with rat coeff cannot be converted ")>>
       else numr v) where v=dip2sq u;

symbolic procedure dip2sq u;
% Convert a dip into a standard quotient.
 if dipzero!? u then nil ./ 1
  else addsq(diplmon2sq(diplbc u,dipevlmon u), dip2sq dipmred u);

symbolic procedure diplmon2sq(bc,ev);
% Convert a monomial into a standard quotient.
 multsq(bc,dipev2f(ev,dipvars!*)./ 1);

symbolic procedure dipev2f(ev,vars);
 if null ev then 1
  else if car ev=0 then dipev2f(cdr ev,cdr vars)
     else multf(car vars .** car ev .* 1 .+ nil,dipev2f(cdr ev,cdr vars));

% evaluate SUBS2 for the coefficients of a dip

symbolic procedure dipsubs2 u;
 begin scalar v,secondvalue!*;
  secondvalue!*:=1 ./ 1;v:=dipsubs21 u;
  return diprectoint(v,secondvalue!*)end;

symbolic procedure dipsubs21 u;
 if dipzero!? u then u else
 begin scalar c;c:=groebsubs2 diplbc u;
  if null numr c then return dipsubs21 dipmred u;
  if not(denr c=1)then secondvalue!*:=bclcmd(c,secondvalue!*);
  return dipmoncomp(c,dipevlmon u,dipsubs21 dipmred u)end;

% conversion standard form to dip

symbolic procedure f2dip u;f2dip1(u,evzero(),bcfd 1);

symbolic procedure f2dip1(u,ev,bc);
% f to dip conversion : scan the standard form. ev
% and bc are the exponent and coefficient parts collected
% so far from higher parts.
 if null u then nil
  else if domainp u then<<numberp bc or f2dip11 bc;
                          dipfmon(bcprod(bc,bcfd u),ev)>>
  else dipsum(f2dip2(mvar u,ldeg u,lc u,ev,bc),f2dip1(red u,ev,bc));

symbolic procedure f2dip11 b;
% Test, if the function names and the parameters of coefficient
% functions are free of Groebner variables.
 !*notestparameters or
 <<if b member vdpvars!* then
    rederr{b,
    "occurs in a parameter and is member of the groebner variables."};
   if atom b then
     <<if b='list then rederr "groebner: LIST not allowed.">>
    else <<f2dip11 car b;f2dip11 cdr b>> >>;

symbolic procedure f2dip2(var,dg,c,ev,bc);
% f to dip conversion:
% multiply leading power either into exponent vector
% or into the base coefficient.
 <<if ev1 then ev:=ev1
     else bc:=multsq(bc,var .** dg .* 1 .+ nil ./ 1);
     f2dip1(c,ev,bc)>>
           where ev1=if memq(var,dipvars!*)then
                evinsert(ev,var,dg,dipvars!*)else nil;

symbolic procedure evinsert(ev,v,dg,vars);
% f to dip conversion:
% Insert the "dg" into the ev in the place of variable v.
 if null ev or null vars then nil
  else if car vars=v then dg.cdr ev
  else car ev.evinsert(cdr ev,v,dg,cdr vars);

symbolic procedure vdpcondense f;dipcondense car cdddr f;

endmodule;;end;
