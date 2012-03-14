% Miscellaneous matrix tests.

% Tests of eigenfunction/eigenvalue code.

v := mat((1,1,-1,1,0),(1,2,-1,0,1),(-1,2,3,-1,0),
        (1,-2,1,2,-1),(2,1,-1,3,0))$

mateigen(v,et);

eigv := third first ws$

% Now check if the equation for the eigenvectors is fulfilled.  Note
% that also the last component is zero due to the eigenvalue equation.
v*eigv-et*eigv;

% Example of degenerate eigenvalues.

u := mat((2,-1,1),(0,1,1),(-1,1,1))$

mateigen(u,eta);


% Example of a fourfold degenerate eigenvalue with two corresponding
% eigenvectors.

w := mat((1,-1,1,-1),(-3,3,-5,4),(8,-4,3,-4),
         (15,-10,11,-11))$

mateigen(w,al);

eigw := third first ws;

w*eigw - al*eigw;

% Calculate the eigenvectors and eigenvalue equation.

f := mat((0,ex,ey,ez),(-ex,0,bz,-by),(-ey,-bz,0,bx),
        (-ez,by,-bx,0))$

factor om;

mateigen(f,om);

% Specialize to perpendicular electric and magnetic field.

let ez=0,ex=0,by=0;

% Note that we find two eigenvectors to the double eigenvalue 0
% (as it must be).

mateigen(f,om);

% The following has 1 as a double eigenvalue. The corresponding
% eigenvector must involve two arbitrary constants.

j := mat((9/8,1/4,-sqrt(3)/8),
         (1/4,3/2,-sqrt(3)/4),
         (-sqrt(3)/8,-sqrt(3)/4,11/8));

mateigen(j,x);

% The following is a good consistency check.

sym := mat(
   (0,  1/2,  1/(2*sqrt(2)),  0,  0),
   (1/2,  0,  1/(2*sqrt(2)),  0,  0),
   (1/(2*sqrt(2)),  1/(2*sqrt(2)),  0,  1/2,  1/2),
   (0,  0,  1/2,  0,  0),
   (0,  0,  1/2,  0,  0))$

ans := mateigen(sym,eta);

% Check of correctness for this example.

for each j in ans do
  for each k in solve(first j,eta) do
      write sub(k,sym*third j - eta*third j);


% Tests of nullspace operator.

a1 := mat((1,2,3,4),(5,6,7,8));

nullspace a1;
 
b1 := {{1,2,3,4},{5,6,7,8}};

nullspace b1;

% Example taken from a bug report for another CA system.

c1 :=
{{(p1**2*(p1**2 + p2**2 + p3**2 - s*z - z**2))/(p1**2 + p3**2), 0,
   (p1*p3*(p1**2 + p2**2 + p3**2 - s*z - z**2))/(p1**2 + p3**2),
   -((p1**2*p2*(s + z))/(p1**2 + p3**2)), p1*(s + z),
   -((p1*p2*p3*(s + z))/(p1**2 + p3**2)),
   -((p1*p3*(p1**2 + p2**2 + p3**2))/(p1**2 + p3**2)), 0,
   (p1**2*(p1**2 + p2**2 + p3**2))/(p1**2 + p3**2)},
   {0, 0, 0, 0, 0, 0, 0, 0, 0},
  {(p1*p3*(p1**2 + p2**2 + p3**2 - s*z - z**2))/(p1**2 + p3**2), 0,
   (p3**2*(p1**2 + p2**2 + p3**2 - s*z - z**2))/(p1**2 + p3**2),
   -((p1*p2*p3*(s + z))/(p1**2 + p3**2)), p3*(s + z),
   -((p2*p3**2*(s + z))/(p1**2 + p3**2)),
   -((p3**2*(p1**2 + p2**2 + p3**2))/(p1**2 + p3**2)), 0,
   (p1*p3*(p1**2 + p2**2 + p3**2))/(p1**2 + p3**2)},
  {-((p1**2*p2*(s + z))/(p1**2 + p3**2)), 0,
   -((p1*p2*p3*(s + z))/(p1**2 + p3**2)),
   -((p1**2*p2**2*(s + 2*z))/((p1**2 + p3**2)*z)), (p1*p2*(s + 2*z))/z,
   -((p1*p2**2*p3*(s + 2*z))/((p1**2 + p3**2)*z)),
   -((p1*p2*p3*z)/(p1**2 + p3**2)), 0, (p1**2*p2*z)/(p1**2 + p3**2)},
  {p1*(s + z), 0, p3*(s + z), (p1*p2*(s + 2*z))/z,
   -(((p1**2+p3**2)*(s+ 2*z))/z), (p2*p3*(s + 2*z))/z, p3*z,0, -(p1*z)},
  {-((p1*p2*p3*(s + z))/(p1**2 + p3**2)), 0,
   -((p2*p3**2*(s + z))/(p1**2 + p3**2)),
   -((p1*p2**2*p3*(s + 2*z))/((p1**2 + p3**2)*z)), (p2*p3*(s + 2*z))/z,
   -((p2**2*p3**2*(s + 2*z))/((p1**2 + p3**2)*z)),
   -((p2*p3**2*z)/(p1**2 + p3**2)), 0, (p1*p2*p3*z)/(p1**2 + p3**2)},
  {-((p1*p3*(p1**2 + p2**2 + p3**2))/(p1**2 + p3**2)), 0,
   -((p3**2*(p1**2 + p2**2 + p3**2))/(p1**2 + p3**2)),
   -((p1*p2*p3*z)/(p1**2 + p3**2)),p3*z,-((p2*p3**2*z)/(p1**2 + p3**2)),
   -((p3**2*(p1**2 + p2**2 + p3**2)*z)/((p1**2 + p3**2)*(s + z))), 0,
   (p1*p3*(p1**2 + p2**2 + p3**2)*z)/((p1**2 + p3**2)*(s + z))},
  {0, 0, 0, 0, 0, 0, 0, 0, 0}, 
  {(p1**2*(p1**2 + p2**2 + p3**2))/(p1**2 + p3**2), 0,
   (p1*p3*(p1**2 + p2**2 + p3**2))/(p1**2 + p3**2),
   (p1**2*p2*z)/(p1**2 + p3**2), -(p1*z), (p1*p2*p3*z)/(p1**2 + p3**2),
   (p1*p3*(p1**2 + p2**2 + p3**2)*z)/((p1**2 + p3**2)*(s + z)), 0,
   -((p1**2*(p1**2 + p2**2 + p3**2)*z)/((p1**2 + p3**2)*(s + z)))}};

