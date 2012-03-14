
lisp;
on comp;

in "i86comp.red"$

on backtrace;

!*genlisting := t;

symbolic procedure foo x; if x then 'one else 'two;

i86!-compile '(de foo (x) (if x 'one 'two));

symbolic procedure fact n;
  if n = 0 then 1 else n * fact sub1 n;

i86!-compile '(de fact (n) (if (equal n 0) 1 (times n (fact (sub1 n)))));

symbol!-env 'fact;

fact 0;
fact 1;
fact 2;
fact 5;
fact 10;

preserve();

end;


