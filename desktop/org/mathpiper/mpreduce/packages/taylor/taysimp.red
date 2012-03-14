module TaySimp;

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


%*****************************************************************
%
%     The special Taylor simplification functions
%
%*****************************************************************


exports taysimpp, taysimpsq, taysimpsq!*, expttayrat, expttayrat1;

imports

% from the REDUCE kernel:
        !*f2q, !*k2q, !*p2f, !*p2q, !*t2q, addsq, apply1, denr,
        domainp, evenp, exptsq, invsq, kernp, mk!*sq, mkrn, multf,
        multpq, multsq, mvar, nth, numr, over, pdeg, prepsq, quotsq,
        reversip, sfp, simp, simp!*, tc, to, tpow,

% from the header module:
        !*q2TayExp, !*tay2f, !*tay2q, !*tayexp2q, comp!.tp!.!-p,
        cst!-taylor!*, has!-Taylor!*, find!-non!-zero,
        get!-degreelist, has!-TayVars, invert!-powerlist,
        make!-cst!-coefflis, make!-cst!-powerlist, make!-Taylor!*,
        prune!-coefflist, resimptaylor, TayCfPl, TayCfSq,
        TayCoeffList, TayFlags, TayGetCoeff, Taylor!-kernel!-sq!-p,
        Taylor!*p, Taylor!:, TayMakeCoeff, taymultcoeffs, TayOrig,
        TayTemplate, TayTpElNext, TayTpElPoint, TayTpElVars,
        TpNextList,

% from module Tayintro:
        confusion, Taylor!-error, Taylor!-error!*,

% from module Tayutils:
        addto!-all!-TayTpElOrders, get!-cst!-coeff, smallest!-increment,
        Taylor!*!-nzconstantp, Taylor!*!-zerop,

% from module Tayinterf:
        taylorexpand, taylorexpand!-sf,

% from module Taybasic:
        addtaylor, addtaylor!-as!-sq, invtaylor, makecoeffpairs,
        makecoeffs0, multtaylor, multtaylor!-as!-sq, multtaylorsq,
        quottaylor!-as!-sq;


fluid '(!*taylorautoexpand !*taylorkeeporiginal)$

comment The procedures in this module provide the higher taylor
        manipulation machinery.  Given any s.q. (s.f.,...) they
        return the equivalent Taylor kernel (disguised as a s.q.)
        if the argument contains a Taylor kernel and everything
        else may be Taylor expanded.
        Otherwise the Taylor kernels in the argument are only
        partially combined (but as far as possible);


symbolic procedure taysimpsq u;
  %
  % The argument u is any standard quotient.
  % numerator and denominator are first simplified independently,
  % then the quotient is built.
  % We have four possible cases here, as both expressions
  %  may or may not be Taylor kernels.
  %
  begin scalar nm,dd;
    dd := taysimpf denr u;
    if null numr dd then Taylor!-error('zero!-denom,'taysimpsq)
     else if Taylor!-kernel!-sq!-p dd
      then return taysimpf multf(numr u,!*tay2f invtaylor mvar numr dd);
    nm := taysimpf numr u;
    return
    if Taylor!-kernel!-sq!-p nm
      then if not has!-TayVars(mvar numr nm,dd)
             then !*tay2q resimptaylor
                            multtaylorsq(mvar numr nm,invsq dd)
            else if Taylor!*!-nzconstantp mvar numr nm
             then quotsq(get!-cst!-coeff mvar numr nm,dd)
            else if null !*taylorautoexpand or has!-Taylor!* dd
             then quotsq(nm,dd)
            else taysimpsq!*
                   quottaylor!-as!-sq(
                     nm,
                     taylorexpand(dd,TayTemplate mvar numr nm))
     else quotsq(nm,dd)
  end;

symbolic procedure taysimpsq!* u;
   %
   % Variant of taysimpsq that does not automatically expand
   %  non-Taylor expressions
   %
   taysimpsq u where !*taylorautoexpand := nil;


