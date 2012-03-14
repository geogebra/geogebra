%*******************************************************************%
%                                                                   %
%                      C O N L A W . T S T                          %
%                      -------------------                          %
%  conlaw.tst  contains test examples for the programs conlaw0.red  %
%  conlaw1.red, conlaw2.red, conlaw3.red, conlaw4.red. To run       %
%  this test read in the files crack.red, conlaw0.red, conlaw1.red, %
%  conlaw2.red, conlaw3.red, conlaw4.red or load their compiled     %
%  version before.                                                  %
%                                                                   %
%  Author: Thomas Wolf                                              %
%  Date:   15. June 1999, 6. May 2003                               %
%                                                                   %
%  Details about the syntax of conlaw1-4 are given in conlaw.tex.   %
%  To run this file read in or load crack, conlaw0 before.          %
%                                                                   %
%  The statement lisp(print_:=nil); suppresses output of the        %
%  computation. To see details of it do lisp(print_:=50).           %
%                                                                   %
%*******************************************************************%

load crack ; % ,conlaw0,conlaw1,conlaw2,conlaw3,conlaw4$

lisp(depl!*:=nil)$     % clearing of all dependencies
%setcrackflags()$       % standart flags
lisp(print_:=nil)$     % no output of the calculation

%% off batch_mode$

comment -------------------------------------------------------------
The following example calculates all conservation laws of the KdV-
equation with a characteristic function of order not higher than two;

nodepnd {u}$           % deletes all dependencies of u
depend u,x,t$          % declares u to be a function of x,t
conlaw4({{df(u,t) = u*df(u,x)+df(u,x,3)}, {u}, {t,x}},
        {0, 2, t, {}, {}}                             )$

comment -------------------------------------------------------------
The next example demonstrates that one can specify an ansatz
for the characteristic function of one or more equations of the
PDE-system. In this example all conservation laws of the wave
equation which is written as a first order system are calculated
such that the characteristic functions of the first of both
equations is proportional to df(u,x,2). (This will include zero
as it is a multiple of df(u,x,2) too.)

In the following input the equations are solved for the t-derivatives,
so the t-derivatives will be substituted in the conservation-law-
conditions, so the ansatz for q_1 should have no t-derivatives of u
included. Therefore the function r in q_1 below is specified as
depending on t,x,u,v,df(u,x),df(v,x). 
In the call of conlaw2 the list of variables is {t,x} and x is
the second of the variables (could equally well be in reverse order).
Therefore df(u,x) takes the form u!`2 when the dependencies of r
are specified (see conlaw.tex);

nodepnd {u,v,r}$
depend u,x,t$
depend v,x,t$
depend r,t,x,u,v,u!`2,v!`2$
q_1:=r*df(u,x,2)$
conlaw2({{df(u,t)=df(v,x),
          df(v,t)=df(u,x) }, {u,v}, {t,x}},
        {2, 2, t, {r}, {}})$
clear q_1$
nodepnd {q_1}$

comment -------------------------------------------------------------
For the Burgers equation the following example finds all conservation
laws of zero'th order in the characteristic function up to the solution
of the linear heat equation. This is an example for what happens when 
not all conditions could be solved, but it is also an example which 
shows that not only characteristic functions of polynomial or rational
form can be found;

nodepnd {u}$
depend u,x,t$
conlaw1({{df(u,t)=df(u,x,2)+df(u,x)**2/2}, {u}, {t,x}}, 
        {0, 0, t, {}, {}}                              )$      

comment -------------------------------------------------------------
In this example all conservation laws of the Ito system are calculated
that have a conserved density of order not higher than one.
This is a further example of non-polynomial conservation laws;

nodepnd {u,v}$
depend u,x,t$
depend v,x,t$
conlaw1({{df(u,t)=df(u,x,3)+6*u*df(u,x)+2*v*df(v,x),
          df(v,t)=2*df(u,x)*v+2*u*df(v,x)           }, {u,v}, {t,x}},
        {0, 1, t, {}, {}})$

comment -------------------------------------------------------------
In the next example the 5th order Korteweg - de Vries equation is
investigated concerning conservation laws of order 0 and 1 in the
conserved density P_t. Parameters a,b,c in the PDE are determined
such that conservation laws exist. This complicates the problem by
making it non-linear with a number of cases to be considered.
Some of the subcases below can be combined to reduce their number
which currently is not done automatically;

nodepnd {u}$
depend u,x,t$
conlaw1({{df(u,t)=-df(u,x,5)-a*u**2*df(u,x)
                  -b*df(u,x)*df(u,x,2)-c*u*df(u,x,3)}, 
         {u}, {t,x}},
        {0, 1, t, {a,b,c}, {}})$

comment -------------------------------------------------------------
conlawi can also be used to determine first integrals of ODEs.
The generality of the ansatz is not just specified by the order.
For example, the Lorentz system below is a first order system
therefore any first integrals are zero order expressions.
The ansatz to be investigated below looks for first integrals of
the form a1(x,1)+a2(y,t)+a3(x,t)=const. and determines parameters
s,b,r such that first integrals exist;

nodepnd {x,y,z,a1,a2,a3,b,s,r}$
depend x,t$
depend y,t$
depend z,t$
depend a1,x,t$
depend a2,y,t$
depend a3,z,t$

p_t:=a1+a2+a3$
conlaw1({{df(x,t) = - s*x + s*y,
          df(y,t) = x*z + r*x - y,
          df(z,t) = x*y - b*z},
         {x,y,z},{t}
	},
	{0,0,t,{a1,a2,a3,s,r,b},{}})$

clear p_t$
nodepnd {u,v,r,p_t,x,y,z,a1,a2,a3,b,s,r}$

end$
