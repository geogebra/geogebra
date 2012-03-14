%***********************************************************************
%*****                                                             *****
%***** Package  F I D E - Test Examples Ver. 1.1.2 May 29,1995     *****
%*****                                                             *****
%***********************************************************************
%***********************************************************************
%*****                                                             *****
%*****    T e s t     Examples   ---   Module    E X P R E S       *****
%*****                                                             *****
%***********************************************************************

let cos th**2=1 - sin th**2,
    cos fi**2=1 - sin fi**2;
factor df;
on rat;
for all x,y let diff(x,y)=df(x,y);
depend u,r,th,fi;
depend v,r,th,fi;
depend f,r,th,fi;
depend w,r,th,fi;
% Spherical coordinate system
scalefactors 3,r*sin th*cos fi,r*sin th*sin fi,r*cos th,r,th,fi;
tensor a1,a2,a3,a4,a5;
vectors u,v;
dyads w;
a1:=grad f;
a2:=div u;
a3:=curl v;
a4:=lapl v;
a3:=2*a3+a4;
a5:=lapl f;
a1:=a1+div w;
a1:=u.dyad((a,0,1),(1,b,3),(0,c,d));
a2:=vect(a,b,c);
a1.a2;
%  Scalar product
u.v;
%  Vector product
u?v;
%  Dyadic
u&v;
%  Directional derivative
dirdf(u,v);
clear a1,a2,a3,a4,a5,u,v,w;
for all x,y clear diff(x,y);
clear cos th**2,
      cos fi**2;
remfac df;
off rat;
scalefactors 3,x,y,z,x,y,z;

%***********************************************************************
%*****                                                             *****
%*****      T e s t     Examples   ---   Module    I I M E T       *****
%*****                                                             *****
%***********************************************************************

% Example I.1 - 1-D Lagrangian Hydrodynamics

off exp;
factor diff;
on rat,eqfu;

% Declare which indexes will be given to coordinates
coordinates x,t into j,m;

% Declares uniform grid in x coordinate
grid uniform,x;

% Declares dependencies of functions on coordinates
dependence eta(t,x),v(t,x),eps(t,x),p(t,x);

% Declares p as known function
given p;

same eta,v,p;

iim a, eta,diff(eta,t)-eta*diff(v,x)=0,
    v,diff(v,t)+eta/ro*diff(p,x)=0,
    eps,diff(eps,t)+eta*p/ro*diff(v,x)=0;

clear a;
clearsame;
cleargiven;

%***********************************************************************

% Example  I.2 - How other functions (here sin, cos) can be used in
%                discretized terms

diffunc sin,cos;
difmatch all,diff(u*sin x,x),u=one,2,(u(i+1)*sin x(i+1)-u(i-1)
                   *sin x(i-1))/(dim1+dip1),
                 u=half,0,(u(i+1/2)*sin x(i+1/2)-u(i-1/2)*sin x(i-1/2))
                   /di;
difmatch all,cos x*diff(u,x,2),u=one,0,cos x i*(u(i+1)-2*u(i)+u(i-1))
                   /di^2,
                 u=half,3,(u(i+3/2)-u(i+1/2))/dip2/2 -
                          (u(i-1/2)-u(i-3/2))/dim2/2;
off exp;
coordinates x,t into j,m;
grid uniform,x,t;
dependence u(x,t),v(x,t);
iim a,u,diff(u,t)+diff(u,x)+cos x*diff(v,x,2)=0,
      v,diff(v,t)+diff(sin x*u,x)=0;
clear a;

%***********************************************************************

% Example I.3 - Schrodinger equation

factor diff;
coordinates t,x into m,j;
grid uniform,x,t;
dependence ur(x,t),ui(x,t);
same ui,ur;
iim a,ur,-diff(ui,t)+1/2*diff(ur,x,2)+(ur**2+ui**2)*ur=0,
       ui,diff(ur,t)+1/2*diff(ui,x,2)+(ur**2+ui**2)*ui=0;
clear a;
clearsame;

%***********************************************************************

% Example I.4 - Vector calculus in p.d.e. input
%               cooperation with expres module
%               2-D hydrodynamics

