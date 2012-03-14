on list;
on errcont;

% 1.) Example of ordering of objects such as fer,bos,axp;
axp(bos(f,0,0))*bos(g,3,1)*fer(k,1,0);
%fer(k,1,0)*bos(g,3,1)*axp(bos(f,0,0));

% 2.) Example of ordering of fer and fer objects
fer(f,1,2)*fer(f,1,2);
% 0
fer(f,1,2)*fer(g,2,3);
% -fer(g,2,3)*fer(f,1,2);
fer(f,1,2)*fer(f,1,3);
% - fer(f,1,3)*fer(f,1,2);
fer(f,1,2)*fer(f,2,2);
% - fer(f,2,2)*fer(f,1,2);

% 3.) Example of ordering of bos and bos objects;
bos(f,3,0)*bos(g,0,4);
%bos(g,0,4)*bos(f,3,0);
bos(f,3,0)*bos(f,0,0);
%bos(f,3,0)*bos(f,0,0);
bos(f,3,2)*bos(f,3,5);
%bos(f,3,5)*bos(f,3,2);

% 4.) ordering of inverse superfunctions;
% last index in bos objects denotes powers;
bos(f,0,3)*bos(k,0,2)*bos(zz,0,3,-1)*bos(k,0,2,-1);
%bos(zz,0,3,-1)*bos(f,0,3);
bos(c,0,3)*bos(b,0,2)*bos(a,0,3,-1)*bos(b,0,2,-1);
%bos(c,0,3)*bos(a,0,3,-1);

% 5.) Demostration of inverse rule;
let inverse;
bos(f,0,3)**3*bos(k,3,1)**40*bos(f,0,3,-2);
%bos(k,3,1,40)*bos(f,0,3,1);
clearrules inverse;


% 6.) Demonstration of (susy) derivative operators;
% Up to now we did not decided on the chirality assumption
% so let us check first the tradicional algebra os susy derivative;
let trad;

%first susy derivative
der(1)*fer(f,1,2)*bos(g,3,1)*axp(bos(h,0,0));

fer(g,2,1)*bos(f,0,2,-2)*axp(fer(k,1,2)*fer(h,2,1))*del(1);
sub(del=der,ws);

%second susy derivative
der(2)*fer(g,2,3)*bos(kk,0,3)*axp(bos(f,3,0));

fer(r,2,1)*bos(kk,3,4,-4)*axp(fer(f,1,2)*fer(g,2,1))*del(2);
sub(del=der,ws);

%usual derivative;
d(1)*fer(f,1,2)*bos(g,3,1)*axp(bos(h,0,0));
fer(g,2,1)*bos(f,0,2,-2)*axp(fer(h,1,2)*fer(k,2,1))*d(2);
sub(d(2)=d(1),ws);

% 7.) the value of action of (susy) derivative;

xxx:=fer(f,1,2)*bos(k,0,2,-2)*axp(fer(h,2,0)*fer(aa,1,3));
yyy:=fer(g,2,3)*bos(kk,3,1,-3)*axp(bos(f,0,2,-3));

%first susy derivative

pr(1,xxx);
pr(1,yyy);

%second susy2 derivative;
pr(2,xxx);
pr(2,yyy);

% third susy2 derivative;

pr(3,xxx);
pr(3,yyy);

clearrules trad;
let chiral;
pr(3,xxx);
clearrules chiral;
let chiral1;
pr(3,xxx);

clearrules chiral1;
let trad;
% usual derivative
pg(1,xxx);
pg(3,yyy);

clear xxx,yyy;

% 8.)
% And now let us change traditional algebra on the chiral algebra;
clearrules trad;
let chiral;
% And now we compute the same derivative but in the chiral
% representation;

%first susy derivative
der(1)*fer(f,1,2)*bos(g,3,1)*axp(bos(h,0,0));

fer(g,2,1)*bos(f,0,2,-2)*axp(fer(k,1,2)*fer(h,2,1))*del(1);
sub(del=der,ws);

%second susy derivative
der(2)*fer(g,2,3)*bos(kk,0,3)*axp(bos(f,3,0));

fer(r,2,1)*bos(kk,3,4,-4)*axp(fer(f,1,2)*fer(g,2,1))*del(2);
sub(del=der,ws);
;

