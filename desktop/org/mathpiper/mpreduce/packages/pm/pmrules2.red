module pmrules2;  % More rules for PM Pattern matcher.

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

% NOTE:  This module is supplied for information purposes only.  It
%        still needs work to run properly in REDUCE 3.4.  However,
%        the examples are sufficiently useful that the module is
%        included in the distribution.

load!-package 'pmrules; % This loads both PM and PMRULES.

algebraic;

% Absolute Value Function.

% Use the name XAbs to avoid problems with abs.

xabs(?a*?b) ::- xabs(?a)*xabs(?b);
xabs(?a/?b)  ::- xabs(?a)/xabs(?b);
xabs(?a^?n)  ::- xabs(?a)^?n;
xabs(?x _=posp(?x)) :- ?x;
xabs(?x _=posp(-?x)) :- -?x;


% XComb -generalization of Comb to general real arguments.

% Author: Paul C Abbott, Univ. of Western Australia, Nov 85.

comb(?a,?b)::- gamma(?a+1)/gamma(?b+1)/gamma(?a-?b+1);
comb(?a,?n _=natp(?n+1))::- (-1)^?n *poc(-?a,?n)/fctl(?n);


% Parity testing simplification.

% Author: J Gottschalk, Univ. of Western Australia, Mar 85.

% SMP already realizes that Evenp[x]:1 => Intp[x]:1 ;

% Use the name XEvenp to avoid probles with evenp.

XEvenp((??x _=XEvenp(??x))+(?y _=XEvenp(?y))) :- t;
XEvenp((??x _= oddp(??x))+(?y _= oddp(?y))) :- t;
XEvenp((??x _= oddp(??x))+(?y _=XEvenp(?y))) :- 0;
XEvenp((??x _= intp(??x)) * (?y _=XEvenp(?y))) :- t;
XEvenp((??x _= oddp(??x)) * (?y _= oddp(?y))) :- 0;
XEvenp(( ?x _= XEvenp(?x))^(?y _= intp(?y))) :- t;
XEvenp(( ?x _=  oddp(?x))^(?y _= intp(?y))) :- 0;

oddp((??x _= oddp(??x))+(?y _= oddp(?y))) :- 0;
oddp((??x _=XEvenp(??x))+(?y _=XEvenp(?y))) :- 0;
oddp((??x _= oddp(??x))+(?y _=XEvenp(?y))) :- t;
oddp((??x _= intp(??x)) * (?y _=XEvenp(?y))) :- 0;
oddp((??x _= oddp(??x)) * (?y _= oddp(?y))) :- t;
oddp(( ?x _= XEvenp(?x))^(?y _= intp(?y))) :- 0;
oddp(( ?x _=  oddp(?x))^(?y _= intp(?y))) :- t;


% Legendre polynomials in ?x of order ?n, ?n a natural number.

operator legp;

legp(?x,0) :- 1;

legp(?x,1) :- ?x;

legp(?x,?n _=natp(?n))
   ::- ((2*?n-1)*?x*legp(?x,?n-1)-(?n-1)*legp(?x,?n-2))/?n;

% Using Mset.

operator mlegp;

mlegp(?x,0) :- 1;
mlegp(?x,1) :- ?x;
mlegp(?x,?n _=natp(?n))
   ::- ((2*?n-1)*?x*mlegp(?x,?n-1)-(?n-1)*mlegp(?x,?n-2))/?n;


comment * Generalized hypergeometric functions: elementary identities *;

% Author: John Gottschalk, Univ. of Western Australia, Sep 84.

comment P: XWarning is automatically loaded. ;
 ;
% Keywords:: hypergeometric: generalized hypergeometric functions:
%       Ghg: sums: summation: gauss: vandermonde: saalschutz: whipple:
%       kummer: watson: dixon: dougall.

comment This file contains assignments and substitutions for rewriting
        special generalized hypergeometric functions in terms of Gamma
        and Polygamma functions. ;

comment These identities are from Appendix 3 of Slater "Generalized
        Hypergeometric Functions", Cambridge University Press,1966.
        Those that have been omitted may be simply derived form other
        results, for example equation III.25 is is a result of equation
        III.11. ;

flag('(#), 'symmetric);

% Some commonly used theorems can be called by the following names:

