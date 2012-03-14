% geometry  Version 1.1 | 6.9.98
% Author | H.-G. Graebe | Univ. Leipzig
% graebe@informatik.uni-leipzig.de

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


COMMENT

The package GEOMETRY is a small package for mechanized (plane)
geometry manipulations with non degeneracy tracing.

It provides the casual user with a couple of procedures that allow
him/her to mechanize his/her own geometry proofs. It grew up from a
course of lectures for students of computer science on this topic held
by the author at the Univ. of Leipzig in fall 1996 and was updated
after a similar lecture in spring 1998.

Author :        H.-G. Graebe
                Univ. Leipzig
                Institut fuer Informatik
                Augustusplatz 10 - 11
                D - 04109 Leipzig
                Germany

        email : graebe@informatik.uni-leipzig.de


Version : 1.1, finished at Sept 6, 1998.

Please send all Comments, bugs, hints, wishes, criticisms etc. to the
above email address.

Reduce version required : The program was tested under v. 3.6. but
should run also under older versions. For the test file the pacakge
CALI should be available.

Relevant publications : See the bibliography in the manual.

Key words : Mechanized geometry theorem proving.

end comment;

module geometry;

comment

Data structures:

        Point A :== {a1,a2}    <=> A=(a1,a2)
        Line  a :== {a1,a2,a3} <=> a1*x+a2*y+a3
                (including degenerate lines with a1=a2=0)

end comment;

put ('geometry,'name," Geometry ")$
put ('geometry,'version," 1.1 ")$
put ('geometry,'date," Sept 6, 1998 ")$

algebraic(write(" Geometry ", get('geometry,'version),
        " Last update ",get('geometry,'date)));

% ============= vector geometry ===============

comment

For affine (plane) geometry one can try to express the coordinates of
all points in the configuration through barycentric coordinates wrt.
three fixed non collinear "base points". Comparison of coefficients
yields equations for the nondetermined ratios that may be solved.

end comment;

algebraic procedure getcoord(u,base);
  % extract coordinates wrt. base point list base.
  begin u:={u};
  for each x in base do u:=for each y in u join coeff(y,x);
  return u;
  end;

% ============= Handling non degeneracy conditions ===============

algebraic procedure clear_ndg; !*ndg!*:={};
algebraic procedure print_ndg; !*ndg!*;
algebraic procedure add_ndg(d);
  if not member(d,!*ndg!*) then !*ndg!*:=d . !*ndg!*;

clear_ndg();

% ================= elementary geometric constructions ===============

% Generators:

algebraic procedure Point(a,b); {a,b};
algebraic procedure Line(a,b,c); {a,b,c};

algebraic procedure pp_line(a,b);
% The line through A and B.
  Line(part(b,2)-part(a,2),part(a,1)-part(b,1),
        part(a,2)*part(b,1)-part(a,1)*part(b,2));

algebraic procedure intersection_point(a,b);
% The intersection point of the lines a,b.
   begin scalar d,d1,d2;
   d:=part(a,1)*part(b,2)-part(b,1)*part(a,2);
   d1:=part(a,3)*part(b,2)-part(b,3)*part(a,2);
   d2:=part(a,1)*part(b,3)-part(b,1)*part(a,3);
   if d=0 then rederr"Lines are parallel";
   add_ndg(num d);
   return Point(-d1/d,-d2/d);
   end;

algebraic procedure lot(p,a);
% The perpendicular from P onto the line a.
  begin scalar u,v; u:=first a; v:=second a;
  return Line(v,-u,u*second p-v*first p);
  end;

algebraic procedure par(p,a);
% The parallel to line a through P.
  Line(part(a,1),part(a,2),
        -(part(a,1)*part(p,1)+part(a,2) *part(p,2)));

algebraic procedure pedalpoint(p,a);
% The pedal point of the perpendicular from P onto the line a.
  intersection_point(lot(P,a),a);

algebraic procedure midpoint(a,b);
% The midpoint of AB
  Point((part(a,1)+part(b,1))/2, (part(a,2)+part(b,2))/2);

