module harmonic; % Solid & spherical harmonics.

% Author: Matthew Rebbeck, ZIB.
% Advisor: Rene' Grognard.

% Redistribution and use in source and binary forms, with or without
% modification, are permitted provided that the following conditions are met:
%
%    * Redistributions of source code must retain the relevant copyright
%      notice, this list of conditions and the following disclaimer.
%    * Redistributions in binary form must reproduce the above copyright
%      notice, this list of conditions and the following disclaimer in the
%      documentation and/or other materials provided with the distribution.
%
% THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
% AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
% THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
% PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNERS OR
% CONTRIBUTORS
% BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
% CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
% SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
% INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
% CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
% ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
% POSSIBILITY OF SUCH DAMAGE.
%


% Date:   March 1994

% Version 0 (experimental)

% Solid Harmonics of order n (Laplace polynomials)
% are homogeneous polynomials of degree n in x,y,z
% which are solutions of Laplace equation:-

%        df(P,x,2) + df(P,y,2) + df(P,z,2) = 0.

% There are 2*n+1 independent such polynomials for any given n >=0
% and with:-

%       w!0 = z, w!+ = i*(x-i*y)/2, w!- = i*(x+i*y)/2,

% they are given by the Fourier integral:-

% S(n,m,w!-,w!0,w!+) =

%       (1/(2*pi)) *
%       for u:=-pi:pi integrate (w!0 + w!+ * exp(i*u) + w!- *
%           exp(-i*u))^n * exp(i*m*u) * du;

% which is obviously zero if |m| > n since then all terms in the
% expanded integrand contain the factor exp(i*k*u) with k neq 0,

% S(n,m,x,y,z) is proportional to
%     r^n * Legendre(n,m,cos theta) * exp(i*phi)

% Let r2 = x^2 + y^2 + z^2 and r = sqrt(r2).

% The spherical harmonics are simply the restriction of the solid
% harmonics to the surface of the unit sphere and the set of all
% spherical harmonics {n >=0; -n <= m =< n} form a complete orthogonal
% basis on it, i.e. <n,m|n',m'> = Kronecker_delta(n,n') *
% Kronecker_delta(m,m') using <...|...> to designate the scalar product
% of functions over the spherical surface.

% The coefficients of the solid harmonics are normalised in what
% follows to yield an ortho-normal system of spherical harmonics.

% Given their polynomial nature, there are many recursions formulae
% for the solid harmonics and any recursion valid for Legendre functions
% can be 'translated' into solid harmonics. However the direct proof is
% usually far simpler using Laplace's definition.

% It is also clear that all differentiations of solid harmonics are
% trivial, qua polynomials.

% Some substantial reduction in the symbolic form would occur if one
% maintained throughout the recursions the symbol r2 (r cannot occur
% as it is not rational in x,y,z). Formally the solid harmonics appear
% in this guise as more compact polynomials in (x,y,z,r2).

% Only two recursions are needed:-

% (i) along the diagonal (n,n);

% (ii) along a line of constant n: (m,m),(m+1,m),...,(n,m).

% Numerically these recursions are stable.

% For m < 0 one has:-

%       S(n,m,x,y,z) = (-1)^m * S(n,-m,x,-y,z);

algebraic procedure SolidHarmonicY(n,m,x,y,z,r2);
begin scalar mp, v, Y0, Y1, Y2;

 if not (fixp(n) and fixp(m)) then  return
    rederr " SolidHarmonicY : n and m must be integers";
 if (n < 0) then return 0;
 mp := abs(m);
 if (n < mp ) then return 0;
 Y0 := 1/sqrt(4*Pi);
 if (n = 0) then return Y0;
 if (mp > 0) then
 << if m > 0 then v:=x+i*y else v:=x-i*y;
  for k:=1:mp do Y0 := - sqrt((2*k+1)/(2*k))*v*Y0;
  if (n > mp) then <<
   k := mp + 1;
   Y1 := Y0;
   Y0 := z*sqrt(2*k+1)*Y1;
   if (n > mp + 1) then for k:=mp+2:n do <<
            Y2 := Y1;
            Y1 := Y0;
            Y0 := z*sqrt((4*k*k-1)/(k*k-mp*mp))*Y1
                   -r2*sqrt(((2*k+1)*(k-mp-1)*(k+mp-1))/
                   ((2*k-3)*(k*k-mp*mp)))*Y2 >>;
                  >>;
 >> else << Y1 := Y0;
            Y0 := z*sqrt(3)*Y1;
            if n > 1 then for k:=2:n do <<
                   Y2 := Y1;
                   Y1 := Y0;
                   Y0 := ( z*sqrt(4*k*k-1)*Y1 - r2*(k-1)*
                         sqrt((2*k+1)/(2*k-3))*Y2)/k >>;
         >>;
 if m < 0 and not evenp mp then Y0 := - Y0;
 return Y0
end;

algebraic procedure SphericalHarmonicY(n,m,theta,phi);
        SolidHarmonicY(n,m,sin(theta)*cos(phi),
                sin(theta)*sin(phi),cos(theta),1)$

endmodule;

end;


