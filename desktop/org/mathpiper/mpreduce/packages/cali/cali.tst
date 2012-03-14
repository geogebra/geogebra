% Author H.-G. Graebe | Univ. Leipzig | Version 28.6.1995
% graebe@informatik.uni-leipzig.de

COMMENT

This is an example session demonstrating and testing the facilities
offered by the commutative algebra package CALI.

END COMMENT;

algebraic;
on echo;
off nat; % To make it easier to compare differing output.
showtime;

comment

	####################################
	###				 ###
	###	Introductory Examples    ###
	###				 ###
	####################################

end comment;

% Example 1 : Generating ideals of affine and projective points.


    vars:={t,x,y,z};
    setring(vars,degreeorder vars,revlex);
    mm:=mat((1,1,1,1),(3,2,3,1),(2,1,3,2));

  % The ideal with zero set at the point in A^4 with coordinates
  % equal to the row vectors of mm :

    setideal(m1,affine_points mm);

	% All parameters are as they should be :

    dim m1;
    degree m1;
    groebfactor m1;
    resolve m1$
    bettinumbers m1;

  % The ideal with zero set at the point in P^3 with homogeneous 
  % coordinates equal to the row vectors of mm :

    setideal(m2,proj_points mm);

	% All parameters as they should be ?

    dim m2;
    degree m2;
    groebfactor m2;

	% It seems to be prime ?

    isprime m2;

	% Not, of course, but it is known to be unmixed. 
	% Hence we can use 

    easyprimarydecomposition m2;
  
% Example 2 : 
% The affine monomial curve with generic point (t^7,t^9,t^10).

    setideal(m,affine_monomial_curve({7,9,10},{x,y,z}));

	% The base ring was changed as side effect :

    getring(); 
    vars:=first getring m;

  % Some advanced commutative algebra :
  
  % The analytic spread of m.

    analytic_spread m;

  % The Rees ring Rees_R(vars) over R=S/m.
    
    rees:=blowup(m,vars,{u,v,w}); 

  % It is multihomogeneous wrt. the degree vectors, constructed during
  % blow up. Lets compute weighted Hilbert series :

    setideal(rees,rees)$
    weights:=second getring();
    weightedhilbertseries(gbasis rees,weights);

  % gr_R(vars), the associated graded ring of the irrelevant ideal
  % over R. The short way.

    interreduce sub(x=0,y=0,z=0,rees); 

  % The long (and more general) way. Gives the result in another
  % embedding. 
  
    % Restore the base ring, since it was changed by blowup as a side
    % effect.  
    setring getring m$
    assgrad(m,vars,{u,v,w}); 

  % Comparing the Rees algebra and the symmetric algebra of M :
  
    setring getring m$
    setideal(rees,blowup({},m,{a,b,c}));

	% Lets test weighted Hilbert series once more :

    weights:=second getring();
    weightedhilbertseries(gbasis rees,weights);

	% The symmetric algebra :

    setring getring m$
    setideal(sym,sym(m,{a,b,c}));
    modequalp(rees,sym);

  % Symbolic powers :

    setring getring m$
    setideal(m2,idealpower(m,2));

	% Let's compute a second symbolic power :

    setideal(m3,symbolic_power(m,2));

	% It is different from the ordinary second power.
	% Hence m2 has a trivial component.

    modequalp(m2,m3);

	% Test x for non zero divisor property :

    nzdp(x,m2);
    nzdp(x,m3);

	% Here is the primary decomposition :

    pd:=primarydecomposition m2;

	% Compare the result with m2 :

    setideal(m4,matintersect(first first pd, first second pd));
    modequalp(m2,m4);

	% Compare the result with m3 :

    setideal(m4,first first pd)$
    modequalp(m3,m4);

	% The trivial component can also be removed with a stable
	% quotient computation : 

    setideal(m5,matstabquot(m2,vars))$
    modequalp(m3,m5);


% Example 3 : The Macaulay curve.

    setideal(m,proj_monomial_curve({0,1,3,4},{w,x,y,z}));
    vars:=first getring();
    gbasis m;
 
  % Test whether m is prime :

    isprime m;

  % A resolution of m :
    
    resolve m;

  % m has depth = 1 as can be seen from the 
    
    gradedbettinumbers m; 

  % Another way to see the non perfectness of m :
    
    hilbertseries m; 

  % Just a third approach. Divide out a parameter system :

    ps:=for i:=1:2 collect random_linear_form(vars,1000);
    setideal(m1,matsum(m,ps))$ 

	% dim should be zero and degree > degree m = 4. 
	% A Gbasis for m1 is computed automatically.

    dim m1; 
    degree m1; 

  % The projections of m on the coord. hyperplanes.
 
    for each x in vars collect eliminate(m,{x}); 

