module jsymbols;

% Author: Matthew Rebbeck, ZIB.
% Advisor: Rene' Grognard.

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


% Date:   March 1994

% Version 1 (experimental code for the symbolic 6j symbol)
%            by Winfried Neun (ZIB) email: neun@zib-berlin.de

% ThreeJSymbol with reasoning on the input for optimal computing;
% Note: the code is 'pedestrian' but should be transparent and it
% seems to work !
% It reflects strongly the exploratory code I used to orientate myself
% It should be rewritten in a more 'professional' style;

load!-package 'matrix;   % Needed for matrix input.
load!-package 'solve;
load!-package 'ineq;

symbolic procedure clean_up_sqrt(input);
  %
  %  Takes input and factorises out all sqrt's.
  %
  begin
    scalar num,denom,answer;
    if not pairp input or car input neq 'quotient then answer := input
     else <<
      num := cadr input;
      denom := caddr input;
      num := collect_sqrt(num);
      denom := collect_sqrt(denom);
      answer := {'quotient,num,denom}; >>;
    return answer;
  end;

flag('(clean_up_sqrt),'opfn);


symbolic procedure collect_sqrt(input);
  %
  %  Cleans up a series of multiplied sqrt's.
  %  eg: sqrt(x)*sqrt(y)->sqrt_of(x*y).
  %
  begin
    scalar sqrt_args,extra_bit,minust,answer;
    sqrt_args := {}; extra_bit := {};
    if not pairp input then answer := input
     else <<
      if car input = 'minus then
                <<minust := t;
                  if pairp cadr input then input := cadr input
                                        else return input
                >>;
      if car input='times then input := cdr input else input := {input};
      for each elt in input do

      << if eqcar(elt,'sqrt)
          then sqrt_args := append(sqrt_args,{cadr elt})
           else extra_bit := append(extra_bit,{elt}); >>;
    if sqrt_args = {} then <<answer := extra_bit;>>
     else <<
       sqrt_args:=reval append({'sqrt_of},{append({'times},sqrt_args)});
       answer := append({sqrt_args},extra_bit); >>;
     answer := append({'times},answer);
    if minust then answer := {'minus,answer}; >>;
    return answer;

end;

symbolic operator listp;

algebraic operator sqrt_of, oddexpt;

algebraic << let sqrt_of(~x) => sqrt (x) when numberp x >>;


algebraic procedure ThreeJSymbol (u1,u2,u3);
 if listp u1 and listp u2 and listp u3 then
  begin
   scalar j1,j2,j3,m1,m2,m3,tmp,lower,upper,better,best;
   matrix range(6,2);
   j1:=first u1; m1:=second u1;
   j2:=first u2; m2:=second u2;
   j3:=first u3; m3:=second u3;

   % Vanishing conditions;

   if numberp(tmp:=m1+m2+m3) and tmp neq 0 then return 0
   else if numberp(tmp:=j1+j2+j3) and tmp < -1 then return 0
   else if numberp(tmp:=j1+j2-j3) and tmp < 0 then return 0
   else if numberp(tmp:=j1-j2+j3) and tmp < 0 then return 0
   else if numberp(tmp:=j2+j3-j1) and tmp < 0 then return 0
   else if numberp(tmp:=j1+m1) and tmp < 0 then return 0
   else if numberp(tmp:=j1-m1) and tmp < 0 then return 0
   else if numberp(tmp:=j2+m2) and tmp < 0 then return 0
   else if numberp(tmp:=j2-m2) and tmp < 0 then return 0
   else if numberp(tmp:=j3+m3) and tmp < 0 then return 0
   else if numberp(tmp:=j3-m3) and tmp < 0 then return 0
   else

   % Restrictions on k

   <<range:=mat((0,infinity),
                (0,infinity),
                (0,infinity),
                (0,infinity),
                (0,infinity),
                (0,infinity));
     % as is;

     lower:=range(1,1);upper:=range(1,2);
     if numberp(tmp:=j1+j2-j3) then upper:=tmp;
     if numberp(tmp:=j1-m1) then
           if upper = infinity then upper:=tmp
            else upper:=min(upper,tmp);
     if numberp(tmp:=j2+m2) then
           if upper = infinity then upper:=tmp
            else upper:=min(upper,tmp);
     if numberp(tmp:=j3+j2+m1) then lower = max(lower,-tmp);
     if numberp(tmp:=j2-j1-m2) then lower = max(lower,-tmp);
     range(1,1):=lower;range(1,2):=upper;
     if upper = infinity then <<better:=upper;best:=0>>
     else <<better:=upper-lower+1;best:=1>>;

     % {j1,m1} <-> {j2,m2};

     lower:=range(2,1);upper:=range(2,2);
     if numberp(tmp:=j2+j1-j3) then upper:=tmp;
     if numberp(tmp:=j2-m2) then
                 if upper = infinity then
                         upper:=tmp else upper:=min(upper,tmp);
     if numberp(tmp:=j1+m1) then
                 if upper = infinity then
                         upper:=tmp else upper:=min(upper,tmp);
     if numberp(tmp:=j3+j1+m2) then lower = max(lower,-tmp);
     if numberp(tmp:=j1-j2-m1) then lower = max(lower,-tmp);
     range(2,1):=lower;range(2,2):=upper;
     if upper neq infinity then
      << tmp:=upper-lower+1;
      if (better = infinity) or (better > tmp) then
                         << better:=tmp;best:=2 >> >>;

     % {j1,m1} <-> {j3,m3};

     lower:=range(3,1);upper:=range(3,2);
     if numberp(tmp:=j3+j2-j1) then upper:=tmp;
     if numberp(tmp:=j3-m3) then
                if upper = infinity then
                         upper:=tmp else upper:=min(upper,tmp);
     if numberp(tmp:=j2+m2) then
                if upper = infinity then
                        upper:=tmp else upper:=min(upper,tmp);
     if numberp(tmp:=j1+j2+m3) then lower = max(lower,-tmp);
     if numberp(tmp:=j2-j3-m2) then lower = max(lower,-tmp);
     range(3,1):=lower;range(3,2):=upper;
     if upper neq infinity then
      << tmp:=upper-lower+1;
      if (better = infinity) or (better > tmp) then
                 << better:=tmp;best:=3 >> >>;

     % {j2,m2} <-> {j3,m3};

     lower:=range(4,1);upper:=range(4,2);
     if numberp(tmp:=j1+j3-j2) then upper:=tmp;
     if numberp(tmp:=j1-m1) then
                if upper = infinity then
                        upper:=tmp else upper:=min(upper,tmp);
     if numberp(tmp:=j3+m3) then
                if upper = infinity then
                        upper:=tmp else upper:=min(upper,tmp);
     if numberp(tmp:=j2+j3+m1) then lower = max(lower,-tmp);
     if numberp(tmp:=j3-j1-m3) then lower = max(lower,-tmp);
     range(4,1):=lower;range(4,2):=upper;
     if upper neq infinity then
      << tmp:=upper-lower+1;
      if (better = infinity) or (better > tmp) then
                         << better:=tmp;best:=4 >> >>;

     % {j1,m1} -> {j2,m2} -> {j3,m3} -> {j1,m1};

     lower:=range(5,1);upper:=range(5,2);
     if numberp(tmp:=j2+j3-j1) then upper:=tmp;
     if numberp(tmp:=j2-m2) then
                if upper = infinity then
                        upper:=tmp else upper:=min(upper,tmp);
     if numberp(tmp:=j3+m3) then
                if upper = infinity
                        then upper:=tmp else upper:=min(upper,tmp);
     if numberp(tmp:=j1+j3+m2) then lower = max(lower,-tmp);
     if numberp(tmp:=j3-j2-m3) then lower = max(lower,-tmp);
     range(5,1):=lower;range(5,2):=upper;
     if upper neq infinity then
      << tmp:=upper-lower+1;
      if (better = infinity) or (better > tmp) then
                         << better:=tmp;best:=5 >> >>;

     % {j1,m1} -> {j3,m3} -> {j2,m2} -> {j1,m1};

     lower:=range(6,1);upper:=range(6,2);
     if numberp(tmp:=j3+j1-j2) then upper:=tmp;
     if numberp(tmp:=j3-m3) then
                if upper = infinity then
                        upper:=tmp else upper:=min(upper,tmp);
     if numberp(tmp:=j1+m1) then
                if upper = infinity then
                        upper:=tmp else upper:=min(upper,tmp);
     if numberp(tmp:=j2+j1+m3) then lower = max(lower,-tmp);
     if numberp(tmp:=j1-j3-m1) then lower = max(lower,-tmp);
     range(6,1):=lower;range(6,2):=upper;
     if upper neq infinity then
      << tmp:=upper-lower+1;
      if (better = infinity) or (better > tmp) then
                << better:=tmp;best:=6 >> >>;

     if best = 1 then return !*3j!-symbol!:sign!*(j1-j2-m3)
      * clean_up_sqrt(
         for k:=range(best,1):range(best,2) sum
          if evenp(k)
            then !*3j!-symbol!:one!-term!*(k,j1,j2,j3,m1,m2,m3)
           else  - !*3j!-symbol!:one!-term!*(k,j1,j2,j3,m1,m2,m3))

     else if best = 2 then
        return !*3j!-symbol!:sign!*((j1+j2+j3)+j2-j1-m3)
         * clean_up_sqrt(
         for k:=range(best,1):range(best,2) sum
          if evenp(k)
            then !*3j!-symbol!:one!-term!*(k,j2,j1,j3,m2,m1,m3)
           else  - !*3j!-symbol!:one!-term!*(k,j2,j1,j3,m2,m1,m3))

     else if best = 3 then
        return !*3j!-symbol!:sign!*((j1+j2+j3)+j3-j2-m1)
         * clean_up_sqrt(
         for k:=range(best,1):range(best,2) sum
          if evenp(k)
            then !*3j!-symbol!:one!-term!*(k,j3,j2,j1,m3,m2,m1)
           else  - !*3j!-symbol!:one!-term!*(k,j3,j2,j1,m3,m2,m1))

     else if best = 4 then
        return !*3j!-symbol!:sign!*((j1+j2+j3)+j1-j3-m2)
         * clean_up_sqrt(
         for k:=range(best,1):range(best,2) sum
          if evenp(k)
            then !*3j!-symbol!:one!-term!*(k,j1,j3,j2,m1,m3,m2)
           else - !*3j!-symbol!:one!-term!*(k,j1,j3,j2,m1,m3,m2))

     else if best = 5 then
        return !*3j!-symbol!:sign!*(j2-j3-m1) * clean_up_sqrt(
         for k:=range(best,1):range(best,2) sum
          if evenp(k)
            then !*3j!-symbol!:one!-term!*(k,j2,j3,j1,m2,m3,m1)
           else - !*3j!-symbol!:one!-term!*(k,j2,j3,j1,m2,m3,m1))

     else if best = 6 then
        return !*3j!-symbol!:sign!*(j3-j1-m2) * clean_up_sqrt(
         for k:=range(best,1):range(best,2) sum
          if evenp(k)
            then !*3j!-symbol!:one!-term!*(k,j3,j1,j2,m3,m1,m2)
           else  - !*3j!-symbol!:one!-term!*(k,j3,j1,j2,m3,m1,m2))

     else return lisp lpri list("ThreeJSymbol({",
                               second u1,",",third u1,
                               "},{",
                               second u2,",",third u2,
                               "},{",
                               second u3,",",third u3,
                               "}) % symbol best left as is;")
   >>
  end
 else
  " the argument must be of the form: {j1,m1},{j2,m2},{j3,m3}"$

algebraic procedure !*3j!-symbol!:sign!* u;
   if rounded then (-1)^u else (-1)^(remainder(num u,2*den u)/(den u))$

algebraic procedure !*3j!-symbol!:one!-term!*(k,j1,j2,j3,m1,m2,m3);
 sqrt(simplify_factorial(
  (  factorial(j1+j2-j3)
    *factorial(j3+j1-j2)
    *factorial(j2+j3-j1)
    *factorial(j1+m1)
    *factorial(j1-m1)
    *factorial(j2+m2)
    *factorial(j2-m2)
    *factorial(j3+m3)
    *factorial(j3-m3)
  )/(factorial(j1+j2+j3+1)
    *(factorial(k)
     *factorial(j1+j2-j3-k)
     *factorial(j3-j2+m1+k)
     *factorial(j3-j1-m2+k)
     *factorial(j1-m1-k)
     *factorial(j2+m2-k)
     )^2
  )
 ))$

algebraic <<

 operator clebsch_gordan;

let clebsch_gordan({~j1,~m1},{~j2,~m2},{~j3,~m3}) =>
       ThreeJSymbol ({~j1,~m1},{~j2,~m2},{~j3, - ~m3}) *
       (2*j3+1)^(1/2) * (-1)^(j1-j2+m3);

% The 6 J symbol
% The naming of the functions follows Landolt-Boernstein

operator SixJSymbol;

let { SixJSymbol({~j1,~j2,~j3} , {~l1,~l2,~l3}) =>
       (begin scalar nnn,mmm,!*factor,!*exp,signum;
       % necessary conditions for existence

       if (necess_6j (j1,j2,j3,l1,l2,l3) = nil) then return nil;
       on factor;
       mmm := Racah_W(j1,j2,j3,l1,l2,l3);
       nnn :=
        square_Racah_delta(j1,j2,j3) * square_Racah_delta(j1,l2,l3)*
        square_Racah_delta(l1,j2,l3) * square_Racah_delta(l1,l2,j3)
        * mmm^2;
       nnn := simplify_factorial (nnn);
       signum := sign mmm;
       if numberp signum then return (signum * sqrt nnn)
          else return (sign nnn * sqrt nnn); end)};

procedure square_Racah_delta(a,b,c);
  simplify_factorial(factorial(a+b-c) *factorial(a-b+c)
                * factorial(-a +b +c) / factorial (a + b + c +1));

procedure Racah_W(j1,j2,j3,l1,l2,l3);
 begin scalar mini,maxi,interv;

    mini := min(j1+j2+l1+l2,j2+j3+l2+l3,j3+j1+l3+l1);
    maxi := max(0,j1+j2+j3,j1+l2+l3,l1+j2+l3,l1+l2+j3);

  aaa:
    if numberp (mini - maxi) then
        return for k:=maxi:mini sum
               ((-1)^k*simplify_factorial (factorial (k+1) /
               (factorial (k-j1-j2-j3)*
                factorial (k-j1-l2-l3) *
                factorial (k-l1-j2-l3) *
                factorial (k-l1-l2-j3)*
                factorial (j1+j2+l1+l2-k)*
                factorial (j2+j3+l2+l3-k)*
                factorial (j3+j1+l3+l1-k))))
        else <<
             interv :=findinterval (j1,j2,j3,l1,l2,l3);
             if interv = {} then
              return
               sum((-1)^k* simplify_factorial
                (factorial (k+1) /
               (factorial (k-j1-j2-j3)*
                factorial (k-j1-l2-l3) *
                factorial (k-l1-j2-l3) *
                factorial (k-l1-l2-j3)*
                factorial (j1+j2+l1+l2-k)*
                factorial (j2+j3+l2+l3-k)*
                factorial (j3+j1+l3+l1-k))),k,maxi,mini)
             else        <<
               maxi := first first interv;
               mini := second first interv + maxi;
               go to aaa >>;
            >>;
 end;

% conditions for non vanishing 6j symbol see Landolt/Boernstein

procedure necess_6j(j1,j2,j3,l1,l2,l3);

 begin scalar nnn, !*rounded, dmode!*;
     off rounded;
     nnn := j1 + j2 + j3;
     if (numberp nnn) then if not fixp nnn then return nil;
     nnn := l1 + l2 + j3;
     if (numberp nnn) then if not fixp nnn then return nil;
     nnn := j1 + l2 +l3;
     if (numberp nnn) then if not fixp nnn then return nil;
     nnn := l1 + j2 +l3;
     if (numberp nnn) then if not fixp nnn then return nil;

     if not numberp j1 or  not numberp j2 or  not numberp j3
        or not numberp l1 or not numberp l2 or not numberp l3
        then return t; % don't know

 % Triangle condtions

     if  j1 + j2 - j3 >=0 and   j1 - j2 + j3 >=0 and
        -j1 + j2 + j3 >=0 and   l1 + l2 - j3 >=0 and
         l1 - l2 + j3 >=0 and  -l1 + l2 + j3 >=0 and
         j1 + l2 - l3 >=0 and   j1 - l2 + l3 >=0 and
        -j1 + l2 + l3 >=0 and   l1 + j2 - l3 >=0 and
         l1 - j2 + l3 >=0 and  -l1 + j2 + l3 >=0 then return t;
    end;

procedure aconds!-6j(j1,j2,j3,l1,l2,l3);

   { % Triangle condtions
     j1 + j2 - j3 >=0,      j1 - j2 + j3 >=0,
    -j1 + j2 + j3 >=0,
     l1 + l2 - j3 >=0,      l1 - l2 + j3 >=0,
    -l1 + l2 + j3 >=0,
     j1 + l2 - l3 >=0,      j1 - l2 + l3 >=0,
    -j1 + l2 + l3 >=0,
     l1 + j2 - l3 >=0,      l1 - j2 + l3 >=0,
    -l1 + j2 + l3 >=0,

     % condtions for the summation index

     !=k +1 >= 0,
     !=k-j1-j2-j3 >=0,
     !=k-j1-l2-l3 >=0,
     !=k-l1-j2-l3 >=0,
     !=k-l1-l2-j3 >=0,
     j1+j2+l1+l2-!=k >=0,
     j2+j3+l2+l3-!=k >=0,
     j3+j1+l3+l1-!=k >=0};

% same in Edmonds formulation

procedure conds!-6j(j1,j2,j3,l1,l2,l3);

   { % Triangle condtions
     j1 + j2 - j3 >=0,      j1 - j2 + j3 >=0,
    -j1 + j2 + j3 >=0,
     l1 + l2 - j3 >=0,      l1 - l2 + j3 >=0,
    -l1 + l2 + j3 >=0,
     j1 + l2 - l3 >=0,      j1 - l2 + l3 >=0,
    -j1 + l2 + l3 >=0,
     l1 + j2 - l3 >=0,      l1 - j2 + l3 >=0,
    -l1 + j2 + l3 >=0,

     % condtions for the summation index

     !=k >= 0,
     j1 + j2 + l1 + l2 + 1 -!=k >=0,
     j1 + j2 -j3 -!=k >=0,
     l1 + l2 - j3 -!=k >=0,
     j1 + l2 - l3 -!=k >=0,
     l1 + j2 -l3 -!=k >=0,
    -j1 -l1 + j3 + l3 + !=k >=0,
    -j2 -l2 + j3 + l3 + !=k >=0};

% conditions for non vanishing 3j symbol see Landolt/Boernstein

procedure conds!-3j (j1,j2,j3,m1,m2,m3);

 { m1 + m2 + m3 =0,
   j1 + j2 - j3 >=0,
   j1 - j2 + j3 >=0,
  -j1 + j2 + j3 >=0,
   !=k >=0,
   j1 + j2 -j3 -!=k >=0,
   j1 - m1 -!=k >=0,
   j3 + m2 -!=k >=0,
   j3 -j2 + m1 + !=k >=0,
   j3 - j1 -m2 + !=k >=0};

% Trying to find the approroate intervals for the summation in the
% 6j symbol computation using the ineq package by Herbert Melenk

procedure findinterval(j1,j2,j3,l1,l2,l3);
 begin scalar interv,svars;
  svars := lisp( 'list .
                solvevars list(simp j1,simp j2,simp j3,
                                simp l1, simp l2,simp l3));
  interv :=reverse
                ineq_solve(aconds!-6j(j1,j2,j3,l1,l2,l3)
                        ,!=k . svars,record=t);
  return findinterval1(part(first interv),0);
 end;
>>;

symbolic procedure findinterval1(maxis,minis);
(<<
  if eqcar(maxis,'equal) and cadr maxis eq '!=k then
      maxis := caddr maxis;

  if not eqcar(maxis,'!*interval!*) then
         list('list,list('list,maxis , 0))
  else <<
  minis := caddr maxis;
  maxis := cadr maxis;
  if eqcar(maxis,'max) then
                 maxis := cdr maxis else maxis := list maxis;
  if eqcar(minis,'min) then
                 minis := cdr minis else minis := list minis;
  foreach xx in maxis do
     foreach yy in minis do
        if numberp (difff :=reval list('difference,yy,xx)) then
                fixedints := {'list,xx,difff} . fixedints;
  'list  . fixedints
      >>
 >>) where fixedints = nil , difff = nil;

flag('(findinterval1),'opfn);

symbolic procedure fancy!-clebsch_gordon(u);
  begin scalar a,j1,m1,j2,m2,j,m;
    u:=cdr u; j1:=cadr car u; m1:=caddr car u;
    u:=cdr u; j2:=cadr car u; m2:=caddr car u;
    u:=cdr u; j :=cadr car u; m :=caddr car u;
    a:={j1,m1,j2,m2,'!|,j1,j2,j,m};
    return fancy!-in!-brackets(
       {'fancy!-inprint, mkquote 'times,0,mkquote a},
         '!(,'!));
  end;

put('clebsch_gordon,'fancy!-prifn, 'fancy!-clebsch_gordon);

symbolic procedure fancy!-ThreeJSymbol(u);
     fancy!-matpri2({cdr cadr u,cdr caddr u},nil,nil);

put('ThreeJSymbol,'fancy!-prifn, 'fancy!-ThreeJSymbol);

symbolic procedure fancy!-SixJSymbol(u);
     fancy!-matpri2({cdr cadr u,cdr caddr u},nil,'("{" . "}"));

put('SixJSymbol,'fancy!-prifn, 'fancy!-SixJSymbol);

symbolic procedure fancy!-NineJSymbol(u);
     fancy!-matpri2({cdr cadr u,cdr caddr u, cdr cadddr u},nil,
          '("{" . "}"));

put('NineJSymbol,'fancy!-prifn, 'fancy!-NineJSymbol);

endmodule;

end;






