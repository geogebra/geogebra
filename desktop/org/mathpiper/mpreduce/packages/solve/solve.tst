% Demonstration of the REDUCE SOLVE package.

on fullroots;   % To get complete solutions.

% Simultaneous linear fractional equations.

solve({(a*x+y)/(z-1)-3,y+b+z,x-y},{x,y,z});


% Use of square-free factorization together with recursive use of
% quadratic and binomial solutions.

solve((x**6-x**3-1)*(x**5-1)**2*x**2);

multiplicities!*;


% A singular equation without and with a consistent inhomogeneous term.

solve(a,x);

solve(0,x);

off solvesingular;

solve(0,x);


% Use of DECOMPOSE to solve high degree polynomials.

solve(x**8-8*x**7+34*x**6-92*x**5+175*x**4-236*x**3+226*x**2-140*x+46);

solve(x**8-88*x**7+2924*x**6-43912*x**5+263431*x**4-218900*x**3+
           65690*x**2-7700*x+234,x);


% Recursive use of inverses, including multiple branches of rational
% fractional powers.

solve(log(acos(asin(x**(2/3)-b)-1))+2,x);


% Square-free factors that are unsolvable, being of fifth degree,
% transcendental, or without a defined inverse.

operator f;

solve((x-1)*(x+1)*(x-2)*(x+2)*(x-3)*(x*log(x)-1)*(f(x)-1),x);

multiplicities!*;


% Factors with more than one distinct top-level kernel, the first factor
% a cubic. (Cubic solution suppressed since it is too messy to be of
% much use).

off fullroots;

solve((x**(1/2)-(x-a)**(1/3))*(acos x-acos(2*x-b))* (2*log x
          -log(x**2+x-c)-4),x);

on fullroots;

% Treatment of multiple-argument exponentials as polynomials.

solve(a**(2*x)-3*a**x+2,x);


% A 12th degree reciprocal polynomial that is irreductible over the
% integers, having a reduced polynomial that is also reciprocal.
% (Reciprocal polynomials are those that have symmetric or antisymmetric
% coefficient patterns.) We also demonstrate suppression of automatic
% integer root extraction.

solve(x**12-4*x**11+12*x**10-28*x**9+45*x**8-68*x**7+69*x**6-68*x**5+
45*x**4-28*x**3+12*x**2-4*x+1);


% The treatment of factors with non-unique inverses by introducing
% unique new real or integer indeterminant kernels.

solve((sin x-a)*(2**x-b)*(x**c-3),x);


% Automatic restriction to principal branches.

off allbranch;

solve((sin x-a)*(2**x-b)*(x**c-3),x);


% Regular system of linear equations.

solve({2*x1+x2+3*x3-9,x1-2*x2+x3+2,3*x1+2*x2+2*x3-7}, {x1,x2,x3});


% Underdetermined system of linear equations.

on solvesingular;

solve({x1-4*x2+2*x3+1,2*x1-3*x2-x3-5*x4+7,3*x1-7*x2+x3-5*x4+8},
      {x1,x2,x3,x4});


% Inconsistent system of linear equations.

solve({2*x1+3*x2-x3-2,7*x1+4*x2+2*x3-8,3*x1-2*x2+4*x3-5},
      {x1,x2,x3});


% Overdetermined system of linear equations.

solve({x1-x2+x3-12,2*x1+3*x2-x3-13,3*x2+4*x3-5,-3*x1+x2+4*x3+20},
      {x1,x2,x3});


% Degenerate system of linear equations.

operator xx,yy;

yy(1) := -a**2*b**3-3*a**2*b**2-3*a**2*b+a**2*(xx(3)-2)-a*b-a*c+a*(xx(2)
         -xx(5))-xx(4)-xx(5)+xx(1)-1;

yy(2) := -a*b**3-b**5+b**4*(-xx(4)-xx(5)+xx(1)-5)-b**3*c+b**3*(xx(2)
         -xx(5)-3)+b**2*(xx(3)-1);

yy(3) := -a*b**3*c-3*a*b**2*c-4*a*b*c+a*b*(-xx(4)-xx(5)+xx(1)-1)
         +a*c*(xx(3)-1)-b**2*c-b*c**2+b*c*(xx(2)-xx(5));

