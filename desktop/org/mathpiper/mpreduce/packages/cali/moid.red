module moid;

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


COMMENT

               ###########################
               ##                       ##
               ##     MONOMIAL IDEALS   ##
               ##                       ##
               ###########################

This module supports computations with leading term ideals. Moideal
monomials are assumed to be without module component, since a module
moideal decomposes into the direct sum of ideal moideals.

Lit.:
[BS] Bayer, Stillman : J. Symb. Comp. 14 (1992), 31 - 50.

This module contains :

        - A moideal prime decomposition along [BS]

        - An algorithm to find all strongly independent sets using
                moideal primes (also for modules),

        - An algorithm to compute the dimension (dim M := dim in(M))
                based on strongly independent sets.

        - An easy dimension computation, correct for puredimensional
                ideals and modules.

Monomial ideals have the following informal syntax :

        <moideal> ::= list of monomials

To manage module moideals they are stored as assoc. list of

        (<component number> . <ideal moideal>)

Moideals are kept ordered with respect to the descending lexicographic
order, see [BS].

END COMMENT;

% ------------- monomial ideal constructors --------------

symbolic procedure moid_from_bas bas;
% Returns the list of leading monomials of the base list bas
% not removing module components.
   for each x in bas_zerodelete bas collect dp_lmon bas_dpoly x;

symbolic procedure moid_from_dpmat m;
% Returns the assoc. list of moideals of the columns of the dpmat m.
  (if dpmat_cols m = 0 then list (0 . u)
  else for i:=1:dpmat_cols m collect
     i . for each x in u join if mo_comp(x)=i then {mo_deletecomp x})
  where u=moid_from_bas dpmat_list m;

symbolic procedure moid_2a m;
% Convert the moideal m to algebraic mode.
  'list . for each x in m collect dp_2a list dp_term(bc_fi 1,x);

symbolic procedure moid_from_a m;
% Convert a moideal from algebraic mode.
  if not eqcar(m,'list) then typerr(m,"moideal")
  else for each x in cdr m collect dp_lmon dp_from_a x;

symbolic procedure moid_print m; mathprint moid_2a m;

% ------- moideal arithmetics ------------------------

symbolic procedure moid_sum(a,b);
% (Reduced) sum of two (v)moideals.
  moid_red append(a,b);

symbolic procedure moid_intersect(a,b);
% Intersection of two (pure !) moideals.
  begin scalar c;
  while b do
    << c:=nconc(for each x in a collect mo_lcm(x,car b),c);
       b:=cdr b
    >>;
  return moid_red c
  end;

symbolic procedure moid_sort m;
% Sorting by descending (pure) lexicographic order, first by mo_comp.
   sort(m,function mo_dlexcomp);

symbolic procedure moid_red m;
% Returns a minimal generating set of the (v)moideal m.
  moid!=red moid_sort m;

symbolic procedure moid!=red m;
  begin scalar v;
  while m do
    << if not moid_member(car m,cdr m) then v:=car m . v;
       m:=cdr m;
    >>;
  return reversip v;
  end;

symbolic procedure moid_member(mo,m);
% true <=> c \in m vdivides mo.
  if null m then nil
  else mo_vdivides!?(car m,mo) or moid_member(mo,cdr m);

symbolic procedure moid_radical u;
% Returns the radical of the (pure) moideal u.
  moid_red for each x in u collect mo_radical x;

symbolic procedure moid_quot(m,x);
% The quotient of the moideal m by the monomial x.
  moid_red for each u in m collect mo_diff(u,mo_gcd(u,x));

% --------------- moideal prime decomposition --------------
% Returns the minimal primes of the moideal m as a list of variable
% lists.

symbolic procedure moid_primes m;
  begin scalar c,m1,m2;
    m:=listminimize(for each x in m collect mo_support x,
                function subsetp);
    for each x in m do
        if length x=1 then m1:=car x . m1
        else m2:=x . m2;
    return for each x in moid!=primes(m2,ring_names cali!=basering)
        collect append(m1,x);
  end;

symbolic procedure moid!=primes(m,vars);
  if null m then list nil
  else begin scalar b; b:=t;
    for each x in m do b:=b and intersection(x,vars);
    if not b then return nil;
    return listminimize(
        for each x in intersection(car m,vars) join
        for each y in moid!=primes(moid!=sps(x,cdr m),
              vars:=delete(x,vars)) collect x . y,
        function subsetp);
  end;

symbolic procedure moid!=sps(x,m);
  for each y in m join if not memq(x,y) then {y};


% ------------ (Strongly) independent sets -----------------

symbolic procedure moid_max l;
  if null l then nil
  else car sort(l,function (lambda(x,y);length x >= length y));


symbolic procedure indepvarsets!* m;
% Returns the sets of (strongly) independent variables for the
% dpmat m. m must be a Groebner basis.
  begin scalar u,n;
    u:=listminimize(
        for each x in moid_from_dpmat m join moid_primes cdr x,
        function subsetp);
    n:=ring_names cali!=basering;
    return for each x in u collect setdiff(n,x);
  end;

% ---------- Dimension and codimension ------------

symbolic procedure moid_goodindepvarset m;
% Returns the lexicographically last maximal independent set of the
% dpmat m.
  begin scalar l,n,l1;
    l:=sort(indepvarsets!* m,
                function (lambda(x,y);length x >= length y));
    if null l then return nil;
    n:=length first l;
    l:=for each x in l join if length x = n then {x};
    for each x in reverse ring_names cali!=basering do
        if length l>1 then
        << l1:=for each y in l join if member(x,y) then {y};
           if l1 then l:=l1;
        >>;
    return first l;
    end;

symbolic procedure dim!* m;
% The dpmat m must be a Groebner basis. Computes the dimension of
% Coker m as the greatest size of a strongly independent set.
  if not eqcar(m,'dpmat) then typerr(m,"DPMAT")
  else length moid_max indepvarsets!* m;

symbolic procedure codim!* m;
  length ring_names cali!=basering - dim!* m;

% ---- An easy independent set procedure -------------

symbolic operator easyindepset;
symbolic procedure easyindepset m;
  if !*mode='algebraic then
        makelist easyindepset!* dpmat_from_a reval m
  else easyindepset!* m;

symbolic procedure easyindepset!* m;
% Returns a maximal with respect to inclusion independent set for the
% moideal m.
  begin scalar b,c,d;
    m:=for each x in m collect mo_support x;
    b:=c:=ring_names cali!=basering;
    for each x in b do if moid!=ept(d:=delete(x,c),m) then c:=d;
    return setdiff(ring_names cali!=basering,c);
  end;

symbolic procedure moid!=ept(l,m);
  if null m then t
  else intersection(l,car m) and moid!=ept(l,cdr m);

symbolic operator easydim;
symbolic procedure easydim m;
  if !*mode='algebraic then easydim!* dpmat_from_a reval m
  else easydim!* m;

symbolic procedure easydim!* m;
% Returns a lower bound for the dimension. The bound is true for
% unmixed ideals (e.g. primes). m must be a gbasis.
  if not eqcar(m,'dpmat) then typerr(m,"DPMAT")
  else listexpand(function max2,
        for each x in moid_from_dpmat m collect
            length easyindepset!* cdr x);

endmodule; % moid

end;
