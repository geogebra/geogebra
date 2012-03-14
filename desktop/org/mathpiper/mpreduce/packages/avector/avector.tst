% Vector test routine

% Author: David Harper (algebra@liverpool.ac.uk)
%         Computer Algebra Support Officer
%         University of Liverpool Computer Laboratory.

% Please compare carefully the output from running this test file with the
% log file provided to make sure your implementation is correct.

linelength 72;
off allfac; on div;
vec a,b,c;
matrix q;
a := avec(ax,ay,az);
b := avec(bx,by,bz);
q := mat((q11,q12,q13),(q21,q22,q23),(q31,q32,q33));

c := a+b;
c := a-b;
c := a cross b;
d := a dot b;
a dot c; b dot c;
q*a;
c:=2*f*a - b/7;
c(0); c(1); c(2);

1/vmod(a);
b/vmod(a);
(a cross b)/(a dot b);
2/3*vmod(a)*a*(a dot c)/(vmod(a cross c));

a := avec(x**2*y**3,log(z+x),13*z-y);
df(a,x);
df(a,x,y);
int(a,x);
exp(a);
log sin b;

a := avec(ax,ay,az);
depend ax,x,y,z; depend ay,x,y,z; depend az,x,y,z;
depend p,x,y,z;
c := grad p;
div c;
delsq p;
div a;
curl a;
delsq a;

depend h1,x,y,z; depend h2,x,y,z; depend h3,x,y,z;
scalefactors(h1,h2,h3);
grad p;
div a;
curl a;
dp1 := delsq p;
dp2 := div grad p;
dp1-dp2;
delsq a;
curl grad p;
grad div a;
div curl a;

% Examples of integration : (1) Volume integrals

getcsystem 'spherical;

% Example 1 : integration of r**n over a sphere

origin := avec(0,0,0);
upperbound := avec(rr,pi,2*pi);
volintegral(r**n,origin,upperbound);

% Substitute n=0 to get the volume of a sphere
sub(n=0,ws);

% Example 2 : volume of a right-circular cone

getcsystem 'cylindrical;
upperbound := avec(pp*z,h,2*pi);
volintorder := avec(2,0,1);      % Integrate in the order : phi, r, z
cone := volintegral(1,origin,upperbound);

% Now we replace P*Z by RR to get the result in the familiar form

let pp*h=rr;
cone := cone;                    % This is the familiar form
clear pp*h;

% Example 3 : line integral to obtain the length of a line of latitude
%             on a sphere

getcsystem 'spherical;

a := avec(0,0,1);            % Function vector is the tangent to the
			     % line of latitude
curve := avec(rr,latitude,phi);   % Path is round a line of latitude

deflineint(a,curve,phi,0,2*pi);

end;
