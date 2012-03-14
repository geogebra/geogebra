module dipoly1;% Distributive polynomial algorithms.

% Authors: R. Gebauer, A. C. Hearn, H. Kredel.
% Modification for REDUCE > 3.3: H. Melenk.

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


% Modification of the function 'dipprodin' by Arthur Norman (august 2002,
% REDUCE 3.7).

fluid'(dipvars!* dipzero);

symbolic procedure dipconst!? p;
 not dipzero!? p and dipzero!? dipmred p and evzero!? dipevlmon p;

symbolic procedure terprit n;for i:=1:n do terpri();

symbolic procedure dfcprint pl;
% h polynomial factor list of distributive polynomials print.
for each p in pl do dfcprintin p;

symbolic procedure dfcprintin p;
% factor with exponent print.
(if cdr p neq 1 then <<prin2 "(";dipprint1(p1,nil);prin2 ")** ";
  prin2 cdr p;terprit 2>> else <<prin2 "  ";dipprint p1>>)
      where p1:= dipmonic a2dip prepf car p;

symbolic procedure dfcprin p;
% print content,factors and exponents of factorized polynomial p.
<<terpri();prin2 " content of factorized polynomials=";
   prin2 car p;
   terprit 2;dfcprint cdr p>>;

symbolic procedure diplcm p;
% Distributive polynomial least common multiple of denominators.
% p is a distributive rational polynomial. diplcm(p) calculates
% the least common multiple of the denominators and returns a
% base coefficient of the form  1/lcm(denom bc1,.....,denom bci).
if dipzero!? p then mkbc(1,1)
     else bclcmd(diplbc p,diplcm dipmred p);

symbolic procedure diprectoint(p,u);
% Distributive polynomial conversion rational to integral.
% p is a distributive rational polynomial,u is a base coefficient
%(1/lcm denom p). diprectoint(p,u) returns a primitive
% associate pseudo integral(denominators are 1)distributive
% polynomial.
if bczero!? u then dipzero else if bcone!? u then p else diprectoint1(p,u);

symbolic procedure diprectoint1(p,u);
% Distributive polynomial conversion rational to integral internal 1.
% diprectoint1 is used in diprectoint.
if dipzero!? p then dipzero
     else dipmoncomp(bclcmdprod(u,diplbc p),dipevlmon p,
                     diprectoint1(dipmred p,u));

symbolic procedure dipbcprod(p,a);
%     Distributive polynomial base coefficient product.
%     p is a distributive polynomial,a is a base coefficient.
%     dipbcprod(p,a) computes p*a,a distributive polynomial.
if bczero!? a then dipzero else if bcone!? a then p else dipbcprodin(p,a);

symbolic procedure dipbcprodin(p,a);
%     Distributive polynomial base coefficient product internal.
%     p is a distributive polynomial,a is a base coefficient,
%     where a is not equal 0 and not equal 1.
%     dipbcprodin(p,a) computes p*a,a distributive polynomial.
if dipzero!? p then dipzero
                   else dipmoncomp(bcprod(a,diplbc p),
                                   dipevlmon p,
                                   dipbcprodin(dipmred p,a));

symbolic procedure dipdif(p1,p2);
%    Distributive polynomial difference. p1 and p2 are distributive
%    polynomials. dipdif(p1,p2) calculates the difference of the
%    two distributive polynomials p1 and p2,a distributive polynomial
if dipzero!? p1 then dipneg p2
        else if dipzero!? p2 then p1
             else(if sl=1 then dipmoncomp(diplbc p1,
                                              ep1,
                                              dipdif(dipmred p1,p2))
                  else if sl=-1 then dipmoncomp(bcneg diplbc p2,
                                                  ep2,
                                                  dipdif(p1,dipmred p2))
                       else(if bczero!? al
                                then dipdif(dipmred p1,dipmred p2)
                              else dipmoncomp(al,
                                              ep1,
                                              dipdif(dipmred p1,
                                                     dipmred p2))
                         )where al=bcdif(diplbc p1,diplbc p2)
         )where sl=evcomp(ep1,ep2)
                 where ep1=dipevlmon p1,ep2=dipevlmon p2;

