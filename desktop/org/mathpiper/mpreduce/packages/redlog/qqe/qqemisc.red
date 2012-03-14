% ----------------------------------------------------------------------
% $Id: qqemisc.red 81 2009-02-06 18:22:31Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 2005-2009 Andreas Dolzmann and Thomas Sturm
% ----------------------------------------------------------------------
% Redistribution and use in source and binary forms, with or without
% modification, are permitted provided that the following conditions
% are met:
%
%    * Redistributions of source code must retain the relevant
%      copyright notice, this list of conditions and the following
%      disclaimer.
%    * Redistributions in binary form must reproduce the above
%      copyright notice, this list of conditions and the following
%      disclaimer in the documentation and/or other materials provided
%      with the distribution.
%
% THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
% "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
% LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
% A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
% OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
% SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
% LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
% DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
% THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
% (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
% OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
% 

lisp <<
   fluid '(qqe_misc_rcsid!* qqe_misc_copyright!*);
   qqe_misc_rcsid!* :=
      "$Id: qqemisc.red 81 2009-02-06 18:22:31Z thomas-sturm $";
   qqe_misc_copyright!* :=
      "Copyright (c) 2005-2009 A. Dolzmann and T. Sturm"
>>;

module qqemisc;
% qqe miscellaneous. Submodule of [qqe].

procedure qqe_prefix!-length(pref);
   % Queue quantifier elimination prefix length. [pref] is a term 
   % in lisp prefix. The length of the prefix of tails and heads is
   % returned. For example: lhead ltail rtail ltail q -> 4.
   begin scalar x,j;
      j := 0;

      if null pref then x:=nil
      else <<x := pref;>>;

      while x and not atom x do
      <<
         
         if car x memq '(rtail ltail rhead lhead) 
         then j := j+1;
         x := cadr x;
      >>;

      return j;

   end;
      
procedure qqe_prefix!-lefts(pref);
   % Queue quantifier elimination prefix length lefts. [pref] is a term 
   % in lisp prefix. The length of the prefix of rtails is
   % returned. For example: lhead ltail rtail ltail q -> 1.
   begin scalar lefts;
      lefts := 0;
      if atom pref then return 0
      else if qqe_op pref memq '(lhead, rhead, ltail, rtail)
      then return qqe_prefix!-lefts1 pref
      else for each x in cdr pref do
         lefts := max(lefts,qqe_prefix!-lefts x);
      return lefts;
   end;

procedure qqe_prefix!-lefts1(pref);
   % Queue quantifier elimination prefix length lefts. [pref] is a term 
   % in lisp prefix. The length of the prefix of rtails is
   % returned. For example: lhead ltail rtail ltail q -> 1.
   begin scalar x, j,op;
      x := pref;
      op := qqe_op pref;
      if op eq 'lhead or op eq 'rtail then j:=1 else j:=0; 
      x := qqe_arg2l x;
      while x and not atom x do
      <<
         
         if qqe_op x eq 'rtail 
         then j := j+1;
         x := qqe_arg2l x;
      >>;
      return j;
   end;

procedure qqe_prefix!-rights(pref);
   % Queue quantifier elimination prefix length rights. [pref] is a term 
   % in lisp prefix. The length of the prefix of ltails and rheads is
   % returned. For example: lhead ltail rtail ltail q -> 2.
   begin scalar rights;
      rights := 0;
      if atom pref then return 0
      else if qqe_op pref memq '(lhead, rhead, ltail, rtail)
      then return rights :=  qqe_prefix!-rights1 pref
      else for each x in cdr pref do
         rights := max(rights,qqe_prefix!-rights x);
      return rights;
   end;

procedure qqe_prefix!-rights1(pref);
   % Queue quantifier elimination prefix length rights. [pref] is a term 
   % in lisp prefix. The length of the prefix of ltails and rheads is
   % returned. For example: lhead ltail rtail ltail q -> 2.
   begin scalar x, j, op;
      x := pref;
      op := qqe_op pref;
      if op eq 'rhead or op eq 'ltail then j := 1 else j := 0;
      x := qqe_arg2l x;
      while x and not atom x  do
      <<
         if qqe_op x eq 'ltail 
         then j := j+1;
         x := qqe_arg2l x;
      >>;
      return j;
   end;

procedure qqe_dfs(u,x);
   % qqe depth first search. searches in lisp prefix [u] for 
   % subformula [x], which is also in lisp prefix. Search method
   % uses 'eq, so it doesn't check for identity in the sense of
   % using the same memory address.
   begin scalar y, not_yet;
      not_yet := t;
      if atom u then
      <<
         if u eq x then return t
         else return nil;
      >>;
      y := u; 
      while y and not_yet do
      <<
         if car y eq x then not_yet := nil;
         if qqe_dfs(car y,x) then not_yet := nil;
         y := cdr y;
      >>;
      if not_yet then return nil
      else return t;
   end;

procedure qqe_lcm(a,b);
   % Queue quantifier elimination lowest common multiplier. [a],[b] are 
   % integers.
   begin scalar x,y;
      x := a;
      y := b;
      while x neq y do
         if x < y then x := x+a
         else y := y+b;
      return x;
   end;

procedure qqe_lcm!-list(list);
   % Queue quantifier elimination lowest common multiplier. [list] is a
   % list of integers.
   begin scalar x,p;
      if null cdr list then return car list;
      x := cdr list;
      p := qqe_lcm(car x, car list);
      x := cdr x;
      while x do 
      <<
         p := qqe_lcm(car x, p);
         x := cdr x;
      >>;
      return p;
   end;

procedure qqe_plcm(a1,a2,b1,b2);
   % Queue quantifier elimination pseudo lowest common multiplier.
   % at the moment not used
   begin
        if not numberp a1 then typerr(a1,"number");
        if not numberp a2 then typerr(a1,"number");
        if not numberp b1 then typerr(a1,"number");
        if not numberp b2 then typerr(a1,"number");

        % if a1 eq b1 then return 0;
        
        if not(remainder(abs(a1-b1), !:gcd(a2,b2)) = 0)
                then return -1;
        
        while a1 neq b1 do
        <<
           if a1 < b1 then a1 := (a1+a2)
           else b1 := (b1+b2);
        >>;

        return a1;
   end;

procedure qqe_plcm!-list(u);
   % Queue quantifier elimination pseudo lowest common multiplier for list.
   % At the moment not used.
   begin scalar x, tmp;
      
      % carefull with special case with one-element lists
      x := cdr u;
      tmp := car u;

      while x do
      <<
         tmp := 
            {qqe_plcm(car tmp, cadr tmp, car car x, cadr car x), 
            qqe_lcm(cadr tmp, cadr car x)}$
         
         if car tmp eq -1 then x := nil
         else x := cdr x;
      >>;
      return car tmp;
         
   end;

procedure qqe_plcm!-2list(u,v);
   % Queue quantifier elimination pseudo lowest common multiplier 
   % with input of two lists. At the moment not used.
   begin scalar x1, x2, tmp1, tmp2;
      
      % !!! special case: one element list!
      x1 := cdr u;
      x2 := cdr v;
      tmp1 := car u;
      tmp2 := car v;

      while x1 and x2 do
      <<
         tmp1 := qqe_plcm(tmp1, tmp2,car x1, car x2); 
         tmp2 := qqe_lcm(tmp2,car x2)}$
         
         if tmp1 eq -1 then x1 := nil
         else
         <<
            x1 := cdr x1;
            x2 := cdr x2;
         >>;
      >>;
      return tmp1;
         
   end;

procedure qqe_quicksort(list);
   % Queue quantifier elimination quicksort. Quicksort of a list [list]
   % of integers in reverse order. For example (2 4 3 5) -> (5 4 3 2).
   begin scalar pivot;
      if list then
      <<
         pivot := car list;
         list := qqe_partition(cdr list,pivot);
         list := append(qqe_quicksort(car list),
            append({pivot}, qqe_quicksort(cadr list)));
      >>;
      return list;
  end;

