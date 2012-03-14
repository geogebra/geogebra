% test of DUMMY package version 1.1 running in REDUCE 3.6 and 3.7
% DATE: 15 September 1998
% Authors: H. Caprasse <hubert.caprasse@ulg.ac.be>
%
% Case of commuting operator:
%
operator co1,co2;

% declare dummy indices

% first syntax : base <name>
%
dummy_base dv;

% dummy indices are dv1, dv2, dv3, ...

exp := co2(dv2)*co2(dv2)$
c_exp := canonical(exp);

exp := dv2*co2(dv2)*co2(dv2)$
c_exp := canonical(exp);

exp := c_exp * co1(dv3);
c_exp := canonical(exp);

%
operator a,aa,dd,te;

clear_dummy_base;

dummy_names a1,a2,b1,b2,mu1,mu2,nu1,nu2;

es1:=a(a1,b1)*a(a2,b2);

asn14:=aa(mu1,a1)*aa(nu2,b2)*dd(nu1,b1,mu2,a2)
         *te(mu1,mu2,nu1,nu2);
asn17:=aa(mu1,a1)*aa(mu2,a2)*dd(nu1,b1,nu2,b2)
          *te(mu1,mu2,nu1,nu2);
             
esn14:=es1*asn14;
esn17:=es1*asn17;
esn:=es1*(asn14+asn17); 
canonical esn;
% that the next result is correct is not trivial 
% to show.
% for esn14 changes of names are 
%
%  nu1 -> nu1
%  b1 -> b2 -> a2
%  mu2 -> nu2 -> mu1 -> mu2
%    
% for esn17 they are
%
%  nu1 -> nu1
%  nu2 -> nu2
%  b1 -> b2 -> a2 -> a1 -> b1
%
% the last result should be zero 
canonical esn -(canonical esn14 +canonical esn17);
% remove dummy_names and operators.
clear_dummy_names;
clear a,aa,dd,te;
%
% Case of anticommuting operators
%
operator ao1, ao2;
anticom ao1, ao2;
% product of anticommuting operators with FREE indices
a_exp := ao1(s1)*ao1(s2) - ao1(s2)*ao1(s1);
a_exp := canonical(a_exp);

% the indices are summed upon, i.e. are DUMMY indices

clear_dummy_names;

dummy_base dv;

a_exp := ao1(dv1)*ao1(dv2)$
canonical(a_exp);

a_exp := ao1(dv1)*ao1(dv2) - ao1(dv2)*ao1(dv1);
a_exp := canonical(a_exp);

a_exp := ao1(dv2,dv3)*ao2(dv1,dv2)$
a_exp := canonical(a_exp);

a_exp := ao1(dv1)*ao1(dv3)*ao2(dv3)*ao2(dv1)$
a_exp := canonical(a_exp);

% Case of non commuting operators
%
operator no1, no2, no3;
noncom no1, no2, no3;

n_exp := no3(dv2)*no2(dv3)*no1(dv1) + no3(dv3)*no2(dv1)*no1(dv2)
         + no3(dv1)*no2(dv2)*no1(dv3);

n_exp:=canonical n_exp;

% ***
% The example below displays a restriction of the package i.e
% The non commuting operators are ASSUMED to COMMUTE with the
% anticommuting operators.
% ***
exp := co1(dv1)*ao1(dv2,dv1,dv4)*no1(dv1,dv5)*co2(dv3)*ao1(dv1,dv3);
canonical(exp);

exp := c_exp * a_exp * no3(dv2)*no2(dv3)*no1(dv1);
can_exp := canonical(exp);

% Case where some operators have a symmetry.
%
operator as1, as2;
antisymmetric as1, as2;

dummy_base s;

% With commuting and antisymmetric:

asc_exp:=as1(s1,s2)*as1(s1,s3)*as1(s3,s4)*co1(s3)*co1(s4)+
        2*as1(s1,s2)*as1(s1,s3)*as1(s3,s4)*co1(s2)*co1(s4)$

canonical asc_exp;

% Indeed: the second term is identically zero as one sees 
%          if the substitutions s2->s4, s4->s2 and 
%           s1->s3, s3->s1 are sucessively done.
% 
% With anticommuting and antisymmetric operators:

dummy_base dv;

exp1 := ao1(dv1)*ao1(dv2)$
canonical(exp1);

exp2 := as1(dv1,dv2)$

canonical(exp2);

canonical(exp1*exp2);

canonical(as1(dv1,dv2)*as2(dv2,dv1));

% With symmetric and antisymmetric operators:

operator ss1, ss2;
symmetric ss1, ss2;

exp := ss1(dv1,dv2)*ss2(dv1,dv2) - ss1(dv2,dv3)*ss2(dv2,dv3);
canonical(exp);

exp := as1(dv1,dv2)*as1(dv3,dv4)*as1(dv1,dv4);
canonical(exp);

% The last result is equal to half the sum given below:
%
exp + sub(dv2 = dv3, dv3 = dv2, dv1 = dv4, dv4 = dv1, exp);

exp1 := as2(dv3,dv2)*as1(dv3,dv4)*as1(dv1,dv2)*as1(dv1,dv4);
canonical(exp1);

exp2 := as2(dv1,dv4)*as1(dv1,dv3)*as1(dv2,dv4)*as1(dv2,dv3);
canonical(exp2);

canonical(exp1-exp2);

% Indeed:
%
exp2 - sub(dv1 = dv3, dv2 = dv1, dv3 = dv4, dv4 = dv2, exp1);

% Case where mixed or incomplete symmetries for operators are declared.

% Function 'symtree' can be used to declare an operator symmetric 
% or antisymmetric:
operator om;

symtree(om,{!+,1,2,3});
exp:=om(dv1,dv2,dv3)+om(dv2,dv1,dv3)+om(dv3,dv2,dv1);
canonical exp;

% Declare om to be antisymmetric in the two last indices ONLY:
symtree(om,{!*,{!*,1},{!-,2,3}});
canonical exp; 

% With an antisymmetric operator m:
operator m;
dummy_base s;
exp := om(nu,s3,s4)*i*psi*(m(s1,s4)*om(mu,s1,s3) 
+ m(s2,s3)*om(mu,s4,s2) - m(s1,s3)*om(mu,s1,s4) 
- m(s2,s4)*om(mu,s3,s2))$

canonical exp;

% Case of the Riemann tensor
%
operator r;
symtree (r, {!+, {!-, 1, 2}, {!-, 3, 4}});
% Without anty dummy indices.
clear_dummy_base;

exp := r(dv1, dv2, dv3, dv4) * r(dv2, dv1, dv4, dv3)$
canonical(exp);

% With dummy indices:
 
dummy_base dv;

canonical( r(x,y,z,t) );
canonical( r(x,y,t,z) );
canonical( r(t,z,y,x) );

exp := r(dv1, dv2, dv3, dv4) * r(dv2, dv1, dv4, dv3)$
canonical(exp);

exp := r(dv1, dv2, dv3, dv4) * r(dv1, dv3, dv2, dv4)$
canonical(exp);

clear_dummy_base;
dummy_names i,j,k,l;

exp := r(i,j,k,l)*ao1(i,j)*ao1(k,l)$

canonical(exp);

exp := r(k,i,l,j)*as1(k,i)*as1(k,j)$
canonical(exp);

% Cleanup of the previousy declared dummy variables..

clear_dummy_names; clear_dummy_base;

exp := co1(dv3)$
c_exp := canonical(exp);

end;