intdiff      ::-  sghg(0,{1,2,3,4});
gauss        ::-  sghg(0,5);
vandermonde  ::-  sghg(0,6);
saalschutz   ::-  sghg(0,7);
whipple      ::-  sghg(0,8);
kummer       ::-  sghg(0,9);
watson       ::-  sghg(0,10);
dixon        ::-  sghg(0,11);
dougall      ::-  sghg(0,12);
nearlypoised ::-  sghg(0,{13,14,15});
wellpoised   ::-  flat({sghg(0,{16,17,18,19}),dixon,dougall,kummer});

comment The patterns are written with a "=" sign as the pattern matcher
        in version 1.5.0. will return a 0 for matches like
        Match[a/2+1/2,(a+1)/2], but use of Eq gets around this problem;

comment  Reduction for 2F1(1,a:a+m:-1) when m is a natural number. ;

%SGhg(0,1) :- Ghg(2,1,#(1,?a),#(?b _=Natp(?b-?a)),-1) ->
%     (-1)^(?b-?a-1) *Gamma(?b)/
%     (2*Gamma(?a)) *Sum((-1)^n/(Gamma(n+1) *Gamma(?b-?a-n))
%     * (Psi(?b/2-n/2)-Psi(?b/2-n/2-1/2)),{n,0,?b-?a-1}) ;

%SGhg(0,2) :- Ghg(?p _=?p>2,?p-1,#(1,??a),
%                #(??b) _=Union({??b})-Union({??a}) = {1},1) -->
%     -Psi(?p-2,{??a}(1)) * (-1)^?p * ({??a}(1))^(?p-1)/Fctl(?p-2);

%SGhg(0,3) :- Ghg(?p _=?p>2,?p-1,#(1,??a),
%            #(??b) _=Union({??b})-Union({??a}) = {1},-1) -->
%     (Psi(?p-2,({??a}(1))/2+1/2)-Psi(?p-2,({??a}(1))/2)) * (-1)^?p
%     * ({??a}(1))^(?p-1) *2^(1-?p)/Fctl(?p-2);

sghg(0,4) :- ghg(3,2,#(1,?a,?b),#(?a+1,?b+1),1 _=symbwt(?b~=?a)) ->
     ?a *?b/(?a-?b) * (psi(?a)-psi(?b));

comment  Gauss's theorem ;
sghg(0,5) :- ghg(2,1,#(?a,?b),#(?c),1) ->
     gamma(?c) *gamma(?c-?a-?b)/(gamma(?c-?a) *gamma(?c-?b));

comment  Vandermonde's theorem ;
sghg(0,6) :- ghg(2,1,#(?a,?n _=natp(1-?n)),#(?c),1)
           -> poc(?c-?a,-?n)/poc(?c,-?n);

comment  Saalschutz's theorem ;
sghg(0,7) :- ghg(3,2,#(?a,?b,?n _=natp(1-?n)),
                     #(?c,?d _=?d=?a+?b+?n-?c+1),1) ->
     gamma(?c-?a-?n) *gamma(?c-?b-?n) *gamma(?c) *gamma(?c-?a-?b)/
     (gamma(?c-?a) *gamma(?c-?b) *gamma(?c-?n) *gamma(?c-?a-?b-?n));

comment  Whipple's theorem ;
sghg(0,8) :- ghg(3,2,#(?a,?b _=?b=1-?a,?c),#(?d,?e) _=?d+?e=1+2*?c,1) ->
     pi *2^(1-2*?c) *gamma(?d) *gamma(?e)/
     (gamma((?a+?e)/2) *gamma((?a+?d)/2) *gamma((?d+?e)/2)
      *gamma((?b+?d)/2));

comment  Kummer's theorem ;
sghg(0,9) :- ghg(2,1,#(?a,?b),#(?c _=?c=1+?a-?b),-1) ->
    gamma(1+?a-?b) *gamma(1+?a/2)/(gamma(1+?a) *gamma(1+?a/2-?b)) ;

comment  Watson's Theorem ;
sghg(0,10) :- ghg(3,2,#(?a,?b,?c),#(?d _=?d=(1+?a+?b)/2,?e _=?e=2*?c),1)->
   gamma(1/2) *gamma(?c+1/2) *gamma((1+?a+?b)/2) *gamma((1-?a-?b)/2+?c)/
   (gamma((1+?a)/2) *gamma((1+?b)/2) *gamma((1-?a)/2+?c)
    *gamma((1-?b)/2+?c));

comment  Dixon's theorem ;
sghg(0,11):- ghg(3,2,#(?a,?b,?c),#(?d _=?d=1+?a-?b,?e _=?e=1+?a-?c),1) ->
     gamma(1+?a/2) *gamma(1+?a-?b)*gamma(1+?a-?c)*gamma(1+?a/2-?b-?c)/
     (gamma(1+?a)*gamma(1+?a/2-?b)*gamma(1+?a/2-?c)*gamma(1+?a-?b-?c));

comment  Dougall's theorem ;
sghg(0,12) :- ghg(7,6,#(?a,?f _=?f=1+?a/2,?b,?c,?d,?e,?n _=natp(1-?n) &
     1+2*?a-?b-?c-?d-?e-?n=0),
     #(?g _=?g=?a/2,?h _=?h=1+?a-?b,?i _=?i=1+?a-?c,?j _=?j=1+?a-?d,
                                    ?k _=?k=1+?a-?e,?l _=?l=1+?a-?n),1) ->
     poc(1+?a,-?n) *poc(1+?a-?b-?c,-?n) *poc(1+?a-?b-?d,-?n)
        *poc(1+?a-?c-?d,-?n)/
     (poc(1+?a-?b,-?n) *poc(1+?a-?c,-?n) *poc(1+?a-?d,-?n)
        *poc(1+?a-?b-?c-?d,-?n));

comment  Appendix III.15 in Slater's book ;
sghg(0,13) :- ghg(3,2,#(?a,?c _=?c=1+?a/2,?n _=natp(1-?n)),
                      #(?d _=?d=?a/2,?b),1) ->
                 (?b-?a-1+?n) *poc(?b-?a,-?n-1)/poc(?b,-?n);

comment  Appendix III.16 in Slater's book ;
sghg(0,14) :- ghg(3,2,#(?a,?b,?n _=natp(1-?n)),
                          #(?c _=?c=1+?a-?b,?d _=?d=1+2*?b+?n),1) ->
     poc(?a-2*?b,-?n) *poc(1+?a/2-?b,-?n) *poc(-?b,-?n)/
     (poc(1+?a-?b,-?n) *poc(?a/2-?b,-?n) *poc(-2*?b,-?n));

comment  Appendix III.17 in Slater's book ;
sghg(0,15) :- ghg(4,3,#(?a,?c _=?c=1+?a/2,?b,?n _=natp(1-?n)),
          #(?d _=?d=?a/2,?e _=?e=1+?a-?b,?f _=?f=1+2*?b+?n),1) ->
     poc(?a-2*?b,-?n) *poc(-?b,-?n)/(poc(1+?a-?b,-?n) *poc(-2*?b,-?n));

comment  Appendix III.19 in Slater's book ;
sghg(0,16) :- ghg(7,6,#(?a,?b,?c _=?c=1+?a/2,?d _=?d=1/2+?b,
  ?e _=?e=?a-2*?b,?f _=?f=1+2*?a-2*?b-?n,?n _=natp(1-?n)),
  #(?g _=?g=?a/2,?h _=?h=1+?a-?b,?i _=?i=?a+1/2-?b,?j _=?j=1+2*?b,
    ?k _=?k=2*?b-?a+?n,?l _=?l=1+?a-?n),1) ->
  poc(1+?a,-?n) *poc(1+2*?a-4*?b,-?n)/(poc(1+?a-2*?b,-?n)
      *poc(1+2*?a-2*?b,-?n));

comment  Appendix III.20 in Slater's book ;
sghg(0,17) :- ghg(4,3,#(?a,?b,?n _=natp(1-?n),?c _=?c=1/2+?a),
          #(?d _=?d=?b/2+?n/2,?e _=?e=?b/2+?n/2+1/2,?f _=?f=1+2*?a),1) ->
     poc(?b+?n-2*?a,-?n)/poc(?b+?n,-?n);

comment  Appendix III.10 in Slater's book ;
sghg(0,18) :- ghg(4,3,#(?a,?b,?c,?d _=?d=1+?a/2),
  #(?e _=?e=?a/2,?f _=?f=1+?a-?b,?g _=?g=1+?a-?c),-1) ->
     gamma(1+?a-?b) *gamma(1+?a-?c)/(gamma(1+?a) *gamma(1+?a-?b-?c));

comment  Appendix III.12 in Slater's book ;
sghg(0,19) :- ghg(5,4,#(?a,?b,?c,?d,?e _=?e=1+?a/2),
  #(?f _=?f=?a/2,?g _=?g=1+?a-?b,?h _=?h=1+?a-?c,?i _=?i=1+?a-?d),1) ->
  gamma(1+?a-?b) *gamma(1+?a-?c) *gamma(1+?a-?d) *gamma(1+?a-?b-?c-?d)/
  (gamma(1+?a)*gamma(1+?a-?b-?c)*gamma(1+?a-?b-?d)*gamma(1+?a-?c-?d));

comment  The ?y _=?y=?x is needed to overcome a bug. It should be removed
  later. ;

ghg(?p,?q,#(?x,??a),#(?y _=?y = ?x & ~natp(1-?y),??b),?z) ::-
                                 ghg(?p-1,?q-1,#(??a),#(??b),?z);
ghg(?p,1,#(?x,??a),#(?y _=?y = ?x & ~natp(1-?y)),?z)
     :- ghg(?p-1,0,#(??a),#(),?z);
ghg(1,?q,#(?x),#(?y _=?y = ?x & ~natp(1-?y),??b),?z)
     :- ghg(0,?q-1,#(),#(??b),?z);
ghg(1,1,#(?x),#(?y _=?y = ?x & ~natp(1-?y)),?z)      :- e^?z;

ghg(1,0,#(?a),?b,?z )         :-  (1-?z)^(-?a);
ghg(0,0,?a,?b,?z)             :-  e^?z;
%Ghg(?p,?q,#(0,??a),#(??b) _=~In(?1 _=Natp(1-?1),{??b},2),?z) :-  1;
ghg(?p,?q,#(??t),#(??b),0)    :-  1;

comment  If one of the bottom parameters is zero or a negative integer
         the hypergeometric functions may be singular, so the presence
         of a functions of this type causes a warning message to
         be printed. ;
comment Note In seems to have an off by one level spec., so this may
   need changing in future. ;

comment W: Sum[Smp] is redefined to be Inf.
    The identities may not be correct if one of the bottom parameters
    is a negative integer, even though the function may be well-behaved.
    The convergence of hypergeometric series should be checked using the
    file XCvgt before the identities here are used. ;


% ------------------------------ gauss1 --------------------------------
% Generalized Hypergeometric functions - transformations on pFqs.
% Keywords: Hypergeometric, Ghg, Transformations, reversal of series,
% Saalschutz.

% Author: Kevin McIsaac, Univ. of Western Australia, Jul 85.


% Some of this code references sum. This causes a problem in REDUCE.

gamma({??a}) ::- ap(times,map(gamma,{??a}));

%_Gamma(Init) ::- Loadonce(XGammaV);
%_Poc(Init)   ::- Loadonce(XPocV);

% SRev reverses finite Hypergeometric series.

sghg(6,1) :- srev ::-
  ghg(?p,?q,#(?m _=natp(1-?m),??a),#(??b),?z) -->
   ap(times,map(poc(?1,-?m),{??a}))/ap(times,map(poc(?1,-?m),{??b}))*
       (-?z)^(-?m)
   *ghg(?q+1,?p-1,ap(#,cat({?m},map(1-?1+?m,{??b}))),
                  ap(#,map(1-?1+?m,{??a})),
                  (-1)^(-1 + ?p + ?q)/?z);

% If there is more than one -ve integer in the numerator the smallest
% should be used.  In the current implementation the largest is used
% because of the natural ordering of Comm functions.

% The followong are commented out since in leads to an infinite recursion
%
%comment :SSaal
%        Saalschutzs theorem in non-terminating form;
%

sghg(6,2) :- ssaal:-
    ghg(3,2,#(?e,?f,?g),#(?b,?c _=(?e+?f+?g+1=?b+?c)),1) ->
        gamma({?e,?f,?g,?e+?b-1,?f+?b-1,?g+?b-1})
           /gamma({?c-?e,?c-?f,?c-?g})-
        gamma({?b,1+?g-?c,1+?f-?c,1+?e-?c,?c-1})
           /gamma({1-?c,1+?b-?c,?e,?f,?g})
        *ghg(3,2,#(1+?e-?c,1+?f-?c,1+?g-?c),#(2-?c,1+?b-?c),1);

comment : SDixon
        Generalization of Dixons theorem, Slater p52 (2.3.3.7);
sghg(6,3) :- sdixon :-
   ghg(3,2,#(?a,?b,?c),#(?e,?f),1) ->
        gamma({?e,?f,?e+?f-?a-?b-?c})
           /gamma({?a,?e+?f-?a-?c,?e+?f-?a-?b})*
        ghg(3,2,#(?e-?a,?f-?a,?e+?f-?a-?b-?c),
                #(?e+?f-?a-?c,?e+?f-?a-?b),1);

comment : SGhg[6,4]
        Three term relations, Slater p 115 (4.3.4);

sghg(6,4) :-
   ghg(3,2,#(?a,?b,?c),#(?d,?e),1) ->
   gamma({1-?a,?d,?e,?c-?b})/gamma({?e-?b,?d-?b,1+?b-?a,?c})
   *ghg(3,2,#(?b,1+?b-?d,1+?b-?e),#(1+?b-?c,1+?b-?a),1) +
   gamma({1-?a,?d,?e,?b-?c})/gamma({?e-?c,?d-?c,1+?c-?a,?b})
   *ghg(3,2,#(?c,1+?c-?e,1+?c-?d),#(1+?c-?b,1+?c-?a),1);

comment : SGhg[6,5]
     transforms a nearly-poised 3F2(-1) to a 4F3(1). Page 33 of Bailey;

sghg(6,5) :- ghg(3,2,#(?a,?b,?c),#(?d,?e _=?e+?c=?d+?b),-1) -->
   ap(gamma({?k-?b,?k-?c})/gamma({?k,?k-?b-?c})
      *ghg(4,3,#(?b,?c,?k/2-?a/2,?k/2+1/2-?a/2),
               #(?k-?a,?k/2,?k/2+1/2),1),
      {?b+?d});

%comment  SGhg[6,6][?n]
%       writes Ghg[p,q,#[a1,..,ap],#[b1,..,bq],z] in terms of
%       Ghg[p+1,q+1,#[1,a1+n,..,ap+n],#[n+1,b1+n,..,bq+n],z] for
%       n positive or negative. ;
%SGhg(6,6,(?n _=Natp(1+?n)) :- Ghg(?p,?q,#(??a),#(??b),?z) -->
%   Ap(Sum,{Ap(times,Map(Poc(?1,%r),{??a})) *?z^%r/
%         (Ap(times,Map(Poc(?1,%r),{??b})) *Gamma(%r+1)),
%         {%r,0,?n-1}}) +
%   Ap(times,Map(Poc(?1,?n),{??a})) *?z^?n /
%  (Ap(times,Map(Poc(?1,?n),{??b})) *Gamma(1+?n))
%   *Ghg(?p+1,?q+1,Ap(#,Cat({??a}+?n,{1})),
%     ap(#,cat({??b}+?n,{1+?n})),?z);
%
%SGhg(6,6,(?n _=Natp(-?n)) :- Ghg(?p,?q,#(??a),#(??b),?z) -->
%  -Ap(Sum,{Ap(times,Map(Gamma(?1+%r)/Gamma(?1),{??a})) *?z^%r/
%         (Ap(times,Map(Poc(?1,%r),{??b})) *Gamma(%r+1)),
%         {%r,?n,-1}}) +
%   Ap(times,Map(Gamma(?1+?n)/Gamma(?1),{??a})) *?z^?n/
%  (Ap(times,Map(Poc(?1,?n),{??b})) *Gamma(1+?n))
%   *Ghg(?p+1,?q+1,Ap(#,Cat({??a}+?n,{1})),
%                   ap(#,cat({??b}+?n,{1+?n})),?z);

sghg(6,7) :- ghg(6,5,#(?a,1+?a/2,?c,?d,?e,?f),
                    #(?a/2,1+?a-?c,1+?a-?d,1+?a-?e,1+?a-?f),-1) ->
  gamma(1+?a-?e) *gamma(1+?a-?f)/(gamma(1+?a) *gamma(1+?a-?e-?f))
  *ghg(3,2,#(1+?a-?c-?d,?e,?f),#(1+?a-?c,1+?a-?d),1);

sghg(6,8) :- ghg(6,5,#(?a,?b _=?b=1+?a/2,?c,?d,?e,?n _=natp(1-?n)),
                    #(?f _=?f=?a/2,?g _=?g=1+?a-?c,?h _=?h=1+?a-?d,
                    ?i _=?i=1+?a-?e,?j _=?j=1+?a-?n),-1) ->
  gamma(1+?a-?e) *gamma(1+?a-?n)/(gamma(1+?a) *gamma(1+?a-?e-?n))
  *ghg(3,2,#(1+?a-?c-?d,?e,?n),#(1+?a-?c,1+?a-?d),1);

%_XGhg6(Loaded) :- 1;


comment Special Elementary Cases of Gausses Series;

comment Abramowitz & Stegun, 15.1;

comment Incomplete. Rest of transformations must be added.

xgauss(1,3) :- Ghg(2,1,#(1,1),#(2),?z) -> 1/?z * Ln(1-?z);

xgauss(1,4) :- ghg(2,1,#(1/2,1),#(3/2),?z) ->
                1/(2*sqrt(?z))*ln((1+sqrt(?z))/(1-sqrt(?z)));

xgauss(1,5) :- ghg(2,1,#(1/2,1),#(3/2),?z) ->
                1/sqrt(-?z) * arctan(sqrt(-?z));

xgauss(1,6) :-{ghg(2,1,#(1/2,1/2),#(3/2),?z) ->
                1/sqrt(?z) * arcsin(sqrt(?z)),
               ghg(2,1,#(1,1),#(3/2),?z) ->
                1/((1-?z)*sqrt(?z)) * arcsin(sqrt(?z))};

xgauss(1,7) :-{ghg(2,1,#(1/2,1/2),#(3/2),?z) ->
                1/sqrt(-?z) * ln(sqrt(?z)+(1-?z)),
               ghg(2,1,#(1,1),#(3/2),?z) ->
                1/((1+?z)*sqrt(-?z)) * ln(sqrt(?z)+(1-?z))};

xgauss(1,8) :- ghg(2,1,#(?a,?b),#(?b),?z) -> (1-?z)^(-?a);

xgauss(1,9) :- ghg(2,1,#(?a,?a+1/2),#(1/2),?z) ->
                1/2*((1+sqrt(z))^(-2*?a) + (1-sqrt(?z))^(-2*?a));

xgauss(1,10):- ghg(2,1,#(?a,?a+1/2),#(3/2),?z) ->
                1/(2*sqrt(?z)*(1-2*?a))*
                    ((1+sqrt(z))^(-2*?a) + (1-sqrt(?z))^(-2*?a));

comment Incomplete. Rest of transformations must be added.;



comment Hypergeometric functions. Transformations of the argument; ;
comment Abramowitiz & Stegun 15.3
comment   Linear transformations *;

sgauss(3,3):-   ghg(2,1,#(?a,?b),#(?c),?z) ->
                  (1-?z)^(?c-?b-?a)*ghg(2,1,#(?c-?a,?c-?b),#(?c),?z);

sgauss(3,4):-    ghg(2,1,#(?a,?b),#(?c),?z) ->
                   ghg(2,1,#(?a,?c-?b),#(?c),?z/(?z-1))/(1-?z)^?a;

sgauss(3,5):-  ghg(2,1,#(?a,?b),#(?c),?z) ->
                gamma(?c)*gamma(?c-?a-?b)/(gamma(?c-?a)*gamma(?c-?b))*
                ghg(2,1,#(?a,?b),#(?a+?b-?c+1),1-?z)
                +(1-?z)^(?c-?a-?b)*gamma(?c)*gamma(?a+?b-?c)/(gamma(?a)*
                gamma(?b))*ghg(2,1,#(?c-?a,?c-?b),#(?c-?a-?b+1),1-?z);

sgauss(3,6):-    ghg(2,1,#(?a,?b),#(?c),?z) ->
                   1/(-?z)^?a*gamma(?c)*gamma(?b-?a)
                    /(gamma(?b)*gamma(?c-?a))*
                   ghg(2,1,#(?a,1-?c+?a),#(1-?b+?a),1/?z)
                   +1/(-?z)^?b*gamma(?c)*gamma(?a-?b)
                     /(gamma(?a)*gamma(?c-?b))*
                   ghg(2,1,#(?b,1-?c+?b),#(1-?a+?b),1/?z);

sgauss(3,7):-    ghg(2,1,#(?a,?b),#(?c),?z) ->
                   1/(1-?z)^?a*gamma(?c)*gamma(?b-?a)
                      /(gamma(?b)*gamma(?c-?a))*
                   ghg(2,1,#(?a,?c-?b),#(?a-?b+1),1/(1-?z))
                   +1/(1-?z)^?b*gamma(?c)*gamma(?a-?b)
                        /(gamma(?a)*gamma(?c-?b))*
                   ghg(2,1,#(?b,?c-?a),#(?b-?a+1),1/(1-?z));


sgauss(3,8):-    ghg(2,1,#(?a,?b),#(?c),?z) ->
                   1/?z^?a*gamma(?c)*gamma(?c-?a-?b)/ (gamma(?c-?a)*
                   gamma(?c-?b))*ghg(2,1,#(?a,?a-?c+1),
                                         #(?a+?b-?c+1),1-1/?z)
                +(1-?z)^(?c-?a-?b)*?z^(?a-?c) *
                gamma(?c)*gamma(?a+?b-?c)/(gamma(?a)*gamma(?b)) *
                ghg(2,1,#(?c-?a,1-?a),#(?c-?a-?b+1),1-1/?z);


comment*  Quadratic transformations *;


sgauss(3,15):-     ghg(2,1,#(?a,?b),#(2*?b),?z) ->
                      (1-?z)^(-?a/2)*ghg(2,1,#(?a/2,?b-?a/2),#(?b+1),
                         ?z^2/(4*?z-4));

sgauss(3,16):-     ghg(2,1,#(?a,?b),#(2*?b),?z) ->
                 (1-?z/2)^(-?a)*ghg(2,1,#(?a/2,?a/2+1/2),
                                        #(?b+1/2),?z^2/(2-?z)^2);

sgauss(3,17):-   ghg(2,1,#(?a,?b),#(2*?b),?z) ->
                    (1/2+sqrt(1-?z)/2)^(-2*?a)
                      *ghg(2,1,#(?a,?a-?b+1/2),#(?b+1/2),
                              ((1-sqrt(1-?z))/(1+sqrt(1-?z)))^2);

sgauss(3,18):-    ghg(2,1,#(?a,?b),#(2*?b),?z) -> (1-?z)^(-?a/2)
               *ghg(2,1,#(?a,2*?b-?a),#(?b+1/2),-(1-sqrt(1-?z))^2
                    /(4*sqrt(1-?z)));

sgauss(3,19):-    ghg(2,1,#(?a,?b _=?b=?a+1/2),#(?c),?z) ->
                     (1/2+sqrt(1-?z)/2)^(-2*?a)
               *ghg(2,1,#(2*?a,2*?a-?c+1),#(?c),(1-sqrt(1-?z))
                     /(1+sqrt(1-?z)));

sgauss(3,20):- {ghg(2,1,#(?a,?b _=?b=?a+1/2),#(?c),?z) ->
                       (1-sqrt(?z))^(-2*?a)
               *ghg(2,1,#(2*?a,?c-1/2),#(2*?c-1),
                     -2*sqrt(?z)/(1-sqrt(?z))),
                  ghg(2,1,#(?a,?b _=?b=?a+1/2),#(?c),?z) ->
                       (1+sqrt(?z))^(-2*?a)
               *ghg(2,1,#(2*?a,?c-1/2),#(2*?c-1),
                     2*sqrt(?z)/(1+sqrt(?z)))};

sgauss(3,21):-    ghg(2,1,#(?a,?b _=?b=?a+1/2),#(?c),?z) -> 1/(1-?z)^?a
               *ghg(2,1,#(2*?a,2*?c-2*?a-1),#(?c),(sqrt(1-?z)-1)
                                                  /(2*sqrt(1-?z)));

sgauss(3,22):-    ghg(2,1,#(?a,?b),#(?a+?b+1/2),?z) ->
                ghg(2,1,#(2*?a,2*?b),#(?a+?b+1/2),1/2-sqrt(1-?z)/2);

sgauss(3,23):-    ghg(2,1,#(?a,?b),#(?a+?b+1/2),?z) ->
                     (1/2+sqrt(1-?z)/2)^(-2*?a)
                       *ghg(2,1,#(2*?a,?a-?b+1/2),#(?a+?b+1/2),
                            (sqrt(1-?z)-1)/(sqrt(1-?z)+1));

sgauss(3,24):-    ghg(2,1,#(?a,?b),#(?a+?b-1/2),?z) ->
                  1/sqrt(1-?z)*ghg(2,1,#(2*?a-1,2*?b-1),#(?a+?b-1/2),
                                   1/2-sqrt(1-?z)/2);

sgauss(3,25):-    ghg(2,1,#(?a,?b),#(?a+?b-1/2),?z) ->
                     (1/2+sqrt(1-?z)/2)^(1-2*?a)/sqrt(1-?z)
                      *ghg(2,1,#(2*?a-1,?a-?b+1/2),#(?a+?b-1/2),
                          (sqrt(1-?z)-1)/(sqrt(1-?z)+1));

sgauss(3,26):-    ghg(2,1,#(?a,?b),#(?a-?b+1),?z) ->
                1/(1+?z)^(2*?a)*ghg(2,1,#(?a/2,?a/2+1/2),#(?a-?b+1),
                                    4*?z/(1+?z)^2);

sgauss(3,27):- {ghg(2,1,#(?a,?b),#(?a-?b+1),?z) -> (1+sqrt(?z))^(-2*?a)
                 *ghg(2,1,#(?a,?a-?b+1/2),#(2*?a-2*?b+1),
                      4*sqrt(?z)/(1+sqrt(?z))^2),
                ghg(2,1,#(?a,?b),#(?a-?b+1),?z) -> (1-sqrt(?z))^(-2*?a)
               *ghg(2,1,#(?a,?a-?b+1/2),#(2*?a-2*?b+1),
                      -4*sqrt(?z)/(1-sqrt(?z))^2)};

sgauss(3,28):-    ghg(2,1,#(?a,?b),#(?a-?b+1),?z) ->
                1/(1-?z)^?a*ghg(2,1,#(?a/2,?a/2-?b+1/2),#(?a-?b+1),
                    -4*?z/(1-?z)^2);

sgauss(3,29):-    ghg(2,1,#(?a,?b),#((?a+?b+1)/2),?z) ->
                ghg(2,1,#(?a/2,?b/2),#((?a+?b+1)/2),-4*?z*(?z-1));

sgauss(3,30):-   ghg(2,1,#(?a,?b),#(?a/2+?b/2+1/2),?z) -> 1/(1-2*?z)^?a
                     *ghg(2,1,#(?a/2,?a/2+1/2),#(?a/2+?b/2+1/2),
                          4*?z*(?z-1)/(1-2*?z)^2);

sgauss(3,31):-    ghg(2,1,#(?a,1-?a),#(?c),?z) ->
                    (1-?z)^(?c-1)*ghg(2,1,#(?c/2-?a/2,?c/2+?a/2-1/2),
                                      #(?c),4*?z-4*?z^2);

sgauss(3,32):-    ghg(2,1,#(?a,1-?a),#(?c),?z) ->
                      (1-?z)^(?c-1)* (1-2*?z)^(?a-?c)
                        *ghg(2,1,#(?c/2-?a/2,?c/2-?a/2+1/2),#(?c),
                             4*?z*(?z-1)/(1-2*?z)^2);


% Gaussian hypergeometric functions. Orthogonal polynomials.
% Abramowitz and Stegun section 15.4.

sgauss(4,3):-   ghg(2,1,#(?n _=intp(-?n),-?n),#(1/2),?x)
                     -> chet(-?n,1-2 *?x);

sgauss(4,4):-   ghg(2,1,#(?n _=intp(-?n),1-?n),#(1),?x)
                     -> legp(-?n,1-2 *?x);

sgauss(4,5):-   ghg(2,1,#(?n _=intp(-?n),?a-?n),#(?a/2+1/2),?x) ->
                fctl(-?n)/poc(?a,-?n) *geg(-?n,?a/2,1-2 *?x);

sgauss(4,6):-   ghg(2,1,#(?n _=intp(-?n),?c),#(?a),?x) ->
                fctl(-?n)/poc(?a,-?n)*jacp(-?n,?a-1,?c-?a+?n,1-2*?x);

endmodule;

end;
