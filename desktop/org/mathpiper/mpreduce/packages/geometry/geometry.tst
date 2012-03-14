% Author H.-G. Graebe | Univ. Leipzig | Version 6.9.1998
% graebe@informatik.uni-leipzig.de

comment 

Test suite for the package GEOMETRY 1.1

end comment;

algebraic;
load cali,geometry;
off nat;
on echo;
showtime;


% #####################
% Some one line proofs
% #####################

% A generic triangle ABC

A:=Point(a1,a2); B:=Point(b1,b2); C:=Point(c1,c2);

% Its midpoint perpendiculars have a point in common:

	concurrent(mp(a,b),mp(b,c),mp(c,a));
 
% This point

	M:=intersection_point(mp(a,b),mp(b,c));

% is the center of the circumscribed circle

	sqrdist(M,A) - sqrdist(M,B);	

% The altitutes intersection theorem

	concurrent(altitude(a,b,c),altitude(b,c,a),altitude(c,a,b));

% The median intersection theorem

	concurrent(median(a,b,c),median(b,c,a),median(c,a,b));

% Euler's line

        M:=intersection_point(mp(a,b),mp(b,c));
        H:=intersection_point(altitude(a,b,c),altitude(b,c,a));
        S:=intersection_point(median(a,b,c),median(b,c,a));

                collinear(M,H,S);
                sqrdist(S,varpoint(M,H,2/3));

% Feuerbach's circle

	% Choose a special coordinate system
	A:=Point(0,0); B:=Point(u1,0); C:=Point(u2,u3);

        M:=intersection_point(mp(a,b),mp(b,c));
        H:=intersection_point(altitude(a,b,c),altitude(b,c,a));
        N:=midpoint(M,H);

                sqrdist(N,midpoint(A,B))-sqrdist(N,midpoint(B,C));
                sqrdist(N,midpoint(A,B))-sqrdist(N,midpoint(H,C));

        D:=intersection_point(pp_line(A,B),pp_line(H,C));

                sqrdist(N,midpoint(A,B))-sqrdist(N,D);

clear_ndg(); clear(A,B,C,D,M,H,S,N);

% ############################# 
% Non-linear Geometric Objects
% #############################

% Bisector intersection theorem
 
A:=Point(0,0); B:=Point(1,0); C:=Point(u1,u2);
P:=Point(x1,x2);
 
polys:={
        point_on_bisector(P,A,B,C),
        point_on_bisector(P,B,C,A),
        point_on_bisector(P,C,A,B)};

con1:=num(sqrdist(P,pedalpoint(p,pp_line(A,C)))-x2^2);
con2:=num(sqrdist(p,pedalpoint(p,pp_line(B,C)))-x2^2);

setring({x1,x2},{},lex);
setideal(polys,polys);
gbasis polys;
{con1,con2} mod gbasis polys;

% Bisector intersection theorem. A constructive proof.
 
A:=Point(0,0); B:=Point(1,0); P:=Point(u1,u2);
l1:=pp_line(A,B);
l2:=symline(l1,pp_line(A,P));
l3:=symline(l1,pp_line(B,P));

point_on_bisector(P,A,B,intersection_point(l2,l3));

clear_ndg(); clear(A,B,C,P,l1,l2,l3);

% Miquel's theorem

on gcd;
A:=Point(0,0); B:=Point(1,0); C:=Point(c1,c2);
P:=choose_pl(pp_line(A,B),u1);
Q:=choose_pl(pp_line(B,C),u2);
R:=choose_pl(pp_line(A,C),u3);

X:=other_cc_point(P,p3_circle(A,P,R),p3_circle(B,P,Q))$

point_on_circle(X,p3_circle(C,Q,R));

off gcd;
clear_ndg(); clear(A,B,C,P,Q,R,X);

% ########################
% Theorems of linear type
% ########################

% Pappus' theorem

A:=Point(u1,u2); B:=Point(u3,u4); C:=Point(x1,u5); 
P:=Point(u6,u7); Q:=Point(u8,u9); R:=Point(u0,x2);

polys:={collinear(A,B,C), collinear(P,Q,R)};

con:=collinear(
	intersection_point(pp_line(A,Q),pp_line(P,B)),
	intersection_point(pp_line(A,R),pp_line(P,C)),
	intersection_point(pp_line(B,R),pp_line(Q,C)))$

vars:={x1,x2};
sol:=solve(polys,vars);

sub(sol,con);

% Pappus' theorem. A constructive approach