algebraic procedure varpoint(a,b,l);
% The point D=l*A+(1-l)*B.
  Point(l*part(a,1)+(1-l)*part(b,1),l*part(a,2)+(1-l)*part(b,2));

algebraic procedure choose_pl(a,u);
% Choose a point on the line a using parameter u.
  begin scalar p,d;
  if part(a,2)=0 then
        << p:=Point(-part(a,3)/part(a,1),u); d:=part(a,1); >>
  else
        << p:=Point(u,-(part(a,3)+part(a,1)*u)/part(a,2));
           d:=part(a,2);
        >>;
  add_ndg(num d);
  return p;
  end;

algebraic procedure sqrdist(a,b);
% The square of the distance between the points A and B.
  (part(b,1)-part(a,1))^2+(part(b,2)-part(a,2))^2;

% ================= elementary geometric properties ===============

algebraic procedure collinear(a,b,c);
% A,B,C are on a common line.
det mat((part(a,1),part(a,2),1),
        (part(b,1),part(b,2),1),
        (part(c,1),part(c,2),1));

algebraic procedure concurrent(a,b,c);
% Lines a,b,c have a common point.
det mat((part(a,1),part(a,2),part(a,3)),
        (part(b,1),part(b,2),part(b,3)),
        (part(c,1),part(c,2),part(c,3)));

algebraic procedure parallel(a,b);
% 0 <=> the lines a,b are parallel.
  part(a,1)*part(b,2)-part(b,1)*part(a,2);

algebraic procedure orthogonal(a,b);
% 0 <=> the lines a,b are orthogonal.
  part(a,1)*part(b,1)+part(a,2)*part(b,2);

algebraic procedure point_on_line(p,a);
% Substitute point P into the line a.
  part(p,1)*part(a,1)+part(p,2)*part(a,2)+part(a,3);

% ================= the transversals in a triangle ===============

algebraic procedure mp(b,c);
% Midpoint perpendicular of BC.
  lot(midpoint(b,c),pp_line(b,c));

algebraic procedure altitude(a,b,c);
% Altitude from A onto BC.
  lot(a,pp_line(b,c));

algebraic procedure median(a,b,c);
% Median line from A to BC.
  pp_line(a,midpoint(b,c));

%       #########################################
%       #                                       #
%       #      Non linear geometric objects      #
%       #                                       #
%       #########################################


% ===================== angles

algebraic procedure l2_angle(a,b);
% tan of the angle between the lines a and b.
  begin scalar d; d:=(part(a,1)*part(b,1)+part(a,2)*part(b,2));
  add_ndg(num(d));
  return (part(a,2)*part(b,1)-part(b,2)*part(a,1))/d;
  end;

algebraic procedure p3_angle(A,B,C);
% tan of the angle between the lines BA and BC
  l2_angle(pp_line(B,A),pp_line(B,C));

algebraic procedure angle_sum(a,b);
% a=tan(\alpha), b=tan(\beta). Returns tan(\alpha+\beta)
  begin scalar d; d:=(1-a*b); add_ndg(num d);
  return (a+b)/d;
  end;

algebraic procedure point_on_bisector(P,A,B,C);
% P is a point on the bisector of the angle ABC.
% Returns num(u)*den(v)-num(v)*den(u) with
% u:=angle(pp_line(A,B),pp_line(P,B))
% v:=angle(pp_line(P,B),pp_line(C,B))
  begin scalar a1,a2,b1,b2,c1,c2,p1,p2;
        a1:=part(A,1); a2:=part(A,2);
        b1:=part(b,1); b2:=part(b,2);
        c1:=part(c,1); c2:=part(c,2);
        p1:=part(p,1); p2:=part(p,2);
  return ( - a1*b2 + a1*p2 + a2*b1 - a2*p1 - b1*p2 + b2*p1)*(b1**2 -
  b1*c1 - b1*p1 + b2**2 - b2*c2 - b2*p2 + c1*p1 + c2*p2) - (a1*b1 -
  a1*p1 + a2*b2 - a2*p2 - b1**2 + b1*p1 - b2**2 + b2*p2)*(b1*c2 -
  b1*p2 - b2*c1 + b2*p1 + c1*p2 - c2*p1)
  end;

