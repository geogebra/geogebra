module linop; % Linear operator package.

% Author: Anthony C. Hearn.

% Copyright (c) 1987 The RAND Corporation. All rights reserved.

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


fluid '(!*intstr);

symbolic procedure linear u;
   for each x in u do
    if not idp x then typerr(x,'operator) else flag(list x,'linear);

rlistat '(linear);

symbolic procedure formlnr u;
   begin scalar x,y,z;
      x := car u;
      if null cdr u or null cddr u
        then rerror(alg,29,list("Linear operator",
                                x,"called with too few arguments"));
      y := cadr u;
      z := !*a2k caddr u . cdddr u;
      return if y = 1 then u
       else if not depends(y,car z)
        then list('times,y,x . 1 . z)
       else if atom y then u
       else if car y eq 'plus
        then 'plus . for each j in cdr y collect formlnr(x . j. z)
       else if car y eq 'minus
        then list('minus,formlnr(x . cadr y . z))
       else if car y eq 'difference
        then list('difference,formlnr(x . cadr y . z),
                              formlnr(x . caddr y . z))
       else if car y eq 'times then formlntms(x,cdr y,z,u)
       else if car y eq 'quotient then formlnquot(x,cdr y,z,u)
       else if car y eq 'recip
        then formlnrecip(x,carx(cdr y,'recip),z,u)
       else if y := expt!-separate(y,car z)
        then list('times,car y,x . cdr y . z)
       else u
   end;

symbolic procedure formseparate(u,v);
   %separates U into two parts, and returns a dotted pair of them: those
   %which are not commutative and do not depend on V, and the remainder;
   begin scalar w,x,y;
      for each z in u do
        if not noncomp z and not depends(z,v) then x := z . x
         else if (w := expt!-separate(z,v))
        then <<x := car w . x; y := cdr w . y>>
         else y := z . y;
      return reversip!* x . reversip!* y
   end;

symbolic procedure expt!-separate(u,v);
   %determines if U is an expression in EXPT that can be separated into
   %two parts, one that does not depend on V and one that does,
   %except if there is no non-dependent part, NIL is returned;
   if not eqcar(u,'expt) or depends(cadr u,v)
           or not eqcar(caddr u,'plus)
     then nil
    else expt!-separate1(cdaddr u,cadr u,v);

symbolic procedure expt!-separate1(u,v,w);
   begin scalar x;
      x := formseparate(u,w);
      return if null car x then nil
              else list('expt,v,replus car x) .
                   if null cdr x then 1 else list('expt,v,replus cdr x)
   end;

symbolic procedure formlntms(u,v,w,x);
   %U is a linear operator, V its first argument with TIMES removed,
   %W the rest of the arguments and X the whole expression.
   %Value is the transformed expression;
   begin scalar y;
      y := formseparate(v,car w);
      return if null car y then x
              else 'times . aconc!*(car y,
                if null cddr y then formlnr(u . cadr y . w)
                      else u . ('times . cdr y) . w)
   end;

symbolic procedure formlnquot(fn,quotargs,rest,whole);
   %FN is a linear operator, QUOTARGS its first argument with QUOTIENT
   %removed, REST the remaining arguments, WHOLE the whole expression.
   %Value is the transformed expression;
   begin scalar x;
      return if not depends(cadr quotargs,car rest)
         then list('quotient,formlnr(fn . car quotargs . rest),
                   cadr quotargs)
        else if not depends(car quotargs,car rest)
               and car quotargs neq 1
         then list('times,car quotargs,
                   formlnr(fn . list('recip,cadr quotargs) . rest))
        else if eqcar(car quotargs,'plus)
         then 'plus . for each j in cdar quotargs
                collect formlnr(fn . ('quotient . j . cdr quotargs)
                                 . rest)
        else if eqcar(car quotargs,'minus)
         then list('minus,formlnr(fn .
                        ('quotient . cadar quotargs . cdr quotargs)
                            . rest))
        else if eqcar(car quotargs,'times)
                and car(x := formseparate(cdar quotargs,car rest))
         then 'times . aconc!*(car x,
                formlnr(fn . list('quotient,mktimes cdr x,
                             cadr quotargs) . rest))
        else if eqcar(cadr quotargs,'times)
                and car(x := formseparate(cdadr quotargs,car rest))
         then list('times,list('recip,mktimes car x),
                formlnr(fn . list('quotient,car quotargs,mktimes cdr x)
                         . rest))
        else if x := expt!-separate(car quotargs,car rest)
         then list('times,car x,formlnr(fn . list('quotient,cdr x,cadr
                                                     quotargs) . rest))
        else if x := expt!-separate(cadr quotargs,car rest)
         then list('times,list('recip,car x),
                   formlnr(fn . list('quotient,car quotargs,cdr x)
                              . rest))
        else if (x := reval!* cadr quotargs) neq cadr quotargs
         then formlnquot(fn,list(car quotargs,x),rest,whole)
        else whole
   end;

symbolic procedure formlnrecip(fn,reciparg,rest,whole);
   % FN is a linear operator, RECIPARG the RECIP argument, REST the
   % remaining arguments, WHOLE the whole expression.  Value is the
   % transformed expression.
   begin scalar x;
      return if not depends(reciparg,car rest)
         then list('quotient,fn . 1 . rest,reciparg)
        else if eqcar(reciparg,'minus)
         then list('minus,formlnr(fn . ('recip . cdr reciparg) . rest))
        else if eqcar(reciparg,'times)
                and car(x := formseparate(cdr reciparg,car rest))
         then list('times,list('recip,mktimes car x),
                formlnr(fn . list('recip,mktimes cdr x)
                         . rest))
        else if x := expt!-separate(reciparg,car rest)
         then list('times,list('recip,car x),
                   formlnr(fn . list('recip,cdr x)
                              . rest))
        else if (x := reval!* reciparg) neq reciparg
         then formlnrecip(fn,x,rest,whole)
        else whole
   end;

symbolic procedure mktimes u;
   if null cdr u then car u else 'times . u;

symbolic procedure reval!* u;
   %like REVAL, except INTSTR is always ON;
   begin scalar !*intstr;
      !*intstr := t;
      return reval u
   end;

endmodule;

end;