yy(4) := -a**2-a*c+a*(xx(2)-xx(4)-2*xx(5)+xx(1)-1)-b**4-b**3*c-3*b**3
         -3*b**2*c-2*b**2-2*b*c+b*(xx(3)-xx(2)-xx(4)+xx(1)-2)
         +c*(xx(3)-1);

yy(5) := -2*a-3*b**3-9*b**2-11*b-2*c+3*xx(3)+2*xx(2)-xx(4)-3*xx(5)+xx(1)
         -4;

soln  :=  solve({yy(1),yy(2),yy(3),yy(4),yy(5)},
                {xx(1),xx(2),xx(3),xx(4),xx(5)});

for i  :=  1:5 do xx(i) := part(soln,1,i,2);

for i  :=  1:5 do write yy(i);


%  Single equations liftable to polynomial systems.

solve ({a*sin x + b*cos x},{x});

solve ({a*sin(x+1) + b*cos(x+1)},{x});
 
% Intersection of 2 curves: system with a free parameter.

solve ({sqrt(x^2 + y^2)=r,0=sqrt(x)+ y**3-1},{x,y,r});

solve ({e^x - e^(1/2 * x) - 7},{x});

% Generally not liftable.
  
   % variable inside and outside of sin.

   solve({sin x + x - 1/2},{x});
 
   % Variable inside and outside of exponential.

   solve({e^x - x**2},{x});

   % Variable inside trigonometrical functions with different forms.

   solve ({a*sin(x+1) + b*cos(x+2)},{x});
 
   % Undetermined exponents.

   solve({x^a - 2},{x});
 

% Example taken from M.L. Griss, ACM Trans. Math. Softw. 2 (1976) 1.

e1 := x1 - l/(3*k)$

e2 := x2 - 1$

e3 := x3 - 35*b6/(6*l)*x4 + 33*b11/(2*l)*x6 - 715*b15/(14*l)*x8$

e4 := 14*k/(3*l)*x1 - 7*b4/(2*l)*x3 + x4$

e5 := x5 - 891*b11/(40*l)*x6 +3861*b15/(56*l)*x8$

e6 := -88*k/(15*l)*x1 + 22*b4/(5*l)*x3 - 99*b9/(8*l)*x5 +x6$

e7 := -768*k/(5005*b13)*x1 + 576*b4/(5005*b13)*x3 -
      324*b9/(1001*b13)*x5 + x7 - 16*l/(715*b13)*x8$

e8 := 7*l/(143*b15)*x1 + 49*b6/(429*b15)*x4 - 21*b11/(65*b15)*x6 +
      x8 - 7*b2/(143*b15)$

solve({e1,e2,e3,e4,e5,e6,e7,e8},{x1,x2,x3,x4,x5,x6,x7,x8});


f1 := x1 - x*x2 - y*x3 + 1/2*x**2*x4 + x*y*x5 + 1/2*y**2*x6 +
      1/6*x**3*x7 + 1/2*x*y*(x - y)*x8 - 1/6*y**3*x9$

f2 := x1 - y*x3 + 1/2*y**2*x6 - 1/6*y**3*x9$

f3 := x1 + y*x2 - y*x3 + 1/2*y**2*x4 - y**2*x5 + 1/2*y**2*x6 +
      1/6*y**3*x7 + 1/2*y**3*x8 - 1/6*y**3*x9$

f4 := x1 + (1 - x)*x2 - x*x3 + 1/2*(1 - x)**2*x4 - y*(1 - x)*x5 +
      1/2*y**2*x6 + 1/6*(1 - x)**3*x7 + 1/2*y*(1 - x - y)*(1 - x)*x8
      - 1/6*y**3*x9$

f5 := x1 + (1 - x - y)*x2 + 1/2*(1 - x - y)**2*x4 +
      1/6*(1 - x - y)**3*x7$

f6 := x1 + (1 - x - y)*x3 + 1/2*(1 - x - y)*x6 +
      1/6*(1 - x - y)**3*x9$