A:=Point(u1,u2); B:=Point(u3,u4);  
P:=Point(u6,u7); Q:=Point(u8,u9); 

C:=choose_pl(pp_line(A,B),u5);
R:=choose_pl(pp_line(P,Q),u0);

con:=collinear(intersection_point(pp_line(A,Q),pp_line(P,B)),
	intersection_point(pp_line(A,R),pp_line(P,C)),
	intersection_point(pp_line(B,R),pp_line(Q,C)));

clear_ndg(); clear(A,B,C,P,Q,R);

% ###########################
% Theorems of non linear type
% ###########################

% Fermat Point

A:=Point(0,0); B:=Point(0,2); C:=Point(u1,u2);
P:=Point(x1,x2); Q:=Point(x3,x4); R:=Point(x5,x6);

polys1:={sqrdist(P,B)-sqrdist(B,C), sqrdist(P,C)-sqrdist(B,C), 
	sqrdist(Q,A)-sqrdist(A,C), sqrdist(Q,C)-sqrdist(A,C), 
	sqrdist(R,B)-sqrdist(A,B), sqrdist(R,A)-sqrdist(A,B)};

con:=concurrent(pp_line(A,P), pp_line(B,Q), pp_line(C,R));

vars:={x1,x2,x3,x4,x5,x6};
setring(vars,{},lex);
iso:=isolatedprimes polys1;

for each u in iso collect con mod u;

polys2:={sqrdist(P,B)-sqrdist(P,C), 
	sqrdist(Q,A)-sqrdist(Q,C),  
	sqrdist(R,A)-sqrdist(R,B), 
	num(p3_angle(R,A,B)-p3_angle(P,B,C)), 
	num(p3_angle(Q,C,A)-p3_angle(P,B,C))};

sol:=solve(polys2,{x1,x2,x3,x4,x6});
sub(sol,con);

clear_ndg(); clear(A,B,C,P,Q,R);

% ####################
%  Desargue's theorem
% ####################

% A constructive proof.

A:=Point(a1,a2); B:=Point(b1,b2); 
C:=Point(c1,c2); R:=Point(d1,d2);

S:=choose_pl(par(R,pp_line(A,B)),u);
T:=intersection_point(par(R,pp_line(A,C)),par(S,pp_line(B,C)));
 
con:=concurrent(pp_line(A,R),pp_line(B,S),pp_line(C,T));

% Desargue's theorem as theorem of linear type.

A:=Point(u1,u2); B:=Point(u3,u4); C:=Point(u5,u6);
R:=Point(u7,u8); S:=Point(u9,x1); T:=Point(x2,x3);

polys:={parallel(pp_line(R,S),pp_line(A,B)),
	parallel(pp_line(S,T),pp_line(B,C)),
	parallel(pp_line(R,T),pp_line(A,C))};

con:=concurrent(pp_line(A,R),pp_line(B,S),pp_line(C,T));

sol:=solve(polys,{x1,x2,x3});
sub(sol,con);

% The general theorem of Desargue.

A:=Point(0,0); B:=Point(0,1); C:=Point(u5,u6);
R:=Point(u7,u8); S:=Point(u9,u1); T:=Point(u2,x1);

con1:=collinear(intersection_point(pp_line(R,S),pp_line(A,B)),
	intersection_point(pp_line(S,T),pp_line(B,C)),
	intersection_point(pp_line(R,T),pp_line(A,C)));

con2:=concurrent(pp_line(A,R),pp_line(B,S),pp_line(C,T));

sol:=solve(con2,x1);
sub(sol,con1);

clear_ndg(); clear(A,B,C,R,S,T);

% #################
%  Brocard points
% #################

A:=Point(0,0); B:=Point(1,0); C:=Point(u1,u2);

c1:=Circle(1,x1,x2,x3);
c2:=Circle(1,x4,x5,x6);
c3:=Circle(1,x7,x8,x9);

polys:={
	cl_tangent(c1,pp_line(A,C)), 
	point_on_circle(A,c1), 
	point_on_circle(B,c1), 
	cl_tangent(c2,pp_line(A,B)), 
	point_on_circle(B,c2), 
	point_on_circle(C,c2), 
	cl_tangent(c3,pp_line(B,C)), 
	point_on_circle(A,c3), 
	point_on_circle(C,c3)};
 
vars:={x1,x2,x3,x4,x5,x6,x7,x8,x9};
sol:=solve(polys,vars);