procedure qqe_partition(list, pivot);
   % Queue quantifier elimination partition. Belongs to quicksort algorithm.
   begin scalar l,r, x;

      for each x in list do
      <<
         if x > pivot then l := append(l,{x})
         else if x < pivot then << r := append(r,{x});>>;
      >>;
      return {l,r};
   end;

% --------------------- length graph --------------------------------%

procedure qqe_length!-graph!-update!-lengths(nodes);
   % QQE length graph update lengths. [nodes] is a structured list of
   % nodes of a length graph (compare return value of
   % [qqe_clause!-length!-graph]). Returns (non-structured) list of
   % all nodes of the length graph. Function calculates min- and
   % maxlengths of all nodes (wherever possible) of length graph.
   begin scalar bad_circles, start_min, start_max, rest;
      start_min := car nodes;
      start_max := cadr nodes;
      rest := caddr nodes;
      bad_circles := qqe_length!-graph!-detect!-bad!-circles(
         append(start_min, append(start_max,rest)));
      %prin2t{"bad circles are ",bad_circles};
      qqe_length!-graph!-maxlength!-bad!-circles bad_circles;
      
      if null qqe_length!-graph!-correct(append(start_min, 
         append(start_max,rest))) then <<
            qqe_length!-graph!-delete append(start_min, 
               append(start_max,rest));
            return 'false; >>;
      qqe_length!-graph!-update!-maxlengths start_max;
      if null qqe_length!-graph!-correct(append(start_min, 
         append(start_max,rest))) then <<
            qqe_length!-graph!-delete append(start_min, 
               append(start_max,rest));
            return 'false; >>;
      %%       qqe_length!-graph!-print(append(start_min, 
      %%          append(start_max,rest)));
      qqe_length!-graph!-update!-minlengths start_min;
      %%       qqe_length!-graph!-print(append(start_min, 
      %%          append(start_max,rest)));
      return append(start_min, append(start_max,rest));
   end;
      
procedure qqe_clause!-update!-lengths(u, headmin);
   % QQE clause update lengths. [u] is a conjunction of atomic
   % formulas, [headmin] is boole. Porcedure initiates a min- and
   % maxlength calculation for the nodes of the length graph of
   % formula [u]. If [headmin] is [t], also headmin values are
   % calculated. It returns a list of all nodes of the length graph of
   % formula [u].
   qqe_length!-graph!-update!-lengths qqe_length!-graph!-clause(u, headmin);


procedure qqe_length!-graph!-clause(u, headmin);
   % QQE length graph clause. [u] is a conjunction of atomic
   % formulas. [headmin] is boole.  Procedure creates length graph for
   % formula [u] and calculates initial min- and maxlength values. If
   % [headmin] is true also headmin values are calculated.
   begin scalar start_min, start_max, op, var_list, var_l, var_r, 
         var_listtemp;

      if not(car u eq 'and) then u := {u} else u := cdr u;

      %% generate graph
      for each at in u do <<
         if qqe_debug!* then prin2t{"lengthgraph-clause at at=",at};
         op := qqe_op at;
         if op eq 'qequal then <<
            var_l := qqe_qprefix!-var qqe_arg2l at;
            var_r := qqe_qprefix!-var qqe_arg2r at;
            if var_l eq 'qepsilon and var_r neq 'qepsilon 
            then <<
               qqe_update!-maxlength(var_r, 
                  qqe_prefix!-length qqe_arg2r at);
               start_max := lto_insertq(var_r,start_max);
            >>
            else if var_r eq 'qepsilon and var_l neq 'qepsilon 
            then <<
               qqe_update!-maxlength(var_l,
                  qqe_prefix!-length qqe_arg2l at);
               start_max := lto_insertq(var_l,start_max);
            >>
            else <<
               qqe_length!-graph!-update!-edges(var_l, var_r, at);
               var_list := lto_insertq(var_l,var_list);
               if var_l neq var_r then 
                  var_list := lto_insertq(var_r,var_list);
               if qqe_debug!* then prin2t var_list;
            >>;
         >>
         else if op eq 'qneq then <<
            var_l := qqe_qprefix!-var qqe_arg2l at;
            var_r := qqe_qprefix!-var qqe_arg2r at;
            if var_l eq 'qepsilon and var_r neq 'qepsilon 
            then <<
               qqe_update!-minlength(var_r,
                  qqe_prefix!-length qqe_arg2r at + 1);
               start_min := lto_insertq(var_r, start_min);
            >>
            else if var_r eq 'qepsilon and var_l neq 'qepsilon 
            then <<
               qqe_update!-minlength(var_l,
                  qqe_prefix!-length qqe_arg2l at + 1);
               start_min := lto_insertq(var_l, start_min);
            >>;
         >>
         else if headmin then var_list := qqe_update!-headmin(rl_prepat at, 
            var_list);
      >>;

      % elements not appearing in start_min and start_max
      if qqe_debug!* then prin2t{"var_list before collect = ",var_list};
      var_list := for each x in var_list do
         if not(x memq start_min) and not(x memq start_max) then 
            var_listtemp :=  x . var_listtemp;
      if qqe_debug!* then qqe_length!-graph!-print(append(start_min,
         append(start_max,var_listtemp)));
      return {start_min, start_max, var_listtemp};
   end;  

procedure qqe_length!-graph!-update!-edges(var_l, var_r, at);
   % QQE length graph update edges. [var_l] and [var_r] are
   % variables. [at] is an atomic formula. Edges of the length graph
   % of [at] for variables [var_l] and [var_r] are created.
   begin scalar el, er, diff,edge, neighbor;
      el := qqe_prefix!-length qqe_arg2l at;
      er := qqe_prefix!-length qqe_arg2r at;
      diff := er-el;
      edge := qqe_length!-graph!-edge(el,er,diff);
      neighbor := qqe_length!-graph!-neighbor!-not!-redundant!-edge(
         var_l,var_r,edge);
      if neighbor and not(neighbor eq t) then
         qqe_length!-graph!-delete!-neighbor(var_l,neighbor);
      if neighbor then <<
         qqe_length!-graph!-insert!-neighbor(var_l,
            qqe_length!-graph!-neighbor(var_r,t,edge));
         if (var_l neq var_r) then 
            qqe_length!-graph!-insert!-neighbor(var_r, 
               qqe_length!-graph!-neighbor(var_l,nil,edge));
      >>
      else if qqe_debug!* then <<
         prin2t {"redundant edge=", edge, "with var_1,var_2=",var_l,
            var_r,"in list = ", qqe_length!-graph!-neighbors var_l};
      >>;
         
   end;

procedure qqe_length!-graph!-neighbor!-not!-redundant!-edge(var_1,
      var_2,edge);
   % QQE length graph neighbor not redundant edge. [var_1] and [var_2]
   % are variables, [edge] is an edge. Returns the neighbor
   % corresponding with [var_2] if [var_1] and [var_2] are connected
   % already with a stronger edge with same diff value, that is an
   % edge with smaller left and right values, else it returns nil if
   % [var_1] and [var_2] are connected already with an edge with the
   % same diff value which is not stronger in the above mentioned
   % sense, else it returns true (this case occurs if there is no
   % neighbor matching [var_2], or if there are, then only over a
   % edges with different diff value compared to [edge]).
   begin scalar edge2, neighbor, neighbors, flag, x;
      flag := t;
      neighbors := qqe_length!-graph!-neighbors var_1;
      while flag and neighbors do
      % for all x in qqe_length!-graph!-neighbors var_1 do
      <<
         x := car neighbors;
         if qqe_length!-graph!-neighbor!-node x eq var_2 then
         <<
            if (qqe_length!-graph!-neighbor!-diff x eq 
               qqe_length!-graph!-edge!-diff edge)
            then <<
               edge2 := qqe_length!-graph!-neighbor!-edge x;
               if qqe_length!-graph!-neighbor!-left!-on!-edge x 
               then <<
                  if qqe_length!-graph!-edge!-el edge < 
                     qqe_length!-graph!-edge!-er edge2 
                  then << neighbor := x, flag := nil;>>
                  else << flag := nil; neighbor := nil; >>;
               >>
               else <<
                  if qqe_length!-graph!-edge!-el edge <
                     qqe_length!-graph!-edge!-el edge2
                  then << neighbor := x, flag := nil;>>
                  else << flag := nil; neighbor := nil;>>
               >>;
            >>;
         >>;
         neighbors := cdr neighbors;
      >>;
      if flag then return t 
      else return neighbor;
   end;

procedure qqe_length!-graph!-neighbor(var,left,edge);
   % QQE length graph neighbor. Constructur for a neigbor. [var] is a
   % variable, [left] is boole, [edge] is an edge.
   {var,left,edge};

procedure qqe_length!-graph!-insert!-neighbor(var,neighbor);
   put(var,'neighbors, neighbor . get(var,'neighbors));

