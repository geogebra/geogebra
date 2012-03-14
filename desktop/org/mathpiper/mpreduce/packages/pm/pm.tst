% Tests of PM.

% TESTS OF BASIC CONSTRUCTS.

% These names are used with varing numbers of arguments here in this
% test script, so to avoid messages of complaint I tag them as
% variadic.
lisp flag('(f h !@), 'variadic);

operator f, h$

% A "literal" template.

m(f(a),f(a));

% Not literally equal.

m(f(a),f(b));

%Nested operators.

m(f(a,h(b)),f(a,h(b)));

% A "generic" template.

m(f(a,b),f(a,?a));
m(f(a,b),f(?a,?b));

% ??a takes "rest" of arguments.

m(f(a,b),f(??a));

% But ?a does not.

m(f(a,b),f(?a));

% Conditional matches.

m(f(a,b),f(?a,?b _=(?a=?b)));

m(f(a,a),f(?a,?b _=(?a=?b)));

% "plus" is symmetric.

m(a+b+c,c+?a+?b);

%It is also associative.

m(a+b+c,c+?a);

% Note the effect of using multi-generic symbol is different.

m(a+b+c,c+??c);

%Flag h as associative.

flag('(h),'assoc);

m(h(a,b,d,e),h(?a,d,?b));

% Substitution tests.

s(f(a,b),f(a,?b)->?b^2);

s(a+b,a+b->a*b);

% "associativity" is used to group a+b+c in to (a+b) + c.

s(a+b+c,a+b->a*b);

% Only substitute top at top level.

s(a+b+f(a+b),a+b->a*b,inf,0);


% SIMPLE OPERATOR DEFINITIONS.

% Numerical factorial.

operator nfac$

s(nfac(3),{nfac(0)->1,nfac(?x)->?x*nfac(?x-1)},1);

s(nfac(3),{nfac(0)->1,nfac(?x)->?x*nfac(?x-1)},2);

si(nfac(3),{nfac(0)->1,nfac(?x)->?x*nfac(?x-1)});

% General factorial.

operator gamma,fac;

fac(?x _=Natp(?x)) ::- ?x*fac(?x-1);

fac(0)  :- 1;

fac(?x) :- Gamma(?x+1);

fac(3);

fac(3/2);

% Legendre polynomials in ?x of order ?n, ?n a natural number.

operator legp;

legp(?x,0) :- 1;

legp(?x,1) :- ?x;

legp(?x,?n _=natp(?n))
   ::- ((2*?n-1)*?x*legp(?x,?n-1)-(?n-1)*legp(?x,?n-2))/?n;

legp(z,5);

legp(a+b,3);

legp(x,y);


% TESTS OF EXTENSIONS TO BASIC PATTERN MATCHER.

comment *: MSet[?exprn,?val] or ?exprn ::: ?val
	assigns the value ?val to the projection ?exprn in such a way
	as to store explicitly each form of ?exprn requested. *;
 
Nosimp('mset,(t t));

Newtok '((!: !: !: !-) Mset);

infix :::-;

precedence Mset,RSetd;

?exprn :::- ?val ::- (?exprn ::- (?exprn :- ?val ));

scs := sin(?x)^2 + Cos(?x)^2 -> 1;

% The following pattern substitutes the rule sin^2 + cos^2 into a sum of
% such terms.  For 2n terms (ie n sin and n cos) the pattern has a worst
% case complexity of O(n^3).

operator trig,u;

trig(?i) :::- Ap(+, Ar(?i,sin(u(?1))^2+Cos(u(?1))^2));

if si(trig 1,scs) = 1 then write("Pm ok") else Write("PM failed");

if si(trig 10,scs) = 10 then write("Pm ok") else Write("PM failed");

% The next one takes about 70 seconds on an HP 9000/350, calling UNIFY
% 1927 times.

% if si(trig 50,scs) = 50 then write("Pm ok") else Write("PM failed");

% Hypergeometric Function simplification.

newtok '((!#) !#);

flag('(#), 'symmetric);

operator #,@,ghg;

xx := ghg(4,3,@(a,b,c,d),@(d,1+a-b,1+a-c),1);

S(xx,sghg(3));

s(ws,sghg(2));

yy := ghg(3,2,@(a-1,b,c/2),@((a+b)/2,c),1);

S(yy,sghg(1));

yy := ghg(3,2,@(a-1,b,c/2),@(a/2+b/2,c),1);

S(yy,sghg(1));

% Some Ghg theorems.

flag('(@), 'symmetric);

% Watson's Theorem.

SGhg(1) := Ghg(3,2,@(?a,?b,?c),@(?d _=?d=(1+?a+?b)/2,?e _=?e=2*?c),1) -> 
     Gamma(1/2)*Gamma(?c+1/2)*Gamma((1+?a+?b)/2)*Gamma((1-?a-?b)/2+?c)/
     (Gamma((1+?a)/2)*Gamma((1+?b)/2)*Gamma((1-?a)/2+?c)
	*Gamma((1-?b)/2+?c));

% Dixon's theorem.

SGhg(2) := Ghg(3,2,@(?a,?b,?c),@(?d _=?d=1+?a-?b,?e _=?e=1+?a-?c),1) -> 
     Gamma(1+?a/2)*Gamma(1+?a-?b)*Gamma(1+?a-?c)*Gamma(1+?a/2-?b-?c)/
     (Gamma(1+?a)*Gamma(1+?a/2-?b)*Gamma(1+?a/2-?c)*Gamma(1+?a-?b-?c));

SGhg(3) := Ghg(?p,?q,@(?a,??b),@(?a,??c),?z)
		   -> Ghg(?p-1,?q-1,@(??b),@(??c),?z);

SGhg(9) := Ghg(1,0,@(?a),?b,?z )       ->  (1-?z)^(-?a);
SGhg(10) := Ghg(0,0,?a,?b,?z)          ->  E^?z;
SGhg(11) := Ghg(?p,?q,@(??t),@(??b),0) ->  1;

% If one of the bottom parameters is zero or a negative integer the
% hypergeometric functions may be singular, so the presence of a
% functions of this type causes a warning message to be printed.

% Note it seems to have an off by one level spec., so this may need
% changing in future.
%
% Reference: AS 15.1; Slater, Generalized Hypergeometric Functions,
%     Cambridge University Press,1966.

s(Ghg(3,2,@(a,b,c),@(b,c),z),SGhg(3));

si(Ghg(3,2,@(a,b,c),@(b,c),z),{SGhg(3),Sghg(9)});

S(Ghg(3,2,@(a-1,b,c),@(a-b,a-c),1),sghg 2);

end;
