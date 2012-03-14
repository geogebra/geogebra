%*******************************************************************%
%                                                                   %
%                      L I E P D E . T S T                          %
%                      -------------------                          %
%  liepde.tst contains test examples for the program liepde.red.    %
%                                                                   %
%  Author of this file: Thomas Wolf                                 %
%  Date:  21. April 1998, 6. May 2003                               %
%                                                                   %
%  Details about the syntax of liepde.red are given in liepde.tex.  %
%                                                                   %
%  To run this demo you need to load liepde and crack through       %
%     load crack,liepde$                                            %
%  and to read in this file as                                      %
%     in "liepde.tst";                                              %
%  If you got the source code of a newer version of liepde then     %
%  either read it in through                                        %
%     in "crack.red","liepde.red"$                                  %
%  (with the appropriate directory name in front of liepde.red)     %
%  or, to speed up the calculation, you compile before with         %
%     faslout "crack"$                                              %
%     in "crack.red"$                                               %
%     faslend$                                                      %
%     faslout "liepde"$                                             %
%     in "liepde.red"$                                              %
%     faslend$                                                      %
%  and then load both it with                                       %
%     load crack,liepde$                                            %
%                                                                   %
%*******************************************************************%

load crack;

lisp(depl!*:=nil)$     % clearing of all dependences
%setcrackflags()$
lisp(print_:=nil)$
on dfprint$

comment
-------------------------------------------------------
The following runs demonstrate the program LIEPDE for 
the computation of infinitesimal symmetries. Times given
below refer to a 8 MB session under LINUX on a 133 MHz
Pentium PC with the CRACK version of April 1998 running
PSL Reduce.
-------------------------------------------------------;

lisp(prelim_:=nil)$      % not necessary as this is the default value
lisp(individual_:=nil)$  % not necessary as this is the default value

comment
-------------------------------------------------------
The first example is a single ODE with a parametric
function f=f(x) for which point symmetries are to be
determined.
(Time ~ 6 sec.);
write"-------------------------------------------------------";

lisp(freeint_:=nil)$ % This enables the solution of differential equ.s in
                     % which unevaluated integrals remain. This becomes
                     % necessary in this example through the parametric
                     % function f=f(x)
depend y,x$
depend f,x$
liepde({df(y,x,2)=-(y+3*f)*df(y,x)+y**3-f*y**2-(2*f**2+df(f,x))*y, 
        {y}, {x}},
       {"point"},{},{})$
nodepnd {y,f}$
lisp(freeint_:=t)$   % Because the simplification of differential
                     % expressions which involve unevaluated integrals
                     % may provide difficulties such solutions involving
                     % unevaluated integrals are disabled.

comment
-------------------------------------------------------
The following example demonstrates a number of things.
The Burgers equation is investigated concerning third
order symmetries. The equation is used to substitute
df(u,t) and all derivatives of df(u,t). This computation
also shows that any equations that remain unsolved are
returned, like in this case the heat quation.
(Time ~ 15 sec.);
write"-------------------------------------------------------";

nodepnd {u}$
depend u,t,x$
liepde({df(u,t)=df(u,x,2)+df(u,x)**2,{u},{t,x}},{"general",3},{},{})$

comment
-------------------------------------------------------
Now the same equation is investigated, this time only
df(u,x,2) and its derivatives are substituted. As a
consequence less jet-variables (u-derivatives of lower
order) are generated in the process of formulating the
symmetry conditions. Less jet-variables in which the
conditions have to be fulfilled identically means less
overdetermined conditions and more solutions which to
compute takes longer than before.
(Time ~ 85 sec.);
write"-------------------------------------------------------";

liepde({df(u,x,2)=df(u,t)-df(u,x)**2,{u},{t,x}},{"general",3},{},{})$
nodepnd {u}$

comment
-------------------------------------------------------
The following example includes the Karpman equations
for three unknown functions in 4 variables. 

