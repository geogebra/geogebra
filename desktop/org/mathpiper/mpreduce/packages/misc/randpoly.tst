% randpoly.tst

% F.J.Wright@Maths.QMW.ac.uk, 14 July 1994

off allfac;  on div, errcont;

% Univariate:
% ----------
randpoly x;
% Equivalent to above:
randpoly {x};
randpoly(x, dense);  % univariate default already dense
randpoly(x, degree=10, ord=5);

% Bivariate:
% ---------
% Default is sparse
randpoly {x,y};
randpoly({x,y}, dense);
randpoly({x,y}, degree=10);
% Lots of terms:
randpoly({x,y}, dense, degree=10);
randpoly({x,y}, dense, degree=10, ord=5);
% Sparse:
randpoly({x,y}, deg=10, ord=5);
% Dense again:
randpoly({x,y}, terms=1000, maxdeg=10, mindeg=5);

% Exponent and coefficient functions:
% ----------------------------------
randpoly({x,y}, expons = rand(-10 .. 10));
% Trivial example:
randpoly({x,y}, expons = proc 5);
randpoly({x,y}, expons = proc(2*random(0 .. 5)));

randpoly({x,y}, coeffs = rand(-999 .. 999));
procedure coe; randpoly(a, terms=2)$
randpoly({x,y}, coeffs = coe);
randpoly({x,y}, coeffs = coe, degree = 10);

% Polynomials composed with general expressions:
% ---------------------------------------------
randpoly({x,y^2});
randpoly(x^2 - y^2);
% This should give the constant term:
sub(x=y, ws);
randpoly({x^2 - a^2, y - b});
% This should give the constant term:
sub(x=a, y=b, ws);

% Polynomials with specified zeros:
% --------------------------------
randpoly(x = a);
% This should give 0:
sub(x=a, ws);
randpoly({x = a, y = b});
% This should give 0:
sub(x=a, y=b, ws);

% Invalid input detection:
% -----------------------
randpoly({x,y}, degree=foo);
randpoly({x,y}, foo);
randpoly({x,y}, degree=-5);

on allfac;  off div, errcont;

end;
