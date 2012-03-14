on rounded;

% This is part of ongoing work... the main Reduce sources in arith/rounded.red
% define safe!_fp!-plus etc but both CSL and PSL defined their own
% "better" versions. The CSL one in cslbase/arith08.c and the PSL one
% is packages/support/psl.red.

% As at least a temporary measure the CSL code has been adjusted to try to
% match the portable code. The PSL version should be trivial to do the
% same to by commenting out the versions in psl.red. However a better
% long term solution will be to end up with compatible nice versions.

% The code here checks against a copy of the reference code. At present I
% believe that CSL matches that. PSL is not expected to - but I *really*
% do not understand the nature of some of the discrepancies and they look
% a bit like bugs to me. If nothing else on one Linux I see results that
% that suggest "1.0 = 1.0" sometimes says "no"...

lisp;

on comp;

errs := 0;

maxx := 1.797e+308;


ll := v := nil;
for i := 0:20 do <<
   w := expt(1.12345, i);
   v := w . (1.0/w) . v;
   v := (!!plumin*w) . (!!plumin/w) . v;
   if w < maxx/!!plumax then
     v := (!!plumax*w) . (!!plumax/w) . v;
   v := (!!timmin*w) . (!!timmax/w) . v
   >>;

v;

for each x in v do
  ll := x . (-x) . ll;

% ll is now a list of critical values

length ll;

fluid '(errs);

symbolic procedure badcase();
 << errs := errs + 1;
    if errs > 20 then stop  0>>;


symbolic procedure portable!-fp!-plus(x,y);
   if zerop x then y
    else if zerop y then x
    else if x>0.0 and y>0.0
     then if x<!!plumax and y<!!plumax then plus2(x,y) else nil
    else if x<0.0 and y<0.0
     then if -x<!!plumax and -y<!!plumax then plus2(x,y) else nil
    else if abs x<!!plumin and abs y<!!plumin then nil
    else (if u=0.0 then u else if abs u<!!fleps1*abs x then 0.0 else u)
         where u = plus2(x,y);

symbolic procedure portable!-fp!-times(x,y);
 if zerop x or zerop y then 0.0
 else if x=1.0 then y else if y=1.0 then x else
   begin scalar u,v; u := abs x; v := abs y;
      if u>=1.0 and u<=!!timmax then
         if v<=!!timmax then go to ret else return nil;
      if u>!!timmax then if v<=1.0 then go to ret else return nil;
      if u<1.0 and u>=!!timmin then
         if v>=!!timmin then go to ret else return nil;
      if u<!!timmin and v<1.0 then return nil;
 ret: return times2(x,y) end;

symbolic procedure portable!-fp!-quot(x,y);
 if zerop y then rdqoterr()
 else if zerop x then 0.0 else if y=1.0 then x else
   begin scalar u,v; u := abs x; v := abs y;
      if u>=1.0 and u<=!!timmax then
         if v>=!!timmin then go to ret else return nil;
      if u>!!timmax then if v>=1.0 then go to ret else return nil;
      if u<1.0 and u>=!!timmin then
         if v<=!!timmax then go to ret else return nil;
      if u<!!timmin and v>1.0 then return nil;
 ret: return quotient(x,y) end;

symbolic procedure tab_to n;
  while posn() < n do prin2 " ";

for each x in ll do
  for each y in ll do <<
     a1 := safe!-fp!-plus(x, y);
     a2 := portable!-fp!-plus(x, y);
     if not eqn(a1, a2) then <<
         terpri();
         prin2t "safe-fp-plus incorrect";
         prin2 "x: "; prin2 x; tab_to 40; prin2t hexfloat1 x;
         prin2 "y: "; prin2 y; tab_to 40; prin2t hexfloat1 y;
         prin2 "new: "; prin2 a1; tab_to 40; prin2t hexfloat1 a1;
         prin2 "ref: "; prin2 a2; tab_to 40; prin2t hexfloat1 y;
         terpri();
         badcase() >>;
     a1 := safe!-fp!-times(x, y);
     a2 := portable!-fp!-times(x, y);
     if not eqn(a1, a2) then <<
         terpri();
         prin2t "safe-fp-times incorrect";
         prin2 "x: "; prin2 x; tab_to 40; prin2t hexfloat1 x;
         prin2 "y: "; prin2 y; tab_to 40; prin2t hexfloat1 y;
         prin2 "new: "; prin2 a1; tab_to 40; prin2t hexfloat1 a1;
         prin2 "ref: "; prin2 a2; tab_to 40; prin2t hexfloat1 y;
         terpri();
         badcase() >>;
     a1 := safe!-fp!-quot(x, y);
     a2 := portable!-fp!-quot(x, y);
     if not eqn(a1, a2) then <<
         terpri();
         prin2t "safe-fp-quot incorrect";
         prin2 "x: "; prin2 x; tab_to 40; prin2t hexfloat1 x;
         prin2 "y: "; prin2 y; tab_to 40; prin2t hexfloat1 y;
         prin2 "new: "; prin2 a1; tab_to 40; prin2t hexfloat1 a1;
         prin2 "ref: "; prin2 a2; tab_to 40; prin2t hexfloat1 y;
         terpri();
         badcase() >> >>;


end;


