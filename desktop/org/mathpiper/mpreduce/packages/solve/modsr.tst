% Test series for the package MODSR: SOLVE and ROOTS for
% modular polynomials and modular polynomial systems.
% Moduli need not be primes.

on modular;

setmod 8;
m_solve(2x=3);         % {}
m_solve(2x=4);         % {{x=2},{x=6}}
m_solve(x^2-1);        % {{x=1},{x=3},{x=5},{x=7}}
m_solve({x^2-y^3=3});  % {{x=0,y=5}, {x=2,y=1}, {x=4,y=5}, {x=6,y=1}}
m_solve({x^2-y^3=3,x=2});  % {{y=1,x=2}}
m_solve({x=2,x^2-y^3=3});  % {{x=2,y=1}}
m_solve({x1,x2 + 6,2*x1**3 + 4*x2**4 + x3 + 6}); % {{x1=0,x2=2,x3=2}}

setmod 800;
m_solve(x^2-1);
  % {{x=1}, {x=49}, {x=351}, {x=399}, {x=401}, {x=449}, {x=751}, {x=799}}

m_solve({x1 + 51,
282*x1^4 + x2 + 468,
x3 + 1054,
256*x1^2 + 257*x2^4 + 197*x3 + x4 + 653,
255*x1^4 + 40*x2^2 + x5 + 868,
230*x1^4 + 670*x3 + 575*x4^4 + 373*x5^3 + x6 + 1328,
182*x4^4 + 727*x5^2 + 609*x6**4 + x7 + 1032,
623*x1^3 + 614*x2^4 + 463*x3**2 + 365*x4 + 300*x7 + x8 + 1681});

% {{x1=749,x2=50,x3=546,x4=729,x5=77,x6=438,x7=419,x8=399}}

m_solve{x+y=4,x^2+y^2=8};

off modular;

% m_roots has the modulus as its second argument.

m_roots(x^2-1,8);   %  {1,3,5,7}
m_roots(x^3-1,7);   %  {1,2,4}
m_roots(x^3-x,7);   %  {0,1,6}
m_roots((x-1)*(x-2)*(x-3),7); % {1,2,3}
m_roots((x-1)*(x-2)*(x^3-1)*(x-5),7); % {1,2,4,5}
m_roots((x-1)*(x-2)*(x^3-1)*(x-5),1009); % {1,2,5,374,634}
m_roots((x-1)*(x-2)*(x^3-1)*(x-5),1000);
length ws;                               % 35

end;
