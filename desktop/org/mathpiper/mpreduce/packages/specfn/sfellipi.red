module sfellipi;  % Procedures and Rules for Elliptic Integrals.

% Author: Lisa Temme, ZIB, October 1994

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


algebraic <<

%######################################################################
%DESCENDING LANDEN TRANSFORMATION

procedure landentrans(phi,alpha);

   begin scalar alpha_n!+1, alpha_n, phi_n!+1, phi_n, aNtoa0, pNtop0,
                a0toaN, p0topN;

        alpha_n := alpha;
        phi_n   := phi;
        aNtoa0 := {alpha_n};
        pNtop0 := {phi_n};

        while alpha_n > 10^(-(Symbolic !:prec!:)) do
           <<
                alpha_n!+1:= asin(2/(1+cos(alpha_n)) -1);
                phi_n!+1 := phi_n + (atan(cos(alpha_n)*tan(phi_n)))
                            + floor((floor(phi_n/(pi/2))+1)/2)*pi;

                aNtoa0 := alpha_n!+1.aNtoa0;
                pNtop0 := phi_n!+1.pNtop0;

                alpha_n := alpha_n!+1;
                phi_n   := phi_n!+1
           >>;

                a0toaN := reverse(aNtoa0);
                p0topN := reverse(pNtop0);
                return list(p0topN, a0toaN)
   end;

%######################################################################
%VALUE OF EllipticF(phi,m)

procedure F_function(phi,m);

   begin scalar alpha, bothlists, a0toaN, a1toaN, p0topN, phi_n, y,
                elptF;

        alpha  := asin(sqrt(m));
        bothlists := landentrans(phi,alpha);
        a0toaN := PART(bothlists,2);
        a1toaN := REST(a0toaN);
        p0topN := PART(bothlists,1);
        phi_n  := PART(reverse(p0topN),1);

        if phi = (pi/2)
           then
                elptF := K_function(m)
           else
                elptF :=
                phi_n *for each y in a1toaN PRODUCT(1/2)*(1+sin(y));
        return elptF
   end;

%~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
%EllipticF definition
%====================

operator EllipticF;

EllipticFrules :=
{
        EllipticF(~phi,0)   => phi,
        EllipticF(i*~phi,0) => i*phi,
        EllipticF(~phi,1)   => ln(sec(phi)+tan(phi)),
        EllipticF(i*~phi,1) => i*atan(sinh(phi)),
        EllipticF(~phi,~m)  => Num_Elliptic(F_function,phi,m)
                              when lisp !*rounded and numberp phi
                              and numberp m
};
let EllipticFrules;

%######################################################################
%VALUE OF K(m)

procedure K_function(m);

   begin scalar AGM, aN;

        AGM := AGM_function(1,sqrt(1-m),sqrt(m));
        aN  := PART(AGM,2);
        return (pi / (2*aN));
   end;

%~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
%EllipticK definition
%====================

EllipticKrules :=

{
        EllipticK(~m)   => K_function(m)   when lisp !*rounded
                                                 and numberp m,

        EllipticK!'(~m) => K_function(1-m) when lisp !*rounded
                                                 and numberp m
};
let EllipticKrules;

%######################################################################
%VALUE OF EllipticE(phi,m)

procedure E_function(phi,m);

   begin scalar F, N, alpha, bothlists, a0toaN, p0topN, a1toaN, p1topN,
                sinalist, sinplist, b, s, blist, c, allz, w, z, allx,
                h, x, elptE;

        F := F_function(phi,m);
        alpha := asin(sqrt(m));

        bothlists := landentrans(phi,alpha);
        a0toaN := PART(bothlists, 2);
        p0topN := PART(bothlists, 1);
        a1toaN := REST(a0toaN);
        p1topN := REST(p0topN);

        N := LENGTH(a1toaN);

        sinalist := sin(a1toaN);
        sinplist := sin(p1topN);

        b := PART(sinalist,1);
        s := b;
        blist := for each c in rest sinalist collect << b := b*c >>;
        blist := s.blist;

        allz := 0;
        for w := 1:N do
           <<
                z := (1/(2^w))*PART(blist,w);
                allz := allz + z
           >>;

        allx := 0;
        for h := 1:N do
           <<
                x := (1/(2^h))*((PART(blist,h))^(1/2))
                              *  PART(sinplist,h);

                allx := allx + x
           >>;

        elptE := F * (1 - (1/2)*((sin(PART(a0toaN,1)))^2)*(1 + allz))
                                           + sin(PART(a0toaN,1))*allx ;
        return elptE;
   end;

%~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
%EllipticE(phi,m) definition
%====================

operator EllipticE;

JacobiErules :=