symbolic procedure diplength p;
%     Distributive polynomial length. p is a distributive
%     polynomial. diplength(p) returns the number of terms
%     of the distributive polynomial p,a digit.
 if dipzero!? p then 0 else 1 + diplength dipmred p;

symbolic procedure diplistsum pl;
%     Distributive polynomial list sum. pl is a list of distributive
%     polynomials. diplistsum(pl) calculates the sum of all polynomials
%     and returns a list of one distributive polynomial.
if null pl or null cdr pl then pl
        else diplistsum(dipsum(car pl,cadr pl).diplistsum cddr pl);

symbolic procedure diplmerge(pl1,pl2);
%    Distributive polynomial list merge. pl1 and pl2 are lists
%    of distributive polynomials where pl1 and pl2 are in non
%    decreasing order. diplmerge(pl1,pl2) returns the merged
%    distributive polynomial list of pl1 and pl2.
if null pl1 then pl2
       else if null pl2 then pl1
            else(if sl >= 0 then cpl1.diplmerge(cdr pl1,pl2)
                 else cpl2.diplmerge(cdr pl2,pl1)
        )where sl=evcomp(ep1,ep2)
                where ep1=dipevlmon cpl1,ep2=dipevlmon cpl2
                where cpl1=car pl1,cpl2=car pl2;

symbolic procedure diplsort pl;
%    Distributive polynomial list sort. pl is a list of
%    distributive polynomials. diplsort(pl) returns the
%    sorted distributive polynomial list of pl.
sort(pl,function dipevlcomp);

symbolic procedure dipevlcomp(p1,p2);
%     Distributive polynomial exponent vector leading monomial
%     compare. p1 and p2 are distributive polynomials.
%     dipevlcomp(p1,p2) returns a boolean expression true if the
%     distributive polynomial p1 is smaller or equal the distributive
%     polynomial p2 else false.
not evcompless!?(dipevlmon p1,dipevlmon p2);

symbolic procedure dipmonic p;
%     Distributive polynomial monic. p is a distributive
%     polynomial. dipmonic(p) computes p/lbc(p) if p is
%     not equal dipzero and returns a distributive
%     polynomial,else dipmonic(p) returns dipzero.
if dipzero!? p then p else dipbcprod(p,bcinv diplbc p);

symbolic procedure dipneg p;
%    Distributive polynomial negative. p is a distributive
%    polynomial. dipneg(p) returns the negative of the distributive
%    polynomial p,a distributive polynomial.
if dipzero!? p then p
       else dipmoncomp(bcneg diplbc p,dipevlmon p,dipneg dipmred p);

symbolic procedure dipone!? p;
%    Distributive polynomial one. p is a distributive polynomial.
%    dipone!?(p) returns a boolean value. If p is the distributive
%    polynomial one then true else false.
not dipzero!? p
        and dipzero!? dipmred p
            and evzero!? dipevlmon p
                and bcone!? diplbc p;

symbolic procedure dippairsort pl;
%    Distributive polynomial list pair merge sort. pl is a list
%    of distributive polynomials. dippairsort(pl) returns the
%    list of merged and in non decreasing order sorted
%    distributive polynomials.
if null pl or null cdr pl then pl
       else diplmerge(diplmerge(car pl.nil,cadr pl.nil),
                      dippairsort cddr pl);

symbolic procedure dipprod(p1,p2);
%    Distributive polynomial product. p1 and p2 are distributive
%    polynomials. dipprod(p1,p2) calculates the product of the
%    two distributive polynomials p1 and p2,a distributive polynomial
if diplength p1 <= diplength p2 then dipprodin(p1,p2) else dipprodin(p2,p1);

% The following function was observed recursing very deeply indeed when
% certain examples were attempted. Automatic recursion to iteration
% conversion in the compiler was not applicable in this case, so a hand
% adjustment follows.

