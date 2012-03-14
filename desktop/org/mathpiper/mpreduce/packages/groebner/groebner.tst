% Examples of use of Groebner code.

% In the Examples 1 - 3 the polynomial ring for the ideal operations
% (variable sequence, term order mode) is defined globally in advance.

% Example 1, Linz 85.

torder ({q1,q2,q3,q4,q5,q6},lex)$

groebner {q1,
          q2**2 + q3**2 + q4**2,
          q4*q3*q2,
          q3**2*q2**2 + q4**2*q2**2 + q4**2*q3**2,
          q6**2 + 1/3*q5**2,
          q6**3 - q5**2*q6,
          2*q2**2*q6 - q3**2*q6 - q4**2*q6 + q3**2*q5 - q4**2*q5,
          2*q2**2*q6**2 - q3**2*q6**2 - q4**2*q6**2 - 2*q3**2*q5*q6
          + 2*q4**2*q5*q6 - 2/3*q2**2*q5**2 + 1/3*q3**2*q5**2
          + 1/3*q4**2*q5**2,
          - q3**2*q2**2*q6 - q4**2*q2**2*q6 + 2*q4**2*q3**2*q6 -
          q3**2*q2**2*q5 + q4**2*q2**2*q5,
          - q3**2*q2**2*q6**2 - q4**2*q2**2*q6**2 + 2*q4**2*q3**2*q6**2
          + 2*q3**2*q2**2*q5*q6 - 2*q4**2*q2**2*q5*q6 + 1/3*q3**2*q2**2
          *q5**2 + 1/3*q4**2*q2**2*q5**2 - 2/3*q4**2*q3**2*q5**2,
          - 3*q3**2*q2**4*q5*q6**2 + 3*q4**2*q2**4*q5*q6**2
          + 3*q3**4*q2**2*q5*q6**2 - 3*q4**4*q2**2*q5*q6**2
          - 3*q4**2*q3**4*q5*q6**2 + 3*q4**4*q3**2*q5*q6**2
          + 1/3*q3**2*q2**4*q5**3 - 1/3*q4**2*q2**4*q5**3
          - 1/3*q3**4*q2**2*q5**3 + 1/3*q4**4*q2**2*q5**3 + 1/3*q4**2
            *q3**4*q5**3 - 1/3*q4**4*q3**2*q5**3};


% Example 2. (Little) Trinks problem with 7 polynomials in 6 variables.

trinkspolys:={45*p + 35*s - 165*b - 36,
          35*p + 40*z + 25*t - 27*s,
          15*w + 25*p*s + 30*z - 18*t - 165*b**2,
          - 9*w + 15*p*t + 20*z*s,
          w*p + 2*z*t - 11*b**3,
          99*w - 11*s*b + 3*b**2,
          b**2 + 33/50*b + 2673/10000}$

trinksvars :=  {w,p,z,t,s,b}$
torder(trinksvars,lex)$
switch varopt; off varopt;
groebner trinkspolys;

groesolve ws;
 

% Example 3. Hairer, Runge-Kutta 1, 6 polynomials 8 variables.
 
torder({c2,c3,b3,b2,b1,a21,a32,a31},lex);
groebnerf{c2 - a21,
          c3 - a31 - a32,
          b1 + b2 + b3 - 1,
          b2*c2 + b3*c3 - 1/2,
          b2*c2**2 + b3*c3**2 - 1/3,
          b3*a32*c2 - 1/6};
 
        
% The examples 4 and 5 use automatic variable extraction.

% Example 4.
 
torder gradlex$
 
g4:= 
groebner{b + e + f - 1,
         c + d + 2*e - 3,
         b + d + 2*f - 1,
         a - b - c - d - e - f,
         d*e*a**2 - 1569/31250*b*c**3,
         c*f - 587/15625*b*d};
hilbertpolynomial g4;
glexconvert(g4,gvarslast,newvars={e},maxdeg=8);

% Example 5.

