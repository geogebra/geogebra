load crack,applysym$
%*******************************************************************%
%                                                                   %
%                    A P P L Y S Y M . T S T                        %
%                    -----------------------                        %
%  applysym.tst contains test examples to test the procedure        %
%  quasilinpde in the file applysym.red.                            %
%                                                                   %
%  Author: Thomas Wolf                                              %
%  Date:   22 May 1998                                              %
%                                                                   %
%  You need crack.red and applysym.red to run this demo.            %
%  To use other contents of the program applysym, not demonstrated  %
%  in this demo you need the program liepde.red.                    %
%                                                                   %
%  To run this demo you read in files with                          %
%     in "crack.red"$                                               %
%     in "applysym.red"$                                            %
%  or, to speed up the calculation you compile them before with     %
%     faslout "crack"$                                              %
%     in "crack.red"$                                               %
%     faslend$                                                      %
%     faslout "applysym"$                                           %
%     in "applysym.red"$                                            %
%     faslend$                                                      %
%  and then load them with                                          %
%     load crack,applysym$                                          %
%                                                                   %
%*******************************************************************%

load crack;

lisp(depl!*:=nil)$     % clearing of all dependencies
%setcrackflags()$
lisp(print_:=nil)$
on dfprint$

comment
-------------------------------------------------------
This file is supposed to provide an automatic test of
the program APPLYSYM. On the other hand the application
of APPLYSYM is an interactive process, therefore the
interested user should inspect the example described
in APPLYSYM.TEX which demonstrates the application
of symmetries to integrate a 2nd order ODE.
Here the program QUASILINPDE for integrating first
order quasilinear PDE is demonstrated.
The following equation comes up in the elimination
of resonant terms in normal forms of singularities
of vector fields (C.Herssens, P.Bonckaert, Limburgs
Universitair Centrum/Belgium, private communication);
write"-------------------"$
lisp(print_:=nil)$

depend w,x,y,z$
QUASILINPDE( df(w,x)*x+df(w,y)*y+2*df(w,z)*z-2*w-x*y,  w,  {x,y,z} )$
nodepend w,x,y,z$

comment
-------------------------------------------------------
The result means that w is defined implicitly through 

        x*y    - log(z)*x*y + 2*w      y              
0 = ff(-----,---------------------,---------)         
         z             z            sqrt(z)           

with an arbitrary function ff of 3 arguments. As the PDE
was linear, the arguments of ff are such that we can 
solve for w:                                   

                        x*y      y             
w = log(z)*x*y/2 + z*f(-----,---------)        
                         z    sqrt(z)          

with an arbitrary function f of 2 arguments.
-------------------------------------------------------
The following PDEs are taken from E. Kamke,
Loesungsmethoden und Loesungen von Differential-
gleichungen, Partielle Differentialgleichungen
erster Ordnung, B.G. Teubner, Stuttgart (1979);

write"-------------------"$% equation 1.4 ----------------------
lisp(depl!*:=nil)$
depend z,x,y$
QUASILINPDE( x*df(z,x)-y, z, {x,y})$
write"-------------------"$% equation 2.5 ----------------------
lisp(depl!*:=nil)$
depend z,x,y$
QUASILINPDE( x**2*df(z,x)+y**2*df(z,y), z, {x,y})$
write"-------------------"$% equation 2.6 ----------------------
lisp(depl!*:=nil)$
depend z,x,y$
QUASILINPDE( (x**2-y**2)*df(z,x)+2*x*y*df(z,y), z, {x,y})$
write"-------------------"$% equation 2.7 ----------------------
lisp(depl!*:=nil)$
depend z,x,y$
QUASILINPDE( (a0*x-a1)*df(z,x)+(a0*y-a2)*df(z,y), z, {x,y})$
write"-------------------"$% equation 2.14 ---------------------
lisp(depl!*:=nil)$
depend z,x,y$
QUASILINPDE( a*df(z,x)+b*df(z,y)-x**2+y**2, z, {x,y})$
write"-------------------"$% equation 2.16 ---------------------
lisp(depl!*:=nil)$
depend z,x,y$
QUASILINPDE( x*df(z,x)+y*df(z,y)-a*x, z, {x,y})$
write"-------------------"$% equation 2.20 ---------------------
lisp(depl!*:=nil)$
depend z,x,y$
QUASILINPDE( df(z,x)+df(z,y)-a*z, z, {x,y})$
write"-------------------"$% equation 2.21 ---------------------
lisp(depl!*:=nil)$
depend z,x,y$
QUASILINPDE( df(z,x)-y*df(z,y)+z, z, {x,y})$
write"-------------------"$% equation 2.22 ---------------------
lisp(depl!*:=nil)$
depend z,x,y$
QUASILINPDE( 2*df(z,x)-y*df(z,y)+z, z, {x,y})$
write"-------------------"$% equation 2.23 ---------------------
lisp(depl!*:=nil)$
depend z,x,y$
QUASILINPDE( a*df(z,x)+y*df(z,y)-b*z, z, {x,y})$
write"-------------------"$% equation 2.24 ---------------------
lisp(depl!*:=nil)$
depend z,x,y$
QUASILINPDE( x*(df(z,x)-df(z,y))-y*df(z,y), z,{x,y})$
write"-------------------"$% equation 2.25 ---------------------
lisp(depl!*:=nil)$
depend z,x,y$
QUASILINPDE( x*df(z,x)+y*df(z,y)-az, z, {x,y})$
write"-------------------"$% equation 2.26 ---------------------
lisp(depl!*:=nil)$
depend z,x,y$
QUASILINPDE( x*df(z,x)+y*df(z,y)-z+x**2+y**2-1, z, {x,y})$
write"-------------------"$% equation 2.39 ---------------------
lisp(depl!*:=nil)$
depend z,x,y$
QUASILINPDE( a*x**2*df(z,x)+b*y**2*df(z,y)-c*z**2, z, {x,y})$
write"-------------------"$% equation 2.40 ---------------------
lisp(depl!*:=nil)$
depend z,x,y$
QUASILINPDE( x*y**2*df(z,x)+2*y**3*df(z,y)-2*(y*z-x**2)**2, z,
             {x,y})$
write"-------------------"$% equation 3.12 ---------------------
lisp(depl!*:=nil)$
depend w,x,y,z$
QUASILINPDE( x*df(w,x)+(a*x+b*y)*df(w,y)+(c*x+d*y+f*z)*df(w,z), w,
             {x,y,z})$
write"-------------------"$% end -------------------------------

lisp(depl!*:=nil)$
end$
