% wu.tst

% Russell Bradford, 8 June 90.

% Some tests for the Wu algorithm
% The order directives are not necessary for general use: they just
% help tie things down for testing purposes.

% run after loading Wu code: in "wu.red"$

% test 1

order x,y,a,b;

wu({x^2+y^2-a,x*y-b}, {x,y});

% test 2

order x,y,a,b;

wu({x^2+y^2-a,x*y-b},{x,y,a,b});

% test 3

order x,y,z,r;

wu({x^2+y^2+z^2-r^2, x*y+z^2-1, x*y*z-x^2-y^2-z+1}, {x,y,z});

% test 4

order x,y,z,r;

wu({x^2+y^2+z^2-r^2, x*y+z^2-1, x*y*z-x^2-y^2-z+1}, {x,y,z,r});

% test 5

order x,y,z;

wu({(x-1)*(y-1)*(z-1), (x-2)*(y-2)*(z-2), (x-3)*(y-3)*(z-3)}, {x,y,z});

% test 6

order x,y,z;

wu({(x-1)*(y-1)*(z-1), (x-2)*(y-2)*(z-2), (x-3)*(y-3)*(z-3)});

% test 7

order x1,x2,x3,x4;

p1 := x1+x2+x3+x4;
p2 := x1*x2+x2*x3+x3*x4+x4*x1;
p3 := x1*x2*x3+x2*x3*x4+x3*x4*x1+x4*x1*x2;
p4 := x1*x2*x3*x4 - 1;

wu({p1,p2,p3,p4}, {x1,x2,x3,x4});

% test 8

order x,y,z;

wu({z*z,y*z-1,x*z-1}, {x,y,z});

end;
