%% sets.tst

%% Author: F.J.Wright@Maths.QMW.ac.uk
%% Date: 20 Feb 1994

%% Test of REDUCE sets package, based on the examples on page 51 of
%% the "Maple V Language Reference Manual"
%% by Char, Geddes, Gonnet, Leong, Monagan and Watt (Springer, 1991).

%% The output (especially of symbolic set expressions) looks better
%% using PSL-REDUCE under MS-Windows or X in graphics mode.

%% Note that REDUCE supports n-ary symbolic infix operators,
%% does not require any special quoting to use an infix operator
%% as a prefix operator, and supports member as an infix operator.
%% However, REDUCE ALWAYS requires  evalb  to explicitly evaluate a
%% Boolean expression outside of a conditional statement.
%% Maple 5.2 does not provide any subset predicates.

clear a, b, c, x, y, z;

s := {x,y} union {y,z};
% s := {x,y,z}

t := union({x,y},{y,z});
% t := {x,y,z}

evalb(s = t);
% true

evalb(s set_eq t);
% true

evalb(member(y, s));
% true

evalb(y member s);
% true

evalb(y member {x*y, y*z});
% false

evalb(x*y member {x*y, y*z});
% true

{3,4} union a union {3,7} union b;
% {3,4,7} union a union b

{x,y,z} minus {y,z,w};
% {x}

a minus b;
% a\b

a\b;
% a\b

minus(a,a);
% {}

{x,y,z} intersect {y,z,w};
% {y,z}

intersect(a,c,b,a);
% a intersection b intersection c

%% End of Maple examples.

(a union b) intersect c where set_distribution_rule;
% a intersection c union b intersection c

algebraic procedure power_set s;
   %% Power set of a set as an algebraic list (inefficiently):
   if s = {} then {{}} else
      {s} union for each el in s join power_set(s\{el});

power_set{};
power_set{1};
power_set{1,2};
power_set{1,2,3};


evalb 1;
% true

evalb 0;
% false

evalb(a = a);
% true

evalb(a = b);
% false

evalb(2 member {1,2} union {2,3});
% true

evalb({2} member {1,2} union {2,3});
% false

evalb({1,3} subset {1,2} union {2,3});
% true

evalb(a subset a union b);
% true

evalb(a subset_eq a union b);
% true

evalb(a set_eq a union b);
% false

evalb(a\b subset a union c);
% true

mkset{1,2,1};
% {1,2}

end;