f7 := x1 - x*x2 + (1 - y)*x3 + 1/2*x*x4 - x*(1 - y)*x5 +
      1/2*(1 - y)**2*x6 - 1/6*x**3*x7 + 1/2*x*(1 - y)*(1 - y + x)*x8
      + 1/6*(1-y)**3*x9$

f8 := x1 - x*x2 + x*x3 + 1/2*x**2*x4 - x**2*x5 + 1/2*x**2*x6 +
      1/6*x**3*x7 - 1/2*x**3*x8 + 1/6*x**3*x9$

f9 := x1 - x*x2 + 1/2*x**2*x4 + 1/6*x**3*x7$

solve({f1,f2,f3,f4,f5,f6,f7,f8,f9},{x1,x2,x3,x4,x5,x6,x7,x8,x9});

solve({f1 - 1,f2,f3,f4,f5,f6,f7,f8,f9},{x1,x2,x3,x4,x5,x6,x7,x8,x9});


% The following examples were discussed in Char, B.W., Fee, G.J.,
% Geddes, K.O., Gonnet, G.H., Monagan, M.B., Watt, S.M., "On the
% Design and Performance of the Maple System", Proc. 1984 Macsyma
% Users' Conference, G.E., Schenectady, NY, 1984, 199-219.

% Problem 1.

solve({ -22319*x0+25032*x1-83247*x2+67973*x3+54189*x4
       -67793*x5+81135*x6+22293*x7+27327*x8+96599*x9-15144,
       79815*x0+37299*x1-28495*x2-52463*x3+25708*x4 -55333*x5-
       2742*x6+83127*x7-29417*x8-43202*x9+93314, -29065*x0-77803*x1-
       49717*x2-64748*x3-68324*x4 -50162*x5-64222*x6-
       4716*x7+30737*x8+22971*x9+90348, 62470*x0+59658*x1-
       46120*x2+58376*x3-28208*x4 -74506*x5+28491*x6+21099*x7+29149*x8-
       20387*x9+36254, -98233*x0-26263*x1-63227*x2+34307*x3+92294*x4
       +10148*x5+3192*x6+24044*x7-83764*x8-1121*x9+13871,
       -20427*x0+62666*x1+27330*x2-78670*x3+9036*x4 +56024*x5-4525*x6-
       50589*x7-62127*x8-32846*x9+38466,
       -85609*x0+5424*x1+86992*x2+59651*x3-60859*x4 -55984*x5-
       6061*x6+44417*x7+92421*x8+6701*x9-9459,
       -68255*x0+19652*x1+92650*x2-93032*x3-30191*x4 -31075*x5-
       89060*x6+12150*x7-78089*x8-12462*x9+1027, 55526*x0-
       91202*x1+91329*x2-25919*x3-98215*x4 +30554*x5+913*x6-
       35751*x7+17948*x8-58850*x9+66583, 40612*x0+84364*x1-
       83317*x2+10658*x3+37213*x4 +50489*x5+72040*x6-
       21227*x7+60772*x8+95114*x9-68533});

solve({ -22319*x0+25032*x1-83247*x2+67973*x3+54189*x4
        -67793*x5+81135*x6+22293*x7+27327*x8+96599*x9-15144,
        79815*x0+37299*x1-28495*x2-52463*x3+25708*x4 -55333*x5-
        2742*x6+83127*x7-29417*x8-43202*x9+93314, -29065*x0-77803*x1-
        49717*x2-64748*x3-68324*x4 -50162*x5-64222*x6-
        4716*x7+30737*x8+22971*x9+90348, 62470*x0+59658*x1-
        46120*x2+58376*x3-28208*x4-74506*x5+28491*x6+21099*x7+29149*x8-
        20387*x9+36254,-98233*x0-26263*x1-63227*x2+34307*x3+92294*x4
        +10148*x5+3192*x6+24044*x7-83764*x8-1121*x9+13871,
        -20427*x0+62666*x1+27330*x2-78670*x3+9036*x4 +56024*x5-4525*x6-
        50589*x7-62127*x8-32846*x9+38466,
        -85609*x0+5424*x1+86992*x2+59651*x3-60859*x4 -55984*x5-
        6061*x6+44417*x7+92421*x8+6701*x9-9459,
        -68255*x0+19652*x1+92650*x2-93032*x3-30191*x4 -31075*x5-
        89060*x6+12150*x7-78089*x8-12462*x9+1027, 55526*x0-
        91202*x1+91329*x2-25919*x3-98215*x4 +30554*x5+913*x6-
        35751*x7+17948*x8-58850*x9+66583, 40612*x0+84364*x1-
        83317*x2+10658*x3+37213*x4 +50489*x5+72040*x6-
        21227*x7+60772*x8+95114*x9-68533});


