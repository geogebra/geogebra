%===========================================
%test file for ORTHOVEC version 2, June 1990
%===========================================

showtime;

%example 1: vector identity
a:=svec(a1,a2,a3);
b:=svec(b1,b2,b3);
c:=svec(c1,c2,c3);
d:=svec(d1,d2,d3);

a><b*c><d - (a*c)*(b*d) + (a*d)*(b*c);
%showtime;

%example 2: Equation of Motion in cylindricals
vstart$
2
v:=svec(vr,vt,vz)$b:=svec(br,bt,bz)$
depend v,r,th,z$
depend b,r,th,z$
depend p,r,th,z$

eom:=vout( vdf(v,tt) + v dotgrad v + grad(p) - curl(b) >< b )$
%showtime;

%example 3: Taylor expansions
on div;
on revpri;
vtaylor(sin(x)*cos(y)+e**z,svec(x,y,z),svec(0,0,0),svec(3,4,5));
vtaylor(sin(x)/x,x,0,5);
te:=vtaylor(svec(x/sin(x),(e**y-1)/y,(1+z)**10),svec(x,y,z),
svec(0,0,0),5);
%showtime;

%example 4: extract components
eom _2;
te _1;
off div;
off revpri;
%showtime;

%example 5: Line Integral
vstart$
1
dlineint(svec(3*x**2+5*y,-12*y*z,2*x*y*z**2),svec(s,s**2,s**3),s,1,2);
%showtime;

%example 6: Volume Integral
ub:=sqrt(r**2-x**2)$
8 * dvolint(1,svec(0,0,0),svec(r,ub,ub),6);

%===========================================
% end of test
%===========================================
showtime;

;end;
