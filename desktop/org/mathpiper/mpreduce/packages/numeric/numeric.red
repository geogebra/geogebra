module numeric;  % Header module for the numeric package and
                 % support of numerical evaluation of symbolic
                 % expressions.

% Author: Herbert Melenk.

% Copyright (c) 1993 ZIB Berlin, RAND.  All rights reserved.

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


create!-package('(numeric numeval numsolve gauss newton steepstd
                  bounds numint numfit chebysh rungeku),
                '(contrib numeric));

imports addsq, accuracycontrol, boundserr, denr, domainpa, errorp,
     impartf, lprim, matrix, mkquote, mkvect, minusf, multsq, negsq,
     numr, precision, prepf, prepsq, rederr, reval, setmatelem, setmode,
     simp, simp!*, sqgreaterp, subsq, subtrs, subtrsq, typerr, valuechk,
     writepri, !:difference, !:quotient, !:!:quotient, !:recip, !*eqn2a;

exports boundseval, boundsevalrd, chebysheveval, fiteval, intrdeval,
        rdsolveeval, rungekuttaeval, rdmineval;

fluid '(!*noequiv accuracy!* minus!-infinity!*);
global '(iterations!* !*trnumeric);
switch trnumeric;

% Create .. as infix operator.

newtok '( (!. !.) !*interval!*);

if null get('!*interval!*,'simpfn) then
<<precedence .., or;
  algebraic operator ..;
  put('!*interval!*,'prtch,'! !.!.! );
>>;

% some common utilities

minus!-infinity!* := '(minus infinity);

% intervals

symbolic procedure adomainp u;
   numberp u or (pairp u and idp car u and get(car u,'dname))
    or (eqcar(u,'minus) and  adomainp cadr u);

symbolic procedure revalnuminterval(u,num);
 % Evaluate u as interval; numeric bounds required if num=T.
  begin scalar l;
    if not eqcar(u,'!*interval!*) then typerr(u,"interval");
    l:={reval cadr u,reval caddr u};
    if adomainpx(car l,num) and adomainpx(cadr l,num)then return l;
    typerr(u,"numeric interval");
  end;

symbolic procedure adomainpx(u,num);
  % extended algebraic domainp test:
  % num = t: u is a domain element;
  % num = inf: u is a domain element or inf or (minus inf)
  % num = nil: u is arbitrary.
    null num or adomainp u or num='infinity
                and member(u,'(infinity (minus infinity)));

symbolic procedure evalgreaterpx(a,b);
  if a =minus!-infinity!* or b = 'infinity then nil else
  a='infinity or b=minus!-infinity!* or evalgreaterp(a,b);

symbolic procedure mkinterval(u,v);
   list('!*interval!*,u,v);

% Easy coding of numerical procedures with REDUCE:
%
%  In statements or procedure bodies tagged with "dm:" all
%  arithmetic function calls are replaced by REDUCE domain
%  arithmetic.

symbolic macro procedure dm!: u;
  subla('((plus2 . !:plus)(times2 . !:times)
          (plus . !:plusn)(times . !:timesn)
          (quotient . !:!:quotient)
          (difference . !:difference)
          (minus . !:minus)
          (minusp . !:minusp)
          (zerop . !:zerop)
          (lessp . (lambda(a b)(!:minusp (!:difference a b))))
          (greaterp . (lambda(a b)(!:minusp (!:difference b a))))
          (leq . (lambda(a b)(not (!:minusp (!:difference b a)))))
          (geq . (lambda(a b)(not (!:minusp (!:difference a b)))))
          (sqrt . num!-sqrtf)
          (abs . absf)
          (min2 . dm!:min)
          (max2 . dm!:max)
          (min . dm!:min)
          (max . dm!:max)
         ) , cadr u);

%wrappers for n-ary plus and times

symbolic macro procedure !:plusn u;
  if null cddr u then cadr u else
   list('!:plus,cadr u,'!:plusn . cddr u);

symbolic macro procedure !:timesn u;
  if null cddr u then cadr u else
   list('!:times,cadr u,'!:timesn . cddr u);

endmodule;

end;
