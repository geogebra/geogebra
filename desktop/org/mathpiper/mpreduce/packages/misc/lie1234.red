module lie1234;
% n-dimensional Lie algebras up to n=4.
% Author: Carsten and Franziska Schoebel.
% e-mail: cschoeb@aix550.informatik.uni-leipzig.de .
% Copyright (c) 1993 The Leipzig University, Computer Science Dept.
% All Rights Reserved.

% Redistribution and use in source and binary forms, with or without
% modification, are permitted provided that the following conditions are met:
%
%    * Redistributions of source code must retain the relevant copyright
%      notice, this list of conditions and the following disclaimer.
%    * Redistributions in binary form must reproduce the above copyright
%      notice, this list of conditions and the following disclaimer in the
%      documentation and/or other materials provided with the distribution.
%
% THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
% AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
% THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
% PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNERS OR
% CONTRIBUTORS
% BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
% CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
% SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
% INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
% CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
% ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
% POSSIBILITY OF SUCH DAMAGE.
%


algebraic;
operator liealg,comtab;

algebraic procedure lieclass(dim);
begin
 if not(dim=1 or dim=2 or dim=3 or dim=4) then
  symbolic rederr "dimension out of range";
 symbolic(if gettype 'liestrin neq 'ARRAY then
          rederr "liestrin not ARRAY");
 if length liestrin neq {dim+1,dim+1,dim+1} then
  symbolic rederr "dimension of liestrin out of range";
 if dim=1 then <<if symbolic !*tr_lie then
                 write "one-dimensional Lie algebra";
                 lie_class:={liealg(1),comtab(0)}>> else
     if dim=2 then lie2(liestrin(1,2,1),liestrin(1,2,2)) else
      if dim=3 then <<matrix lie3_ff(3,3);
                      for i:=1:3 do <<lie3_ff(1,i):=liestrin(1,2,i);
                                      lie3_ff(2,i):=liestrin(1,3,i);
                                      lie3_ff(3,i):=liestrin(2,3,i)>>;
                      lie3(lie3_ff);clear lie3_ff>> else
      <<array cc(4,4,4);
        for i:=1:4 do for j:=1:4 do for k:=1:4 do
         cc(i,j,k):=liestrin(i,j,k);
        lie4();clear cc>>;return lie_class
end;

algebraic procedure lie2(f,g);
BEGIN
  IF G=0 THEN
   IF F=0 THEN liemat:=MAT((1,0),(0,1))
   ELSE liemat:=MAT((0,-1/F),(F,0))
  ELSE liemat:=MAT((1/G,0),(F,G));
  IF (F=0 AND G=0) THEN <<if symbolic !*tr_lie then
   WRITE "The given Lie algebra is commutative";
   lie_class:={liealg(2),comtab(0)}>>
  ELSE <<if symbolic !*tr_lie then
         write "[X,Y]=Y";lie_class:={liealg(2),comtab(1)}>>
END;

algebraic procedure lie3(ff);
BEGIN
      MATRIX liemat(3,3),l_f(3,3);
      ARRAY l_jj(3);
  l_f:=ff;
  FOR N:=1:3 DO
      l_jj(N):=l_f(1,N)*(-l_f(2,1)-l_f(3,2))+
            l_f(2,N)*(l_f(1,1)-l_f(3,3))+
            l_f(3,N)*(l_f(1,2)+l_f(2,3));
  IF NOT(l_jj(1)=0 AND l_jj(2)=0 AND l_jj(3)=0) THEN
     <<clear lie3_ff,liemat,l_f,l_jj;
       symbolic rederr "not a Lie algebra">>;
    IF l_f=MAT((0,0,0),(0,0,0),(0,0,0)) THEN
    <<if symbolic !*tr_lie then WRITE "Your Lie algebra is commutative";
         lie_class:={liealg(3),comtab(0)};liemat:=liemat**0>> ELSE
      IF DET(l_f) NEQ 0 THEN com3(ff) ELSE
        IF independ(1,2,ff)=1 THEN com2(ff,1,2) ELSE
         IF independ(1,3,ff)=1 THEN com2(ff,1,3) ELSE
          IF independ(2,3,ff)=1 THEN com2(ff,2,3) ELSE
           com1(ff);
CLEAR l_jj,l_f
END;


algebraic procedure independ(I,J,F0);
BEGIN MATRIX F1(3,3);
  F1:=F0;
  IF (F1(I,1)*F1(J,2)-F1(I,2)*F1(J,1)=0 AND
      F1(I,2)*F1(J,3)-F1(I,3)*F1(J,2)=0 AND
      F1(I,1)*F1(J,3)-F1(I,3)*F1(J,1)=0) THEN RETURN 0
  ELSE RETURN 1
END;

algebraic procedure com1(F2);
BEGIN
      SCALAR ALPHA,AA,BB;
      INTEGER R,I,J,M,N,Z1;
      MATRIX F3(3,3);
      ARRAY l_C(3,3,3);
  F3:=F2;
  FOR M:=3 STEP -1 UNTIL 1 DO
   FOR N:=3 STEP -1 UNTIL 1 DO
   IF F3(M,N) NEQ 0 THEN I:=M;
  IF I=1 THEN <<I:=1;J:=2>> ELSE
   IF I=2 THEN <<I:=1;J:=3>> ELSE <<I:=2;J:=3>>;
  FOR K:=1:3 DO
   <<l_C(1,2,K):=F3(1,K);l_C(2,1,K):=-F3(1,K);
     l_C(1,3,K):=F3(2,K);l_C(3,1,K):=-F3(2,K);
     l_C(2,3,K):=F3(3,K);l_C(3,2,K):=-F3(3,K)>>;
  Z1:=0;
  FOR U:=3 STEP -1 UNTIL 1 DO
   FOR V:=3 STEP -1 UNTIL 1 DO
   IF l_C(I,J,1)*l_C(V,1,U)+l_C(I,J,2)*l_C(V,2,U)+
   l_C(I,J,3)*l_C(V,3,U) NEQ 0
  THEN <<M:=U;N:=V;Z1:=1>>;
  IF Z1=0 THEN
    <<A1:=MAT((1,0,0),(0,1,0),(l_C(1,2,1),l_C(1,2,2),l_C(1,2,3)));
      A2:=MAT((1,0,0),(0,0,1),(l_C(1,3,1),l_C(1,3,2),l_C(1,3,3)));
      A3:=MAT((0,1,0),(0,0,1),(l_C(2,3,1),l_C(2,3,2),l_C(2,3,3)));
      IF DET(A1) NEQ 0 THEN liemat:=A1 ELSE
       IF DET(A2) NEQ 0 THEN liemat:=A2 ELSE liemat:=A3;
      if symbolic !*tr_lie then
      WRITE "[X,Y]=Z";lie_class:={liealg(3),comtab(1)}>> ELSE
    <<ALPHA:=(l_C(I,J,1)*l_C(N,1,M)+l_C(I,J,2)*l_C(N,2,M)+
              l_C(I,J,3)*l_C(N,3,M))/l_C(I,J,M);
      A1:=MAT((0,0,0),(0,0,0),(l_C(I,J,1),l_C(I,J,2),l_C(I,J,3)));
      A1(1,N):=1/ALPHA;A1(2,1):=1;
      IF DET(A1) NEQ 0 THEN R:=1 ELSE
      <<A1(2,1):=0;A1(2,2):=1;
        IF DET(A1) NEQ 0 THEN R:=2 ELSE
        <<A1(2,2):=0;A1(2,3):=1;R:=3>>>>;
      AA:=l_C(N,R,M)/(ALPHA*l_C(I,J,M));
      BB:=(l_C(I,J,1)*l_C(R,1,M)+l_C(I,J,2)*l_C(R,2,M)+
           l_C(I,J,3)*l_C(R,3,M))/l_C(I,J,M);
      IF AA=0 THEN liemat:=MAT((1,0,0),(-BB,1,0),(0,0,1))*A1 ELSE
        liemat:=MAT((1,0,0),(BB/AA,-1/AA,1),(0,0,1))*A1;
      if symbolic !*tr_lie then
      WRITE "[X,Z]=Z";lie_class:={liealg(3),comtab(2)}>>;
  CLEAR A1,A2,A3,l_C,F3
END;

algebraic procedure com2(F2,M,N);
BEGIN SCALAR Z1,ALPHA,ALPHA1,ALPHA2,BETA,BETA1,BETA2;
      MATRIX F3(3,3);
  F3:=F2;
  A1:=MAT((F3(M,1),F3(M,2),F3(M,3)),
          (F3(N,1),F3(N,2),F3(N,3)),(0,0,0));
  A1(3,1):=1;Z1:=DET(A1);
  IF Z1 NEQ 0 THEN
   <<ALPHA1:=(-F3(N,3)*(F3(M,2)*F3(1,2)+F3(M,3)*F3(2,2))+
               F3(N,2)*(F3(M,2)*F3(1,3)+F3(M,3)*F3(2,3)))/Z1;
     ALPHA2:=(-F3(N,3)*(F3(N,2)*F3(1,2)+F3(N,3)*F3(2,2))+
               F3(N,2)*(F3(N,2)*F3(1,3)+F3(N,3)*F3(2,3)))/Z1;
     BETA1:=(F3(M,3)*(F3(M,2)*F3(1,2)+F3(M,3)*F3(2,2))-
             F3(M,2)*(F3(M,2)*F3(1,3)+F3(M,3)*F3(2,3)))/Z1;
     BETA2:=(F3(M,3)*(F3(N,2)*F3(1,2)+F3(N,3)*F3(2,2))-
             F3(M,2)*(F3(N,2)*F3(1,3)+F3(N,3)*F3(2,3)))/Z1>>
   ELSE
   <<A1(3,1):=0;A1(3,2):=1;Z1:=DET(A1);
   IF Z1 NEQ 0 THEN
    <<ALPHA1:=(-F3(N,3)*(F3(M,1)*F3(1,1)-F3(M,3)*F3(3,1))+
                F3(N,1)*(F3(M,1)*F3(1,3)-F3(M,3)*F3(3,3)))/Z1;
      ALPHA2:=(-F3(N,3)*(F3(N,1)*F3(1,1)-F3(N,3)*F3(3,1))+
                F3(N,1)*(F3(N,1)*F3(1,3)-F3(N,3)*F3(3,3)))/Z1;
      BETA1:=(F3(M,3)*(F3(M,1)*F3(1,1)-F3(M,3)*F3(3,1))-
              F3(M,1)*(F3(M,1)*F3(1,3)-F3(M,3)*F3(3,3)))/Z1;
      BETA2:=(F3(M,3)*(F3(N,1)*F3(1,1)-F3(N,3)*F3(3,1))-
              F3(M,1)*(F3(N,1)*F3(1,3)-F3(N,3)*F3(3,3)))/Z1>>
   ELSE
   <<A1(3,2):=0;A1(3,3):=1;Z1:=DET(A1);
      ALPHA1:=(F3(N,2)*(F3(M,1)*F3(2,1)+F3(M,2)*F3(3,1))-
               F3(N,1)*(F3(M,1)*F3(2,2)+F3(M,2)*F3(3,2)))/Z1;
      ALPHA2:=(F3(N,2)*(F3(N,1)*F3(2,1)+F3(N,2)*F3(3,1))-
               F3(N,1)*(F3(N,1)*F3(2,2)+F3(N,2)*F3(3,2)))/Z1;
      BETA1:=(-F3(M,2)*(F3(M,1)*F3(2,1)+F3(M,2)*F3(3,1))+
               F3(M,1)*(F3(M,1)*F3(2,2)+F3(M,2)*F3(3,2)))/Z1;
      BETA2:=(-F3(M,2)*(F3(N,1)*F3(2,1)+F3(N,2)*F3(3,1))+
               F3(M,1)*(F3(N,1)*F3(2,2)+F3(N,2)*F3(3,2)))/Z1>>>>;
  IF (ALPHA2=0 AND BETA1=0 AND ALPHA1=BETA2) THEN
   <<liemat:=MAT((1,0,0),(0,1,0),(0,0,1/ALPHA1))*A1;
     if symbolic !*tr_lie then
     WRITE "[X,Z]=X, [Y,Z]=Y";lie_class:={liealg(3),comtab(3)}>> ELSE
   <<IF ALPHA2 NEQ 0 THEN
     <<ALPHA:=ALPHA1+BETA2;BETA:=ALPHA2*BETA1-ALPHA1*BETA2;
       A2:=MAT((0,BETA1-ALPHA1*BETA2/ALPHA2,0),
               (1,-ALPHA1/ALPHA2,0),(0,0,1))>> ELSE
      IF BETA1 NEQ 0 THEN
       <<ALPHA:=1+ALPHA1/BETA2;BETA:=-ALPHA1/BETA2;
         A2:=MAT((-ALPHA1*BETA2/BETA1,0,0),
                 (-(BETA2**2)/BETA1,BETA2,0),(0,0,1/BETA2))>> ELSE
        <<ALPHA:=ALPHA1+BETA2;BETA:=-ALPHA1*BETA2;
          A2:=MAT((1,1,0),(1/ALPHA1,1/BETA2,0),(0,0,1))>>;
     IF ALPHA=0 THEN
      <<liemat:=MAT((1,0,0),(0,SQRT(ABS(BETA)),0),
               (0,0,1/SQRT(ABS(BETA))))*A2*A1;
        if symbolic !*tr_lie then
        WRITE "[X,Z]=",BETA/ABS(BETA),"Y, [Y,Z]=X";
        if BETA>0 then lie_class:={liealg(3),comtab(4)} else
                       lie_class:={liealg(3),comtab(5)}>>
                 ELSE
      <<liemat:=MAT((1,0,0),(0,-ALPHA,0),(0,0,-1/ALPHA))*A2*A1;
        if symbolic !*tr_lie then
        WRITE "[X,Z]=-X+",BETA/(ALPHA**2),"Y, [Y,Z]=X";
        lie_class:={liealg(3),comtab(6),BETA/(ALPHA**2)}>>>>;
  CLEAR A1,A2,F3
