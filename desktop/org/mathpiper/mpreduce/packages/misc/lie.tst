% test file for the Lie package

% 1. n-dimensional Lie algebras with dimL1=1
% n=6

array lienstrucin(6,6,6)$

lienstrucin(1,2,2):=lienstrucin(1,2,6):=lienstrucin(1,5,2):=-1$
lienstrucin(1,5,6):=lienstrucin(2,5,3):=lienstrucin(2,5,5):=-1$
lienstrucin(1,2,3):=lienstrucin(1,2,5):=lienstrucin(1,5,3):=1$
lienstrucin(1,5,5):=lienstrucin(2,5,2):=lienstrucin(2,5,6):=1$

liendimcom1(6);

% transformation matrix

lientrans;

clear lienstrucin$

% n=8

array lienstrucin(8,8,8)$

lienstrucin(1,2,2):=lienstrucin(1,5,2):=lienstrucin(2,4,3):=1$
lienstrucin(2,4,5):=lienstrucin(4,5,2):=1$
lienstrucin(1,2,3):=lienstrucin(1,2,5):=lienstrucin(1,5,3):=-1$
lienstrucin(1,5,5):=lienstrucin(2,4,2):=lienstrucin(4,5,3):=-1$
lienstrucin(4,5,5):=-1$
lienstrucin(1,2,6):=lienstrucin(1,5,6):=lienstrucin(4,5,6):=5$
lienstrucin(2,4,6):=-5$

liendimcom1(8);

% same with verbose output

on tr_lie$

liendimcom1(8);

clear lienstrucin$

off tr_lie$

% 2. 4-dimensional Lie algebras

% Korteweg-de Vries Equation: u_t+u_{xxx}+uu_x=0
% symmetry algebra spanned by four vector fields:
% v_1=d_x, v_2=d_t, v_3=td_x+d_u, v_4=xd_x+3td_t-2ud_u

array liestrin(4,4,4)$

liestrin(1,4,1):=liestrin(2,3,1):=1$
liestrin(2,4,2):=3$
liestrin(3,4,3):=-2$

lieclass(4);

clear liestrin$


% dimL1=3, dimL2=3

array liestrin(4,4,4)$

liestrin(1,2,1):=-6$liestrin(1,2,3):=-2$liestrin(1,2,4):=6$
liestrin(1,3,1):=-1$liestrin(1,3,2):=1$liestrin(1,3,4):=1$
liestrin(2,3,1):=-3$liestrin(2,3,4):=2$
liestrin(2,4,1):=6$liestrin(2,4,3):=2$liestrin(2,4,4):=-6$
liestrin(3,4,1):=1$liestrin(3,4,2):=-1$liestrin(3,4,4):=-1$

lieclass(4);

% same with verbose output

on tr_lie$

lieclass(4);

% transformation matrix

liemat;

clear liestrin$

off tr_lie$


end$


