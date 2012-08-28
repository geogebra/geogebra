
comment 

  Testing some integrals of inverse trigonometric and hyperbolic functions

end;

int(asin(x),x);

int(acos(x),x);

int(asinh(x),x);

int(acosh(x),x);

int(acos(x-1),x);

int(acos(x)*x,x);

int(x^2*asin(3*x),x);

int(x^2*acos(3*x),x);

int(x^2*asinh(3*x),x);

int(x^2*acosh(3*x),x);

int(asin(3*x)/x^3,x);

int(acos(3*x)/x^3,x);

int(asinh(3*x)/x^3,x);

int(acosh(3*x)/x^3,x);


% The following is correct, but not optimal

int(x*acos(x-1),x);

df(ws,x);

int(x*acos(3*x-2),x);

df(ws,x);

end;