END;

algebraic procedure com3(F2);
BEGIN MATRIX l_K(3,3),F3(3,3);
  F3:=F2;
  l_K(1,1):=F3(1,2)**2+2*F3(1,3)*F3(2,2)+F3(2,3)**2;
  l_K(1,2):=-F3(1,1)*F3(1,2)+F3(1,3)*F3(3,2)-
           F3(2,1)*F3(1,3)+F3(2,3)*F3(3,3);
  l_K(1,3):=-F3(1,1)*F3(2,2)-F3(1,2)*F3(3,2)-
           F3(2,1)*F3(2,3)-F3(2,2)*F3(3,3);
  l_K(2,1):=l_K(1,2);
  l_K(2,2):=F3(1,1)**2-2*F3(1,3)*F3(3,1)+F3(3,3)**2;
  l_K(2,3):=F3(1,1)*F3(2,1)+F3(1,2)*F3(3,1)-
          F3(3,1)*F3(2,3)-F3(3,2)*F3(3,3);
  l_K(3,1):=l_K(1,3);
  l_K(3,2):=l_K(2,3);
  l_K(3,3):=F3(2,1)**2+2*F3(2,2)*F3(3,1)+F3(3,2)**2;
  IF NOT(NUMBERP(l_K(1,1)) AND
   NUMBERP(l_K(1,1)*l_K(2,2)-l_K(1,2)*l_K(2,1)) AND
         NUMBERP(DET(l_K))) THEN
  <<WRITE "Is ",-l_K(1,1),">0 and ",
     l_K(1,1)*l_K(2,2)-l_K(1,2)*l_K(2,1),">0 and ",
      -DET(l_K),">0 ? (y/n) and press <RETURN>";
    HE:=SYMBOLIC READ();
    IF HE=y THEN so3(F2) ELSE so21(F2)>> ELSE
  IF (-l_K(1,1)>0 AND l_K(1,1)*l_K(2,2)-l_K(1,2)*l_K(2,1)>0 AND
      -DET(l_K)>0) THEN so3(F2) ELSE so21(F2);
  CLEAR l_K,F3
END;

algebraic procedure so3(F4);
BEGIN SCALAR S,TT,Q,R,ALPHA;
      MATRIX F5(3,3);
  F5:=F4;
  S:=F5(2,2)/ABS(F5(2,2));
  TT:=ABS(F5(1,2)**2+F5(1,3)*F5(2,2));
  R:=F5(1,1)-F5(1,2)*F5(2,1)/F5(2,2);
  ALPHA:=TT*(-R*R-((F5(2,1)/F5(2,2))**2+F5(3,1)/F5(2,2))*TT);
  Q:=1/SQRT(ALPHA);
  liemat(1,1):=1/(S*SQRT(TT));
  liemat(1,2):=0;
  liemat(1,3):=0;
  liemat(2,1):=Q*R;
  liemat(2,2):=0;
  liemat(2,3):=-Q*TT/F5(2,2);
  liemat(3,1):=-Q*S*SQRT(TT)*F5(2,1)/F5(2,2);
  liemat(3,2):=-Q*S*SQRT(TT);
  liemat(3,3):=Q*S*SQRT(TT)*F5(1,2)/F5(2,2);
  if symbolic !*tr_lie then
  WRITE "[X,Y]=Z, [X,Z]=-Y, [Y,Z]=X";lie_class:={liealg(3),comtab(7)};
  CLEAR F5;
END;

algebraic procedure so21(F4);
BEGIN SCALAR GAM,EPS,S,TT,Q,R,ALPHA;
      MATRIX l_G(3,3),F5(3,3);
  F5:=F4;
  liemat:=MAT((1,0,0),(0,1,0),(0,0,1));
  IF F5(2,2)=0 THEN
   IF F5(1,3) NEQ 0 THEN <<liemat:=MAT((1,0,0),(0,0,1),(0,1,0));
    l_G(1,1):=F5(2,1);l_G(1,2):=F5(2,3);l_G(1,3):=F5(2,2);
    l_G(2,1):=F5(1,1);l_G(2,2):=F5(1,3);l_G(2,3):=F5(1,2);
    l_G(3,1):=-F5(3,1);l_G(3,2):=-F5(3,3);l_G(3,3):=-F5(3,2);
    F5:=l_G>> ELSE
    IF F5(3,1) NEQ 0 THEN <<liemat:=MAT((0,1,0),(1,0,0),(0,0,1));
     l_G(1,1):=-F5(1,2);l_G(1,2):=-F5(1,1);l_G(1,3):=-F5(1,3);
     l_G(2,1):=F5(3,2);l_G(2,2):=F5(3,1);l_G(2,3):=F5(3,3);
     l_G(3,1):=F5(2,2);l_G(3,2):=F5(2,1);l_G(3,3):=F5(2,3);
     F5:=l_G>> ELSE
      <<liemat:=MAT((1,0,1),(1,0,0),(0,1,0));
        l_G(1,1):=-F5(2,3);l_G(1,2):=F5(2,3)-F5(2,1);l_G(1,3):=0;
        l_G(2,1):=-F5(3,3);l_G(2,2):=2*F5(1,1);
        l_G(2,3):=F5(1,2)-F5(3,2);
        l_G(3,1):=0;l_G(3,2):=F5(1,1);l_G(3,3):=F5(1,2);
        F5:=l_G>>;
  IF F5(1,2)**2+F5(1,3)*F5(2,2)=0 THEN
   <<GAM:=-F5(1,2)/F5(2,2);EPS:=F5(1,1)-F5(1,2)*F5(2,1)/F5(2,2);
     IF 1/4*(F5(3,2)**2+F5(3,1)*F5(2,2))-EPS*F5(2,2)/2=0 THEN
      <<liemat:=MAT((0,0,1),(0,2/EPS,2*GAM/EPS),(1,0,0))*liemat;
        l_G(1,1):=2*GAM*F5(3,2)/EPS-F5(3,3);
        l_G(1,2):=-F5(3,2);l_G(1,3):=-2*F5(3,1)/EPS;
        l_G(2,1):=0;l_G(2,2):=-EPS*F5(2,2)/2;l_G(2,3):=-F5(2,1);
        l_G(3,1):=0;l_G(3,2):=0;l_G(3,3):=-2;F5:=l_G>> ELSE
      <<liemat:=MAT((1/2,0,1/2),(0,1/EPS,GAM/EPS),(-1/2,0,1/2))*liemat;
        l_G(1,1):=-F5(3,1)/(2*EPS);l_G(1,2):=-F5(3,2)/2;
        l_G(1,3):=F5(3,1)/(2*EPS)-1;
        l_G(2,1):=F5(2,1)/2;l_G(2,2):=F5(2,2)*EPS/2;
        l_G(2,3):=-F5(2,1)/2;l_G(3,1):=F5(3,1)/(2*EPS)+1;
        l_G(3,2):=F5(3,2)/2;l_G(3,3):=-F5(3,1)/(2*EPS);F5:=l_G>>>>;
  IF NOT(NUMBERP(F5(1,2)**2+F5(1,3)*F5(2,2))) THEN
 <<WRITE "Is ",F5(1,2)**2+F5(1,3)*F5(2,2),
         "<0 ? (y/n) and press <RETURN>";
    HE:=SYMBOLIC READ();
    IF HE=y THEN
    <<S:=F5(2,2)/ABS(F5(2,2));
     TT:=ABS(F5(1,2)**2+F5(1,3)*F5(2,2));
     R:=F5(1,1)-F5(1,2)*F5(2,1)/F5(2,2);
     ALPHA:=TT*(-R*R-((F5(2,1)/F5(2,2))**2+F5(3,1)/F5(2,2))*TT);
     Q:=1/SQRT(ABS(ALPHA));
     l_G(1,1):=-Q*S*SQRT(TT)*F5(2,1)/F5(2,2);
     l_G(1,2):=-Q*S*SQRT(TT);
     l_G(1,3):=Q*S*SQRT(TT)*F5(1,2)/F5(2,2);
     l_G(2,1):=1/(S*SQRT(TT));
     l_G(2,2):=0;
     l_G(2,3):=0;
     l_G(3,1):=Q*R;
     l_G(3,2):=0;
     l_G(3,3):=-Q*TT/F5(2,2);
     liemat:=l_G*liemat>> ELSE
   <<S:=F5(2,2)/ABS(F5(2,2));
     TT:=F5(1,2)**2+F5(1,3)*F5(2,2);
     R:=F5(1,1)-F5(1,2)*F5(2,1)/F5(2,2);
     ALPHA:=TT*(R*R-((F5(2,1)/F5(2,2))**2+F5(3,1)/F5(2,2))*TT);
     Q:=1/SQRT(ABS(ALPHA));
     IF NOT(NUMBERP(ALPHA)) THEN
     <<WRITE "Is ",ALPHA,">0 ? (y/n) and press <RETURN>";
       HE:=SYMBOLIC READ();
       IF HE=y THEN
       <<l_G(1,1):=1/(S*SQRT(TT));
         l_G(1,2):=0;
         l_G(1,3):=0;
         l_G(2,1):=Q*R;
         l_G(2,2):=0;
         l_G(2,3):=Q*TT/F5(2,2);
         l_G(3,1):=Q*S*SQRT(TT)*F5(2,1)/F5(2,2);
         l_G(3,2):=Q*S*SQRT(TT);
         l_G(3,3):=-Q*S*SQRT(TT)*F5(1,2)/F5(2,2);
         liemat:=l_G*liemat>> ELSE
       <<l_G(1,1):=1/(S*SQRT(TT));
         l_G(1,2):=0;
         l_G(1,3):=0;
         l_G(2,1):=Q*S*SQRT(TT)*F5(2,1)/F5(2,2);
         l_G(2,2):=Q*S*SQRT(TT);
         l_G(2,3):=-Q*S*SQRT(TT)*F5(1,2)/F5(2,2);
         l_G(3,1):=Q*R;
         l_G(3,2):=0;
         l_G(3,3):=Q*TT/F5(2,2);
         liemat:=l_G*liemat>>>> ELSE
     IF ALPHA>0 THEN
      <<l_G(1,1):=1/(S*SQRT(TT));
        l_G(1,2):=0;
        l_G(1,3):=0;
        l_G(2,1):=Q*R;
        l_G(2,2):=0;
        l_G(2,3):=Q*TT/F5(2,2);
        l_G(3,1):=Q*S*SQRT(TT)*F5(2,1)/F5(2,2);
        l_G(3,2):=Q*S*SQRT(TT);
        l_G(3,3):=-Q*S*SQRT(TT)*F5(1,2)/F5(2,2);
        liemat:=l_G*liemat>> ELSE
      <<l_G(1,1):=1/(S*SQRT(TT));
        l_G(1,2):=0;
        l_G(1,3):=0;
        l_G(2,1):=Q*S*SQRT(TT)*F5(2,1)/F5(2,2);
        l_G(2,2):=Q*S*SQRT(TT);
        l_G(2,3):=-Q*S*SQRT(TT)*F5(1,2)/F5(2,2);
        l_G(3,1):=Q*R;
        l_G(3,2):=0;
        l_G(3,3):=Q*TT/F5(2,2);
        liemat:=l_G*liemat>>>>>> ELSE
  IF F5(1,2)**2+F5(1,3)*F5(2,2)<0 THEN
   <<S:=F5(2,2)/ABS(F5(2,2));
     TT:=ABS(F5(1,2)**2+F5(1,3)*F5(2,2));
     R:=F5(1,1)-F5(1,2)*F5(2,1)/F5(2,2);
     ALPHA:=TT*(-R*R-((F5(2,1)/F5(2,2))**2+F5(3,1)/F5(2,2))*TT);
     Q:=1/SQRT(ABS(ALPHA));
     l_G(1,1):=-Q*S*SQRT(TT)*F5(2,1)/F5(2,2);
     l_G(1,2):=-Q*S*SQRT(TT);
     l_G(1,3):=Q*S*SQRT(TT)*F5(1,2)/F5(2,2);
     l_G(2,1):=1/(S*SQRT(TT));
     l_G(2,2):=0;
     l_G(2,3):=0;
     l_G(3,1):=Q*R;
     l_G(3,2):=0;
     l_G(3,3):=-Q*TT/F5(2,2);
     liemat:=l_G*liemat>> ELSE
   <<S:=F5(2,2)/ABS(F5(2,2));
     TT:=F5(1,2)**2+F5(1,3)*F5(2,2);
     R:=F5(1,1)-F5(1,2)*F5(2,1)/F5(2,2);
     ALPHA:=TT*(R*R-((F5(2,1)/F5(2,2))**2+F5(3,1)/F5(2,2))*TT);
     Q:=1/SQRT(ABS(ALPHA));
     IF NOT(NUMBERP(ALPHA)) THEN
     <<WRITE "Is ",ALPHA,">0 ? (y/n) and press <RETURN>";
       HE:=SYMBOLIC READ();
       IF HE=y THEN
       <<l_G(1,1):=1/(S*SQRT(TT));
         l_G(1,2):=0;
         l_G(1,3):=0;
         l_G(2,1):=Q*R;
         l_G(2,2):=0;
         l_G(2,3):=Q*TT/F5(2,2);
         l_G(3,1):=Q*S*SQRT(TT)*F5(2,1)/F5(2,2);
         l_G(3,2):=Q*S*SQRT(TT);
         l_G(3,3):=-Q*S*SQRT(TT)*F5(1,2)/F5(2,2);
         liemat:=l_G*liemat>> ELSE
       <<l_G(1,1):=1/(S*SQRT(TT));
         l_G(1,2):=0;
         l_G(1,3):=0;
         l_G(2,1):=Q*S*SQRT(TT)*F5(2,1)/F5(2,2);
         l_G(2,2):=Q*S*SQRT(TT);
         l_G(2,3):=-Q*S*SQRT(TT)*F5(1,2)/F5(2,2);
         l_G(3,1):=Q*R;
         l_G(3,2):=0;
         l_G(3,3):=Q*TT/F5(2,2);
         liemat:=l_G*liemat>>>> ELSE
     IF ALPHA>0 THEN
      <<l_G(1,1):=1/(S*SQRT(TT));
        l_G(1,2):=0;
        l_G(1,3):=0;
        l_G(2,1):=Q*R;
        l_G(2,2):=0;
        l_G(2,3):=Q*TT/F5(2,2);
        l_G(3,1):=Q*S*SQRT(TT)*F5(2,1)/F5(2,2);
        l_G(3,2):=Q*S*SQRT(TT);
        l_G(3,3):=-Q*S*SQRT(TT)*F5(1,2)/F5(2,2);
        liemat:=l_G*liemat>> ELSE
      <<l_G(1,1):=1/(S*SQRT(TT));
        l_G(1,2):=0;
        l_G(1,3):=0;
        l_G(2,1):=Q*S*SQRT(TT)*F5(2,1)/F5(2,2);
        l_G(2,2):=Q*S*SQRT(TT);
        l_G(2,3):=-Q*S*SQRT(TT)*F5(1,2)/F5(2,2);
        l_G(3,1):=Q*R;
        l_G(3,2):=0;
        l_G(3,3):=Q*TT/F5(2,2);
        liemat:=l_G*liemat>>>>;
  if symbolic !*tr_lie then
  WRITE "[X,Y]=Z, [X,Z]=Y, [Y,Z]=X";lie_class:={liealg(3),comtab(8)};
  CLEAR l_G,F5
