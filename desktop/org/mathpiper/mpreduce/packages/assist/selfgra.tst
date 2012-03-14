%%%%%%%%%%%%%%%%%%%  A. Burnel and H. Caprasse  %%%%%%%%%%%%%%%%%%%%%%
% 
% Application of CANTENS.RED 
% Date: 15/09/98
%
% Computes the gluon contribution  to the gluon self-energy in the 
% "finite" theory
% contains initially 18 terms which are reduced to 10 by cantens 
% in a dm-dimensional Minkowski space and 8 terms in a 4-dimensional 
% Minkowski space. 
% 
%          *** Will look much nicer if run in the GRAPHIC mode 
%
% LOADING CANTENS

load cantens$


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Structure definitions, Minkowski space  X internal symmetry space
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

off onespace; % to be allowed to work within several subspaces

define_spaces wholespace={dm+di,signature=1};
define_spaces mink={dm,signature=1};%,indexrange=0 .. 3};
define_spaces internal={di,signature=0};%,indexrange=4 .. 11};
%
%  Memberships of indices:
mk_ids_belong_space({mu1,mu2,nu1,nu2,tau},mink);
mk_ids_belong_space({a1,a2,b1,b2,c1,c2},internal);

                         %%%%%%%%%%%%%%%%
                         % Used Tensors %
                         %%%%%%%%%%%%%%%%

%%                 variables x1,x2 and xi=x1-x2,
%%                 aa, gluon field
%%                 dd, contracted gluon field 
%%                 which appears inside the expression
%%                 a is the antisymmetric structure constant of SU3.
%%                 It is called "a" to assure that it appears first 
%%                 inside REDUCE expressions and to assure that they 
%%                 factorize in front of the output expression.
%
tensor aa,dd,a,x1,x2,xi; % tensor declaration

make_variables x1,x2,xi; % variable declaration

% declare to which subspace the declared tensors belong to.

make_tensor_belong_space(x1,mink); 
make_tensor_belong_space(x2,mink);
make_tensor_belong_space(xi,mink);
make_tensor_belong_space(a,internal);

antisymmetric a; % antisymmetry of structure constant.

  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
  % building of starting expression to be manipulated  and simplified.
  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

es1:=g^2*a(a1,b1,c1)*a(a2,b2,c2);
as1:=-aa(x1,nu1,-b1)*aa(x2,nu2,-b2)*df(df(dd(xi,mu1,-c1,mu2,-c2),xi(nu1)),xi(nu2))
*dd(xi,-mu1,-a1,-mu2,-a2);
as2:=-aa(x1,nu1,-b1)*aa(x2,nu2,-b2)*df(dd(xi,mu1,-c1,mu2,-a2),xi(nu1))
*df(dd(xi,-mu1,-a1,-mu2,-c2),xi(nu2));
as3:=aa(x1,nu1,-b1)*df(aa(x2,mu2,-c2),x2(nu2))*df(dd(xi,mu1,-c1,nu2,-b2),xi(nu1))
*dd(xi,-mu1,-a1,-mu2,-a2);
as4:=aa(x1,nu1,-b1)*df(aa(x2,mu2,-c2),x2(nu2))*df(dd(xi,mu1,-c1,-mu2,-a2),xi(nu1))
*dd(xi,-mu1,-a1,nu2,-b2);
as5:=-aa(x1,nu1,-b1)*aa(x2,mu2,-a2)*df(dd(xi,mu1,-c1,nu2,-b2),xi(nu1))
*df(dd(xi,-mu1,-a1,-mu2,-c2),xi(nu2));
as6:=-aa(x1,nu1,-b1)*aa(x2,mu2,-a2)*df(df(dd(xi,mu1,-c1,-mu2,-c2),xi(nu1)),xi(nu2))
*dd(xi,-mu1,-a1,nu2,-b2);
as7:=-df(aa(x1,mu1,-c1),x1(nu1))*aa(x2,nu2,-b2)*df(dd(xi,nu1,-b1,mu2,-c2),xi(nu2))
*dd(xi,-mu1,-a1,-mu2,-a2);
as8:=-df(aa(x1,mu1,-c1),x1(nu1))*aa(x2,nu2,-b2)*df(dd(xi,-mu1,-a1,mu2,-c2),xi(nu2))
*dd(xi,nu1,-b1,-mu2,-a2);
as9:=df(aa(x1,mu1,-c1),x1(nu1))*df(aa(x2,mu2,-c2),x2(nu2))*dd(xi,nu1,-b1,nu2,-b2)
*dd(xi,-mu1,-a1,-mu2,-a2);
as10:=df(aa(x1,mu1,-c1),x1(nu1))*df(aa(x2,mu2,-c2),x2(nu2))*dd(xi,nu1,-b1,-mu2,-a2)
*dd(xi,-mu1,-a1,nu2,-b2);
as11:=-df(aa(x1,mu1,-c1),x1(nu1))*aa(x2,mu2,-a2)*df(dd(xi,-mu1,-a1,-mu2,-c2),xi(nu2))
*dd(xi,nu1,-b1,nu2,-b2);
as12:=-df(aa(x1,mu1,-c1),x1(nu1))*aa(x2,mu2,-a2)*df(dd(xi,nu1,-b1,-mu2,-c2),xi(nu2))
*dd(xi,-mu1,-a1,nu2,-b2);
as13:=-aa(x1,mu1,-a1)*aa(x2,nu2,-b2)*df(dd(xi,nu1,-b1,mu2,-c2),xi(nu2))
*df(dd(xi,-mu1,-c1,-mu2,-a2),xi(nu1));
as14:=-aa(x1,mu1,-a1)*aa(x2,nu2,-b2)*dd(xi,nu1,-b1,mu2,-a2)
*df(dd(xi,-mu1,-c1,-mu2,-c2),xi(nu1),xi(nu2));
as15:=aa(x1,mu1,-a1)*df(aa(x2,mu2,-c2),x2(nu2))*dd(xi,nu1,-b1,nu2,-b2)
*df(dd(xi,-mu1,-c1,-mu2,-a2),xi(nu1));
as16:=aa(x1,mu1,-a1)*df(aa(x2,mu2,-c2),x2(nu2))*dd(xi,nu1,-b1,-mu2,-a2)
*df(dd(xi,-mu1,-c1,nu2,-b2),xi(nu1));
as17:=-aa(x1,mu1,-a1)*aa(x2,mu2,-a2)*df(dd(xi,-mu1,-c1,-mu2,-c2),xi(nu1),xi(nu2))
*dd(xi,nu1,-b1,nu2,-b2);
as18:=-aa(x1,mu1,-a1)*aa(x2,mu2,-a2)*df(dd(xi,-mu1,-c1,nu2,-b2),xi(nu1))
*df(dd(xi,nu1,-b1,-mu2,-c2),xi(nu2));

     %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
     % building of the gluon contribution to gluon self-energy %
     %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

