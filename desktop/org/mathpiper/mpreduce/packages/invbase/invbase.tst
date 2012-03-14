
% *****  Example 1  *****

g:=invbase{4*x^2 + x*y^2 - z +1/4,
           2*x + y^2*z + 1/2,
           x^2*z - 1/2*x - y^2};

h:=invlex g;


% *****  Example 2  *****

on trinvbase$
invtorder revgradlex,{x,y,z}$

g:=invbase{x^3 + y^2 + z - 3,
           y^3 + z^2 + x - 3,
           z^3 + x^2 + y - 3};

h:=invlex g;

 
% *****  Example 3 (limited by the degree bound)  *****

invtorder revgradlex,{x,z,y,t}$

k:=5$

on errcont$

invbase{x^(k+1)-y^(k-1)*z*t, 
         x*z^(k-1)-y**k, 
         x^k*y-z^k*t};

invtempbasis;

end$

