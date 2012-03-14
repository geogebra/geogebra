% This is a first sample test file in the regression test sequence

% Added by A C Norman, 29 August 2011

% It used to be the case that the variable !!fleps1 got corrupted in
% CSL at times. The conseqence was least-significant digit divergence
% in the output of this case (adapted from numeric.tst).

load_package numeric;
% Note that these regressions tests will all be run on an otherwise
% fresh Reduce, so you need to load any packages that are to be used
% and set any special switches;

depend(y,x);

num_odesolve(df(y,x)=y,y=2,x=(0 .. 1),iterations=4);
 

end;
