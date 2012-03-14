% polynomial Inequality (Example where another system returned {1 <= x})

ineq_solve(  (2*x^2+x-1)/(x-1) >=  (x+1/2)^2 ,x);

ineq_solve({(2*x^2+x-1)/(x-1) >=  (x+1/2)^2, x>0});

ineq_solve({(2*x^2+x-1)/(x-1) >=  (x+1/2)^2, x<-1});


% Systems for determining indices of Jacobi polynomials (Winfried Neun).

reg := 
{2*a - 3>=0, 3>=0, 3>=0, 1>=0, 1>=0, 5>=0, 4>=0, 2*a - 4>=0, 2>=0,
 2>=0, 0>=0, 2*a - 2>=0, k + 1>=0, - 2*a + k - 3>=0, - 2*a + k - 2>=0,
  - 2*a + k>=0, k - 7>=0, 2*a - k + 4>=0, 2*a - k + 5>=0, 2*a - k + 3>=0}$

ineq_solve(reg,{k,a});

reg:=
{a + b - c>=0, a - b + c>=0, - a + b + c>=0, 0>=0, 2>=0,
 2*c - 2>=0, a - b + c>=0, a + b - c>=0, - a + b + c - 2>=0,
 2>=0, 0>=0, 2*b - 2>=0, k + 1>=0, - a - b - c + k>=0,
  - a - b - c + k + 2>=0, - 2*b + k>=0, - 2*c + k>=0, a + b + c - k>=0,
 2*b + 2*c - k - 2>=0, a + b + c - k>=0}$

ineq_solve (reg,{k,a,b,c});

clear reg;

% Example from Richard Liska.

lvars:={a,b,d}$
lfcond := {d>=0,
           b + d>=0,
           2 a - b + d + 2>=0,
            - a + 2 d + 1>=0,
           b>=0,
           2 a - b>=0,
            - a + 2 d>=0,
           b - d>=0,
           2 a - b - d - 2>=0,
            - a + 2 d - 1>=0}$

ineq_solve(lfcond,lvars);

clear lfcond,lvars;

end;
