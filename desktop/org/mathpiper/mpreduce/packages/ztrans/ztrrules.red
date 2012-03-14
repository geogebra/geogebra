module ztrrules;  % Ztrans ruleset.

% Author: Lisa Temme.

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


algebraic <<
ztransrules := {
  ztrans_aux(1,~n,~z) => z/(z-1),

  ztrans_aux(BINOMIAL(~n+~~k,~m),~n,~z) => z^(k+1)/(z-1)^(m+1) when
                                       (freeof(k,n) and freeof(m,n)),

  ztrans_aux(factorial(~n)/(factorial(~n-~k)*factorial(~k)),~n,~z)
                        => ztrans(binomial(n,k),n,z) when freeof(k,n),

  ztrans_aux(1/(~n+~~k),~n,~z) => z^(k-1)*(z*log(z/(z-1))
                              - sum(1/((j+1)*z^j),j,0,k-2))
                              when (freeof(k,n) and fixp(k) and k>0),

  ztrans_aux(~a^(~n+~~k),~n,~z) => a^k*z/(z-a) when (freeof(a,n)
                               and freeof(k,n)),

  ztrans_aux(1/~a^(~n+~~k),~n,~z) => SUB(a=1/a,ztrans(a^(n+k),n,z))
                                 when (freeof(a,n) and freeof(k,n)),

  ztrans_aux(e^(~n*~~a),~n,~z) => -z/(e^a-z) when freeof(a,n),

  ztrans_aux(e^((~n+~~k)*~~a),~n,~z) => e^(a*k)*-z/(e^a-z)
                                   when (freeof(a,n) and freeof(k,n)),

  ztrans_aux(1/FACTORIAL(~n),~n,~z) => e^(1/z),

  ztrans_aux(1/FACTORIAL(2*~n+~~k),~n,~z) =>
                        z^((k-1)/2)*(SQRT(z)*SINH(1/SQRT(z))
                        - sum(1/(factorial(2*j+1)*z^j),j,0,(k-3)/2))
                        when (freeof(k,n) and fixp((k+1)/2) and k>0),

  ztrans_aux(1/FACTORIAL(2*~n+~~k),~n,~z) =>
                        z^(k/2)*(COSH(1/SQRT(z))
                        - sum(1/(factorial(2*j)*z^j),j,0,k/2-1))
                        when (freeof(k,n) and fixp(k/2) and k>=0),

  ztrans_aux((-1)^~n/FACTORIAL(2*~n+~~k),~n,~z) =>
                (-z)^((k-1)/2)*(SQRT(z)*SIN(1/SQRT(z))
                - sum((-1)^j/(factorial(2*j+1)*z^j),j,0,(k-3)/2))
                      when(freeof(k,n) and fixp((k+1)/2) and k>0),

  ztrans_aux((-1)^~n/FACTORIAL(2*~n+~~k),~n,~z) =>
                        (-z)^(k/2)*(COS(1/SQRT(z))
                        - sum((-1)^j/(factorial(2*j)*z^j),j,0,k/2-1))
                        when (freeof(k,n) and fixp(k/2) and k>=0),

  ztrans_aux(SINH(~~al*~n+~~p),~n,~z) => z*(z*SINH(p) + SINH(al-p))
                                   /(z^2 - 2*z*COSH(al) + 1)
                                   when (freeof(al,n) and freeof(p,n)),

  ztrans_aux(COSH(~~al*~n+~~p),~n,~z) => z*(z*COSH(p) - COSH(al-p))
                                   /(z^2 - 2*z*COSH(al) + 1)
                                   when (freeof(al,n) and freeof(p,n)),

  ztrans_aux(SIN(~~b*~n+~~p),~n,~z) =>  z*(z*SIN(p) + SIN(b-p))
                                   /(z^2 - 2*z*COS(b) + 1)
                                    when (freeof(b,n) and freeof(p,n)),

  ztrans_aux(COS(~~b*~n+~~p),~n,~z) =>  z*(z*COS(p) - COS(b-p))
                                   /(z^2 - 2*z*COS(b) + 1)
                                    when (freeof(b,n) and freeof(p,n)),

  ztrans_aux(e^(~~a*~n)*SIN(~~b*~n),~n,~z) =>
                            z*e^a*SIN(b)/(z^2-2*z*e^a*COS(b)+e^(2*a))
                                   when (freeof(a,n) and freeof(b,n)),

  ztrans_aux(e^(~~a*~n)*COS(~~b*~n),~n,~z) =>
                        z*(z-e^a*COS(b))/(z^2-2*z*e^a*COS(b)+e^(2*a))
                                   when (freeof(a,n) and freeof(b,n)),

  ztrans_aux(COS(~~b*(~n+~~k))/(~n+~~k),~n,~z) =>
                        z^(k-1)*(z*log(z/SQRT(z^2-2*z*COS(b)+1))
                        - sum(cos(b*(j+1))/((j+1)*z^j),j,0,k-2))
                        when (freeof(b,n) and freeof(k,n)
                                     and fixp(k) and k>0),

  ztrans_aux(SIN(~~b*(~n+~~k))/(~n+~~k),~n,~z) =>
                        z^(k-1)*(-z*ATAN(SIN(b)/(COS(b)-z))
                        - sum(sin(b*(j+1))/((j+1)*z^j),j,0,k-2))
                        when (freeof(b,n) and freeof(k,n)
                                     and fixp(k) and k>0),

  ztrans_aux((-1)^n*COS(~~b*(~n+~~k))/(~n+~~k),~n,~z) =>
                        -(-z)^(k-1)*(z*log(SQRT(z^2+2*z*COS(b)+1/z))
                        - sum((-1)^j*cos(b*(j+1))/((j+1)*z^j),j,0,k-2))
                        when (freeof(b,n) and freeof(k,n) and fixp(k)),

  ztrans_aux(COS(~~b*~n)/FACTORIAL(~n),~n,~z) =>
                                COS(SIN(b)/z)*e^(COS(b)/z)
                                when freeof(b,n),

  ztrans_aux(COS(~~b*(~n+~~k))/FACTORIAL(~n+~~k),~n,~z) =>
                        z^k*(COS(SIN(b)/z)*e^(COS(b)/z)
                        - sum(cos(b*j)/(factorial(j)*z^j),j,0,k-1))
                                     when (freeof(b,n) and fixp(k)),

  ztrans_aux(SIN(~~b*~n)/FACTORIAL(~n),~n,~z) =>
                                SIN(SIN(b)/z)*e^(COS(b)/z)
                                when freeof(b,n),

  ztrans_aux(SIN(~~b*(~n+~~k))/FACTORIAL(~n+~~k),~n,~z) =>
                        z^k*(SIN(SIN(b)/z)*e^(COS(b)/z)
                        - sum(sin(b*j)/(factorial(j)*z^j),j,0,k-1))
                                     when (freeof(b,n) and fixp(k)),

%LINEARITY
  ztrans_aux(-~f,~n,~z)   => -ztrans(f,n,z),
  ztrans_aux(~a,~n,~z)    => a*ztrans(1,n,z)   when freeof(a,n),
  ztrans_aux(~a*~f,~n,~z) => a*ztrans(f,n,z)   when freeof(a,n),
  ztrans_aux(~f/~b,~n,~z) => ztrans(f,n,z)/b   when freeof(b,n),
  ztrans_aux(~a/~g,~n,~z) => a*ztrans(1/g,n,z) when (freeof(a,n)
                                                    and not(a=1)),

  ztrans_aux(~a*~f/~g,~n,~z) => a*ztrans(f/g,n,z) when freeof(a,n),

  ztrans_aux(~f/(~b*~g),~n,~z) => ztrans(f/g,n,z)/b when freeof(b,n),

  ztrans_aux((~f+~g)/~~h,~n,~z) => ztrans(f/h,n,z) + ztrans(g/h,n,z),

%MULTIPLICATION
  ztrans_aux(~n^~~p*~~f,~n,~z) => -z*DF(ztrans(n^(p-1)*f,n,z),z)
                                  when freeof(p,n) and fixp(p) and p>0,

  ztrans_aux(~n^~~p*~~f/~g,~n,~z) => -z*DF(ztrans(n^(p-1)*f/g,n,z),z)
                                  when freeof(p,n) and fixp(p) and p>0,

%Shift up
  ztrans_aux(~f(~n+~k),~n,~z) =>
                        z^k*(ztrans(f(n),n,z)-SUM(f(n)*z^(-n),n,0,k-1))
                        when freeof(k,n) and fixp(k) and k>0,

  ztrans_aux(~f(~n+~k)/~g(~n+~k),~n,~z) =>
                        z^k*(ztrans(f(n)/g(n),n,z)-
                        SUM(f(n)/g(n)*z^(-n),n,0,k-1))
                        when freeof(k,n) and fixp(k) and k>0,

  ztrans_aux(1/~g(~n+~k),~n,~z) =>
                        z^k*(ztrans(1/g(n),n,z)-
                        SUM(1/g(n)*z^(-n),n,0,k-1))
                        when freeof(k,n) and fixp(k) and k>0,

%Similar Expressions
  ztrans_aux(~a^(~n+~~k)*~f,~n,~z) => a^k*SUB(z=(z/a),ztrans(f,n,z))
                                  when freeof(a,n) and freeof(k,n),

  ztrans_aux(~a^(~n+~~k)*~~f/~g,~n,~z) =>
                                    a^k*SUB(z=(z/a),ztrans(f/g,n,z))
                                    when freeof(a,n) and freeof(k,n),

  ztrans_aux(~a^(~n-~~k)*~~f/~g,~n,~z) =>
                                    a^k*SUB(z=(z/a),ztrans(f/g,n,z))
                                    when freeof(a,n) and freeof(k,n),

  ztrans_aux(1/~a^(~n+~~k)*~f,~n,~z) => 1/a^k*SUB(z=z*a,ztrans(f,n,z))
                                    when freeof(a,n) and freeof(k,n),

  ztrans_aux(1/~a^(~n+~~k)*~~f/~g,~n,~z) =>
                                    1/a^k*SUB(z=z*a,ztrans(f/g,n,z))
                                    when freeof(a,n) and freeof(k,n),

%Summations
  ztrans_aux(sum(~f(~k)*~g(~n-~k),~k,0,~n),~n,~z) =>
                                ztrans(f(n),n,z)*ztrans(g(n),n,z)
                                when freeof(k,n),

  ztrans_aux(~summ(~f,~k,0,~n),~n,~z) => z*ztrans(SUB(k=n,f),n,z)/(z-1)
                                     when freeof(k,n) and summ = sum,

%  ztrans_aux(~summ(~~f/~g,~k,0,~n),~n,~z) =>
%                                     z*ztrans(SUB(k=n,f/g),n,z)/(z-1)
%                                    when (freeof(k,n) and summ = sum),

  ztrans_aux(~summ(~f,~k,0,(~n+~w)),~n,~z) =>
                        z*ztrans(SUB(k=n,f),n,z)/(z-1) +
                        sum(z^x*(ztrans(SUB(k=n,f),n,z) -
                        sum(SUB(k=n,f)/z^n,n,0,x-1)),x,1,w)
                        when (freeof(w,n) and fixp(w) and w>0
                        and summ = sum),

%  ztrans_aux(~summ(~~f/~g,~k,0,(~n+~w)),~n,~z) =>
%                       z*ztrans(SUB(k=n,f/g),n,z)/(z-1) +
%                       sum(z^x*(ztrans(SUB(k=n,f/g),n,z) -
%                       sum(SUB(k=n,f/g)/z^n),n,0,(x-1)),x,1,w)
%                       when (freeof(w,n) and fixp(w) and w>0
%                       and summ = sum),

  ztrans_aux(~summ(~f,~k,~p,~n),~n,~z) =>
                        ztrans(sum(SUB(k=k+p,f),k,0,n-p),n,z)
                        when (freeof(p,n) and fixp(p) and p>0
                        and summ = sum),

  ztrans_aux(~summ(~f,~k,0,(~nn)),~n,~z) =>
                ztrans(SUB(k=n,f),n,z)/(z-1) -
                sum(1/z^y*ztrans(SUB(k=n,f),n,z),y,1,((n-nn)-1))
                when (freeof((nn-n),n) and fixp(nn-n) and
                (nn-n)<0 and summ = sum),

%  ztrans_aux(~summ(~~f/~g,~k,0,(~nn)),~n,~z) =>
%               ztrans(SUB(k=n,f/g),n,z)/(z-1) -
%               sum(1/z^y*ztrans(SUB(k=n,f/g),n,z),y,1,((n-nn)-1))
%               when (freeof((nn-n),n) and fixp (nn-n) and
%               (nn-n)<0 and summ = sum),

  ztrans_aux(~summ(~f,~k,~p,~n),~n,~z) =>
                        ztrans(sum(SUB(k=k+p,f),k,0,n+(-p)),n,z)
                        when (freeof(p,n) and fixp(p) and p<0
                        and summ = sum),

  ztrans_aux(~summ(~f,~k,~p,~q),~n,~z) =>
                        (begin scalar r;
                         r := q-p;
                         return ztrans(sum(SUB(k=k+p,f),k,0,r),n,z);
                         end)  when (not(p=0) and summ = sum),

%Errors
%======
  ztrans_aux(~~f/(~n+~~k),~n,~z) =>
        (begin
        newrederr{"ERROR: zero divisor in ",
                  sum(f/((n+k)*z^n),n,0,infinity)}
        end)
        when (numberp k and k<1),

  ztrans_aux(~~f/factorial(~n+~~k),~n,~z) =>
        (begin
        newrederr{"ERROR: zero divisor in "
                  ,sum(f/(factorial(n+k)*z^n),n,0,infinity)}
        end)
        when (numberp k and k<0)

}$