off varopt;
torder({u0,u2,u3,u1},lex)$
groesolve({u0**2 - u0 + 2*u1**2 + 2*u2**2 + 2*u3**2,
          2*u0*u1 + 2*u1*u2 + 2*u2*u3 - u1,
          2*u0*u2 + u1**2 + 2*u1*u3 - u2,
          u0 + 2*u1 + 2*u2 + 2*u3 - 1},
         {u0,u2,u3,u1});
 
% Example 6. (Big) Trinks problem with 6 polynomials in 6 variables.
 
torder(trinksvars,lex)$
btbas:=
 groebner{45*p + 35*s - 165*b - 36,
          35*p + 40*z + 25*t - 27*s,
          15*w + 25*p*s + 30*z - 18*t - 165*b**2,
          -9*w + 15*p*t + 20*z*s,
           w*p + 2*z*t - 11*b**3,
          99*w - 11*b*s + 3*b**2};
 
% The above system has dimension zero. Therefore its Hilbert polynomial
% is a constant which is the number of zero points (including complex
% zeros and multipliticities);

hilbertpolynomial ws;

% Example of Groebner with numerical postprocessing.

on rounded;off varopt;
groesolve(trinkspolys,trinksvars);
off rounded;

% Additional groebner operators.

% Reduce one polynomial wrt the basis of big Trinks. The result 0
% is a proof for the ideal membership of the polynomial.

torder(trinksvars,lex)$
preduce(45*p + 35*s - 165*b - 36,btbas);
 
% The following examples show how to work with the distributive
% form of polynomials.

torder({u0,u1,u2,u3},gradlex)$
gsplit(2*u0*u2 + u1**2 + 2*u1*u3 - u2,{u0,u1,u2,u3});

torder(trinksvars,lex)$
gsort trinkspolys;
 
gspoly(first trinkspolys,second trinkspolys);

gvars trinkspolys;

% Tagged basis and reduction trace. A tagged basis is a basis where
% each polynomial is equated to a linear combination of the input
% set. A tagged reduction shows how the result is computed by using
% the basis polynomials.

% First example for tagged polynomials: show how a polynomial is
% represented as linear combination of the basis polynomials.

  % First I set up an environment for the computation.

torder(trinksvars,lex)$

  % Then I compute an ordinary Groebner basis.

bas:=groebner trinkspolys$

  % Next I assign a tag to each basis polynomial.

taggedbas:=for i:=1:length bas collect mkid(p,i)=part(bas,i);

  % And finally I reduce a (tagged) polynomial wrt the tagged basis.

preducet(new=w*p + 2*z*t - 11*b**3,taggedbas);

% Second example for tagged polynomials: representing a Groebner basis 
% as a combination of the input polynomials, here in a simple geometric
% problem.

torder({x,y},lex)$
groebnert {circle=x**2 + y**2 - r**2,line=a*x + b*y};

% In the third example I enter two polynomials that have no common zero.
% Consequently the basis is {1}. The tagged computation gives me a proof
% for the inconsistency of the system which is independent of the
% Groebner formalism.

groebnert {circle1=x**2 + y**2 - 10,circle2=x**2 + y**2 - 2};

% Solve a special elimination task by using a blockwise elimination
% order defined by a matrix.  The equation set goes back to A.M.H.
% Levelt (Nijmegen).  The question is whether there is a member in the
% ideal which depends only on two variables.  Here we select x4 and y1.
% The existence of such a polynomial proves that the system has exactly
% one degree of freedom.

% The first two rows of the term order matrix define the groupwise
% elimination. The remaining lines define a secondary local
% lexicographical behavior which is needed to construct an admissible
% ordering.

f1:=y1^2 + z1^2 -1;
f2:=x2^2 + y2^2 + z2^2 -1;
f3:=x3^2 + y3^2 + z3^2 -1;
f4:=x4^2 + z4^2 -1;
f5:=y1*y2 + z1*z2;
f6:=x2*x3 + y2*y3 + z2*z3;
f7:=x3*x4 + z3*z4;
f8:=x2 + x3 + x4 + 1;
f9:=y1 + y2 + y3 - 1;
f10:=z1 + z2 + z3 + z4;