P:=other_cc_point(B,sub(sol,c1),sub(sol,c2));
con:=point_on_circle(P,sub(sol,c3));

clear_ndg(); clear A,B,C,c1,c2,c3;	

% ##################
%  Simson's theorem
% ##################

% A constructive proof

        M:=Point(0,0);
        A:=choose_pc(M,r,u1);
        B:=choose_pc(M,r,u2);
        C:=choose_pc(M,r,u3);
        P:=choose_pc(M,r,u4);
        X:=pedalpoint(P,pp_line(A,B))$
        Y:=pedalpoint(P,pp_line(B,C))$
        Z:=pedalpoint(P,pp_line(A,C))$
 
        collinear(X,Y,Z);

clear_ndg(); clear(M,A,B,C,P,X,Y,Z);

% Simson's theorem almost constructive

clear_ndg();

	A:=Point(0,0); B:=Point(u1,u2); 
	C:=Point(u3,u4); P:=Point(u5,x1);
        X:=pedalpoint(P,pp_line(A,B));
        Y:=pedalpoint(P,pp_line(B,C));
        Z:=pedalpoint(P,pp_line(A,C));

	poly:=p4_circle(A,B,C,P); 

        con:=collinear(X,Y,Z);

	remainder(num con,poly);

print_ndg();

% Equational proof, first version:

M:=Point(0,0); A:=Point(0,1); 
B:=Point(u1,x1); C:=Point(u2,x2); P:=Point(u3,x3);

X:=varpoint(A,B,x4); Y:=varpoint(B,C,x5); Z:=varpoint(A,C,x6);

polys:={sqrdist(M,B)-1, sqrdist(M,C)-1, sqrdist(M,P)-1,
	orthogonal(pp_line(A,B),pp_line(P,X)),
	orthogonal(pp_line(A,C),pp_line(P,Z)),
	orthogonal(pp_line(B,C),pp_line(P,Y))};

con:=collinear(X,Y,Z);

vars:={x4,x5,x6,x1,x2,x3};
setring(vars,{},lex);
setideal(polys,polys);
con mod gbasis polys;

% Second version:

A:=Point(0,0);
B:=Point(1,0);
C:=Point(u1,u2);
P:=Point(u3,x1);
X:=Point(x2,0);		% => on the line AB 
Y:=varpoint(B,C,x3);
Z:=varpoint(A,C,x4);

polys:={orthogonal(pp_line(A,C),pp_line(P,Z)),
	orthogonal(pp_line(B,C),pp_line(P,Y)),
	orthogonal(pp_line(A,B),pp_line(P,X)),
       	p4_circle(A,B,C,P)};

con:=collinear(X,Y,Z);

vars:={x2,x3,x4,x1};
setring(vars,{},lex);
con mod interreduce polys;

% The inverse theorem

polys:={orthogonal(pp_line(A,C),pp_line(P,Z)),
	orthogonal(pp_line(B,C),pp_line(P,Y)),
	orthogonal(pp_line(A,B),pp_line(P,X)),
       	collinear(X,Y,Z)};

con:=p4_circle(A,B,C,P);

con mod interreduce polys;

clear_ndg(); clear(M,A,B,C,P,Y,Z);

% ########################
%  The butterfly theorem
% ########################
 
% An equational proof with groebner factorizer and constraints. 

P:=Point(0,0);
O:=Point(u1,0);
A:=Point(u2,u3);	
B:=Point(u4,x1);
C:=Point(x2,x3); 
D:=Point(x4,x5); 
F:=Point(0,x6);
G:=Point(0,x7);

polys:={sqrdist(O,B)-sqrdist(O,A), 
	sqrdist(O,C)-sqrdist(O,A), 
	sqrdist(O,D)-sqrdist(O,A),
	point_on_line(P,pp_line(A,C)),
	point_on_line(P,pp_line(B,D)),
	point_on_line(F,pp_line(A,D)),
	point_on_line(G,pp_line(B,C))
};

con:=num sqrdist(P,midpoint(F,G));

vars:={x6,x7,x3,x5,x1,x2,x4};
setring(vars,{},lex);

sol:=groebfactor(polys,{sqrdist(A,C),sqrdist(B,D)});

for each u in sol collect con mod u;

% A constructive proof

on gcd;

O:=Point(0,0);
A:=Point(1,0);	
B:=choose_pc(O,1,u1);
C:=choose_pc(O,1,u2);
D:=choose_pc(O,1,u3);
P:=intersection_point(pp_line(A,C),pp_line(B,D));

