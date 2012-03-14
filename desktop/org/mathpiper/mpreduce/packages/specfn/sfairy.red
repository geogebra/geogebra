module sfairy;  % Procedures and Rules for the Airy functions.

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


%***********************************************************************
%
%The following is the code to evaluate Airy Functions and their primes
%using REDUCE.
%
%Author: Stephen Scowcroft                        Date: September 1994
%
%***********************************************************************

%The first section deals with code that evaluates the Airy Functions.
%The second deals with code that evaluates the Airyprime Functions.

%For the sake of efficiency a recursive approach has been taken for all
%expressions. As a result the equations do not directly resemble those
%given in "The Handbook of Mathematical Functions" (Abramowitz & Stegun)
%although this is the source of the expressions.

% The following procedures evaluate the fseries and gseries which are
% used in the ascending series approach to calculating Airy_Ai and
% Airy_Bi.

algebraic procedure myfseries(z);

%Declared local variables used throughout the procedure.

  begin scalar summ,accu,term,zcube,int1,int2;

%These are the initial values of variables used in the procedure.

     summ := 1;
     int1 := 2;
     int2 := 3;
     accu := 10 ^(-(symbolic !:prec!:));
     term := 1;
     zcube := (z ^ 3);

%This loop calculates term without a check with the accuracy. As a
%result the code is faster and more efficient.

     for kk:=0:30 do
     <<  term := term * zcube / ((int1) * (int2));
         summ := summ + term;
         int1 := int1 + 3;
         int2 := int2 + 3;
      >>;

%Now the check against the accuracy is carried out in order to bring the
% infinite sum to an approximate summation for use later on.

     while abs(term) > accu do
     <<  term := term * zcube / ((int1) * (int2));
         summ := summ + term;
         int1 := int1 + 3;
         int2 := int2 + 3;
      >>;

%The value of the infinite sum is then returned for use in calculating
%the function.

  return summ;

end;

%This is similar to the above code. As a result the comments above
%are valid here.

algebraic procedure mygseries(z);

  begin scalar k,summ,accu,term,zcube,int1,int2;
     summ := z;
     int1 := 3;
     int2 := 4;
     accu := 10 ^(-(symbolic !:prec!:));
     term := summ;
     zcube := (z ^ 3);

     for kk:=0:30 do
     << term := term * zcube / ((int1)* (int2));
        summ := summ + term;
        int1 := int1 + 3;
        int2 := int2 + 3;
      >>;

     while abs(term) > accu do
     << term := term * zcube / ((int1)* (int2));
        summ := summ + term;
        int1 := int1 + 3;
        int2 := int2 + 3;
      >>;

  return summ;
end;

%The following procedure calls the above f and g series in order to
%calculate the Airy_Ai and Airy_Bi for specific values of z.
%There is one expression for either the Ai or Bi evaluation. This is
%because each is similar.

%The code selects which expression to calculate depending on the value
%of proc. This is done automatically every time the procedure is called.

algebraic  procedure airya2(z,proc);

  begin scalar c1,c2,summ,oldprec;

%In order to calculate the infinite sums with a high accuracy, the
%precision is changed using the following code.
%This is done automatically each time the function is called. The
%precision is then reset to the original value.

   oldprec := precision 0;
   precision (oldprec + 10);

%Initial value used within the equation.

   c1 := (3 ^ (-2/3)) / gamma(2/3);
   c2 := (3 ^ (-1/3)) / gamma(1/3);

%This part selects automatically either Ai or Bi depending on proc.

   if proc=ai then summ := (c1 * myfseries(z)) - (c2 * mygseries(z))
   else summ := sqrt(3) *  ((c1 * myfseries(z)) + (c2 * mygseries(z)));
   precision (oldprec);

 return summ;

 end;

%The following code is the procedures for calculating the infinite sums
%used in the evaluation of the asymptotic expansions of Airy Functions.

%Again this code is used in the expression for Ai and Bi. As a result
%depending on the value of proc the correct one is called.

algebraic procedure asum1(z,proc);

  begin scalar p,k,summ,accu,term,zterm;

