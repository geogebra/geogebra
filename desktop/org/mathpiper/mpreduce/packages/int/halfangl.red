module halfangl;  % Routines for conversion to half angle tangents.

% Author: Steve Harrington.
% Modifications by: John P. Fitch.

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


fluid '(!*gcd);

exports halfangle,untan;

symbolic procedure transform(u,x);
   % Transform the SQ U to remove the 'bad' functions sin, cos, cot etc.
   % in favor of half angles.
   % Do this with regard to cases like sin(x)*tan(x/2), so attempt to
   % limit times we use half angles.
   begin scalar zl,tnarg,substlist;
      zl := zlist;
      while car(tnarg := tan!-function!-in zl)
         and halfangle!-confusion(zlist,cadar tnarg)
       do <<zl := cdr tnarg;
            tnarg := car tnarg;
            if eqcar(tnarg,'tan)
              then substlist := (gensym() . tnarg) . substlist
             else substlist := (gensym() .
                                  list('quotient,1,('tan . cdr tnarg)))
                                  . substlist;
            u := subst(caar substlist,tnarg,u)>>;
      return if substlist
               then simp sublis(substlist,prepsq halfangle(u,x))
              % simp prepsq was added so that 1/(e**x*cos(1/e**x)**2)
              % for example returns a closed-form result.
              else simp prepsq halfangle(u,x)
   end;

symbolic procedure tan!-function!-in zz;
   % Look at zlist for tangents or cotangents.
   <<while zz and not eqcar(car zz,'tan) and not eqcar(car zz,'cot)
        do zz := cdr zz;
     if null zz then nil . nil else zz>>;

symbolic procedure halfangle!-confusion(zz,tnarg);
   % Is there a function in the zlist with twice the tangent argument?
   <<while zz and (atom car zz
              or not(tnarg = prepsq simp list('quotient,cadar zz,2)))
        do zz := cdr zz;
     zz>>;

symbolic procedure quotqq(u1,v1);
   multsq(u1,invsq(v1));

symbolic procedure !*subtrq(u1,v1);
   addsq(u1, negsq(v1));

symbolic procedure !*int2qm(u1);
   if u1=0 then nil . 1 else u1 . 1;

symbolic procedure halfangle(r,x);
   % Top level procedure for converting;
   % R is a rational expression to be converted,
   % X the integration variable.
   % A rational expression is returned.
   quotqq(hfaglf(numr(r),x), hfaglf(denr(r),x));

symbolic procedure hfaglf(p,x);
   % Converting polynomials,  a rational expression is returned.
   if domainp(p) then !*f2q(p)
    else subs2q addsq(multsq(exptsq(hfaglk(mvar(p),x), ldeg(p)),
                             hfaglf(lc(p),x)),
                      hfaglf(red(p),x));

symbolic procedure hfaglk(k,x);
   % Converting kernels,  a rational expression is returned.
   begin
      scalar kt;
      if atom k or not member(x,flatten(cdr(k))) then return !*k2q k;
      k := car(k) . hfaglargs(cdr(k), x);
      if cadr k eq 'pi then return !*k2q k;  % Don't consider tan(pi/2).
      kt := simp list('tan, list('quotient, cadr(k), 2));
      return if car(k) = 'sin
       then quotqq(multsq(!*int2qm(2),kt), addsq(!*int2qm(1),
                            exptsq(kt,2)))
      else if car(k) = 'cos
       then quotqq(!*subtrq(!*int2qm(1),exptsq(kt,2)),addsq(!*int2qm(1),
         exptsq(kt,2)))
      else if car(k) = 'tan
       then quotqq(multsq(!*int2qm(2),kt), !*subtrq(!*int2qm(1),
                            exptsq(kt,2)))
      else if car(k) = 'cot
       then quotqq(!*subtrq(!*int2qm(1),
                   exptsq(kt,2)),multsq(!*int2qm(2),kt))
      else if car(k) = 'sec
       then quotqq(addsq(!*int2qm(1), exptsq(kt,2)),
                   !*subtrq(!*int2qm(1),exptsq(kt,2)))
      else if car(k) = 'csc
       then quotqq(addsq(!*int2qm(1),exptsq(kt,2)),
%%%                   !*subtrq(!*int2qm(1),exptsq(kt,2)))
                    % FJW - was identical to sec!!!
                   multsq(!*int2qm(2),kt))
      else if car(k) = 'sinh then
        quotqq(!*subtrq(!*p2q mksp('expt.('e. cdr k),2),
        !*int2qm(1)), multsq(!*int2qm(2),
                             !*p2q mksp('expt . ('e . cdr(k)),1)))
      else if car(k) = 'cosh then
        quotqq(addsq(!*p2q mksp('expt.('e. cdr k),2),
        !*int2qm(1)), multsq(!*int2qm(2),
                             !*p2q mksp('expt . ('e . cdr(k)),1)))
      else if car(k) = 'tanh then
        quotqq(!*subtrq(!*p2q mksp('expt.('e. cdr k),2),
        !*int2qm(1)), addsq(!*p2q mksp ('expt.('e.cdr(k)),2),
        !*int2qm(1)))
      else if car(k) = 'coth then
        quotqq(addsq(!*p2q mksp('expt.('e.cdr(k)),2), !*int2qm(1)),
              !*subtrq(!*p2q mksp('expt.('e . cdr k),2),!*int2qm(1)))
      else if car(k) = 'sech then
        quotqq(multsq(!*int2qm(2),!*p2q mksp('expt . ('e . cdr(k)),1)),
               addsq(!*p2q mksp('expt.('e. cdr k),2),!*int2qm(1)))
      else if car(k) = 'csch then
        quotqq(multsq(!*int2qm(2),!*p2q mksp('expt . ('e . cdr(k)),1)),
               !*subtrq(!*p2q mksp('expt.('e. cdr k),2),!*int2qm(1)))
      else if car(k) = 'acot then
        !*p2q mksp(list('atan, list('quotient, 1, cadr k)),1)
      else !*k2q(k);  % additional transformation might be added here.
   end;

