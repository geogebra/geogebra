% Hamiltonian structures for Boussinesq equation
% Degree of the components Fdu+Gdv: [F]=0,[G]=-1
% Raffaele Vitolo, 2 June 2010
% In order to work with the examples, load first CDIFF with the command
% load_package cdiff;

super_vectorfield(ddx,{x,t,u,v,u1,v1,u2,v2,u3,v3,u4,v4,u5,v5,u6,v6,u7,
v7,u8,v8,u9,v9,u10,v10,u11,v11,u12,v12,u13,v13,u14,v14,u15,v15,u16,v16,u17,v17},
{ext 1,ext 2,ext 3,ext 4,ext 5,ext 6,ext 7,ext 8,ext 9,ext 10,ext
11,ext 12,ext 13,ext 14,ext 15,ext 16,ext 17,ext 18,ext 19,ext 20,ext
21,ext 22,ext 23,ext 24,ext 25,ext 26,ext 27,ext 28,ext 29,ext 30,
ext 31,ext 32,ext 33,ext 34,ext 35,ext 36,ext 37,ext 38,ext 39,ext 40,
ext 41,ext 42,ext 43,ext 44,ext 45,ext 46,ext 47,ext 48,ext 49,ext 50,
ext 51,ext 52,ext 53,ext 54,ext 55,ext 56,ext 57,ext 58,ext 59,ext 60,
ext 61,ext 62,ext 63,ext 64,ext 65,ext 66,ext 67,ext 68,ext 69,ext 70,
ext 71,ext 72,ext 73,ext 74,ext 75,ext 76,ext 77,ext 78,ext 79,ext 80
});

