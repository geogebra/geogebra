% test symmetry package
% implementation of theory of linear representations
% for small groups 

availablegroups();

printgroup(D4);

generators(D4);

charactertable(D4);

characternr(D4,1);
characternr(D4,2);
characternr(D4,3);
characternr(D4,4);
characternr(D4,5);
irreduciblereptable(D4);
irreduciblerepnr(D4,1);
irreduciblerepnr(D4,2);
irreduciblerepnr(D4,3);
irreduciblerepnr(D4,4);
irreduciblerepnr(D4,5);


rr:=mat((1,0,0,0,0),
        (0,0,1,0,0),
        (0,0,0,1,0),
        (0,0,0,0,1),
        (0,1,0,0,0));

sp:=mat((1,0,0,0,0),
        (0,0,1,0,0),
        (0,1,0,0,0),
        (0,0,0,0,1),
        (0,0,0,1,0));

rep:={D4,rD4=rr,sD4=sp};

canonicaldecomposition(rep);

character(rep);

symmetrybasis(rep,1);
symmetrybasis(rep,2);
symmetrybasis(rep,3);
symmetrybasis(rep,4);
symmetrybasis(rep,5);
symmetrybasispart(rep,5);
allsymmetrybases(rep);


% Ritz matrix from Stiefel, Faessler p. 200
m:=mat((eps,a,a,a,a),
       (a  ,d,b,g,b),
       (a  ,b,d,b,g),
       (a  ,g,b,d,b),
       (a  ,b,g,b,d));


diagonalize(m,rep);

% eigenvalues are obvious. Eigenvectors may be obtained with
% the coordinate transformation matrix given by allsymmetrybases.

r1:=mat((0,1,0),
        (0,0,1),
        (1,0,0));

repC3:={C3,rC3=r1};

mC3:=mat((a,b,c),
         (c,a,b),
         (b,c,a));

diagonalize(mC3,repC3); 

% note difference between real and complex case

on complex;
diagonalize(mC3,repC3); 
off complex;

end;
