% GeoProver test file for Reduce, created on Jan 18 2003
load cali,geoprover;
off nat; on echo;

% The following "in" fails in the test harness that I use at present and
% so I have put the contents of the file that would have been read in-line
% in the test script.

%in "$reduce/packages/geometry/supp.red"$

%###############################################################
%
% FILE:    supp.red
% AUTHOR:  graebe
% CREATED: 2/2002
% PURPOSE: Interface for the extended GEO syntax to Reduce
% VERSION: $Id: supp.red,v 1.1 2002/12/26 16:27:22 compalg Exp $


% Redistribution and use in source and binary forms, with or without
% modification, are permitted provided that the following conditions are met:
%
%    * Redistributions of source code must retain the relevant copyright
%      notice, this list of conditions and the following disclaimer.
%    * Redistributions in binary form must reproduce the above copyright
%      notice, this list of conditions and the following disclaimer in the
%      documentation and/or other materials provided with the distribution.
%
% THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
% AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
% THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
% PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNERS OR
% CONTRIBUTORS
% BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
% CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
% SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
% INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
% CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
% ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
% POSSIBILITY OF SUCH DAMAGE.
%

algebraic procedure geo_simplify u; u;
algebraic procedure geo_normal u; u;
algebraic procedure geo_subs(a,b,c); sub(a=b,c);

algebraic procedure geo_gbasis(polys,vars);
  begin
  setring(vars,{},lex);
  setideal(uhu,polys);
  return gbasis uhu;
  end;

algebraic procedure geo_groebfactor(polys,vars,nondeg);
  begin
  setring(vars,{},lex);
  return groebfactor(polys,nondeg);
  end;

algebraic procedure geo_normalf(p,polys,vars);
  begin
  setring(vars,{},lex);
  return p mod polys;
  end;

algebraic procedure geo_eliminate(polys,vars,elivars);
  begin
  setring(vars,{},lex);
  return eliminate(polys,elivars);
  end;

algebraic procedure geo_solve(polys,vars);
  solve(polys,vars);

algebraic procedure geo_solveconstrained(polys,vars,nondegs);
  begin scalar u;
  setring(vars,{},lex);
  u:=groebfactor(polys,nondegs);
  return for each x in u join solve(x,vars);
  end;

algebraic procedure geo_eval(con,sol);
  for each x in sol collect sub(x,con);

% End of what was in supp.red





% Example Arnon
% 
% The problem:
% Let $ABCD$ be a square and $P$ a point on the line parallel to $BD$
% through $C$ such that $l(BD)=l(BP)$, where $l(BD)$ denotes the
% distance between $B$ and $D$. Let $Q$ be the intersection point of
% $BF$ and $CD$. Show that $l(DP)=l(DQ)$.
% 
% The solution:

vars_:=List(x1, x2, x3);
% Points
A__:=Point(0,0); B__:=Point(1,0); P__:=Point(x1,x2);
% coordinates
D__:=rotate(A__,B__,1/2);
C__:=par_point(D__,A__,B__); 
Q__:=varpoint(D__,C__,x3);
% polynomials
polys_:=List(on_line(P__,par_line(C__,pp_line(B__,D__))),
  eq_dist(B__,D__,B__,P__), on_line(Q__,pp_line(B__,P__)));
% conclusion
con_:=eq_dist(D__,P__,D__,Q__);
% solution
gb_:=geo_gbasis(polys_,vars_);
result_:=geo_normalf(con_,gb_,vars_);


% Example CircumCenter_1
% 
% The problem:
% The intersection point of the midpoint perpendiculars is the
% center of the circumscribed circle.
% 
% The solution:

parameters_:=List(a1, a2, b1, b2, c1, c2);
% Points
A__:=Point(a1,a2);
B__:=Point(b1,b2);
C__:=Point(c1,c2);
% coordinates
M__:=intersection_point(p_bisector(A__,B__),
  p_bisector(B__,C__));
% conclusion
result_:=List( eq_dist(M__,A__,M__,B__), eq_dist(M__,A__,M__,C__) );