eqns:={f1,f2,f3,f4,f5,f6,f7,f8,f9,f10}$
vars:={x2,x3,y2,y3,z1,z2,z3,z4,x4,y1}$

torder(vars,matrix,
mat((1,1,1,1,1,1,1,1,0,0),
    (0,0,0,0,0,0,0,0,1,1),
    (1,0,0,0,0,0,0,0,0,0),
    (0,1,0,0,0,0,0,0,0,0),
    (0,0,1,0,0,0,0,0,0,0),
    (0,0,0,1,0,0,0,0,0,0),
    (0,0,0,0,1,0,0,0,0,0),
    (0,0,0,0,0,1,0,0,0,0),
    (0,0,0,0,0,0,1,0,0,0),
    (0,0,0,0,0,0,0,0,1,0)));

first reverse groebner(eqns,vars);

% For a faster execution we convert the matrix into a
% proper machine code routine.

on comp;
torder_compile(levelt,mat(
  (1,1,1,1,1,1,1,1,0,0),
  (0,0,0,0,0,0,0,0,1,1),
  (1,0,0,0,0,0,0,0,0,0),
  (0,1,0,0,0,0,0,0,0,0),
  (0,0,1,0,0,0,0,0,0,0),
  (0,0,0,1,0,0,0,0,0,0),
  (0,0,0,0,1,0,0,0,0,0),
  (0,0,0,0,0,1,0,0,0,0),
  (0,0,0,0,0,0,1,0,0,0),
  (0,0,0,0,0,0,0,0,1,0)));
torder(vars,levelt)$
first reverse groebner(eqns,vars);

% For a homogeneous polynomial set we compute a graded Groebner
% basis with grade limits. We use the graded term order with lex
% as following order. As the grade vector has no zeros, this ordering
% is functionally equivalent to a weighted ordering.

torder({x,y,z},graded,{1,1,2},lex);
dd_groebner(0,10,{x^10*y + y*z^5, x*y^12 + y*z^6});  
dd_groebner(0,50,{x^10*y + y*z^5, x*y^12 + y*z^6});  
dd_groebner(0,infinity,{x^10*y + y*z^5, x*y^12 + y*z^6});  

% Test groebner_walk
trinkspolys := {45*p + 35*s - 165*b - 36,
          35*p + 40*z + 25*t - 27*s,
          15*w + 25*p*s + 30*z - 18*t - 165*b**2,
          - 9*w + 15*p*t + 20*z*s,
          w*p + 2*z*t - 11*b**3,
          99*w - 11*s*b + 3*b**2,
          b**2 + 33/50*b + 2673/10000}$
trinksvars :=  {w,p,z,t,s,b}$
torder(trinksvars,gradlex)$
gg:=groebner trinkspolys$
g:=groebner_walk gg$

on div$
g;

on varopt;
g1:=solve({first g},{b});
g0:=sub({first g1},g);
solve({ second g0},{w});
solve({third g0},{p});
solve({part(g0,4)},{z});
solve({part(g0,5)},{t});
solve({part(g0,6)},{s});

g0:=sub({second g1},g);
solve({second g0},{w});
solve({third g0},{p});
solve({part(g0,4)},{z});
solve({part(g0,5)},{t});
solve({part(g0,6)},{s});

% Example after the book "David Cox, John Little, Donal O'Shea:
% "Ideals, Varieties and Algorithms", chapter 2, paragraph 8, example 3.
% This example was given by Shigetoshi Katsura (Japan).
 
off groebopt;torder({x,y,z,l},lex);
g:=groebner{3*x^2+2*y*z-2*x*l,2*x*z-2*y*l,2*x*y-2*z-2*z*l,x^2+y^2+z^2-1}$
gdimension g;
gindependent_sets g;

clear g, gg, trinkspolys, trinksvars$

end;
