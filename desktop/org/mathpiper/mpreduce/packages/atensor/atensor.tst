%*********************************************************************
%                          ATENSOR  TEST  RUN.
%
%                       V.A.Ilyin & A.P.Kryukov
%                  E-mail:   ilyin@theory.npi.msu.su
%                          kryukov@theory.npi.msu.su
%
%                Nucl. Phys. Inst., Moscow State Univ. 
%                        119899 Moscow, RUSSIA
%*********************************************************************

% First of all we have to load the ATENSOR program using the one of the
% following command:
%       1) in "atensor.red"$         % If we load source code
%       2) load atensor$             % If we load binary (compiled) code.
load atensor;

% To control of total execution time clear timer:
showtime;

% Switch on the switch TIME to control of executing time 
% for each statement.
%on time$

% Let us introduce the antisymmetric tensor of the second order.
tensor a2;

% The antisymmetric property can be expressed as:
tsym a2(i,j)+a2(j,i);

% The K-basis that span K subspace is:
kbasis a2;

% Let us input very simple example:
a2(k,k);

% By the way the next two expressions looks like different ones:
a2(i,j);
a2(j,i);

% But the difference of them has a correct value:
a2(j,i)-a2(i,j);

% Next examples. For this purpose we introduce 3 abstract 
% vectors - v1,v2,v3:
tensor v1,v2,v3;

% The following expression equal zero:
a2(i,j)*v1(i)*v1(j);
% It is interest that the result is consequence of the equivalence 
% of the name of tensors.

% While the next one - not:
a2(i,j)*v1(i)*v2(j);

% Well. Let us introduce the symmetric tensor of the second order.
tensor s2;
tsym s2(i,j)-s2(j,i);

% Their K-basis look like for a2 excepted sign:
kbasis s2;

% Of course the contraction symmetric  and antisymmetric tensors 
% equal zero:
a2(i,j)*s2(i,j);

% By the way, the next example not so trivial for computer...
a2(i,j)*a2(j,k)*a2(k,i);

% Much more interesting examples we can demonstrate with the
% the tensor higher order. For example full antisymmetric tensor
% of the third order:
tensor a3;

% The antisymmetric property we can introduce through the 
% permutation of the two first indices: 
tsym a3(i,j,k)+a3(j,i,k);

% And the cyclic permutation all of them:
tsym a3(i,j,k)-a3(j,k,i);

% The K basis of a3 consist of 5 vectors:
kbasis a3;

% In the beginning some very simple examples:
a3(i,k,i);
a3(i,j,k)*s2(i,j);

% The full symmetric tensor of the third order may be introduce
% by the similar way: 
tensor s3;
tsym s3(i,j,k)-s3(j,i,k);
tsym s3(i,j,k)-s3(j,k,i);
kbasis s3;

% The next examples demonstrate some calculation with them:
s3(i,j,k)-s3(i,k,j);
s3(i,j,k)*a2(i,j);
a3(i,j,k)*s2(i,j);
s3(i,j,k)*a3(i,j,k);


% Now we consider very important physical case - Rieman tensor:
tensor ri;

% It has the antisymmetric property with respect to the permutation
% of the first two indices:
tsym ri(i,j,k,l) + ri(j,i,k,l);

% It has the antisymmetric property with respect to the permutation
% of the second two indices:
tsym ri(i,j,k,l) + ri(i,j,l,k);

% And the triple term identity with cyclic permutation the 
% third of them:
tsym ri(i,j,k,l) + ri(i,k,l,j) + ri(i,l,j,k);

% The corresponding K basis consist of 22(!) vectors:
kbasis ri;

% So we get the answer for any expressions with 3 and more terms of 
% Rieman tensors with not more then 2 terms. For example:
ri(i,j,k,l)+ri(j,k,l,i)+ri(k,l,i,j)+ri(l,i,j,k); 

% This three identities leads us to very important symmetry property with
% respect to exchange of pairs indices:
ri(i,j,k,l)-ri(k,l,i,j);

% Let us start with simple example:
ri(m,n,m,n)-ri(m,n,n,m);

% Much more complicated example is:
a2(m,n)*ri(m,n,c,d) + a2(k,l)*ri(c,d,l,k);
% The answer is trivial but not so simple to obtain one.

% The dimension of the full space is 6! = 720.
% The K basis consists of 690 vectors (to reduce output we 
% commented the last statement):
%kbasis ri(a2);

% One else nontrivial examples with Riemann tensors:
(ri(i,j,k,l)-ri(i,k,j,l))*a2(i,j);

%***************** END OF TEST RUN ************************
% The total execution time is:
showtime;

$END$