symbolic procedure taysimpf u;
  %
  % u is a standard form which may contain Taylor subexpressions;
  % value is a standard form
  %
  begin scalar tay,notay,x,flg;
    %
    % Remember the definition of a s.f.:
    %  it is either a domain element,
    %  or it's car is a standard term and it's cdr is a s.f.
    %
    notay := nil ./ 1;
    while u do
      %
      % Split the constituents of the s.f. into non-Taylor and
      %  Taylor parts.  Taylor s.t.'s are simplified accordingly.
      % A domain element can never be a Taylor kernel.
      %
      <<if domainp u then notay := addsq(!*f2q u,notay)
         else if not has!-Taylor!* car u
          then notay := addsq(notay,!*t2q car u)
         else <<x := taysimpt car u;
                if Taylor!-kernel!-sq!-p x
                  then if null tay then tay := mvar numr x
                    else if comp!.tp!.!-p(tay,mvar numr x)
                           then tay := addtaylor(tay,mvar numr x)
                          else <<flg := t;
                                 %
                                 % flg indicates that there are
                                 %  Taylor kernels whose templates
                                 %  are not compatible
                                 %
                                 notay := addsq(notay,x)>>
                 else notay := addsq(notay,x)>>;
         u := if domainp u then nil else cdr u>>;
    %
    % tay is now a Taylor kernel or nil.
    %
    % We first make sure that it is not actually a constant.
    %
    if not null tay and not null TayOrig tay and null numr TayOrig tay
      then return notay
    %
    % If tay is nil, return the non-taylor parts.
    %
     else if null numr notay and not null tay then return !*tay2q tay
     else if null tay or Taylor!*!-zerop tay then return notay;
    %
    % Otherwise the non-taylor parts (if the are non-nil)
    % must be expanded if !*taylorautoexpand is non-nil.
    % The only exception are terms that do not contain
    % any of the Taylor variables: these are always expanded.
    %
    if Taylor!*!-nzconstantp tay and not has!-Taylor!* notay
      then return addsq(get!-cst!-coeff tay,notay)
     else if null !*taylorautoexpand and has!-TayVars(tay,notay)
      then return addsq(!*tay2q tay,notay);
    if flg then return addsq(!*tay2q tay,notay)
     else <<
       notay := taylorexpand(notay,TayTemplate tay);
       return taysimpsq!* addtaylor!-as!-sq(notay,!*tay2q tay)>>
  end;

symbolic procedure taysimpt u;
  %
  % u is a standard term containing one or more Taylor kernels,
  % value is the simplified Taylor expression (also as a s.f.).
  %
  begin scalar rest,pow;
    %
    % Since the coefficient of a term is a s.f.
    % we call taysimpf on it.
    %
    rest := taysimpf tc u;
    if null numr rest then return rest;
    pow := tpow u;
    %
    % Then we have to distinguish three cases:
    %   the case where no Taylor kernel appears was already caught
    %   by taysimpf before taysimpt was called.
    %
    % If combination of different Taylor kernels is impossible
    %   return them separately
    %
    % Remark: the call to SMEMQLP checks if rest contains one of
    %         the Taylor variables if it is not a Taylor kernel.
    %
    return if not has!-Taylor!* pow
      then if Taylor!-kernel!-sq!-p rest
             then multpowerintotaylor(pow,mvar numr rest)
            else multpq(pow,rest)
     else <<pow := taysimpp pow;
            if not has!-Taylor!* rest and Taylor!-kernel!-sq!-p pow
              then if has!-TayVars(mvar numr pow,rest)
                 then if !*taylorautoexpand
                   then taysimpsq!* multtaylor!-as!-sq(pow,
%                          taylorexpand(rest,TayTemplate mvar numr pow))
%
% the above is not entirely correct. the expansion should be done
% in a way so that the result of the multiplication has same order
% as pow
%
                          taylorexpand!-sf(tc u,
                                           TayTemplate mvar numr pow,
                                           nil))
                  else multsq(pow,rest)
                else !*tay2q multtaylorsq(mvar numr pow,rest)
              else multtaylor!-as!-sq(pow,rest)>>
  end;

symbolic procedure multpowerintotaylor (p, tk);
  %
  % p is a standard power, tk a Taylor kernel
  % value is p expanded to a Taylor kernel multiplied by tk
  % this requires Taylor expansion of p if it contains
  % at least one of the expansion variables
  %
  % Remark: the call to SMEMQLP checks if p contains one of
  %         the Taylor variables.
  %
  if not has!-TayVars(tk,p)
    then !*tay2q multtaylorsq(tk,!*p2q p)
   else if !*taylorautoexpand
