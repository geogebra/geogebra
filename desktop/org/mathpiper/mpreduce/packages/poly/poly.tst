% Tests of the poly package polynomial decomposition and gcds.


% Test for the univariate and multivariate polynomial decomposition.

% Herbert Melenk, ZIB Berlin, 1990.

procedure testdecompose u;
   begin scalar r,p,val,nextvar;
       write "decomposition of ",u;
       r := decompose u;
       if length r = 1 then rederr "decomposition failed";
       write " leads to ",r;
	 % test if the result is algebraically correct.
       r := reverse r;
       nextvar := lhs first r; val := rhs first r;
       r := rest r;
       while not(r={}) do
       << p := first r; r := rest r;
          if 'equal = part(p,0) then
          <<val := sub(nextvar=val,rhs p); nextvar := lhs p>>
              else
            val := sub(nextvar=val,p);     
       >>;
       if val = u then write "     O.K.  "
          else
         <<write "**** reconstructed polynomial: ";
           write val; 
           rederr "reconstruction leads to different polynomial";
         >>;
    end;


    % univariate decompositions
testdecompose(x**4+x**2+1); 
testdecompose(x**6+9x**5+52x**4+177x**3+435x**2+630x+593); 
testdecompose(x**6+6x**4+x**3+9x**2+3x-5); 
testdecompose(x**8-88*x**7+2924*x**6-43912*x**5+263431*x**4-218900*x**3+ 
           65690*x**2-7700*x+234);

    % multivariate cases
testdecompose(u**2+v**2+2u*v+1); 
testdecompose(x**4+2x**3*y + 3x**2*y**2 + 2x*y**3 + y**4 + 2x**2*y  
         +2x*y**2 + 2y**3 + 5 x**2 + 5*x*y + 6*y**2 + 5y + 9);
testdecompose  sub(u=(2 x**2 + 17 x+y + y**3),u**2+2 u + 1);
testdecompose  sub(u=(2 x**2 *y + 17 x+y + y**3),u**2+2 u + 1);

    % some cases which require a special (internal) mapping
testdecompose  ( (x + y)**2);
testdecompose ((x + y**2)**2);
testdecompose  ( (x**2 + y)**2);
testdecompose  ( (u + v)**2 +10 );

    % the decomposition is not unique and might generate quite
    % different images:
testdecompose  ( (u + v + 10)**2 -100 );

    % some special (difficult) cases
testdecompose (X**4 + 88*X**3*Y + 2904*X**2*Y**2 - 10*X**2 
           + 42592*X*Y**3 - 440*X*Y + 234256*Y**4 - 4840*Y**2);

    % a polynomial with complex coefficients
on complex;
testdecompose(X**4 + (88*I)*X**3*Y - 2904*X**2*Y**2 - 10*X**2 - 
              (42592*I)*X*Y**3 - (440*I)*X*Y + 234256*Y**4 + 4840*Y**2);
off complex;


% Examples given by J. Gutierrez and J.M. Olazabal.

f1:=x**6-2x**5+x**4-3x**3+3x**2+5$
testdecompose(f1);

f2:=x**32-1$
testdecompose(f2);

f3:=x**4-(2/3)*x**3-(26/9)*x**2+x+3$
testdecompose(f3);

f4:=sub(x=x**4-x**3-2x+1,x**3-x**2-1)$
testdecompose(f4);

f5:=sub(x=f4,x**5-5)$
testdecompose(f5);

clear f1,f2,f3,f4,f5;


% Tests of gcd code.

% The following examples were introduced in Moses, J. and Yun, D.Y.Y.,
% "The EZ GCD Algorithm", Proc. ACM 73 (1973) 159-166, and considered
% further in Hearn, A.C., "Non-modular Computation of Polynomial GCD's
% Using Trial Division", Proc. EUROSAM 79, 227-239, 72, published as
% Lecture Notes on Comp. Science, # 72, Springer-Verlag, Berlin, 1979.

on gcd;

% The following is the best setting for this file.

on ezgcd;

% In systems that have the heugcd code, the following is also a
% possibility, although not all examples complete in a reasonable time.

% load heugcd; on heugcd;

% The final alternative is to use neither ezgcd nor heugcd. In that case,
% most examples take excessive amounts of computer time.

