module buchbg;% Central Groebner base code: Buchberger algorithm.

% Authors: H. Melenk,H. M. Moeller,W. Neun
% ZIB Berlin,August 2000

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


flag('(groebrestriction groebresmax gvarslast groebmonfac
           groebprotfile glterms),'share);

groebrestriction:=nil;groebresmax:=300;groebmonfac:=1;
groecontcount!*:=10;
!*gsugar:=t;
!*groelterms:=t;
!*groebfullreduction:=t;
!*groebdivide:=t;

switch groebopt,trgroeb,trgroebs,trgroeb1,
 trgroebr,groebfullreduction,groebstat,groebprot;

% Variables for counting and numbering.

% Option'groebopt'"optimizes" the given input
%                         polynomial set(variable ordering).
% option'trgroeb'Prints intermediate
%                         results on the output file.
% option'trgroeb1'Prints internal representation
%                         of critical pair list d.
% option'trgroebs'Prints's'- polynomials
%                         on the output file.
% option'trgroebr'Prints(intermediate)results and
%                         computation statistics.
% option'groebstat'The statistics are printed.
%
% option'groebrm'Multiplicities of factors in h-polynomials are reduced
%                         to simple factors .
% option'groebdivide'The algorithm avoids all divisions(only for modular
%                         calculation), if this switch is set off.
% option'groebprot'Write a protocol to the variable "groebprotfile".

symbolic procedure buchvevdivides!?(vev1,vev2);
% Test : vev1 divides vev2 ? for exponent vectors vev1,vev2.
vevmtest!?(vev2,vev1)and
(null gmodule!* or gevcompatible1(vev1,vev2,gmodule!*));