%    then taysimpsq!*
%           multtaylor!-as!-sq(!*tay2q tk,
%                     taylorexpand(!*p2q p,TayTemplate tk))
%
% here the same comment as above applies
%
    then taysimpsq!*
           multtaylor!-as!-sq(!*tay2q tk,
                     taylorexpand!-sf(!*p2f p,TayTemplate tk,nil))
   else if Taylor!*!-nzconstantp tk
    then multpq(p,get!-cst!-coeff tk)
   else multpq(p,!*tay2q tk);


symbolic procedure taysimpp u;
  %
  % u is a standard power containing a Taylor expression,
  % value is the simplified Taylor expression, as a s.f.
  %
  % We begin by isolating base and exponent.
  % First we simplify them separately.
  % Remember that the exponent is always an integer,
  % base is a kernel.
  %
  % If the main variable of the power is a kernel made of one
  %  of the functions known to the Taylor simplifier, call
  %  the appropriate simplification function.
  %  (There is a user hook here!)
  %
  if null car u or null pdeg u then confusion 'taysimpp
   else if sfp car u then !*p2q u
%%%% taysimpsq exptsq(taysimpf car u,cdr u)
   else if not taylor!*p car u
    then ((if kernp x
             then if (x := mvar numr x) = car u then !*p2q u
                   else if cdr u=1 then !*k2q x
                   else taysimpp(x .** cdr u)
            else if cdr u=1 then x
            else taysimpsq exptsq(x,cdr u))
          where x := (taysimpmainvar car u))
   %
   % We know how to raise a Taylor series to a rational power:
   %  positive integer --> multiply
   %  negative integer --> multiply and invert
   % Zero exponent should not appear: should be already simplified
   %  to 1 by the standard simplifier
   %
   else if not fixp pdeg u or pdeg u = 0 then confusion 'taysimpp
   else if not null TayOrig car u and null numr TayOrig car u
    then (nil ./ 1)
   else !*tay2q
     if pdeg u = 1 then car u else expttayi(car u,cdr u)$


symbolic procedure taysimpmainvar u;
  if not sfp u then taysimpkernel u
   else !*f2q taysimpf u;


