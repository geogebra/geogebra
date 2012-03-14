module zeilberg; % An implementation of the Gosper and Zeilberger
                 % algorithms.

% Authors: Gregor Stoelting & Wolfram Koepf
% version 1.2, April 1995.

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



% Reduce version 3.6
%
% References:
%
% R. W. Gosper, Jr.:
% Decision procedure for indefinite hypergeometric summation,
% Proc. Nat. Acad. Sci. USA 75 (1978), 40-42.
%
% Koornwinder, T. H.:
% On Zeilberger's algorithm and
% its q-analogue: a rigorous description.
% J. of Comput. and Appl. Math. 48 (1993), 91-111.
%
% Zeilberger, D.:
% A fast algorithm for proving terminating hypergeometric identities,
% Discrete Math. 80 (1990), 207-211.
%
% Koepf, W.:
% Algorithms for the indefinite and definite summation.
% Konrad-Zuse-Zentrum Berlin (ZIB), Preprint SC 94-33, 1994.
%
%
create!-package('(zeilberg),'(contrib sum));


fluid '(zb_version);
zb_version:="package zeilberg, version 1.1, Feb. 15, 1995"$
global '(inconsistent!*);

algebraic;
share zb_order$
zb_order:=5 $
gosper_representation:=nil;
zeilberger_representation:=nil;

% operator gamma,binomial;   % Now in entry.red.
operator hypergeom,pochhammer;
operator summ,zb_f,zb_sigma;
operator local_gamma,local_prod;
gamma1!*rules:={
gamma(~k)=> factorial(k-1)
 when fixp(k) and k>0
};
let gamma1!*rules;

pochhammer!*rules:={
pochhammer(~z,~k)  =>  ( for i:=0:(k-1) product(z + i))
   when fixp k and k < 20 and k > 0,
pochhammer(~z,~k)  => factorial(z+k-1)/factorial(z-1)
   when fixp z and z > 0
};

let pochhammer!*rules;

onerules:=
{gamma(~zb_x)=>1,
 binomial(~zb_x,~zb_y)=> 1,
 factorial(~zb_x)=> 1,
 pochhammer(~zb_x,~zb_y)=> 1
};

onerules2:={summ(~zb_x)=>1,hypergeom(~zb_x1,~zb_x2,~zb_x3)=>1};

gammatofactorial:={gamma(~a) => factorial(a-1)};

zb_binomialrules:={binomial(~n,0)=>1,
binomial(~n,~n)=>1,
binomial(~n,~k)=>0 when (fixp k and k<0),
binomial(~n,~k)=>0 when (fixp (n-k) and (n-k)<0),
binomial(~n,~k)=>factorial(n)/(factorial(k)*factorial(n-k)) when
(fixp k and fixp n)
};

let zb_binomialrules;




switch zb_factor, zb_timer,zb_proof, zb_trace,zb_inhomogeneous;
lisp setq(!*zb_factor,t);
% zb_factor:=1;
zb_direction:=down;

symbolic procedure gosper!*(u,v);
% gosper(f,k) searches for a hypergeometric term that is a closed form
% antidifference.
% form solution does not exist.
% gosper(f,k,m,n) determines
%
%  __n___
%  \
%   \   f(k)
%   /
%  /
%  -----
%   k=m
%
% using Gosper's algorithm. This is only successful if
% Gosper's algorithm applies.
   begin scalar x;
      x := if length v=1 then gosper0(u,aeval car v)
            else if length v=2
             then gosperborders(u,aeval car v,0,aeval cadr v)
            else if length v=3
             then gosperborders(u,aeval car v,aeval cadr v,
                                aeval caddr v)
            else rederr("Illegal number of arguments to SUM");
      return simp x
   end;

