% Tests of Cvitanovic Package.


% COPYRIGHT (C) 1990, INSTITUTE OF NUCLEAR PHYSICS, MOSCOW STATE UNIV.
% CVITBUBLE  TEST OF CVITANOVIC PACKAGE
% AUTHOR     A. KRYUKOV, ARODIONOV, A.TARANOV
% VERSION    1.1
% RELEASE    18-SEP-90

index j1,j2,j3,j4,j5,j6,j7,j8,j9,j0;
vecdim n$

%       Tests of the weels with buble
%       (Use notation from SIGSAM Bull, 1989, v.23, no.4, pp.15-24)

g(l,j1,j2,j2,j1);

g(l,j1,j2)*g(l1,j3,j1,j2,j3);
g(l,j1,j2)*g(l1,j3,j1,j3,j2);
g(l,j1,j2)*g(l1,j3,j3,j2,j1);

g(l,j1,j2,j3,j4)*g(l1,j1,j2,j3,j4);
g(l,j1,j2)*g(l1,j3,j4,j1,j2,j4,j3);
g(l,j1,j2,j3,j4)*g(l1,j1,j4,j2,j3);
g(l,j1,j2)*g(l1,j3,j4,j1,j4,j3,j2);

g(l,j1,j2)*g(l1,j3,j4,j5,j1,j2,j3,j4,j5);
g(l,j1,j2,j3,j4)*g(l1,j5,j1,j2,j3,j5,j4);
g(l,j1,j2,j3,j4,j5,j1)*g(l1,j2,j5,j3,j4);
g(l,j1,j2,j3,j4,j5,j1,j2,j5)*g(l1,j4,j3);

g(l,j1,j2)*g(l1,j3,j4,j5,j6,j1,j2,j3,j4,j5,j6);
g(l,j1,j2,j3,j4)*g(l1,j5,j6,j1,j2,j3,j4,j6,j5);
g(l,j1,j2,j3,j4,j5,j6)*g(l1,j1,j2,j4,j3,j6,j5);
g(l,j1,j2,j3,j4,j5,j6,j1,j2)*g(l1,j6,j3,j4,j5);
g(l,j1,j2,j3,j4,j5,j6,j7,j1,j2,j3,j4,j5)*g(l1,j6,j7);
g(l,j1,j2,j3,j4,j5,j6,j7,j1,j2,j3)*g(l1,j4,j5,j7,j6);
g(l,j1,j2,j3,j4,j5,j6,j7,j2)*g(l1,j1,j3,j4,j5,j6,j7);


% COPYRIGHT (C) 1988,1990, INSTITUTE OF NUCLEAR PHYSICS, MOSCOW STATE U.
% CVITEST   Test of CVITANOVIC PACKAGE
% AUTHOR    A. KRYUKOV, A.RODIONOV, A.TARANOV
% VERSION   1.2
% RELEASE   11-MAR-90

%
%     Test for trace of Dirac matrices.
%
%     All tests are the lattices with difference lines
%     (Use notation from SIGSAM Bull, 1989, v.4,no.23, pp.15-24)

index m1,m2,m3,m4,m5,m6,m7,m8,m9,m0;
index n1,n2,n3,n4,n5,n6,n7,n8,n9,n0;

vecdim n;

 g(l,n1,n1);
 g(l,n1,m1,n1,m1);

 g(l,n1,n2,n2,n1);
 g(l,n1,n2,m1,n2,n1,m1);
 g(l,n1,n2,m1,m2,n2,n1,m2,m1);

 g(l,n1,n2,n3,n3,n2,n1);
 g(l,n1,n2,n3,m1,n3,n2,n1,m1);
 g(l,n1,n2,n3,m1,m2,n3,n2,n1,m2,m1);
 g(l,n1,n2,n3,m1,m2,m3,n3,n2,n1,m3,m2,m1);
 g(l,n1,n2,n3,m1,n3,n1,n2,m1);
 g(l,n1,n2,n3,m1,m2,n3,n1,n2,m1,m2);
 g(l,n1,n2,n3,m1,m2,m3,n2,n3,n1,m3,m1,m2);


% COPYRIGHT (C) 1988,1990, INSTITUTE OF NUCLEAR PHYSICS, MOSCOW STATE U.
% CVITWEEL  TEST OF CVITANOVIC PACKAGE
% AUTHOR    A. KRYUKOV, ARODIONOV, A.TARANOV
% VERSION   1.2
% RELEASE   11-MAR-90

index j1,j2,j3,j4,j5,j6,j7,j8,j9,j0;
vecdim n$

%       Test of CVITANOVIC PACKAGE
%
%       All tests are the weels with defferent spoke
%       (Use notation from SIGSAM Bull, 1989, v.23, no.4, pp.15-24)

g(l,j1,j2,j2,j1);

g(l,j1,j2,j3,j1,j2,j3);
g(l,j1,j2,j3,j1,j3,j2);
g(l,j1,j2,j3,j3,j2,j1);

g(l,j1,j2,j3,j4,j1,j2,j3,j4);
g(l,j1,j2,j3,j4,j1,j2,j4,j3);
g(l,j1,j2,j3,j4,j1,j4,j2,j3);
g(l,j1,j2,j3,j4,j1,j4,j3,j2);

g(l,j1,j2,j3,j4,j5,j1,j2,j3,j4,j5);
g(l,j1,j2,j3,j4,j5,j1,j2,j3,j5,j4);
g(l,j1,j2,j3,j4,j5,j1,j2,j5,j3,j4);
g(l,j1,j2,j3,j4,j5,j1,j2,j5,j4,j3);

g(l,j1,j2,j3,j4,j5,j6,j1,j2,j3,j4,j5,j6);
g(l,j1,j2,j3,j4,j5,j6,j1,j2,j3,j4,j6,j5);
g(l,j1,j2,j3,j4,j5,j6,j1,j2,j4,j3,j6,j5);
g(l,j1,j2,j3,j4,j5,j6,j1,j2,j6,j3,j4,j5);
g(l,j1,j2,j3,j4,j5,j6,j7,j1,j2,j3,j4,j5,j6,j7);
g(l,j1,j2,j3,j4,j5,j6,j7,j1,j2,j3,j4,j5,j7,j6);
g(l,j1,j2,j3,j4,j5,j6,j7,j2,j1,j3,j4,j5,j6,j7);

%  Test of example that calculated incorrectly in earlier package.

index ix,iy,iz;
mass p1=mm, p2=mm, p3=mm, p4=mm, k1=0; 
mshell p1,p2,p3,p4,k1;
vector q1,q2; 
operator ga,gb;
for all p let ga(p)=g(la,p) + mm,
              gb(p)=g(lb,p) + mm;
xx := g(la,ix)*g(la,iy)*(g(lb,ix)*gb(p1)*g(lb,iy)*gb(q2) +
                      gb(p3)*g(lb,ix)*g(lb,iy));
let q1=p1-k1, q2=p3+k1;

xx;

end;
