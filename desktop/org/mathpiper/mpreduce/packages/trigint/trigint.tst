% some test examples for the trigint package. If the input expression is
% free of sin, cos or tan at the moment, then no Weierstrass substitutions
% will be made, and the standard int operator is called.

trigint(1/x,x);
trigint(1,y);
trigint(sin(x),x);
trigint(1/(cos(x)+2),x);
trigint(1/(cos(x)-2),x);
trigint(1/(sin(x)),x);
trigint(1/(sin(x)+2),x);

trigint(15/(cos(x)*(5-4*cos(x))),x);

trigint(3/(5+4*sin(x)),x);
trigint(3/(5-4*cos(x)),x);

trigint(tan(x),x);
%trigint(sqrt(cos(x)),x);

on tracetrig;
trigint(1/(cos(x)-5),x);
trigint(1/(sqrt(sin(x))),x); 

end;
