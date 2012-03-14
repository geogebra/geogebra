% Test file for the REDUCE package QSUM
%
% Copyright (c) Wolfram Koepf, Harald Boeing, Konrad-Zuse-Zentrum Berlin, 1997
%
% Implementation of the q-Gosper and q-Zeilberger algorithms
%
% Reference:
%
% Koornwinder, T. H.:
% On Zeilberger's algorithm and its q-analogue: a rigorous description.
% J. of Comput. and Appl. Math. 48, 1993, 91-111.
%
% Some examples are from
%
% Koekoek, R. and Swarttouw, R.F.:
% The Askey-scheme of Hypergeometric Orthogonal Polynomials and its q-analogue.
% Report 94-05, Technische Universiteit Delft, Faculty of Technical Mathematics
% and Informatics, Delft, 1994.
%
% Gasper, G. and Rahman, M.:
% Basic Hypergeometric Series.
% Encyclopedia of Mathematics and its Applications 35. 
% Ed. by G.-C. Rota, Cambridge University Press, London and New York, 1990.


% Results of manual qsum.tex
%
load qsum;
qgosper(qpochhammer(a,q,k)*q^k/qpochhammer(q,q,k),q,k);
qgosper(qpochhammer(a,q,k)*qpochhammer(a*q^2,q^2,k)*
        qpochhammer(q^(-n),q,k)*q^(n*k)/(qpochhammer(a,q^2,k)*
        qpochhammer(a*q^(n+1),q,k)*qpochhammer(q,q,k)),q,k);
qgosper(qpochhammer(q^(-n),q,k)*z^k/qpochhammer(q,q,k)*z^n,q,k);
off qgosper_down;
qgosper(q^k*qbrackets(k,q),q,k);
on qgosper_down;
qgosper(q^k,q,k,0,n);
qsumrecursion(qpochhammer(q^(-n),q,k)*z^k/qpochhammer(q,q,k),q,k,n);
on qsumrecursion_certificate;
proof:=qsumrecursion(qpochhammer(q^(-n),q,k)*z^k/qpochhammer(q,q,k),q,k,n);
off qsumrecursion_certificate;
% proof of statement
lhside:= qsimpcomb(sub(summ(n)=part(proof,3),
summ(n-1)=sub(n=n-1,part(proof,3)),part(proof,1)));
rhside:= qsimpcomb((part(proof,2)*part(proof,3)-
sub(k=k-1,part(proof,2)*part(proof,3))));
qsimpcomb((rhside-lhside)/part(proof,3));
% proof done
operator qlaguerre, qcharlier;
% q-Laguerre polynomials, Koekoek, Swarttouw (3.21)
qsumrecursion(qpochhammer(q^(alpha+1),q,n)/qpochhammer(q,q,n),
   {q^(-n)}, {q^(alpha+1)}, q, -x*q^(n+alpha+1), qlaguerre(n));
% q-Charlier polynomials, Koekoek, Swarttouw (3.23) 
qsumrecursion({q^(-n),q^(-x)},{0},q,-q^(n+1)/a,qcharlier(n));
% continuous q-Jacobi polynomials, Koekoek, Swarttouw (3.10)
%% on qsum_nullspace;
%% term:= qpochhammer(q^(alpha+1),q,n)/qpochhammer(q,q,n)*
%%    qphihyperterm({q^(-n),q^(n+alpha+beta+1),
%%    q^(alpha/2+1/4)*exp(I*theta), q^(alpha/2+1/4)*exp(-I*theta)},
%%    {q^(alpha+1), -q^((alpha+beta+1)/2), -q^((alpha+beta+2)/2)},
%%     q, q, k)$
%% qsumrecursion(term,q,k,n,2);
%% off qsum_nullspace;

 
% Some more qgosper results with proof
%
% Gasper, Rahman (2.3.4)
term:=qpochhammer(a,q,k)*qpochhammer(a*q^2,q^2,k)*qpochhammer(q^(-n),q,k)*
q^(n*k)/(qpochhammer(a,q^2,k)*qpochhammer(a*q^(n+1),q,k)*qpochhammer(q,q,k));

result:=qgosper(qpochhammer(a,q,k)*qpochhammer(a*q^2,q^2,k)*
qpochhammer(q^(-n),q,k)*q^(n*k)/
(qpochhammer(a,q^2,k)*qpochhammer(a*q^(n+1),q,k)*qpochhammer(q,q,k)),q,k);

qsimpcomb(result-sub(k=k-1,result)-term);

% Gasper, Rahman (3.8.16)
term:=(1-a*c*q^(4*k))*(1-b/c*q^(-2*k))*qpochhammer(a,q,k)*qpochhammer(b,q,k)*
qpochhammer(q^(-3*n),q^3,k)*qpochhammer(a*c^2/b*q^(3*n),q^3,k)*q^(3*k)/
((1-a*c)*(1-b/c)*qpochhammer(c*q^3,q^3,k)*qpochhammer(a*c/b*q^3,q^3,k)*
qpochhammer(a*c*q^(3*n+1),q,k)*qpochhammer(b/c*q^(1-3*n),q,k));

result:=qgosper((1-a*c*q^(4*k))*(1-b/c*q^(-2*k))*qpochhammer(a,q,k)*
qpochhammer(b,q,k)*qpochhammer(q^(-3*n),q^3,k)*qpochhammer(a*c^2/
b*q^(3*n),q^3,k)*q^(3*k)/((1-a*c)*(1-b/c)*qpochhammer(c*q^3,q^3,k)*
qpochhammer(a*c/b*q^3,q^3,k)*qpochhammer(a*c*q^(3*n+1),q,k)*
qpochhammer(b/c*q^(1-3*n),q,k)),q,k);

qsimpcomb(result-sub(k=k-1,result)-term);

end;

