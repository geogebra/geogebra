module elem; % Simplification rules for elementary functions.

% Author: Anthony C. Hearn.
% Modifications by:  Herbert Melenk, Rainer Schoepf.

% Copyright (c) 1993 The RAND Corporation. All rights reserved.

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


fluid '(!*!*sqrt !*complex !*keepsqrts !*precise !*precise_complex
        !*rounded dmode!* !*elem!-inherit);

% No references to RPLAC-based functions in this module.

% For a proper bootstrapping the following order of operator
% declarations is essential:

%    sqrt
%    sign with reference to sqrt
%    trigonometrical functions using abs which uses sign

algebraic;

% Square roots.

deflist('((sqrt simpsqrt)),'simpfn);

% for all x let sqrt x**2=x;

% !*!*sqrt:  used to indicate that SQRTs have been used.

% !*keepsqrts:  causes SQRT rather than EXPT to be used.

symbolic procedure mksqrt u;
   if not !*keepsqrts then list('expt,u,list('quotient,1,2))
    else <<if null !*!*sqrt then <<!*!*sqrt := t;
                              algebraic for all x let sqrt x**2=x>>;
      list('sqrt,u)>>;

for all x let df(sqrt x,x)=sqrt x/(2*x);


% SIGN operator.

symbolic procedure sign!-of u;
  % Returns -1,0 or 1 if the sign of u is known. Otherwise nil.
   (numberp s and s) where s = numr simp!-sign{u};

symbolic procedure simp!-sign u;
 begin scalar s,n;
   u:=reval car u;
   s:=if eqcar(u,'abs) then '(1 . 1)
      else if eqcar(u,'times) then simp!-sign!-times u
      else if eqcar(u,'plus) then simp!-sign!-plus u
      else simpiden{'sign,u};
   if not numberp(n:=numr s) or n=1 or n=-1 then return s;
   typerr(n,"sign value");
 end;

symbolic procedure simp!-sign!-times w;
 % Factor all known signs out of the product.
  begin scalar n,s,x;
   n:=1;
   for each f in cdr w do
   <<x:=simp!-sign {f};
     if fixp numr x then n:=n * numr x else s:=f.s>>;
   n:=(n/abs n) ./ 1;
   s:=if null s then '(1 . 1) else
     simpiden {'sign, if cdr s then 'times.reversip s else car s};
   return multsq (n,s)
  end;

symbolic procedure simp!-sign!-plus w;
 % Stop sign evaluation as soon as two different signs
 % or one unknown sign were found.
  begin scalar n,m,x,q;
   for each f in cdr w do if null q then
   <<x:=simp!-sign {f};
     m:=if fixp numr x then numr x/abs denr x;
     if null m or n and m neq n then q:=t;
     n:=m>>;
   return if null q then n ./ 1 else
       simpiden {'sign,w};
  end;

fluid '(rd!-sign!*);

symbolic procedure rd!-sign u;
  % if U is constant evaluable return sign of u.
  % the value is set aside.
  if pairp rd!-sign!* and u=car rd!-sign!* then cdr rd!-sign!*
    else
  if !*complex or !*rounded or not constant_exprp u then nil
    else
  (begin scalar x,y,dmode!*;
    setdmode('rounded,t);
    x := aeval u;
    if evalnumberp x and 0=reval {'impart,x}
    then y := if evalgreaterp(x,0) then 1 else
         if evalequal(x,0) then 0 else -1;
    setdmode('rounded,nil);
    rd!-sign!*:=(u.y);
    return y
  end) where alglist!*=alglist!*;

symbolic operator rd!-sign;

operator sign;

put('sign,'simpfn,'simp!-sign);

% The rules for products and sums are covered by the routines
% below in order to avoid a combinatoric explosion. Abs (sign ~x)
% cannot be defined by a rule because the evaluation of abs needs
% sign.

sign_rules :=
   { sign ~x => (if x>0 then 1 else if x<0 then -1 else 0)
        when numberp x and impart x=0,
     sign(-~x) => -sign(x),
%%   sign( ~x * ~y) =>  sign x * sign y
%%         when numberp sign x or numberp sign y,
     sign( ~x / ~y) =>  sign x * sign y
           when y neq 1 and (numberp sign x or numberp sign y),
%%   sign( ~x + ~y) =>  sign x when sign x = sign y,
     sign( ~x ^ ~n) => 1 when fixp (n/2) and 
                              lisp(not (!*complex or !*precise_complex)),
     sign( ~x ^ ~n) => sign x^n when fixp n and numberp sign x,
     sign( ~x ^ ~n) => sign x when fixp n and 
                                   lisp(not (!*complex or !*precise_complex)),
     sign(sqrt ~a)  => 1 when sign a=1,
     sign( ~a ^ ~x) => 1 when sign a=1 and impart x=0,
%%   sign(abs ~a)   => 1,
     sign ~a => rd!-sign a when rd!-sign a,
     % Next rule here for convenience.
     abs(~x)^2 => x^2 when symbolic not !*precise}$
     % $ above needed for bootstrap.

let sign_rules;

% Rule for I**2.

remflag('(i),'reserved);

let i**2= -1;

flag('(e i nil pi),'reserved);   % Leave out T for now.

% Logarithms.

let log(e)= 1,
    log(1)= 0,
    log10(10) = 1,
    log10(1) = 0;

for all x let logb(1,x) = 0,
              logb(x,x) = 1,
              logb(x,e) = log(x),
              logb(x,10) = log10(x);

for all x let log(e**x)=x; % e**log x=x now done by simpexpt.

for all x let logb(a**x,a)=x;

for all x let log10(10**x)=x;

for all x let 10^log10(x)=x;

%% The following rule interferes with HE vector simplification,
%% until the problem is resolved use a more complicated rule
%for all a,x let a^logb(x,a)=x;
for all a,b,x such that a=b let a^logb(x,b)=x;


% The next rule is implemented via combine/expand logs.

% for all x,y let log(x*y) = log x + log y, log(x/y) = log x - log y;

let df(log(~x),~x) => 1/x;

let df(log(~x/~y),~z) => df(log x,z) - df(log y,z);

let df(log10(~x),~x) => 1/(x*log(10));

let df(logb(~x,~a),~x) => 1/(x*log(a)) - logb(x,a)/(a*log(a))*df(a,x);

% Trigonometrical functions.

deflist('((acos simpiden) (asin simpiden) (atan simpiden)
          (acosh simpiden) (asinh simpiden) (atanh simpiden)
          (acot simpiden) (cos simpiden) (sin simpiden) (tan simpiden)
          (sec simpiden) (sech simpiden) (csc simpiden) (csch simpiden)
          (cot simpiden)(acot simpiden)(coth simpiden)(acoth simpiden)
          (cosh simpiden) (sinh simpiden) (tanh simpiden)
          (asec simpiden) (acsc simpiden)
          (asech simpiden) (acsch simpiden)
   ),'simpfn);

% The following declaration causes the simplifier to pass the full
% expression (including the function) to simpiden.

flag ('(acos asin atan acosh acot asinh atanh cos sin tan cosh sinh tanh
        csc csch sec sech cot acot coth acoth asec acsc asech acsch),
      'full);

% flag ('(atan),'oddreal);

flag('(acoth acsc acsch asin asinh atan atanh sin tan csc csch sinh
       tanh cot coth),
     'odd);

flag('(cos sec sech cosh),'even);

flag('(cot coth csc csch),'nonzero);

% In the following rules, it is not necessary to let f(0)=0, when f
% is odd, since simpiden already does this.

% Some value have been commented out since these can be computed from
% other functions.

let cos(0)= 1,
  % sec(0)= 1,
  % cos(pi/12)=sqrt(2)/4*(sqrt 3+1),
    sin(pi/12)=sqrt(2)/4*(sqrt 3-1),
    sin(5pi/12)=sqrt(2)/4*(sqrt 3+1),
  % cos(pi/6)=sqrt 3/2,
    sin(pi/6)= 1/2,
  % cos(pi/4)=sqrt 2/2,
    sin(pi/4)=sqrt 2/2,
  % cos(pi/3) = 1/2,
    sin(pi/3) = sqrt(3)/2,
    cos(pi/2)= 0,
    sin(pi/2)= 1,
    sin(pi)= 0,
    cos(pi)=-1,
    cosh 0=1,
    sech(0) =1,
    sinh(i) => i*sin(1),
    cosh(i) => cos(1),
    acosh(1) => 0,
    acosh(-1) => i*pi
  % acos(0)= pi/2,
  % acos(1)=0,
  % acos(1/2)=pi/3,
  % acos(sqrt 3/2) = pi/6,
  % acos(sqrt 2/2) = pi/4,
  % acos(1/sqrt 2) = pi/4
  % asin(1/2)=pi/6,
  % asin(-1/2)=-pi/6,
  % asin(1)=pi/2,
  % asin(-1)=-pi/2
  ;

for all x let cos acos x=x, sin asin x=x, tan atan x=x,
           cosh acosh x=x, sinh asinh x=x, tanh atanh x=x,
           cot acot x=x, coth acoth x=x, sec asec x=x,
           csc acsc x=x, sech asech x=x, csch acsch x=x;

for all x let acos(-x)=pi-acos(x),
              acot(-x)=pi-acot(x);

% Fold the elementary trigonometric functions down to the origin.

let

 sin( (~~w + ~~k*pi)/~~d)
     => (if evenp fix(k/d) then 1 else -1)
          * sin((w + remainder(k,d)*pi)/d)
      when w freeof pi and ratnump(k/d) and fixp k and abs(k/d) >= 1,

 sin( ~~k*pi/~~d) => sin((1-k/d)*pi)
      when ratnump(k/d) and k/d > 1/2,

 cos( (~~w + ~~k*pi)/~~d)
     => (if evenp fix(k/d) then 1 else -1)
          * cos((w + remainder(k,d)*pi)/d)
      when w freeof pi and ratnump(k/d) and fixp k and abs(k/d) >= 1,

 cos( ~~k*pi/~~d) => -cos((1-k/d)*pi)
      when ratnump(k/d) and k/d > 1/2,

 tan( (~~w + ~~k*pi)/~~d)
     => tan((w + remainder(k,d)*pi)/d)
      when w freeof pi and ratnump(k/d) and fixp k and abs(k/d) >= 1,

 cot( (~~w + ~~k*pi)/~~d)
     => cot((w + remainder(k,d)*pi)/d)
      when w freeof pi and ratnump(k/d) and fixp k and abs(k/d) >= 1;

% The following rules follow the pattern
%   sin(~x + pi/2)=> cos(x) when x freeof pi
% however allowing x to be a quotient and a negative pi/2 shift.
% We need to handleonly pi/2 shifts here because
% the bigger shifts are already covered by the rules above.

let sin((~x + ~~k*pi)/~d) => sign(k/d)*cos(x/d)
         when x freeof pi and abs(k/d) = 1/2,

    cos((~x + ~~k*pi)/~d) => -sign(k/d)*sin(x/d)
         when x freeof pi and abs(k/d) = 1/2,

    tan((~x + ~~k*pi)/~d) => -cot(x/d)
         when x freeof pi and abs(k/d) = 1/2,

    cot((~x + ~~k*pi)/~d) => -tan(x/d)
         when x freeof pi and abs(k/d) = 1/2;

% Inherit function values.

symbolic (!*elem!-inherit := t);

symbolic procedure knowledge_about(op,arg,top);
  % True if the form '(op arg) can be formally simplified.
  % Avoiding recursion from rules for the target operator top by
  % a local remove of the property opmtch.
  % The internal switch !*elem!-inherit!* allows us to turn the
  % inheritage temporarily off.
    if dmode!* eq '!:rd!: or dmode!* eq '!:cr!:
     or null !*elem!-inherit then nil else
    (begin scalar r,old;
       old:=get(top,'opmtch); put(top,'opmtch,nil);
       r:= errorset!*({'aeval,mkquote{op,arg}},nil);
       put(top,'opmtch,old);
       return not errorp r and not smemq(op,car r)
             and not smemq(top,car r);
    end) where varstack!*=nil;

symbolic operator knowledge_about;

symbolic procedure trigquot(n,d);
  % Form a quotient n/d, replacing sin and cos by tan/cot
  % whenver possible.
  begin scalar m,u,w;
    u:=if eqcar(n,'minus) then <<m:=t; cadr n>> else n;
    if pairp u and pairp d then
      if car u eq 'sin and car d eq 'cos and cadr u=cadr d
            then w:='tan else
      if car u eq 'cos and car d eq 'sin and cadr u=cadr d
            then w:='cot;
    if null w then return{'quotient,n,d};
    w:={w,cadr u};
    return if m then {'minus,w} else w;
  end;

symbolic operator trigquot;

% cos, tan, cot, sec, csc inherit from sin.


let cos(~x)=>sin(x+pi/2)
        when (x+pi/2)/pi freeof pi and knowledge_about(sin,x+pi/2,cos),
    cos(~x)=>-sin(x-pi/2)
        when (x-pi/2)/pi freeof pi and knowledge_about(sin,x-pi/2,cos),
    tan(~x)=>trigquot(sin(x),cos(x)) when knowledge_about(sin,x,tan),
    cot(~x)=>trigquot(cos(x),sin(x)) when knowledge_about(sin,x,cot),
    sec(~x)=>1/cos(x) when knowledge_about(cos,x,sec),
    csc(~x)=>1/sin(x) when knowledge_about(sin,x,csc);

% area functions

let asin(~x)=>pi/2 - acos(x) when knowledge_about(acos,x,asin),
    acot(~x)=>pi/2 - atan(x) when knowledge_about(atan,x,acot),
    acsc(~x) => asin(1/x) when knowledge_about(asin,1/x,acsc),
    asec(~x) => acos(1/x) when knowledge_about(acos,1/x,asec),
    acsch(~x) => acsc(-i*x)/i when knowledge_about(acsc,-i*x,acsch),
    asech(~x) => asec(x)/i when knowledge_about(asec,x,asech);

% hyperbolic functions

let sinh(i*~x)=>i*sin(x) when knowledge_about(sin,x,sinh),
    sinh(i*~x/~n)=>i*sin(x/n) when knowledge_about(sin,x/n,sinh),
    cosh(i*~x)=>cos(x) when knowledge_about(cos,x,cosh),
    cosh(i*~x/~n)=>cos(x/n) when knowledge_about(cos,x/n,cosh),
    cosh(~x)=>-i*sinh(x+i*pi/2)
       when (x+i*pi/2)/pi freeof pi
          and knowledge_about(sinh,x+i*pi/2,cosh),
    cosh(~x)=>i*sinh(x-i*pi/2)
       when (x-i*pi/2)/pi freeof pi
          and knowledge_about(sinh,x-i*pi/2,cosh),
    tanh(~x)=>sinh(x)/cosh(x) when knowledge_about(sinh,x,tanh),
    coth(~x)=>cosh(x)/sinh(x) when knowledge_about(sinh,x,coth),
    sech(~x)=>1/cosh(x) when knowledge_about(cosh,x,sech),
    csch(~x)=>1/sinh(x) when knowledge_about(sinh,x,csch);

let acsch(~x) => asinh(1/x) when knowledge_about(asinh,1/x,acsch),
    asech(~x) => acosh(1/x) when knowledge_about(acosh,1/x,asech),
    asinh(~x) => -i*asin(i*x) when i*x freeof i
                   and knowledge_about(asin,i*x,asinh);


% hyperbolic functions

let

 sinh( (~~w + ~~k*pi)/~~d)
      => (if evenp fix(i*k/d) then 1 else -1)
           * sinh((w + remainder(i*k,d)*pi/i)/d)
       when w freeof pi and ratnump(i*k/d) and fixp k and abs(i*k/d)>=1,

 sinh( ~~k*pi/~~d) => sinh((i-k/d)*pi)
       when ratnump(i*k/d) and abs(i*k/d) > 1/2,

 cosh( (~~w + ~~k*pi)/~~d)
      => (if evenp fix(i*k/d) then 1 else -1)
           * cosh((w + remainder(i*k,d)*pi/i)/d)
       when w freeof pi and ratnump(i*k/d) and fixp k and abs(i*k/d)>=1,

 cosh( ~~k*pi/~~d) => -cosh((i-k/d)*pi)
       when ratnump(i*k/d) and abs(i*k/d) > 1/2,

 tanh( (~~w + ~~k*pi)/~~d)
      => tanh((w + remainder(i*k,d)*pi/i)/d)
       when w freeof pi and ratnump(i*k/d) and fixp k and abs(i*k/d)>=1,

 coth( (~~w + ~~k*pi)/~~d)
      => coth((w + remainder(i*k,d)*pi/i)/d)
       when w freeof pi and ratnump(i*k/d) and fixp k and abs(i*k/d)>=1;

% The following rules follow the pattern
%   sinh(~x + i*pi/2)=> cosh(x) when x freeof pi
% however allowing x to be a quotient and a negative i*pi/2 shift.
% We need to handle only pi/2 shifts here because
% the bigger shifts are already covered by the rules above.

let sinh((~x + ~~k*pi)/~d) => i*sign(-i*k/d)*cosh(x/d)
          when x freeof pi and abs(i*k/d) = 1/2,

    cosh((~x + ~~k*pi)/~d) => i*sign(-i*k/d)*sinh(x/d)
          when x freeof pi and abs(i*k/d) = 1/2,

    tanh((~x + ~~k*pi)/~d) => coth(x/d)
          when x freeof pi and abs(i*k/d) = 1/2,

    coth((~x + ~~k*pi)/~d) => tanh(x/d)
          when x freeof pi and abs(i*k/d) = 1/2;


% Transfer inverse function values from cos to acos and tan to atan.
% Negative values not needed.

%symbolic procedure simpabs u;
%   if null u or cdr u then mksq('abs . revlis u, 1)  % error?.
%    else begin scalar x;
%      u := car u;
%      if eqcar(u,'quotient) and fixp cadr u and fixp caddr u
%         and cadr u>0 and caddr u>0 then return simp u;
%      if x := rd!-abs u then return x;
%      u := simp!* u;
%      return if null numr u then nil ./ 1
%             else quotsq(mkabsf1 absf numr u,mkabsf1 denr u)
%  end;

acos_rules :=
  symbolic(
  'list . for j:=0:12 join
    (if eqcar(q,'acos) and cadr q=w then {{'replaceby,q,u}})
     where q=reval{'acos,w}
      where w=reval{'cos,u}
       where u=reval{'quotient,{'times,'pi,j},12})$

let acos_rules;

clear acos_rules;

atan_rules :=
  symbolic(
  'list . for j:=0:5 join
    (if eqcar(q,'atan) and cadr q=w then {{'replaceby,q,u}})
     where q= reval{'atan,w}
      where w= reval{'tan,u}
       where u= reval{'quotient,{'times,'pi,j},12})$

let atan_rules;

clear atan_rules;


repart(pi) := pi$       % $ used for bootstrapping purposes.
impart(pi) := 0$

% ***** Differentiation rules *****.

for all x let df(acos(x),x)= -sqrt(1-x**2)/(1-x**2),
              df(asin(x),x)= sqrt(1-x**2)/(1-x**2),
              df(atan(x),x)= 1/(1+x**2),
              df(acosh(x),x)= sqrt(x**2-1)/(x**2-1),
              df(acot(x),x)= -1/(1+x**2),
              df(acoth(x),x)= -1/(1-x**2),
              df(asinh(x),x)= sqrt(x**2+1)/(x**2+1),
              df(atanh(x),x)= 1/(1-x**2),
              df(acoth(x),x)= 1/(1-x**2),
              df(cos x,x)= -sin(x),
              df(sin x,x)= cos(x),
              df(sec x,x) = sec(x)*tan(x),
              df(csc x,x) = -csc(x)*cot(x),
              df(tan x,x)=1 + tan x**2,
              df(sinh x,x)=cosh x,
              df(cosh x,x)=sinh x,
              df(sech x,x) = -sech(x)*tanh(x),
%              df(tanh x,x)=sech x**2,
              % J.P. Fitch prefers this one for integration purposes
              df(tanh x,x)=1-tanh(x)**2,
              df(csch x,x)= -csch x*coth x,
              df(cot x,x)=-1-cot x**2,
              df(coth x,x)=1-coth x**2;

let df(acsc(~x),x) =>  -1/(x*sqrt(x**2 - 1)),
%   df(asec(~x),x) => 1/(x*sqrt(x**2 - 1)),  % Only true for abs x>1.
    df(asec(~x),x) => 1/(x^2*sqrt(1-1/x^2)),
    df(acsch(~x),x)=> -1/(x*sqrt(1+ x**2)),
    df(asech(~x),x)=> -1/(x*sqrt(1- x**2));

%for all x let e**log x=x;   % Requires every power to be checked.

for all x,y let df(x**y,x)= y*x**(y-1),
                df(x**y,y)= log x*x**y;

% Ei, erf, exp and dilog.

operator dilog,ei,erf,exp;

let {
   dilog(0) => pi**2/6,
   dilog(1) => 0,
   dilog(2) => -pi^2/12,
   dilog(-1) => pi^2/4-i*pi*log(2)
};

for all x let df(dilog x,x)=-log x/(x-1);

for all x let df(ei(x),x)=e**x/x;

let erf 0=0;

for all x let erf(-x)=-erf x;

for all x let df(erf x,x)=2*sqrt(pi)*e**(-x**2)/pi;

for all x let exp(x)=e**x;

% Supply missing argument and simplify 1/4 roots of unity.

let   e**(i*pi/2) = i,
      e**(i*pi) = -1;
%     e**(3*i*pi/2)=-i;

% Ci and si.

operator ci,si;

let {
  df(ci(~x),x) => cos(x)/x,
  df(si(~x),x) => sin(x)/x
};

% Rule for derivative of absolute value.

for all x let df(abs x,x)=abs x/x;

% More trigonometrical rules.

invtrigrules := {
  sin(atan ~u)   => u/sqrt(1+u^2),
  cos(atan ~u)   => 1/sqrt(1+u^2),
  sin(2*atan ~u) => 2*u/(1+u^2),
  cos(2*atan ~u) => (1-u^2)/(1+u^2),
  sin(~n*atan ~u) => sin((n-2)*atan u) * (1-u^2)/(1+u^2) +
                     cos((n-2)*atan u) * 2*u/(1+u^2)
                     when fixp n and n>2,
  cos(~n*atan ~u) => cos((n-2)*atan u) * (1-u^2)/(1+u^2) -
                     sin((n-2)*atan u) * 2*u/(1+u^2)
                     when fixp n and n>2,
  sin(acos ~u) => sqrt(1-u^2),
  cos(asin ~u) => sqrt(1-u^2),
  sin(2*acos ~u) => 2 * u * sqrt(1-u^2),
  cos(2*acos ~u) => 2*u^2 - 1,
  sin(2*asin ~u) => 2 * u * sqrt(1-u^2),
  cos(2*asin ~u) => 1 - 2*u^2,
  sin(~n*acos ~u) => sin((n-2)*acos u) * (2*u^2 - 1) +
                     cos((n-2)*acos u) * 2 * u * sqrt(1-u^2)
                     when fixp n and n>2,
  cos(~n*acos ~u) => cos((n-2)*acos u) * (2*u^2 - 1) -
                     sin((n-2)*acos u) * 2 * u * sqrt(1-u^2)
                     when fixp n and n>2,
  sin(~n*asin ~u) => sin((n-2)*asin u) * (1 - 2*u^2) +
                     cos((n-2)*asin u) * 2 * u * sqrt(1-u^2)
                     when fixp n and n>2,
  cos(~n*asin ~u) => cos((n-2)*asin u) * (1 - 2*u^2) -
                     sin((n-2)*asin u) * 2 * u * sqrt(1-u^2)
                     when fixp n and n>2
%  Next rule causes a simplification loop in solve(atan y=y).
% atan(~x) => acos((1-x^2)/(1+x^2)) * sign (x) / 2
%             when symbolic(not !*complex) and x^2 neq -1
%                   and acos((1-x^2)/(1+x^2)) freeof acos
}$

invhyprules := {
  sinh(atanh ~u)   => u/sqrt(1-u^2),
  cosh(atanh ~u)   => 1/sqrt(1-u^2),
  sinh(2*atanh ~u) => 2*u/(1-u^2),
  cosh(2*atanh ~u) => (1+u^2)/(1-u^2),
  sinh(~n*atanh ~u) => sinh((n-2)*atanh u) * (1+u^2)/(1-u^2) +
                       cosh((n-2)*atanh u) * 2*u/(1-u^2)
                       when fixp n and n>2,
  cosh(~n*atanh ~u) => cosh((n-2)*atanh u) * (1+u^2)/(1-u^2) +
                       sinh((n-2)*atanh u) * 2*u/(1-u^2)
                       when fixp n and n>2,
  sinh(acosh ~u) => sqrt(u^2-1),
  cosh(asinh ~u) => sqrt(1+u^2),
  sinh(2*acosh ~u) => 2 * u * sqrt(u^2-1),
  cosh(2*acosh ~u) => 2*u^2 - 1,
  sinh(2*asinh ~u) => 2 * u * sqrt(1+u^2),
  cosh(2*asinh ~u) => 1 + 2*u^2,
  sinh(~n*acosh ~u) => sinh((n-2)*acosh u) * (2*u^2 - 1) +
                       cosh((n-2)*acosh u) * 2 * u * sqrt(u^2-1)
                       when fixp n and n>2,
  cosh(~n*acosh ~u) => cosh((n-2)*acosh u) * (2*u^2 - 1) +
                       sinh((n-2)*acosh u) * 2 * u * sqrt(u^2-1)
                       when fixp n and n>2,
  sinh(~n*asinh ~u) => sinh((n-2)*asinh u) * (1 + 2*u^2) +
                       cosh((n-2)*asinh u) * 2 * u * sqrt(1+u^2)
                       when fixp n and n>2,
  cosh(~n*asinh ~u) => cosh((n-2)*asinh u) * (1 + 2*u^2) +
                       sinh((n-2)*asinh u) * 2 * u * sqrt(1+u^2)
                       when fixp n and n>2,
  atanh(~x) => acosh((1+x^2)/(1-x^2)) * sign (x) / 2
               when symbolic(not !*complex)
                     and acosh((1+x^2)/(1-x^2)) freeof acosh
}$

let invtrigrules,invhyprules;

trig_imag_rules := {
    sin(i * ~~x / ~~y)   => i * sinh(x/y) when impart(y)=0,
    cos(i * ~~x / ~~y)   => cosh(x/y) when impart(y)=0,
    sinh(i * ~~x / ~~y)  => i * sin(x/y) when impart(y)=0,
    cosh(i * ~~x / ~~y)  => cos(x/y) when impart(y)=0,
    asin(i * ~~x / ~~y)  => i * asinh(x/y) when impart(y)=0,
    atan(i * ~~x / ~~y)  => i * atanh(x/y) when impart(y)=0
                                and not(x=1 and y=1),
    asinh(i * ~~x / ~~y) => i * asin(x/y) when impart(y)=0,
    atanh(i * ~~x / ~~y) => i * atan(x/y) when impart(y)=0
}$

let trig_imag_rules;

% Generalized periodicity rules for trigonometric functions.
% FJW, 16 October 1996.

let {
 cos(~n*pi*arbint(~i) + ~~x) => cos(remainder(n,2)*pi*arbint(i) + x)
  when fixp n,
 sin(~n*pi*arbint(~i) + ~~x) => sin(remainder(n,2)*pi*arbint(i) + x)
  when fixp n,
 tan(~n*pi*arbint(~i) + ~~x) => tan(x) when fixp n,
 sec(~n*pi*arbint(~i) + ~~x) => sec(remainder(n,2)*pi*arbint(i) + x)
  when fixp n,
 csc(~n*pi*arbint(~i) + ~~x) => csc(remainder(n,2)*pi*arbint(i) + x)
  when fixp n,
 cot(~n*pi*arbint(~i) + ~~x) => cot(x) when fixp n
};

endmodule;

end;

