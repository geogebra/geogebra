COMMENT

                 THE REDUCE INTEGRATION TEST PACKAGE

                              Edited By

                           Anthony C. Hearn
			 The RAND Corporation


This file is designed to provide a set of representative tests of the
Reduce integration package.  Not all examples go through, even when an
integral exists, since some of the arguments are outside the domain of
applicability of the current package.  However, future improvements to
the package will result in more closed-form evaluations in later
releases.  We would appreciate any additional contributions to this test
file either because they illustrate some feature (good or bad) of the
current package, or suggest domains which future versions should handle.
Any suggestions for improved organization of this test file (e.g., in a
way which corresponds more directly to the organization of a standard
integration table book such as Gradshteyn and Ryznik) are welcome.

Acknowledgments:

The examples in this file have been contributed by the following.
Any omissions to this list should be reported to the Editor.

David M. Dahm
James H. Davenport
John P. Fitch
Steven Harrington
Anthony C. Hearn
K. Siegfried Koelbig
Ernst Krupnikov
Arthur C. Norman
Herbert Stoyan
;

Comment we first set up a suitable testing functions;

fluid '(gcknt!*);

global '(faillist!* gcnumber!* inittime number!-of!-integrals
         unintlist!*);

symbolic operator time;

symbolic procedure initialize!-integral!-test;
   begin
      faillist!* := unintlist!* := nil;
      number!-of!-integrals := 0;
      gcnumber!* := gcknt!*;
      inittime := time()
   end;   

symbolic procedure summarize!-integral!-test;
   begin scalar totaltime;
      totaltime := time()-inittime;
      prin2t 
   "                *****     SUMMARY OF INTEGRAL TESTS     *****";
      terpri();
      prin2 "Number of integrals tested: ";
      prin2t number!-of!-integrals;
      terpri();
      prin2 "Total time taken: ";
      prin2 totaltime;
      prin2t " ms";
      terpri();
      if gcnumber!*
        then <<prin2 "Number of garbage collections: ";
               prin2t (gcknt!* - gcnumber!*);
               terpri()>>;
      prin2 "Number of incorrect integrals: ";
            prin2t length faillist!*;
      terpri();
      prin2 "Number of unevaluated integrals: ";
      prin2t length unintlist!*;
      terpri();
      if faillist!* 
        then <<prin2t "Integrands of incorrect integrals are:";
               for each x in reverse faillist!* do mathprint car x>>;
      if unintlist!*
        then <<prin2t "Integrands of unevaluated integrals are:";
               terpri();
               for each x in reverse unintlist!* do mathprint car x>>
   end;

procedure testint(a,b);
  begin scalar der,diffce,res,tt;
      tt:=time();
      symbolic (number!-of!-integrals := number!-of!-integrals + 1);
      res:=int(a,b);
