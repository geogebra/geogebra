module meijerg;  % Meijer's G-function.

% Author : Victor Adamchik, Byelorussian University Minsk, Byelorussia.

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


% Major modifications by: Winfried Neun, ZIB Berlin.

symbolic smacro procedure fehler();
        rerror('specialf,140,"Wrong arguments to operator MeijerG");

symbolic procedure simpMeijerG(U);

begin scalar list1,list2,list1a,list2a;

if pairp u then list1 :=car u else fehler();
if pairp cdr u then list2 := cadr u else fehler();
if not pairp cddr u then  fehler();

if not eqcar(list1,'list) then fehler();
if not eqcar(list2,'list) then fehler();

list1a := for each x in cdadr list1 collect simp reval x;
list2a := for each x in cdadr list2 collect simp reval x;

list1 := list1a . for each x in cddr list1 collect simp reval x;
list2 := list2a . for each x in cddr list2 collect simp reval x;

list1 :=gfmsq(list1,list2,simp caddr u);

if list1 = 'fail then return simp 'fail else
        list1 := prepsq list1;

if eqcar(list1,'MeijerG) then return list1 else return
                simp list1;
                        end;

put('MeijerG,'simpfn,'simpMeijerG);

if not getd('simpmeijerg) then
        flag('(f6 f8 f9 f10 f11 f12 f13 f14 f26 f27 f28 f29 f30
        f31 f32 f33 f34 f35 f36 f37 f38 f39),'internalfunction);

switch tracespecfns;

symbolic procedure GFMsq(a,b,z);
 begin scalar v1,v2; integer m,n,p,q,aa,bb;
  v1:=redpar(car b,cdr a);
  v2:=redpar(cdr b,car a);
  aa:= (cadr v2 . cadr v1);
  bb:= (car v1 . car v2);
  a := append (car aa,cdr aa);
  b := append (car bb,cdr bb);
  m:=length(car v1); n:=length(cadr v2);
  q:=m + length(car v2); p:=n + length(cadr v1);   %WN
  if !*tracespecfns then
        << prin2 list( "MeijerG<",m,n,p,q,">",a,"|",b,"|",Z,"|aa=",aa,
                       "|bb=",bb);
        terpri()>>;
  if p=0 and q=0 then return
        << rerror('specialf,141,"DIVERGENT INTEGRAL");
          'FAIL
        >>;
  if greaterp(p,q) then return GFMinvers(aa,bb,z) else
  if greaterp(q,3) or greaterp(p,3) then return
     simpGtoH(aa,bb,z) else
  if q=3 and p=1 then go to q3 else
  if q=2 and (p=0 or p=1) then go to q2 else
  if q=1 then go to q1 else
          return simpGtoH(aa,bb,z);

q1:if p=0 and n=0 and m=1 then return
      multsq(expdeg(z,car b),expdeg(simp!* 'e,negsq z)) else

   if p=1 and n=0 and m=1 and null caar b and car a = '(1 . 1)
        then return gfmexit(aa,bb,z) else
        % change in order to make defint(cos(x) *sin(x)/x) correct. WN

   if p=1 and n=0 and m=1 then return % WN
       multsq (heavisidesq diff1sq('(1 . 1),z),
               quotsq(multsq(expdeg(z,car b),
           expdeg(diff1sq('(1 . 1),z),
                  diff1sq(car a,addsq('(1 . 1),car b)))),
            gamsq(diff1sq(car a,car b))))
      else
   if p=1 and n=1 and m=0 then return  %WN
           multsq(heavisidesq diff1sq(z,'(1 . 1)),
            quotsq(multsq(expdeg(z,car b),expdeg(diff1sq
            (z,'(1 . 1)),diff1sq(car a,addsq('(1 . 1),car b)))),
            gamsq(diff1sq(car a,car b))))
   else
   if p=1 and n=1 and m=1 then return
      multsq(gamsq(diff1sq('(1 . 1),diff1sq(car a,car b))),
             multsq(expdeg(z,car b),expdeg(addsq('(1 . 1),z),
                      diff1sq(car a,addsq('(1 . 1),car b)))))
            else return  rerror('specialf,142,
                        "***** parameter error in G-function");
q2:  if p=2 then  return  simpGtoH(aa,bb,z) else
     if p=1 then go to q2p1 else
     if p=0 and m=1 then return f6(car b,cadr b,z) else
     if p=0 and m=2 then return f8(car b,cadr b,z) else
               return  rerror('specialf,143,
                        "***** parameter error in G-function");

q2p1: if m=1 and n=0 then return q2p1m1n0(a,b,z) else
      if m=2 and n=0 then return q2p1m2n0(a,b,z) else
      if m=2 and n=1 then return q2p1m2n1(a,b,z) else
                      return simpGtoH(aa,bb,z);

q3:   if p=1 then go to q3p1 else
                      return simpGtoH(aa,bb,z);

q3p1: if m=1 and n=1 then return q3p1m1n1(a,b,z) else
      if m=2 and n=0 then return q3p1m2n0(a,b,z) else
      if m=2 and n=1 then return q3p1m2n1(a,b,z) else
      if m=3 and n=0 then return q3p1m3n0(a,b,z) else
      if m=3 and n=1 then return q3p1m3n1(a,b,z) else
                      return simpGtoH(aa,bb,z);
end;

symbolic procedure GFMinvers(a,b,z);
  GFMsq( (pdifflist(('1 . 1),car b) . pdifflist('(1 . 1),cdr b)),
         (pdifflist('(1 . 1),car a) . pdifflist('(1 . 1),cdr a)),
         invsq z);

symbolic procedure f6(a,b,z);
  multsq(expdeg(z,multsq('(1 . 2),addsq(a,b))),besssq(diff1sq(a,b),
  multsq('(2 . 1),simpx1(prepsq z,1,2))));

symbolic procedure f8(a,b,z);
  multsq('(2 . 1),multsq(expdeg(z,multsq('(1 . 2),addsq(a,b))),
         macdsq(diff1sq (a,b),multsq('(2 . 1),simpx1(prepsq z,1,2)))));

%***********************************************************************
%*     Representation G-function through hypergeometric functions      *
%***********************************************************************

symbolic procedure simpGtoH(a,b,z);
   %a=((a1,,,an).(an+1,,,ap)).
   %b=((b1,,,bm).(bm+1,,,bq)).
   %z -- argument.
   %value is the generalized hypergeometric function.
   if length(car b) + length(cdr b) >= length(car a) + length(cdr a)
     then fromGtoH(a,b,z)
   else
      fromGtoH(
        cons(pdifflist('(1 . 1),car b),pdifflist('(1 . 1),cdr b)),
        cons(pdifflist('(1 . 1),car a),pdifflist('(1 . 1),cdr a)),
        invsq z);

%symbolic procedure fromGtoH(a,b,z);
   %a=((a1,,,an).(an+1,,,ap)).
   %b=((b1,,,bm).(bm+1,,,bq)).
   %z -- argument.
   %value is the generalized hypergeometric function.
%   if null car b then gfmexit(a,b,z)
%     else
%   if not null a and listfooltwo(difflist(car b,'(-1 . 1)),car a)
%      then 'FAIL
%     else
% dont understand this W.N.
% but reopened nevertheless
%  if length(car b) > length(car a) then
%     fromGtoH(
%     (pdifflist('(1 . 1),car b ) . pdifflist('(1 . 1),cdr b )),
%     (pdifflist('(1 . 1),car a ) . pdifflist('(1 . 1),cdr a )),
%     invsq(z))
%    else
%  if listfool(car b) then GFMlogcase(a,b,z)
%                    else allsimplpoles(car b,a,b,z);

symbolic procedure fromGtoH(a,b,z);
  %a=((a1,,,an).(an+1,,,ap)).
  %b=((b1,,,bm).(bm+1,,,bq)).
  %z -- argument.
  %value is the generalized hypergeometric function.
  if null car b then gfmexit(a,b,z)
    else
  if not null a and listfooltwo(difflist(car b,'(-1 . 1)),car a)
     then 'FAIL
    else
 if listfool(car b) then GFMlogcase(a,b,z)

 else
  if length car a + length cdr a <= length car b + length cdr b then
      allsimplpoles(car b,a,b,z) else allsimplpoles(car a,a,b,z);

symbolic procedure GFMexit(a,b,z);
 begin scalar mnpq,aa,bb;
  if (length car a + length cdr a) > (length car b + length cdr b)
                then return GFMexitinvers(a,b,z);
   mnpq := 'lst . list(length car b,length car a,
                       length car a + length cdr a,
                       length car b + length cdr b);
  aa:= 'lst . append (listprepsq car a, listprepsq cdr a);
  bb:= 'lst . append (listprepsq car b, listprepsq cdr b);
  return mksqnew('gfm .
        list(mnpq,aa,bb,prepsq z));
  end;

symbolic procedure GFMexitinvers(a,b,z);
  GFMexit((pdifflist('(1 . 1),car b) . pdifflist('(1 . 1),cdr b)),
          (pdifflist('(1 . 1),car a) . pdifflist('(1 . 1),cdr a)),
           invsq z) ;

symbolic procedure allsimplpoles(v,a,b,z);
  if null v then '(nil . 1) else
     addsq(infinitysimplpoles(a,(car redpar(car b,list(car v)) . cdr b)
                        ,car v,z),
            allsimplpoles(cdr v,a,b,z));

symbolic procedure infinitysimplpoles(a,b,v,z);
 begin scalar coefgam;
   coefgam:=
     quotsq(
       multsq(
         multgamma(difflist(car b,v)),
         if null a or null car a then '(1 . 1) else
           multgamma(pdifflist(addsq('(1 . 1),v), car a))),
       multsq(
         if null cdr b then '(1 . 1) else
           multgamma(pdifflist(addsq('(1 . 1),v),cdr b)),
         if null a or null cdr a then '(1 . 1) else
           multgamma(difflist(cdr a,v))));
  return multsq(multsq(coefgam,expdeg(z,v)),
            GHFsq(list(length(car a) + length(cdr a),
                       length(car b) + length(cdr b)),
             if null a then nil else
             if null car a then pdifflist(addsq('(1 . 1),v),cdr a)
               else
             append(pdifflist(addsq('(1 . 1),v),car a),
                    pdifflist(addsq('(1 . 1),v),cdr a)),
             if null cdr b then
                    pdifflist(addsq('(1 . 1),v),car b)
               else
             append(pdifflist(addsq('(1 . 1),v),car b),
                    pdifflist(addsq('(1 . 1),v),cdr b)),
             multsq(z,
             exptsq('(-1 . 1),1+length(cdr a)-length(car b)))));
end;

%***********************************************************************
%*            PARTICULAR CASES FOR G-FUNCTION, Q=2                     *
%***********************************************************************

symbolic procedure q2p1m1n0(a,b,z);
  begin scalar v;
  v:=addend(a,b,'(1 . 2));
  if null car addsq(cadr v,caddr v) then
                 return f7(car v,cadr v,z) else
                 return simpGtoH((nil . a),redpar1(b,1),z);
  end;

symbolic procedure f7(a,b,z);
  multsq(quotsq(simpfunc('cos,multsq(b,simp!* 'pi)),simpx1('pi,1,2)),
  multsq(expdeg(z,a),multsq(expdeg(simp!* 'e,multsq(z,'(1 . 2))),
  bessmsq(b,multsq(z,'(1 . 2))))));

  symbolic procedure q2p1m2n0(a,b,z);
  begin scalar v;
  v:=addend(a,b,'(1 . 2));
    if null car addsq(cadr v,caddr v) then
                return f9(car v,cadr v,z) else
                return f11(car a,car b,cadr b,z);
 end;

symbolic procedure f9(a,b,z);
   multsq(quotsq(expdeg(z,a),simpx1('pi,1,2)),
     multsq(expdeg(simp!* 'e,multsq(
       '(1 . 2),negsq z)),macdsq(b,multsq(z,'(1 . 2)))));

 symbolic procedure f11(a,b,c,z);
 multsq(expdeg(z,b),multsq(expdeg(simp!* 'e,negsq z),
        tricomisq(diff1sq(a,c),addsq('(1 . 1),diff1sq(b,c)),z)));

symbolic procedure q2p1m2n1(a,b,z);
 begin scalar v;
 v:=addend(a,b,'(1 . 2));
 if null car addsq(cadr v,caddr v) and
    ((equal(cdadr v,2) and not numberp(cadar v)) or
    not equal(cdadr v,2)) then
               return f10(car v,cadr v,z) else
               return simpGtoH((a . nil),(b . nil),z);
 end;

symbolic procedure f10(a,b,z);
  multsq(quotsq(simpx1('pi,1,2),simpfunc('cos,multsq(simp!* 'pi,b))),
  multsq(expdeg(z,a),multsq(expdeg(simp!* 'e,multsq('(1 . 2),z)),
  macdsq(b,multsq('(1 . 2),z)))));

%***********************************************************************
%*                PARTICULAR CASES FOR G-FUNCTION, Q=3                 *
%***********************************************************************

symbolic procedure q3p1m2n1(a,b,z);
  begin scalar v,v1;
    if equal(diff1sq(car a,caddr b),'(1 . 2)) then
    if equal(car a,car b) and
       ((equal(cdr diff1sq(cadr b,caddr b),2) and
       not numberp(car diff1sq(cadr b,caddr b))) or
       not equal(cdr diff1sq(cadr b,caddr b),2))
                         then return f34(caddr b,cadr b,z) else
    if equal(car a,cadr b) and
       ((equal(cdr diff1sq(car b,caddr b),2) and
       not numberp(car diff1sq(car b,caddr b))) or
       not equal(cdr diff1sq(car b,caddr b),2))
                         then return f34(caddr b,car b,z) else goto m;
    if equal(diff1sq(car a,car b),'(1 . 2)) and equal(car a,cadr b) then
                              return f35(car b,caddr b,z) else
    if equal(diff1sq(car a,cadr b),'(1 . 2)) and equal(car a,car b) then
                              return f35(cadr b,caddr b,z) else
             return simpGtoH((a . nil),redpar1(b,2),z);
 m: v:=addend(a,b,'(1 . 2));   v1:=cdr v;
    if null caar v1 and null car addsq(cadr v1,caddr v1) then
                              return f32( car v,cadr v1,z) else
    if null caadr v1 and null car addsq(car v1,caddr v1) then
                              return f32(car v,car v1,z) else
    if null caaddr v1 and null car addsq(car v1,cadr v1) and
       ((not equal(cdar v1,1) and not equal(cdar v1,2)) or
         not numberp(caar v1))
                           then  return f33(car v,car v1,z);
             return simpGtoH((a . nil),redpar1(b,2),z);
  end;

symbolic procedure f34(a,b,z);
  multsq(quotsq(simp!* 'pi,
                simpfunc('cos,multsq(simp!* 'pi,diff1sq(b,a)))),
  multsq(expdeg(z,multsq('(1 . 2),addsq(a,b))),
    diff1sq(bessmsq(diff1sq(b,a),
       multsq('(2 . 1),simpx1(prepsq z,1,2))),struvelsq(diff1sq(a,b),
              multsq('(2 . 1),simpx1(prepsq z,1,2))))));

symbolic procedure f35(a,b,z);
  multsq(simp!* 'pi,
  multsq(expdeg(z,multsq('(1 . 2),addsq(a,b))),
         diff1sq(bessmsq(diff1sq(a,b),
  multsq('(2 . 1),simpx1(prepsq z,1,2))),struvelsq(diff1sq(a,b),
         multsq('(2 . 1),simpx1(prepsq z,1,2))))));

symbolic procedure f33(c,a,z);
  multsq(quotsq(simpx1('pi,3,2),simpfunc('sin,multsq('(2 . 1),multsq(a,
  simp!* 'pi)))),multsq(expdeg(z,c),
      diff1sq(multsq(bessmsq(negsq a,simpx1
  (prepsq z,1,2)),bessmsq(negsq a,simpx1(prepsq z,1,2))),
  multsq(bessmsq(a,simpx1(prepsq z,1,2)),
         bessmsq(a,simpx1(prepsq z,1,2))))));

symbolic procedure f32(c,a,z);
  multsq(multsq('(2 . 1),simpx1('pi,1,2)),multsq(expdeg(z,c),multsq(
  bessmsq(a,simpx1(prepsq z,1,2)),macdsq(a,simpx1(prepsq z,1,2)))));

symbolic procedure q3p1m2n0(a,b,z);
  begin scalar v,v1;
   if equal(car a,caddr b) then
   if equal(diff1sq(car b,car a),'(1 . 2))
     then return f29(car b,cadr b,z)
    else if equal(diff1sq(cadr b,car a),'(1 . 2)) then
                                        return f29(cadr b,car b,z);
   v:=addend(a,b,'(1 . 2));  v1:=cdr v;
   if null caar v1 and null car addsq(cadr v1,caddr v1) then
                                        return f31(car v,cadr v1,z) else
   if null caadr v1 and null car addsq(car v1,caddr v1) then
                                        return f31(car v,car v1,z) else
   if null caaddr v1 and null car addsq(cadr v1,car v1) and
       ((equal(cdar v1,1) and not numberp(caar v1)) or
       not equal(cdar v1,1))
                  then return f30(car v,car v1,z);
             return simpGtoH((nil . a),redpar1(b,2),z);
  end;

symbolic procedure f29(a,b,z);
  multsq(expdeg(z,multsq('(1 . 2),addsq(a,b))),neumsq(diff1sq(b,a),
         multsq('(2 . 1),simpx1(prepsq z,1,2))));

symbolic procedure f30(c,a,z);
  multsq(quotsq(simpx1('pi,1,2),multsq('(2 . 1),simpfunc('sin,multsq(a,
  simp!* 'pi)))),multsq(expdeg(z,c),diff1sq(multsq(besssq(negsq a,simpx1
  (prepsq z,1,2)),besssq(negsq a,simpx1(prepsq z,1,2))),multsq(besssq(a,
  simpx1(prepsq z,1,2)),besssq(a,simpx1(prepsq z,1,2))))));

symbolic procedure f31(c,a,z);
  multsq(negsq(simpx1('pi,1,2)),multsq(expdeg(z,c),multsq(
  besssq(a,simpx1(prepsq z,1,2)),neumsq(a,simpx1(prepsq z,1,2)))));

symbolic procedure q3p1m1n1(a,b,z);
  begin scalar v,v1;
  if equal(car a,car b) then
  if equal(diff1sq(car a,caddr b),'(1 . 2))
    then return f28(car a,cadr b,z)
   else if equal(diff1sq(car a,cadr b),'(1 . 2))
    then return f28(car a,caddr b,z);
  v:=addend(a,b,'(1 . 2));  v1:=cdr v;
    if null caar v1 and null car addsq(cadr v1,caddr v1) then
                             return f26(car v,cadr v1,z) else
    if (null caadr v1 or null caaddr v1)
      and (null car addsq(car v1,cadr v1)
    or null car addsq(car v1,caddr v1)) then return f27(car v,car v1,z);
                  return simpGtoH((a . nil),redpar1(b,1),z);
  end;

symbolic procedure f26(c,a,z);
  multsq(simpx1('pi,1,2),multsq(expdeg(z,c),
         multsq(besssq(a,simpx1(prepsq z,1,2)),
                besssq(negsq a,simpx1(prepsq z,1,2)))));

  symbolic procedure f27(c,a,z);
  multsq(simpx1('pi,1,2),multsq(expdeg(z,c),multsq(
  besssq(a,simpx1(prepsq z,1,2)),besssq(a,simpx1(prepsq z,1,2)))));

symbolic procedure f28(a,b,z);
 multsq(expdeg(z,multsq('(1 . 2),diff1sq(addsq(a,b),'(1 . 2)))),
 struvehsq(diff1sq(a,addsq(b,'(1 . 2))),multsq('(2 . 1),
                        simpx1(prepsq z,1,2))));

symbolic procedure q3p1m3n0(a,b,z);
  begin scalar v,v1;
  v:=addend(a,b,'(1 . 2));  v1:=cdr v;
    if (null car(addsq(car v1,cadr v1)) and null caaddr v1) or
       (null car(addsq(car v1,caddr v1)) and null caadr v1) then
                       return f36(car v,car v1,z) else
    if null car(addsq(cadr v1,caddr v1)) and null caar v1 then
                       return f36(car v,cadr v1,z);
                   return simpGtoH((nil . a),(b . nil),z);
  end;

symbolic procedure f36(a,b,z);
  multsq(quotsq('(2 . 1),simpx1('pi,1,2)),multsq(expdeg(z,a),multsq(
  macdsq(b,simpx1(prepsq z,1,2)),macdsq(b,simpx1(prepsq z,1,2)))));

symbolic procedure q3p1m3n1(a,b,z);
    if equal(car a,car b) and null car(addsq(cadr b,caddr b)) then
                            f38(car a,cadr b,z) else
    if (equal(car a,cadr b) and null car(addsq(car b,caddr b))) or
       (equal(car a,caddr b) and null car(addsq(car b,cadr b))) then
                            f38(car a,car b,z) else
    if equal(diff1sq(car a,caddr b),'(1 . 2)) and
       null numr(addsq(addsq(car b,cadr b),
                      multf(-2,caaddr b) ./ cdaddr b))
                       then f39(caddr b,car b,z) else
    if equal(diff1sq(car a,cadr b),'(1 . 2)) and
     null numr(addsq(addsq(car b,caddr b),multf(-2,caadr b) ./ cdadr b))
                       then f39(cadr b,car b,z) else
    if equal(diff1sq(car a,car b),'(1 . 2)) and
      null numr(addsq(addsq(cadr b,caddr b),multf(-2,caar b) ./ cdar b))
                       then f39(car b,cadr b,z) else
                       simpGtoH((a . nil),(b . nil),z);

symbolic procedure f38(a,b,z);
 if parfool(diff1sq('(1 . 1),addsq(a,b))) or
    parfool(addsq('(1 . 1),diff1sq(b,a))) then
   simpGtoH((list(a) . nil),(list(a,b,negsq b) . nil),z) else
 multsq(expdeg('(4 . 1),diff1sq('(1 . 1),a)),
        multsq(multgamma(list(diff1sq( '(1 . 1),addsq(a,b)),
                addsq(b,diff1sq('(1 . 1),a)))),
                lommel2sq(diff1sq(multsq('( 2 . 1),a),'(1 . 1))
                ,multsq('(2 . 1),b),multsq('(2 . 1),
                        simpx1(prepsq z,1,2)))));

symbolic procedure f39(a,b,z);
  if not numberp(car diff1sq(a,b)) or
     not equal(cdr diff1sq(a,b),2) then
  multsq(quotsq(multsq(simpx1('pi,5,2),expdeg(z,a)),multsq('(2 . 1),
  simpfunc('cos,multsq(simp!* 'pi,diff1sq(b,a))))),multsq(hankel1sq(
  diff1sq(b,a),simpx1(prepsq z,1,2)),hankel2sq(diff1sq(b,a),
  simpx1(prepsq z,1,2)))) else
       simpGtoH((list(addsq(a,'(1 . 2))) . nil),
       (list(b,a,diff1sq(multsq('(2 . 1),a),b)) . nil),z);

%***********************************************************************
%*           Logarithmic case of Meijer's G-function                   *
%***********************************************************************

fluid '(!*infinitymultpole);

symbolic smacro procedure priznak(u,v);
 for each uu in u collect ( uu . v) ;

symbolic procedure GFMlogcase(a,b,z);
 begin scalar w;
  w:=allpoles(logcase(append(priznak(cdr a,'N),priznak(car b,'P))));
  w:=sortpoles(w);
  if null !*infinitymultpole then
              return GFMlogcasemult(w,a,b,z)
     else << !*infinitymultpole := nil;
        % to prevent lots of integrals from failing.
             return 'FAIL>>;
 end;

array res(5);

symbolic procedure allpoles uu;
  for each u in uu join
   begin scalar w;integer kr;
   while u do <<
    if equal(cdar u,'N) then kr:=kr-1
                        else kr:=kr+1;
    if kr > 0 then
     if not null cdr u then
      if not equal(caadr u,caar u) then
           w:=cons(list(
              kr,prepsq diff1sq(caadr u,caar u),negsq caar u),w)
        else w:=w
       else
          <<    w:=cons(list(kr,'infinity,negsq caar u),w);
                if not eqn(kr,1) then !*infinitymultpole:=T
          >>;
    u:=cdr u;
   >>;
  return w;
 end;

symbolic procedure logcase u;
   begin scalar blog,blognew,sb;
   sb:=u; u:=cdr sb;
 M1: if null sb then return blognew;

 M2: if null u then
           << if not null blog then
                        << blognew:=cons(blog,blognew);
                           blog:=nil
                        >>
                               else
                           blognew:=cons(list car sb,blognew);
              sb:=cdr sb;  if sb then u:=cdr sb;
              GOTO M1
           >>
          else
           if equal(caar sb,caar u) or
              and(numberp car diff1sq(caar sb,caar u),
                  equal(cdr diff1sq(caar sb,caar u),1))
             then
              << if null blog then
                    if equal(caar sb,caar u) or
                       car diff1sq(caar sb,caar u) < 0  then
                         blog:=list(car sb,car u)
                       else blog:=list(car u,car sb)
                            else
                    blog:=ordern(car u,blog);
                 sb:=delete(car u, sb);
                 if u then u:=cdr u;
                 GOTO M2
              >>
              else
              << if u then u:=cdr u;
                 GOTO M2;
              >>
end;

symbolic procedure ordern(u,v);
  %u - dotted pair (SQ . ATOM).
  %v - list of dotted pair.
  if null v then list(u)
    else
  if equal(car u,caar v) or car diff1sq(car u,caar v) > 0 then
              (car v) . ordern(u,cdr v)
    else
              u . v ;

symbolic procedure sortpoles(w);
 begin scalar w1,w2;
  while w do begin
   if equal(cadar w,'infinity) then w1:=(car w) . w1
     else w2:=(car w) . w2;
   w:=cdr w;
  end;
  return append(w2,w1);
 end;

symbolic procedure GFMlogcasemult(w,a,b,z);
  % w -- list of lists.
  if null w then (nil . 1) else
     addsq(groupresudes(car w,a,b,z),
           GFMlogcasemult(cdr w,a,b,z));

symbolic procedure groupresudes(w,a,b,z);
  % w -- (order number start).
   if not equal(cadr w,'infinity) then multpoles(w,a,b,z)
   else
   if equal(cadr w,'infinity) and car w = 1 then
      simplepoles(caddr w,a,b,z)
   else
      'FAIL;

symbolic procedure simplepoles(at,a,b,z);
 if member(at, car b) then
    infinitysimplpoles(a,(car redpar(car b,list(at)) . cdr b),
                          negsq at,z)
 else specialtransf(at,a,b,z);

symbolic procedure specialtransf(at,a,b,z);  %some changes by WN
 if listfooltwo(car b, cdr a) then
    begin scalar c, cc;
      c:=redpar(car b,cdr a);
      cc:=redpar(cdr b,car a);
      a:=(cadr cc . cadr c);
      b:=(car c . car cc);
      if listfooltwo(car b, cdr a) then
        <<
          c:=findtwoparams(car b, cdr a);
          a:=((car c) . car a ) .  car redpar(cdr a,list(car c));
          b:=(car redpar(car b,list(cadr c)) . (cadr c) . cdr b);
          return
%         multsq( expdeg('(-1 . 1), diff1sq(simp car c, simp cadr c)),
          multsq( expdeg('(-1 . 1), diff1sq(car c,  cadr c)),
                  specialtransf(at,a,b,z) )
        >>
      else
        return infinitysimplpoles( a,b,negsq at,z );
     end
 else
    begin scalar c;
      c:=redpar(cdr b,car a);
      a:=(cadr c . cdr a);
      b:=(car b . car c);
      return infinitysimplpoles( a,b,negsq at,z );
    end;

symbolic procedure findtwoparams(u, v);
 % u, v -- lists.
 begin scalar c;
   foreach uu in u do
      foreach vv in v do
        if parfool diff1sq(uu,vv)
          then << c := list(vv,uu); u := nil; v := nil>> ;
   return c;
end;

symbolic procedure multpoles (u,a,b,z);
  % u -- (order number start).
  if cadr u = 0 then (nil . 1) else
     addsq(multresude(list(car u, caddr u),a,b,z),
           multpoles(list(car u,cadr u-1,
                     diff1sq(caddr u,'(1 . 1))),a,b,z));

symbolic procedure multresude (u,a,b,z);
  % u -- (order start).
  % a,b -- parameters of G-function.
  % z - argument of G-function.
  <<
   for i:=0 step 1 until 5 do res(i):='(nil . 1);
   findresude(multlistasym(list(
          listtaylornom(listplus(car b,cadr u),simp 'eps,car u),
          listtaylornom(pdifflist(addsq('(1 . 1),negsq cadr u),car a),
                                 negsq simp 'eps,car u),
          listtaylorden(listplus(cdr a,cadr u),simp 'eps,car u),
          listtaylorden(pdifflist(addsq('(1 . 1),negsq cadr u),cdr b),
                                 negsq simp 'eps,car u),
          if equal(z,'(1 . 1)) then '(1 . 1) else
              multsq(expdeg(z,negsq cadr u),
                     seriesfordegree(car u,simp 'eps,z))),car u))
 >>;

symbolic procedure findresude u;
  begin scalar s,cc;
   cc:=prepsq(cdr u ./ 1);
   cc:= cdr algebraic coeff(cc,eps);
   while car cc = 0 do cc:=cdr cc;
   s:=if numberp car cc then simp car cc else cadr car cc;
   cc:=prepsq(car u ./ 1);
   cc:= cdr algebraic coeff(cc,eps);
   return
   multsq(invsq s,if numberp car cc then simp car cc
                                     else cadr car cc);
 end;

symbolic procedure seriesfordegree(n,v,z);
 if n=1 then '(1 . 1) else
    addsq(quotsq(multsq(exptsq(negsq v,n-1),
            exptsq(simpfunc('log,z),n-1)),gamsq((n . 1))),
          seriesfordegree(n-1,v,z));

symbolic procedure listtaylornom(u,v,n);
  % u -- list of SQ.
  % v -- EPS -> 0.
  % n -- order of the representation by the polynom.
  if null u then '(1 . 1) else
     multasym(taylornom(car u,v,n),listtaylornom(cdr u,v,n),n);

symbolic procedure multlistasym(u,n);
  if null u then '(1 . 1)
    else
     multasym(car u,multlistasym(cdr u,n),n);

symbolic procedure multasym(u,v,n);
  begin integer k;
  if null car u or null car v then return '(nil . 1);
  u:=multsq(u,v);
  if not oldpolstack(car u ./ 1) then return u;
  v:=res(0);
   while (k:=k+1) < n do
     v:=addsq(v,multsq(res(k),exptsq(simp 'eps,k)));
   return multsq(v,1 ./ cdr u);
  end;

symbolic procedure oldpolstack u;
  begin scalar cc; integer k;
   cc := prepsq u;
   cc:=cdr algebraic coeff(cc,eps);
   if null cc then return nil else k:=0;
   while not null cc do
      <<
      res(k):=if numberp car cc then simp(car cc)
                                else cadr car cc;
      cc:=cdr cc;k:=k+1;
      >>;
   return t;
  end;

symbolic procedure listtaylorden(u,v,n);
  % u -- list of SQ.
  % v -- EPS -> 0.
  % n -- order of the representation by the polynom.
  if null u then '(1 . 1) else
     multasym(taylorden(car u,v,n),listtaylorden(cdr u,v,n),n);

symbolic procedure taylornom(u,v,n);
  % u -- SQ.
  % v -- SQ is EPS -> 0.
  % n -- order of the representation by the polynom.
  if null car u then multsq(invsq v,taylorgamma('(1 . 1),v,n))
    else
  if parfool u then multlistasym(list(
     exptsq('(-1 . 1),if null car negsq u then 0 else car negsq u),
     invsq v,
     taylornom('(1 . 1),v,n),taylornom('(1 . 1),negsq v,n),
     taylorden(diff1sq('(1 . 1),u),negsq v,n)),n)
    else
     multsq(gamsq(u),taylorgamma(u,v,n));

symbolic procedure taylorden(u,v,n);
  % u -- SQ.
  % v -- SQ is EPS -> 0.
  % n -- order of the representation by the polynom.
  if parfool u then multlistasym(list(
     exptsq('(-1 . 1),if null car negsq u then 0 else car negsq u),
     v,
     taylornom(diff1sq('(1 . 1),u),negsq v,n),
     taylorden('(1 . 1),v,n),
     taylorden('(1 . 1),negsq v,n)),n)
               else
     quotsq(inversepol(taylorgamma(u,v,n),n),gamsq(u));

symbolic procedure inversepol(u,n);
  begin scalar sstack,c,w;integer k,m;
   if n=1 then return '(1 . 1);
   if null oldpolstack(car u ./ 1) then return u;
   sstack:=list('(1 . 1));  k:=2;
   while k <= n do begin
    w:=sstack;   m:=2; c:=nil . 1;
     while m <= k do begin
       c:=addsq(c,multsq(res(m-1),car w));
       m:=m+1; w:=cdr w;
     end;
    sstack:=(negsq c) . sstack;
    k:=k+1;
   end;
   w:=nil . 1;
   while sstack do begin
     w:=addsq(w,multsq(car sstack,exptsq(simp 'eps,n-1)));
     sstack:=cdr sstack;
     n:=n-1;
   end;
    return multsq(cdr u ./ 1,quotsq(w,res(0)));
  end;

symbolic procedure taylorgamma(u,v,n);
  % representation of gamma-function by the polynom of the
  % order  n  in  u  on the degree  v.
  if n=1 then '(1 . 1) else
     addsq(quotsq(multsq(exptsq(v,n-1),GammaToPsi(u,n-1)),
              gamsq(n ./ 1)),
           taylorgamma(u,v,n-1));

symbolic procedure GammaToPsi(u,n);
  if n=1 then psisq(u) else
     addsq(multsq(psisq(u),GammaToPsi(u,n-1)),
           diffsq(GammaToPsi(u,n-1),prepsq u));

algebraic <<
  operator lst,gfm;
  let gfm(lst(1,0,1,1),lst(1),lst(0),~z)=> (sign(1 + z) + sign(1 - z))/2
>>;

algebraic
   let meijerg({{},1},{{0,0},-1/2},~x) =>
        G_Fresnel_S(2*sqrt(x),-1)/(2^(-2)*sqrt(pi));


endmodule;

end;





