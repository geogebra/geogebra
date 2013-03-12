lisp;

load!-package 'assert;
on1 'assert;

load!-package 'redlog;
rl_set '(r);

procedure anu_approx(anu);
   begin scalar iv;
      iv := anu_iv anu;
      return {float(numr car iv or 0)/float denr car iv,
	 float(numr cdr iv or 0)/float denr cdr iv}
   end;

setkorder '(x);

f1 := numr simp xread t;
(x**5 - 3)*(x**2 -2)*(x**3 - 42)*(x + 7);

f2 := numr simp xread t;
(x + 100)*(x^19 - 1000);

f2 := multf(f1, f2);

g := numr simp xread t;
x^2 - 3;

trl := trail_push(declit_mk ofsf_0mk2('equal, f1), nil);
trl := trail_push(declit_mk ofsf_0mk2('equal, f2), trl);
trl := trail_push(declit_mk ofsf_0mk2('greaterp, g), trl);

res := ofsf_feasible trl;
assert(length res = 2);

fres := for each anu in res collect anu_approx anu;
i1 := car fres;
i2 := cadr fres;

s1 := -7.0;
s2 := 42^(1.0 / 3.0);

assert(car i1 < s1 and s1 < cadr i1);
assert(car i2 < s2 and s2 < cadr i2);


end;
