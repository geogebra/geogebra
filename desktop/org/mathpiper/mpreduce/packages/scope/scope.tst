% Test SCOPE Package.
% ==================
% NOTE:  The SCOPE, GHORNER, GSTRUCTR and GENTRAN packages must be loaded
% to run these tests.

% Further reading: SCOPE 1.5 manual Section 3, example 1;

scope_switches$

% Further reading: SCOPE 1.5 manual Section 3.1, examples 2,3,4 and 5.

on priall$
optimize z:=a^2*b^2+10*a^2*m^6+a^2*m^2+2*a*b*m^4+2*b^2*m^6+b^2*m^2
	   iname s;
off priall$
on primat,acinfo$
optimize
     ghorner <<z:=a^2*b^2+10*a^2*m^6+a^2*m^2+2*a*b*m^4+2*b^2*m^6+b^2*m^2>>
     vorder m
     iname s;
off exp,primat,acinfo$ 
q:=a+b$
r:=q+a+b$
optimize x:=a+b,q:=:q^2,p(q)::=:r iname s;
on exp$
clear q,r$

% A similar example follows.
% operator a$% Not necessary. Some differences between REDUCE 3.5 and REDUCE 3.6
% when dealing with indices.

on inputc$
k:=j:=1$
u:=c*x+d$
v:=sin(u)$
optimize {a(k,j):=v*(v^2*cos(u)^2+u),
          a(k,j)::=:v*(v^2*cos(u)^2+u)} iname s;
off exp$
optimize {a(k,j):=v*(v^2*cos(u)^2+u),
          a(k,j)::=:v*(v^2*cos(u)^2+u)} iname s;
off inputc,period$
optlang fortran$
optimize z:=(6*a+18*b+9*c+3*d+6*j+18*f+6*g+5*h+5*k+3)^13 iname s;
off ftch$
optimize z:=(6*a+18*b+9*c+3*d+6*j+18*f+6*g+5*h+5*k+3)^13 iname s;
optlang c$
optimize z:=(6*a+18*b+9*c+3*d+6*j+18*f+6*g+5*h+5*k+3)^13 iname s;

% Note: C code never contains exponentiations.

on ftch$
optimize {x:=3*a*p,y:=3*a*q,z:=6*a*r+2*b*p,u:=6*a*d+2*b*q,
v:=9*a*c+4*b*d,w:=4*b} iname s;
off ftch$
optlang fortran$
optimize {x:=3*a*p,y:=3*a*q,z:=6*a*r+2*b*p,u:=6*a*d+2*b*q,
v:=9*a*c+4*b*d,w:=4*b} iname s;
on ftch$
setlength 2$
optimize {x:=3*a*p,y:=3*a*q,z:=6*a*r+2*b*p,u:=6*a*d+2*b*q,
v:=9*a*c+4*b*d,w:=4*b} iname s;
resetlength$
optlang nil$

% Further reading: SCOPE 1.5 manual section 3.1, example 9 and section 3.2.

u:=a*x+2*b$
v:=sin(u)$
w:=cos(u)$
f:=v^2*w;
off exp$
optimize f:=:f,g:=:f^2+f iname s$
alst:=aresults;
restorables;
f;
arestore f;
f;
alst;
optimize f:=:f,g:=:f^2+f iname s$
alst:=aresults$
optimize f:=:f,g:=:f^2+f iname s$
restoreall$
f;

% Further reading: SCOPE 1.5 manual section 3.1, example 8. 
%                  See also section 5.
%                  Also recommended: section 9.

clear a$
matrix a(2,2)$
a(1,1):=x+y+z$
a(1,2):=x*y$
a(2,1):=(x+y)*x*y$
a(2,2):=(x+2*y+3)^3-x$
on exp$
off fort,nat$
optimize detexp:=:det(a) out "expfile" iname s$
off exp$
optimize detnexp:=:det(a) out "nexpfile" iname t$
in expfile$
in nexpfile$
on nat$
detexp-detnexp;
system "rm expfile nexpfile"$

% Further reading: SCOPE 1.5 manual section 4.2, example 15.
% Although the output is similar, it is in general equivalent and
% not identical when using REDUCE 3.6 in stead of REDUCE 3.5. This
% is due to improvements in the simplification strategy.

on acinfo$
optimize 
   gstructr<<a;aa:=(x+y)^2;b:=(x+y)*(y+z);c:=(x+2*y)*(y+z)*(z+x)^2>>
name v iname s;
alst:=
  algopt(algstructr({a,b=(x+y)^2,c=(x+y)*(y+z),d=(x+2*y)*(y+z)*(z+x)^2},v),s);
off acinfo$

% Further reading: SCOPE 1.5 manual section 4.3, example 16.

clear a$
procedure taylor(fx,x,x0,n);
 sub(x=x0,fx)+(for k:=1:n sum(sub(x=x0,df(fx,x,k))*(x-x0)^k/factorial(k)))$