% 9.) the value of action of (susy) derivative;

xxx:=fer(f,1,2)*bos(k,0,2,-2)*axp(fer(h,2,0)*fer(aa,1,3));
yyy:=fer(g,2,3)*bos(kk,3,1,-3)*axp(bos(f,0,2,-3));

%first susy derivative

pr(1,xxx);
pr(1,yyy);

%second susy2 derivative;
pr(2,xxx);
pr(2,yyy);

clear xxx,yyy;
% We return back to the traditional algebra;

clearrules chiral;
let trad;

% 10.) The components of super-objects;

xxx:=fer(f,2,3)*bos(g,3,2,2);

% all components;
fpart(xxx);

%bosonic sector;
bpart(xxx);

%the given component
bf_part(xxx,0);

%the given component in the bosonic sector;

b_part(xxx,0);
b_part(xxx,1);



clear zzz;
clearrules trad;
let chiral;
zzz:=bos(f,3,1,-1)*bos(g,0,1,2);
b_part(zzz,0);
b_part(zzz,3);
clearrules chiral;
let chiral1;
b_part(zzz,0);
b_part(zzz,3);
clearrules chiral1;
let trad;

%11 matrix represenattion of operators;
lax:=der(1)*der(2)+bos(u,0,0);

macierz(lax,b,b);
macierz(lax,f,b);
macierz(lax,b,f);
macierz(lax,f,f);

% 12.) Demonstration of chirality properties;
clearrules trad;
let chiral;
b_chiral:={f0};
b_antychiral:={f1};
f_chiral:={f2};
f_antychiral:={f3};
for k:=0:3 do  write fer(f0,k,0);
for k:=0:3 do  write fer(f1,k,0);
for k:=0:3 do  write fer(f2,k,0);
for k:=0:3 do  write fer(f3,k,0);
for k:=0:3 do  write bos(f1,k,0);
for k:=0:3 do  write bos(f2,k,0);
for k:=0:3 do  write bos(f2,k,0);
for k:=0:3 do  write bos(f3,k,0);

% 13.) Integrations;

d(-1)*xxx;

%we have to declare ww;
ww:=2;

d(-1)*xxx;
xxx*d(-2);
d(-3)*xxx;

ww:=4;
d(-1)**5:=0;d(-2)**5:=0;

d(-1)*yyy;
yyy*d(-2);

clear d(-1)**5,d(-2)**5;


on list;

% 14.) The accelerations of integrations;

clear ww;
ww:=3;
let drr;
let cutoff;
cut:=4;
d(-1)*xxx;
d(-1)**2*yyy;
clear ww,cut;
ww:=4;
cut:=5;
d(-1)**3*yyy;
d(-1)*xxx;

clearrules cutoff;clearrules drr;
clear cut,ww;

% it is possible to use directly accelerated integrations oprators dr;
ww:=4;
dr(-2)*fer(f,1,2)*bos(kk,0,2);

on time;
showtime;

dr(-3)*bos(g,3,1)*bos(ff,3,2);

showtime;
%if you try usual integration

d(-1)**3*bos(g,3,1)*bos(ff,3,2);

showtime;

% then the time - diffrences is evident. In this example d(-1)
% integration is 10 times slower then  dr integrations.

off time;

let cutoff;
cut:=5;
dr(-2)*fer(f,1,2)*bos(aa,0,1);
dr(-3)*bos(g,3,1)*bos(bb,0,3);
clear ww,cut;
ww:=6;
cut:=7;
dr(-3)*fer(k,2,3)*bos(h,0,2);
dr(-4)*bos(h,0,3)*bos(k,0,2);

clear ww,cut;
clearrules cutoff;

% 15.) The combinations

%the combinations of dim 7 constructed from fields of
% the 2 ,3 dimensions, free  parameters are numerated by "a";

w_comb({{f,2,b},{g,3,b}},7,a,b);
w_comb({{f,2,f},{g,3,f}},4,s,f);

% and now compute the last example but withouth the (susy)divergence
%terms;

fcomb({{f,2,b},{g,3,b}},5,c,b);
fcomb({{f,1,f}},4,r,f);


% 16.) The element of pseudo - susy -differential algebra;

pse_ele(2,{{f,2,b}},c);
pse_ele(3,{{f,2,b}},c);
pse_ele(4,{{f,2,b}},c);
pse_ele(3,{{f,1,b},{g,2,b}},r);

