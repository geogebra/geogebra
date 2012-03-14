module expvec;

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


% Specific support for distributive polynomial exponent vectors

%  Authors: R. Gebauer, A. C. Hearn, H. Kredel

%   We assume here that an exponent vector is a list of integers.  This
%   version uses small integer arithmetic on the individual exponents
%   and assumes that a compiled function can be dynamically redefined

%   Modification H. Melenk (August 1988)
%   1. Most ev-routines handle exponent vectors with variable length:
%      the convention is, that trailing zeros may be omitted.
%   2. evcompless!? is mapped to evcomp such that each term order mode
%      is supported by exactly one procedure entry.
%   3. complete exponent vector compare collected in separate module
%      TORDER (TORD33)

symbolic procedure evperm (e1,n);
%   Exponent vector permutation. e1 is an exponent vector, n is a
%    index list , a list of digits. evperm(e1,n) returns a list e1
%    permuted in respect to n.
     if null n then nil
        else evnth(e1, car n) . evperm(e1, cdr n);

symbolic procedure evcons (e1,e2);
%    Exponent vector construct. e1 and e2 are exponents. evcons(e1,e2)
%    constructs an exponent vector.
     e1 . e2;

symbolic procedure evnth (e1,n);
%    Exponent vector n-th element. e1 is an exponent vector, n is a
%    digit. evnth(e1,n) returns the n-th element of e1, an exponent.
     if null e1 then 0 else
     if n = 1 then evfirst e1 else evnth(evred e1, n - 1);

symbolic procedure evred e1;
%    Exponent vector reductum. e1 is an exponent vector. evred(e1)
%    returns the reductum of the exponent vector e1.
     if e1 then cdr e1 else NIL;

symbolic procedure evfirst e1;
%    Exponent vector first. e1 is an exponent vector. evfirst(e1)
%   returns the first element of the exponent vector e1, an exponent.
     if e1 then car e1 else 0;

symbolic procedure evsum0(n,p);
% exponent vector sum version 0. n is the length of dipvars!*.
% p is a distributive polynomial.
  if dipzero!? p then evzero1 n else
  evsum(dipevlmon p, evsum0(n,dipmred p));

symbolic procedure evzero1 n;
% Returns the exponent vector power representation
% of length n for a zero power.
  begin scalar x;
   for i:=1:n do <<x:=0 . x>>;
  return x
  end;

symbolic procedure indexcpl(ev,n);
% returns a list of indexes of non zero exponents.
  if null ev then ev else(if car ev = 0 then
                            indexcpl(cdr ev,n + 1) else
    (n . indexcpl(cdr ev,n + 1)));

symbolic procedure evzer1!? e;
% returns a boolean expression. true if e is null else false.
  null e;

symbolic procedure evzero!? e;
%   Returns a boolean expression. True if all exponents are zero
   null e or car e = 0 and evzero!? cdr e;

symbolic procedure evzero;
%   Returns the exponent vector representation for a zero power
% for i:=1:length dipvars!* collect 0;
   begin scalar x;
      for i:=1:length dipvars!* do <<x:=0 . x>>;
      return x end;

symbolic procedure mkexpvec u;
%   Returns an exponent vector with a 1 in the u place
   if not(u member dipvars!*) then typerr(u,"dipoly variable")
    else for each x in dipvars!* collect if x eq u then 1 else 0;

symbolic procedure evlcm (e1,e2);
%    Exponent vector least common multiple. e1 and e2 are
%    exponent vectors. evlcm(e1,e2) computes the least common
%    multiple of the exponent vectors e1 and e2, and returns
%    an exponent vector.
   % for each lpart in e1 each rpart in e2 collect
   %     if lpart #> rpart then lpart else rpart;
   begin scalar x;
      while e1 and e2 do
         <<x:=(if car e1 #> car e2 then car e1 else car e2) . x;
           e1:=cdr e1;e2:=cdr e2>>;
      return reversip x
   end;

symbolic procedure evmtest!? (e1,e2);
%    Exponent vector multiple test. e1 and e2 are compatible exponent
%    vectors. evmtest!?(e1,e2) returns a boolean expression.
%    True if exponent vector e1 is a multiple of exponent
%    vector e2, else false.
   if e1 and e2 then not(car e1 #< car e2) and evmtest!?(cdr e1,cdr e2)
   else  evzero!? e2;

symbolic procedure evsum (e1,e2);
%    Exponent vector sum. e1 and e2 are exponent vectors.
%    evsum(e1,e2) calculates the sum of the exponent vectors.
%    e1 and e2 componentwise and returns an exponent vector.
   % for each lpart in e1 each rpart in e2 collect lpart #+ rpart;
     begin scalar x;
      while e1 and e2 do
         <<x:=(car e1 #+ car e2) . x;e1:=cdr e1;e2:=cdr e2>>;
      x:= reversip x;
      return if e1 then nconc(x,e1) else
             if e2 then nconc(x,e2) else x;
   end;

symbolic procedure evdif (e1,e2);
%    Exponent vector difference. e1 and e2 are exponent
%    vectors. evdif(e1,e2) calculates the difference of the
%    exponent vectors e1 and e2 componentwise and returns an
%    exponent vector.
   % for each lpart in e1 each rpart in e2 collect lpart #- rpart;
   begin scalar x;
      while e2 do
         <<if null e1 then e1:='(0);
           x:=(car e1 #- car e2) . x;e1:=cdr e1;e2:=cdr e2>>;
      return nconc (reversip x,e1);
   end;

symbolic procedure intevprod(n,e);
%  Multiplies each element of the exponent vector u by the integer n
   for each x in e collect n #* x;

symbolic procedure expvec2a e;
%   Returns list of prefix equivalents of exponent vector e
   expvec2a1(e,dipvars!*);

symbolic procedure expvec2a1(u,v);
%   Sub function of expvec2a
   if null u then nil
    else if car u = 0 then expvec2a1(cdr u,cdr v)
    else if car u = 1 then car v . expvec2a1(cdr u,cdr v)
    else list('expt,car v,car u) . expvec2a1(cdr u,cdr v);

symbolic procedure dipevlpri(e,v);
%    Print exponent vector e in infix form. V is a boolean variable
%    which is true if an element in a product has preceded this one
   dipevlpri1(e,dipvars!*,v);

symbolic procedure dipevlpri1(e,u,v);
%   Sub function of dipevlpri
   if null e then nil
    else if car e = 0 then dipevlpri1(cdr e,cdr u,v)
    else <<if v then dipprin2 "*";
           if atom car u or null get(caar u,'dipprifn)
             then dipprin2 car u
            else apply1(get(caar u,'dipprifn),car u);
           if car e #> 1 then <<dipprin2 "**";dipprin2 car e>>;
           dipevlpri1(cdr e,cdr u,t)>>;

endmodule;;end;
