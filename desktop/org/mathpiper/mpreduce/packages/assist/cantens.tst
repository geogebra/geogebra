% Test of CANTENS.RED
%
%     Authors: H. Caprasse <hubert.caprasse@ulg.ac.be>
%
% Version and Date:  Version 1.1, 15 September 1998.
%----------------------------------------------------------------
off errcont;
% Default : 
onespace ?;
wholespace_dim ?;  
global_sign ? ; 
signature ?; 
% answers to the 4 previous commands: yes, dim, 1, 0
wholespace_dim 4; 
signature 1;
global_sign(-1);
% answers to the three previous commands: 4, 1, (-1) 
% answer to the command below: {}
show_spaces();
% Several spaces:
off onespace;
onespace ?; 
% answer: no
show_spaces();
define_spaces wholespace={6,signature=1,indexrange=0 .. 5};
% indexrange command is superfluous since 'wholespace':
show_spaces();
rem_spaces wholespace;
define_spaces wholespace={11,signature=1}; 
define_spaces mink={4,signature=1,indexrange=0 .. 3}; 
define_spaces eucl={6,euclidian,indexrange=4 .. 9}; 
show_spaces();
%
% if input error or modifications necessary:
%
define_spaces eucl={7,euclidian,indexrange=4 .. 10};
%
% do:
%
rem_spaces eucl;
define_spaces eucl={7,euclidian,indexrange=4 .. 10};
show_spaces();
% done
%
define_spaces eucl1={1,euclidian,indexrange=11 .. 11}; 
show_spaces();
rem_spaces wholespace,mink,eucl,eucl1;
show_spaces();
%
% Indices can be made to belong to a subspace or replaced 
% in the whole space:
define_spaces eucl={3,euclidean};
show_spaces();
mk_ids_belong_space({a1,a2},eucl);
% a1,a2 belong to the subspace eucl.
mk_ids_belong_anyspace a1,a2;
% replaced in the whole space.
rem_spaces eucl;
%%  
%% GENERIC TENSORS:
on onespace;
wholespace_dim dim;
tensor te; 
te(3,a,-4,b,-c,7); 
te(3,a,{x,y},-4,b,-c,7);
te(3,a,-4,b,{u,v},-c,7);
te({x,y}); 
make_variables x,y;
te(x,y);
te(x,y,a); 
remove_variables x;
te(x,y,a);
remove_variables y;
%
% implicit dependence:
%
operator op2;
depend op1,op2(x);
df(op1,op2(x));
% the next response is 0:
df(op1,op2(y));
clear op2;
% case of a tensor: 
operator op1;
depend te,op1(x); 
df(te(a,-b),op1(x));
% next the outcome is 0:
df(te(a,-b),op1(y));
%
tensor x;
depend te,x;
% outcome is NOT 0:
df(te(a,-b),x(c));
%
% Substitutions:
sub(a=-c,te(a,b));
sub(a=-1,te(a,b)); 
% the following operation is wrong:
sub(a=-0,te(a,b));  
% should be made as following to be correct:
sub(a=-!0,te(a,b)); 
% dummy indices recognition
dummy_indices();
te(a,b,-c,-a);
dummy_indices();
te(a,b,-c,-a);
dummy_indices();
% hereunder an error message correctly occurs:
on errcont;
te(a,b,-c,a);
off errcont;
sub(c=b,te(a,b,-c,-a));
dummy_indices();
% dummy indices suppression:
on errcont;
te(d,-d,d);
off errcont;
dummy_indices();
rem_dummy_indices d;
te(d,d);
dummy_indices();
rem_dummy_indices a,b;
onespace ?;
% case of space of integer dimension:
wholespace_dim 4;
signature 0;
% 7 out of range
on errcont;
te(3,a,-b,7);
off errcont;
te(3,a,-b,3);
te(4,a,-b,4);
% an 'out-of-range' error is issued:
on errcont;
sub(a=5,te(3,a,-b,3));
off errcont;
signature 1;
% now indices should run from 0 to 3 => error: 
on errcont;
te(4,a,-b,4);
off errcont;
% correct:
te(0,a,-b,3);
%
off onespace;
define_spaces wholespace={4,euclidean};
% We MUST say that te BELONG TO A SPACE, here to wholespace:
make_tensor_belong_space(te,wholespace);
on errcont;
te(a,5,-b);
off errcont;
te(a,4,-b);
rem_spaces wholespace;
define_spaces wholespace={5,signature=1};
define_spaces eucl={1,signature=0};
show_spaces();
make_tensor_belong_space(te,eucl);
te(1);
% hereunder, an error message is issued: 
on errcont;
te(2);
off errcont;
% hereunder, an error message should be issued, it is not 
% because no indexrange has been declared:
te(0);
rem_spaces eucl;
define_spaces eucl={1,signature=0,indexrange=1 .. 1};
% NOW an error message is issued:
on errcont;
te(0);
off errcont;
te(1);
% again an error message:
on errcont;
te(2);
off errcont;
%
rem_dummy_indices a,b,c,d;
% symmetry properties:
%
symmetric te;
te(a,-b,c,d);
remsym te;
antisymmetric te;
te(a,b,-c,d);
remsym te;
% mixed symmetries:
tensor r;
% 
symtree(r,{!+,{!-,1,2},{!-,3,4}});
ra:=r(b,a,c,d)$ 
canonical ra; 
ra:=r(c,d,a,b)$
canonical ra;
% here canonical is short-cutted
ra:=r(b,b,c,a);
%
% symmetrization:
on onespace;
symmetrize(r(a,b,c,d),r,permutations,perm_sign);
canonical ws;
off onespace;
symmetrize({a,b,c,d},r,cyclicpermlist);
canonical ws;
rem_tensor r;
% Declared bloc-diagonal tensor:
rem_spaces wholespace,eucl;
define_spaces wholespace={7,signature=1};
define_spaces mink={4,signature=1,indexrange=0 .. 3};
define_spaces eucl={3,euclidian,indexrange=4 .. 6};
show_spaces();
make_tensor_belong_space(te,eucl);
make_bloc_diagonal te;
mk_ids_belong_space({a,b,c},eucl);
te(a,b,z);
mk_ids_belong_space({m1,m2},mink);
te(a,b,m1);
te(a,b,m2);
mk_ids_belong_anyspace a,b,c,m1,m2;
te(a,b,m2);
% how to ASSIGN a particular component ?
% take the simplest context: 
rem_spaces wholespace,mink,eucl;
on onespace;
te({x,y},a,-0)==x*y*te(a,-0); 
te({x,y},a,-0); 
te({x,y},a,0); 
% hereunder an error message  is issued because already assigned:
on errcont;
te({x,y},a,-0)==x*y*te(a,-0);
off errcont;
% clear value:
rem_value_tens te({x,y},a,-0);
te({x,y},a,-0); 
te({x,y},a,-0)==(x+y)*te(a,-0);
% A small illustration
te(1)==sin th * cos phi; 
te(-1)==sin th * cos phi;
te(2)==sin th * sin phi;
te(-2)==sin th * sin phi;
te(3)==cos th ;
te(-3)==cos th ;
for i:=1:3 sum te(i)*te(-i);
rem_value_tens te;
te(2);
let te({x,y},-0)=x*y;
te({x,y},-0);
te({x,y},0);
te({x,u},-0);
for all x,a let te({x},a,-b)=x*te(a,-b);
te({u},1,-b);
te({u},c,-b);
te({u},b,-b);
te({u},a,-a);
for all x,a clear te({x},a,-b);
te({u},c,-b);
% rule for indices only
for all a,b let te({x},a,-b)=x*te(a,-b);
te({x},c,-b);
te({x},a,-a);
% A BUG still exists for -0 i.e. rule does NOT apply:
te({x},a,-0);
% the cure is to use -!0 in this case
te({x},0,-!0);
%
% local rules: 
%
rul:={te(~a) => sin a};
te(1) where rul;
% 
rul1:={te(~a,{~x,~y}) => x*y*sin(a)}; 
%
te(a,{x,y}) where rul1;
te({x,y},a) where rul1;
%
rul2:={te(-~a,{~x,~y}) => x*y*sin(-a)};
% 
te(-a,{x,y}) where rul2; 
te({x,y},-a) where rul2;
%% CANONICAL 
%
% 1. Coherence of tensorial indices.
%
tensor te,tf; 
dummy_indices();
make_tensor_belong_anyspace te;
on errcont;
bb:=te(a,b)*te(-b)*te(b);
% hereunder an error message is issued:
canonical bb;
off errcont;
bb:=te(a,b)*te(-b);
% notice how it is rewritten by canonical:
canonical bb;
% 
dummy_indices();
aa:=te(d,-c)*tf(d,-c);
% if a and c are FREE no error message:
canonical aa;
% do NOT introduce powers for NON-INVARIANT tensors:
aa:=te(d,-c)*te(d,-c);
% Powers are taken away
canonical aa;
% A trace CANNOT be squared because powers are removed by 'canonical':
cc:=te(a,-a)^2$
canonical cc;
%
% Correct writing of the previous squared:
cc:=te(a,-a)*te(b,-b)$
canonical cc;
% all terms must have the same variance:
on errcont;
aa:=te(a,c)+x^2;
canonical aa;
aa:=te(a,b)+tf(a,c);
canonical aa;
off errcont;
dummy_indices();
rem_dummy_indices a,b,c;
dummy_indices();
% a dummy VARIABLE is NOT a dummy INDEX
dummy_names b;
dummy_indices();
% so, no error message in the following: 
canonical(te(b,c)*tf(b,c));
% it is an incorrect input for a variable.
% correct input is:
canonical(te({b},c)*tf({b},c));
clear_dummy_names;
% contravariant indices are placed before covariant ones if possible.
% i.e. Riemanian spaces by default: 
pp:=te(a,-a)+te(-a,a)+1; 
canonical pp;
pp:=te(a,-c)+te(-b,b,a,-c);
canonical pp;
pp:=te(r,a,-f,d,-a,f)+te(r,-b,-c,d,b,c);
canonical pp;
% here, a case where a normal form cannot be obtained:
tensor nt;
a1:=nt(-a,d)*nt(-c,a);
a2:=nt(-c,-a)*nt(a,d); 
% obviously, a1-a2 =0, but ....
canonical(a1-a2);
% does give the same expression with the sign changed.
% zero is either:
canonical a1 -a2;
% or
a1 -canonical a2;
% below the result is a2:
canonical a1;
% below result is a1 again: 
canonical ws;
%  the above manipulations are NOT DONE if space is AFFINE
off onespace;
define_spaces aff={dd,affine};
make_tensor_belong_space(te,aff); 
% dummy indices MUST be declared to belong 
% to a well defined space. here to 'aff':
mk_ids_belong_space({a,b},aff);
canonical(te(-a,a));
canonical(te(-a,a)+te(b,-b));
canonical(te(-a,c));
% put back  the system in the previous status: 
make_tensor_belong_anyspace te;
mk_ids_belong_anyspace a,b;
rem_spaces aff;
on onespace;
%
% 2. Summations with DELTA tensor.
%
make_partic_tens(delta,delta);
aa:=delta(a,-b)*delta(b,-c)*delta(c,-a) + 1;
% below, answer is dim+1:
canonical aa;
aa:=delta(a,-b)*delta(b,-c)*delta(c,-d)*te(d,e)$
canonical aa;
% 3. Summations with DELTA and ETA tensors.
make_partic_tens(eta,eta);
signature 1;
aa:=eta(a,b)*eta(-b,-c);
canonical aa;
aa:=eta(a,b)*eta(-b,-c)*eta(c,d);
canonical aa;
aa:=eta(a,b)*eta(-b,-c)*eta(d,c)*te(d,-a) +te(d,d);
canonical aa;
aa:=delta(a,-b)*eta(b,c);
canonical aa;
aa:=delta(a,-b)*delta(d,-a)*eta(-c,-d)*eta(b,c);
% below the answer is dim:
canonical aa;
aa:=delta(a,-b)*delta(d,-a)*eta(-d,-e)*te(f,g,e);
canonical aa;
% Summations with the addition of the METRIC tensor:
make_partic_tens(g,metric);
g(1,2,{x})==1/4*sin x;
g({x},1,2);
aa:=g(a,b)*g(-a,-c); 
canonical aa;
aa:=g(a,b)*g(c,d)*eta(-c,-b);
% answer is g(a,d):
canonical aa;
tensor te;
aa:=g(a,b)*g(c,d)*eta(-c,-e)*eta(e,f)*te(-f,g);
canonical aa;
% Summations with the addition of the EPSILON tensor.
dummy_indices();
rem_dummy_indices a,b,c,f;
dummy_indices();
wholespace_dim ?;
signature ?;
% define the generalized delta function:
make_partic_tens(gd,del);
make_partic_tens(epsilon,epsilon);
aa:=epsilon(a,b)*epsilon(-c,-d);
% Minus sign reflects the chosen signature.
canonical aa;
aa:=epsilon(a,b)*epsilon(-a,-b);
canonical aa;
aa:=epsilon(a,b,c,d)*epsilon(-a,-b,-c,-e);
canonical aa;
on exdelt;
% extract delta function down to the bottom:
aa:=epsilon(a,b,c)*epsilon(-b,-d,-e);
canonical aa;
off exdelt;
% below expressed in terms of 'gd' tensor.
canonical aa;
rem_dummy_indices a;
aa:=epsilon(- b,-c)*eta(a,b)*eta(a,c);
% answer below is zero:
canonical aa;
aa:=epsilon(a,b,c)*te(-a)*te(-b);
% below the result is again zero.
canonical aa;
%
tensor tf,tg;
aa:=epsilon(a,b,c)*te(-a)*tf(-b)*tg(-c)+epsilon(d,e,f)*te(-d)*tf(-e)*tg(-f);
% below the result is twice the first term.
canonical aa;
aa:=epsilon(a,b,c)*te(-a)*tf(-c)*tg(-b)+epsilon(d,e,f)*te(-d)*tf(-e)*tg(-f);
% below the result is zero.
canonical aa;
% An illustration when working inside several spaces. 
rem_dummy_indices a,b,c,d,e,f;
off onespace;
define_spaces wholespace={dim,signature=1};
define_spaces sub4={4,signature=1};
define_spaces subd={dim-4,signature=0};
show_spaces();
make_partic_tens(epsilon,epsilon);
make_tensor_belong_space(epsilon,sub4);
make_partic_tens(kappa,epsilon);
make_tensor_belong_space(kappa,subd);
show_epsilons();
mk_ids_belong_space({i,j,k,l,m,n,r,s},sub4);
mk_ids_belong_space({a,b,c,d,e,f},subd);
off exdelt;
aa:=kappa(a,b,c)*kappa(-d,-e,-f)*epsilon(i,j,k,l)*epsilon(-k,-l,-i,-j);
canonical aa;
aa:=kappa(a,b,c)*kappa(-d,-e,-f)*epsilon(i,j,k,l)*epsilon(-m,-n,-r,-s);
canonical aa;
end;
