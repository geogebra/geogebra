% ***** Simple test 1 *****
vars := {x, y, z};
polys := {x,z};
bibasis(polys, vars, degrevlex, t);


% ***** Simple test 2 *****
vars := {x, y, z};
polys := {x,z};
bibasis(polys, vars, degrevlex, nil);


% ***** Simple test 3 *****
vars := {x, y};
polys := {x*y+x+1};
bibasis(polys, vars, degrevlex, t);


% ***** Simple test 4 *****
vars := {x0, x1, x2, x3, x4};
polys := {x0*x3+x1*x2, x2*x4+x0};
bibasis(polys, vars, degrevlex, t);


% ***** Simple test 5 *****
vars := {x0, x1, x2, x3, x4};
polys := {x0*x3+x1*x2, x2*x4+x0};
bibasis(polys, vars, deglex, t);


% ***** Simple test 6 *****
vars := {x0, x1, x2, x3, x4};
polys := {x0*x3+x1*x2, x2*x4+x0};
bibasis(polys, vars, lex, t);


% ***** life 4 *****
vars := {x0, x1, x2, x3, x4};
polys := {x4+x0*x1+x0*x2+x1*x2+x0*x1*x2+x0*x3+x1*x3+x2*x3+x0*x1*x2*x3};
bibasis(polys, vars, degrevlex, t);


end$