% The components of the elements of pseudo - susy - differential algebra;

xxx:=pse_ele(2,{{f,1,b},{g,2,b}},r);

for k:=0:3 do write s_part(xxx,k);

for k:=0:2 do write d_part(xxx,k);

for k:=0:2 do for l:=0:3 do write sd_part(xxx,l,k);

clear xxx;

% 17.) Projection onto invariant subspace;

xxx:=
w_comb({{f,1,b}},2,a,b)*d(1)+
w_comb({{f,1,b}},3,b,b)*der(1)*der(2)+
w_comb({{f,1,b}},5/2,c,b)*der(1)+
w_comb({{f,1,b}},3,ee,b)*d(1)^2+
w_comb({{f,1,b}},7/2,fe,b)*d(1)*der(2)+
w_comb({{f,1,b}},3,g,b)*der(1)*der(2)*d(1);

for k:=0:2 do write rzut(xxx,k);

clear xxx;

% 18.) Test for the adjoint operators;

cp(der(1));
cp(der(1)*der(2));
clearrules trad;
let chiral1;
cp(der(3));
cp(der(1)*d(1));
clearrules chiral1;
let trad;
cp(d(1));
cp(d(2));
as:=fer(f,1,0)*d(-3)*fer(g,2,0)+fer(h,1,2)*d(-3)*fer(kk,2,1);
cp(as);
cp(as*as);

as:=fer(f,1,0);
cp(as);
cp(ws);

clear as;

as:=bos(f,0,0);
as1:=as*der(1);
cp(as1);
cp(ws);
cp(as1)+der(1)*as;

as2:=as*der(1)*der(2);
cp(as2);
cp(ws);
cp(as2) - der(1)*der(2)*as;


clear as;
as:=mat((fer(f,1,0)*der(1),bos(g,0,0)*d(-3)*bos(h,0,0)),
(fer(h,2,1),fer(h,1,2)*d(-3)*fer(k,2,3)));
cp(as);
clear as;


% 19.) Analog of coeff

xxx:=pse_ele(2,{{f,1,b}},a);
yyy:=lyst(xxx);
zzz:=lyst1(xxx);
yyy:=lyst2(xxx);
clear xxx,yyy,zzz;

% 20.) Simplifications;

% we would like to compute third generalizations of the SUSY KdV
% equation
% example from Z.Popowicz Phys.Lett.A.174 (1993) p.87

lax:=d(1)+d(-3)*der(1)*der(2)*bos(u,0,0);
lb2:=lax^2;
la2:=chan(lb2);
lb3:=lax*la2;
la3:=chan(lb3);
lax3:=rzut(la3,1);
comm:=lax*lax3 - lax3*lax;
com:=chan(comm);
result:=sub(der=del,com);
%the equation is
equ:=sub(del(1)=1,del(2)=1,d(-3)=1,result);

clear lax,lb2,la2,lb3,la3,lax3,comm,com,result;

% we now compute the same but starting from
% different realizations of susy algebra
%
clearrules trad;
let chiral1;
lax:=d(1)+d(-3)*del(3)*bos(u,0,0);
la2:=chan(lax^2);
la3:=rzut(chan(lax*la2),0);
com:=chan(lax*la3-la3*lax);
equ_chiral1:=sub(d(-3)=1,del(3)=1,com);
clear lax,lb2,la2,lb3,la3,lax3,lax,comm,com,result;
clearrules chiral1;
let trad;

% 21.) Conservation laws;
% we would like to check the conservations laws for our third
%generalization of susy kdv equation;
%

ham:=fcomb({{u,1,b}},3,a,b);

conserv:=dot_ham({{u,equ}},ham);
% we check now on susy-divergence behaviour;
%
az:=war(conserv,u);
solve(az);
clear equ,ha,conserv,az;

