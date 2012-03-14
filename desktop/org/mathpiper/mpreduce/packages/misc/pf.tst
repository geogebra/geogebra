% Tests of the partial fraction module.

% Author: Anthony C. Hearn

off exp;

pf(2/((x+1)^2*(x+2)),x);

pf(x/((x+1)^2*(x+2)^2*(x+3)),x);

pf(x/(x^2-2x-3),x);

pf((10x^2-11x-6)/(x^3-x^2-2x),x);

pf(x^2/((x+1)*(x^2+1)),x);

pf((2x^6-11x^5+37x^4-94x^3+212x^2-471x+661)
 /(x^7-5x^6+5x^5-25x^4+115x^3-63x^2+135x-675),x);

% A harder example.

pf(((2*w**2+2*h**2*l**2*t**2+2*h**2*l**2*qst**2)*z**2-8*h**2*l**2*qst
     *t*z+2*w**2+2*h**2*l**2*t**2+2*h**2*l**2*qst**2)/((w**2+h**4*l**2)
     *((w**2+l**2*t**4+2*l**2*qst**2*t**2+l**2*qst**4)*z**4+(-8*l**2
     *qst*t**3-8*l**2*qst**3*t)*z**3+(2*w**2+2*l**2*t**4+20*l**2*
     qst**2*t**2+2*l**2*qst**4)*z**2+(-8*l**2*qst*t**3-8*l**2*qst**3
     *t)*z+w**2+l**2*t**4+2*l**2*qst**2*t**2+l**2*qst**4))
   -2*h**2/((w**2+h**4*l**2)*((t**2+qst**2+h**2)*z**2-4*qst*t*z+t**2
      +qst**2+h**2)),z);

end;

