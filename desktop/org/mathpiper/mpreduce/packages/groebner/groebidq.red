module groebidq;

% Calculation of ideal quotient using a modified Buchberger algorithm .

% Authors: H . Melenk,H . M . Moeller,W . Neun,July 1988 .

switch groebfac,groebrm,trgroeb,trgroebs,trgroebr,groebstat;

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


!*groebidqbasis:=t;       % Default : basis from idq .

% Variables for counting and numbering .

symbolic procedure groebidq2(p,f);
% Setup all global variables for the Buchberger algorithm;
% printing of statistics .
begin scalar groetime!*,tim1,spac,spac1,p1,
  pairsdone!*,!*gsugar;
 groetime!*:=time();
 vdponepol();% we construct dynamically
 hcount!*:=0;pcount!*:=0;mcount!*:=0;fcount!*:=0;bcount!*:=0;
 b4count!*:=0;hzerocount!*:=0;basecount!*:=0;
 if !*trgroeb then
 << prin2 "IDQ Calculation starting ";terprit 2 >>;
 spac:=gctime();p1:=  groebidq3(p,f);
 if !*trgroeb or !*trgroebr or !*groebstat then
 << spac1:=gctime()-spac;terpri();
  prin2t "statistics for IDQ calculation";
  prin2t "==============================";
  prin2 " total computing time(including gc): ";
  prin2(( tim1:=time())-groetime!*);prin2t "          milliseconds  ";
  prin2 "(time spent for garbage collection:  ";prin2 spac1;
  prin2t "          milliseconds)";terprit 1;
  prin2  "H-polynomials total: ";prin2t hcount!*;
  prin2  "H-polynomials zero : ";prin2t hzerocount!*;
  prin2  "Crit M hits: ";prin2t mcount!*;
  prin2  "Crit F hits: ";prin2t fcount!*;
  prin2  "Crit B hits: ";prin2t bcount!*;
  prin2  "Crit B4 hits: ";prin2t b4count!* >>;
 return if !*groebidqbasis then car groebner2(p1,nil)else p1 end;