% ========== symmetric lines and points

algebraic procedure sympoint(P,l);
% The point symmetric to P wrt. the line l.
  varpoint(P,pedalpoint(P,l),-1);

algebraic procedure symline(a,l);
% The line symmetric to a wrt. the line l.
  begin scalar a1,a2,a3,l1,l2,l3,u;
  a1:=part(a,1); a2:=part(a,2); a3:=part(a,3);
  l1:=part(l,1); l2:=part(l,2); l3:=part(l,3);
  u:=l1^2 - l2^2;
  return Line(- a1*u - 2*a2*l1*l2, - 2*a1*l1*l2 + a2*u,
                - 2*(a1*l1 + a2*l2)*l3 + a3*(l1^2 + l2^2));
  end;

% ===================== circles

comment

        Circle1 represents a circle as the pair {M,sqr} consisting of
        the center M and the square of its radius.

end comment;

algebraic procedure Circle1(M,sqr); {M,sqr};

algebraic procedure p3_circle1(A,B,C);
% The circle through three given points
  begin scalar M;
  M:=intersection_point(mp(A,B),mp(B,C));
  return Circle1(M,sqrdist(M,A));
  end;

algebraic procedure point_on_circle1(P,c);
% Test a point P to be on c:Circle1.
  sqrdist(P,part(c,1))-part(c,2);

algebraic procedure choose_pc(M,r,u);
% Choose a point on the circle with center M and radius (not squared
% radius !) r using a rational parametrization of the circle.
  begin scalar d;
  d:=(u^2+1); add_ndg(num d);
  return Point(r*(u^2-1)/d+part(M,1), 2*r*u/d+part(M,2));
  end;

comment

Another approach represents a circle through its equation

                      c1*(x^2+y^2)+c2*x+c3*y+c4

This is better adapted for analytic geometry. The coordinates are
homogeneous as those for lines, hence we may adjust either c1=1 or
allow for division-free computations without such a scaling. Another
advantage of the latter is, that for c1=0 we get lines as circles with
infinite radius.

A circle is henceforth a quadruple c={c1,c2,c3,c4}.

end comment;

algebraic procedure Circle(c1,c2,c3,c4); {c1,c2,c3,c4};

algebraic procedure c1_circle(M,sqr);
% Circle from center M and squared radius sqr.
  Circle(1, -2*part(M,1), -2*part(M,2),
        part(M,1)^2 + part(M,2)^2 - sqr);

algebraic procedure circle_center c;
% The center of the circle c.
  begin add_ndg(num part(c,1));
  return Point(-part(c,2)/2/part(c,1) ,-part(c,3)/(2*part(c,1)));
  end;

algebraic procedure circle_sqradius c;
% The squared radius of the circle c.
  begin add_ndg(num part(c,1));
  return
   ((part(c,2)^2+part(c,3)^2) - 4*part(c,4)*part(c,1)) /
        (2*part(c,1))^2;
  end;

algebraic procedure p3_circle(A,B,C);
% The circle through three given points
  begin scalar a1,a2,a3,b1,b2,b3,c1,c2,c3;
  a1:=part(A,1); a2:=part(A,2); a3:=a1^2+a2^2;
  b1:=part(b,1); b2:=part(b,2); b3:=b1^2+b2^2;
  c1:=part(c,1); c2:=part(c,2); c3:=c1^2+c2^2;
  return Circle(a1*(b2-c2) + (a2-b2)*c1 + b1*(c2-a2),
    a3*(c2-b2) + (a2-c2)*b3 + (b2-a2)*c3,
    a3*(b1-c1) + (c1-a1)*b3 + (a1-b1)*c3,
    a3*(b2*c1-b1*c2) + (a1*c2-a2*c1)*b3 + (a2*b1-a1*b2)*c3)
  end;