%Initial values that are used within the procedure.

     summ := 1;
     k := 1;
     accu := 10 ^(-(symbolic !:prec!:));
     term := 1 ;
     zterm := (2/3 * (z ^ (3/2)));

%A check to see when the infinite sum should be stopped.

     while abs(term) > accu do
     <<
        term := term * ((if proc=ai then -1 else 1) * ((3k-1/2) *
                 (3k-3/2) * (3k-5/2))
                   / (54 * (k) * (k-1/2))) / zterm;
        summ := summ + term;
        k := k+1;
      >>;

  return summ;
end;

%The following are similar to the code for asum1. As a result the above
%comments apply.

algebraic procedure asum2(z);

  begin scalar p,k,summ,accu,term,sqzterm,sqnum;
     summ := 1;
     k := 1;
     accu := 10 ^(-(symbolic !:prec!:));
     term := 1;
     sqzterm := (2/3 * (z ^ (3/2))) ^ 2;
     sqnum := (54 ^ 2);

     while abs(term) > accu do
     <<  term := term * ((-1) * ((6k-5.5)*(6k-4.5)*(6k-3.5)*(6k-2.5)
                      *(6k-1.5)*(6k-0.5) / (sqnum * (2k)*(2k-1)
                      *(2k-1.5)*(2k-0.5)))) / sqzterm;

         summ := summ + term;
         k := k+1;
      >>;

  return summ;
end;


algebraic procedure asum3(z);

  begin scalar p,k,summ,accu,term,zterm,sqzterm,sqnum;
     zterm := (2/3 * (z ^ (3/2)));
     sqzterm := zterm ^ 2;
     sqnum := 54 ^ 2;
     summ := ((3/2)*(5/2) / 54) / zterm;
     k := 1;
     accu := 10 ^(-(symbolic !:prec!:));
     term := ((3/2)*(5/2) / 54) / zterm;

     while abs(term) > accu do
     <<  term := term * ((-1) * ((6k+3)-1/2)*((6k+3)-3/2)*
                        ((6k+3)-5/2)*((6k+3)-7/2)*((6k+3)-9/2)
                       *((6k+3)-11/2)
                         /( sqnum * (2k)*(2k+1)
                      *((2k -1/2)*(2k+1/2)))) / sqzterm;

         summ := summ + term;
         k := k+1;
      >>;

  return summ;
end;

%There are two procedures depending on certain criteria for the arg of z
%for both Ai and Bi. They are asymptotic for large values of (-z) and z
%respectively. The choice as to which one is called for large values of
%z is determined in later code.

%Once again, as the expression for Ai and Bi is similar the code has
%been combined.

algebraic procedure asairyam(minusz,proc);

  begin scalar tt,p,ee,summ;
    z := - minusz;
    tt := (z ^ (-1/4));
    p := (pi ^ (-1/2));
    ee := (2/3 * (z ^ (3/2))) + (pi/4);

    if proc=ai then summ := tt * p * ((sin(ee) * asum2(z)) - (cos(ee)
                 * asum3(z)))
    else summ := tt * p * ((cos(ee) * asum2(z)) + (sin(ee) * asum3(z)));
    return summ;
   end;



algebraic procedure asairyap(z,proc);

  begin scalar tt,p,ee,summ;
    tt := (z ^ (-1/4));
    p := (pi ^ (-1/2));
    ee := e ^ ((if proc=ai then -1 else 1)*(2/3 * (z ^ (3/2))));
    if proc=ai then summ := (1/2) * tt * p * ee * asum1(z,ai)
    else summ := tt * p * ee * asum1(z,bi);
    return summ;
   end;

%The following section are the procedures that deal with the evaluation
%of the Airyprime functions.

%Similarly f and g series are calculated for use within the standard
%series approach. The same techniques for obtaining efficiency that were
%used in the code above are used here. As a result comments above apply.


