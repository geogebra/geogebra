load_package defint;

% used to do a car/cdr of nil
int(xxx,x,-infinity, infinity);

df(erf(x),x);

int(ws,x,-infinity, infinity);

int(exp(-x^2)/sqrt(pi),x,-infinity, infinity);

exp(-x^2)/sqrt(pi);

int(ws,x,-infinity, infinity);

int(exp(-x^2)/sqrt(pi)/y,x,-infinity, infinity);

exp(-x^2-x)/sqrt(pi);

int(ws,x,-infinity, infinity);

erf(x+1/2);

df(ws,x);

% doesn't work
int(ws,x,-infinity, infinity);

e^(1/4)*erf(x+1/2);

df(ws,x);

% nor does this
int(ws,x,-infinity, infinity);

erf(sqrt(log(2))*x);

df(ws,x);

% or this
int(ws,x,-infinity, infinity);

int(exp(-5*x^2),x,-infinity, infinity);

int(exp(-sqrt(2)*x^2),x,-infinity, infinity);

% the following is not recognized as exponential
int(2^(-x^2),x,-infinity, infinity);

% standard simplification turns the following integrand into 2^(-x^2)
int(exp(-log(2)*x^2),x,-infinity, infinity);

int(exp(-a*x^2),x,-infinity, infinity);

int(exp(-abs(a)*x^2),x,-infinity, infinity);

int(e^(-abs(a)^2*x^2),x,-infinity,infinity);

int(e^(-abs(a^2)*x^2),x,-infinity,infinity);

% not simplified since a may be complex
int(exp(-a^2*x^2),x,-infinity, infinity);

% but works if a is real
let impart(a)=0;
int(exp(-a^2*x^2),x,-infinity, infinity);

end;