scalefactors 2,x,y,x,y;
vectors u;
off exp,twogrid;
on eqfu;
factor diff,ht,hx,hy;
coordinates x,y,t into j,i,m;
grid uniform,x,y,t;
dependence n(t,x,y),u(t,x,y),p(t,x,y);
iim a,n,diff(n,t)+u.grad n+n*div u=0,
     u,m*n*(diff(u,t)+u.grad u)+grad p=vect(0,0),
     p,3/2*(diff(p,t)+u.grad p)+5/2*p*div u=0;
clear a,u;

%***********************************************************************

% Example I.5 - 1-D hydrodynamics up to 3-rd moments (heat flow)

coordinates x,t into j,m;
grid uniform,x,t;
dependence n(x,t),u(x,t),tt(x,t),p(x,t),q(x,t);
iim a, n,diff(n,t)+u*diff(n,x)+diff(u,x)=0,
    u,n*m*(diff(u,t)+u*diff(u,x))+k*diff(n*tt,x)+diff(p,x)=0,
    tt,3/2*k*n*(diff(tt,t)+u*diff(tt,x))+n*k*tt*diff(u,x)+1/2*p
       *diff(u,x)+diff(q,x)=0,
    p,diff(p,t)+u*diff(p,x)+p*diff(u,x)+n*k*tt*diff(u,x)+2/5*diff(q,x)
       =0,
    q,diff(q,t)+u*diff(q,x)+q*diff(u,x)+5/2*n*k**2*tt/m*diff(tt,x)+n*k
       *tt*diff(p,x)-p*diff(p,x)=0;
clear a;

remfac diff,ht,hx,hy;
on exp;
off rat;

%***********************************************************************
%*****                                                             *****
%*****     T e s t     Examples   ---   Module    A P P R O X      *****
%*****                                                             *****
%***********************************************************************

% Example A.1

coordinates x,t into j,n;
maxorder t=2,x=3;
functions u,v;
approx( (u(n+1/2)-u(n-1/2))/ht=(v(n+1/2,j+1/2)-v(n+1/2,j-1/2)
                               +v(n-1/2,j+1/2)-v(n-1/2,j-1/2))/(2*hx) );

% Example A.2

maxorder t=3,x=3;
approx( (u(n+1)-u(n))/ht=(u(n+1,j+1/2)-u(n+1,j-1/2)
                               +u(n,j+1/2)-u(n,j-1/2))/(2*hx) );

% Example A.3

maxorder t=2,x=3;
center t=1/2;
approx( (u(n+1)-u(n))/ht=(v(n+1,j+1/2)-v(n+1,j-1/2)
                               +v(n,j+1/2)-v(n,j-1/2))/(2*hx) );

% Example A.4

approx( u(n+1)/ht=(v(n+1,j+1/2)-v(n+1,j-1/2)
                               +v(n,j+1/2)-v(n,j-1/2))/(2*hx) );

% Example A.5

maxorder t=3,x=3;
approx( (u(n+1)-u(n))/ht=(u(n+1,j+1/2)-u(n+1,j-1/2))/hx);

% Example A.6

approx( (u(n+1)-u(n))/ht=(u(n+1/2,j+1/2)-u(n+1/2,j-1/2))/hx);

% Example A.7;

maxorder x=4;
approx((u(n+1)-u(n))/ht=(u(n+1/2,j+1)-2*u(n+1/2,j)+u(n+1/2,j-1))/hx**2);

%***********************************************************************
%*****                                                             *****
%*****     T e s t     Examples   ---   Module    C H A R P O L    *****
%*****                                                             *****
%***********************************************************************

% Example C.1

coordinates t,x into i,j;
grid uniform,t,x;
let cos ax**2=1-sin ax**2;
unfunc u,v;
matrix aa(1,2),bb(2,2);
aa(1,1):=(u(i+1)-u(i))/ht+(v(j+1)-v(j))/hx$
aa(1,2):=(v(i+1)-v(i))/ht+(u(j+1/2)-u(j-1/2))/hx$
bb:=ampmat aa;
bb:=denotemat bb;
factor lam;
pol:=charpol bb;
prdenot;
cleardenot;
clear aa,bb,pol;

%***********************************************************************

% Example C.2 : Reprint Vorozcov, Ganza, Mazurik: Simvolno-cislennyj
%               interfejs.  v zadacach ..., Novosibirsk 1986,  p.47.

