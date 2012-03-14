% Tests of the COMPACT package.

% Author: Anthony C. Hearn.

% First some simple examples.

aa := {cos(x)^2+sin(x)^2-1};

xx := 2*cos(x)^2+2*sin(x)^2-2;

compact(xx,aa);

xx := (1-cos(x)^2)^4;

compact(xx,aa);

% These examples are from Lars Hornfeldt.

% This should be cos x^10*sin x^10.

compact(((1-(sin x)**2)**5)*((1-(cos x)**2)**5)
	    *(((sin x)**2+(cos x)**2)**5),
	{cos x^2+sin x^2=1});

% This example illustrates the problem in the above. It is cos(x)^6.

compact(-3cos(x)^2*sin(x)^2-sin(x)^6+1,{cos x^2+sin x^2-1});

compact(s*(1-(sin x**2))+c*(1-(cos x)**2)+(sin x)**2+(cos x)**2,
	{cos x^2+sin x^2=1});

xx := s*(1-(sin x**2))+c*(1-(cos x)**2)+(sin x)**2+(cos x)**2
	*((sin x)**2+(cos x)**2)*(sin x)**499*(cos x)**499;

compact(xx,{cos(x)^2+sin(x)^2=1});

compact((s*(1-(sin x**2))+c*(1-(cos x)**2)+(sin x)**2+(cos x)**2)
	     *((sin x)**2+(cos x)**2)*(sin x)**499*(cos x)**499,
	 {cos x^2+sin x^2=1});

compact(df((1-(sin x)**2)**4,x),{cos x^2+sin x^2=1});

% End of Lars Hornfeld examples.

xx := a*(cos(x)+2*sin(x))^3-w*(cos(x)-sin(x))^2;

compact(xx,aa);

xx := (1-cos(x)^2)^2+(1-sin(x)^2)^2;

compact(xx,aa);

xx := (c^2-1)^6+7(s-1)^4+23(c+s)^5;

compact(xx,{c+s=1});

yy := (c+1)^6*s^6+7c^4+23;

compact(yy,{c+s=1});

zz := xx^3+c^6*s^6$

compact(zz,{c+s=1});

xx := (c+s)^5 - 55(1-s)^2 + 77(1-c)^3 + (c+2s)^8;

% This should reduce to something like:

yy := 1 - 55c^2 + 77s^3 + (1+s)^8;

% The result contains the same number but different terms.

compact(xx,{c+s=1});

compact(yy,{c+s=1});

% Test showing order of expressions is important.

d2:= - 4*r3a**2 - 4*r3b**2 - 4*r3c**2 + 3*r3**2$

d1:=  4 * r3a**2 * r3
   +  4 * r3b**2 * r3
   +  4 * r3c**2 * r3
   + 16 * r3a * r3b * r3c
   -      r3**3$

d0:= 16 * r3a**4
   + 16 * r3b**4
   + 16 * r3c**4
   +       r3**4
   - 32 * r3a**2 * r3b**2
   - 32 * r3a**2 * r3c**2
   - 32 * r3b**2 * r3c**2
   -  8 * r3a**2 *  r3**2
   -  8 * r3b**2 *  r3**2
   -  8 * r3c**2 *  r3**2
   - 64 * r3a * r3b * r3c * r3$

alist := { c0 = d0, c1 = d1, c2 = d2}$

blist := { c2 = d2, c1 = d1, c0 = d0}$

d:= d2 * l*l + d1 * l + d0;

compact(d,alist); % Works fine.

compact(d,blist); % Only c2=d2 is applied.

% This example illustrates why parallel application of the individual
% side relations is necessary.

lst:={x1=a+b+c, x2=a-b-c, x3=-a+b-c, x4=-a-b+c};

z1:=(a+b+c)*(a-b-c)*(-a+b-c);    % This is x1*x2*x3.

z2:=(a+b+c)*(a-b-c)*(-a+b-c)*(-a-b+c);   % This is x1*x2*x3*x4.

compact(z1,lst); % Not the best solution but better than nothing.

compact(z2,lst); % Does nothing.

end;