If point symmetries are to be computed for a single
equation or a system of equations of higher than first
order then there is the option to formulate at first
preliminary conditions for each equation, have CRACK
solving these conditions before the full set of conditions
is formulated and solved. This strategy is adopted if a
lisp flag prelim_ has the value t. The default value
is nil. 

Similarly, if a system of equations is to be investigated
and a flag individual_ has the value t then symmetry
conditions are formulated and investigated for each
individual equation successively. The default value is nil.

It is advantageous to split a large set of conditions
into smaller sets to be investigated successively if
each set is sufficiently overdetermined to be solvable
quickly. Then any substitutions are done in the smaller
set and the next set of conditions is shorter. For
example, for the Karpman equations below the speedup for
prelim_:=t and individual_:=t is a factor of 10.
(Time ~ 1 min.);
write"-------------------------------------------------------";

lisp(prelim_:=t)$
lisp(individual_:=t)$

depend r,x,y,z,t;
depend f,x,y,z,t;
depend v,x,y,z,t;

on time$
liepde({

first 
solve(
        {df(r,t) + w1*df(r,z)
         + s1*(df(r,x)*df(f,x)+df(r,y)*df(f,y)+r*df(f,x,2)/2+r*df(f,y,2)/2) 
         + s2*(df(r,z)*df(f,z)+r*df(f,z,2)/2),
 
         df(f,t) + w1*df(f,z) 
         - (s1*(df(r,x,2)/r+df(r,y,2)/r-df(f,x)**2-df(f,y)**2) +
            s2*(df(r,z,2)/r-df(f,z)**2))/2 + a1*v,
       
         df(v,t,2) - w2**2*(df(v,x,2)+df(v,y,2)+df(v,z,2))
         - 2*a2*r*(df(r,x,2)+df(r,y,2)+df(r,z,2))
         - 2*a2*(df(r,x)**2+df(r,y)**2+df(r,z)**2)},
         
        {df(v,x,2), df(r,x,2), df(f,x,2)}         
 
     )         
         , {r,f,v}, {x,y,z,t}},

       {"point"}, 
       
       {},{})$

off time$
nodepnd {r,f,v}$

comment
-------------------------------------------------------
In the following example a system of two equations (by
V.Sokolov) is investigated concerning a special ansatz for
4th order symmetries. The ansatz for the symmetries includes
two unknown functions f,g. Because x is the second variable
in the list of variables {t,x}, the name u!`2 stands for
df(u,x).
Because higher order symmetries are investigated we have
to set prelim_:=nil. The symmetries to be calculated are
lengthy and therefore conditions are not very overdetermined.
In that case CRACK can take long to solve a single 
subset of conditions. The complete set of conditions would
have been more overdetermined and easier to solve. Therefore
the advantage of first formulating all conditions and then
solving them together with one CRACK call is that having
more equations, the chance of finding short integrable
equations among then is higher, i.e. CRACK has more freedom
in optimizing the computation. Therefore individual_:=nil
is more appropriate in this example.

Because 4th order conditions are to be computed the
`binding stack size' is increased.
(Time ~ 5 min.);
write"-------------------------------------------------------";

lisp(prelim_:=nil)$
lisp(individual_:=nil)$
lisp(if getd 'set_bndstk_size then set_bndstk_size(7000))$

nodepnd {u,v}$
depend  u,x,t;
depend  v,x,t;

des:={df(u,t)=+df(u,x,2) + (u + v)*df(u,x) + u*df(v,x),
      df(v,t)=-df(v,x,2) + (u + v)*df(v,x) + v*df(u,x)
     }$

nodepnd {f,g}$
depend f,t,x,u,v,u!`2,v!`2,u!`2!`2,v!`2!`2,u!`2!`2!`2,v!`2!`2!`2$
depend g,t,x,u,v,u!`2,v!`2,u!`2!`2,v!`2!`2,u!`2!`2!`2,v!`2!`2!`2$
liepde({des,{u,v},{t,x}},
       {xi_t=0,
        xi_x=0,
        eta_u=+df(u,x,4)+f,
        eta_v=-df(v,x,4)+g
       },
       {f,g},{}
      )$
nodepnd {f,g}$

end$
