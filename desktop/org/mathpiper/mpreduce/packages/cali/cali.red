module cali;

% Author H.-G. Graebe | Univ. Leipzig
% graebe@informatik.uni-leipzig.de

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


% terpri(); write "CALI 2.2.1 Last update June 22, 1995"; terpri();

COMMENT

              #########################
              ####                 ####
              ####  HEADER MODULE  ####
              ####                 ####
              #########################

This is the header module of the package CALI, a package for
computational commutative algebra.

Author :        H.-G. Graebe
                Univ. Leipzig
                Institut fuer Informatik
                Augustusplatz 10 - 11
                D - 04109 Leipzig
                Germany

        email : graebe@informatik.uni-leipzig.de


Version : 2.2.1, finished at June 22, 1995.

See cali.chg for change's documentation.

Please send all Comments, bugs, hints, wishes, criticisms etc. to the
above email address.


Abstract :

This package contains algorithms for computations in commutative
algebra closely related to the Groebner algorithm for ideals and
modules. There are facilities for local computations, using a modern
implementation of Mora's standard basis algorithm, that works for
arbitrary term orders. This reflects the full analogy between modules
over local rings and homogeneous (in fact H-local) modules over
polynomial rings.

CALI extends also the term order facilities of the REDUCE internal
groebner package, defining term orders by degree vector lists, and
the rigid implementation of the sugar idea, by a more flexible ecart
vector, in particular useful for local computations. Version 2.2. has
also a common view on normal forms for noetherian and non-noetherian
term orders.

The package was designed mainly as a symbolic mode programming
environment extending the build-in facilities of REDUCE for the
computational approach to problems arising naturally in commutative
algebra. An algebraic mode interface allows to access (in a more
rigid frame) all important features implemented symbolically.

As main topics CALI contains facilities for

-- defining rings, ideals and modules,

-- computing Groebner bases and local standard bases,

-- computing syzygies, resolutions and (graded) Betti numbers,

-- computing (also weighted) Hilbert series, multiplicities,
        independent sets, dimensions,

-- computing normal forms and representations,

-- computing sums, products, intersections, elimination ideals etc.,

-- primality tests, computation of radicals, unmixed radicals,
        equidimensional parts, primary decompositions etc. of ideals
        and modules,

-- advanced applications of Groebner bases (blowup, associated graded
        ring, analytic spread, symmetric algebra, monomial curves),

-- applications of linear algebra techniques to zerodimensional
        ideals, as e.g. the FGLM change of term orders, border bases
        and affine and projective ideals of sets of points,

-- splitting polynomial systems of equations mixing factorization and
        Groebner algorithm, triangular systems, and different versions
        of the extended Groebner factorizer.

Reduce version required :

The program was tested under v. 3.4 - 3.6.
(I had some trouble with the module dualbases under 3.4.1)

Relevant publications :

See the bibliography in the manual.


Key words :

Groebner algorithm for ideals and modules, local standard bases,
Groebner factorizer, extended Groebner factorizer, triangular systems,
normal forms, ideal and module operations, Hilbert series, independent
sets, dual bases, border bases, affine and projective sets of points,
free resolution, constructive commutative algebra, primality test,
radical, unmixed radical, equidimensional part, primary decomposition,
blowup, associated graded ring, analytic spread, symmetric algebra,
monomial curves.



To be done :

eo(vars) : test cali!=basering for eliminationorder according to vars
        -> eliminate

Remind :

Never "put" variables, that are subject to rebounding via "where" !

end comment;

create!-package( '(
        cali            % This header module.
        bcsf            % Base coeff. arithmetics.
        ring            % Base ring and monomial arithmetics.
        mo              % Monomial arithmetic.
        dpoly           % Distr. polynomial (and vector) arithmetics.
        bas             % Polynomial lists.
        dpmat           % dpmat's arithmetic.
        red             % Normal form algorithms and related topics.
        groeb           % Groebner algorithm and related topics.
        groebf          % Groebner factorizer and extensions.
        matop           % Module operations on dpmats.
        quot            % Different quotients.
        moid            % Lead. term ideal algorithms.
        hf              % Hilbert series.
        res             % Resolutions.
        intf            % Interface to algebraic mode.
        odim            % Alg. for zerodimensional ideals and
                        %       modules.
        prime           % Primality test, radical, and primary
                        % decomposition.
        scripts         % Advanced applications, inspired by the
                        %       scripts of Bayer/Stillman.
        calimat         % CALI's extension of the matrix package.
        lf              % The dual bases approach (FGLM etc.).
        triang          % (Zero dimensional) triangular systems.
        ),'(contrib cali));

