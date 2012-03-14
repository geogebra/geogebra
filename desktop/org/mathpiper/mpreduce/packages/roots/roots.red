module roots;  % Header module for roots package.

% Author: Stanley L. Kameny <stan_kameny@rand.org>.

% Version and Date:  Mod 1.96, 30 March 1995.

% Copyright (c) 1988,1989,1990,1991,1992,1993,1994,1995.
% Stanley L. Kameny.  All Rights Reserved.

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


Comment
  Revisions:
  30 March 95   Mod 1.96 adds the additional capability of solving
                 polynomials by automatically reversing the powers and
                 finding inverse roots first when direct root finding
                 fails to converge.  Examples that caused aborts in the
                 previous versions are now solved.

   2 March 94.  Mod 1.95 adds multroot, a program to solve a nest of
                 polynomials composed of one or more unary polynomials
                 solved by roots or realroots, with others solved by
                 back-substitution.  Multroot can be called directly, or
                 it is called automatically if roots or realroots is
                 given as argument a list of polynomials.

    15 Nov 93.  Error in reporting real roots in the variable rootsreal
                 corrected (in function ALLOUT).

    28 May 93.  Mod 1.94 is adapted to use the binary bigfloats of
                 Reduce 3.5 and later versions.  It will not work with
                 previous versions.  Handling of rational limits in real
                 roots functions corrected.  Allroots is strengthened to
                 handle polynomials with extremely close large roots.
                 Rounding of extremely close roots by realroots made
                 more precise.  Polynomials directly input are evaluated
                 exactly whenever possible, and with rounded evaluation
                 done only when necessary.  Some minor bugs in handling
                 polynomial inputs and limit inputs into real roots
                 functions corrected.  Rounding of complex roots changed
                 to round real and imaginary parts independently.  Root
                 reordering standardizes root order output from
                 allroots.

    9 July 92.  Mod 1.93 improves polynomial handling by using big
                 float for polynomials which the system floating point
                 representation cannot handle.  Also, it derives
                 floating point polynomials from bigfloat versions,
                 never the reverse order (which could induce errors).
                 Improved ability to handle almost degenerate complex
                 polynomials and to find difficult roots more rapidly.
                 Firstroot function now uses allroots.  Dynamic
                 adjustment of maximum iteration limits added to avoid
                 aborts on difficult examples.

    10 Oct 90.  Mod 1.92 uses exact arithmetic in bigfloat polynomial
                 evaluation and all polynomial deflations.  Extremely
                 small real or complex parts of roots can be handled.
                 Polynomials with non-unit initial coefficients and
                 almost degenerate polynomials which require high
                 precision calculations cause no trouble.  REDUCE 3.4
                 required.

    16 May 90.  Mod 1.91 adds capability for handling enormous or
                 infinitesimal coefficients, and uses powergcd logic to
                 speed up root finding when powers are all multiples of
                 a power.  Root separation is improved in difficult
                 cases in which close roots occur on different square-
                 free factors and on real and complex factors.  Better
                 starting points for iteration are found in cases where
                 one or more derivatives vanish at usual initial points.

    11 Feb 90.  Mod 1.90 avoids floating point overflows under extreme
                 conditions.  Files are reorganized to be compatible
                 with REDUCE 3.3 and also be operable under the ROUNDED
                 domain logic being developed for REDUCE 3.4.

     8 Oct 89.  Mod 1.89 avoids floating point under- and overflows
                 which could occur in SLISP.

    21 Aug 89.  Mod 1.88 contains improved precision and accuracy
                 logic and a RATROOT switch for obtaining root output
                 in rational format.  Roots are individually output to
                 the accuracy required to separate them.

    19 Jun 89.  Corrected sign change count error in procedures SCH and
                 SCHINF in isoroots module.
;

create!-package('(roots bfdoer bfdoer2 complxp allroot rootaux),
                '(contrib roots));

exports bfabs, bfnump, bfrlmult, bfsiz, ceillog, cpxp, getprec, im2gf,
        minprec, mkxcl, ncpxp, pmsg, rl2gf, roots, setflbf, setprec,
        trmsg1, trmsg10, trmsg11, trmsg12, trmsg13, trmsg2,
        trmsg3, trmsg4, trmsg6, trmsg7, trmsg8, xclp;

imports abs!:, bfloat, bfp!:, ceiling, cflot, eqcar, log10, preci!:,
        precision, precision1, timbf, trmsg10a, trmsg11a, trmsg12a,
        trmsg13a, trmsg1a, trmsg2a, trmsg3a, trmsg4a, trmsg6a,
        trmsg7a, trmsg8a;

% load!-package 'arith;   % For bootstrapping purposes.

global '(roots!-mod); roots!-mod := "Mod 1.96, 30 March 1995.";

fluid '(!*bftag !:prec!: !*rootmsg !*trroot);

global '(!!nfpd bfz!*);

symbolic procedure roots u; nil;   % To fool loader.

symbolic procedure minprec; if !*bftag then !:prec!: else !!nfpd;

symbolic smacro procedure getprec(); 2+precision 0;
symbolic smacro procedure setprec p; precision1(p-2,t);

symbolic smacro procedure bfsiz p; preci!: bfloat p;

symbolic smacro procedure bfnump p; numberp p or bfp!: p;

symbolic smacro procedure bfrlmult(r,u);
   if atom u then r*u else timbf(bfloat r,u);

symbolic smacro procedure bfabs u; if atom u then abs u else abs!: u;

symbolic smacro procedure rl2gf u;
   if !*bftag then (bfloat u) . bfz!* else (cflot u) . 0.0;

symbolic smacro procedure im2gf u;
   if !*bftag then bfz!* . (bfloat u) else 0.0 . (cflot u);

symbolic smacro procedure xclp a; eqcar(a,'list);

symbolic smacro procedure mkxcl a; if xclp a then a else 'list . a;

symbolic smacro procedure ncpxp p; bfnump p or bfnump cdar p;

symbolic smacro procedure cpxp p; not ncpxp p;

symbolic smacro procedure pmsg a;
   if !*rootmsg and !*trroot then <<write a; terpri()>>;

symbolic smacro procedure ceillog m; ceiling log10 float m;

symbolic smacro procedure setflbf b; !*bftag := b;

symbolic smacro procedure trmsg1 (a, nx);
  if !*trroot then trmsg1a (a, nx);

symbolic smacro procedure trmsg2 (a, xn, px);
  if !*trroot then trmsg2a (a, xn, px);

symbolic smacro procedure trmsg3 (a, xn);
  if !*trroot then trmsg3a (a, xn);

symbolic smacro procedure trmsg4 req;
  if !*trroot then trmsg4a req;

symbolic smacro procedure trmsg6 (k, xn, px);
  if !*trroot then trmsg6a (k, xn, px);

symbolic smacro procedure trmsg7 xn;
  if !*trroot then trmsg7a xn;

symbolic smacro procedure trmsg8;
  if !*trroot then trmsg8a();

symbolic smacro procedure trmsg10 a;
  if !*trroot or !*rootmsg then trmsg10a a;

symbolic smacro procedure trmsg11 (xn, n);
  if !*trroot then trmsg11a (xn, n);

symbolic smacro procedure trmsg12 z;
  if !*trroot then trmsg12a z;

symbolic smacro procedure trmsg13(n,xn,px);
   if !*trroot then trmsg13a(n,xn,px);

endmodule;

end;
