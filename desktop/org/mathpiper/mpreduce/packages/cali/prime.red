module prime; % corrected version | 15.6.1995

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

        ####################################
        #                                  #
        #  PRIME DECOMPOSITION, RADICALS,  #
        #        AND RELATED PROBLEMS      #
        #                                  #
        ####################################


This module contains algorithms

    - for zerodimensional ideals :
            - to test whether it is radical
            - to compute its radical
            - for a primality test

    - for zerodimensional ideals and modules :
            - to compute its primes
            - to compute its primary decomposition

    - for arbitrary ideals :
            - for a primality test
            - to compute its radical
            - to test whether it is radical

    - for arbitrary ideals and modules :
        - to compute its isolated primes
        - to compute its primary decomposition and
            the associated primes
        - a shortcut for the primary decomposition
            computation for unmixed modules

The algorithms follow

        Seidenberg : Trans. AMS 197 (1974), 273 - 313.

        Kredel : in Proc. EUROCAL'87, Lecture Notes in Comp. Sci. 378
                (1986), 270 - 281.

        Gianni, Trager, Zacharias :
                J. Symb. Comp. 6 (1988), 149 - 167.

The primary decomposition now proceeds as follows:
        1) compute the isolated primes
        2) compute by ideal separation quasi-primary components
        3) for each of them split off embedded components
        4) apply the decomposition recursively to them
        5) Decide in a last (global) step unnecessary components among
                them

See     Gr\"abe : Factorized Gr\"obner bases and primary
                decomposition. To appear

The switch factorprimes switches between invokation of the Groebner
factorizer (on/ the default) and algorithms, that use only univariate
factorization as described in [GTZ] (off).


END COMMENT;

switch factorprimes;     % (on) see primes

!*factorprimes:=t;      % Invoke the Groebner factorizer first.

% ------ The radical of a zerodimensional ideal -----------

symbolic procedure prime!=mksqrfree(pol,x);
% Make the univariate dpoly p(x) squarefree.
  begin scalar p;
    p:=numr simp dp_2a pol;
    return dp_from_a prepf car qremf(p,gcdf!*(p,numr difff(p,x)))
    end;