{
        EllipticE(0,~m)     => 0,
        EllipticE(~phi,0)   => phi,
        EllipticE(i*~phi,0) => i*phi,
        EllipticE(~phi,1)   => sin(phi),
        EllipticE(i*~phi,1) => i*sinh phi,
        EllipticE(-~phi,~m) => -EllipticE(phi,m),
        EllipticE(~phi,-~m) =>  EllpiticE(phi,m),

        df(EllipticE(~phi,~m),~phi) => Jacobidn(phi,m)^2,
        df(EllipticE(~phi,~m),~m)   =>

               m * (Jacobisn(phi,m) * Jacobicn(phi,m) * Jacobidn(phi,m)
                     -  EllipticE(phi,m) * Jacobicn(phi,m)^2) / (1-m^2)
                     -  m * phi * Jacobisn(phi,m)^2,

        EllipticE(~phi,~m) => Num_Elliptic(E_function,phi,m)
                              when lisp !*rounded and numberp phi
                              and numberp m,

        EllipticE(~m) => Num_Elliptic(E_function,pi/2,m)
                         when lisp !*rounded and numberp m
};
let JacobiErules;

%######################################################################
%CALCULATING THE FOUR THETA FUNCTIONS
%Theta 1 (often written H(u) - and has period 4K)
%Theta 2 (often written H1(u) -and has period 4K)
%Theta 3 (often written Theta1(u) - and has period 2K)
%Theta 4 (often written Theta(u) - and has period 2K)

procedure num_theta(a,u,m);

   begin scalar n, new, all, z, q, total;

        n := if a>2 then 1 else 0;
        new := 100;                     % To initiate loop
        all := 0;
        z := (pi*u)/(2*EllipticK(m));
        q := EXP(-pi*EllipticK(1-m)/EllipticK(m));

        while new > 10^(-(Symbolic !:prec!:)) do
          << new := if a =1 then
                        ((-1)^n)*(q^(n*(n+1)))*sin((2*n+1)*z)
                else if a=2 then (q^(n*(n+1)))*cos((2*n+1)*z)
                else if a=3 then (q^(n*n))*cos(2*n*z)
                else if a=4 then ((-1)^n)*(q^(n*n))*cos(2*n*z);
             all := new + all;
             n := n+1
           >>;
        return if a > 2 then (1 + 2*all)
                else   (2*(q^(1/4))*all);
   end;

%~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
%Theta Functions

operator EllipticTheta;