% The next two problems give the current routines some trouble and
% have therefore been commented out.

% Problem 2.

comment
solve({ 81*x30-96*x21-45, -36*x4+59*x29+26,
       -59*x26+5*x3-33, -81*x19-92*x23-21*x17-9, -46*x29-
       13*x22+22*x24+83, 47*x4-47*x14-15*x26-40, 83*x30+70*x17+56*x10-
       31, 10*x27-90*x9+52*x21+52, -33*x20-97*x26+20*x6-76,
       97*x16+41*x8-13*x12+66, 16*x16-52*x10-73*x28+49, -28*x1-53*x24-
       x27-67, -22*x26-29*x24+73*x10+8, 88*x18+61*x19-98*x9-55, 99*x28-
       91*x26+26*x21-95, -6*x18+25*x7-77*x2+99, 28*x13-50*x17-52*x14-64,
       -50*x20+26*x11+93*x2+77, -70*x8+74*x19-94*x26+86, -18*x18-2*x16-
       79*x23+91, 36*x26-13*x11-53*x25-5, 10*x7+57*x16-85*x10-14,
       -3*x27+44*x4+52*x22-1, 21*x11+20*x25-30*x4-83, 70*x2-97*x19-
       41*x26-50, -51*x8+95*x12-85*x26+45, 83*x30+41*x12+50*x2+53,
       -4*x26+69*x8-58*x5-95, 59*x27-78*x30-66*x23+16, -10*x20-36*x11-
       60*x1-59});


% Problem 3.
comment
solve({ 115*x40+566*x41-378*x42+11401086415/6899901,
       560*x0-45*x1-506*x2-11143386403/8309444, -621*x1-
       328*x2+384*x3+1041841/64675, -856*x2+54*x3+869*x4-41430291/24700,
       596*x3-608*x4-560*x5-10773384/11075,
       -61*x4+444*x5+924*x6+4185100079/11278780, 67*x5-95*x6-
       682*x7+903866812/6618863, 196*x6+926*x7-930*x8-
       2051864151/2031976, -302*x7-311*x8-890*x9-14210414139/27719792,
       121*x8-781*x9-125*x10-4747129093/39901584, 10*x9+555*x10-
       912*x11+32476047/3471829, -151*x38+732*x39-
       397*x40+327281689/173242, 913*x10-259*x11-982*x12-
       18080663/5014020, 305*x11+9*x12-357*x13+1500752933/1780680,
       179*x12-588*x13+665*x14+8128189/51832, 406*x13+843*x14-
       833*x15+201925713/97774, 107*x14+372*x15+505*x16-
       5161192791/3486415, 720*x15-212*x16+607*x17-31529295571/7197760,
       951*x16-685*x17+148*x18+1034546543/711104, -654*x17-
       899*x18+543*x19+1942961717/1646560,
       -448*x18+673*x19+702*x20+856422818/1286375, 396*x19-
       196*x20+218*x21-4386267866/21303625, -233*x20-796*x21-373*x22-
       85246365829/57545250, 921*x21-368*x22+730*x23-
       93446707622/51330363, -424*x22+378*x23+727*x24-
       6673617931/3477462, -633*x23+565*x24-208*x25+8607636805/4092942,
       971*x24+170*x25-865*x26-25224505/18354, 937*x25+333*x26-463*x27-
       339307103/1025430, 494*x26-8*x27-50*x28+57395804/34695,
       530*x27+631*x28-193*x29-8424597157/680022,
       -435*x28+252*x29+916*x30+196828511/19593, 327*x29+403*x30-
       845*x31+8458823325/5927971, 246*x30+881*x31-
       394*x32+13624765321/156546826, 946*x31+169*x32-43*x33-
       53594199271/126093183, -146*x32+503*x33-
       363*x34+66802797635/15234909, -132*x33-
       686*x34+376*x35+8167530636/902635, -38*x34-188*x35-
       583*x36+1814153743/1124240, 389*x35+562*x36-688*x37-
       12251043951/5513560, -769*x37-474*x38-89*x39-2725415872/1235019,
       -625*x36-122*x37+468*x38+7725682775/4506736,
       839*x39+936*x40+703*x41+1912091857/1000749,
       -314*x41+102*x42+790*x43+7290073150/8132873, -905*x42-
       454*x43+524*x44-10110944527/4538233, 379*x43+518*x44-328*x45-
       2071620692/519645, 284*x44-979*x45+690*x46-915987532/16665,
       198*x45-650*x46-763*x47+548801657/11220, 974*x46+12*x47+410*x48-
       3831097561/51051, -498*x47-135*x48-230*x49-18920705/9282,
       665*x48+156*x49+34*x0-27714736/156585, -519*x49-366*x0-730*x1-
       2958446681/798985});


