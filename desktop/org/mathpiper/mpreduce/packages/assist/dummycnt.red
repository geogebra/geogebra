module dummycnt;

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


fluid '(g_dvnames g_dvbase g_sc_ve g_init_stree g_skip_to_level
        !*distribute);

%%%%%%%%%%%%%%%%%%%%%%% MISCELANEOUS ROUTINES %%%%%%%%%%%%%%%%%%%%%%%%%
symbolic procedure ad_splitname(u);
  if idp u then
    begin scalar uu, nn;
    uu := reverse(explode u);
    while uu and (charnump!: car uu) do
      <<
      nn := car uu . nn;
      uu := cdr uu;
      >>;
    if uu then uu := intern(compress(reverse(uu)));
    if nn then nn := compress(nn);
    return (uu.nn);
    end;

symbolic procedure anticom_assoc(u,v);
  begin  scalar next_cell;
  if null v then
    return nil
  else if u = caar v then
    return (1 . car v)
  else
    <<
    next_cell := anticom_assoc(u, cdr v);
    if null next_cell then return nil;
    if oddp(length(cdar v)) then
      rplaca(next_cell, - car next_cell);
    return next_cell;
    >>;
  end;

%%%%%%%%%%%%%%%%%%%%%%% ORDERING FUNCTIONS %%%%%%%%%%%%%%%%%%%%
symbolic procedure ad_signsort(l, fn);
 begin scalar  tosort, sorted, insertl, dig;
        integer  thesign;
  tosort := copy l;
  thesign := 1;
  sorted := nil;
  while tosort do
    if null sorted then
      <<
      sorted := car tosort . sorted;
      tosort := cdr tosort;
      >>
    else if car tosort = car sorted then
      <<
      thesign := 0;
      sorted := tosort := nil;
      >>
    else if apply(fn, {car sorted, car tosort}) then
      <<
      sorted := car tosort . sorted;
      tosort := cdr tosort;
      >>
    else
      <<
      thesign := - thesign;
      insertl := sorted;
      dig := t;
      while dig do
        if null cdr insertl then
          dig := nil
        else if cadr insertl = car tosort then
          <<
          insertl := {nil};
          dig := nil;
          thesign := 0;
          sorted := tosort := nil;
          >>
        else if not apply(fn, {cadr insertl, car tosort}) then
          <<
          insertl := cdr insertl;
          thesign := - thesign;
          >>
        else
          dig := nil;
      if tosort then
        <<
        rplacd(insertl, (car tosort) . cdr insertl);
        tosort := cdr tosort;
        >>;
      >>;
 return (thesign . reverse sorted);
 end;

symbolic procedure cdr_sort(lst, fn);
  begin scalar  tosort, sorted, insertl;
  tosort := lst;
  while tosort do
    <<
    if (null sorted) or apply(fn, {cdar sorted, cdar tosort}) then
      <<
      sorted := car tosort . sorted;
      tosort := cdr tosort;
      >>
    else
      <<
      insertl := sorted;
      while (cdr insertl) and
                    not(apply(fn, {cdadr insertl, cdar tosort})) do
        insertl := cdr insertl;
      rplacd(insertl, (car tosort) . cdr insertl);
      tosort := cdr tosort
      >>
    >>;
  return reverse sorted;
  end;

symbolic procedure cdr_signsort(l, fn);
  begin scalar  tosort, sorted, insertl, dig;
        integer thesign;
  tosort := copy l;
  thesign := 1;
  sorted := nil;
  while tosort do
    if null sorted then
      <<
      sorted := car tosort . sorted;
      tosort := cdr tosort;
      >>
    else if cdar tosort = cdar sorted then
      <<
      thesign := 0;
      sorted := tosort := nil;
      >>
    else if apply(fn, {cdar sorted, cdar tosort}) then
      <<
      sorted := car tosort . sorted;
      tosort := cdr tosort;
      >>
    else
      <<
      thesign := - thesign;
      insertl := sorted;
      dig := t;
      while dig do
        if null cdr insertl then
          dig := nil
        else if cdadr insertl = cdar tosort then
          <<
          dig := nil;
          thesign := 0;
          sorted := tosort := nil;
          >>
        else if not apply(fn, {cdadr insertl, cdar tosort}) then
          <<
          insertl := cdr insertl;
          thesign := - thesign;
          >>
        else
          dig := nil;
      if tosort then
        <<
        rplacd(insertl, (car tosort) . cdr insertl);
        tosort := cdr tosort
        >>;
      >>;
  return (thesign . reverse sorted);
  end;

symbolic procedure num_signsort(l);
  ad_signsort(l, function(lambda(x,y); x <= y));

symbolic procedure cons_ordp(u,v, fn);
  if null u then t
  else if null v then nil
  else if pairp u then
    if pairp v then
      if car u = car v then
        cons_ordp(cdr u, cdr v, fn)
      else
        cons_ordp(car u, car v, fn)
    else
      nil
  else if pairp v then t
  else apply2(fn,u,v);

symbolic procedure atom_compare(u,v);
  if numberp u then numberp v and not(u < v)
  else if idp v then orderp(u,v)
  else numberp v;

symbolic procedure idcons_ordp(u,v);
  cons_ordp(u, v, function atom_compare);

symbolic procedure skp_ordp(u,v);
  cons_ordp(car u, car v, function atom_compare);

symbolic procedure numlist_ordp(u,v);
  cons_ordp(u,v,function(lambda(x,y); x <= y));

symbolic procedure ad_numsort(l);
  sort(l,function(lambda(x,y); x <= y));

%%%%%%%%%%%%%%%%%%%%%%% ACCESS ROUTINES %%%%%%%%%%%%%%%%%%%%%%%%%

symbolic procedure sc_kern(ind);
  caddr venth(g_sc_ve, ind);

symbolic procedure sc_rep(ind);
  cadr venth(g_sc_ve, ind);

symbolic procedure sc_desc(ind);
  car venth(g_sc_ve, ind);

symbolic procedure dummyp(var);
  begin scalar varsplit;
       integer count, res;
  if not idp var then return nil;
  count := 1;
  while count <= upbve(g_dvnames) do
    <<
   if var = venth(g_dvnames, count) then
    <<
      res := count;
      count := upbve(g_dvnames) + 1
      >>
    else
      count := count + 1;
    >>;
  if res eq 0 then
    <<
    varsplit := ad_splitname(var);
    if (car varsplit eq g_dvbase) then
      return cdr varsplit
    >>
  else return res;
  end;

symbolic procedure dv_ind2var(ind);
  if ind <= upbve(g_dvnames) then
    venth(g_dvnames, ind)
  else
    mkid(g_dvbase, ind);