let ztransrules>>;

% INVZTRANS: inverse Z transformation, see
% Bronstein, Semendjajew: Taschenbuch der Mathematik, 4.4.4

load!-package 'residue;

%######################################################################

% Final simplification,
% by Wolfram Koepf
algebraic<<
ztranstrighypsimplificationrules:={
asin(sin(~xx))=>xx,
acos(cos(~xx))=>xx,
atan(tan(~xx))=>xx,
acot(cot(~xx))=>xx,
asinh(sinh(~xx))=>xx,
acosh(cosh(~xx))=>xx,
atanh(tanh(~xx))=>xx,
acoth(coth(~xx))=>xx,
(1-sin(~xx)^2)^(1/2)=>cos(xx),
(1-cos(~xx)^2)^(1/2)=>sin(xx),
(cosh(~xx)^2-1)^(1/2)=>sinh(xx),
(1+sinh(~xx)^2)^(1/2)=>cosh(xx),
(cosh(~xx)+sinh(~xx))^~nn=>cosh(nn*xx)+sinh(nn*xx),
(cosh(~xx)-sinh(~xx))^~nn=>cosh(nn*xx)-sinh(nn*xx)
} $

operator invztrans,invztrans_aux,invztrans1,invztrans_end;

% let {binomial(~n,~k)=>prod(n-i,i,0,k-1)/factorial(k) when fixp(k)};
let {binomial(~n,~k)=>
        (for i:=0:k-1 product n-i)/factorial(k) when fixp(k)};