END;

algebraic procedure lie4();
BEGIN
     SCALAR LAM,JAC1,JAC2,JAC3,JAC4;
     INTEGER P1,M1,M2,M3,DIML1;
     MATRIX l_F(6,4);
     ARRAY ORDV(12);
 ORDV(1):=ORDV(3):=ORDV(7):=1;ORDV(2):=ORDV(5):=ORDV(9):=2;
 ORDV(4):=ORDV(6):=ORDV(11):=3;ORDV(8):=ORDV(10):=ORDV(12):=4;
 FOR I:=1:4 DO
  <<l_F(1,I):=CC(1,2,I);l_F(2,I):=CC(1,3,I);l_F(3,I):=CC(2,3,I);
    l_F(4,I):=CC(1,4,I);l_F(5,I):=CC(2,4,I);l_F(6,I):=CC(3,4,I);
    CC(1,1,I):=CC(2,2,I):=CC(3,3,I):=CC(4,4,I):=0;
    CC(2,1,I):=-l_F(1,I);CC(3,1,I):=-l_F(2,I);CC(3,2,I):=-l_F(3,I);
    CC(4,1,I):=-l_F(4,I);CC(4,2,I):=-l_F(5,I);CC(4,3,I):=-l_F(6,I)>>;
 FOR S:=1:4 DO
 <<JAC1:=FOR R:=1:4 SUM
    CC(1,2,R)*CC(R,3,S)+CC(2,3,R)*CC(R,1,S)+CC(3,1,R)*CC(R,2,S);
   JAC2:=FOR R:=1:4 SUM
    CC(1,2,R)*CC(R,4,S)+CC(2,4,R)*CC(R,1,S)+CC(4,1,R)*CC(R,2,S);
   JAC3:=FOR R:=1:4 SUM
    CC(1,3,R)*CC(R,4,S)+CC(3,4,R)*CC(R,1,S)+CC(4,1,R)*CC(R,3,S);
   JAC4:=FOR R:=1:4 SUM
    CC(2,3,R)*CC(R,4,S)+CC(3,4,R)*CC(R,2,S)+CC(4,2,R)*CC(R,3,S);
   IF (JAC1 NEQ 0 OR JAC2 NEQ 0 OR JAC3 NEQ 0 OR JAC4 NEQ 0 ) THEN
    S:=4>>;
 IF (JAC1 NEQ 0 OR JAC2 NEQ 0 OR JAC3 NEQ 0 OR JAC4 NEQ 0 )THEN
  <<clear l_F,ORDV,CC;symbolic rederr "not a Lie algebra">>;
 M1:=0;
 FOR S:=1:6 DO
  FOR TT:=1:4 DO
   IF l_F(S,TT) NEQ 0 THEN <<M1:=S;P1:=TT;S:=6;TT:=4>>;
 IF M1=0 THEN DIML1:=0 ELSE
  IF M1=6 THEN DIML1:=1 ELSE
  <<M2:=0;
    FOR S:=M1+1:6 DO
     <<LAM:=l_F(S,P1)/l_F(M1,P1);
       FOR TT:=1:4 DO
       IF l_F(S,TT) NEQ LAM*l_F(M1,TT) THEN <<M2:=S;S:=6;TT:=4>>>>;
       IF M2=0 THEN DIML1:=1 ELSE
     IF M2=6 THEN DIML1:=2 ELSE
     <<M3:=0;
       FOR S:=M2+1:6 DO
       IF NOT(DET(MAT((l_F(M1,2),l_F(M1,3),l_F(M1,4)),
                      (l_F(M2,2),l_F(M2,3),l_F(M2,4)),
                      (l_F(S,2),l_F(S,3),l_F(S,4))))=0 AND
              DET(MAT((l_F(M1,1),l_F(M1,3),l_F(M1,4)),
                      (l_F(M2,1),l_F(M2,3),l_F(M2,4)),
                      (l_F(S,1),l_F(S,3),l_F(S,4))))=0 AND
              DET(MAT((l_F(M1,1),l_F(M1,2),l_F(M1,4)),
                      (l_F(M2,1),l_F(M2,2),l_F(M2,4)),
                      (l_F(S,1),l_F(S,2),l_F(S,4))))=0 AND
              DET(MAT((l_F(M1,1),l_F(M1,2),l_F(M1,3)),
                      (l_F(M2,1),l_F(M2,2),l_F(M2,3)),
                      (l_F(S,1),l_F(S,2),l_F(S,3))))=0)
        THEN <<M3:=S;S:=6>>;
       IF M3=0 THEN DIML1:=2 ELSE DIML1:=3>>>>;
   IF DIML1=0 THEN
    <<if symbolic !*tr_lie then WRITE "Your Lie algebra is commutative";
      lie_class:={liealg(4),comtab(0)};
      liemat:=mat((1,0,0,0),(0,1,0,0),(0,0,1,0),(0,0,0,1))>> ELSE
    IF DIML1=3 THEN
    com43(ORDV(2*M1-1),ORDV(2*M1),ORDV(2*M2-1),ORDV(2*M2),
            ORDV(2*M3-1),ORDV(2*M3)) ELSE
      IF DIML1=1 THEN
       com41(ORDV(2*M1-1),ORDV(2*M1),P1) ELSE
        com42(ORDV(2*M1-1),ORDV(2*M1),ORDV(2*M2-1),ORDV(2*M2));
 CLEAR ORDV,l_F
END;

algebraic procedure com41(I1,J1,P1);
BEGIN SCALAR Y1,Y2,Y3,BETA1,BETA2,BETA3,BETA4,BETA5,BETA6;
      MATRIX liemat(4,4);
 FOR I:=1:4 DO liemat(1,I):=CC(I1,J1,I);
 IF P1=1 THEN <<Y1:=2;Y2:=3;Y3:=4>> ELSE
  IF P1=2 THEN <<Y1:=1;Y2:=3;Y3:=4>> ELSE
   IF P1=3 THEN <<Y1:=1;Y2:=2;Y3:=4>> ELSE
    <<Y1:=1;Y2:=2;Y3:=3>>;
 liemat(2,Y1):=liemat(3,Y2):=liemat(4,Y3):=1;
 BETA1:=(FOR L:=1:4 SUM CC(I1,J1,L)*CC(L,Y1,P1))/CC(I1,J1,P1);
 BETA2:=(FOR L:=1:4 SUM CC(I1,J1,L)*CC(L,Y2,P1))/CC(I1,J1,P1);
 BETA3:=CC(Y1,Y2,P1)/CC(I1,J1,P1);
 BETA4:=(FOR L:=1:4 SUM CC(I1,J1,L)*CC(L,Y3,P1))/CC(I1,J1,P1);
 BETA5:=CC(Y1,Y3,P1)/CC(I1,J1,P1);
 BETA6:=CC(Y2,Y3,P1)/CC(I1,J1,P1);
 IF (BETA1=0 AND BETA2=0 AND BETA3=0 AND BETA4=0 AND BETA5=0) THEN
  <<liemat:=MAT((1,0,0,0),(0,0,0,1),(0,0,1,0),(0,1,0,0))*liemat;
    BETA3:=-BETA6;BETA6:=0>> ELSE
  IF (BETA1=0 AND BETA2=0 AND BETA3=0) THEN
   <<liemat:=MAT((1,0,0,0),(0,1,0,0),(0,0,0,1),(0,0,1,0))*liemat;
     BETA2:=BETA4;BETA3:=BETA5;BETA4:=BETA5:=0;BETA6:=-BETA6>>;
 IF (BETA1=0 AND BETA2=0) THEN
  <<liemat:=MAT((BETA3,0,0,0),(0,1,0,0),(0,0,1,0),(0,0,0,1))*liemat;
    Y1:=BETA4;Y2:=BETA5/BETA3;Y3:=BETA6/BETA3>> ELSE
  IF BETA1=0 THEN
   <<liemat:=MAT((1,0,0,0),(-BETA3/BETA2,1,0,0),(0,0,1/BETA2,0),
             (0,0,0,1))*liemat;Y1:=BETA4;
     Y2:=BETA5-BETA3*BETA4/BETA2;Y3:=BETA6/BETA2>> ELSE
    <<liemat:=MAT((1,0,0,0),(BETA3/BETA1,-BETA2/BETA1,1,0),
              (0,1/BETA1,0,0),(0,0,0,1))*liemat;
      Y1:=BETA4;Y2:=(BETA3*BETA4-BETA2*BETA5)/BETA1;
      Y3:=BETA5/BETA1>>;
 IF (BETA1=0 AND BETA2=0) THEN
  <<liemat:=MAT((1,0,0,0),(0,1,0,0),(0,Y3,-Y2,1),(0,0,1,0))*liemat;
    if symbolic !*tr_lie then
    WRITE "[X,Z]=W";lie_class:={liealg(4),comtab(2)}>> ELSE
  <<IF Y1=0 THEN
    liemat:=MAT((1,0,0,0),(0,1,0,0),(-Y3,0,0,-1),(0,0,1,1))*liemat ELSE
    liemat:=MAT((1,0,0,0),(0,1,0,0),(-Y3/Y1,0,1,-1/Y1),(0,0,0,1/Y1))*
    liemat;
    if symbolic !*tr_lie then
    WRITE "[W,Z]=W";lie_class:={liealg(4),comtab(1)}>>
