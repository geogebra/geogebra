% Examples for the conversion of reaction equations to ordinary
% differential equations.

% Example taken from Feinberg (Chemical Engineering):

   species := {A1,A2,A3,A4,A5};

   reac2ode {  A1 + A4 <> 2A1,       rho, beta,
               A1 + A2 <> A3,        gamma, epsilon,
               A3      <> A2 + A5,   theta, mue};
        
  inputmat;

  outputmat;

% Computation of the classical reaction matrix as difference
% of output and input matrix:

  reactmat := outputmat-inputmat;

% Example with automatic generation of rate constants and automatic
% extraction of species.
 
   species := {};
   reac2ode {  A1 + A4 <> 2A1, 
               A1 + A2 <> A3,
               A3      <> A2 + A5};
 
   on rounded;
   species := {};
   reac2ode {  A1 + A4 <> 2A1, 17.3* 22.4**1.5,
                               0.04* 22.4**1.5 };

end;
