% load tri;

global '(textest!*);

symbolic procedure texexa(code);
   begin
     prin2 "\TRIexa{"; prin2 textest!*;
     if !*TeXindent then prin2 "}{TeXindent}{" else
     if !*TeXbreak  then prin2 "}{TeXBreak}{" else
     if !*TeX then prin2 "TeX" else prin2 "}{---}{";
     if !*TeXbreak then prin2 tolerance!* else prin2 "---";
     prin2 "}{"; prin2 code; prin2 "}"; terpri()
   end;

algebraic procedure exa(expression,code);
   begin symbolic texexa code; return expression end;

% ----------------------------------------------------------------------
% Examples from the Integrator Test File
% ----------------------------------------------------------------------

symbolic(textest!*:="Integration");

texsetbreak(120,1000);

on texindent;

off echo;

% out "log/tritst.tex";

exa(int(1+x+x**2,x),
       "int(1+x+x**2,x);");
exa(int(x**2*(2*x**2+x)**2,x),
       "int(x**2*(2*x**2+x)**2,x);");
exa(int(x*(x**2+2*x+1),x),
       "int(x*(x**2+2*x+1),x);");
exa(int(1/x,x),
       "int(1/x,x);");
exa(int((x+1)**3/(x-1)**4,x),
       "int((x+1)**3/(x-1)**4,x);");
exa(int(1/(x*(x-1)*(x+1)**2),x),
       "int(1/(x*(x-1)*(x+1)**2),x);");
exa(int((a*x+b)/((x-p)*(x-q)),x),
       "int((a*x+b)/((x-p)*(x-q)),x);");
exa(int(1/(a*x**2+b*x+c),x),
       "int(1/(a*x**2+b*x+c),x);");
exa(int((a*x+b)/(1+x**2),x),
       "int((a*x+b)/(1+x**2),x);");
exa(int(1/(x**2-2*x+3),x),
       "int(1/(x**2-2*x+3),x);");

% Rational function examples from Hardy, Pure Mathematics, p 253 et seq.

exa(int(1/((x-1)*(x**2+1))**2,x),
       "int(1/((x-1)*(x**2+1))**2,x);");
exa(int(x/((x-a)*(x-b)*(x-c)),x),
       "int(x/((x-a)*(x-b)*(x-c)),x);");
exa(int(x/((x**2+a**2)*(x**2+b**2)),x),
       "int(x/((x**2+a**2)*(x**2+b**2)),x);");
exa(int(x**2/((x**2+a**2)*(x**2+b**2)),x),
       "int(x**2/((x**2+a**2)*(x**2+b**2)),x);");
exa(int(x/((x-1)*(x**2+1)),x),
       "int(x/((x-1)*(x**2+1)),x);");
exa(int(x/(1+x**3),x),
       "int(x/(1+x**3),x);");
exa(int(x**3/((x-1)**2*(x**3+1)),x),
       "int(x**3/((x-1)**2*(x**3+1)),x);");
exa(int(1/(1+x**4),x),
       "int(1/(1+x**4),x);");
exa(int(x**2/(1+x**4),x),
       "int(x**2/(1+x**4),x);");
exa(int(1/(1+x**2+x**4),x),
       "int(1/(1+x**2+x**4),x);");

exa(int(sin x**2/x,x),
       "int(sin x**2/x,x);");
exa(int(x*cos(xi/sin(x))*cos(x)/sin(x)**2,x),
       "int(x*cos(xi/sin(x))*cos(x)/sin(x)**2,x);");

% shut "log/tritst.tex";

off tex;

end;
