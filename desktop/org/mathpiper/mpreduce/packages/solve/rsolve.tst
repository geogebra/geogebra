% Test file for i_solve and r_solve operators.
% Author: F.J.Wright@Maths.QMW.ac.uk
% Version 1.051, 16 Jan 1995

i_solve((x - 10)*(x + 20)*(x - 30)*(x + 40)*(x - 50));
% {x=-20,x=-40,x=50,x=30,x=10}

i_solve(x^4 - 1, x);
% {x=1,x=-1}

i_solve(x^4 + 1);
% {}

r_solve((x^2 - 1)*(x^2 - 9));
% {x=1,x=-3,x=3,x=-1}

r_solve(9x^2 - 1);
%     1      - 1
% {x=---,x=------}
%     3      3

r_solve(9x^2 - 4, x);
%      - 2     2
% {x=------,x=---}
%      3       3

r_solve(9x^2 + 16, x);
% {}

r_solve((9x^2 - 16)*(x^2 - 9), x);
%      - 4              4
% {x=------,x=3,x=-3,x=---}
%      3                3

% First two examples from Loos' paper:
% ===================================
r_solve(6x^4 - 11x^3 - x^2 - 4);
%      - 2
% {x=------,x=2}
%      3

r_solve(2x^3 + 12x^2 + 13x + 15);
% {x=-5}

% Remaining four CORRECTED examples from Loos' paper:
% ==================================================
r_solve(2x^4 - 4x^3 + 3x^2 - 5x - 2);
% {x=2}

r_solve(6x^5 + 11x^4 - x^3 + 5x - 6);
%      - 3     2
% {x=------,x=---}
%      2       3

r_solve(x^5 - 5x^4 + 2x^3 - 25x^2 + 21x + 270);
% {x=3,x=5,x=-2}

r_solve(2x^6 + x^5 - 9x^4 - 6x^3 - 5x^2 - 7x + 6);
%     1
% {x=---,x=-2}
%     2

% Degenerate equations:
% ====================
i_solve 0;
% {}

i_solve(0, x);
% {x=arbint(1)}

r_solve(a = a, x);
% {x=arbrat(2)}

r_solve(x^2 - 1, y);
% {}

% Test of options and multiplicity:
% ================================
i_solve(x^4 - 1, x, noeqs);
% {1,-1}

i_solve((x^4 - 1)^3, x);
% {x=1,x=-1}
root_multiplicities;
% {3,3}

on multiplicities;
i_solve((x^4 - 1)^3, x);
% {x=1,x=1,x=1,x=-1,x=-1,x=-1}
root_multiplicities;
% {}

i_solve((x^4 - 1)^3, x, separate);
% {x=1,x=-1}
root_multiplicities;
% {3,3}
off multiplicities;

i_solve((x^4 - 1)^3, x, multiplicities);
% {x=1,x=1,x=1,x=-1,x=-1,x=-1}
root_multiplicities;
% {}

i_solve((x^4 - 1)^3, x, expand, noeqs);
% {1,1,1,-1,-1,-1}
root_multiplicities;
% {}

i_solve((x^4 - 1)^3, x, together);
% {{x=1,3},{x=-1,3}}
root_multiplicities;
% {}

i_solve((x^4 - 1)^3, x, together, noeqs);
% {{1,3},{-1,3}}
root_multiplicities;
% {}

i_solve((x^4 - 1)^3, x, nomul);
% {x=-1,x=1}
root_multiplicities;
% {}

% Test of error handling:
% ======================
on errcont;
r_solve();
% ***** r/i_solve called with no equations 

r_solve(x^2 - a, x);
%               2
% *****  - a + x  invalid as univariate polynomial over Z

r_solve(x^2 - 1, x, foo);
% ***** foo invalid as optional r/i_solve argument

r_solve({x^2 - 1}, x);
%         2
% ***** {x  - 1} invalid as univariate polynomial over Z

on complex;
i_solve((x-1)*(x-i), x);
%                     2
% *****  - i*x + i + x  - x invalid as univariate polynomial over Z

end$
