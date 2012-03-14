module vdp2dip1;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% interface for DIPOLY polynomials as records (objects).
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%

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

fluid'(intvdpvars!* vdpvars!* secondvalue!* vdpsfsortmode!* !*groebrm
        !*vdpinteger !*trgroeb !*trgroebs !*groebdivide pcount!*
        !*groebsubs);

fluid'(vdpsortmode!*);

global'(vdpprintmax groebmonfac);
flag('(vdpprintmax),'share);

fluid'(dipvars!* !*vdpinteger);

symbolic procedure dip2vdp u;
 % is unsed when u can be empty
   (if dipzero!? uu then makeVdp(a2bc 0,nil,nil)
                    else makeVdp(diplbc uu,dipevlmon uu,uu))
           where uu = if !*groebsubs then dipsubs2 u else u;


% some simple mappings

smacro procedure makedipzero(); nil;

symbolic procedure vdpredzero!? u; dipzero!? dipmred vdppoly u;

symbolic procedure vbczero!? u; bczero!? u;

symbolic procedure vbcnumber u;
       if  pairp u and numberp car u and 1=cdr u then cdr u else nil;

symbolic procedure vbcfi u; bcfi u;

symbolic procedure a2vbc u; a2bc u;

symbolic procedure vbcquot(u,v); bcquot(u,v);

symbolic procedure vbcneg u; bcneg u;

symbolic procedure vbcabs u; if vbcminus!? u then bcneg u else u;

symbolic procedure vbcone!? u; bcone!? u;

symbolic procedure vbcprod (u,v); bcprod(u,v);

 % initializing vdp-dip polynomial package
symbolic procedure vdpinit2(vars);
  begin scalar oldorder;
    oldorder:=kord!*;
    if null vars then rerror(dipoly,8,"Vdpinit: vdpvars not set");
    vdpvars!*:=dipvars!*:=vars;
    torder2 vdpsortmode!*;
    return oldorder end;

symbolic procedure vdpred u;
    (if dipzero!? r then makevdp(nil ./ nil,nil,makedipzero())
                   else makevdp(diplbc r,dipevlmon r,r))
       where  r = dipmred vdppoly u;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%  coefficient handling; here we assume that coefficients are
%  standard quotients;
%

symbolic procedure vbcgcd (u,v);
     if denr u = 1 and denr v = 1 then
       if fixp u and fixp numr v then gcdn(numr u,numr v)  ./ 1
            else gcdf!*(numr u,numr v) ./ 1
     else 1 ./ 1;

% the following functions must be redefinable
symbolic procedure vbcplus!? u; (numberp v and v>0) where v = numr u;
symbolic procedure bcplus!? u; (numberp v and v>0) where v = numr u;

symbolic procedure vbcminus!? u;
       (numberp v and v<0) where v = numr u;

symbolic procedure vbcinv u; bcinv u;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%  conversion between forms, vdps and prefix expressions
%

% prefix to vdp
symbolic procedure a2vdp u;
     if u=0 or null u then makevdp(nil ./ nil,nil,makedipzero())
     else (makevdp(diplbc r,dipevlmon r,r)  where  r = a2dip u);

% vdp to prefix
symbolic procedure vdp2a u; dip2a vdppoly u;

symbolic procedure vbc2a u; bc2a  u;

% form to vdp
symbolic procedure f2vdp(u);
     if u=0 or null u then makevdp(nil ./ nil,nil,makedipzero())
     else (makevdp(diplbc r,dipevlmon r,r)  where  r = f2dip u);

% vdp to form
symbolic procedure vdp2f u; dip2f vdppoly u;

% vdp from monomial
symbolic procedure vdpfmon (coef,vev);
                 makevdp(coef,vev,dipfmon(coef,vev));

% add a monomial to a vdp in front (new vev and coeff)
symbolic procedure vdpmoncomp(coef,vev,vdp);
   if vdpzero!? vdp then vdpfmon(coef,vev) else
   if vbczero!? coef then vdp else
   makevdp(coef,vev,dipmoncomp(coef,vev,vdppoly vdp));