algebraic procedure myfseriesp(z);

  begin scalar k,summ,accu,term,zcube,int1,int2;
     summ := ((z^2) / 2);
     int1 := 3;
     int2 := 5;
     accu := 10 ^(-(symbolic !:prec!:));
     term := ((z^2) / 2);
     zcube := z ^ 3;

     for kk:=0:30 do
     <<  term := term * zcube / ((int1) * (int2));
         summ := summ + term;
         int1 := int1 + 3;
         int2 := int2 + 3;
      >>;

     while abs(term) > accu do
     <<  term := term * zcube / ((int1) * (int2));
         summ := summ + term;
         int1 := int1 + 3;
         int2 := int2 + 3;
      >>;

  return summ;

end;

algebraic procedure mygseriesp(z);

  begin scalar k,summ,accu,term,zcube,int1,int2;
     summ := 1;
     int1 := 3;
     int2 := 1;
     accu := 10 ^(-(symbolic !:prec!:));
     term := 1;
     zcube := z ^ 3;

     for kk:=0:30 do
     << term := term * zcube / ((int1) * (int2));
        summ := summ + term;
        int1 := int1 + 3;
        int2 := int2 + 3;
      >>;

     while abs(term) > accu do
     << term := term * zcube / ((int1) * (int2));
        summ := summ + term;
        int1 := int1 + 3;
        int2 := int2 + 3;
      >>;

  return summ;
end;

%Once again, the code for Aiprime and  Biprime is similar and have been
%combined.

algebraic procedure airyap(z,proc);

  begin scalar c1,c2,summ,oldprec;
  oldprec := precision 0;
  precision (oldprec + 10);
  c1 := (3 ^ (-2/3)) / gamma(2/3);
  c2 := (3 ^ (-1/3)) / gamma(1/3);
  if proc=aiprime
    then summ := (c1 * myfseriesp(z)) - (c2 * mygseriesp(z))
   else summ :=  sqrt(3)*((c1 * myfseriesp(z)) + (c2 * mygseriesp(z)));
  precision(oldprec);
  return summ;
end;

%The following are the procedures for calculating the infinite sums used
%in the evaluation of the asymptotic expansion of Airyprime functions.

algebraic procedure apsum1(z,proc);

  begin scalar p,k,summ,accu,term,zterm;
     summ := 1;
     k := 1;
     accu := 10 ^(-(symbolic !:prec!:));
     term := 1;
     zterm := 2/3 * (z ^ (3/2));

     while abs(term) > accu do
     <<
        term := term * ((if proc=aiprime then -1 else 1)
                       * ((6k-7)/(6k-5) * (6k+1)/(6k-1))
                         *((3k -1/2)*(3k-3/2)*(3k-5/2)) /
                       (54 * k * (k-1/2))) / zterm;
         summ := summ + term;
         k := k+1
      >>;

  return summ;
end;

algebraic procedure apsum2(z);

  begin scalar p,k,summ,accu,term,sqzterm,sqnum;
     summ := 1;
     k := 1;
     accu := 10 ^(-(symbolic !:prec!:));
     term := 1;
     sqzterm := ((2/3 * (z ^ (3/2))) ^ 2);
     sqnum := (54 ^2);

     while abs(term) > accu do
     <<  term := term * ((-1) * ((12k-13)/(12k-11) * (12k+1)/(12k-1))
                 *((6k-5.5)*(6k-4.5)*(6k-3.5)*(6k-2.5)*(6k-1.5)
                  *(6k-0.5)) / (sqnum*(2k)*(2k-1)*(2k-1.5)*(2k-0.5))
                / sqzterm);
         summ := summ + term;
         k := k+1
      >>;

  return summ;
end;

algebraic procedure apsum3(z);

  begin scalar p,k,summ,accu,term,zterm,sqzterm,sqnum;
     zterm := (2/3 * (z ^ (3/2)));
     sqzterm := zterm ^2;
     sqnum := 54 ^ 2;
     summ := (-7/5) * ((3/2)*(5/2) / 54)/ zterm;
     k := 1;
     accu := 10 ^(-(symbolic !:prec!:));
     term := (-7/5) * ((3/2)*(5/2) / 54)/ zterm;

     while abs(term) > accu do
     <<  term := term * ((-1) * ((12k-7)/(12k-5) * (12k+7)/(12k+5)))
                 *((6k+3)-1/2)*((6k+3)-3/2)*((6k+3)-5/2)*((6k+3)-7/2)*
                 ((6k+3)-9/2)*((6k+3)-11/2) / (sqnum * (2k)*(2k+1) *
                 ((2k-1/2)*(2k+1/2)))/ sqzterm;
         summ := summ + term;
         k := k+1
      >>;

  return summ;
