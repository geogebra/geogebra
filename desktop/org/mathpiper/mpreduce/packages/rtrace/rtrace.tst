% rtrace.tst -- Test portable REDUCE tracing

% Author: Francis J. Wright, 13 July 1998

symbolic procedure test(a, b);
   begin scalar c, d;
      d := c := {a, b};
      return c
   end$

rtr test;
getd 'test;
prop 'test;
test('a, 'b);

rtrst test;
getd 'test;
prop 'test;
test('a, 'b);

unrtr test;
getd 'test;
prop 'test;
test('a, 'b);

algebraic procedure test(a, b);
   begin scalar c, d;
      d := c := {a, b};
      return c
   end$

rtr test;
getd 'test;
prop 'test;
test(a, b);

rtrst test;
getd 'test;
prop 'test;
test(a, b);

unrtr test;
getd 'test;
prop 'test;
test(a, b);

algebraic procedure test(a, b);
   d := c := {a, b}$

rtr test;
getd 'test;
prop 'test;
test(a, b);

rtrst test;
getd 'test;
prop 'test;
test(a, b);

unrtr test;
getd 'test;
prop 'test;
test(a, b);


% Examples used in documentation (rtrace.tex):

algebraic procedure power(x, n);
   if n = 0 then 1 else x*power(x, n-1)$

rtr power;
power(x+1, 2);
off rtrace;
power(x+1, 2);
on rtrace;
unrtr power;

rtr int;
unrtr int;

procedure fold u;
   for each x in u sum x$

rtrst fold;
fold {z, z*y, y};
unrtrst fold;

trigrules := {sin(~x)^2 => 1 - cos(x)^2};

let trigrules;

trrl trigrules;
1 - sin(x)^2;
untrrl trigrules;

trrl sin;
1 - sin(x)^2;
untrrl sin;

clearrules trigrules;

trrlid trigrules;
1 - sin(x)^2 where trigrules;
untrrlid trigrules;

end;