nullspace c1;
 
d1 := mat
(((p1**2*(p1**2 + p2**2 + p3**2 - s*z - z**2))/(p1**2 + p3**2), 0,
   (p1*p3*(p1**2 + p2**2 + p3**2 - s*z - z**2))/(p1**2 + p3**2),
   -((p1**2*p2*(s + z))/(p1**2 + p3**2)), p1*(s + z),
   -((p1*p2*p3*(s + z))/(p1**2 + p3**2)),
   -((p1*p3*(p1**2 + p2**2 + p3**2))/(p1**2 + p3**2)), 0,
   (p1**2*(p1**2 + p2**2 + p3**2))/(p1**2 + p3**2)),
   (0, 0, 0, 0, 0, 0, 0, 0, 0),
  ((p1*p3*(p1**2 + p2**2 + p3**2 - s*z - z**2))/(p1**2 + p3**2), 0,
   (p3**2*(p1**2 + p2**2 + p3**2 - s*z - z**2))/(p1**2 + p3**2),
   -((p1*p2*p3*(s + z))/(p1**2 + p3**2)), p3*(s + z),
   -((p2*p3**2*(s + z))/(p1**2 + p3**2)),
   -((p3**2*(p1**2 + p2**2 + p3**2))/(p1**2 + p3**2)), 0,
   (p1*p3*(p1**2 + p2**2 + p3**2))/(p1**2 + p3**2)),
  ( ((p1**2*p2*(s + z))/(p1**2 + p3**2)), 0,
   -((p1*p2*p3*(s + z))/(p1**2 + p3**2)),
   -((p1**2*p2**2*(s + 2*z))/((p1**2 + p3**2)*z)), (p1*p2*(s + 2*z))/z,
   -((p1*p2**2*p3*(s + 2*z))/((p1**2 + p3**2)*z)),
   -((p1*p2*p3*z)/(p1**2 + p3**2)), 0, (p1**2*p2*z)/(p1**2 + p3**2)),
  (p1*(s + z), 0, p3*(s + z), (p1*p2*(s + 2*z))/z,
   -(((p1**2 + p3**2)*(s + 2*z))/z),(p2*p3*(s + 2*z))/z,p3*z,0,-(p1*z)),
  (-((p1*p2*p3*(s + z))/(p1**2 + p3**2)), 0,
   -((p2*p3**2*(s + z))/(p1**2 + p3**2)),
   -((p1*p2**2*p3*(s + 2*z))/((p1**2 + p3**2)*z)), (p2*p3*(s + 2*z))/z,
   -((p2**2*p3**2*(s + 2*z))/((p1**2 + p3**2)*z)),
   -((p2*p3**2*z)/(p1**2 + p3**2)), 0, (p1*p2*p3*z)/(p1**2 + p3**2)),
  (-((p1*p3*(p1**2 + p2**2 + p3**2))/(p1**2 + p3**2)), 0,
   -((p3**2*(p1**2 + p2**2 + p3**2))/(p1**2 + p3**2)),
   -((p1*p2*p3*z)/(p1**2 + p3**2)),p3*z,-((p2*p3**2*z)/(p1**2 + p3**2)),
   -((p3**2*(p1**2 + p2**2 + p3**2)*z)/((p1**2 + p3**2)*(s + z))), 0,
   (p1*p3*(p1**2 + p2**2 + p3**2)*z)/((p1**2 + p3**2)*(s + z))),
  (0, 0, 0, 0, 0, 0, 0, 0, 0), 
   ((p1**2*(p1**2 + p2**2 + p3**2))/(p1**2 + p3**2), 0,
   (p1*p3*(p1**2 + p2**2 + p3**2))/(p1**2 + p3**2),
   (p1**2*p2*z)/(p1**2 + p3**2), -(p1*z), (p1*p2*p3*z)/(p1**2 + p3**2),
   (p1*p3*(p1**2 + p2**2 + p3**2)*z)/((p1**2 + p3**2)*(s + z)), 0,
   -((p1**2*(p1**2 + p2**2 + p3**2)*z)/((p1**2 + p3**2)*(s + z)))));

