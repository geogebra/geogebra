% Test of Assist Package version 2.31.
% DATE : 30 August 1996
% Author: H. Caprasse <hubert.caprasse@ulg.ac.be>
%load_package assist$

Comment 2. HELP for ASSIST:;
;
assist();
;
assisthelp(7);
;
Comment 3. CONTROL OF SWITCHES:;
;
switches;
off exp; on gcd; off precise;
switches;
switchorg;
switches;
;
if !*mcd then "the switch mcd is on";
if !*gcd then "the switch gcd is on";
;
Comment 4. MANIPULATION OF THE LIST STRUCTURE:;
;
t1:=mklist(5);

Comment   MKLIST does NEVER destroy anything ;

mklist(t1,10);
mklist(t1,3);
;
sequences 3;
lisp;
sequences 3;
algebraic;
;
for i:=1:5 do t1:= (t1.i:=mkid(a,i));
t1;
;
t1.5;
;
t1:=(t1.3).t1;
;
% Notice the blank spaces ! in the following illustration: 
1 . t1;
;
% Splitting of a list:
split(t1,{1,2,3}); 
;
% It truncates the list :
split(t1,{3});
;
% A KERNEL may be coerced to a list:
kernlist sin x;
;
% algnlist constructs a list which contains n-times a given list 
algnlist(t1,2); 
;
% Delete :

delete(x, {a,b,x,f,x});
;
% delete_all eliminates ALL occurences of x: 
delete_all(x,{a,b,x,f,x});
;
remove(t1,4);
;
% delpair deletes a pair if it is possible.
delpair(a1,pair(t1,t1));
; 
elmult(a1,t1);
;
frequency append(t1,t1);
;
insert(a1,t1,3);
;
li:=list(1,2,5);
;
%  Not to destroy an already ordered list during insertion:
insert_keep_order(4,li,lessp);
insert_keep_order(bb,t1,ordp);
;
% the same function when appending two correctly ORDERED lists:
merge_list(li,li,<);
;
merge_list({5,2,1},{5,2,1},geq);
;
depth list t1;
;
depth a1;
% Any list can be flattened into a list of depth 1:
mkdepth_one {1,{{a,b,c}},{c,{{d,e}}}};
position(a2,t1);
appendn(li,li,li);
;
clear t1,li;
comment 5. THE BAG STRUCTURE AND OTHER FUNCTION FOR LISTS AND BAGS.
 ;
aa:=bag(x,1,"A");
putbag bg1,bg2;
on errcont;
putbag list;
off errcont;
aa:=bg1(x,y**2);
;
if bagp aa then "this is a bag";
;
% A bag is a composite object:
clearbag bg2;
;
depth bg2(x);
;
depth bg1(x);
;
if baglistp aa then "this is a bag or list";
if baglistp {x} then "this is a bag or list";
if bagp {x} then "this is a bag";
if bagp aa then "this is a bag";
;
ab:=bag(x1,x2,x3);
al:=list(y1,y2,y3);
% The basic lisp functions are also active for bags:
first ab;  third ab;  first al;
last ab; last al;
belast ab; belast al; belast {a,b,a,b,a};
rest ab; rest al;

;
% The "dot" plays the role of the function "part":
ab.1; al.3;
on errcont;
ab.4;
off errcont;
a.ab;
% ... but notice
1 . ab;
% Coercion from bag to list and list to bag:
kernlist(aa);
;
listbag(list x,bg1);
;
length ab;
;
remove(ab,3);
;
delete(y2,al);
;
reverse al;
;
member(x3,ab);
;
al:=list(x**2,x**2,y1,y2,y3);
;
elmult(x**2,al);
;
position(y3,al);
;
repfirst(xx,al);
;
represt(xx,ab);
;
insert(x,al,3);
insert( b,ab,2);
insert(ab,ab,1);
;
substitute (new,y1,al);
;
appendn(ab,ab,ab);
;
append(ab,al);
append(al,ab);
clear ab; a1;
;comment Association list or bag may be constructed and thoroughly used;
;
l:=list(a1,a2,a3,a4);
b:=bg1(x1,x2,x3);
al:=pair(list(1,2,3,4),l);
ab:=pair(bg1(1,2,3),b);
;
clear b;
comment : A BOOLEAN function abaglistp to test if it is an association;
;
if abaglistp bag(bag(1,2)) then "it is an associated bag";
;
% Values associated to the keys can be extracted
% first occurence ONLY.
;
asfirst(1,al);
asfirst(3,ab);
;
assecond(a1,al);
assecond(x3,ab);
;
aslast(z,list(list(x1,x2,x3),list(y1,y2,z)));
asrest(list(x2,x3),list(list(x1,x2,x3),list(y1,y2,z)));
;
clear a1;
;
% All occurences.
asflist(x,bg1(bg1(x,a1,a2),bg1(x,b1,b2)));
asslist(a1,list(list(x,a1),list(y,a1),list(x,y)));
restaslist(bag(a1,x),bg1(bag(x,a1,a2),bag(a1,x,b2),bag(x,y,z)));
restaslist(list(a1,x),bag(bag(x,a1,a2),bag(a1,x,b2),bag(x,y,z)));
;
Comment 6. SETS AND THEIR MANIPULATION FUNCTIONS
;
ts:=mkset list(a1,a1,a,2,2);
if setp ts then "this is a SET";
;
union(ts,ts);
;
diffset(ts,list(a1,a));
diffset(list(a1,a),ts);
;
symdiff(ts,ts);
;
intersect(listbag(ts,set1),listbag(ts,set2));