END;


algebraic procedure com42(I1,J1,I2,J2);
BEGIN SCALAR D,D1,D2,D3,D4,A1,A2,A3,A4,A5,B1,B2,B3,B4,B5;
      MATRIX liemat(4,4);
      ARRAY SOL(1,4);
 FOR I:=1:4 DO <<liemat(1,I):=CC(I1,J1,I);liemat(2,I):=CC(I2,J2,I)>>;
 liemat(3,1):=liemat(4,2):=1;IF (D:=DET(liemat)) NEQ 0 THEN
  <<D1:=1;D2:=2;D3:=3;D4:=4>> ELSE
  <<liemat(4,2):=0;liemat(4,3):=1;IF (D:=DET(liemat)) NEQ 0 THEN
   <<D1:=1;D2:=3;D3:=2;D4:=4;D:=-D>> ELSE
   <<liemat(3,1):=0;liemat(3,2):=1;IF (D:=DET(liemat)) NEQ 0 THEN
    <<D1:=2;D2:=3;D3:=1;D4:=4>> ELSE
    <<liemat(3,2):=liemat(4,3):=0;liemat(3,1):=liemat(4,4):=1;
      IF (D:=DET(liemat)) NEQ 0 THEN
     <<D1:=1;D2:=4;D3:=2;D4:=3>> ELSE
     <<liemat(3,1):=0;liemat(3,2):=1;IF (D:=DET(liemat)) NEQ 0 THEN
      <<D1:=2;D2:=4;D3:=1;D4:=3;D:=-D>> ELSE
      <<liemat(3,2):=0;liemat(3,3):=1;D:=DET(liemat);
        D1:=3;D2:=4;D3:=1;D4:=2>>
  >>>>>>>>;
 A1:=FOR R:=1:4 SUM ( CC(I1,J1,R)*CC(R,D1,D3)*CC(I2,J2,D4)-
                      CC(I1,J1,R)*CC(R,D1,D4)*CC(I2,J2,D3))/D;
 B1:=FOR R:=1:4 SUM (-CC(I1,J1,R)*CC(R,D1,D3)*CC(I1,J1,D4)+
                      CC(I1,J1,R)*CC(R,D1,D4)*CC(I1,J1,D3))/D;
 A2:=FOR R:=1:4 SUM ( CC(I2,J2,R)*CC(R,D1,D3)*CC(I2,J2,D4)-
                      CC(I2,J2,R)*CC(R,D1,D4)*CC(I2,J2,D3))/D;
 B2:=FOR R:=1:4 SUM (-CC(I2,J2,R)*CC(R,D1,D3)*CC(I1,J1,D4)+
                      CC(I2,J2,R)*CC(R,D1,D4)*CC(I1,J1,D3))/D;
 A3:=FOR R:=1:4 SUM ( CC(I1,J1,R)*CC(R,D2,D3)*CC(I2,J2,D4)-
                      CC(I1,J1,R)*CC(R,D2,D4)*CC(I2,J2,D3))/D;
 B3:=FOR R:=1:4 SUM (-CC(I1,J1,R)*CC(R,D2,D3)*CC(I1,J1,D4)+
                      CC(I1,J1,R)*CC(R,D2,D4)*CC(I1,J1,D3))/D;
 A4:=FOR R:=1:4 SUM ( CC(I2,J2,R)*CC(R,D2,D3)*CC(I2,J2,D4)-
                      CC(I2,J2,R)*CC(R,D2,D4)*CC(I2,J2,D3))/D;
 B4:=FOR R:=1:4 SUM (-CC(I2,J2,R)*CC(R,D2,D3)*CC(I1,J1,D4)+
                      CC(I2,J2,R)*CC(R,D2,D4)*CC(I1,J1,D3))/D;
 A5:=( CC(D1,D2,D3)*CC(I2,J2,D4)-CC(D1,D2,D4)*CC(I2,J2,D3))/D;
 B5:=(-CC(D1,D2,D3)*CC(I1,J1,D4)+CC(D1,D2,D4)*CC(I1,J1,D3))/D;
 findcentre(A1,A2,A3,A4,A5,B1,B2,B3,B4,B5);
   IF NOTTRIV=0 THEN trivcent(A1,A2,A3,A4,A5,B1,B2,B3,B4,B5)
  ELSE
  IF (SOL(1,3)=0 AND SOL(1,4)=0) THEN
   IF SOL(1,1)=0 THEN
   <<liemat:=MAT((0,1,0,0),(1,0,0,0),(0,0,1,0),(0,0,0,1))*liemat;
   centincom(B1,B3,B5,A1,A3,A5)>> ELSE
   <<liemat:=MAT((1,SOL(1,2)/SOL(1,1),0,0),(0,1,0,0),(0,0,1,0),
            (0,0,0,1))*liemat;centincom(A2,A4,A5,
     B2-SOL(1,2)/SOL(1,1)*A2,B4-SOL(1,2)/SOL(1,1)*A4,
     B5-SOL(1,2)/SOL(1,1)*A5)>> ELSE
  IF DET(MAT((1,0,0,0),(0,1,0,0),
        (SOL(1,1),SOL(1,2),SOL(1,3),SOL(1,4)),(0,0,0,1)))=0 THEN
  <<liemat:=MAT((1,0,0,0),(0,1,0,0),
        (SOL(1,1),SOL(1,2),SOL(1,3),SOL(1,4)),(0,0,1,0))*liemat;
  centoutcom(A1,A2,B1,B2)>> ELSE
  <<liemat:=MAT((1,0,0,0),(0,1,0,0),
        (SOL(1,1),SOL(1,2),SOL(1,3),SOL(1,4)),(0,0,0,1))*liemat;
  centoutcom(A3,A4,B3,B4)>>;
 CLEAR SOL,NOTTRIV
END;

algebraic procedure findcentre(A1,A2,A3,A4,A5,B1,B2,B3,B4,B5);
BEGIN INTEGER FLAG;
      SCALAR HELP;
 NOTTRIV:=0;FLAG:=0;
 CENT:=MAT((A1,A2,0,-A5),(A3,A4,A5,0),(B1,B2,0,-B5),
           (B3,B4,B5,0),(0,0,A1,A3),(0,0,A2,A4),
           (0,0,B1,B3),(0,0,B2,B4));
 FOR I:=1:4 DO
  IF (CENT(I,1) NEQ 0 AND FLAG=0) THEN
  <<FLAG:=1;FOR J:=1:4 DO
   <<HELP:=CENT(1,J);CENT(1,J):=CENT(I,J);CENT(I,J):=HELP>>>>;
 IF FLAG=0 THEN <<NOTTRIV:=1;SOL(1,1):=1>> ELSE
 <<FOR I:=2:4 DO <<HELP:=CENT(I,1)/CENT(1,1);
   FOR J:=1:4 DO CENT(I,J):=CENT(I,J)-HELP*CENT(1,J)>>;
   FLAG:=0;
   FOR I:=2:4 DO
    IF (CENT(I,2) NEQ 0 AND FLAG=0) THEN
    <<FLAG:=1;FOR J:=2:4 DO
     <<HELP:=CENT(2,J);CENT(2,J):=CENT(I,J);CENT(I,J):=HELP>>>>;
   IF FLAG=0 THEN <<NOTTRIV:=1;SOL(1,1):=-CENT(1,2);
                    SOL(1,2):=CENT(1,1)>> ELSE
   <<FOR I:=3:4 DO <<HELP:=CENT(I,2)/CENT(2,2);
     FOR J:=2:4 DO CENT(I,J):=CENT(I,J)-HELP*CENT(2,J)>>;
     FLAG:=0;
     FOR I:=3:8 DO
      IF (CENT(I,3) NEQ 0 AND FLAG=0) THEN
      <<FLAG:=1;FOR J:=3:4 DO
       <<HELP:=CENT(3,J);CENT(3,J):=CENT(I,J);CENT(I,J):=HELP>>>>;
     IF FLAG=0 THEN <<NOTTRIV:=1;
      SOL(1,1):=(CENT(1,2)*CENT(2,3)/CENT(2,2)-CENT(1,3))/CENT(1,1);
      SOL(1,2):=-CENT(2,3)/CENT(2,2);SOL(1,3):=1>> ELSE
     <<FOR I:=4:8 DO <<HELP:=CENT(I,3)/CENT(3,3);
       FOR J:=3:4 DO CENT(I,J):=CENT(I,J)-HELP*CENT(3,J)>>;
       FLAG:=0;
       FOR I:=4:8 DO
        IF (CENT(I,4) NEQ 0 AND FLAG=0) THEN
        <<FLAG:=1;CENT(4,4):=CENT(I,4)>>;
       IF FLAG=0 THEN <<NOTTRIV:=1;
        SOL(1,1):=(-(CENT(2,3)*CENT(3,4)/CENT(3,3)-CENT(2,4))*
                  CENT(1,2)/CENT(2,2)+CENT(3,4)*CENT(1,3)/
                  CENT(3,3)-CENT(1,4))/CENT(1,1);
        SOL(1,2):=(CENT(2,3)*CENT(3,4)/CENT(3,3)-CENT(2,4))/
                  CENT(2,2);
        SOL(1,3):=-CENT(3,4)/CENT(3,3);SOL(1,4):=1>>
  >>>>>>;
 CLEAR CENT
END;

algebraic procedure centincom(A,C,E,B,D,F);
BEGIN SCALAR V1,W1,V2,W2;
 IF C=0 THEN IF D=0 THEN
   <<liemat:=MAT((1,0,0,0),(0,1,0,0),(0,0,0,1),(0,0,1,0))*liemat;
     V1:=A;V2:=-E;W1:=B;W2:=-F>> ELSE
     <<liemat:=MAT((1,0,0,0),(0,1,0,0),(0,0,1,-B/D),(0,0,0,1))*liemat;
       V1:=C;V2:=E;W1:=D;W2:=F>> ELSE
  <<liemat:=MAT((1,0,0,0),(0,1,0,0),(0,0,1,-A/C),(0,0,0,1))*liemat;
    V1:=C;V2:=E;W1:=D;W2:=F>>;
 IF W1=0 THEN
  <<liemat:=MAT((1,0,0,0),(0,1,0,0),(0,-V2/W2,V1/W2,0),(0,0,0,1/V1))*
  liemat;
    if symbolic !*tr_lie then
    WRITE "[X,Z]=W, [Y,Z]=X";lie_class:={liealg(4),comtab(6)}>> ELSE
  <<liemat:=MAT((1,0,0,0),(0,1,0,0),(0,-W2/(W1*V2-W2*V1),
             W1*W1/(W1*V2-W2*V1),0),(0,0,0,1/W1))*
        MAT((1,0,0,0),(V1,W1,0,0),(0,0,1,0),(0,0,0,1))*liemat;
    if symbolic !*tr_lie then
    WRITE "[X,Z]=X, [Y,Z]=W";lie_class:={liealg(4),comtab(7)}>>
END;