unfunc u;
matrix aa(1,1),bb(1,1);
aa(1,1):=(u(i+1)-u(i))/ht+a*(u(j)-u(j-1))/hx$
bb:=ampmat aa;
bb:=denotemat bb;
pol:=charpol bb;
prdenot;
cleardenot;
clear aa,bb,pol;

%***********************************************************************

% Example C.3 : Reprint Vorozcov, Ganza, Mazurik: Simvolno-cislennyj
%               interfejs.  v zadacach ..., Novosibirsk 1986,  p.52.

coordinates t,x into m,j;
unfunc u,r;
matrix aa(1,2),bb(2,2);
aa(1,1):=(r(m+1)-r(m))/ht+u0*(r(m+1,j+1)-r(m+1,j-1))/2/hx
                         +r0*(u(m+1,j+1)-u(m+1,j-1))/2/hx$
aa(1,2):=(u(m+1)-u(m))/ht+u0*(u(m+1,j+1)-u(m+1,j-1))/2/hx
                         +c0**2/r0*(r(m,j+1)-u(m,j-1))/2/hx$
bb:=ampmat aa;
bb:=denotemat bb;
pol:=charpol bb;
prdenot;
cleardenot;
clear aa,bb,pol;

%***********************************************************************

% Example C.4 : Richtmyer, Morton: Difference methods for initial value
%               problems, &10.3.  p.262

coordinates t,x into n,j;
unfunc v,w;
matrix aa(1,2),bb(2,2);
aa(1,1):=(v(n+1)-v(n))/ht-c*(w(j+1/2)-w(j-1/2)+
                             w(n+1,j+1/2)-w(n+1,j-1/2))/(2*hx)$
aa(1,2):=(w(n+1,j-1/2)-w(n,j-1/2))/ht-c*(v(n+1,j)-v(n+1,j-1)+
                                         v(j)-v(j-1))/(2*hx)$
bb:=ampmat aa;
bb:=denotemat bb;
pol:=charpol bb;
prdenot;
cleardenot;
clear aa,bb,pol;

%***********************************************************************

% Example C.5: Mazurik: Algoritmy resenia zadaci..., Preprint no.24-85,
%               AN USSR SO, Inst. teor. i prikl. mechaniky, p.34

coordinates t,x,y into n,m,k;
grid uniform,t,x,y;
unfunc u1,u2,u3;
matrix aa(1,3),bb(3,3);
aa(1,1):=(u1(n+1)-u1(n))/ht+c/2*((-u1(m-1)+2*u1(m)-u1(m+1))/hx +
         (u2(m+1)-u2(m-1))/hx - (u1(k-1)-2*u1(k)+u1(k+1))/hy +
         (u3(k+1)-u3(k-1))/hy)$
aa(1,2):=(u2(n+1)-u2(n))/ht+c/2*((u1(m+1)-u1(m-1))/hx -
         (u2(m-1)-2*u2(m)+u2(m+1))/hx)$
aa(1,3):=(u3(n+1)-u3(n))/ht + c/2*((u1(k+1)-u1(k-1))/hy -
         (u3(k-1)-2*u3(k)+u3(k+1))/hy)$
off prfourmat;
bb:=ampmat aa;
pol:=charpol bb;
let
  cos ax=cos ax2**2-sin ax2**2,
  cos ay=cos ay2**2-sin ay2**2,
  sin ax=2*sin ax2*cos ax2,
  sin ay=2*sin ay2*cos ay2,
  cos ax2**2=1-sin ax2**2,
  cos ay2**2=1-sin ay2**2,
  sin ax2=s1,
  sin ay2=s2,
  hx=c*ht/cap1,
  hy=c*ht/cap2;
order s1,s2;
pol:=pol;
clear cos ax,cos ay,sin ax,sin ay,cos ax2**2,cos ay2**2,sin ax2,sin ay2,
      hx,hy;
pol:=complexpol pol;
pol1:=hurw pol;
denotid cp;
pol:=denotepol pol;
prdenot;
cleardenot;
clear aa,bb,pol,pol1;

%***********************************************************************

% Example C.6 : Lax-Wendrov (V. Ganzha)

coordinates t,x,y into n,m,k;
grid uniform,t,x,y;
let cos ax**2=1-sin ax**2,
    cos ay**2=1-sin ay**2;