% Problem 4.

% This one needs the Cramer code --- it takes forever otherwise.

on cramer;

solve({ -b*k8/a+c*k8/a, -b*k11/a+c*k11/a,
       -b*k10/a+c*k10/a+k2,
        -k3-b*k9/a+c*k9/a, -b*k14/a+c*k14/a, -b*k15/a+c*k15/a,
        -b*k18/a+c*k18/a-k2, -b*k17/a+c*k17/a, -b*k16/a+c*k16/a+k4,
        -b*k13/a+c*k13/a-b*k21/a+c*k21/a+b*k5/a-c*k5/a,
        b*k44/a-c*k44/a, -b*k45/a+c*k45/a, -b*k20/a+c*k20/a,
        -b*k44/a+c*k44/a, b*k46/a-c*k46/a,
        b**2*k47/a**2-2*b*c*k47/a**2+c**2*k47/a**2,
        k3, -k4, -b*k12/a+c*k12/a-a*k6/b+c*k6/b,
        -b*k19/a+c*k19/a+a*k7/c-b*k7/c, b*k45/a-c*k45/a,
        -b*k46/a+c*k46/a, -k48+c*k48/a+c*k48/b-c**2*k48/(a*b),
        -k49+b*k49/a+b*k49/c-b**2*k49/(a*c), a*k1/b-c*k1/b,
        a*k4/b-c*k4/b, a*k3/b-c*k3/b+k9, -k10+a*k2/b-c*k2/b,
        a*k7/b-c*k7/b, -k9, k11, b*k12/a-c*k12/a+a*k6/b-c*k6/b,
        a*k15/b-c*k15/b, k10+a*k18/b-c*k18/b,
        -k11+a*k17/b-c*k17/b, a*k16/b-c*k16/b,
        -a*k13/b+c*k13/b+a*k21/b-c*k21/b+a*k5/b-c*k5/b,
        -a*k44/b+c*k44/b, a*k45/b-c*k45/b,
        a*k14/c-b*k14/c+a*k20/b-c*k20/b, a*k44/b-c*k44/b,
        -a*k46/b+c*k46/b, -k47+c*k47/a+c*k47/b-c**2*k47/(a*b),
        a*k19/b-c*k19/b, -a*k45/b+c*k45/b, a*k46/b-c*k46/b,
        a**2*k48/b**2-2*a*c*k48/b**2+c**2*k48/b**2,
        -k49+a*k49/b+a*k49/c-a**2*k49/(b*c), k16, -k17,
        -a*k1/c+b*k1/c, -k16-a*k4/c+b*k4/c, -a*k3/c+b*k3/c,
        k18-a*k2/c+b*k2/c, b*k19/a-c*k19/a-a*k7/c+b*k7/c,
        -a*k6/c+b*k6/c, -a*k8/c+b*k8/c, -a*k11/c+b*k11/c+k17,
        -a*k10/c+b*k10/c-k18, -a*k9/c+b*k9/c,
        -a*k14/c+b*k14/c-a*k20/b+c*k20/b,
        -a*k13/c+b*k13/c+a*k21/c-b*k21/c-a*k5/c+b*k5/c,
        a*k44/c-b*k44/c, -a*k45/c+b*k45/c, -a*k44/c+b*k44/c,
        a*k46/c-b*k46/c, -k47+b*k47/a+b*k47/c-b**2*k47/(a*c),
        -a*k12/c+b*k12/c, a*k45/c-b*k45/c, -a*k46/c+b*k46/c,
        -k48+a*k48/b+a*k48/c-a**2*k48/(b*c),
        a**2*k49/c**2-2*a*b*k49/c**2+b**2*k49/c**2, k8, k11, -k15,
        k10-k18, -k17, k9, -k16, -k29, k14-k32, -k21+k23-k31,
        -k24-k30, -k35, k44, -k45, k36, k13-k23+k39, -k20+k38,
        k25+k37, b*k26/a-c*k26/a-k34+k42, -2*k44, k45, k46,
        b*k47/a-c*k47/a, k41, k44, -k46, -b*k47/a+c*k47/a,
        k12+k24, -k19-k25, -a*k27/b+c*k27/b-k33, k45, -k46,
        -a*k48/b+c*k48/b, a*k28/c-b*k28/c+k40, -k45, k46,
        a*k48/b-c*k48/b, a*k49/c-b*k49/c, -a*k49/c+b*k49/c,
        -k1, -k4, -k3, k15, k18-k2, k17, k16, k22, k25-k7,
        k24+k30, k21+k23-k31, k28, -k44, k45, -k30-k6, k20+k32,
        k27+b*k33/a-c*k33/a, k44, -k46, -b*k47/a+c*k47/a, -k36,
        k31-k39-k5, -k32-k38, k19-k37, k26-a*k34/b+c*k34/b-k42,
        k44, -2*k45, k46, a*k48/b-c*k48/b, a*k35/c-b*k35/c-k41,
        -k44, k46, b*k47/a-c*k47/a, -a*k49/c+b*k49/c, -k40, k45,
        -k46, -a*k48/b+c*k48/b, a*k49/c-b*k49/c, k1, k4, k3, -k8,
        -k11, -k10+k2, -k9, k37+k7, -k14-k38, -k22, -k25-k37, -k24+k6,
        -k13-k23+k39, -k28+b*k40/a-c*k40/a, k44, -k45, -k27, -k44,
        k46, b*k47/a-c*k47/a, k29, k32+k38, k31-k39+k5, -k12+k30,
        k35-a*k41/b+c*k41/b, -k44, k45, -k26+k34+a*k42/c-b*k42/c,
        k44, k45, -2*k46, -b*k47/a+c*k47/a, -a*k48/b+c*k48/b,
        a*k49/c-b*k49/c, k33, -k45, k46, a*k48/b-c*k48/b,
        -a*k49/c+b*k49/c },
       {k1, k2, k3, k4, k5, k6, k7, k8, k9, k10, k11, k12, k13, k14,
        k15, k16, k17, k18, k19, k20, k21, k22, k23, k24, k25, k26,
        k27, k28, k29, k30, k31, k32, k33, k34, k35, k36, k37, k38,
        k39, k40, k41, k42, k43, k44, k45, k46, k47, k48, k49});

