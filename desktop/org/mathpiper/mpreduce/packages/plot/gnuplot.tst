on demo;

plot(sin x,x=(-3 .. 3));
plot(s=sin phi,phi=(-3 .. 3));
plot(sin phi,cos phi,phi=(-3 .. 3));
plot(sin(1/x),x=(-1 .. 1),y=(-3 .. 3));
plot(sin(1/x),x=(-10 .. 10));
plot(y=tan x,y=(-10 .. 10));


plot (cos sqrt(x**2 + y**2),x=(-3 .. 3),y=(-3 .. 3));
plot (cos sqrt(x**2 + y**2),x=(-3 .. 3),y=(-3 .. 3),hidden3d);
plot(x*y, x=(0 .. 2), y=(0 .. 2));
plot(x*y, x=(-2 .. 2), y=(-2 .. 2));
plot(x+y, x=(0 .. 2), y=(0 .. 2));
plot(1/(x**2+y**2),x=(-0.5 .. 0.5),y=(-0.5 .. 0.5));
plot(1/(x**2+y**2),x=(-0.5 .. 0.5),y=(-0.5 .. 0.5),hidden3d);
plot(1/(x**2+y**2),x=(0.1 .. 5),y=(0.1 .. 5),size="0.7,1");
plot(1/(x**2+y**2),x=(0.1 .. 5),y=(0.1 .. 5),view="30,89");
plot(1/(x**2+y**2),x=(-0.5 .. 0.5),y=(-0.5 .. 0.5),
      hidden3d,contour,view="70,20");

% this may be slow on some machines because of 
% a delicate evaluation context.
plot(sinh(x*y)/sinh(2*x*y),hidden3d);

%parametric curves and surfaces

plot(point(cos(u),sin(u),0.1*u),u=(0 .. 4*pi),points=100);
plot(point(sin(u)*cos(v),sin(u)*sin(v),cos(u)),u=(0 .. pi),v=(0 .. 2*pi)
      ,points=60);

% implicit curves and surfaces

plot(x^3+y^3 -3*x*y ={0,1,2,3},x=(-2.5 .. 2),y=(-5 .. 5));
plot(x^2+y^2+z^2-1=0,x=(-1 .. 1),y=(-1 .. 1),points=40);

% equations and parts

wss :=
{{u=(665280*t**6 + 1995840*t**5*x**2 - 3991680*t**5
+ 831600*t**4*x**4 - 9979200
*t**4*x**2 + 19958400*t**4 + 110880*t**3*x**6 - 3326400*t**3*x**4 + 
39916800*t**3*x**2 - 79833600*t**3 + 5940*t**2*x**8 - 332640*t**2*x**6 + 
9979200*t**2*x**4 - 119750400*t**2*x**2 + 239500800*t**2 + 132*t*x**10 - 
11880*t*x**8 + 665280*t*x**6 - 19958400*t*x**4 + 239500800*t*x**2 - 
479001600*t + x**12 - 132*x**10 + 11880*x**8 - 665280*x**6 + 19958400*x**4 
- 239500800*x**2 + 479001600)/479001600}}$

plot(rhs first first wss,x=(-5 .. 5),t=(-1 .. 1),hidden3d);

% general curves and surfaces computed as lists of data points

plot {{0,0},{0,1},{1,1},{0,0},{1,0},{0,1},{0.5,1.5},{1,1},{1,0}};
on rounded;
w:=for j:=1:200 collect {1/j*sin j,1/j*cos j,j/200}$
plot w;

% the following examples need some computing time

w:= {for j:=1 step 0.1 until 20 collect
         {1/j*sin j,1/j*cos j,j},
     for j:=1 step 0.1 until 20 collect
         {(0.1+1/j)*sin j,(0.1+1/j)*cos j,j}
     }$
plot w;

dd:=pi/15;

w:=for u:=dd step dd until pi-dd collect
    for v:=0 step dd until 2pi collect
      {sin(u)*cos(v), sin(u)*sin(v), cos(u)}$

plot w;

symbolic procedure ikeda(tt);

% from Willi-Hans Steeb: The NONLINEAR WORKBOOK, chap. 1.2
%      World Scientific, 1999

begin scalar taut,X,Y,x1,y1,c1,c2,c3,rho;

x := 0.5; y := 0.5;
c1 := 0.4; c2 := 0.9; c3 := 9.0; rho := 0.85;

return
'list .
for ttt :=0:tt collect
<< x1 := x; y1 := y; taut := c1 -c3/(1 + x1^2 + y1^2);
   x := rho + c2*x1*cos taut - y1*sin(taut);
   y := c2*(x1*sin(taut) + y1*cos(taut));
   list('list, floor (90*x +200 + 0.5), floor (90*y +200 + 0.5)) >>;
end;

ikeda := lisp ikeda(20000)$

plot(ikeda,style=points);
plot(ikeda,style=dots); 
plot(ikeda,style=errorbars);


plotreset;

end;