Comment 7. GENERAL PURPOSE UTILITY FUNCTIONS :;
;
clear a1,a2,a3,a,x,y,z,x1,x2,op$
;
% DETECTION OF A GIVEN VARIABLE IN A GIVEN SET
;
mkidnew();
mkidnew(a);
;
dellastdigit 23;
;
detidnum aa;
detidnum a10;
detidnum a1b2z34;
;
list_to_ids list(a,1,rr,22);
;
if oddp 3 then "this is an odd integer";
;
<<prin2 1; followline 7; prin2 8;>>;
;
operator foo;
foo(x):=x;
foo(x)==value;

x; 	% it is equal to value

clear x;
;
randomlist(10,20);
% Generation of tables of random numbers:
% One dimensional:
mkrandtabl({4},10,ar);
array_to_list ar;
;
% Two dimensional:
mkrandtabl({3,4},10,ar);
array_to_list ar; 
;
% With a base which is a decimal number:
on rounded;
mkrandtabl({5},3.5,ar);
array_to_list ar;
off rounded;
;
% Combinatorial functions :
permutations(bag(a1,a2,a3));
permutations {1,2,3};
;
cyclicpermlist{1,2,3};
;
combnum(8,3);
;
combinations({1,2,3},2);
;
perm_to_num({3,2,1,4},{1,2,3,4});
num_to_perm(5,{1,2,3,4});
;
operator op;
symmetric op;
op(x,y)-op(y,x);
remsym op;
op(x,y)-op(y,x);
;
labc:={a,b,c};
symmetrize(labc,foo,cyclicpermlist);
symmetrize(labc,list,permutations);
symmetrize({labc},foo,cyclicpermlist);
;
extremum({1,2,3},lessp);
extremum({1,2,3},geq);
extremum({a,b,c},nordp);
;
funcvar(x+y);
funcvar(sin log(x+y));
funcvar(sin pi);
funcvar(x+e+i);
funcvar sin(x+i*y);
;
operator op;
noncom op;
op(0)*op(x)-op(x)*op(0);
remnoncom op;
op(0)*op(x)-op(x)*op(0);
clear op;
;
depatom a;
depend a,x,y;
depatom a;
;
depend op,x,y,z;
;
implicit op;
explicit op;
depend y,zz;
explicit op;
aa:=implicit op;
clear op;
;
korder x,z,y;
korderlist;
;
if checkproplist({1,2,3},fixp) then "it is a list of integers";
;
if checkproplist({a,b1,c},idp) then "it is a list of identifiers";
;
if checkproplist({1,b1,c},idp) then "it is a list of identifiers";
;
lmix:={1,1/2,a,"st"};
;
extractlist(lmix,fixp);
extractlist(lmix,numberp);
extractlist(lmix,idp);
extractlist(lmix,stringp);
;
% From a list to an array:
list_to_array({a,b,c,d},1,ar);
array_to_list ar;  
list_to_array({{a},{b},{c},{d}},2,ar);
;
comment 8. PROPERTIES AND FLAGS:;
;
putflag(list(a1,a2),fl1,t);
putflag(list(a1,a2),fl2,t);
displayflag a1;
;
clearflag a1,a2;
displayflag a2;
putprop(x1,propname,value,t);
displayprop(x1,prop);
displayprop(x1,propname);
;
putprop(x1,propname,value,0);
displayprop(x1,propname);
;
Comment 9. CONTROL FUNCTIONS:;
;
alatomp z;
z:=s1;
alatomp z;
;
alkernp z;
alkernp log sin r;
;
precp(difference,plus);
precp(plus,difference);
precp(times,.);
precp(.,times);
;
if stringp x then "this is a string";
if stringp "this is a string" then "this is a string";
;
if nordp(b,a) then "a is ordered before b";
operator op;
for all x,y such that nordp(x,y) let op(x,y)=x+y;
op(a,a);
op(b,a);
op(a,b);
clear op;
;
depvarp(log(sin(x+cos(1/acos rr))),rr);
;
clear y,x,u,v;
clear op;
;
% DISPLAY and CLEARING of user's objects of various types entered
% to the console. Only TOP LEVEL assignments are considered up to now.
% The following statements must be made INTERACTIVELY. We put them
% as COMMENTS for the user to experiment with them. We do this because
% in a fresh environment all outputs are nil.
;
% THIS PART OF THE TEST SHOULD BE REALIZED INTERACTIVELY.
% SEE THE ** ASSIST LOG **  FILE .
%v1:=v2:=1;
%show scalars;
%aa:=list(a);
%show lists;
%array ar(2);
%show arrays;
%load matr$
%matrix mm;
%show matrices;
%x**2;
%saveas res;
%show saveids;
%suppress scalars;
%show scalars;
%show lists;
%suppress all;
%show arrays;
%show matrices;
;
comment end of the interactive part;
;
clear op;
operator op;
op(x,y,z);
clearop op;
;
clearfunctions abs,tan;
;
comment  THIS FUNCTION MUST BE USED WITH CARE !!!!!;
;
Comment 10. HANDLING OF POLYNOMIALS