unfunc u1,u2,u3,u4;
matrix aa(1,4),bb(4,4);
aa(1,1):=4*(u1(n+1)-u1(n))/ht+
         (w*(u1(m+2)-u1(m-2)+u1(m+1,k+1)+u1(m+1,k-1)-
         u1(m-1,k+1)-u1(m-1,k-1))+p*(u2(m+2)-u2(m-2)+u2(m+1,k+1)+
         u2(m+1,k-1)-u2(m-1,k+1)-u2(m-1,k-1))+
         v*(u1(m+1,k+1)+u1(m-1,k+1)-
         u1(m+1,k-1)-u1(m-1,k-1)+u1(k+2)-u1(k-2))+p*(u3(m+1,k+1)+
         u3(m-1,k+1)-u3(m+1,k-1)-u3(m-1,k-1)+u3(k+2)-u3(k-2)))/hx+ht*
         (2*w**2*(-u1(m+2)+2*u1(m)-u1(m-2))+4*w*p*(-u2(m+2)+2*u2(m)-
         u2(m-2))+2*(-u4(m+2)+2*u4(m)-u4(m-2))+2*v**2*(-u1(k+2)+
         2*u1(k)-u1(k-2))+4*v*p*(u3(k+2)+2*u3(k)-u3(k-2))+2*(-u4(k+2)+
         2*u4(k)-u4(k-2))+4*w*v*(-u1(m+1,k+1)+u1(m+1,k-1)+u1(m-1,k+1)-
         u1(m-1,k-1))+4*p*v*(-u2(m+1,k+1)+u2(m+1,k-1)+u2(m-1,k+1)-
         u2(m-1,k-1))+4*w*p*(-u3(m+1,k+1)+u3(m+1,k-1)+u3(m-1,k+1)-
         u3(m-1,k-1)))/hx/hx$
aa(1,2):=4*p*(u2(n+1)-u2(n))/ht+
       (w*p*(u2(m+2)-u2(m-2)+u2(m+1,k+1)+
         u2(m+1,k-1)-u2(m-1,k+1)-u2(m-1,k-1))+u4(m+2)-u4(m-2)+
         u4(m+1,k+1)+
         u4(m+1,k-1)-u4(m-1,k+1)-u4(m-1,k-1)+
         p*v*(u2(m+1,k+1)+u2(m-1,k+1)+
         u2(k+2)-u2(k-2)-u2(m+1,k-1)-u2(m-1,k-1)))/hx+ht*(2*w**2*p*
         (-u2(m+2)+2*u2(m)-u2(m-2))+2*p*c**2*(-u2(m+2)+2*u2(m)-u2(m-2))
         +4*w*(-u4(m+2)+2*u4(m)-u4(m-2))+2*p*v**2*(-u2(k+2)+2*u2(k)-
         u2(k-2))+4*w*p*v*(-u2(m+1,k+1)+u2(m+1,k-1)+u2(m-1,k+1)-
         u2(m-1,k-1))+2*p*c**2*(-u3(m+1,k+1)+u3(m+1,k-1)+u3(m-1,k+1)
         -u3(m-1,k-1))+4*v*(-u4(m+1,k+1)+u4(m+1,k-1)+u4(m-1,k+1)-
         u4(m-1,k-1)))/hx/hx$
aa(1,3):=4*p*(u3(n+1)-u3(n))/ht+(w*p*(u3(m+2)-u3(m-2)+u3(m+1,k+1)+
         u3(m+1,k-1)-u3(m-1,k+1)-u3(m-1,k-1))+u4(k+2)-u4(k-2)+
         u4(m+1,k+1)-u4(m+1,k-1)+u4(m-1,k+1)-u4(m-1,k-1)+
         p*v*(u3(m+1,k+1)+u3(m-1,k+1)+u3(k+2)-u3(k-2)-u3(m+1,k-1)-
         u3(m-1,k-1)))/hx+ht*(2*w**2*p*(-u3(m+2)+2*u3(m)-u3(m-2))+
         2*p*c**2*(-u3(k+2)+2*u3(k)-u3(k-2))+4*v*(-u4(k+2)+
         2*u4(k)-u4(k-2))+2*p*v**2*(-u3(k+2)+2*u3(k)-u3(k-2))+
         4*w*p*v*(-u3(m+1,k+1)+u3(m+1,k-1)+u3(m-1,k+1)-
         u3(m-1,k-1))+2*p*c**2*(-u2(m+1,k+1)+u2(m+1,k-1)+
         u2(m-1,k+1)-u2(m-1,k-1))+4*w*(u4(m+1,k+1)+u4(m+1,k-1)+
         u4(m-1,k+1)-u4(m-1,k-1)))/hx/hx$
