module perms;

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


% returns product of two permutations

symbolic procedure pe_mult(p1, p2);
  begin scalar prod;
  integer count;
  prod := mkve(upbve(p1));
  for count := 1:upbve(p1) do
    putve(prod, count, venth(p2, venth(p1, count)));
  return prod;
  end;

% returns inverse of permutation
symbolic procedure pe_inv(pe);
  begin
  scalar inv;
  integer count;
  inv := mkve(upbve(pe));
  for count := 1:upbve(pe) do
    putve(inv, venth(pe, count), count);
  return inv;
  end;

% returns image of elt by permutation pe
symbolic smacro procedure pe_apply(pe, elt);
  venth(pe, elt);


%%% Stabilizer chain routines

%% Access macros

symbolic smacro procedure sc_orbits(sc, k);
  venth(venth(cdr sc, k), 1);

symbolic smacro procedure sc_transversal(sc,k);
  venth(venth(cdr sc, k), 2);

symbolic smacro procedure sc_generators(sc,k);
  venth(venth(cdr sc, k), 3);

symbolic smacro procedure sc_inv_generators(sc,k);
  venth(venth(cdr sc, k),4);

symbolic smacro procedure sc_stabdesc(sc, k);
  venth(cdr sc, k);

symbolic smacro procedure sd_orbrep(sd, elt);
  venth(venth(sd,1),elt);

symbolic smacro procedure sd_orbreps(sd);
  venth(sd,5);

%% Building routines

symbolic procedure copy_vect(v1, v2);
  begin
  integer count, top;
  top := upbv v2;
  for count := 0 : top do
    putv(v1, count, getv(v2, count));
  end;

symbolic procedure sd_addgen(sd, pe, inv);
  begin scalar
    t1, t2, orbits, orbreps, transversal, generators, inv_generators,
    new_elems, next_elem;
       integer
    count, img;

%% initialize local variables
  orbits := venth(sd, 1);
  transversal := venth(sd, 2);

%% add generator and inverse
  generators := vectappend1(venth(sd,3), pe);
  inv_generators := vectappend1(venth(sd,4), inv);

%% Join elements from the orbits.
  for count := 1 : upbve(orbits) do
    <<
    t1 := venth(orbits, count);
    while (t1 neq venth(orbits, t1)) do t1 := venth(orbits, t1);
    t2 := venth(orbits, pe_apply(pe, count));
    while (t2 neq venth(orbits, t2)) do t2 := venth(orbits, t2);
    if (t1 < t2) then
      putve(orbits, t2, t1)
    else
      putve(orbits, t1, t2)
    >>;
  for count := 1 : upbve(orbits) do
    <<
    putve(orbits, count, venth(orbits, venth(orbits, count)));
    if venth(orbits, count) = count then
      orbreps := count . orbreps
    >>;

%% extend transversal
  % add images of elements of basic orbit by pe to new_elems
  for count := 1 : upbve(transversal) do
    <<
    if venth(transversal, count) then
      <<
      img := pe_apply(pe, count);
      if null(venth(transversal, img)) then
        <<
        putve(transversal, img, inv);
        new_elems := img . new_elems
        >>
      >>
    >>;
  % add all possible images of each new_elems to the transversal
  while new_elems do
    <<
    next_elem := car new_elems;
    new_elems := cdr new_elems;
    for count := 1 : upbve(generators) do
      <<
      img := pe_apply(venth(generators, count), next_elem);
      if null(venth(transversal, img)) then
        <<
        putve(transversal, img, venth(inv_generators, count));
        new_elems := img . new_elems;
        >>
      >>
    >>;

%% update sd
  putve(sd, 1, orbits);
  putve(sd, 2, transversal);
  putve(sd, 3, generators);
  putve(sd, 4, inv_generators);
  putve(sd, 5, orbreps);
  return sd;
  end;

symbolic procedure sd_create(n, beta);
  begin
  scalar sd, orbits, transversal;
  integer count;
  sd := mkve(5);
  orbits := mkve(n);
for count := 1:n do
    putve(orbits, count, count);
  transversal := mkve(n);
  putve(transversal, beta, 0);

  putve(sd, 1, orbits);
  putve(sd, 2, transversal);
  putve(sd, 3, mkve(0));
  putve(sd, 4, mkve(0));
  putve(sd, 5, for count := 1:n collect count);
  return sd
  end;

