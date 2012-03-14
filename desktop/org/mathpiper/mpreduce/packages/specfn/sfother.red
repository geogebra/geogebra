module sfother;   % Rulesets for the Struve H and L functions, Lommel
                  % 1 and 2 functions and Whittaker M and W functions.

% Author: Chris Cannam, Nov 1992.

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


% The aim is to re-express in terms % of other (more `standard') special
% functions. No numerical approximation code.

% Neither imports nor exports functions.
% This module contains only rulesets.

algebraic (operator struveh, struvel);

algebraic (struve!*rules := {

df(struveh(~n,~z),z)  =>
   (2/pi) - struveh(1,z) when numberp n and n = 0,

df(struveh(~n,~x),x) => (x*StruveH(-1 + n,x)- n*StruveH(n,x))/x,

df((z**n)*struveh(~n,~z),z)  => (z**n)*struveh(n-1,z),

df((z**(-n))*struveh(~n,~z),z)  =>
   (1/(sqrt(pi)*(2**n)*gamma(n+(3/2)))) - (z**(-n))*struveh(n+1,z),


struveh(~n,~z)  =>
   ((-1)**n)*besselj(-n,z)
      when numberp n and impart n = 0
         and n < 0 and (n*2)=floor(n*2) and not evenp floor(n*2),

struveh(~n,~z)  =>
   ((2/(pi*z))**(1/2))*(1-cos z) when numberp n and n=1/2,

struveh(~n,~z)  =>
   ((z/(pi*2))**(1/2)) * (1+(2/(z**2))) -
      ((2/(pi*z))**(1/2)) * (sin z + ((cos z)/z))
      when numberp n and n=3/2,

struveh(~n,~x) => (x*0.5)^(n+1)*struve_compute_term(n,x,h)
          when numberp x and numberp n and symbolic !*rounded,

struvel(~n,~x) => struve_compute_term(n,x,l)
          when numberp x and numberp n and symbolic !*rounded,

struvel(~n,~z)  =>
   besseli(-n,z)
      when numberp n and impart n = 0
         and n < 0 and (n*2)=floor(n*2) and not evenp floor(n*2),

struvel(~n,~z)  =>
   -i*(e**((-i*n*pi)/2))*struveh(n,i*z) when symbolic !*complex,

df(struvel(~n,~x),x) => (x*StruveL(-1 + n,x)- n*StruveL(n,x))/x

})$

algebraic (let struve!*rules);



algebraic (operator lommel1, lommel2);

algebraic (lommel!*rules := {

lommel1(~a,~b,~z)  =>
   -(2**a)*besselj(a,z)*gamma(a+1)+z**a
      when numberp a and numberp b and a = b+1,

lommel1(~a,~b,~z)  =>
   lommel1(a,-b,z)
      when numberp b and b < 0 and a neq b and a neq (b+1),

lommel1(~a,~b,~z)  =>
   (sqrt(pi)*(2**a)*gamma((2*a + 1)/2)*struveh(a,z))/2 when a = b,

lommel2(~a,~b,~z)  => z**b when numberp a and numberp b and a = b+1,

lommel2(~a,~b,~z)  => lommel2(a,-b,z)
      when numberp b and b < 0 and a neq b and a neq (b+1),

lommel2(~a,~b,~z)  =>
   (sqrt(pi)*(2**a)*gamma((2*a + 1)/2)*(-bessely(a,z)+struveh(a,z)))/2
      when a = b

})$

algebraic (let lommel!*rules);



algebraic (operator whittakerm, whittakerw);

algebraic (whittaker!*rules := {

whittakerm(~k,~m,~z)  =>
   exp(-z/2)*(z**(1/2+m))*kummerm(1/2+m-k,1+2*m,z),

whittakerw(~k,~m,~z)  =>
   exp(-z/2)*(z**(1/2+m))*kummeru(1/2+m-k,1+2*m,z),

df(WhittakerM(~n,~m,~z),z)  => 1/(2*z)*
        ((1+2*m-2*n)*WhittakerM(n-1,m,z) + (2*n-z)*WhittakerM(n,m,z)),

df(WhittakerW(~n,~m,~z),z)  => 1/(4*z)*
        ((1-4*m^2-4*n+4*n^2)*WhittakerW(n-1,m,z)
                 + (4*n-2*z)*WhittakerW(n,m,z))
% AS (8.5.4)

})$

algebraic (let whittaker!*rules);

%Handbook of Mathematical Functions - page 496

algebraic procedure struve_compute_term(n,x,h_or_l);

begin scalar dmode!*!*;
  lisp(dmode!*!* :=  dmode!*);
 return
  begin scalar pre,term,k,precis,result,!*complex,!*rounded,
   dmode!*,expo,!*msg;
   lisp (dmode!* := dmode!*!*);
  if h_or_l = l
        then << on complex;
                off rounded;
                expo := e^(-i*n*pi/2);
                on rounded;
                return  (-i*expo*struveh(n,i*x))>>
  else <<
   pre := precision 0;
   precis := 10.0^(-pre-2);
   result := 0;

    << if n > -2 then <<k:=1, term := 2^(n+2)/(pi *
                            (for i:= 1 :n+1 product(2i-1))) ;
                        result := term >>
        else for kk:=0:-(n+2) do << k:=kk+1;
                        term := (-1)^kk*(1/2*x)^(2*kk)/
                                (Gamma(kk+3/2) * Gamma(kk+n+3/2));
                        result := result + term>>;
        while abs(term) > precis do
          <<  term:= term*(-0.25)*(x^2)/((k+0.5)*(k+n+0.5));
              result := result + term;
              k := k+1>>;
  >>;  >>;
  return result;
end; end;

symbolic operator struve_compute_term;

% Lambert's W  (Omega) function.
% see: "On Lambert's W function" by R. Corless, G. Gonnet et. al.
% only the principal branch is implemented

algebraic <<

    % Remove autoload properties.
 lisp null remprop('lambert_w,'simpfn);
 lisp null remflag('(lambert_w),'full);

 operator lambert_w;

 let {    lambert_w(0) => 0,
        lambert_w(-1/e) => -1,
        sum((- ~n)^(n-1)/factorial n *~z^n,n,1,infinity)
                        => lambert_w(z),
        df(lambert_w(~z),z) => 1/((1 + lambert_w(z))*e^lambert_w z),
        log(lambert_w(~z)) => log(z) - lambert_w z,
        e^(lambert_w ~z) => ~z/lambert_w z,
        int(lambert_w(~z),z) => z*(lambert_w z -1 +1/lambert_w z),
        lambert_w(~z) =>  num_lambert_w(z)
                when numberp z and lisp !*rounded};

procedure num_lambert_w(z);

     if z=0 then 0 else

       begin scalar wjnew,wj,accu,expwj,oldprec,!*complex,olddmode!*;

         lisp setq(olddmode!* ,dmode!*);
         on complex;
         oldprec := precision 5;
         accu := 10^(- lisp !:prec!:);
         if (abs z) <= 1 then  % starting point for iteration
                 if z >= -1/e then wj := 0 else wj := log(z)
         else wj := log(z) - log(log(z));
         wjnew := 100;
         while abs(wjnew) > accu do <<
             expwj := exp(wj);
             wjnew := - (wj*expwj -z)/
                (expwj*(wj+1)-(1/2(wj+2)*(wj*expwj -z))/(wj+1));
             wj := wj + wjnew >>;

         precision oldprec;
         accu := 10^(- lisp !:prec!:);
         while abs(wjnew) > accu do <<
             expwj := exp(wj);
             wjnew := - (wj*expwj -z)/
                (expwj*(wj+1)-(1/2(wj+2)*(wj*expwj -z))/(wj+1));
             wj := wj + wjnew >>;
        lisp setq(dmode!*,olddmode!*);
         return wj;
    end;

>>;

endmodule;

end;