put('zeroradical,'psopfn,'prime!=evzero);
symbolic procedure prime!=evzero m;
  begin scalar c;
  intf_test m; intf_get(m:=car m);
  if not (c:=get(m,'gbasis)) then
        put(m,'gbasis,c:=gbasis!* get(m,'basis));
  return dpmat_2a zeroradical!* c;
  end;

symbolic procedure zeroradical!* m;
% Returns the radical of the zerodim. ideal m. m must be a gbasis.
  if dpmat_cols m>0 or not dimzerop!* m then
        rederr"ZERORADICAL only for zerodimensional ideals"
  else if dpmat_unitideal!? m then m
  else begin scalar u;
    u:=for each x in ring_names cali!=basering collect
        bas_make(0,prime!=mksqrfree(odim_up(x,m),x));
    u:=dpmat_make(length u,0,bas_renumber u,nil,nil);
    return gbasis!* matsum!* {m,u};
    end;

put('iszeroradical,'psopfn,'prime!=eviszero);
symbolic procedure prime!=eviszero m;
  begin scalar c;
  intf_test m; intf_get(m:=car m);
  if not (c:=get(m,'gbasis)) then
        put(m,'gbasis,c:=gbasis!* get(m,'basis));
  return if iszeroradical!* c then 'yes else 'no;
  end;

symbolic procedure iszeroradical!* m;
% Test whether the zerodim. ideal m is radical. m must be a gbasis.
  if dpmat_cols m>0 or not dimzerop!* m then
        rederr"ISZERORADICAL only for zerodimensional ideals"
  else if dpmat_unitideal!? m then t
  else begin scalar isradical;
    isradical:=t;
    for each x in ring_names cali!=basering do
        isradical:=isradical and
            null matop_pseudomod(prime!=mksqrfree(odim_up(x,m),x),m);
    return isradical;
    end;

% ---- The primes of a zerodimensional ideal or module ------

symbolic operator zeroprimes;
symbolic procedure zeroprimes m;
  if !*mode='algebraic then
        makelist for each x in zeroprimes!* dpmat_from_a reval m
                collect dpmat_2a x
  else zeroprimes!* m;

symbolic procedure zeroprimes!* m;
% The primes of the zerodimensional ideal Ann F/M.
% The unit ideal has no primes.
  listminimize(for each x in
        if !*factorprimes then groebf_zeroprimes1(annihilator2!* m,nil)
        else prime_zeroprimes1 gbasis!* annihilator2!* m
                join prime!=zeroprimes2 x, function submodulep!*);

symbolic procedure prime_iszeroprime m;
% Test a zerodimensiomal ideal to be prime. m must be a gbasis.
  if dpmat_cols m>0 or not dimzerop!* m then
    rederr "iszeroprime only for zerodimensional ideals"
  else if dpmat_unitideal!? m then rederr"the ideal is the unit ideal"
  else prime!=iszeroprime1 m and prime!=iszeroprime2 m;

symbolic procedure prime_zeroprimes1 m;
% A first approximation to the isolated primes in dim=0 : Factor all
% univariate polynomials in m.
% m must be a gbasis. Returns a reduced list of gbases.
  if dpmat_cols m>0 then rederr"ZEROPRIMES only for ideals"
  else if dpmat_unitideal!? m then nil
  else if not dimzerop!* m then
        rederr"ZEROPRIMES only for zerodimensional ideals"
  else begin scalar l;
    l:={m};
    for each x in ring_names cali!=basering do
        l:=for each y in l join
                if not member(x,for each v in dpmat_list y join
                        {mo_linear dp_lmon bas_dpoly v}) then
            (begin scalar u,p;
            u:=dp_factor (p:=odim_up(x,y));
            if (length u=1) and equal(car u,p) then return {y}
            else return for each z in u join
                if not dpmat_unitideal!?(p:=gbasis!* matsum!*
                        {y,dpmat_from_dpoly z}) then {p};
            end)
                else {y};
    return l;
    end;

symbolic procedure prime!=iszeroprime1 m;
% A first non primality test.
  if dpmat_cols m>0 then rederr"ISZEROPRIME only for ideals"
  else if dpmat_unitideal!? m then nil
  else if not dimzerop!* m then
        rederr"ISZEROPRIME only for zerodimensional ideals"
  else begin scalar b; b:=t;
    for each x in ring_names cali!=basering do
       b:=b and
            begin scalar u,p;
            u:=dp_factor (p:=odim_up(x,m));
            if (length u=1) and equal(car u,p) then return t
            else return nil
            end;
    return b;
    end;

symbolic procedure prime_gpchange(vars,v,m);
% Change to general position with respect to v. Only for pure lex.
% term order and radical ideal m.
  if null vars or dpmat_unitideal!? m then m
  else begin scalar s,x,a;
    s:=0; x:=mo_from_a car vars;
    a:=list (v.prepf addf(!*k2f v,!*k2f car vars));
            % the substitution rule v -> v + x .
    while not member(x,moid_from_bas dpmat_list m)
                % i.e. m has a leading term equal to x
        and ((s:=s+1) < 10)
                % to avoid too much loops.
        do m:=gbasis!* dpmat_sub(a,m);
    if s=10 then rederr" change to general position failed";
    return prime_gpchange(cdr vars,v,m);
    end;

symbolic procedure prime!=zeroprimes2 m;
% Decompose the radical zerodimensional dmpat ideal m using a general
% position argument. Returns a reduced list of gbases.
  (begin scalar c,v,vars,u,d,r;
    c:=cali!=basering; vars:=ring_names c; v:=make_cali_varname();
    u:=setdiff(vars,for each x in moid_from_bas dpmat_list m
                join {mo_linear x});
    if (length u)=1 then return prime!=zeroprimes3(m,first u);
    if ring_tag c='revlex then % for proper ring_sum redefine it.
        r:=ring_define(vars,ring_degrees c,'lex,ring_ecart c)
    else r:=c;
    setring!* ring_sum(r,ring_define(list v,nil,'lex,'(1)));
    cali!=degrees:=nil;
    m:=gbasis!* matsum!*
                {dpmat_neworder(m,nil), dpmat_from_dpoly dp_from_a v};
    u:=setdiff(v.vars,for each x in moid_from_bas dpmat_list m
                join {mo_linear x});
    if not dpmat_unitideal!? m then
      << m:=prime_gpchange(u,v,m);
         u:=for each x in prime!=zeroprimes3(m,v) join
             if not dpmat_unitideal!? x and
                not dpmat_unitideal!?(d:=eliminate!*(x,{v})) then {d}
                    % To recognize (1) even if x is not a gbasis.
      >>
    else u:=nil;
    setring!* c;
    return for each x in u collect gbasis!* dpmat_neworder(x,nil);
   end)
   where cali!=degrees:=cali!=degrees,
                cali!=basering:=cali!=basering;

symbolic procedure prime!=zeroprimes3(m,v);
% m is in general position with univariate polynomial in v.
  begin scalar u,p;
  u:=dpmat_list m;
  while u and not equal(mo_support dp_lmon (p:=bas_dpoly car u),
                        list v) do u:=cdr u;
  if null u then rederr"univariate polynomial not found";
  p:=for each x in cdr ((fctrf numr simp dp_2a p) where !*factor=t)
        collect dpmat_from_dpoly dp_from_a prepf car x;
  return for each x in p collect matsum!* {m,x};
  end;

symbolic procedure prime!=iszeroprime2 m;
% Test the radical zerodimensional dmpat ideal m to be prime using a
% general position argument.
  (begin scalar c,v,vars,u,r;
    c:=cali!=basering; vars:=ring_names c; v:=make_cali_varname();
    if ring_tag c='revlex then % for proper ring_sum redefine it.
        r:=ring_define(vars,ring_degrees c,'lex,ring_ecart c)
    else r:=c;
    setring!* ring_sum(r,ring_define(list v,nil,'lex,'(1)));
    cali!=degrees:=nil;
    m:=matsum!* {dpmat_neworder(m,nil), dpmat_from_dpoly dp_from_a v};
    m:=prime_gpchange(vars,v,gbasis!* m);
    u:=prime!=iszeroprime3(m,v);
    setring!* c; return u;
   end)
   where cali!=degrees:=cali!=degrees,
                cali!=basering:=cali!=basering;

symbolic procedure prime!=iszeroprime3(m,v);
  begin scalar u,p;
  u:=dpmat_list m;
  while u and not equal(mo_support dp_lmon (p:=bas_dpoly car u),
                        list v) do u:=cdr u;
  if null u then rederr"univariate polynomial not found";
  if (length(u:=cdr ((fctrf numr simp dp_2a p) where !*factor=t))>1)
        or (cdar u>1) then return nil
  else return t
  end;

% --------- Primality test for an arbitrary ideal. ---------

put('isprime,'psopfn,'prime!=isprime);
symbolic procedure prime!=isprime m;
  begin scalar c;
    intf_test m; intf_get(m:=car m);
    if not (c:=get(m,'gbasis)) then
        put(m,'gbasis,c:=gbasis!* get(m,'basis));
    return if isprime!* c then 'yes else 'no;
  end;

symbolic procedure isprime!* m;
% Test an dpmat ideal m to be prime. m must be a gbasis.
  if dpmat_cols m>0 then rederr"prime test only for ideals"
  else (begin scalar vars,u,v,c1,c2,m1,m2,lc;
    v:=moid_goodindepvarset m; cali!=degrees:=nil;
    if null v then return prime_iszeroprime m;
    vars:=ring_names(c1:=cali!=basering);
        % Change to dimension zero.
    u:=setdiff(ring_names c1,v);
    setring!* ring_rlp(c1,u);
    m1:=dpmat_2a gbasis!* dpmat_neworder(m,nil);
    setring!*(c2:= ring_define(u,degreeorder!* u,'revlex,
                        for each x in u collect 1));
    m1:=groeb_mingb dpmat_from_a m1;
    if dpmat_unitideal!?(m1) then
      << setring!* c1; rederr"Input must be a gbasis" >>;
    lc:=bc_2a prime!=quot m1; setring!* c1;
        % Test recontraction of m1 to be equal to m.
    m2:=gbasis!* matqquot!*(m,dp_from_a lc);
    if not submodulep!*(m2,m) then return nil;
        % Test the zerodimensional ideal m1 to be prime
    setring!* c2; u:=prime_iszeroprime m1; setring!* c1;
    return u;
   end)
   where cali!=degrees:=cali!=degrees,
                cali!=basering:=cali!=basering;

symbolic operator isolatedprimes;
symbolic procedure isolatedprimes m;
  if !*mode='algebraic then
        makelist for each x in isolatedprimes!* dpmat_from_a reval m
                collect dpmat_2a x
  else isolatedprimes!* m;

symbolic procedure isolatedprimes!* m;
% Returns the isolated primes of the dpmat m as a dpmat list.
  if !*factorprimes then
        listminimize(
        for each x in groebfactor!*(annihilator2!* m,nil) join
                prime!=factorisoprimes car x,
        function submodulep!*)
  else prime!=isoprimes gbasis!* annihilator2!* m;

symbolic procedure prime!=isoprimes m;
% m is a gbasis and an ideal.
  if dpmat_zero!? m then nil else
  (begin scalar u,c,v,vars,m1,m2,l,p;
    if null(v:=odim_parameter m) then return
        for each x in prime_zeroprimes1 m join prime!=zeroprimes2 x;
    vars:=ring_names(c:=cali!=basering); cali!=degrees:=nil;
    u:=delete(v,vars);
    setring!* ring_rlp(c,u);
    m1:=dpmat_2a gbasis!* dpmat_neworder(m,nil);
    setring!* ring_define(u,degreeorder!* u,
                        'revlex,for each x in u collect 1);
    p:=bc_2a prime!=quot(m1:=groeb_mingb dpmat_from_a m1);
    l:=for each x in prime!=isoprimes m1 collect
            (dpmat_2a x . bc_2a prime!=quot x);
    setring!* c;
    l:=for each x in l collect
                gbasis!* matqquot!*(dpmat_from_a car x,dp_from_a cdr x);
    if dp_unit!?(p:=dp_from_a p) or
        submodulep!*(matqquot!*(m,p),m) or
        dpmat_unitideal!?(m2:=gbasis!* matsum!* {m,dpmat_from_dpoly p})
                then return l
    else return
        listminimize(append(l,prime!=isoprimes  m2),
                        function submodulep!*);
   end)
   where cali!=degrees:=cali!=degrees,
                cali!=basering:=cali!=basering;

symbolic procedure prime!=factorisoprimes m;
% m is a gbasis and an ideal.
  if dpmat_zero!? m then nil else
  (begin scalar u,c,v,vars,m1,m2,l,p;
    if null(v:=odim_parameter m) then
        return for each x in groebf_zeroprimes1(m,nil) join
                prime!=zeroprimes2 x;
    vars:=ring_names(c:=cali!=basering); cali!=degrees:=nil;
    u:=delete(v,vars);
    setring!* ring_rlp(c,u);
    m1:=dpmat_2a gbasis!* dpmat_neworder(m,nil);
    setring!* ring_define(u,degreeorder!* u,
                        'revlex,for each x in u collect 1);
    p:=bc_2a prime!=quot(m1:=groeb_mingb dpmat_from_a m1);
    l:=for each x in prime!=factorisoprimes m1 collect
            (dpmat_2a x . bc_2a prime!=quot x);
    setring!* c;
    l:=listgroebfactor!* for each x in l collect
                matqquot!*(dpmat_from_a car x,dp_from_a cdr x);
    if dp_unit!?(p:=dp_from_a p) or
        submodulep!*(matqquot!*(m,p),m) or
        null (m2:=groebfactor!*(matsum!* {m,dpmat_from_dpoly p},nil))
                then return l
    else return
        listminimize(append(l,for each x in m2 join
                prime!=factorisoprimes car x), function submodulep!*);
   end)
   where cali!=degrees:=cali!=degrees,
                cali!=basering:=cali!=basering;

symbolic procedure prime!=quot m;
% The lcm of the leading coefficients of m.
  begin scalar p,u;
    u:=for each x in dpmat_list m collect dp_lc bas_dpoly x;
    if null u then return bc_fi 1;
    p:=car u; for each x in cdr u do p:=bc_lcm(p,x);
    return p
  end;

% ------------------- The radical ---------------------

symbolic operator radical;
symbolic procedure radical m;
% Returns the radical of the dpmat ideal m.
  if !*mode='algebraic then
        dpmat_2a radical!* gbasis!* dpmat_from_a reval m
  else radical!* m;

symbolic procedure radical!* m;
% m must be a gbasis.
  if dpmat_cols m>0 then rederr"RADICAL only for ideals"
  else (begin scalar u,c,v,vars,m1,l,p,p1;
    if null(v:=odim_parameter m) then return zeroradical!* m;
    vars:=ring_names (c:=cali!=basering);
    cali!=degrees:=nil; u:=delete(v,vars);
    setring!* ring_rlp(c,u);
    m1:=dpmat_2a gbasis!* dpmat_neworder(m,nil);
    setring!* ring_define(u,degreeorder!* u,
                        'revlex,for each x in u collect 1);
    p:=bc_2a prime!=quot(m1:=groeb_mingb dpmat_from_a m1);
    l:=radical!* m1; p1:=bc_2a prime!=quot l;
    l:=dpmat_2a l; setring!* c;
    l:=gbasis!* matqquot!*(dpmat_from_a l,dp_from_a p1);
    if dp_unit!?(p:=dp_from_a p) or
    submodulep!*(matqquot!*(m,p),m) then return l
    else << m1:=radical!* gbasis!* matsum!* {m,dpmat_from_dpoly p};
            if submodulep!*(m1,l) then l:=m1
            else if not submodulep!*(l,m1) then
                    l:= matintersect!* {l,m1};
         >>;
    return l;
   end)
   where cali!=degrees:=cali!=degrees,
                cali!=basering:=cali!=basering;

% ------------------- The unmixed radical ---------------------

symbolic operator unmixedradical;
symbolic procedure unmixedradical m;
% Returns the radical of the dpmat ideal m.
  if !*mode='algebraic then
        dpmat_2a unmixedradical!* gbasis!* dpmat_from_a reval m
  else unmixedradical!* m;

symbolic procedure unmixedradical!* m;
% m must be a gbasis.
  if dpmat_cols m>0 then rederr"UNMIXEDRADICAL only for ideals"
  else (begin scalar u,c,d,v,vars,m1,l,p,p1;
    if null(v:=moid_goodindepvarset m) then return zeroradical!* m;
    vars:=ring_names (c:=cali!=basering);
    d:=length v; u:=setdiff(vars,v);
    setring!* ring_rlp(c,u);
    m1:=dpmat_2a gbasis!* dpmat_neworder(m,nil);
    setring!* ring_define(u,degreeorder!* u,'revlex,
                for each x in u collect 1);
    p:=bc_2a prime!=quot(m1:=groeb_mingb dpmat_from_a m1);
    l:=zeroradical!* m1; p1:=bc_2a prime!=quot l;
    l:=dpmat_2a l; setring!* c;
    l:=matqquot!*(dpmat_from_a l,dp_from_a p1);
    if dp_unit!?(p:=dp_from_a p) then return l
    else << m1:=gbasis!* matsum!* {m,dpmat_from_dpoly p};
            if dim!* m1=d then
                l:= matintersect!* {l,unmixedradical!* m1};
         >>;
    return l;
   end)
   where cali!=degrees:=cali!=degrees,
                cali!=basering:=cali!=basering;

% ------------------- The equidimensional hull---------------------

symbolic operator eqhull;
symbolic procedure eqhull m;
% Returns the radical of the dpmat ideal m.
  if !*mode='algebraic then
        dpmat_2a eqhull!* gbasis!* dpmat_from_a reval m
  else eqhull!* m;

symbolic procedure eqhull!* m;
% m must be a gbasis.
  begin scalar d;
  if (d:=dim!* m)=0 then return m
  else return prime!=eqhull(m,d)
  end;

symbolic procedure prime!=eqhull(m,d);
% d(>0) is the dimension of the dpmat m.
  (begin scalar u,c,v,vars,m1,l,p;
  v:=moid_goodindepvarset m;
  if length v neq d then
        rederr "EQHULL found a component of wrong dimension";
  vars:=ring_names(c:=cali!=basering);
  cali!=degrees:=nil; u:=setdiff(ring_names c,v);
  setring!* ring_rlp(c,u);
  m1:=dpmat_2a gbasis!* dpmat_neworder(m,nil);
  setring!* ring_define(u,degreeorder!* u,'revlex,
                                for each x in u collect 1);
  p:=bc_2a prime!=quot(m1:=groeb_mingb dpmat_from_a m1);
  setring!* c; cali!=degrees:=dpmat_coldegs m;
  if submodulep!*(l:=matqquot!*(m,dp_from_a p),m) then return m;
  m1:=gbasis!* matstabquot!*(m,annihilator2!* l);
  if dim!* m1=d then return matintersect!* {l,prime!=eqhull(m1,d)}
  else return l;
   end)
   where cali!=degrees:=cali!=degrees,
                cali!=basering:=cali!=basering;

% ---------- Primary Decomposition Algorithms ------------

Comment

by [GTZ]'s approach:
        - Compute successively a list {(Q_i,p_i)} of pairs
                (primary module, associated prime ideal)
          such that
                Q = \intersection{Q_i}
        - figure out the superfluous components

(Note, that different to our former opinion (v. 2.2.) it is not
sufficient to extract the elements from that list, that are minimal
wrt. inclusion for the primary component. There may be components,
containing none of these minimal primaries, but containing their
intersection !!)

Primary decompositions return a list of {Q,P} pairs with prime
ideal P and corresponding primary component Q.

end comment;

% - The primary decomposition of a zerodimensional ideal or module -

symbolic procedure prime_separate l;
% l is a list of (gbases of) prime ideals.
% Returns a list of (p . f) with p \in l and dpoly f \in all q \in l
% except p.
  for each x in l collect (x . prime!=polynomial(x,delete(x,l)));

symbolic procedure prime!=polynomial(x,l);
% Returns a dpoly f inside all q \in l and outside x.
  if null l then dp_fi 1
  else begin scalar u,p,q;
    p:=prime!=polynomial(x,cdr l);
    if null matop_pseudomod(p,car l) then return p;
    u:=dpmat_list car l;
    while u and null matop_pseudomod(q:=bas_dpoly car u,x) do u:=cdr u;
    if null u then
        rederr"prime ideal separation failed"
    else return dp_prod(p,q);
  end;

symbolic operator zeroprimarydecomposition;
symbolic procedure zeroprimarydecomposition m;
% Returns a list of {Q,p} with p a prime ideal and Q a p-primary
% component of m. For m=S^c the list is empty.
  if !*mode='algebraic then
        makelist for each x in
                zeroprimarydecomposition!* dpmat_from_a reval m
        collect makelist {dpmat_2a first x,dpmat_2a second x}
  else zeroprimarydecomposition!* m;

symbolic procedure zeroprimarydecomposition!* m;
% The symbolic counterpart, returns a list of {Q,p}. m is not
% assumed to be a gbasis.
    if not dimzerop!* m then rederr
 "zeroprimarydecomposition only for zerodimensional ideals or modules"
    else for each f in prime_separate
            (for each y in zeroprimes!* m collect gbasis!* y)
        collect {matqquot!*(m,cdr f),car f};

% -- Primary decomposition for modules without embedded components ---

symbolic operator easyprimarydecomposition;
symbolic procedure easyprimarydecomposition m;
  if !*mode='algebraic then
        makelist for each x in
                easyprimarydecomposition!* dpmat_from_a reval m
        collect makelist {dpmat_2a first x,dpmat_2a second x}
  else easyprimarydecomposition!* m;

symbolic procedure easyprimarydecomposition!* m;
% Primary decomposition for a module without embedded components.
   begin scalar u; u:=isolatedprimes!* m;
      return if null u then nil
        else if length u=1 then {{m,car u}}
        else for each f in
        prime_separate(for each y in u collect gbasis!* y)
                    collect {matqquot!*(m,cdr f),car f};
  end;

% ---- General primary decomposition ----------

symbolic operator primarydecomposition;
symbolic procedure primarydecomposition m;
  if !*mode='algebraic then
        makelist for each x in
                primarydecomposition!* gbasis!* dpmat_from_a reval m
        collect makelist {dpmat_2a first x,dpmat_2a second x}
  else primarydecomposition!* m;

symbolic procedure primarydecomposition!* m;
% m must be a gbasis. The [GTZ] approach.
  if dpmat_cols m=0 then
    for each x in prime!=decompose1 ideal2mat!* m collect
        {mat2list!* first x,second x}
  else prime!=decompose1 m;

% --------------- Implementation of the [GTZ] approach

symbolic procedure prime!=decompose1 m;
% The method as in the final version of the paper: Dropping dimension
% by one in each step.
  (begin scalar u,c,v,vars,m1,l,l1,p,q;
    if null(v:=odim_parameter m) then
            return zeroprimarydecomposition!* m;
    vars:=ring_names (c:=cali!=basering);
    cali!=degrees:=nil; u:=delete(v,vars);
    setring!* ring_rlp(c,u);
    m1:=dpmat_2a gbasis!* dpmat_neworder(m,nil);
    setring!* ring_define(u,degreeorder!* u,
                                'revlex,for each x in u collect 1);
    p:=bc_2a prime!=quot(m1:=groeb_mingb dpmat_from_a m1);
    l:=for each x in prime!=decompose1 m1 collect
          {(dpmat_2a first x . bc_2a prime!=quot first x),
           (dpmat_2a second x . bc_2a prime!=quot second x)};
    setring!* c;
    l:=for each x in l collect
    << cali!=degrees:=dpmat_coldegs m;
       {gbasis!* matqquot!*(dpmat_from_a car first x,
                                dp_from_a cdr first x),
        gbasis!* matqquot!*(dpmat_from_a car second x,
                                dp_from_a cdr second x)}
    >>;
    if dp_unit!?(p:=dp_from_a p) or
        submodulep!*(m1:=matqquot!*(m,p),m) then return l
    else
      << q:=p; v:=1;
         while not submodulep!*(m1:=dpmat_times_dpoly(p,m1),m)
                and (v<15) do << q:=dp_prod(p,q); v:=v+1 >>;
        if (v=15) then
                rederr"Power detection in prime!=decompose1 failed";
        l1:=prime!=decompose1 gbasis!* matsum!*
                {m, dpmat_times_dpoly(q,
                        dpmat_unit(dpmat_cols m,dpmat_coldegs m))};

Comment

At this moment M = M:<p>\intersection (M,q*F), q=p^n, and
        - l is the list of primary comp., lifted from the first part
                (they are lifted from a localization and have p as non
                        zero divisor)
        - l1 is the list of primary comp. of the second part
                (which have p as zero divisor and should be tested
                against M, whether they are indeed necessary)

end comment;

        p:=append(for each x in l collect second x,
                for each x in l1 collect second x);
        l:=append(l,for each x in l1 join
                if prime!=necessary(second x,m,p) then {x});

      >>;
    return l;
   end)
   where cali!=degrees:=cali!=degrees,
                cali!=basering:=cali!=basering;

symbolic procedure prime!=decompose2 m;
% The method as in [BKW] : Reducing directly to dimension zero. This
% is usually a quite bad guess.
  (begin scalar u,c,v,vars,m1,l,l1,p,q;
    v:=moid_goodindepvarset m;
    if null v then return zeroprimarydecomposition!* m;
    vars:=ring_names (c:=cali!=basering);
    cali!=degrees:=nil; u:=setdiff(vars,v);
    setring!* ring_rlp(c,u);
    m1:=dpmat_2a gbasis!* dpmat_neworder(m,nil);
    setring!* ring_define(u,degreeorder!* u,
                                'revlex,for each x in u collect 1);
    p:=bc_2a prime!=quot(m1:=groeb_mingb dpmat_from_a m1);
    l:=for each x in zeroprimarydecomposition!* m1 collect
          {(dpmat_2a first x . bc_2a prime!=quot first x),
           (dpmat_2a second x . bc_2a prime!=quot second x)};
    setring!* c;
    l:=for each x in l collect
    << cali!=degrees:=dpmat_coldegs m;
       {gbasis!* matqquot!*(dpmat_from_a car first x,
                                dp_from_a cdr first x),
        gbasis!* matqquot!*(dpmat_from_a car second x,
                                dp_from_a cdr second x)}
    >>;
    if dp_unit!?(p:=dp_from_a p) or
        submodulep!*(m1:=matqquot!*(m,p),m) then return l
    else
      << q:=p; v:=1;
         while not submodulep!*(m1:=dpmat_times_dpoly(p,m1),m)
                and (v<15) do << q:=dp_prod(p,q); v:=v+1 >>;
        if (v=15) then
                rederr"Power detection in prime!=decompose2 failed";
        l1:=prime!=decompose2 gbasis!* matsum!*
                {m, dpmat_times_dpoly(q,
                        dpmat_unit(dpmat_cols m,dpmat_coldegs m))};
        p:=append(for each x in l collect second x,
                for each x in l1 collect second x);
        l:=append(l,for each x in l1 join
                if prime!=necessary(second x,m,p) then {x});
      >>;
    return l;
   end)
   where cali!=degrees:=cali!=degrees,
                cali!=basering:=cali!=basering;

symbolic procedure prime!=necessary(P,m,l);
% P a prime to be testet, M the original module, l the list of
% (possibly) associated primes of M, including P.
% Returns true <=> P is an embedded prime.

begin scalar l1,unit;
  l1:=for each u in l join if (u=p) or submodulep!*(u,p) then {t};
  if null l1 then
        rederr"prime!=necessary: supplied prime's list incorrect";
  if length l1 = 1 then % P is an isolated prime.
        return t;
  unit:=dpmat_unit(dpmat_cols m,cali!=degrees);
        % Unit matrix for reference.
  l1:=for each u in l join if not submodulep!*(u,p) then {u};
        % L_1 = Primes not contained in P.
  l:=delete(p,setdiff(l,l1)); % L = Primes contained in P.
  m:=matqquot!*(m,prime!=polynomial(p,l1));
        % Ass M is now contained in L \union (P).
  return not submodulep!*(matstabquot!*(m,p),m);
  end;

endmodule; % prime

end;
