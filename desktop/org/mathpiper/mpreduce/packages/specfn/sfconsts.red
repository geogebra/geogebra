module sfconsts; % Constants from pecial functions such as Euler_Gamma,
                 % Catalan, Khinchin.

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


algebraic let Euler_gamma => compute_Euler_gamma();

symbolic flag('(compute_Euler_gamma),'opfn);

symbolic procedure compute_Euler_gamma ();

  if not(!*rounded) then mk!*sq('((((Euler_gamma . 1) . 1)) . 1))
         else aeval '(minus (psi 1));

%%%%%%%%%%%%%%%

algebraic let  Golden_Ratio = (1 + sqrt(5))/2; % for Architects

%%%%%%%%%%%%%%%%

fluid '(intlogrem);

Comment this program is taken from:

COMPUTATION OF CATALAN'S CONSTANT USING RAMANUJAN'S FORMULA

Greg J. Fee, Dept of Comp Sci, U of Waterloo,

published in ISSAC '90, ACM press ;

algebraic let catalan => compute_catalan();

symbolic flag('(compute_catalan),'opfn);

symbolic procedure compute_catalan ();

  if not(!*rounded) then mk!*sq('((((catalan . 1) . 1)) . 1)) else
   begin scalar ii,j,p,tt,s,g,!*rounded;

      g := !:prec!: + length explode !:prec!: + 3;
      p := 10^g/2;
      tt := p; s := tt; j :=1; ii := 1;

      while tt > 0 do
        << j := j+2; p := (p*ii) / j; tt := (tt * ii + p)/j;
           s := s + tt; ii := ii + 1 >>;

      return list('quotient,s,10^(g));
  end;

%%%%%%%%%%%%%%%%%%%%

algebraic <<

% Khinchin's constant =(prod((1+1/(n*(n+2)))^(ln n/ln2),n,1,infinity))
%
% translated from a (Maple code) posting by Paul Zimmermann
%       in sci.math.symbolic
%
let Khinchin => compute_Khinchin();

symbolic procedure compute_Khinchin();

 (if not(!*rounded) then mk!*sq('((((Khinchin . 1) . 1)) . 1)) else
    aeval ('compute_Khinchin1 . NIL)) where !:prec!: = !:prec!: ;

symbolic flag('(compute_Khinchin intlog),'opfn);

procedure compute_Khinchin1();
  begin scalar term,summ,acc,k,ln2,ln3,oldprec,zp;
     if evenp(oldprec := precision 0) then
             precision (oldprec+13) else
             precision (oldprec+12);
     acc := 10^(-oldprec -3);
     ln2 := log 2;
     ln3 := log 3;
     k:=2;
     term :=1; summ :=0;
     while abs(term) > acc do <<
        zp := Zetaprime(k);
        term :=(-1)^k*(2*zp-2^k*(zp+ln2/2^k+ln3/3^k))/k;
        summ := summ + term;
        k:=k+1   >>;

     summ := e^(summ /ln2+ln3/ln2*(2./3-log(5/3))+1-ln2);
     precision(oldprec);
     return summ;
  end;

procedure Zetaprime (u);

  begin scalar term,summ,n,acc,f,j,logm,m,oldprec;
     oldprec := precision 0;
     precision(oldprec+5);
     n:= u;
     lisp setq(intlogrem,nil);
     f := -df(log(x)/x^n,x)/2;
     m:= (oldprec+5)/2;
     logm := log(m);
     term := logm;
     acc := 10^(1-(oldprec +5))/2;
     j:=1;
     summ := -(for ii:=2:(fix m -1) sum intlog(ii)/ii^n) -
        (logm+1/(n-1))/m^(n-1)/(n-1)-logm/2/m^n;
     while abs(term) > acc do
          << term := Bernoulli(2*j) * sub(log(x)=logm,x=m,f);
             f := df(f,x,x)/((4j+6)*j +2);
             summ := summ -term;
             j:= j+1;
           >>;
     precision oldprec;
     return summ;
  end;

symbolic procedure intlog(n);

  ( if found := atsoc(n,intlogrem) then cdr found else
       << found := intlog1 n;
    intlogrem := (( n . found)  . intlogrem);
          found >>) where found = nil;

symbolic procedure intlog1 (n);

 (if n=2 or n=3 or n=4 or n=5 or n=7 then
                rdlog!* ('!:rd!: . (n . 0)) else
  if cdr(div := divide(n,2)) #= 0 then
                rd!:plus(intlog 2,intlog(car div)) else
  if cdr(div := divide(n,3)) #= 0 then
                rd!:plus(intlog 3,intlog(car div)) else
  if cdr(div := divide(n,5)) #= 0 then
                rd!:plus(intlog 5,intlog(car div)) else
  if cdr(div := divide(n,7)) #= 0 then
                rd!:plus(intlog 7,intlog(car div)) else
   rdlog!* ('!:rd!: . (n . 0))) where div = nil;

>>;
endmodule;

end;





