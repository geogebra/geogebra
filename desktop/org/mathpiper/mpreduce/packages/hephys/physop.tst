COMMENT
        test file for the PHYSOP package;
% load_package physop;  % Load a compiled version of the physop package.
% showtime;
linelength(72)$
% Example 1: Quantum Mechanics of a Dirac particle in an external
%                      electromagnetic field
VECOP P,A,K;
SCALOP M;
NONCOM P,A;
PHYSINDEX J,L;
oporder M,K,A,P;

% we have to set off allfac here since otherwise there appear
% spurious negative powers in the printed output
 off allfac;
FOR ALL J,L LET COMM(P(J),A(L))=K(J)*A(L);
H:= COMMUTE(P**2/(2*M),E/(4*M**2)*(P DOT A));
% showtime;
%assign the corresponding value to the adjoint of H
H!+ := adj H;
% showtime;
% note the ordering of operators in the result!
% enhance the readability of the output
 on allfac;
ON CONTRACT;
H;
% showtime;
% Example 2: Virasoro Algebra from Conformal Field Theory


operator  del;  % this is just a definition of a delta function
for all n such that numberp n let del(n) =
     if n=0 then 1
     else 0;

scalop l;
noncom l,l;
state bra,ket;
% commutation relation of the operator l;
for all n,m let comm(l(n),l(m)) =
      (m-n)*l(n+m)+c/12*(m**3-m)*del(n+m)*unit; %modified 1.1

for all n let l!+(n) = l(-n);


% relation for the states
for all h let bra!+(h) = ket(h);
for all p,q let bra(q) | ket(p) = del(p-q);

for all r,h such that r < 0 or (r <2 and h=0) let
             l(r) | ket(h) = 0;

for all r,h such that r > 0 or (r  > -2 and h = 0) let
             bra(h) | l(r) = 0;

% define a procedure to calculate V.E.V.
procedure Vak(X);
bra(0) | X | ket(0);

% and now some calculations;
MA:= adj(l(3)*l(5))*l(3)*l(5);  %modified 1.1
% showtime;

% here is the VEV of m
vak(Ma);
% showtime;
% and now calculate another matrix element

matel := bra(1) | ma  | ket(1);  %modified 1.1
% showtime;
% this evaluation is incomplete so supply the missing relation
for all h let l(0) | ket(h) = h*ket(h);
% and reevaluate matel
matel := matel;
% showtime;


% Example 4: some manipulations with gamma matrices to demonstrate
%            the use of commutators and anticommutators


off allfac;
vecop gamma,q;
tensop sigma(2);
antisymmetric sigma;
noncom gamma,gamma;
noncom sigma,gamma;
physindex mu,nu;
operator delta;
for all mu,nu let anticomm(gamma(mu),gamma(nu))=2*delta(mu,nu)*unit,
                  comm(gamma(mu),gamma(nu))=2*I*sigma(mu,nu);

oporder p,q,gamma,sigma;
off allfac;
on anticom;
(gamma dot p)*(gamma dot q);
% showtime;

off anticom;
(gamma dot p)*(gamma dot q);
% showtime;

commute((gamma dot p),(gamma dot q));
% showtime;
anticommute((gamma dot p),(gamma dot q));
on anticom;
anticommute((gamma dot p),(gamma dot q));
% showtime;

end;