%%%%%%%%%%%%%%%%%%%%%% SYMMETRY CELLS %%%%%%%%%%%%%%%%%%%%%%%%%%

symbolic procedure sc_repkern(s_cell, n);
  if car s_cell eq '!* then               % nil symmetric cell
    begin scalar  kern, rest, next_rest;
          integer head, rep;
    rest := cdr s_cell;
    rep := 0;
    while rest do
      <<
      head := car rest;
      kern := {head} . kern;
      rest := cdr rest;
      next_rest := nil;
      rep := rep*2 + 1;
      for each elt in rest do
        <<
        if elt eq head then
          rep := rep * 2 + 1
        else
          <<
          rep := rep * 2;
          next_rest := elt . next_rest
          >>
        >>;
      rest := reverse next_rest;
      >>;
    return {rep, pa_list2vect(reverse kern, n)};
    end
  else
    begin scalar  count, replist, rep, kern;
          integer last_count;
    s_cell := cdr s_cell; % s_cell supposed sorted
    for each elt in s_cell do
      if (count := assoc(elt, replist)) then
        rplacd (count, cdr count + 1)
      else
        replist := (elt . 1) . replist;
    replist := sort(replist, function(lambda(x,y); cdr x <= cdr y));
    last_count := 0;
    for each elt in replist do
      if (cdr elt neq last_count) then
        <<
        rep := (cdr elt . 1) . rep;
        kern := {car elt} . kern;
        last_count := cdr elt;
        >>
      else
        <<
        rplacd(car rep, cdar rep + 1);
        rplaca(kern, car elt . car kern)
        >>;
    return {rep , pa_list2vect(kern, n)};
    end;

%%%%%%%%%%%%%%%%%%%%% PARTITIONS COMP %%%%%%%%%%%%%%%%%%%%%%%%%%