off cramer;

% Problem 5.

solve ({2*a3*b3+a5*b3+a3*b5, a5*b3+2*a5*b5+a3*b5,
        a5*b5, a2*b2, a4*b4, a5*b1+b5+a4*b3+a3*b4,
        a5*b3+a5*b5+a3*b5+a3*b3, a0*b2+b2+a4*b2+a2*b4+c2+a2*b0+a2*b1,
        a0*b0+a0*b1+a0*b4+a3*b2+b0+b1+b4+a4*b0+a4*b1+a2*b5+a4*b4+c1+c4
        +a5*b2+a2*b3+c0,
        -1+a3*b0+a0*b3+a0*b5+a5*b0+b3+b5+a5*b4+a4*b3+a4*b5+a3*b4+a5*b1
        +a3*b1+c3+c5,
        b4+a4*b1, a5*b3+a3*b5, a2*b1+b2, a4*b5+a5*b4, a2*b4+a4*b2,
        a0*b5+a5*b0+a3*b4+2*a5*b4+a5*b1+b5+a4*b3+2*a4*b5+c5,
        a4*b0+2*a4*b4+a2*b5+b4+a4*b1+a5*b2+a0*b4+c4,
        c3+a0*b3+2*b3+b5+a4*b3+a3*b0+2*a3*b1+a5*b1+a3*b4,
        c1+a0*b1+2*b1+a4*b1+a2*b3+b0+a3*b2+b4});


