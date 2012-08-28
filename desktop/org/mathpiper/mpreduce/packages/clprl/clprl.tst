off clprlverbose;

% Choose real closed fields as the domain:
rlset r;


% Floor function:
prog1 := {
   nat(0) :- true,
   nat(x+1) :- nat(x),
   floor(x,y) :- nat(y) & y<=x & y+1>x};

goal1 := false :- floor(47.3,y);

clp(prog1,goal1);


% Loan computations:
prog2 := {
   loan(d,t,z,r,s) :- t=0 & d=s,
   loan(d,t,z,r,s) :- t>0 & loan(d+d*z-r,t-1,z,r,s)};

goal2a := false :- loan(20000,36,0.01,600,s);
goal2b := false :- loan(20000,36,0.01,r,0);
goal2c := false :- loan(d,36,0.01,600,0);
goal2d := false :- loan(20000,t,0.01,600,s) & s<=0;

clp(prog2,goal2a);
clp(prog2,goal2b);
clp(prog2,goal2c);
clp(prog2,goal2d);


% Pythagorean triplets:
na := {
   nat(0) :- true,
   nat(x+1) :- nat(x) & x>=0};

py := append(na,{
   pyth(x,y,z) :- nat(x) & nat(y) & nat(z) & 2<=x
      and x<=y and y<=z and x**2+y**2=z**2});

clp(py, false :- pyth(3,4,z));
clp(py, false :- pyth(9,y,z));
clp(py, false :- pyth(x,y,9));


% Wilkonson's Polynomial:
wi := {
   wilk(x,e) :- (for i:=1:20 product x+i) + e*x^19 = 0};

wi1 := clp(wi, false :- wilk(x,0) & -20 <= x <= -10);
wi2 := clp(wi, false :- wilk(x,2^-23) & -20 <= x <= -10);
realroots(part(wi2,3,1));
rlqe rlex wi2;


% Minimum (uses disjunction):
mi := {
   min(x,y,z) :- x<=y and z=x or x>=y and z=y,
   max(x,y,z) :- x<=y and z=y or x>=y and z=x};

clp(mi, false :- min(3,4,z));
clp(mi, false :- min(x,y,3));


% Central projection of x on u with light source in c
% (uses quantified constraints):
pr := {
   proj(c1,c2,x1,x2,u1,u2) :-
      ex(t,t>0 and for i := 1:2 mkand mkid(u,i)=t*(mkid(x,i)-mkid(c,i)))};

clp(pr,false :- proj(42,4711,100,1000,u1,-1));


% Linear theory of p-adically valued fields as another domain:
rlset(padics,101);


% Detect power of 101:
va2 := {
   ppow(1) :- true,
   ppow(p*x) :- ppow(x) & 1 | x};

clp(va2, false :- ppow(12201900399479668244827490915525641902001));
clp(va2, false :- ppow(12201900399479668244827490915525641902002));

end;
