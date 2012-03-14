module cvit;   % Header module for CVIT package.

% Authors:  A.Kryukov, A.Rodionov, A.Taranov.

% Copyright (C) 1988,1990, Institute of Nuclear Physics, Moscow State
%                          University.
% VERSION   2.1
% RELEASE   11-MAR-90

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


% 07.06.90 all MAP replaced by MAP_   RT
% 08.06.90 SOME MACROS FROM CVITMAP FILE ADDED to section IV RT
% 10.06.90 SOME MACROS FROM CVITMAP FILE ADDED  RT

% Modifications for Reduce 3.4.1 by John Fitch.

create!-package('(cvit red2cvit map2strn evalmaps intfierz cvitmap),
                '(contrib physics));

% The High Energy Physics package must be loaded first.

load_package hephys;

% These fluids and globals have been moved here for cleaner compilation.

  fluid  '(!*msg ndims!* dindices!*)$
  global '(windices!* indices!* !*cvit gamma5!* !*g5cvit)$
  if null windices!*
    then windices!*:=
            '(nil !_f0 !_f1 !_f2 !_f3 !_f4 !_f5 !_f6 !_f7 !_f8 !_f9)$

  if null gamma5!*
    then gamma5!*:=
            '(nil !_a0 !_a1 !_a2 !_a3 !_a4 !_a5 !_a6 !_a7 !_a8 !_a9)$

%GGGGGGGGGGGGGGGGGGGGGGGGG GLOBALS & FLUIDS FFFFFFFFFFFFFFFFFFFFFFFFF$


 global '( !_0edge)$

 fluid '( new_edge_list old_edge_list )$

      % NEW_EDGE_LIST - LIST OF CREATED EDGES$
      % OLD_EDGE_LIST - LIST OF INITIAL EDGES$

 fluid '(n_edge)$

      % N_EDGE - NUMBER OF CREATED EDGES$

% The following smacros need only be present during compilation.

  %************ SECTION I ************************************

  smacro   procedure hvectorp x$
     get(x,'rtype) eq 'hvector$

  smacro   procedure windexp x$
     x memq car windices!*$

  smacro   procedure replace_by_indexp v$
     get(v,'replace_by_index)$

  smacro   procedure indexp i$
     i memq indices!*$

  smacro   procedure replace_by_vectorp i$
     get(i,'replace_by_vector)$

  smacro   procedure replace_by_vector i$
     get(i,'replace_by_vector) or i$

  smacro   procedure gamma5p x$
     memq(x,car gamma5!*)$

  smacro   procedure nospurp x$
     flagp(x,'nospur)$

  smacro   procedure clear_gamma5()$
     gamma5!* := nil . append(reverse car gamma5!*,cdr gamma5!*)$

%********************* SECTION II **************************

symbolic smacro procedure p_empty_map_ map_$
% IS MAP_ EMPTY ? $
    null map_$

symbolic smacro procedure p_empty_vertex vertex$
% IS VERTEX EMPTY ? $
    null vertex$

%++++++++++++++++++++++++++ SELECTORS +++++++++++++++++++++++++++++++$

symbolic smacro procedure s_vertex_first map_$
% SELECT FIRST VERTEX IN MAP_ $
  car map_$

symbolic smacro procedure s_map__rest map_$
% SELECT TAIL OF MAP_ $
  cdr map_$

symbolic smacro procedure s_vertex_second map_$
% SELECT SECOND VERTEX IN MAP_ $
  s_vertex_first s_map__rest map_$

symbolic smacro procedure first_edge vertex$
% SELECT FIRST EDGE IN VERTEX $
  car vertex$

symbolic smacro procedure s_vertex_rest vertex$
% SELECT TAIL OF VERTEX $
  cdr vertex$

symbolic smacro procedure second_edge vertex$
% SELECT SECOND EDGE IN VERTEX $
  first_edge s_vertex_rest vertex$

symbolic smacro procedure s_edge_name edge$
% SELECT EDGE'S NAME $
  car edge$

symbolic smacro procedure s_edge_prop_ edge$
% SELECT PROP_ERTY OF AN EDGE (NAMES OF PARENTS OR NUMBERS)$
  cadr edge$

symbolic smacro procedure s_edge_type edge$
% SELEC TYPE (PARITY) OF AN EDGE$
  caddr edge$

%?????????????????????? CONSTRUCTORS ??????????????????????????????$

symbolic smacro procedure add_vertex (vertex,map_)$
% ADD VERTEX TO MAP_ $
  vertex . map_ $

symbolic smacro procedure add_edge (edge,vertex)$
% ADD EDGE TO VERTEX$
  edge . vertex$

symbolic smacro procedure append_map_s (map_1,map_2)$
% APPEND TWO MAP_S  $
  append(map_1,map_2)$

symbolic smacro procedure conc_map_s (map_1,map_2)$
% APPEND TWO MAP_S  $
 nconc(map_1,map_2)$

symbolic smacro procedure conc_vertex (vertex1,vertex2)$
% APPEND TWO VERTICES
 nconc(vertex1,vertex2)$

symbolic smacro procedure mk_name1 name$
 explode name$

symbolic smacro procedure mk_edge_prop_ (prop_1,prop_2)$
 prop_1 . prop_2 $

symbolic smacro procedure mk_edge_type (typ1,typ2)$
% DEFINED EDGE <=> TYPE T,
% UNDEFINED EDGE <=> TYPE NIL$
    typ1 and  typ2 $

symbolic smacro procedure mk_edge (name,prop_,type)$
% MAKE UP NEW EDGE $
 list(name,prop_,type)$

symbolic smacro procedure mk_edge3_vertex (edge1,edge2,edge3)$
% MAKES PRIMITIVE VERTEX  $
  list(edge1,edge2,edge3)$

symbolic smacro procedure mk_empty_map_ ()$
% GENERATE EMPTY MAP_ $
  nil $

symbolic smacro procedure mk_empty_vertex ()$
% GENERATE EMPTY VERTEX $
  nil $

symbolic smacro procedure mk_vertex1_map_ vertex1$
% MAKE MAP_ OF ONE VERTEX $
  list(vertex1)$

symbolic smacro procedure mk_vertex2_map_ (vertex1,vertex2)$
% MAKE MAP_ OF TWO VERTICES $
  list(vertex1,vertex2)$

symbolic smacro procedure mk_edge2_vertex (edge1,edge2)$
%MAKES VERTEX FROM TWO EDGES$
  list(edge1,edge2)$

 symbolic smacro procedure conc_vertex (vertex1,vertex2)$
  nconc(vertex1,vertex2)$

symbolic smacro procedure cycl_map_ map_$
% MAKES CYCLIC PERMUTATION OF MAP_$
    append(cdr map_,list car map_)$

symbolic smacro procedure cycl_vertex vertex$
% MAKES CYCLIC PERMUTATION OF VERTEX$
    append(cdr vertex,list car vertex)$

symbolic smacro procedure mk_world (actedges,world1)$
  list(actedges,list nil,world1)$

%====================== PREDICATES (CONTINUE) =====================$

symbolic smacro procedure p_member_edge (edge,vertex)$
% IS EDGE (WITH THE SAME NAME) CONTAINS IN VERTEX ?$
  assoc(s_edge_name edge,vertex)$

symbolic smacro procedure equal_edges (edge1,edge2)$
% IF EDGES HAVE THE SAME NAMES ? $
  eq   ( s_edge_name edge1, s_edge_name edge2)$

symbolic smacro procedure single_no_parents edges$
 length edges = 1 $

symbolic smacro procedure resto_map__order map_$
% REVERSE (BETTER REVERSIP) MAP_ $
  reverse map_$

symbolic smacro procedure map__length map_$
% NUMBER OF VERTICES IN MAP_$$
  length map_$

symbolic smacro procedure vertex_length vertex$
% NUMBER OF EDGES IN VERTEX $
  length vertex$

symbolic smacro procedure prepare_map_ map_$
 for each x in map_
  collect mk_old_edge x$

symbolic smacro procedure p_vertex_prim vertex$
% IS VERTEX PRIMITIVE ? $
  vertex_length (vertex) <= 3 $

  %************ SECTION III ************************************

symbolic smacro procedure s!-edge!-name edge$ car edge$

symbolic smacro procedure sappend(x,y)$ append(x,y)$

symbolic smacro procedure sreverse y $ reverse   y$

symbolic smacro procedure getedge(x,y)$ cdr assoc(x,y)$

symbolic smacro procedure mk!-road!-name(x,y,n)$
list(car x . n,car y . n)$

symbolic smacro procedure mk!-external!-leg edge$
%< FLAG(LIST EDGE,'EXTRNL)$
list(    edge . 0)   $

symbolic smacro procedure index!-in(ind,l)$
if atom ind then nil
else member(ind,l)$

  %************ SECTION IV ************************************

symbolic smacro procedure reverse_map_ map_$
  reverse map_$

symbolic smacro procedure mk_edge1_vertex edge$
 list edge$

symbolic smacro procedure mk_edges_vertex edges$
  edges$

symbolic smacro procedure reversip_vertex vertex$
  reversip vertex$

symbolic smacro procedure append_vertex (vertex1,vertex2)$
   append(vertex1,vertex2)$

%symbolic smacro procedure conc_vertex (vertex1,vertex2)$
% nconc(vertex1,vertex2)$

symbolic smacro procedure mk_edge4_vertex (edge1,edge2,edge3,edge4)$
  list(edge1,edge2,edge3,edge4)$

symbolic smacro procedure p_old_edge edge$
 assoc(s_edge_name edge,old_edge_list )$


symbolic smacro procedure s_atlas_map_ atlas$
 car atlas$

symbolic smacro procedure s_atlas_coeff atlas$
 cadr atlas$

symbolic smacro procedure s_atlas_den_om atlas$
 caddr atlas$

symbolic smacro procedure mk_atlas (map_,atlases,den_om)$
 list(map_,atlases,den_om)$

symbolic smacro procedure vertex_edges edge$
  edge$

symbolic smacro procedure s_coeff_world1 world1$
 cadr world1 $

symbolic smacro procedure s_edgelist_world world$
 car world$

symbolic smacro procedure s_world1 world$
 caddr world $


symbolic smacro procedure s_world_var world$
 cadr world$

symbolic smacro procedure s_world_atlas world$
  caddr world$

symbolic smacro procedure s_world_edges world$
 car world$

endmodule;

end;
