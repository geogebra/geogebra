module sfellip;  % Procedures and Rules for Elliptic functions.

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


algebraic;

%ARITHMETIC GEOMETRIC MEAN

%The following procedure is the process of the Arithmetic Geometric
%Mean.

procedure AGM_function(a0,b0,c0);

   begin scalar aN, bN, cN, aN!-1, bN!-1, alist, blist, clist;

     %Initial values.

        aN!-1 := a0;
        bN!-1 := b0;
        cN    := 20; %To initiate while loop below.

     %Put initial values at end of list.

        alist := {a0}$
        blist := {b0}$
        clist := {c0}$

     %Loop to generate lists of aN,bN and cN starting with the Nth
     %value and ending with the initial value.

     %When the absolute value of cN reaches a value smaller than that
     %of the required precision the loop exits. The value of aN=bN=AGM.

        while abs(cN) > 10^(-(Symbolic !:prec!:)) do

           << %Calculations for the process of the AGM

                aN := (aN!-1 + bN!-1) / 2;
                bN := sqrt(aN!-1 * bN!-1);
                cN := (aN!-1 - bN!-1) / 2;

             %Adding the next term to each of the lists.

                alist := aN.alist;
                blist := bN.blist;
                clist := cN.clist;

             %Resetting the values in order to execute the next loop.

                aN!-1 := aN;
                bN!-1 := bN
           >>;

     %N is the number of terms in each list (excluding the initial
     % values) used to calculate the AGM.

        N := LENGTH(alist) - 1;

     %The following list contains all the items required in the
     %calculation of other procedures which use the AGM
     %ie. {N, AGM, {aN to a0},{bN to b0},{cN to c0}}

        return list(N ,aN, alist, blist, clist)

   end;
%######################################################################
%CALCULATING PHI
%               N


%The following procedure sucessively computes phi   ,phi   ,...,phi ,
%                                                N-1    N-2        0
%from the recurrence relation:
%
%       sin(2phi    - phi ) = (c /a )sin phi
%               N-1      N       N  N          N
%
%and returns a list of phi  to phi . This list is then used in the
%                        0        N
%calculation of Jacobisn, Jacobicn, Jacobidn, which in turn are used
%to calculate the remaining twelve Jacobi Functions.


procedure PHI_function(a0,b0,c0,u);

   begin scalar alist, clist,N,a_n,aN,cN,i, phi_N, phi_N!-1, phi_list;

        agm   := AGM_function(a0,b0,c0);
        alist := PART(agm,3);              % aN to a0
        clist := PART(agm,5);              % cN to c0
        N := PART(agm,1);
        a_n := PART(alist,1);              % Value of the AGM.
        phi_N := (2^N)*a_n*u;
        phi_list := {phi_N}$
        i := 1;

        while i < LENGTH(alist) do

           <<
                aN := PART(alist,i);
                cN := PART(clist,i);

                phi_N!-1 := (asin((cN/aN)*sin(phi_N)) + phi_N) / 2;
                phi_list := phi_N!-1.phi_list;
                phi_N := phi_N!-1;
                i := i+1
           >>;

     %Returns {phi_0 to phi_N}.

        return phi_list;

   end;

%######################################################################
%JACOBI AMPLITUDE


%This computes the Amplitude of u.

procedure Amplitude(u,m);
        asin(Jacobisn(u,m));

%~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

operator JacobiAmplitude;

JacobiAMrules :=

{
        JacobiAmplitude(~u,~m) => Amplitude(u,m) when lisp !*rounded
                                         and numberp u and numberp m
}$

let JacobiAMrules;

%######################################################################
%JACOBI FUNCTIONS

%Increases the precision used to evaluate algebraic arguments.

symbolic procedure  Num_JACOBI (u);
% check that length u >= 3 !
 if length u < 3 then
         rederr "illegal call to num_jacobisn" else
   begin scalar oldprec,res;
     oldprec := precision 0;
     precision max(oldprec,15);

    res :=  aeval u;
    precision oldprec;
    return res;

  end;