share n;

operator xx;

% Case 1.

for n := 2:5
   do write gcd(((for i:=1:n sum xx(i))-1)*((for i:=1:n sum xx(i)) + 2),
		((for i:=1:n sum xx(i))+1)
		     *(-3xx(2)*xx(1)**2+xx(2)**2-1)**2);

% Case 2.

let d = (for i:=1:n sum xx(i)**n) + 1;

for n := 2:7 do write gcd(d*((for i:=1:n sum xx(i)**n) - 2),
			  d*((for i:=1:n sum xx(i)**n) + 2));


for n := 2:7 do write gcd(d*((for i:=1:n sum xx(i)**n) - 2),
			  d*((for i:=1:n sum xx(i)**(n-1)) + 2));

% Case 3.

let d = xx(2)**2*xx(1)**2 + (for i := 3:n sum xx(i)**2) + 1;

for n := 2:5
   do write gcd(d*(xx(2)*xx(1) + (for i:=3:n sum xx(i)) + 2)**2,
		d*(xx(1)**2-xx(2)**2 + (for i:=3:n sum xx(i)**2) - 1));

% Case 4.

let u = xx(1) - xx(2)*xx(3) + 1,
    v = xx(1) - xx(2) + 3xx(3);

gcd(u*v**2,v*u**2);

gcd(u*v**3,v*u**3);

gcd(u*v**4,v*u**4);

gcd(u**2*v**4,v**2*u**4);


% Case 5.

let d = (for i := 1:n product (xx(i)+1)) - 3;

for n := 2:5 do write gcd(d*for i := 1:n product (xx(i) - 2),
			  d*for i := 1:n product (xx(i) + 2));

clear d,u,v;


% The following examples were discussed in Char, B.W., Geddes, K.O.,
% Gonnet, G.H., "GCDHEU:  Heuristic Polynomial GCD Algorithm Based
% on Integer GCD Computation", Proc. EUROSAM 84, 285-296, published as
% Lecture Notes on Comp. Science, # 174, Springer-Verlag, Berlin, 1984.


% Maple Problem 1.

gcd(34*x**80-91*x**99+70*x**31-25*x**52+20*x**76-86*x**44-17*x**33
    -6*x**89-56*x**54-17,
    91*x**49+64*x**10-21*x**52-88*x**74-38*x**76-46*x**84-16*x**95
    -81*x**72+96*x**25-20);
    
% Maple Problem 2.

g := 34*x**19-91*x+70*x**7-25*x**16+20*x**3-86;

gcd(g * (64*x**34-21*x**47-126*x**8-46*x**5-16*x**60-81),
    g * (72*x**60-25*x**25-19*x**23-22*x**39-83*x**52+54*x**10+81) );

% Maple Problem 3.

gcd(3427088418+8032938293*x-9181159474*x**2-9955210536*x**3
    +7049846077*x**4-3120124818*x**5-2517523455*x**6+5255435973*x**7
    +2020369281*x**8-7604863368*x**9-8685841867*x**10+4432745169*x**11
    -1746773680*x**12-3351440965*x**13-580100705*x**14+8923168914*x**15
    -5660404998*x**16 +5441358149*x**17-1741572352*x**18
    +9148191435*x**19-4940173788*x**20+6420433154*x**21+980100567*x**22
    -2128455689*x**23+5266911072*x**24-8800333073*x**25-7425750422*x**26
    -3801290114*x**27-7680051202*x**28-4652194273*x**29-8472655390*x**30
    -1656540766*x**31+9577718075*x**32-8137446394*x**33+7232922578*x**34
    +9601468396*x**35-2497427781*x**36-2047603127*x**37-1893414455*x**38
    -2508354375*x**39-2231932228*x**40,
    2503247071-8324774912*x+6797341645*x**2+5418887080*x**3
    -6779305784*x**4+8113537696*x**5+2229288956*x**6+2732713505*x**7
    +9659962054*x**8-1514449131*x**9+7981583323*x**10+3729868918*x**11
    -2849544385*x**12-5246360984*x**13+2570821160*x**14-5533328063*x**15
    -274185102*x**16+8312755945*x**17-2941669352*x**18-4320254985*x**19
    +9331460166*x**20-2906491973*x**21-7780292310*x**22-4971715970*x**23
    -6474871482*x**24-6832431522*x**25-5016229128*x**26-6422216875*x**27
    -471583252*x**28+3073673916*x**29+2297139923*x**30+9034797416*x**31
    +6247010865*x**32+5965858387*x**33-4612062748*x**34+5837579849*x**35
    -2820832810*x**36-7450648226*x**37+2849150856*x**38+2109912954*x**39
    +2914906138*x**40);