%add a monomial to the end of a vdp (vev remains unchanged)
symbolic procedure vdpappendmon(vdp,coef,vev);
   if vdpzero!? vdp then vdpfmon(coef,vev) else
   if vbczero!? coef then vdp else
   makevdp(vdpLbc vdp,vdpevlmon vdp,
            dipsum(vdppoly vdp,dipfmon(coef,vev)));

% add monomial to vdp, place of new monomial still unknown
symbolic procedure vdpmonadd(coef,vev,vdp);
    if vdpzero!? vdp then vdpfmon(coef,vev) else
   (if c = 1 then vdpmoncomp(coef,vev,vdp) else
    if c = -1 then makevdp (vdplbc vdp,vdpevlmon vdp,
                               dipsum(vdppoly vdp,dipfmon(coef,vev)))
    else vdpsum(vdp,vdpfmon(coef,vev))
   ) where c = vevcomp(vev,vdpevlmon vdp);

symbolic procedure vdpzero(); a2vdp 0;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%  comparing of exponent vectors
%
%

symbolic procedure vdpvevlcomp (p1,p2);
                  dipevlcomp (vdppoly p1,vdppoly p2);
symbolic procedure vevilcompless!?(e1,e2); 1 = evilcomp(e2,e1);
symbolic procedure vevilcomp (e1,e2); evilcomp (e1,e2);
symbolic procedure vevcompless!?(e1,e2); 1 = evcomp(e2,e1);
symbolic procedure vevcomp (e1,e2); evcomp (e1,e2);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%  routines traversing the "coefficients"
%

% CONTENT of a vdp
% The content is the gcd of all coefficients.

symbolic procedure vdpcontent d;
     if vdpzero!? d then a2bc 0 else
     <<d:=vdppoly d; dipnumcontent(dipmred d,diplbc d)>>;

symbolic procedure vdpcontent1(d,c); dipnumcontent(vdppoly d,c);

symbolic procedure dipnumcontent(d,c);
     if bcone!? c or dipzero!? d then c
     else dipnumcontent(dipmred d,vbcgcd(c,diplbc d));

symbolic procedure dipcontenti p;
% the content is a pair of the lcm of the coefficients and the
% exponent list of the common monomial factor.
   if dipzero!? p then 1 else
   (if dipzero!? rp then diplbc p .  (if !*groebrm then dipevlmon p else nil)
      else dipcontenti1(diplbc p, if !*groebrm then dipevlmon p else nil,rp) )
                           where rp=dipmred p;

symbolic procedure dipcontenti1 (n,ev,p1);
   if dipzero!? p1 then n . ev
   else begin scalar nn;
             nn:=vbcgcd (n,diplbc p1);
             if ev then ev:=dipcontevmin(dipevlmon p1,ev);
             if bcone!? nn and null ev then return nn . nil
                       else return dipcontenti1 (nn,ev,dipmred p1) end;

% CONTENT and MONFAC (if groebrm on)
symbolic procedure vdpcontenti d;
       vdpcontent d . if !*groebrm then vdpmonfac d else nil;

symbolic procedure vdpmonfac d; dipmonfac vdppoly d;

symbolic procedure dipmonfac p;
% exponent list of the common monomial factor.
   if dipzero!? p or not !*groebrm then evzero()
   else (if dipzero!? rp then dipevlmon p
         else dipmonfac1(dipevlmon p,rp) ) where rp=dipmred p;

symbolic procedure dipmonfac1(ev,p1);
   if dipzero!? p1 or evzero!? ev then ev
   else dipmonfac1(dipcontevmin(ev,dipevlmon p1),dipmred p1);

% vdpCoeffcientsfromdomain!?
symbolic procedure vdpcoeffcientsfromdomain!? w;
          dipcoeffcientsfromdomain!? vdppoly w;

symbolic procedure dipcoeffcientsfromdomain!? w;
      if dipzero!? w then t else
      (if denr v = 1 and domainp numr v then
              dipcoeffcientsfromdomain!? dipmred w
            else nil) where v =diplbc w;