h:=lot(P,pp_line(O,P));

F:=intersection_point(h,pp_line(A,D)); 
G:=intersection_point(h,pp_line(B,C));

con:=sqrdist(P,midpoint(F,G));

off gcd;
clear_ndg(); clear(O,A,B,C,D,P,h,F,G);

% ################################
% Tangency of Feuerbach's circle
% ################################
 
A:=Point(0,0); B:=Point(2,0); C:=Point(u1,u2);
M:=intersection_point(mp(A,B),mp(B,C));
H:=intersection_point(altitude(A,B,C),altitude(B,C,A));
N:=midpoint(M,H);	
c1:=c1_circle(N,sqrdist(N,midpoint(A,B)));
			% Feuerbach's circle

P:=Point(x1,x2);	% => x2 is the radius of the inscribed circle. 

polys:={point_on_bisector(P,A,B,C), point_on_bisector(P,B,C,A)};

con:=cc_tangent(c1_circle(P,x2^2),c1); 

vars:={x1,x2};
setring(vars,{},lex);
setideal(polys,polys);
num con mod gbasis polys;

% Now let P be the incenter of the triangle ABH

polys1:={point_on_bisector(P,A,B,H), point_on_bisector(P,B,H,A)};

con1:=cc_tangent(c1_circle(P,x2^2),c1); 
setideal(polys1,polys1);
num con1 mod gbasis polys1;

clear_ndg(); clear A,B,C,P,M,N,H,c1;

% #############################
% Solutions to the exercises
% #############################

% 1)

A:=Point(0,0); B:=Point(1,0); C:=Point(1,1); D:=Point(0,1);
P:=Point(x1,x2); Q:=Point(x3,1);

polys:={point_on_line(P,par(C,pp_line(B,D))),
	sqrdist(B,D)-sqrdist(B,P),
	point_on_line(Q,pp_line(B,P))};

con:=sqrdist(D,P)-sqrdist(D,Q);

setring({x1,x2,x3},{},lex);
setideal(polys,polys);
con mod gbasis polys;

clear_ndg(); clear(A,B,C,D,P,Q);

% 2)

A:=Point(u1,0); B:=Point(u2,0); C:=Point(0,u3); 
Q:=Point(0,0);		% the pedal point on AB
R:=pedalpoint(B,pp_line(A,C)); 
P:=pedalpoint(A,pp_line(B,C)); 

con1:=point_on_bisector(C,P,Q,R);
con2:=angle_sum(p3_angle(P,Q,C),p3_angle(R,Q,C));

clear_ndg(); clear(A,B,C,P,Q,R);

% 3)

A:=Point(u1,0); B:=Point(u2,0); C:=Point(0,u3); 
P:=pedalpoint(A,pp_line(B,C)); 
Q:=pedalpoint(B,pp_line(A,C)); 
R:=pedalpoint(C,pp_line(A,B));

P1:=pedalpoint(P,pp_line(A,B));
P2:=pedalpoint(P,pp_line(A,C));
Q1:=pedalpoint(Q,pp_line(A,B));
Q2:=pedalpoint(Q,pp_line(B,C));
R1:=pedalpoint(R,pp_line(A,C));
R2:=pedalpoint(R,pp_line(B,C));

con:=for each X in {Q2,R1,R2} collect p4_circle(P1,P2,Q1,X);

clear_ndg(); clear(O,A,B,C,P,Q,R,P1,P2,Q1,Q2,R1,R2);

% 4) 

A:=Point(u1,0); B:=Point(u2,0); C:=Point(0,u3); 
		% => Pedalpoint from C is (0,0)
M:=intersection_point(mp(A,B),mp(B,C));

% Prove (2*h_c*R = a*b)^2

con:=4*u3^2*sqrdist(M,A)-sqrdist(C,B)*sqrdist(A,C);

clear_ndg(); clear(A,B,C,M);

% 5. A solution of constructive type.

on gcd;
O:=Point(0,u1); A:=Point(0,0);	% hence k has radius u1.
B:=Point(u2,0);
M:=midpoint(A,B);
D:=choose_pc(O,u1,u3); 
k:=c1_circle(O,u1^2);
C:=other_cl_point(D,k,pp_line(M,D));
Eh:=other_cl_point(D,k,pp_line(B,D));
F:=other_cl_point(C,k,pp_line(B,C));

con:=parallel(pp_line(A,B),pp_line(Eh,F));