%     write "time for integral:  ",time()-tt," ms";
      off precise;
      der := df(res,b);
      diffce := der-a;
      if diffce neq 0
	then  begin for all x let cot x=cos x/sin x,
				  sec x=1/cos x,
				  sin x**2=1-cos x**2,
				  tan(x/2)=sin x/(1+cos x),
				  tan x=sin x/cos x,
		    		  tanh x=
				     (e**(x)-e**(-x))/(e**x+e**(-x)),
				  coth x= 1/tanh x;
	       	    diffce := diffce;
		    for all x clear cot x,sec x,sin x**2,tan x,tan(x/2),
				    tanh x,coth x
	      end;
	%hopefully, difference appeared non-zero due to absence of
	%above transformations;
      if diffce neq 0
	then <<on combineexpt; diffce := diffce; off combineexpt>>;
      if diffce neq 0
	then begin scalar !*reduced;
		symbolic(!*reduced := t);
		for all x let cos(2x)= 1-2sin x**2, sin x**2=1-cos x**2;
		diffce := diffce;
	       for all x clear cos(2x),sin x**2
	     end;
      if diffce neq 0
	then <<write 
       "     ***** DERIVATIVE OF INTEGRAL NOT EQUAL TO INTEGRAND *****";
	       symbolic(faillist!* := list(a,b,res,der) . faillist!*)>>;
               symbolic if smemq('int,res)
                    then unintlist!* := list(a,b,res) . unintlist!*;
      on precise;
      return res
  end;

symbolic initialize!-integral!-test();

% References are to Gradshteyn and Ryznik.

testint(1+x+x**2,x);
testint(x**2*(2*x**2+x)**2,x);
testint(x*(x**2+2*x+1),x);
testint(1/x,x);  % 2.01 #2;
testint((x+1)**3/(x-1)**4,x);
testint(1/(x*(x-1)*(x+1)**2),x);
testint((a*x+b)/((x-p)*(x-q)),x);
testint(1/(a*x**2+b*x+c),x);
testint((a*x+b)/(1+x**2),x);
testint(1/(x**2-2*x+3),x);

% Rational function examples from Hardy, Pure Mathematics, p 253 et seq.

testint(1/((x-1)*(x**2+1))**2,x);
testint(x/((x-a)*(x-b)*(x-c)),x);
testint(x/((x**2+a**2)*(x**2+b**2)),x);
testint(x**2/((x**2+a**2)*(x**2+b**2)),x);
testint(x/((x-1)*(x**2+1)),x);
testint(x/(1+x**3),x);
testint(x**3/((x-1)**2*(x**3+1)),x);
testint(1/(1+x**4),x);
testint(x**2/(1+x**4),x);
testint(1/(1+x**2+x**4),x);

% Examples involving a+b*x.

z := a+b*x;

testint(z**p,x);
testint(x*z**p,x);
testint(x**2*z**p,x);
testint(1/z,x);
testint(1/z**2,x);
testint(x/z,x);
testint(x**2/z,x);
testint(1/(x*z),x);
testint(1/(x**2*z),x);
testint(1/(x*z)**2,x);
testint(1/(c**2+x**2),x);
testint(1/(c**2-x**2),x);

% More complicated rational function examples, mostly contributed
% by David M. Dahm, who also developed the code to integrate them.

testint(1/(2*x**3-1),x);
testint(1/(x**3-2),x);
testint(1/(a*x**3-b),x);
testint(1/(x**4-2),x);
testint(1/(5*x**4-1),x);
testint(1/(3*x**4+7),x);
testint(1/(x**4+3*x**2-1),x);
testint(1/(x**4-3*x**2-1),x);
testint(1/(x**4-3*x**2+1),x);
testint(1/(x**4-4*x**2+1),x);
testint(1/(x**4+4*x**2+1),x);
testint(1/(x**4+x**2+2),x);
testint(1/(x**4-x**2+2),x);
testint(1/(x**6-1),x);
testint(1/(x**6-2),x);
testint(1/(x**6+2),x);
testint(1/(x**8+1),x);
testint(1/(x**8-1),x);
testint(1/(x**8-x**4+1),x);
testint(x**7/(x**12+1),x);

% Examples involving logarithms.

testint(log x,x);
testint(x*log x,x);
testint(x**2*log x,x);
testint(x**p*log x,x);
testint((log x)**2,x);
testint(x**9*log x**11,x);
testint(log x**2/x,x);
testint(1/log x,x);
testint(1/log(x+1),x);
testint(1/(x*log x),x);
testint(1/(x*log x)**2,x);
testint((log x)**p/x,x);
testint(log x *(a*x+b),x);
testint((a*x+b)**2*log x,x);
testint(log x/(a*x+b)**2,x);
testint(x*log (a*x+b),x);
testint(x**2*log(a*x+b),x);
testint(log(x**2+a**2),x);
testint(x*log(x**2+a**2),x);
testint(x**2*log(x**2+a**2),x);
testint(x**4*log(x**2+a**2),x);
testint(log(x**2-a**2),x);
testint(log(log(log(log(x)))),x);

% Examples involving circular functions.

testint(sin x,x); % 2.01 #5;
testint(cos x,x); %     #6;
testint(tan x,x); %     #11;
testint(1/tan(x),x); % 2.01 #12;
testint(1/(1+tan(x))**2,x);
testint(1/cos x,x);
testint(1/sin x,x);
testint(sin x**2,x);
testint(x**3*sin(x**2),x);
testint(sin x**3,x);
testint(sin x**p,x);
testint((sin x**2+1)**2*cos x,x);
testint(cos x**2,x);
testint(cos x**3,x);
testint(sin(a*x+b),x);
testint(1/cos x**2,x);
testint(sin x*sin(2*x),x);
testint(x*sin x,x);
testint(x**2*sin x,x);
testint(x*sin x**2,x);
testint(x**2*sin x**2,x);
testint(x*sin x**3,x);
testint(x*cos x,x);
testint(x**2*cos x,x);
testint(x*cos x**2,x);
testint(x**2*cos x**2,x);
testint(x*cos x**3,x);
testint(sin x/x,x);
testint(cos x/x,x);
testint(sin x/x**2,x);
testint(sin x**2/x,x);
testint(tan x**3,x);
% z := a+b*x;
testint(sin z,x);
testint(cos z,x);
testint(tan z,x);
testint(1/tan z,x);
testint(1/sin z,x);
testint(1/cos z,x);
testint(sin z**2,x);
testint(sin z**3,x);
testint(cos z**2,x);
testint(cos z**3,x);
testint(1/cos z**2,x);
testint(1/(1+cos x),x);
testint(1/(1-cos x),x);
testint(1/(1+sin x),x);
testint(1/(1-sin x),x);
testint(1/(a+b*sin x),x);
testint(1/(a+b*sin x+cos x),x);
testint(x**2*sin z**2,x);
testint(cos x*cos(2*x),x);
testint(x**2*cos z**2,x);
testint(1/tan x**3,x);
testint(x**3*tan(x)**4,x);
testint(x**3*tan(x)**6,x);
testint(x*tan(x)**2,x);
testint(sin(2*x)*cos(3*x),x);
testint(sin x**2*cos x**2,x);
testint(1/(sin x**2*cos x**2),x);
testint(d**x*sin x,x);
testint(d**x*cos x,x);
testint(x*d**x*sin x,x);
testint(x*d**x*cos x,x);
testint(x**2*d**x*sin x,x);
testint(x**2*d**x*cos x,x);
testint(x**3*d**x*sin x,x);
testint(x**3*d**x*cos x,x);
testint(sin x*sin(2*x)*sin(3*x),x);
testint(cos x*cos(2*x)*cos(3*x),x);
testint(sin(x*kx)**3*x**2,x);
testint(x*cos(xi/sin(x))*cos(x)/sin(x)**2,x);

% Mixed angles and half angles.

int(cos(x)/(sin(x)*tan(x/2)),x);

% This integral produces a messy result because the code for
% converting half angle tans to sin and cos is not effective enough.

testint(sin(a*x)/(b+c*sin(a*x))**2,x);

% Examples involving logarithms and circular functions.

testint(sin log x,x);
testint(cos log x,x);

% Examples involving exponentials.

testint(e**x,x); % 2.01 #3;
testint(a**x,x); % 2.01 #4;
testint(e**(a*x),x);
testint(e**(a*x)/x,x);
testint(1/(a+b*e**(m*x)),x);
testint(e**(2*x)/(1+e**x),x);
testint(e**(2*x)*e**(a*x),x);
testint(1/(a*e**(m*x)+b*e**(-m*x)),x);
testint(x*e**(a*x),x);
testint(x**20*e**x,x);
testint(a**x/b**x,x);
testint(a**x*b**x,x);
testint(a**x/x**2,x);
testint(x*a**x/(1+b*x)**2,x);
testint(x*e**(a*x)/(1+a*x)**2,x);
testint(x*k**(x**2),x);
testint(e**(x**2),x);
testint(2**(x**2),x);
testint(2**(2*x**2),x);
testint(e**(a*x**2),x);
testint(e**(a**2*x**2),x);
testint(x*e**(x**2),x);
testint((x+1)*e**(1/x)/x**4,x);
testint((2*x**3+x)*(e**(x**2))**2*e**(1-x*e**(x**2))/(1-x*e**(x**2))**2,
	x);
testint(e**(e**(e**(e**x))),x);

% Examples involving exponentials and logarithms.

testint(e**x*log x,x);
testint(x*e**x*log x,x);
testint(e**(2*x)*log(e**x),x);

% Examples involving square roots.

testint(sqrt(2)*x**2 + 2*x,x);
testint(log x/sqrt(a*x+b),x);
u:=sqrt(a+b*x);
v:=sqrt(c+d*x);
testint(u*v,x);
testint(u,x);
testint(x*u,x);
testint(x**2*u,x);
testint(u/x,x);
testint(u/x**2,x);
testint(1/u,x);
testint(x/u,x);
testint(x**2/u,x);
testint(1/(x*u),x);
testint(1/(x**2*u),x);
testint(u**p,x);
testint(x*u**p,x);
testint(atan((-sqrt(2)+2*x)/sqrt(2)),x);
testint(1/sqrt(x**2-1),x);
testint(sqrt(x+1)*sqrt x,x);

testint(sin(sqrt x),x);
testint(x*(1-x^2)^(-9/4),x);
testint(x/sqrt(1-x^4),x);
testint(1/(x*sqrt(1+x^4)),x);
testint(x/sqrt(1+x^2+x^4),x);
testint(1/(x*sqrt(x^2-1-x^4)),x);

% Examples from James Davenport's thesis:

testint(1/sqrt(x**2-1)+10/sqrt(x**2-4),x);      % p. 173
testint(sqrt(x+sqrt(x**2+a**2))/x,x);

% Examples generated by differentiating various functions.

testint(df(sqrt(1+x**2)/(1-x),x),x);
testint(df(log(x+sqrt(1+x**2)),x),x);
testint(df(sqrt(x)+sqrt(x+1)+sqrt(x+2),x),x);
testint(df(sqrt(x**5-2*x+1)-sqrt(x**3+1),x),x);

% Another such example from James Davenport's thesis (p. 146).
% It contains a point of order 3, which is found by use of Mazur's
% bound on the torsion of elliptic curves over the rationals;

testint(df(log(1+sqrt(x**3+1)),x),x);

% Examples quoted by Joel Moses:

testint(1/sqrt(2*h*r**2-alpha**2),r);
testint(1/(r*sqrt(2*h*r**2-alpha**2-epsilon**2)),r);
testint(1/(r*sqrt(2*h*r**2-alpha**2-2*k*r)),r);
testint(1/(r*sqrt(2*h*r**2-alpha**2-epsilon**2-2*k*r)),r);
testint(r/sqrt(2*e*r**2-alpha**2),r);
testint(r/sqrt(2*e*r**2-alpha**2-epsilon**2),r);
testint(r/sqrt(2*e*r**2-alpha**2-2*k*r**4),r);
testint(r/sqrt(2*e*r**2-alpha**2-2*k*r),r);

% These two integrals will evaluate, but they take a very long time
% and the results are messy (compared with the algint results).

% testint(1/(r*sqrt(2*h*r**2-alpha**2-2*k*r**4)),r);
% testint(1/(r*sqrt(2*h*r**2-alpha**2-epsilon**2-2*k*r**4)),r);


Comment many of these integrals used to require Steve Harrington's
	code to evaluate. They originated in Novosibirsk as examples
	of using Analytik. There are still a few examples that could
	be evaluated using better heuristics;

testint(a*sin(3*x+5)**2*cos(3*x+5),x);
testint(log(x**2)/x**3,x);
testint(x*sin(x+a),x);
testint((log(x)*(1-x)-1)/(e**x*log(x)**2),x);
testint(x**3*(a*x**2+b)**(-1),x);
testint(x**(1/2)*(x+1)**(-7/2),x);
testint(x**(-1)*(x+1)**(-1),x);
testint(x**(-1/2)*(2*x-1)**(-1),x);
testint((x**2+1)*x**(1/2),x);
testint(x**(-1)*(x-a)**(1/3),x);
testint(x*sinh(x),x);
testint(x*cosh(x),x);
testint(sinh(2*x)/cosh(2*x),x);
testint((i*eps*sinh x-1)/(eps*i*cosh x+i*a-x),x);
testint(sin(2*x+3)*cos(x)**2,x);
testint(x*atan(x),x);
testint(x*acot(x),x);
testint(x*log(x**2+a),x);
testint(sin(x+a)*cos(x),x);
testint(cos(x+a)*sin(x),x);
testint((1+sin(x))**(1/2),x);
testint((1-sin(x))**(1/2),x);
testint((1+cos(x))**(1/2),x);
testint((1-cos(x))**(1/2),x);
testint(1/(x**(1/2)-(x-1)**(1/2)),x);
testint(1/(1-(x+1)**(1/2)),x);
testint(x/(x**4+36)**(1/2),x);
testint(1/(x**(1/3)+x**(1/2)),x);
testint(log(2+3*x**2),x);
testint(cot(x),x);
testint(cot x**4,x);
testint(tanh(x),x);
testint(coth(x),x);
testint(b**x,x);
testint((x**4+x**(-4)+2)**(1/2),x);
testint((2*x+1)/(3*x+2),x);
testint(x*log(x+(x**2+1)**(1/2)),x);
testint(x*(e**x*sin(x)+1)**2,x);
testint(x*e**x*cos(x),x);

Comment the following set came from Herbert Stoyan;

testint(1/(x-3)**4,x);
testint(x/(x**3-1),x);
testint(x/(x**4-1),x);
testint(log(x)*(x**3+1)/(x**4+2),x);
testint(log(x)+log(x+1)+log(x+2),x);
testint(1/(x**3+5),x);
testint(1/sqrt(1+x**2),x);
testint(sqrt(x**2+3),x);
testint(x/(x+1)**2,x);

COMMENT The following integrals were used among others as a test of
	Moses' SIN program;

testint(asin x,x);
testint(x**2*asin x,x);
testint(sec x**2/(1+sec x**2-3*tan x),x);
testint(1/sec x**2,x);
testint((5*x**2-3*x-2)/(x**2*(x-2)),x);
testint(1/(4*x**2+9)**(1/2),x);
testint((x**2+4)**(-1/2),x);
testint(1/(9*x**2-12*x+10),x);
testint(1/(x**8-2*x**7+2*x**6-2*x**5+x**4),x);
testint((a*x**3+b*x**2+c*x+d)/((x+1)*x*(x-3)),x);
testint(1/(2-log(x**2+1))**5,x);

% The next integral appeared in Risch's 1968 paper.

testint(2*x*e**(x**2)*log(x)+e**(x**2)/x+(log(x)-2)/(log(x)**2+x)**2+
    ((2/x)*log(x)+(1/x)+1)/(log(x)**2+x),x);

% The following integral would not evaluate in REDUCE 3.3.

testint(exp(x*ze+x/2)*sin(pi*ze)**4*x**4,ze);

% This one evaluates:

testint(erf(x),x);

% So why not this one?

testint(erf(x+a),x);

Comment here is an example of using the integrator with pattern
	matching;

for all m,n let int(k1**m*log(k1)**n/(p**2-k1**2),k1)=foo(m,n),
		int(k1*log(k1)**n/(p**2-k1**2),k1)=foo(1,n),
		int(k1**m*log(k1)/(p**2-k1**2),k1)=foo(m,1),
		int(k1*log(k1)/(p**2-k1**2),k1)=foo(1,1),
		int(log(k1)**n/(k1*(p**2-k1**2)),k1)=foo(-1,n);

int(k1**2*log(k1)/(p**2-k1**2),k1);

Comment It is interesting to see how much of this one can be done;

let f1s= (12*log(s/mc**2)*s**2*pi**2*mc**3*(-8*s-12*mc**2+3*mc)
	+ pi**2*(12*s**4*mc+3*s**4+176*s**3*mc**3-24*s**3*mc**2
	-144*s**2*mc**5-48*s*mc**7+24*s*mc**6+4*mc**9-3*mc**8))
	 /(384*e**(s/y)*s**2);

int(f1s,s);

factor ei,log;

ws;

Comment the following is an example of integrals that used to loop
        forever.  They were first revealed by problems with Bessel
	function integration when specfn was loaded,
	e.g., int(x*besseli(2,x),x) or int(besselj(n,x),x);

operator f; let {df(f(~x),x) => x*f(x-1)};

int(f x,x);


Comment the following integrals reveal deficiencies in the current
integrator;

%high degree denominator;
%testint(1/(2-log(x**2+1))**5,x);

%this example should evaluate;
testint(sin(2*x)/cos(x),x);

%this example, which appeared in Tobey's thesis, needs factorization
%over algebraic fields. It currently gives an ugly answer and so has
%been suppressed;

% testint((7*x**13+10*x**8+4*x**7-7*x**6-4*x**3-4*x**2+3*x+3)/
%         (x**14-2*x**8-2*x**7-2*x**4-4*x**3-x**2+2*x+1),x);

symbolic summarize!-integral!-test();

end;
