module groeweak;% Weak test for f ~ 0 modulo g .

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


switch groebweak;

symbolic procedure groebweakzerotest(f,g,type);
% Test f == 0 modulo g with ON MODULAR .
begin scalar f1,c,vev,divisor,oldmode,a;
 if vdpzero!? f then return f;
 if current!-modulus = 1 then setmod list 2097143;
 oldmode:=setdmode(' modular,t);
 f:=groebvdp2mod f;
 f1:=vdpzero();a:=vbcfi 1;
 while not vdpzero!? f and vdpzero!? f1 do
 begin
  vev:=vdpevlmon f;c:=vdpLbc f;
  if type = 'sort then while g and vevcompless!?(vev,vdpevlmon(car g))
   do g:=cdr g;
  divisor:=groebsearchinlist(vev,g);
  if divisor and !*trgroebs then
  <<prin2 "//m-";prin2 vdpnumber divisor>>;
  if divisor then
   if vdplength divisor = 1 then f:=vdpcancelmvev(f,vdpevlmon divisor)
    else <<divisor:=groebvdp2mod divisor;
   if divisor then f:=groebreduceonesteprat(f,nil,c,vev,divisor)
    else f1:=f>>
   else f1:=f end;
 if not vdpzero!? f1 and !*trgroebs then
 <<prin2t " - nonzero result in modular reduction:";vdpprint f1>>;
 setdmode(' modular,nil);
 if oldmode then setdmode(get(oldmode,' dname), t);
  return vdpzero!? f1 end;

symbolic procedure groebweaktestbranch!=1(poly,g,d);
% Test gb(g)== { 1 } in modular style .
 groebweakbasistest({ poly },g,d);

symbolic procedure groebweakbasistest(g0,g,d);
begin scalar oldmode,d,d1,d2,p,p1,s,h;
 scalar !*vdpinteger;  % Switch to field type calclulation .
 return nil;
 if not !*groebfac then return nil;
 if current!-modulus= 1 then setmod { 2097143 };
 if !*trgroeb then
  prin2t "---------------- modular test of branch ------";
 oldmode:=setdmode(' modular,t);
 g0:=for each p in g0 collect groebvdp2mod p;
 g:=for each p in g collect groebvdp2mod p;
 d:=for each p in d collect { car p,
             groebvdp2mod cadr p,groebvdp2mod caddr p };
 while d or g0 do
 begin if g0 then
 <<        % Take next poly from input .
  h:=car g0;g0:=cdr g0;p:={ nil,h,h }>>
  else
 <<        % Take next poly from pairs .
  p:=car d;d:=delete(p,d);
  s:=groebspolynom(cadr p,caddr p);
  h:=groebsimpcontnormalform groebnormalform(s,g,' sort);
  if vdpzero!? h then !*trgroeb and groebmess4(p,d)>>;
  if vdpzero!? h then
  <<pairsdone!*:=( vdpnumber cadr p . vdpnumber caddr p). pairsdone!*;
   go to bott>>;
  if vevzero!? vdpevlmon h then % Base 1 found .
  <<       !*trgroeb and groebmess5(p,h);goto stop>>;
  s:=nil;
  h:=vdpenumerate h;!*trgroeb and groebmess5(p,h);
                              % Construct new critical pairs .
  d1:=nil;
  for each f in g do
  <<d1:=groebcplistsortin({ tt(f,h),f,h },d1);
   if tt(f,h)= vdpevlmon f then
   <<g:=delete(f,g);
                                !*trgroeb and groebmess2 f>>>>;
          !*trgroeb and groebmess51 d1;
  d2:=nil;
  while d1 do
  <<d1:=groebinvokecritf d1;
   p1:=car d1;d1:=cdr d1;
   d2:=groebinvokecritbuch4(p1,d2);d1:=groebinvokecritm(p1,d1)>>;
  d:=groebinvokecritb(h,d);d:=groebcplistmerge(d,d2);g:=h . g;
  go to bott;
stop: d:=g:=g0:=nil;
bott: end;
 if !*trgroeb and null g then
          prin2t "**** modular test detects empty branch!";
 if !*trgroeb then
          prin2t "------ end of  modular test of branch ------";
 setdmode(' modular,nil);
 if oldmode then setdmode(get(oldmode,' dname), t);
  return  null g end;

fluid '(!*localtest);

symbolic procedure groebfasttest(g0,g,d,g99);
 if !*localtest then
 <<!*localtest:=nil;g99:=nil;groebweakbasistest(g0,g,d)>>
  else if !*groebweak and g and vdpunivariate!? car g
   then groebweakbasistest(g0,g,d);

symbolic procedure groebvdp2mod f;
% Convert a vdp in modular form;in case of headterm loss,nil is returned .
begin scalar u,c,mf;
 u:=vdpgetprop(f,' modimage);
 if u then return if u = ' nasty then nil else u;
 mf:=vdpresimp f;
 if !*gsugar then vdpputprop(mf,' sugar,vdpgetprop(f,' sugar));
 c:=errorset!*( { ' vbcinv,mkquote vdplbc mf },nil);
 if not pairp c then
 <<prin2t "************** nasty module(loss of headterm) ****";
  print f;print u;vdpprint f;vdpprint u;
  vdpputprop(f,' modimage,' nasty);return nil>>;
 u:=vdpvbcprod(mf,car c);
 vdpputprop(u,' number,vdpgetprop(f,' number));
 vdpputprop(f,' modimage,u);
 if !*gsugar then vdpputprop(u,' sugar,vdpgetprop(f,' sugar));
 return u end;


symbolic procedure groebmodeval(f,break);
% Evaluate LISP form r with REDUCE modular domain .
begin scalar oldmode,a,!*vdpinteger,groebmodular!*;
 groebmodular!*:=t;break:=nil;
 if current!-modulus = 1 then setmod list 2097143;
 oldmode:=setdmode(' modular,t);
 a:=errorset!*(f,t);
 setdmode(' modular,nil);
 if oldmode then setdmode(get(oldmode,' dname), t);
 return if atom a then nil else car a end;

endmodule;;end;