end;

%Once again the procedures which call the above infinite sums to
%calculate Aiprime and Biprime have been combined.

algebraic procedure airyapp(z,proc);

  begin scalar tt,p;
    tt := (z ^ (1/4));
    p := (pi ^ (-1/2));
    ee := e ^ ((if proc=aiprime then -1 else 1)*(2/3 * (z ^ (3/2))));
    if proc=aiprime then summ := (1/2) * tt * p * ee * apsum1(z,ai)
    else summ := tt * p * ee * apsum1(z,bi);
    return summ;
   end;


algebraic procedure airyapm(z,proc);

  begin scalar tt,p,ee,summ;
    tt := (z ^ (1/4));
    p := (pi ^ (-1/2));
    ee := (2/3 * (z ^ (3/2))) + (pi/4);
    if proc=aiprime then summ := tt * (-p) * ((cos(ee) * apsum2(z))
                 + (sin(ee) * apsum3(z)))
    else summ := tt*p*((cos(ee) * apsum2(z)) - (sin(ee) * apsum3(z)));
    return summ;
   end;


%When using both standard series and asymptotic approaches for the
%evaluation of Airy functions, there is a point when it is more
%efficient to use the asymptotic approach.

%It therefore remains to choose a value of z where this change over
%occurs. This choice depends on the precision desired.
%A table showing various values of z and the given precision where the
%change should take place was found. This has been implemented below.

%The table appears in a paper called "Numerical Evaluation of airy
%functions with complex arguments" (Corless,Jefferey,Rasmussen),
%J. Comput Phys. 99(1992), 106-114"

algebraic procedure Ai_Asymptotic(absz);

    begin scalar prec;
       prec := lisp !:prec!:;
       return
         if prec <= 6 and absz > 5 then 1
         else if prec <= 12 and absz > 8 then 1
         else if prec <= 16 and absz > 10 then 1
         else if prec <= 23 and absz > 12 then 1
         else if prec <= 33 and absz > 15 then 1
         else 0 ;
    end;

%Finally the following code deals with selecting the correct approach a
%function should take, depending on z, the above table and the upper
%bounds of the asymptotic functions.

%This procedure also allows for the user to call the correct evaluation
%of an Airy function from the Reduce command line argument.

algebraic procedure num_airy(z,fname);

   begin scalar summ;

%This is the procedure to evaluate Airy_ai of z.

 if fname = Ai then <<
    if Ai_Asymptotic(abs(z)) = 1 then
        <<if abs(arg(-z)) < ((2/3)*pi) then summ:= asairyam(z,ai)
        else if abs(arg(z)) < pi then summ := asairyap(z,ai);
        >>
     else summ := airya2(z,ai);

   return summ; >>

%This is the procedure to evaluate Airy_bi of z.
%Similar procedures for Airy_aiprime and Airy_biprime follow.

 else if fname = Bi then <<
     if Ai_Asymptotic(abs(z)) = 1 then
<<    if abs(arg(-z)) < ((2/3)*pi) then summ := asairyam(z,bi)
       else if abs(arg(z)) < ((1/3)*pi)  then summ := asairyap(z,bi);
>>
   else summ := airya2(z,bi);
    return summ; >>

 else if fname = Aiprime then <<
      if Ai_Asymptotic(abs(z)) = 1 then
<<
       if abs(arg(-z)) < (2/3) * pi then summ := airyapm(z,aiprime)
       else if abs(arg(z)) < pi then summ := airyapp(z,aiprime);
>>
       else summ := airyap(z,aiprime);
    return summ; >>

 else if fname = Biprime then <<
       if Ai_Asymptotic(abs(z)) = 1 then
