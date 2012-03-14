% Test file for TrigSimp package

%-------------------------TrigSimp--------------------------

trigsimp(tan(x+y), keepalltrig);
trigsimp(ws, keepalltrig, combine);
trigsimp(sin(5x-9y));
trigsimp(ws, combine);
trigsimp(cos(10x), cos);
trigsimp(cos(10x), sin);
trigsimp((sin(x-a)+sin(x+a))/(cos(x-a)+cos(x+a)));
trigsimp(cos(6x+4y), sin);
trigsimp(ws, expon);
trigsimp(ws, hyp);
trigsimp(ws, combine);
trigsimp(ws, trig, combine);
trigsimp(sqrt(1-cos(2x)));
trigsimp(sin(x)^20*cos(x)^20, sin);
trigsimp(sin(x)^20*cos(x)^20, cos);
trigsimp(sin(x)^20*cos(x)^20, compact);
trigsimp(sin(x)^10, combine);
trigsimp(ws, hyp);
trigsimp(ws, expon);
trigsimp(ws, trig);
int(sin(x+y)*cos(x-y)*tan(x), x);
int(trigsimp(sin(x+y)*cos(x-y)*tan(x)), x);
% int(sin(x+y)*cos(x-y)/tan(x), x) hangs
int(trigsimp(sin(x+y)*cos(x-y)/tan(x)), x);
trigsimp(2tan(x)*(sec(x)^2 - tan(x)^2 - 1));
on rationalize;
df(sqrt(1+cos(x)), x, 4);
off rationalize;
trigsimp(ws);
df(2cos((x+y)/2)*cos((x-y)/2), x);
trigsimp(ws, combine);
df(int(1/cos(x), x), x);
trigsimp(ws, combine);
trigsimp(cos(100x));
trigsimp(ws, combine);
trigsimp(sinh(3a+4b-5c)*cosh(3a-5b-6c));
trigsimp(ws, combine);
trigsimp(sec(20x-y), keepalltrig);
trigsimp(csc(10a-9b), keepalltrig);
trigsimp(ws, combine);
trigsimp(cosh(50*acosh(x))-cos(50*acos(x)));
trigsimp(cos(n*acos(x))-cosh(n*acosh(x)), trig);
trigsimp((2tan(log(x))*(sec(log(x))^2 - tan(log(x))^2 - 1))/x);
trigsimp(sech(10x), keepalltrig);
trigsimp(ws, combine);
trigsimp(csch(3x-5y), keepalltrig);
trigsimp(ws, combine);
off precise;
trigsimp((sinh(x)+cosh(x))^n+(cosh(x)-sinh(x))^n, expon);
on precise;
trigsimp(ws, hyp);
load_package taylor;
taylor(sin(x+a)*cos(x+b), x, 0, 4);
trigsimp(ws, combine);

%-----------------------TrigFactorize-----------------------

on nopowers;  % for comparison with version 2.0
trigfactorize(sin(x)**2, x);
trigfactorize(1+cos(x), x);
trigfactorize(1+cos(x), x/2);
trigfactorize(1+cos(x), x/6);
trigfactorize(sin(x)*(1-cos(x)), x);
trigfactorize(sin(x)*(1-cos(x)), x/2);
trigfactorize(tan(x), x);
trigfactorize(sin(x*3), x);
trigfactorize(sin(4x)-1, x);
trigfactorize(sin(x)**4-1, x);
trigfactorize(cos(x)**4-1, x);
trigfactorize(sin(x)**10-cos(x)**6, x);
trigfactorize(sin(x)*cos(y), x);
trigfactorize(sin(2x)*cos(y)**2, y/2);
trigfactorize(sin(y)**4-x**2, y);
trigfactorize(sin(x), x+1);
trigfactorize(sin(x), 2x);
trigfactorize(sin(x)*cosh(x), x/2);
trigfactorize(1+cos(2x)+2cos(x)*cosh(x), x/2);

%-------------------------TrigGCD---------------------------

triggcd(sin(x), cos(x), x);
triggcd(1-cos(x)^2, sin(x)^2, x);
triggcd(sin(x)^4-1, cos(x)^2, x);
triggcd(sin(5x+1), cos(x), x);
triggcd(1-cos(2x), sin(2x), x);
triggcd(-5+cos(2x)-6sin(x), -7+cos(2x)-8sin(x), x/2);
triggcd(1-2cosh(x)+cosh(2x), 1+2cosh(x)+cosh(2x), x/2);
triggcd(1+cos(2x)+2cos(x)*cosh(x), 1+2cos(x)*cosh(x)+cosh(2x), x/2);
triggcd(-1+2a*b+cos(2x)-2a*sin(x)+2b*sin(x),
   -1-2a*b+cos(2x)-2a*sin(x)-2b*sin(x), x/2);
triggcd(sin(x)^10-1, cos(x), x);
triggcd(sin(5x)+sin(3x), cos(x), x);
triggcd(sin(3x)+sin(5x), sin(5x)+sin(7x), x);

%-----------------------------------------------------------

% New facilities in version 2

%-----------------------------------------------------------

% TrigSimp applied to non-scalars data structures:
trigsimp( sin(2x) = cos(2x) );
trigsimp( { sin(2x), cos(2x) } );
trigsimp( { sin(2x) = cos(2x) } );
trigsimp( mat((sin(2x),cos(2x)),
              (csc(2x),sec(2x))) );

% An amusing identify:
trigsimp(csc x - cot x - tan(x/2));

% which could be DERIVED like this:
trigsimp(csc x - cot x, x/2, tan);

% A silly illustration of multiple additional trig arguments:
trigsimp(csc x - cot x, x/2, x/3);

% A more useful illustration of multiple additional trig arguments:
trigsimp(csc x - cot x + csc y - cot y, x/2, y/2, tan);

%-----------------------------------------------------------

% New TrigFactorize facility:
off nopowers;  % REDUCE 3.7 default, gives more compact output ...
trigfactorize(sin(x)^2, x);
trigfactorize(1+cos(x), x);
trigfactorize(1+cos(x), x/2);
trigfactorize(1+cos(x), x/6);
trigfactorize(sin(x)*(1-cos(x)), x);
trigfactorize(sin(x)*(1-cos(x)), x/2);
trigfactorize(tan(x), x);
trigfactorize(sin(3x), x);
trigfactorize(sin(4x) - 1, x);
trigfactorize(sin(x)^4 - 1, x);
trigfactorize(cos(x)^4 - 1, x);
trigfactorize(sin(x)^10 - cos(x)^6, x);
trigfactorize(sin(x)*cos(y), x);
trigfactorize(sin(2x)*cos(y)^2, y/2);
trigfactorize(sin(y)^4 - x^2, y);
trigfactorize(sin(x), x+1);
trigfactorize(sin(x), 2x);
trigfactorize(sin(x)*cosh(x), x/2);
trigfactorize(1 + cos(2x) + 2cos(x)*cosh(x), x/2);

end;