hlst:={f1=taylor(e^x,x,0,4),f2=taylor(cos x,x,0,6)}$
on rounded$
hlst:=hlst;
optimize alghorner(hlst,{x}) iname g$
off rounded$

% Further reading: SCOPE 1.5 manual section 3.1, examples 6 and 7.

optimize z:=:for j:=2:6 sum a^(1/j) iname s$
optimize z1:=a+sqrt(sin(a^2+b^2)), z2:=b+sqrt(sin(a^2+b^2)), 
         z3:=a+b+(a^2+b^2)^(1/2),  z4:=sqroot(a^2+b^2)+(a^2+b^2)^3, 
         z5:=a^2+b^2+cos(a^2+b^2), z6:=(a^2+b^2)^(1/3)+(a^2+b^2)^(1/6)  
iname s;

% Further reading: SCOPE 1.5 manual section 6, examples 18 and 19.

optlang fortran$
optimize {x(i+1,i-1):=a(i+1,i-1)+b(i),y(i-1):=a(i-1,i+1)-b(i)} iname s
         declare <<x(4),a(4,4),y(5): real;b(5): integer>>;
optlang c$
optimize {x(i+1,i-1):=a(i+1,i-1)+b(i),y(i-1):=a(i-1,i+1)-b(i)} iname s
         declare <<x(4),a(4,4),y(5): real;b(5): integer>>;
optlang  pascal$
optimize {x(i+1,i-1):=a(i+1,i-1)+b(i),y(i-1):=a(i-1,i+1)-b(i)} iname s
         declare <<x(4),a(4,4),y(5): real;b(5): integer>>;
optlang ratfor$
optimize {x(i+1,i-1):=a(i+1,i-1)+b(i),y(i-1):=a(i-1,i+1)-b(i)} iname s
         declare <<x(4),a(4,4),y(5): real;b(5): integer>>;
precision 7$
on rounded, double$
optlang fortran$
optimize x1:=2         *a + 10        *b,
         x2:=2.00001   *a + 10        *b,
         x3:=2         *a + 10.00001  *b,
         x4:=6         *a + 10        *b,
         x5:=2.0000001 *a + 10.000001 *b
iname s
declare << x1,x2,x3,x4,x5,a,b: real>>$

% Further reading: SCOPE 1.5 manual section 7, example 20.
% Notice the double role of e: In the lhs as identifier. In the rhs as
% exponential function.
% Further notice that a is expected to be declared operator. This is
% due to lower level scope activities.

optimize a(1,x+1)  := g + h*r^f,
         b(y+1)    := a(1,2*x+1)*(g+h*r^f),
         c1        := (h*r)/g*a(2,1+x),
         c2        := c1*a(1,x+1) + sin(d),
         a(1,x+1)  := c1^(5/2),
         d         := b(y+1)*a(1,x+1),
         a(1,1+2*x):= (a(1,x+1)*b(y+1)*c)/(d*g^2),
         b(y+1)    := a(1,1+x)+b(y+1) + sin(d),
         a(1,x+1)  := b(y+1)*c + h/(g + sin(d)),
         d         := k*e + d*(a(1,1+x) + 3),
         e         := d*(a(1,1+x) + 3) + sin(d),
         f         := d*(3 + a(1,1+x)) + sin(d),
         g         := d*(3 + a(1,1+x)) + f
iname s
declare << a(5,5),b(7),c,c1,d,e,f,g,h,r: real*8; x,y: integer>>$

% Further reading: SCOPE 1.5 manual section 8, examples 21 and 22.
%                  Also recommended: section 9.

optlang nil$
delaydecs$
 gentran declare <<a,b,c,d,q,w: real>>$
 gentran a:=b+c$
 gentran d:=b+c$
 gentran <<q:=b+c;w:=b+c>>$
makedecs$
on gentranopt$
delaydecs$
 gentran declare <<a,b,c,d,q,w: real>>$
 gentran a:=b+c$
 gentran d:=b+c$
 gentran <<q:=b+c;w:=b+c>>$
makedecs$
off gentranopt$
delayopts$
 gentran declare <<a,b,c,d,q,w: real>>$
 gentran a:=b+c$
 gentran d:=b+c$
 gentran <<q:=b+c;w:=b+c>>$
makeopts$
delaydecs$
 gentran declare <<a,b,c,d,q,w: real>>$
 delayopts$
  gentran a:=b+c$
  gentran d:=b+c$
  gentran <<q:=b+c;w:=b+c>>$
 makeopts$
makedecs$
clear a,b,c,d,q,w$
matrix a(2,2)$
a:=mat(((b+c)*(c+d),(b+c+2)*(c+d-3)),((c+b-3)*(d+b),(c+b)*(d+b+4)));
gentranlang!*:='c$
delayopts$
 gentran aa:=:a$
makeopts$
end;