EllipticTHETArules :=
{
%Theta1rules
%-----------
        EllipticTheta(1,~u,~m) =>
                 Num_Elliptic(num_theta,1,u,m) when lisp !*rounded
                                  and numberp u and numberp m,

        EllipticTheta(1,-~u,~m) => -EllipticTheta(1,u,m),

        EllipticTheta(1,~u+EllipticK(~m),~m) =>  EllipticTheta(2,u,m),

        EllipticTheta(1,~u+(2*EllipticK(~m)),~m) =>
                                                -EllipticTheta(1,u,m),

        EllipticTheta(1,~u+i*EllipticK!'(~m),~m) =>
                         i*(EXP(-i*pi*0.5*u/EllipticK(m)))*(nome_q^(-1/2))
                                                *EllipticTheta(4,u,m),

        EllipticTheta(1,~u+2*i*EllipticK!'(~m),~m) =>
                                  -(EXP(-i*pi*u/EllipticK(m)))*(nome_q^-1)
                                                *EllipticTheta(1,u,m),

        EllipticTheta(1,~u+EllipticK(~m)+i*EllipticK!'(~m),~m) =>
                           (EXP(-i*pi*0.5*u/EllipticK(m)))*(nome_q^(-1/2))
                                                *EllipticTheta(3,u,m),

        EllipticTheta(1,~u+2*EllipticK(~m)+2*i*EllipticK!'(~m),~m) =>
                                   (EXP(-i*pi*u/EllipticK(m)))*(nome_q^-1)
                                                *EllipticTheta(1,u,m),

%Theta2rules
%-----------
        EllipticTheta(2,~u,~m) =>
                 Num_Elliptic(num_theta,2,u,m) when lisp !*rounded
                                  and numberp u and numberp m,

        EllipticTheta(2,-~u,~m) =>  EllipticTheta(2,u,m),

        EllipticTheta(2,~u+EllipticK(~m),~m) => -EllipticTheta(1,u,m),

        EllipticTheta(2,~u+(2*EllipticK(~m)),~m) =>
                                                -EllipticTheta(2,u,m),

        EllipticTheta(2,~u+i*EllipticK!'(~m),~m) =>
                           (EXP(-i*pi*0.5*u/EllipticK(m)))*(nome_q^(-1/2))
                                                *EllipticTheta(3,u,m),

        EllipticTheta(2,~u+2*i*EllipticK!'(~m),~m) =>
                                   (EXP(-i*pi*u/EllipticK(m)))*(nome_q^-1)
                                                *EllipticTheta(2,u,m),

        EllipticTheta(2,~u+EllipticK(~m)+i*EllipticK!'(~m),~m) =>
                        -i*(EXP(-i*pi*0.5*u/EllipticK(m)))*(nome_q^(-1/2))
                                                *EllipticTheta(4,u,m),

        EllipticTheta(2,~u+2*EllipticK(~m)+2*i*EllipticK!'(~m),~m) =>
                                  -(EXP(-i*pi*u/EllipticK(m)))*(nome_q^-1)
                                                *EllipticTheta(2,u,m),

%Theta3rules
%-----------
        EllipticTheta(3,~u,~m) =>
                 Num_Elliptic(num_theta,3,u,m) when lisp !*rounded
                                  and numberp u and numberp m,

        EllipticTheta(3,-~u,~m) =>  EllipticTheta(3,u,m),

        EllipticTheta(3,~u+EllipticK(~m),~m) =>  EllipticTheta(4,u,m),

        EllipticTheta(3,~u+(2*EllipticK(~m)),~m) =>
                                                 EllipticTheta(3,u,m),

        EllipticTheta(3,~u+i*EllipticK!'(~m),~m) =>
                           (EXP(-i*pi*0.5*u/EllipticK(m)))*(nome_q^(-1/2))
                                                *EllipticTheta(2,u,m),
        EllipticTheta(3,~u+2*i*EllipticK!'(~m),~m) =>
                                   (EXP(-i*pi*u/EllipticK(m)))*(nome_q^-1)
                                                *EllipticTheta(3,u,m),

        EllipticTheta(3,~u+EllipticK(~m)+i*EllipticK!'(~m),~m) =>
                         i*(EXP(-i*pi*0.5*u/EllipticK(m)))*(nome_q^(-1/2))
                                                *EllipticTheta(1,u,m),

        EllipticTheta(3,~u+2*EllipticK(~m)+2*i*EllipticK!'(~m),~m) =>
                                   (EXP(-i*pi*u/EllipticK(m)))*(nome_q^-1)
                                                *EllipticTheta(3,u,m),

%Theta4rules
%-----------
        EllipticTheta(4,~u,~m) =>
                 Num_Elliptic(num_theta,4,u,m) when lisp !*rounded
                                  and numberp u and numberp m,

        EllipticTheta(4,-~u,~m) =>  EllipticTheta(4,u,m),

        EllipticTheta(4,~u+EllipticK(~m),~m) =>  EllipticTheta(3,u,m),

        EllipticTheta(4,~u+(2*EllipticK(~m)),~m)=>EllipticTheta(4,u,m),

        EllipticTheta(4,~u+i*EllipticK!'(~m),~m) =>
                         i*(EXP(-i*pi*0.5*u/EllipticK(m)))*(nome_q^(-1/2))
                                                *EllipticTheta(1,u,m),
        EllipticTheta(4,~u+2*i*EllipticK!'(~m),~m) =>
                                  -(EXP(-i*pi*u/EllipticK(m)))*(nome_q^-1)
                                                *EllipticTheta(4,u,m),

        EllipticTheta(4,~u+EllipticK(~m)+i*EllipticK!'(~m),~m) =>
                           (EXP(-i*pi*0.5*u/EllipticK(m)))*(nome_q^(-1/2))
                                                *EllipticTheta(2,u,m),

        EllipticTheta(4,~u+2*EllipticK(~m)+2*i*EllipticK!'(~m),~m) =>
                                  -(EXP(-i*pi*u/EllipticK(m)))*(nome_q^-1)
                                                *EllipticTheta(4,u,m),
%Error
%-----
        EllipticTheta(~a,~u,~m) =>

            printerr ("In EllipticTheta(a,u,m);   a = 1,2,3 or 4.")
                         when numberp a
                                    and not(fixp a and a<5 and a>0)
};
let EllipticTHETArules;

%######################################################################
%CALCULATING ZETA

procedure ZETA_function(u,m);

   begin scalar phi_list, clist, L, j, z, cn, phi_n;

        phi_list := PHI_function(1,sqrt(1-m),sqrt(m),u);
        clist := PART(AGM_function(1,sqrt(1-m),sqrt(m)),5);
        L := LENGTH(phi_list);
        j := 1;
        z := 0;
        while j < L do
           <<
                cn    := PART(clist,L-j);
                phi_n := PART(phi_list,1+j);
                z := cn*sin(phi_n) + z;
                j := j+1
           >>;
        return z
   end;

%~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
%JacobiZETA definition
%=====================

operator JacobiZeta;

JacobiZETArules :=

{

        JacobiZeta(~u,0)     => 0,
        JacobiZeta(~u,1)     => tanh(u),
        JacobiZeta(-~u,~m)   => -JacobiZeta(u,m),
        JacobiZeta(~u+~v,~m) => JacobiZeta(u,m) + JacobiZeta(v,m) -
                                (m*Jacobisn(u,m)*Jacobisn(v,m)
                                                 *Jacobisn(u+v,m)),

        JacobiZeta(~u+2*EllipticK(~m),m) => JacobiZeta(u,m),
        JacobiZeta(EllipticK(~m) - ~u,m) =>
                                        -JacobiZeta(EllipticK(m)+u,m),

%       JacobiZeta(~u,~m) => JacobiZeta(u - EllipticK(m),m) -
%                            m * Jacobisn(u - EllipticK(m),m)
%                              * Jacobicd(u - EllipticK(m),m),

        JacobiZeta(~u,~m) => Num_Elliptic(ZETA_function,u,m)
                             when lisp !*rounded and numberp u
                             and numberp m
};
let JacobiZETArules;
%######################################################################
>>;
endmodule;
end;