symbolic procedure groebidq3(g0,fff);
begin scalar result,x,g,d,d1,d2,p,p1,s,h,g99,one,gi;
 gi:=g0;fff:=vdpsimpcont fff;
 vdpputprop(fff,' number,0);   % Assign number 0 .
 vdpputprop(fff,' cofact,a2vdp 1);% Assign cofactor 1 .
 x:=for each fj in g0 collect
 << fj:=vdpenumerate vdpsimpcont fj;
  vdpputprop(fj,' cofact,a2vdp 0);% Assign cofactor 0 .
  fj >>;
 g0:={ fff };
 for each fj in x do g0:=vdplsortin(fj,g0);
% ITERATION :
  while(d or g0)and not one do
  begin if g0 then
  <<          % Take next poly from input .
   h:=car g0;g0:=cdr g0;p:={ nil,h,h };>>
   else <<          % Take next poly from pairs .
    p:=car d;d:=delete(p,d);s:=idqspolynom(cadr p, caddr p);
   idqmess3(p,s);h:=idqsimpcont idqnormalform(s,g99,'tree);
   if vdpzero!? h then
   <<      !*trgroeb and groebmess4(p,d);
    x:=vdpgetprop(h,'cofact);
    if not vdpzero!? x then
     if vevzero!? vdpevlmon x then one:= t else
     << result:=idqtoresult(x,result);idqmess0 x >>;
   >> >>;
   if vdpzero!? h then goto bott;
   if vevzero!? vdpevlmon h then % Base 1 found .
   <<          idqmess4(p,h);
     result:=gi;d:=g0:=nil;goto bott >>;
     s:=nil;
                  % h polynomial is accepted now .
     h:=vdpenumerate h;
                        idqmess4(p,h);
                              % Construct new critical pairs .
     d1:=nil;
     for each f in g do
     << d1:=groebcplistsortin({ tt(f,h), f,h },d1);
      if tt(f,h)=vdpevlmon f then
      << g:=delete(f,g);!*trgroeb and groebmess2 f >> >>;
                  !*trgroeb and groebmess51 d1;
       d2:=nil;
       while d1 do
       << d1:=groebinvokecritf d1;
        p1:=car d1;d1:=cdr d1;
        if groebbuchcrit4t(cadr p1,caddr p1)
         then d2:=append(d2,list p1)
          else
          << x:=idqdirectelement(cadr p1,caddr p1);
           if not vdpzero!? x then
            if vevzero!? vdpevlmon x then one:= t else
            << idqmess1(x,cadr p1,caddr p1);
             result:=idqtoresult(x,result)>> >>;
       d1:=groebinvokecritm(p1,d1) >>;
             %   D:=groebInvokeCritB(h,D);
       d:=groebcplistmerge(d,d2);
       g:=h . g;
       g99:=groebstreeadd(h,g99);
                            !*trgroeb and groebmess8(g,d);
bott: end;%  ITERATION
                      % Now calculate groebner base from quotient base .
 if one then result:=list vdpfmon(a2vbc 1,vevzero());
 idqmess2 result;return result end;% MACROLOOP

symbolic procedure idqtoresult(x,r);
% X is a new element for the quotient r,
% is is reduced by r and then added .
<< x:=groebsimpcontnormalform groebnormalform(x,r,' sort);
 if vdpzero!? x then r else vdplsortin(x,r)>>;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%    Reduction of polynomials .
%

symbolic procedure idqnormalform(f,g,type);
% General procedure for reduction of one polynomial from a set;
% f is a polynomial, G is a Set of polynomials either in
% a search tree or in a sorted list;
% type describes the ordering of the set G :
%    'TREE     G is a search tree
%    'SORT     G is a sorted list
%    'LIST     G is a list,but not sorted;
% f has to be reduced modulo G;
% version for idealQuotient : doing side effect calculations for
% the cofactors;only headterm reduction .
begin scalar c,vev,divisor,done,fold;
 fold:=f;
 while not vdpzero!? f and g do
 begin vev:=vdpevlmon f;c:=vdplbc f;
  if type='sort then
   while g and vevcompless!?(vev,vdpevlmon(car g)) do g:=cdr g;
  divisor:=groebsearchinlist(vev,g);
  if divisor then done:=t;% True action indicator .
  if divisor and !*trgroebs then
  << prin2 "//-";prin2 vdpnumber divisor >>;
  if divisor then
   if !*vdpinteger then f:=idqreduceonestepint(f,nil,c,vev,divisor)
    else f:=idqreduceonesteprat(f,nil,c,vev,divisor)
   else g:=nil end;
 return if done then f else fold % In order to preserve history .
 end;

% % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % %
%
%  Special reduction procedures .

symbolic procedure idqreduceonestepint(f,dummy,c,vev,g1);
% Reduction step for integer case :
% calculate f=a * f-b * g   a,b such that leading term vanishes
%                     (vev of lvbc g divides vev of lvbc f)
%
% and  calculate f1=a * f1;
% return value=f,secondvalue=f1 .
begin scalar vevlcm,a,b,cg,x,fcofa,gcofa;
 dummy:=nil;fcofa:=vdpgetprop(f,' cofact);
 gcofa:=vdpgetprop(g1,' cofact);vevlcm:=vevdif(vev,vdpevlmon g1);
 cg:=vdplbc g1;
            % Calculate coefficient factors .
 x:=vbcgcd(c,cg);a:=vbcquot(cg,x);
 b:=vbcquot(c,x);
 f:=vdpilcomb1(vdpred f,a,vevzero(),vdpred g1,vbcneg b,vevlcm);
  x:=vdpilcomb1(fcofa,a,vevzero(),gcofa,vbcneg b,vevlcm);
  vdpputprop(f,' cofact,x);return f end;

symbolic procedure idqreduceonesteprat(f,dummy,c,vev,g1);
% Reduction step for rational case :
% calculate f=f-g / vdplbc f .
begin scalar x,fcofa,gcofa,vev;
 dummy:=nil;fcofa:=vdpgetprop(f,' cofact);
 gcofa:=vdpgetprop(g1,' cofact);vev:=vevdif(vev,vdpevlmon g1);
 x:=vbcneg vbcquot(c,vdplbc g1);
 f:=vdpilcomb1(vdpred f,a2vbc 1,vevzero(),vdpred g1,x,vev);
 x:=vdpilcomb1(fcofa,a2vbc 1,vevzero(),gcofa,x,vev);
 vdpputprop(f,' cofact,x);return f end;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%   Calculation of an S-polynomial and related things .
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

symbolic procedure idqspolynom(p1,p2);
begin scalar s,ep1,ep2,ep,rp1,rp2,db1,db2,x,
 cofac1,cofac2;
 if vdpzero!? p1 then return p1;if vdpzero!? p2 then return p2;
 cofac1:=vdpgetprop(p1,' cofact);
 cofac2:=vdpgetprop(p2,' cofact);
 ep1:=vdpevlmon p1;ep2:=vdpevlmon p2;ep:=vevlcm(ep1,ep2);
 rp1:=vdpred p1;rp2:=vdpred p2;db1:=vdplbc p1;db2:=vdplbc p2;
 if !*vdpinteger then
 << x:=vbcgcd(db1,db2);
  db1:=vbcquot(db1,x);db2:=vbcquot(db2,x)>>;
 ep1:=vevdif(ep,ep1);ep2:=vevdif(ep,ep2);db2:=vbcneg db2;
 s:=vdpilcomb1(rp2,db1,ep2,rp1,db2,ep1);
 x:=vdpilcomb1(cofac2,db1,ep2,cofac1,db2,ep1);
 vdpputprop(s,' cofact,x);return s end;

symbolic procedure idqdirectelement(p1,p2);
% The s-Polynomial is reducable to zero because of
% buchcrit 4 . So we can calculate the corresponing cofactor directly .
( if vdpzero!? c1 and vdpzero!? c2 then c1
   else vdpdif(vdpprod(p1,c2), vdpprod(p2,c1))
                     )where c1=vdpgetprop(p1,' cofact),
                                 c2=vdpgetprop(p2,' cofact);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%   Normailsation with cofactors taken into account .
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


symbolic procedure idqsimpcont p;
 if !*vdpinteger then idqsimpconti p else idqsimpcontr p;

% Routines for integer coefficient case :
% Calculation of contents and dividing all coefficients by it .

symbolic procedure idqsimpconti p;
% Calculate the contents of p and divide all coefficients by it .
begin scalar res,num,cofac;
 if vdpzero!? p then return p;
 cofac:=vdpgetprop(p,' cofact);num:=car vdpcontenti p;
 if not vdpzero!? cofac then num:=vbcgcd(num,car vdpcontenti cofac);
 if not vbcplus!? num then num:=vbcneg num;
 if not vbcplus!? vdplbc p then num:=vbcneg num;
 if vbcone!? num then return p;
 res:=vdpreduceconti(p,num,nil);
 if not vdpzero!? cofac then cofac:=vdpreduceconti(cofac,num,nil);
 res:=vdpputprop(res,' cofact,cofac);
 return res end;

% Routines for rational coefficient case :
% calculation of contents and dividing all coefficients by it .

symbolic procedure idqsimpcontr p;
% Calculate the contents of p and divide all coefficients by it .
begin scalar res,cofac;
 cofac:=vdpgetprop(p,' cofact);
 if vdpzero!? p then return p;
 if vbcone!? vdplbc p then return p;
 res:=vdpreduceconti(p,vdplbc p,nil);
 if not vdpzero!? cofac then
  cofac:=vdpreduceconti(cofac,vdplbc p,nil);
 res:=vdpputprop(res,' cofact,cofac);return res end;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%  Trace messages .
%

symbolic procedure idqmess0 x;
if !*trgroeb then
 << prin2t "adding member to intermediate quotient basis:";
  vdpprint x;terpri() >>;

symbolic procedure idqmess1(x,p1,p2);
if !*trgroeb then
 << prin2 "pair(";prin2 vdpnumber p1;prin2 ",";
  prin2 vdpnumber p2;
  prin2t ") adding member to intermediate quotient basis:";
  vdpprint x;terpri() >>;

symbolic procedure idqmess2 b;
if !*trgroeb then
 << prin2t "---------------------------------------------------";
  prin2 "the full intermediate base of the ideal quotient is:";
  for each x in b do vdpprin3t x;
  prin2t "---------------------------------------------------";
  terpri() >>;

symbolic procedure idqmess3(p,s);
if !*trgroebs then
 << prin2 "S-polynomial from ";groebpairprint p;vdpprint s;
  prin2t "with cofactor";vdpprint vdpgetprop(s,' cofact);
  groetimeprint();terprit 3 >>;

symbolic procedure idqmess4(p,h);
if car p then                  % Print for true h-Polys .
 << hcount!*:=hcount!* #+ 1;
  if !*trgroeb then << terpri();prin2  "H-polynomial ";
  prin2 pcount!*;
  groebmessff(" from pair(",cadr p,nil);
  groebmessff(",",caddr p,")");vdpprint h;
  prin2t "with cofactor";vdpprint vdpgetprop(h,' cofact);
  groetimeprint() >> >>
 else
  if !*trgroeb then <<          % Print for input polys .
   prin2t "candidate from input:";
   vdpprint h;
   groetimeprint() >>;

endmodule;;end;
