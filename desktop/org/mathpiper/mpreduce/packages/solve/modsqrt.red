module modsqrt; % SQRT n mod p, n integer, p prime.
% Sqrt mod p, p prime.

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


% Winfried Neun:  added legendre_symbol as an algebraic operator.

symbolic procedure modsqrt(a,p);
  <<if not fixp p or p<2 then typerr(p,"modulus for root computation");
    a:=general!-modular!-number a;
      % The break even point between primitive and general algorithm
      % has been evaluated on a 486. For machines without hardware
      % support for integer division, the limit might be set higher.
    if p<50 then modsqrt1(a,p) else modsqrt2(a,p)>>
       where current!-modulus=p;

symbolic procedure modsqrt1(a,p);
  % Primitve but fast algorithm for small p: check all possible values.
   begin integer i,w; scalar r;
     while null r and i<p do
      if w = a then r:=i
        else
        << w:=w #+ i #+ i #+ 1;
           while w #> p do w:=w #- p;
           i:=i#+1;
         >>;
    if null r then typerr({'sqrt,a},"expression mod p");
    return r;
   end;

symbolic procedure modsqrt2(a,p);
 % General algorithm for arbitrary prime p:
 % H. Cohen: Computational Algebraic Number theory, 1.5.1
   begin integer a,b,m,r,y,e,p,q,tt,n,p!-1,x,z;
     x:=a; p!-1:=p-1;
     q:=p-1;
     while evenp q do <<q:=q/2;e:=e+1>>;

 s1: repeat n:=random(p) until legendre!-symbol(n,p)=p!-1;
     z:=general!-modular!-expt(n,q);

 s2: y:=z; r:=e; x:=general!-modular!-expt(a,(q-1)/2);
     b:=modp(a*x*x,p); x:=modp(a*x,p);

 s3: if modp(b,p)=1 then return x;
     m:=0;
     repeat m:=m+1 until general!-modular!-expt(b,expt(2,m)) = 1 or m=r;
     if m=r then typerr({'sqrt,a},"expression mod p");

 s4: tt:= general!-modular!-expt(y,expt(2,r-m-1));
     y:= general!-modular!-times(tt,tt); r:=m;
     x:=general!-modular!-times(x,tt);
     b:=general!-modular!-times(b,y);
     goto s3;
   end;

symbolic procedure modsqrt!*(u);
    %  print {"we got through:", u};
    !*modular2f modsqrt(cdr u,current!-modulus);

put('sqrt,'!:mod!:,'modsqrt!*);

algebraic operator legendre_symbol;

symbolic procedure legendre!-symbol(a,p);
   general!-modular!-expt(a,(p-1)/2);

algebraic procedure legendre_symbol1(a,p);
 begin scalar !*modular,current!-modulus,dmode!*,res;
   if p=1 then return 1;
   if primep p and remainder(p,2) =1
     then <<res := (p-1)/2; on modular; setmod p;
            res := lisp general!-modular!-expt(a,res);
            off modular>>
    else rederr(
       "The second argument to legendre_symbol must be an odd prime");
   return( if res=p-1 then -1 else res);
 end;

algebraic let legendre_symbol(~a,~p)
       => legendre_symbol1(a,p) when fixp a and fixp p;

endmodule;

end;
