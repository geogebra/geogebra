%Demonstration file for some Riemann surfaces

% Caroline Cotter, ZIB,Berlin, 1998
%(with reference to paper:"Graphing Elementary Riemann Surfaces",
% by Robert M. Corless & David J. Jeffrey, December 1997)


load_package gnuplot;
on complex;
on demo;



%The Riemann surface for w=arcsin(z)

begin scalar w,x,y,z;
  w:=u+i*v;
  z:=sin(w);
  x:=repart(z);  y:=impart(z);
  plot(point(x,y,u),u=(-pi ..pi),v=(-4 ..4),title=
	"The Arcsin Function",zlabel="u",view="75,50",points=30,hidden3d)
 end;



%The Riemann surface for w=arccos(z)

begin scalar w,x,y,z;
  w:=u+i*v;
  z:=cos(w);
  x:=repart(z);  y:=impart(z);
  plot(point(x,y,u),u=(-pi ..pi),v=(-4 ..4),title=
	"The Arccos Function",zlabel="u",view="75,50",points=30,hidden3d)
 end;



%The Riemann surface for w=arctan(z)

begin scalar w,x,y,z;
  w:=u+i*v;
  z:=tan(w);
  x:=repart(z);  y:=impart(z);
  plot(point(x,y,u),u=(-pi ..pi),v=(-2 ..2),title=
	"The Arctan Function",zlabel="u",view="80,30",points=40)
 end;



%The Riemann surface for w=z^(1/2)

%(a) With Cartesian coordinates parameterization

begin scalar w,x,y,z;
  w:=u+i*v;
  z:=w^2;
  x:=repart(z);  y:=impart(z);
  plot(point(x,y,v),u=(-2 ..2),v=(-2 ..2), title=
     "The Squareroot Function (a)",zlabel="v",view="60,60",points=30,hidden3d)
 end;

%(b) With polar coordinates parameterization

begin scalar w,x,y;
  w:=r*cos(theta) + i*r*sin(theta);
  x:=r^2*cos(2*theta);
  y:=r^2*sin(2*theta);
  plot(point(x,y,impart(w)),r=(0 .. 1.5),theta=(-2*pi ..2*pi),
   title="The Squareroot Function (b)",view="70,50",points=50,hidden3d)
 end;



%The Riemann surface for w=z^(1/3)

%(a) With Cartesian coordinates parameterization

begin scalar w,x,y,z;
  w:=u+i*v;
  z:=w^3;
  x:=repart(z);  y:=impart(z);
  plot(point(x,y,v),u=(-2 ..2),v=(-2 ..2),
   title="The Cuberoot Function (a)",zlabel="v",view="50,60",hidden3d)
 end;

%(b) With polar coordinates parameterization

begin scalar w,x,y;
  w:=r*cos(theta) + i*r*sin(theta);
  x:=r^3*cos(3*theta);
  y:=r^3*sin(3*theta);
  plot(point(x,y,impart(w)),r=(0 .. 1.5),theta=(-2*pi ..2*pi),
	title="The Cuberoot Function (b)",view="70,100",points=50,hidden3d)
 end;



%The Riemann surface for w=z^(2/3)

begin scalar w,x,y;
  w:=r*cos(theta) + i*r*sin(theta);
  x:=r^(3/2)*cos((3/2)*theta);
  y:=r^(3/2)*sin((3/2)*theta);
  plot(point(x,y,impart(w)),r=(0 .. 1.5),theta=(-2*pi ..2*pi),
	title="The Cuberoot-squared Function",view="65,280",points=40,hidden3d)
 end;