algebraic procedure centoutcom(A,C,B,D);
BEGIN INTEGER FLAG;
      SCALAR ALPHA,BETA;
 FLAG:=0;
 IF C NEQ 0 THEN
 <<liemat:=MAT((0,B-A*D/C,0,0),(1,-A/C,0,0),(0,0,1,0),(0,0,0,1))*liemat;
    ALPHA:=A+D;BETA:=B*C-A*D>> ELSE
  IF B NEQ 0 THEN
   <<liemat:=MAT((-A*D/B,0,0,0),(-D*D/B,D,0,0),(0,0,1,0),(0,0,0,1/D))*
   liemat;
     ALPHA:=1+A/D;BETA:=-A/D>> ELSE
   IF A NEQ D THEN
    <<liemat:=MAT((1,1,0,0),(1/A,1/D,0,0),(0,0,1,0),(0,0,0,1))*liemat;
      ALPHA:=A+D;BETA:=-A*D>> ELSE
      <<liemat:=MAT((1,0,0,0),(0,1,0,0),(0,0,1,0),(0,0,0,1/A))*liemat;
        FLAG:=1>>;
 IF FLAG=1 THEN
  <<if symbolic !*tr_lie then
    WRITE "[W,Z]=W, [X,Z]=X";lie_class:={liealg(4),comtab(10)}>> ELSE
  IF ALPHA=0 THEN
   <<liemat:=MAT((1,0,0,0),(0,SQRT(ABS(BETA)),0,0),(0,0,1,0),
             (0,0,0,1/SQRT(ABS(BETA))))*liemat;
     if symbolic !*tr_lie then
     WRITE "[W,Z]=",BETA/ABS(BETA),"X, [X,Z]=W";
     if BETA>0 then lie_class:={liealg(4),comtab(11)} else
                    lie_class:={liealg(4),comtab(8)}>> ELSE
    <<liemat:=MAT((1,0,0,0),(0,-ALPHA,0,0),(0,0,1,0),
              (0,0,0,-1/ALPHA))*liemat;
      if symbolic !*tr_lie then
      WRITE "[W,Z]=-W+",BETA/(ALPHA**2),"X, [X,Z]=W";
      lie_class:={liealg(4),comtab(9),BETA/(ALPHA**2)}>>
END;

algebraic procedure trivcent(A1,A2,A3,A4,A5,B1,B2,B3,B4,B5);
BEGIN INTEGER FLAG;
      SCALAR HE,HELP,ALPHA,BETA,C1,C2,C3,C4,C5,
             D1,D2,D3,D4,D5,P,E1,E2,E3,E4,E5,E6;
 IF (A1*B2-A2*B1)=0 THEN
  IF (A3*B4-A4*B3)=0 THEN
   <<liemat:=MAT((1,0,0,0),(0,1,0,0),(0,0,1,1),(0,0,0,1))*liemat;
     A1:=A1+A3;B1:=B1+B3;A2:=A2+A4;B2:=B2+B4>> ELSE
   <<liemat:=MAT((1,0,0,0),(0,1,0,0),(0,0,0,1),(0,0,1,0))*liemat;
     HELP:=A1;A1:=A3;A3:=HELP;HELP:=A2;A2:=A4;A4:=HELP;
     HELP:=B1;B1:=B3;B3:=HELP;HELP:=B2;B2:=B4;B4:=HELP;
     A5:=-A5;B5:=-B5>>;
 IF A2 NEQ 0 THEN <<ALPHA:=A1+B2;BETA:=A2*B1-A1*B2;
  IF ALPHA=0 THEN
   <<C1:=0;C2:=B1-A1*B2/A2;C3:=SQRT(ABS(BETA));C4:=-C3*A1/A2;
     C5:=1/C3;D1:=A1/(A2*C2);D2:=C5;D3:=1/C2;D4:=0;D5:=C3;
     IF NOT(NUMBERP(BETA)) THEN
     <<WRITE "Is ",BETA,">0 ? (y/n) and press <RETURN>";
       HE:=SYMBOLIC READ();
       IF HE=y THEN FLAG:=2 ELSE FLAG:=3>> ELSE
     IF BETA>0 THEN FLAG:=2 ELSE FLAG:=3>> ELSE
   <<C1:=0;C2:=B1-A1*B2/A2;C3:=-ALPHA;C4:=ALPHA*A1/A2;
     C5:=1/C3;D1:=A1/(A2*C2);D2:=C5;D3:=1/C2;D4:=0;D5:=C3;
     FLAG:=4;P:=BETA/(ALPHA*ALPHA)>>>> ELSE
  IF B1 NEQ 0 THEN <<ALPHA:=1+A1/B2;BETA:=-A1/B2;
   IF ALPHA=0 THEN
    <<C1:=-A1*B2/B1;C2:=0;C3:=-SQRT(ABS(BETA))*B2/B1;C4:=-C3*B1;
      C5:=1/C4;D1:=1/C1;D2:=0;D3:=-1/(A1*B2);D4:=C5;D5:=C4;
      IF NOT(NUMBERP(BETA)) THEN
     <<WRITE "Is ",BETA,">0 ? (y/n) and press <RETURN>";
       HE:=SYMBOLIC READ();
       IF HE=y THEN FLAG:=2 ELSE FLAG:=3>> ELSE
      IF BETA>0 THEN FLAG:=2 ELSE FLAG:=3>> ELSE
    <<C1:=-A1*B2/B1;C2:=0;C3:=ALPHA*B2/B1;C4:=-ALPHA*B2;
      C5:=1/C4;D1:=1/C1;D2:=0;D3:=-1/(A1*B2);D4:=C5;D5:=C4;
      FLAG:=4;P:=BETA/(ALPHA*ALPHA)>>>> ELSE
   IF A1 NEQ B2 THEN <<ALPHA:=A1+B2;BETA:=-A1*B2;
    IF ALPHA=0 THEN
     <<C1:=1;C2:=1;C3:=SQRT(ABS(BETA))/A1;C4:=SQRT(ABS(BETA))/B2;
       C5:=1/SQRT(ABS(BETA));HELP:=1/B2-1/A1;D1:=1/(B2*HELP);
       D2:=-C5/HELP;D3:=-1/(A1*HELP);D4:=-D2;D5:=1/C5;
       IF NOT(NUMBERP(BETA)) THEN
     <<WRITE "Is ",BETA,">0 ? (y/n) and press <RETURN>";
       HE:=SYMBOLIC READ();
       IF HE=y THEN FLAG:=2 ELSE FLAG:=3>> ELSE
       IF BETA>0 THEN FLAG:=2 ELSE FLAG:=3>> ELSE
     <<C1:=1;C2:=1;C3:=-ALPHA/A1;C4:=-ALPHA/B2;C5:=-1/ALPHA;
       HELP:=1/B2-1/A1;D1:=1/(B2*HELP);D2:=1/(ALPHA*HELP);
       D3:=-1/(A1*HELP);D4:=-D2;D5:=-ALPHA;
       FLAG:=4;P:=BETA/(ALPHA*ALPHA)>>>> ELSE
     <<C1:=1;C2:=0;C3:=0;C4:=1;C5:=1/A1;
       D1:=1;D2:=0;D3:=0;D4:=1;D5:=A1;FLAG:=1>>;
 liemat:=MAT((C1,C2,0,0),(C3,C4,0,0),(0,0,C5,0),(0,0,0,1))*liemat;
 E1:=D1*(C1*A3+C2*A4)+D3*(C1*B3+C2*B4);
 E2:=D2*(C1*A3+C2*A4)+D4*(C1*B3+C2*B4);
 E3:=D1*(C3*A3+C4*A4)+D3*(C3*B3+C4*B4);
 E4:=D2*(C3*A3+C4*A4)+D4*(C3*B3+C4*B4);
 E5:=C5*A5*D1+C5*B5*D3;
 E6:=C5*A5*D2+C5*B5*D4;
 IF FLAG=4 THEN
  <<liemat:=MAT((1,0,0,0),(0,1,0,0),(0,0,E1+E4,1),(0,0,1,0))*liemat;
    A1:=-E4;A2:=E1+E3+E4;A3:=-1;A4:=1;A5:=-E5;
    B1:=P*(E1+E4)+E2;B2:=E4;B3:=P;B4:=0;B5:=-E6>> ELSE
  IF FLAG=1 THEN
   IF (E1+E4=0) THEN
    <<liemat:=MAT((1,0,0,0),(0,1,0,0),(0,0,0,1),(0,0,1,0))*liemat;
      A1:=E1;A2:=E3;A3:=1;A4:=0;A5:=-E5;
      B1:=E2;B2:=E4;B3:=0;B4:=1;B5:=-E6>> ELSE
    <<liemat:=MAT((1,0,0,0),(0,1,0,0),(0,0,E1+E4,-2),(0,0,0,1))*liemat;
      A1:=E4-E1;A2:=-2*E3;A3:=E1;A4:=E3;A5:=E5*(E1+E4);
      B1:=-2*E2;B2:=E1-E4;B3:=E2;B4:=E4;B5:=E6*(E1+E4)>>;
 IF (FLAG=1 OR FLAG=4) THEN
  IF A1*B2-A2*B1=0 THEN
   IF B1=0 THEN
    <<liemat:=MAT((A2,0,0,0),(0,1,0,0),(0,0,1,0),(0,0,0,1))*liemat;
      FLAG:=5;E1:=A3;E2:=B3*A2;E3:=A4/A2;E4:=B4;E5:=A5/A2;
      E6:=B5>> ELSE
    <<liemat:=MAT((A1,B1,0,0),(1,0,0,0),(0,0,1,0),(0,0,0,1))*liemat;
      FLAG:=5;E1:=(A1*B3+B1*B4)/B1;
      E2:=A1*A3+B1*A4-A1*(A1*B3+B1*B4)/B1;E3:=B3/B1;
      E4:=A3-A1*B3/B1;E5:=B5/B1;E6:=A5-B5*A1/B1>> ELSE
   <<IF A2 NEQ 0 THEN
      <<BETA:=A2*B1-A1*B2;C1:=0;C2:=B1-A1*B2/A2;
        C3:=SQRT(ABS(BETA));C4:=-C3*A1/A2;C5:=1/C3;
        D1:=A1/(A2*C2);D2:=C5;D3:=1/C2;D4:=0;D5:=C3>> ELSE
       IF B1 NEQ 0 THEN
        <<BETA:=-A1/B2;C1:=-A1*B2/B1;C2:=0;
          C3:=-SQRT(ABS(BETA))*B2/B1;C4:=-C3*B1;C5:=1/C4;
          D1:=1/C1;D2:=0;D3:=-1/(A1*B2);D4:=C5;D5:=C4>> ELSE
        <<BETA:=-A1*B2;C1:=1;C2:=1;C3:=SQRT(ABS(BETA))/A1;
          C4:=SQRT(ABS(BETA))/B2;C5:=1/SQRT(ABS(BETA));
          HELP:=1/B2-1/A1;D1:=1/(B2*HELP);D2:=-C5/HELP;
          D3:=-1/(A1*HELP);D4:=-D2;D5:=1/C5>>;
     IF NOT(NUMBERP(BETA)) THEN
     <<WRITE "Is ",BETA,">0 ? (y/n) and press <RETURN>";
       HE:=SYMBOLIC READ();
       IF HE=y THEN FLAG:=2 ELSE FLAG:=3>> ELSE
     IF BETA>0 THEN FLAG:=2 ELSE FLAG:=3;
     liemat:=MAT((C1,C2,0,0),(C3,C4,0,0),(0,0,C5,0),(0,0,0,1))*liemat;
     E1:=D1*(C1*A3+C2*A4)+D3*(C1*B3+C2*B4);
     E2:=D2*(C1*A3+C2*A4)+D4*(C1*B3+C2*B4);
     E3:=D1*(C3*A3+C4*A4)+D3*(C3*B3+C4*B4);
     E4:=D2*(C3*A3+C4*A4)+D4*(C3*B3+C4*B4);
     E5:=C5*A5*D1+C5*B5*D3;
     E6:=C5*A5*D2+C5*B5*D4>>;
 IF FLAG=2 THEN
  <<liemat:=MAT((1,0,0,0),(0,1,0,0),(-E5/E1,-E6/E1,1,0),
            (0,0,-E2/E1,1/E1))*liemat;
    liemat:=MAT((1/2,1/2,0,0),(1/2,-1/2,0,0),(0,0,1/2,1/2),
            (0,0,-1/2,1/2))*liemat;
    if symbolic !*tr_lie then
    WRITE "[W,Y]=W, [X,Z]=X";lie_class:={liealg(4),comtab(3)}>> ELSE
  IF FLAG=3 THEN
   <<liemat:=MAT((1,0,0,0),(0,1,0,0),(-E5/E1,-E6/E1,1,0),
             (0,0,E2/E1,1/E1))*liemat;
     if symbolic !*tr_lie then
     WRITE "-[W,Y]=[X,Z]=X, [X,Y]=[W,Z]=W";
     lie_class:={liealg(4),comtab(4)}>> ELSE
  <<liemat:=MAT((1,0,0,0),(0,1,0,0),(-E5/E1,-E6/E1,1,0),
            (0,0,-E3/E1,1/E1))*liemat;
    if symbolic !*tr_lie then
    WRITE "[X,Y]=[W,Z]=W, [X,Z]=X";lie_class:={liealg(4),comtab(5)}>>;
END;