aa(1,4):=4*(u4(n+1)-u4(n))/ht+(p*c**2*(u2(m+2)-u2(m-2)+u2(m+1,k+1)+
         u2(m+1,k-1)-u2(m-1,k+1)-u2(m-1,k-1))+w*(u4(m+2)-
         u4(m-2)+u4(m+1,k+1)+u4(m+1,k-1)-u4(m-1,k+1)-u4(m-1,k-1))+
         +p*c**2*(u3(m+1,k+1)+u3(m-1,k+1)-u3(m+1,k-1)-
         u3(m-1,k-1)+u3(k+2)-u3(k-2))+v*(u4(m+1,k+1)+u4(m-1,k+1)-
         u4(m+1,k-1)-u4(m-1,k-1)+u4(k+2)-u4(k-2)))/hx+ht*
         (2*w**2*(-u4(m+2)+2*u4(m)-u4(m-2))+4*w*p*c**2*(-u2(m+2)+
         2*u2(m)-u2(m-2))+2*c**2*(-u4(m+2)+2*u4(m)-u4(m-2))+
         4*p*v*c**2*(-u3(k+2)+2*u3(k)-u3(k-2))+2*c**2*(-u4(k+2)+
         2*u4(k)-u4(k-2))+2*v**2*(-u4(k+2)+2*u4(k)-u4(k-2))+
         4*p*v*c**2*(-u2(m+1,k+1)+u2(m+1,k-1)+u2(m-1,k+1)-
         u2(m-1,k-1))+4*w*p*c**2*(-u3(m+1,k+1)+u3(m+1,k-1)+
         u3(m-1,k+1)-u3(m-1,k-1))+4*w*v*(-u4(m+1,k+1)+
         u4(m+1,k-1)+u4(m-1,k+1)-u4(m-1,k-1)))/hx/hx$
bb:=ampmat aa;
let
  sin(ax)=s1,
  cos(ax)=c1,
  sin(ay)=s2,
  cos(ay)=c2,
  w=k1*hx/ht,
  v=k2*hx/ht,
  c=k3*hx/ht,
  ht=r1*hx;
denotid a;
bb:=denotemat bb;
clear sin ax,cos ax,sin ay,cos ay,w,v,c,ht;
pol:=charpol bb;
denotid cp;
pol:=denotepol pol;
pol:=complexpol pol;
denotid rp;
pol:=denotepol pol;
prdenot;
cleardenot;
clear aa,bb,pol;

%***********************************************************************
%*****                                                             *****
%*****      T e s t     Examples   ---   Module    H U R W P       *****
%*****                                                             *****
%***********************************************************************

% Example H.1

x0:=lam-1;
x1:=lam-(ar+i*ai);
x2:=lam-(br+i*bi);
x3:=lam-(cr+i*ci);
hurwitzp x1;

% Example H.2

x:=hurw(x0*x1);
hurwitzp x;

% Example H.3

x:=(x1*x2);
hurwitzp x;

% Example H.4

x:=(x1*x2*x3);
hurwitzp x;
clear x,x0,x1,x2,x3;

%***********************************************************************
%*****                                                             *****
%*****    T e s t     Examples   ---   Module    L I N B A N D     *****
%*****                                                             *****
%***********************************************************************

on evallhseqp;  % So both sides of equations evaluate.

% Example L.1

operator v;
off echo;
gentran
<<literal
   tab!*,"dimension u(200),v(200),acof(200,3),arhs(200),xl(200,3)",cr!*$
  dx:=0.05$
  x:=0.1$
  for i:=1:101 do
    <<v(i):=x**2/2$
      x:=x+dx >> >>$
off period;
gentran
<<iacof:=200$
  iarhs:=200 >>$
on period;
genlinbandsol(1,1,{{u(1),u(1)=v(1)},{do,{k,2,100,1 },{u(k),u(k+1)-
   2*u(k)+u(k-1)=v(k+1)-2*v(k)+v(k-1)}},{u(101),u(101)=v(101)}})$
