% Examples taken from the manual:

% 1 Introduction
oo := torder({x,y},lex)$
cgb {a*x,x+y};
gsys {a*x,x+y};
torder oo;

% 4 CGB: Comprehensive Groebner Basis
oo := torder({x,y},lex)$
cgb {a*x+y,x+b*y};
torder oo;

% 5 GSYS: Groebner System
oo := torder({x,y},lex)$
gsys {a*x+y,x+b*y};
torder oo;

% 6 GSYS2CGB: Groebner System to CGB
oo := torder({x,y},lex)$
gsys {a*x+y,x+b*y};
gsys2cgb ws;
torder oo;

% 7 Switch CGBREAL: Computing over the Real Numbers
oo := torder({x,y},lex)$
off cgbreal;
gsys {a*x+y,x-a*y};
on cgbreal;
gsys({a*x+y,x-a*y});
torder oo;

% Miscellaneous examples:

% Dolzmann's Example
oo := torder({x,y,z},lex);
cgb({a*x+b*y,c*x+d*y,(a*d-b*c)*z});
gsys({a*x+b*y,c*x+d*y,(a*d-b*c)*z});
gsys2cgb ws;
torder oo;

% Forsman's Example (hybrid control system).
oo := torder({x1,x2,y2,y1,y0},lex);
gsys({(u1*u2-u1)*x1+u2*x2+y2,(u2-1)*x1+u2*x2+y1,-x2+y0});
torder oo;

% Weispfenning's Example
oo := torder({x,y},lex);
gsys({v*x*y + x,u*y^2 + x^2});
torder oo;

% The folllowing three examples are taken from
% Weispfenning, Comprehensive Groebner Bases,
% J. Symbolic Computation (1992) 14, 1-29

% Weispfenning's Example 7.1
oo := torder({x},lex);
gsys({a0*x**2 + a1*x + a2,b0*x**2 + b1*x + b2});
torder oo;

% Weispfenning's Example 7.2
oo := torder({x,y},lex);
gsys({v*x*y + u*x**2 + x,u*y**2 + x**2});
torder oo;

% Weispfenning's Example 7.3
oo := torder({x1,x2,x3,x4},lex);
gsys {x4 - (a4-a2),x1 + x2 + x3 + x4 + (a1 + a3 + a4),
   x1*x3 + x1*x4 + x2*x3 + x3*x4 - (a1*a4 + a1*a3 + a3*a4),x1*x3*x4 - a1*a3*a4};
torder oo;

% Pesch's example (Circle through three points)
oo := torder({y,x},revgradlex);
gsys({2*b2*y + 2*a2*x - b2**2 + a2**2,2*b1*y + 2*a1*x - b1**2 + a1**2});
torder oo;

% Effelterre's example (Aspect graphs)
f1 := -4-4*v**2-4*u**2+40*v*v1+24*v-120*v1+8*u-40*v2-68*v1**2-100*v2**2+40*u*v2+
24*v1*v2-4*v1**2*u-4*v2**2*v**2+24*v2**2*v-24*v1*u*v2+8*v*v1*u*v2$
f2 := 8*v*v1*u*v2-4*v1**2*u**2+4*v1**2-4*v2**2*v**2+4*v2**2-16*v**2-16*u**2+16$
f3 := 16*v-48*u+16*v*v1**2-48*u*v2**2-12*v1**2*u+4*v2**2*v-36*v*v1*v2+
12*v1*u*v2+12*v*v2**2*u-
80*u*v1+80*v2*v-20*v1*u*v2**2+20*v2*v*v1**2-20*v1**3*u+20*v2**3*v-12*v1**2*v*u+
12*v2*v**2*v1-12*v1*u**2*v2$
f4 := -160u*v2-1596v2**2+3200*v2-1596-4*u**2+160*u$

% Special case I2, v1=0
oo := torder({v,u},lex);
gsys(sub(v1=0,{f1,f2,f4}));
torder oo;
clear f1,f2,f3,f4;

% Sit's Example 2.2
oo := torder({z2,z2},revgradlex);
gsys({d*z2 + c*z1 - v,b*z2 + a*z1 - u});
torder oo;

% Sit's Example 2.3
oo := torder({z2,z2},revgradlex);
gsys({x**3*z2 + (x**2+1)*z1,x**2*z2 + x*z1 - 1});
torder oo;

% Sit's Example 3.3
oo := torder({z3,z2,z2},revgradlex);
gsys({z3 + b*z2 + a*z1 - 1,a*z3 + z2 + b*z1 - 1,b*z3 + a*z2 + z1 - 1});
torder oo;

% Sit's Example 8.3
oo := torder({z4,z3,z2,z2},revgradlex);
gsys({z4 + c*z3 + b*z2 + a*z1 - w2,2*z4 + z2 - w1,a*z4 - z3 - w4,d*z4 + z3 +
2*z1 - w3,z4 + z1 - w5});
torder oo;

% Two dimensional transportation problem
oo := torder({x33,x32,x31,x23,x22,x21,x13,x12,x11},lex);
gsys({x11+x12+x13-a1,x11+x21+x31-b1,x12+x22+x32-b2,x13+x23+x33-b3,
x21+x22+x23-a2,x31+x32+x33-a3});
torder oo;

% Thomas Weis's Example 1
oo := torder({x,y,z},lex);
gsys({z*y*x-b*y*x-b*z*x+b**2*x-b*z*y+b**2*y+b**2*z-(n3+b**3),
z*y*x-a*y*x-a*z*x+a**2*x-a*z*y+a**2*y+a**2*z-(n3+a**3),
z*y*x-n1});
torder oo;

% Thomas Weis's Example 2
oo := torder({z,y,x,w},lex);
gsys({w*x*y*z-x*y*z-w*y*z+y*z-w*x*z+x*z+w*z-z-w*x*y+x*y+w*y-
y+w*x-x-w-(b-1),
w*x*y*z-2*x*y*z-2*w*y*z+4*y*z-2*w*x*z+4*x*z+4*w*z-8*z-2*w*x*y+4x*y+
4*w*y-8*y+4*w*x-8*x-8*w-(c-16),
w*x*y*z-a,z+y+x+w-v});
torder oo;

end;  % of file