load!-package 'matrix;

fluid '(
        cali!=basering  % see rings
        cali!=degrees   % see mons in rings
        cali!=monset    % see groeb
        );

                        % Default :
switch
        hardzerotest,   % (off) see bcsf, try simp for each zerotest.
        red_total,      % (on)  see red, do total reductions.
        bcsimp,         % (on)  see red, cancel coefficient's gcd.
        noetherian,     % (on)  see interf, test term orders and
                        %                choose non local algorithms.
        factorprimes,   % (on)  see primes, invoke groebfactor during
                        %               prime decomposition.
        factorunits,    % (off) see groeb, try to remove units from
                        %               polynomials by factorization.
        detectunits,    % (off) see groeb, detect generators of the form
                        %               monomial * unit.
        lexefgb;        % (off) see groebf, invoke the extended
                        %               Groebner factorizer with pure
                        %               lex zerosolve.

% The first initialization :

put('cali,'trace,0);    % No tracing.
% linelength 79;          % This is much more convenient than 80.
                          % However, it causes problems in window sys.

% The new tracing. We hope that this shape will easily interface to a
% forthcoming general trace utility.

symbolic operator setcalitrace;
symbolic procedure setcalitrace(n);
% Set trace intensity.
        put('cali,'trace,n);

symbolic operator setcaliprintterms;
symbolic procedure setcaliprintterms(n);
% Set number of terms to be printed in intermediate output.
  if n<=0 then typerr(n,"number of terms to be printed")
  else put('cali,'printterms,n);

symbolic operator clearcaliprintterms;
symbolic procedure clearcaliprintterms;
% Set intermediate output printing to "all".
  << remprop('cali,'printterms); write"Term print bound cleared";
     terpri();
  >>;

symbolic procedure cali_trace();
% Get the trace intensity.
        get('cali,'trace);

% ---- Some useful things, probably implemented also elsewhere
% ---- in the system.

% symbolic procedure first x; car x;
% symbolic procedure second x; cadr x;
% symbolic procedure third x; caddr x;

symbolic procedure strcat l;
% Concatenate the items in the list l to a string.
  begin scalar u;
  u:=for each x in l join explode x;
  while memq('!!,u) do u:=delete('!!,u);
  while memq('!",u) do u:=delete('!",u);
  return compress append(append('(!"),u),'(!"));
  end;

symbolic procedure numberlistp l;
% l is a list of numbers.
  if null l then t
  else fixp car l and numberlistp cdr l;

symbolic procedure merge(l1,l2,fn);
% Returns the (physical) merge of the two sorted lists l1 and l2.
  if null l1 then l2
  else if null l2 then l1
  else if apply2(fn,car l1,car l2) then rplacd(l1,merge(cdr l1,l2,fn))
  else rplacd(l2,merge(l1,cdr l2,fn));

symbolic procedure listexpand(fn,l); eval expand(l,fn);

symbolic procedure listtest(a,b,f);
% Return the first u in a s.th. f(u,b) or nil.
  if null a then nil
  else if apply2(f,car a,b) then if car a=nil then t else car a
  else listtest(cdr a,b,f);

symbolic procedure listminimize(a,f);
% Returns a minimal list b such that for all v in a ex. u in b such
% that f(u,v). The elements are in the same order as in a.
  if null a then nil else reverse cali!=min(nil,a,f);

symbolic procedure cali!=min(b,a,f);
  if null a then b
  else if listtest(b,car a,f) or listtest(cdr a,car a,f) then
        cali!=min(b,cdr a,f)
  else cali!=min(car a . b,cdr a,f);

% symbolic procedure makelist u; 'list . u;

symbolic procedure subsetp(u,v);
% true :<=> u \subset v
  if null u then t else member(car u,v) and subsetp(cdr u,v);

symbolic procedure disjoint(a,b);
  if null a then t else not member(car a,b) and disjoint(cdr a,b);

symbolic procedure print_lf u;
% Line feed after about 70 characters.
  <<if posn()>69 then <<terpri();terpri()>>; prin2 u>>;

symbolic procedure cali_choose(m,k);
% Returns the list of k-subsets of m.
  if (length m < k) then nil
  else if k=1 then for each x in m collect list x
  else nconc(
        for each x in cali_choose(cdr m,k-1) collect (car m . x),
        cali_choose(cdr m,k));

fluid '(cali!:varindex!!);
cali!:varindex!! := 0;

symbolic procedure make_cali_varname();
  <<cali!:varindex!! := cali!:varindex!!+1;
    mkid('cali!:var,cali!:varindex!!)>>;

endmodule;

end;
