

		  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
		  %   Twisting type N solutions of GR   %
		  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% The problem is to analyse an ansatz for a particular type of vacuum
% solution to Einstein's equations for general relativity. The analysis was
% described by Finley and Price (Proc Aspects of GR and Math Phys
% (Plebanski Festschrift), Mexico City June 1993). The equations resulting
% from the ansatz are:

% F    - F*gamma = 0
%  3 3
%
% F   *x  + 2*F *x    + x     *F - x *Delta*F = 0
%  2 2  1      2  1 2    1 2 2      1
%
% 2*F   *x    + 2*F *x      + 2*F   *x    + 2*F *x      + x       *F = 0
%    2 3  2 3      2  2 3 3      3 2  2 3      3  2 2 3    2 2 3 3
%
% Delta =0       Delta  neq 0
%      3	           1      
%
% gamma =0       gamma  neq 0
%      2	           1      

% where the unknowns are {F,x,gamma,Delta} and the indices refer to
% derivatives with respect to an anholonomic basis. The highest order is 4,
% but the 4th order jet bundle is too large for practical computation, so
% it is necessary to construct partial prolongations. There is a single
% known solution, due to Hauser, which is verified at the end.

on evallhseqp,edssloppy,edsverbose;
off arbvars,edsdebug;

pform {F,x,Delta,gamma,v,y,u}=0;
pform v(i)=0,omega(i)=1;
indexrange {i,j,k,l}={1,2,3};

% Construct J1({v,y,u},{x}) and transform coordinates. Use ordering
% statement to get v eliminated in favour of x where possible.
% NB Coordinate change cc1 is invertible only when x(-1) neq 0.

J1 := contact(1,{v,y,u},{x});
korder x(-1),x(-2),v(-3);
cc1 :=	{x(-v) = x(-1),
	 x(-y) = x(-2),
	 x(-u) = -x(-1)*v(-3)};
J1 := restrict(pullback(J1,cc1),{x(-1) neq 0});

% Set up anholonomic cobasis

bc1 :=	{omega(1) = d v - v(-3)*d u,
	 omega(2) = d y,
	 omega(3) = d u};
J1 := transform(J1,bc1);

% Prolong to J421: 4th order in x, 2nd in F and 1st in rest

J2 := prolong J1$
J20 := J2 cross {F}$
J31 := prolong J20$
J310 := J31 cross {Delta,gamma}$
J421 := prolong J310$
cc4 := first pullback_maps;

% Apply first order de and restrictions

de1 :=	{Delta(-3) = 0,
	 gamma(-2) = 0,
	 Delta(-1) neq 0,
	 gamma(-1) neq 0};

J421 := pullback(J421,de1)$

% Main de in original coordinates 

de2 :=	{F(-3,-3) - gamma*F,
	 x(-1)*F(-2,-2) + 2*x(-1,-2)*F(-2)
		 + (x(-1,-2,-2) - x(-1)*Delta)*F,
	 x(-2,-3)*(F(-2,-3)+F(-3,-2)) + x(-2,-2,-3)*F(-3)
		 + x(-2,-3,-3)*F(-2) + (1/2)*x(-2,-2,-3,-3)*F};

% This is not expressed in terms of current coordinates.
% Missing coordinates are seen from 1-form variables in following

d de2 xmod cobasis J421;

% The necessary equation is contained in the last prolongation

pullback(d de2,cc4) xmod cobasis J421;

% Apply main de

pb1 := first solve(pullback(de2,cc4),{F(-3,-3),F(-2,-2),F(-2,-3)});
Y421 := pullback(J421,pb1)$

% Check involution

on ranpos;
characters Y421;
dim_grassmann_variety Y421;

% 15+2*7 = 29 > 28: Y421 not involutive, so prolong

Y532 := prolong Y421$

characters Y532;
dim_grassmann_variety Y532;

% 22+2*6 = 34: just need to check for integrability conditions

torsion Y532;

% Y532 involutive. Dimensions?

dim Y532;
length one_forms Y532;

% The following puts in part of Hauser's solution and ends up with an ODE
% system (all characters 0), so no more solutions, as described by Finley
% at MG6.

hauser := {x=-v+(1/2)*(y+u)**2,delta=3/(8x),gamma=3/(8v)};
H532 := pullback(Y532,hauser)$
lift ws;
characters ws;

