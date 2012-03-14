% Test file for plots of special functions.

% Kerry Gaskell, Konrad-Zuse-Zentrum Berlin, 1993.

% This file runs with    ON DEMO;

load_package gnuplot;

load_package specfn;

on demo;

%Mathematica - page 112

plot(sin(x^2),title="sin(x^2)");

plot(sin(1/x),x=(-1 .. 1),title="sin(1/x)");


%Mathematica - page 112

plot(sin(x^2),x=(0 ..3),title="sin(x^2)");


plot(cos(1/x),x=(-1 .. 1),title="cos(1/x)");


plot(cos(x^2),title="cos(x^2)");


plot(cos(x^2),x=(-3 .. 3),title="cos(x^2)");



%Handbook of Mathematical Functions - page 778

plot(chebyshevt(1,x),chebyshevt(2,x),chebyshevt(3,x),chebyshevt(4,x),
chebyshevt(5,x),x=(-1 .. 1),title="Chebyshev t Polynomials");


%Handbook of Mathematical Functions - page 779

plot(chebyshevu(1,x),chebyshevu(2,x),chebyshevu(3,x),chebyshevu(4,x),
chebyshevu(5,x),x=(-1 .. 1),y=(-6 .. 6),
title="Chebyshev u Polynomials");

%Handbook of Mathematical Functions - page 776

plot(gegenbauerp(2,1/2,x),gegenbauerp(3,1/2,x),gegenbauerp(4,1/2,x),
gegenbauerp(5,1/2,x),x=(-1 .. 1),y=(-1 .. 1),
title="Gegenbauer Polynomials");


%Handbook of Mathematical Functions - page 777


plot(gegenbauerp(5,0.2,x),gegenbauerp(5,0.4,x),gegenbauerp(5,0.6,x),
gegenbauerp(5,0.8,x),gegenbauerp(5,1,x),x=(-1 .. 1),y=(-1.5 .. 1.5),
title="Gegenbauer Polynomials");


%Handbook of Mathematical Functions - page 780


plot(laguerrep(2,x),laguerrep(3,x),laguerrep(4,x),laguerrep(5,x),
x=(0 .. 6),y=(-2 .. 4),title="Laguerre Polynomials");


%Handbook of Mathematical Functions - page 780

plot(legendrep(2,x),legendrep(3,x),legendrep(4,x),legendrep(5,x),
x=(-1 .. 1),y=(-1 .. 1),title="Legendre Polynomials");

%Handbook of Mathematical Functions - page 496

plot(struveh(0,x),struveh(1,x),struveh(2,x),struveh(3,x),
x=(0 .. 12),y=(-0.5 .. 2),title="Struve Functions");

%Handbook of Mathematical Functions - page 496

plot(struveh(-1,x),struveh(-2,x),struveh(-3,x),
x=(0 .. 16),y=(-1 .. 1),title="Struve Functions");

%Handbook of Mathematical Functions - page 776

plot(jacobip(5,1.5,0,x),jacobip(5,1.5,-0.2,x),jacobip(5,1.5,-0.4,x),
jacobip(5,1.5,-0.6,x),jacobip(5,1.5,-0.8,x),x=(-1 .. 1),
y=(-1.5 .. 1.5),title="Jacobi Polynomials");
 

%Handbook of Mathematical Functions - page 776

plot(jacobip(5,1.0,-0.5,x),jacobip(5,1.2,-0.5,x),jacobip(5,1.4,-0.5,x),
jacobip(5,1.6,-0.5,x),jacobip(5,1.8,-0.5,x),jacobip(5,2.0,-0.5,x),
x=(-1 .. 1),y=(-1.5 .. 1.5),title="Jacobi Polynomials");
 

%Handbook of Mathematical Functions - page 773

plot(jacobip(1,1.5,-0.5,x),jacobip(2,1.5,-0.5,x),jacobip(3,1.5,-0.5,x),
jacobip(4,1.5,-0.5,x),jacobip(5,1.5,-0.5,x),x=(-1 .. 1),
y=(-1.5 .. 3),title="Jacobi Polynomials");
 
%Handbook of Mathematical Functions - page 780

<<
algebraic procedure hermite_base(x,n);
     hermite_base1(x,n,{2*x,1},1);

plot(hermitep(2,x)/8,hermitep(3,x)/27,hermitep(4,x)/64,
 hermitep(5,x)/125,x=(0 .. 4),y=(-2 .. 8),
 title="Hermite Polynomials/n^3");
>>;


%Mathematica - page 115

plot(besselj(0,x),besselj(1,x),besselj(2,x),besselj(4,x),x=(0 .. 10),
y=(-2 .. 2),title="Bessel functions of 1st kind");


%Handbook of Mathematical Functions - page 359

plot(bessely(0,x),bessely(1,x),bessely(2,x),x=(0.1 .. 10),y=(-1 .. 1),
title="Bessel functions of 2nd kind");


%bessely(2,x),bessely(4,x)


plot(besseli(0,x),besseli(1,x),besseli(2,x),x=(-4 .. 4),y=(-10 .. 15),
title="Bessel functions of 3rd kind");


%Evolutionstheorie und dynamische Systeme - page 93

%A function to calculate a given number of consecutive points on a
%curve when given a small value h and then to plot the curve.

on comp;
<<
symbolic procedure vera(h,x,y,n);