gentran
  <<amer:=0.0$
    arer:=0.0$
    for i:=1:101 do
      <<am:=abs(u(i)-v(i))$
        ar:=am/v(i)$
        literal tab!*,"if(am.gt.amer) amer=am",cr!*$
        literal tab!*,"if(ar.gt.arer) arer=ar",cr!* >>$
    literal tab!*,"write(*,100)amer,arer",cr!*$
    literal tab!*,"stop",cr!*$
    literal "100     format(' max. abs. error = ',e12.2,",
            "' max. rel. error = ',e12.2)",cr!*$
    literal tab!*,"end",cr!* >>$
on echo;

%***********************************************************************

% Example L.2

on nag;
off echo;
gentran
<<literal
   tab!*,"dimension u(200),v(200),acof(200,3),arhs(200),xl(200,3)",cr!*$
  dx:=0.05$
  x:=0.1$
  for i:=1:101 do
    <<v(i):=x**2/2$
      x:=x+dx >> >>$
off period;
gentran
<<iacof:=200$
  iarhs:=200 >>$
on period;
genlinbandsol(1,1,{{u(1),u(1)=v(1)},{do,{k,2,100,1 },{u(k),u(k+1)-
   2*u(k)+u(k-1)=v(k+1)-2*v(k)+v(k-1)}},{u(101),u(101)=v(101)}})$
gentran
  <<amer:=0.0$
    arer:=0.0$
    for i:=1:101 do
      <<am:=abs(u(i)-v(i))$
        ar:=am/v(i)$
        literal tab!*,"if(am.gt.amer) amer=am",cr!*$
        literal tab!*,"if(ar.gt.arer) arer=ar",cr!* >>$
    literal tab!*,"write(*,100)amer,arer",cr!*$
    literal tab!*,"stop",cr!*$
    literal "100     format(' max. abs. error = ',e12.2,",
            "' max. rel. error = ',e12.2)",cr!*$
    literal tab!*,"end",cr!* >>$
on echo;

%***********************************************************************

% Example L.3

on imsl;
off echo,nag;
gentran
<<literal
   tab!*,"dimension u(200),v(200),acof(200,3),arhs(200),xl(200,3)",cr!*$
  dx:=0.05$
  x:=0.1$
  for i:=1:101 do
    <<v(i):=x**2/2$
      x:=x+dx >> >>$
off period;
gentran
<<iacof:=200$
  iarhs:=200 >>$
on period;
genlinbandsol(1,1,{{u(1),u(1)=v(1)},{do,{k,2,100,1 },{u(k),u(k+1)-
   2*u(k)+u(k-1)=v(k+1)-2*v(k)+v(k-1)}},{u(101),u(101)=v(101)}})$
gentran
  <<amer:=0.0$
    arer:=0.0$
    for i:=1:101 do
      <<am:=abs(u(i)-v(i))$
        ar:=am/v(i)$
        literal tab!*,"if(am.gt.amer) amer=am",cr!*$
        literal tab!*,"if(ar.gt.arer) arer=ar",cr!* >>$
    literal tab!*,"write(*,100)amer,arer",cr!*$
    literal tab!*,"stop",cr!*$
    literal "100     format(' max. abs. error = ',e12.2,",
            "' max. rel. error = ',e12.2)",cr!*$
    literal tab!*,"end",cr!* >>$
on echo;

%***********************************************************************

% Example L.4

on essl;
off echo,imsl;
gentran
<<literal
   tab!*,"dimension u(200),v(200),acof(200,3),arhs(200),xl(200,3)",cr!*$
  dx:=0.05$
  x:=0.1$
  for i:=1:101 do
    <<v(i):=x**2/2$
      x:=x+dx >> >>$
off period;
gentran
<<iacof:=200$
  iarhs:=200 >>$
on period;
genlinbandsol(1,1,{{u(1),u(1)=v(1)},{do,{k,2,100,1 },{u(k),u(k+1)-
   2*u(k)+u(k-1)=v(k+1)-2*v(k)+v(k-1)}},{u(101),u(101)=v(101)}})$
gentran
  <<amer:=0.0$
    arer:=0.0$
    for i:=1:101 do
      <<am:=abs(u(i)-v(i))$
        ar:=am/v(i)$
        literal tab!*,"if(am.gt.amer) amer=am",cr!*$
        literal tab!*,"if(ar.gt.arer) arer=ar",cr!* >>$
    literal tab!*,"write(*,100)amer,arer",cr!*$
    literal tab!*,"stop",cr!*$
    literal "100     format(' max. abs. error = ',e12.2,",
            "' max. rel. error = ',e12.2)",cr!*$
    literal tab!*,"end",cr!* >>$