% Maple Problem 4.

g := 34271+80330*x-91812*x**2-99553*x**3+70499*x**4-31201*x**5
     -25175*x**6+52555*x**7+20204*x**8-76049*x**9-86859*x**10;

gcd(g * (44328-17468*x-33515*x**2-5801*x**3+89232*x**4-56604*x**5
	 +54414*x**6-17416*x**7+91482*x**8-49402*x**9+64205*x**10
	 +9801*x**11-21285*x**12+52669*x**13-88004*x**14-74258*x**15
	 -38013*x**16-76801*x**17-46522*x**18-84727*x**19-16565*x**20
	 +95778*x**21-81375*x**22+72330*x**23+96015*x**24-24974*x**25
	 -20476*x**26-18934*x**27-25084*x**28-22319*x**29+25033*x**30),
    g * (-83248+67974*x+54189*x**2-67793*x**3+81136*x**4+22293*x**5
	 +27327*x**6+96600*x**7-15145*x**8+79816*x**9+37299*x**10
	 -28496*x**11-52464*x**12+25708*x**13-55334*x**14-2742*x**15
	 +83128*x**16-29417*x**17-43203*x**18+93315*x**19-29065*x**20
	 -77803*x**21-49717*x**22-64749*x**23-68325*x**24-50163*x**25
	 -64222*x**26-4716*x**27+30737*x**28+22972*x**29+90348*x**30));

% Maple Problem 5.

gcd(-8472*x**4*y**10-8137*x**9*y**10-2497*x**4*y**4-2508*x**4*y**6
    -8324*x**9*y**8-6779*x**9*y**6+2733*x**10*y**4+7981*x**7*y**3
    -5246*x**6*y**2-274*x**10*y**3-4320,
    15168*x**3*y-4971*x*y-2283*x*y**5+3074*x**6*y**10+6247*x**8*y**2
    +2849*x**6*y**7-2039*x**7-2626*x**2*y**7+9229*x**6*y**5+2404*y**5
    +1387*x**4*y**8+5602*x**5*y**2-6212*x**3*y**7-8561);

% Maple Problem 6.

g := -19*x**4*y**4+25*y**9+54*x*y**9+22*x**7*y**10-15*x**9*y**7-28;

gcd(g*(91*x**2*y**9+10*x**4*y**8-88*x*y**3-76*x**2-16*x**10*y
       +72*x**10*y**4-20),
    g*(34*x**9-99*x**9*y**3-25*x**8*y**6-76*y**7-17*x**3*y**5
       +89*x**2*y**8-17));

% Maple Problem 7.

gcd(6713544209*x**9+8524923038*x**3*y**3*z**7+6010184640*x*z**7
    +4126613160*x**3*y**4*z**9+2169797500*x**7*y**4*z**9
    +2529913106*x**8*y**5*z**3+7633455535*y*z**3+1159974399*x**2*z**4
    +9788859037*y**8*z**9+3751286109*x**3*y**4*z**3,
    3884033886*x**6*z**8+7709443539*x*y**9*z**6
    +6366356752*x**9*y**4*z**8+6864934459*x**3*y**2*z**6
    +2233335968*x**4*y**9*z**3+2839872507*x**9*y**3*z
    +2514142015*x*y*z**2+1788891562*x**4*y**6*z**6
    +9517398707*x**8*y**7*z**2+7918789924*x**3*y*z**6
    +6054956477*x**6*y**3*z**6);

% Maple Problem 8.

g := u**3*(x**2-y)*z**2+(u-3*u**2*x)*y*z-u**4*x*y+3;
gcd(g * ((y**2+x)*z**2+u**5*(x*y+x**2)*z-y+5),
    g * ((y**2-x)*z**2+u**5*(x*y-x**2)*z+y+9) );

% Maple Problem 9.

