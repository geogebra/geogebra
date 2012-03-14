on rlverbose;

% Ordered fields standard form:
rlset ofsf;
rlset();

% Chains
-3/5<x>y>z<=a<>b>c<5/3;

% For-loop actions.
g := for i:=1:6 mkor
   for j := 1:6 mkand
      mkid(a,i) <= mkid(a,j);

% Quantifier elimination and variants
h := rlsimpl rlall g;
rlmatrix h;
on rlrealtime;
rlqe h;
off rlrealtime;

h := rlsimpl rlall(g,{a2});
rlqe h;

off rlqeheu,rlqedfs;
rlqe ex(x,a*x**2+b*x+c>0);
on rlqedfs;
rlqe ex(x,a*x**2+b*x+c>0);
on rlqeheu;

rlqe(ex(x,a*x**2+b*x+c>0),{a<0});

rlgqe ex(x,a*x**2+b*x+c>0);
rlthsimpl ({a*b*c=0,b<>0});


rlqe ex({x,y},(for i:=1:5 product mkid(a,i)*x**10-mkid(b,i)*y**2)<=0);

sol := rlqe ex(x,a*x**2+b*x+c>0);
rlatnum sol;

rlatl sol;
rlatml sol;
rlterml sol;
rltermml sol;
rlifacl sol;
rlifacml sol;

rlstruct(sol,v);
rlifstruct(sol,v);

rlitab sol;
rlatnum ws;
rlgsn sol;
rlatnum ws;

off rlverbose;

rlqea ex(x,m*x+b=0);

% Substitution
sub(x=a,x=0 and a=0 and ex(x,x=y) and ex(a,x>a));

% Boolean normal forms.
f1 := x=0 and b>=0;
f2 := a=0;
f := f1 or f2;

rlcnf f;

rldnf ws;

rlcnf f;

% Negation normal form and prenex normal form
hugo := a=0 and b=0 and y<0 equiv ex(y,y>=a) or a>0;
rlnnf hugo;
rlpnf hugo;

% Length and Part
part(hugo,0);
part(hugo,2,1,2);
length ws;
length hugo;
length part(hugo,1);

% Tableau
mats := all(t,ex({l,u},(
(t>=0 and t<=1) impl
(l>0 and u<=1 and
  -t*x1+t*x2+2*t*x1*u+u=l*x1 and
  -2*t*x2+t*x2*u=l*x2))));
sol := rlgsn rlqe mats;
rltab(sol,{x1>0,x1<0,x1=0});

% Part on psopfn / cleanupfn
part(rlqe ex(x,m*x+b=0),1);
walter := (x>0 and y>0);
rlsimpl(true,rlatl walter);
part(rlatl walter,1,1);


% QE by partial CAD:
cox6 := ex({u,v},x=u*v and y=u**2 and z=v**2)$
rlcad cox6;

% Algebraically closed fields standard form:
sub(x=a,x=0 and a=0 and ex(x,x=y) and ex(a,x<>a));

rlset acfsf;

rlsimpl(x^2+y^2+1<>0);

rlqe ex(x,x^2=y);

clear f;
h := rlqe ex(x,x^3+a*x^2+b*x+c=0 and x^3+d*x^2+e*x+f=0);
rlstruct h;
rlqe rlall (h equiv resultant(x^3+a*x^2+b*x+c,x^3+d*x^2+e*x+f,x)=0);
clear h;

% Discretely valued fields standard form:
rlset dvfsf;
sub(x=a,x=0 and a=0 and ex(x,x=y) and ex(a,x~a));


% P-adic Balls, taken from Andreas Dolzmann, Thomas Sturm. P-adic
% Constraint Solving, Proceedings of the ISSAC '99.
rlset dvfsf;
rlqe all(r_1,all(r_2,all(a,all(b,
ex(x,r_1||x-a and r_2||x-b and r_1|r_2) impl
all(y,r_2||y-b impl r_1||y-a)))));
rlmkcanonic ws;
rlset(dvfsf,100003);
rlqe all(r_1,all(r_2,all(a,all(b,
ex(x,r_1||x-a and r_2||x-b and r_1|r_2) impl
all(y,r_2||y-b impl r_1||y-a)))));

% Size of the Residue Field, taken from Andreas Dolzmann, Thomas
% Sturm. P-adic Constraint Solving. Proceedings of the ISSAC '99.
rlset(dvfsf);
rlqe ex(x,x~1 and x-1~1 and x-2~1 and x-3~1 and 2~1 and 3~1);
rlexplats ws;
rldnf ws;

% Selecting contexts:

rlset ofsf;
f:= ex(x,m*x+b=0);
rlqe f;
rlset dvfsf;
rlqe f;
rlset acfsf;
rlqe f;

end;  % of file