% Example 4 : Two submodules of S^4.
  
	% Get the stored result of the earlier computation.

    r:=resolve m$

  % See whether cali!=degrees contains a relict from earlier
  % computations. 
  
    getdegrees();

  % Introduce the 2nd and 3rd syzygy module as new modules.
  % Both are submodules in S^4.

    setmodule(m1,second r)$ setmodule(m2,third r)$ 
 
  % The second is already a gbasis.

    setgbasis m2; 
    getleadterms m1; getleadterms m2;

  % Since rk(F/M)=rk(F/in(M)), they have ranks 1 resp. 3.

    dim m1;
    indepvarsets m1;

  % Its intersection is zero :

    matintersect(m1,m2);

  % Its sum :
 
    setmodule(m3,matsum(m1,m2));
    dim m3;

  % Hence it has a nontrivial annihilator :

    annihilator m3;

  % One can compute isolated primes and primary decomposition also for
  % modules. Let's do it, although being trivial here:
 
    isolatedprimes m3;

    primarydecomposition m3;

  % To get a meaningful Hilbert series make m1 homogeneous :
 
    setdegrees {1,x,x,x}; 
 
  % Reevaluate m1 with the new column degrees. 

    setmodule(m1,m1)$
    hilbertseries m1;

% Example 5 : From the MACAULAY manual (D.Bayer, M.Stillman).
% An elliptic curve on the Veronese in P^5.

    rvars:={x,y,z}$ svars:={a,b,c,d,e,f}$
    r:=setring(rvars,degreeorder rvars,revlex)$
    s:=setring(svars,{for each x in svars collect 2},revlex)$
    map:={s,r,{a=x^2,b=x*y,c=x*z,d=y^2,e=y*z,f=z^2}};
    preimage({y^2z-x^3-x*z^2},map);

% Example 6 : The preimage under a rational map.

    r:=setring({x,y},{},lex)$ s:=setring({t},{},lex)$
    map:={r,s,{x=2t/(t^2+1),y=(t^2-1)/(t^2+1)}};
  
  % The preimage of (0) is the equation of the circle :

    ratpreimage({},map);

  % The preimage of the point (t=3/2) :

    ratpreimage({2t-3},map);


% Example 7 : A zerodimensional ideal.

    setring({x,y,z},{},lex)$
    setideal(n,{x**2 + y + z - 3,x + y**2 + z - 3,x + y + z**2 - 3});

  % The groebner algorithm with factorization :

    groebfactor n;

  % Change the term order and reevaluate n :

    setring({x,y,z},{{1,1,1}},revlex)$
    setideal(n,n);

  % its primes :
 
    zeroprimes n;

  % a vector space basis of S/n :

    getkbase n;

% Example 8 : A modular computation. Since REDUCE has no multivariate
% factorizer, factorprimes has to be turned off !

    on modular$ off factorprimes$
    setmod 181; setideal(n1,n); zeroprimes n1;
    setmod 7; setideal(n1,n); zeroprimes n1;
 
	% Hence some of the primes glue together mod 7.

    zeroprimarydecomposition n1;
    off modular$ on factorprimes$

% Example 9 : Independent sets once more.
  
    n:=10$
    vars:=for i:=1:(2*n) collect mkid(x,i)$
    setring(vars,{},lex)$
    setideal(m,for j:=0:n collect 
            for i:=(j+1):(j+n) product mkid(x,i));
    setgbasis m$
    indepvarsets m;
    dim m;
    degree m;


comment

	####################################
	###				 ###
	###     Local Standard Bases     ###
	###				 ###
	####################################

end comment;