g := 34*u**2*y**2*z-25*u**2*v*z**2-18*v*x**2*z**2-18*u**2*x**2*y*z+53
     +x**3;
gcd( g * (-85*u*v**2*y**2*z**2-25*u*v*x*y*z-84*u**2*v**2*y**2*z
      +27*u**2*v*x**2*y**2*z-53*u*x*y**2*z+34*x**3),
     g * (48*x**3-99*u*x**2*y**2*z-69*x*y*z-75*u*v*x*y*z**2
     -43*u**2*v+91*u**2*v**2*y**2*z) );

% Maple Problem 10.

gcd(-9955*v**9*x**3*y**4*z**8+2020*v*y**7*z**4
    -3351*v**5*x**10*y**2*z**8-1741*v**10*x**2*y**9*z**6
    -2128*v**8*y*z**3-7680*v**2*y**4*z**10-8137*v**9*x**10*y**4*z**4
    -1893*v**4*x**4*y**6+6797*v**8*x*y**9*z**6
    +2733*v**10*x**4*y**9*z**7-2849*v**2*x**6*y**2*z**5
    +8312*v**3*x**3*y**10*z**3-7780*v**2*x*y*z**2
    -6422*v**5*x**7*y**6*z**10+6247*v**8*x**2*y**8*z**3
    -7450*v**7*x**6*y**7*z**4+3625*x**4*y**2*z**7+9229*v**6*x**5*y**6
    -112*v**6*x**4*y**8*z**7-7867*v**5*x**8*y**5*z**2
    -6212*v**3*x**7*z**5+8699*v**8*x**2*y**2*z**5
    +4442*v**10*x**5*y**4*z+1965*v**10*y**3*z**3-8906*v**6*x*y**4*z**5
    +5552*x**10*y**4+3055*v**5*x**3*y**6*z**2+6658*v**7*x**10*z**6
    +3721*v**8*x**9*y**4*z**8+9511*v*x**6*y+5437*v**3*x**9*y**9*z**7
    -1957*v**6*x**4*y*z**3+9214*v**3*x**9*y**3*z**7
    +7273*v**2*x**8*y**4*z**10+1701*x**10*y**7*z**2
    +4944*v**5*x**5*y**8*z**8-1935*v**3*x**6*y**10*z**7
    +4029*x**6*y**10*z**3+9462*v**6*x**5*y**4*z**8-3633*v**4*x*y**7*z**5
    -1876,
    -5830*v**7*x**8*y*z**2-1217*v**8*x*y**2*z**5
    -1510*v**9*x**3*y**10*z**10+7036*v**6*x**8*y**3*z**3
    +1022*v**9*y**3*z**8+3791*v**8*x**3*y**7+6906*v**6*x*y*z**10
    +117*v**7*x**2*y**4*z**4+6654*v**6*x**5*y**2*z**3
    -7302*v**10*x**8*y**3-5343*v**8*x**5*y**9*z
    -2244*v**9*x**3*y**8*z**9-3719*v**5*x**10*y**6*z**8
    +2629*x**3*y**2*z**10+8517*x**9*y**6*z**7-9551*v**5*x**6*y**6*z**2
    -7750*x**10*y**7*z**4-5035*v**5*x**2*y**5*z-5967*v**9*x**5*y**9*z**5
    -8517*v**3*x**2*y**7*z**6-2668*v**10*y**9*z**4+1630*v**5*x**5*y*z**8
    +9099*v**7*x**9*y**4*z**3-5358*v**9*x**5*y**6*z**2
    +5766*v**5*y**3*z**4-3624*v*x**4*y**10*z**10
    +8839*v**6*x**9*y**10*z**4+3378*x**7*y**2*z**5+7582*v**7*x*y**8*z**7
    -85*v*x**2*y**9*z**6-9495*v**9*x**10*y**6*z**3+1983*v**9*x**3*y
    -4613*v**10*x**4*y**7*z**6+5529*v**10*x*y**6
    +5030*v**4*x**5*y**4*z**9-9202*x**6*y**3*z**9
    -4988*v**2*x**2*y**10*z**4-8572*v**9*x**7*y**10*z**10
    +4080*v**4*x**8*z**8-382*v**9*x**9*y**2*z**2-7326);

end;