symbolic procedure gosper!-eval u;
   <<argnochk('gosper . u) where !*argnochk := t;
     prepsq!* gosper!*(car u,cdr u)>>;

put('gosper,number!-of!-args,2);

put ('gosper,'psopfn,'gosper!-eval);

algebraic procedure gosperborders(func,k,k0,k1);
% gosperborders(func,k,k0,k1) = gosper(func,k,k0,k1)
begin
scalar tmp,gosper2,!*factor;
gosper2:=gosper0(func,k);
tmp:=sub(k=k1,gosper2)-sub(k=k0-1,gosper2);
if lisp !*zb_factor then
 <<
  on factor;
  return num(tmp)/den(tmp)
 >>
else
 return tmp;
end;


algebraic procedure gosper0(func,k);
begin
scalar tmp;
tmp:=gosper1(func,k);
if tmp = zb_gancfse then
  rederr("Gosper algorithm: no closed form solution exists")
else
 return (tmp);
end;

algebraic procedure gosper1(func,k);
% gosper1(func,k) = gosper(func,k)
begin
scalar dexp,gexp,d,g,dg,degree, downmax,facj,partj, j,jj, p,r1,r2,
polynomials,sol,!*exp,!*factor,equations,equationlist,f,varlist,s,l;
clear(gosper_representation,
 zeilberger_representation,rational_certificate);
on exp;
%tester:=func;
%tester:=(tester where onerules);
%if not(type_ratpoly(tester,k)) then
%<<
% tester:=tester/sub(k=k-1,tester);
% if not(type_ratpoly(tester,k))  then
%  rederr("tester: algorithm not applicable")
%>>;
if polynomqq(func,k) then
  <<
  if lisp !*zb_trace then write "Gosper algorithm applicable";
  polynomials:={func,1,1}
  >>
else
 <<
 on factor;
 off exp;
  dg:=simplify_combinatorial(func/sub(k=k-1,func));
  %on exp;
  if dg = 0 then return 0;
  d:=num(dg);
  g:=den(dg);
 %Dexp:=D;
 %Gexp:=G;
 if lisp !*zb_trace then
   write "a(",k,")/a(",k,"-1):=",dg;
 %off factor;
  %on exp;
 %if not ratpoly2(D,G,k) then
 %rederr("Gosper algorithm not applicable");
 if not (polynomq4(d,k) and polynomq4(g,k)) then
  rederr("Gosper algorithm not applicable");
 % Gexp:=NIL;
 % Dexp:=NIL;
  if lisp !*zb_trace then write "Gosper algorithm applicable";
 %off exp;
 %on factor;
 % write "D:=",D;
 %write "G:=",G;
  polynomials:=determine!_polynomials2(d,g,k);
  d:=nil; g:=nil;
  %on exp;
 >>;
if lisp !*zb_timer then
 << write "flag: determine polynomials"; showtime>>;
p:=first(polynomials);
r1:=second(polynomials);
r2:=third(polynomials);
if lisp !*zb_trace then
  << write  "p:=",p; write  "q:=",r1; write  "r:=",r2>>;
off factor;
on exp;
% compute the maximum degree for the polynomial f.
% (Lemma 3.6 in Koornwinder)
degree:=maxdegf(r1,r2,p,k);
if lisp !*zb_trace then write  "degreebound:=",degree;
if lisp !*zb_timer then
 << write "flag: maxdegf"; showtime>>;
if (degree< 0) then
 return (zb_gancfse);
f:=(for j:=0 :degree sum (zb_f(j) * k^j));
equations:=(sub(k=k+1,r1) * f - r2 *  sub(k=k-1,f) - p);
on exp;
equationlist:=coeff(equations,k);
varlist:=(for j:=0 : degree collect (zb_f(j)));
l:=arglength(equationlist);
downmax:=max(degree -l +1 ,0);
sol:={};
sol2:={};
for j:=degree step -1 until downmax   do
<<
 off factor;
 partj:=sub(sol,part(equationlist,l -degree  +j));
 on exp;
 jj:=degree;
 while freeof(partj,zb_f(jj)) and jj geq 0 do
  jj:=jj-1;
 facj:=coeff(partj,zb_f(jj));
 off exp;
 on factor;
 if arglength(facj) = 2 then
 <<
  solj:={zb_f(jj)= -part(facj,1)/part(facj,2)};
  off factor;
  sol:=append(solj,sub(solj,sol));
  sol2:=append({zb_f(jj)},sol2)
 >>;
>>;
 if arglength(sol) = degree  then
    <<
     tmp:=t; j:=0;
     while tmp and j leq degree do
      <<
       if freeof (sol2,zb_f(j)) then
         <<
          sol:=append({zb_f(j) = 0},sub(zb_f(j) = 0,sol));
          tmp:=nil
         >>;
      j:=j +1;
     >>
    >>;
if lisp !*zb_timer then
 << write "flag: sol";showtime>>;
if sub(sol,equations) neq 0 then
 <<
  if lisp !*zb_proof then gosper_representation:={p,r1,r2,nil};
  return(zb_gancfse)
 >>;
f:=sub(sol,f);
if lisp !*zb_proof then gosper_representation:={p,r1,r2,f};
if lisp !*zb_proof then
 if zb_direction = down  then
  <<
   rational_certificate:=sub(k=k+1,r1)/p*f;
   s:=rational_certificate*func
  >>
 else
  <<
   rational_certificate:=sub(k=k+1,r2/p)*f;
   s:=rational_certificate*sub(k=k+1,func)
  >>
else
 s:=sub(k=k+1,r1)/p*f*func;
if lisp !*zb_trace then write  "f:=",f;
off factor;
if lisp !*zb_timer then
<< write "flag: simplify comb von sol:=";showtime>>;
if lisp !*zb_factor then
 <<
  on factor;
  s:=num(s)/den(s);
 >>;
if lisp !*zb_timer then
<< write "flag: num(s)/den(s) under factor";showtime>>;
if lisp !*zb_trace then write  "Gosper algorithm successful";
if zb_direction = down  then
 return s
else
 return sub(k=k-1,s);
end; %gosper1


symbolic procedure sumrecursion!-eval u;
% sumrecursion(f,k,n,j) determines a holonomic recurrence equation
% of order less or equal j for
%
%  _____
%  \
%   \   f(n,k)   with respect to n
%   /
%  /
%  -----
%   k
% sumrecursion(f,k,n) = sumrecursion(f,k,n,zb_order), where zb_order
% is a global variable (default = 5)
<<
 if length u = 3 then
  sumrecursion0(aeval car u,aeval cadr u,aeval caddr u,1,zb_order)
 else
  if  length u = 4 then
   sumrecursion0(aeval car u, aeval cadr u, aeval caddr u,
    aeval cadddr u, aeval cadddr u)
  else rederr("illegal number of arguments")
>>;

put ('sumrecursion, 'psopfn,'sumrecursion!-eval);

algebraic procedure sumrecursion0(func,secundus,n,mini, maxi);
begin
scalar !*factor,!*exp;
if lisp !*zb_factor then on factor;
return part(sumrecursion1(func,secundus,n,mini, maxi),1);
end;

algebraic procedure sumrecursion1(func,secundus,n,mini, maxi);
begin
scalar result1,ank,b,c,d,g,bc,dg, j,jj, inhomogeneous,k,aa,bb,
!*factor,!*exp,order1;
clear(gosper_representation,
 zeilberger_representation,rational_certificate);
result1:=-1;
on exp;
inhomogeneous:=nil;
% provisorisch
if (arglength(secundus )= 3) and (part(secundus,0)=list) then
 rederr("not yet implemented.");
if (arglength(secundus )= 3) and (part(secundus,0)=list) then
  <<
   k:=part(secundus,1);
   aa:=part(secundus,2);
   bb:=part(secundus,3);
   inhomogeneous:=t
  >>
 else
  <<
   k:=secundus;
   aa:=0;
   bb:=0
  >>;
ank:=func;
tester:=func;
%Write("tester:=",tester);
tester:=(tester where onerules);
%Write("tester:=",tester);
if tester neq 0  then
<<
 tester2:=tester;
 tester:=tester/sub(k=k-1,tester);
 %Write("tester:=",tester);
 if not(type_ratpoly(tester,k))  then
  rederr("algorithm not applicable");
 %Write("tester2:=",tester);
 tester2:=tester2/sub(n=n-1,tester2);
 %Write("tester2:=",tester);
 if not(type_ratpoly(tester2,n))  then
  rederr("algorithm not applicable");
>>;
bc:=simplify_combinatorial(ank/sub(n=n-1,ank));
b:=num(bc);
c:=den(bc);
if lisp !*zb_trace then
  write "F(",n,",",k,")/F(",n,"-1,",k,"):=",bc;
on exp;
if not type_ratpoly(bc,n) then
 <<
 if lisp !*zb_trace then
   << write "not rational";
      write "Zeilberger algorithm not applicable";
   >>;
 return extended_sumrecursion1(ank,k,n);
 >>;
dg:=simplify_combinatorial(ank/sub(k=k-1,ank));
d:=num(dg);
g:=den(dg);
if lisp !*zb_trace then
  write "F(",n,",",k,")/F(",n,",",k,"-1):=",dg;
on exp ; % achtung
if not type_ratpoly(dg,k) then
 <<
  if lisp !*zb_trace then
   << write "not rational";
      write "Zeilberger algorithm not applicable";
   >>;
return extended_sumrecursion1(ank,k,n);
 >>;
if lisp !*zb_trace then write "Zeilberger algorithm applicable";
on factor;
order1:=0;
for j:=mini : maxi  do
 if result1 = -1 then
   result1:=sumrecursion2(ank,b,c,d,g,k,n,aa,bb,inhomogeneous,j)
 else
   if order1= 0 then order1:=j -1;
   if result1 = -1 then
   rederr("Zeilberger algorithm fails. Enlarge zb_order");
off factor;
if lisp !*zb_factor then
<<
 on factor;
 if lisp minusp (lc numr cadr aeval prepsq cadr result1) then
   result1:=-result1;
>>;
return {result1,order1};
end;
% sumrecursion1

algebraic procedure
   sumrecursion2(ank,b,c,d,g,k,n,aa,bb,inhomogeneous,order1);
% applies Zeilberger algorithm for order order1
begin
scalar j,jj, p,r10,r20,r1,r2,p1,polynomials, gg, recursion,recursion2,
equations,inhomogeneous,f,varlist,r12,summe,s,k,z,!*factor,!*exp;
if lisp !*zb_factor then
on factor;
if lisp !*zb_trace then
  write  "applying Zeilberger algorithm for order:=",order1;
p0:=
 (for j:=0 : (order1-1) product sub(n=n-j,b) )+
 (for j:=1 : order1 sum (zb_sigma(j) *
 (for jj:=0 : (j-1) product sub(n=n-jj,c)) *
 (for jj:=j : (order1-1) product sub(n=n-jj,b)) ));
r12:=
 d * (for j:=0 : (order1-1) product sub({n=n-j,k=k-1},b))/
 (g * (for j:=0 : (order1-1) product sub(n=n-j,b)));
r12:=simplify_combinatorial(r12);
r10:=num(r12);
r20:=den(r12);
off factor;
polynomials:=
  determine!_polynomials(r10,r20,k);
p1:=first(polynomials);
p:=p1 *p0;
r1:=second(polynomials);
r2:=third(polynomials);
if lisp !*zb_trace then
   <<write  "p:=",p; write  "q:=",r1; write  "r:=",r2>>;
% compute the maximum degree for the polynomial f.
% (Lemma 3.6 in Koornwinder)
degree:=maxdegf(r1,r2,p,k);
if lisp !*zb_trace then write  "degreebound:=",degree;
if (degree< 0) then
  return (-1);
f:=(for j:=0 :degree sum (zb_f(j) * k^j));
equations:=(sub(k=k+1,r1) * f - r2 *  sub(k=k-1,f) - p);
on exp;
equationlist:=coeff(equations,k);
va:=(for j:=0 : degree collect zb_f(j));
vb:=(for j:=1 : order1 collect zb_sigma(j));
varlist:=append(va,vb);
sol:=savesolve(equationlist,varlist);
if sub(sol,equations) neq 0 then return -1;
f:=sub(sol,f);
%if arglength(sol)<degree + order1 then
%write "warning: solution not unique";
f:=(f where arbcomplex(~x)=>0);
if lisp !*zb_trace then write  "f:=",f;
p:=sub(sol,p);
p:=(p where arbcomplex(~x)=>0);
if lisp !*zb_trace then write  "p:=",p;
if lisp !*zb_proof then zeilberger_representation:={p,r1,r2,f};
if zb_direction = down then
  <<if lisp !*zb_proof then rational_certificate:=sub(k=k+1,r1)/p *f>>
else
  <<if lisp !*zb_proof then rational_certificate:=sub(k=k+1,r2/p)*f>>;
va:=sub(sol,va);
vb:=sub(sol,vb);
n0:=order1-1;
for j:=1 : degree do
    n0:=max(testnonnegintroots(den(part(va,j)),n),n0);
%write "n0:=",n0;
%write "first va " , testnonnegintroots(den(part(va,1)),n);
%write "last va " , testnonnegintroots(den(part(va,degree)),n);
for j:=1 :order1 do
    n0:=max(testnonnegintroots(den(part(vb,j)),n),n0);
%write "n0:=",n0;
%write "first vb " , testnonnegintroots(den(part(vb,1)),n);
%write "last vb " , testnonnegintroots(den(part(vb,order1)),n);
n0:=max(testnonnegintroots(den(sub(k=n+1,q)),n),n0);
n0:=max(testnonnegintroots(num(sub(k=n+1,p)),n),n0);
%write "n0:=",n0;
if n0>=order1 then write "recursion valid for n>=",n0+1;
%zb_testnonnegintroots:=n0+1;
zb_testnonnegintroots:=n0-order1+1;
recursion:=summ(n) +
(for j:=1 : order1 sum part(vb,j)* summ(n-j));
 recursion:=num(recursion);
recursion:=(recursion where arbcomplex(~local_x) => 1);
if lisp !*zb_proof or inhomogeneous then
 <<
  gg:=
  f*sub(k=k+1,r2*ank/
    (p1*(for j:=0: order1-1 product sub(n=n-j ,b))));
  gg:=sub(sol,gg);
  proof:=gg;
  gg:=sub({k=k-1,n=n+1},gg);
  gg:=simplify_combinatorial(gg);
  if lisp !*zb_trace then write "G:=",gg;
 >>;
if inhomogeneous then
<<
 on factor;
 if lisp !*zb_inhomogeneous then
 <<
  recursion:=
  {recursion ,
  simplify_combinatorial(sub(k=bb+1,gg) - sub(k=aa,gg))};
  tempo:=
   simplify_combinatorial(sub(k=bb,gg) - sub(k=aa-1,gg));
 >>
 else
 <<
  recursion:=
   gg * sub(k=k+1,recursion) -sub(k=k+1,gg)*recursion;
  recursion:=simplify_combinatorial(recursion);
  recursion:=num(recursion)
 >>;
>>;
%if inhomogeneous
if lisp !*zb_trace then write  "Zeilberger algorithm successful";
if zb_direction = down then
 return recursion
else
 return sub(n=n+order1,recursion);
end; %sumrecursion2

algebraic procedure testnonnegintroots(term1,n);
begin
scalar  n0,l,j,n1;
n0:=-1;
term1 := old_factorize(term1);
l:=arglength(term1);
for j:=1:l do
 <<
 f:=part(term1,j);
 if  deg(f,n )= 1 then
  n1:=-part(coeff(f,n),1)/part(coeff(f,n),2);
   if fixp(n1) then n0:=max(n1,n0)
 >>;
%write "returning",n0;
return n0;
end;




symbolic  procedure hypersum!-eval u;
 <<
  if length u = 4 then
   hypersum1(aeval car u, aeval cadr u, aeval
       caddr u,aeval cadddr u,1,zb_order)
  else
   if  length u = 5 then hypersum1(aeval car u, aeval cadr u,
      aeval caddr u, aeval cadddr u,car cddddr u,car cddddr u)
   else rederr("illegal number of arguments")
 >>;

put ('hypersum, 'psopfn,'hypersum!-eval);

algebraic procedure recursion_to_closed_form(recursion,startl,n,m);
begin
scalar aj,recj,list1,p,q,tmp,j,nonhyp,order1,!*factor,!*exp;
on exp;
list1:={};
order1:= arglength(startl);
p:=part(coeff(recursion,summ(n)),2);
q:=part(coeff(recursion,summ(n-order1)),2);
nonhyp:=0;
if not(freeof(summ,p) and freeof(summ,q)) then
  <<
  nonhyp:=1;
  write "no hypergeometric solution found";
  return  recursion;
   >>;
for j:=1:order1 do
  <<
   aj:=part(startl,j);
   if aj=0 then
    list1:=append(list1,{0})
   else
    <<
     recj:=sub(n= n*order1 +j-1,p) * summ(n) +
        sub(n= n*order1 +j-1,q) *summ(n-1);
      tmp:=rectopoch(recj,n,1,m);
      tmp:={aj * sub(n=(n-j+1)/order1,tmp)};
      list1:=append(list1,tmp);
    >>;
   >>;%for
if order1 = 1 then
 return part(list1,1)
else
 return list1;
end;%recursion_to_closed_form



algebraic procedure hypersum1(upper,lower,z,n,mini, maxi);
begin
scalar tmp1,tmp,j,jj,aj,order1,recursion,term1,!*exp,startl;
off exp;
tmp:=hyperrecursion1(upper,lower,z,n,mini,maxi);
recursion:=part(tmp,1);
order1:=part(tmp,2);
%order1:=recorder(f,n);
if lisp !*zb_trace then
 write "recursion for underlying hypergeometric term:=",recursion;
startl:={1};
if order1 > 1 then
<<
 for j:=1: order1-1 do
 <<
  aj:=sub(n=j,(for jj:=0 :j sum hyperterm(upper,lower,z,jj)));
  aj:=simplify_combinatorial(aj);
  %write "aj:=",aj;
   startl:=append(startl,{aj});
 >>;
>>;
% write "startl in hyp1:=",startl;
return recursion_to_closed_form(recursion, startl,n,0);
end;%hypersum1



% sumtohyper(hyperterm({-a,b},{c},z,k),k);
% sumtohyper(hyperterm({-a,b},{c},z,k),k);
algebraic procedure summation(f,k,n);
begin
scalar l,localhypersum ,upper,lower,z,term,i,tmp,
startl, aj,piecewiseterm,piecewiseseq,f1,partj,
recursion,counter,m,tmpterm,upper,lower,z,prefactor,init,ht,
initial,initialnumber,summand,j,gammasummand,!*exp;
on exp;
ht:=sumtohyper(f,k);
prefactor:=(ht where onerules2);
%write "prefactor:=",prefactor;
ht:=ht/prefactor;
upper:=part(ht,1);
lower:=part(ht,2);
z:=part( ht,3);
f1:=simplify_combinatorial(f);
tmp:=sumrecursion1(f1,k,n,1,zb_order);
%write("zb_testnonnegintroots:=",zb_testnonnegintroots);
recursion:=part(tmp,1);
order1:=part(tmp,2);
if (order1 = 1) and (zb_testnonnegintroots= 0)  then
<<
 return recursion_to_closed_form(recursion,{prefactor},n,0);
>>;
% evaluate upper border
l:=arglength(upper);
initialnumber:=0;
%write "UPPER:=",upper;
for j:=1:l do
 <<
 partj:=part(upper,j);
 %write "partj :=",partj;
 tmp:=coeff(partj,n);
 if arglength(tmp)=2 then
  if fixp(part(tmp,2)) and part(tmp,2)<0 and fixp(part(tmp,1)) then
    initialnumber:=-partj;
 >>;
if initialnumber = 0 then
  rederr("no reccurent evaluation possible");
startl:={};
 for j:=zb_testnonnegintroots: order1-1+ zb_testnonnegintroots do
 <<
  write "prefactor:=",prefactor;
  write "sum(hyperterm(UPPER,LOWER,z,k),k,0,initialnumber):=",
   sum(hyperterm(upper,lower,z,k),k,0,initialnumber);
  aj:=sub(n=j,prefactor*
   sum(hyperterm(upper,lower,z,k),k,0,initialnumber));
  aj:=simplify_combinatorial(aj);
   startl:=append(startl,{aj});
 >>;
write "startl:=",startl;
   term:=recursion_to_closed_form(recursion,startl,n,
            zb_testnonnegintroots);
write "term:=",term;
if freeof(term,summ) then
  return(term)
else
 if freeof(prefactor,n) then
  recursion:=term
 else
  recursion:=sumrecursion(f,k,n);
if lisp !*zb_trace then
 %write "recursion:=",recursion;
counter:=0;
l:=arglength(recursion);
for i:=1 : l do
 <<
  term:=
   part(recursion,i)/(part(recursion,i) where onerules2);
  term:=part(term,1);
   m:=part(term,1);
    counter:=max(counter,m-term);
 >>;
%initial values, depend on testnonnegintroots
if lisp !*zb_trace then
 write "calculating initial values";
 initialnumber:=0;
 l:=arglength(upper);
 for i:=1 : l do
  <<
  tmp:=part(coeff(part(i,upper),n),2);
  if fixp(tmp) and (tmp <0) then
     initialnumber:=part(upper,i);
  % still to implement: rational case
 if initialnumber=0 then
   rederr("no initialization found");
 if zb_testnonnegintroots=0 then
  errorset;
>>;
tmp:=sub(n=0,prefactor);
end; %summation


%in "zeilberger.red"$ hypersum({-n,-n},{1},-1,n);
%hypersum({-n,n+3*a,a},{3*a/2,(3*a+1)/2},3/4,n);
%hyperrecursion({-n,-n},{1},-1,n);
%hypersum({-2 *n ,-2 *n},{1},-1,n);
%sub(n=n/2,hypersum({-2 *n ,-2 *n},{1},-1,n));
%sumtohyper((-1)^k*binomial(n,k)^2,k);
% boolsche Variable
% Polynomgeschichten revisited
% w. schickt
% t durch 1 erstezen.



algebraic procedure recorder(f,n);
begin
pa:=patternarguments(f,summ,{});
pa:=sub(n=0,pa);
return (-min(pa));
end;

algebraic procedure rectopoch(f,n,order1,m);
begin
scalar dennum,denden,cases1,k,nume,deno,!*exp,!*gcd;
on exp; on gcd;
%write "order1:=",order1;
%order1:=recorder(f,n);
%write "f:=",f;
deno:=-part(coeff(f, summ(n)),2);
%if freeof(f, summ(n-order1)) then rederr("not yet implemented");
nume:=part(coeff(f, summ(n-order1)),2);
if order1 >1 then
 <<
 for j:=1 : order1 -1 do
  if not freeof(f,summ(n-j)) then rederr("no hypergeometric solution");
 cases1:={};
 for j:=0 :order1-1 do
 cases1:=append({sub(n=(n-j)/order1,
  rectopoch(summ(n) * sub(n=order1*n+j,nume) +
  summ(n-1) * sub(n=order1*n+j,deno),n,1,m))},cases1);
 return cases1
 >>;
%if not freeof(deno,summ) or not freeof(nume,summ) then
% rederr("no hypergeometric solution");
lcr2:=first(reverse(coeff(deno,n)));
lcr1:=first(reverse(coeff(nume,n)));
nume:=nume/lcr1;
dennum:=den(nume);
nume:=num(nume);
deno:=deno/lcr2;
denden:=den(deno);
deno:=num(deno);
deno:= old_factorize(deno);
nume:= old_factorize(nume);
deno:=(part(deno,1):=part(deno,1)/denden);
nume:=(part(nume,1):=part(nume,1)/dennum);
deno:=refactors(deno,n);
nume:=append({1},refactors(nume,n));
tmp:={};l:=arglength(nume);
for j:=1:l do
 tmp:=append(tmp, {part(nume,j)+m});
nume:=tmp;
tmp:={};l:=arglength(deno);
for j:=1:l do
 tmp:=append(tmp, {part(deno,j)+m});
deno:=tmp;
%write "deno:=",deno;
%write "nume:=",nume;
return hyperterm(nume,deno,lcr1/lcr2,n-m)*factorial(n-m)/
pochhammer(m+1,n-m);
end;
%extended_sumrecursion((pochhammer( - n,k)* pochhammer(b,k)*
%pochhammer(c,k))/(factorial(k)*pochhammer((b - n + 1)/2,k)*
%pochhammer(2*c,k)), k, n);
%hypersum({-n,b,c},{1/2*(1-n+b),2*c},1,n);
% hypersum({a,b},{c},1,a);
%hypersum({-n,b},{c},1,n);
%zeilb([-n,b],[c],1,n,1);




algebraic procedure refactors(term1,n);
begin
scalar a, l,i,c,d,g,pol,degree ,!*exp, !*factor,
denpol,numpol;denpol;
on exp;
g:={};
l:=arglength(term1);
%p1:=part(term1,1);
for i:=1:l do
<<
   pol:=part(term1,i);
   on exp;
  if not freeof(pol,n) then
   <<
   numpol:=num(pol);
   denpol:=den(pol);
   degree:=deg(numpol,n);
   if degree=1 then
     <<
      d:=part(coeff(numpol,n),2)/denpol;
      c:=part(coeff(numpol,n),1)/d/denpol+1;
      <<for j:=1 :degree do g:=append(g,{c})>>;
       >>
    else
      newrederr{pol," does not factorize."}
   >>
>>;
return g;
end;

symbolic  procedure hyperrecursion!-eval u;
% hyperrecursion({a_1,...,a_p},{b_1,...,b_q},x,n,j) determines
% a holonomic recurrence equation (up to order j)
% with respect to n for the peneralized hypergeometric function
%  F (a_1,...,a_p;b_1,...,b_q;x)
% hyperrecursion(upper,lower,x,n) =
% hyperrecursion(upper,lower,x,n,zb_order)
% where zb_order is a global variable (default = 5)
 <<
  if length u = 4 then
   hyperrecursion0(aeval car u, aeval cadr u, aeval
       caddr u,aeval cadddr u,1,zb_order)
  else
   if  length u = 5 then hyperrecursion0(aeval car u, aeval cadr u,
      aeval caddr u, aeval cadddr u,car cddddr u,car cddddr u)
   else rederr("illegal number of arguments")
 >>;

put ('hyperrecursion, 'psopfn,'hyperrecursion!-eval);


algebraic procedure hyperrecursion0(upper,lower,z,n,mini, maxi);
begin
scalar !*factor,!*exp;
if lisp !*zb_factor then on factor;
return part(hyperrecursion1(upper,lower,z,n,mini, maxi),1)
end;


algebraic procedure hyperrecursion1(upper,lower,z,n,mini, maxi);
begin
scalar tester,result1,b,c,d,g,bc,dg, upl,lol,func,j,goon,x,
liste,!*factor,!*exp,order1;
clear(gosper_representation,
zeilberger_representation,rational_certificate);
result1:=-1;
upl:=arglength(upper);
lol:=arglength(lower);
%% test if some upper index is a nonnegative integer
%goon:=t;
%liste:=upper;
%while goon do
% <<
%  if liste = {} then
%   goon:=NIL
%  else
%  <<
%   x:=first(liste);
%   if fixp(x) and x>-1 then
%     goon:=NIL
%   else
%     liste:=rest(liste)
%  >>
% >>;
%if arglength(liste)>0 then
% rederr ("some upper index is a nonnegative integer");
goon:=t;
liste:=lower;
while goon do
 <<
  if liste = {} then
   goon:=nil
  else
  <<
   x:=first(liste);
   if fixp(x) and x<1 then
     goon:=nil
   else
     liste:=rest(liste)
  >>
 >>;
if arglength(liste)>0 then
  rederr ("some lower index is a nonpositive integer");
func:=hyperterm(upper,lower,z,local_k);
tester:=func;
%Write("tester:=",tester);
tester:=(tester where onerules);
%Write("tester:=",tester);
if tester neq 0  then
<<
 tester:=tester/sub(n=n-1,tester);
 %Write("tester:=",tester);
 if not(type_ratpoly(tester,n))  then
  rederr("algorithm not applicable");
>>;
bc:=simplify_combinatorial(func/sub(n=n-1,func));
on factor;
b:=num(bc);
c:=den(bc);
if lisp !*zb_trace then
  write "F(",n,",local_k)/F(",n,"-1,local_k):=",b/c;
%off factor;
on exp;
if not type_ratpoly(bc,n) then
 <<
 if lisp !*zb_trace then
  <<
   write "not rational";
   write "Zeilberger algorithm not applicable"
  >>;
 return extended_hyperrecursion1(upper,lower,z,n);
 >>;
dg:=(for j:=1 : upl product(local_k - 1 + part(upper,j))) * z/
  ((for j:=1 :lol product(local_k - 1 + part(lower,j)))*(local_k));
d:=num(dg);
g:=den(dg);
if lisp !*zb_trace then
 <<
  write "F(",n,",local_k)/F(",n,",local_k-1):=",d/g;
  write "Zeilberger algorithm applicable"
 >>;
order1:=0;
for j:=mini:maxi  do
 if result1 = -1 then
  result1:=sumrecursion2(func,b,c,d,g,local_k,n,0,0,nil,j)
 else
  if order1= 0 then order1:=j -1;
if result1 = -1 then
 rederr("Zeilberger algorithm fails. Enlarge zb_order");
if lisp !*zb_factor then
<<
 on factor;
 if lisp minusp (lc numr cadr aeval prepsq cadr result1) then
   result1:=-result1;
>>;
return {result1,order1};
end;
% hyperrecursion1

algebraic procedure determine!_polynomials(r10,r20,k);
% determines polynomials p(k),r1(k),r2(k) as in Lemma 3.1 in
% Koornwinder,  or p_k,r_k,q_k as in Gosper,
% respectively.
begin
scalar tmp,r1divr2,p,r1,r2,j,jj, gamma1,!*exp,!*factor;
on exp;
%write "enter maxshift with ",{r10,r20};
%globalns:={r10,r20};
maxshift1:=maxshift(r10,r20,k);
%write "maxshift:=",maxshift1;
p:=1;
r1:=r10;
r2:=r20;
for jj:=0: maxshift1 do
<<
 %write "jj:=",jj;
 gamma1:=gcd(r1,sub(k= k+jj,r2));
 %write "jj:=",jj;
 if gamma1 neq 1 then
  <<
    r1:=r1/gamma1;
    r2:=r2/sub(k= k-jj,gamma1);
    p:=p * (for j:=0 : (jj-1) product sub(k=k-j,gamma1))
  >>; % if
>>;
return {p,r1,r2};
end;

algebraic procedure determine!_polynomials2(r10,r20,k);
% determines polynomials r1(k),r2(k),p(k) as in Lemma 3.1 in
% Koornwinder
begin
scalar
!*exp,!*factor,
f1,f2,order1,order2,ma,leadj,leadjj,jj,j,r1,r2,p;
on factor;off exp;
p:=1;
r1:=r10;
r2:=r20;
f1:= old_factorize(r1);
f2:= old_factorize(r2);
order1:=arglength(f1);
order2:=arglength(f2);
for j:=1 : order1 do
 for jj:=1 : order2 do
  <<
  complist:=comppol(part(f1,j),part(f2,jj),k);
  comp:=part(complist,1);
  leadj:=part(complist,2);
  leadjj:=part(complist,3);
  if comp> -1 then
  <<
   gamma1:=part(f1,j);
   gamma2:=part(f2,jj);
  %if  gamma1 neq  sub(k=k+j,gamma2) then
    r1:=r1/gamma1;
    r2:=r2/sub(k= k-comp,gamma1);
    p:=p * (for jj:=0 : (comp-1) product sub(k=k-jj,gamma1));
    f1:=(part(f1,j):=1);
    f2:=(part(f2,jj):=1);
 % neu
  >> % if
 >>;
on exp;
return {p,r1,r2};
end; % determine!_polynomials2

algebraic procedure maxshift(p1,p2,k);
% computes the maximal j with
% gcd(p1(k),p2(k+j)) neq 1
begin
scalar f1,f2,order1,order2,ma,j,jj;
ma:=-1;
f1:= old_factorize(p1);
f2:= old_factorize(p2);
order1:=arglength(f1);
order2:=arglength(f2);
for j:=1 : order1 do
 for jj:=1 : order2 do
  ma:=max(ma,comppol(part(f1,j),part(f2,jj),k));
return ma;
end;

algebraic procedure maxdegf(r1,r2,p,k);
% evalutes an upper bound for the degree of f
% with respect to variable k
% (Lemma 3.6 in Koornwinder)
begin
scalar l,dp, hold,hold2,!*exp,!*factor;
on exp;
pminus:=sub(k= k+1,r1) - r2;
pplus:=sub(k= k+1,r1) + r2;
lplus:=deg( pplus,k);
lminus:=deg( pminus,k);
if pminus=0 then lminus:=-1;
dp:=deg(p,k);
if (lplus leq lminus) then
 return max(dp - lminus,0)
else
 <<
 el:=
 part(coeff(pplus,k),lplus+1);
>>;
 if arglength(coeff(pminus,k))<lplus then
   dlminus1:=0
 else
   dlminus1:=part(coeff(pminus,k),lplus);
 hold:=-2 * dlminus1/el;
 hold2:=dp -lplus +1;
 if fixp(hold) and (hold geq  0) then
 % case b2 in Koornwinder
   return max(hold,hold2)
 else
 % case b1 in Koornwinder
 return max(hold2,0);
;
end;

algebraic procedure comppol(f,g,k);
% Tests for polynomials f and g in k if f(k)=g(k+j)
% for some nonnegative
% integer j, while f and g are not constant in k
% if this is the case comppol returns that j and
% the leading coefficiants of the polynomials
begin
 scalar nn,a,b,c,d,j, !*exp;
on exp;
nn:=deg(f,k);
if (nn=0) or (nn neq deg(g,k)) then return {-1,1,1};
a:=part(coeff(f,k),nn+1);
b:=part(coeff(f,k),nn);
c:=part(coeff(g,k),nn+1);
d:=part(coeff(g,k),nn);
j:=(b*c-a*d)/nn/a/c;
if not fixp(j) then return {-1,1,1}
else if j<0 then return {-1,1,1};
if (c * f - a * sub(k=k+j,g) = 0) then return {j,a,c}
else return {-1,1,1};
end;

algebraic procedure hyperterm(upper,lower,z,k);
% converts the representation of a hypergeomeric term
begin
scalar lu,ll;
lu:=arglength(upper);
ll:=arglength(lower);
return ((for j:=1:lu product(pochhammer(part(upper,j),k)))*z^k/
((for j:=1:ll product(pochhammer(part(lower,j),k)))*factorial(k)));
end;

algebraic procedure simplify_combinatorial(term1);
% converts binomials, products, pochhammers, and
% factorials in term1 into gammas, and
% applies simplify_gamma to the modified term1
   simplify_gamma(togamma(term1));

% write *mode;

% in "zeilberg.red"$
% gosper(gamma(k+n+2)*n/((k+n+1)*gamma(k+2)*gamma(n+1)),k);

algebraic procedure togamma(term1);
% converts binomials, products, pochhammers, and
% factorials in term1 into gammas
begin
term1:=(term1 where prod(~term,~k,~m1,~m2)=>
   producttopochhammer(term,k,m1,m2));
term1:=(term1 where local_prod(~term,~k,~m1,~m2)=>
   prod(~term,~k,~m1,~m2));
term1:=(term1 where pochhammer(0,~k)=>0);
term1:=(term1 where pochhammer(~n,~k)=> gamma(~n+~k)/gamma(~n));
term1:=(term1 where binomial(~n,~k) =>
        factorial(~n)/(factorial(~n - ~k)*factorial(~k)));
term1:=(term1 where factorial(~k)=> gamma(~k+1));
return term1;
end;

%ratsimplify_gamma(gamma(n) *n);

algebraic procedure ratsimplify_gamma(term1);
begin
scalar !*exp,!*factor,deno,nume, ln,ld,dega,nuga,derest,nurest,
lnurest,lnuga,lderest,ldega,jj,j,sp,term2,tmp;
on factor;
deno:=den(term1);
nume:=num(term1);
nurest:={};nuga:={};
derest:={};dega:={};
% construct two lists
% dega with parts that are gamma terms
% and derest with the others.
if arglength(deno) >0 then
<<
 if not(part(deno,0)= times) then
  if not freeof(deno,gamma) then
   <<
   deno:=strip_power(deno);
   tmp:=part(deno,1);
   if not(part( tmp,0) = gamma) then
    return term1
   else
    dega:=deno
   >>
  else
   derest:=strip_power(deno)
 else
 <<
  ld:=arglength(deno);
  for j:=1: ld do
  <<
   sp:=strip_power(part(deno,j));
   tmp:=part(sp,1);
   if not freeof(tmp,gamma) and part(tmp,0) = gamma then
     dega:=append(dega,sp)
   else
     derest:=append(derest,sp);
  >>; %for
 >>; %else
>> % if
else
 derest:={deno};
%ende
if arglength(nume) >0 then
<<
 if not(part(nume,0)= times) then
  if not freeof(nume,gamma) then
   <<
   nume:=strip_power(nume);
   tmp:=part(nume,1);
   if not(part( tmp,0) = gamma) then
    return term1
   else
    nuga:=nume
   >>
  else
   nurest:=strip_power(nume)
 else
 <<
  ln:=arglength(nume);
  for j:=1: ln do
  <<
   sp:=strip_power(part(nume,j));
   tmp:=part(sp,1);
   if not freeof(tmp,gamma) and part(tmp,0) = gamma then
     nuga:=append(nuga,sp)
   else
     nurest:=append(nurest,sp);
  >>; %for
 >>; %else
>> % if
else
 nurest:={nume};
%ende
% dega with parts that are gamma terms
% and derest with the others.
ldega:=arglength(dega);
lderest:=arglength(derest);
lnuga:=arglength(nuga);
lnurest:=arglength(nurest);
if ldega>0 then
<<
 for j:=1 : ldega  do
 <<
  tmp:=part(dega ,j);
  tmp:=part(tmp,1);
  for jj:=1 : lderest do
   if (part(derest,jj) - tmp) = 0  then
    <<
     derest:=(part(derest,jj):=1);
     tmp:=tmp+1;
     dega:=(part(dega,j):=gamma(tmp));
    >>;
  for jj:=1 : lnurest do
   if (part(nurest,jj) - tmp) = -1  then
    <<
     nurest:=(part(nurest,jj):=1);
     tmp:=tmp-1;
     dega:=(part(dega,j):=gamma(tmp));
    >>;
 >> %for j
>>; %ldega>0
if lnuga>0 then
<<
 for j:=1 : lnuga  do
 <<
  tmp:=part(nuga ,j);
  tmp:=part (tmp,1);
  for jj:=1 : lnurest do
   if (part(nurest,jj) - tmp) = 0  then
    <<
     nurest:=(part(nurest,jj):=1);
     tmp:=tmp+1;
     nuga:=(part(nuga,j):=gamma(tmp));
    >>;
  for jj:=1 : lderest do
   if (part(derest,jj) - tmp) = -1  then
    <<
     derest:=(part(derest,jj):=1);
     tmp:=tmp-1;
     nuga:=(part(nuga,j):=gamma(tmp));
    >>;
 >>% for j;
>>; %lnuga>0
 term2:=1;
%if lnuga>0 then
for j:=1 : lnuga do term2:=term2 *part(nuga,j);
%if lnurest>0 then
for j:=1 : lnurest  do term2:=term2 * part(nurest,j);
%if ldega>0 then
for j:=1 : ldega do term2:=term2 /part(dega,j);
%if lderest>0 then
for j:=1 : lderest  do term2:=term2 / part(derest,j);
if term2 = term1 then
 return term2
else
 return ratsimplify_gamma(term2);
end; %ratsimplify_gamma

algebraic procedure strip_power(term1);
begin
scalar j,!*factor,list1;
on factor;
list1:={};
if (arglength(term1)<2) or
   (part(term1,0) neq expt) or
   not fixp(part(term1,2))  then
  return {term1}
else
  for j:=1: part(term1,2) do
   list1:=append(list1,{part(term1,1)});
return list1;
end;





% ratsimplify_gamma(gamma(n)/n);
% ratsimplify_gamma(gamma(n)/(n-1));
% ratsimplify_gamma(gamma(n)^2/(n-1)^2);
% ratsimplify_gamma(gamma(n)^2*n^2);
% ratsimplify_gamma((n+1) * gamma(n)^2*n^2);


algebraic procedure simplify_gamma(term1);
% converts all subexpressions
% gamma(xi) -> gamma(xi + m)/((xi)*(xi+1)*...* (xi+m-1))
% where m is the largest integer , so that a subexpression
% gamma(xj) of term1 exists with xj = xi + m.
%
begin
scalar !*exp,!*factor,!*gcd,high,highl,highlength,j;
%on gcd;
%on factor;
if freeof(term1,gamma) then return term1;
highl:={};
highl:=highest_gamma_order(term1,highl);
if lisp !*zb_timer then
 << write "flag:highl:=",highl, "at "; showtime>>;
if highl = {} then return term1;
%term1:=gammashift(term1,highl);
term1:=matchgammashift(term1,highl);
%globalterm3:=term1;
%globalterm1:=gammashift(term1,highl);
%highlength:=arglength(highl);
%for j:=1:highlength do
%<<
%high:=part(highl,j);
%term1:=gammashift(term1,high);
%term1:=(term1 where gamma(~local_x)=>shift_gamma(~local_x,high));
%term1:=(term1 where local_gamma(~local_x)=>gamma(~local_x));
%>>;
%globalterm2:=term1;
%on exp;
return term1;
end;

algebraic procedure matchgammashift(term1,highl);
begin
scalar deno,nume,!*factor;
%on factor;
nume:=num(term1);
deno:=den(term1);
nume:=(nume where gamma(~local_x)=>listshift_gamma(~local_x,highl));
nume:=(nume where local_gamma(~local_x)=>gamma(~local_x));
if nume=0 then return 0;
deno:=(deno where gamma(~local_x)=>listshift_gamma(~local_x,highl));
deno:=(deno where local_gamma(~local_x)=>gamma(~local_x));
return nume/deno;
end;


algebraic procedure highest_gamma_order(term1,highl);
% produces a list of maximal xi for which
% exist subexpressions gamma(xi) of term1, and
% xi-xj is no integer iff xi neq xj
begin
scalar jjj,jj,j,max, term1length,localhighl,localhighllength,new;
term1length:=arglength(term1);
if (term1length<1) or freeof(term1,gamma)  then return highl;
new:=1;
highllength:=arglength(highl);
if part(term1,0) = gamma then
 <<
  if term1length neq 1 then
   rederr("gamma has illegal number of arguments");
  for j:=1 : highllength do
   if fixp(part(highl,j) - part(term1,1)) then
     <<
      if (part(highl,j) - part(term1,1)<0) then
        highl:=(part(highl,j):=part(term1,1));
      new:=0;
     >>;
  if new = 1 then highl:=append(highl,{part(term1,1)});
 >>
else
 for j:=1 : term1length do
 <<
  localhighl:=highest_gamma_order(part(term1,j),{});
  localhighllength:=arglength(localhighl);
  for jjj:=1:localhighllength do
   <<
    highllength:=arglength(highl);
    new:=1;
    for jj:=1 :highllength do
      if fixp(part(highl,jj) - part(localhighl,jjj)) then
        <<
        if (part(highl,jj) - part(localhighl,jjj)<0)  then
         highl:=(part(highl,jj):=part(localhighl,jjj));
        new:=0
        >>;
    if new = 1 then highl:=append(highl,{part(localhighl,jjj)})
   >>;
 >>; % for j:=1 : term1length
% if new = 1
return highl;
end;

algebraic procedure gammashift(term1,highl);
begin
scalar lhighl,term2,xx,nminusxx, j,jj,n;
if freeof(term1,gamma) then
  return term1;
if (arglength(term1)>1) then
  return map(gammashift(~zbglobal,highl),term1);
if (part(term1,0) = gamma) then
<<
 lhighl:=arglength(highl);
 term2:=term1;jj:=1;
 while (term1=term2) and (jj leq lhighl) do
 <<
  xx:=part(term1,1);
  n:=part(highl,jj);
  nminusxx:=n-xx;
  if (nminusxx = 0) then term1:=0;
  if fixp(nminusxx) and (nminusxx neq 0) then
   if nminusxx>0 then
    term2:=(gamma(n) / (for j:=1: nminusxx product(n-j)))
   else
    term2:=gamma(n) * (for j:=1: -nminusxx product(xx-j));
  jj:=jj+1;
  >>;
 return term2
>>
else
 return map(gammashift(~zbglobal,highl),term1);
end;




algebraic procedure shift_gamma(xx,n);
% shifts gamma-expression if n - xx is an integer
% warning: returns operator local_gamma instead of gamma
begin
scalar nminusx,j;
nminusx:=n-xx;
if not fixp(nminusx) then return local_gamma(xx);
if nminusx>0 then return local_gamma(n) /
(for j:=1: nminusx product(n-j)) else
return local_gamma(n) * (for j:=1: -nminusx product(xx-j));
end;


algebraic procedure listshift_gamma(xx,highl);
begin
scalar lhighl,nminusx,j,n,ret;
lhighl:=arglength(highl);
ret:=local_gamma(xx);
for j:=1 :lhighl  do
<<
 n:=part(highl,j);
 nminusx:=n-xx;
 if fixp(nminusx) then
 <<
  if nminusx>0 then
   ret:=local_gamma(n) / (for j:=1: nminusx product(n-j))
  else
   ret:=local_gamma(n) * (for j:=1: -nminusx product(xx-j));
  % j:=highl
 >>
>>;
return ret;
end;

algebraic procedure producttopochhammer(term,k,m1,m2);
% converts products into pochhammers
begin
 scalar fehler,ar,co,aa,bb,liste,tlength,j,pa;
 fehler:=nil;
if (den(term) neq 1) then return
  producttopochhammer(num(term),k,m1,m2)/
producttopochhammer(den(term),k,m1,m2);
 liste:= old_factorize(term);
 %gets initialized with factors of term
 %during the procedure I exchange them with pochhammer terms
 tlength:=arglength(liste);
 for j:=1 : tlength do
 <<
  pa:=part(liste,j);
  co:=coeff(pa,k);
  ar:=arglength(co);
  if ar>2 then fehler:=t
  else if ar<2 then
     liste:=(part(liste,j):= pa^(m2-m1+1))
  else
   <<
    aa:=part(co,2);
    bb:=pa/aa -k;
    if bb = 0 then
     liste:=(part(liste,j):=pochhammer(m1+ part(co,1),m2-m1+1))
    else
     liste:=(part(liste,j):=
      aa^(m2-m1)*pochhammer(bb,m2+1)/pochhammer(bb,m1))
   >>
 >>;
 if fehler then return local_prod(term,k,m1,m2);
 return (for j:=1: tlength  product( part(liste,j)));
end;

% extended
% authors: Gregor Stoelting & Wolfram Koepf

symbolic  procedure extended_gosper!-eval u;
(<< abc:=
<<
if length u = 2 then extended_gosper1(aeval car u, aeval cadr u)
else  if length u = 3 then extended_gosper2(aeval car u, aeval cadr u,
  aeval caddr u)
else  if length u = 4
 then extended_gosperborders(aeval car u, aeval cadr u,
  aeval caddr u,aeval cadddr u)
else rederr("illegal number of arguments")>>;
if eqcar (abc,'!*sq) then
      list('!*sq,cadr abc,nil) else abc>>) where abc=nil;
put ('extended_gosper, 'psopfn,'extended_gosper!-eval);

algebraic procedure extended_gosperborders(term1,k,k0,k1);
begin
scalar tmp,gosper2,!*factor;
gosper2:=extended_gosper1(term1,k);
if zb_direction = up then gosper2:=sub(k=k+1,gosper2);
tmp:=sub(k=k1,gosper2)-sub(k=k0-1,gosper2);
if lisp !*zb_factor then
 <<
  on factor;
  return num(tmp)/den(tmp)
 >>
else
 return tmp;
end;% extended_gosperborders


algebraic procedure extended_gosper2(term1,k,m);
begin
scalar !*exp,!*factor,s,tmp;
tmp:=gosper1(sub(k=k*m,term1),k);
if tmp = zb_gancfse then
  newrederr {"extended Gosper algorithm (Koepf): no ",m,
  "-fold hypergeometric solution"};
s:=sub(k=k/m,tmp);
if lisp !*zb_factor then on factor;
return s;
end; %extended_gosper2

algebraic procedure extended_gosper1(term1,k);
begin
scalar sol,!*factor,j,l,partj,s,m,tmp;
if lisp !*zb_trace then
     write "Koepf extension of Gosper algorithm entered...";
list1:=argumentlist(term1,{});
if list1 = {} then return gosper0(term1,k);
list2:=foreach partj in list1 collect linearfactor(partj,k);
m:=lcml(list2);
if lisp !*zb_trace then
  write "linearizing integer with respect to ",k," is ",m;
s:=extended_gosper2(term1,k,m);
if lisp !*zb_trace then write "s(",k,"):=",s;
sol:=(for j:=0:m-1 sum(sub(k=k-j,s)));
%if m>1 then sol:=simplify_combinatorial(sol);
if zb_direction = up then
 sol:=sub(k=k+1,sol);
if lisp !*zb_factor then on factor;
return sol
end; %extended_gosper1


symbolic  procedure extended_sumrecursion!-eval u;
(<< abc:=
<<
if length u = 3 then
  extended_sumrecursion0(aeval car u, aeval cadr u,aeval caddr u)
else  if length u = 5
 then extended_sumrecursion20(aeval car u, aeval cadr u,
  aeval caddr u,aeval cadddr u,car cddddr u)
else rederr("illegal number of arguments")>>;
if eqcar (abc,'!*sq) then
      list('!*sq,cadr abc,nil) else abc>>) where abc=nil;
put ('extended_sumrecursion, 'psopfn,'extended_sumrecursion!-eval);

algebraic procedure extended_sumrecursion0(term1,k,n);
begin
scalar !*factor,!*exp;
if lisp !*zb_factor then on factor;
return part(extended_sumrecursion1(term1,k,n),1);
end;

%extended_hyperrecursion1({ - n,b,c},{(b - n + 1)/2,2*c},1,n);

algebraic procedure extended_sumrecursion1(term1,k,n);
begin
scalar m,j,l,partj,s,!*exp,dg,bc;
on exp;
if lisp !*zb_trace then
     write "Koepf extension of Zeilberger algorithm entered...";
list1:=argumentlist(term1,{});
if list1 = {} then return sumrecursion1(term1,k,n,1,zb_order);
listk:=foreach partj in list1 collect linearfactor(partj,k);
listn:=foreach partj in list1 collect linearfactor(partj,n);
l:=lcml(listk);
m:=lcml(listn);
if lisp !*zb_trace then
   <<
     write "linearizing integer with respect to ",k," is ",l;
     write "linearizing integer with respect to ",n," is ",m;
   >>;
if m=1 and l=1 then
<<
 bc:=simplify_combinatorial(term1/sub(n=n-1,term1));
 globalbc:=bc;
 if not type_ratpoly(bc,n) then
 <<
  if lisp !*zb_trace then
    write "F(",n,",local_k)/F(",n,"-1,local_k):=",bc;
  rederr("Zeilberger algorithm not applicable")
 >>;
 dg:=simplify_combinatorial(term1/sub(k=k-1,term1));
 on exp;
 if not type_ratpoly(dg,k) then
 <<
  if lisp !*zb_trace then
    write "F(",n,",",k,")/F(",n,",",k,"-1):=",dg;
  rederr("Zeilberger algorithm not applicable")
 >>;
 return(sumrecursion1(term1,k,n,1,zb_order))
>>;
return extended_sumrecursion2(term1,k,n,m,l);
end; %extended_sumrecursion1

algebraic procedure extended_sumrecursion20(term1,k,n,m,l);
begin
scalar !*factor,!*exp;
if lisp !*zb_factor then on factor;
return part(extended_sumrecursion2(term1,k,n,m,l),1);
end;

algebraic procedure extended_sumrecursion2(term1,k,n,m,l);
begin
scalar term2,tmpterm,rule,!*factor,!*exp,order1;
term2:=sub({k=k*l,n=n*m},term1);
if lisp !*zb_trace then
  write  "applying Zeilberger algorithm to F(",n,",",k,"):=",term2;
tmpterm:=sumrecursion1(term2,k,n,1,zb_order);
order1:=m* part(tmpterm,2);
tmpterm:=part(tmpterm,1);
tmpterm:=sub({n=n/m},tmpterm);
rule:={summ(~nn/~mm)=>summ(nn) when mm=m};
tmpterm:=num(tmpterm where rule);
off factor;
tmpterm:=tmpterm;
if lisp !*zb_factor then on factor;
return({tmpterm,order1})
end;

symbolic  procedure extended_hyperrecursion!-eval u;
(<< abc:=
<<
if length u = 4 then
  extended_hyperrecursion0(aeval car u, aeval cadr u,aeval caddr u,
   aeval cadddr u)
%else  if length u = 5
%        then extended_hyperrecursion2(aeval car u, aeval cadr u,
%  aeval caddr u,aeval cadddr u,car cddddr u)
else rederr("illegal number of arguments")>>;
if eqcar (abc,'!*sq) then
      list('!*sq,cadr abc,nil) else abc>>) where abc=nil;
put ('extended_hyperrecursion, 'psopfn,'extended_hyperrecursion!-eval);


algebraic procedure extended_hyperrecursion0(upper,lower,x,n);
   part(extended_hyperrecursion1(upper,lower,x,n),1);

algebraic procedure extended_hyperrecursion1(upper,lower,x,n);
  extended_sumrecursion1(hyperterm(upper,lower,x,local_k),local_k,n);


algebraic procedure linearfactor(term1,n);
begin
scalar p,co;
co:=coeff(term1,n);
if arglength(co) = 1 then return 1;
p:=den(part(co,2));
if arglength(co) > 2 or (not fixp(p)) then
   rederr("Extended Gosper algorithm not applicable");
return p;
end;

algebraic procedure lcml(list1);
begin
% finds least common multiple of a list of integers
scalar p1,l;
l:=arglength(list1);
p1:=part(list1,1);
if l = 1 then return p1;
if l = 2 then return lcm( p1, part(list1,2));
return lcm(p1,lcml(rest(list1)));
end;

algebraic procedure argumentlist(term1, list1);
begin
scalar head1,j,l;
l:=arglength(term1);
if l<1 then return list1;
head1:=part(term1,0);
if head1 = gamma or
   %head1 = expt or
   head1 = factorial then
   list1:=append(list1,{part(term1,1)})
else if
   head1 = pochhammer or
   head1 = binomial then
   list1:=append(list1,{part(term1,1),part(term1,2)})
else
 for j:=1:l do
  list1:=argumentlist(part(term1,j),list1);
return list1;
end;

operator hypergeometric;

%let {gamma(~n)=>factorial(n-1) when (fixp(n) and n>0)};


%sumtohyper(hyperterm({a,a,a,a,a,b},{c},x,k),k);


algebraic procedure negintoccurs(list1);
begin
scalar l,tmp,tmp2,j;
 tmp2:=nil;
 l:=arglength(list1);
 if l = 0 then return nil;
 for j:=1 : l do
 <<
  tmp:=part(list1,j)   ;
  if fixp(tmp) and tmp<0 then tmp2:=t
 >>;
 return tmp2 ;
end; % negintoccurs

algebraic procedure sumtohyper(ank,k);
begin
scalar de,rat,numerator,denominator,numfactors,denfactors,lc,l,numlist,
oldnumlist, olddenlist,tmp,tmp2,
numdegree,denfactors,denlist, dendegree,i,j,lcden,
lcnum,!*exp,!*factor,!*gcd, gcdterm;
on exp;on gcd;
ank:=simplify_combinatorial(ank);
de:=simplify_combinatorial(sub(k=k+1,ank)/ank);
 if lisp !*zb_trace then
   write "a(",k,"+1)/a(",k,"):=",de;
numerator:=num(de);
denominator:=den(de);
if not polynomq4(numerator,k) then
 rederr("cannot be converted into hypergeometric form");
if not polynomq4(denominator,k) then
 rederr("cannot be converted into hypergeometric form");
numerator:=numerator;
denominator:=denominator;
numfactors:= old_factorize(numerator);
denfactors:= old_factorize(denominator);
lcnum:=lcof(numerator,k);
lcden:=lcof(denominator,k);
if lcnum = 0 then lcnum:=numerator;
if lcden = 0 then lcden:=denominator;
lc:=lcnum/lcden;
if freeof(first(numfactors),k) then numfactors:=rest(numfactors);
numlist:={};
len:=length(numfactors);
for i:=1:len do
<<
 fir:=first(numfactors);
 if not freeof(fir,k) then
 <<
  new:=-part(first(solve(fir,k)),2);
  numlist:=append(numlist,{new});
 >>;
 numfactors:=rest(numfactors);
>>;
maxint:=maxposint(numlist);
len:=length(denfactors);
denlist:={};
for j:=1:len do
<<
 fir:=first(denfactors);
 if not freeof(fir,k) then
 <<
  if not polynomq4(fir,k) or deg(fir,k)>2  then
    rederr("not yet implemented")
  else
   tmp:=solve( fir,k);
  for jj:=1: arglength(tmp) do
   denlist:=append(denlist,{-part(part(tmp,jj),2)});
 >>;
 denfactors:=rest(denfactors);
>>;
minint:=minnegint(denlist);
if minint leq 0 then
<<
 if lisp !*zb_trace then
    write "shifting by ",1-minint;
 numlist:=sub(k= k+1-minint,numlist);
 if numberofzeros(numlist)>0 then
  rederr("not yet implemented")
>>
else
<<
 if maxint geq 0 then
 <<
 if lisp !*zb_trace then
    write "shifting by ",1-maxint;
 denlist:=sub(k= k+1-maxint,denlist);
 if numberofzeros(denlist)>0 then
  rederr("not yet implemented");
  minint:=maxint;
 >>
>>;
shiftnumber:=1-minint;
if lisp !*zb_trace then
    write "calculating initial value";
olddenlist:=denlist;
denlist:={};
for j:=1: arglength(olddenlist) do
 denlist:=append({part(olddenlist,j ) + 1-minint},denlist);
oldnumlist:=numlist;
numlist:={};
for j:=1: arglength(oldnumlist) do
 numlist:=append({part(oldnumlist,j ) + 1-minint},numlist);
if sub(k=1-minint,den(ank)) = 0 or sub(pochhammer= poch, den(ank)) = 0
  then tmp:=limit(ank,k,1-minint)
else
 tmp:=sub(k=1-minint,ank);
if  member(1,denlist) then
<<
 tmplist:={};
 done:=0;
 for i:=1 : arglength(denlist) do
  if not(part(denlist,i)=1) or done then
   tmplist:=append(tmplist,{ part(denlist,i)})
  else
   done:=1;
 denlist:=tmplist;
>>
else
 numlist:=append(numlist,{1});
tmp:=simplify_combinatorial(tmp)*hypergeom(numlist,denlist,lc);
if lisp !*zb_trace then
  <<
    write "finished conversion in hypergeometric notation";
    write tmp;
  >>;
return tmp;
end$  % sumtohyper


%remove_reduntant_elements({1,3,6},{1,1,1});
%remove_reduntant_elements({1,3,6},{1,1,3});


algebraic procedure remove_reduntant_elements(denlist,numlist);
begin
scalar j,jj,jjj,ln,ld,tmp;
ln:=arglength(numlist);
ld:=arglength(denlist);
if (ln>0) and (ld>0) then
 <<
  for j:=1:arglength(numlist) do
   for jj:=1 : arglength(denlist) do
     if part(numlist,j) =  part(denlist,jj) then
       <<
        tmp:=denlist; denlist:={};
        for jjj:=1 : jj-1 do denlist:=append(denlist,{part(tmp,jjj)});
        for jjj:=jj+1 :arglength(tmp) do
         denlist:=append(denlist,{part(tmp,jjj)});
         tmp:=numlist; numlist:={};
        for jjj:=1 : j-1 do numlist:=append(numlist,{part(tmp,jjj)});
        for jjj:=j+1 :arglength(tmp) do
         numlist:=append(numlist,{part(tmp,jjj)});
       jj:=arglength(denlist)
        >>
 >>;
return {denlist,numlist};
end;



algebraic procedure trim (u);
  if u = {} then {} else
      if member(first u,rest u) then trim rest u
         else first u . trim rest u;



algebraic procedure maxposint(list1);
begin
scalar partj, l,j,tmp;
tmp:=-1;
l:=arglength(list1);
for j:=1 : l do
<<
 partj:=part(list1,j);
 if fixp(partj) and (partj geq 0)  then
   tmp:=max(tmp,partj);
>>;
return tmp;
end;


algebraic procedure minnegint(list1);
begin
scalar partj, l,j,tmp;
tmp:=1;
l:=arglength(list1);
for j:=1 : l do
<<
 partj:=part(list1,j);
 if fixp(partj) and (partj leq 0)  then
   tmp:=min(tmp,partj);
>>;
return tmp;
end;





algebraic procedure binom(n,k);
begin
scalar i;
 if fixp(n) then
  if n>0  then
   return factorial(n)/(factorial(k)*factorial(n-k))
  else
   if n<0  then
    rederr("negative integer argument")
   else return delta(0,k)
 else
  if fixp(k) then
   return (for i:=0:k-1 product(n-i))/factorial(k)
  else
   return binomial(n,k);
end;



algebraic procedure numberofzeros(list1);
begin scalar c,l,j;
c:=0;
l:=arglength(list1);
for j:=1 :l do
 if part(list1,j) = 0 then c:=c+1;
return c;
end;


algebraic procedure patternarguments(term1,pattern,list1);
begin
scalar j,l;
if freeof(term1,pattern) then return list1;
l:=arglength(term1);
if part(term1,0) = pattern then
 return append(list1 ,{part(term1,1)})
else
 for j:=1:l do
  list1:=patternarguments(part(term1,j),pattern,list1);
return list1;
end;

algebraic procedure remove_part(list1,j);
begin
scalar jj,l,list2;
list2:={};
l:=arglength(list1);
for jj:=1 :j-1 do
list2:=append(part(list1,jj),list2);
for jj:=j+1 : l do
 list2:=append(part(list1,jj),list2);
return list2;
end;

algebraic procedure remove_nonlinear_parts(list1,k);
begin
scalar j,list2,!*exp;
on exp;
list2:=list1;
while list1 neq {} do
<<
 if deg(first(list1),k) > 1 then
  rederr("nonlinear argument in gamma")
 else
  if deg(first(list1),k) = 0 then
   list2:=rest(list2);
 list1:=rest(list1)
>>;
return list2;
end;



algebraic procedure closedform_initialization(f,k,n);
begin
scalar co,j,l,ga,mini,maxi,!*exp,ba,b,a,tmpmax,tmpmin;
on exp;
f:=den(simplify_combinatorial(f));
mini:=nil;
maxi:=nil;
ga:=patternarguments(f,gamma,{});
ga:=remove_nonlinear_parts(ga,k);
l:=arglength(ga);
for j:=1 :l do
<<
 co:=coeff(part(ga,j),k);
 a:=part(co,2);
 b:=part(co,1);
 ba:=-b/a;
 if numberp(a) and fixp(ba) then
  if a >0 then
   if maxi = nil then
    maxi:=ba
   else
    maxi:=max(maxi,ba)
  else % a <0
   if mini = nil then
    mini:=ba
   else
    mini:=min(mini,ba)
 else
  if not freeof(ba,n) then
   <<
   if a >0 then
    tmpmax:=ba
   else
    tmpmin:=ba;
   >>;
>>;
if maxi = nil then maxi:=tmpmax;
if mini = nil then mini:=tmpmin;
return {maxi,mini};
end; %closedform_initialization

%f:=(2*pi)^(-1/2)* 2^(2*w2-1/2)*gamma(w2) * gamma(w2+1/2)-gamma(w2*2);
%simplify_gamma2(f);
%simplify_gamman(f,2);


algebraic procedure simplify_gamma2(term1);
begin
scalar p,l,j,jj,jjj,w1,w2,list1,changed;
list1:=patternarguments(term1,gamma,{});
l:=arglength(list1);
changed:={};
for j:=1:l do
<<
 changed:=nil;
 w1:=part(list1,j);
 jj:=0;
 while not changed and jj < l do
  <<
  jj:=jj+1;
  w2:=part(list1,jj);
  p:=w1 - 2* w2;
  if fixp(p) then
   <<
    if p = 0 then
     term1:=sub(gamma(w1)= (2*pi)^(-1/2)* 2^(2*w2-1/2)*
             gamma(w2) * gamma(w2+1/2),term1)
    else
     if p>0 then
      term1:=sub(gamma(w1)= (for jjj:=1 : p product w1-jjj) *
             (2*pi)^(-1/2)* 2^(2*w2-1/2)*
             gamma(w2) * gamma(w2+1/2),term1)
     else
      term1:=sub(gamma(w1)= 1/(for jjj:=0 : (-p-1) product w1 -jjj)*
             (2*pi)^(-1/2)* 2^(2*w2-1/2)*
              gamma(w2) * gamma(w2+1/2),term1);
    changed:=1
   >> % if
  >> %while
>>; % for
return simplify_combinatorial(term1);
end; %simplify_gamma2

%f:=(2*pi)^(-1/2)* 2^(2*w2-1/2)*gamma(w2) * gamma(w2+1/2)-gamma(w2*2);
%simplify_gamma2(f);
%simplify_gamman(f,2);
%simplify_gamman(gamma(3*w2) -subst,3);
%ff:=( - 2*sqrt(3)*gamma(3*w2)*pi + 3**(3*w2)*gamma((3*w2 + 2)/3)*
%    gamma((3*w2 + 1)/3)*gamma(w2))/(2*sqrt(3)*pi)$
%sub(w2=w2+1,ff);
%simplify_gamman(ws,3);
%simplify_gamman(ff,3);



algebraic procedure simplify_gamman(term1,n);
% applies rule 6.1.20 p 77 in Abramowitz
begin
scalar subst,p,l,j,jj,jjj,jjjj,w1,w2,list1,changed;
list1:=patternarguments(term1,gamma,{});
l:=arglength(list1);
changed:={};
for j:=1:l do
<<
 changed:=nil;
 w1:=part(list1,j);
 jj:=0;
 while not changed and jj < l do
  <<
  jj:=jj+1;
  w2:=part(list1,jj);
  p:=w1 - n* w2;
  if fixp(p) then
   <<
    subst:=(2*pi)^(1/2*(1-n))*n^(n*w2-1/2)*(for jjjj:=0:(n-1)
        product (gamma(w2+ jjjj/n)));
    if p = 0 then
     term1:=sub(gamma(w1)=subst ,term1)
    else
     if p>0 then
      term1:=sub(gamma(w1)= (for jjj:=1 : p product w1-jjj) *
             subst,term1)
     else
      term1:=sub(gamma(w1)= 1/(for jjj:=0 : (-p-1) product w1 +jjj)*
             subst,term1);
    changed:=1
   >> % if
  >> %while
>>; % for
return simplify_combinatorial(term1);
end; %simplify_gamman

operator zb_subst;

% simplify_gamma3(f);
algebraic procedure simplify_gamma3(term1);
begin
scalar subst,p,l,j,jj,jjj,w1,w2,list1,changed;
list1:=patternarguments(term1,gamma,{});
l:=arglength(list1);
changed:={};
for j:=1:l do
<<
 changed:=nil;
 w1:=part(list1,j);
 jj:=0;
 while not changed and jj < l do
  <<
  jj:=jj+1;
  w2:=part(list1,jj);
  p:=w1 - 3* w2;
  if fixp(p) then
   <<
   subst:=
  (2*pi)^(-1) * 3^(3*w2-1/2)* gamma(w2) * gamma(w2+1/3)* gamma(w2+2/3);
    if p = 0 then
     term1:=sub(gamma(w1)= zb_subst(j) ,term1)
    else
     if p>0 then
      term1:=sub(gamma(w1)= (for jjj:=1 : p product w1-jjj) *
              zb_subst(j)  ,term1)
     else
      term1:=sub(gamma(w1)= 1/(for jjj:=0 : (-p-1) product w1 +jjj)*
               zb_subst(j)  ,term1);
    term1:=sub(zb_subst(j)=subst,term1);
    changed:=1
   >> % if
  >> %while
>>; % for
return simplify_combinatorial(term1);
end; %simplify_gamma3



% auxiliary functions

symbolic procedure newrederr(u);
   <<terpri!* t;
     prin2!* "***** ";
     if eqcar(u,'list) then foreach xx in cdr u do newrederr1(xx)
       else  newrederr1 u;
     terpri!* nil; erfg!*:=t; error1()>>;

symbolic procedure newrederr1(u);
     if not atom u and atom car u and cdr u and atom cadr u
        and null cddr u
       then <<prin2!* car u; prin2!* " "; prin2!* cadr u>>
      else maprin u;

flag('(newrederr),'opfn);



% some compatibility functions for Maple sources.
% by Winfried Neun

put('polynomqq,'psopfn,'polynomqqq);

algebraic procedure polynomq4(expr1,k);
begin scalar !*exp;
on exp;
return polynomqq(expr1,k);
end;


% checks if expr is rational in var
algebraic procedure type_ratpoly(expr1,var);
begin
scalar deno, nume;
deno:=den expr1;
nume:=num expr1;
  if (polynomqq (deno,var) and polynomqq (nume,var))
    then return t else return nil;
end;
flag ('(type_ratpoly),'boolean);

symbolic procedure tttype_ratpoly(u,xx);
  ( if fixp xx then t else
        if not eqcar (xx , '!*sq) then  nil
          else and(polynomqqq(list(mk!*sq (numr cadr xx ./ 1),
                                  reval cadr u))
                 ,polynomqqq(list(mk!*sq (denr cadr xx ./ 1),
                                  reval cadr u)))
 ) where xx = aeval(car u);

flag ('(tttype_ratpoly),'boolean);





symbolic flag('(savesolve ),'opfn);

symbolic procedure savesolve (x,y);
<< switch solveinconsistent;
   on solveinconsistent;
   inconsistent!*:=nil;
   if pairp (x:=errorset!*(list ('solveeval,mkquote list(x,y)),nil))
        and not inconsistent!* and not (x = '((list)))
        then << x:=car x;
                if eqcar(cadr x,'equal) then % one element solution
                        list('list,x) else x>>
        else <<erfg!*:=nil;list('list)>> >>;


%checks if x is polynomial in var
symbolic procedure polynomq (x,var);

 if not fixp denr simp x then nil else
 begin scalar kerns,kern,aa;

 kerns:=kernels !*q2f simp x;

 aa: if null kerns then return t;
     kern:=first kerns;
     kerns:=cdr kerns;
     if not(eq (kern, var)) and depends(kern,var)
                then return nil else go aa;
end;

flag('(polynomq),'opfn);

flag ('(polynomq type_ratpoly),'boolean);



symbolic procedure polynomqqq (x);

(
 if not fixp denr (xx:=cadr aeval car x) then nil else
 begin scalar kerns,kern,aa,var;

 var:=reval cadr x;
 kerns:=kernels !*q2f xx;

 aa: if null kerns then return t;
     kern:=first kerns;
     kerns:=cdr kerns;
     if not(eq (kern, var)) and depends(kern,var)
                then return nil else go aa;
end) where xx = x;


put('polynomqq,'psopfn,'polynomqqq);

symbolic procedure polynomqqq (x);

(if fixp xx then t else
 if not onep denr (xx:=cadr xx) then nil
 else begin scalar kerns,kern,aa,var,fform,mvv,degg;

 fform:=sfp  mvar  numr xx;
 var:=reval cadr x;
 if fform then << xx:=numr xx;
    while (xx neq 1) do
     << mvv:=mvar  xx;
        degg:=ldeg  xx;
        xx:=lc  xx;
        if domainp mvv then <<if not freeof(mvv,var) then
                << xx:=1 ; kerns:=list list('sin,var) >> >> else
        kerns:=append ( append (kernels mvv,kernels degg),kerns) >> >>
   else kerns:=kernels !*q2f xx;

 aa: if null kerns then return t;
     kern:=first kerns;
     kerns:=cdr kerns;
     if not(eq (kern, var)) and depends(kern,var)
                then return nil else go aa;
end) where xx = aeval(car x);

put('polynomqq,'psopfn,'polynomqqq);

symbolic procedure ttttype_ratpoly(u);
  ( if fixp xx then t else
        if not eqcar (xx , '!*sq) then nil
          else and(polynomqqq(list(mk!*sq (numr cadr xx ./ 1),
                              reval cadr u)),
                   polynomqqq(list(mk!*sq (denr cadr xx ./ 1),
                              reval cadr u)))
 ) where xx = aeval(car u);

flag ('(type_ratpoly),'boolean);

put('type_ratpoly,'psopfn,'ttttype_ratpoly);

endmodule;

end;