symbolic procedure gevcompatible1(v1,v2,g);
% Test whether'v1'and'v2'belong to the same vector column.
 if null g then t
  else if null v1 then(null v2 or gevcompatible1('(0), v2,g))
  else if null v2 then gevcompatible1(v1,'(0), g)else
(car g=0 or car v1=car v2)
     and gevcompatible1(cdr v1,cdr v2,cdr g);

symbolic procedure gcompatible(f,h);
 (null gmodule!* or gevcompatible1(vdpevlmon f,vdpevlmon h,gmodule!*));
 
%symbolic procedure gcompatible(f,h);
% null gmodule!* or gevcompatible1(vdpevlmon f,vdpevlmon h,gmodule!*);

symbolic procedure groebmakepair(f,h);
% Construct a pair from polynomials'f'and'h'.
begin scalar ttt,sf,sh;
 ttt:=tt(f,h);
 return if !*gsugar then
 << sf:=gsugar(f)#+ vevtdeg vevdif(ttt,vdpevlmon f);
  sh:=gsugar(h)#+ vevtdeg vevdif(ttt,vdpevlmon h);
{ttt,f,h,max(sf,sh)}>>
 else{ttt,f,h}end;

% The 1-polynomial will be constructed at run time
% because the length of the vev is not known in advance.
fluid'(vdpone!*);

symbolic procedure vdponepol;
% Construct the polynomial=1.
 vdpone!*:=vdpfmon(a2vbc 1,vevzero());

symbolic procedure groebner2(p,r);
% Setup all global variables for the Buchberger algorithm,
% printing of statistics.
 begin scalar groetime!*,tim1,spac,spac1,p1,factortime!*,
  pairsdone!*,factorlevel!*,groesfactors!*,!*gcd;
  factortime!*:=0;groetime!*:=time();
  vdponepol();% we construct dynamically
  hcount!*:=0;mcount!*:=0;fcount!*:=0;
  bcount!*:=0;b4count!*:=0;hzerocount!*:=0;
  basecount!*:=0;!*gcd:=t;glterms:={'list};
  groecontcount!*:=10;
  if !*trgroeb then
  <<prin2"Groebner Calculation starting ";terprit 2;
   prin2" groebopt: ";print !*groebopt>>;
   spac:=gctime();
   p1:= if !*groebfac or null !*gsugar
    then groebbasein(p,!*groebfac,r)where !*gsugar=nil
    else gtraverso(p,nil,nil);
   if !*trgroeb or !*trgroebr or !*groebstat then
   <<spac1:=gctime()-spac;terpri();
    prin2t"statistics for GROEBNER calculation";
    prin2t"===================================";
    prin2" total computing time(including gc): ";
    prin2(( tim1:=time())- groetime!*);
    prin2t"          milliseconds  ";
    if factortime!* neq 0 then
    <<prin2"(time spent in FACTOR(excl. gc):    ";
     prin2 factortime!*;prin2t "          milliseconds)">>;
     prin2"(time spent for garbage collection:  ";
     prin2 spac1;prin2t "          milliseconds)";terprit 1;
     prin2"H-polynomials total: ";prin2t hcount!*;
     prin2"H-polynomials zero : ";prin2t hzerocount!*;
     prin2"Crit M hits: ";prin2t mcount!*;
     prin2"Crit F hits: ";prin2t fcount!*;
     prin2"Crit B hits: ";prin2t bcount!*;
     prin2"Crit B4 hits: ";prin2t b4count!*>>;return p1 end;

smacro procedure testabort h;
 vdpmember(h,abort1)or
'cancel=( abort2:=groebtestabort(h,abort2));

symbolic procedure groebenumerate f;
%'f'is a temporary result. Prepare it for medium range storage
% and ssign a number.
 if vdpzero!? f then f else
 <<vdpcondense f;
  if not vdpgetprop(f,'number)then
  <<f:=vdpputprop(f,'number,(pcount!*:=pcount!* #+ 1));
    if !*groebprot then
    <<groebprotsetq(mkid('poly,pcount!*),'candidate);
      vdpputprop(f,'groebprot,mkid('poly,pcount!*))
  >> >>;f>>;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Buchberger's Algorithm
%
% INPUT :   G0={f1,...,fr} set of nonzero polynomials.
% OUTPUT:   groebner basis(list of nonzero polynomials).
%
% internal variables:
%
% problems  list of problems to be processed. Problems is non nil,
%           if the inital problem was split by a successful factoring.
% results   Collection of results from problems.
% g         Basis under construction.
% g1        Local pointer to g.
% d         List of critical pairs during algorithm.
% d1,d2     Local lists of pairs during update of d.
% f,fj      Polynomials.
% p,p1,p2   Pairs.
% s,h       Polynomials in the central part of the algorithm
%       (the "s-poly" and the "h-poly" selon Buchberger).
% g99       Set of polynomials used for reduction.
% abort1    List of polynomials in factorization context.
%           S calculation branch can be cancelled if one of
%           these polys is detected.
% abort2    List of sets of polynomials. If a new h polynomial
%           is calculated,it should be removed from the sets.
%           If one set becomes null,the set restriction is
%           fulfilled and the branch can be cancelled.

% Fix by Herbert Melenk, Feb 2011
 
symbolic procedure groebbasein(g0,fact,abort1);
begin scalar abort2,d,d1,d2,g,gg,g1,g99,h,hlist,lasth,lv,p,problems,vars_g,
   p1,results,s,x;integer gvbc,probcount!*;
 groebabort!*:=abort1;lv:=length vdpvars!*;
 for each p in g0 do if vdpzero!? p then g0:=delete(p,g0);
 if !*groebprereduce then g0:=groebprereduce g0;
 x:=for each fj in g0 collect
 <<groebsavelterm fj;gsetsugar(vdpenumerate vdpsimpcont fj,nil)>>;
  if !*groebprot then
  for each f in x do
  <<groebprotsetq(mkid('poly,h:=vdpnumber f),vdp2a f);
   vdpputprop(f,'groebprot,mkid('poly,h))>>;
  g0:=x;
                                   % Establish the initial problem
  problems:={{nil,nil,nil,g0,abort1,nil,nil,vbccurrentmode!*,nil,nil}};
  !*trgroeb and groebmess1(g,d);
  go to macroloop;
macroloop:
while problems and gvbc < groebresmax do
begin
                                   % Pick up next problem
 x:=car problems;d:=car x;g:=cadr x;
 % g99:=groeblistreconstruct caddr x;
 g99:=vdplsort caddr x;g0:=cadddr x;abort1:=nth(x,5);
 abort2:=nth(x,6);pairsdone!*:=nth(x,7);h:=nth(x,8);
                     % vbccurrentmode!*
 factorlevel!*:=nth(x,9);groesfactors!*:=nth(x,10);
 problems:=cdr problems;
 g0:=% Sort'g0',but keep factor in first position
 if factorlevel!* and  g0 and cdr g0 then car g0.vdplsort cdr g0
  else vdplsort g0;x:=nil;lasth:=nil;
            !*trgroeb and groebmess23(g0,abort1,abort2);
 while d or g0 do
 begin
  if groebfasttest(g0,g,d,g99)then go to stop;
  !*trgroeb and groebmess50 g;
  if g0 then
  <<h:=car g0;g0:=cdr g0;gsetsugar(h,nil);
   groebsavelterm h;p:={nil,h,h}
  >>else
  <<p:=car d;d:=delete(p,d);
    s:=groebspolynom(cadr p,caddr p);
    if fact then
     pairsdone!*:=(vdpnumber cadr p.vdpnumber caddr p).pairsdone!*;
                       !*trgroeb and groebmess3(p,s);
     h:=groebnormalform0(s,g99,'tree,fact);groebsavelterm h;
     h:=groebsimpcontnormalform h;
     if vdpzero!? h then !*trgroeb and groebmess4(p,d);
                                  % Test for possible chains
     if not vdpzero!? h then  % only for real h's
     <<s:=groebchain(h,cadr p,g99);
      if s=h then h:=groebchain(h,caddr p,g99);
      if secondvalue!* then g:=delete(secondvalue!*,g)
     >>
  >>;
  if vdpzero!? h then go to bott;
  if vevzero!? vdpevlmon h then % base 1 found
  <<!*trgroeb and groebmess5(p,h);go to stop>>;
  if testabort(h)then
  <<!*trgroeb and groebmess19(h,abort1,abort2);go to stop>>;
  s:= nil;
                % Look for implicit or explicit factorization
  hlist:=nil;
  if groebrestriction!* then hlist:=groebtestrestriction(h,abort1);
  if not hlist and fact then hlist:=groebfactorize(h,abort1,g,g99);
  if hlist='zero then go to bott;
  if groefeedback!* then g0:=append(groefeedback!*,g0);
  groefeedback!*:=nil;
               % Factorisation found but only one factor survived
  if hlist and length hlist=2 then<<h:=car cadr hlist;hlist:=nil>>;
  if hlist then
  <<if hlist neq'cancel then
    problems:= groebencapsulate(hlist,d,g0,g,g99,abort1,abort2,problems,fact);
    go to stop
  >>;
              %'h'polynomial is accepted now
  h:=groebenumerate h;!*trgroeb and groebmess5(p,h);
                          % Construct new critical pairs
  d1:=nil;
               !*trgroeb and groebmess50(g);
  gg:=g;vars_g:=variables g;                                  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
  for each f in g do
   if(car p or % That means "not an input polynomial"
       not member(vdpnumber h.vdpnumber f,pairsdone!*)
        )and gcompatible(f,h)then
  <<d1:=groebcplistsortin(groebmakepair(f,h),d1);
    if tt(f,h)=vdpevlmon(f)then
    <<g:=delete(f,g);!*trgroeb and groebmess2 f>>
  >>;
  if vars_g neq variables g then g:=gg;                       %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
                !*trgroeb and groebmess51 d1;
  d2:=nil;
  while d1 do
  <<d1:=groebinvokecritf d1;p1:=car d1;d1:=cdr d1;
    d2:=groebinvokecritbuch4(p1,d2);
    d1:=groebinvokecritm(p1,d1)
  >>;
  d:=groebinvokecritb(h,d);
  d:=groebcplistmerge(d,d2);
                     % Monomials and binomials
  if vdplength h < 3 and car p then
  <<g:=groebsecondaryreduction(h,g,g99,d,nil,t);
    if g='abort then go to stop;g99:=secondvalue!*;
    d:=thirdvalue!*
  >>;
  g:=h.g;lasth:=h;
  g99:=groeblistadd(h,g99);
                         !*trgroeb and groebmess8(g,d);
  go to bott;
stop:d:=g:=g0:=nil;
bott:;
end;
g:=vdplsort g;% Such that g descending
x:=groebbasein2(g,g99,problems,abort1,abort2,fact);
g1:=car x;problems:=cdr x;
if g1 then<<results:=g1.results;gvbc:=gvbc+1>>;
!*trgroeb and groebmess13(g1,problems)end;
if gvbc >= groebresmax then
  lpriw("########","warning: GROEBRESMAX limit reached");
return groebbasein3 results end;

symbolic procedure variables g;
% "g" is a list of vdps; the procedure returns a list of nil and t
% where t is set if the corresponding dip variable occurs in "g".
 begin integer l,var_count; scalar ev,f,cvars,vars;
  if null g then return nil;
  l:=length dipvars!*;
  vars:=for i:=1 step 1 until l collect nil;
b: f:=cadr cddr car g;
c: ev:=car f; cvars:=vars;
d: if car ev #> 0 and null car cvars then
  <<car cvars:=t; var_count:=var_count#+1>>;
  if var_count eq l then return vars;
  ev:=cdr ev; if ev then <<cvars:=cdr cvars; go to d>>;
  f:=cddr f; if f then go to c;
  g:=cdr g; if g then go to b;
  return vars;
 end;

% end of fix, 3.4.2011

symbolic procedure groebfasttest(g0,g,d,g99);
<<g:=g0:=d:=g99:=nil;nil>>;% A hook for special techniques

symbolic procedure groebbasein2(g,g99,problems,abort1,abort2,fact);
% Final reduction for a base'g': reduce each polynomial with the
% other members;here again support of factorization.
begin scalar f,g1,!*groebfullreduction,!*groebheufact,!*gsugar,% Saving value
    h,hlist,x;integer cnt;
 !*groebfullreduction:=t;g1:=nil;
 while g do
 <<h:=car g;g:=cdr g;
  if !*groebprot then
   groebprotsetq('candidate,mkid('poly,vdpnumber h));
  h:=groebnormalform0(h,g,'sort,nil);
  f:=groebsimpcontnormalform h;
                       !*trgroeb and groebmess26(h,f);
  if !*groebprot then groebprotsetq({'gb,cnt:=cnt+1},'candidate);
  if vdpone!? f then<<g1:=g:=nil>>;% Base{1} found
                                               % very late now
  if fact and not vdpzero!? f then
  <<hlist:=groebfactorize(f,abort1,nil,nil);
  if not null hlist then
  <<          % lift structure
   hlist:=for each x in cdr hlist collect car x;
                         % discard superfluous factors
   for each h in hlist do
    if vdpmember(h,abort1)then
    <<hlist:=delete(h,hlist);!*trgroeb and groebmess19(h,abort1,abort2)>>;
                                   % Generate new subproblems
     x:=0;
     for each h in hlist do
     <<hlist:=delete(h,hlist);
      h:=groebenumerate h;
      problems:=
      {nil,              % null D
           append(g1,g),  % base
           g99,               % g99
           {h},               % g0
           append(hlist,abort1),
           abort2,
           pairsdone!*,
           vbccurrentmode!*,
        (x:=x+1).factorlevel!*,
           groesfactors!*}. problems>>;
      g:=g1:=nil;% Cancel actual final reduction
      f:=nil >> >>;
    if f and vdpevlmon h neq vdpevlmon f then
    <<g:=vdplsort append(g,f.g1);g1:=nil>>else
     if f and not vdpzero!? f then g1:=append(g1,{f})>>;
 return g1.problems end;

symbolic procedure groebbasein3 results;
% Final postprocessing: remove multiple bases from the result.
 begin scalar x,g,f,p1,p2;
  x:=nil;g:=results;p1:=p2:=0;
  while results do
  <<if vdpone!? car car results     % Exclude multiple{1}
   then p1:=p1+1              % count ones
   else
   <<f:=for each p in car results   % Delete props for member
    collect vdpremallprops p;
    if member(f,x)              % each base only once
     then p2:=p2+1            % count multiples
     else if not groebabortid( f,groebabort!*)
      then x:=f.x;
    results:=cdr results>> >>;
 results:=if null x then{{vdpone!*}}else x;
 return results end;

fluid'(!*vbccompress);

symbolic procedure groebchain(h,f,g99);
% Test if a chain from h-plynomials can be computed from the'h'.
begin scalar count,found,h1,h2,h3;
 secondvalue!*:=nil;
 return h;% Erst einmal.
 if not buchvevdivides!?(vdpevlmon h,vdpevlmon f)
  then return h;
 h2:=h;h1:=f;found:=t;count:=0;
 while found do
 <<h3:=groebspolynom(h1,h2);
  h3:=groebnormalform0(h3,g99,'tree,t);
  h3:=vdpsimpcont h3;
  if vdpzero!? h3 then
  <<found:=nil;
   prin2t "chain---------------------------";
   vdpprint h1;vdpprint h2;vdpprint h3;
   secondvalue!*:=f;count:=9999>>
  else if vdpone!? h3 then
  <<found:=nil;
     prin2t "chain---------------------------";
     vdpprint h1;vdpprint h2;vdpprint h3;
     h2:=h3;count:=9999>>
  else if buchvevdivides!?(vdpevlmon h3,vdpevlmon h2)then
  <<found:=t;
   prin2t "chain---------------------------";
   vdpprint h1;vdpprint h2;vdpprint h3;
   h1:=h2;h2:=h3;count:=count+1>>
  else found:=nil>>;
  return if count > 0 then
  <<prin2 "CHAIN :";prin2t count;h2>>else h end;


symbolic procedure groebencapsulate(hlist,d,g0,g,g99,
               abort1,abort2,problems,fact);
%'hlist'is a factorized h-poly. This procedure has the job to
% form new problems from hlist and to add them to problems.
% Result is problems.
% Standard procedure: only creation of subproblems.
begin scalar factl,     % List of factorizations under way.
 u,y,z;integer fc;
 if length vdpvars!* > 10 or car hlist neq'factor then
  return groebencapsulatehardcase(hlist,d,g0,g,g99,
   abort1,abort2,problems,fact);
                        % Encapsulate for each factor.
  factl:=groebrecfactl{hlist};
               !*trgroeb and groebmess22(factl,abort1,abort2);
  for each x in reverse factl do
  <<y:=append(car x,g0);
   z:=vdpunion(cadr x,abort1);
   u:=append(caddr x,abort2);
   problems:={d,g,
    g,                      % future g99
    y,                      % as new problem
    z,                      % abort1
    u,                      % abort2
    pairsdone!*,            % pairsdone!*
    vbccurrentmode!*,
    (fc:=fc+1).factorlevel!*,
    groesfactors!*
    }. problems>>;
   return problems end;

symbolic procedure groebencapsulatehardcase(hlist,d,g0,g,g99,
               abort1,abort2,problems,fact);
%'hlist'is a factorized h-poly. This procedure has the job to
% form new problems from hlist and to add them to problems.
% Result is problems.
% First the procedure tries to compute new h-polynomials from the
% remaining pairs which are not affected by the factors in hlist.
% Purpose is to find further factorizations and to do calculations
% in common for all factors in order to shorten the separate later
% branches.
begin scalar factl,     % List of factorizations under way.
  factr,     % Variables under factorization.
  break,d1,d2,f,fl1,gc,h,p,pd,p1,s,u,y,z;
 integer fc;
 factl:={hlist};factr:=vdpspace car cadr hlist;
 for each x in cdr hlist do
 for each p in x do factr:=vevunion(factr,vdpspace p);
% ITER:
                             % Now process additional pairs.
 while d or g0 do
 begin
  break:=nil;
  if g0 then
  <<               % Next poly from input.
   s:=car g0;g0:=cdr g0;p:={nil,s,s}>>
    else
  <<               % Next poly fropm pairs.
   p:=car d;d:=delete(p,d);
   if not vdporthspacep(car p,factr)then
    s:=nil else
    <<s:=groebspolynom(cadr p,caddr p);
     !*trgroeb and groebmess3(p,s)>> >>;
    if null s or not vdporthspacep(vdpevlmon s,factr)then
    <<                     % Throw away s polynomial .
     f:=cadr p;
     if not vdpmember3(f,g0,g,gc)then gc:=f.gc;
     f:=caddr p;
     if car p and not vdpmember3(f,g0,g,gc)
      then gc:=f.gc;go to bott>>;
     h:=groebnormalform(s,g99,'tree);
     if vdpzero!? h and car p then !*trgroeb and groebmess4(p,d);
     if not vdporthspacep(vdpevlmon h,factr)then
     <<                     % Throw away h-polynomial.
      f:=cadr p;
      if not vdpmember3(f,g0,g,gc)then gc:=f.gc;
      f:=caddr p;
      if car p and not vdpmember3(f,g0,g,gc)then gc:=f.gc;
      go to bott>>;
%%%  if car p then
%%%    pairsdone!*:=(vdpnumber cadr p.vdpnumber caddr p).pairsdone!*;
     if vdpzero!? h then go to bott;
     if vevzero!? vdpevlmon h then     % Base 1 found.
      go to stop;
     h:=groebsimpcontnormalform h;%  Coefficients normalized.
     if testabort h then
     <<!*trgroeb and groebmess19(h,abort1,abort2);
      go to stop>>;
     s:=nil;hlist:=nil;
     if groebrestriction!* then hlist:=groebtestrestriction(h,abort1);
     if hlist='cancel then go to stop;
     if not hlist and fact then
     hlist:=groebfactorize(h,abort1,g,g99);
     if groefeedback!* then g0:=append(groefeedback!*,g0);
     groefeedback!*:=nil;
     if hlist and length hlist=2 then
     <<h:=car cadr hlist;hlist:=nil>>;
     if hlist then
     <<for each x in cdr hlist do
      for each h in x do factr:=vevunion(factr,vdpspace h);
      factl:=hlist.factl;% Add to factors.
      go to bott>>;
     h:=groebenumerate h;      % Ready now.
                    !*trgroeb and groebmess5(p,h);
                          % Construct new critical pairs.
     d1:=nil;
     for each f in g do
      if tt(f,h)=vdpevlmon(f)and gcompatible(f,h)then
      <<g:=delete(f,g);
       d1:=groebcplistsortin(groebmakepair(f,h),d1);
                         !*trgroeb and groebmess2 f>>;
                 !*trgroeb and groebmess51 d1;
       d2:=nil;
       while d1 do
       <<d1:=groebinvokecritf d1;p1:=car d1;d1:=cdr d1;
        d2:=groebinvokecritbuch4(p1,d2);
        d1:=groebinvokecritm(p1,d1)>>;
        d:=groebinvokecritb(h,d);d:=groebcplistmerge(d,d2);
        if vdplength h < 3 then
        <<g:=groebsecondaryreduction(h,g,g99,d,gc,t);
         if g='abort then go to stop;g99:=secondvalue!*;
        d:=thirdvalue!*;gc:=fourthvalue!*>>;
        g:=h.g;
        g99:=groeblistadd(h,g99);
                        !*trgroeb and groebmess8(g,d);
        go to bott;
stop:   d:=g:=g0:=gc:=factl:=nil;
bott:  end;%ITER
                        % Now collect all relvevant polys.
    g0:=vdpunion(g0,vdpunion(g,gc));
                        % Encapsulate for each factor.
    if factl then
    <<factl:=groebrecfactl factl;
     !*trgroeb and groebmess22(factl,abort1,abort2)>>;
    for each x in reverse factl do
    <<fl1:=(fc:=fc+1).factorlevel!*;
     break:= nil;y:=append(car x,g0);
     z:=vdpunion(cadr x,abort1);
     u:=append(caddr x,abort2);
     if vdpmember(vdpone!*,y)then break:=vdpone!*;
                    % Inspect the unreduced list first .
     if not break then for each p in z do
     if vdpmember(p,y)then break:=p;
                    % Now prepare the reduced list.
     if not break then
     <<y:=append(car x,groebreducefromfactors(g0,car x));
      pd:=secondvalue!*;
      if vdpmember(vdpone!*,y)then break:=vdpone!* else
      for each p in z do if vdpmember(p,y)then break:=p;
      pd:=subla(pd,pairsdone!*)>>;
      if not break then
       problems:={
        nil,                    % new d
        nil,                    % new g
        nil,                    % future g99
        y,                      % as new problem
        z,                      % abort1
        u,                      % abort2
        nil,                    % pairsdone!*
        vbccurrentmode!*,
        fl1,                    % factorlevel!*,
        groesfactors!*            % factor db
    }.problems else !*trgroeb and groebmess19a(break,fl1)>>;
    return problems end;

symbolic procedure groebrecfactl(factl);
% Factl is a list of factorizations:a list of lists of vdps
% generate calculation pathes from them.
begin scalar rf,res,type;
 if null factl then return{{nil,nil,nil}};
 rf:=groebrecfactl(cdr factl);
 factl:=car factl;
 type:=car factl;% FACTOR or RESTRICT
 factl:=cdr factl;
 while factl do
 <<for each y in rf do
  if vdpdisjoint!?(car factl,cadr y)then
  res:={vdpunion(car factl,car y),
  (if type='factor then
      append(for each x in cdr factl collect car x, cadr y)
        else cadr y),
        (if type neq'factor then append(cdr factl,caddr y)
            else caddr y)}.res;
  factl:=cdr factl>>;
 return res end;

symbolic procedure groebtestabort(h,abort2);
% Tests if h is member of one of the sets in abort2.
% if yes, it is deleted. If one wet becomes null,the message
% "CANCEL is returned, otherwise the updated abort2.
begin scalar x,break,res;
          % First test the occurence.
 x:=abort2;
 while x and not break do
 <<if vdpmember(h,car x)then break:=t;x:=cdr x>>;
 if not break then return abort2;% not relvevant
 break:=nil;
 while abort2 and not break do
 <<x:=vdpdeletemember(h,car abort2);
  if null x then break:=t;res:=x.res;
  abort2:=cdr abort2>>;
   !*trgroeb and groebmess25(h,res);
  if break then return'cancel;
  return res end;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%    Reduction of polynomials.
%

symbolic procedure groebnormalform(f,g,type);
 groebnormalform0(f,g,type,nil);

symbolic procedure groebnormalform0(f,g,type,m);
% General procedure for reduction of one polynomial from a set
%'f'is a polynomial,'g'is a set of polynomials either in
%  a search tree or in a sorted list.
%'f'has to be reduced modulo'g'.
%'m'is indicator,whether a selection('m'is true)is wanted.
begin scalar a,break,c,divisor,done,f0,f1,f2,fold,gl,vev;
 integer n,s1,s2;
 scalar zzz;
  if !*groebweak and !*vdpinteger
   and groebweakzerotest( f,g,type)then return f2vdp nil;
  fold:=f;f1:=vdpzero();a:= vbcfi 1;
  gsetsugar(f1,gsugar f);
  while not vdpzero!? f do
  begin
   vev:=vdpevlmon f;c:=vdplbc f;
   if not !*groebfullreduction and not vdpzero!? f1 then g:=nil;
    if null g then
    <<f1:=vdpsum(f1,f);f:=vdpzero();break:=t;divisor:=nil;go to ready>>;
    divisor:=groebsearchinlist(vev,g);
    if divisor then<<done:=t;% true action indicator
                     if m and vdpsortmode!*='revgradlex
                      and vdpzero!? f1 then gl:=f.gl;
                     if !*trgroebs then
                     <<prin2"//-";prin2 vdpnumber divisor>> >>;
    if divisor then
     if vdplength divisor=1 then
      f:=vdpcancelmvev(f,vdpevlmon divisor)else
                      if !*vdpinteger or not !*groebdivide then
                      <<f:=groebreduceonestepint(f,f1,c,vev,divisor);
                        f1:=secondvalue!*;n:=n+1;
                        if not vdpzero!? f and n #> groecontcount!* then
                        <<f0:=f;
                          f:=groebsimpcont2(f,f1);
                          groecontentcontrol(f neq f0);
                          f1:=secondvalue!*;n:=0>> >>
                       else
                        f:=groebreduceonesteprat(f,nil,c,vev,divisor)
               else
                  <<!*gsugar and<<s1:=gsugar(f);s2:=gsugar(f1)>>;
                    f1:=vdpappendmon(f1,vdplbc f,vdpevlmon f);
                    f:=vdpred f;
                    !*gsugar and<<gsetsugar(f,s1);
                                  gsetsugar(f1,max(s2,s1))>> >>;
            ready:
          end;
      if !*groebprot then groebreductionprotocolborder();
      if vdpzero!? f1 then go to ret;
      zzz:=f1;
      if not done then f1:=fold else
      if m and vdpsortmode!*='revgradlex then
      <<if not vdpzero!? f1 then gl:=f1.gl;
       while gl do
       <<f2:=groebnormalformselect car gl;
          if f2 then<<f1:=f2;gl:=nil>>else gl:=cdr gl>> >>;
ret:  return f1 end;

symbolic procedure groecontentcontrol u;
%'u'indicates,that a substantial content reduction was done;
% update content reduction limit from'u'.
 groecontcount!*:=if not numberp groecontcount!* then 10 else
  if u then max(0,groecontcount!*-1)
   else min(10,groecontcount!*+1);

symbolic procedure groebvbcbig!? a;
% Test if'a'is a "big" coefficient.
(if numberp x then(x > 1000000000000 or x <-1000000000000)
  else t)where x=vbcnumber a;

symbolic procedure groebnormalformselect v;
% Select the vdp'v',if the'vdplastvar*'- variable occurs in all
% terms(then return it)or don't select it(then return'nil').
 if countlastvar(v,t)#> 0 then v;

symbolic procedure groebsimpcontnormalform h;
% SimpCont version preserving the property SUGAR.
 if vdpzero!? h then h else
 begin scalar sugar,c;
  sugar:=gsugar h;c:=vdplbc h;
  h:=vdpsimpcont h;gsetsugar(h,sugar);
  if !*groebprot and not(c=vdplbc h)then groebreductionprotocol2
   reval{'quotient,vbc2a vdplbc h,vbc2a c};
  return h end;

symbolic procedure groebsimpcont2(f,f1);
% Simplify two polynomials with the gcd of their contents.
 begin scalar c,s1,s2;
  s1:=gsugar f;s2:=gsugar f1;
  c:=vdpcontent f;
  if vbcone!? vbcabs c then go to ready;
  if not vdpzero!? f1 then
  <<c:=vdpcontent1(f1,c);
   if vbcone!? vbcabs c then go to ready;
   f1:= vdpdivmon(f1,c,nil)>>;
   f:=vdpdivmon(f,c,nil);
                 !*trgroeb and groebmess28 c;
   groebsaveltermbc c;
   gsetsugar(f,s1);gsetsugar(f1,s2);
ready:secondvalue!*:=f1;return f end;

% % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % %
%
%  Special case reductions.
%

symbolic procedure groebprereduce g;
% Reduce the polynomials in g with themselves.
% The reduction is continued until headterms are stable is possible.
 begin scalar res,work,oldvev,f,oldf,!*groebweak,
  !*groebfullreduction;integer count;
  if !*trgroebs then
  <<g:=for each p in g collect vdpenumerate p;
   for each p in g do vdpprint p>>;
  res:=nil;% Delete zero polynomials from'g'.
  for each f in g do if not vdpzero!? f then res:=f.res;
  work:=g:=res:=reversip res;
  while work do
  <<g:=vdplsort res;% Sort prvevious result.
   if !*trgroebs then prin2t "Starting cycle in prereduction.";
   res:=nil;count:=count+1;work:=nil;
   while g do
   <<oldf:=f:= car g;g:=cdr g;
    oldvev:=vdpevlmon f;
    f:=vdpsimpcont groebnormalform(f,g,'sort);
    if(!*trgroebs or !*groebprot)and not vdpequal(f,oldf)then
    <<f:=vdpenumerate f;
     if !*groebprot then
      if not vdpzero!? f then
       groebprotsetq(mkid('poly,vdpnumber f),vdp2a f)
      else groebprotval 0;
      if !*trgroebs then
      <<prin2t "reducing";vdpprint oldf;prin2t "to";vdpprint f>> >>;
      if not vdpzero!? f then
      <<if oldvev neq vdpevlmon f then work:=t;
       res:=f.res>> >> >>;
  return for each f in res collect vdpsimpcont f end;

symbolic procedure groebreducefromfactors(g,facts);
% Reduce the polynomials in G from those in facts.
 begin scalar new,gnew,f,nold,nnew,numbers;
  if !*trgroebs then
  <<prin2t "replacing from factors:";
   for each x in facts do vdpprin2t x>>;
  while g do
  <<f:=car g;g:=cdr g;nold:=vdpnumber(f);
   new:= groebnormalform(f,facts,'list);
   if vdpzero!? new then
   <<if !*trgroebs then<<prin2 "vanishes ";
     prin2 vdpnumber f>> >>
  else if vevzero!? vdpevlmon new then
  <<if !*trgroebs then<<prin2 "ONEPOL ";prin2 vdpnumber f>>;
   g:=nil;
   gnew:={vdpone!*}>>
  else<<if new neq f then
        <<new:=vdpenumerate vdpsimpcont new;
          nnew:=vdpnumber new;
          numbers:=(nold.nnew).numbers;
                if !*trgroebs then<<prin2 "replacing ";
                                     prin2 vdpnumber f;
                                     prin2 " by ";
                                     prin2t vdpnumber new>> >>;
        gnew:=new.gnew>> >>;
   secondvalue!*:=numbers;
   return gnew end;

% % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % %
%
%  Support for reduction by "simple" polynomials.

symbolic procedure groebnormalform1(f,p);
% Short version;reduce f by p;
% special case: p is a monomial.
 if vdplength p=1 then vdpcancelmvev(f,vdpevlmon p)
  else groebnormalform(f,{p},nil);

symbolic procedure groebprofitsfromvev(p,vev);
% Tests,if at least one monomial from p would be reduced by vev.
 if vdpzero!? p then nil
  else if buchvevdivides!?(vev,vdpevlmon p)then t
  else groebprofitsfromvev(vdpred p,vev);

% % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % %
%
%  Special reduction procedures.

symbolic procedure groebreduceonestepint(f,f1,c,vev,g1);
% Reduction step for integer case:
% calculate f= a*f-b*g a,b such that leading term vanishes
%(vev of lvbc g divides vev of lvbc f)
% and  calculate f1=a * f1;
% return value=f,secondvalue=f1.
 begin scalar vevlcm,a,b,cg,x,rg1;
% Trivial case: g1 single monomial.
  if vdpzero!?(rg1:=vdpred g1)
   then return<<f:=vdpred f;secondvalue!*:=f1;f>>;
  vevlcm:=vevdif(vev,vdpevlmon g1);
  cg:=vdplbc g1;
            % Calculate coefficient factors .
  x:=if not !*groebdivide then vbcfi 1 else vbcgcd(c,cg);
  a:=vbcquot(cg,x);
  b:=vbcquot(c,x);
            % Multiply relvevant parts from f and f1 by a(vbc).
  if f1 and not vdpzero!? f1 then f1:=vdpvbcprod(f1,a);
  if !*groebprot then groebreductionprotocol(a,vbcneg b,vevlcm,g1);
  f:= vdpilcomb1(vdpred f,a,vevzero(),
                     rg1,vbcneg b,vevlcm);
            % Return with f and f1.
  secondvalue!*:= f1;thirdvalue!*:=a;return f end;

symbolic procedure groebreduceonesteprat(f,dummy,c,vev,g1);
% Reduction step for rational case:
% calculate f= f-g/vdpLbc(f).
 begin scalar x,rg1,vevlcm;
            % Trivial case: g1 single monomial.
  dummy:=nil;
  if vdpzero!?(rg1:=vdpred g1)then return vdpred f;
            % Calculate coefficient factors.
  x:=vbcneg vbcquot(c,vdplbc g1);
  vevlcm:=vevdif(vev,vdpevlmon g1);
  if !*groebprot then
   groebreductionprotocol( a2vbc 1,x,vevlcm,g1);
  return vdpilcomb1(vdpred f,a2vbc 1,vevzero(),
                            rg1,x,vevlcm)end;

symbolic procedure groebreductionprotocol(a,b,vevlcm,g1);
 if !*groebprot then
  groebprotfile:=
   if not vbcone!? a then
    append(groebprotfile,
  {{'equal,'candidate,
         {'times,'candidate,vbc2a a}},
   {'equal,'candidate,
         {'plus,'candidate,
              {'times,vdp2a vdpfmon(b,vevlcm),
                              mkid('poly,vdpnumber g1)}}}
   })
    else
    append(groebprotfile,
 {{'equal,'candidate,
         {'plus,'candidate,
              {'times,vdp2a vdpfmon(b,vevlcm),
                              mkid('poly,vdpnumber g1)}}}
   });

symbolic procedure groebreductionprotocol2 a;
 if !*groebprot then
  groebprotfile:=
   if not(a=1)then
    append(groebprotfile,
 {{'equal,'candidate,{'times,'candidate,a}}});

symbolic procedure groebreductionprotocolborder();
 append(groebprotfile,'!+!+!+!+!+!+!+!+!+!+!+!+!+!+!+!+.nil);

symbolic procedure groebprotsetq(a,b);
 groebprotfile:=append(groebprotfile,{{'equal,a,b}});

symbolic procedure groebprotval a;
 groebprotfile:=
  append(groebprotfile,{{'equal,'intermediateresult,a}});

symbolic procedure subset!?(s1,s2);
% Tests,if s1 is a subset of s2.
 if null s1 then t else
 if member(car s1,s2)then subset!?(cdr s1,s2)
  else nil;

symbolic procedure vevsplit vev;
% Split vev such that each exponent vector has only one 1.
 begin scalar e,vp;integer n;
  for each x in vev do
  <<n:=n+1;
   if x neq 0 then
   <<e:=append(vdpevlmon vdpone!*,nil);
    rplaca(pnth(e,n),1);
    vp:=e.vp>> >>;return vp end;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%   Calculation of an S-polynomial.
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% General strategy:
%
% groebspolynom4 calculates the traditional s-polynomial from p1,p2
%(linear combination such that the highest term vanishes).
% groebspolynom2 subtracts multiples of p2 from the s-polynomial such
% that head terms are eliminated early.

symbolic procedure groebspolynom(p1,p2);
 groebspolynom2(p1,p2);

symbolic procedure groebspolynom2(p1,p2);
 if vdpzero!? p1 then p2 else if vdpzero!? p2 then p1 else
 begin scalar cand,s,tp1,tp2,ts;
  s:=groebspolynom3(p1,p2);
  if vdpzero!? s or vdpone!? s or !*groebprot then return s;
  tp1:=vdpevlmon p1;tp2:=vdpevlmon p2;
  while not vdpzero!? s
   and(( buchvevdivides!?(tp2,(ts:=vdpevlmon s)) and(cand:=p2))
           or(buchvevdivides!?(tp1,(ts:=vdpevlmon s))
                and(cand:=p1)))
    do<<if !*vdpinteger then
        s:=% vdpsimpcont
         groebreduceonestepint(s,nil,vdplbc s,ts,cand)
       else
                          % Rational, float and modular case .
        s:=groebreduceonesteprat(s,nil,vdplbc s,ts,cand)>>;
   return s end;

symbolic procedure groebspolynom3(p,q);
 begin scalar r;r:=groebspolynom4(p,q);
  groebsavelterm r;return r end;

symbolic procedure groebspolynom4(p1,p2);
 begin scalar db1,db2,ep1,ep2,ep,r,rp1,rp2,x;
  ep1:=vdpevlmon p1;ep2:=vdpevlmon p2;
  ep:=vevlcm(ep1,ep2);
  rp1:=vdpred p1;rp2:=vdpred p2;
  gsetsugar(rp1,gsugar p1);gsetsugar(rp2,gsugar p2);
  r:=(if vdpzero!? rp1 and vdpzero!? rp2 then rp1
         else(if vdpzero!? rp1 then
          <<db2:=a2vbc 0;
           vdpprod( rp2,vdpfmon(db1:=a2vbc 1,vevdif(ep,ep2)))>>
         else if vdpzero!? rp2 then
          <<db1:=a2vbc 0;
            vdpprod(rp1,vdpfmon(db2:=a2vbc 1,
                   vevdif(ep,ep1)))>>
         else
          <<db1:=vdplbc p1;
           db2:=vdplbc p2;
           if !*vdpinteger then
           <<x:= vbcgcd(db1,db2);
            if not vbcone!? x then
            <<db1:=vbcquot(db1,x);db2:=vbcquot(db2,x)>> >>;
             vdpilcomb1(rp2,db1,vevdif(ep,ep2),
              rp1,vbcneg db2,vevdif(ep,ep1)) >>
  ));
  if !*groebprot then
   groebprotsetq('candidate,
{'difference,
  {'times,vdp2a vdpfmon(db2,vevdif(ep,ep2)),
                mkid('poly,vdpnumber p1)},
  {'times,vdp2a vdpfmon(db1,vevdif(ep,ep1)),
                mkid('poly,vdpnumber p2)}});
  return r end;

symbolic procedure groebsavelterm r;
 if !*groelterms and not vdpzero!? r then groebsaveltermbc vdplbc r;

symbolic procedure groebsaveltermbc r;
 <<r:=vbc2a r;
  if not numberp r and not constant_exprp r then
   for each p in cdr fctrf numr simp r do
   <<p:=prepf car p;
    if not member(p,glterms)then nconc(glterms,{p})>> >>;

symbolic procedure sfcont f;
% Calculate the integer content of standard form f.
 if domainp f then f else gcdf(sfcont lc f,sfcont red f);

symbolic procedure vdplmon u;vdpfmon(vdplbc u,vdplbc u);

symbolic procedure vdpmember3(p,g1,g2,g3);
% Test membership of p in one of then lists g1,g2,g3.
 vdpmember(p,g1)or vdpmember(p,g2)or vdpmember(p,g3);

symbolic procedure groebabortid(base,abort1);
% Test whether one of the elements in abort1 is
% member of the ideal described by base. Definite
% test here.
 if null abort1 then nil else
  vdpzero!?(groebnormalform(car abort1,base,'list))
   or groebabortid(base,cdr abort1);

endmodule;;end;