begin scalar result_i, old, lst;
  lst := {{'list,x,y}};
  lst := append(lst,{result_i := bambam(0,x,y)});
  lst := append(lst,
	 for i:= 2 :n collect
<<result_i:= rest result_i;
  old := result_i;
  result_i :=
	bambam(h,first(result_i),second(result_i));
    result_i
>>);
return ('list . lst);
end;

symbolic operator vera;

symbolic procedure bambam(h,x,y);

begin scalar result_x, result_y, temp, new_x, new_y;
	temp := frank(h,x,y);
	new_x := car temp;
	new_y := cadr temp;
        result_x := x+h*new_x;
        result_y := y+h*new_y;
return {'list,result_x,result_y};

end;

symbolic procedure frank(h,x,y),

begin scalar result, temp, temp2, new_x, new_y;

	temp := bob(x,y);
	new_x :=x + h*car temp;
	new_y :=y + h*cadr temp;
	temp2 :=bob(new_x,new_y);
	result := {(car temp+car temp2)*0.5,
		   (cadr temp+cadr temp2)*0.5};

return result;

end;

% A function to calculate the values of dx/dt and dy/dt from given
%equations.

symbolic procedure bob(x,y);

begin scalar result_x, result_y;

   if numberp x and numberp y
        then <<result_x := x*((1+x)*(3-x)-y);
               result_y := y*(x-2)>>
   else << printlist ("Try again with numbers next time!") >>;

return {result_x,result_y};

end;


plot({symbolic vera(0.02,6,1,30),symbolic vera(0.03,6,3,80),
symbolic vera(0.03,6,5,75), symbolic vera(0.03,0.5,7,160)},
title="dx/dt=x((1+x)(3-x)-y), dy/dt=y(x-2)");

>>;

<<
%Evolutionstheorie und dynamische Systeme - page 92

%A function to calculate a given number of consecutive points on a
%curve when given a small value h and then to plot the curve.

symbolic procedure joan(h,x,y,n);

begin scalar result_i, old, lst;
  lst := {{'list,x,y}};

  lst := append(lst,{result_i := pebbles(0,x,y)});
  
  lst := append(lst,
	 for i:= 2 :n collect

<<result_i:= rest result_i;
  old := result_i;
  result_i := pebbles(h,first(result_i),second(result_i));
  result_i

>>);

return ('list . lst);

end;

symbolic operator joan;


symbolic procedure pebbles(h,x,y);

begin scalar result_x, result_y, temp, new_x, new_y;

	temp := wilma(x,y);
	new_x := car temp;
	new_y := cadr temp;
        result_x := x+h*new_x;
        result_y := y+h*new_y;;
return {'list,result_x,result_y};

end;


% A function to calculate the values of dx/dt and dy/dt from given
%equations.

symbolic procedure wilma(x,y);

begin scalar result_x, result_y;

   if numberp x and numberp y
        then <<result_x := x*((1+x)*(3-x)-y);

               result_y := y*(x-4)>>

   else << printlist ("Try again with numbers next time!") >>;

return {result_x,result_y};

end;


plot({symbolic joan(0.02,6,1,75),symbolic joan(0.02,6,3,65),
symbolic joan(0.02,6,5,65),symbolic joan(0.02,4,7,65),
symbolic joan(0.02,1.5,7,55),symbolic joan(0.02,0.5,7,100)},
title="dx/dt=x((1+x)(3-x)-y), dy/dt=y(x-4)");
>>;

<<
%Evolutionstheorie und dynamische Systeme - page 83

%A function to calculate a given number of consecutive points on a
%curve when given a small value h and then to plot the curve.

symbolic procedure betty(h,x,y,n);

begin scalar result_i, old, lst;
  lst := {{'list,x,y}};
  lst := append(lst,{result_i := barney(0,x,y)});
  lst := append(lst,
	 for i:= 2 :n collect
<<result_i:= rest result_i;
  old := result_i;
  result_i :=
	barney(h,first(result_i),second(result_i));
    result_i
>>);

return ('list . lst);

end;

symbolic operator betty;

symbolic procedure barney(h,x,y);

begin scalar result_x, result_y, temp, new_x, new_y;
	temp := fred(x,y);
	new_x := car temp;
        new_y := cadr temp;
        result_x := x+h*new_x;
        result_y := y+h*new_y;
return {'list,result_x,result_y};

end;

% A function to calculate the values of dx/dt and dy/dt from given
%equations.

symbolic procedure fred(x,y);

begin scalar result_x, result_y;

   if numberp x and numberp y
        then <<result_x := x-y-x*(x^2+y^2);
               result_y := x+y-y*(x^2+y^2)>>
   else << printlist ("Try again with numbers next time!") >>;

return {result_x,result_y};

end;

plot({symbolic betty(0.05,-2,0,25),symbolic betty(0.05,1,-2,25),
symbolic betty(0.05,1.25,1.75,25),symbolic betty(0.1,0.01,0.01,65),
symbolic betty(0.1,-0.01,0.005,65),symbolic betty(0.1,0.005,-0.01,65),
symbolic betty(0.1,0,1,65)},
title="dx/dt=x-y-x(x^2+y^2), dy/dt=x+y-y(x^2+y^2)");

>>;

plot(repart(lambert_w(x)));

plot(Jacobisn(u,1/2),Jacobicn(u,1/2),Jacobidn(u,1/2), u=(0 .. 4*EllipticK
(1/2)), y = (-1 .. 1),title="sn , cn and dn for m=1/2");

plot(Jacobins(u,1/2),Jacobinc(u,1/2),Jacobind(u,1/2), u=(0 .. 4*EllipticK
(1/2)), y = (-2 .. 2.5),title="ns , nc and nd for m=1/2");

plot(Jacobics(u,1/2),Jacobisc(u,1/2),Jacobicd(u,1/2), Jacobidc(u,1/2), u=
(0 .. 4*EllipticK(1/2)),y = (-1.5 .. 1.5)
,title="cs, sc, cd and dc for m=1/2");

end;