put('Num_Elliptic, 'psopfn, 'Num_JACOBI);

%######################################################################
%This procedure is called by Jacobisn when the on rounded switch is
%used. It evaluates the value of Jacobisn numerically.


procedure Num_Jacobisn(u,m);

   begin scalar phi0, Jsn;
        phi0 := PART(PHI_function(1,sqrt(1-m),sqrt(m),u),1);
        Jsn := sin(phi0);
        return Jsn
   end;

%~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
%Jacobisn definition
%===================

operator Jacobisn;
operator EllipticK!';
operator EllipticK;

%This rule list includes all the special cases of the Jacobisn
%function.

JacobiSNrules :=
{
%When m=0 or 1, Change of Parameter
%----------------------------------
        Jacobisn(~u,0)   => sin u,
        Jacobisn(~u,1)   => tanh u,
        Jacobisn(~u,-~m) => Jacobisn(u,m),
%Change of argument
%------------------
        Jacobisn(~u + ~v,~m) =>
                ( Jacobisn(u,m)*Jacobicn(v,m)*Jacobidn(v,m)
                + Jacobisn(v,m)*Jacobicn(u,m)*Jacobidn(u,m) )
                / (1-m*((Jacobisn(u,m))^2)*((Jacobisn(v,m))^2)),

        Jacobisn(2*~u,~m) =>
                ( 2*Jacobisn(u,m)*Jacobicn(u,m)*Jacobidn(u,m) )
                / (1-m*((Jacobisn(u,m))^4)),

        Jacobisn(~u/2,~m) =>
                ( 1- Jacobicn(u,m) ) / ( 1 + Jacobidn(u,m) ),


        Jacobisn(-~u,~m) => -Jacobisn(u,m),
        Jacobisn((~u+EllipticK(~m)),~m)   =>  Jacobicd(u,m),
        Jacobisn((~u-EllipticK(~m)),~m)   => -Jacobicd(u,m),
        Jacobisn((EllipticK(~m)-~u),~m)   =>  Jacobicd(u,m),
        Jacobisn((~u+2*EllipticK(~m)),~m) => -Jacobisn(u,m),
        Jacobisn((~u-2*EllipticK(~m)),~m) => -Jacobisn(u,m),
        Jacobisn((2*EllipticK(~m)-~u),~m) =>  Jacobisn(u,m),
        Jacobisn(~u+i*EllipticK!'(~m),~m) => (m^(-1/2))*Jacobins(u,m),

        Jacobisn((~u+2*i*EllipticK!'(~m)),~m) => Jacobisn(u,m),

        Jacobisn((~u+EllipticK(~m)+i*EllipticK!'(~m)),~m) =>
                                              (m^(-1/2))*Jacobidc(u,m),

        Jacobisn((~u+2*EllipticK(~m)+2*i*EllipticK!'(~m)),~m) =>
                                                        -Jacobisn(u,m),
%Special Arguments
%-----------------
        Jacobisn(0,~m) => 0,

        Jacobisn((1/2)*EllipticK(~m),~m) =>1/((1+((1-m)^(1/2)))^(1/2)),

        Jacobisn(EllipticK(~m),~m) => 1,

        Jacobisn((1/2)*i*EllipticK!'(~m),~m) => i*m^(-1/4),

        Jacobisn((1/2)*(EllipticK(~m)+i*EllipticK!'(~m)),~m) =>
                            (2^(-1/2))*m^(-1/4)*(((1+(m^(1/2)))^(1/2))
                                            + i*((1-(m^(1/2)))^(1/2))),

        Jacobisn(EllipticK(~m)+(1/2)*i*EllipticK!'(~m),~m) => m^(-1/4),

        Jacobisn(i*EllipticK!'(~m),~m) => infinity,

        Jacobisn((1/2)*EllipticK(~m)+i*EllipticK!'(~m),~m) =>
                                              (1-((1-m)^(1/2)))^(-1/2),

        Jacobisn(EllipticK(~m)+i*EllipticK!'(~m),~m) => m^(-1/2),


%Derivatives, Integral
%---------------------
        df(Jacobisn(~u,~m),~u)  => Jacobicn(u,m)*Jacobidn(u,m),
        df(Jacobisn(~u,~m),~m)  => (m*Jacobisn(u,m)*Jacobicn(u,m)^2
                        - EllipticE(u,m)*Jacobicn(u,m)*Jacobidn(u,m)/m)
                        / (1-(m^2)) +  u*Jacobicn(u,m)*Jacobidn(u,m)/m,

        int(Jacobisn(~u,~m),~u) =>
                (m^(-1/2))*ln(Jacobidn(u,m)-(m^(1/2))*Jacobicn(u,m)),

%Calls Num_Jacobisn when the rounded switch is on.
%-------------------------------------------------
        Jacobisn(~u,~m) => Num_Elliptic(Num_Jacobisn, u, m)
                           when lisp !*rounded and numberp u
                           and numberp m and IMPART(u) = 0,

        Jacobisn(~u,~m) => Num_Elliptic(complex_SN, u, m)
                           when lisp !*rounded and numberp repart u
                           and numberp impart u and numberp m
                           and IMPART(u) neq 0

}$
let JacobiSNrules;

%......................................................................
%Evaluates Jacobisn when imaginary argument.

operator complex_SN;
SNrule :=
{

        complex_SN(i*~u,~m) => i*Num_Jacobisc(u,1-m),

        complex_SN(~x + i*~y,~m) =>
                ( Num_Jacobisn(x,m)*Num_Jacobidn(y,1-m)
                + i*Num_Jacobicn(x,m)*Num_Jacobidn(x,m)
                   *Num_Jacobisn(y,1-m)*Num_Jacobicn(y,1-m) )
                / (((Num_Jacobicn(y,1-m))^2)+
                   m*((Num_Jacobisn(x,m))^2)*((Num_Jacobisn(y,1-m))^2))
}$
let SNrule;

%~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
%This procedure is called by Jacobicn when the on rounded switch is
%used. It evaluates the value of Jacobicn numerically.

procedure Num_Jacobicn(u,m);

   begin scalar phi0, Jcn;
        phi0 := PART(PHI_function(1,sqrt(1-m),sqrt(m),u),1);
        Jcn := cos(phi0);
        return Jcn
   end;
%~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
%Jacobicn definition
%===================

operator Jacobicn;

%This rule list includes all the special cases of the Jacobicn
%function.

JacobiCNrules :=
{
%When m=0 or 1, Change of Parameter
%----------------------------------
        Jacobicn(~u,0)   => cos u,
        Jacobicn(~u,1)   => sech u,
        Jacobicn(~u,-~m) => Jacobicn(u,m),
%Change of Argument
%------------------
        Jacobicn(~u + ~v,~m) =>
                ( Jacobicn(u,m)*Jacobicn(v,m) - Jacobisn(u,m)
                   *Jacobidn(u,m)*Jacobisn(v,m)*Jacobidn(v,m) )
                / (1 - m*((Jacobisn(u,m))^2)*((Jacobisn(v,m))^2)),

        Jacobicn(2*~u,~m) =>
                ( ((Jacobicn(u,m))^2) - ((Jacobisn(u,m))^2)
                  *((Jacobidn(u,m))^2) )
                / (1- m*((Jacobisn(u,m))^4)),

        Jacobicn(~u/2,~m) =>
                ( Jacobidn(u,m) + Jacobicn(u,m) )
                / ( 1 + Jacobidn(u,m) ),

        Jacobicn(-~u,~m) => Jacobicn (u,m),

        Jacobicn((~u+EllipticK(~m)),~m) =>-((1-m)^(1/2))*Jacobisd(u,m),
        Jacobicn((~u-EllipticK(~m)),~m) => ((1-m)^(1/2))*Jacobisd(u,m),
        Jacobicn((EllipticK(~m)-~u),~m) => ((1-m)^(1/2))*Jacobisd(u,m),
        Jacobicn((~u+2*EllipticK(~m)),~m) => -Jacobicn(u,m),
        Jacobicn((~u-2*EllipticK(~m)),~m) => -Jacobicn(u,m),
        Jacobicn((2*EllipticK(~m)-~u),~m) => -Jacobicn(u,m),
        Jacobicn((~u+i*EllipticK!'(~m)),~m) =>
                                            -i*(m^(-1/2))*Jacobids(u,m),

        Jacobicn((~u+2*i*EllipticK!'(~m)),~m) => -Jacobicn(u,m),

        Jacobicn((~u+EllipticK(~m)+i*EllipticK!'(~m)),~m) =>
                             -i*((1-m)^(1/2))*(m^(-1/2))*Jacobinc(u,m),

        Jacobicn((~u+2*EllipticK(~m)+2*i*EllipticK!'(~m)),~m) =>
                                                         Jacobicn(u,m),
%Special Arguments
%-----------------
        Jacobicn(0,~m) => 1,

        Jacobicn((1/2)*EllipticK(~m),~m) =>
                                 ((1-m)^(1/4))/(1+((1-m)^(1/2)))^(1/2),

        Jacobicn(EllipticK(~m),~m) => 0,

        Jacobicn((1/2)*i*EllipticK!'(~m),~m) =>
                                       ((1+(m^(1/2)))^(1/2))/(m^(1/4)),

        Jacobicn((1/2)*(EllipticK(~m)+i*EllipticK!'(~m)),~m) =>
                                           (((1-m)/(4*m))^(1/4))*(1-i),

        Jacobicn(EllipticK(~m)+(1/2)*i*EllipticK!'(~m),~m) =>
                                  -i*(((1-(m^(1/2)))/(m^(1/2))))^(1/2),

        Jacobicn(i*EllipticK!'(~m),~m) => infinity,

        Jacobicn((1/2)*EllipticK(~m)+i*EllipticK!'(~m),~m) =>
                          -i*((((1-m)^(1/2))/(1-((1-m)^(1/2))))^(1/2)),

        Jacobicn(EllipticK(~m)+i*EllipticK!'(~m),~m) =>
                                                  -i*(((1-m)/m)^(1/2)),
%Derivatives, Integral
%---------------------
        df(Jacobicn(~u,~m),~u)  => -Jacobisn(u,m)*Jacobidn(u,m),
        df(Jacobicn(~u,~m),~m)  => (-m*(Jacobisn(u,m)^2)*Jacobicn(u,m)
                                   + EllipticE(u,m)*Jacobisn(u,m)
                                     *Jacobidn(u,m)/m)/(1-(m^2))
                                   - u*Jacobisn(u,m)*Jacobidn(u,m)/m,

        int(Jacobicn(~u,~m),~u) => (m^(-1/2))*acos(Jacobidn(u,m)),

%Calls Num_Jacobicn when rounded switch is on.
%---------------------------------------------
        Jacobicn(~u,~m) => Num_Elliptic(Num_Jacobicn, u, m)
                           when lisp !*rounded and numberp u
                           and numberp m and IMPART(u) = 0,

        Jacobicn(~u,~m) => Num_Elliptic(complex_CN, u, m)
                           when lisp !*rounded and numberp repart u
                           and numberp impart u and numberp m
                           and IMPART(u) neq 0
}$
let JacobiCNrules;

%......................................................................
%Evaluates Jacobicn when imaginary argument.

operator complex_CN;
CNrule :=
{

        complex_CN(i*~u,~m) => Num_Jacobinc(u,1-m),

        complex_CN(~x + i*~y,~m) =>

                ( Num_Jacobicn(x,m)*Num_Jacobicn(y,1-m)
                - i*Num_Jacobisn(x,m)*Num_Jacobidn(x,m)
                   *Num_Jacobisn(y,1-m)*Num_Jacobidn(y,1-m) )
                / (((Num_Jacobicn(y,1-m))^2)+
                   m*((Num_Jacobisn(x,m))^2)*((Num_Jacobisn(y,1-m))^2))
}$
let CNrule;

%~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
%This procedure is called by Jacobidn when the on rounded switch is
%used. It evaluates the value of Jacobidn numerically.

procedure Num_Jacobidn(u,m);
   begin scalar PHI, phi0,  phi1, numer, denom, Jdn;
        PHI  := PHI_function(1,sqrt(1-m),sqrt(m),u);
        phi0 := PART(PHI,1);
        phi1 := PART(PHI,2);
        numer := cos(phi0);
        denom := cos(phi1 - phi0);

        if denom < 10^(-(Symbolic !:prec!:))
        then  Jdn := otherDN(u,m)
        else  Jdn := numer/denom;
        return Jdn
   end;

procedure otherDN(u,m);
   begin scalar mu, v, dn;
        mu := ((1-((1-m)^(1/2))) / (1+((1-m)^(1/2))))^2;
        v  := u / (1+(mu^(1/2)));

        dn := ((approx(v,mu))^2 - (1-(mu^(1/2))))

                / ((1+(mu^(1/2))) - (approx(v,mu))^2);
        return dn
   end;


procedure approx(u,m);
   begin scalar near;
        near := 1 - (1/2)*m*(sin(u))^2;
        return near
   end;

%~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
%Jacobidn definition
%===================

operator Jacobidn;

%This rule list includes all the special cases of the Jacobidn
%function.

JacobiDNrules :=
{
%When m=0 or 1, Change of Parameter
%----------------------------------
        Jacobidn(~u,0)   => 1,
        Jacobidn(~u,1)   => sech u,
        Jacobidn(~u,-~m) => Jacobidn(u,m),
%Change of Argument
%------------------
        Jacobidn(~u + ~v,~m) =>
                ( Jacobidn(u,m)*Jacobidn(v,m) - m*Jacobisn(u,m)
                   *Jacobicn(u,m)*Jacobisn(v,m)*Jacobicn(v,m) )
                / (1 - m*((Jacobisn(u,m))^2)*((Jacobisn(v,m))^2)),

        Jacobidn(2*~u,~m) =>
                (  ((Jacobidn(u,m))^2) - m*((Jacobisn(u,m))^2)
                  *((Jacobicn(u,m))^2) )
                / (1- m*((Jacobisn(u,m))^4)),

        Jacobidn(~u/2,~m) =>
                ( (1-m) + Jacobidn(u,m) + m*Jacobicn(u,m))
                / ( 1 + Jacobidn(u,m) ),

        Jacobidn(-~u,~m) => Jacobidn(u,m),

        Jacobidn((~u+EllipticK(~m)),~m) => ((1-m)^(1/2))*Jacobind(u,m),
        Jacobidn((~u-EllipticK(~m)),~m) => ((1-m)^(1/2))*Jacobind(u,m),
        Jacobidn((EllipticK(~m)-~u),~m) => ((1-m)^(1/2))*Jacobind(u,m),
        Jacobidn((~u+2*EllipticK(~m)),~m) => Jacobidn(u,m),
        Jacobidn((~u-2*EllipticK(~m)),~m) => Jacobidn(u,m),
        Jacobidn((2*EllipticK(~m)-~u),~m) => Jacobidn(u,m),
        Jacobidn((~u+i*EllipticK!'(~m)),~m)   => -i*Jacobics(u,m),
        Jacobidn((~u+2*i*EllipticK!'(~m)),~m) => -Jacobidn(u,m),

        Jacobidn((~u+EllipticK(~m)+i*EllipticK!'(~m)),~m) =>
                                         i*((1-m)^(1/2))*Jacobisc(u,m),

        Jacobidn((~u+2*EllipticK(~m)+2*i*EllipticK!'(~m)),~m) =>
                                                        -Jacobidn(u,m),
%Special Arguments
%-----------------
        Jacobidn(0,~m) => 1,

        Jacobidn((1/2)*EllipticK(~m),~m) => (1-m)^(1/4),

        Jacobidn(EllipticK(~m),~m) => (1-m)^(1/2),

        Jacobidn((1/2)*i*EllipticK!'(~m),~m) =>   (1+(m^(1/2)))^(1/2),

        Jacobidn((1/2)*(EllipticK(~m)+i*EllipticK!'(~m)),~m) =>
                         (((1-m)/4)^(1/4))*(((1+((1-m)^(1/2)))^(1/2))
                                        - i*((1-((1-m)^(1/2)))^(1/2))),

        Jacobidn(EllipticK(~m)+(1/2)*i*EllipticK!'(~m),~m) =>
                                                   (1-(m^(1/2)))^(1/2),

        Jacobidn(i*EllipticK!'(~m),~m) => infinity,

        Jacobidn((1/2)*EllipticK(~m)+i*EllipticK!'(~m),~m) =>
                                                      -i*((1-m)^(1/4)),

        Jacobidn(EllipticK(~m)+i*EllipticK!'(~m),~m) => 0,

%Derivatives, Intergal
%---------------------
% Following a hint from Alain Moussiaux
%       df(Jacobidn(~u,~m),~u)  => -m *Jacobisn(u,m)*Jacobicn(u,m),
        df(Jacobidn(~u,~m),~u)  => -m**2 *Jacobisn(u,m)*Jacobicn(u,m),
        df(Jacobidn(~u,~m),~m)  => m*(-(Jacobisn(u,m)^2)*Jacobidn(u,m)
                                   + EllipticE(u,m)*Jacobisn(u,m)
                                     *Jacobicn(u,m))/(1-(m^2))
                                   - m*u*Jacobisn(u,m)*Jacobicn(u,m),

        int(Jacobidn(~u,~m),~u) => asin(Jacobisn(u,m)),

%Calls Num_Jacobidn when rounded switch is on.
%---------------------------------------------
        Jacobidn(~u,~m) => Num_Elliptic(Num_Jacobidn, u, m)
                           when lisp !*rounded and numberp u
                           and numberp m and IMPART(u) = 0,

        Jacobidn(~u,~m) => Num_Elliptic(complex_DN, u, m)
                           when lisp !*rounded and numberp repart u
                           and numberp impart u and numberp m
                           and IMPART(u) neq 0
}$
let JacobiDNrules;

%......................................................................
%Evaluates Jacobidn when imaginary argument.

operator complex_DN;
DNrule :=
{       complex_DN(i*~u,~m) => Num_Jacobidc(u,1-m),

        complex_DN(~x + i*~y,~m) =>

        ( Num_Jacobidn(x,m)*Num_Jacobicn(y,1-m)*Num_Jacobidn(y,1-m)
        - i*m*Num_Jacobisn(x,m)*Num_Jacobicn(x,m)*Num_Jacobisn(y,1-m) )
        / ( ((Num_Jacobicn(y,1-m))^2) + m*((Num_Jacobisn(x,m))^2)
                                         *((Num_Jacobisn(y,1-m))^2) )
}$
let DNrule;

%~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
%This procedure is called by Jacobicd when the on rounded switch is
%used. It evaluates the value of Jacobicd numerically.

procedure Num_Jacobicd(u,m);

        Num_Jacobicn(u,m) / Num_Jacobidn(u,m);


%~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
%Jacobicd definition
%===================

operator Jacobicd;

%This rule list includes all the special cases of the Jacobicd
%function.

JacobiCDrules :=
{
%When m=0 or 1, Change of Parameter
%----------------------------------
        Jacobicd(~u,0)   => cos u,
        Jacobicd(~u,1)   => 1,
        Jacobicd(~u,-~m) => Jacobicd(u,m),
%Change of Argument
%------------------
        Jacobicd(-~u,~m)   => Jacobicd(u,m),
        Jacobicd((~u+EllipticK(~m)),~m)   => -Jacobisn(u,m),
        Jacobicd((~u-EllipticK(~m)),~m)   =>  Jacobisn(u,m),
        Jacobicd((EllipticK(~m)-~u),~m)   =>  Jacobisn(u,m),
        Jacobicd((~u+2*EllipticK(~m)),~m) => -Jacobicd(u,m),
        Jacobicd((~u-2*EllipticK(~m)),~m) => -Jacobicd(u,m),
        Jacobicd((2*EllipticK(~m)-~u),~m) => -Jacobicd(u,m),
        Jacobicd((~u+i*EllipticK!'(~m)),~m) =>
                                              (m^(-1/2))*Jacobidc(u,m),

        Jacobicd((~u+2*i*EllipticK!'(~m)),~m) => Jacobicd(u,m),

        Jacobicd((~u+EllipticK(~m)+i*EllipticK!'(~m)),~m) =>
                                             -(m^(-1/2))*Jacobins(u,m),

        Jacobicd((~u+2*EllipticK(~m)+2*i*EllipticK!'(~m)),~m) =>
                                                        -Jacobicd(u,m),
%Special Arguments
%-----------------
        Jacobicd(0,~m) => 1,

        Jacobicd((1/2)*EllipticK(~m),~m) => 1 /(1+((1-m)^(1/2)))^(1/2),

        Jacobicd(EllipticK(~m),~m) => 0,

        Jacobicd((1/2)*i*EllipticK!'(~m),~m) => 1/(m^(1/4)),

        Jacobicd((1/2)*(EllipticK(~m)+i*EllipticK!'(~m)),~m) =>
                           (1-i)/((m^(1/4))*(((1+((1-m)^(1/2)))^(1/2))
                                        -i*((1-((1-m)^(1/2)))^(1/2)))),

        Jacobicd(EllipticK(~m)+(1/2)*i*EllipticK!'(~m),~m) =>
                                                          -i/(m^(1/4)),

        Jacobicd(i*EllipticK!'(~m),~m) =>
                                        Jacobicn(i*EllipticK!'(~m),~m)
                                      / Jacobidn(i*EllipticK!'(~m),~m),

        Jacobicd((1/2)*EllipticK(~m)+i*EllipticK!'(~m),~m) =>
                                           1/((1-((1-m)^(1/2)))^(1/2)),

        Jacobicd(EllipticK(~m)+i*EllipticK!'(~m),~m) => infinity,

%Derivatives,Integral
%--------------------
        df(Jacobicd(~u,~m),~u) => -(1 - m)*Jacobisd(u,m)*Jacobind(u,m),
        df(Jacobicd(~u,~m),~m) =>
                                ( Jacobidn(u,m)*df(Jacobicn(u,m),m)
                                - Jacobicn(u,m)*df(Jacobidn(u,m),m))
                                / ((Jacobidn(u,m))^2),

        int(Jacobicd(~u,~m),~u) =>
                m^(-1/2)*ln(Jacobind(u,m) + (m^(1/2))*Jacobisd(u,m)),

%Calls Num_Jacobicd when rounded switch is on.
%---------------------------------------------
        Jacobicd(~u,~m) => Num_Elliptic(Num_Jacobicd, u, m)
                           when lisp !*rounded and numberp u
                           and numberp m
}$
let JacobiCDrules;

%~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
%This procedure is called by Jacobisd when the on rounded switch is
%used. It evaluates the value of Jacobisd numerically.

procedure Num_Jacobisd(u,m);

   Num_Jacobisn(u,m) / Num_Jacobidn(u,m);

%~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
%Jacobisd definition
%===================

operator Jacobisd;

%This rule list includes all the special cases of the Jacobisd
%function.

JacobiSDrules :=
{
%When m=0 or 1, Change of Parameter
%----------------------------------
        Jacobisd(~u,0)   => sin u,
        Jacobisd(~u,1)   => sinh u,
        Jacobisd(~u,-~m) => Jacobisd(u,m),
%Change of Argument
%------------------
        Jacobisd(-~u,~m)   => -Jacobisd(u,m),

        Jacobisd((~u+EllipticK(~m)),~m) =>((1-m)^(-1/2))*Jacobicn(u,m),
        Jacobisd((~u-EllipticK(~m)),~m) => -((1-m)^(-1/2))
                                                        *Jacobicn(u,m),

        Jacobisd((EllipticK(~m)-~u),~m) =>((1-m)^(-1/2))*Jacobicn(u,m),

        Jacobisd((~u+2*EllipticK(~m)),~m) => -Jacobisd(u,m),
        Jacobisd((~u-2*EllipticK(~m)),~m) => -Jacobisd(u,m),
        Jacobisd((2*EllipticK(~m)-~u),~m) =>  Jacobisd(u,m),

        Jacobisd((~u+i*EllipticK!'(~m)),~m) =>
                                             i*(m^(-1/2))*Jacobinc(u,m),

        Jacobisd((~u+2*i*EllipticK!'(~m)),~m) => -Jacobisd(u,m),

        Jacobisd((~u+EllipticK(~m)+i*EllipticK!'(~m)),~m) =>
                          -i*((1-m)^(-1/2))*(m^(-1/2))*Jacobids(u,m),

        Jacobisd((~u+2*EllipticK(~m)+2*i*EllipticK!'(~m)),~m) =>
                                                         Jacobisd(u,m),
%Special Arguments
%-----------------
        Jacobisd(0,~m) => 0,

        Jacobisd((1/2)*EllipticK(~m),~m) =>
                         1 / (((1+((1-m)^(1/2)))^(1/2))*((1-m)^(1/4))),

        Jacobisd(EllipticK(~m),~m) => 1/((1-m)^(1/2)),

        Jacobisd((1/2)*i*EllipticK!'(~m),~m) =>
                                    i*(m^(-1/4))/((1+(m^(1/2)))^(1/2)),

        Jacobisd((1/2)*(EllipticK(~m)+i*EllipticK!'(~m)),~m) =>

                Jacobisn((1/2)*(EllipticK(~m)+i*EllipticK!'(~m)),~m)
                / Jacobidn((1/2)*(EllipticK(~m)+i*EllipticK!'(~m)),~m),

        Jacobisd(EllipticK(~m)+(1/2)*i*EllipticK!'(~m),~m) =>
                                        (m^(-1/4))/(1-(m^(1/2))^(1/2)),

        Jacobisd(i*EllipticK!'(~m),~m) =>
                                        Jacobisn(i*EllipticK!'(~m),~m)
                                      / Jacobidn(i*EllipticK!'(~m),~m),

        Jacobisd((1/2)*EllipticK(~m)+i*EllipticK!'(~m),~m) =>
                         ((1-((1-m)^(1/2)))^(-1/2))/(-i*((1-m)^(1/4))),

        Jacobisd(EllipticK(~m)+i*EllipticK!'(~m),~m) => infinity,

%Derivatives, Integral
%---------------------
        df(Jacobisd(~u,~m),~u) => Jacobicd(u,m)*Jacobind(u,m),
        df(Jacobisd(~u,~m),~m) =>
                                ( Jacobidn(u,m)*df(Jacobisn(u,m),m)
                                - Jacobisn(u,m)*df(Jacobidn(u,m),m))
                                / ((Jacobidn(u,m))^2),

        int(Jacobisd(~u,~m),~u) =>
                   (m*(1-m))^(-1/2)*asin(-(m^(1/2))*(Jacobicd(u,m))),

%Calls Num_Jacobisd when rounded switch is on.
%---------------------------------------------
        Jacobisd(~u,~m) => Num_Elliptic(Num_Jacobisd, u, m)
                           when lisp !*rounded and numberp u
                           and numberp m
}$
let JacobiSDrules;

%~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
%This procedure is called by Jacobind when the on rounded switch is
%used. It evaluates the value of Jacobind numerically.

procedure Num_Jacobind(u,m);

        1 / Num_Jacobidn(u,m);

%~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
%Jacobind definition
%===================

operator Jacobind;

%This rule list includes all the special cases of the Jacobind
%function.

JacobiNDrules :=
{
%When m=0 or 1, Change of Parameter
%----------------------------------
        Jacobind(~u,0)   => 1,
        Jacobind(~u,1)   => cosh u,
        Jacobind(~u,-~m) => Jacobind(u,m),
%Change of Argument
%------------------
        Jacobind(-~u,~m)   => Jacobind(u,m),

        Jacobind((~u+EllipticK(~m)),~m) =>((1-m)^(-1/2))*Jacobidn(u,m),
        Jacobind((~u-EllipticK(~m)),~m) =>((1-m)^(-1/2))*Jacobidn(u,m),
        Jacobind((EllipticK(~m)-~u),~m) =>((1-m)^(-1/2))*Jacobidn(u,m),

        Jacobind((~u+2*EllipticK(~m)),~m) => Jacobind(u,m),
        Jacobind((~u-2*EllipticK(~m)),~m) => Jacobind(u,m),
        Jacobind((2*EllipticK(~m)-~u),~m) => Jacobind(u,m),

        Jacobind((~u+i*EllipticK!'(~m)),~m)   => i*Jacobisc(u,m),
        Jacobind((~u+2*i*EllipticK!'(~m)),~m) => -Jacobind(u,m),

        Jacobind((~u+EllipticK(~m)+i*EllipticK!'(~m)),~m) =>
                                       -i*((1-m)^(-1/2))*Jacobics(u,m),

        Jacobind((~u+2*EllipticK(~m)+2*i*EllipticK!'(~m)),~m) =>
                                                        -Jacobind(u,m),
%Special Arguments
%-----------------
        Jacobind(0,~m) => 1,

        Jacobind((1/2)*EllipticK(~m),~m) => 1 / ((1-m)^(1/4)),

        Jacobind(EllipticK(~m),~m) => 1 / ((1-m)^(1/2)),

        Jacobind((1/2)*i*EllipticK!'(~m),~m) =>
                                               1/((1+(m^(1/2)))^(1/2)),

        Jacobind((1/2)*(EllipticK(~m)+i*EllipticK!'(~m)),~m) =>

                1/Jacobidn((1/2)*(EllipticK(~m)+i*EllipticK!'(~m)),~m),

        Jacobind(EllipticK(~m)+(1/2)*i*EllipticK!'(~m),~m) =>
                                               1/((1-(m^(1/2)))^(1/2)),

        Jacobind(i*EllipticK!'(~m),~m) =>
                                    1 / Jacobidn(i*EllipticK!'(~m),~m),

        Jacobind((1/2)*EllipticK(~m)+i*EllipticK!'(~m),~m) =>
                                                1 / (-i*((1-m)^(1/4))),

        Jacobind(EllipticK(~m)+i*EllipticK!'(~m),~m) => infinity,

%Derivatives, Integral
%---------------------
        df(Jacobind(~u,~m),~u) => m*Jacobisd(u,m)*Jacobicd(u,m),
        df(Jacobind(~u,~m),~m) =>
                            -(df(Jacobidn(u,m),m))/((Jacobidn(u,m))^2),

        int(Jacobind(~u,~m),~u) => (1-m)^(-1/2)*(acos(Jacobicd(u,m))),

%Calls Num_Jacobind when rounded switch is on.
%---------------------------------------------
        Jacobind(~u,~m) => Num_Elliptic(Num_Jacobind, u, m)
                           when lisp !*rounded and numberp u
                           and numberp m
}$
let JacobiNDrules;

%~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
%This procedure is called by Jacobidc when the on rounded switch is
%used. It evaluates the value of Jacobidc numerically.

procedure Num_Jacobidc(u,m);

        Num_Jacobidn(u,m) / Num_Jacobicn(u,m);

%~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
%Jacobidc definition
%===================

operator Jacobidc;

%This rule list includes all the special cases of the Jacobidc
%function.

JacobiDCrules :=
{
%When m=0 or 1, Change of Parameter
%----------------------------------
        Jacobidc(~u,0)   => sec u,
        Jacobidc(~u,1)   => 1,
        Jacobidc(~u,-~m) => Jacobidc(u,m),
%Change of Argument
%------------------
        Jacobidc(-~u,~m)   => Jacobidc(u,m),

        Jacobidc((~u+EllipticK(~m)),~m) => -Jacobins(u,m),
        Jacobidc((~u-EllipticK(~m)),~m) =>  Jacobidns(u,m),
        Jacobidc((EllipticK(~m)-~u),~m) =>  Jacobins(u,m),
        Jacobidc((~u+2*EllipticK(~m)),~m)   => -Jacobidc(u,m),
        Jacobidc((~u-2*EllipticK(~m)),~m)   => -Jacobidc(u,m),
        Jacobidc((2*EllipticK(~m)-~u),~m)   => -Jacobidc(u,m),
        Jacobidc((~u+i*EllipticK!'(~m)),~m) => (m^(1/2))*Jacobicd(u,m),
        Jacobidc((~u+2*i*EllipticK!'(~m)),~m) => Jacobidc(u,m),

        Jacobidc((~u+EllipticK(~m)+i*EllipticK!'(~m)),~m) =>
                                               (m^(1/2))*Jacobisn(u,m),

        Jacobidc((~u+2*EllipticK(~m)+2*i*EllipticK!'(~m)),~m) =>
                                                        -Jacobidc(u,m),
%Special Arguments
%-----------------
        Jacobidc(0,~m) => 1,

        Jacobidc((1/2)*EllipticK(~m),~m) => (1+((1-m)^(1/2)))^(1/2),

        Jacobidc(EllipticK(~m),~m) => infinity,

        Jacobidc((1/2)*i*EllipticK!'(~m),~m) => m^(1/4),

        Jacobidc((1/2)*(EllipticK(~m)+i*EllipticK!'(~m)),~m) =>
                Jacobidn((1/2)*(EllipticK(~m)+i*EllipticK!'(~m)),~m)
                / Jacobicn((1/2)*(EllipticK(~m)+i*EllipticK!'(~m)),~m),

        Jacobidc(EllipticK(~m)+(1/2)*i*EllipticK!'(~m),~m) =>
                                                           i*(m^(1/4)),

        Jacobidc(i*EllipticK!'(~m),~m) =>
                                        Jacobidn(i*EllipticK!'(~m),~m)
                                      / Jacobicn(i*EllipticK!'(~m),~m),

        Jacobidc((1/2)*EllipticK(~m)+i*EllipticK!'(~m),~m) =>
                                               (1-((1-m)^(1/2)))^(1/2),

        Jacobidc(EllipticK(~m)+i*EllipticK!'(~m),~m) => 0,

%Derivatives, Integral
%---------------------
        df(Jacobidc(~u,~m),~u) => (1-m)*Jacobisc(u,m)*Jacobinc(u,m),
        df(Jacobidc(~u,~m),~m) =>
                                (Jacobicn(u,m)*df(Jacobidn(u,m),m)
                                - Jacobidn(u,m)*df(Jacobicn(u,m),m))
                                / ((Jacobicn(u,m))^2),

        int(Jacobidc(~u,~m),~u) => ln(Jacobinc(u,m) + Jacobisc(u,m)),

%Calls Num_Jacobidc when rounded switch is on.
%---------------------------------------------
        Jacobidc(~u,~m) => Num_Elliptic(Num_Jacobidc, u, m)
                           when lisp !*rounded and numberp u
                           and numberp m
}$
let JacobiDCrules;

%~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
%This procedure is called by Jacobinc when the on rounded switch is
%used. It evaluates the value of Jacobinc numerically.

procedure Num_Jacobinc(u,m);

        1 / Num_Jacobicn(u,m);

%~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
%Jacobinc definition
%===================

operator Jacobinc;

%This rule list includes all the special cases of the Jacobinc
%function.

JacobiNCrules :=
{
%When m=0 or 1, Change of Parameter
%----------------------------------
        Jacobinc(~u,0)   => sec u,
        Jacobinc(~u,1)   => cosh u,
        Jacobinc(~u,-~m) => Jacobinc(u,m),
%Change of Argument
%------------------
        Jacobinc(-~u,~m)   => Jacobinc(u,m),

        Jacobinc((~u+EllipticK(~m)),~m) => -((1-m)^(-1/2))
                                                        *Jacobids(u,m),

        Jacobinc((~u-EllipticK(~m)),~m) =>((1-m)^(-1/2))*Jacobids(u,m),
        Jacobinc((EllipticK(~m)-~u),~m) =>((1-m)^(-1/2))*Jacobids(u,m),

        Jacobinc((~u+2*EllipticK(~m)),~m) => -Jacobinc(u,m),
        Jacobinc((~u-2*EllipticK(~m)),~m) => -Jacobinc(u,m),
        Jacobinc((2*EllipticK(~m)-~u),~m) => -Jacobinc(u,m),
        Jacobinc((~u+i*EllipticK!'(~m)),~m) =>
                                             i*(m^(1/2))*Jacobisd(u,m),

        Jacobinc((~u+2*i*EllipticK!'(~m)),~m) => -Jacobinc(u,m),

        Jacobinc((~u+EllipticK(~m)+i*EllipticK!'(~m)),~m) =>
                            i*((1-m)^(-1/2))*(m^(1/2))*Jacobicn(u,m),

        Jacobinc((~u+2*EllipticK(~m)+2*i*EllipticK!'(~m)),~m) =>
                                                         Jacobinc(u,m),
%Special Arguments
%-----------------
        Jacobinc(0,~m) => 1,

        Jacobinc((1/2)*EllipticK(~m),~m) => ((1+((1-m)^(1/2)))^(1/2))
                                                        /((1-m)^(1/4)),

        Jacobinc(EllipticK(~m),~m) => infinity,

        Jacobinc((1/2)*i*EllipticK!'(~m),~m) =>
                                       (m^(1/4))/((1+(m^(1/2)))^(1/2)),

        Jacobinc((1/2)*(EllipticK(~m)+i*EllipticK!'(~m)),~m) =>
                                             ((4*m/(1-m))^(1/4))/(1-i),

        Jacobinc(EllipticK(~m)+(1/2)*i*EllipticK!'(~m),~m) =>
                1 / Jacobicn(EllipticK(~m)+(1/2)*i*EllipticK!'(~m),~m),

        Jacobinc(i*EllipticK!'(~m),~m) =>
                                    1 / Jacobicn(i*EllipticK!'(~m),~m),

        Jacobinc((1/2)*EllipticK(~m)+i*EllipticK!'(~m),~m) =>
                1 / Jacobicn((1/2)*EllipticK(~m)+i*EllipticK!'(~m),~m),

        Jacobinc(EllipticK(~m)+i*EllipticK!'(~m),~m) =>
                                                   i*((m/(1-m))^(1/2)),
%Derivatives, Integral
%---------------------
        df(Jacobinc(~u,~m),~u) => Jacobisc(u,m)*Jacobidc(u,m),
        df(Jacobinc(~u,~m),~m) =>
                            -(df(Jacobicn(u,m),m))/((Jacobicn(u,m))^2),

        int(Jacobinc(~u,~m),~u) =>

         ((1-m)^(-1/2))*ln(Jacobidc(u,m)+((1-m)^(1/2))*Jacobisc(u,m)),

%Calls Num_Jacobinc when rounded switch is on.
%---------------------------------------------
        Jacobinc(~u,~m) => Num_Elliptic(Num_Jacobinc, u, m)
                           when lisp !*rounded and numberp u
                           and numberp m
}$
let JacobiNCrules;

%~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
%This procedure is called by Jacobisc when the on rounded switch is
%used. It evaluates the value of Jacobisc numerically.

procedure Num_Jacobisc(u,m);

        Num_Jacobisn(u,m) / Num_Jacobicn(u,m);

%~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
%Jacobisc definition
%===================

operator Jacobisc;

%This rule list includes all the special cases of the Jacobisc
%function.

JacobiSCrules :=
{
%When m=0 or 1, Change of Parameter
%----------------------------------
        Jacobisc(~u,0)   => tan u,
        Jacobisc(~u,1)   => sinh u,
        Jacobisc(~u,-~m) => Jacobisc(u,m),
%Change of Argument
%------------------
        Jacobisc(-~u,~m)   => -Jacobisc(u,m),

        Jacobisc((~u+EllipticK(~m)),~m) => -((1-m)^(-1/2))
                                                        *Jacobics(u,m),

        Jacobisc((~u-EllipticK(~m)),~m) => -((1-m)^(-1/2))
                                                        *Jacobics(u,m),

        Jacobisc((EllipticK(~m)-~u),~m) =>((1-m)^(-1/2))*Jacobics(u,m),

        Jacobisc((~u+2*EllipticK(~m)),~m) =>  Jacobisc(u,m),
        Jacobisc((~u-2*EllipticK(~m)),~m) =>  Jacobisc(u,m),
        Jacobisc((2*EllipticK(~m)-~u),~m) => -Jacobisc(u,m),
        Jacobisc((~u+i*EllipticK!'(~m)),~m)   =>i*Jacobind(u,m),
        Jacobisc((~u+2*i*EllipticK!'(~m)),~m) => -Jacobisc(u,m),

        Jacobisc((~u+EllipticK(~m)+i*EllipticK!'(~m)),~m) =>
                                        i*((1-m)^(-1/2))*Jacobidn(u,m),

        Jacobisc((~u+2*EllipticK(~m)+2*i*EllipticK!'(~m)),~m) =>
                                                        -Jacobisc(u,m),
%Special Arguments
%-----------------
        Jacobisc(0,~m) => 0,

        Jacobisc((1/2)*EllipticK(~m),~m) => 1 / ((1-m)^(1/4)),

        Jacobisc(EllipticK(~m),~m) => infinity,

        Jacobisc((1/2)*i*EllipticK!'(~m),~m) =>
                                               i/((1+(m^(1/2)))^(1/2)),

        Jacobisc((1/2)*(EllipticK(~m)+i*EllipticK!'(~m)),~m) =>

                Jacobisn((1/2)*(EllipticK(~m)+i*EllipticK!'(~m)),~m)
                / Jacobicn((1/2)*(EllipticK(~m)+i*EllipticK!'(~m)),~m),

        Jacobisc(EllipticK(~m)+(1/2)*i*EllipticK!'(~m),~m) =>
                                               i/((1-(m^(1/2)))^(1/2)),

        Jacobisc(i*EllipticK!'(~m),~m) =>
                                      Jacobisn(i*EllipticK!'(~m),~m)
                                      / Jacobicn(i*EllipticK!'(~m),~m),

        Jacobisc((1/2)*EllipticK(~m)+i*EllipticK!'(~m),~m) =>

                  Jacobisn((1/2)*EllipticK(~m)+i*EllipticK!'(~m),~m)
                  / Jacobicn((1/2)*EllipticK(~m)+i*EllipticK!'(~m),~m),

        Jacobisc(EllipticK(~m)+i*EllipticK!'(~m),~m) =>i/((1-m)^(1/2)),


%Derivatives, Integral
%---------------------
        df(Jacobisc(~u,~m),~u) => Jacobidc(u,m)*Jacobinc(u,m),
        df(Jacobisc(~u,~m),~m) =>
                                ( Jacobicn(u,m)*df(Jacobisn(u,m),m)
                                - Jacobisn(u,m)*df(Jacobicn(u,m),m))
                                /((Jacobicn(u,m))^2),

        int(Jacobisc(~u,~m),u) =>

          ((1-m)^(-1/2))*ln(Jacobidc(u,m)+((1-m)^(1/2))*Jacobinc(u,m)),

%Calls Num_Jacobisc when rounded switch is on.
%---------------------------------------------
        Jacobisc(~u,~m) => Num_Elliptic(Num_Jacobisc, u, m)
                           when lisp !*rounded and numberp u
                           and numberp m
}$
let JacobiSCrules;

%~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
%This procedure is called by Jacobins when the on rounded switch is
%used. It evaluates the value of Jacobins numerically.

procedure Num_Jacobins(u,m);

        1 / Num_Jacobisn(u,m);

%~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
%Jacobins definition
%===================

operator Jacobins;

%This rule list includes all the special cases of the Jacobins
%function.

JacobiNSrules :=
{
%When m=0 or 1, Change of Parameter
%----------------------------------
        Jacobins(~u,0)   => csc u,
        Jacobins(~u,1)   => coth u,
        Jacobins(~u,-~m) => Jacobins(u,m),
%Change of Argument
%------------------
        Jacobins(-~u,~m)   => -Jacobins(u,m),

        Jacobins((~u+EllipticK(~m)),~m)   =>  Jacobidc(u,m),
        Jacobins((~u-EllipticK(~m)),~m)   => -Jacobidc(u,m),
        Jacobins((EllipticK(~m)-~u),~m)   =>  Jacobidc(u,m),
        Jacobins((~u+2*EllipticK(~m)),~m) => -Jacobins(u,m),
        Jacobins((~u-2*EllipticK(~m)),~m) => -Jacobins(u,m),
        Jacobins((2*EllipticK(~m)-~u),~m) =>  Jacobins(u,m),
        Jacobins((~u+i*EllipticK!'(~m)),~m) => (m^(1/2))*Jacobisn(u,m),
        Jacobins((~u+2*i*EllipticK!'(~m)),~m) => Jacobins(u,m),
        Jacobins((~u+EllipticK(~m)+i*EllipticK!'(~m)),~m) =>
                                               (m^(1/2))*Jacobicd(u,m),

        Jacobins((~u+2*EllipticK(~m)+2*i*EllipticK!'(~m)),~m) =>
                                                        -Jacobins(u,m),
%Special Arguments
%-----------------
        Jacobins(0,~m) => infinity,

        Jacobins((1/2)*EllipticK(~m),~m) => (1+((1-m)^(1/2)))^(1/2),

        Jacobins(EllipticK(~m),~m) => 1,

        Jacobins((1/2)*i*EllipticK!'(~m),~m) => -i*(m^(1/4)),

        Jacobins((1/2)*(EllipticK(~m)+i*EllipticK!'(~m)),~m) =>

                1/Jacobisn((1/2)*(EllipticK(~m)+i*EllipticK!'(~m)),~m),

        Jacobins(EllipticK(~m)+(1/2)*i*EllipticK!'(~m),~m) =>(m^(1/4)),

        Jacobins(i*EllipticK!'(~m),~m) =>

                                      1/Jacobisn(i*EllipticK!'(~m),~m),

        Jacobins((1/2)*EllipticK(~m)+i*EllipticK!'(~m),~m) =>

                                               (1-((1-m)^(1/2)))^(1/2),

        Jacobins(EllipticK(~m)+i*EllipticK!'(~m),~m) => m^(1/2),
%Derivatives, Integral
%---------------------
        df(Jacobins(~u,~m),~u) => -Jacobids(u,m)*Jacobics(u,m),
        df(Jacobins(~u,~m),~m) =>
                            -(df(Jacobisn(u,m),m))/((Jacobisn(u,m))^2),

        int(Jacobins(~u,~m),~u) => ln(Jacobids(u,m) - Jacobics(u,m)),

%Calls Num_Jacobins when rounded switch is on.
%---------------------------------------------
        Jacobins(~u,~m) => Num_Elliptic(Num_Jacobins, u, m)
                           when lisp !*rounded and numberp u
                           and numberp m
}$
let JacobiNSrules;

%~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
%This procedure is called by Jacobids when the on rounded switch is
%used. It evaluates the value of Jacobids numerically.

procedure Num_Jacobids(u,m);

        Num_Jacobidn(u,m) / Num_Jacobisn(u,m);

%~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
%Jacobids definition
%===================

operator Jacobids;

%This rule list includes all the special cases of the Jacobids
%function.

JacobiDSrules :=
{
%When m=0 or 1, Change of Parameter
%----------------------------------
        Jacobids(~u,0)   => csc u,
        Jacobids(~u,1)   => csch u,
        Jacobids(~u,-~m) => Jacobids(u,m),
%Change of Argument
%------------------
        Jacobids(-~u,~m)   =>-Jacobids(u,m),

        Jacobids((~u+EllipticK(~m)),~m) => ((1-m)^(1/2))*Jacobinc(u,m),
        Jacobids((~u-EllipticK(~m)),~m) =>-((1-m)^(1/2))*Jacobinc(u,m),
        Jacobids((EllipticK(~m)-~u),~m) => ((1-m)^(1/2))*Jacobinc(u,m),

        Jacobids((~u+2*EllipticK(~m)),~m) => -Jacobids(u,m),
        Jacobids((~u-2*EllipticK(~m)),~m) => -Jacobids(u,m),
        Jacobids((2*EllipticK(~m)-~u),~m) =>  Jacobids(u,m),
        Jacobids((~u+i*EllipticK!'(~m)),~m) =>
                                             -i*(m^(1/2))*Jacobicn(u,m),

        Jacobids((~u+2*i*EllipticK!'(~m)),~m) => -Jacobids(u,m),

        Jacobids((~u+EllipticK(~m)+i*EllipticK!'(~m)),~m) =>
                               i*((1-m)^(1/2))*(m^(1/2))*Jacobisd(u,m),

        Jacobids((~u+2*EllipticK(~m)+2*i*EllipticK!'(~m)),~m) =>
                                                         Jacobids(u,m),
%Special Arguments
%-----------------
        Jacobids(0,~m) => infinity,

        Jacobids((1/2)*EllipticK(~m),~m) =>
                               ((1+((1-m)^(1/2)))^(1/2))*((1-m)^(1/4)),

        Jacobids(EllipticK(~m),~m) => (1-m)^(1/2),

        Jacobids((1/2)*i*EllipticK!'(~m),~m) =>
                                    -i*(m^(1/4))*((1+(m^(1/2)))^(1/2)),

        Jacobids((1/2)*(EllipticK(~m)+i*EllipticK!'(~m)),~m) =>

                Jacobidn((1/2)*(EllipticK(~m)+i*EllipticK!'(~m)),~m)
                / Jacobisn((1/2)*(EllipticK(~m)+i*EllipticK!'(~m)),~m),

        Jacobids(EllipticK(~m)+(1/2)*i*EllipticK!'(~m),~m) =>
                                       (m^(1/4))*((1-(m^(1/2)))^(1/2)),

        Jacobids(i*EllipticK!'(~m),~m) =>
                                      Jacobidn(i*EllipticK!'(~m),~m)
                                      / Jacobisn(i*EllipticK!'(~m),~m),

        Jacobids((1/2)*EllipticK(~m)+i*EllipticK!'(~m),~m) =>
                            -i*((1-m)^(1/4))*((1-((1-m)^(1/2)))^(1/2)),

        Jacobids(EllipticK(~m)+i*EllipticK!'(~m),~m) => 0,

%Derivatives, Integral
%---------------------
        df(Jacobids(~u,~m),~u) => -Jacobics(u,m)*Jacobins(u,m),
        df(Jacobids(~u,~m),~m) =>
                                (Jacobisn(u,m)*df(Jacobidn(u,m),m)
                                - Jacobidn(u,m)*df(Jacobisn(u,m),m))
                                / ((Jacobisn(u,m))^2),

        int(Jacobids(~u,~m),~u) => ln(Jacobins(u,m) - Jacobics(u,m)),

%Calls Num_Jacobids when on rounded switch is on.
%------------------------------------------------
        Jacobids(~u,~m) => Num_Elliptic(Num_Jacobids, u, m)
                           when lisp !*rounded and numberp u
                           and numberp m
}$
let JacobiDSrules;

%~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
%This procedure is called by Jacobics when the on rounded switch is
%used. It evaluates the value of Jacobics numerically.

procedure Num_Jacobics(u,m);

        Num_Jacobicn(u,m) / Num_Jacobisn(u,m);

%~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
%Jacobics definition
%===================

operator Jacobics;

%This rule list includes all the special cases of the Jacobics
%function.

JacobiCSrules :=
{
%When m=0 or 1, Change of Parameter
%----------------------------------
        Jacobics(~u,0)   => cot u,
        Jacobics(~u,1)   => csch u,
        Jacobics(~u,-~m) => Jacobics(u,m),
%Change of Argument
%------------------
        Jacobics(-~u,~m)   =>-Jacobics(u,m),

        Jacobics((~u+EllipticK(~m)),~m) =>-((1-m)^(1/2))*Jacobisc(u,m),
        Jacobics((~u-EllipticK(~m)),~m) =>-((1-m)^(1/2))*Jacobisc(u,m),
        Jacobics((EllipticK(~m)-~u),~m) => ((1-m)^(1/2))*Jacobisc(u,m),
        Jacobics((~u+2*EllipticK(~m)),~m) =>  Jacobics(u,m),
        Jacobics((~u-2*EllipticK(~m)),~m) =>  Jacobics(u,m),
        Jacobics((2*EllipticK(~m)-~u),~m) => -Jacobics(u,m),
        Jacobics((~u+i*EllipticK!'(~m)),~m)   => -i*Jacobidn(u,m),
        Jacobics((~u+2*i*EllipticK!'(~m)),~m) => -Jacobics(u,m),

        Jacobics((~u+EllipticK(~m)+i*EllipticK!'(~m)),~m) =>
                                        -i*((1-m)^(1/2))*Jacobind(u,m),

        Jacobics((~u+2*EllipticK(~m)+2*i*EllipticK!'(~m)),~m) =>
                                                        -Jacobics(u,m),
%Special Arguments
%-----------------
        Jacobics(0,~m) => infinity,

        Jacobics((1/2)*EllipticK(~m),~m) => (1-m)^(1/4),

        Jacobics(EllipticK(~m),~m) => 0,

        Jacobics((1/2)*i*EllipticK!'(~m),~m) =>
                                              -i*((1+(m^(1/2)))^(1/2)),

        Jacobics((1/2)*(EllipticK(~m)+i*EllipticK!'(~m)),~m) =>

                Jacobicn((1/2)*(EllipticK(~m)+i*EllipticK!'(~m)),~m)
                / Jacobisn((1/2)*(EllipticK(~m)+i*EllipticK!'(~m)),~m),

        Jacobics(EllipticK(~m)+(1/2)*i*EllipticK!'(~m),~m) =>
                                              -i*((1-(m^(1/2)))^(1/2)),

        Jacobics(i*EllipticK!'(~m),~m) =>
                                      Jacobicn(i*EllipticK!'(~m),~m)
                                      / Jacobisn(i*EllipticK!'(~m),~m),

        Jacobics((1/2)*EllipticK(~m)+i*EllipticK!'(~m),~m) =>
                                                      -i*((1-m)^(1/4)),

        Jacobics(EllipticK(~m)+i*EllipticK!'(~m),~m) =>
                                                       -i*((1-m)^(1/2)),
%Derivatives, Integral
%---------------------
        df(Jacobics(~u,~m),~u) => -Jacobins(u,m)*Jacobids(u,m),
        df(Jacobics(~u,~m),~m) =>
                                ( Jacobisn(u,m)*df(Jacobicn(u,m),m)
                                - Jacobicn(u,m)*df(Jacobisn(u,m),m))
                                / ((Jacobisn(u,m))^2),

        int(Jacobics(~u,~m),~u) => ln(Jacobins(u,m) - Jacobids(u,m)),

%Calls Num_Jacobics when rounded switch is on.
%---------------------------------------------
        Jacobics(~u,~m) => Num_Elliptic(Num_Jacobics, u, m)
                           when lisp !*rounded and numberp u
                           and numberp m
}$
let JacobiCSrules;

endmodule;

end;