algebraic procedure com43(I1,J1,I2,J2,I3,J3);
BEGIN INTEGER LL;
      MATRIX liemat(4,4),BB(4,4),FF(3,3);
      ARRAY l_Z(4,4,3);
 FOR I:=1:4 DO
  <<CC(2,1,I):=-CC(1,2,I);CC(3,1,I):=-CC(1,3,I);
    CC(3,2,I):=-CC(2,3,I);CC(4,1,I):=-CC(1,4,I);
    CC(4,2,I):=-CC(2,4,I);CC(4,3,I):=-CC(3,4,I);
    CC(1,1,I):=CC(2,2,I):=CC(3,3,I):=CC(4,4,I):=0;
    liemat(1,I):=CC(I1,J1,I);liemat(2,I):=CC(I2,J2,I);
    liemat(3,I):=CC(I3,J3,I)>>;
 liemat(4,1):=1;IF DET(liemat) NEQ 0 THEN LL:=1 ELSE
  FOR J:=2:4 DO <<liemat(4,J-1):=0;liemat(4,J):=1;
  IF DET(liemat) NEQ 0 THEN <<LL:=J;J:=4>>>>;
 BB:=1/liemat;
 FOR I:=1:3 DO
  <<l_Z(1,2,I):=FOR R:=1:4 SUM FOR S:=1:4 SUM FOR TT:=1:4 SUM
              liemat(1,R)*liemat(2,S)*CC(R,S,TT)*BB(TT,I);
    l_Z(1,3,I):=FOR R:=1:4 SUM FOR S:=1:4 SUM FOR TT:=1:4 SUM
              liemat(1,R)*liemat(3,S)*CC(R,S,TT)*BB(TT,I);
    l_Z(2,3,I):=FOR R:=1:4 SUM FOR S:=1:4 SUM FOR TT:=1:4 SUM
              liemat(2,R)*liemat(3,S)*CC(R,S,TT)*BB(TT,I);
    l_Z(1,4,I):=FOR R:=1:4 SUM FOR TT:=1:4 SUM
              liemat(1,R)*CC(R,LL,TT)*BB(TT,I);
    l_Z(2,4,I):=FOR R:=1:4 SUM FOR TT:=1:4 SUM
              liemat(2,R)*CC(R,LL,TT)*BB(TT,I);
    l_Z(3,4,I):=FOR R:=1:4 SUM FOR TT:=1:4 SUM
              liemat(3,R)*CC(R,LL,TT)*BB(TT,I)>>;
 FOR I:=1:3 DO
  <<FF(1,I):=l_Z(1,2,I);FF(2,I):=l_Z(1,3,I);FF(3,I):=l_Z(2,3,I)>>;
 LL:=0;
 FOR I:=1:3 DO FOR J:=1:3 DO
  IF FF(I,J) NEQ 0 THEN <<LL:=1;I:=3;J:=3>>;
 IF LL=0 THEN comcom0() ELSE
  IF DET(FF)=0 THEN comcom1() ELSE comcom3();
 CLEAR BB,FF,l_Z
END;

algebraic procedure comcom0();
BEGIN SCALAR HE,A1,B1,C1,A2,B2,C2,A3,B3,C3,AA1,BB1,CC1,
             AA2,BB2,CC2,AL1,BE1,GA1,AL2,BE2,GA2,R,S,P,Q;
 A1:=l_Z(1,4,1);B1:=l_Z(1,4,2);C1:=l_Z(1,4,3);
 A2:=l_Z(2,4,1);B2:=l_Z(2,4,2);C2:=l_Z(2,4,3);
 A3:=l_Z(3,4,1);B3:=l_Z(3,4,2);C3:=l_Z(3,4,3);
 IF (A3=0 AND B3=0) THEN
  <<liemat:=MAT((1,0,0,0),(0,1,0,0),(0,0,1,0),(0,0,0,1/C3))*liemat;
    AL1:=A1/C3;BE1:=B1/C3;GA1:=C1/C3;
    AL2:=A2/C3;BE2:=B2/C3;GA2:=C2/C3>> ELSE
  <<IF (A3=0 AND B3 NEQ 0) THEN
   <<liemat:=MAT((0,B3,C3,0),(1,0,0,0),(0,0,1,0),(0,0,0,1))*liemat;
     AA1:=B2+C3;BB1:=B3*A2;CC1:=B3*C2-B2*C3;
     AA2:=B1/B3;BB2:=A1;CC2:=C1-B1*C3/B3>> ELSE
   <<liemat:=MAT((A3,B3,C3,0),(0,1,0,0),(0,0,1,0),(0,0,0,1))*liemat;
     AA1:=A1+B3*A2/A3+C3;BB1:=A3*B1-A1*B3-B3*B3*A2/A3+B3*B2;
     CC1:=A3*C1-A1*C3-B3*A2*C3/A3+B3*C2;
     AA2:=A2/A3;BB2:=B2-A2*B3/A3;CC2:=C2-A2*C3/A3>>;
  <<liemat:=MAT((1,0,0,0),(0,1,-AA2,0),(0,0,1,0),(0,0,0,1))*liemat;
    CC1:=CC1+BB1*AA2;CC2:=CC2+BB2*AA2;AA2:=0>>;
  IF (BB1=0 AND AA1=BB2 AND CC2 NEQ 0) THEN
   <<liemat:=MAT((0,0,1,0),(0,1,0,0),(1,-CC1/CC2,0,0),(0,0,0,1/AA1))*
   liemat;
     AL1:=0;BE1:=CC1/(AA1*CC2);GA1:=1/AA1;
     AL2:=CC2/AA1;BE2:=1;GA2:=0>> ELSE
    IF (BB1=0 AND AA1 NEQ BB2 AND CC2 NEQ 0) THEN
     <<A1:=1/(BB2-AA1);B1:=(BB2*AA1-BB2*BB2+CC1)/(CC2*(AA1-BB2));
     liemat:=MAT((1,0,0,0),(0,1,0,0),(A1,B1,1,0),(0,0,0,1/BB2))*liemat;
       AL1:=(AA1-CC1*A1)/BB2;BE1:=-B1*CC1/BB2;GA1:=CC1/BB2;
       AL2:=-CC2*A1/BB2;BE2:=1-B1*CC2/BB2;GA2:=CC2/BB2>>ELSE
     IF(BB1=0 AND CC2=0) THEN
      <<liemat:=MAT((1,0,0,0),(0,0,1,0),(0,1,0,0),(0,0,0,1/BB2))*liemat;
        AL1:=AA1/BB2;BE1:=CC1/BB2;AL2:=1/BB2;GA1:=BE2:=GA2:=0>>
     ELSE
      <<R:=-AA1-BB2;S:=AA1*BB2-CC1;P:=S-R*R/3;
        Q:=2*R*R*R/27-S*R/3+BB2*CC1-BB1*CC2;
        C1:=(-Q/2+SQRT(Q*Q/4+P*P*P/27))**(1/3)+
            (-Q/2-SQRT(Q*Q/4+P*P*P/27))**(1/3)-R/3;
        A1:=(C1-BB2)/BB1;B1:=(C1-BB2)*(C1-AA1)/BB1;
      liemat:=MAT((1,0,0,0),(0,0,1,0),(A1,1,B1,0),(0,0,0,1/C1))*liemat;
        AL1:=(AA1-A1*BB1)/C1;BE1:=(CC1-B1*BB1)/C1;
        GA1:=BB1/C1;AL2:=1/C1;BE2:=GA2:=0>>>>;
 IF GA2 NEQ 0 THEN
 <<liemat:=MAT((1,-GA1/GA2,0,0),(0,1,0,0),(0,0,1,0),(0,0,0,1))*liemat;
   AA1:=AL1-GA1*AL2/GA2;BB1:=BE1+AL1*GA1/GA2-AL2*GA1*GA1/
   (GA2*GA2)-GA1*BE2/GA2;AA2:=AL2;BB2:=BE2+AL2*GA1/GA2;CC2:=GA2>>
  ELSE <<liemat:=MAT((0,1,0,0),(1,0,0,0),(0,0,1,0),(0,0,0,1))*liemat;
         AA1:=BE2;BB1:=AL2;AA2:=BE1;BB2:=AL1;CC2:=GA1>>;
 IF (AA2=0 AND AA1-BB1-BB2=0 AND -AA1-BB1+BB2=0 AND CC2=0)
  THEN c0111(AA1,AA1) ELSE
  <<IF AA2=0 THEN
     IF (AA1-BB1-BB2) NEQ 0 THEN
     <<liemat:=MAT((1,0,0,0),(1,1,0,0),(0,0,1,0),(0,0,0,1))*liemat;
       AA2:=AA1-BB1-BB2;BB2:=BB1+BB2;AA1:=AA1-BB1>> ELSE
      IF (-AA1-BB1+BB2) NEQ 0 THEN
       <<liemat:=MAT((1,0,0,0),(-1,1,0,0),(0,0,1,0),(0,0,0,1))*liemat;
         AA2:=-AA1-BB1+BB2;BB2:=BB2-BB1;AA1:=AA1+BB1>> ELSE
      <<liemat:=MAT((0,0,1,0),(0,1,0,0),(1,0,0,0),(0,0,0,1/AA1))*liemat;
          AA2:=CC2/AA1;BB2:=1;CC2:=0;AA1:=1/AA1>>;
    liemat:=MAT((1,-AA1/AA2,AA1*CC2/AA2,0),(0,1,0,0),(0,0,1,0),
            (0,0,0,1))*liemat;
    BE1:=BB1-AA1*BB2/AA2;
    AL2:=AA2;BE2:=AA1+BB2;GA2:=CC2-AA1*CC2;
    liemat:=MAT((1,0,0,0),(-BE2,BE1,0,0),(0,0,1,0),(0,0,0,1))*liemat;
    AA1:=BE2; AA2:=AL2*BE1;CC2:=GA2*BE1;
    IF (CC2 NEQ 0 AND AA2=(1-AA1)) THEN
     <<liemat:=MAT((1,0,0,0),(1,1,0,0),(0,0,CC2,0),(0,0,0,1))*liemat;
       AL1:=AA1-1;
       IF AL1=1 THEN
        <<if symbolic !*tr_lie then
          WRITE "[W,Z]=W+X, [X,Z]=X+Y, [Y,Z]=Y";
          lie_class:={liealg(4),comtab(12)}>>
        ELSE <<liemat:=MAT((0,0,1,0),(0,1,0,0),(1,1/(AL1-1),
               1/((AL1-1)*(AL1-1)),0),(0,0,0,1/AL1))*liemat;
        liemat:=MAT((0,1,0,0),(1/AL1,0,0,0),(0,0,1,0),(0,0,0,1))*liemat;
        if symbolic !*tr_lie then
        WRITE "[W,Z]=",1/AL1,"W+X, [X,Z]=",1/AL1,"X, [Y,Z]=Y";
        lie_class:={liealg(4),comtab(15),1/AL1}>>>> ELSE
     <<IF CC2 NEQ 0 THEN
       liemat:=MAT((1,0,-CC2/(1-AA2-AA1),0),(0,1,(-1+AA1)*
               CC2/(1-AA2-AA1),0),(0,0,CC2/(1-AA2-AA1),0),
               (0,0,0,1))*liemat;
       liemat:=MAT((1,0,0,0),(AA1/2,1,0,0),(0,0,1,0),(0,0,0,1))*liemat;
       R:=(AA1*AA1/4+AA2);
       IF R=0 THEN
       <<if symbolic !*tr_lie then
         WRITE "[W,Z]=",AA1/2,"W+X, [X,Z]=",AA1/2,"X, [Y,Z]=Y";
         lie_class:={liealg(4),comtab(15),AA1/2}>> ELSE
        <<liemat:=MAT((SQRT(ABS(R)),0,0,0),(0,1,0,0),(0,0,1,0),
                  (0,0,0,1))*liemat;
          IF NOT(NUMBERP(R)) THEN
          <<WRITE "Is ",R,"<0 ? (y/n) and press <RETURN>";
            HE:=SYMBOLIC READ();
            IF HE=y THEN
            <<liemat:=MAT((1,0,0,0),(0,1,0,0),(0,0,1,0),
                     (0,0,0,SQRT(ABS(1/R))))*liemat;
             S:=AA1/(2*SQRT(ABS(R)));
             if symbolic !*tr_lie then
             WRITE "[W,Z]=",S,"W+X, [X,Z]=-W+",S,"X, [Y,Z]=",
                   SQRT(ABS(1/R)),"Y";
                   lie_class:={liealg(4),comtab(14),S,SQRT(ABS(1/R))}>>
            ELSE
          <<liemat:=MAT((1,0,0,0),(1,1,0,0),(0,0,1,0),(0,0,0,1))*liemat;
              liemat:=MAT((-2*SQRT(ABS(R)),SQRT(ABS(R)),0,0),
                      (0,SQRT(ABS(R)),0,0),(0,0,1,0),(0,0,0,1))*liemat;
              <<c0111(AA1/2-SQRT(ABS(R)),AA1/2+SQRT(ABS(R)))>>>>>> ELSE
          IF R<0 THEN
           <<liemat:=MAT((1,0,0,0),(0,1,0,0),(0,0,1,0),
                     (0,0,0,SQRT(ABS(1/R))))*liemat;
             S:=AA1/(2*SQRT(ABS(R)));
             if symbolic !*tr_lie then
             WRITE "[W,Z]=",S,"W+X, [X,Z]=-W+",S,"X, [Y,Z]=",
                   SQRT(ABS(1/R)),"Y";
                   lie_class:={liealg(4),comtab(14),S,SQRT(ABS(1/R))}>>
            ELSE
          <<liemat:=MAT((1,0,0,0),(1,1,0,0),(0,0,1,0),(0,0,0,1))*liemat;
              liemat:=MAT((-2*SQRT(ABS(R)),SQRT(ABS(R)),0,0),
                      (0,SQRT(ABS(R)),0,0),(0,0,1,0),(0,0,0,1))*liemat;
              c0111(AA1/2-SQRT(ABS(R)),AA1/2+SQRT(ABS(R)))>>>>
     >>>>
 END;