% symbolic procedure dipprodin(p1,p2);
%    Distributive polynomial product internal. p1 and p2 are distrib
%    polynomials. dipprodin(p1,p2) calculates the product of the
%    two distributive polynomials p1 and p2,a distributive polynomial.
%    if dipzero!? p1 or dipzero!? p2 then dipzero
%     else(dipmoncomp(bcprod(bp1,diplbc p2),
%                     evsum(ep1,dipevlmon p2),
%                     dipsum(dipprodin(dipfmon(bp1,ep1),dipmred p2),
%                            dipprodin(dipmred p1,p2))))
%    where bp1=diplbc p1,ep1=dipevlmon p1;

% This next definition is one that recursion elimination can handle.
% As compared to the original code it introduces a slight time
% inefficiency. The original version exploited the fact that the leading
% monomial in the result was the product of the two input leading
% monomials.  In this version dipsum will have to do an exponent
% comparison to re-discover this.  But the assymptotic overhead grows
% linearly while the overall cost here grows quadratically (or worse) if
% the two input polys are around the same length, so the cost is ok.

symbolic procedure dipprodin(p1, p2);
%    Distributive polynomial product internal. p1 and p2 are distrib
%    polynomials. dipprodin(p1,p2) calculates the product of the
%    two distributive polynomials p1 and p2,a distributive polynomial.
   if dipzero!? p1 or dipzero!? p2 then dipzero
    else dipsum(dipprodin1(diplbc p1,dipevlmon p1,p2),
                dipprodin(dipmred p1,p2));

symbolic procedure dipprodin1(p1lbc,p1lmon,p2);
   if dipzero!? p2 then dipzero
    else dipmoncomp(bcprod(p1lbc,diplbc p2),
                    evsum(p1lmon,dipevlmon p2),
                    dipprodin1(p1lbc,p1lmon,dipmred p2));

symbolic procedure dipprodls(p1,p2);
%     Distributive polynomial product. p1 and p2 are distributive
%     polynomials. dipprod(p1,p2) calculates the product of the
%     two distributive polynomials p1 and p2,a distributive polynomial
%     using distributive polynomials list sum(diplistsum).
if dipzero!? p1 or dipzero!? p2 then dipzero
        else car diplistsum if diplength p1 <= diplength p2
                               then dipprodlsin(p1,p2)
                               else dipprodlsin(p2,p1);

symbolic procedure dipprodlsin(p1,p2);
%     Distributive polynomial product. p1 and p2 are distributive
%     polynomials. dipprod(p1,p2) calculates the product of the
%     two distributive polynomials p1 and p2,a distributive polynomial
%     using distributive polynomials list sum(diplistsum).
if dipzero!? p1 or dipzero!? p2 then nil
        else(dipmoncomp(bcprod(bp1,diplbc p2),evsum(ep1,dipevlmon p2),
                          car dipprodlsin(dipfmon(bp1,ep1),dipmred p2))
                          .dipprodlsin(dipmred p1,p2)
          )where bp1=diplbc p1,ep1=dipevlmon p1;

symbolic procedure dipsum(p1,p2);
%    Distributive polynomial sum. p1 and p2 are distributive
%    polynomials. dipsum(p1,p2)calculates the sum of the
%    two distributive polynomials p1 and p2.
%    Iterative version,better suited for very long polynomials.
if null p1 then p2 else if null p2 then p1 else
begin scalar al,done,ep1,ep2,nt,rw,sl,w;
  while not done do
   <<if dipzero!? p1 then <<nt:=p2;done:=t>> else
     if dipzero!? p2 then <<nt:=p1;done:=t>> else
     <<ep1:=dipevlmon p1;ep2:=dipevlmon p2;
       sl:=evcomp(ep1,ep2);
       % Compute the next term.
       if sl #= 1 then
       <<nt:=dipmoncomp(diplbc p1,ep1,nil);
         p1:=dipmred p1>> else
       if sl #= -1 then
       <<nt:=dipmoncomp(diplbc p2,ep2,nil);
         p2:=dipmred p2>> else
       <<al:=bcsum(diplbc p1,diplbc p2);
         nt:=if not bczero!? al then dipmoncomp(al,ep1,nil)else nil;
         p1:=dipmred p1;p2:=dipmred p2>>>>;
       % Append the term to the sum polynomial.
     if nt then
        if null w then w:=rw:=nt
            else <<cdr cdr rw:=nt;rw:=nt>>;
  >>;return w end;

endmodule;;end;