clear v(i),omega(i);
clear F,x,Delta,gamma,v,y,u,omega;
off ranpos;



	%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	%   Isometric embeddings of Ricci-flat R(4) in ISO(10)   %
	%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Determine the Cartan characters of a Ricci-flat embedding of R(4) into
% the orthonormal frame bundle ISO(10) over flat R(6).  Reference:
% Estabrook & Wahlquist, Class Quant Grav 10(1993)1851

% Indices

indexrange {p,q,r,s}={1,2,3,4,5,6,7,8,9,10},
      	   {i,j,k,l}={1,2,3,4},{a,b,c,d}={5,6,7,8,9,10};

% Metric for R10

pform g(p,q)=0;
g(p,q) := 0$ g(-p,-q) := 0$ g(-p,-p) := g(p,p) := 1$

% Hodge map for R4

pform epsilon(i,j,k,l)=0;
index_symmetries epsilon(i,j,k,l):antisymmetric;
epsilon(1,2,3,4) := 1;

% Coframe for ISO(10)
% NB index_symmetries must come after o(p,-q) := ... (EXCALC bug)

pform e(r)=1,o(r,s)=1;
korder index_expand {e(r)};
e(-p) := g(-p,-q)*e(q)$
o(p,-q) := o(p,r)*g(-r,-q)$
index_symmetries o(p,q):antisymmetric;

% Structure equations

flat_no_torsion := {d e(p) => -o(p,-q)^e(q),
      	       	    d o(p,q) => -o(p,-r)^o(r,q)};

% Coframing structure

ISO := coframing({e(p),o(p,q)},flat_no_torsion)$
dim ISO;

% 4d curvature 2-forms

pform F(i,j)=2;
index_symmetries F(i,j):antisymmetric;
F(-i,-j) := -g(-i,-k)*o(k,-a)^o(a,-j);

% EDS for vacuum GR (Ricci-flat) in 4d

GR0 := eds({e(a),epsilon(i,j,k,l)*F(-j,-k)^e(-l)},
	   {e(i)},
      	   ISO)$

% Find an integral element, and linearise

Z := integral_element GR0$
GRZ := linearise(GR0,Z)$

% This actually tells us the characters already:
%  {45-39,39-29,29-21,21} = {6,10,8,21}

% Get the characters and dimension at Z

characters GRZ;
dim_grassmann_variety GRZ;

% 6+2*10+3*8+4*21 = 134, so involutive

clear e(r),o(r,s),g(p,q),epsilon(i,j,k,l),F(i,j);
clear e,o,g,epsilon,F,Z;
indexrange 0;

			%%%%%%%%%%%%%%%%%%%%%%%%%%
			%   Janet's PDE system   %
			%%%%%%%%%%%%%%%%%%%%%%%%%%

% This is something of a standard test problem in analysing integrability
% conditions. Although it looks very innocent, it must be prolonged five
% times from the second jet bundle before reaching involution. The initial 
% equations are just
%
%     u   =w,    u   =u   *y + v
%      y y        z z  x x 

load sets;
off varopt;
pform {x,y,z,u,v,w}=0$

janet := contact(2,{x,y,z},{u,v,w})$
janet := pullback(janet,{u(-y,-y)=w,u(-z,-z)=y*u(-x,-x)+v})$

% Prolong to involution

involutive janet;
involution janet;
involutive ws;

% Solve the homogeneous system, for which the
% involutive prolongation is completely integrable

fdomain u=u(x,y,z),v=v(x,y,z),w=w(x,y,z);

janet := {@(u,y,y)=0,@(u,z,z)=y*@(u,x,x)};
janet := involution pde2eds janet$

% Check if completely integrable
if frobenius janet then write "yes" else write "no";
length one_forms janet;

% So there are 12 constants in the solution: there should be 12 invariants

length(C := invariants janet);
solve(for i:=1:length C collect
         part(C,i) = mkid(k,i),coordinates janet \ {x,y,z})$
S := select(lhs ~q = u,first ws);

% Check solution
mkdepend dependencies;
sub(S,{@(u,y,y),@(u,z,z)-y*@(u,x,x)});

clear u(i,j),v(i,j),w(i,j),u(i),v(i),w(i);
clear x,y,z,u,v,w,C,S;

end;
