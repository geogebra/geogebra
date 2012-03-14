
% -------------------------------------------------------------------------
% Neil Langmead
% ZIB Berlin, December 1996 / January 1997
%
% Package to integrate rational functions. Uses the Hermite Horowitz Rothstein
% Trager algorithms to determine firstly, the reduction of the rational fn
% into its polynomial and logarithmic parts, then the integration  of these
% parts seperately.


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

create!-package('(ratint convert),nil);

global '(traceratint!*); % for the tracing facility

switch traceratint;

off traceratint; % set to off by default

algebraic;

% We first need a few utility functions, given below
%
% -------------------------------------------------------------------------
% routine to return the square free factorisation of a polynomial
% outputs factors in a list, coupled with their exponents.
% However, such a function is already defined in poly/facform.

% expr procedure square_free(poly,x);
% begin  scalar  !*EXP, !*FACTOR, !*DIV, !*RATARG, !*GCD, !*RATIONAL,
% l,k,c,w,z,y,output, answer, result;
% on exp; on div; on ratarg; off factor; on rational;
% k:=1; output:={}; answer:= df(poly,x);
% poly:=poly;
% c:=gcd(poly,answer);
% w:=poly/c;
% while (c neq 1) do
%        <<
%          y:=gcd(w,c);
%          z:=w/y;
%          if (z neq 1) then
%           output:=append({{z,k}},output);
%           k:=k+1;
%           w:=y; c:=c/y;
%         >>;
% output:=append({{w,k}},output);
% return output;
% end;

% expr procedure eval_square_free(list,var);
% begin scalar output;
% off exp;
% output:= for l:=1:arglength(list)
% product(part(part(list,l),1)^(part(part(list,l),2)));
% return output;
% end;

expr procedure make_mon(li,var);

begin scalar current, li2;
li2:={};
for k:=1:arglength(li) do
  <<
    current:=part(li,k);
    current:=monic(current,var);
    li2:=append(li2,{current});
   >>;
return(li2);
end;

expr procedure monic(exp,var);
begin scalar lecof, temp;
lecof:=lcof(exp,var);
exp:=exp/lecof;
return exp;
end;
%x^8-2*x^6+2*x^2-1;
%z:=square_free(x^8-2*x^6+2*x^2-1,x);
%res:= for l:=1:2 product(part(part(z,l),1)^(part(part(z,l),2)));

% -------------------------------------------------------------------------
% An implementation of the extended Eucledian algorithm. Given polynomials
% a and b in the variable x in a Euclidean domain D, this computes elements
% s and t in D such that g:=gcd(a,b)=sa+tb

%off factor;
% input is polynomials a and b, in variable var
on rational;
%expr procedure ratint_u(exp,var);
%lcof(exp,var);

expr procedure norm(exp,var);
exp/lcof(exp,var);

expr procedure gcd_ex(a,b,var);
begin scalar c, c1, c2,d,d1,d2,q,r,m,r1,r2,g,s,b;
on rational;
c:=norm(a,var); d:=norm(b,var);
c1:=1; d1:=0; c2:=0; d2:=1;

while(d neq 0) do
  <<
    on rational;
    m:=pseudorem(c,d,var);
    q:=part(pseudorem(c,d,var),3)/part(pseudorem(c,d,var),2);
    %q:=part(pf(c/d,var),1); q should be quo(c,d)
    %off rational;
    r:=c-q*d;
    %r:=part(m,1);
    r1:=c1-q*d1; r2:=c2-q*d2;
    c:=d; c1:=d1; c2:=d2;
    d:=r; d1:=r1; d2:=r2;
  >>;
s:=c1/(ratint_u(a,var)*ratint_u(c,var));
b:=c2/(ratint_u(b,var)*ratint_u(c,var));
return({s,b});
end;

expr procedure ratint_u(exp,var);
if(numberp(exp)) then exp else
lcof(exp,var);

%l:=3*x^3+x^2+x+5;% p:=5*x^2-3*x+1
%pseudorem(l,p,x);
%quote:=part(pseudorem(o,p,x),3)/part(pseudorem(o,p,x),2);
%gcd_ex(x^2-1,x+1,x);
%a:=x^2+(7/6)*x+(1/3); b:=2*x+(7/6);
%gcd_ex(a,b,x);
%f:=48*x^3-84*x^2+42*x-36; g:=-4*x^3-10*x^2+44*x-30;
%gcd_ex(f,g,x);

% -------------------------------------------------------------------------
% routine to remove elements from a list which are zero

expr procedure rem_zero(li);
begin scalar k,j,l,li1,li2;
for k:=1:arglength(li) do
   <<
     j:=part(li,k);
     if(j neq 0) then nil else
       <<
         li1:=for l:=1:k-1 collect part(li,l);
         li2:=for l:=k+1:arglength(li) collect part(li,l);
         li:=append(li1,li2);
       >>;
   >>;
return li;
end;

% --------------------------------------------------------------------------
% This routine takes as input a rational function p/q^(expt), returning p, q
% and expt seperately in a list

expr procedure hr_monic_den(li,x);
begin scalar !*EXP, !*FACTOR, q, lc;
on EXP;
li:= (for each r in li collect << lc:=lcof(den(r),x);
                                  {num(r)/lc, den(r)/lc} >> );
on FACTOR;
li:= (for each r in li collect
     <<  q:=part(r,2);
         if(arglength(q) > -1 and part(q,0)=expt) then
         {part(r,1),part(q,1),part(q,2)} else
         {part(r,1),part(q,1),1} >> ) ; return li;
end;

%q:={3/(x+3)^2,4/(x+1)^5};
%hr_monic_den(q,x);
%in "monic";

% -------------------------------------------------------------------------
% The implementation of the Rostein Trager algorithm
% Takes as input a/b in x, and returns a two element list, with the polynomial
% and logarithmic parts of the integral. For aesthetic reasons in REDUCE, the
% values aren't added. This should be done manually by the user, but is a
% trivial task.
% in "mkmonic.red";
operator c, rtof,v, alpha;
load_package arnum;

load_package assist;

expr procedure not_numberp(x);
if (not numberp(x)) then t else nil;

expr procedure rt(a,b,x);
begin scalar vv, j,k,i,current,sol,res,cc,b_prime,extra_term,
      current1,vvv,integral, eqn,d,v_list, sol1,sol2, temp, temp2;
b_prime:=df(b,x);
v_list:={};
on rational;
res:=resultant(a-z*b_prime,b,x);
on rational; on ifactor;
res:= old_factorize(res);
res:=extractlist(res, not_numberp);
res:=make_mon(res,z);
res:=mkset(res); % removes duplicates by turning list into a set
%write "res is ", res;

integral:=0;

for k:=1:arglength(res) do
<<
  current:=part(res,k);% write "current is ", current;
  d:=deg(current,z); %write "d is ", d;
  if(d=1) then
    <<
      sol:=solve(current=0,z);
      sol:=part(sol,1);
      cc:=part(sol,2);% write "cc is ", cc;
      vv:=gcd(a-cc*b_prime,b);% write "vv is " , vv;
      vv:=vv/(lcof(vv,x));
      extra_term:=append({cc},{log(vv)});% write extra_term;
      extra_term:=part(extra_term,1)*part(extra_term,2); %write extra_term;
      integral:=extra_term+integral; %write "integral is ", integral;
    >>
   else
    <<
      current:=sub(z=alpha,current);% write "current is ", current;
      current1:=sub(alpha=alp,current);% write "current1 is ", current1;
      defpoly(current);
      %write "alpha is ", alpha;
      a:=sub(x=z,a); b:=sub(x=z,b); b_prime:=sub(x=z,b_prime);
      vv:=gcd(a-alpha*b_prime,b);% write "vv is ", vv; % OK up to here

      off arnum;
      on fullroots;
      vv:=sub(a1=alpha*8,vv);
      vv:=sub(z=x,vv);
      vvv:=solve(vv=0,x);
      vvv:=sub(a1=1/alp,vvv);% write "vvv is ", vvv;
      eqn:=part(part(vvv,1),1)-part(part(vvv,1),2);
      %write "eqn is ", eqn;
      if(d=2) then
         <<
             sol:=solve(current1=0,alp);
             sol1:=part(sol,1); sol2:=part(sol,2);
             %write "sol1, 2 are ", sol1, sol2;
             c(1):=part(sol1,2); c(2):=part(sol2,2);
             %write "c(1), c(2) are ", c(1), c(2);
             for j:=1:2 do
             <<
               v(j):=sub(alp=c(j),eqn);
               integral:=integral+c(j)*log(v(j));
               %write "integral is ", integral;
             >>;
           >>
       else
        <<
           k:=1;
           %write "d is ", d;
           while (k<=d) do
           %for k:=1:3 do
           <<
              c(k):=rtof(current1);% write "c(k) is ", c(k);
              v(k):=sub({alp=c(k)},eqn);% write "v(k) is ", v(k);
              integral:=integral+c(k)*log(v(k));
              %write "integral is ", integral;
              k:=k+1;
           >>;
        >>;
    >>;
lisp null remprop ('alpha,'currep);
lisp null remprop ('alpha,'idvalfn);
>>;
return(integral);
end;

% -------------------------------------------------------------------------
% This piece of code was written by Matt Rebeck. Input are the functions
% p, q and variable x. It returns the pseudo remainder of p and q, and the
% quotient.

symbolic procedure prem(r,v,var);
  begin
    scalar d,dr,dv,l,n,tt,rule_list,m,q,input1,input2,rr,vv;
    on rational; off factor;
    rr := r;
    vv := v;
    dr := deg(r,var);
    dv := deg(v,var);
    if dv <= dr then
    <<
      l := reval coeffn(v,var,dv);
      v := reval{'plus,v,{'minus,{'times,l,{'expt,var,dv}}}};
    >>
    else l := 1;
    d := dr-dv+1;
    n := 0;
    while dv<=dr and r neq 0 do
    <<
      tt := reval{'times,{'expt,var,(dr-dv)},v,coeffn(r,var,dr)};
      if dr = 0 then r := 0
      else
      <<
        rule_list := {'expt,var,dr}=>0;
        let rule_list;
        r := reval r;
        clearrules rule_list;
      >>;
      r := reval{'plus,{'times,l,r},{'minus,tt}};
      dr := deg(r,var);
      n := n+1;
    >>;
    r := reval{'times,{'expt,l,(d-n)},r};
    m := reval{'expt,l,d};
    input1 := reval{'plus,{'times,{'expt,l,d},rr},{'minus,r}};
    input2 := vv;
    q := reval{'quotient,input1,input2};
    return {r,m,q};
  end;

procedure pseudorem(x,y,var); lisp ('list . prem(x,y,var));
%e.g.
 %pseudorem(3x^5+4,x^2+1,x);
%exp1:=441*x^7+780*x^6-2861*x^5+4085*x^4+7695*x^3+3713*x^2-43253*x+24500;
%exp2:=9*x^6+6*x^5-65*x^4+20*x^3+135*x^2-154*x+49;
%pseudorem(exp1,exp2,x);
%a:=x^8+x^6-3*x^4-3*x^3+8*x^2+2*x-5;
%b:=3*x^6+5*x^4-4*x^2-9*x+21;
%pseudorem(a,b,x);
%r:=-15*x^4+3*x^2-9;
%rr:=
%operator a,c, neil;
%in "rem";

% -------------------------------------------------------------------------
% this routine is the implementation of Horowitz' method of reducing the
% rational function into a polynomial and logarithmic part.

operator a,c, neil ;
expr procedure howy(p,q,x);

begin
scalar pseudo, quo,rem,pp, poly_part,d,mm,b,nn,j,k,aa,cc,
pseudo3,i,quo3,r,pseudo2,eqn,l,neil1,sol, var1, temp,var2,var3,p,test,output;
pseudo:=pseudorem(p,q,x);
quo:=part(pseudo,3)/part(pseudo,2);
rem:=part(pseudo,1)/part(pseudo,2);

poly_part:=quo;
pp:=rem;% write "pp is ", pp;
d:=gcd(q,df(q,x));
pseudo2:=pseudorem(q,d,x);
b:=part(pseudo2,3)/part(pseudo2,2);

mm:=deg(b,x);
nn:=deg(d,x);

aa:=for k:=0:(mm-1) sum (a(k))*(x^(k));
cc:=for j:=0:(nn-1) sum (c(j))*(x^(j));

var1:=for i:=0:(mm-1) collect a(i);
var2:=for k:=0:(nn-1) collect c(k);
var3:=append(var1,var2);
%write var3;
on rational;
pseudo3:=pseudorem(b*df(d,x),d,x);
quo3:=part(pseudo3,3)/part(pseudo3,2);

temp:=b*df(d,x)/d;

temp:=pseudorem(num(temp),den(temp),x);
temp:=part(temp,3)/part(temp,2);
%write "temp is ", temp;
r:=b*df(cc,x)-cc*temp+d*aa;% write "r is: ", r;

for k:=0:(mm+nn-1) do
     <<
       %on factor;
       neil(k):=coeffn(pp,x,k)-coeffn(r,x,k);
       %write "neil(k)= ", neil(k);
     >>;

neil1:=for k:=0:(mm+nn-1) collect neil(k)=0;
%write "neil1= ", neil1;

sol:=solve(neil1,var3);% write "sol= ", sol;
sol:=first(sol);% write "sol= ", sol;
aa:=sub(sol,aa);
%write "aa= ", aa;
%aa:=for k:=1:mm sum(part(sol,k));
cc:=sub(sol,cc);% write "cc is ", cc;
ans1:=cc/d;
ans2:=int(poly_part,x);
ans3:=(aa/b);
output:={ans1,ans2,ans3};
return output;
end;

% -------------------------------------------------------------------------
%in "eea"; in "rem"; in "phi";

expr procedure newton(a,p,u1,w1,B);
begin scalar alpha,gamma,eea_result,s,tt,u,w,ef,modulus,c,sigma,
sigma_tilde,tau, tau_tilde,re,r,quo;

alpha:=lcof(a,x);
a:=alpha*a;
gamma:=alpha;
%a:=gamma*a;

%u1:=n(u1,x);
u1:=ratint_phi(u1,x,p); write "u1 is ", u1;

off modular;
w1:=ratint_phi(alpha*w1,x,p); off modular;% write "w1 is ", w1;
%w1:=ratint_phi(alpha*w1,x,p); off modular;% write "w1 is ",w1;

eea_result:=gcd_ex(u1,w1,x);

on modular; setmod p;
s:=part(eea_result,1);   tt:=part(eea_result,2);


on modular; setmod p;
u:=replace_lc(u1,x,gamma);   w:=replace_lc(w1,x,alpha);

off modular;
ef:=a-u*w; off modular;  modulus:=p;
%write "ef is ", ef; write "u,w are  ", u,w;
% iterate until either the factorisation in Z[x] is obtained, or else
% the bound on modulus is reached

on modular; setmod p;
while(ef neq 0 and modulus<2*B*gamma) do
    <<

       c:=ef/modulus;% write "c is ", c; off modular;
      sigma_tilde:=ratint_phi(s*c,x,p); off modular;
      tau_tilde:=ratint_phi(tt*c,x,p); off modular;
      %
       re:=pseudorem(sigma_tilde,w1,x);

      r:=part(re,1)/part(re,2); quo:=part(re,3)/part(re,2);
     sigma:=re; tau:=ratint_phi(tau_tilde+quo*u1,x,p); off modular;
      % update the factors and compute the error
      u:=u+tau*modulus;  w:=w+sigma*modulus;
     % write "u is ",u; write "w is ",w; write "ef is ", ef;
      ef:=a-u*w; modulus:=modulus*p;
   >>;

% check termination status
        if(ef=0) then << u:=u; w:=w/gamma;   >>
                          else rederr "nsfe";
return {u,w};
end;

%trst newton;
 %newton(12*x^3+10*x^2-36*x+35,5,x,x^2+3,10000);
% in "phi.red";
% in "eea.red"; in "rem.red"; %in "replace_lc";
clear p;
%trst newton;trst newton;
%newton(12*x^3+10*x^2-36*x+35,5,2*x,x^2+2,10000)

% -------------------------------------------------------------------------
expr procedure replace_lc(exp,var,val);
begin scalar lead_term, new_lead_term,red;
lead_term:=lterm(exp,var);
red:=reduct(exp,var);
new_lead_term:=lead_term/lcof(exp,var);
new_lead_term:=new_lead_term*val;
new_exp:=new_lead_term+red;

return new_exp;
end;

% -------------------------------------------------------------------------
% in "rem"; in "eea";
% routine to solve the polynomial diophantine equation
% s(x)a(x)+t(x)b(x)=c(x) for the unknown polynomials s and t

expr procedure polydi(a,b,c,x);
begin scalar q,r, sigma,tau, s,tt,sol,sigma_tilde,tau_tilde,g;
on rational;
g:=gcd(a,b);

s:=part(gcd_ex(a,b,x),1);
tt:=part(gcd_ex(a,b,x),2);
sol:=(s*c/g)*a+(tt*c/g)*b;
% here, sol=c(x), our right hand side

sigma_tilde:=s*c/g;
tau_tilde:=tt*c/g;

result:=pseudorem(sigma_tilde,b/g,x);
q:=part(result,3)/part(result,2);
r:=part(result,1)/part(result,2);

sigma:=r;
tau:=tau_tilde+(q*(a/g));

return {sigma,tau};
end;
%in "rem"; in "eea";
%trst polydi;
%polydi(x+(7/3),1,294,x);
%polydi(x^2+(7/6)*x+(1/3),2*x+(7/6),-(4425/2)*x-(5525/4),x);

% -------------------------------------------------------------------------
expr procedure ratint_phi(exp,var,p);
 begin scalar prime;
 prime:=p;
 if(primep p) then << on modular;
                      setmod p;
                      exp:=exp mod p;% off modular;
                   >> else rederr "p should be prime";
 return exp;
 end;


expr procedure nn(exp,var,p);
 begin scalar lcoef;
 lcoef:=lcof(exp,var);
 if(primep p) then <<   on modular; setmod p;
                        exp:=exp/lcoef;

                  >> else rederr "p should be prime";

return exp;
 end;
off modular;
%in "ratint" %in "examples"
%in "make_monic"
%operator c, rtof,v, alpha;
load_package arnum;

%load_package assist

expr procedure not_numberp(x);
if (not numberp(x)) then t else nil;

operator log_sum;
expr procedure rt(a,b,x);
begin scalar vv, j,k,i,current,sol,res,cc,b_prime,extra_term,
      current1,vvv,integral, eqn,d,v_list, sol1,sol2, temp, temp2;
b_prime:=df(b,x);
v_list:={};
on rational;
res:=resultant(a-z*b_prime,b,x);
on rational; on ifactor;
res:= old_factorize(res);
res:=extractlist(res, not_numberp);
res:=make_mon(res,z);
res:=mkset(res); % removes duplicates by turning list into a set
%write "res is ", res;

integral:=0;

for k:=1:arglength(res) do
<<
  current:=part(res,k); %write "current is ", current;
  d:=deg(current,z); %write "d is ", d;
  if(d=1) then
    <<
      sol:=solve(current=0,z);
      sol:=part(sol,1);
      cc:=part(sol,2);% write "cc is ", cc;
      vv:=gcd(a-cc*b_prime,b);% write "vv is " , vv;
      vv:=vv/(lcof(vv,x));
      extra_term:=append({cc},{log(vv)});% write extra_term;
      extra_term:=part(extra_term,1)*part(extra_term,2);%write extra_term;
      integral:=extra_term+integral; %write "integral is ", integral
  if(lisp !*traceratint) then write "integral in Rothstein T is ", integral;
    >>
   else
    <<
      current:=sub(z=alpha,current);% write "current is ", current;
      current1:=sub(alpha=alp,current);% write "current1 is ", current1;
      off mcd; current:=current;
      defpoly(current);
      on mcd;
      %write "alpha is ", alpha; %write part(alpha,1);
      a:=sub(x=z,a); b:=sub(x=z,b); b_prime:=sub(x=z,b_prime);
      vv:=gcd(a-alpha*b_prime,b); % write "vv is ", vv; % OK up to here
      off arnum;
      on fullroots;
      %vv:=sub(a1=alpha*(1/part(alpha,1)),vv);
      vv:=sub(z=x,vv); % vv:=sub(a1=part(alpha,1)*alpha,vv);
      %write "vv is ", vv;
      %write "deg is ", deg(vv,x);
      on rational; on ratarg;
      % write "deg is ", deg(vv,x);
      if(deg(vv,x)>2) then
<<
% we want to give the answer not in terms of a complete pf decomposition,
% but without splitting the field

integral:=integral+log_sum(alpha_a,current1,0,alpha_a*log(vv),x);
integral:=sub(alpha_a=alpha,integral);
%integral:=sub(a1=part(alpha,1)*alpha,integral)
integral:=sub(alp=alpha,integral);
if(lisp !*traceratint) then write "integral in Rothstein T is ", integral;
>>
else % degree less than or eq to 2, so no problem solving vv=0
<<
      % write "current is ", current;
      current:=sub(alpha=beta,current);
      vv:=sub(alpha=beta,vv);
      integral:=integral+log_sum(beta,current,0,beta*log(vv));
      % vvv:=solve(vv=0,x);
      %vvv:=sub(a1=1/alp,vvv); write "vvv is ", vvv;
      %eqn:=part(part(vvv,1),1)-part(part(vvv,1),2);
      %write "eqn is ", eqn;
      if(d=2) then
         <<
             sol:=solve(current1=0,alp);
             sol1:=part(sol,1); sol2:=part(sol,2);
             %write "sol1, 2 are ", sol1, sol2;
             c(1):=part(sol1,2); c(2):=part(sol2,2);
             %write "c(1), c(2) are ", c(1), c(2)
             for j:=1:2 do
             <<
               v(j):=sub(alp=c(j),eqn);
               %integral:=integral+c(j)*log(v(j));
               % write "integral is ", integral;
             >>;
           >>
       else
        <<
           k:=1;
           %write "d is ", d;
           while (k<=d) do
           %for k:=1:3 do
           <<
              c(k):=rtof(current1); % write "c(k) is ", c(k);
              v(k):=sub({alp=c(k)},eqn); % write "v(k) is ", v(k);
              %integral:=integral+c(k)*log(v(k));
              %write "integral is ", integral;
              k:=k+1;
           >>;
        >>;
    >>;
>>;
lisp null remprop ('alpha,'currep);
lisp null remprop ('alpha,'idvalfn);
>>;
return(integral);
end;

expr procedure dependp(exp,x);

  if(freeof(exp,x) and not numberp(exp)) then nil else t ;
% -------------------------------------------------------------------------
% procedure to integrate any rational function, using the implementations of
% the above algorithms.

expr procedure ratint(p,q,x);
begin scalar s_list,first_term, second_term, r_part, answer;
% check input carefully

if(not dependp(p,x) and not dependp(q,x)) then
   return (p/q)*x
else <<
   if(not dependp(p,x) and dependp(q,x)) then return
      p*ratint(1,q,x)
 else <<
   if( dependp(p,x) and not dependp(q,x)) then return
       (1/q)*int(p,x);
      >>;
     >>;
if(numberp p and numberp q) then return (p/q)*x;
%if(not polynomp p or not polynomp q) then rederr "input must be polynomials"
if(lisp !*traceratint) then write "performing Howoritz reduction on ", p/q;
s_list:=howy(p,q,x);
if(lisp !*traceratint) then write "Howoritz gives: ", s_list;
first_term:=part(s_list,1);
second_term:=part(s_list,2);
r_part:=part(s_list,3);% write "r_part is ", r_part;
if(lisp !*traceratint) then write "computing Rothstein Trager on ", r_part;
r_part:=rt(num(r_part),den(r_part),x);
answer:={first_term+second_term,r_part};
return (answer);
end;

% examples

%exp1:=441*x^7+780*x^6-2861*x^5+4085*x^4+7695*x^3+3713*x^2-43253*x+24500;

%exp2:=9*x^6+6*x^5-65*x^4+20*x^3+135*x^2-154*x+49;

%k:=36*x^6+126*x^5+183*x^4+(13807/6)*x^3-407*x^2-(3242/5)*x+(3044/15);
%l:=(x^2+(7/6)*x+(1/3))^2*(x-(2/5))^3;

%ratint(k,l,x);

%aa:=7*x^13+10*x^8+4*x^7-7*x^6-4*x^3-4*x^2+3*x+3;
%bb:=x^14-2*x^8-2*x^7-2*x^4-4*x^3-x^2+2*x+1;
%trst ratint;
%ratint(aa,bb,x);
%-----------------------------------------------------------------------------

end;


