symbolic procedure sc_create(n);
  begin
  scalar base;
  integer count;
  for count := n step -1 until 1 do
    base := count . base;
  return ((list2vect!*(base,'symbolic)) . mkve(n));
  end;

symbolic procedure sd_recomp_transversal(sd, beta);
  begin
  scalar
    new_trans,
    new_elems, next_elem,
    generators, inv_generators,
    img;
  integer count;

  new_trans := mkve(upbve(venth(sd,1)));
  new_elems := beta . nil;
  putve(new_trans, beta, 0);
  generators := venth(sd,3);
  inv_generators := venth(sd,4);

  while new_elems do
    <<
    next_elem := car new_elems;
    new_elems := cdr new_elems;
    for count := 1 : upbve(generators) do
      <<
      img := pe_apply(venth(generators, count), next_elem);
      if null(venth(new_trans, img)) then
        <<
        putve(new_trans, img, venth(inv_generators, count));
        new_elems := img . new_elems;
        >>
      >>
    >>;
  putve(sd, 2, new_trans);
  return sd;
  end;

symbolic procedure sc_swapbase(sc, k);
  begin scalar
    sd,                    % stab desc being constructed
    pe, inv_pe,
    nu_1, nu_2,
    sd_reps_orb1,          % O_k \cap orbit reps of sd \ beta_k
    b_orb2;                % O_k+1

  integer
    b_1, b_2,              % reps of basic orbits of G_k and G_k+1
    img,
    sigma, swap,
    count,
    ngens,
    elt;

%% take care of nil stabilizer descriptions

 % if k'th sd is null, then the base may be changed with no other modif
  if null sc_stabdesc(sc,k) then
    <<
    swap := venth(car sc, k);
    putve(car sc, k , venth(car sc, k+1));
    putve(car sc, k+1, swap);
    return sc
    >>;

 % if k+1'th sd is null, then one must create a trivial
 % stabilizer desc
  if null sc_stabdesc(sc,k+1) then
    putve(cdr sc, k+1, sd_create(upbve(car sc), venth(car sc, k+1)));

%% initialize sd to copy of stabdesc(k+2), changing the basic rep

  if (k+2 > upbve(car sc)) or null sc_stabdesc(sc, k+2) then
    sd := sd_create(upbve(car sc), venth(car sc, k))
  else
    <<
    sd := mkve(5);
    putve(sd, 1, fullcopy(sc_orbits(sc, k+2)));
    % make copy of generators, but not total copy
    ngens := upbve(sc_generators(sc, k+2));
    putve(sd, 3, mkve(ngens));
    putve(sd, 4, mkve(ngens));
    for count := 1 : ngens do
      <<
      putve(venth(sd, 3), count, venth(sc_generators(sc, k+2), count));
      putve(venth(sd,4), count, venth(sc_inv_generators(sc,k+2),count))
      >>;
    putve(sd, 5, venth(venth(cdr sc, k+2),5));
    sd_recomp_transversal(sd, venth(car sc, k));
    >>;

%% initialize sd_reps_orb1 and b_orb2
  for count := 1:upbve(car sc) do
    <<
    if venth(sc_transversal(sc, k+1), count) then
      b_orb2 := count . b_orb2;
    if venth(sc_transversal(sc, k), count) then
      sd_reps_orb1 := count . sd_reps_orb1
    >>;

  sd_reps_orb1 :=
    intersection(sd_reps_orb1, venth(sd, 5));

  b_1 := venth(car sc, k);
  b_2 := venth(car sc, k+1);

  sd_reps_orb1 := delete(venth(car sc, k), sd_reps_orb1);
%% join orbits of sd by joining elts of sd_reps_orb1
  while sd_reps_orb1 do
    <<
    elt := car sd_reps_orb1;
    sd_reps_orb1 := cdr sd_reps_orb1;
    nu_1 := nu_2 := nil;
    img := elt;
    while (img neq b_1) do
      <<
      nu_1 :=
        if nu_1 then
          pe_mult(nu_1, venth(sc_transversal(sc,k),img))
        else
          venth(sc_transversal(sc,k),img);
      img := pe_apply(nu_1, elt);
      >>;
    sigma := pe_apply(nu_1, b_2);
    if member(sigma, b_orb2) then
      <<
      img := sigma;
      while (img neq b_2) do
        <<
        nu_2 :=
          if nu_2 then
            pe_mult(nu_1, venth(sc_transversal(sc,k+1),img))
          else
            venth(sc_transversal(sc,k+1),img);
        img := pe_apply(nu_2, sigma);
        >>;
      if nu_2 then
        pe := pe_mult(nu_1, nu_2)
      else
        pe := nu_1;
      inv_pe := pe_inv(pe);
      sd_addgen(sd, pe, inv_pe);
      %% update sd_reps_orb1
      %% nu_1 taken as temp storage
      nu_1 := nil;
      for each img in sd_reps_orb1 do
        if sd_orbrep(sd, img)= img then
          nu_1 := img . nu_1;
      sd_reps_orb1 := nu_1;
      >>
    >>;
%% update base specifications
  swap := venth(car sc, k);
  putve(car sc, k, venth(car sc, k+1));
  putve(car sc, k+1, swap);
%% sd is new description of stabilizer at level k+1 of sc
  putve(cdr sc, k+1, sd);
%% update transversal for sd(k), as base element has changed
  sd_recomp_transversal(sc_stabdesc(sc, k), venth(car sc, k));
  return sc;
  end;

symbolic procedure sc_setbase(sc, base_vect);
  begin integer count, k;
  for count := 1:upbve(base_vect) do
    <<
    if venth(base_vect, count) neq venth(car sc, count) then
      for k := index_elt(venth(base_vect, count), car sc)-1
               step -1 until count do sc_swapbase(sc, k)
    >>;
  end;

endmodule;

end;
