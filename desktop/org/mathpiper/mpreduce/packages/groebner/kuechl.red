module kuechl;% Walking faster,B . Amrhrein,O . Gloor,W . Kuechlin
% in: Calmet,Limongelli(Eds .)Design and
% Implementation of Symbolic Computation Systems,Sept.1996

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


% Version 3 with a rational local solution(after letters from H.M.Moeller).
% Version 4 with keeping the polynomials as DIPs converting only
% their order mode.

switch trgroeb;

put('groebner_walk,' psopfn,' groeb!-walk);

symbolic procedure groeb!-walk u;
begin if !*groebopt then
  rerror(groebner,31,"don't call 'groebner_walk' with 'on groebopt'");
  if null dipvars!* then rerror(groebner,30,"'torder' must be called before");
  groetime!*:=time();
  !*gsugar:=t;!*groebrm:=nil;u:=car groeparams(u,1,1);
  groebnervars(u,nil);u:=groeb!-list(u,'simp);
  groedomainmode();u:=groeb!-w2 u;
  return'list.groeb!-collect(u,'mk!*sq)end;

symbolic procedure groeb!-list(u,fcn);
% Execute the function ' fcn ' for the elements of the algebriac
% list 'u'.
<<if atom u or not(eqcar(u,'list))then
    rerror('groebner,29,"groebner: list as argument required");
   groeb!-collect(cdr u, fcn)>>;

symbolic procedure groeb!-collect(l,f);
% Collect the elements of function 'f' applied to the elements of
% the symbolic list 'l'. If 'f' is a number,map 'l' only.
for each x in l collect if numberp f then f else apply1(f,x);

symbolic procedure groeb!-w2 g;
% This is(essentially)the routine Groebner_Walk.
% G is a list of standard quotients,
% a Groebner basis gradlex or based on a vector like [1 1 1 ...].
% The result is the Groebner basis(standard quotients)with the
% final term order(lex)as its main order.
begin scalar iwv,owv,omega,gomega,gomegaplus,tt,tto,pc;
 scalar first,mx,imx,mmx,immx,nn,ll,prim;
 scalar !*vdpinteger,!*groebdivide;
 !*vdpinteger: nil;        % switch on division mode
 !*groebdivide:=t;
 first:=t;pcount!*:=0;mmx:=!*i2rn 1;immx:=mmx;
 iwv:=groeb!-collect(dipvars!*,1);
 omega:=iwv;              % Input order vector.
 owv:=1 .groeb!-collect(cdr dipvars!*,0);
 tto:=owv;                % Output order vector .
 groeb!-w9('weighted,omega);% Install omega as weighted order.
 g:=groeb!-collect(g,'sq2vdp);
 pc:=pcount!*;
 gbtest g;                  % Test the Groebner property.
 nn:=length dipvars!*;
 ll:=rninv!: !*i2rn nn;   % Inverse of the length.
 prim:=t;                 % Preset.
loop:groeb!-w9(' weighted,omega);
 mx:=groeb!-w6!-4 groeb!-collect(omega,1);
                              % Compute the maximum of \omega.
        if !*trgroeb then groebmess34 cadr mx;
 imx:=rninv!: mx;
 g:=if first then groeb!-collect(g,'vdpsimpcont) else groeb!-w10 g;
        if !*trgroeb then groebmess29 omega;
 gomega:=if first or not prim then g
         else groeb!-w3(g,omega); % G_\omega = initials(G_\omega);
 pcount!*:=pc;
        if !*trgroeb and not first then groebmess32 gomega;
 gomegaplus:=if first then{gomega}else gtraverso(gomega,nil,nil);
 if cdr gomegaplus then rerror(groebner,31,
                "groebner_walk,cdr of 'groebner' must be nil")
  else gomegaplus:=car gomegaplus;
        if !*trgroeb and not first then groebmess30 gomegaplus;
 if not first and prim
  then g:=groeb!-w4(gomegaplus,gomega,g)
  else if not prim then g:=gomega;
      % G=lift(G_{%omega}{plus},<{plus},G_{%omega),G,<)
 if not first then g:=for each x in g collect gsetsugar(x,nil);
        if !*trgroeb and not first then groebmess31 g;
 if groeb!-w5(omega,imx,tto,immx)then go to ret;
       % Stop if tt has been 1 once.
 if not first and rnonep!: tt then go to ret;% Secodary abort crit.
 tt:=groeb!-w6!-6(g,tto,immx,omega,imx,ll);% Determine_border .
        if !*trgroeb then groebmess36 tt;
 if null tt then go to ret;
        % criterion: take primary only if tt neq 1
 prim:=not rnonep!: tt;
        if !*trgroeb then groebmess37 prim;
          %\omega =(1-t)*\omega+t*tau
 omega:=groeb!-w7(tt,omega,imx,tto,immx);
        if !*trgroeb then groebmess35 omega;
 first:=nil;go to loop;
ret:
        if !*trgroeb then groebmess33 g;
 g:=groeb!-collect(g,'vdpsimpcont);
 g:=groeb!-collect(g,'vdp2sq);
 return g end;

symbolic procedure groeb!-w3(g,omega);
% Extract head terms of g corresponding to omega.
begin scalar x,y,gg,ff;
 gg:=for each f in g collect<<ff:=vdpfmon(vdplbc f,vdpevlmon f);
  gsetsugar(ff,nil);
  x:=evweightedcomp2(0,vdpevlmon ff,omega);
  y:=x;
  f:=vdpred f;
  while not vdpzero!? f and y=x do
  <<y:=evweightedcomp2(0,vdpevlmon f,omega);
   if y=x then
   ff:=vdpsum(ff,vdpfmon(vdplbc f,vdpevlmon f));f:=vdpred f>>;
  ff>>;return gg end;

symbolic procedure groeb!-w4(gb,gomega,g);
% gb     Groebner basis of gomega,
% gomega head term system g_\omega of g,
% g      full(original)system of polynomials.
begin scalar x;
 for each y in gb do gsetsugar(y,nil);
 x:=for each y in gomega collect groeb!-w8(y,gb);
 x:=for each z in x collect groeb!-w4!-1(z,g);return x end;

symbolic procedure groeb!-w4!-1(pl,fs);
% pl is a list of polynomials corresponding to the full system fs.
% Compute the sum of pl*fs. Result is the sum.
begin scalar z;z:=vdpzero();
 gsetsugar(z,0);
 for each p in pair(pl,fs)do
  if car p then z:=vdpsum(z,vdpprod(car p,cdr p));
 z:=vdpsimpcont z;return z end;

symbolic procedure groeb!-w5(ev1,x1,ev2,x2);
% ev1=ev2 equality test.
 groeb!-w5!-1(x1,ev1,x2,ev2);

symbolic procedure groeb!-w5!-1(x1,ev1,x2,ev2);
( null ev1 and null ev2)or
(rntimes!:(!*i2rn car ev1,x1)=rntimes!:(!*i2rn car ev2,x2)
         and groeb!-w5!-1(x1,cdr ev1,x2,cdr ev2));

symbolic procedure groeb!-w6!-4 omega;
% Compute the weighted length of \omega.
 groeb!-w6!-5(omega,vdpsortextension!*,0);

symbolic procedure groeb!-w6!-5(omega,v,m);
 if null omega then !*i2rn m
  else if 0=car omega then groeb!-w6!-5(cdr omega,cdr v,m)
    else if 1 = car omega
     then groeb!-w6!-5(cdr omega,cdr v,m #+ car v)
      else groeb!-w6!-5(cdr omega,cdr v,m #+ car omega #* car v);

symbolic procedure groeb!-w6!-6(gb,tt,ifactt,tp,ifactp,ll);
% Compute the weight border(minimum over all polynomials of gb).
begin scalar mn,x,zero,one;
 zero:=!*i2rn 0;one:=!*i2rn 1;
 while not null gb do
 <<x:=groeb!-w6!-7(car gb,tt,ifactt,tp,ifactp,zero,one,ll);
  if null mn or(x and rnminusp!: rndifference!:(x,mn))
   then mn:=x;gb:=cdr gb>>;return mn end;

symbolic procedure groeb!-w6!-7(pol,tt,ifactt,tp,ifactp,zero,one,ll);
% Compute the minimal weight for one polynomial;the idea is,
% that the polynomial has a degree greater than 0.
begin scalar a,b,ev1,ev2,x,y,z,mn;
 ev1:=vdpevlmon pol;
 a:=evweightedcomp2(0,ev1,vdpsortextension!*);
 y:=groeb!-w6!-8(ev1,tt,ifactt,tp,ifactp,zero,zero,one,ll);
 y:=(rnminus!: car y).(rnminus!: cdr y);
 pol:=vdpred pol;
 while not(vdpzero!? pol)do
 <<ev2:=vdpevlmon pol;
  pol:=vdpred pol;
  b:=evweightedcomp2(0,ev2,vdpsortextension!*);
  if not(a=b)then
   <<x:=groeb!-w6!-9(ev2,tt,ifactt,tp,ifactp,car y,cdr y,one,ll,nil);
     if x then
     <<z:=rndifference!:(x,one);
       if  rnminusp!: rndifference!:(zero,x)and
     (rnminusp!: z or rnzerop!: z)and
     (null mn or rnminusp!: rndifference!:(x,mn))
       then mn:=x>>>>>>;return mn end;

symbolic procedure groeb!-w6!-8(ev,tt,ifactt,tp,ifactp,sum1,sum2,m,dm);
begin scalar x,y,z;
 if ev then<<x:=rntimes!:(!*i2rn car ev,m);
             y:=rntimes!:(!*i2rn car tp,ifactp);
             z:=rntimes!:(!*i2rn car tt,ifactt)>>;
 return
  if null ev then sum1.sum2 else
   groeb!-w6!-8(cdr ev,cdr tt,ifactt,cdr tp,ifactp,
    rnplus!:(sum1,rntimes!:(y,x)),
    rnplus!:(sum2,rntimes!:(rndifference!:(z,y),x)),
    rndifference!:(m,dm),
    dm)end;

symbolic procedure groeb!-w6!-9(ev,tt,ifactt,tp,ifactp,y1,y2,m,dm,done);
% Compute the rational solution s:
%(tp+s*(tt-tp))*ev1=(tp+s*(tt-tp))*evn.
% The sum with ev1 is collected already in y1 and y2(with negative sign).
% This routine collects the sum with evn and computes the solution.
begin scalar x,y,z;
 if ev then<<x:=rntimes!:(!*i2rn car ev,m);
             y:=rntimes!:(!*i2rn car tp,ifactp),
             z:=rntimes!:(!*i2rn car tt,ifactt)>>;
 return if null ev then
         if null done then nil
          else rnquotient!:(rnminus!: y1,y2)
         else
         groeb!-w6!-9(cdr ev,cdr tt,ifactt,cdr tp,ifactp,
              rnplus!:(y1,rntimes!:(y,x)),
              rnplus!:(y2,rntimes!:(rndifference!:(z,y),x)),
              rndifference!:(m,dm),
              dm,
              done or not(car ev = 0)) end;

symbolic procedure groeb!-w7(tt,omega,x,tto,y);
% Compute omega*x*(1-tt)+tto*y*tt.
% tt is a rational number.
% x and y are rational numbers(inverses of the legths of omega/tt).
begin scalar n,z;n:=!*i2rn 1;
 omega:=for each g in omega collect
 <<z:=rnplus!:(rntimes!:(rntimes!:(!*i2rn g,x),
                                 rndifference!:(!*i2rn 1,tt)),
                     rntimes!:(rntimes!:(!*i2rn car tto,y),tt));
   tto:=cdr tto;
   n:=groeb!-w7!-1(n,rninv!: z);z>>;
 omega:=for each a in omega collect rnequiv rntimes!:(a,!*i2rn n);
 return omega end;

symbolic procedure groeb!-w7!-1(n,m);
% Compute lcm of n and m. N and m are rational numbers.
% Return the lcm.
% Ignore the denominators of n and m.
begin scalar x,y,z;
 if atom n then x:=n else
 <<x:=rnprep!: n;
  if not atom x then x:=cadr x>>;
 if atom m then y:=m else
 <<y:=rnprep!: m;if not atom y then y:=cadr y>>;
 z:=lcm(x,y);return z end;

symbolic procedure groeb!-w8(p,gb);
% Computes the cofactor of p wrt gb.
% Result is a list of cofactors corresponding to g.
% The cofactor 0 is represented as nil.
begin scalar x,y;
 x:=groeb!-w8!-1(p,gb);p:=secondvalue!*;
 while not vdpzero!? p do
 <<y:=groeb!-w8!-1(p,gb);p:=secondvalue!*;
  x:=for each pp in pair(x,y)do
   if null car pp then cdr pp
    else if null cdr pp then car pp
     else vdpsum(car pp,cdr pp)>>;return x end;

symbolic procedure groeb!-w8!-1(p,gb);
% Search in groebner basis gb the polynomial which divides the
% head monomial of the polynomial p. The walk version of
% groebsearchinlist.
% Result: the sequence corresponding to g with the monomial
% factor inserted.
begin scalar e,cc,r,done,pp;
 pp:=vdpevlmon p;cc:=vdplbc p;
 r:=for each poly in gb collect
  if done then nil else
   if vevdivides!?(vdpevlmon poly,pp)then
   <<done:=t;e:=poly;
    cc:=vbcquot(cc,vdplbc poly);
    pp:=vevdif(pp,vdpevlmon poly);
    secondvalue!*:=
      vdpsum(vdpprod(gsetsugar(vdpfmon(vbcneg cc,pp),nil),poly), p);
    vdpfmon(cc,pp)>>
   else nil;
 if null e then
 <<print p;print "-----------------";print gb;
  rerror(groebner,28,"groeb-w8-1 illegal structure")>>;
 return r end;

symbolic procedure groeb!-w9(mode,ext);
% Switch on vdp order mode 'mode' with extension 'ext'.
% Result is the previous extension.
begin scalar x;
 x:=vdpsortextension!*;vdpsortextension!*:=ext;
 torder2 mode;return x end;

symbolic procedure groeb!-w10 s;
% Convert the dips in s corresponding to the actual order.
groeb!-collect(s,'groeb!-w10!-1);

symbolic procedure groeb!-w10!-1 p;
% Convert the dip p corresponding to the actal order.
begin scalar x;
 x:=vdpfmon(vdplbc p,vdpevlmon p);
 x:=gsetsugar(vdpenumerate x,nil);
 p:=vdpred p;
 while not vdpzero!? p do
 <<x:=vdpsum(vdpfmon(vdplbc p,vdpevlmon p),x);p:=vdpred p>>;
 return x end;

symbolic procedure rninv!: x;
% Return inverse of a(rational)number x: 1/x.
<<if atom x then x:=!*i2rn x;
  x:=cdr x;
  if car x < 0 then mkrn(- cdr x,- car x)else mkrn(cdr x,car x)>>;

symbolic procedure sq2vdp s;
% Standard quotient to vdp.
begin scalar x,y;x:=f2vdp numr s;
 gsetsugar(x,nil);y:=f2vdp denr s;
 gsetsugar(y,0);s:=vdpdivmon(x,vdplbc y,vdpevlmon y);
 return s end;

symbolic procedure vdp2sq v;
% Conversion vdp to standard quotient.
begin scalar x,y,z,one;one := 1 ./ 1;x := nil ./ 1;
 while not vdpzero!? v do
 <<y:=vdp2f vdpfmon(one,vdpevlmon v)./ 1;
  z:=vdplbc v;
  x:=addsq(x,multsq(z,y));
  v:=vdpred v>>;return x end;

endmodule;;end;