symbolic procedure pa_list2vect(pa, n);
  begin scalar  ve, reps;
        integer abs;
  ve := mkve(n);
  for each cell in pa do
    <<
    reps := eval('min . cell) . reps;
    for each elt in cell do putve(ve, elt, car reps);
    >>;
  for count := 1:n do
    <<
    if null venth(ve, count) then
      <<
      if abs = 0 then
        <<
        abs := count;
        reps := abs . reps
        >>;
      putve(ve, count, abs)
      >>
    >>;
  return ((reverse reps) . ve);
  end;

symbolic procedure pa_part2list(p);
  begin scalar  ve;
        integer len, rep;
  len := upbve(cdr p);
  ve := mkve(len);
  for count := len step -1 until 1 do
    <<
    rep := venth(cdr p, count);
    putve(ve, rep, count . venth(ve, rep));
    >>;
  return for each count in car p join copy
    venth(ve,count);
  end;

symbolic procedure pa_vect2list(pa);
  begin scalar  ve;
        integer count, rep;
  ve := mkve(upbve(cdr pa));
  for count := 1 : upbve(cdr pa) do
    <<
    rep := venth(cdr pa, count);
    putve(ve, rep, count . venth(ve,rep));
    >>;
  return for each rep in (car pa) collect ordn(venth(ve, rep));
  end;

symbolic procedure pa_coinc_split(p1, p2);
  begin
     scalar  ve1, ve2, cursplit, split_alist, split_info, coinc, split;
     integer count, plength;
  plength := upbve(cdr p1);
  ve1 := mkve(plength);
  ve2 := mkve(plength);
  split := mkve(plength);
  count := 0;
  for each rep in car p1 do
    <<
    count := count + 1;
    putve(ve1, rep, count)
    >>;
  count := 0;
  for each rep in car p2 do
    <<
    count := count + 1;
    putve(ve2, rep, count)
    >>;
  for count := 1 : plength do
    <<
    cursplit := (venth(ve1, venth(cdr p1, count)) .
                 venth(ve2, venth(cdr p2, count)));
    if (split_info := assoc(cursplit, split_alist)) then
      <<
      rplacd(cdr split_info, cddr split_info + 1);
      putve(split, count, cadr split_info)
      >>
    else
      <<
      split_info := cursplit . (count . 1);
      split_alist := split_info . split_alist;
      putve(split, count, count)
      >>
    >>;
  split_alist :=
     sort(split_alist,
          function(lambda x,y;
            if caar x < caar y then t
            else if caar y < caar x then nil
            else cdar x leq cdar y));
  split := (for each cell in split_alist collect cadr cell) . split;
  coinc := for each cell in split_alist collect
    (car cell) . (cddr cell);
  return coinc . split;
  end;

%%%%%%%%%%%%%%%%%%%%% SYMMETRY TREES %%%%%%%%%%%%%%%%%%%%%%%%%%%

symbolic procedure st_flatten(stree);
  if numberp(cadr stree) then
    cdr stree
  else
    for each elt in cdr stree join copy st_flatten(elt);

symbolic procedure st_extract_symcells(stree, maxind);
  begin scalar  ve, symcells;
        integer count;
  if null stree then return (nil . mkve(0));
  symcells := st_extract_symcells1(st_consolidate(stree),nil,1);
  stree := car symcells;
  if not listp stree then % stree is a single symcell
    stree := {'!* , stree};
  symcells := cadr symcells;
  ve := mkve(length(symcells));
  count := upbve(ve);
  while symcells do
    <<
    putve(ve, count, car symcells . sc_repkern(car symcells, maxind));
    symcells := cdr symcells;
    count := count - 1
    >>;
  return(st_consolidate(stree) . ve);
  end;

symbolic procedure st_extract_symcells1(stree, symcells, count);
  begin scalar  res, new_stree;
  if not listp cadr stree then % stree is a symcell
    return { count , stree . symcells, count + 1}
  else
    <<
    new_stree := car stree .
      for each inner_stree in cdr stree collect
        <<
        res := st_extract_symcells1(inner_stree, symcells, count);
        symcells := cadr res;
        count := caddr res;
        if numberp car res then
          {'!*, car res}
        else
          car res
        >>;
    return ({ new_stree, symcells, count })
    >>;
  end;

symbolic procedure st_signchange(ve1, ve2);
  car st_signchange1(g_init_stree, vect2list ve1) *
  car st_signchange1(g_init_stree, vect2list ve2);

symbolic procedure st_signchange1(stree, eltlist);
  begin scalar  levlist, elt_levlist, subsign;
        integer the_sign;
  the_sign := 1;
  levlist := for each child in cdr stree collect
    if numberp child then
      child
    else
      <<
      subsign := st_signchange1(child, eltlist);
      the_sign := the_sign * car subsign;
      cdr subsign
      >>;
  if not cdr levlist then return (the_sign . car levlist);
  elt_levlist := eltlist;
  if member(car eltlist, levlist) then
    elt_levlist := 0 . elt_levlist
  else while not member(cadr elt_levlist, levlist) do
    elt_levlist := cdr elt_levlist;
%% cdr elt_levlist starts with the elements of levlist
%% Compute the sign change
  if car stree eq '!- and not permp(levlist, cdr elt_levlist) then
      the_sign := - the_sign;
%% remove from elt_levlist (and thus from eltlist)
%% the elements of levlist except the last (which will be the
%% ref).
  rplacd(elt_levlist, pnth(cdr elt_levlist, length(levlist)));
  return (the_sign . cadr elt_levlist);
  end;

symbolic procedure st_sorttree(stree, ve, fn);
  cdr st_sorttree1(stree, ve, fn);

symbolic procedure st_sorttree1(stree, ve, fn);
  begin scalar  schild, vallist, sorted, thesign, tosort;
  thesign := 1;
  if numberp cadr stree then
    <<
    if car stree eq '!* then
      <<
      vallist := for each elt in cdr stree collect venth(ve,elt);
      return (vallist . (1 . stree))
      >>;
    tosort := for each elt in cdr stree collect
      elt . venth(ve,elt);
    >>
  else
    <<
    if (car stree) eq '!* then
      <<
      for each child in cdr stree do
        if thesign neq 0 then
          <<
          schild := st_sorttree1(child, ve, fn);
          thesign := thesign * cadr schild;
          vallist := (car schild) . vallist;
          sorted := (cddr schild) . sorted;
          >>;
      if thesign = 0 then
        return (nil . 0 . nil)
      else
        <<
        sorted := reverse sorted;
        vallist := reverse vallist;
        return (vallist . (thesign . ('!* . sorted)));
        >>
      >>;
    for each child in cdr stree do
      if thesign neq 0 then
        <<
        schild := st_sorttree1(child, ve, fn);
        thesign := thesign * cadr schild;
        tosort := ((cddr schild) . (car schild)) . tosort;
        >>;
    >>;
  if thesign = 0 then return (nil . (0 . nil));
  if car stree = '!+ then
    tosort := cdr_sort(tosort, fn)
  else
    <<
    tosort := cdr_signsort(tosort, fn);
    if car tosort = 0 then
      return (nil . (0 . nil))
    else
      thesign := thesign * car tosort;
    tosort := cdr tosort;
    >>;
  % fill up return structures
  while tosort do
    <<
    sorted := (caar tosort) . sorted;
    vallist := (cdar tosort) . vallist;
    tosort := cdr tosort;
    >>;
  sorted := (car stree) . reverse sorted;
  vallist := reverse(vallist);
  return (vallist . (thesign . sorted));
  end;

symbolic procedure st_ad_numsorttree(stree);
  begin scalar sorted;
  sorted := st_ad_numsorttree1(stree);
  return car sorted . cadr sorted;
  end;

symbolic procedure st_ad_numsorttree1(stree);
  begin scalar  subtree, contents, tosort;
        integer thesign;
  if numberp stree then return {1, stree, stree};
  thesign := 1;
  if car stree eq '!* then
    <<
    stree := '!* . for each elt in cdr stree collect
      <<
      subtree := st_ad_numsorttree1(elt);
      thesign := thesign * car subtree;
      contents := cddr subtree . contents;
      cadr subtree
      >>;
    contents := ad_numsort(for each elt in contents join elt);
    return thesign . (stree . contents);
    >>;
  tosort := for each elt in cdr stree collect
    <<
    subtree := st_ad_numsorttree1(elt);
    thesign := thesign * car subtree;
    cdr subtree
    >>;
  if car stree eq '!+ then
    <<
    tosort := cdr_sort(tosort, function numlist_ordp);
    tosort := for each elt in tosort collect
      <<
      contents := (cdr elt) . contents;
      car elt
      >>;
    contents := ad_numsort(for each elt in reverse contents join elt);
    return (thesign . (('!+ . tosort) . contents));
    >>;
  if car stree eq '!- then
    <<
    tosort := cdr_signsort(tosort, function numlist_ordp);
    thesign := car tosort;
    tosort := for each elt in cdr tosort collect
      <<
      contents := (cdr elt) . contents;
      car elt
      >>;
    contents := ad_numsort(for each elt in reverse contents join elt);
    return (thesign . (('!- . tosort) . contents));
    >>;
  end;

symbolic procedure st_consolidate(stree);
  begin scalar join_cells, children, tmp;
   if null stree then return nil;
   if numberp cadr stree then return stree;
   join_cells := t;
   for each child in reverse(cdr stree) do
     <<
     tmp := st_consolidate(child);
     if tmp then
      <<
      if cddr tmp then
        join_cells := nil
      else
        tmp := {'!*, cadr tmp};
      children := tmp . children;
      >>;
    >>;
  if children then
    <<
    if null cdr children then
      return car children;
    if join_cells then
      children := for each elt in children collect cadr elt;
    return (car stree) . children
    >>
  else
    return nil;
  end;

%%%%%%%%%%%%%%%%%%%%%% SKELETONS %%%%%%%%%%%%%%%%%%%%%%%%%%

symbolic procedure dv_cambhead(camb);
  begin
  if listp camb then
    <<
    if member(car camb, {'expt, 'minus}) then
      return dv_cambhead(cadr camb);
    if listp camb then return car camb;
    >>;
  end;

symbolic procedure dv_skelhead(skelpair);
  dv_cambhead car(skelpair);

symbolic procedure dv_skelsplit(camb);
  begin scalar  skel, stree, subskels;
        integer count, ind, maxind, thesign;
  thesign := 1;
  if not listp camb then
    if (ind := dummyp(camb)) then
      return {1, ind, ('!~dv . {'!*, ind})}
    else
      return {1, 0, (camb . nil)};
  stree := get(car camb, 'symtree);
  if not stree then
    <<
    stree := for count := 1 : length(cdr camb) collect count;
    if flagp(car  camb, 'symmetric) then
      stree := '!+ . stree
    else if flagp(car camb, 'antisymmetric) then
      stree := '!- . stree
    else
      stree := '!* . stree
    >>;
  subskels := mkve(length(cdr camb));
  count := 0;
  for each arg in cdr camb do
    <<
    count := count + 1;
    if listp arg then
      putve(subskels, count, (arg . nil))
    else if (ind := dummyp(arg)) then
      <<
      maxind := max(maxind, ind);
      putve(subskels, count, ('!~dv . {'!*, ind}))
      >>
    else
      putve(subskels, count, (arg . nil));
    >>;
  stree := st_sorttree(stree, subskels, function skp_ordp);
  if stree and (car stree = 0) then return nil;
  thesign := car stree;
  skel := dv_skelsplit1(cdr stree, subskels);
  stree := st_consolidate(cdr skel);
  skel := (car camb) . car skel;
  return {thesign, maxind, skel . stree};
  end;

symbolic procedure dv_skelsplit1(stree, skelve);
  begin scalar
            cell_stree, child_list, cur_cell, dv_stree, part, skel, ve;
        integer count, len;
  if numberp cadr stree then
    <<
    ve := skelve;
    child_list := cdr stree;
    skel := for each elt in cdr stree collect car venth(ve,elt);
    >>
  else
    <<
    len := length(cdr stree);
    ve := mkve(len);
    count := len;
    for each child in reverse(cdr stree) do
      <<
      putve(ve, count, dv_skelsplit1(child, skelve));
      skel := car(venth(ve,count)) . skel;
      child_list := count . child_list;
      count := count - 1;
      >>;
    skel := for each elt in skel join copy elt;
    >>;
  %% if root of stree is * node, then
  %% no partition of children is necessary
  if car stree eq '!* then
    <<
    for each elt in reverse(child_list) do
      if cdr venth(ve, elt) then
        dv_stree := cdr venth(ve, elt) . dv_stree;
    if length(dv_stree) = 1 then
      dv_stree := car dv_stree
    else if dv_stree then
      dv_stree := '!* . dv_stree;
    return (skel . dv_stree);
    >>;
  %% regroup children with equal skeletons
  for each elt in child_list do
    if null cur_cell then % new skeleton
      cur_cell := car venth(ve, elt) . {cdr venth(ve, elt)}
    else if (car venth(ve, elt)) = (car cur_cell) then
      rplacd(cur_cell, (cdr venth(ve,elt)) . cdr cur_cell)
    else
      <<
      part := cur_cell . part;
      cur_cell := car venth(ve, elt) . {cdr venth(ve, elt)};
      >>;
  part := cur_cell . part;
  %% prepend contribution of each cell to dv_stree
  %% note that cells of part are in reverse order,
  %% as are elements of each cell
  for each cell in part do
    if cdr cell then
      <<
      cell_stree := car stree . reverse(cdr cell);
      dv_stree := cell_stree . dv_stree
      >>;
  %% now set type of dv_stree, if it has more than one element
  if length(dv_stree) neq 1 then
    dv_stree := '!* . dv_stree
  else
    dv_stree := car dv_stree;
  return skel . dv_stree;
  end;


symbolic procedure nodum_varp u;
% u is a list or an atom (index) or !~dv or !~dva
% returns true if it is neither a list nor a dummy var
% nor !~dv or !~dva.
 if listp u then t
   else
 if flagp(u,'dummy) or car ad_splitname u = g_dvbase
                    or u member {'!~dv,'!~dva}
         then nil
  else t;


symbolic procedure list_is_all_free u;
% u is a list of indices
% returns nil if there is at least one dummy index
% or if one of them is !~dv or !~dva.
 if null u then t
  else
 if nodum_varp car u then list_is_all_free cdr u
   else nil;


symbolic procedure dv_skelprod(sklist, maxind);
% This is the corrected function for commuting
% operators which do not depend on dummy variables.
  begin scalar
               skform, stree, symcells, skel, apair, anticom_alist,
               com_alist, noncom_alist, acom_odd, acom_even, idvect,
               varskel;
        integer the_sign, count;
  %% sort skeletons according to lexicograpical order of dv_skelhead,
  %% placing commuting factors before anticommuting factors
  the_sign := 1;
  for each skelpair in sklist do
    <<
    skel := car skelpair;
    varskel:=if listp skel then
                if car skel neq 'expt then cdr skel;
% else
%                     if car skel neq 'expt then cdr skel;
    if flagp(dv_skelhead skelpair , 'anticom) then
      <<
      if (apair := anticom_assoc(skel, anticom_alist)) then
        <<
        if member(cdr skelpair, cddr apair) then
          the_sign := 0
        else
          the_sign := the_sign * car apair;
        rplacd(cdr apair, (cdr skelpair) . (cddr apair))
        >>
      else
        anticom_alist := (skel . {cdr skelpair}) . anticom_alist;
      >>
    else if flagp(dv_skelhead skelpair, 'noncom) then
      noncom_alist := (skel . {cdr skelpair}) . noncom_alist
   % we do not need the "else if" for commuting operators
   %   if no dummy variable is involved:
 %   else  if null list_is_all_free varskel or atom skel then
 %               if(apair := assoc(skel, com_alist)) then
    else  if ( (null list_is_all_free varskel or atom skel) and
               (apair := assoc(skel, com_alist)) ) then
    rplacd (apair, (cdr skelpair) . (cdr apair))
     %           else nil
    else
      com_alist := (skel . {cdr skelpair}) . com_alist;
    >>;
  if the_sign = 0 then return nil;
  %% restore order of factors for each anticom cell
  anticom_alist := for each elt in anticom_alist collect
    (car elt) . reverse(cdr elt);
  %% sort com_alist
  com_alist := sort(com_alist,
                    function(lambda(x,y); idcons_ordp(car x, car y)));
  %% sort anticom_alist, taking care of sign changes
  %% isolate even prod of anticoms from odd prod of anticoms
  for each elt in anticom_alist do
    if evenp(length(cdr elt)) then
      acom_even := elt . acom_even
    else
      acom_odd := elt . acom_odd;
  acom_even := sort(acom_even,
                    function(lambda(x,y); idcons_ordp(car x, car y)));
  anticom_alist := ad_signsort(acom_odd,
                     function(lambda(x,y); idcons_ordp(car x, car y)));
  the_sign := the_sign * car anticom_alist;
  anticom_alist :=
    merge_list1(acom_even, cdr anticom_alist, function idcons_ordp);
  skform := append(com_alist, anticom_alist);
  skform := append(skform, reverse noncom_alist);
  if maxind = 0 then
    <<
    if the_sign = -1 then skform := ((-1) . {nil}) . skform;
    return skform . nil;
    >>;
  %% build complete symtree,
  %% omiting skels which do not depend on dummy variables
  for each elt in reverse noncom_alist do
    stree := cadr elt . stree;
  for each elt in reverse anticom_alist do
    if length(cdr elt) > 1 then
      stree := ('!- . cdr elt) . stree
    else if (cdr elt) then
      stree := cadr elt . stree;
  for each elt in reverse com_alist do
    if length(cdr elt) > 1 then
      stree := ('!+ . cdr elt) . stree
    else if (cdr elt) then
      stree := cadr elt . stree;
  if length(stree) > 1 then
    stree := '!* . stree
  else
    stree := car stree;
  stree := st_consolidate(stree);
  idvect := mkve(maxind);
  for count := 1 : maxind do putve(idvect, count, count);
  stree := st_sorttree(stree, idvect, function numlist_ordp);
  %% the sign change for sorting the symmetry tree does not influence
  %% the sign of the expression.  Indeed, the symtree used to fill up
  %% the blanks in the expression is the symtree stored with the
  %% skeleton, which is not sorted.  Note however that if the sign here
  %% is 0, then the expression is null.
  %  the_sign := the_sign * car stree;
  if car stree = 0 then return nil;
  if the_sign = -1 then
    skform := ((-1) . {nil}) . skform;
  symcells := st_extract_symcells(cdr stree, maxind);
  return skform . symcells;
  end;


symbolic procedure dv_skel2factor1(skel_kern, dvars);
  begin scalar dvar,scr;
    if null skel_kern then return nil;
   return
    if listp skel_kern then
     <<scr:=dv_skel2factor1(car skel_kern, dvars);
          scr:=scr . dv_skel2factor1(cdr skel_kern, dvars)
     >>
  else
    if skel_kern eq '!~dv then
      <<
      dvar := car dvars;
      if cdr dvars then
        <<
        rplaca(dvars, cadr dvars);
        rplacd(dvars, cddr dvars);
        >>;
      dvar
      >>
    else
      skel_kern;
  end;

%%%%%%%%%%%%%%% PARTITION SYMMETRY TREES %%%%%%%%%%%%%%%%%%

symbolic procedure pst_termnodep(pst);
  null cdr venth(cdr pst, 1);

symbolic procedure pst_mkpst(stree);
  pst_equitable(nil . pst_mkpst1(stree));

symbolic procedure st_multtermnodep(stree);
  begin
  scalar res, subtrees;
  if car stree neq '!* then return nil;
  subtrees := cdr stree;
  res := t;
  while subtrees do
    <<
    if numberp cadar subtrees then
      subtrees := cdr subtrees
    else
      <<
      subtrees := nil;
      res := nil;
      >>
    >>;
  return res;
  end;

symbolic procedure pst_mkpst1(stree);
  begin
  scalar
    subtrees, s_cells, ve, pst, cell;
  integer
    count, lastcount;
  if null stree then return nil;
  ve := mkve(length(cdr stree));
  subtrees := cdr stree;
  count := 1;
  if numberp(car subtrees) then       % terminal node with single cell
    while subtrees do
      <<
      putve(ve, count, ({car subtrees} . nil));
      count := count + 1;
      subtrees := cdr subtrees;
      >>
  % check if valid as pst terminal node with several cells
  else if st_multtermnodep(stree) then
    <<
    ve := mkve(for each cell in subtrees sum (length(cdr cell)));
    lastcount := 0;
    for each s_cell in subtrees do
      <<
      cell := cdr s_cell;
      if car s_cell eq '!* then
        for count := 1 : length(cell) do
          pst := {count + lastcount} . pst
      else
        pst := (
          for count := 1 : length(cell) collect (count + lastcount)
          ) . pst;
      count := lastcount + 1;
      lastcount := lastcount + length(cell);
      for each elt in cell do
        <<
        putve(ve,count, {{elt}});
        count := count + 1;
        >>;
      >>;
    return (reverse pst . ve);
    >>
  else
    while subtrees do
      <<
      pst := pst_mkpst1(car subtrees);
      s_cells := nil;
      for count2 := 1 : upbve(cdr pst) do
        s_cells := append(car venth(cdr pst, count2), s_cells);
      putve(ve, count, (s_cells . pst));
      count := count + 1;
      subtrees := cdr subtrees;
      >>;
  if ((car stree) eq '!*) then % discrete partition
    pst := ((for count := 1 : upbve(ve) collect {count}) . ve)
  else % single cell partition
    pst := ({(for count := 1 : upbve(ve) join {count})} . ve);
  return pst;
  end;

symbolic procedure pst_subpst(pst, ind);
  venth(cdr pst, ind);

symbolic procedure pst_reduce(pst);
  begin
  scalar
       isolated, f_cell, rpst, tmp, npart, nsubs;
  integer
       ind, count;
  if null pst then return (nil . nil);
  if null cdr pst then return pst;
  f_cell := caar pst;
  while length(f_cell) eq 1 do
    <<
    ind := car f_cell;                   % index of pst_subpst
    if pst_termnodep(pst) then
      <<
      isolated := append(isolated, {caar venth(cdr pst, ind)});
      %% remove first cell from pst, and set f_cell
      if cdar pst then % pst is not fully reduced
        <<
        %% remove first cell
        rplaca(pst, cdar pst);
        %% update pst representation
        npart := for each cell in car pst collect
          for each elt in cell collect
            if (elt > ind) then elt - 1 else elt;
        nsubs := mkve(upbve(cdr pst)-1);
        for count := 1 : upbve(nsubs) do
          if count geq ind then
            putve(nsubs, count, venth(cdr pst, count+1))
          else
            putve(nsubs, count, venth(cdr pst, count));
        rplaca(pst, npart);
        rplacd(pst, nsubs);
        f_cell := caar pst;
        >>
      else % pst fully reduced
        f_cell := pst := nil;
      >>
    else
      <<
      rpst := pst_reduce(cdr pst_subpst(pst,ind));
      if car rpst then
      %% new isolates
        <<
        %% add new isolates to isolated
        isolated := append(isolated, car rpst);
        if cdr rpst then
        %% first subtree in pst was not discrete, update subtree spec
          <<
          tmp := pst_subpst(pst,ind);
          rplaca(tmp, setdiff(car tmp, car rpst));
          rplacd(tmp, cdr rpst);
          f_cell := nil;
          >>
        else  % first subtree in pst was discrete, so remove it
          <<
          if cdar pst then % pst not fully reduced
            <<
            rplaca(pst, cdar pst);
            npart := for each cell in car pst collect
              for each elt in cell collect
                if (elt > ind) then elt - 1 else elt;
            nsubs := mkve(upbve(cdr pst)-1);
            for count := 1 : upbve(nsubs) do
              if count geq ind then
                putve(nsubs, count, venth(cdr pst, count+1))
              else
                putve(nsubs, count, venth(cdr pst, count));
            rplaca(pst, npart);
            rplacd(pst, nsubs);
            f_cell := caar pst;
            >>
          else
            f_cell := pst := nil;
          >>;
        >>
      else
      %% car rpst is nil, so no more isolated d-elts
        <<
        f_cell := nil;
        >>;
      >>
    >>;
  return (isolated . pst);
  end;

symbolic procedure pst_isolable(rpst);
  begin
  scalar
    ve, f_cell;
  %% verify if fully reduced.
  if null cdr rpst then return nil;
  %% f_cell is list of elts in first cell in rpst.
  %% ve is vector of descriptions of elts in f_cell
  f_cell := caadr rpst;
  ve := cddr rpst;
  %% if the elts in f_cell are d-elts, then return the list of d-elts
  if null cdr venth(ve, car f_cell) then
    return for each ind in f_cell collect caar venth(ve, ind);
  return for each ind in f_cell join copy
    pst_isolable(nil . cdr venth(ve, ind));
  end;

symbolic procedure pst_isolate(s_cell, rpst);
  begin
  scalar
    redisol;
  redisol := pst_reduce(pst_isolate1(s_cell, cdr rpst));
  rplaca(redisol, append(car rpst, car redisol));
  return redisol;
  end;

symbolic procedure pst_isolate1(s_cell, pst);
  begin
  scalar
    fcell, tmp, spst;
  integer
    ind;
  %% fcell is the list of elts in the first cell of rpst
  %% ve is the vector of descriptions of elts in fcell
  fcell := caar pst;
  %% find out which elt of fcell needs to be set aside, if any
  tmp := fcell;
  while (ind = 0) do
    <<
    if null tmp then ind := -1;
    ind := car tmp;
    tmp := cdr tmp;
    if not member(s_cell, car (spst := pst_subpst(pst, ind))) then
      ind := 0
    >>;
  %% if no elt should be set aside, then s_cell is not isolable
  if (ind = -1) then return nil;
  %% effectively isolate, splitting first cell if necessary
  if (length(fcell) > 1) then
    <<
    tmp := delete(ind, fcell) . cdar pst;
    tmp := {ind} . tmp;
    rplaca(pst, tmp)
    >>;
  %% if the set aside elt is not a mere dummy variable, then isolate
  %% s_cell in the partition it represents.
  if not pst_termnodep(pst) then
    <<
    spst := car spst . pst_isolate1(s_cell, cdr spst);
    putve(cdr pst, ind, spst)
    >>;
  return pst;
  end;

symbolic procedure pst_equitable(rpst);
  begin
  scalar
    nrpst, reduced, isol;
  if null cdr rpst then return rpst;
  isol := car rpst;
  nrpst := pst_reduce(cdr rpst);
  rplaca(nrpst, append(isol, car nrpst));
  repeat
    <<
    isol := car nrpst;
    nrpst := isol . pst_equitable1(isol, cdr nrpst);
    reduced := pst_reduce(cdr nrpst);
    if car reduced then
      nrpst := (append(isol, car reduced) . cdr reduced);
    reduced := car reduced
    >>
  until not reduced;
  return nrpst;
  end;

symbolic procedure pst_equitable1(isolated, pst);
  begin
  scalar
    isol, ve, alpha, beta, p1, equit, cell, psi;
  integer
    len, k, n_delems;
  if null pst then return nil;
  %% make partition to equitate, merging isolated and car pst
  isol := isolated;
  len := length(isolated);
  ve := mkve(upbve(cdr pst) + len);
  for count := 1 : upbve(cdr pst) do
    putve(ve, count, car venth(cdr pst, count));
  alpha := car pst;
  for count := upbve(cdr pst) + 1 : upbve(ve) do
    <<
    putve(ve, count, {car isol});
    isol := cdr isol;
    alpha := {count} . alpha;
    >>;
  p1 := fullcopy alpha;
  len := length(p1);
  n_delems := upbve(ve);
  while (alpha and len  < n_delems) do
    <<
    beta := car alpha;
    alpha := cdr alpha;
    equit := nil;
    len := 0;
    while(p1) do
      <<
      cell := car p1;
      p1 := cdr p1;
      psi := if cdr cell then
                       pst_partition(cell, beta, ve) else {cell};
      k := length(psi);
      equit := append(equit, psi);
      len := len + k;
      if k geq 2 then alpha := append(cdr psi, alpha);
      >>;
    p1 := equit;
    >>;
  equit := pnth(p1,length(isolated)+1);
  %%% make every child of pst equitable w.r.t. isolated
  if not pst_termnodep(pst) then
    for count := 1 : upbve(cdr pst) do
      <<
      p1 := venth(cdr pst, count);
      putve(cdr pst, count,
            (car p1 . pst_equitable1(isolated, cdr p1)));
      >>;
  return (equit . cdr pst);
  end;

symbolic procedure pst_d1(d1,d2, ve);
  for each e1 in venth(ve,d1) collect ordn
    for each e2 in venth(ve, d2) collect ordn
      car pa_coinc_split(sc_kern(e1), sc_kern(e2));

symbolic procedure pst_d(d1, d2, ve);
  if listp d1 then
    if listp d2 then
      ordn for each e1 in d1 collect
        ordn for each e2 in d2 collect pst_d(e1, e2, ve)
    else
      ordn for each e1 in d1 collect pst_d(e1, d2, ve)
  else
    if listp d2 then
      ordn for each e2 in d2 collect pst_d(d1, e2, ve)
    else
      pst_d1(d1, d2, ve);

symbolic procedure pst_partition(s1, s2, ve);
  begin
  scalar
    elt_d, elt_apair, pst_alist;
  for each elt in s1 do
    <<
    elt_d := pst_d(elt, s2, ve);
    if (elt_apair := assoc(elt_d, pst_alist)) then
      rplacd(elt_apair, elt . cdr elt_apair)
    else
      pst_alist := (elt_d . {elt}) . pst_alist;
    >>;
  % sort regrouped elts according to distance to s2
  pst_alist := sort(pst_alist,
      function( lambda(x,y); numlist_ordp(car x, car y)));
  return for each elt in pst_alist collect reverse(cdr elt);
  end;

%%%%%%%%%%%%%%%%%%%%%%%% BACKTRACKING %%%%%%%%%%%%%%%%%%%%%%%%%%

symbolic procedure dv_next_choice(sc, partial_perm, rpst, comp_info);
  begin scalar
    next_perm, extensions, nrpst, new_aut;
  integer
    npoints, len, next_img ;
  npoints := upbve(car sc);
  g_skip_to_level := len := upbve(partial_perm) + 1;
  sc_setbase(sc, partial_perm);
  extensions := pst_isolable(rpst);
  repeat
    <<
    extensions := idsort( intersection
        (extensions, candidate_extensions(sc, partial_perm)));
    if extensions then
      <<
      next_img := car extensions;
      extensions := cdr extensions;
      nrpst := pst_equitable(pst_isolate(next_img, fullcopy(rpst)));
      next_perm := list2vect!*(car nrpst,'symbolic);
      comp_info := dv_compare(next_perm, comp_info, len, npoints);
      if (car comp_info = 0) then
       if (upbve(next_perm) = npoints) then
       <<
       new_aut := pe_mult(pe_inv(venth(cadr comp_info, 1)), next_perm);
          process_new_automorphism(sc, new_aut);
          >>
        else
          comp_info := dv_next_choice(sc, next_perm, nrpst, comp_info)
      else if (car comp_info = 1) then
        if (upbve(next_perm) < npoints) then
          comp_info := dv_next_choice(sc, next_perm, nrpst, comp_info)
        else
          rplaca(comp_info, 0);
      rplacd(cdr comp_info, cdr cddr comp_info);
      >>
    >>
  until (null extensions) or (len > g_skip_to_level);
  return comp_info;
  end;

symbolic procedure can_rep_cell(comp_info, level);
  venth(venth(cadr comp_info, 2), level);

symbolic procedure last_part_kern(comp_info);
  car cddr comp_info;

symbolic procedure dv_compare(next_perm, comp_info, len, npoints);
  begin
  scalar
    part_kern, part_rep, can_rep, curlev, res;
  if car comp_info = 1 then
    return
     dv_fill_comp_info(next_perm, comp_info, len, npoints, nil, nil);
  if len = 1 then
    <<
    part_kern := sc_kern(venth(next_perm, 1));
    part_rep := {sc_rep(venth(next_perm,1))};
    >>
  else
    <<
    part_kern := last_part_kern(comp_info);
    part_kern := pa_coinc_split(part_kern,
                                sc_kern(venth(next_perm, len)));
    part_rep := (sc_rep(venth(next_perm, len)) . car part_kern);
    part_kern := cdr part_kern;
    >>;
  can_rep := can_rep_cell(comp_info, len);
  curlev := len;
  res := 0;
  repeat
    <<
    if equal(can_rep, part_rep) then
      <<
      res := 0;
      if (curlev < upbve(next_perm)) then
        <<
        curlev := curlev + 1;
        part_kern := pa_coinc_split(part_kern,
                                   sc_kern(venth(next_perm, curlev)));
        part_rep := (sc_rep(venth(next_perm,curlev)) . car part_kern);
        part_kern := cdr part_kern;
        can_rep := can_rep_cell(comp_info, curlev);
        >>
      >>
    else if numlist_ordp(can_rep, part_rep) then
      <<
      res := 1;
      rplaca(comp_info, 1);

      comp_info := dv_fill_comp_info(next_perm, comp_info,
                              curlev, npoints, part_rep, part_kern);
      >>
    else
      <<
      res := 2;
      % grow partial permutation kernel stack
      rplacd(cdr comp_info, nil . (cddr comp_info));
      rplaca(comp_info, 2);
      >>
    >>
  until (res neq 0) or (curlev = upbve(next_perm));
  if res = 0 then
    <<
    % update partial permutation stack
    rplacd(cdr comp_info, part_kern . cddr comp_info);
    if (curlev = npoints) and
                 dv_new_aut_hook(next_perm, comp_info) then
      <<
      g_skip_to_level := 0;
      rplaca(comp_info, 2);
      >>;
    >>;
  return comp_info;
  end;


symbolic procedure dv_fill_comp_info(pe, comp_info, len, npoints,
                                     part_rep, part_kern);
  begin scalar
    part_rep;
  integer level;
  if len = 1 then
    <<
    part_kern := sc_kern(venth(pe, 1));
    part_rep := {sc_rep(venth(pe,1))};
    >>
  else if null part_kern then
    <<
    part_kern := last_part_kern(comp_info);
    part_kern := pa_coinc_split(part_kern, sc_kern(venth(pe, len)));
    part_rep := (sc_rep(venth(pe, len)) . car part_kern);
    part_kern := cdr part_kern;
    >>;
  putve(venth(cadr comp_info, 2), len, part_rep);
  level := len + 1;
  while(level <= upbve(pe)) do
    <<
    part_kern := pa_coinc_split(part_kern, sc_kern(venth(pe, level)));
    part_rep := (sc_rep(venth(pe, level)) . car part_kern);
    part_kern := cdr part_kern;
    putve(venth(cadr comp_info, 2), level, part_rep);
    level := level + 1
    >>;
  rplacd(cdr comp_info, part_kern . (cddr comp_info));
  if level = npoints+1 then
    if null venth(cadr comp_info, 1) and
                             dv_null_first_kern(part_kern) then
      <<
      g_skip_to_level := 0;
      rplaca(comp_info, 2);
      >>
    else
      <<
      putve(cadr comp_info, 1, fullcopy(pe));
      putve(cadr comp_info, 3, part_kern);
      >>;
  return comp_info;
  end;

symbolic procedure dv_null_first_kern(kern);
  begin
  scalar
    l_kern, cell, nullexp, acell;
  integer
    count, count2;
  nullexp := nil;
  l_kern := pa_vect2list kern;
  for each cell in l_kern do
    if cdr cell and not nullexp then
      <<
      count := 0;
      for count2 := 1 : upbve(g_sc_ve) do
        if (car (acell := car venth(g_sc_ve, count2)) eq '!-) and
          member (car cell, acell) then count := count + 1;
      if oddp count then
        nullexp := t;
      >>;
  return nullexp;
  end;

symbolic procedure dv_new_aut_hook(pe, comp_info);
  begin
  scalar tmp1, tmp2, ve;
  integer count, thesign;
  thesign := st_signchange(venth(cadr comp_info,1), pe);
  tmp1 := pa_part2list(venth(cadr comp_info, 3));
  tmp2 := pa_part2list(caddr comp_info);
  ve := mkve(length(tmp1));
  count := 1;
  while tmp1 do
    <<
    putve(ve, car tmp1, car tmp2);
    tmp1 := cdr tmp1;
    tmp2 := cdr tmp2;
    count := count + 1;
    >>;
  for count := 1 : upbve(g_sc_ve) do
    <<
    tmp1 := car venth(g_sc_ve, count);
    if car tmp1 eq '!- then
      <<
      tmp1 := cdr tmp1;
      tmp2 := for each elt in tmp1 collect venth(ve,elt);
      % tmp2 is the image of tmp1. Since all cells in g_sc_ve are
      % ordered in increased numerical order
      thesign := thesign * car num_signsort(tmp2);
      >>
    >>;
  if thesign = -1 then
    return t;
  return nil;
  end;

%%%%%%%%%%%%%%%%%%%%%%%%%% TOP LEVEL %%%%%%%%%%%%%%%%%%%%%%%%%%%

symbolic procedure dv_canon_monomial(sf);
  begin
  scalar
    tmp, sklist, camb, skel, skprod, aut_sc, can_kern,
    new_dvnames, pst, comp_info, factlst, res, fact,
    sorted_factlst;
  integer
    count, expnt, thesign, maxind;
  %% get skeleton pairs for each one of the factors
  thesign := 1;
  while not domainp sf do
    <<
    tmp := lpow(sf); sf := lc(sf);
    %% suppose exponents are integers
    expnt := cdr tmp; camb := car tmp;
    if expnt neq 1 and flagp(dv_cambhead(camb),'anticom) then
      <<
      skel := nil;
      sf := nil;
      >>
    else
      skel := dv_skelsplit(camb);
    if null skel then
      sf := nil
    else
      <<
      if car skel < 0 then
        <<
        skel := cdr skel;
        if oddp(expnt) then thesign := - thesign
        else rplacd(cdr skel, subst('!-, '!+, cdr skel));
        >>
      else
        skel := cdr skel;
      if (car skel > maxind) then maxind := car skel;
      skel := cadr skel;
      if expnt neq 1 then
        rplaca(skel, {'expt, car skel, expnt});
      sklist := skel . sklist;
      >>;
    >>;
  if null sf then return nil;
  sklist := reverse((sf . nil) . sklist);
  %% regroup factors with identical skeletons
  skprod := dv_skelprod(sklist, maxind);
  if null skprod then return nil;
  sklist := car skprod;
  if maxind > 0 then
    <<
    g_sc_ve := cddr skprod;
    g_init_stree := cadr skprod;
    aut_sc := sc_create(upbve(g_sc_ve));
    comp_info := mkve(3);
    putve(comp_info, 2, mkve(upbve(g_sc_ve)));
    comp_info := {1, comp_info, nil};
    pst := pst_mkpst(g_init_stree);
    tmp := list2vect!*(car pst,'symbolic);
    g_skip_to_level := 1;
    if car pst then
      comp_info := dv_compare(tmp, comp_info, 1, upbve(g_sc_ve));
    if cdr pst then
      comp_info := dv_next_choice(aut_sc, tmp, pst, comp_info);
    if g_skip_to_level = 0 then return nil;
    can_kern := pa_part2list(venth(cadr comp_info, 3));
    count := 0;
    new_dvnames := nil;
    for each elt in can_kern do
      <<
      count := count + 1;
      if elt neq count then
        new_dvnames := (elt . count) . new_dvnames;
      >>;
    >>;
  for each cell in sklist do
    <<
    factlst := nil;
    skel := car cell;
    if cadr cell then
     <<
     for each stree in cdr cell do
       <<
       fact := dv_skel2factor( (skel . stree), new_dvnames);
       if car fact = -1 then
         thesign := - thesign;
       factlst := (cdr fact) . factlst;
       >>;
     factlst := reverse factlst;
     if flagp(dv_cambhead skel, 'anticom) then
       <<
       sorted_factlst := ad_signsort(factlst, 'idcons_ordp);
       thesign := thesign * car sorted_factlst;
       sorted_factlst := cdr sorted_factlst;
       >>
     else
       sorted_factlst :=  sort(factlst, 'idcons_ordp);
     res := append(res, sorted_factlst);
     >>
   else
     res := append(res, {skel});
    >>;
  %% transform res, list of factors, into standard form
  if thesign = -1 then
    skprod := {'minus, 'times . res}
  else if thesign = 1 then
    skprod := 'times . res
  else
    skprod := 0;
  return !*a2f skprod;
  end;

symbolic procedure dv_skel2factor(skelpair, newnames);
  begin
  scalar stree, dvars;
  if null cdr skelpair then return car skelpair;
  stree := sublis(newnames, cdr skelpair);
  stree := st_ad_numsorttree(stree);
  dvars :=
   for each elt in st_flatten(cdr stree) collect dv_ind2var elt;
  return (car stree . dv_skel2factor1(car skelpair, dvars));
  end;

%%%%%%%%%%%%%%%%%%%%%%% USER INTERFACE %%%%%%%%%%%%%%%%%%%%%%%%%

symbolic procedure canonical sq;
  begin
  scalar
    sf, denom, res, !*distribute;
  res := nil;
  sq := simp!* car sq;
  denom := denr sq;
  on distribute;
  sf := distri_pol numr sq;
  %% process each monomial in sf
  while not domainp(sf) do
    <<
    res := addf(res, dv_canon_monomial(lt sf .+ nil));
    sf := red sf;
    >>;
  res := addf(res,sf);
  %% simplify the whole thing, and return
  return simp!*( {'!*sq, res ./ denom, nil} )
  end;

put ('canonical, 'simpfn, 'canonical);

flag('(symtree),'opfn);

symbolic procedure symtree (name, s);
  <<
  put (name, 'symtree, alg_to_symb s);
  >>;

symbolic procedure remsym u;
% ALLOWS TO ELIMINATE THE DECLARED SYMMETRIES.
 for each j in u do
   if flagp(j,'symmetric) then remflag(list j,'symmetric)
     else
   if flagp(j,'antisymmetric) then remflag(list j,'antisymmetric)
     else remprop(j,'symtree);

symbolic procedure dummy_names u;
 <<if g_dvbase then msgpri("The created dummy base",g_dvbase,
                           "must be cleared",nil,t);
    g_dvnames := list2vect!*(u,'symbolic);
   flag(u, 'dummy);
  t>>;

rlistat '(dummy_names);

 symbolic procedure show_dummy_names;
  if g_dvnames then symb_to_alg vect2list g_dvnames
     else symb_to_alg list('list);

symbolic procedure dummy_base u;
 if g_dvnames then
     msgpri("Named variables",symb_to_alg vect2list g_dvnames,
                            "must be eliminated",nil,t)
   else g_dvbase := u;

symbolic procedure clear_dummy_base;
<< g_dvbase := nil;t>>;

symbolic procedure clear_dummy_names;
<< g_dvnames := nil;t>>;

flag ('(show_dummy_names clear_dummy_names dummy_base
                                   clear_dummy_base), 'opfn);

deflist(
  '((clear_dummy_base endstat) (clear_dummy_names endstat)),'stat);

symbolic procedure anticom u;
 << for each x in u do
  <<flag(list x, 'anticom); flag(list x, 'noncom)>>;
  t>>;

symbolic procedure remanticom u;
% ALLOWS TO ELIMINATE THE DECLARED anticom flag.
% Operators becomes COMMUTATIVE operators.
 <<
 for each x in u do
      <<remflag(x,'noncom); remflag(x,'anticom)>>;
    t>>;

deflist('((anticom rlis) (remanticom rlis)),'stat);

endmodule;

end;