<<
       if abs(arg(-z)) < ((2/3)*pi) then summ := airyapm(z,biprime)
       else if abs(arg(z)) < ((1/3)*pi) then summ := airyapp(z,biprime);
>>
       else  summ := airyap(z,biprime);
       return summ;>>

   end;


algebraic <<
operator Airy_Ai, Airy_Bi, Airy_Aiprime, Airy_Biprime;

%The following deals with the trivial cases of all of the Airy and
%Airyprime functions. It also calls the above code to allow the user to
%evaluate each of the four Airy function cases respectively.
%The rule for differentiation are also described.

Airy_rules := { Airy_Ai(0) => (3 ^ (-2/3)) / gamma(2/3),
                Airy_Ai(~z) => num_airy (z,Ai)
                        when symbolic !*rounded and numberp z,
                df(Airy_Ai(~z),z) => Airy_Aiprime(z),

                Airy_Bi(0) => sqrt(3) * (3 ^ (-2/3)) / gamma(2/3),
                Airy_Bi(~z) => num_airy (z,Bi)
                        when symbolic !*rounded and numberp z,
                df(Airy_Bi(~z),z) => Airy_Biprime(z),

                Airy_Aiprime(0) => -((3 ^ (-1/3)) / gamma(1/3)),
                Airy_Aiprime(~z) => num_airy (z,Aiprime)
                        when symbolic !*rounded and numberp z,
                df(Airy_Aiprime(~z),z) => z * Airy_Ai(z),

                Airy_Biprime(0) => sqrt(3) * (3 ^ (-1/3)) / gamma(1/3),
                Airy_Biprime(~z) => num_airy (z,Biprime)
                        when symbolic !*rounded and numberp z,
                df(Airy_Biprime(~z),z) => z * Airy_Bi(z)
               };

%This activates the above rule set.

let Airy_rules;

%The following is an inactive rule set that can be activated by the user
%if desired.
%When activated, it will represent the Airy functions in terms of Bessel
%Functions.

Airy2Bessel_rules :=
{ Airy_Ai(~z) => (1/3) * sqrt(z) * << (BesselI(-1/3,ee) -
                 BesselI(1/3,ee))
                  where ee => (2/3 * (z ^ (3/2))) >>
                  when numberp z and repart z >=0 ,

Airy_Ai(~minusz) => <<(sqrt(z/3) * BesselJ(1/3,ee) + BesselJ(-1/3,ee))
                  where {ee => (2/3 * (z ^ (3/2))) , z => -minusz} >>
                  when numberp z and repart z <=0,

Airy_Aiprime(~z) => -(z/3 * << (BesselI(-2/3,ee) - BesselI(2/3,ee))
                  where ee => (2/3 * (z ^ (3/2))) >>)
                  when numberp z and repart z >=0,

Airy_Aiprime(~minusz) => << (-(z)/3) * (BesselJ(-2/3,ee) -
                 BesselJ(2/3,ee))
                   where {ee => (2/3 * (z ^ (3/2))) , z => -minusz} >>
                   when numberp z and repart z <=0,

Airy_Bi(~z) => sqrt(z/3) * << (BesselI(-1/3,ee) + BesselI(1/3,ee))
                   where ee => (2/3 * (z ^ (3/2))) >>
                   when numberp z and repart z >=0,

Airy_Bi(~minusz) => << sqrt(z/3) * (BesselJ(-1/3,ee) - BesselJ(1/3,ee))
                   where {ee => (2/3 * (z ^ (3/2))) , z => -minusz}>>
                   when numberp z and repart <=0,

Airy_Biprime(~z) => (z / sqrt(3)) * << (BesselI(-2/3,ee)
                         + BesselI(2/3,ee))
                   where ee => (2/3 * (z ^ (3/2))) >>
                   when numberp z and repart z >=0,

Airy_Biprime(~minusz) => <<(z/sqrt(3)) * (BesselJ(-2/3,ee)
                         + BesselJ(2/3,ee))
                   where {ee => (2/3 * (z ^ (3/2))) , z => -minusz} >>
                   when numberp z and repart z <=0
};

>>;

endmodule;

end;