% Example EulerLine_1
% 
% The problem:
% Euler's line: The center $M$ of the circumscribed circle,
% the orthocenter $H$ and the barycenter $S$ are collinear and $S$
% divides $MH$ with ratio 1:2.
% 
% The solution:

parameters_:=List(a1, a2, b1, b2, c1, c2);
% Points
A__:=Point(a1,a2);
B__:=Point(b1,b2);
C__:=Point(c1,c2);
% coordinates
S__:=intersection_point(median(A__,B__,C__),median(B__,C__,A__));
M__:=intersection_point(p_bisector(A__,B__),
  p_bisector(B__,C__));
H__:=intersection_point(altitude(A__,B__,C__),altitude(B__,C__,A__));
% conclusion
result_:=List(is_collinear(M__,H__,S__), sqrdist(S__,fixedpoint(M__,H__,1/3)));


% Example Brocard_3
% 
% The problem:
% Theorem about the Brocard points:
% Let $\Delta\,ABC$ be a triangle. The circles $c_1$ through $A,B$ and
% tangent to $g(AC)$, $c_2$ through $B,C$ and tangent to $g(AB)$, and
% $c_3$ through $A,C$ and tangent to $g(BC)$ pass through a common
% point.
% 
% The solution:

parameters_:=List(u1, u2);
% Points
A__:=Point(0,0);
B__:=Point(1,0);
C__:=Point(u1,u2);
% coordinates
M_1_:=intersection_point(altitude(A__,A__,C__),p_bisector(A__,B__));
M_2_:=intersection_point(altitude(B__,B__,A__),p_bisector(B__,C__));
M_3_:=intersection_point(altitude(C__,C__,B__),p_bisector(A__,C__));
c1_:=pc_circle(M_1_,A__);
c2_:=pc_circle(M_2_,B__);
c3_:=pc_circle(M_3_,C__);
P__:=other_cc_point(B__,c1_,c2_);
% conclusion
result_:= on_circle(P__,c3_);


% Example Feuerbach_1
% 
% The problem:
% Feuerbach's circle or nine-point circle: The midpoint $N$ of $MH$ is
% the center of a circle that passes through nine special points, the
% three pedal points of the altitudes, the midpoints of the sides of the
% triangle and the midpoints of the upper parts of the three altitudes.
% 
% The solution:

parameters_:=List(u1, u2, u3);
% Points
A__:=Point(0,0);
B__:=Point(u1,0);
C__:=Point(u2,u3);
% coordinates
H__:=intersection_point(altitude(A__,B__,C__),altitude(B__,C__,A__));
D__:=intersection_point(pp_line(A__,B__),pp_line(H__,C__));
M__:=intersection_point(p_bisector(A__,B__),
  p_bisector(B__,C__));
N__:=midpoint(M__,H__);
% conclusion
result_:=List( eq_dist(N__,midpoint(A__,B__),N__,midpoint(B__,C__)),
  eq_dist(N__,midpoint(A__,B__),N__,midpoint(H__,C__)),
  eq_dist(N__,midpoint(A__,B__),N__,D__) );


% Example FeuerbachTangency_1
% 
% The problem:
% For an arbitrary triangle $\Delta\,ABC$ Feuerbach's circle (nine-point
% circle) is tangent to its 4 tangent circles.
% 
% The solution:

vars_:=List(x1, x2);
parameters_:=List(u1, u2);
% Points
A__:=Point(0,0);
B__:=Point(2,0);
C__:=Point(u1,u2);
P__:=Point(x1,x2);
% coordinates
M__:=intersection_point(p_bisector(A__,B__), p_bisector(B__,C__));
H__:=intersection_point(altitude(A__,B__,C__),altitude(B__,C__,A__));
N__:=midpoint(M__,H__);
c1_:=pc_circle(N__,midpoint(A__,B__));
Q__:=pedalpoint(P__,pp_line(A__,B__));
% polynomials
polys_:=List(on_bisector(P__,A__,B__,C__), on_bisector(P__,B__,C__,A__));
% conclusion
con_:=is_cc_tangent(pc_circle(P__,Q__),c1_);
% solution
gb_:=geo_gbasis(polys_,vars_);
result_:=geo_normalf(con_,gb_,vars_);