super_vectorfield(ddt,{x,t,u,v,u1,v1,u2,v2,u3,v3,u4,v4,u5,v5,u6,v6,u7,
v7,u8,v8,u9,v9,u10,v10,u11,v11,u12,v12,u13,v13,u14,v14,u15,v15,u16,v16,u17,v17},
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
ddx(0,4):=v1$
ddx(0,5):=u2$
ddx(0,6):=v2$
ddx(0,7):=u3$
ddx(0,8):=v3$
ddx(0,9):=u4$
ddx(0,10):=v4$
ddx(0,11):=u5$
ddx(0,12):=v5$
ddx(0,13):=u6$
ddx(0,14):=v6$
ddx(0,15):=u7$
ddx(0,16):=v7$
ddx(0,17):=u8$
ddx(0,18):=v8$
ddx(0,19):=u9$
ddx(0,20):=v9$
ddx(0,21):=u10$
ddx(0,22):=v10$
ddx(0,23):=u11$
ddx(0,24):=v11$
ddx(0,25):=u12$
ddx(0,26):=v12$
ddx(0,27):=u13$
ddx(0,28):=v13$
ddx(0,29):=u14$
ddx(0,30):=v14$
ddx(0,31):=u15$
ddx(0,32):=v15$
ddx(0,33):=u16$
ddx(0,34):=v16$
ddx(0,35):=u17$
ddx(0,36):=v17$
ddx(0,37):=letop$
ddx(0,38):=letop$

%specification of the vectorfield ddt
%the even variables
ddt(0,1):=0$
ddt(0,2):=1$
ddt(0,3):=ut$
ddt(0,4):=vt$
ddt(0,5):=ut1$
ddt(0,6):=vt1$
ddt(0,7):=ut2$
ddt(0,8):=vt2$
ddt(0,9):=ut3$
ddt(0,10):=vt3$
ddt(0,11):=ut4$
ddt(0,12):=vt4$
ddt(0,13):=ut5$
ddt(0,14):=vt5$
ddt(0,15):=ut6$
ddt(0,16):=vt6$
ddt(0,17):=ut7$
ddt(0,18):=vt7$
ddt(0,19):=ut8$
ddt(0,20):=vt8$
ddt(0,21):=ut9$
ddt(0,22):=vt9$
ddt(0,23):=ut10$
ddt(0,24):=vt10$
ddt(0,25):=ut11$
ddt(0,26):=vt11$
ddt(0,27):=ut12$
ddt(0,28):=vt12$
ddt(0,29):=ut13$
ddt(0,30):=vt13$
ddt(0,31):=ut14$
ddt(0,32):=vt14$
ddt(0,33):=ut15$
ddt(0,34):=vt15$
ddt(0,35):=ut16$
ddt(0,36):=vt16$
ddt(0,37):=letop$
ddt(0,38):=letop$

ut:=u1*v+u*v1+sig*v3;

vt:=u1+v*v1;

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

vt1:=ddx vt;
vt2:=ddx vt1;
vt3:=ddx vt2;
vt4:=ddx vt3;
vt5:=ddx vt4;
vt6:=ddx vt5;
vt7:=ddx vt6;
vt8:=ddx vt7;
vt9:=ddx vt8;
vt10:=ddx vt9;
vt11:=ddx vt10;
vt12:=ddx vt11;
vt13:=ddx vt12;
vt14:=ddx vt13;


for i:=1:17 do write ev(0,i):=ddt(ddx(0,i))-ddx(ddt(0,i));

pause;

%we now introduce odd variables ext 1,....,ext 20, and associated relations
%

%Specification of odd variables in ddx

ddx(1,1):=0$
ddx(1,2):=0$
ddx(1,3):=ext 5$
ddx(1,4):=ext 6$
ddx(1,5):=ext 7$
ddx(1,6):=ext 8$
ddx(1,7):=ext 9$
ddx(1,8):=ext 10$
ddx(1,9):=ext 11$
ddx(1,10):=ext 12$
ddx(1,11):=ext 13$
ddx(1,12):=ext 14$
ddx(1,13):=ext 15$
ddx(1,14):=ext 16$
ddx(1,15):=ext 17$
ddx(1,16):=ext 18$
ddx(1,17):=ext 19$
ddx(1,18):=ext 20$
ddx(1,19):=ext 21$
ddx(1,20):=ext 22$
ddx(1,21):=ext 23$
ddx(1,22):=ext 24$
ddx(1,23):=ext 25$
ddx(1,24):=ext 26$
ddx(1,25):=ext 27$
ddx(1,26):=ext 28$
ddx(1,27):=ext 29$
ddx(1,28):=ext 30$
ddx(1,29):=ext 31$
ddx(1,30):=ext 32$
ddx(1,31):=ext 33$
ddx(1,32):=ext 34$
ddx(1,33):=ext 35$
ddx(1,34):=ext 36$
ddx(1,35):=ext 37$
ddx(1,36):=ext 38$
ddx(1,37):=letop$
ddx(1,38):=letop$

%Specification of odd variables in ddt
ddt(1,1):=0$
ddt(1,2):=0$
ddt(1,3):=+v*ext 5+ext 6$            %v*ext 5+v1*ext 3+ext 6$
ddt(1,4):=u*ext 5+sig*ext 9+v*ext 6$ %sig*ext 9-u1*ext 3+v*ext 6$
ddt(1,5):=ddx(ddt(1,3))$
ddt(1,6):=ddx(ddt(1,4))$
ddt(1,7):=ddx(ddt(1,5))$
ddt(1,8):=ddx(ddt(1,6))$
ddt(1,9):=ddx(ddt(1,7))$
ddt(1,10):=ddx(ddt(1,8))$
ddt(1,11):=ddx(ddt(1,9))$
ddt(1,12):=ddx(ddt(1,10))$
ddt(1,13):=ddx(ddt(1,11))$
ddt(1,14):=ddx(ddt(1,12))$
ddt(1,15):=ddx(ddt(1,13))$
ddt(1,16):=ddx(ddt(1,14))$
ddt(1,17):=ddx(ddt(1,15))$
ddt(1,18):=ddx(ddt(1,16))$
ddt(1,19):=ddx(ddt(1,17))$
ddt(1,20):=ddx(ddt(1,18))$
ddt(1,21):=ddx(ddt(1,19))$
ddt(1,22):=ddx(ddt(1,20))$
ddt(1,23):=ddx(ddt(1,21))$
ddt(1,24):=ddx(ddt(1,22))$
ddt(1,25):=ddx(ddt(1,23))$
ddt(1,26):=ddx(ddt(1,24))$
ddt(1,27):=ddx(ddt(1,25))$
ddt(1,28):=ddx(ddt(1,26))$
ddt(1,29):=ddx(ddt(1,27))$
ddt(1,30):=ddx(ddt(1,28))$
ddt(1,31):=ddx(ddt(1,29))$
ddt(1,32):=ddx(ddt(1,30))$
ddt(1,33):=ddx(ddt(1,31))$
ddt(1,34):=ddx(ddt(1,32))$
ddt(1,35):=ddx(ddt(1,33))$
ddt(1,36):=ddx(ddt(1,34))$
ddt(1,37):=letop$
ddt(1,38):=letop$

% remember: the list starts with 1 !!!!!!!!!

graadlijst:={{v},{u,v1},{u1,v2},{u2,v3},{u3,v4},{u4,v5},
{u5,v6},{u6,v7},{u7,v8},{u8,v9},{u9,v10},{u10,v11},{u11,v12},{u12,v13},
{u13,v14},{u14,v15},{u15,v16},{u16,v17},{u17}};

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

phi1:=
(for each el in grd2 sum (c(ctel:=ctel+1)*el))*ext 3+
(for each el in grd1 sum (c(ctel:=ctel+1)*el))*ext 5+
(for each el in grd1 sum (c(ctel:=ctel+1)*el))*ext 4+
(for each el in grd0 sum (c(ctel:=ctel+1)*el))*ext 6
$

phi2:=
(for each el in grd1 sum (c(ctel:=ctel+1)*el))*ext 3+
(for each el in grd0 sum (c(ctel:=ctel+1)*el))*ext 5+
(for each el in grd0 sum (c(ctel:=ctel+1)*el))*ext 4
$

equ 1:=ddt(phi1)-v*ddx(phi1)-v1*phi1-u1*phi2-u*ddx(phi2)
-sig*ddx(ddx(ddx(phi2)));

equ 2:=-ddx(phi1)-v*ddx(phi2)-v1*phi2+ddt(phi2);

vars:={x,t,u,v,u1,v1,u2,v2,u3,v3,u4,v4,u5,v5,u6,v6,u7,v7,u8,v8,u9,v9,u10,v10,
u11,v11,u12,v12,u13,v13,u14,v14,u15,v15,u16,v16,u17,v17};

tel:=2;

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

for i:=1:2 do splitext i;

tel1:=tel;

for i:=3:tel1 do begin splitvars i;equ i:=0;end;

pte tel;


end;