% Example 10 : An example from [ Alonso, Mora, Raimondo ] 

    vars := {z,x,y}$
    r:=setring(vars,{},lex)$
    setideal(m,{x^3+(x^2-y^2)*z+z^4,y^3+(x^2-y^2)*z-z^4});
    dim m;
    degree m;

  % 2 = codim m is the codimension of the curve m. The defining 
  % equations of the singular locus with their nilpotent structure : 

    singular_locus(m,2); 
    groebfactor ws; 

  % Hence this curve has two singular points : 
  % (x=y=z=0) and (y=-x=256/81,z=64/27)
  % Let's find the brances of the curve through the origin.
  % The first critical tropism is (-1,-1,-1).

    off noetherian$
    setring(vars,{{-1,-1,-1}},lex)$
    setideal(m,m);
	% Let's first test two different approaches, not fully
	% integrated into the algebraic interface :
    setideal(m1,homstbasis m);
    setideal(m2,lazystbasis m);
    setgbasis m1$ setgbasis m2$
    modequalp(m1,m2);
    gbasis m;
    modequalp(m,m1);
    dim m;
    degree m;

  % Find the tangent directions not in z-direction :

    tangentcone m; 
    setideal(n,sub(z=1,ws));
    setring r$ on noetherian$ setideal(n,n)$
    degree n;

  % The points of n outside the origin.

    matstabquot(n,{x,y}); 

  % Hence there are two branches x=z'*(a-3+x'),y=z'*(a+y'),z=z'
  % with the algebraic number a : a^2-3a+3=0
  % and the new equations for (z',x',y') :

    setrules {a^2=>3a-3};
    sub(x=z*(a-3+x),y=z*(a+y),m);
    setideal(m1,matqquot(ws,z));

  % This defines a loc. smooth system at the origin, since the
  % jacobian at the origin of the gbasis is nonsingular :

    off noetherian$
    setring getring m;
    setideal(m1,m1);
    gbasis m1;

	% clear the rules previously set.

    setrules {}; 


% Example 11 : The standard basis of another example. 

	% Comparing different approaches.

    vars:={x,y}$
    setring(vars,localorder vars,lex);
    ff:=x^5+y^11+(x+x^3)*y^9;
    setideal(p1,mat2list matjac({ff},vars));
    gbasis p1;

    gbtestversion 2$
    setideal(p2,p1);
    gbasis p2;

    gbtestversion 3$
    setideal(p3,p1);
    gbasis p3;

    gbtestversion 1$
    modequalp(p1,p2);
    modequalp(p1,p3);
    dim p1;
    degree p1;

% Example 12 : A local intersection wrt. a non inflimited term order.

    setring({x,y,z},{},revlex);
    m1:=matintersect({x-y^2,y-x^2},{x-z^2,z-x^2},{y-z^2,z-y^2});
  
	% Delete polynomial units post factum :
  
    deleteunits ws;

	% Detecting polynomial units early :

    on detectunits;
    m1:=matintersect({x-y^2,y-x^2},{x-z^2,z-x^2},{y-z^2,z-y^2});
    off detectunits;


comment

	####################################
	###				 ###
	###  More Advanced Computations  ###
	###				 ###
	####################################

end comment;

  % Return to a noetherian term order:
   
    vars:={x,y,z}$
    setring(vars,degreeorder vars,revlex);
    on noetherian;

% Example 13 : Use of "mod".

  % Polynomials modulo ideals :

    setideal(m,{2x^2+y+5,3y^2+z+7,7z^2+x+1});
    x^2*y^2*z^2 mod m;

  % Lists of polynomials modulo ideals :

    {x^3,y^3,z^3} mod gbasis m;

  % Matrices modulo modules :

    mm:=mat((x^4,y^4,z^4));
    mm1:=tp<< ideal2mat m>>;
    mm mod mm1;

% Example 14 : Powersums through elementary symmetric functions.

    vars:={a,b,c,d,e1,e2,e3,e4}$
    setring(vars,{},lex)$
    m:=interreduce {a+b+c+d-e1,
        a*b+a*c+a*d+b*c+b*d+c*d-e2,
        a*b*c+a*b*d+a*c*d+b*c*d-e3,
        a*b*c*d-e4};
    
    for n:=1:5 collect a^n+b^n+c^n+d^n mod m;    

% Example 15 : The setrules mechanism. 

    setring({x,y,z},{},lex)$
    setrules {aa^3=>aa+1};
    setideal(m,{x^2+y+z-aa,x+y^2+z-aa,x+y+z^2-aa});
    gbasis m;
    
	% Clear the rules previously set.

    setrules {};

% Example 16 : The same example with advanced coefficient domains.

    load_package arnum;
    defpoly aa^3-aa-1;
    setideal(m,{x^2+y+z-aa,x+y^2+z-aa,x+y+z^2-aa});
    gbasis m;

	% The following needs some more time since factorization of
	% arnum's is not so easy :

    groebfactor m;
    off arnum;
    off rational;


comment

	####################################
	###				 ###
	###  Using Advanced Scripts in   ###
	###	a Complex Example	 ###
	###				 ###
	####################################

end comment;


% Example 17 : The square of the 2-minors of a symmetric 3x3-matrix.

    vars:=for i:=1:6 collect mkid(x,i);
    setring(vars,degreeorder vars,revlex);

	% Generating the ideal :

    mm:=mat((x1,x2,x3),(x2,x4,x5),(x3,x5,x6));
    m:=ideal_of_minors(mm,2);
    setideal(n,idealpower(m,2));

	% The ideal itself :

    gbasis n;
    length n;
    dim n;
    degree n;

	% Its radical.

    radical n;

	% Its unmixed radical.

    unmixedradical n;

	% Its equidimensional hull :

    n1:=eqhull n;
    length n1;
    setideal(n1,n1)$ 
    submodulep(n,n1);
    submodulep(n1,n);

	% Hence there is an embedded component. Let's find it making
	% an excursion to symbolic mode. Of course, this can be done
	% also algebraically. 

    symbolic;
    n:=get('n,'basis);

	% This needs even more time than the eqhull, of course.

    u:=primarydecomposition!* n;
    for each x in u collect easydim!* cadr x;
    for each x in u collect degree!* car x;

	% Hence the embedded component is a trivial one. Let's divide
	% it out by a stable ideal quotient calculation :

    algebraic;
    setideal(n2,matstabquot(n,vars));
    modequalp(n1,n2);


comment

	########################################
	###				     ###
	###  Test Examples for New Features  ###
	###				     ###
	########################################

end comment;


% ==> Testing the different zerodimensional solver 

	vars:={x,y,z}$
	setring(vars,degreeorder vars,revlex);
	setideal(m,{x^3+y+z-3,y^3+x+z-3,z^3+x+y-3});
	zerosolve1 m;
	zerosolve2 m;
	setring(vars,{},lex)$ setideal(m,m)$ m1:=gbasis m$
	zerosolve  m1;
	zerosolve1 m1;
	zerosolve2 m1;

% ==> Testing groebfactor, extendedgroebfactor, extendedgroebfactor1 

  % Gerdt et al. : Seventh order KdV type equation.

A1:=-2*L1**2+L1*L2+2*L1*L3-L2**2-7*L5+21*L6$
A2:=7*L7-2*L1*L4+3/7*L1**3$
B1:=L1*(5*L1-3*L2+L3)$
B2:=L1*(2*L6-4*L4)$
B3:=L1*L7/2$
P1:=L1*(L4-L5/2+L6)$
P2:=(2/7*L1**2-L4)*(-10*L1+5*L2-L3)$
P3:=(2/7*L1**2-L4)*(3*L4-L5+L6)$
P4:=A1*(-3*L1+2*L2)+21*A2$
P5:=A1*(2*L4-2*L5)+A2*(-45*L1+15*L2-3*L3)$
P6:=2*A1*L7+A2*(12*L4-3*L5+2*L6)$
P7:=B1*(2*L2-L1)+7*B2$
P8:=B1*L3+7*B2$
P9:=B1*(-2*L4-2*L5)+B2*(2*L2-8*L1)+84*B3$
P10:=B1*(8/3*L5+6*L6)+B2*(11*L1-17/3*L2+5/3*L3)-168*B3$
P11:=15*B1*L7+B2*(5*L4-2*L5)+B3*(-120*L1+30*L2-6*L3)$
P12:=-3*B1*L7+B2*(-L4/2+L5/4-L6/2)+B3*(24*L1-6*L2)$
P13:=3*B2*L7+B3*(40*L4-8*L5+4*L6)$

polys:={P1,P2,P3,P4,P5,P6,P7,P8,P9,P10,P11,P12,P13};
vars:={L7,L6,L5,L4,L3,L2,L1};
clear a1,a2,b1,b2,b3$

	off lexefgb; 
	setring(vars,{},lex);

  % The factorized Groebner algorithm.
	groebfactor polys;

  % The extended Groebner factorizer, producing triangular sets.
	extendedgroebfactor polys;

  % The extended Groebner factorizer with subproblem removal check. 
	extendedgroebfactor1 polys;

  % Gonnet's example (ACM SIGSAM Bulletin 17 (1983), 48 - 49)

vars:={a0,a2,a3,a4,a5,b0,b1,b2,b3,b4,b5,c0,c1,c2,c3,c4,c5};
polys:={a4*b4,
a5*b1+b5+a4*b3+a3*b4,
a2*b2,a5*b5,
(a0+1+a4)*b2+a2*(b0+b1+b4)+c2,
(a0+1+a4)*(b0+b1+b4)+(a3+a5)*b2+a2*(b3+b5)+c0+c1+c4,
(a3+a5)*(b0+b1+b4)+(b3+b5)*(a0+1+a4)+c3+c5-1,
(a3+a5)*(b3+b5),
a5*(b3+b5)+b5*(a3+a5),
b5*(a0+1+2*a4)+a5*(b0+b1+2*b4)+a3*b4+a4*b3+c5,
a4*(b0+b1+2*b4)+a2*b5+a5*b2+(a0+1)*b4+c4,
a2*b4+a4*b2,
a4*b5+a5*b4,
2*a3*b3+a3*b5+a5*b3,
c3+b3*(a0+2+a4)+a3*(b0+2*b1+b4)+b5+a5*b1,
c1+(a0+2+a4)*b1+a2*b3+a3*b2+(b0+b4),
a2*b1+b2,
a5*b3+a3*b5,
b4+a4*b1};

	on lexefgb; % Switching back to the default.
	setring(vars,{},lex);
	groebfactor polys;
	extendedgroebfactor polys;
	extendedgroebfactor1 polys;

  % Schwarz' example s5

vars:=for k:=1:5 collect mkid(x,k);

s5:={
x1**2+x1+2*x2*x5+2*x3*x4,
2*x1*x2+x2+2*x3*x5+x4**2,
2*x1*x3+x2**2+x3+2*x4*x5,
2*x1*x4+2*x2*x3+x4+x5**2,
2*x1*x5+2*x2*x4+x3**2+x5};

	setring(vars,degreeorder vars,revlex);
	m:=groebfactor s5;

  % Recompute a list of problems with listgroebfactor for another term 
  % order. 
	setring(vars,{},lex);
	listgroebfactor m;


% ==> Testing the linear algebra package

  % Find the ideal of points in affine and projective space. 

	vars:=for k:=1:6 collect mkid(x,k);
	setring(vars,degreeorder vars,revlex);
	matrix mm(10,6);
	on rounded;
	for k:=1:6 do for l:=1:10 do mm(l,k):=floor(exp((k+l)/4));
	off rounded;
	mm;
	setideal(u,affine_points mm); setgbasis u$ dim u; degree u;
	setideal(u,proj_points mm); setgbasis u$ dim u; degree u;

  % Change the term order to pure lex in dimension zero.
  % Test both approaches, with and without precomputed borderbasis.

	vars:=for k:=1:6 collect mkid(x,k);
	r1:=setring(vars,{},lex);
	r2:=setring(vars,degreeorder vars,revlex);
	setideal(m,{x1**2+x1+2*x2*x6+2*x3*x5+x4**2,
		2*x1*x2+x2+2*x3*x6+2*x4*x5,
		2*x1*x3+x2**2+x3+2*x4*x6+x5**2,
		2*x1*x4+2*x2*x3+x4+2*x5*x6,
		2*x1*x5+2*x2*x4+x3**2+x5+x6**2,
		2*x1*x6+2*x2*x5+2*x3*x4+x6});
	gbasis m;
	m1:=change_termorder(m,r1);
	setring r2$ m2:=change_termorder1(m,r1);
	setideal(m1,m1)$ setideal(m2,m2)$
	setgbasis m1$ setgbasis m2$ modequalp(m1,m2);

% ==> Different hilbert series driver
   
    setideal(m,proj_monomial_curve(w1:={0,2,5,9},{w,x,y,z}));
    weights:={{1,1,1,1},w1};
    hftestversion 2;
    f1:=weightedhilbertseries(gbasis m,weights);
    sub(x=1,ws); % The ordinary Hilbert series.
    hftestversion 1; % The default.
    f2:=weightedhilbertseries(gbasis m,weights);
    sub(x=1,ws); 
    f1-f2;

% ==> Different primary decomposition approaches. The example is due
	% to Shimoyama Takeshi. CALI 2.2. produced auxiliary embedded
	% primes on it.

    vars:={dx,dy,x,y};
    setring(vars,degreeorder vars,revlex);
    f3:={DY*( - X*DX + Y**2*DY - Y*DY),DX*(X**2*DX - X*DX - Y*DY)}$
    primarydecomposition f3;

showtime;

end;
