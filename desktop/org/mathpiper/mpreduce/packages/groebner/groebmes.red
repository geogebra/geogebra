module groebmes;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%  Trace messages for the algorithms .
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

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


symbolic procedure groebpairprint(p);
<< groebmessff(" pair(",cadr p,nil);
 groebmessff(",",caddr p,nil);
 prin2 "), ";prin2 " lcm = ";print car p >>;

symbolic procedure groetimeprint;
<< prin2 " >> accum. cpu time : ";
 prin2(time() - groetime!*);prin2t " ms " >>;

symbolic procedure groebmessff(m1,f,m2);
<< prin2 m1;prin2 vdpnumber f;
 if !*gsugar then << prin2 " / ";prin2 gsugar f >>;
 if m2 then prin2t m2 >>;

symbolic procedure groebmess1(g,d);
if !*trgroeb then << g := g;d := d;
 prin2 " variables : ";print vdpvars!*;
 printbl();prin2t " Start of ITERATION ";terpri() >>;

symbolic procedure groebmess2 f;
if !*trgroeb then << terpri();
 groebmessff(" polynomial ",f," eliminated ");
 groetimeprint() >>;

symbolic procedure groebmess2a(f,cf,fn);
if !*trgroeb then << terpri();
 groebmessff("polynomial ",f,nil);
 groebmessff(" elim . with cofactor ",cf," to ");
 vdpprint fn;terpri();groetimeprint() >>;

symbolic procedure groebmess3(p,s);
if !*trgroebs then << prin2 " S - polynomial from ";
 groebpairprint p;vdpprint s;terpri();
 groetimeprint();terprit 3 >>;

symbolic procedure groebmess4(p,d);
<< hcount!* := hcount!* + 1;
 hzerocount!* := hzerocount!* + 1;
 if !*trgroeb then << terpri();printbl();
  groebmessff(" reduction(",cadr p,nil);
  groebmessff(",",caddr p,nil);
  prin2 ")leads to 0;";
  prin2 n;
  prin2 if n = 1 then " pair" else " pairs" >> where n = length d;
  prin2t " left ";
  printbl();groetimeprint() >>;

symbolic procedure groebmess41 p;
<< hcount!* := hcount!* + 1;
 hzerocount!* := hzerocount!* + 1;
 if !*trgroeb then << terpri();printbl();
  groebmessff(" polynomial(",p,nil);
  prin2 ")reduced to 0;";
  terpri();printbl();groetimeprint() >> >>;

symbolic procedure groebmess5(p,h);
if car p then
<< hcount!* := hcount!* + 1;
 if !*trgroeb then << terpri();prin2  " H - polynomial ";
  prin2 pcount!*;prin2 " ev : ";prin2 vdpevlmon h;
  groebmessff(" from pair(",cadr p,nil);
  groebmessff(",",caddr p,")");
  vdpprint h;terpri();groetimeprint() >> >>
 else
 if !*trgroeb then << prin2t " from actual problem input : ";
  vdpprint h;groetimeprint() >>;

