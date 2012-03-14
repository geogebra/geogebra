% examples for trigsimp.tex
linelength 60;

trigsimp(sin(x)^2+cos(x)^2);
trigsimp(sin(x)^2);
trigsimp(sin(x)^2, cos);
trigsimp(sin(2x+y));
trigsimp(sin(x)*cos(y), combine);
trigsimp((1-sin(x)^2)^20*(1-cos(x)^2)^20, compact);
trigsimp(sin(x), hyp);
trigsimp(sinh(x), expon);
trigsimp(e^x, trig);
trigsimp(tan(x+y), keepalltrig);
trigsimp(tan(x+y), tan);
trigsimp(csc x - cot x + csc y - cot y, x/2, y/2, tan);
trigsimp(sin(x)^4, cos, combine);
trigsimp((sinh(x)+cosh(x))^n+(cosh(x)-sinh(x))^n, expon);
trigsimp(ws, hyp);
trigsimp((cosh(a*n)*sinh(a)*sinh(p)+cosh(a)*sinh(a*n)*sinh(p)+
   sinh(a - p)*sinh(a*n))/sinh(a));
trigsimp(ws, combine);
trigsimp( { sin(2x) = cos(2x) } );

trigfactorize(sin(x), x/2);
trigfactorize(1+cos(x), x);
trigfactorize(1+cos(x), x/2);
trigfactorize(sin(2x)*sinh(2x), x);
on nopowers;
trigfactorize(1+cos(x), x/2);
off nopowers;

triggcd(sin(x), 1+cos(x), x/2);
triggcd(sin(x), 1+cos(x), x);
triggcd(sin(2x)*sinh(2x), (1-cos(2x))*(1+cosh(2x)), x);

trigsimp(tan(x)*tan(y));
trigsimp(ws, combine);
trigsimp((sin(x-a)+sin(x+a))/(cos(x-a)+cos(x+a)));
trigsimp(cosh(n*acosh(x))-cos(n*acos(x)), trig);
trigsimp(sec(a-b), keepalltrig);
trigsimp(tan(a+b), keepalltrig);
trigsimp(ws, keepalltrig, combine);
df(sqrt(1+cos(x)), x, 4);
on rationalize;
trigsimp(ws);
off rationalize;
load_package taylor;
taylor(sin(x+a)*cos(x+b), x, 0, 4);
trigsimp(ws, combine);
int(trigsimp(sin(x+y)*cos(x-y)*tan(x)), x);
int(trigsimp(sin(x+y)*cos(x-y)/tan(x)), x);
trigfactorize(sin(2x)*cos(y)^2, y/2);
trigfactorize(sin(y)^4-x^2, y);
trigfactorize(sin(x)*sinh(x), x/2);
triggcd(-5+cos(2x)-6sin(x), -7+cos(2x)-8sin(x), x/2);
triggcd(1-2cosh(x)+cosh(2x), 1+2cosh(x)+cosh(2x), x/2);

end;
