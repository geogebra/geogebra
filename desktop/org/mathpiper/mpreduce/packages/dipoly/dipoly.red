module dipoly;% Header module for dipoly package .

% Authors : R . Gebauer,A . C . Hearn,H . Kredel,
%
% Significant modifications : H . Melenk .
%
% Modifications :
%
% 14-Dec-1994(HM):  Term order GRADED added .
%
% 17-Sep-1994(HM):  The ideal variables are now declared in the TORDER
%                    statement .  The calling conventions can be still
%                    used,but are removed from the documents .
%
% 12-Sep-1994(HM):  Make the base coefficient arithmatic call subs2 if
%                    the switch *bcsub2 is on .  This is turned on if
%                    there are roots in the coefficient domain .  Without
%                    subs2 the zero detection would be incomplete in
%                    such cases .
%                    Term order MATRIX added .
%
%  5-Jun-1994(HM):  Introduced zero divisor list for the base
%                    coefficients .  These are polynomial variants of let
%                    rules which Groebner has found for the parameters .

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


% For the time being,this contains the smacros that used to be in
% consel,and repeats those in bcoeff .

%----------------------------------------------------------------

% For compatibility with REDUCE 3 . 5 :

fluid'(bczerodivl!* compiled!-orders!* dipevlist!* dipsortmode!* dipsortevcomp!*
 dipvars!* dmode!* dipvars!* dipzero global!-dipvars!* intvdpvars!* olddipsortmode!*
 intvdpvars!* olddipsortmode!* pcount!* secondvalue!* vdpsfsortmode!* vdpmatrix!*
 vdpsortextension!* vdpsortmode!* vdplastvar!* vdpvars!* !*balanced_mod !*bcsubs2
 !*gcd !*grmod!* !*groebdivide !*groebsubs !*groebrm !*gsugar !*trgroeb !*trgroebs
 !*vdpinteger
 !*notestparameters);

global'(groebmonfac vdpprintmax);

%----------------------------------------------------------------

% Constructors and selectors for a distributed polynomial form .

% A distributive polynomial has the following informal syntax :
%
%   <dipoly> ::= dipzero
%                | <exponent vector> . <base coefficient> . <dipoly>

% Vdp2dip modules included .  They could be in a separate package .

create!-package('(dipoly a2dip bcoeff dip2a dipoly1 dipvars
                  expvec torder vdp2dip vdpcom condense dipprint),
                '(contrib dipoly));

put('dipoly,'version,4.1);

% define dipzero='nil;

fluid'(dipzero pi);
% Until we understand how to define something to nil .

smacro procedure dipzero!? u;null u;

smacro procedure diplbc p;
% Distributive polynomial leading base coefficient.
% p is a distributive polynomial . diplbc(p)  returns
% the leading base coefficient of p.
 cadr p;

smacro procedure dipmoncomp(a,e,p);
% Distributive polynomial monomial composition . a is a base
% coefficient,e is an exponent vector and p is a
% distributive polynomial . dipmoncomp( a,e,p)returns a dis-
% tributive polynomial with p as monomial reductum,e as
% exponent vector of the leading monomial and a as leading
% base coefficient.
   e.a.p;

smacro procedure dipevlmon p;
% Distributive polynomial exponent vector leading monomial .
% p is a distributive polynomial . dipevlmon(p)returns the
% exponent vector of the leading monomial of p.
 car p;

smacro procedure dipfmon(a,e);
% Distributive polynomial from monomial . a is a base coefficient
% and e is an exponent vector . dipfmon(a,e)returns a
% distributive polynomial with e as exponent vector and
% a as base coefficient.
 e.a.dipzero;

smacro procedure dipnov p;
% Distributive polynomial number of variables . p is a distributive
% polynomial . dipnov(p)returns a digit,the number of variables
% of the distributive polynomial p.
 length car p;

smacro procedure dipmred p;
% Distributive polynomial reductum . p is a distributive polynomial
% dipmred(p)returns the reductum of the distributive polynomial p,
% a distributive polynomial.
 cddr p;

endmodule;;end;