symbolic procedure groebmess50 g;
if !*trgroeb1 then << prin2 " list of active polynomials : ";
 for each d1 in g do
  << prin2 vdpgetprop(d1,'number);prin2 " " >>;terprit 2 >>;

symbolic procedure groebmess51 d;
if !*trgroeb1 then <<
 prin2t " Candidates for pairs in this step : ";
 for each d1 in d do groebpairprint d1;terprit 2 >>;

symbolic procedure groebmess52 d;
if !*trgroeb1 then <<
 prin2t " Actual new pairs from this step : ";
 for each d1 in d do groebpairprint d1;terprit 2 >>;

symbolic procedure groebmess7 h;
if !*trgroebs then
<< prin2t " Testing factorization for ";vdpprint h >>;

symbolic procedure groebmess8(g,d);
if !*trgroeb1 then <<
 g := g;prin2t " actual pairs : ";
 if null d then prin2t " null "
  else for each d1 in d do groebpairprint d1;
 groetimeprint() >>
 else if !*trgroeb then <<
  prin2 n;prin2t if n = 1 then " pair" else " pairs " >>
   where n = length d;

symbolic procedure groebmess13(g,problems);
if !*trgroeb or !*trgroebr then <<
 if g then << basecount!* := basecount!* + 1;
 printbl();printbl();
 prin2  " end of iteration ";
 for each f in reverse factorlevel!* do
 << prin2 f;prin2 " . " >>;
 prin2 ";basis ";prin2 basecount!*;prin2t " : ";
 prin2 " { ";for each g1 in g do vdpprin3t g1;prin2t " } ";
 printbl();printbl();groetimeprint() >>
 else
 << printbl();prin2  " end of iteration branch ";
  for each f in reverse factorlevel!* do
  << prin2 f;prin2 " . " >>;
  prin2t "  ";printbl();groetimeprint() >>;
  if problems and !*trgroeb then
  << groetimeprint();terpri();printbl();
   prin2 " number of partial problems still to be solved : ";
   prin2t length problems;terpri();
   prin2 " preparing  next problem ";
   if car car problems = 'file then prin2 cdr car problems
    else if cadddr car problems then
     vdpprint car cadddr car problems;terpri() >> >>;

symbolic procedure groebmess14(h,hf);
if !*trgroeb then <<
 prin2 " ******************* factorization of polynomial ";
(if x then prin2t x else terpri())where x = vdpnumber h;
 prin2t " factors : ";
 for each g in hf do vdpprint car g;groetimeprint() >>;

symbolic procedure  groebmess15 f;
if !*trgroeb then
<< prin2t " ***** monomial factor reduced : ";
 vdpprint vdpfmon(a2vbc 1,f)>>;

symbolic procedure groebmess19(p,restr,u);
if !*trgroeb then <<
 u := u;restr := restr;printbl();
 prin2  " calculation branch ";
 for each f in reverse factorlevel!* do
 << prin2 f;prin2 " . " >>;
 prin2t " cancelled because ";vdpprint p;
 prin2t " is member of an actual abort condition ";
 printbl();printbl() >>;

symbolic procedure groebmess19a(p,u);
if !*trgroeb then << u := u;printbl();
 prin2  " during branch preparation ";
 for each f in reverse u do << prin2 f;prin2 "." >>;
 prin2t " cancelled because ";vdpprint p;
 prin2t " was found in the ideal branch ";printbl() >>;

symbolic procedure groebmess20 p;
if !*trgroeb then <<
 terpri();prin2 " secondary reduction starting with ";vdpprint p >>;

symbolic procedure groebmess21(p1,p2);
if !*trgroeb then <<
 prin2 " polynomial ";prin2 vdpnumber p1;
 prin2 " replaced during secondary reduction by ";
 vdpprint p2 >>;

symbolic procedure groebmess22(factl,abort1,abort2);
if null factl then nil
 else if !*trgroeb then
  begin integer n;
   prin2t " BRANCHING after factorization point ";
   n := 0;for each x in reverse factl do
   << n := n+1;prin2 " branch ";
    for each f in reverse factorlevel!* do << prin2 f;prin2 " . " >>;
    prin2t n;for each y in car x do vdpprint y;
    prin2t " simple IGNORE restrictions for this branch : ";
    for each y in abort1 do vdpprint y;
    for each y in cadr x do vdpprint y;
    if abort2 or caddr x then
    << prin2t " set type IGNORE  restrictions for this branch : ";
     for each y in abort2 do vdpprintset y;
     for each y in caddr x do vdpprintset y >>;
    printbl() >> end;

symbolic procedure  groebmess23(g0,rest1,rest2);
if !*trgroeb then
 if null factorlevel!* then
  prin2t " ** starting calculation ****************************** "
  else << prin2 "** resuming calculation for branch ";
 for each f in reverse factorlevel!* do << prin2 f;prin2 "." >>;
 terpri();if rest1 or rest2 then
 << prin2t " -------IGNORE restrictions for this branch : ";
  g0 := g0;for each x in rest1 do vdpprint x;
  for each x in rest2 do vdpprintset x >> >>;

symbolic procedure groebmess24(h,problems1,restr);
 %  if !*trgroeb then
<< prin2t " ********** polynomial affected by branch restriction : ";
 vdpprint h;if restr then prin2t " under current restrictions ";
 for each x in restr do vdpprint x;
 if null problems1 then prin2t "        CANCELLED "
 else << prin2t " partitioned into ";vdpprintset car problems1 >> >>;

symbolic procedure groebmess25(h,abort2);
<< prin2t " reduction of set type cancel conditions by ";
 vdpprint h;prin2t " remaining : ";
 for each x in abort2 do vdpprintset x >>;

symbolic procedure groebmess26(f1,f2);
if !*trgroebs and not vdpequal(f1,f2)then
 << terpri();prin2t  " during final reduction ";
  vdpprint f1;prin2t " reduced to ";vdpprint f2;terpri() >>;

symbolic procedure groebmess27 r;
if !*trgroeb then << terpri();
 prin2t " factor ignored(considered already): ";vdpprint r >>;

symbolic procedure groebmess27a(h,r);
if !*trgroeb then
<< terpri();vdpprint h;
 prin2t "     reduced to zero by factor ";vdpprint r >>;

symbolic procedure groebmess28 r;
if !*trgroeb then
<< writepri(" interim content reduction : ",'first);
 writepri(mkquote prepsq r,'last)>>;

symbolic procedure groebmess29 omega;
if !*trgroeb then
<< terpri();prin2 " actual weight vector : [ ";
 for each x in omega do << prin2 " ";prin2 x >>;prin2 " ] ";
 terpri();terpri() >>;

symbolic procedure groebmess30 gomegaplus;
if !*trgroeb and gomegaplus then
<< terpri();prin2 " new head term(or full)basis ";terpri();
 for each x in gomegaplus do << vdpprint x;terpri() >> >>;

symbolic procedure groebmess31 gg;
if !*trgroeb then << prin2 " full basis ";terpri();
 for each x in gg do << vdpprint x;terpri();terpri() >> >>;

symbolic procedure groebmess32 g;
if !*trgroeb then << terpri();
 prin2 " ***** start of iteation with ";terpri();
 for each x in g do vdpprint x;
 prin2 " **************************** ";terpri() >>;

symbolic procedure groebmess33 g;
if !*trgroeb then
<< terpri();prin2 " ***** resulting system ***** ";terpri();
 for each x in g do vdpprint x;
 prin2 " **************************** ";terpri() >>;

symbolic procedure groebmess34 mx;
if !*trgroeb then
<< terpri();prin2 " sum of weight vector ";print mx;terpri() >>;

symbolic procedure groebmess35 omega;
if !*trgroeb then
<< terpri();prin2 " next weight vector ";print omega;terpri() >>;

symbolic procedure groebmess36 tt;
if !*trgroeb then
<< terpri();prin2 " new weight : ";print tt >>;

symbolic procedure groebmess37 s;
if !*trgroeb then
<< if not s then prin2 " NOT ";prin2 " taking initials ";
 terpri();terpri() >>;

symbolic procedure printbl();printb(linelength nil #- 2);

symbolic procedure printb n;<< for i := 1 : n do prin2 "-";terpri() >>;

symbolic procedure vdpprintset l;
if l then << prin2 " { ";vdpprin2 car l;
 for each x in cdr l do << prin2 ";";vdpprin2 x >>;
 prin2t " } " >>;

symbolic procedure vdpprin2l u;
<< prin2 "(";vdpprin2 car u;
 for each x in cdr u do << prin2 ",";vdpprin2 x >>;rin2 ")" >>;

endmodule;;end;
