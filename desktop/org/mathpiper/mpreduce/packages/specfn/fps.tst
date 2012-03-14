%       Examples for the algorithmic calculation of formal 
%       Puiseux, Laurent and power series,
%
%       Wolfram Koepf, Freie Universitaet Berlin, Germany
%       (taken from the original paper and adapted to REDUCE 
%        form by Winfried Neun, ZIB Berlin)

% Formal Laurent series

fps(E^x,x);

fps(E^x/(x^3),x);

fps(x * e^(x^4),x);

fps(sin (x + y),x);

simplede (sin x,x); %find a DE for sin

simplede (sin (x)^2,x,w); % DE in w and x   

fps(asin x,x);

fps((asin x)^2,x);

fps(e^(asin x),x);

fps(e^(asinh x),x);

fps((x + sqrt(1+x^2))^A,x);

fps(e^(x^2)*erf x,x);

fps(e^x - 2 e^(-x/2) * cos(sqrt(3) * x/2 -pi/3),x);

% fps(int(e^(-a^2*t^2) * cos(2*x*t),t,0,infinity),x)  % not yet

% fps(4/x * int(e^(t^2)*erf(t),t,0,sqrt(x)/2),x);

fps(sin x * e^x,x);

fps(cos x * e^(2*x),x);

fps(1/(x-x^3),x);

fps(1/(x^2 + 3 x + 2),x);

fps(x/(1-x-x^2),x);

% Logarithmic singularities and Puisieux series

fps(sin sqrt x,x);

fps(((1 + sqrt x)/x)^(1/3),x);

fps(asech x,x);

% some more (Wolfram Koepf, priv. comm.)

fps((1+x)^alpha,x);

fps((1+sqrt(1+x))^beta,x);

fps(sin(x)^2+cos(x)^2,x);

fps(sin(x)^2*cos(x)^2,x);

fps(sin(x)*cos(x^2),x);

fps((x-1)^(-1),x);

fps(atan(x+y),x);

fps((1-x^5)^6,x);

fps(asec x,x);

fps(besseli(0,x),x);
fps(besseli(1,x),x);

fps(exp(x^(1/3)),x);
fps(log(1-x),x);
fps(exp x*sinh x,x);
fps(atan x,x);
fps(sin x+sinh x,x);
fps(sin x*sinh x,x);
fps(int(erf(x),x),x);
fps(sqrt(2-x),x);
fps(sqrt(1+x)+sqrt(1-x),x);
fps(exp(a+b*x)*exp(c+d*x),x);
fps(1/cos(asin x),x);
fps(sqrt(1-x^2)+x*asin x,x);
fps(sqrt(1-sqrt(x)),x);
fps(cos(n*acos x),x);
fps(cos x+I*sin x,x);
fps(cos(3*asinh x),x);
fps(cos(n*asinh x),x);
fps(sin(n*log(x+sqrt(1+x^2))),x);
fps(sqrt(1+x^2)*asinh x-x,x);

fps(int(erf(x)/x,x),x);
fps(asin(x)^2/x^4,x);


% we had problems here:

fps(cos(asin x),x);
fps(sinh(log x),x);
fps(atan(cot x),x);
 
% we can cure this one by defining the limit:

let limit(atan(cot ~x),x,0) => pi/2;
fps(atan(cot x),x);

fps(exp(nnn*x)*cos(mmm*x),x);
fps(sqrt(2-x^2),x);
fps(ci x,x);
fps(log(1-2*x*y+x^2),x);

FPS(sin x,x,pi);

% This one takes ages :

%fps(acos(cos(x)),x);

fps_search_depth := 7; % does not find aa DE with the default
fps(sin(x^(1/3)),x);

end;

