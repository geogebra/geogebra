% Regression test for a sensible error message

% Added by Rainer Schöpf, 30 Aug 2011

% The substitution at the end of this test file causes 0/0 to be formed.
% This file tests that a sensible error message is printed.

zz2 := (z*(z-2*pi*i)*(z-pi*i/2)^2)/(sinh z-i);

dz2 := df(zz2,z);

z0 := pi*i/2;

sub(z=z0,dz2);

end;