algebraic procedure point_on_circle(P,c);
  begin scalar p1,p2; p1:=part(P,1); p2:=part(P,2);
  return part(c,1)*(p1^2+p2^2)+part(c,2)*p1+part(c,3)*p2+part(c,4);
  end;

algebraic procedure p4_circle(A,B,C,D);
  point_on_circle(D,p3_circle(A,B,C));

% Intersecting with circles

algebraic procedure other_cl_point(P,c,l);
% circle c and line l intersect at P. The procedure returns their
% second intersection point.
  if point_on_line(P,l) neq 0 then rederr "Point not on the line"
  else if point_on_circle(P,c) neq 0 then
        rederr "Point not on the circle"
  else begin scalar c1,c2,c3,l1,l2,d,d1,p1,p2;
  c1:=part(c,1); c2:=part(c,2); c3:=part(c,3);
  l1:=part(l,1); l2:=part(l,2); p1:=part(P,1); p2:=part(P,2);
  d:=c1*(l1^2 + l2^2); add_ndg(num d); d1:=c1*(l1^2-l2^2);
  return {(d1*p1+((2*c1*p2 + c3)*l1-c2*l2)*l2)/d,
        (- d1*p2+((2*c1*p1 + c2)*l2-c3*l1)*l1)/d};
  end;

algebraic procedure other_cc_point(P,c1,c2);
% Circles c1 and c2 intersect at P. The procedure returns their
% second intersection point, computing by elimination the line through
% the common intersection points.
  begin scalar l;
  l:=for i:=2:4 collect
        (part(c1,1)*part(c2,i)-part(c1,i)*part(c2,1));
  return other_cl_point(P,c1,l);
  end;

algebraic procedure cl_tangent(c,l);
% Line l is tangent to the circle c.
  begin scalar c1,c2,c3,c4,l1,l2,l3;
  c1:=part(c,1); c2:=part(c,2); c3:=part(c,3); c4:=part(c,4);
  l1:=part(l,1); l2:=part(l,2); l3:=part(l,3);
  return - 4*c1^2*l3^2 + 4*c1*c2*l1*l3 + 4*c1*c3*l2*l3 -
        4*c1*c4*l1^2 - 4*c1*c4*l2^2 + c2^2*l2^2 - 2*c2*c3*l1*l2 +
        c3^2*l1^2
  end;

algebraic procedure cc_tangent(c,d);
% Two circles c,d are tangent.
  begin scalar c1,c2,c3,c4,d1,d2,d3,d4;
  c1:=part(c,1); c2:=part(c,2); c3:=part(c,3); c4:=part(c,4);
  d1:=part(d,1); d2:=part(d,2); d3:=part(d,3); d4:=part(d,4);
  return
4*c1^2*d4^2 - 4*c1*c2*d2*d4 - 4*c1*c3*d3*d4 - 8*c1*c4*d1*d4 +
4*c1*c4*d2^2 + 4*c1*c4*d3^2 + 4*c2^2*d1*d4 - c2^2*d3^2 + 2*c2*c3*d2*d3
- 4*c2*c4*d1*d2 + 4*c3^2*d1*d4 - c3^2*d2^2 - 4*c3*c4*d1*d3 +
4*c4^2*d1^2
  end;

% ============= some additional tools ===============

symbolic operator list2mat;
symbolic procedure list2mat u;
  'mat. for each x in cdr reval u collect cdr x;

algebraic procedure extractmat(polys,vars);
% extract the coefficient matrix from the linear system polys.
  begin
  if length polys neq length vars then
        rederr"Number of variables doesn't match";
  for each p in polys do for each x in vars do
        if deg(p,x)>1 then rederr"Equations not of linear type";
  return list2mat
        for each x in vars collect
        for each p in polys collect coeffn(p,x,1);
end;

algebraic procedure red_hom_coords u;
% Divide out the content of homogeneous coordinates.
  begin scalar l,g;
  l:=den first u; g:=num first u;
  for each x in rest u do <<l:=lcm(l,den x); g:=gcd(g,num x) >>;
  add_ndg(g);
  return for each x in u collect (x*l/g);
  end;



endmodule; % geometry

end;