clear x,y,z;
COMMENT  To see the internal representation :;
;
off pri;
;
pol:=(x-2*y+3*z**2-1)**3;
;
pold:=distribute pol;
;
on distribute;
leadterm (pold);
pold:=redexpr pold;
leadterm pold;
;
off distribute;
polp:=pol$
leadterm polp;
polp:=redexpr polp;
leadterm polp;
;
monom polp;
;
on pri;
;
splitterms polp;
;
splitplusminus polp;
;
divpol(pol,x+2*y+3*z**2);
;
lowestdeg(pol,y);
;
Comment 11.  HANDLING OF SOME TRANSCENDENTAL FUNCTIONS:;
;
trig:=((sin x)**2+(cos x)**2)**4;
trigreduce trig;
trig:=sin (5x);
trigexpand trig;
trigreduce ws;
trigexpand sin(x+y+z);
;
;
hypreduce (sinh x **2 -cosh x **2);
;
;
clear a,b,c,d;
;

Comment 13. HANDLING OF N-DIMENSIONAL VECTORS:;
;
clear u1,u2,v1,v2,v3,v4,w3,w4;
u1:=list(v1,v2,v3,v4);
u2:=bag(w1,w2,w3,w4);
%
sumvect(u1,u2);
minvect(u2,u1);
scalvect(u1,u2);
crossvect(rest u1,rest u2);
mpvect(rest u1,rest u2, minvect(rest u1,rest u2));
scalvect(crossvect(rest u1,rest u2),minvect(rest u1,rest u2));
;
Comment 14. HANDLING OF GRASSMANN OPERATORS:;
;
putgrass eta,eta1;
grasskernel:=
{eta(~x)*eta(~y) => -eta y * eta x when nordp(x,y),
(~x)*(~x) => 0 when grassp x};
;
eta(y)*eta(x);
eta(y)*eta(x) where grasskernel;
let grasskernel;
eta(x)^2;
eta(y)*eta(x);
operator zz;
grassparity (eta(x)*zz(y));
grassparity (eta(x)*eta(y));
grassparity(eta(x)+zz(y));
clearrules grasskernel;
grasskernel:=
{eta(~x)*eta(~y) => -eta y * eta x when nordp(x,y),
eta1(~x)*eta(~y) => -eta x * eta1 y,
eta1(~x)*eta1(~y) => -eta1 y * eta1 x when nordp(x,y),
(~x)*(~x) => 0 when grassp x};
;
let grasskernel;
eta1(x)*eta(x)*eta1(z)*eta1(w);
clearrules grasskernel;
remgrass eta,eta1;
clearop zz;
;
Comment  15. HANDLING OF MATRICES:;
;
clear m,mm,b,b1,bb,cc,a,b,c,d,a1,a2;
load_package matrix;
baglmat(bag(bag(a1,a2)),m);
m;
on errcont;
;
baglmat(bag(bag(a1),bag(a2)),m);
off errcont;
%    **** i.e. it cannot redefine the matrix! in order
%         to avoid accidental redefinition of an already given matrix;

clear m; baglmat(bag(bag(a1),bag(a2)),m);
m;
on errcont;
baglmat(bag(bag(a1),bag(a2)),bag);
off errcont;
comment  Right since a bag-like object cannot become a matrix.;
;
coercemat(m,op);
coercemat(m,list);
;
on nero;
unitmat b1(2);
matrix b(2,2);
b:=mat((r1,r2),(s1,s2));
b1;b;
mkidm(b,1);
;
seteltmat(b,newelt,2,2);
geteltmat(b,2,1);
%
b:=matsubr(b,bag(1,2),2);
;
submat(b,1,2);
;
bb:=mat((1+i,-i),(-1+i,-i));
cc:=matsubc(bb,bag(1,2),2);
;
cc:=tp matsubc(bb,bag(1,2),2);
matextr(bb, bag,1);
;
matextc(bb,list,2);
;
hconcmat(bb,cc);
vconcmat(bb,cc);
;
tpmat(bb,bb);
bb tpmat bb;
;
clear hbb;
hermat(bb,hbb);
% id hbb changed to a matrix id and assigned to the hermitian matrix
% of bb.
;
load_package HEPHYS;

% Use of remvector.
;
vector v1,v2;
v1.v2;
remvector v1,v2;
on errcont;
v1.v2;
off errcont;
% To see the compatibility with ASSIST:
v1.{v2};
;
index u; vector v;
(v.u)^2;
remindex u;
(v.u)^2;
;
% Gamma matrices properties may be translated to any identifier:
clear l,v;
vector v;
g(l,v,v);
mkgam(op,t);
op(l,v,v);
mkgam(g,0);
operator g;
g(l,v,v);
;
clear g,op;
;
% showtime;
end;