algebraic procedure c0111(MY,NY);
BEGIN
 liemat:=MAT((0,0,1,0),(1,0,0,0),(0,1,0,0),(0,0,0,1))*liemat;
 if symbolic !*tr_lie then
 WRITE "[W,Z]=W, [X,Z]=",MY,"X, [Y,Z]=",NY,"Y";
 lie_class:={liealg(4),comtab(13),MY,NY}
END;

ALGEBRAIC PROCEDURE COMCOM1();
BEGIN INTEGER II;
      SCALAR HE,A1,A2,A3,B2,B3,C2,C3,HELP;
      MATRIX A11(4,4),A22(4,4),A33(4,4),CCC(3,3);
 HELP:=0;
 FOR M:=1:3 DO FOR N:=1:3 DO
  IF FF(M,N) NEQ 0 THEN <<II:=M;M:=3;N:=3>>;
  A11:=MAT((1,0,0,0),(0,1,0,0),(FF(II,1),FF(II,2),FF(II,3),0),
           (0,0,0,1));
  A22:=MAT((1,0,0,0),(0,0,1,0),(FF(II,1),FF(II,2),FF(II,3),0),
           (0,0,0,1));
  A33:=MAT((0,1,0,0),(0,0,1,0),(FF(II,1),FF(II,2),FF(II,3),0),
           (0,0,0,1));
 IF DET(A11) NEQ 0 THEN liemat:=A11*liemat ELSE
  IF DET(A22) NEQ 0 THEN liemat:=A22*liemat ELSE liemat:=A33*liemat;
 liemat:=MAT((0,0,1,0),(1,0,0,0),(0,1,0,0),(0,0,0,1))*liemat;
 A11:=1/liemat;
 FOR M:=1:3 DO FOR N:=1:3 DO
 CCC(M,N):=FOR I:=1:4 SUM FOR J:=1:4 SUM FOR K:=1:4 SUM
  liemat(M,I)*liemat(4,J)*CC(I,J,K)*A11(K,N);
 A1:=CCC(1,1);A2:=CCC(2,1);A3:=CCC(3,1);B2:=CCC(2,2);
 B3:=CCC(3,2);C2:=CCC(2,3);C3:=CCC(3,3);
 IF A1=0 THEN
  <<IF C2=0 THEN
     IF B3=0 THEN
      <<liemat:=MAT((1,0,0,0),(0,1,1,0),(0,0,1,0),(0,0,0,1))*liemat;
        A2:=A2+A3;C2:=-2*B2>> ELSE
      <<liemat:=MAT((1,0,0,0),(0,1,B2/B3,0),(0,0,1,0),(0,0,0,1))*liemat;
        A2:=A2+A3*B2/B3;C2:=-3*B2*B2/B3;B2:=2*B2>>;
    HELP:=B2*B2+C2*B3;C3:=SQRT(ABS(HELP));
    liemat:=MAT((C2/C3,0,0,0),(0,1,0,0),(0,B2/C3,C2/C3,0),
            (0,A3*C3/HELP,-A2*C3/HELP,C3/HELP))*liemat;
    if symbolic !*tr_lie then
    WRITE "[X,Y]=W, [X,Z]=",HELP/ABS(HELP),"Y, [Y,Z]=X";
    if HELP>0 then lie_class:={liealg(4),comtab(19)} else
                   lie_class:={liealg(4),comtab(20)}>> ELSE
  <<liemat:=MAT((1,0,0,0),(0,1,0,0),(0,0,1,0),
            (0,2*A3/A1,-2*A2/A1,2/A1))*liemat;
    B2:=2*B2/A1;C2:=2*C2/A1;B3:=2*B3/A1;C3:=2*C3/A1;
    IF B3 NEQ 0 THEN
  <<liemat:=MAT((1,0,0,0),(0,1,(1-B2)/B3,0),(0,0,1,0),(0,0,0,1))*liemat;
      C2:=C2+(1-B2)*(C3-1)/B3;B2:=C3:=1;
      IF C2=0 THEN
       <<liemat:=MAT((-1,0,0,0),(0,0,1,0),(0,1,0,0),(0,0,0,1))*liemat;
         C2:=B3>> ELSE
       <<A1:=B3/ABS(B3);A2:=C2/ABS(C2);A3:=SQRT(ABS(B3*C2));
         liemat:=MAT((1,0,0,0),(0,(ABS(B3/C2))**(1/4),0,0),
                 (0,0,(ABS(C2/B3))**(1/4),0),(0,0,0,1))*liemat;
         IF A1=A2 THEN
          <<IF NOT(NUMBERP(A1)) THEN
            <<WRITE "Is ",A1,"<0 ? (y/n) and press <RETURN>";
              HE:=SYMBOLIC READ();
              IF HE=y THEN A3:=-A3>> ELSE
            IF A1<0 THEN A3:=-A3;
            liemat:=MAT((1,0,0,0),(0,1,0,0),(0,1,1,0),(0,0,0,1))*liemat;
            B2:=1-A3;C2:=A3;C3:=A3+1>> ELSE
          <<HELP:=1;
            IF NOT(NUMBERP(A1)) THEN
            <<WRITE "Is ",A1,"<0 ? (y/n) and press <RETURN>";
              HE:=SYMBOLIC READ();
              IF HE=y THEN
              liemat:=MAT((-1,0,0,0),(0,0,1,0),(0,1,0,0),(0,0,0,1))*
              liemat>> ELSE
            IF A1<0 THEN
            liemat:=MAT((-1,0,0,0),(0,0,1,0),(0,1,0,0),(0,0,0,1))
                       *liemat;
            if symbolic !*tr_lie then
            WRITE "[W,Z]=2W, [X,Y]=W, [X,Z]=X-",A3,"Y, ",
           "[Y,Z]=",A3,"X+Y";lie_class:={liealg(4),comtab(17),A3}>>>>>>;
    IF (HELP NEQ 1) THEN
     IF (C2=0 OR B2 NEQ C3) THEN
     <<IF (B2 NEQ C3) THEN
      liemat:=MAT((1,0,0,0),(0,1,C2/(B2-C3),0),(0,0,1,0),(0,0,0,1))*
      liemat;
      IF NOT(NUMBERP(B2)) THEN
            <<WRITE "Is ",B2,"<1 ? (y/n) and press <RETURN>";
              HE:=SYMBOLIC READ();
              IF HE=y THEN
          liemat:=MAT((-1,0,0,0),(0,0,1,0),(0,1,0,0),(0,0,0,1))*liemat;
              HELP:=B2;B2:=C3;C3:=HELP>> ELSE
      IF B2<1 THEN
        <<liemat:=MAT((-1,0,0,0),(0,0,1,0),(0,1,0,0),(0,0,0,1))*liemat;
          HELP:=B2;B2:=C3;C3:=HELP>>;
        if symbolic !*tr_lie then
        WRITE "[W,Z]=2W, [X,Y]=W, [X,Z]=",B2,"X, [Y,Z]=",C3,"Y";
        lie_class:={liealg(4),comtab(16),B2-1}>> ELSE
      <<liemat:=MAT((1,0,0,0),(0,1/SQRT(ABS(C2)),0,0),
                (0,0,SQRT(ABS(C2)),0),(0,0,0,1))*liemat;
        IF NOT(NUMBERP(C2)) THEN
            <<WRITE "Is ",C2,"<0 ? (y/n) and press <RETURN>";
              HE:=SYMBOLIC READ();
              IF HE=y THEN
              liemat:=MAT((-1,0,0,0),(0,1,0,0),(0,0,-1,0),(0,0,0,1))*
              liemat>> ELSE
        IF C2<0 THEN
         liemat:=MAT((-1,0,0,0),(0,1,0,0),(0,0,-1,0),(0,0,0,1))*liemat;
        if symbolic !*tr_lie then
        WRITE "[W,Z]=2W, [X,Y]=W, [X,Z]=X+Y, [Y,Z]=Y";
        lie_class:={liealg(4),comtab(18)}>>>>;
 CLEAR A11,A22,A33,CCC
END;

algebraic procedure comcom3();
BEGIN INTEGER HELP;
      SCALAR HE,AL,BE,GA;
      MATRIX l_K(3,3),l_A(3,3);
 HELP:=0;
 l_K(1,1):=FF(1,2)**2+2*FF(1,3)*FF(2,2)+FF(2,3)**2;
 l_K(1,2):=-FF(1,1)*FF(1,2)+FF(1,3)*FF(3,2)-
          FF(2,1)*FF(1,3)+FF(2,3)*FF(3,3);
 l_K(1,3):=-FF(1,1)*FF(2,2)-FF(1,2)*FF(3,2)-
          FF(2,1)*FF(2,3)-FF(2,2)*FF(3,3);
 l_K(2,1):=l_K(1,2);
 l_K(2,2):=FF(1,1)**2-2*FF(1,3)*FF(3,1)+FF(3,3)**2;
 l_K(2,3):=FF(1,1)*FF(2,1)+FF(1,2)*FF(3,1)-
         FF(3,1)*FF(2,3)-FF(3,2)*FF(3,3);
 l_K(3,1):=l_K(1,3);l_K(3,2):=l_K(2,3);
 l_K(3,3):=FF(2,1)**2+2*FF(2,2)*FF(3,1)+FF(3,2)**2;
 IF NOT(NUMBERP(l_K(1,1)) AND
  NUMBERP(l_K(1,1)*l_K(2,2)-l_K(1,2)*l_K(2,1)) AND
        NUMBERP(DET(l_K))) THEN
 <<WRITE "Is ",-l_K(1,1),">0 and ",
      l_K(1,1)*l_K(2,2)-l_K(1,2)*l_K(2,1),">0 and ",
     -DET(l_K),">0 ? (y/n) and press <RETURN>";
   HE:=SYMBOLIC READ();
   IF HE=y THEN <<HELP:=1;lie4so3()>> ELSE lie4so21()>> ELSE
 IF (-l_K(1,1)>0 AND l_K(1,1)*l_K(2,2)-l_K(1,2)*l_K(2,1)>0 AND
     -DET(l_K)>0) THEN
  <<HELP:=1;lie4so3()>> ELSE lie4so21();
 liemat:=MAT((l_A(1,1),l_A(1,2),l_A(1,3),0),(l_A(2,1),l_A(2,2),
          l_A(2,3),0), (l_A(3,1),l_A(3,2),l_A(3,3),0),(0,0,0,1))*liemat;
 BB:=1/liemat;
 AL:=FOR J:=1:4 SUM FOR K:=1:4 SUM FOR L:=1:4 SUM
         liemat(1,J)*liemat(4,K)*CC(J,K,L)*BB(L,2);
 BE:=FOR J:=1:4 SUM FOR K:=1:4 SUM FOR L:=1:4 SUM
         liemat(1,J)*liemat(4,K)*CC(J,K,L)*BB(L,3);
 GA:=FOR J:=1:4 SUM FOR K:=1:4 SUM FOR L:=1:4 SUM
         liemat(2,J)*liemat(4,K)*CC(J,K,L)*BB(L,3);
 IF HELP=1 THEN
  liemat:=MAT((1,0,0,0),(0,1,0,0),(0,0,1,0),(GA,-BE,AL,1))*liemat ELSE
  liemat:=MAT((1,0,0,0),(0,1,0,0),(0,0,1,0),(GA,-BE,-AL,1))*liemat;
 IF HELP=1 THEN
  <<if symbolic !*tr_lie then
    WRITE "[W,X]=Y, [W,Y]=-X, [X,Y]=W";
    lie_class:={liealg(4),comtab(21)}>> ELSE
  <<if symbolic !*tr_lie then
    WRITE "[W,X]=Y, [W,Y]=X, [X,Y]=W";
    lie_class:={liealg(4),comtab(22)}>>;
 CLEAR l_K,l_A