>>;
% Procedural embedding,
% by Wolfram Koepf
algebraic procedure do_invztrans(f,z,n);
begin
scalar tmp,numtmp,dentmp;
   tmp := invztrans1(f,z,n);
   numtmp:=num(tmp);
   dentmp:=den(tmp);
   numtmp:=(numtmp where ztranstrighypsimplificationrules);
   dentmp:=(dentmp where ztranstrighypsimplificationrules);
   tmp:=numtmp/dentmp;
   % tmp:=sub(invztrans_end=invztrans,tmp);
   % macht Probleme wegen Rekursivitaet, next if has no part(.0)
   return tmp;
%   if part(tmp,0)=invztrans then
%       return lisp mk!*sq((list((car fkern list('invztrans,f,z,n) . 1)
%                                                 . 1)) . 1)
%   else return tmp;
end$


%********************************************************************

% invztrans ruleset
% by Lisa Temme

put('slash, 'simpfn, 'simpiden);

algebraic <<
invztransrules:=
{
%Linear rules
%============

  invztrans (~P,~z,~n) => !$do_invztrans!$
        when freeof((!$do_invztrans!$ :=
                          do_invztrans(P,z,n)),lisp 'fail),

  invztrans1(~P,~z,~n) => P*invztrans1(1,z,n)
                 when freeof(P,z) and not (p=1),

  invztrans1(~P*~f,~z,~n) => P*invztrans1(f,z,n) when freeof(P,z),

  invztrans1(~f/~Q,~z,~n) => invztrans1(f,z,n)/Q when freeof(Q,z),

  invztrans1(~P/~g,~z,~n) => P*invztrans1(1/g,z,n) when freeof(P,z)
                            and NOT(P=1),

  invztrans1(~P*~f/~g,~z,~n) => P*invztrans1(f/g,z,n) when freeof(P,z),

  invztrans1(~f/(~Q*~g),~z,~n) =>
                                invztrans1(f/g,z,n)/Q when freeof(Q,z),

  invztrans1(-~f,~z,~n) => -invztrans1(f,z,n),

  invztrans1((~f+~g)/~~h,~z,~n) =>
                             invztrans1(f/h,z,n) + invztrans1(g/h,z,n),


%**********************************************************************

%For trigonometric/hyperbolic rational
%input goto ruleset invztrans_aux
%=====================================

  invztrans1(~f/~g,~z,~n) => invztrans_aux(f,g,z,n)

        when ( NOT(freeof(f/g,sin))  OR NOT(freeof(f/g,cos)) OR
               NOT(freeof(f/g,sinh)) OR NOT(freeof(f/g,cosh)) ),


%If not a trig/hyperbolic rational
%input goto ruleset invztrans_end
%(ie. all remaining inputs)
%=================================

  invztrans1(~f,~z,~n) => invztrans_end(f,z,n)
%,


%  invztrans1(~f,~z,~n) =>
%   (begin
%    return lisp mk!*sq((list((car fkern list('invztrans1,reval 'f,
%                                             reval 'z,reval 'n) . 1)
%. 1)) . 1);
%   end)


};let invztransrules;


%######################################################################

invztrans_auxrules :=
{

%Linearity
%=========
  invztrans_aux(~f,-~~X*~z^2+~~W*~z-~Y,~z,~n) =>
                                -invztrans_aux(f,X*z^2-W*z+Y,z,n),

  invztrans_aux(~f+~h,~g,~z,~n) =>
                invztrans_aux(f,g,z,n) + invztrans_aux(h,g,z,n),


%Rules to match trigonometric/hyperbolic
%rational inputs.
%=======================================

  invztrans_aux(~z,(~~X*~z^2-~~W*~z+~Y),~z,~n) =>

        SUB(srX=sqrt(X), srW=sqrt(W), srY=sqrt(Y),
            2*srY^n*SIN(ACOS(srX*W/(2*srY*X))*n)
            / ( srX^n*sqrt(4*X*Y-W^2) ) )

        when (numberp(X) and numberp(W) and numberp(Y) and
              Y>0 and W>0 and (W^2)<(4*X*Y))
        OR   (numberp(X) and numberp(W) and NOT(numberp(Y)) and W>0)
        OR   (NOT(numberp(X) and numberp(W) and numberp(Y))
              and freeof((W/X),cosh)),


  invztrans_aux(~z,(~~X*~z^2+~~W*~z+~Y),~z,~n) =>

        SUB(srX=sqrt(X), srW=sqrt(W), srY=sqrt(Y),
            -2*srY^n*(-1)^n*SIN(ACOS(srX*W/(2*srY*X))*n)
            / ( srX^n*sqrt(4*X*Y-W^2) ) )

        when (numberp(X) and numberp(W) and numberp(Y) and
              Y>0 and W>0 and (W^2)<(4*X*Y))
        OR   (numberp(X) and numberp(W) and NOT(numberp(Y)) and W<0)
        OR   (NOT(numberp(X) and numberp(W) and numberp(Y))
              and freeof((W/X),cosh)),

  invztrans_aux(~z,(~~X*~z^2-~~W*~z+~Y),~z,~n) =>

        SUB(srX=sqrt(X), srW=sqrt(W), srY=sqrt(Y),
            2*srY^n*SINH(ACOSH(srX*W/(2*srY*X))*n)
            / ( srX^n*sqrt(W^2-4*X*Y) ) )

        when (numberp(X) and numberp(W) and numberp(Y) and
              Y>0 and (W^2)>(4*X*Y))
        OR   (NOT(numberp(X) and numberp(W) and numberp(Y))),

  invztrans_aux(~z,(~~X*~z^2+~~W*~z+~Y),~z,~n) =>

        SUB(srX=sqrt(X), srW=sqrt(W), srY=sqrt(Y),
            -2*(-srY)^n*SINH(ACOSH(srX*W/(2*srY*X))*n)
            / ( srX^n*sqrt(W^2-4*X*Y) ) )

        when (numberp(X) and numberp(W) and numberp(Y) and
              Y>0 and (W^2)>(4*X*Y))
        OR   (NOT(numberp(X) and numberp(W) and numberp(Y))),

  invztrans_aux(~z^2,(~~X*~z^2-~~W*~z+~Y),~z,~n) =>

        SUB(srX=sqrt(X), srW=sqrt(W), srY=sqrt(Y),
            (srY^n*(sqrt(4*X*Y-W^2)*COS(ACOS(srX*W/(2*srY*X))*n)
            + SIN(ACOS(srX*W/(2*srY*X))*n)*W))
            / ( srX^n*sqrt(4*X*Y-W^2)*X ) )

        when (numberp(X) and numberp(W) and numberp(Y) and
              Y>0 and W>0 and (w^2)<(2*X*Y))
        OR   (numberp(X) and numberp(W) and W>0)
        OR   (NOT(numberp(X) and numberp(W) and numberp(Y))
              and freeof((W/X),cosh)),


  invztrans_aux(~z^2,(~~X*~z^2+~~W*~z+~Y),~z,~n) =>

        SUB(srX=sqrt(X), srW=sqrt(W), srY=sqrt(Y),
            (srY^n*(-1)^n*(sqrt(4*X*Y-W^2)*COS(ACOS(srX*W/(2*srY*X))*n)
            + SIN(ACOS(srX*W/(2*srY*X))*n)*W))
            / ( srX^n*sqrt(4*X*Y-W^2)*X ) )

        when (numberp(X) and numberp(W) and numberp(Y) and
              Y>0 and W>0 and (W^2)>(4*X*Y))
        OR   (numberp(X) and numberp(W) and W<0)
        OR   (NOT(numberp(X) and numberp(W) and numberp(Y))
              and freeof((W/X),cosh)),


  invztrans_aux(~z^2,(~~X*~z^2-~~W*~z+~Y),~z,~n) =>

        SUB(srX=sqrt(X), srW=sqrt(W), srY=sqrt(Y),
            (srY^n*(sqrt(W^2-4*X*Y)*COSH(ACOSH(srX*W/(2*srY*X))*n)
            + SINH(ACOSH(srX*W/(2*srY*X))*n)*W))
            / ( srX^n*sqrt(W^2-4*X*Y)*X ) )

        when (numberp(X) and numberp(W) and numberp(Y) and
              Y>0 and W>(4*X*Y))
        OR   (NOT(numberp(X) and numberp(W) and numberp(Y))),

  invztrans_aux(~z^2,(~~X*~z^2+~~W*~z+~Y),~z,~n) =>

        SUB(srX=sqrt(X), srW=sqrt(W), srY=sqrt(Y),
            ((-srY)^n*(sqrt(W^2-4*X*Y)*COSH(ACOSH(srX*W/(2*srY*X))*n)
            + SINH(ACOSH(srX*W/(2*srY*X))*n)*W))
            / ( srX^n*sqrt(W^2-4*X*Y)*X ) )

        when (numberp(X) and numberp(W) and numberp(Y) and
              Y>0 and W>(4*X*Y))
        OR   (NOT(numberp(X) and numberp(W) and numberp(Y))),

  invztrans_aux(~f,~g,~z,~n) => invztrans_end(f/g,z,n)

};let invztrans_auxrules;

%######################################################################

invztrans_endrules :=
{

%Rules to match other
%trigonometric inputs
%====================

  invztrans_end(~z*atan(SIN(~b)//(COS(~b)-~z)),~z,~n) =>
        -SIN(b*(n+1))/(n+1) when numberp(b)
     OR (freeof(b,z) and NOT(numberp(b))),

  invztrans_end(~z*atan(SIN(~b)//(~z+COS(~b))),~z,~n) =>
        (-1)^n*SIN(b*(n+1))/(n+1) when numberp(a)
     OR (freeof(a,z) and NOT(numberp(a))),

  invztrans_end(~z*log(~z/sqrt(~z^2-~a*~z+1)),~z,~n) =>
        COS(ACOS(a/2)*(n+1))/(n+1) when (numberp(a) and a>0 and a<=-2)
     OR (freeof(a,z) and NOT(numberp(a))),

  invztrans_end(~z*log(~z/sqrt(~z^2+~a*~z+1)),~z,~n) =>
        COS(ACOS(-a/2)*(n+1))/(n+1) when (numberp(a) and a<0 and a>=-2)
     OR (freeof(a,z) and NOT(numberp(a))),

  invztrans_end(~z*log(sqrt(~z^2-~a*~z+1)/~z),~z,~n) =>
       (-1)^n* COS(ACOS(-a/2)*(n+1))/(n+1)
        when (numberp(a) and a<0 and a>=-2)
     OR (freeof(a,z) and NOT(numberp(a))),

  invztrans_end(~z*log(sqrt(~z^2+~a*~z+1)/~z),~z,~n) =>
        (-1)^n*COS(ACOS(a/2)*(n+1))/(n+1)
        when (numberp(a) and a>0 and a<=-2)
     OR (freeof(a,z) and NOT(numberp(a))),

  invztrans_end(COS(~a/~z)*e^(sqrt(1-~a^2)/~z),~z,~n) =>
        COS(ASIN(a)*n)/factorial(n) when (numberp(a) and a<=1 and
       a>=-1)
     OR (freeof(a,z) and NOT(numberp(a))),

%**********************************************************************

%Rule to calculate the Residues and hence
%determine the invztrans of a rational input
%===========================================
% by Wolfram Koepf

  invztrans_end(~f,~z,~n)=>
        (begin scalar denominator, result, solutionset, solution,
          !*fullroots;
          on fullroots;
          denominator:=den(f);
          solution:=solve(denominator,z);
          if not freeof(solution,root_of) then
            rederr("denominator could not be factorized");
          solutionset:=
           for i:=1:length(solution) collect(part(part(solution,i),2));
          result:=
            for each a in solutionset sum(residue(f*z^(n-1),z,a));
          return(result)
        end) when type_ratpoly(f,z),

%**********************************************************************

%Rules to match non-rational inputs
%==================================

%(Binomial)
%----------
  invztrans(~z^~~k/(z+~~a)^~~m,~z,~n) =>
                binomial(n+k-1,m-1)*(-a)^(n+k)/(-a)^m
                when freeof(k,z) and freeof(m,z) and freeof(a,z) and
                     (NOT(numberp k) OR (numberp k and fixp k)) and
                     (NOT(numberp m) OR (numberp m and fixp m)),

%(over n!)
%---------

  invztrans_end(e^(~k/~z),~z,~n) => k^n/factorial(n) when freeof(k,z),

  invztrans_end(e^(~k/~z)/~z,~z,~n) =>
                                 n/k*k^n/factorial(n) when freeof(k,z),

  invztrans_end(1/e^(~k/~z),~z,~n) =>
                                  (-k)^n/factorial(n) when freeof(k,z),

  invztrans_end(1/(e^(~k/~z)*~z),~z,~n) =>
                             -n/k*(-k)^n/factorial(n) when freeof(k,z),

  invztrans_end(e^(~k/(~~j*~z)),~z,~n) =>
                 (k/j)^n/factorial(n) when freeof(k,z) and freeof(j,z),

  invztrans_end(e^(~k/(~~j*~z))/~z,~z,~n) =>
         n/(k/j)*(k/j)^n/factorial(n) when freeof(k,z) and freeof(j,z),

  invztrans_end(1/e^(~k/(~~j*~z)),~z,~n) =>
                (-k/j)^n/factorial(n) when freeof(k,z) and freeof(j,z),

  invztrans_end(1/(e^(~k/(~~j*~z))*~z),~z,~n) =>
       n/(-k/j)*(-k/j)^n/factorial(n) when freeof(k,z) and freeof(j,z),

  invztrans_end(cos(sin(~~b)/~z)*e^(cos(~~b)/~z),~z,~n) =>
                               cos(b*n)/factorial(n) when freeof (b,z),

  invztrans_end(sin(sin(~~b)/~z)*e^(cos(~~b)/~z),~z,~n) =>
                               sin(b*n)/factorial(n) when freeof (b,z),


%(over 2n!)
%----------

  invztrans_end(cosh(~k/sqrt(~z)),~z,~n) =>
                               k^(2*n)/factorial(2*n) when freeof(k,z),

  invztrans_end(cos(~k/sqrt(~z)),~z,~n) =>
                            (-(k^2))^n/factorial(2*n) when freeof(k,z),

  invztrans_end(cosh(~k/(~~j*sqrt(~z))),~z,~n) =>
           (k/j)^(2*n)/factorial(2*n) when freeof(k,z) and freeof(j,z),

  invztrans_end(cos(~k/(~~j*sqrt(~z))),~z,~n) =>
          (-(k/j)^2)^n/factorial(2*n) when freeof(k,z) and freeof(j,z),

%(over (2n+1)!)
%--------------

  invztrans_end(sqrt(~z)*sinh(~k/sqrt(~z)),~z,~n) =>
                          k*k^(2*n)/factorial(2*n+1) when freeof (k,z),

  invztrans_end(sqrt(~z)*sinh(~k/sqrt(-~z)),~z,~n) =>
                       i*k*(-k^2)*n/factorial(2*n+1) when freeof (k,z),

  invztrans_end(sqrt(~z)*sin(~k/sqrt(~z)),~z,~n) =>
                         k*(-k^2)^n/factorial(2*n+1) when freeof (k,z),

  invztrans_end(sqrt(-~z)*sinh(~k/sqrt(~z)),~z,~n) =>
                 sqrt(-k^2)*k^(2*n)/factorial(2*n+1) when freeof (k,z),

  invztrans_end(sqrt(-~z)*sin(~k/sqrt(~z)),~z,~n) =>
                 k*(-k^2)^n/(i*factorial(2*n+1)) when freeof (k,z),

  invztrans_end(sqrt(-~z)*sinh(~k/sqrt(-~z)),~z,~n) =>
                         k*(-k^2)*n/factorial(2*n+1) when freeof (k,z),

  invztrans_end(sqrt(-~z)*sin(~k/sqrt(~z)),~z,~n) =>
                k*(-k^2)*n/(i*factorial(2*n+1)) when freeof (k,z),


  invztrans_end(sqrt(~z)*sinh(~k/(~~j*sqrt(~z))),~z,~n) =>
                                    (k/j)*(k/j)^(2*n)/factorial(2*n+1)
                                     when freeof (k,z) and freeof(j,z),

  invztrans_end(sqrt(-~z)*sinh(~k/(~~j*sqrt(~z))),~z,~n) =>
                                    (k/j)*(k/j)^(2*n)/factorial(2*n+1)
                                     when freeof (k,z) and freeof(j,z),

  invztrans_end(sqrt(-~b*~z)*sinh(~k/(sqrt(~b)*sqrt(~z))),~z,~n) =>
                                 sqrt(-k^2)*(k^2/b)^n/factorial(2*n+1)
                                     when freeof (k,z) and freeof(j,z),

  invztrans_end(sqrt(~z)*sin(~k/(~~j*sqrt(~z))),~z,~n) =>
                  (sqrt(-k^2)/j)*(-k^2)^n/j^(2*n)/(i*factorial(2*n+1))
                                     when freeof (k,z) and freeof(j,z),

  invztrans_end(sqrt(-~z)*sin(~k/(~~j*sqrt(~z))),~z,~n) =>
                                (k/j)*(k/j)^(2*n)/(i*factorial(2*n+1))
                                     when freeof (k,z) and freeof(j,z),

  invztrans_end(sqrt(-~b*~z)*sin(~k/(sqrt(~b)*sqrt(~z))),~z,~n) =>
                                     k*(-k^2/b)^n/(i*factorial(2*n+1))
                                     when freeof (k,z) and freeof(b,z),


  invztrans_end(sqrt(~z)*sinh(~k/(~~j*sqrt(-~z))),~z,~n) =>
                                  i*(k/j)*(k/j)^(2*n)/factorial(2*n+1)
                                     when freeof (k,z) and freeof(j,z),

  invztrans_end(sqrt(~z)*sin(~k/(~~j*sqrt(-~z))),~z,~n) =>
                  (sqrt(-k^2)/j)*(sqrt(-k^2)/j)^(2*n)/factorial(2*n+1)
                                     when freeof (k,z) and freeof(j,z),

%(over n+1)
%----------

  invztrans_end(~z*log(~~b*~z/(~~b*~z+~a)),~z,~n) =>
                   (-a/b)^(n+1)/(n+1) when freeof(a,z) and freeof(b,z),

  invztrans_end(~z*log((~~b*~z+~a)/(~~b*~z)),~z,~n) =>
                                   -invztrans1(z*log(b*z/(b*z+a)),z,n)
                                      when freeof(a,z) and freeof(b,z),


%If input has not matched any rules
%return INVZTRANS(~f,~z,~n)
%==================================

  invztrans_end(~f,~z,~n) =>  lisp 'fail


};let invztrans_endrules;
>>;

endmodule;

end;