symbolic procedure taysimpkernel u;
  begin scalar fn, x;
    u := simp!* u;
    if not kernp u then return u
     else << x := mvar numr u;
             if atom x or Taylor!*p x then return u;
             fn := get (car x, 'taylorsimpfn);
             return if null fn then u
                     else apply1 (fn, x)>>
  end;


symbolic procedure expttayi(u,i);
  %
  % raise Taylor kernel u to integer power i
  % algorithm is a scheme that computes powers of two.
  %
  begin scalar v,flg;
    if i<0 then <<i := -i; flg := t>>;
    v := if evenp i then cst!-Taylor!*(1 ./ 1,TayTemplate u)
          else <<i := i - 1; u>>;
    while (i:=i/2)>0 do <<u := multtaylor(u,u);
                          if not evenp i then v := multtaylor(v,u)>>;
    return if flg then invtaylor v else v
  end;


comment non-integer powers of Taylor kernels;


comment The implementation of expttayrat follows the algorithm
        quoted by Knuth in the second volume of `The Art of
        Computer Programming', extended to the case of series in
        more than one variable.

        Assume you want to compute the series W(x) where

            W(x) = V(x)**alpha

        Differentiation of this equation gives

            W'(x) = alpha * V(x)**alpha * V'(x) .

        You make now the ansatz

                    -----
                    \           n
            W(x) =   >      W  x  ,
                    /        n
                    -----

        substitute this into the above equation and compare
        powers of x.  This yields the recursion formula

                       n-1
                      -----
                  1   \                  m      m
           W  = -----  >    (alpha (1 - ---) - --- ) W  V     .
            n    V    /                  n      n     m  n-m
                  0   -----
                       m=0

        The first coefficient must be calculated directly, it is

           W   = V  ** alpha .
            0     0

        To use this for series in more than one variable you have to
        calculate all partial derivatives: n and m refer then to the
        corresponding component of the multi index.  Looking closely
        one finds that there is an ambiguity: the same coefficient
        can be calculated using any of the partial derivatives.  The
        only restriction is that the corresponding component of the
        multi index must not be zero, since we have to divide by it.

        We resolve this ambiguity by simply taking the first nonzero
        component.

        We use it here only for the case that alpha is a rational
        number;


symbolic procedure expttayrat(tay,rat);
  %
  % tay is a Taylor kernel, rat is a s.q. of two integers
  % value is tay ** rat
  % algorithm as quoted by Knuth
  %
  Taylor!:
  begin scalar clist,tc,tp;
    %
    % First of all we have to find out if we can raise the leading
    %  term to the power rat.
    % If so we calculate the reciprocal of this leading coefficient
    %  and multiply all other terms with it.
    % This guarantees that the resulting Taylor kernel starts with
    %  coefficient 1.
    %
    if not Taylor!*p tay then return simp!* {'expt,tay,mk!*sq rat};
    tc := prune!-coefflist TayCoeffList tay;
    tp := TayTemplate tay;
    %
    % Find first non-zero coefficient.
    %
    if null tc
      then if minusp numr rat
             then Taylor!-error!*('not!-a!-unit,'expttayrat)
            else <<tp := for each tpel in tp collect begin scalar w;
                           w := TayTpElNext tpel * !*q2TayExp rat;
                           return {TayTpElVars tpel,
                                   TayTpElPoint tpel,
                                   w - mkrn(1,denr rat),
                                   w};
                           end;
                   clist := make!-cst!-coefflis(nil ./ 1,tp)>>
     else begin scalar c0,l,l1;
       c0 := car tc;
       l1 := for each ll in TayCfPl c0 collect
               for each p in ll collect (p * !*q2TayExp rat);
       l := invert!-powerlist TayCfPl c0;
       tp := addto!-all!-TayTpElOrders(tp,get!-degreelist l);
       l := TayMakeCoeff(l,invsq TayCfSq c0);
       %
       % We divide the rest of the kernel (without the leading term)
       %  by the leading term.
       %
       l := for each el in cdr tc collect taymultcoeffs(el,l);
       clist := expttayrat1(tp,l,rat);
       %
       % Next we multiply the resulting Taylor kernel by the leading
       %  coefficient raised to the power rat.
       %
       c0 := TayMakeCoeff(l1,simp!* {'expt,mk!*sq TayCfSq c0,
                                     {'quotient,numr rat,denr rat}});
       clist := for each el in clist collect taymultcoeffs(el,c0);
       tp := addto!-all!-TayTpElOrders(tp,get!-degreelist l1);
    end;
    %
    % Finally we fill in the original expression
    %
    return make!-Taylor!*(
             clist,
             tp,
             if !*taylorkeeporiginal and TayOrig tay
               then simp {'expt,prepsq TayOrig tay,
                          {'quotient,car rat,cdr rat}}
              else nil,
             TayFlags tay)
  end;

symbolic procedure expttayrat1(tp,tcl,rat);
  Taylor!:
   begin scalar clist,coefflis,il,l0,rat1;
     rat1 := addsq(rat,1 ./ 1);
     %
     % Now we compute the coefficients
     %
     l0 := make!-cst!-powerlist tp;
     clist := {TayMakeCoeff(l0,1 ./ 1)};
     tcl := TayMakeCoeff(l0,1 ./ 1) . tcl;
     il := smallest!-increment tcl;
     coefflis := makecoeffs0(tp,TpNextList tp,il);
     if null coefflis then return clist;
     for each cc in cdr coefflis do
       begin scalar s,pos,pp,q,n,n1;
         s := nil ./ 1;
         pos := find!-non!-zero cc;
         n := nth(nth(cc,car pos),cdr pos);
         pp := makecoeffpairs(l0,cc,l0,il);
         for each p in pp do begin scalar v,w;
           v := TayGetCoeff(cdr p,tcl);
           w := TayGetCoeff(car p,clist);
           %
           % The following line is a short cut for efficiency.
           %
           if null numr v or null numr w then return;
           w := multsq(w,v);
           n1 := nth(nth(car p,car pos),cdr pos);
           q := quotsq(!*TayExp2q(-n1),!*TayExp2q n);
           s := addsq(s,multsq(addsq(rat,multsq(q,rat1)),w))
          end;
         if not null numr s then clist := TayMakeCoeff(cc,s) . clist
       end;
     return reversip clist
   end;

endmodule;

end;
