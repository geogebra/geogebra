depend r,x;

depend {u}, a, b, {a, b}, v;

df(u,v);

on expanddf;

df(u,v);

operator f;

df(f(a),v);

df(f(u),v);

let x^3=u;

let f(r)^3=u;

df(x,u);

df (f(r),u);

df(f(r,r^2),x);

df(f(r,cos y),x);

df(f(r,cos(x)),x);

df(f(r,cos y),x);

depend cos(y),x;

df(f(r,cos y),x);

depend y,x;

df(f(r,cos y),x);

df(cos y,x);

nodepend y,x;

df(cos y,x);

off expanddf;

df(f(r),r,x);

on expanddf;

df(f(r),r,x);

operator g;

off expanddf;

depend g(v),A1;

df(g(v),A1);

on expanddf;

df(g(v),A1);

off expanddf;

df(df(int(g(v),v),A1),v);

on expanddf;

df(df(int(g(v),v),A1),v);

% more chain rule tests: multiple derivatives

off expanddf;
operator y;
depend ux,x;

let df(ux,x) = 1/e**x;

df(y(x),x);

df(y(ux),x);

df(y(x),x,ux);

df(y(x),ux,x);

df(y(ux),x,ux);

df(y(ux),ux,x);

on expanddf;

df(y(x),x);

df(y(ux),x);

df(y(x),x,ux);

df(y(x),ux,x);

df(y(ux),x,ux);

df(y(ux),ux,x);

% test commuting vs. non-commuting

off expanddf;

df(df(g(v),A1),v);

on expanddf;

df(df(g(v),A1),v);

on commutedf;

df(df(g(v),A1),v);

off commutedf;

df(df(g(v),A1),v);

depend uuu,xxx;

off expanddf;

df(uuu,xxx,uuu);

df(y(uuu),xxx,uuu);

df(y(uuu),uuu,xxx);

on commutedf;

df(uuu,xxx,uuu);

df(y(uuu),xxx,uuu);

df(y(uuu),uuu,xxx);

off commutedf;
on expanddf;

df(uuu,xxx,uuu);

df(y(uuu),xxx,uuu);

df(y(uuu),uuu,xxx);

on commutedf;

df(uuu,xxx,uuu);

df(y(uuu),xxx,uuu);

df(y(uuu),uuu,xxx);


% test inconsistent dependencies

operator h;

depend u1,a2,b1;

depend {a2,b1,c1},v1;

depend h(u1),a2,b1,c1;

off expanddf;

df(h(u1),v1);

on expanddf;

df(h(u1),v1);

end;
