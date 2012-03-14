%***********************************************************************
%*                             INTEGRATION                             *
%***********************************************************************

module definta$

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


transform_lst := '();

algebraic operator f1$
algebraic operator f2$

fluid '(MELLINCOEF);

fluid '(plotsynerr!*);

%***********************************************************************
%*                          MAIN PROCEDURES                            *
%***********************************************************************

symbolic smacro procedure gw u;
  caar u$

symbolic smacro procedure gl u;
  caadar u$

symbolic smacro procedure gk u;
  cdadar u$

symbolic smacro procedure gr u;
  cadar u$

symbolic smacro procedure gm u;
  caadr u$

symbolic smacro procedure gn u;
  cadadr u$

symbolic smacro procedure gp u;
  caddr cadr u$

symbolic smacro procedure gq u;
  cadddr cadr u$

symbolic smacro procedure ga u;
  caddr u$

symbolic smacro procedure gb u;
  cadddr u$

symbolic procedure rdwrap f;
if numberp f then float f
  else if f='pi then 3.141592653589793238462643
  else if f='e then 2.7182818284590452353602987
  else if atom f then f
  else if eqcar(f, '!:RD!:) then
          if atom cdr f then cdr f else bf2flr f
  else if eqcar(f, '!:DN!:) then rdwrap2 cdr f
  else if eqcar(f, 'MINUS) then
    begin scalar x;
       x := rdwrap cadr f;
       return if numberp x then minus float x else {'MINUS, x}
    end
  else if get(car f, 'DNAME) then
    << plotsynerr!*:=t;
       rerror(PLOTPACKAGE, 32, {get(car f, 'DNAME),
                                "illegal domain for PLOT"})
    >>
  else if eqcar(f,'expt) then rdwrap!-expt f
  else rdwrap car f . rdwrap cdr f;

symbolic procedure rdwrap!-expt f;
% preserve integer second argument.
  if fixp caddr f then {'expt!-int,rdwrap cadr f,caddr f}
    else {'expt,rdwrap cadr f, rdwrap caddr f};

symbolic procedure rdwrap2 f;
% Convert from domain to LISP evaluable value.
  if atom f then f else float car f * 10^cdr f;

symbolic procedure rdwrap!* f;
% convert a domain element to float.
  if null f then 0.0 else rdwrap f;

symbolic procedure rdunwrap f;
  if f=0.0 then 0 else if f=1.0 then 1 else '!:rd!: . f;

symbolic procedure expt!-int(a,b); expt(a,fix b);

put('intgg,'simpfn,'simpintgg)$

symbolic procedure simpintgg(u);
<<u:=intggg(car u,cadr u,caddr u,cadddr u);
  simp prepsq u>>;

symbolic procedure intggg(u1,u2,u3,u4);
begin scalar v,v1,v2,s1,s2,s3,coef,uu1,uu2,test_1,test_1a,test_2,m,n,p,
        q,delta,xi,eta,test,temp,temp1,temp2,var,var1,var2;


 off allfac;
 uu1:= cadr u1; uu1:= prepsq cadr(algebraic uu1);
 uu2:= cadr u2; uu2:= prepsq cadr(algebraic uu2);
 u1:=if null cddr u1 then list('f1, uu1) else 'f1 . uu1 . cddr u1;
 u2:=if null cddr u2 then list('f2, uu2) else 'f2 . uu2 . cddr u2;


% Cases for the integration of a single Meijer G-function
 if equal(get('F1,'G),'(1 . 1)) and
                 equal(get('F2,'G),'(1 . 1)) then

         return simp 'UNKNOWN

 else if equal(get('F1,'G),'(1 . 1)) then

% Obtain the appropriate Meijer G-function

    <<s1:=bastab(car u2,cddr u2);

      v:= trpar(car cddddr s1, cadr u2, u4);
      on allfac;
      if v='FAIL then return simp 'FAIL;

% Substitute in the correct variable value

      temp := car cddddr s1;
      var := cadr u2;
      temp := reval algebraic(sub(x=var,temp));

      s1 := {car s1,cadr s1,caddr s1,cadddr s1,temp};

% Ensure by simplification that the variable does not contain a power

      s1 := simp_expt(u3,s1);

      u3 := car s1;
      s1 := cdr s1;

% Test the validity of the following formulae

% 'The Special Functions and their Approximations', Volume 1,
%  Y.L.Luke. Chapter 5.6 page 157 (3),(3*) & (4)

      test_1 := test_1(nil,u3,s1);
      test_1a := test_1('a,u3,s1);
      test_2 := test2(u3,cadr s1,caddr s1);

      m := caar s1;
      n := cadar s1;
      p := caddar s1;
      q := car cdddar s1;

      delta := reval algebraic(m + n - 1/2*(p + q));
      xi := reval algebraic(m + n - p);

      eta := car cddddr s1;
      eta := reval algebraic(eta/u4);

% Test for validity of the integral

      test := reval list('test_cases,m,n,p,q,delta,xi,eta,test_1,
                                                test_1a,test_2);

      if transform_tst = 't then
          test := 't;

      if test neq 'T then
          return simp 'UNKNOWN;

      coef:=simp!* cadddr s1;

      s1:=list(v,car s1,listsq cadr s1,
               listsq caddr s1,simp!*(subpref(cadr u2,1,u4)));
      s3:=addsq(simp!* u3,'(1 . 1));
      RETURN intg(s1,s3,coef)

    >>

 else if equal(get('F2,'G),'(1 . 1)) then

% Obtain the appropriate Meijer G-function

    <<s1:=bastab(car u1,cddr u1);

      v:= trpar(car cddddr s1, cadr u1, u4);
      on allfac;
      if v='FAIL then return simp 'FAIL;

% Substitute in the correct variable value

      temp := car cddddr s1;
      var := cadr u1;
      temp := reval algebraic(sub(x=var,temp));

      s1 := {car s1,cadr s1,caddr s1,cadddr s1,temp};

% Ensure by simplification that the variable does not contain a power

      s1 := simp_expt(u3,s1);

      u3 := car s1;
      s1 := cdr s1;

% Test the validity of the following formulae

% 'The Special Functions and their Approximations', Volume 1,
%  Y.L.Luke. Chapter 5.6 page 157 (3),(3*) & (4)

      test_1 := test_1(nil,u3,s1);
      test_1a := test_1('a,u3,s1);
      test_2 := test2(u3,cadr s1,caddr s1);

      m := caar s1;
      n := cadar s1;
      p := caddar s1;
      q := car cdddar s1;

      delta := reval algebraic(m + n - 1/2*(p + q));
      xi := reval algebraic(m + n - p);

      eta := car cddddr s1;
      eta := reval algebraic(eta/u4);

% Test for validity of the integral

      test := list('test_cases,m,n,p,q,delta,xi,eta,test_1,test_1a,
                                                        test_2);

      test := reval list('test_cases,m,n,p,q,delta,xi,eta,test_1,
                                                test_1a,test_2);

      if transform_tst = 't then
          test := 't;
      if test neq 'T then
         return simp 'UNKNOWN;

      coef:=simp!* cadddr s1;

      s1:=list(v,car s1,listsq cadr s1,
               listsq caddr s1,simp!*(subpref(cadr u1,1,u4)));
      s3:=addsq(simp!* u3,'(1 . 1));
      RETURN intg(s1,s3,coef)

    >>;

% Case for the integration of a product of two Meijer G-functions

% Obtain the correct Meijer G-functions

  s1:=bastab(car u1,cddr u1);

  s2:=bastab(car u2,cddr u2);

  coef:=multsq(simp!* cadddr s1,simp!* cadddr s2);

  v1:= trpar(car cddddr s1, cadr u1, u4);
  if v1='FAIL then
  << on allfac;
     return simp 'FAIL >>;


  v2:= trpar(car cddddr s2, cadr u2, u4);
  if v2='FAIL then
  << on allfac;
     return simp 'FAIL >>;

  on allfac;

% Substitute in the correct variable value

  temp1 := car cddddr s1;
  var1 := cadr u1;
  temp1 := reval algebraic(sub(x=var1,temp1));

  s1 := {car s1,cadr s1,caddr s1,cadddr s1,temp1};

  temp2 := car cddddr s2;
  var2 := cadr u2;
  temp2 := reval algebraic(sub(x=var2,temp2));

  s2 := {car s2,cadr s2,caddr s2,cadddr s2,temp2};

  s1:=list(v1,car s1,listsq cadr s1,
         listsq caddr s1,simp!*(subpref(cadr u1,1,u4)));


  s2:=list(v2,car s2,listsq cadr s2,
         listsq caddr s2,simp!*(subpref(cadr u2,1,u4)));
  s3:=addsq(simp!* u3,'(1 . 1));

  if not numberp(gl s1) or not numberp(gl s2) then
        RETURN simp 'FAIL
  else
  if gl s1<0 then s1:=cong s1 else
  if gl s2<0 then s2:=cong s2 else


  if gl s1=gk s1 then GOTO A  else      % No reduction is necessary if
                                                                     % it is not a meijer G-function
                                                                     % of a power of x
  if gl s2=gk s2 then
                  <<v:=s1;s1:=s2;s2:=v;go to a>>;
                                                                 % No reduction necessary but
                                        % the Meijer G-functions must
                                        % be inverted
  coef:=multsq(coef,invsq gr s1);
                                                          %premultiply by inverse of power
  v:=modintgg(s3,s1,s2);
  s3:=car v;    s1:=cadr v;   s2:=caddr v;
  A:

% Test for validity of the integral


  test := validity_check(s1,s2,u3);

  if test neq 't then
      return simp 'UNKNOWN;

  coef := multsq(if numberp(mellincoef) then simp(mellincoef)
                                     else cadr mellincoef,
              multsq(coef,coefintg(s1,s2,s3)));

  v := deltagg(s1,s2,s3);
  v := redpargf(list(arggf(s1,s2),indgf(s1,s2),car v,cadr v));
  v := ('meijerg . mgretro (cadr v,caddr v,car v));
  v := aeval v;

  if eqcar(v,'!*sq) then
      v := cadr v
  else if fixp v then
      v := simp v;

  if v='FAIL then
      return simp 'FAIL
  else
      return multsq(coef,v);

end$



symbolic procedure mgretro (u,v,w);

  begin scalar caru,carv,cdru,cdrv;

  caru := car u; cdru := cdr u; carv := car v; cdrv := cdr v;

  return
  list('list . cons ('list . foreach aa in caru collect prepsq aa,
              foreach aa in cdru collect prepsq aa),
       'list . cons ('list . foreach aa in carv collect prepsq aa,
              foreach aa in cdrv collect prepsq aa),
       prepsq w);
end;

symbolic procedure intg(u1,u2,u3);
 begin scalar v;
   if numberp(gl(u1)) and gl(u1) < 0 then u1:=cong u1;
   v:=modintg(u2,u1);
   u1:=cadr v;
   v:=
     multlist(
       list(u3,
            expdeg(gw u1,negsq u2),
            quotsq(
               multgamma(
                 append(
                   listplus(car redpar1(gb u1,gm u1),u2),
                   listplus(
                     listmin(car redpar1(ga u1,gn u1)),
                     diff1sq('(1 . 1),u2)))),
               multgamma(
                 append(
                   listplus(cdr redpar1(ga u1,gn u1),u2),
                   listplus(
                     listmin(cdr redpar1(gb u1,gm u1)),
                     diff1sq('(1 . 1),u2)))))));
   return multsq(if numberp(mellincoef) then simp(mellincoef)
                                     else cadr mellincoef,
                 v);
end$

%***********************************************************************
%*              EVALUATION OF THE PARAMETERS FOR THE G-FUNCTION        *
%***********************************************************************

symbolic procedure simp_expt(u,v);

% Reduce Meijer G functions of powers of x to x

begin scalar var,m,n,coef,alpha,beta,alpha1,alpha2,expt_flag,k,temp1,
                  temp2;

var := car cddddr(v);

beta := 1;

% If the Meijer G-function is is a function of a variable which is not
% raised to a power then return initial function

if length var = 0 then
    return u . v

else

<< k := u;
   coef := cadddr v;

   for each i in var do

   << if listp i and not constant_exprp i then
      << if car i = 'expt then
         << alpha := caddr i;
            expt_flag := 't>>

         else if car i = 'sqrt then
         << beta := 2;
            alpha := 1;
            expt_flag := 't>>

         else if car i = 'times then
         << temp1 := cadr i;
            temp2 := caddr i;

            if listp temp1 then
            << if  car temp1 = 'sqrt then
               << beta := 2;
                  alpha1 := 1;
                  expt_flag := 't>>

               else if car temp1 = 'expt and listp caddr temp1 then
                  << beta := cadr cdaddr temp1;
                     alpha1 := car cdaddr temp1;
                            expt_flag := 't>>;
            >>;

            if listp temp2 and car temp2 = 'expt then
            << alpha2 := caddr temp2;
               expt_flag := 't>>;

            if alpha1 neq 'nil then

     alpha := reval algebraic(alpha1 + beta*alpha2)
            else alpha := alpha2;
         >>;

      >>

      else
      << if i = 'expt then
         << alpha := caddr var;
            expt_flag := 't>>;
      >>;
   >>;

% If the Meijer G-function is is a function of a variable which is not
% raised to a power then return initial function

   if expt_flag = nil then
       return u . v

% Otherwise reduce the power by using the following formula :-
%
%   infinity                  infinity
%  /                         /
%  |                       n |
%  |t^alpha*F(t^(m/n))dt = - |z^[((alpha + 1)*n - m)/m]*F(z)dz
%  |                       m |
%  /                         /
% 0                         0

   else

   << if listp alpha then
      << m := cadr alpha;
         n := caddr alpha;
         n := reval algebraic(beta*n)>>

      else
      << m := alpha;
         n := beta>>;

      k := reval algebraic(((k + 1)*n - m)/m);
      coef := reval algebraic((n/m)*coef);
      var := reval algebraic(var^(n/m));

      return {k,car v,cadr v, caddr v,coef,var}>>;

>>;


end;

symbolic procedure test_1(aa,u,v);

% Check validity of the following formulae :=
%
% -min Re{bj} < Re{s} < 1 - max Re{ai}    i=1..n, j=1..m
% -min Re{bj} < Re{s} < 1 - max Re{ai}    i=1..n, j=1..p
%
% 'The Special Functions and their Approximations', Volume 1,
%  Y.L.Luke. Chapter 5.6 page 157 (3) & (3*)

begin scalar s,m,n,a,b,ai,bj,a_max,b_min,temp,temp1,
                !*rounded,dmode!*;

off rounded;

transform_tst := reval algebraic(transform_tst);

if transform_tst neq 't then

<< s := algebraic(repart(1 + u));
   s := simp!* s;

   m := caar v;
   n := cadar v;

   a := cadr v;
   b := caddr v;

   if aa = nil then
   << for i := 1 :n do

      << if car a = 'nil then
              car a := 0;
          ai := append(ai,{car a});
         a := cdr a>>;


      if ai neq 'nil then
      << a_max := simpmax list('list . ai);
         a_max := simprepart list(list('!*sq,a_max,t))>>;

   >>

   else if aa = 'a then

   << if a neq 'nil then
      << a_max := simpmax list('list . a);
         a_max := simprepart list(list('!*sq,a_max,t))>>;
   >>;


   for j := 1 :m do

   << if car b = 'nil then
          car b := 0;
      bj := append(bj,{car b});
      b := cdr b>>;

   if bj neq 'nil then
   << b_min := simpmin list('list . bj);
      b_min := simprepart list(list('!*sq,negsq(b_min),t))>>;


   if a_max neq nil and b_min neq nil then

   << temp := subtrsq(s,diffsq(a_max,1));
      temp1 := subtrsq(b_min,s);

      if car temp = 'nil or car temp1 = 'nil
       or car temp > 0 or car temp1> 0 then
          return 'FAIL

      else
          return test2(s,cadr v,caddr v)>>

   else if a_max = nil then

   << temp := subtrsq(b_min,s);

      if car temp = 'nil or car temp > 0 then
          return 'FAIL

       else
          return 'T>>

   else if b_min = nil then

   << temp :=  subtrsq(s,diffsq(a_max,1));

      if car temp = 'nil or car temp > 0 then
          return 'FAIL

      else
          return 'T>>;

>>

else
<< transform_lst := cons (('tst1 . '(list 'lessp (list 'lessp
        (list 'minus
                (list 'min (list 'repart 'bj))) (list 'repart 's))
        (list 'difference 1
                (list 'max(list 'repart 'ai))))),transform_lst);

   return 't>>;

end;


symbolic procedure test2(s,a,b);

% Check validity of the following formula :=
%
% Re{Sum(ai) - Sum(bj)} + 1/2 * (q + 1 - p) > (q - p) * Re{s}
%                                                        i=1..p, j=1..q
% 'The Special Functions and their Approximations', Volume 1,
%  Y.L.Luke. Chapter 5.6 page 157 (4)

begin scalar s,p,q,sum_a,sum_b,diff_sum,temp1,temp2,temp,diff;

transform_tst := reval algebraic(transform_tst);

if transform_tst neq 't then

<< s := algebraic(repart(1 + s));

   p := length a;
   q := length b;

   for each i in a do << sum_a := reval algebraic(sum_a + i)>>;

   for each j in b do << sum_b := reval algebraic(sum_b + j)>>;

   diff_sum := reval algebraic(repart(sum_a - sum_b));

   temp := reval algebraic(1/2*(q + 1 - p));
   temp1 := reval algebraic(diff_sum + temp);
   temp2 := reval algebraic((q-p)*s);
   diff := simp!* reval algebraic(temp1 - temp2);

   if car diff ='nil then return 'FAIL

   else if car diff < 0 then return 'FAIL else return T>>

else
<< transform_lst := cons (('tst2 . '(list 'greaterp (list 'plus
        (list 'repart (list 'difference (list 'sum 'ai)(list 'sum 'bj)))
        (list 'times (list 'quotient 1 2)
        (list 'plus 'q (list 'difference 1 'p)))) (list 'times
        (list 'difference 'q 'p) (list 'repart 's)))),
         transform_lst);

   return 't;

>>;

end;


symbolic procedure validity_check(s1,s2,u3);

% Check validity of the following formulae :=
%
% 'Integrals and Series, Volume 3, More Special Functions',
%  A.P.Prudnikov, Yu.A.Brychkov, O.I.Marichev. Chapter 2.24.1
%  page 345  (1) - (15)


begin scalar alpha,m,n,p,q,k,l,u,v,delta,epsilon,sigma,omega,r,a,b,c,d,
               b_sum,a_sum,d_sum,c_sum,mu,rho,phi,eta,r1,r2,
         test_1a,test_1b,test_2,test_3,test_4,test_5,test_6,test_7,
         test_8,test_9,test_10,test_11,test_12,test_13,test_14,test_15,
         test;

transform_lst := '();

alpha := reval algebraic(1 + u3);

m := caadr s1;
n := cadadr s1;
p := car cddadr s1;
q := cadr cddadr s1;

epsilon := reval algebraic(m + n - 1/2*(p + q));

k := caadr s2;
l := cadadr s2;
u := car cddadr s2;
v := cadr cddadr s2;

delta := reval algebraic(k + l -1/2*(u + v));

sigma := prepsq caar s1;
omega := prepsq caar s2;

r := prepsq cadar s2;

a := caddr s1;
b := cadddr s1;
c := caddr s2;
d := cadddr s2;

for each i in b do
        << i := prepsq i; b_sum := reval algebraic(b_sum + i)>>;

for each j in a do
        << j := prepsq j; a_sum := reval algebraic(a_sum + j)>>;

for each i in d do
        << i := prepsq i; d_sum := reval algebraic(d_sum + i)>>;

for each j in c do
        << j := prepsq j; c_sum := reval algebraic(c_sum + j)>>;

mu := reval algebraic(b_sum - a_sum + 1/2*(p - q) + 1);
rho := reval algebraic(d_sum - c_sum + 1/2(u - v) + 1);

phi := reval algebraic(q - p - r*(v - u));
eta := reval algebraic(1 - alpha*(v - u) - mu - rho);


if listp r then << r1 := symbolic(cadr r); r2 := symbolic(caddr r)>>
else            << r1 := r; r2 := 1>>;

test_1a := tst1a(m,n,a,b);
test_1b := tst1b(k,l,c,d);
test_2 := tst2(m,k,b,d,alpha,r);
test_3 := tst3(n,l,a,c,alpha,r);
test_4 := tst4(l,p,q,c,alpha,r,mu);
test_5 := tst5(k,p,q,d,alpha,r,mu);
test_6 := tst6(n,u,v,a,alpha,r,rho);
test_7 := tst7(m,u,v,b,alpha,r,rho);
test_8 := tst8(p,q,u,v,alpha,r,mu,rho,phi);
test_9 := tst9(p,q,u,v,alpha,r,mu,rho,phi);
test_10 := tst10(sigma,delta);
test_11 := tst11(sigma,delta);
test_12 := tst12(omega,epsilon);
test_13 := tst13(omega,epsilon);
test_14 := tst14(u,v,alpha,mu,rho,delta,epsilon,sigma,omega,r,phi,r1,
                               r2);
if p = q or u = v then test_15 := 'FAIL
        else test_15 := tst15(m,n,p,q,k,l,u,v,sigma,omega,eta);

test := {'test_cases2,m,n,p,q,k,l,u,v,delta,epsilon,sigma,omega,rho,
   eta,mu,r1,r2,phi,test_1a,test_1b,test_2,test_3,test_4,test_5,test_6,
   test_7,test_8,test_9,test_10,test_11,test_12,test_13,test_14,
   test_15};

test := reval test;

if transform_tst = t and spec_cond neq nil then test := t;
return test;
end;


symbolic procedure tst1a(m,n,a,b);

% Check validity of the following formula :=
%
%  ai - bj neq 1,2,3,....         i=1..n, j=1..m
%
% 'Integrals and Series, Volume 3, More Special Functions',
%  A.P.Prudnikov, Yu.A.Brychkov, O.I.Marichev. Chapter 2.24.1
%  page 345  (1)

begin scalar a_new,b_new,temp,fail_test;

for i := 1 :n do << a_new := append(a_new,{car a}); a := cdr a>>;

for j := 1 :m do << b_new := append(b_new,{car b}); b := cdr b>>;

for each i in a_new do
<< for each j in b_new do
   << temp := subtrsq(i,j);
      if car temp neq 'nil and car temp > 0
          and cdr temp = 1 then
          fail_test := t>>;
>>;
if fail_test = t then return 'FAIL else return t;
end;

symbolic procedure tst1b(k,l,c,d);

% Check validity of the following formula :=
%
%  ci - dj neq 1,2,3,....         i=1..l, j=1..k
%
% 'Integrals and Series, Volume 3, More Special Functions',
%  A.P.Prudnikov, Yu.A.Brychkov, O.I.Marichev. Chapter 2.24.1
%  page 345  (1)

begin scalar c_new,d_new,temp,fail_test;

for i := 1 :l do << c_new := append(c_new,{car c}); c := cdr c>>;

for j := 1 :k do << d_new := append(d_new,{car d}); d := cdr d>>;

for each i in c_new do
<< for each j in d_new do
   << temp := subtrsq(i,j);
      if car temp neq 'nil and car temp > 0
          and cdr temp = 1 then
          fail_test := t>>;
>>;
if fail_test = t then return 'FAIL else return t;
end;

symbolic procedure tst2(m,k,b,d,alpha,r);

% Check validity of the following formula :=
%
%  Re{alpha + r*bi + dj} > 0                 i=1..m, j=1..k
%
% 'Integrals and Series, Volume 3, More Special Functions',
%  A.P.Prudnikov, Yu.A.Brychkov, O.I.Marichev. Chapter 2.24.1
%  page 345  (2)

begin scalar b_new,d_new,temp,temp1,temp2,fail_test;

transform_tst := reval algebraic(transform_tst);

if transform_tst neq t then

<< for i := 1 :m do
   << temp1 := prepsq car b;
      b_new := append(b_new,{temp1});
      b := cdr b>>;

   for j := 1 :k do
   << temp2  := prepsq car d;
      d_new := append(d_new,{temp2});
      d := cdr d>>;

   for each k in b_new do
   << for each h in d_new do
      << temp := simp!* reval algebraic(repart(alpha + r*k + h));
         if car temp = 'nil or car temp < 0 then
             fail_test := 't>>;
   >>;
   if fail_test = t then return 'FAIL else return t>>
else
<< transform_lst := cons (('test2 . '(list 'greaterp
  (list 'repart (list 'plus 'alpha (list 'times 'r 'bi) 'dj))
   0)),transform_lst);
   return t>>;
end;

symbolic procedure tst3(n,l,a,c,alpha,r);

% Check validity of the following formula :=
%
%  Re{alpha + r*ai + cj} < r + 1                  i=1..n, j=1..l
%
% 'Integrals and Series, Volume 3, More Special Functions',
%  A.P.Prudnikov, Yu.A.Brychkov, O.I.Marichev. Chapter 2.24.1
%  page 345  (3)

begin scalar a_new,c_new,temp,temp1,temp2,fail_test;

transform_tst := reval algebraic(transform_tst);

if transform_tst neq 't then

<< for i := 1 :n do
   << temp1 := prepsq car a;
      a_new := append(a_new,{temp1});
      a := cdr a>>;

   for j := 1 :l do
   << temp2 := prepsq car c;
      c_new := append(c_new,{temp2});
      c := cdr c>>;

   for each k in a_new do
   << for each h in c_new do
      << temp := simp!* reval algebraic(repart(alpha + r*k + h)- r -1);
         if car temp = 'nil or car temp > 0 then
             fail_test := 't>>;
   >>;
   if fail_test = 't then return 'FAIL else return t>>
else

<< transform_lst := cons (('test3 . '(list 'lessp (list 'repart
  (list 'plus 'alpha (list 'times 'r 'ai) 'cj)) (list 'plus 'r 1))),
     transform_lst);
   return 't>>;
end;

symbolic procedure tst4(l,p,q,c,alpha,r,mu);

% Check validity of the following formula :=
%
%  (p - q)*Re{alpha + cj - 1} - r*Re{mu} > -3*r/2         j=1..l
%
% 'Integrals and Series, Volume 3, More Special Functions',
%  A.P.Prudnikov, Yu.A.Brychkov, O.I.Marichev. Chapter 2.24.1
%  page 345  (4)

begin scalar c_new,temp1,temp,fail_test;

transform_tst := reval algebraic(transform_tst);

if transform_tst neq 't then
<< for j := 1 :l do
   << temp1 := prepsq car c;
      c_new := append(c_new,{temp1});
      c := cdr c>>;

   for each i in c_new do
   << temp := simp!* reval algebraic((p - q)*repart(alpha + i - 1)
                                                                - r*repart(mu) + 3/2*r);
      if car temp = 'nil or car temp < 0 then fail_test := t;
   >>;

   if fail_test = t then return 'FAIL else return t>>
else
<< transform_lst := cons (('test4 . '(list 'greaterp (list 'difference
 (list 'times (list 'difference 'p 'q) (list 'repart (list 'plus 'alpha
 (list 'difference 'cj 1)))) (list 'times 'r (list 'repart (list 'plus
 (list 'difference (list 'sum 'bj) (list 'sum 'ai))
 (list 'times (list 'quotient 1 2) (list 'difference 'p 'q)) 1))))
 (list 'minus (list 'times 3 (list 'quotient 'r 2))))),transform_lst);
   return 't>>;

end;

symbolic procedure tst5(k,p,q,d,alpha,r,mu);

% Check validity of the following formula :=
%
%  (p - q)*Re{alpha + dj} - r*Re{mu} > -3*r/2         j=1..k
%
% 'Integrals and Series, Volume 3, More Special Functions',
%  A.P.Prudnikov, Yu.A.Brychkov, O.I.Marichev. Chapter 2.24.1
%  page 345  (5)

begin scalar d_new,temp1,temp,fail_test;

transform_tst := reval algebraic(transform_tst);

if transform_tst neq t then

<< for j := 1 :k do
   << temp1 := prepsq car d;
      d_new := append(d_new,{temp1});
      d := cdr d>>;

   for each i in d_new do

   << temp := simp!* reval algebraic((p - q)*repart(alpha + i)
                                                                - r*repart(mu) + 3/2*r);
      if car temp = 'nil or car temp < 0 then fail_test := 't;
   >>;

   if fail_test = t then return 'FAIL else return t>>
else
<< transform_lst := cons (('test5 .'(list 'greaterp (list 'difference
   (list 'times(list 'difference 'p 'q)
   (list 'repart (list 'plus 'alpha 'dj)))
   (list 'times 'r (list 'repart (list 'plus (list 'difference
   (list 'sum 'bj) (list 'sum 'ai)) (list 'quotient
   (list 'difference 'p 'q) 2) 1))))
   (list 'minus (list 'times 3 (list 'quotient 'r 2)))) ),
   transform_lst);
   return t>>;
end;

symbolic procedure tst6(n,u,v,a,alpha,r,rho);

% Check validity of the following formula :=
%
%  (u - v)*Re{alpha + r*ai - r} - Re{rho} > -3/2         i=1..n
%
% 'Integrals and Series, Volume 3, More Special Functions',
%  A.P.Prudnikov, Yu.A.Brychkov, O.I.Marichev. Chapter 2.24.1
%  page 345  (6)

begin scalar a_new,temp1,temp,fail_test;

transform_tst := reval algebraic(transform_tst);

if transform_tst neq 't then
<< for j := 1 :n do

   << temp1 := prepsq car a;
      a_new := append(a_new,{temp1});
      a := cdr a>>;

   for each i in a_new do

   << temp := simp!* reval algebraic((u - v)*repart(alpha + r*i - r)
                                                                              - repart(rho) + 3/2);
      if car temp = 'nil or car temp < 0 then fail_test := 't;
   >>;

  if fail_test = 't then return 'FAIL else return 't>>
else
<< transform_lst := cons (('test6 . '(list 'greaterp (list 'difference
  (list 'times (list 'difference 'u 'v) (list 'repart
  (list 'plus 'alpha (list 'difference (list 'times 'r 'ai) 'r))))
  (list 'repart (list 'plus (list 'difference (list 'sum 'dj)
  (list 'sum 'ci)) (list 'times (list 'quotient 1 2)
  (list 'difference 'u 'v)) 1))) (list 'minus (list 'quotient 3 2)))),
  transform_lst);
   return 't>>;
end;

symbolic procedure tst7(m,u,v,b,alpha,r,rho);

% Check validity of the following formula :=
%
%  (u - v)*Re{alpha + r*bi} - Re{rho} > -3/2         i=1..m
%
% 'Integrals and Series, Volume 3, More Special Functions',
%  A.P.Prudnikov, Yu.A.Brychkov, O.I.Marichev. Chapter 2.24.1
%  page 345  (7)

begin scalar b_new,temp1,temp,fail_test;

transform_tst := reval algebraic(transform_tst);

if transform_tst neq 't then

<< for j := 1 :m do
   << temp1 := prepsq car b;
      b_new := append(b_new,{temp1});
      b := cdr b>>;

   for each i in b_new do

   << temp := simp!* reval algebraic((u - v)*repart(alpha + r*i)
                                                                              - repart(rho) + 3/2);

      if car temp = 'nil or car temp < 0 then fail_test := 't;
   >>;

   if fail_test = t then return 'FAIL else return t>>
else

<< transform_lst := cons (('test7 . '(list 'greaterp (list 'difference
   (list 'times (list 'difference 'u 'v)
   (list 'repart (list 'plus 'alpha (list 'times 'r 'bi))) )
   (list 'repart (list 'plus (list 'difference (list 'sum 'dj)
   (list 'sum 'ci)) (list 'quotient (list 'difference 'u 'v) 2)1)))
   (list 'minus (list 'quotient 3 2)))),transform_lst);
   return 't>>;
end;

symbolic procedure tst8(p,q,u,v,alpha,r,mu,rho,phi);

% Check validity of the following formula :=
%
% abs(phi) + 2*Re{(q - p)*(v - u)*alpha +
%                     r*(v - u)*(mu - 1) + (q - p)*(rho - 1)} > 0
%
% 'Integrals and Series, Volume 3, More Special Functions',
%  A.P.Prudnikov, Yu.A.Brychkov, O.I.Marichev. Chapter 2.24.1
%  page 345  (8)

begin scalar sum,temp,fail_test;

transform_tst := reval algebraic(transform_tst);

if transform_tst neq 't then

<< sum := reval algebraic(2*repart((q - p)*(v - u)*alpha
                                  + r*(v - u)*(mu - 1) + (q - p)*(rho - 1)));

   temp := simp!* reval algebraic(abs phi + sum);
   if car temp = 'nil or car temp < 0 then fail_test := 't;
   if fail_test = t then return 'FAIL else return t>>

else

<< transform_lst := cons (('test8 . '(list 'greaterp (list 'plus
  (list 'abs (list 'difference (list 'difference 'q 'p)
  (list 'times 'r (list 'difference 'v 'u))))
  (list 'times 2 (list 'repart (list 'plus
  (list 'times (list 'difference 'q 'p) (list 'difference 'v 'u)
  'alpha) (list 'times 'r (list 'difference 'v 'u)
  (list 'plus (list 'difference (list 'sum 'bj) (list 'sum 'ai))
  (list 'quotient (list 'difference 'p 'q) 2)))
  (list 'times (list 'difference 'q 'p) (list 'plus
  (list 'difference (list 'sum 'dj) (list 'sum 'ci))
  (list 'quotient (list 'difference 'u 'v) 2)))) )))
  0)),transform_lst);
   return 't>>;

end;

symbolic procedure tst9(p,q,u,v,alpha,r,mu,rho,phi);

% Check validity of the following formula :=
%
% abs(phi) - 2*Re{(q - p)*(v - u)*alpha +
%                     r*(v - u)*(mu - 1) + (q - p)*(rho - 1)} > 0
%
% 'Integrals and Series, Volume 3, More Special Functions',
%  A.P.Prudnikov, Yu.A.Brychkov, O.I.Marichev. Chapter 2.24.1
%  page 345  (9)

begin scalar sum,temp,fail_test;

transform_tst := reval algebraic(transform_tst);

if transform_tst neq 't then

<< sum := reval algebraic(2*repart((q - p)*(v - u)*alpha
                                  + r*(v - u)*(mu - 1) + (q - p)*(rho - 1)));

   temp := simp!* reval algebraic(abs phi - sum);

   if car temp = 'nil or car temp < 0 then fail_test := 't;

   if fail_test = t then return 'FAIL else return t>>
else

<< transform_lst := cons (('test9 . '(list 'greaterp (list 'difference
  (list 'abs (list 'difference (list 'difference 'q 'p)
  (list 'times 'r (list 'difference 'v 'u))))
  (list 'times 2 (list 'repart (list 'plus
  (list 'times (list 'difference 'q 'p) (list 'difference 'v 'u)
  'alpha) (list 'times 'r (list 'difference 'v 'u)
  (list 'plus (list 'difference (list 'sum 'bj) (list 'sum 'ai))
  (list 'quotient (list 'difference 'p 'q) 2)))
  (list 'times (list 'difference 'q 'p) (list 'plus
  (list 'difference (list 'sum 'dj) (list 'sum 'ci))
  (list 'quotient (list 'difference 'u 'v) 2)))) )))
  0)),transform_lst);
   return 't>>;

end;

algebraic procedure tst10(sigma,delta);

% Check validity of the following formula :=
%
% abs(arg sigma) < delta*pi
%
% 'Integrals and Series, Volume 3, More Special Functions',
%  A.P.Prudnikov, Yu.A.Brychkov, O.I.Marichev. Chapter 2.24.1
%  page 345  (10)

begin scalar arg_sigma,pro,temp,fail_test,!*rounded,dmode!*;

if transform_tst neq 't then

<< on rounded;
   arg_sigma := abs(atan(impart sigma/repart sigma));
   pro :=  delta*pi;
   temp :=  pro - arg_sigma;
   if  numberp temp and temp <= 0 then fail_test := t;

   off rounded;

   if fail_test = t then return reval 'FAIL else return reval t>>

else

<<symbolic(transform_lst := cons (('test10 .
'(list 'lessp (list 'abs (list 'arg 'sigma))
(list 'times (list 'plus 'k (list 'difference 'l (list 'quotient
(list 'plus 'u 'v) 2))) 'pi))),transform_lst));
   return reval 't>>;

end;

algebraic procedure tst11(sigma,delta);

% Check validity of the following formula :=
%
% abs(arg sigma) = delta*pi
%
% 'Integrals and Series, Volume 3, More Special Functions',
%  A.P.Prudnikov, Yu.A.Brychkov, O.I.Marichev. Chapter 2.24.1
%  page 345  (11)

begin scalar arg_sigma,pro,fail_test;

if transform_tst neq 't then

<< arg_sigma := abs(atan(impart sigma/repart sigma));
   pro := delta*pi;
   if arg_sigma neq pro then fail_test := 't;
   if fail_test = 't then return reval 'FAIL else return reval 't>>
else

<< symbolic(transform_lst := cons (('test11 .
'(list 'equal (list 'abs (list 'arg 'sigma))
 (list 'times (list 'plus 'k (list 'difference 'l (list 'quotient
(list 'plus 'u 'v) 2))) 'pi))),transform_lst));
   return reval 't>>;

end;

algebraic procedure tst12(omega,epsilon);

% Check validity of the following formula :=
%
% abs(arg omega) < epsilon*pi
%
% 'Integrals and Series, Volume 3, More Special Functions',
%  A.P.Prudnikov, Yu.A.Brychkov, O.I.Marichev. Chapter 2.24.1
%  page 345  (12)

begin scalar arg_omega,pro,temp,fail_test,!*rounded,dmode!*;

if transform_tst neq 't then

<< on rounded;

   arg_omega := abs(atan(impart omega/repart omega));
   pro := epsilon*pi;
   temp := pro - arg_omega;
   if numberp temp and temp <= 0 then fail_test := 't;

   off rounded;

   if fail_test = 't then return reval 'FAIL else return reval 't>>

else

<< symbolic(transform_lst := cons (('test12 .
'(list 'lessp (list 'abs (list 'arg 'omega))
(list 'times (list 'plus 'm (list 'difference 'n
(list 'times (list 'quotient 1 2) (list 'plus 'p 'q))))
'pi))),transform_lst));
   return reval 't>>;

end;

algebraic procedure tst13(omega,epsilon);

% Check validity of the following formula :=
%
% abs(arg omega) = epsilon*pi
%
% 'Integrals and Series, Volume 3, More Special Functions',
%  A.P.Prudnikov, Yu.A.Brychkov, O.I.Marichev. Chapter 2.24.1
%  page 345  (13)

begin scalar arg_omega,pro,fail_test;

if transform_tst neq 't then

<< arg_omega := abs(atan(impart omega/repart omega));
   pro := epsilon*pi;
   if arg_omega neq pro then fail_test := 't;
   if fail_test = t then return reval 'FAIL else return reval 't>>
else

<< symbolic(transform_lst := cons (('test13 .
'(list 'equal (list 'abs (list 'arg 'omega))
(list 'times (list 'plus 'm (list 'difference 'n
(list 'times (list 'quotient 1 2) (list 'plus 'p 'q))))
'pi))),transform_lst));
   return reval 't>>;
end;

algebraic procedure tst14(u,v,alpha,mu,rho,delta,epsilon,sigma,omega,
                                                                                                   r,phi,r1,r2);
% Check validity of the following formula :=
%
%  abs(arg(1 - z*sigma^(-r1)*omega^r2)) < pi
%
%     when phi = 0 and epsilon + r*(delta - 1) <= 0
%
%  where z = r^[r1*(v - u)]*exp[-(r1*delta + r2*epsilon)*pi*i]
%
%   or z = sigma^r1*omega^(-r2)
%     when Re{mu + rho + alpha*(v - u)}
%
% 'Integrals and Series, Volume 3, More Special Functions',
%  A.P.Prudnikov, Yu.A.Brychkov, O.I.Marichev. Chapter 2.24.1
%  page 345  (14)

begin scalar temp,z,arg,arg_test,!*rounded,dmode!*;

if transform_tst neq 't then

<< on rounded;

   temp := epsilon + r *(delta - 1);
   if phi = 0 and temp <= 0 then
       z := r^(r2*(v - u))*e^(-(r2*delta+r1*epsilon)*pi*i)

   else if numberp (mu + rho + alpha*(v - u)) and
                        repart(mu + rho + alpha*(v - u)) < 1 then
       z := sigma^r2*omega^(-r1)
   else return reval 'FAIL; % Wn
   arg := 1 - z*sigma^(-r2)*omega^r1;

   if arg = 0 then arg_test := 0
   else     arg_test := abs(atan(impart arg/repart arg));

   if numberp arg_test and arg_test < pi then
                << off rounded; return reval 't>>
   else     << off rounded; return reval 'FAIL>>;
>>

else
<< symbolic(transform_lst := cons (('test14 .'(list 'or (list 'and
  (list 'abs (list 'arg (list 'difference 1 (list 'times
  (list 'times (list 'expt 'r (list 'times 'r1
  (list 'difference 'v 'u))) (list 'exp (list 'minus
  (list 'times (list 'plus (list 'times 'r1 (list 'plus 'k
  (list 'difference 'l (list 'times (list 'quotient 1 2)
  (list 'plus 'u 'v)))) ) (list 'times 'r2 (list 'plus 'm
  (list 'difference 'n (list 'times (list 'quotient 1 2)
  (list 'difference 'p 'q)))) )) 'pi 'i))))
  (list 'expt 'sigma (list 'minus 'r1)) (list 'expt 'omega 'r2)))) )
  (list 'equal 'phi 0) (list 'leq (list 'plus 'k (list 'difference 'l
  (list 'times (list 'quotient 1 2) (list 'plus 'u 'v)))
  (list 'times 'r (list 'plus 'm (list 'difference (list 'difference 'n
  (list 'times (list 'quotient 1 2) (list 'plus 'p 'q))) 1)))) 0))
  (list 'and (list 'lessp (list 'repart (list 'plus
  (list 'difference (list 'sum 'bj) (list 'sum 'ai))
  (list 'times (list 'quotient 1 2) (list 'difference 'p 'q)) 1
  (list 'difference (list 'sum 'dj) (list 'sum 'ci))
  (list 'times (list 'quotient 1 2) (list 'difference 'u 'v)) 1
  (list 'times 'alpha (list 'difference 'v 'u)))) 0)
  (list 'equal 'phi 0) (list 'leq (list 'plus 'k (list 'difference 'l
  (list 'times (list 'quotient 1 2) (list 'plus 'u 'v)))
  (list 'times 'r (list 'plus 'm (list 'difference (list 'difference 'n
  (list 'times (list 'quotient 1 2) (list 'plus 'p 'q))) 1)))) 0)))),
                                        transform_lst));

   return reval 't>>;

end;

algebraic procedure tst15(m,n,p,q,k,l,u,v,sigma,omega,eta);

begin scalar lc,ls,temp_ls,psi,theta,arg_omega,arg_sigma,
        !*rounded,dmode!*;

if transform_tst neq 't then

<< arg_omega := atan(impart omega/repart omega);
   arg_sigma := atan(impart sigma/repart sigma);

   psi := (abs arg_omega + (q - m - n)*pi)/(q - p);
   theta := (abs arg_sigma + (v - k - l)*pi)/(v - u);


   lc := (q - p)*abs(omega)^(1/(q - p))*cos psi +
                             (v - u)*abs(sigma)^(1/(v - u))*cos theta;

   lc := lc;
   temp_ls := (q - p)*abs(omega)^(1/(q - p))*sign(arg_omega)*sin psi +
          (v - u)*abs(sigma)^(1/(v - u))*sign(arg_sigma)*sin theta;

   if arg_sigma*arg_omega neq 0 then ls := temp_ls
        else return reval 'fail;

   on rounded;
   if (numberp lc and lc > 0) or lc = 0 and ls = 0 and repart eta > -1
                  or lc = 0 and ls = 0 and repart eta > 0 then
   << off rounded; return reval 't>>

   else         << off rounded; return reval 'fail>>
>>

else
<< symbolic(transform_lst := cons (('test15 . '(list 'or
(list 'greaterp 'lambda_c 0) (list 'and (list 'equal 'lambda_c 0)
(list 'neq 'lambda_s 0) (list 'greaterp (list 'repart 'eta)
(list 'minus 1))) (list 'and (list 'equal 'lambda_c 0)
(list 'equal 'lambda_s 0) (list 'greaterp (list 'repart 'eta) 0)))),
                                        transform_lst));
return reval 't>>;

end;

symbolic procedure bastab(u,v);
 if u eq 'f1 then subpar(get('f1,'g),v) else
 if u eq 'f2 then subpar(get('f2,'g),v)$

symbolic procedure subpar(u,v);
if null v then list(cadr u,caddr u, cadddr u,car cddddr u,
                    cadr cddddr u)
  else list(cadr u,sublist1(caddr u,v,car u),
          sublist1(cadddr u,v,car u),
          subpref1(car cddddr u,v,car u),cadr cddddr u)$

symbolic procedure sublist1(u,v,z);
 % u,v,z - list PF.
 if null cdr v or null cdr z then sublist(u,car v,car z)
   else
    sublist1(
      sublist(u,car v,car z),
      cdr v,cdr z)$

symbolic procedure subpref1(u,v,z);
% u - pf
% v,z - list pf
 if null cdr v or null cdr z then subpref(u,car v,car z)
   else subpref(subpref1(u,cdr v,cdr z),car v,car z)$

symbolic procedure subpref(u,v,z);
% u,v,z - pf
 prepsq subsqnew(simp!* u,simp!* v,z)$

symbolic procedure sublist(u,v,z);
% u - list pf
% v,z - pf
if null u then nil else
     subpref(car u,v,z) . sublist(cdr u,v,z)$

symbolic procedure trpar(u1,u2,u3);
  if not numberp u2 and not atom u2 and car(u2)='plus then 'FAIL else
  begin scalar a!3,l!1,v1,v2,v3,v4;
   if (v1:=dubdeg(car simp u1,'x))='FAIL or
           (v2:=dubdeg(cdr simp u1,'x))='FAIL or
           (v3:=dubdeg(car simp u2,u3))='FAIL or
           (v4:=dubdeg(cdr simp u2,u3))='FAIL then return 'FAIL;
   a!3:=multsq(diff1sq(v1,v2), diff1sq(v3,v4));
   l!1:=subpref(u1,u2,'x);
   l!1:=subpref(l!1,1,u3);
   return list(simp!*(l!1),a!3);
  end$

symbolic procedure modintgg(u1,u2,u3);
 list(
    multsq(u1,invsq gr u2),
    change(u2,list(cons(gw u2,list '(1 . 1))),'(1)),
    change(u3,list(cons(gw u3,list(quotsq(gr u3,gr u2)))),'(1)))$

symbolic procedure change(u1,u2,u3);
 begin scalar v;integer k;
  while u1 do begin
   if u3 and car u3=(k:=k+1) then
            << v:=append(v,list car u2);
               if u2 then u2:=cdr u2;
               if u3 then u3:=cdr u3
            >>
     else
      v:=append(v,list car u1);
   u1:=cdr u1;
   if null u3 then << v:= append(v,u1); u1:= nil>>; %WN
  end;
  return v;
end$

symbolic procedure cong(u);
 list(
   list(invsq gw u,negsq gr u),
   list(gn u,gm u,gq u,gp u),
   difflist(listmin gb u,'(-1 . 1)),
   difflist(listmin ga u,'(-1 . 1)))$

symbolic procedure modintg(u1,u2);
 list(
  multsq(u1,invsq gr u2),
  change(u2,
    list(
      cons(gw u2,list '(1 . 1))),'(1)))$

symbolic procedure ccgf(u);
  quotsq(
     simp(2 * gm u + 2 * gn u - gp u - gq u),
     '(2 . 1))$

symbolic procedure vgg(u1,u2);
  diff1sq(
     simp(gq u2 - gp u2),
     multsq(gr u2,simp(gq u1 - gp u1)))$

symbolic procedure nugg(u1,u2,u3);
  diff1sq( diff1sq('(1 . 1), multsq(u3, simp(gq u1 - gp u1))),
           addsq(mugf u2,mugf u1))$

symbolic smacro procedure sumlistsq(u);
<< for each pp in u do <<p := addsq(pp,p)>>; p>> where p = '(nil . 1);

symbolic procedure mugf(u);
  addsq(
     quotsq(simp(2 + gp u - gq u),'(2 . 1)),
     addsq(sumlistsq gb u,negsq sumlistsq ga u))$

symbolic procedure coefintg(u1,u2,u3);
  multlist(
   list(
     expdeg(gk u2 . 1,mugf u2),
     expdeg(gl u2 . 1,
       addsq(mugf u1,
         diff1sq(
            multsq(u3,(gq u1-gp u1) . 1),
            '(1 . 1)))),
     expdeg(gw u1,negsq u3),
     expdeg(simp '(times 2 pi),
       addsq(multsq(ccgf u1,(1-gl u2) . 1),
             multsq(ccgf u2,(1-gk u2) . 1)))))$

% The procedure name "delta" had been changed to "defint_delta" to avoid
% clashed with the cantens package.

symbolic procedure deltagg(u1,u2,u3);
  list(
     append( defint_delta(car redpar1(ga u2,gn u2), gk u2),
      append(
        defint_delta( difflist( listmin gb u1, addsq(u3,'(-1 . 1))), gl u2),
        defint_delta( cdr redpar1(ga u2,gn u2), gk u2))),
     append( defint_delta(car redpar1(gb u2,gm u2), gk u2),
      append(defint_delta( difflist(listmin ga u1,addsq(u3,'(-1 . 1))),gl u2),
        defint_delta( cdr redpar1(gb u2,gm u2), gk u2))))$

symbolic procedure redpargf(u);
 begin scalar v1,v2;
  v1:=redpar(car redpar1(gb u,gm u), cdr redpar1(ga u,gn u));
  v2:=redpar(cdr redpar1(gb u,gm u), car redpar1(ga u,gn u));

  return
    list(car u, (cadr v2 . cadr v1), (car v1 . car v2));

end$

symbolic procedure arggf(u1,u2);

% Calculate the coefficient of the variable in the combined meijerg
% function

 multlist(list(
     expdeg(gw u2, gk u2 . 1),
     expdeg(gk u2 . 1, (gk u2 * gp u2 - gk u2 * gq u2) . 1),
     invsq(expdeg(gw u1, gl u2 . 1)),
     expdeg(gl u2 . 1,(gl u2 * gq u1 - gl u2 * gp u1) . 1)))$

symbolic procedure indgf(u1,u2);

% Calculate the values of m,n,p,q of the combined meijerg function

 list(gk u2 * gm u2 + gl u2 * gn u1,
      gk u2 * gn u2 + gl u2 * gm u1,
      gk u2 * gp u2 + gl u2 * gq u1,
      gk u2 * gq u2 + gl u2 * gp u1)$

symbolic procedure dubdeg(x,y);
% x -- SF.
% y -- atom.
begin scalar c,b,a1,a3;
if numberp x or null x then return '(nil . 1);
if not null cdr(x) then return 'FAIL;
lb1: a1:=caar x;a3:=car a1;
  if atom a3 and a3=y then b:=cdr a1 . 1 ;
  if not atom a3 then
    if cadr a3=y then
     if null cddr(a3) then return 'FAIL else
       if not nump(simp caddr a3) then return simp(caddr a3)
                                else
          c:=times(cdr a1,cadr caddr a3).caddr caddr a3;
  if atom cdar x then
    if null b then
      if null c then return '(nil . 1)
      else return c
    else
    if null c then return b
    else return plus(times(car b,cdr c),car c).cdr c;
  x:=cdar x;go to lb1;
end$

symbolic procedure defint_delta(u,n);
  % u -- list of sq.
  % n -- number.
  if null u then nil else
    append(if n=1 then list car u else
             delta0(quotsq(car u,simp!* n),n,n)
          ,defint_delta(cdr u,n))$

symbolic procedure delta0(u,n,k);
  % u -- SQ.
  % n,k -- numbers.
   if k=0 then nil else
       u . delta0(addsq(u,invsq(simp!* n)),n,k-1)$

symbolic procedure nump(x);
 or(null car x,and(numberp car x,numberp cdr x))$


endmodule;

end;