on echo;
off essl;

%***********************************************************************
%*****                                                             *****
%*****      T e s t     Complex Examples   ---   More Modules      *****
%*****                                                             *****
%***********************************************************************

% Example M.1

off exp;
coordinates t,x into n,j;
grid uniform,x,t;
dependence v(t,x),w(t,x);
isgrid v(x..one),w(x..half);
iim aa, v, diff(v,t)=c*diff(w,x),
        w, diff(w,t)=c*diff(v,x);
on exp;
center t=1/2;
functions v,w;
approx( aa(0,0)=aa(0,1));
center x=1/2;
approx( aa(1,0)=aa(1,1));
let cos ax**2=1-sin ax**2;
unfunc v,w;
matrix a(1,2),b(2,2),bt(2,2);
a(1,1):=aa(0,0);
a(1,2):=aa(1,0);
off prfourmat;
b:=ampmat a;
clear a,aa;
factor lam;
pol:=charpol b;
pol:=troot1 pol;
pol:=hurw num pol;
hurwitzp pol;
bt:=tcon b;
bt*b;
bt*b-b*bt;
clear aa,a,b,bt;

%***********************************************************************

% Example M.2 : Richtmyer, Morton: Difference methods for initial value
%               problems, &10.2.  p.261

coordinates t,x into n,j;
grid uniform,t,x;
let cos ax**2=1-sin ax**2;
unfunc v,w;
matrix a(1,2),b(2,2),bt(2,2);
a(1,1):=(v(n+1)-v(n))/ht-c*(w(j+1/2)-w(j-1/2))/hx$
a(1,2):=(w(n+1,j-1/2)-w(n,j-1/2))/ht-c*(v(n+1,j)-v(n+1,j-1))/hx$
off prfourmat;
b:=ampmat a;
clear a;
factor lam;
pol:=charpol b;
pol:=hurw num pol;
hurwitzp pol;
bt:=tcon b;
bt*b;
bt*b-b*bt;
clear a,b,bt;

%***********************************************************************

% Example M.3: Mazurik: Algoritmy resenia zadaci..., preprint no.24-85,
%               AN USSR SO, Inst. teor. i prikl. mechaniky, p.34

operator v1,v2;
matrix a(1,3),b(3,3),bt(3,3);
a(1,1):=(p(n+1)-p(n))/ht+c/2*((-p(m-1)+2*p(m)-p(m+1))/hx +
         (v1(m+1)-v1(m-1))/hx - (p(k-1)-2*p(k)+p(k+1))/hy +
         (v2(k+1)-v2(k-1))/hy)$
a(1,2):=(v1(n+1)-v1(n))/ht+c/2*((p(m+1)-p(m-1))/hx -
         (v1(m-1)-2*v1(m)+v1(m+1))/hx)$
a(1,3):=(v2(n+1)-v2(n))/ht + c/2*((p(k+1)-p(k-1))/hy -
         (v2(k-1)-2*v2(k)+v2(k+1))/hy)$
coordinates t,x,y into n,m,k;
functions p,v1,v2;
for k:=1:3 do approx(a(1,k)=0);
grid uniform,t,x,y;
unfunc p,v1,v2;
hy:=hx;
off prfourmat;
b:=ampmat a;
pol:=charpol b;
let
  cos ax=cos ax2**2-sin ax2**2,
  cos ay=cos ay2**2-sin ay2**2,
  sin ax=2*sin ax2*cos ax2,
  sin ay=2*sin ay2*cos ay2,
  cos ax2**2=1-sin ax2**2,
  cos ay2**2=1-sin ay2**2,
  sin ax2=s1,
  sin ay2=s2,
  hx=c*ht/cap;
factor lam;
order s1,s2;
pol:=troot1 pol;
clear cos ax,cos ay,sin ax,sin ay,cos ax2**2,cos ay2**2,sin ax2,sin ay2,
      hx,hy;
pol:=hurw num pol;
hurwitzp pol;
bt:=tcon b;
bt*b;
bt*b-b*bt;
clear a,b,bt,pol;

%***********************************************************************

end;
