% polydiv.tst  -*- REDUCE -*-

% Test and demonstration file for enhanced polynomial division
% file polydiv.red.

% F.J.Wright@Maths.QMW.ac.uk, 7 Nov 1995.

% The example from "Computer Algebra" by Davenport, Siret & Tournier,
% first edition, section 2.3.3.

% First check that remainder still works as before.

% Compute the gcd of the polynomials a and b by Euclid's algorithm:
a := aa := x^8 + x^6 - 3x^4 - 3x^3 + 8x^2 + 2x - 5;
b := bb := 3x^6 + 5x^4 - 4x^2 - 9x + 21;
on rational;  off allfac;
c := remainder(a, b);  a := b$  b := c$
c := remainder(a, b);  a := b$  b := c$
c := remainder(a, b);  a := b$  b := c$
c := remainder(a, b);  a := b$  b := c$
c := remainder(a, b);
off rational;

% Repeat using pseudo-remainders, to avoid rational arithmetic:
a := aa;
b := bb;
c := pseudo_remainder(a, b);  a := b$  b := c$
c := pseudo_remainder(a, b);  a := b$  b := c$
c := pseudo_remainder(a, b);  a := b$  b := c$
c := pseudo_remainder(a, b);  a := b$  b := c$
c := pseudo_remainder(a, b);


% Example from Chris Herssens <herc@sulu.luc.ac.be>
% involving algebraic numbers in the coefficient ring
% (for which naive pseudo-division fails in REDUCE):
factor x;
a:=8*(15*sqrt(2)*x**3 + 18*sqrt(2)*x**2 + 10*sqrt(2)*x + 12*sqrt(2) -
   5*x**4 - 6*x**3 - 30*x**2 - 36*x);
b:= - 16320*sqrt(2)*x**3 - 45801*sqrt(2)*x**2 - 50670*sqrt(2)*x -
   26534*sqrt(2) + 15892*x**3 + 70920*x**2 + 86352*x + 24780;

pseudo_remainder(a, b, x);
% Note: We must specify the division variable even though the
% polynomials are apparently univariate:
pseudo_remainder(a, b);

% Confirm that quotient * b + remainder = constant * a:
pseudo_divide(a, b, x);
first ws * b + second ws;
ws / a;                                 % is this constant?
on rationalize;
ws;                                     % yes, it is constant
off rationalize;

on allfac;  remfac x;

procedure test_pseudo_division(a, b, x);
   begin scalar qr, L;
      qr := pseudo_divide(a, b, x);
      L := lcof(b,x);
      %% For versions of REDUCE prior to 3.6 use:
      %% L := if b freeof x then b else lcof(b,x);
      if first qr * b + second qr =
         L^(deg(a,x)-deg(b,x)+1) * a then
         write "Pseudo-division OK"
      else
         write "Pseudo-division failed"
   end;

a := 5x^4 + 4x^3 + 3x^2 + 2x + 1;
test_pseudo_division(a, x, x);
test_pseudo_division(a, x^3, x);
test_pseudo_division(a, x^5, x);
test_pseudo_division(a, x^3 + x, x);
test_pseudo_division(a, 0, x);          % intentional error!
test_pseudo_division(a, 1, x);

test_pseudo_division(5x^3 + 7y^2, 2x - y, x);
test_pseudo_division(5x^3 + 7y^2, 2x - y, y);

end;
