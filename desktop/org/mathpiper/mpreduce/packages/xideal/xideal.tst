% Test file for XIDEAL package (Groebner bases for exterior algebra)

% Declare EXCALC variables

pform {x,y,z,t}=0,f(i)=1,{u,u(i),u(i,j)}=0;


% Reductions with xmodideal (all should be zero)

d x^d y      xmodideal {d x - d y};
d x^d y^d z  xmodideal {d x^d y - d z^d t};
d x^d z^d t  xmodideal {d x^d y - d z^d t};
f(2)^d x^d y xmodideal {d t^f(1) - f(2)^f(3),
                         f(3)^f(1) - d x^d y};
d t^f(1)^d z xmodideal {d t^f(1) - f(2)^f(3),
                         f(1)^d z - d x^d y,
                         d t^d y - d x^f(2)};
f(3)^f(4)^f(5)^f(6)
             xmodideal {f(1)^f(2) + f(3)^f(4) + f(5)^f(6)};
f(1)^f(4)^f(5)^f(6)
             xmodideal {f(1)^f(2) + f(2)^f(3) + f(3)^f(4)
                        + f(4)^f(5) + f(5)^f(6)};
d x^d y^d z  xmodideal {x**2+y**2+z**2-1,x*d x+y*d y+z*d z};


% Changing the division between exterior variables and parameters

xideal {a*d x+y*d y};
xvars {a};
xideal {a*d x+y*d y};
xideal({a*d x+y*d y},{a,y});
xvars {};       % all 0-forms are coefficients
excoeffs(d u - (a*p - q)*d y);
exvars(d u - (a*p - q)*d y);
xvars {p,q};    % p,q are no longer coefficients
excoeffs(d u - (a*p - q)*d y);
exvars(d u - (a*p - q)*d y);
xvars nil;


% Exterior system for heat equation on 1st jet bundle

S := {d u - u(-t)*d t - u(-x)*d x,
      d u(-t)^d t + d u(-x)^d x,
      d u(-x)^d t - u(-t)*d x^d t};

% Check that it's closed.

dS := d S xmodideal S;


% Exterior system for a Monge-Ampere equation

korder d u(-y,-y),d u(-x,-y),d u(-x,-x),d u(-y),d u(-x),d u;
M := {u(-x,-x)*u(-y,-y) - u(-x,-y)**2,
      d u       -  u(-x)*d x   -  u(-y)*d y,
      d u(-x) - u(-x,-x)*d x - u(-x,-y)*d y,
      d u(-y) - u(-x,-y)*d x - u(-y,-y)*d y}$

% Get the full Groebner basis

gbdeg := xideal M;

% Changing the term ordering can be dramatic

xorder gradlex;
gbgrad := xideal M;

% But the bases are equivalent

gbdeg xmod gbgrad;
xorder deglex;
gbgrad xmod gbdeg;


% Some Groebner bases

gb := xideal {f(1)^f(2) + f(3)^f(4)};
gb := xideal {f(1)^f(2), f(1)^f(3)+f(2)^f(4)+f(5)^f(6)};


% Non-graded ideals

% Left and right ideals are not the same

d t^(d z+d x^d y) xmodideal {d z+d x^d y};
(d z+d x^d y)^d t xmodideal {d z+d x^d y};

% Higher order forms can now reduce lower order ones

d x xmodideal {d y^d z + d x,d x^d y + d z};

% Anything whose even part is a parameter generates the trivial ideal!!

gb := xideal({x + d y},{});
gb := xideal {1 + f(1) + f(1)^f(2) + f(2)^f(3)^f(4) + f(3)^f(4)^f(5)^f(6)};
xvars nil;


% Tracing Groebner basis calculations

on trxideal;
gb := xideal {x-y+y*d x-x*d y};
off trxideal;


% Same thing in lexicographic order, without full reduction

xorder lex;
off xfullreduce;
gblex := xideal {x-y+y*d x-x*d y};

% Manual autoreduction

gblex := xauto gblex;


% Tracing reduction

on trxmod;
first gb xmod gblex;


% Restore defaults

on xfullreduce;
off trxideal,trxmod;
xvars nil;
xorder deglex;


end;
