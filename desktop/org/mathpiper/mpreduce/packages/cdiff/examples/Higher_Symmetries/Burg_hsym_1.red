% Raffaele Vitolo, 09/10/09
% This is the computation for (higher) symmetries of Burgers
% In order to work with the examples, load first CDIFF with the command
% load_package cdiff;

% The following instructions initialize the total derivatives. The first
% string is the name of the vector field,
% the second item is the list of even variables
% (note that u1, u2, ... are u_x, u_xx, ...),
% the third item is the list of non-commuting variables
% (`ext' stands for `external' like in external (wedge) product).

super_vectorfield(ddx,{x,t,u,u1,u2,u3,u4,u5,u6,u7,
u8,u9,u10,u11,u12,u13,u14,u15,u16,u17},
{ext 1,ext 2,ext 3,ext 4,ext 5,ext 6,ext 7,ext 8,ext 9,ext 10,ext
11,ext 12,ext 13,ext 14,ext 15,ext 16,ext 17,ext 18,ext 19,ext 20,ext
21,ext 22,ext 23,ext 24,ext 25,ext 26,ext 27,ext 28,ext 29,ext 30,
ext 31,ext 32,ext 33,ext 34,ext 35,ext 36,ext 37,ext 38,ext 39,ext 40,
ext 41,ext 42,ext 43,ext 44,ext 45,ext 46,ext 47,ext 48,ext 49,ext 50,
ext 51,ext 52,ext 53,ext 54,ext 55,ext 56,ext 57,ext 58,ext 59,ext 60,
ext 61,ext 62,ext 63,ext 64,ext 65,ext 66,ext 67,ext 68,ext 69,ext 70,
ext 71,ext 72,ext 73,ext 74,ext 75,ext 76,ext 77,ext 78,ext 79,ext 80
});

super_vectorfield(ddt,{x,t,u,u1,u2,u3,u4,u5,u6,u7,
u8,u9,u10,u11,u12,u13,u14,u15,u16,u17},
{ext 1,ext 2,ext 3,ext 4,ext 5,ext 6,ext 7,ext 8,ext 9,ext 10,ext
11,ext 12,ext 13,ext 14,ext 15,ext 16,ext 17,ext 18,ext 19,ext 20,ext
21,ext 22,ext 23,ext 24,ext 25,ext 26,ext 27,ext 28,ext 29,ext 30,
ext 31,ext 32,ext 33,ext 34,ext 35,ext 36,ext 37,ext 38,ext 39,ext 40,
ext 41,ext 42,ext 43,ext 44,ext 45,ext 46,ext 47,ext 48,ext 49,ext 50,
ext 51,ext 52,ext 53,ext 54,ext 55,ext 56,ext 57,ext 58,ext 59,ext 60,
ext 61,ext 62,ext 63,ext 64,ext 65,ext 66,ext 67,ext 68,ext 69,ext 70,
ext 71,ext 72,ext 73,ext 74,ext 75,ext 76,ext 77,ext 78,ext 79,ext 80
});


% Specification of the vectorfield ddx.
% The meaning of the first index is the parity of variables.
% In particular here we have just even variables.
% The second index parametrizes the second item (list)
% in the super_vectorfield declaration.

ddx(0,1):=1$
ddx(0,2):=0$
ddx(0,3):=u1$
ddx(0,4):=u2$
ddx(0,5):=u3$
ddx(0,6):=u4$
ddx(0,7):=u5$
ddx(0,8):=u6$
ddx(0,9):=u7$
ddx(0,10):=u8$
ddx(0,11):=u9$
ddx(0,12):=u10$
ddx(0,13):=u11$
ddx(0,14):=u12$
ddx(0,15):=u13$
ddx(0,16):=u14$
ddx(0,17):=u15$
ddx(0,18):=u16$
ddx(0,19):=u17$
ddx(0,20):=letop$

% Specification of the vectorfield ddt
% In the evolutionary case we never have more than one time derivative
% other derivatives are u_txxx ...

ddt(0,1):=0$
ddt(0,2):=1$
ddt(0,3):=ut$
ddt(0,4):=ut1$
ddt(0,5):=ut2$
ddt(0,6):=ut3$
ddt(0,7):=ut4$
ddt(0,8):=ut5$
ddt(0,9):=ut6$
ddt(0,10):=ut7$
ddt(0,11):=ut8$
ddt(0,12):=ut9$
ddt(0,13):=ut10$
ddt(0,14):=ut11$
ddt(0,15):=ut12$
ddt(0,16):=ut13$
ddt(0,17):=ut14$
ddt(0,18):=letop$
ddt(0,19):=letop$
ddt(0,20):=letop$

% The equation -- this is also used to specify internal variables.
% For evolutionary equations internal variables are of the type
% (t,x,u,u_x,u_xx,...)

ut:=u2+2*u*u1;

ut1:=ddx ut;
ut2:=ddx ut1;
ut3:=ddx ut2;
ut4:=ddx ut3;
ut5:=ddx ut4;
ut6:=ddx ut5;
ut7:=ddx ut6;
ut8:=ddx ut7;
ut9:=ddx ut8;
ut10:=ddx ut9;
ut11:=ddx ut10;
ut12:=ddx ut11;
ut13:=ddx ut12;
ut14:=ddx ut13;

% Test for verifying the commutation of total derivatives.
% Highest order defined terms yield some `letop'
% which means `careful' in Dutch and is treated as a new variable.

for i:=1:17 do write ev(0,i):=ddt(ddx(0,i))-ddx(ddt(0,i));

pause;

%% This is the list of variables with respect to their grading,
%% starting from degree ONE.

graadlijst:={{u},{u1},{u2},{u3},{u4},{u5},
{u6},{u7},{u8},{u9},{u10},{u11},{u12},{u13},{u14},{u15},{u16},{u17}};

% This is the list of all monomials of degree 0, 1, 2, ... 
% which can be constructed from the above list of elementary variables 
% with their grading.

grd0:={1};
grd1:= mkvarlist1(1,1)$
grd2:= mkvarlist1(2,2)$
grd3:= mkvarlist1(3,3)$
grd4:= mkvarlist1(4,4)$
grd5:= mkvarlist1(5,5)$
grd6:= mkvarlist1(6,6)$
grd7:= mkvarlist1(7,7)$
grd8:= mkvarlist1(8,8)$
grd9:= mkvarlist1(9,9)$
grd10:= mkvarlist1(10,10)$
grd11:= mkvarlist1(11,11)$
grd12:= mkvarlist1(12,12)$
grd13:= mkvarlist1(13,13)$
grd14:= mkvarlist1(14,14)$
grd15:= mkvarlist1(15,15)$
grd16:= mkvarlist1(16,16)$

% Initialize a counter for the vector of arbitrary constants

ctel:=0;

% we assume a generating function of degree <= 5

sym:=
(for each el in grd0 sum (c(ctel:=ctel+1)*el))+
(for each el in grd1 sum (c(ctel:=ctel+1)*el))+
(for each el in grd2 sum (c(ctel:=ctel+1)*el))+
(for each el in grd3 sum (c(ctel:=ctel+1)*el))+
(for each el in grd4 sum (c(ctel:=ctel+1)*el))+
(for each el in grd5 sum (c(ctel:=ctel+1)*el))$

% This is the equation ell_B(sym)=0, where B=0 is Burgers'equation
% and sym is the generating function. From now on all equations
% are arranged in a single vector whose name is `equ'.

equ 1:=ddt(sym)-ddx(ddx(sym))-2*u*ddx(sym)-2*u1*sym ;

% This is the list of variables, to be passed to the equation solver.

vars:={x,t,u,u1,u2,u3,u4,u5,u6,u7,u8,u9,u10,u11,u12,u13,u14,u15,u16,u17};

% This is the number of initial equation(s)

tel:=1;

% The following procedure uses multi_coeff (from the package `tools').
% It gets all coefficients of monomials appearing in the initial equation(s).
% The coefficients are put into the vector equ after the initial equations.

procedure splitvars i;
begin;
ll:=multi_coeff(equ i,vars);
equ(tel:=tel+1):=first ll;
ll:=rest ll;
for each el in ll do equ(tel:=tel+1):=second el;
end;

% This command initialize the equation solver.
% It passes the equation(s) togeher with their number `tel',
% the constants'vector `c', its length `ctel',
% an arbitrary constant `f' that may appear in computations.

initialize_equations(equ,tel,{},{c,ctel,0},{f,0,0});

% Run the procedure splitvars in order to obtain equations on coefficiens
% of each monomial.

splitvars 1;

% Next command tells the solver the total number of equations obtained
% after running splitvars.

pte tel;

% It is worth to write down the equations for the coefficients.

for i:=2:tel do write equ i;

pause;

% This command solves the equations for the coefficients.
% Note that we have to skip the initial equations!

for i:=2:te do es i;

;end;