procedure qqe_length!-graph!-is!-neighbor(var_1,var_2);
   % QQE length graph is neighbor. [var_1] and [var_2] are
   % variables. Function return [t] if [var_2] is a node in the
   % neighborhood of [var_1], else [nil].
   begin scalar flag, neighbors, neighbor;
      flag := nil;
      neighbors := qqe_length!-graph!-neighbors(var_1);
      while null flag and neighbors do <<
         neighbor := car  neighbors;
         if qqe_length!-graph!-neighbor!-node neighbor eq var_2 
         then
            flag := t;
         neighbors := cdr neighbors;
      >>;
      return if flag then neighbor else nil;
   end;

procedure qqe_length!-graph!-delete!-neighbor(var, neighbor);
   % QQE length graph delete neighbor. [var] is a variable, [neighbor]
   % is a neighbor element. [neighbor] is deleted from the
   % neighborhood of [var].
   begin scalar neighbors, neighbors2, flag;
      neighbors := qqe_length!-graph!-neighbors var;
      while null flag and neighbors do <<
         if neighbor eq car neighbors then <<
            flag := t;
            put(var, 'neighbors,append(neighbors2,cdr neighbors));
         >>;
         neighbors2 := append(neighbors2, {car neighbors});
         neighbors := cdr neighbors;
      >>;
   end;

procedure qqe_length!-graph!-edge(el,er,diff);
   % QQE length graph edge. Constructor for an edge. [el], [er] and
   % [diff] are integers.
   {el,er,diff};

procedure qqe_length!-graph!-neighbor!-left!-on!-edge(neighbor);
   % QQE length graph neighbor left on edge. [neighbor] is a neighbor
   % element. Return a boole.
   cadr neighbor;

procedure qqe_length!-graph!-neighbor!-edge1(neighbor);
   % QQE length graph neighbor edge1. [neighbor] is a neighbor. Returns
   % the left edge value of neighbor [neighbor]. !!! Misnamer
   if qqe_length!-graph!-neighbor!-left!-on!-edge neighbor then
      qqe_length!-graph!-neighbor!-edge!-left neighbor
   else qqe_length!-graph!-neighbor!-edge!-right neighbor;
   %car cadr cadr neighbor;

procedure qqe_length!-graph!-neighbor!-edge2(neighbor);
   % QQE length graph neighbor edge2. [neighbor] is a neighbor. Returns
   % the right edge value of neighbor [neighbor]. !!! Misnamer
   if qqe_length!-graph!-neighbor!-left!-on!-edge neighbor then
      qqe_length!-graph!-neighbor!-edge!-right neighbor
   else qqe_length!-graph!-neighbor!-edge!-left neighbor;
   % cadr cadr cadr neighbor;

procedure qqe_length!-graph!-neighbor!-edge(neighbor);
   % QQE length graph neighbor edge. [neighbor] is a neighbor
   % element. Returns the edge belonging to [neighbor].
   car cddr neighbor;

procedure qqe_length!-graph!-neighbor!-edge!-left(neighbor);
   % QQE length graph neighbor edge left. [neighbor] is a neighbor
   % element. Returns the left edge value of the edge belonging to
   % [neighbor].
   qqe_length!-graph!-edge!-el qqe_length!-graph!-neighbor!-edge neighbor;

procedure qqe_length!-graph!-neighbor!-edge!-right(neighbor);
   % QQE length graph neighbor edge right. [neighbor] is a neighbor
   % element. Returns the right edge value of the edge belonging to
   % [neighbor].
   qqe_length!-graph!-edge!-er qqe_length!-graph!-neighbor!-edge neighbor;

procedure qqe_length!-graph!-neighbor!-edge!-diff(neighbor);
   % QQE length graph neighbor diff. [neighbor] is a neighbor. Return
   % the diff value of the edge belonging to [neighbor].
   caddr qqe_length!-graph!-neighbor!-edge neighbor;

procedure qqe_length!-graph!-edge!-mark(edge);
   % QQE length graph edge mark. [edge] is an edge element. Marks
   % [edge].
   cdr edge := append(cdr edge, {t});

procedure qqe_length!-graph!-edge!-marked(edge);
   % QQE length graph edge marked. [edge] is an edge element. Returns
   % [t], if [edge] is marked, else [nil].
   cdddr edge;

procedure qqe_length!-graph!-edge!-unmark(edge);
   % QQE length graph edge unmark. [edge] is an edge element. Unmarks
   % [edge].
   if qqe_length!-graph!-edge!-marked edge then
      cddr edge := {caddr edge};

procedure qqe_length!-graph!-edge!-el(edge);
   % QQE length graph edge edge value left. [edge] is an edge
   % element. Returns left edge value of [edge].
   car edge;

procedure qqe_length!-graph!-edge!-er(edge);
      % QQE length graph edge edge value right. [edge] is an edge
   % element. Returns right edge value of [edge].
   cadr edge;

procedure qqe_length!-graph!-edge!-diff(edge);
   % QQE length graph edge diff. [edge] is an edge element. Returns
   % the diff value of [edge].
   caddr edge;

procedure qqe_length!-graph!-neighbor!-diff(neighbor);
   % QQE length graph neighbor diff. [neighbor] is a neighbor
   % element. Returns the diff value of the edge belonging to
   % [neighbor].
   if qqe_length!-graph!-neighbor!-left!-on!-edge neighbor then
      qqe_length!-graph!-edge!-diff 
         qqe_length!-graph!-neighbor!-edge neighbor
   else - qqe_length!-graph!-edge!-diff
      qqe_length!-graph!-neighbor!-edge neighbor;
   % car cadr neighbor;

procedure qqe_length!-graph!-neighbor!-node(neighbor);
   % QQE length graph neighbor node. [neighbor] is a neighbor
   % element. Return node belonging to [neighbor].
   car neighbor;

procedure qqe_length!-graph!-neighbors(node);
   % QQE length graph neighbors. [node] is an node element. Returns
   % all neighbors of node in the length graph.
   get(node,'neighbors);

procedure qqe_update!-minlength(var, length);
   % QQE update minlength. [var] is a node, [length] an
   % integer. Updates the minlength of [var] to [length]. Returns
   % value if update is successfull.
   << if null minlength or minlength < length 
   then put(var,'minlength, length) >>
      where minlength=get(var,'minlength);

procedure qqe_update!-maxlength(var, length);
   % QQE update maxlength. [var] is a node, [length] an
   % integer. Updates the maxlength of [var] to [length]. Returns
   % value if update is successfull.
   << if null maxlength or maxlength > length 
   then put(var,'maxlength, length) >>
      where maxlength=get(var,'maxlength);

procedure qqe_update!-headmin(at, var_list);
   % QQE update headmin. [at] is an atomic formula. [var_list] is a
   % list of nodes. Updates the headmin values of all variables of
   % atomic formula [at]. Returns [var_list] extended with the nodes,
   % for which the headmin values has been updated.
   begin 
      var_list := qqe_length!-graph!-bterm(qqe_arg2l at, var_list);
      var_list := qqe_length!-graph!-bterm(qqe_arg2r at, var_list);
      return var_list;
   end;

% that has to be done for every connection component


procedure qqe_length!-graph!-detect!-bad!-circles(nodes);
   % QQE length graph detect bad circles. [nodes] is a list of nodes
   % of a length graph. Function returns a list of all bad circles
   % which appear in the graph represented by [nodes].
   begin scalar bad_circles, new_nodes, new_edges, new_bad!-circles;
      for each x in nodes do
         if not qqe_length!-graph!-marked x then <<
            % components := x . components;
            << new_bad!-circles := car y;
               new_nodes := append(new_nodes,cadr y);
               new_edges := append(new_edges,caddr y);
               if qqe_debug!* then 
                  prin2t{"bad circles component returned" ,y};
            >> where y=qqe_length!-graph!-detect!-bad!-circles!-component(
               x,0,nil,0);
               if new_bad!-circles then 
                  bad_circles := append(new_bad!-circles,bad_circles);
         >>;
      qqe_length!-graph!-detect!-bad!-circles!-clean!-up(new_nodes,
         new_edges);
      return bad_circles;
   end;

procedure qqe_length!-graph!-detect!-bad!-circles!-clean!-up(nodes,edges);
   % QQE length graph detect bad circles clean up. [nodes] is a list
   % of nodes. [edges] is a list of edges. Function removes all marks
   % which were generated by the bad circles detection algorithm from
   % [nodes] and [edges].
   <<
      qqe_length!-graph!-detect!-bad!-circles!-rem!-marks!-nodes(nodes);
      qqe_length!-graph!-detect!-bad!-circles!-rem!-marks!-edges(edges);
   >>;

procedure qqe_length!-graph!-remove!-edge!-marks(edges);
   % QQE length graph remove edge marks. [edges] is a list of
   % edges. Removes all marks from [edges].
   for each x in edges do
      qqe_length!-graph!-remove!-edge!-marks!-neighbors(
         qqe_length!-graph!-neighbors x);

procedure qqe_length!-graph!-remove!-edge!-marks!-neighbors(neighbors);
   % QQE length graph remove edge marks neighbor. [neighbors] is a
   % list of neighbors. Function removes all marks from nodes in
   % [neighbors].
   for each x in neighbors do
      qqe_length!-graph!-edge!-unmark qqe_length!-graph!-neighbor!-edge x;

procedure qqe_graph!-get!-dfsnum(node);
   % QQE graph get dfsnum. [node] is a node element. Return the dfsnum
   % of [node].
   get(node,'dfsnum);

procedure qqe_graph!-put!-dfsnum(node,num);
   % QQE graph put dfsnum. [node] is a node element. [num] is a
   % positive integer or zero. Updates [node] dfsnum by [num].
   put(node,'dfsnum,num);

procedure qqe_graph!-rem!-dfsnum(node);
   % QQE graph remove dfsnum. [node] is a node element. Remove dfsnum
   % from [node].
   remprop(node,'dfsnum);

procedure qqe_length!-graph!-detect!-bad!-circles!-component(node,sigma,path,
      dfsnum);
   % QQE length graph detect bad circles component. [node] is a node
   % element, [sigma] is a integer, [path] is a path, [dfsnum] is a
   % positive integer. Function returns {bad_circles, nodes, edges},
   % where [bad_circles] is a list of all bad graphs in the component
   % represented by [node], [nodes] ([edges]) are all nodes
   % (resp. edges) touched by the algorithm (this is only needed for
   % later clean-up). 
   begin scalar bad_circles, nodex, sigmax, edgex, leftx, new_nodes, 
         new_edges, bad_circles!-branch;
      if qqe_debug!* then 
         prin2t{"qqe_graph!-detect!-bad!-circles with node = ",node,
         "sigma = ", sigma, "path = ", path};
      qqe_length!-graph!-mark node;
      qqe_graph!-put!-dfsnum(node,dfsnum);
      new_nodes := {node};
      dfsnum := dfsnum + 1;
      put(node,'blocksum,sigma);
      path := qqe_length!-graph!-path!-insert!-node!-right(path,node);

      for each x in qqe_length!-graph!-neighbors node do <<
         sigmax := sigma + qqe_length!-graph!-neighbor!-diff x;
         nodex := qqe_length!-graph!-neighbor!-node x;
         edgex := qqe_length!-graph!-neighbor!-edge x;
         leftx := qqe_length!-graph!-neighbor!-left!-on!-edge x;
         % prin2t edgex;
         if not qqe_length!-graph!-neighbor!-marked x 
            and not qqe_length!-graph!-edge!-marked edgex
         then <<
            if qqe_debug!* then  prin2t{"no circle with ",nodex};
            qqe_length!-graph!-edge!-mark edgex;
            new_edges := edgex . new_edges;
            <<
               bad_circles!-branch := car x;
               new_nodes := append(new_nodes,cadr x);
               new_edges := append(new_edges,caddr x);
            >> where x=qqe_length!-graph!-detect!-bad!-circles!-component(
               nodex,sigmax,qqe_length!-graph!-path!-insert!-edge!-right(path,
                  edgex,leftx), dfsnum);
            if bad_circles and bad_circles!-branch then 
               bad_circles := bad_circles!-branch . bad_circles
            else if bad_circles!-branch then
               bad_circles := {bad_circles!-branch};
            >>
         else if not qqe_length!-graph!-edge!-marked edgex then <<
            if qqe_debug!* then 
               prin2t{"circle with sum=",sigmax, node, nodex};
            qqe_length!-graph!-edge!-mark edgex;
            new_edges := edgex . new_edges;
            if not(sigmax = get(nodex,'blocksum)) then << %bad cycle
               if qqe_debug!* then prin2t "detect blocks : !!! bad_cycle";
               bad_circles :=  if bad_circles then 
                  {qqe_length!-graph!-get!-circle(
                  qqe_length!-graph!-path!-insert!-edge!-right(path,edgex,
                     leftx), nodex)} . bad_circles
               else {{qqe_length!-graph!-get!-circle(
                  qqe_length!-graph!-path!-insert!-edge!-right(path,edgex,
                     leftx), nodex)}};
               put(node,'bad_cycle,t); >>;
         >>;
      >>;
      if qqe_debug!* then 
         prin2t{"return from node = ",node,"with bad_circles = ",bad_circles};
      bad_circles := 
         qqe_length!-graph!-shuffle!-circle!-psets(bad_circles,node);
      return {bad_circles,new_nodes,new_edges};
   end;

procedure qqe_length!-graph!-detect!-bad!-circles!-rem!-marks!-nodes(nodes);
   % QQE length graph detect bad circles remove marks nodes. [nodes]
   % is a list of nodes. 
   for each node in nodes do <<
      qqe_length!-graph!-unmark node;
      remprop(node,'blocksum);
      qqe_graph!-rem!-dfsnum(node);
   >>;

procedure qqe_length!-graph!-detect!-bad!-circles!-rem!-marks!-edges(edges);
   % QQE length graph detect bad circles remove marks edges. [edges]
   % is a list of edges.
   for each x in edges do
      qqe_length!-graph!-edge!-unmark x;

procedure qqe_length!-graph!-get!-circle(path,node);
   % QQE length graph get circle. [path] is a path, [node] is a node
   % element. Returs the circle (in path format) in path [path].
   begin
      while car path neq node do
         path := cdr path;
      return path;
   end;

procedure qqe_print!-prop!-path(list,prop);
   % Debugging function.
   while list do <<
      % prin2t{car list, get(car list,prop)};
      list := qqe_length!-graph!-path!-step list;
   >>;

procedure qqe_length!-graph!-maxlength!-bad!-circles(circles);
   % QQE length graph maxlength bad circles. [circles] is a list of
   % (bad) circles. Function calculates maxlength for all nodes
   % appearing in circles of [circles].
   for each circle in circles do
      qqe_length!-graph!-maxlength!-bad!-circle circle;

procedure qqe_length!-graph!-maxlength!-bad!-circle(circle);
   % QQE length graph maxlength bad circle. [circle] is a bad
   % circle. Function updates maxlength value of all nodes appearing
   % in [circle].  use new constructors
   begin scalar rev_circle, top, circle_ext, top_circle;
      top_circle := car circle;
      rev_circle := qqe_length!-graph!-reverse!-path circle;
      rev_circle := append(rev_circle,rev_circle);
      circle_ext := append(circle,circle);
      qqe_update!-maxlength(car circle,
            qqe_length!-graph!-maxlength!-bad!-circle1 circle_ext);
      circle_ext := qqe_length!-graph!-path!-step circle_ext;
      top := car circle_ext; %!!!
      while top neq top_circle do <<
         qqe_update!-maxlength(top,
            qqe_length!-graph!-maxlength!-bad!-circle1 circle_ext);
         circle_ext := qqe_length!-graph!-path!-step circle_ext;
         top := car circle_ext;
      >>;
      qqe_update!-maxlength(car rev_circle,
         qqe_length!-graph!-maxlength!-bad!-circle1 rev_circle);
      rev_circle := qqe_length!-graph!-path!-step rev_circle;
      top := car rev_circle;
      while top neq top_circle do <<
         qqe_update!-maxlength(top,
            qqe_length!-graph!-maxlength!-bad!-circle1 rev_circle);
         rev_circle := qqe_length!-graph!-path!-step rev_circle;
         top := car rev_circle;
      >>;
      circle_ext := circle;
      while circle_ext do <<
         top := car circle_ext;
         qqe_length!-graph!-update!-maxlength!-context(top,
            get(top,'maxlength));
         circle_ext := qqe_length!-graph!-path!-step circle_ext;
      >>;
      if qqe_debug!* then qqe_print!-prop!-path(circle,'maxlength);
   end;

procedure qqe_length!-graph!-path!-next!-node(path);
   % QQE length graph path next node. [path] is a path. Returns next
   % node appearing on path.
   if cdr path then if cddr path then caddr path;

procedure qqe_length!-graph!-path!-step(path);
   % QQE length graph path step. [path] is a path. Proceeds one step
   % forward in path [path].
   if cdr path then cddr path else nil;

procedure qqe_length!-graph!-path!-next!-edge(path);
   % QQE length graph path next edge. [path] is a path. Returns the
   % next edge in [path].  i'm standing on a node
   if cdr path then cadr cadr path;

procedure qqe_length!-graph!-path!-next!-edge!-left(path);
   % QQE length graph path next edge left. [path] is a path. Returns
   % the left value of the next edge appearing in [path].
   if cdr path then car cadr path;

procedure qqe_length!-graph!-path!-next!-edge!-value(path);
   % QQE length graph path next edge value. Return the value of the
   % side according to the value [left!-on!-edge] for the next edge on
   % [path].
   if qqe_length!-graph!-path!-next!-edge!-left path then
      qqe_length!-graph!-edge!-el 
         qqe_length!-graph!-path!-next!-edge path
   else qqe_length!-graph!-edge!-er 
      qqe_length!-graph!-path!-next!-edge path;

procedure qqe_length!-graph!-path!-next!-edge!-diff(path);
   % QQE length graph path next edge diff. [path] is a path. Returns
   % the diff value of the next edge appearing on [path].  i'm
   if qqe_length!-graph!-path!-next!-edge!-left path then
      qqe_length!-graph!-edge!-diff 
         qqe_length!-graph!-path!-next!-edge path
   else - qqe_length!-graph!-edge!-diff 
      qqe_length!-graph!-path!-next!-edge path;

procedure qqe_length!-graph!-maxlength!-bad!-circle1(circle);
   begin scalar maxlength, sigma, e, node, top;
      maxlength := qqe_length!-graph!-path!-next!-edge!-value circle 
         + 1;
      sigma := maxlength + 
         qqe_length!-graph!-path!-next!-edge!-diff circle;
      top := car circle;
      while node neq top do <<
         circle := qqe_length!-graph!-path!-step circle;
         node := car circle;
         e :=  max(0,qqe_length!-graph!-path!-next!-edge!-value circle
            - sigma + 1);
         maxlength := maxlength + e;
         sigma := sigma + e;
      >>;
      return maxlength - 1;   
   end;

procedure qqe_length!-graph!-reverse!-path(path);
   % QQE length graph reverse path. [path] is a path. Returns reversed
   % path [path].
   begin scalar flag, rev_path;
      flag := t;
      for each x in cdr path do <<
         if flag then 
            rev_path := qqe_length!-graph!-path!-insert!-edge!-left(
               rev_path, cadr x, not car x)
         else rev_path := x . rev_path;
         flag := not flag;
      >>;
      return car path . rev_path;
   end;
         

procedure qqe_length!-graph!-path!-insert!-edge!-right(path,edge,left);
   % QQE length graph insert edge right. Inserts a new edge to the
   % right of the ath [path]. [edge] is a edge. [left] is a boole.
   append(path,{{left,edge}});

procedure qqe_length!-graph!-path!-insert!-edge!-left(path,edge,left);
   % QQE length graph path insert edge left. Inserts a new edge to the
   % left of the ath [path]. [edge] is a edge. [left] is a boole.
   {left,edge} . path;

procedure qqe_length!-graph!-path!-insert!-node!-right(path,node);
   % QQE length graph path insert node right. [path] is a path, [node]
   % is a node. Inserts node from the right in [path].
   append(path,{node});


procedure qqe_length!-graph!-path!-insert!-node!-left(path,node);
   % QQE length graph path insert node left. [path] is a path, [node]
   % is a node. Inserts node from the left in [path].
   node . path;
      

procedure qqe_length!-graph!-shuffle!-circle!-psets(circle_sets,node);
   %% QQE length graph shuffle circle powersets. [circle_sets] is a
   %% list of sets of circles. [node] is a node element. Function
   %% returns a list of circles.
   begin scalar set1, circles;
      if null circle_sets then return nil
      else if null cdr circle_sets then return car circle_sets;
      for each cs1 on circle_sets do <<
         set1 := car cs1;
         for each set2 in cdr cs1 do
            circles := append(qqe_length!-graph!-shuffle!-circle!-sets(set1,
               set2,node),circles);
      >>;
      return circles;
   end;

procedure qqe_length!-graph!-shuffle!-circle!-sets(set1, set2,node);
   % QQE length graph shuffle circle sets. [set1], [set2] are sets of
   % circles. [node] is a node element. Function shuffles circles in
   % [set1] with circles in [set2] and returns a list of circles.
   begin scalar circles;
      for each circle1 in set1 do 
         for each circle2 in set2 do 
            if qqe_length!-graph!-shufflable(circle1,circle2,node) then
               circles := qqe_length!-graph!-shuffle!-circles(circle1, 
                  circle2,node) . circles;
      return append(circles,append(set1,set2));
   end;

procedure qqe_length!-graph!-shufflable(circle1,circle2,node);
   % QQE length graph shufflable. [circle1], [circle2] are
   % circles. [node] is a node element. Returns [t] if circles are
   % shufflable, else [nil].
   <<
      if circle1_topdfs and circle2_topdfs and circle1_topdfs >= nodedfs 
         and 
         circle2_topdfs >= nodedfs and not(circle1_topdfs = circle2_topdfs)
      then t else nil >>
         where circle1_topdfs=qqe_graph!-get!-dfsnum car circle1,
      circle2_topdfs=qqe_graph!-get!-dfsnum car circle2, 
      nodedfs=qqe_graph!-get!-dfsnum node;

procedure qqe_length!-graph!-shuffle!-circles(c1,c2, node);
   % QQE length graph shuffle circles. [c1], [c2] are circles. [node]
   % is a node element. Function shuffles [c1] with [c2].
   begin scalar firstc, secondc, shufflec, temp;
      if qqe_length!-graph!-shuffle!-circles!-order(c1,c2,node) then <<
         firstc := c1;
         secondc := c2;
      >>
      else <<
         firstc := c2;
         secondc := c1;
      >>;
      % temp_c := firstc;
      temp := car firstc;
      while temp neq car secondc do <<
         shufflec := append(shufflec,{temp});
         firstc := cdr firstc;
         temp := car firstc;
      >>;
      shufflec := append(shufflec,{temp});
      secondc := reverse secondc;
      temp := car secondc;
      while temp neq node do <<
         shufflec := append(shufflec,{temp});
         secondc := cdr secondc;
         temp := car secondc;
      >>;
      while car firstc neq node do 
         firstc := cdr firstc;
      shufflec := append(shufflec,firstc);
      
      return shufflec;
   end;

   
procedure qqe_length!-graph!-shuffle!-circles!-order(c1,c2,node);
   % QQE length graph shuffle circles order. [c1], [c2] are
   % circles. [node] is a node element. Returns [t] if [c1] is of
   % higher order then [c2], [nil] else.
   begin scalar top1, top2, top1_temp, top2_temp;
     top1_temp := car c1;
     top2_temp := car c2;
     top1 := top1_temp;
     top2 := top2_temp;
     while top1_temp neq top2 and top2_temp neq top1 and
        top1_temp neq node and top2_temp neq node do <<
           c1 := cdr c1;
           c2 := cdr c2;
           top1_temp := car c1;
           top2_temp := car c2;
        >>;
     if top2_temp eq top1 or top1_temp eq node then
        return nil
     else return t;
   end;  



procedure qqe_length!-graph!-neighbor!-mark(neighbor);
   % QQE length graph neighbor mark. [neighbor] is a neighbor
   % element. Function marks the node belonging to [neighbor]. 
   qqe_length!-graph!-mark qqe_length!-graph!-neighbor!-node neighbor;

procedure qqe_length!-graph!-neighbor!-marked(neighbor);
   % QQE length graph neighbor marked. [neighbor] is a neighbor
   % element. Returns [t] if the node belonging to [neighbor] is
   % marked, [nil] else.
   qqe_length!-graph!-marked qqe_length!-graph!-neighbor!-node neighbor;
       
procedure qqe_length!-graph!-update!-maxlength(node,length);
   % QQE length graph update maxlength. [node] is a node element,
   % [length] is a positive integer. Recursively updates maxlengths of
   % all nodes within the reach of [node] (t.i. the transitive closure
   % of the neighborhood). [node] is updated.
   if qqe_update!-maxlength(node,length) then 
      for each x in qqe_length!-graph!-neighbors node do 
         qqe_length!-graph!-update!-maxlength(
            qqe_length!-graph!-neighbor!-node x, 
            if qqe_length!-graph!-neighbor!-edge1 x >= length then 
               qqe_length!-graph!-neighbor!-edge2 x 
            else length + qqe_length!-graph!-neighbor!-diff x);

procedure qqe_length!-graph!-update!-maxlength!-context(node,length);
   % QQE length graph update maxlength context. [node] is a node
   % element, [length] is a positive integer. Recursively update the
   % maxlengths of all nodes within the reach of [node] (t.i. the
   % transitive closure of the neighborhood). [node] is not updated.
   for each x in qqe_length!-graph!-neighbors node do <<
      % prin2t x;
      qqe_length!-graph!-update!-maxlength(
         qqe_length!-graph!-neighbor!-node x, 
         if qqe_length!-graph!-neighbor!-edge1 x >= length then 
            qqe_length!-graph!-neighbor!-edge2 x 
         else length + qqe_length!-graph!-neighbor!-diff x); 
   >>;

procedure qqe_length!-graph!-update!-maxlengths(nodes);
   % QQE length graph update maxlengths. [nodes] is a list of
   % nodes. Recursively update the maxlength in the contexts of each
   % element in [nodes].
   for each x in nodes do 
      qqe_length!-graph!-update!-maxlength!-context(x,get(x,'maxlength));

procedure qqe_length!-graph!-update!-minlengths(nodes);
   % QQE length graph update minlength. [nodes] is a list of
   % nodes. Recursively update the minlength in the contexts of each
   % element in [nodes].
   for each x in nodes do 
      qqe_length!-graph!-update!-minlength!-context(x,get(x,'minlength));

procedure qqe_length!-graph!-update!-minlength!-context(node,length);
   % QQE length graph update minlength context. [node] is a node
   % element, [length] is a positive integer. Recursively update the
   % minlengths of all nodes within the reach of [node] (t.i. the
   % transitive closure of the neighborhood). [node] is not updated.
   for each x in qqe_length!-graph!-neighbors node do
      if qqe_length!-graph!-neighbor!-edge1 x < length then
         qqe_length!-graph!-update!-minlength(
            qqe_length!-graph!-neighbor!-node x, 
            length + qqe_length!-graph!-neighbor!-diff x);

procedure qqe_length!-graph!-update!-minlength(node, length);
   % QQE length graph update minlength. [node] is a node element,
   % [length] is a positive integer. Recursively updates minlengths of
   % all nodes within the reach of [node] (t.i. the transitive closure
   % of the neighborhood). [node] is updated.
   if qqe_update!-minlength(node,length) then
      for each x in qqe_length!-graph!-neighbors node do
         if qqe_length!-graph!-neighbor!-edge1 x < length then
            qqe_length!-graph!-update!-minlength(
               qqe_length!-graph!-neighbor!-node x, 
               length + qqe_length!-graph!-neighbor!-diff x);

procedure qqe_length!-graph!-marked(var);
   % QQE length graph marked. [var] is a node. Return [t] if [var] is
   % marked, [nil] else.
    get(var,'blockmark);

procedure qqe_length!-graph!-mark(var);
   % QQE length graph mark. [var] is a node. Functions marks [var].
   put(var,'blockmark,t);

procedure qqe_length!-graph!-unmark(var);
   % QQE length graph unmark. [var] is a node. Function unmarks [var].
   remprop(var,'blockmark);

procedure qqe_length!-graph!-bterm(term, var_list);
   % QQE length graph basic type term. Subroutine of
   % [qqe_length!-graph!-clause]. [term] is a term of basic
   % type. [var_list] is a list of identifiers. Returns a list of
   % identifiers. Newly in the graph as nodes inserted identifiers are
   % add to [var_list].
   begin
      if null term or atom term then return var_list;
      if qqe_op term memq '(lhead rhead) then 
         var_list := qqe_length!-graph!-bterm!-update!-headmin(term, var_list)
      else for each x in cdr term do 
         var_list := qqe_length!-graph!-bterm(x,var_list);
      return var_list;
   end;

procedure qqe_length!-graph!-bterm!-update!-headmin(term, var_list);
   % QQE ength graph basic term update headmin. Subroutine of
   % [qqe_length!-graph!-bterm]. [term] is a basic term with leading
   % lhead or rhead. [var_list] is a list of identifiers. Returns a
   % list of identifiers. Newly in the graph as nodes inserted
   % identifiers are added to [var_list].
   begin scalar var, prefix_length, headmin;
      var := qqe_qprefix!-var term;
      if var eq 'qepsilon then return;
      prefix_length := qqe_prefix!-length term;
      headmin := get(var,'headmin);
      if null headmin then <<
         put(var,'headmin,prefix_length);
         var_list := lto_insertq(var,var_list);
      >>
      else if prefix_length > headmin then 
         put(var,'headmin,prefix_length); 
      return var_list;
   end; 

procedure qqe_length!-graph!-at!-notq(at, var_list);
   % TODO obsolete
   begin
      var_list := qqe_length!-graph!-term!-notq(qqe_arg2l at, 
         var_list);
      var_list := qqe_length!-graph!-term!-notq(qqe_arg2r at, 
         var_list);
      return var_list;
   end;

procedure qqe_length!-graph!-term!-notq(term, var_list);
   % TODO obsolete
   begin scalar var;
      if atom term then return var_list
      else if qqe_op term memq '(lhead rhead) then << 
         var := qqe_qprefix!-var term;
         qqe_update!-minlength(var, qqe_prefix!-length term);
         return lto_insertq(var,var_list);
      >>
      else <<
         for each x in cdr term do
            var_list := qqe_length!-graph!-term!-notq(x, var_list);
         return var_list;
      >>;
   end;

procedure qqe_length!-graph!-at!-qneq(lhs, var_list);
   % QQE length graph atomic formula with qneq. Subroutine of
   % [qqe_length!-graph!-clause]. [lhs] is one / the left hand side of
   % a atomic formula M q <<>> qepsilon. [var_list] is a list of
   % identifiers. Returns a list of identifiers. Newly in the graph as
   % nodes inserted identifiers are add to [var_list].
   begin scalar var_lhs;
      var_lhs := qqe_qprefix!-var lhs;
      qqe_update!-minlength(var_lhs, qqe_prefix!-length lhs + 1);
      return lto_insertq(var_lhs,var_list);
   end;

procedure qqe_length!-graph!-at!-qequal(at, var_list);
   % QQE length graph atomic formula with qequal. Subroutine of
   % [qqe_length!-graph!-clause]. [at] is one / the left hand side of
   % a atomic formula M q <<>> qepsilon. [var_list] is a list of
   % identifiers. Returns a list of identifiers. Newly in the graph as
   % nodes inserted identifiers are add to [var_list].
   begin scalar varlhs, varrhs, lhs, rhs;
      lhs := qqe_arg2l at;
      rhs := qqe_arg2r at;
      varlhs := qqe_qprefix!-var(lhs);
      varrhs := qqe_qprefix!-var(rhs);
      
      if varrhs eq 'qepsilon then <<
         qqe_update!-maxlength(varlhs,qqe_prefix!-length lhs);
         var_list := lto_insertq(varlhs,var_list);
      >>
      else if varlhs eq 'qepsilon then <<
         qqe_update!-maxlength(varrhs,qqe_prefix!-length rhs);
         var_list := lto_insertq(varrhs,var_list);
      >>
      else << 
         qqe_update!-graph!-adlist(varlhs,varrhs,qqe_prefix!-length lhs,
            qqe_prefix!-length rhs);
         var_list := lto_insertq(varlhs,var_list);
         var_list := lto_insertq(varrhs,var_list);
      >>;
      
      return var_list;
   end;

procedure qqe_length!-graph!-correct(var_list);
   % QQE length graph correct. [var_list] is a list of identifiers
   % representing nodes of the graph. Return [t] if graph is correct,
   % [nil] otherwise. Subroutine is [qqe_length!-graph!-correct!-adlist].
   begin scalar minlength, maxlength, list, v, correct;
      list := var_list;
      if null var_list then return t;
      correct := t;
      while list and correct do <<
         v := car list;
         % if null get(v,'lengthmark) then <<
         %   put(v,'lengthmark,t);
         minlength := get(v,'minlength);
         maxlength := get(v,'maxlength);
         %% todo: work here with the length comparison functions
         if minlength and maxlength and maxlength < minlength then 
            correct := nil;
         if correct and minlength then 
            correct := qqe_length!-graph!-correct!-adlist(v, minlength);
         % >>;
         list := cdr list;
      >>;
      % qqe_length!-graph!-remove!-mark(var_list);
      return if null correct then correct else var_list;   
   end;

procedure qqe_length!-graph!-correct!-adlist(v, minlength);
   % QQE length graph correct adlist. [v] is a identifier representing
   % a node in the graph. [minlength] is an integer: the minlength of
   % [v]. Routine checks the adlist of [v] for correctness according
   % to [minlength] of [v]. Returns [t] if adlist is correct, [nil]
   % otherwise.
   begin scalar correct, temp, el, x, list, maxlength, var;
      % minlength := get(v,'minlength);
      correct := t;
      list := get(v,'adlist);
      while list and correct do <<
         var := car car list;
         maxlength := get(var,'maxlength);
      
         el := cdr car list;
         while correct and el do <<
            %% x := (diffi,(li,ri));
            x := car el;
            if minlength > car cadr x then <<
               if maxlength and maxlength < cadr cadr x then 
                  correct := nil;
               if null temp then temp := car x
               else if car x neq temp then correct := nil;
            >>;
            el := cdr el;
         >>;
         temp := nil;
         list := cdr list;
      >>;
      return correct;
   end;

procedure qqe_length!-graph!-remove!-mark(var_list,mark);
   % QQE length graph remove mark. [var_list] is a list of identifiers
   % representing nodes in a length graph. [mark] is a
   % property. Removes all properties [mark] for each element in list
   % [var_list].
   for each v in var_list do <<
      % prin2t{"removing mark",mark," from ",v};
      remprop(v,mark);
   >>;

procedure qqe_length!-graph!-delete(list);
   % QQE length graph delete. [list] is a list of identifiers
   % representing a length graph. Routine deletes the length graph,
   % that is: all properties of the length graph are removed from the
   % variables.
   begin
      if null list then return;
      qqe_length!-graph!-remove!-mark(list,'maxlength);
      qqe_length!-graph!-remove!-mark(list,'minlength);
      qqe_length!-graph!-remove!-mark(list,'adlist);
      qqe_length!-graph!-remove!-mark(list,'headmin);
      qqe_length!-graph!-remove!-mark(list,'neighbors);
   end;

procedure qqe_length!-graph!-print(list);
   % QQE length graph print. [list] is a list of identifiers
   % representing a length graph. Textual print routine for length
   % graphs. Needed for debugging purposes only.
   for each x in list do <<
      prin2t{"var=",x,"with minlength=",get(x,'minlength),
         " and maxlength=", get(x,'maxlength), " and headmin=", 
         get(x,'headmin), " with adlist", get(x,'neighbors)};
   >>;

procedure qqe_print!-prop!-list(list,prop);
   for each x in list do
      prin2t{x,get(x,prop)};

procedure qqe_minlength!-var(var);
   % QQE minlength var. Returns the minlength of variable [var].
   <<
      if minlength then minlength
      else 0
   >> where minlength=get(var,'minlength);

procedure qqe_maxlength!-var(var);
   % QQE minlength var. Returns the maxlength of variable [var].
   <<
      if maxlength then maxlength
      else 'infty
   >> where maxlength=get(var,'maxlength);

procedure qqe_less!-length(l1,l2);
   % QQE less length. [l1] and [l2] are lengths, that is integers or
   % 'infty. Returns [t], if [l1] < [l2], otherwise [nil].
   if l1 eq l2 then nil
   else if l1 eq 'infty then nil
   else if l2 eq 'infty then t
   else if l1 < l2 then t
   else nil;

procedure qqe_lesseq!-length(l1,l2);
   if l1 eq l2 then t
   else if l1 eq 'infty then nil
   else if l2 eq 'infty then t
   else if l1 < l2 then t
   else nil;

procedure qqe_greatereq!-length(l1,l2);
   % QQE greater length. [l1] and [l2] are lengths, that is integers or
   % 'infty. Returns [t], if [l1] >= [l2], otherwise [nil].
   not qqe_less!-length(l1,l2);

procedure qqe_min!-length(l1,l2);
   % QQE min length. [l1] and [l2] are lengths, that is integers or
   % 'infty. Returns the min of [l1] and [l2].
   if l1 eq l2 then l1
   else if l1 eq 'infty then l2
   else if l2 eq 'infty then l1
   else if l1 < l2 then l1
   else l2;

procedure qqe_max!-length(l1,l2);
   % QQE max length. [l1] and [l2] are lengths, that is integers or
   % 'infty. Returns the max of [l1] and [l2].
   if l1 eq l2 then l1
   else if l1 eq 'infty then l1
   else if l2 eq 'infty then l2
   else if l1 < l2 then l2
   else l1;
   
procedure qqe_qprefix!-var(u);
   % QQE queue prefix of variable. [u] is a queue term without
   % appearences of ladd, radd. Returns the variable of sort queue
   % which is argument of the term, or qepsilon if V_queue(u) =
   % emptyset.
   begin
      while u and not atom u do
      <<
         if qqe_op u memq '(ltail rtail lhead rhead) then
            u := qqe_arg2l u
         else u :=  qqe_arg2r u;
      >>;
      return u;
   end;


%--------------harmless test-----------------------------

procedure qqe_harmless!-formula!-test(f);
   % QQE harmless formula test. [f] is a formula. Returns [t] if [f]
   % is harmless, [nil] otherwise.
   begin scalar dnf, flag;
      if atom f then return t;
      dnf := rl_dnf f;
      flag := t;
      if qqe_op dnf eq 'and then dnf := {dnf}
      else dnf := cdr dnf;
      while flag and dnf do <<
         flag := qqe_harmless!-formula!-test!-clause(car dnf);
         dnf := cdr dnf;
      >>;
      return flag;
   end;

procedure qqe_harmless!-formula!-test!-clause(clause);
   % QQE harmless formula test. [f] is a conjunction of atomic
   % formulas. Returns [t] if [f] is harmless, [nil] otherwise.
   begin scalar var_list, flag, flag2;
      var_list := qqe_length!-graph!-clause(clause,nil);
      flag := qqe_harmless!-formula!-test!-clause1(clause,var_list);
      flag2 := qqe_harmless!-formula!-test!-clause2(var_list);
      if flag neq flag2 then rederr "harmless-test failure";
      % qqe_length!-graph!-print var_list;
      qqe_length!-graph!-delete var_list;
      return flag;
   end;

procedure qqe_harmless!-formula!-test!-clause2(var_list);
   % subroutine of [qqe_harmless!-formula!-test!-clause].
   begin scalar harmless, minlength, headmin, v;
      harmless := t;

      while var_list and harmless do <<
         v := car var_list;
         minlength := get(v, 'minlength);
         headmin := get(v, 'headmin);
         
         if headmin and ((null minlength) or (headmin > minlength)) 
         then harmless := nil;

         var_list := cdr var_list;
      >>;
      return harmless;
   end;

%% following parts of harmless formula test is only needed if we want
%% to put the headmin calculation out of length graph ... but as
%% length graph calculation is at the moment only needed in
%% combination with harmless formula test we can save time this way

procedure qqe_harmless!-formula!-test!-clause1(clause, var_list);
   % TODO
   begin scalar flag, at, var_list;
      flag := t;
      if atom clause then return t;
      % if null qqe_length!-graph!-correct(var_list) then return nil;
      if null var_list then
         return t;  %% was nil -- why?!
      % qqe_length!-graph!-print var_list;
      
      if car clause neq 'and then clause := {clause}
      else clause := cdr clause;
      while flag and clause do <<
         at := car clause;
         if pairp at and not(qqe_op at memq '(qequal qneq)) 
         then flag := qqe_harmless!-formula!-test!-at(rl_prepat at);
         clause := cdr clause;
      >>;
      return flag;
   end;

procedure qqe_harmless!-formula!-test!-at(at);
   % QQE harmless formula test atomic formula. [at] is an atomic
   % formula. This is a subfunction of
   % [qqe_harmless!-formula!-test!-clause1].
   begin scalar flag;
      flag := qqe_harmless!-formula!-test!-term(qqe_arg2l at);
      if flag then
         flag := qqe_harmless!-formula!-test!-term(qqe_arg2r at);
      return flag;
   end;

procedure qqe_harmless!-formula!-test!-term(term);
   % QQE harmless formula test term. [term] is a
   % term. Function is a subfunction of
   % [qqe_harmless!-formula!-test!-at.
   begin scalar flag;
      if atom term then return t;
      flag := t;
      if qqe_op term memq '(lhead rhead) then 
         return qqe_harmless!-formula!-test!-qterm term;
      term := cdr term;
      while term and flag do <<
         flag := qqe_harmless!-formula!-test!-term car term;
         term := cdr term;
      >>;
      return flag;
   end;

procedure qqe_harmless!-formula!-test!-qterm(term);
   % QQE harmless formula test queue term. [term] is a
   % term of type queue. Function is a subfunction of
   % [qqe_harmless!-formula!-test!-term.
   begin scalar var, minlength;
      var := qqe_qprefix!-var term;
      if var eq 'qepsilon then return t;
      minlength := get(var,'minlength);
      if null minlength then return nil;
      if minlength < qqe_prefix!-length term then return nil
      else return t;
   end;

procedure qqe_make!-harmless(f);
   % QQE make harmless. rl_dnf doesnt preserve the harmless property
   % for harmless formulas in the conjunctions. This procedure takes
   % care of this. [f] is a dnf.
   begin scalar f2;
      if atom f then return f
      else if car f eq 'or then <<
         for each x in cdr f do 
            f2 := append(f2,{qqe_make!-harmless!-clause x});
         return append({'or}, f2);
      >>
      else return qqe_make!-harmless!-clause f;
   end;

procedure qqe_make!-harmless!-clause(f);
   % QQE make harmless clause. subfunction of
   % [qqe_make!-harmless]. [f] is a conjunction of atomic
   % formulas. returns an equivalent formula which is harmless.
   begin scalar add_on;
      if not (rl_cxp car f) then <<
         add_on := qqe_make!-harmless!-at rl_prepat f;
         return if add_on then append(append({'and},{f}),add_on)
         else f;
      >>
      else for each x in cdr f do 
         add_on := append(add_on,qqe_make!-harmless!-at rl_prepat x);
      return if add_on then append(append({'and},cdr f),add_on)
      else f;
   end;

procedure qqe_make!-harmless!-at f;
   % QQE make harmless atomic formula. subroutine of
   % [qqe_make!-harmless!-clause]. [f] is an atomic formula.
   begin scalar add_on;
      add_on := nil;
      if atom f or qqe_op f memq '(qneq qequal) then return nil;
      for each x in cdr f do <<
         if not atom x and qqe_op x memq '(lhead rhead) then 
            add_on := append(add_on,{{'qneq, qqe_arg2l x, 'qepsilon}})
         else add_on := append(add_on, qqe_make!-harmless!-at x);
      >>;
      % prin2t{"will return", add_on};
      return add_on;
   end;



%% end of the at the moment not needed code

% ---------------- satlengths qneq--------------------------------------------

procedure qqe_quicksort!-dbl!-crit(list);
   % Queue quantifier elimination quicksort. Quicksort of a list of integers
   % in reverse order. For example (2 4 3 5) -> (5 4 3 2).
   begin scalar pivot;
      if list then
      <<
         pivot := car list;
         list := qqe_partition!-dbl!-crit(cdr list,pivot);
         list := append(qqe_quicksort!-dbl!-crit(car list),
            append({pivot}, qqe_quicksort!-dbl!-crit(cadr list)));
      >>;
      return list;
  end;

procedure qqe_partition!-dbl!-crit(list, pivot);
   % Queue quantifier elimination partition. Belongs to quicksort algorithm.
   begin scalar l,r, x, lp;

      for each x in list do
      <<
         lp := qqe_lessp!-dbl!-crit(x,pivot);
         if null lp then r := append(r,{x})
         else if lp eq t then l := append(l,{x});
      >>;
      return {l,r};
   end;

procedure qqe_lessp!-dbl!-crit(x1,x2);
   % TODO at the moment not needed
   begin scalar minlength1, minlength2, maxlength1, maxlength2;
      minlength1 := get(x1,'minlength);
      minlength2 := get(x2,'minlength);
      maxlength1 := get(x1,'maxlength);
      maxlength2 := get(x2,'maxlength);
      
      if null minlength1 and null minlength2 then <<
         if null maxlength1 and null maxlength2 then return 'eq
         else if null maxlength2 then return nil
         else if null maxlength1 then return t
         else if maxlength1 < maxlength2 then return nil
         else if maxlength1 = maxlength2 then return 'eq 
         else return t;
      >>
      else if null minlength1 then return t
      else if null minlength2 then return nil
      else if minlength1 < minlength2 then return t
      else if minlength1 = minlength2 then <<
         if null maxlength1 and maxlength2 then return nil
         else if maxlength1 eq maxlength2 then return 'eq
         else return t;
      >>
      else return nil;
   end;

endmodule;  % [qqemisc]

end;  % of file