es:=es1*for i:=1:18 sum mkid(as,i);

      %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
      % Are some terms identical ? %
      %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
es:=canonical es;
length es;
% no simplification
    
tensor dc;  % new tensor
make_tensor_belong_space(dc,mink); % belongs to Minkowski space
make_partic_tens(rho,metric); % "rho" is a metric tensor
make_tensor_belong_space(rho,internal); % in the internal space

          %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
          % rewriting rule and subsequent simplification %  
          % dd(mu1,mu2,a,b)=>rho(a,b)*dc(mu1,mu2)        %
          %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

ddrule:={dd({~xi},~a,~b,~c,~d)=>rho(b,d)*dc({xi},a,c)};


          %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
          % simplification after application of the rule % 
          %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


es:=(es where ddrule);
%
es:=canonical es;


               %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
               %         Particular gauge:                       %
               % case of Fermi gauge : dc(mu1,mu2)=g(mu1,mu2)*dc %
               %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

make_partic_tens(delta,delta); % delta tenseur defined with name "delta"

% eta tenseur introduced with name "eta":
make_partic_tens(eta,eta);  
make_tensor_belong_space(eta,mink);

%  rule for the choice of gauge:

dcrule:={dc({~xi},~a,~c)=>eta(a,c)*dc(xi)};

% rewriting of the expression

res:=(es where dcrule);

% simplification

res:=canonical res;

                %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
                % last rewriting rule:             %
                % second derivative of dc(xi) with %
                %  respect to xi tensor is zero    %
                %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

dalrule:={df(dc(xi),xi(~a),xi(-~a))=>0};
res:=(res where dalrule);
canonical res - res; % gives 0
length res;   
dm:=4; % particularization to 4-dimensional Minkowski space
res4:=res;
length res4; % 8  is the correct number of terms.



end;
%in "skelsplt.red";
tensor ff;
%symtree(ff,{!*,{!-,1,2},3});
symbolic procedure nordpl(u,v);
if listp u and listp v then nordp(cadr u,cadr v) else
  if listp u then nordp(cadr u,v) else
    if listp v then nordp(u,cadr v) else nordp(u,v);

flag('(nordpl),'opfn);

%frule:={df(aa({x1},~mu1,~b),x1(~mu2))=>ff({x1},-mu2,mu1,b)+df(aa({x1},-mu2,b),x1(-mu1))
%        when nordpl(mu1,mu2)};
%ffrule:={df(aa({x2},~mu1,~b),x2(~mu2))=>ff({x2},-mu2,mu1,b)+df(aa({x2},-mu2,b),x2(-mu1))
%        when nordpl(mu1,mu2)};
frule:={df(aa({~x1},~mu1,~b),~x1(~mu2))=>ff({x1},-mu2,mu1,b)+df(aa({x1},-mu2,b),x1(-mu1))
        when nordpl(mu1,mu2)};
res4 where frule;