% Example GeneralizedFermatPoint_1
% 
% The problem:
% A generalized theorem about Napoleon triangles:
% Let $\Delta\,ABC$ be an arbitrary triangle and $P,Q$ and $R$ the third
% vertex of isosceles triangles with equal base angles erected
% externally on the sides $BC, AC$ and $AB$ of the triangle. Then the
% lines $g(AP), g(BQ)$ and $g(CR)$ pass through a common point.
% 
% The solution:

vars_:=List(x1, x2, x3, x4, x5);
parameters_:=List(u1, u2, u3);
% Points
A__:=Point(0,0);
B__:=Point(2,0);
C__:=Point(u1,u2);
P__:=Point(x1,x2);
Q__:=Point(x3,x4);
R__:=Point(x5,u3);
% polynomials
polys_:=List(eq_dist(P__,B__,P__,C__), 
  eq_dist(Q__,A__,Q__,C__),  
  eq_dist(R__,A__,R__,B__), 
  eq_angle(R__,A__,B__,P__,B__,C__), 
  eq_angle(Q__,C__,A__,P__,B__,C__));
% conclusion
con_:=is_concurrent(pp_line(A__,P__), pp_line(B__,Q__), pp_line(C__,R__));
% solution
sol_:=geo_solve(polys_,vars_);
result_:=geo_eval(con_,sol_);


% Example TaylorCircle_1
% 
% The problem:
% Let $\Delta\,ABC$ be an arbitrary triangle. Consider the three
% altitude pedal points and the pedal points of the perpendiculars from
% these points onto the the opposite sides of the triangle. Show that
% these 6 points are on a common circle, the {\em Taylor circle}.
% 
% The solution:

parameters_:=List(u1, u2, u3);
% Points
A__:=Point(u1,0);
B__:=Point(u2,0);
C__:=Point(0,u3);
% coordinates
P__:=pedalpoint(A__,pp_line(B__,C__));
Q__:=pedalpoint(B__,pp_line(A__,C__));
R__:=pedalpoint(C__,pp_line(A__,B__));
P_1_:=pedalpoint(P__,pp_line(A__,B__));
P_2_:=pedalpoint(P__,pp_line(A__,C__));
Q_1_:=pedalpoint(Q__,pp_line(A__,B__));
Q_2_:=pedalpoint(Q__,pp_line(B__,C__));
R_1_:=pedalpoint(R__,pp_line(A__,C__));
R_2_:=pedalpoint(R__,pp_line(B__,C__));
% conclusion
result_:=List( is_concyclic(P_1_,P_2_,Q_1_,Q_2_), 
  is_concyclic(P_1_,P_2_,Q_1_,R_1_),
  is_concyclic(P_1_,P_2_,Q_1_,R_2_));


% Example Miquel_1
% 
% The problem:
% Miquels theorem: Let $\Delta\,ABC$ be a triangle. Fix arbitrary points
% $P,Q,R$ on the sides $AB, BC, AC$. Then the three circles through each
% vertex and the chosen points on adjacent sides pass through a common
% point.
% 
% The solution:

parameters_:=List(c1, c2, u1, u2, u3);
% Points
A__:=Point(0,0);
B__:=Point(1,0);
C__:=Point(c1,c2);
% coordinates
P__:=varpoint(A__,B__,u1);
Q__:=varpoint(B__,C__,u2);
R__:=varpoint(A__,C__,u3);
X__:=other_cc_point(P__,p3_circle(A__,P__,R__),p3_circle(B__,P__,Q__));
% conclusion
result_:=on_circle(X__,p3_circle(C__,Q__,R__));


% Example PappusPoint_1
% 
% The problem:
% Let $A,B,C$ and $P,Q,R$ be two triples of collinear points. Then by
% the Theorem of Pappus the intersection points $g(AQ)\wedge g(BP),
% g(AR)\wedge g(CP)$ and $g(BR)\wedge g(CQ)$ are collinear. 
% 
% Permuting $P,Q,R$ we get six such {\em Pappus lines}.  Those
% corresponding to even resp. odd permutations are concurrent.
% 
% The solution:

parameters_:=List(u1, u2, u3, u4, u5, u6, u7, u8);
% Points
A__:=Point(u1,0);
B__:=Point(u2,0);
P__:=Point(u4,u5);
Q__:=Point(u6,u7);
% coordinates
C__:=varpoint(A__,B__,u3);
R__:=varpoint(P__,Q__,u8);
% conclusion
result_:=is_concurrent(pappus_line(A__,B__,C__,P__,Q__,R__),
  pappus_line(A__,B__,C__,Q__,R__,P__), 
  pappus_line(A__,B__,C__,R__,P__,Q__));


% Example IMO/36_1
% 
% The problem:
% Let $A,B,C,D$ be four distinct points on a line, in that order. The
% circles with diameters $AC$ and $BD$ intersect at the points $X$ and
% $Y$. The line $XY$ meets $BC$ at the point $Z$. Let $P$ be a point on
% the line $XY$ different from $Z$. The line $CP$ intersects the circle
% with diameter $AC$ at the points $C$ and $M$, and the line $BP$
% intersects the circle with diameter $BD$ at the points $B$ and
% $N$. Prove that the lines $AM, DN$ and $XY$ are concurrent.
% 
% The solution:

vars_:=List(x1, x2, x3, x4, x5, x6);
parameters_:=List(u1, u2, u3);
% Points
X__:=Point(0,1);
Y__:=Point(0,-1);
M__:=Point(x1,x2);
N__:=Point(x3,x4);
% coordinates
P__:=varpoint(X__,Y__,u3);
Z__:=midpoint(X__,Y__);
l_:=p_bisector(X__,Y__);
B__:=line_slider(l_,u1);
C__:=line_slider(l_,u2);
A__:=line_slider(l_,x5);
D__:=line_slider(l_,x6);
% polynomials
polys_:=List(is_concyclic(X__,Y__,B__,N__), is_concyclic(X__,Y__,C__,M__),
  is_concyclic(X__,Y__,B__,D__), is_concyclic(X__,Y__,C__,A__),
  is_collinear(B__,P__,N__), is_collinear(C__,P__,M__));
% constraints
nondeg_:=List(x5-u2,x1-u2,x6-u1,x3-u1);
% conclusion
con_:=is_concurrent(pp_line(A__,M__),pp_line(D__,N__),pp_line(X__,Y__));
% solution
sol_:=geo_solveconstrained(polys_,vars_,nondeg_);
result_:=geo_eval(con_,sol_);


% Example IMO/43_2
% 
% The problem:
% 
% No verbal problem description available
% 
% The solution:

vars_:=List(x1, x2);
parameters_:=List(u1);
% Points
B__:=Point(-1,0);
C__:=Point(1,0);
% coordinates
O__:=midpoint(B__,C__);
gamma_:=pc_circle(O__,B__);
D__:=circle_slider(O__,B__,u1);
E__:=circle_slider(O__,B__,x1);
F__:=circle_slider(O__,B__,x2);
A__:=sym_point(B__,pp_line(O__,D__));
J__:=intersection_point(pp_line(A__,C__), par_line(O__, pp_line(A__,D__)));
m_:=p_bisector(O__,A__);
P_1_:=pedalpoint(J__,m_);
P_2_:=pedalpoint(J__,pp_line(C__,E__));
P_3_:=pedalpoint(J__,pp_line(C__,F__));
% polynomials
polys_:=List(on_line(E__,m_), on_line(F__,m_));
% constraints
nondegs_:=List(x1-x2);
% conclusion
con_:=List(eq_dist(J__,P_1_,J__,P_2_), eq_dist(J__,P_1_,J__,P_3_));
% solution
sol_:=geo_solveconstrained(polys_,vars_,nondegs_); 
result_:=geo_simplify(geo_eval(con_,sol_));

showtime;

end;
