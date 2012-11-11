% Some gaussian integral give erf(infinity) when specfn is loaded,
% spotted by Andrey G. Grozin

load_package specfn;

int(exp(-x^2),x,0,infinity);

int(exp(-x^2),x,-infinity,0);

int(exp(-x^2),x,-infinity,infinity);

end;