% Problem 6.

solve({2*a3*b3+a5*b3+a3*b5, a5*b3+2*a5*b5+a3*b5,
       a4*b4, a5*b3+a5*b5+a3*b5+a3*b3, b1, a3*b3, a2*b2, a5*b5,
       a5*b1+b5+a4*b3+a3*b4, a0*b2+b2+a4*b2+a2*b4+c2+a2*b0+a2*b1,
       b4+a4*b1, b3+a3*b1, a5*b3+a3*b5, a2*b1+b2, a4*b5+a5*b4,
       a2*b4+a4*b2, a0*b0+a0*b1+a0*b4+a3*b2+b0+b1+b4+a4*b0+a4*b1
       +a2*b5+a4*b4+c1+c4+a5*b2+a2*b3+c0,-1+a3*b0+a0*b3+a0*b5+a5*b0
       +b3+b5+a5*b4+a4*b3+a4*b5+a3*b4+a5*b1+a3*b1+c3+c5,
       a0*b5+a5*b0+a3*b4+2*a5*b4+a5*b1+b5+a4*b3+2*a4*b5+c5,
       a4*b0+2*a4*b4+a2*b5+b4+a4*b1+a5*b2+a0*b4+c4,
       c3+a0*b3+2*b3+b5+a4*b3+a3*b0+2*a3*b1+a5*b1+a3*b4,
       c1+a0*b1+2*b1+a4*b1+a2*b3+b0+a3*b2+b4});

% Example cited by Bruno Buchberger
%        in R.Janssen: Trends in Computer Algebra,
%     Springer, 1987
% Geometry of a simple robot,
%   l1,l2   length of arms
%   ci,si   cos and sin of rotation angles


solve( { c1*c2 -cf*ct*cp + sf*sp,
         s1*c2 - sf*ct*cp - cf*sp,
         s2 + st*cp,
         -c1*s2 - cf*ct*sp + sf*cp,
         -s1*s2 + sf*ct*sp - cf*cp,
         c2 - st*sp,
         s1 - cf*st,
         -c1 - sf*st,
         ct,
         l2*c1*c2 - px,
         l2*s1*c2 - py,
         l2*s2 + l1 - pz,
         c1**2 + s1**2 -1,
         c2**2 + s2**2 -1,
         cf**2 + sf**2 -1,
         ct**2 + st**2 -1,
         cp**2 + sp**2 -1},
      {c1,c2,s1,s2,py,cf,ct,cp,sf,st,sp});

% Steady state computation of a prototypical chemical
% reaction network (the "Edelstein" network)
 
solve(
 { alpha * c1 - beta * c1**2 - gamma*c1*c2 + epsilon*c3,
   -gamma*c1*c2 + (epsilon+theta)*c3 -eta *c2,
   gamma*c1*c2 + eta*c2 - (epsilon+theta) * c3},
  {c3,c2,c1});

solve(
{( - 81*y1**2*y2**2 + 594*y1**2*y2 - 225*y1**2 + 594*y1*y2**2 - 3492*
y1*y2 - 750*y1 - 225*y2**2 - 750*y2 + 14575)/81,
( - 81*y2**2*y3**2 + 594*y2**2*y3 - 225*y2**2 + 594*y2*y3**2 - 3492*
y2*y3 - 750*y2 - 225*y3**2 - 750*y3 + 14575)/81,
( - 81*y1**2*y3**2 + 594*y1**2*y3 - 225*y1**2 + 594*y1*y3**2 - 3492*
y1*y3 - 750*y1 - 225*y3**2 - 750*y3 + 14575)/81,
(2*(81*y1**2*y2**2*y3 + 81*y1**2*y2*y3**2 - 594*y1**2*y2*y3 - 225*y1
**2*y2 - 225*y1**2*y3 + 1650*y1**2 + 81*y1*y2**2*y3**2 - 594*y1*
y2**2*y3 - 225*y1*y2**2 - 594*y1*y2*y3**2 + 2592*y1*y2*y3 + 2550
*y1*y2 - 225*y1*y3**2 + 2550*y1*y3 - 3575*y1 - 225*y2**2*y3 +
1650*y2**2 - 225*y2*y3**2 + 2550*y2*y3 - 3575*y2 + 1650*y3**2 -
3575*y3 - 30250))/81}, {y1,y2,y3,y4});