nullspace d1;


% The following example, by Kenton Yee, was discussed extensively by
% the sci.math.symbolic newsgroup.

m := mat((e^(-1), e^(-1), e^(-1), e^(-1), e^(-1), e^(-1), e^(-1), 0),
         (1, 1, 1, 1, 1, 1, 0, 1),(1, 1, 1, 1, 1, 0, 1, 1),
         (1, 1, 1, 1, 0, 1, 1, 1),(1, 1, 1, 0, 1, 1, 1, 1),
         (1, 1, 0, 1, 1, 1, 1, 1),(1, 0, 1, 1, 1, 1, 1, 1),
         (0, e, e, e, e, e, e, e));

eig := mateigen(m,x);

% Now check the eigenvectors and calculate the eigenvalues in the
% respective eigenspaces:

factor expt;

for each eispace in eig do
  begin scalar eivaleq,eival,eivec;
    eival := solve(first eispace,x);
    for each soln in eival do
      <<eival := rhs soln;
        eivec := third eispace;
        eivec := sub(soln,eivec);
        write "eigenvalue = ", eival;
        write "check of eigen equation: ", 
              m*eivec - eival*eivec>>
  end;

% For the special choice:

let e = -7 + sqrt 48;

% we get only 7 eigenvectors.

eig := mateigen(m,x);

for each eispace in eig do
  begin scalar eivaleq,eival,eivec;
    eival := solve(first eispace,x);
    for each soln in eival do
      <<eival := rhs soln;
        eivec := third eispace;
        eivec := sub(soln,eivec);
        write "eigenvalue = ", eival;
        write "check of eigen equation: ", 
              m*eivec - eival*eivec>>
  end;

% The same behaviour for this choice of e.

clear e; let e = -7 - sqrt 48;

% we get only 7 eigenvectors.

eig := mateigen(m,x);

for each eispace in eig do
  begin scalar eivaleq,eival,eivec;
    eival := solve(first eispace,x);
    for each soln in eival do
      <<eival := rhs soln;
        eivec := third eispace;
        eivec := sub(soln,eivec);
        write "eigenvalue = ", eival;
        write "check of eigen equation: ", 
              m*eivec - eival*eivec>>
  end;


% For this choice of values

clear e; let e = 1;

% the eigenvalue 1 becomes 4-fold degenerate. However, we get a complete
% span of 8 eigenvectors.

eig := mateigen(m,x);

for each eispace in eig do
  begin scalar eivaleq,eival,eivec;
    eival := solve(first eispace,x);
    for each soln in eival do
      <<eival := rhs soln;
        eivec := third eispace;
        eivec := sub(soln,eivec);
        write "eigenvalue = ", eival;
        write "check of eigen equation: ", 
              m*eivec - eival*eivec>>
  end;

ma := mat((1,a),(0,b));

% case 1:

let a = 0;

mateigen(ma,x);

% case 2:

clear a; let a = 0, b = 1;

mateigen(ma,x);

% case 3:

clear a,b; 

mateigen(ma,x);

% case 4:

let b = 1; 

mateigen(ma,x);

% Example from H.G. Graebe:

m1:=mat((-sqrt(3)+1,2         ,3         ),
	(2         ,-sqrt(3)+3,1         ),
	(3         ,1         ,-sqrt(3)+2));

nullspace m1;

for each n in ws collect m1*n;

end;