symbolic procedure hfaglargs(l,x);
   % Conversion of argument list.
   if null l then nil
    else prepsq(hfaglk(car(l),x)) . hfaglargs(cdr(l),x);

symbolic procedure untanf x;
   % This should be done by a table.
   % We turn off gcd to avoid unnecessary gcd calculations, as suggested
   % by Rainer Schoepf.
   begin scalar !*gcd,y,z,w;
      if domainp x then return x . 1;
      y := mvar x;
      if eqcar(y,'int) then error1();  % assume all is hopeless.
      z := ldeg x;
      w := 1 . 1;
      y :=
       if atom y then !*k2q y
        else if car y eq 'tan
         then begin scalar yy;
%%              printc "Recursive tan"; printc cadr y;
                yy := prepsq untan simp cadr y . nil;
%%              princ "==> "; printc yy;
                if evenp z
                then <<z := z/2;
                       return simp list('quotient,
                                        list('plus,
                                             list('minus,
                                                  list('cos,
                                                       'times
                                                         . (2 . yy))),
                                             1),list('plus,
                                                     list('cos,
                                                          'times
                                                            . (2 . yy)),
                                              1))>>
               else if z=1
                then return simp list('quotient,
                                      list('plus,
                                           list('minus,
                                                list('cos,
                                                    'times . (2 . yy))),
                                           1),list('sin,
                                                   'times . (2 . yy)))
               else <<z := (z - 1)/2;
                      w :=
                       simp list('quotient,
                                 list('plus,
                                      list('minus,
                                           list('cos,
                                                'times
                                                  . (2 . yy))),
                                      1),list('sin,
                                              'times
                                                . (2 . yy)));
                      return simp list('quotient,
                                       list('plus,
                                            list('minus,
                                                 list('cos,
                                                      'times
                                                        . (2 . yy))),
                                            1),list('plus,
                                                    list('cos,
                                                         'times
                                                           . (2 . yy)),
                                                    1)) >>
        end
        else simp y;
      return addsq(multsq(multsq(exptsq(y,z),untanf lc x),w),
                   untanf red x)
   end;

% symbolic procedure untanlist(y);
%    if null y then nil
%      else (prepsq (untan(simp car y)) . untanlist(cdr y));

symbolic procedure untan(x);
   % Expects x to be canonical quotient.
   begin scalar y;
      y:=cossqchk sinsqrdchk multsq(untanf(numr x),
                                    invsq untanf(denr x));
      return if length flatten y>length flatten x then x else y
   end;

symbolic procedure sinsqrdchk(x);
   multsq(sinsqchkf(numr x), invsq sinsqchkf(denr x));

symbolic procedure sinsqchkf(x);
   begin
      scalar y,z,w;
      if domainp x then return x . 1;
      y := mvar x;
      z := ldeg x;
      w := 1 . 1;
      y := if eqcar(y,'sin) then if evenp z
       then <<z := quotient(z,2);
              simp list('plus,1,list('minus,
                                     list('expt,('cos . cdr(y)),2)))>>
      else if z = 1 then !*k2q y
      else  << z := quotient(difference(z,1),2); w := !*k2q y;
             simp list('plus,1,list('minus,
                                    list('expt,('cos . cdr(y)),2)))>>
       else !*k2q y;
      return addsq(multsq(multsq(exptsq(y,z),sinsqchkf(lc x)),w),
                   sinsqchkf(red x));
   end;

symbolic procedure cossqchkf(x);
   begin
      scalar y,z,w,x1,x2;
      if domainp x then return x . 1;
      y := mvar x;
      z := ldeg x;
      w := 1 . 1;
      x1 := cossqchkf(lc x);
      x2 := cossqchkf(red x);
      x := addsq(multsq(!*p2q lpow x,x1),x2);
      y := if eqcar(y,'cos) then if evenp z
       then <<z := quotient(z,2);
              simp list('plus,1,list('minus,
                                     list('expt,('sin . cdr(y)),2)))>>
      else if z = 1 then !*k2q y
      else  << z := quotient(difference(z,1),2); w := !*k2q y;
             simp list('plus,1,list('minus,
                                    list('expt,('sin . cdr(y)),2)))>>
       else !*k2q y;
      y := addsq(multsq(multsq(exptsq(y,z),w),x1),x2);
      return if length(y) > length(x) then x else y;
   end;

symbolic procedure cossqchk(x);
begin scalar !*gcd;
   !*gcd := t;
   return multsq(cossqchkf(numr x), invsq cossqchkf(denr x))
end;

symbolic procedure lrootchk(l,x);
   % Checks each member of list l for a root.
   if null l then nil else krootchk(car l, x) or lrootchk(cdr l, x);

symbolic procedure krootchk(f,x);
   % Checks a kernel to see if it is a root.
   if atom f then nil
    else if car(f) = 'sqrt and member(x, flatten cdr f) then t
   else if car(f) = 'expt
        and not atom caddr(f)
        and caaddr(f) = 'quotient
        and member(x, flatten cadr f)  then t
   else lrootchk(cdr f, x);

symbolic procedure rootchk1p(f,x);
   % Checks polynomial for a root.
   if domainp f then nil
    else krootchk(mvar f,x) or rootchk1p(lc f,x) or rootchk1p(red f,x);

symbolic procedure rootcheckp(f,x);
   % Checks rational (standard quotient) for a root.
   rootchk1p(numr f,x) or rootchk1p(denr f,x);

endmodule;

end;