symbolic procedure vdplength f; diplength vdppoly f;


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%  polynomial operations:
%             coefficient normalization and reduction of monomial
%             factors
%

symbolic procedure vdpequal(p1,p2);
     p1 eq p2
     or (n1 and n1 = n2   % number comparison is faster most times
     or dipequal(vdppoly p1,vdppoly p2)
           where n1 = vdpgetprop(p1,'number),
                 n2 = vdpgetprop(p2,'number));

symbolic procedure dipequal(p1,p2);
    if dipzero!? p1 then dipzero!? p2
    else if dipzero!? p2 then nil
    else diplbc p1 = diplbc p2
     and evequal(dipevlmon p1,dipevlmon p2)
     and dipequal(dipmred p1,dipmred p2);

symbolic procedure evequal(e1,e2);
   % test equality with variable length exponent vectors
     if null e1 and null e2 then t
     else if null e1 then evequal('(0),e2)
     else if null e2 then evequal(e1,'(0))
     else 0=(car e1 #- car e2) and evequal(cdr e1,cdr e2);

symbolic procedure vdplcm p; diplcm vdppoly p;

symbolic procedure vdprectoint(p,q); dip2vdp diprectoint(vdppoly p,q);

symbolic procedure vdpsimpcont(p);
          begin scalar r;
              r:=vdppoly p;
              if dipzero!? r then return p;
              r:=dipsimpcont r;
              p:=dip2vdp cdr r;  % the polynomial
              r:=car r;          % the monomial factor if any
              if not evzero!? r then vdpputprop(p,'monfac,r);
              return p end;

symbolic procedure dipsimpcont  (p);
    if !*vdpinteger or not !*groebdivide  then dipsimpconti p
                          else dipsimpcontr p;

% routines for integer coefficient case:
% calculation of contents and dividing all coefficients by it

symbolic procedure dipsimpconti (p);
%   calculate the contents of p and divide all coefficients by it
  begin scalar co,lco,res,num;
    if dipzero!?  p then return nil . p;
    co:=bcfi 1;
    co:=if !*groebdivide then dipcontenti p
             else if !*groebrm then co . dipmonfac p
             else co . nil;
    num:=car co;
    if not bcplus!? num then num:=bcneg num;
    if not bcplus!? diplbc p then num:=bcneg num;
    if bcone!? num  and cdr co = nil  then return nil . p;
    lco:=cdr co;
    if groebmonfac neq 0  then lco:=dipcontLowerEv cdr co;
    res:=p;
    if not(bcone!? num and lco = nil) then
                 res:=dipreduceconti (p,num,lco);
    if null cdr co then return nil . res;
    lco:=evdif(cdr co,lco);
    return(if lco and not evzero!? evdif(dipevlmon res,lco)
                           then lco else nil).res end;

symbolic procedure vdpreduceconti (p,co,vev);
%  divide polynomial p by monomial from co and vev
        vdpdivmon(p,co,vev);

%  divide all coefficients of p by cont
symbolic procedure dipreduceconti (p,co,ev);
    if dipzero!? p then makedipzero() else
           dipmoncomp ( bcquot (diplbc p,co),
                        if ev then evdif(dipevlmon p,ev) else dipevlmon p,
                        dipreduceconti (dipmred p,co,ev));

% routines for rational coefficient case:
% calculation of contents and dividing all coefficients by it

symbolic procedure dipsimpcontr (p);
%   calculate the contents of p and divide all coefficients by it
  begin scalar co,lco,res;
    if dipzero!?  p then return nil . p;
    co:=dipcontentr p;
    if bcone!? diplbc p  and co = nil  then return nil . p;
    lco:=dipcontLowerEv co;
    res:=p;
    if not(bcone!? diplbc p and lco = nil) then
               res:=dipreducecontr (p,bcinv diplbc p,lco);
    return (if co then evdif(co,lco) else nil) . res end;

symbolic procedure dipcontentr p;
% the content is the exponent list of the common monomial factor.
   (if dipzero!? rp then (if !*groebrm then dipevlmon p else nil) else
    dipcontentr1(if !*groebrm then dipevlmon p else nil,rp) )
                          where rp=dipmred p;

symbolic procedure dipcontentr1 (ev,p1);
   if dipzero!? p1 then ev
      else begin
             if ev then ev:=dipcontEvMin(dipevlmon p1,ev);
             if null ev then return nil
                       else return dipcontentr1 (ev,dipmred p1) end;

%  divide all coefficients of p by cont
symbolic procedure dipreducecontr (p,co,ev);
    if dipzero!? p then makedipzero() else
           dipmoncomp ( bcprod (diplbc p,co),
                        if ev then evdif(dipevlmon p,ev) else dipevlmon p,
                        dipreducecontr (dipmred p,co,ev));

symbolic procedure dipcontevmin (e1,e2);
% calculates the minimum of two exponents; if one is shorter, trailing
% zeroes are assumed.
% e1 is an exponent vector. e2 is a list of exponents
    begin scalar res;
       while e1 and e2 do
          <<res:=(if ilessp(car e1,car e2) then car e1 else car e2) . res;
            e1:=cdr e1; e2:=cdr e2>>;
       while res and 0=car res do res:=cdr res;
       return reversip res end;

symbolic procedure dipcontlowerev (e1);
% subtract a 1 from those elements of an exponent vector which
% are greater  than 1.
% e1 is a list of exponents, the result is an exponent vector.
    begin scalar res;
       while e1 do
          <<res:=(if igreaterp(car e1,0) then car e1 - 1 else 0) . res;
            e1:=cdr e1>>;
       while res and 0 = car res do res:=cdr res;
            if res and !*trgroebs then
                   <<prin2 "***** exponent reduction:"; prin2t reverse res>>;
       return reversip res end;

% routine for the non-integer case (we can divide coefficients):

remflag('(dipmonic),'lose);

symbolic procedure  dipmonic p;
%  divide the polynomial by the leading coefficient, so that the
%  new leading coefficient will be == 1
   if dipzero!? p then p else dipbcprod(p,bcinv diplbc p);

flag('(dipmonic),'lose);

symbolic procedure dipappendmon(dip,bc,ev); append(dip,dipfmon(bc,ev));

smacro procedure dipnconcmon(dip,bc,ev); nconc(dip,dipfmon(bc,ev));

smacro procedure dipnconcdip(dip1,dip2); nconc(dip1,dip2);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%  basic polynomial arithmetic:
%

symbolic procedure vdpsum(d1,d2); dip2vdp dipsum(vdppoly d1,vdppoly d2);

symbolic procedure vdpdif(d1,d2); dip2vdp dipdif(vdppoly d1,vdppoly d2);

symbolic procedure vdpprod(d1,d2); dip2vdp dipprod(vdppoly d1,vdppoly d2);

% % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % %
%
%  linear combination: the Buchberger Workhorse
%
% LCOMB1: calculate mon1 * vdp1 + mon2 * vdp2
symbolic procedure vdpilcomb1(d1,vbc1,vev1,d2,vbc2,vev2);
 dip2vdp dipILcomb1 (vdppoly d1,vbc1,vev1,vdppoly d2,vbc2,vev2);

symbolic procedure dipilcomb1 (p1,bc1,ev1,p2,bc2,ev2);
% same asl dipILcomb, exponent vectors multiplied in already
   begin scalar ep1,ep2,sl,res,sum,z1,z2,p1new,p2new,lptr,bptr;
       z1:=not evzero!? ev1; z2:=not evzero!? ev2;
       p1new:=p2new:=t;
       lptr:=bptr:=res:=makedipzero();
 loop:
       if p1new then
       << if dipzero!? p1 then
              return if dipzero!? p2 then res else
                     dipnconcdip(res, dipprod(p2,dipfmon(bc2,ev2)));
          ep1:=dipevlmon p1;
          if z1 then ep1:=evsum(ep1,ev1);
          p1new:=nil>>;
       if p2new then
       << if dipzero!? p2 then
              return dipnconcdip(res, dipprod(p1,dipfmon(bc1,ev1)));
          ep2:=dipevlmon p2;
          if z2 then ep2:=evsum(ep2,ev2);
          p2new:=nil>>;
        sl:=evcomp(ep1, ep2);
        if sl = 1 then
                      << lptr:=dipnconcmon (bptr,
                                              bcprod(diplbc p1,bc1),
                                              ep1);
                         bptr:=dipmred lptr;
                         p1:=dipmred p1; p1new:=t >>
        else if sl = -1 then
                      << lptr:=dipnconcmon (bptr,
                                              bcprod(diplbc p2,bc2),
                                              ep2);
                         bptr:=dipmred lptr;
                         p2:=dipmred p2; p2new:=t >>
        else
                      << sum:=bcsum (bcprod(diplbc p1,bc1),
                                       bcprod(diplbc p2,bc2));
                         if not bczero!? sum then
                             <<   lptr:=dipnconcmon(bptr,sum,ep1);
                                  bptr:=dipmred lptr>>;
                         p1:=dipmred p1; p2:=dipmred p2;
                         p1new:=p2new:=t >>;
        if dipzero!? res then <<res:=bptr:=lptr>>; % initial
        goto loop end;

symbolic procedure vdpvbcprod(p,a); dip2vdp dipbcprod(vdppoly p,a);

symbolic procedure vdpdivmon(p,c,vev);
   dip2vdp dipdivmon(vdppoly p,c,vev);

symbolic procedure dipdivmon(p,bc,ev);
    % divides a polynomial by a monomial
    % we are sure that the monomial ev is a factor of p
    if dipzero!? p
         then makedipzero()
         else
           dipmoncomp ( bcquot(diplbc p,bc),
                        evdif(dipevlmon p,ev),
                        dipdivmon (dipmred p,bc,ev));

symbolic procedure vdpcancelmvev(f,vev);
     dip2vdp dipcancelmev(vdppoly f,vev);

symbolic procedure dipcancelmev(f,ev);
    % cancels all monomials in f which are multiples of ev
      dipcancelmev1(f,ev,makedipzero());

symbolic procedure dipcancelmev1(f,ev,res);
    if dipzero!? f then res
    else if evmtest!?(dipevlmon f,ev) then
                       dipcancelmev1(dipmred f,ev,res)
     else dipcancelmev1(dipmred f,ev,
                         dipnconcmon(res,diplbc f,dipevlmon f));

% some prehistoric routines needed in resultant operation
symbolic procedure vevsum0(n,p);
% exponent vector sum version 0. n is the length of vdpvars!*.
% p is a distributive polynomial.
  if vdpzero!? p then vevzero1 n else vevsum(vdpevlmon p, vevsum0(n,vdpred p));

symbolic procedure vevzero1 n;
% Returns the exponent vector power representation
% of length n for a zero power.
  begin scalar x;
   for i:=1: n do << x:=0 . x >>;
  return x end;

symbolic procedure vdpresimp u;
   % fi domain changes, the coefficients have to be resimped
   dip2vdp dipresimp vdppoly u;

symbolic procedure dipresimp u;
   if null u then nil else
   (for each x in u collect
      <<toggle:=not toggle;
      if toggle then simp prepsq x else x>>
      ) where toggle = t;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% printing of polynomials
%

symbolic procedure vdpprint u; <<vdpprin2 u; terpri()>>;

symbolic procedure vdpprin2 u;
   <<(if x then <<prin2 "p("; prin2 x; prin2 "):  ">>)
                 where x=vdpGetProp(u,'number);
      vdpprint1(u,nil,vdpprintmax)>>;

symbolic procedure vdpprint1(u,v,max); vdpprint1x(vdppoly u,v,max);

symbolic procedure vdpprint1x(u,v,max);
%   /* Prints a distributive polynomial in infix form.
%     U is a distributive form. V is a flag which is true if a term
%     has preceded current form
%     max limits the number of terms to be printed
   if dipzero!? u then if null v then dipprin2 0 else nil
    else if max = 0 then   % maximum of terms reached
              << terpri();
                 prin2 " ### etc (";
                 prin2 diplength u;
                 prin2 " terms) ###";
                 terpri()>>
    else begin scalar bool,w;
       w:=diplbc u;
       if bcminus!? w then <<bool:=t; w:=bcneg w>>;
       if bool then dipprin2 " - " else if v then dipprin2 " + ";
       (if not bcone!? w or evzero!? x then<<bcprin w; dipevlpri(x,t)>>
         else dipevlpri(x,nil))
           where x = dipevlmon u;
       vdpprint1x(dipmred u,t, max - 1) end;

symbolic procedure dipprin2 u;
   <<if posn()>69 then terprit 2 ; prin2 u>>;

symbolic procedure vdpsave u; u;

%   switching between term order modes

symbolic procedure torder2 u; dipsortingmode u;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% additional conversion utilities

% conversion dip to standard form / standard quotient

symbolic procedure dip2f u;
      (if denr v neq 1 then
        <<print u;
          rerror(dipoly,9,
                 "distrib. poly. with rat coeff cannot be converted")>>
       else numr v) where v = dip2sq u;

symbolic procedure dip2sq u;
   % convert a dip into a standard quotient.
     if dipzero!? u then nil ./ 1
     else addsq(diplmon2sq(diplbc u,dipevlmon u),dip2sq dipmred u);

symbolic procedure diplmon2sq(bc,ev);
   %convert a monomial into a standard quotient.
     multsq(bc,dipev2f(ev,dipvars!*) ./ 1);

symbolic procedure dipev2f(ev,vars);
     if null ev then 1
     else if car ev = 0 then dipev2f(cdr ev,cdr vars)
     else multf(car vars .** car ev .* 1 .+ nil,
                dipev2f(cdr ev,cdr vars));

% evaluate SUBS2 for the coefficients of a dip

symbolic procedure dipsubs2 u;
   begin scalar v,secondvalue!*;
      secondvalue!*:=1 ./ 1;
      v:=dipsubs21 u;
      return diprectoint(v,secondvalue!*) end;

symbolic procedure dipsubs21 u;
   begin scalar c;
      if dipzero!? u then return u;
      c:=groebsubs2 diplbc u;
      if null numr c then return dipsubs21 dipmred u;
      if not(denr c = 1) then
          secondvalue!*:=bclcmd(c,secondvalue!*);
      return dipmoncomp(c,dipevlmon u,dipsubs21 dipmred u) end;

% conversion standard form to dip

symbolic procedure f2dip u; f2dip1(u,evzero(),1 ./ 1);

symbolic procedure f2dip1 (u,ev,bc);
  % f to dip conversion: scan the standard form. ev
  % and bc are the exponent and coefficient parts collected
  % so far from higher parts.
   if null u then nil
   else if domainp u then dipfmon(multsq(bc,u ./ 1),ev)
   else dipsum(f2dip2(mvar u,ldeg u,lc u,ev,bc),
               f2dip1(red u,ev,bc));

symbolic procedure f2dip2(var,dg,c,ev,bc);
  % f to dip conversion:
  % multiply leading power either into exponent vector
  % or into the base coefficient.
   <<if ev1 then ev:=ev1
     else bc:=multsq(bc,var.**dg.*1 .+nil./1);
     f2dip1(c,ev,bc)>>
           where ev1=if memq(var,dipvars!*) then
                evinsert(ev,var,dg,dipvars!*) else nil;

symbolic procedure evinsert(ev,v,dg,vars);
   % f to dip conversion:
   % Insert the "dg" into the ev in the place of variable v.
     if null ev or null vars then nil
     else if car vars eq v then dg . cdr ev
     else car ev . evinsert(cdr ev,v,dg,cdr vars);

endmodule;;end;