% Another nice nonlinear system.

solve({y=x+t^2,x=y+u^2},{x,y,u,t});

% Example from Stan Kameny (relation between Gamma function values)
%   containing surds in the coefficients.

solve({x54=x14/4,x54*x34=sqrt pi/sqrt 2*x32,x32=x12/2,
       x12=sqrt pi, x14*x34=pi*sqrt 2});

% A system given by J. Hietarinta with complex coefficients.

on complex;

apu := {2*a - a6,2*b*c3 - 1,i - 2*x + 1,2*x**2 - 2*x + 1,n1 + 1}$

solve apu;

clear apu;

off complex;

% a trivial system which led to a wrong result:

{a**2*b - a*b**2 + 1, a**2*b + a*b**2 - 1}$

solve ws; 

% also communicated by Jarmo Hietarinta

% More examples that can now be solved.

solve({e^(x+y)-1,x-y},{x,y});

solve({e^(x+y)+sin x,x-y},{x,y}); % no algebraic solution exists.

solve({e^(x+y)-1,x-y**2},{x,y});

solve(e^(y^2) * e^y -1,y);

solve(e^(y^2 +y)-1,y);

solve(e^(y^2)-1,y);

solve(e^(y^2+1)-1,y);

solve({e^(x+y+z)-1,x-y**2=1,x**2-z=2},{x,y,z});

solve(e^(y^4+3y^2+y)-1,y);

% Transcendental equations proposed by Roger Germundsson
% <roger@isy.liu.se>

eq1 := 2*asin(x) + asin(2*x) - PI/2;
eq2 := 2*asin(x) - acos(3*x);
eq3 := acos(x) - atan(x);
eq4 := acos(2*x**2 - 4*x -x) - 2*asin(x);
eq5 := 2*atan(x) - atan( 2*x/(1-x**2) );

sol1 := solve(eq1,x);
sol2 := solve(eq2,x);
sol3 := solve(eq3,x);
sol4 := solve(eq4,x);
sol5 := solve(eq5,x);   % This solution should be the open interval
			% (-1,1).

% Example 52 of M. Wester: the function has no real zero although
%  REDUCE 3.5 and Maple tend to return 3/4.

if solve(sqrt(x^2 +1) - x +2,x) neq {} then rederr "Illegal result";

% Using a root_of expression as an algebraic number.

solve(x^5 - x - 1,x);

w:=rhs first ws;

w^5;

w^5-w;

clear w;

% The following examples come from Daniel Lichtblau of WRI and were
% communicated by Laurent.Bernardin from ETH Zuerich.

solve(x-Pi/2 = cos(x+Pi),x);

solve(exp(x^2+x+2)-1,x);

solve(log(sqrt(1+z)/sqrt(z-1))=x,z);

solve({exp(x+3*y-2)=7,3^(2*x-y+4)=2},{x,y});

solve(a*3^(c*t)+b*3^((c+a)*t),t);

solve(log(x+sqrt(x^2+a))=b,{x});

solve(z=log(w)/log(2)+w^2,w);

solve(w*2^(w^2)=5,w);

solve(log(x/y)=1/y^2*(x+(1/x)),y);

solve(exp(z)=w*z^(-n),z);

solve(-log(3)+log(2+y/3)/2-log(y/3)/2=(-I)/2*Pi,y);

solve(-log(x)-log(y/x)/2+log(2+y/x)/2=(-3*I)/2*Pi,y);

solve((I+1)*log(x)+(3*I+3)*log(x+3)=7,x);

solve(x+sqrt(x)=1,x);

solve({cos(1/5+alpha+x)=5,cos(2/5+alpha-x)=6},{alpha,x});

end;
