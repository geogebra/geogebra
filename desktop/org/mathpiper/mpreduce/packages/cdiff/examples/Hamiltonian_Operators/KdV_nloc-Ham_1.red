% Nonlocal Hamiltonian structures on KdV
% Raffaele Vitolo, 30 May 2010
% In order to work with the examples, load first CDIFF with the command
% load_package cdiff;

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

%specification of the vectorfield ddx
%the even variables
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

%specification of the vectorfield ddt
%the even variables
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

ut:=u*u1+u3;

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

for i:=1:17 do write ev(0,i):=ddt(ddx(0,i))-ddx(ddt(0,i));

% we now introduce odd variables ext 1,....,ext 20, and associated relations
%
% Note that now the variables with index 50,51,52 play an essential role.
% Indeed, these are the covering variables that we can indicate with
% r_1, r_2, r_3. We are going to find a first non-local Hamiltonian operator
% depending linearly on one of these variables. Higher non-local Hamiltonian 
% operators could be found by introducing total derivatives of the r's.
% Of course, the variables are specified through the components of the 
% previously found conservation laws according with the rule
% {r_1}_x=fx, {r_1}_t=ft.

%Specification of odd variables in ddx

ddx(1,1):=0$
ddx(1,2):=0$
ddx(1,3):=ext 4$
ddx(1,4):=ext 5$
ddx(1,5):=ext 6$
ddx(1,6):=ext 7$
ddx(1,7):=ext 8$
ddx(1,8):=ext 9$
ddx(1,9):=ext 10$
ddx(1,10):=ext 11$
ddx(1,11):=ext 12$
ddx(1,12):=ext 13$
ddx(1,13):=ext 14$
ddx(1,14):=ext 15$
ddx(1,15):=ext 16$
ddx(1,16):=ext 17$
ddx(1,17):=ext 18$
ddx(1,18):=ext 19$
ddx(1,19):=ext 20$
ddx(1,20):=letop$
ddx(1,50):=(t*u1+1)*ext 3$ % degree -2
ddx(1,51):=u1*ext 3$ % degree +1
ddx(1,52):=(u*u1+u3)*ext 3$ % degree +3

% Specification of odd variables in ddt
ddt(1,1):=0$
ddt(1,2):=0$
ddt(1,3):=ext 6 + u*ext 4$
ddt(1,4):=ddx(ddt(1,3))$
ddt(1,5):=ddx(ddt(1,4))$
ddt(1,6):=ddx(ddt(1,5))$
ddt(1,7):=ddx(ddt(1,6))$
ddt(1,8):=ddx(ddt(1,7))$
ddt(1,9):=ddx(ddt(1,8))$
ddt(1,10):=ddx(ddt(1,9))$
ddt(1,11):=ddx(ddt(1,10))$
ddt(1,12):=ddx(ddt(1,11))$
ddt(1,13):=ddx(ddt(1,12))$
ddt(1,14):=ddx(ddt(1,13))$
ddt(1,15):=ddx(ddt(1,14))$
ddt(1,16):=ddx(ddt(1,15))$
ddt(1,17):=ddx(ddt(1,16))$
ddt(1,18):=letop$
ddt(1,19):=letop$
ddt(1,20):=letop$
ddt(1,50) := ext(5)*t*u1 + ext(5) - ext(4)*t*u2 + ext(3)*t*u*u1 + ext(3)*t*u3 + 
ext(3)*u$
ddt(1,51) := ext(5)*u1 - ext(4)*u2 + ext(3)*u*u1 + ext(3)*u3$
ddt(1,52) := ext(5)*u*u1 + ext(5)*u3 - ext(4)*u*u2 - ext(4)*u1**2 - ext(4)*u4 + 
ext(3)*u**2*u1 + 2*ext(3)*u*u3 + 3*ext(3)*u1*u2 + ext(3)*u5$


graadlijst:={{},{u},{u1},{u2},{u3},{u4},{u5},
{u6},{u7},{u8},{u9},{u10},{u11},{u12},{u13},{u14},{u15},{u16},{u17}};

grdm6:={0};
grdm5:={0};
grdm4:={0};
grdm3:={0};
grdm2:={0};
grdm1:={0};

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
grd17:= mkvarlist1(17,17)$
grd18:= mkvarlist1(18,18)$
grd19:= mkvarlist1(19,19)$

ctel:=0;

% This is the ansatz for the nonlocal Hamiltonian operator.
% It comes from the fact that local Hamiltonian operators have 
% grading -1 and +1 when written in terms of p's. So we are looking 
% for a nonlocal Hamiltonian operator of degree 3.

phi:=
(for each el in grd6 sum (c(ctel:=ctel+1)*el))*ext 50+
(for each el in grd3 sum (c(ctel:=ctel+1)*el))*ext 51+
(for each el in grd1 sum (c(ctel:=ctel+1)*el))*ext 52+

(for each el in grd5 sum (c(ctel:=ctel+1)*el))*ext 3+
(for each el in grd4 sum (c(ctel:=ctel+1)*el))*ext 4+
(for each el in grd3 sum (c(ctel:=ctel+1)*el))*ext 5+
(for each el in grd2 sum (c(ctel:=ctel+1)*el))*ext 6+
(for each el in grd1 sum (c(ctel:=ctel+1)*el))*ext 7+
(for each el in grd0 sum (c(ctel:=ctel+1)*el))*ext 8
$

% equation for shadows of nonlocal symmetries in \ell^*-covering

equ 1:=ddt(phi)-u*ddx(phi)-u1*phi-ddx(ddx(ddx(phi)));

pause;

vars:={x,t,u,u1,u2,u3,u4,u5,u6,u7,u8,u9,u10,u11,u12,u13,u14,u15,u16,u17};

tel:=1;

procedure splitext i;
begin;
ll:=operator_coeff(equ i,ext);
equ(tel:=tel+1):=first ll;
ll:=rest ll;
for each el in ll do equ(tel:=tel+1):=second el;
end;

procedure splitvars i;
begin;
ll:=multi_coeff(equ i,vars);
equ(tel:=tel+1):=first ll;
ll:=rest ll;
for each el in ll do equ(tel:=tel+1):=second el;
end;

initialize_equations(equ,tel,{},{c,ctel,0},{f,0,0});

splitext 1;

tel1:=tel;

for i:=2:tel1 do begin splitvars i;equ i:=0;end;

pte tel;

end;