% 22.) The residue of Lax operator
% we would like to find conservation laws for Lax susy KdV
% equation considered in the previous example
%
lax:=d(1)-d(-3)*del(1)*der(2)*bos(u,0,0);
lb2:=lax^2;
la2:=chan(lb2);
lb4:=la2^2;
kxk^3:=0;
la4:=chan(lb4);
lc4:=sub(kxk=1,qq=-3,sub(d(-3)=kxk*d(qq),la4));
lb5:=lax*lc4;
lc5:=s_part(lb5,3);
la5:=lc5-sub(d(-3)=0,lc5);
ld5:=chan(la5);
konserv:=sub(d(-3)=1,d_part(ld5,-1));
clear lax,lb2,la2,lb4,kxk,la4,lc4,lb5,lc5,la5,konserv;

%22.) The N=2 SuSy Boussinesq equation
% example from Z.Popowicz Phys.LettB.319 (1993) 478-484

clearrules trad;
let chiral;

lax:=del(1)*(d(1)^2+bos(j,0,0)*d(1)+bos(tt,0,0))*der(2);
la2:=del(1)*(d(1)+2*bos(j,0,0)/3)*der(2);
com:=sub(del(1)=1,der(2)=1,lax*la2-la2*lax);
operator boss;
boss(j,t):=d_part(com,1);
boss(tt,t):=d_part(com,0);

% let us shift bos(tt,0,0) to

bos(tt,0,0):=bos(tx,0,0)/2+bos(j,0,0)**2/6 + bos(j,0,1)/2;
bos(tt,0,1):=pg(1,bos(tt,0,0));
bos(tt,0,2):=pg(1,bos(tt,0,1));
fer(tt,1,0):=pr(1,bos(tt,0,0));
fer(tt,2,0):=pr(2,bos(tt,0,0));

% then the equations of motion are;

bos(j,t):=boss(j,t);
bos(tx,t):=2*(boss(tt,t) - boss(j,t)*bos(j,0,0)/3-
            pg(1,boss(j,t))/2);

clear lax,la2;
clearrules chiral;
let trad;

%23.) the Jacobi identity;
% we will find the N=2 susy extension of the Virasoro algebra.
% First we found the most general form of the susy-pseudo-differential
% element of the dimension two.

vira:=pse_ele(2,{{f,1,b}},a);

% This vira should be antisymmetrical so we found

ewa:=vira+cp(vira);

%we first solve ewa in order to found free coefficients;

load_package groebner;
adam:=groesolve(sub(der(1)=1,der(2)=1,d(1)=1,lyst1(ewa)));

% we define now the most general antisymmetrical susy-pseudo-symmetrical
% element of conformal dimension two.

vira:=sub(adam,vira);

% we make additional assumption that our Poisson tensor vira should be O(2)
% invariant under the change of susy derivatives;

dad:=odwa(vira)-vira;
factor der;
wyr1:=sub(der(1)=1,der(2)=1,lyst1(dad));
remfac der;
dad:=groesolve(wyr1);
vira:=sub(dad,vira);
% we check wheather it is really O(2) invariant;
vira-odwa(vira);
% O.K
%so
%now we check the Jacobi identity

jjacob:=fjacob(vira,f);

% we now check jjacob on the susy-divergence behaviour w.r. to the test
% superfunction !#a;

az:=war(jjacob,!#a);
as:=groesolve(az);
array ew(3);

for k:=1:2 do ew(k):=part(as,k);

% as we see we have two different solutions
% first give us classical realizations of the Virasoro algebra
% (without the center term)  which is

sub(ew(1),vira);


% the second solution give us desired susy generalizations of
% Virasoro algebra

sub(ew(2),vira);

% the coefficient "a" could be absorbed by redefinations of
% bos(f,0,0)
% we check that previous result satisfies the antisymmetric requirements

ws + cp(ws);

clearrules trad;
let chiral1 ;

% We check that for chiral1 realization the following operator
vira:=der(3)*d(1)+bos(j,0,1)+bos(j,0,0)*d(1)+
        fer(j,1,0)*der(2)+fer(j,2,0)*der(1);
% satisfy the Jacobi identity;
jjacob:=fjacob(vira,j);

az:=war(jjacob,!#a);

%24 superintegration
clearrules chiral1;
let trad;


as:=s_int(0,bos(f,3,0)^2-bos(f,0,1)^2,{f});
as1:=sub(d(-3)=0,ws);
as2:=sub(d(-3)=1,as-as1);
as3:=s_int(1,as2,{f});
as4:=sub(del(-1)=0,ws);
as4:=sub(del(-1)=1,as3-as4);
as5:=s_int(2,as4,{f});

end;