off gcd;
clear_ndg(); clear(O,A,B,C,D,Eh,F,M,k);

% 6)

Z:=Point(0,0); X:=Point(0,1); Y:=Point(0,-1); 
B:=Point(u1,0); C:=Point(u2,0); P:=Point(0,u3);
M:=Point(x1,x2); N:=Point(x3,x4); 
A:=Point(x5,0); D:=Point(x6,0);

polys:={p4_circle(X,Y,B,N), p4_circle(X,Y,C,M),
	p4_circle(X,Y,B,D), p4_circle(X,Y,C,A),
	collinear(B,P,N), collinear(C,P,M)};

con:=concurrent(pp_line(A,M),pp_line(D,N),pp_line(X,Y));

vars:={x1,x2,x3,x4,x5,x6};
setring(vars,{},lex);
res:=groebfactor(polys,{x5-u2,x1-u2,x6-u1,x3-u1});
	% constraints A\neq C, M\neq C, D\neq B, N\neq B
for each u in res collect con mod u;

clear_ndg(); clear(Z,X,Y,B,C,P,M,N,A,D);

% 7)

M:=Point(0,0);
A:=Point(0,u1); B:=Point(-1,0); C:=Point(1,0); 
Eh:=varpoint(A,B,x1); F:=varpoint(A,C,x2);
O:=intersection_point(pp_line(A,M),lot(B,pp_line(A,B))); 
Q:=intersection_point(pp_line(Eh,F),pp_line(B,C));

con1:=num orthogonal(pp_line(O,Q),pp_line(Eh,Q));
con2:=num sqrdist(Q,midpoint(Eh,F));

vars:={x1,x2};
setring(vars,{},lex);
p1:=groebfactor({con1},{x1-1,x2-1,x1,x2});
p2:=groebfactor({con2},{x1-1,x2-1,x1,x2});
	% constraint A,C\neq Eh, B,C\neq F

for each u in p1 collect con2 mod u;
for each u in p2 collect con1 mod u;

% Note that the second component of p2 has no relevant *real* roots,
% since it factors as u1^2 * (x1 - x2)^2 + (x1 + x2 -2)^2 :

u1^2 * (x1 - x2)^2 + (x1 + x2 -2)^2 mod second p2;

clear_ndg(); clear(M,A,B,C,O,Eh,F,Q);

% 8)

on gcd; 

A:=Point(u1,0); B:=Point(u2,0); l1:=pp_line(A,B);
M:=Point(0,u3);		% the incenter, hence u3 = incircle radius 

C:=intersection_point(symline(l1,pp_line(A,M)),
		symline(l1,pp_line(B,M)));  

N:=intersection_point(mp(A,B),mp(B,C)); % the outcenter

sqr_rad:=sqrdist(A,N);	% the outcircle sqradius.

(sqr_rad-sqrdist(M,N))^2-4*u3^2*sqr_rad;

off gcd;
clear_ndg(); clear A,B,C,M,N,l1,sqr_rad;

% 9)

on gcd;

A:=Point(0,0); B:=Point(1,0); M:=Point(u1,0);
C:=Point(u1,u1); F:=Point(u1,1-u1);

c1:=red_hom_coords p3_circle(A,M,C); 
c2:=red_hom_coords p3_circle(B,M,F);
N:=other_cc_point(M,c1,c2);

point_on_line(N,pp_line(A,F));
point_on_line(N,pp_line(B,C));

l1:=red_hom_coords pp_line(M,N);
l2:=sub(u1=u2,l1);

intersection_point(l1,l2); % = (1/2,-1/2)

off gcd;
clear_ndg(); clear A,B,C,F,M,N,c1,c2,l1,l2;

% ####################
% Some more examples
% ####################

% Origin: D. Wang at
%	http://cosmos.imag.fr/ATINF/Dongming.Wang/geother.html
% --------------------------
% Given triangle ABC, H orthocenter, O circumcenter, A1 circumcenter
% of BHC, B1 circumcenter of AHC.
%
% Claim: OH, AA1, BB1 are concurrent.
% --------------------------

A:=Point(u1,0); B:=Point(u2,0); C:=Point(0,u3); 
H:=intersection_point(altitude(C,A,B),altitude(A,B,C));
O:=circle_center(p3_circle(A,B,C)); 
A1:=circle_center(p3_circle(H,B,C)); 
B1:=circle_center(p3_circle(H,A,C)); 

con:=concurrent(pp_line(O,H),pp_line(A,A1),pp_line(B,B1));

end;