END;

algebraic procedure lie4so3();
BEGIN SCALAR S,TT,Q,R,ALPHA;
 S:=FF(2,2)/ABS(FF(2,2));
 TT:=ABS(FF(1,2)**2+FF(1,3)*FF(2,2));
 R:=FF(1,1)-FF(1,2)*FF(2,1)/FF(2,2);
 ALPHA:=TT*(-R*R-((FF(2,1)/FF(2,2))**2+FF(3,1)/FF(2,2))*TT);
 Q:=1/SQRT(ALPHA);
 l_A(1,1):=1/(S*SQRT(TT));l_A(1,2):=l_A(1,3):=l_A(2,2):=0;l_A(2,1):=Q*R;
 l_A(2,3):=-Q*TT/FF(2,2);l_A(3,1):=-Q*S*SQRT(TT)*FF(2,1)/FF(2,2);
 l_A(3,2):=-Q*S*SQRT(TT);l_A(3,3):=Q*S*SQRT(TT)*FF(1,2)/FF(2,2)
END;

algebraic procedure lie4so21();
BEGIN SCALAR GAM,EPS,S,TT,Q,R,ALPHA;
      MATRIX l_G(3,3);
 l_A:=MAT((1,0,0),(0,1,0),(0,0,1));
 IF FF(2,2)=0 THEN
  IF FF(1,3) NEQ 0 THEN <<l_A:=MAT((1,0,0),(0,0,1),(0,1,0));
   l_G(1,1):=FF(2,1);l_G(1,2):=FF(2,3);l_G(1,3):=FF(2,2);
   l_G(2,1):=FF(1,1);l_G(2,2):=FF(1,3);l_G(2,3):=FF(1,2);
   l_G(3,1):=-FF(3,1);l_G(3,2):=-FF(3,3);l_G(3,3):=-FF(3,2);FF:=l_G>>
    ELSE
   IF FF(3,1) NEQ 0 THEN <<l_A:=MAT((0,1,0),(1,0,0),(0,0,1));
    l_G(1,1):=-FF(1,2);l_G(1,2):=-FF(1,1);l_G(1,3):=-FF(1,3);
    l_G(2,1):=FF(3,2);l_G(2,2):=FF(3,1);l_G(2,3):=FF(3,3);
    l_G(3,1):=FF(2,2);l_G(3,2):=FF(2,1);l_G(3,3):=FF(2,3);FF:=l_G>> ELSE
                         <<l_A:=MAT((1,0,1),(1,0,0),(0,1,0));
    l_G(1,1):=-FF(2,3);l_G(1,2):=FF(2,3)-FF(2,1);l_G(1,3):=0;
    l_G(2,1):=-FF(3,3);l_G(2,2):=2*FF(1,1);l_G(2,3):=FF(1,2)-FF(3,2);
    l_G(3,1):=0;l_G(3,2):=FF(1,1);l_G(3,3):=FF(1,2);FF:=l_G>>;
 IF FF(1,2)**2+FF(1,3)*FF(2,2)=0 THEN
  <<GAM:=-FF(1,2)/FF(2,2);EPS:=FF(1,1)-FF(1,2)*FF(2,1)/FF(2,2);
  IF 1/4*(FF(3,2)**2+FF(3,1)*FF(2,2))-EPS*FF(2,2)/2=0 THEN
   <<l_A:=MAT((0,0,1),(0,2/EPS,2*GAM/EPS),(1,0,0))*l_A;
   l_G(1,1):=2*GAM*FF(3,2)/EPS-FF(3,3);
   l_G(1,2):=-FF(3,2);l_G(1,3):=-2*FF(3,1)/EPS;
   l_G(2,1):=0;l_G(2,2):=-EPS*FF(2,2)/2;l_G(2,3):=-FF(2,1);
   l_G(3,1):=l_G(3,2):=0;l_G(3,3):=-2;FF:=l_G>> ELSE
   <<l_A:=MAT((1/2,0,1/2),(0,1/EPS,GAM/EPS),(-1/2,0,1/2))*l_A;
   l_G(1,1):=-FF(3,1)/(2*EPS);l_G(1,2):=-FF(3,2)/2;
   l_G(1,3):=FF(3,1)/(2*EPS)-1;
   l_G(2,1):=FF(2,1)/2;l_G(2,2):=FF(2,2)*EPS/2;
   l_G(2,3):=-FF(2,1)/2;l_G(3,1):=FF(3,1)/(2*EPS)+1;
   l_G(3,2):=FF(3,2)/2;l_G(3,3):=-FF(3,1)/(2*EPS);FF:=l_G>>>>;
 IF NOT(NUMBERP(FF(1,2)**2+FF(1,3)*FF(2,2))) THEN
 <<WRITE "Is ",FF(1,2)**2+FF(1,3)*FF(2,2),
         "<0 ? (y/n) and press <RETURN>";
   HE:=SYMBOLIC READ();
   IF HE=y THEN
   <<S:=FF(2,2)/ABS(FF(2,2));
  TT:=ABS(FF(1,2)**2+FF(1,3)*FF(2,2));
  R:=FF(1,1)-FF(1,2)*FF(2,1)/FF(2,2);
  ALPHA:=TT*(-R*R-((FF(2,1)/FF(2,2))**2+FF(3,1)/FF(2,2))*TT);
  Q:=1/SQRT(ABS(ALPHA));
  l_G(1,1):=-Q*S*SQRT(TT)*FF(2,1)/FF(2,2);
  l_G(1,2):=-Q*S*SQRT(TT);l_G(1,3):=Q*S*SQRT(TT)*FF(1,2)/FF(2,2);
  l_G(2,1):=1/(S*SQRT(TT));l_G(2,2):=l_G(2,3):=0;
  l_G(3,1):=Q*R;l_G(3,2):=0;l_G(3,3):=-Q*TT/FF(2,2);l_A:=l_G*l_A>> ELSE
  <<S:=FF(2,2)/ABS(FF(2,2));
  TT:=FF(1,2)**2+FF(1,3)*FF(2,2);
  R:=FF(1,1)-FF(1,2)*FF(2,1)/FF(2,2);
  ALPHA:=TT*(R*R-((FF(2,1)/FF(2,2))**2+FF(3,1)/FF(2,2))*TT);
  Q:=1/SQRT(ABS(ALPHA));
  IF NOT(NUMBERP(ALPHA)) THEN
  <<WRITE "Is ",ALPHA,">0 ? (y/n) and press <RETURN>";
    HE:=SYMBOLIC READ();
    IF HE =y THEN
    <<l_G(1,1):=1/(S*SQRT(TT));l_G(1,2):=l_G(1,3):=0;
   l_G(2,1):=Q*R;l_G(2,2):=0;l_G(2,3):=Q*TT/FF(2,2);
   l_G(3,1):=Q*S*SQRT(TT)*FF(2,1)/FF(2,2);l_G(3,2):=Q*S*SQRT(TT);
   l_G(3,3):=-Q*S*SQRT(TT)*FF(1,2)/FF(2,2);l_A:=l_G*l_A>> ELSE
   <<l_G(1,1):=1/(S*SQRT(TT));l_G(1,2):=l_G(1,3):=0;
   l_G(2,1):=Q*S*SQRT(TT)*FF(2,1)/FF(2,2);l_G(2,2):=Q*S*SQRT(TT);
   l_G(2,3):=-Q*S*SQRT(TT)*FF(1,2)/FF(2,2);
   l_G(3,1):=Q*R;l_G(3,2):=0;l_G(3,3):=Q*TT/FF(2,2);
   l_A:=l_G*l_A>>>> ELSE
  IF ALPHA>0 THEN
   <<l_G(1,1):=1/(S*SQRT(TT));l_G(1,2):=l_G(1,3):=0;
   l_G(2,1):=Q*R;l_G(2,2):=0;l_G(2,3):=Q*TT/FF(2,2);
   l_G(3,1):=Q*S*SQRT(TT)*FF(2,1)/FF(2,2);l_G(3,2):=Q*S*SQRT(TT);
   l_G(3,3):=-Q*S*SQRT(TT)*FF(1,2)/FF(2,2);l_A:=l_G*l_A>> ELSE
   <<l_G(1,1):=1/(S*SQRT(TT));l_G(1,2):=l_G(1,3):=0;
   l_G(2,1):=Q*S*SQRT(TT)*FF(2,1)/FF(2,2);l_G(2,2):=Q*S*SQRT(TT);
   l_G(2,3):=-Q*S*SQRT(TT)*FF(1,2)/FF(2,2);
   l_G(3,1):=Q*R;l_G(3,2):=0;l_G(3,3):=Q*TT/FF(2,2);l_A:=l_G*l_A>>
>>>> ELSE
 IF FF(1,2)**2+FF(1,3)*FF(2,2)<0 THEN
  <<S:=FF(2,2)/ABS(FF(2,2));
  TT:=ABS(FF(1,2)**2+FF(1,3)*FF(2,2));
  R:=FF(1,1)-FF(1,2)*FF(2,1)/FF(2,2);
  ALPHA:=TT*(-R*R-((FF(2,1)/FF(2,2))**2+FF(3,1)/FF(2,2))*TT);
  Q:=1/SQRT(ABS(ALPHA));
  l_G(1,1):=-Q*S*SQRT(TT)*FF(2,1)/FF(2,2);
  l_G(1,2):=-Q*S*SQRT(TT);l_G(1,3):=Q*S*SQRT(TT)*FF(1,2)/FF(2,2);
  l_G(2,1):=1/(S*SQRT(TT));l_G(2,2):=l_G(2,3):=0;
  l_G(3,1):=Q*R;l_G(3,2):=0;l_G(3,3):=-Q*TT/FF(2,2);
  l_A:=l_G*l_A>> ELSE
  <<S:=FF(2,2)/ABS(FF(2,2));
  TT:=FF(1,2)**2+FF(1,3)*FF(2,2);
  R:=FF(1,1)-FF(1,2)*FF(2,1)/FF(2,2);
  ALPHA:=TT*(R*R-((FF(2,1)/FF(2,2))**2+FF(3,1)/FF(2,2))*TT);
  Q:=1/SQRT(ABS(ALPHA));
  IF NOT(NUMBERP(ALPHA)) THEN
  <<WRITE "Is ",ALPHA,">0 ? (y/n) and press <RETURN>";
    HE:=SYMBOLIC READ();
    IF HE =y THEN
    <<l_G(1,1):=1/(S*SQRT(TT));l_G(1,2):=l_G(1,3):=0;
   l_G(2,1):=Q*R;l_G(2,2):=0;l_G(2,3):=Q*TT/FF(2,2);
   l_G(3,1):=Q*S*SQRT(TT)*FF(2,1)/FF(2,2);l_G(3,2):=Q*S*SQRT(TT);
   l_G(3,3):=-Q*S*SQRT(TT)*FF(1,2)/FF(2,2);l_A:=l_G*l_A>> ELSE
   <<l_G(1,1):=1/(S*SQRT(TT));l_G(1,2):=l_G(1,3):=0;
   l_G(2,1):=Q*S*SQRT(TT)*FF(2,1)/FF(2,2);l_G(2,2):=Q*S*SQRT(TT);
   l_G(2,3):=-Q*S*SQRT(TT)*FF(1,2)/FF(2,2);
   l_G(3,1):=Q*R;l_G(3,2):=0;l_G(3,3):=Q*TT/FF(2,2);
   l_A:=l_G*l_A>>>> ELSE
  IF ALPHA>0 THEN
   <<l_G(1,1):=1/(S*SQRT(TT));l_G(1,2):=l_G(1,3):=0;
   l_G(2,1):=Q*R;l_G(2,2):=0;l_G(2,3):=Q*TT/FF(2,2);
   l_G(3,1):=Q*S*SQRT(TT)*FF(2,1)/FF(2,2);l_G(3,2):=Q*S*SQRT(TT);
   l_G(3,3):=-Q*S*SQRT(TT)*FF(1,2)/FF(2,2);l_A:=l_G*l_A>> ELSE
   <<l_G(1,1):=1/(S*SQRT(TT));l_G(1,2):=l_G(1,3):=0;
   l_G(2,1):=Q*S*SQRT(TT)*FF(2,1)/FF(2,2);l_G(2,2):=Q*S*SQRT(TT);
   l_G(2,3):=-Q*S*SQRT(TT)*FF(1,2)/FF(2,2);
   l_G(3,1):=Q*R;l_G(3,2):=0;l_G(3,3):=Q*TT/FF(2,2);l_A:=l_G*l_A>>>>;
 CLEAR l_G
END;

endmodule;

end;
