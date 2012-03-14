module cvitmap;

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


  exports calc_spur$
  imports simp!*,reval,calc_map_tar ,calc_den_tar,spaces$

% SIMP!* AND REVAL REDUCE SYSTEM GENERAL FUNCTIONS FOR
% EVALUATING ALGEBRAIC EXPRESSIONS.

%*********************************************************************
%                                                                    *
%                        FOR CVITANOVIC GAMMA MATRICES               *
%                                CALCULATIONS                        *
%                                                                    *
%                                                                    *
%                       18.03.88  10.06.90  15.06.90 31.08.90        *
%                       01.09.90  11.09.90  14.09.90                 *
%********************************************************************$
 lisp$
 % 07.06.90 all MAP was replaced by MAP_
 % 07.06.90 all DEN was replaced by DEN_
 % 07.06.90 all PROP was replaced by PROP_
 % SOME FUNCTIONS WAS MOVED TO SMACRO SECTION  08.06.90 10.06.90

%**********************************************************************
%                                                                     *
%                      _DATA_STRUCTURE                                *
%                                                                     *
%    WORLD::=(EDGELIST,VARIANTS,WORLD1)                               *
%    WORLD1::=(MAP_2,COEFF,DEN_OM)                                    *
%    MAP_2::=(MAP_S,VARIANTS,PLAN)                                    *
%    MAP_S::=(EDGEPAIR . GSTRAND)                                     *
%    MAP_1::=(EDGEPAIR . MAP_)                                        *
%    EDGEPAIR::=(OLDEDGELIST . NEWEDGELIST)                           *
%    COEFF::=LIST OF WORLDS (UNORDERED)                               *
%    ATLAS::=(MAP_,COEFF,DEN_OM)                                      *
%    MAP_::=LIST OF VERTICES (UNORDERED)                              *
%    VERTEX::=LIST OF EDGES (CYCLIC ORDER)                            *
%    VERTEX::=(NAME,PROP_ERTY,TYPE)                                   *
%    NAME::=ATOM                                                      *
%    PROP_ERTY::= (FIRSTPARENT . SECONDPARENT)                        *
%    TYPE::=T OR NIL                                                  *
%                                                                     *
%*********************************************************************$

%========================== PREDICATES =============================$


symbolic procedure is_indexp x$  % 01.09.90 RT
    (lambda z$
       z and cdr z)
     assoc(s_edge_name x,dindices!*)$

symbolic procedure mk_edge_name (name1,name2)$
% GENERATE NEW EDGE NAME $
<< n_edge := n_edge +1$
%INTERN COMPRESS APPEND(MK_NAME1 NAME1,
        compress append(mk_name1 name1,
                        append ( mk_name1 n_edge ,
                                 mk_name1 name2)) >> $

symbolic procedure new_edge (fedge,sedge)$
% GENERATE NEW EDGE $
begin
 scalar s$
 s:=
 mk_edge ( mk_edge_name ( s_edge_name fedge,
                          s_edge_name sedge),
            mk_edge_prop_ ( s_edge_name fedge,
                            s_edge_name sedge),
            mk_edge_type ( nil,
                           nil))$
 %          MK_EDGE_TYPE ( S_EDGE_TYPE FEDGE,
 %                         S_EDGE_TYPE SEDGE))$
 new_edge_list := s . new_edge_list $
 return s
 end$

symbolic procedure delete_vertex (vertex,map_)$
%DELETS VERTEX FROM MAP_$
if p_empty_map_ map_ then mk_empty_map_ ()
else
  if p_eq_vertex (vertex,s_vertex_first map_)
    then s_map__rest map_
  else
    add_vertex (s_vertex_first map_,
                 delete_vertex (vertex,s_map__rest map_))$

%====================== PREDICATES (CONTINUE) =====================$

symbolic procedure p_eq_vertex (vertex1,vertex2)$
% VERTICES ARE EQ IF THEY HAVE EQUAL NUMBER OF EDGES
% IN THE SAME ORDER WITH EQUAL _NAMES $
if p_empty_vertex vertex1 then p_empty_vertex vertex2
else
 if p_empty_vertex vertex2 then nil
 else
   if equal_edges (first_edge vertex1,
                    first_edge vertex2)
      then p_eq_vertex (s_vertex_rest vertex1,
                          s_vertex_rest vertex2)
   else nil$

%::::::::::::::::::::::: SOME ROUTINES :::::::::::::::::::::::::::::$

symbolic procedure mk_old_edge x$
 begin
  scalar s$
  s:=assoc(x,old_edge_list )$
  if s then return s$
  s:=mk_edge ( x,
                if not gamma5p x
                   then  mk_edge_prop_ (1,1)           %10.06.90 RT
                else     mk_edge_prop_ (ndim!*,ndim!*),
                mk_edge_type (t,t))$
 old_edge_list :=cons(s,old_edge_list )$
 return s
 end$

symbolic procedure change_name (name,edge)$
% CHANGES EDGE'S NAME $
mk_edge (name,
          s_edge_prop_ edge,
          s_edge_type edge )$

%======================= PREDICATES (CONTINUE) ================== $

symbolic procedure is_tadpole vertex$    %11.09.90 RT
% RETURNS T IF THERE IS ONLY ONE EXTERNAL LEG
    is_tadpolen(vertex) < 2$

symbolic procedure is_tadpolen vertex$    %11.09.90 RT
% RETURNS NUMBER OF EXTERNAL LEGS
  vertex_length diff_legs(vertex,mk_empty_vertex())$

symbolic procedure diff_legs(vertex,vertex1)$  %11.09.90 RT
% RETURNS LIST OF EXTERNAL LEGS
  if p_empty_vertex vertex then vertex1
  else  if p_member_edge(first_edge vertex,
                          s_vertex_rest vertex)
                      or
           p_member_edge(first_edge vertex,
                           vertex1)
           then diff_legs(s_vertex_rest vertex,vertex1)
         else  diff_legs(s_vertex_rest vertex,
                   add_edge(first_edge vertex,vertex1))$


symbolic procedure is_buble (vertex1,vertex2)$
% RETURNS NIL IF VERTEX1 AND VERTEX2 DOES NOT FORMED A BUBLE,
% OR N . MAP_ ,WHERE N IS A NUMBER OF EXTERNAL LINES ( 0 OR 2 ),
% MAP_ IS A MAP_ CONTAINING THIS BUBLE $
%NOT(IS_TADPOLE VERTEX1) AND NOT(IS_TADPOLE VERTEX2) AND  %14.09.90 RT
(lambda z$ if z >= 2 then nil
           else (2*z) . mk_vertex2_map_ (vertex1,vertex2))
vertex_length ( diff_vertex (vertex1,vertex2))$

%^^^^^^^^^^^^^^^^^^^^^^^ MAIN PROGRAM ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^$

symbolic procedure transform_map_ map_$
% GENERATE SIMPLE MAP_ (ONLY PRIMITIVE VERTICES) FROM INITIAL ONE$
begin
 scalar n_edge$
 n_edge := 0$
 new_edge_list :=nil$
 old_edge_list :=nil$
return
mk_simple_map_
(for each vertex in map_ collect
   prepare_map_ vertex)$
end$


%,,,,,,,,,,,,,,,,,,,,,RODIONOV & TARANOV INTERFACE ,,,,,,,,,,,,,,,$

global '(bubltr freemap_)$

symbolic procedure to_taranov map_$
% MAP_ IS INITIAL MAP_,
% RETURNS (FULL LIST OF EDGES (INITIAL AND GENERATED) .
%         (MAP_ OF PRIMITIVE VERTICES )  .
%         (LIST OF ALL POSSIBLE ENUMERATION OF MAP_'S EDGES) $
begin
  scalar new_edge_list ,old_edge_list ,full_edge_list ,
         new_map_ ,free_map_ ,marks ,variants ,alst ,bubles$
  new_map_ :=transform_map_ map_$
  free_map_ :=find_bubltr new_map_ $
  bubles:=car free_map_ $
  bubltr:=bubles $
  free_map_ := cdr free_map_ $
  freemap_:=free_map_ $
  full_edge_list := for each edge in old_edge_list collect
                          s_edge_name edge $
  alst:=nconc(for each x in full_edge_list collect (x . 1) ,
              list('!_0 . 0) ) $                   %ADD EMPTY EDGE $
  marks:=set_mark (new_edge_list ,
                   nil,
                   buble_proves bubles,
                   new_map_ ,
                   add_tadpoles (bubles,alst))$
 variants:=edge_bind (marks,alst)$
  full_edge_list :=nconc (for each edge in new_edge_list collect
                              s_edge_name edge,
                          full_edge_list )$
return full_edge_list .
       new_map_ .
       variants
end$


 % TEST TEST TEST  TEST TEST TEST TEST TEST TEST TEST $
 % TO_TARANOV '((A B C C B A)) $


%END$    %cvit2.red
%********************************************************************
%                     NOW WE MARKED THE MAP_                        *
%*******************************************************************$

                  % 09.03.88  $
lisp$

global '(ndim!* )$

%ERROERRORERRORERRORERROR ERROR ROUTINES ERRORERRORERRORERRORERROR $

global '(!*cviterror)$

flag('(cviterror),'switch)$

!*cviterror:=t$           % IF T THEN ERROR MESSAGES WILL BE PRINTED$

% The FEXPR for set_error has been re-written by JPff
%%%  symbolic fexpr procedure set_error u$
%%%  if !*cviterror then set_error0 (u,alst)
%%%  else
%%%     error(55,"ERROR IN MAP_ CREATING ROUTINES")  $
symbolic macro procedure set_error u$
   list('set_error_real,mkquote cadr u,cons('list,cddr u))$

symbolic procedure set_error_real (u,v)$
<<
   if !*cviterror then <<
     prin2 "Function: "$
     prin2 car u$
     prin2 " Arguments: "$
     if v then for each x in v do <<
        prin2 x$ prin2 " IS " $
        prin2 x$
        terpri()  >>;
    >>;
    error(55,"Error in MAP_ creating routines")
>>$

%ERERERERERERERERERERERERERERERERERERERERERERERERERERERERERERERERE$

symbolic procedure mark_edges (newedges,oldedges,map_)$
  mk_proves (map_,oldedges) .
  set_mark (newedges,nil,nil,map_,
             for each x in oldedges collect (s_edge_name x .
                                             car s_edge_prop_ x ) ) $

symbolic procedure mk_proves (map_,oldedges)$
if p_empty_map_ map_ then nil
else
  if defined_vertex (s_vertex_first map_,oldedges) then
      s_vertex_first map_ .
      mk_proves (s_map__rest map_,oldedges)
  else
      mk_proves (s_map__rest map_,oldedges)$

symbolic procedure defined_vertex (vertex,oldedges)$
if p_empty_vertex vertex then t
else
 memq_edgelist (first_edge vertex,oldedges)
                and
 defined_vertex (s_vertex_rest vertex,oldedges)$

symbolic procedure set_mark (edges,notdef,toprove,map_,blst)$
% EDGES - LIST OF NEW EDGES CREATED WHILE MAKING A MAP_,
% NOTDEF - LIST OF EDGES WHICH CANNOT BE FULLY IDEN_TIFY,
% TOPROVE - LIST OF VERTICES FOR CHECKING TRIANGLE RULE,
% MAP_ - MAP_ CREATED EARLIER,
% BLST - ALIST OF BINDED EDGES$
if null edges then
    if notdef or toprove then          % 15.06.90 RT
                 set_error_real('set_mark,
                                list(edges,notdef,toprove,map_,blst))
    else nil
else
  (lambda z$
      if z then                  %THE EDGE IS FULLY DEFINED$
         set_prove (append(notdef,            %RESTOR LIST OF EDGES$
                            cdr edges),
                     car edges,
                     append(new_prove (car edges,    %ADD CHECKS$
                                        map_),
                            toprove),
                     map_,
                     (s_edge_name car edges . 0) .
                     blst)
     else
         set_mark (cdr edges,                   %TRY NEXT$
                    car edges . notdef,         % ADD NOT DEF. LIST$
                    toprove,
                    map_,
                    blst))
    ( assoc(caadar edges,blst)          %CHECK IF BOTH PARENT IS $
             and                        %ALREADY DEFINED         $
      assoc(cdadar edges,blst) ) $

symbolic procedure new_prove (edge,map_)$
% RETURNS NEW VERTEX FOR TRIANGLE RULE CHECKING LIST$
if null map_ then nil
else
  (lambda z$  if z then list z
              else new_prove (edge,cdr map_))
   new_provev (edge,car map_) $

 symbolic procedure new_provev (edge,vertex)$
 % CAN THIS VERTEX BE UTILIZED FOR CHECKING ? $
 if not member(edge,vertex) then nil
 else
  if (assoc(caadr edge,vertex)
           and
      assoc(cdadr edge,vertex))
    then nil
  else vertex $

symbolic procedure is_son (edge,vertex)$
 assoc(car s_edge_prop_ edge,vertex)$

symbolic procedure not_parents (edge,proves)$
if null proves then nil
else
  if is_son (edge,car proves)
     then cdr proves
  else car proves . not_parents (edge,cdr proves)$

symbolic procedure set_prove (edges,edge,toprove,map_,blst)$
% RETURNS A PAIR (EDGE . (LIST FOF VERICES FOR TRIANGLE RULE TEST))$
(lambda z$
   (edge . not_parents (edge,car z)) .
    set_mark (edges,nil,cdr z,map_,blst))
  find_proved (toprove,nil,nil,blst)$

symbolic procedure find_proved (toprove,proved,unproved,blst)$
% RETURNS A PAIR ((LIST OF ALREADY DEFINED VERTICES) .
%                 (LIST OF NOT YET DEFINED EDGES) ) $
if null toprove then proved . unproved
else
  if is_proved (car toprove,blst) then
                   find_proved (cdr toprove,
                                 car toprove . proved,
                                  unproved,
                                  blst)
  else             find_proved (cdr toprove,
                                 proved,
                                 car toprove . unproved,
                                 blst) $

symbolic procedure is_proved (vertex,blst)$
 if null vertex then t
 else if assoc(caar vertex,blst) then is_proved (cdr vertex,blst)
      else nil $

%@@@@@@@@@@@@@@@@@@@@@@@ NOW GENERATES ALL POSSIBLE NUMBERS @@@@@@@@$

symbolic procedure mk_binding (provedge,blst)$
 can_be_proved (car provedge,blst)
             and
 edge_bind (cdr provedge,blst)$

symbolic procedure edge_bind (edgelist,blst)$
if null edgelist then list blst
else
 begin
   scalar defedge,prop_,p,emin,emax,s,proves,i$
% DEFEDGE - EDGE WITH DEFINED RANG,
% PROP_ - ITS PROP_ERTY: (NUM1 . NUM2),
% P - ITS NAME,
% EMIN AND EMAX - RANGE OF P,
% S - TO STORE RESULTS,
% PROVES - CHECKS OF TRIANGLE LAW$
  defedge:=car edgelist$
  proves:=cdr defedge$
  defedge:=car defedge$
  edgelist:=cdr edgelist$
  p:=s_edge_name defedge$
  prop_:=s_edge_prop_ defedge$
  emin:=assoc(car prop_,blst)$
  emax:=assoc(cdr prop_,blst)$
 if null emin or null emax
   then set_error_real ('edge_bind,list(prop_,blst))$
 prop_:=(cdr emin) . (cdr emax)$
  emin:=abs((car prop_)-(cdr prop_))$
  emax:=(car prop_)+(cdr prop_)$
 if numberp ndim!* then              %NUMERICAL DIMENSIONAL$
    << emax:=min(emax,ndim!*)$
       if emin > ndim!* then return nil >> $
 i:=emin$
loop:
       if i > emax then return s$
       if can_be_proved (proves,(p . i) . blst)
         then s:=append(edge_bind (edgelist,
                                    (p . i) . blst),
                        s) $
       i:=i+2$
 go loop
end$

symbolic procedure can_be_proved (proves,blst)$
if null proves then t
else if can_be_p (car proves,blst) then
        can_be_proved (cdr proves,blst)
     else nil$

symbolic procedure can_be_p (vertex,blst)$
%CHECKS TRIANGLE RULE$
begin
  scalar i,j,k$
  i:=assoc(car car   vertex,blst)$
  j:=assoc(car cadr  vertex,blst)$
  k:=assoc(car caddr vertex,blst)$
  if null i or null j or null k then set_error_real('can_be_proved,
                                                     list(vertex,%%edge,
                                                     blst))$
  i:=cdr i$
  j:=cdr j$
  k:=cdr k$
  if numberp ndim!* and (i+j+k) > (2*ndim!*) then
    return nil $                           %SINCE S+T+U<NDIM!* $
 % ======== NOW CHECK TRIANGLE RULE ======= $
  return
        if not evenp(i+j+k) or
           k < abs(i-j)     or
           k > (i+j)
         then nil
        else t
end$

%END$  %cvit4.red
%OOOOOOOOOOOOOOOOOOOOOOOOO ROUTINES TO SELECT BUBLES OOOOOOOOOOOOOOOO$

lisp$                                   %24.05.88$

symbolic procedure find_bubles atlas$
 find_bubles1 (atlas,old_edge_list )$

symbolic procedure find_bubles_coeff (atlaslist,edgelist,bubles)$
%F NULL BUBLES THEN NIL . ATLASLIST
%LSE
 find_bubles1_coeff (atlaslist,nil,edgelist,bubles)$

symbolic procedure find_bubles1_coeff (atlaslist,passed,edgelist,
                                                          bubles)$
if null atlaslist then bubles . passed
else
 (lambda z$             %Z - PAIR = (BUBLES . REDUCED MAP_)
    find_bubles1_coeff (cdr atlaslist,
                          cdr z . passed,
                          edgelist,
                          if null car z then bubles else
                          car z . bubles) )
  find_bubles1 (car atlaslist,edgelist) $

symbolic procedure mk_atlaslist (map_,coeff,den_om)$
 list mk_atlas (map_,coeff,den_om)$

symbolic procedure find_bubles1 (atlas,edgelist)$
  select_bubles (nil,
                 s_atlas_map_ atlas,
                 nil,
                 s_atlas_coeff atlas,
                 s_atlas_den_om atlas,
                 edgelist)$

symbolic procedure select_bubles(bubles,map_,passed,coeff,den_om,al)$
% RETURNS (LIST OF BUBLES ) . ATLAS,
% WHERE BUBLES ARE TWO OR ONE VERTICES MAP_S $
if p_empty_map_  map_ then
             (lambda x$
                     car x .
                           mk_atlas (passed,cdr x,den_om))
              find_bubles_coeff (coeff,
                                 union_edges (map__edges passed,
                                              al),
                                 bubles)
else
 if (map__length map_ + map__length passed) < 3 then
         select_bubles (bubles,
                        mk_empty_map_ (),
                        append_map_s(map_,
                                     passed),
                        coeff,
                        den_om,
                        al)
 else
  (lambda z$                          % Z IS NIL OR A PAIR
                                      % N . MAP_ ,WHERE
                                      % N - NUMBER OF FREE EDGES$
     if z then                                      %A BUBLE IS FIND$
      (lambda d$
       (lambda bool$              %BOOL=T IFF ALL EDGES CAN BE DEFINED$
      if car z = 0 then                             %NO EXTERNAL LINES$
       if bool then
        select_bubles ( z . bubles,
                         mk_empty_map_ (),
                         cdr z,
                         mk_atlaslist ( conc_map_s (passed,
                                                    delete_vertex (
                                                      s_vertex_second
                                                                cdr z,
                                                      s_map__rest
                                                                map_)),
                                         coeff,
                                         den_om),
                         nil,
                         al)
       else
        select_bubles ( z . bubles,               %ADD BUBLE$
                         delete_vertex (s_vertex_second cdr z,
                                         s_map__rest map_),
                         passed,
                         try_sub_atlas (mk_atlas (cdr z,
                                                  nil,
                                                  nil),
                                        coeff),
                         den_om,
                         al)
      else
       if not p_old_vertex d then
        if bool then
        select_bubles (z . bubles,
                        mk_empty_map_ (),
                        cdr z,
                        mk_atlaslist (conc_map_s (passed,
                                                  buble_vertex (
                                                    cdr z,
                                                    delete_vertex (
                                                     s_vertex_second
                                                        cdr z,
                                                     s_map__rest
                                                        map_ ),
                                                     al)),
                                       coeff,
                                       den_om),
                         list d,
                         al)
        else
        select_bubles ( z . bubles,               %ADD NEW BUBLE$
                        buble_vertex (cdr z,      %RENAME EDGES $
                             conc_map_s (passed,
                             delete_vertex (s_vertex_second cdr z,
                                            s_map__rest map_)),
                                        al),
                         mk_empty_map_ (),
                         try_sub_atlas (mk_atlas (cdr z,nil,list d),
                                        coeff),
                         den_om,
                         al)
       else
        if bool then
        select_bubles (z . bubles,
                        mk_empty_map_ (),
                        ren_vertmap_ (d,cdr z),
                        mk_atlaslist (
                          conc_map_s (
                            passed,
                            add_vertex (add_edge (!_0edge ,d),
                                        delete_vertex (
                                            s_vertex_second cdr z,
                                            s_map__rest map_ ))),
                          coeff,
                          den_om),
                        list ren_vertices (d,d),
                        al)
        else
        select_bubles (z . bubles,
                        add_vertex (add_edge (!_0edge ,d),
                                    delete_vertex(s_vertex_second cdr z,
                                                  s_map__rest map_)
                                    ),
                        passed,
                        try_sub_atlas (mk_atlas (ren_vertmap_
                                                    (d,cdr z),
                                                 nil,
                                                 list
                                                 ren_vertices (d,d)
                                                 ),
                                        coeff),
                        den_om,
                        al )
                                     )
   %      ALL_DEFINED (CDR Z,AL))
          t                           )
        delta_edges cdr z
     else
        select_bubles (bubles,
                       s_map__rest map_,
                       add_vertex (s_vertex_first map_,passed),
                       coeff,
                       den_om,
                       al )
                                )
   find_buble (s_vertex_first map_,
               s_map__rest map_ ) $

symbolic procedure p_old_vertex vertex$
% RETURNS T IFF ALL EDGES OF VERTEX ARE OLD OR VERTEX IS EMPTY ONE$
if p_empty_vertex vertex then t
else p_old_edge first_edge vertex
             and
     p_old_vertex s_vertex_rest vertex$

symbolic procedure renames_edges (vertex,al)$
rename_edges_par (first_edge vertex,
                    second_edge vertex,
                    al)$

symbolic procedure rename_edges_par (vertex1,vertex2,al)$
% Here VERTEX1 and VERTEX2 are edges!
if   defined_edge (vertex1,al)
     and not p_old_edge(vertex2)  then        % 14.09.90 RT
       replace_edge (vertex2,vertex1,new_edge_list )
else
  if defined_edge (vertex2,al)
     and not p_old_edge(vertex1)  then        % 14.09.90 RT
       replace_edge (vertex1,vertex2,new_edge_list )
  else
    if  p_old_edge (vertex1)
        and not p_old_edge(vertex2)  then        % 14.09.90 RT
         replace_edge (vertex2,vertex1,new_edge_list )
    else
      if p_old_edge (vertex2)
         and not p_old_edge(vertex1)  then        % 14.09.90 RT
          replace_edge (vertex1,vertex2,new_edge_list )
      else rename_edges (vertex1,vertex2)$

symbolic procedure buble_vertex (map_2,map_,al)$
if p_empty_map_ map_2 then mk_empty_map_ ()
else
  << renames_edges (delta_edges map_2,al)$
     map_ >> $

symbolic procedure delta_edges map_2$
% MAP_2 - MAP_ OF TWO VERTICES $
mk_edge2_vertex (
                   first_edge
                               diff_vertex (s_vertex_first map_2,
                                            s_vertex_second map_2),
                   first_edge
                               diff_vertex (s_vertex_second map_2,
                                            s_vertex_first map_2 )
                  )$

symbolic procedure delta_names map_2$
% MAP_2 - MAP_ OF TWO VERTICES $
(lambda z$
     s_edge_name first_edge car z .
     s_edge_name first_edge cdr z  )
 (diff_vertex (s_vertex_first map_2,
               s_vertex_second map_2) .
  diff_vertex (s_vertex_second map_2,
               s_vertex_first map_2) ) $

symbolic procedure old_rename_edges (names,map_)$
if p_empty_map_ map_ then mk_empty_map_ ()
else add_vertex (ren_edge (names,s_vertex_first map_),
                 old_rename_edges (names,
                                s_map__rest map_) ) $

symbolic procedure ren_vertmap_ (vertex1,map_)$
% VERTEX1 MUST BE TWO EDGE VERTEX,
% EDGES OF VERTEX2 TO BE RENAME$
if vertex_length vertex1 neq 2 then set_error_real ('ren_vertmap_  ,
                                                     list(vertex1,map_))
else old_rename_edges (s_edge_name first_edge vertex1 .
                       s_edge_name second_edge vertex1,
                       map_)$

symbolic procedure ren_vertices (vertex1,vertex2)$
% VERTEX1 MUST BE TWO EDGE VERTEX,
% EDGES OF VERTEX2 TO BE RENAME$
if vertex_length vertex1 neq 2
  then set_error_real ('ren_vertices,list(vertex1,vertex2))
else ren_edge (s_edge_name first_edge vertex1 .
               s_edge_name second_edge vertex1,
               vertex2)$

symbolic procedure ren_edge (names,vertex)$
% NAMES IS NAME1 . NAME2,
% CHANGE NAME1 TO NAME2$
if null assoc(car names,vertex) then vertex  %NO SUCH EDGES IN VERTEX$
else ren_edge1 (names,vertex)$

symbolic procedure ren_edge1 (names,vertex)$
if p_empty_vertex vertex then mk_empty_vertex ()
else if car names =s_edge_name first_edge vertex then
        add_edge ( change_name (cdr names,first_edge vertex),
                   ren_edge1 (names ,s_vertex_rest vertex))
     else
        add_edge ( first_edge vertex,
                   ren_edge1 (names,s_vertex_rest vertex))$

symbolic procedure find_buble (vertex,map_)$
if p_empty_map_ map_ then mk_empty_map_ ()
else
  is_buble (vertex,s_vertex_first map_)
  or
  find_buble (vertex,s_map__rest map_) $

symbolic procedure diff_vertex (vertex1,vertex2)$
if p_empty_vertex vertex1 then mk_empty_vertex ()
else
  if p_member_edge (first_edge vertex1,vertex2)
                    and
      not equal_edges (first_edge vertex1,!_0edge )
     then diff_vertex (s_vertex_rest vertex1,vertex2)
  else
    add_edge (first_edge vertex1,
              diff_vertex (s_vertex_rest vertex1,vertex2)) $

%SSSSSSSSSSSSSSSSSSSSSSSSSS NOW MAKES PROVES FROM BUBLE PPPPPPPPPPPPPP$

global '(!_0edge )$

!_0edge :=mk_edge ('!_0 ,
                   mk_edge_prop_ (0,0),
                   mk_edge_type (t,t)) $

symbolic procedure buble_proves bubles$
if null bubles then nil
else
  if caar bubles = 0                           %NO EXTERNAL LINES $
    then buble_proves cdr bubles
  else if caar bubles = 2 then
       mk_edge3_vertex (
                          first_edge diff_vertex (
                                      s_vertex_first cdar bubles,
                                      s_vertex_second cdar bubles),
                          first_edge diff_vertex (
                                      s_vertex_second cdar bubles,
                                      s_vertex_first cdar bubles),
                          !_0edge ) .
                                      buble_proves cdr bubles
        else
         if caar bubles = 3 then
               car cdar bubles .
                                 buble_proves cdr bubles
         else buble_proves cdr bubles $


symbolic procedure try_sub_atlas (atlas,atlaslist)$
if null atlaslist then list atlas
else
  if sub_map__p (s_atlas_map_ atlas,
                 s_atlas_den_om car atlaslist)
   then try_sub_atlas (mk_sub_atlas (atlas,car atlaslist),
%  THEN TRY_SUB_ATLAS (MK_SUB_ATLAS (CAR ATLASLIST,
%                                        ATLAS        ),
                         cdr atlaslist)
  else  car atlaslist .
        try_sub_atlas (atlas,cdr atlaslist)$

symbolic procedure sub_map__p (map_1,den_)$
%MAP_1 AND DEN_  HAVE COMMON VERTEX (DEN_ - DEN_OMINATOR)$
if p_empty_map_ map_1 then nil
else  sub_vertex_map_ (s_vertex_first map_1,den_)
                    or
      sub_map__p (s_map__rest map_1,den_)$

symbolic procedure sub_vertex_map_ (vertex,den_)$
if null den_ then nil
else p_common_den_ (vertex,car den_)
                  or
     sub_vertex_map_ (vertex,cdr den_)$

symbolic procedure  p_common_den_ (vertex,vertexd)$
(lambda n$
    if n = 3 then                                %TRIANGLE
             p_eq_vertex (vertex,vertexd)

    else
      if n = 2 then                              %KRONEKER
             p_member_edge (first_edge vertexd,vertex)
      else nil )
  vertex_length vertexd $

symbolic procedure mk_sub_atlas (atlas1,atlas2)$
 mk_atlas (s_atlas_map_ atlas1,
           atlas2 . s_atlas_coeff atlas1,
           s_atlas_den_om atlas1)$

symbolic procedure all_defined (map_,al)$
 all_defined_map_ (map_,
                   defined_append(map__edges map_,al))$

symbolic procedure all_defined_map_ (map_,al)$
 al1_defined_map_ (map_,mk_empty_map_ (),al)$

symbolic procedure al1_defined_map_ (map_,passed,al)$
% T IF ALL EDGES IN MAP_ CAN BE DEFINED $
if p_empty_map_ map_ then
 if p_empty_map_ passed then t
 else nil
else
 if all_defined_vertex (s_vertex_first map_,al) then
     al1_defined_map_ (conc_map_s(passed,s_map__rest map_),
                       mk_empty_map_ (),
                       append(vertex_edges s_vertex_first map_ ,al))
 else
     al1_defined_map_ (s_map__rest map_,
                       add_vertex (s_vertex_first map_,passed),
                       al)$

symbolic procedure all_defined_vertex (vertex,al)$
 al1_defined_vertex (vertex,mk_empty_vertex (),
                            mk_empty_vertex (),al)$

symbolic procedure al1_defined_vertex (vertex,passed,defined,al)$
% T IF ALL EDGES IN VERTEX CAN BE DEFINED $
if p_empty_vertex vertex then
 if p_empty_vertex passed then t
 else re_parents (passed,defined)
else
  if defined_edge (first_edge vertex,al)
                    then
     al1_defined_vertex (conc_vertex(passed,s_vertex_rest vertex),
                         mk_empty_vertex (),
                         add_edge (first_edge vertex,defined),
                         first_edge vertex . al)
  else
     al1_defined_vertex (s_vertex_rest vertex,
                         add_vertex (first_edge vertex,passed),
                         defined,
                         al)$

symbolic procedure re_parents (passed,defined)$
%TRY TO MAKE NEW PARENTS
if vertex_length passed = 1 and vertex_length defined = 2
  then make_new_parents (first_edge passed,defined)
else nil$

symbolic procedure make_new_parents (edge,vertex)$
%VERTEX CONSISTS OF TWO EDGES
 add_parents0 (edge,
               s_edge_name first_edge vertex .
               s_edge_name second_edge vertex ,
               t)$


%^.^.^.^.^.^.^.^.^.^.^.^.^.^.^..^.^.^.^.^.^.^.^.^.^.^.^.^.^.^.^.^.^.^
%          13.05.88


symbolic procedure p_def_edge edge$
 s_edge_type edge$
%P_OLD_EDGE EDGE$

symbolic procedure defined_edge (edge,al)$
  p_old_edge edge
      or
 defined_all_edge (all_edge (s_edge_name edge,new_edge_list ),
                   nil,
                   al) $

symbolic procedure all_edge (edgename,edgelist)$
if null edgelist then nil
else
  if edgename eq s_edge_name car edgelist then
                 car edgelist . all_edge (edgename,cdr edgelist)
  else all_edge (edgename,cdr edgelist)$

symbolic procedure def_edge (edge,al)$
(lambda z$
          assoc(car z,al) and assoc(cdr z,al))
 s_edge_prop_ edge$

symbolic procedure defined_all_edge (edgelist,passed,al)$
if null edgelist then nil
else
  if def_edge (car edgelist,al) then
    if p_def_edge car edgelist then t  %REPLACE WAS ALREADY DONE
    else  rep_edge_prop_ (nconc(passed,edgelist),
                          s_edge_prop_ car edgelist . list t)
  else defined_all_edge (cdr edgelist,
                         car edgelist . passed,
                         al)$

symbolic procedure rep_edge_prop_ (edgelist,prop_)$
if null edgelist then t
else << rplacd(car edgelist,prop_)$           %CHANGE EDGE PARENTS
        rep_edge_prop_ (cdr edgelist,prop_) >> $



%END$  %cvit6.red
%<><><><><><><><><><><> ROUTINES FOR SELECTING TRIANGLES <><><><><><>$
                            %24.05.88$

global '(!*cvitbtr !*cviterror)$

flag('(cvitbtr),'switch)$

!*cvitbtr:=t$           %IF T THEN BUBLES AND TRIANGLES  WILL BE
                        %     FACTORIZED

!*cviterror:=t$         %IF T THEN ERROR MESSAGES WILL BE PRINTED

symbolic procedure find_triangles atlas$
 find_triangles1 (atlas,old_edge_list)$

symbolic procedure find_triangles1 (atlas,al)$
 select_triangles (nil,
                   s_atlas_map_ atlas,
                   nil,
                   s_atlas_coeff atlas,
                   s_atlas_den_om atlas,
                   al)$

symbolic procedure find_triangl_coeff (atlaslist,edgelist,triangles)$
 find_triangle_coeff (atlaslist,nil,edgelist,triangles)$

symbolic procedure find_triangle_coeff(atlaslist,passed,edgelist,
                                         triangles)$
if null atlaslist then triangles . passed
else
 (lambda z$                 % Z - PAIR= (TRIANGLES . REDUCED MAP_)
     find_triangle_coeff (cdr atlaslist,
                          cdr z . passed,
                          edgelist,
                          if null car z then triangles
                          else car z . triangles))
  find_triangles1 (car atlaslist,edgelist)$

symbolic procedure select_triangles (triangles,map_,passed,
                                      coeff,den_om,al)$
%RETURNS A PAIR OF THE FORM ( (LIST OF TRIANGLES) . (ATL.WITHOUT TR.))$
if p_empty_map_ map_ then                       %No triangles found.
      (lambda x$
              car x .
              mk_atlas (passed,cdr x,den_om))
      find_triangl_coeff (coeff,
                          union_edges (map__edges passed,al),
                          triangles)
else
 if (map__length map_ + map__length passed) < 4 then
    select_triangles (triangles,
                      mk_empty_map_ (),
                      append_map_s (map_,passed),
                      coeff,
                      den_om,
                      al)
 else
 (lambda z$
    if z then                                    %TRIANGLE IS FOUND$
     (lambda trn$                                %TRN - NEW VERTEX $
     %IF ALL_DEFINED (CDDR Z,AL) THEN
      if t then
         select_triangles (
             z . triangles,
             mk_empty_map_ (),
             add_vertex (trn,cddr z),
             mk_atlaslist (
                  conc_map_s (
                      mk_vertex1_map_  trn,
                      conc_map_s (passed,delete_map_s (cddr z,map_))
                             ),
                  coeff,
%                 TRN . DEN_OM    ),
                  den_om    ),
%            NIL,
             list trn,
             al             )
       else
       select_triangles ( z . triangles,       %ADD NEW TRIANGLE $
 %     SELECT_TRIANGLES ( CDDR Z . TRIANGLES,  %ADD NEW TRIANGLE$
                           conc_map_s (mk_vertex1_map_
                                       trn,     %ADD NEW VERTEX$
                                       conc_map_s (passed,
                                                   delete_map_s(cddr z,
                                                                map_)
                                                  )
                                      ),
                          mk_empty_map_ (),
                          try_sub_atlas (
                              mk_atlas (add_vertex (trn,cddr z),
                                        nil,
                                        list trn),
                              coeff       ),
                          den_om,
                          al
                         )
                   )
      sk_vertextr z
    else
       select_triangles (triangles,
                         s_map__rest map_,
                         add_vertex (s_vertex_first map_,passed),
                         coeff,
                         den_om,
                         al
                         ) )
reduce_triangle
 find_triangle (s_vertex_first map_,
                s_map__rest map_) $

symbolic procedure vertex_neighbour (vertex,map_)$
%RETURNS A MAP_ OF VERTEX NEIGHBOURS $
if p_empty_vertex vertex
           or
   p_empty_map_ map_ then mk_empty_map_ ()
else
  (lambda z$             %Z - NIL OR A PAIR (EDGE . ADJACENT EDGE )$
     if z then
        add_vertex (cdr z,
                     vertex_neighbour (delete_edge (car z,vertex),
                                       delete_vertex (cdr z,map_)))
     else
        vertex_neighbour (vertex,s_map__rest map_))
   is_neighbour (vertex,
                  s_vertex_first map_)$

symbolic procedure delete_map_s (map_1,map_2)$
if p_empty_map_ map_1 then map_2
else delete_map_s (s_map__rest map_1,
                   delete_vertex (s_vertex_first map_1,map_2) ) $

symbolic procedure delete_edge (edge,vertex)$
%DELETES EDGE FROM VERTEX $
if p_empty_vertex vertex then mk_empty_vertex ()
else
  if equal_edges (edge,first_edge vertex)
   then s_vertex_rest vertex
  else
    add_edge (first_edge vertex,
               delete_edge (edge,
                            s_vertex_rest vertex ) ) $

symbolic procedure is_neighbourp (vertex1,vertex2)$
% ARE VERTEX1 AND VERTEX2 NEIGHBOURS ?
if p_empty_vertex vertex1 then nil         % NIL IF NOT NEIGHBOURS$
else
     p_member_edge (first_edge vertex1,vertex2)
                    or
     is_neighbourp (s_vertex_rest vertex1,vertex2)$

symbolic procedure is_neighbour (vertex1,vertex2)$
% ARE VERTEX1 AND VERTEX2 NEIGHBOURS ?
% IF THEY ARE THEN RETURN A PAIR: (ADJ.EDGE . VERTEX2)$
if p_empty_vertex vertex1 then nil         % NIL IF NOT NEIGHBOURS$
else
  (lambda z$
     if z then                   %FIRTS VERTEX IS ADJACENT TO VERTEX2$
          first_edge vertex1 . vertex2
     else is_neighbour (s_vertex_rest vertex1,
                         vertex2 ) )
   p_member_edge (first_edge vertex1,
                    vertex2)$

symbolic procedure find_triangle (vertex,map_)$
%FINDS TRIANGLE WICH INCLUDES THE VERTEX.
%RETURNS MAP_ OF THREE VERTICES (TRIANGLE) OR NIL $
(lambda z$               %Z - MAP_ OF VERTICES WICH ARE NEIGHBOURS
                         %  OF VERTEX OR (IF NO NEIGHBOURS) EMPTY MAP_$
   if map__length z neq 2 then nil
   else add_vertex (vertex,z) )
 is_closed vertex_neighbour (vertex,map_)$

symbolic procedure is_closed map_$
if p_empty_map_ map_   or
   p_empty_map_ s_map__rest map_ then mk_empty_map_ ()
else
    two_neighbour (s_vertex_first map_,
                    s_map__rest map_)
             or
    is_closed s_map__rest map_$

symbolic procedure two_neighbour (vertex,map_)$
% HAS VERTEX A NEIGHBOUR IN THE MAP_ ? $
if p_empty_map_ map_ then nil
else
 if is_neighbourp (vertex,s_vertex_first map_)
    then mk_vertex2_map_ (vertex,s_vertex_first map_)
 else two_neighbour (vertex,s_map__rest map_)$

symbolic procedure mk_vertextr map_$
%MAKES VERTEX FROM TRIANGLE MAP_$
if map__length map_ neq 3 then set_error_real ('mk_vertextr ,list(map_))
else
    mk_vertextr3 (map_,3)$

symbolic procedure add_edge1(edge,vertex)$ % 14.09.90 RT
 if null edge then vertex
 else add_edge(edge,vertex)$

symbolic procedure mk_vertextr3 (map_,n)$
if n <= 0 then mk_empty_map_ ()
else
  add_edge1 (take_edge (s_vertex_first map_,
                         s_map__rest map_),
             mk_vertextr3 (cycl_map_ map_,n-1)) $

symbolic procedure take_edge (vertex,map_)$
if p_empty_vertex vertex then nil    %14.09.90 RT
% SET_ERROR ('TAKE_EDGE ,VERTEX,MAP_)    % 14.09.90  RT
else
%  IF P_EMPTY_VERTEX S_VERTEX_REST VERTEX THEN FIRST_EDGE VERTEX
%  ELSE                                     % 14.09.90 RT
    if contain_edge (first_edge vertex,map_)
                 and
       not equal_edges (first_edge vertex,!_0edge )
     then take_edge (s_vertex_rest vertex,map_)
    else first_edge vertex$

symbolic procedure contain_edge (edge,map_)$
% IS THERE A VERTEX IN THE MAP_ CONTAINING THE EDGE? $
if p_empty_map_ map_ then nil
else
    p_member_edge (edge,s_vertex_first map_)
                    or
    contain_edge (edge,s_map__rest map_) $

% ,,,,,,,,,,,,,,,,,,,,,,,,,,,,SORTING AFTER FACTORIZATION ,,,,,,,,,,,$
%      19.05.88 $

symbolic procedure find_bubltr atlas$
if null !*cvitbtr then atlas else
begin
 scalar s$
 s:=errorset(list('find_bubltr0 ,mkquote atlas),
             !*cviterror,
             !*backtrace)$
 return
     if atom s then atlas
     else car s
 end$

symbolic procedure find_bubltr0 atlas$
%(LAMBDA Z$
%    IF CAR Z THEN  SORT_ATLAS CDR Z  %FACTORIZATION HAPPENED
%    ELSE CDR Z)
 sort_atlas cdr
  find_bubltr1 (atlas,old_edge_list )$

% ,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,$

symbolic procedure find_bubltr1 (atlas,al)$
%FINDS BOTH BUBLES AND TRIANGLES IN ATLAS$
begin
 scalar s,c,bubles$
 s:=find_bubles1 (atlas,al)$
 c:=car s$
 atlas:=cdr s$
 bubles:=append(c,bubles)$
loop:
 s:=find_triangles1 (atlas,al)$
 c:=car s$
 atlas:=cdr s$
 bubles:=append(c,bubles)$
 if null c then return bubles . atlas$
 s:=find_bubles1 (atlas,al)$
 c:=car s$
 atlas:=cdr s$
 bubles:=append(c,bubles)$
 if null c then return bubles . atlas$
 go loop
end$

symbolic procedure reduce_triangle triangle$
% RETURN (N . VERTEX . TRIANGLE) OR NIL,
% N - NUMBER OF EXTERNAL EDGES$
if null triangle then nil
else
  begin
    scalar extedges,vertex,n$
    %EXTEDGES - LIST OF EXTERNAL EDGES,
    % N - NUMBER OF EXTERNAL EDGES,
    %VERTEX - NEW VERTEX,MADE FROM TRIANGLE$
      vertex:=mk_vertextr triangle$
      extedges:=ext_edges vertex$
      n:=length extedges$
  return
         if n = 1 then nil            % 14.09.90    RT
         else                         % 14.09.90    RT
             n . vertex . triangle
end$

symbolic procedure sk_vertextr z$
% Z IS (N . VERTEX . TRIANGLE) $
if car z = 1 then mk_empty_vertex ()
else
 if car z = 3 then cadr z
 else set_error_real ('sk_vertextr,list z) $

symbolic procedure ext_edges vertex$
%SELECT EXTERNAL EDGES IN VERTEX $
if p_empty_vertex vertex then nil
else
  if p_member_edge (first_edge vertex,s_vertex_rest vertex)
                         or
     equal_edges (first_edge vertex,!_0edge )
   then ext_edges delete_edge (first_edge vertex,
                                 s_vertex_rest vertex)
  else first_edge vertex .
                           ext_edges s_vertex_rest vertex $

symbolic procedure ext_edges_map_ map_$
%SELECT EXTERNAL EDGES OF MAP_$
if p_empty_map_ map_ then nil
else
  ext_map__ver (ext_edges s_vertex_first map_,
                 ext_edges_map_ s_map__rest map_)$

symbolic procedure ext_map__ver (vlist,mlist)$
if null vlist then mlist
else
  if memq(car vlist,mlist) then
       ext_map__ver (cdr vlist,
                      delete(car vlist,mlist))
  else ext_map__ver (cdr vlist,car vlist . mlist)$

symbolic procedure add_tadpoles (bubles,alst)$
if null bubles then alst
else
  if caar bubles = 1 then
       add_tadpoles (cdr bubles,
                      cons(cons(car mk_vertextr cadr car bubles,
                                0),
                           alst))
   else add_tadpoles (cdr bubles,alst)$


%END$  %cvit8.red
%::::::::::::::::::::::: ATLAS SORTING ROUTINES ********************** $
% 13.06.88$

lisp$

global '(!*cvitrace)$

!*cvitrace:=nil$  %IF T THEN TRACE BACTRAKING WHILE ATLAS SORTING$

 flag('(cvitrace),'switch)$

symbolic procedure sort_atlas atlas$
%TOP LEVEL PROCEDURE
if null atlas then atlas
else
 (lambda z$
   if z then z        %ATLAS FULLY SORTED
   else set_error_real ('sort_atlas ,list atlas))
  sort_atlas1 atlas $

symbolic procedure sort_atlas1 atlas$
(lambda z$
   if z then z          %ATLAS FULLY SORTED
   else
     if !*cviterror then print_atlas_sort (atlas,nil)
     else nil )
 atlas_sort (atlas,old_edge_list )$

symbolic procedure print_atlas_sort (atlas,edgelist)$
<< print "Atlas not sorted "$
   print_atlas atlas$
   if edgelist then
      << print "Defined edges: "$
         for each edge in edgelist do print edge >> $
   nil >> $

symbolic procedure atlas_sort (atlas,edgelist)$
begin
  scalar z,newedges$
 newedges:=store_edges new_edge_list$
 z:=
 errorset(list('atlas_sort1 ,mkquote atlas,mkquote edgelist),
          !*cvitrace,
          !*backtrace)$
 return
 if atom z then        %ATLAS NOT SORTED
 << restor_edges (newedges,new_edge_list)$  %RESTORE EDGES PARENTS
    if !*cvitrace then print_atlas_sort (atlas,edgelist)
    else nil >>
 else car z
end$

symbolic procedure store_edges edgelist$
for each edge in edgelist collect
      (car edge . cdr edge)$

symbolic procedure restor_edges (edgelist,newedgelist)$
if null edgelist then
 if newedgelist
   then set_error_real ('restor_edges ,list(edgelist,newedgelist))
 else nil
else
 if null newedgelist then
           set_error_real ('restor_edges ,list(edgelist,newedgelist))
 else
  if s_edge_name car edgelist = s_edge_name car newedgelist then
      << rplacd(car newedgelist,cdar edgelist)$
         car newedgelist . restor_edges (cdr edgelist,
                                          cdr newedgelist) >>
  else
    set_error_real ('restor_edges ,list(edgelist,newedgelist))$

symbolic procedure defined_atlas (atlas,edgelist)$
(lambda edges$
    defined_edges (edges,
%                   DEFINED_APPEND(EDGES,EDGELIST)))
                    edgelist))
 atlas_edges atlas$

symbolic procedure defined_append (edges,edgelist)$
 if null edges then edgelist
 else if defined_edge (car edges,edgelist) then
          car edges . defined_append (cdr edges,edgelist)
      else defined_append (cdr edges,edgelist) $

symbolic procedure defined_edges (edges,edgelist)$
if null edges then t
else
  if defined_edge (car edges,edgelist)
              then
     defined_edges (cdr edges,car edges . edgelist)
  else definedl_edges (cdr edges,list car edges,edgelist)$

symbolic procedure definedl_edges (edges,passed,edgelist)$
if null edges then null passed
else
 if defined_edge (car edges,edgelist) then
    defined_edges (nconc(passed,cdr edges),car edges . edgelist)
 else definedl_edges (cdr edges,car edges . passed,edgelist)$

symbolic procedure atlas_sort1 (atlas,edgelist)$
if all_defined (s_atlas_map_ atlas,edgelist) then
     mk_atlas (s_atlas_map_ atlas,
                coeff_sortl( s_atlas_coeff atlas,
                              nil,
                              nconc( map__edges s_atlas_map_ atlas,
                                     edgelist)),
                s_atlas_den_om atlas)
else coeff_sort (coeff_ordn (s_atlas_coeff atlas,edgelist),
%LSE COEFF_SORT (S_ATLAS_COEFF ATLAS,
                  mk_atlaslist (s_atlas_map_ atlas,
                                 nil,
                                 s_atlas_den_om atlas),
                  edgelist)$

symbolic procedure coeff_sortl (atlaslist,passed,edgelist)$
 coeff_sortl1 (coeff_ordn (atlaslist,edgelist),passed,edgelist)$

symbolic procedure coeff_sort (atlaslist,passed,edgelist)$
if atlaslist then
  (lambda z$                   %Z - NIL OR SORDET ATLAS
      if z then                %FIRST ATLAS ALREADY DEFINED
         mk_atlas (s_atlas_map_ z,
                    coeff_sortl (append(s_atlas_coeff z,
                                        append(cdr atlaslist,passed)),
                                  nil,
                                  nconc(map__edges s_atlas_map_ z,
                                        edgelist)),
                    s_atlas_den_om z)
      else
          coeff_sort (cdr atlaslist,
                       car atlaslist . passed,
                       edgelist))
  atlas_sort (car atlaslist,edgelist)
else coeff_sort_f (passed,nil,edgelist)$

symbolic procedure coeff_sort_f (passed,farewell,edgelist)$
if null passed then
  if null farewell then nil
  else error(51,nil)
else
 if s_atlas_coeff car passed then          %NOT EMPTY COEFF
   coeff_sort (append(
                   s_atlas_coeff car passed,
                   mk_atlas (s_atlas_map_ car passed,
                              nil,
                              s_atlas_den_om car passed) .
                     append(cdr passed,farewell)),
                nil,
                edgelist)
 else coeff_sort_f (cdr passed,
                      car passed . farewell,
                      edgelist) $

%.......... 31.05.88 ::::::::::: $

symbolic procedure coeff_ordn (atlaslist,edgelist)$
for each satlas in
 coeff_ordn1 (mk_spec_atlaslist (atlaslist,edgelist),nil)
collect cdr satlas$

symbolic procedure mk_spec_atlaslist (atlaslist,edgelist)$
 for each atlas in atlaslist collect mk_spec_atlas (atlas,edgelist)$

symbolic procedure mk_spec_atlas (atlas,edgelist)$
%RETURN PAIR (PAIR1 . ATLAS)
%WHERE PAIR1 IS A PAIR - EDGES . PARENTS
%WHERE EDGES - ALL EDGES OF ATLAS
%WHERE PARENTS-THOSE PARENTS OF EDGES WICH NOT CONTAITED IN EDGELIST
(lambda edges$
(edges . diff_edges (edges_parents edges,edgelist)) . atlas)
 atlas_edges atlas$

symbolic procedure edges_parents edgelist$
if null edgelist then nil
else
  (lambda z$
     append(z ,edges_parents cdr edgelist))
   edge_new_parents car edgelist$

symbolic procedure edge_new_parents edge$
% SELECT EDGE PARENTS FROM NEW_EDGE_LIST$
if p_old_edge edge then nil else
(lambda names$
  edge_new_parent list(car names,cdr names))
 s_edge_prop_ edge$

symbolic procedure edge_new_parent namelist$
if null namelist then nil
else
 (lambda z$
    if z then z . edge_new_parent cdr namelist
    else edge_new_parent cdr namelist)
  assoc(car namelist,new_edge_list) $

symbolic procedure diff_edges (edgelist1,edgelist2)$
if null edgelist1 then nil
else
 if p_member_edge (car edgelist1,edgelist2) then
    diff_edges (cdr edgelist1,edgelist2)
 else  car edgelist1 .
       diff_edges (cdr edgelist1,edgelist2)$

symbolic procedure coeff_ordn1 (satlaslist,passed)$
if null satlaslist then passed
else
%IF NULL CAAR SATLASLIST THEN     %ATLAS HAS NO UNDEFINED
%      COEFF_ORDN1 (CDR SATLASLIST,CAR SATLASLIST . PASSED)
%ELSE
 (lambda z$                    % Z - NIL OR SATLASLIST
   if z then                   % SUBATLAS FINED AND ADDED$
        coeff_ordn1 (z,passed)
   else coeff_ordn1 (cdr satlaslist,car satlaslist . passed) )
  p_subsatlaslist (car satlaslist,cdr satlaslist,nil)$

symbolic procedure p_subsatlaslist (satlas,satlaslist,passed)$
if null satlaslist then nil
else
 if or_subsatlas(satlas,car satlaslist) then
            embed_satlases (satlas,car satlaslist) .
            nconc(passed,cdr satlaslist)
 else p_subsatlaslist (satlas,
                       cdr satlaslist,
                       car satlaslist . passed)$

symbolic procedure or_subsatlas (satlas1,satlas2)$
  p_subsatlas (satlas1,satlas2)
             or
  p_subsatlas (satlas2,satlas1) $

symbolic procedure p_subsatlas (satlas1,satlas2)$
 p_subedgelist (caar satlas1,caar satlas2)
               or
 p_inbothlists (cdar satlas1,caar satlas2) $

symbolic procedure p_inbothlists (edgelist1,edgelist2)$
if null edgelist1 then nil
else p_member_edge (car edgelist1,edgelist2)
                 or
     p_inbothlists (cdr edgelist1,edgelist2)$

symbolic procedure p_subedgelist (edgelist1,edgelist2)$
 if null edgelist1 then t
 else
   p_member_edge (car edgelist1,edgelist2)
              and
   p_subedgelist (cdr edgelist1,edgelist2)$

symbolic procedure embed_satlases (satlas1,satlas2)$
 if p_subsatlas (satlas1,satlas2) then embed_satlas (satlas1,satlas2)
 else
 if p_subsatlas (satlas2,satlas1) then embed_satlas (satlas2,satlas1)
 else set_error_real ('embed_satlases,list(satlas1,satlas2)) $

symbolic procedure embed_satlas (satlas1,satlas2)$
 car satlas2 . embed_atlas (cdr satlas1,cdr satlas2)$

symbolic procedure embed_atlas (atlas1,atlas2)$
%EMBED ATLAS1 INTO ATLAS2
 mk_atlas (s_atlas_map_ atlas2,
            atlas1 . s_atlas_coeff atlas2,
            s_atlas_den_om atlas2)$

symbolic procedure coeff_sortl1 (atlaslist,passed,edgelist)$
if null atlaslist then
 if null passed then nil
 else list coeff_sort_f (passed,nil,edgelist)
else
(lambda z$
 if z then      %ATLAS SORTED
      z . coeff_sortl1 (cdr atlaslist,passed,edgelist)
 else coeff_sortl1 (cdr atlaslist,car atlaslist . passed,edgelist))
 atlas_sort (car atlaslist,edgelist)$

% ,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,$


%END$  %cvit82.red
%:*:*:*:*:*:*:*:*:*:*:*:* FACTORIZATION OF MAP_S :*:*:*:*:*:*:*:*:*:$
% 19.05.88 $
lisp$

symbolic procedure renamel_edges edges$
if not equal_edges (car edges,cadr edges) then
  rename_edges (car edges ,cadr edges)$

symbolic procedure map__vertex_first map_$
  mk_vertex1_map_
                  s_vertex_first map_$

symbolic procedure  both_empty_map_s (map_1,map_2)$
 p_empty_map_ map_1
      and
 p_empty_map_ map_2 $

symbolic procedure has_parents edge$
 (lambda z$
           car z neq '!? and
           cdr z neq '!?    )
  s_edge_prop_ edge $

symbolic procedure less_edge (edge1,edge2,edgelist)$
% EDGE1 < EDGE2 IFF EDGE1 WAS CREATED EARLIER$
 less_edge_name (s_edge_name edge1,
                 s_edge_name edge2,
                 edgelist)$

symbolic procedure less_edge_name (name1,name2,edgelist)$
if null edgelist then set_error_real ('less_edge_name ,
                                      list(name1,name2,edgelist))
else
  if name1 eq s_edge_name car edgelist then nil
  else
    if name2 eq s_edge_name car edgelist then t
    else less_edge_name (name1,name2,cdr edgelist)$

symbolic procedure rename_edges (edge1,edge2)$
if p_old_edge edge1 then
 %IF P_OLD_EDGE EDGE2 THEN OLD_EDGE_LIST
  if p_old_edge edge2 then replace_old_edge (edge1,edge2)
  else replace_edge (edge2,edge1,new_edge_list )
else
  if p_old_edge edge2 then replace_edge (edge1,edge2,
                                         new_edge_list )
  else
    if has_parents edge1 then
      if has_parents edge2 then replace_new_edge (edge1,edge2)
      else replace_edge (edge2,edge1,new_edge_list )
    else
      if has_parents edge2 then
           replace_edge (edge1,edge2,new_edge_list )
      else replace_new_edge (edge1,edge2)$

symbolic procedure replace_new_edge (edge1,edge2)$
 replace_o_edge (edge1,edge2,new_edge_list )$

symbolic procedure replace_old_edge (edge1,edge2)$
% 31.08.90 RT
if is_indexp edge1 then
 if is_indexp edge2 then
     replace_o_edge (edge1,edge2,old_edge_list )
 else  replace_edge (edge1,edge2,old_edge_list)
else
  if is_indexp edge2 then
       replace_edge (edge2,edge1,old_edge_list)
  else
      replace_o_edge (edge1,edge2,old_edge_list )$

symbolic procedure replace_o_edge (edge1,edge2,edgelist)$
if less_edge (edge1,edge2,edgelist) then
              replace_edge (edge2,edge1,edgelist)
else          replace_edge (edge1,edge2,edgelist)$

symbolic procedure copy_edge edge$
 car edge . cadr edge . caddr edge . nil $


symbolic procedure replace_edge2 (oldedge,newedge)$
<< rplaca(oldedge,car newedge)$
   rplacd(oldedge,cdr newedge) >> $

symbolic procedure replace_edge (oldedge,newedge,edgelist)$
 replace1_edge (copy_edge oldedge,newedge,edgelist)$

symbolic procedure replace1_edge (oldedge,newedge,edgelist)$
if null edgelist then nil
else
  <<  if equal_edges (oldedge,car edgelist) then
                         replace_edge2 (car edgelist,newedge)$
      replace1_parents (oldedge,newedge,car edgelist)$
      replace1_edge (oldedge,newedge,cdr edgelist) >> $

symbolic procedure replace1_parents (oldedge,newedge,edge)$
 replace2_parents (s_edge_name oldedge,
                   s_edge_name newedge,
                   s_edge_prop_ edge)$

symbolic procedure replace2_parents (oldname,newname,edgeprop_)$
 << if oldname = car edgeprop_ then rplaca(edgeprop_,newname)$
    if oldname = cdr edgeprop_ then rplacd(edgeprop_,newname) >> $

symbolic procedure mk_simple_map_ inmap_$
  mk_simple_map_1 (inmap_,mk_empty_map_ (),nil,nil)$

symbolic procedure both_old edges$
 p_old_edge  car edges
        and
 p_old_edge cadr edges$

symbolic procedure both_vectors edges$   % 31.08.90 RT
 not is_indexp car edges
        and
 not is_indexp cadr edges$

symbolic procedure old_renamel_edv (vertex,edges)$
% RENAMES EDGES IN VERTEX$
 ren_edge (s_edge_name car edges .
           s_edge_name cadr edges,vertex)$

symbolic procedure mk1_simple_map_ map_d$
%MAP_D IS A PAIR (MAP_.DEN_OM)$
mk_simple_map_1 (car map_d,mk_empty_map_ (),list cdr map_d,nil)$

symbolic procedure mk_simple_map_1 (inmap_,outmap_,den_om,coeff)$
if p_empty_map_ inmap_ then <<
 %        FIND_BUBLTR
        outmap_ := mk_parents_map_ outmap_;
        mk_atlas  (outmap_ ,
                   if null coeff then nil
                    else for each map_ in coeff
                             collect mk1_simple_map_ map_,
                   den_om) >>
else
(lambda edges$
 (lambda n$
  if p_vertex_prim s_vertex_first inmap_ then
   if n=2 then                 % VERTEX=(A,B)=DELTA(A,B)  $
    if both_old edges and both_vectors edges then   % 31.08.90
          mk_simple_map_1 (s_map__rest inmap_,
                           add_vertex (s_vertex_first inmap_,outmap_),
                           den_om,
                           coeff)
    else
     << renamel_edges edges$
        if both_empty_map_s (s_map__rest inmap_,outmap_) then
          mk_simple_map_1 (s_map__rest inmap_,
                           add_vertex (s_vertex_first inmap_,outmap_),
                           den_om,
                           coeff)
        else
           mk_simple_map_1 (s_map__rest inmap_,
                            outmap_,
                            den_om,
                            coeff )
     >>
   else
      mk_simple_map_1 ( s_map__rest inmap_,
                        add_vertex ( s_vertex_first inmap_,outmap_),
                        den_om,
                        coeff)
  else
   if n=2  then
    if both_old edges  and both_vectors edges then  %11.09.90 RT
        mk_simple_map_1 (add_vertex (mk_edges_vertex edges,
                                     s_map__rest inmap_),
                          outmap_,
                          den_om,
                          (mk_vertex1_map_ (
                           old_renamel_edv(s_vertex_first inmap_,edges))
                         . old_renamel_edv(mk_edges_vertex edges,edges))
                         . coeff  )
    else
     << renamel_edges edges$
        mk_simple_map_1 (s_map__rest inmap_,
                         outmap_,
                         den_om,
                         (map__vertex_first inmap_ . edges) . coeff)
      >>
   else
   if n=3  and
     ((map__length (inmap_) + map__length (outmap_)) > 2 )
    then
     (lambda v$
     mk_simple_map_1 (add_vertex (v,s_map__rest inmap_),
                      outmap_,
                      den_om,
                      (add_vertex (v,map__vertex_first inmap_) . v)
                        . coeff))
      mk_edges_vertex edges
   else
    if
      (lambda k$
           k > 4 and
           n < k )              %NOT ALL LINES EXTERNAL $
           vertex_length s_vertex_first inmap_
    then
    (lambda firz$
    mk_simple_map_1 (append_map_s (firz,s_map__rest inmap_),
                     outmap_,
                     den_om,
                     coeff) )
     (mk_firz_op s_vertex_first inmap_)        %26.04.88
  else if t then
    mk_simple_map_1 (s_map__rest inmap_,
                     add_vertex (s_vertex_first inmap_,outmap_),
                     den_om,
                     coeff)
    else
    mk_simple_map_1 (append_map_s (mk_simple_vertex
                                   s_vertex_first inmap_,
                                   s_map__rest inmap_),
                      outmap_,
                      den_om,
                      coeff)  )
  length edges)
(ext_edges s_vertex_first inmap_) $


% ?^?^?^?^?^?^?^?^?^?^?^?^? FIERZ OPTIMIZATION ?^?^?^?^?^?^?^?^?^?^?^?$
%       13.05.88$


global '(!*cvitop)$

flag('(cvitop),'switch)$

symbolic procedure mk_firz_op vertex$
if null !*cvitop then mk_firz vertex
else firz_op vertex$

symbolic procedure firz_op vertex$
mk_firz find_cycle (optimal_edge vertex,
                    vertex,
                    mk_empty_vertex ())$

symbolic procedure find_cycle (edge,vertex,passed)$
if equal_edges (edge,first_edge vertex) then
   append_vertex (vertex,reversip_vertex passed)
else find_cycle (edge,
                 s_vertex_rest vertex,
                 add_edge (first_edge vertex,passed))$

symbolic procedure optimal_edge vertex$
optimal1_edge
 internal_edges (vertex,mk_empty_vertex ())$

symbolic procedure internal_edges (vertex1,vertex2)$
if p_empty_vertex vertex1 then vertex2
else
  if p_member_edge (first_edge vertex1,s_vertex_rest vertex1)
            or
     p_member_edge (first_edge vertex1,vertex2)
   then internal_edges (s_vertex_rest vertex1,
                        add_edge (first_edge vertex1,vertex2))
  else  internal_edges (s_vertex_rest vertex1,vertex2)$

symbolic procedure optimal1_edge vertex$
% VERTEX CONTAINS ONLY PAIRED EDGES
(lambda (l,z)$
   opt_edge (z,
             edge_distance (z,vertex,l),
             s_vertex_rest vertex,
             add_edge (z,mk_empty_vertex ()),
             l))
(vertex_length vertex,
 first_edge vertex)$

symbolic procedure edge_distance (edge,vertex,l)$
% L - FULL VERTEX LENGTH
(lambda n$
     min(n,l - n - 2))
 edge_dist (edge,s_vertex_rest vertex)$

symbolic procedure edge_dist (edge,vertex)$
if equal_edges (edge,first_edge vertex) then 0
else add1 edge_dist (edge,s_vertex_rest vertex)$

symbolic procedure opt_edge (edge,distance,vertex,passed,n)$
% N - FULL VERTEX LENGTH
if distance = 0 or p_empty_vertex vertex then edge
else
(lambda firstedge$
   if p_member_edge (firstedge,passed) then
              opt_edge (edge,
                        distance,
                        s_vertex_rest vertex,
                        passed,
                        n)
   else
 (lambda dist$
    if dist < distance then
              opt_edge (firstedge,
                        dist,
                        s_vertex_rest vertex,
                        add_edge (firstedge,passed),
                        n)
     else     opt_edge (edge,
                        distance,
                        s_vertex_rest vertex,
                        add_edge (firstedge,passed),
                        n))
  edge_distance (firstedge,vertex,n))
first_edge vertex $



%<?><?><?><?><?><?><?> END OF OPTIMIZATION PART <?><?><?><?><?><?> $

symbolic procedure mk_firz vertex$
% VERTEX=(A1,...,AM,Z,B1,...,BN,Z,C1,...,CK)
% RETURNS UNION MAP_ WHERE
% MAP_ =MAP_1 & MAP_2  WHERE
% MAP_1=((B1,...,BN,X)(Y,C1,...,CK,A1,...,AM)),
% MAP_2=((Z,X,Z,Y)) $
   mk_firz1 (vertex,mk_empty_vertex ())$

symbolic procedure mk_firz1 (vertex1,vertex2)$
if p_empty_vertex vertex1 then reversip_vertex vertex2
else
  (lambda z$
      if z then                   %FIRST EDGE CONTAINS TWICE$
         mk_firz2 (first_edge vertex1,
                   car z,
                   append_vertex (cdr z,reversip_vertex vertex2))
      else
         mk_firz1 (s_vertex_rest vertex1,
                   add_edge (first_edge vertex1,vertex2) ) )
   mp_member_edge (first_edge vertex1,
                     s_vertex_rest vertex1)$

symbolic procedure mk_firz2 (edge,vertex1,vertex2)$
%RETURNS MAP_ =MAP_1 & MAP_2 ,
%VERTEX1=(B1,...,BN),
%VERTEX2=(C1,...,CK,A1,...,AM)  $
(lambda (nedge,nedg1)$
 append_map_s (
  mk_coeff2 (edge,nedge,nedg1),
  mk_vertex2_map_ (conc_vertex (vertex1,mk_edge1_vertex nedge),
                   add_edge (nedg1,vertex2))
               ))
(mk_nedge (),
 mk_nedge ()) $

symbolic procedure mk_coeff2 (edge,nedge,nedg1)$
 mk_vertex1_map_ mk_edge4_vertex (edge,nedge,edge,nedg1)$

symbolic procedure mk_nedge $
(lambda edge$
   new_edge (edge,edge))
 mk_edge ('!?,'!? . '!?,nil) $

symbolic procedure mp_member_edge (edge,vertex)$
% RETURNS NIL OR PAIR.
% IF VERTEX=(A1,...,AM,EDGE,...,B1,...,BN) THEN
% PAIR= (A1,...,AM) . (B1,...,BM) $
  mp_member1_edge (edge,vertex,mk_empty_vertex ())$

symbolic procedure mp_member1_edge (edge,vertex,tail)$
if p_empty_vertex vertex then nil
else
 if
  equal_edges (edge,first_edge vertex) then
           reversip_vertex tail .
           s_vertex_rest vertex
 else mp_member1_edge (edge,
                       s_vertex_rest vertex,
                       add_edge (first_edge vertex,tail) ) $


%END$  %cvit10.red
% ()()()()()()()()()()()()()() PRINTING ATLAS AND MAP_ ROUTINES ()()().
lisp$             %30.01.87$

fluid '(ntab!*)$

symbolic procedure print_atlas atlas$
begin
  scalar ntab!*$
  ntab!*:=0$
  prin2_atlas atlas$
end$

symbolic procedure prin2_atlas atlas$
if null atlas then nil
else
    << print_map_ s_atlas_map_ atlas$
       print_den_om s_atlas_den_om atlas$
       print_coeff s_atlas_coeff atlas
     >> $

symbolic procedure print_map_ map_$
 << pttab ntab!*$
    prin2 "Map_ is: ("$
    prin2_map_ map_$
    prin2 " )"$
    terpri()  >> $

symbolic procedure prin2_map_ map_$
if p_empty_map_ map_ then nil
else
    << print_vertex s_vertex_first map_$
       prin2_map_ s_map__rest map_ >> $

symbolic procedure print_vertex vertex$
 <<
    prin2 "( "$
    prin2_vertex vertex$
    prin2 ")"      >> $

symbolic procedure prin2_vertex vertex$
if p_empty_vertex vertex then nil
else
    << print_edge first_edge vertex$
       prin2_vertex s_vertex_rest vertex >> $

symbolic procedure print_edge edge$
 << prin2_edge edge$
    prin2 " "    >> $

symbolic procedure prin2_edge edge$
 prin2 s_edge_name edge $

symbolic procedure pttab n$
  << spaces n $   % TTAB N$         % 07.06.90
     prin2 n$
     prin2 ":" >> $

symbolic procedure print_coeff coeff$
 << ntab!*:=ntab!*+1$
    prin2_coeff coeff$
    ntab!*:=ntab!*-1    >> $

symbolic procedure prin2_coeff atlases$
if null atlases then nil
else << prin2_atlas car atlases$
        prin2_coeff cdr atlases >> $

symbolic procedure print_den_om den_list$
 << pttab ntab!*$
    prin2 "DEN_OM is: "$
    if null den_list then prin2 nil
    else prin2_map_ den_list $
    terpri() >> $

unfluid '(ntab!*)$

symbolic procedure print_old_edges ()$
   print_edge_list old_edge_list $

symbolic procedure print_new_edges ()$
   print_edge_list new_edge_list $

symbolic procedure print_edge_list edgelist$
if null edgelist then nil
else << print car edgelist$
        print_edge_list cdr edgelist >> $


%END$  %cvit12.red
%---------------------- MAKES PARENTS AFTER FIERZING ----------------$

          %24.05.88$
 lisp$


symbolic procedure mk_simpl_map_ map_$
  mk_simpl_map_1 (map_,mk_empty_map_ ())$

symbolic procedure mk_simpl_map_1 (inmap_,outmap_)$
if p_empty_map_ inmap_ then resto_map__order outmap_
else
  if p_vertex_prim s_vertex_first inmap_ then
      mk_simpl_map_1 ( s_map__rest inmap_,
                       add_vertex(mk_parents_prim s_vertex_first inmap_,
                                   outmap_))
  else
    mk_simpl_map_1 (append_map_s(mk_simple_vertex s_vertex_first inmap_,
                                  s_map__rest inmap_),
                    outmap_)$

symbolic procedure mk_simple_vertex vertex$
 % VERTEX => MAP_ $
begin
 scalar nedge,fedge,sedge$
 fedge:=first_edge vertex$
 sedge:=second_edge vertex$
 if not has_parents fedge or not has_parents sedge then
    return mk_simple_vertex cycl_vertex vertex$
 nedge:=new_edge (fedge,sedge)$
 vertex:=s_vertex_rest
         s_vertex_rest vertex$
 return
     mk_vertex2_map_ ( mk_edge3_vertex (nedge,fedge,sedge),
                       add_edge (nedge,vertex))
end$

symbolic procedure mk_parents_map_ map_$
%MAKES PARENTS FOR ALL EDGES IN MAP_.
%THIS CAN BE DONE BECAUSE NEW EDGES NEVER CREATE CYCLES$
 standard_map_
 mk_simpl_map_
 mk_parents1_map_ (map_,mk_empty_map_ (),mk_empty_map_ ())$

symbolic procedure standard_map_ map_$
if p_empty_map_ map_ then mk_empty_map_ ()
else
 if vertex_length s_vertex_first map_ > 2 then
    add_vertex (s_vertex_first map_,
                standard_map_ s_map__rest map_)
 else standard_map_ add_vertex (add_0_edge s_vertex_first map_,
                                s_map__rest map_)$

symbolic procedure add_0_edge vertex$
%ADDS SPECIAL VERTEX$
add_edge (!_0edge ,vertex)$

symbolic procedure mk_parents1_map_ (inmap_,outmap_,passed)$
if p_empty_map_ inmap_ then
 if p_empty_map_ passed then outmap_     %ALL EDGES HAVE PARENTS$
 else mk_parents1_map_ (passed,outmap_,mk_empty_map_ ())
else
 (lambda edges$
   if null edges then        %IN FIRST VERTEX ALL EDGES HAVE PARENTS$
      mk_parents1_map_ (s_map__rest inmap_,
                        add_vertex (s_vertex_first inmap_,outmap_),
                        passed)
   else
    if single_no_parents edges then   %ONLY ONE EDGE IN THE VERTEX$
                                        %HAS NO PARENTS$
       mk_parents1_map_ (s_map__rest inmap_,
                         append_map_s (mk_parents_vertex
                                          s_vertex_first inmap_,
                                       outmap_),
                         passed)
    else
       mk_parents1_map_ (s_map__rest inmap_,
                         outmap_,
                         add_vertex (s_vertex_first inmap_,passed)))
  s_noparents s_vertex_first inmap_ $

symbolic procedure s_noparents vertex$
%SELECTS EDGES WITHOUT PARENTS IN VERTEX$
if p_empty_vertex vertex then nil
else
 if has_parents first_edge vertex then
         s_noparents s_vertex_rest vertex
 else
     first_edge vertex .
     s_noparents s_vertex_rest vertex$

symbolic procedure mk_parents_vertex vertex$
%MAKES PARENTS FOR THE SINGLE EDGE WITHOUT PARENTS IN VERTEX,
% (VERTEX HAS ONLY ONE EDGE WITHOUT PARENTS ^)       $
 mk_simpl_map_ mk_vertex1_map_ vertex$

symbolic procedure mk_parents_prim pvertex$
% CREATES PARENTS FOR THE ONLY EDGE WITHOUT PARENTS IN PRIMITIVE
% (THREE EDGES) VERTEX $
if vertex_length pvertex neq 3 then pvertex
else
  (lambda edges$
     if null edges then pvertex
     else
        << mk_edge_parents (pvertex,car edges)$
           pvertex >> )
   s_noparents pvertex$

symbolic procedure mk_edge_parents (vertex,edge)$
 mk_edge1_parents (delete_edge (edge,vertex),edge)$

symbolic procedure mk_edge1_parents (vertex2,edge)$
 add_parents (edge,
              mk_edge_prop_ (
                             s_edge_name first_edge vertex2,
                             s_edge_name second_edge vertex2))$

symbolic procedure add_parents (edge,names)$
  add_parents0(edge,names,nil)$

symbolic procedure add_parents0 (edge,names,bool)$
 addl_parents (new_edge_list,edge,names . list bool)$

symbolic procedure addl_parents (edgelist,edge,names)$
% NAMES IS A PAIR NAME1 . NAME2 $
if null edgelist then nil
else
(if equal_edges (car edgelist,edge) then
    rep_parents (car edgelist,names)
 else car edgelist) .
                      addl_parents (cdr edgelist,edge,names) $

symbolic procedure rep_parents (edge,names)$
 << rplacd(edge,names)$
    edge >> $

%END$  %cvit14.red
%EEEEEEEEEEEEEEEEEEEEEEEEE SELECT ALL EDGES %%%%%%%%%%%%%%%%%%%%%%%%% $

                       % 07.06.88$

lisp$

symbolic procedure atlas_edges atlas$
union_edges (
 union_edges (map__edges s_atlas_map_ atlas,
              den__edges s_atlas_den_om atlas),
 coeff_edges s_atlas_coeff atlas)$

symbolic procedure den__edges den_om$
 map__edges den_om$

symbolic procedure coeff_edges atlaslist$
 if null atlaslist then nil
 else union_edges (atlas_edges car atlaslist,
                   coeff_edges cdr atlaslist) $

symbolic procedure map__edges map_$
 if p_empty_map_ map_ then nil
 else union_edges (vertex_edges s_vertex_first map_,
                   map__edges s_map__rest map_)$

symbolic procedure union_edges (newlist,oldlist)$
if null newlist then oldlist
else union_edges (cdr newlist,
                  union_edge (car newlist,oldlist))$

symbolic procedure union_edge (edge,edgelist)$
 if memq_edgelist (edge,edgelist) then edgelist
 else edge . edgelist$

symbolic procedure memq_edgelist (edge,edgelist)$
 assoc(s_edge_name edge,
       edgelist)$

symbolic procedure exclude_edges (edgelist,exclude)$
% EXCLUDE IS A LIST OF EDGES TO BE EXCLUDED FROM EDGELIST$
 if null edgelist then nil
 else
   if memq_edgelist (car edgelist,exclude) then
           exclude_edges (cdr edgelist,exclude)
   else car edgelist .
           exclude_edges (cdr edgelist,exclude)  $


symbolic procedure constr_worlds (atlas,edgelist)$
(lambda edges$
   actual_edges_world (
      mk_world1 (actual_edges_map_ (edges,
                                    edgelist,
                                    s_atlas_map_ atlas),
                  constr_coeff (s_atlas_coeff atlas,
                                union_edges (edges,edgelist)),
                  s_atlas_den_om atlas
                  )
               ) )
union_edges(
  den__edges s_atlas_den_om atlas,
  map__edges s_atlas_map_ atlas)$

symbolic procedure constr_coeff (atlases,edgelist)$
 if null atlases then nil
 else
      constr_worlds (car atlases,edgelist) .
      constr_coeff (cdr atlases,edgelist)$

symbolic procedure actual_edges_map_ (edges,edgelist,map_)$
 actedge_map_ (edges,edgelist,list_of_parents(edges,edgelist),nil)
%ACTEDGE_MAP_ (EDGES,EDGELIST,NIL,NIL)
  . map_$

symbolic procedure list_of_parents (edges,edgelist)$
if null edges then nil
else append(list_of_parent (car edges,edgelist),
            list_of_parents (cdr edges,edgelist))$

symbolic procedure list_of_parent (edge,edgelist)$
if p_old_edge edge or memq_edgelist (edge,edgelist) then nil
               %IF EDGE IS DEF. THEN NO NEED IN ITS PARENTS
else
begin$
 scalar pr1,pr2,p,s$
 p:=s_edge_prop_ edge$
 pr1:=assoc(car p,edgelist)$
 if pr1 then s:=pr1 . s$
 pr2:=assoc(cdr p,edgelist)$
 if pr2 then s:=pr2 . s$
%IF NULL PR1 OR NULL PR2 THEN
% SET_ERROR (LIST_OF_PARENTS ,EDGE,EDGELIST)$
 return  s
end$

symbolic procedure actedge_map_ (edges,edgelist,old,new)$
if null edges then old . new
else
  if memq_edgelist (car edges,edgelist) then
       actedge_map_ (cdr edges,edgelist,car edges . old,new)
  else actedge_map_ (cdr edges,edgelist,old,car edges . new) $

symbolic procedure actual_edges_world world1$
 mk_world (actual_world (s_actual_world1 world1,
                         s_actual_coeff s_coeff_world1 world1),
            world1)$

symbolic procedure mk_world1 (edges!-map_,coeff,den_om)$
 mk_atlas (map_2_from_map_1 edges!-map_,coeff,den_om)$

symbolic procedure map_2_from_map_1 map_1$
 list(map_1_to_strand1 map_1,
      list nil,
      mark_edges (cdar map_1,
                % UNION_EDGES(OLD_EDGE_LIST,CAAR MAP_1),
                  caar map_1,
                  cdr map_1))$

symbolic procedure map_1_to_strand1 map_1$
car map_1 .
pre!-calc!-map_ (cdr map_1,
                 names_edgepair map__edges cdr map_1)$

symbolic procedure names_edgepair edgepair$
%NCONC(FOR EACH EDGE IN CAR EDGEPAIR COLLECT S_EDGE_NAME EDGE,
%      FOR EACH EDGE IN CDR EDGEPAIR COLLECT S_EDGE_NAME EDGE)$
       for each edge in edgepair collect s_edge_name edge $

symbolic procedure s_actual_world1 world1$
%RETURNS PAIR: OLDEDGES . NEWEDGES $
caar s_atlas_map_ world1$

symbolic procedure actual_world (map_edges,coeffedges)$
%MAP_EDGES IS A PAIR OLD . NEW,
%COEFFEDGES IS LIST OF ACTUAL EDGES OF COEEF.$
 union_edges (car map_edges,
              exclude_edges (coeffedges,cdr map_edges)) $

symbolic procedure s_actual_coeff worldlist$
if null worldlist then nil
else union_edges (s_edgelist_world car worldlist,
                  s_actual_coeff cdr worldlist) $

symbolic procedure world_from_atlas atlas$
%TOP LEVEL PROCEDURE$
 constr_worlds (atlas,old_edge_list )$


%END$  %cvit16.red
%^^^^^^^^^^^^^^^^^^^^^^^^^^ CALCULATION OF WORLDS ^^^^^^^^^^^^^^^^^^^ $
           %26.03.88$
lisp$

symbolic procedure s_world_names world$
for each edge in s_world_edges world
  collect s_edge_name edge$

symbolic procedure calc_world (world,alst)$
% ALST LIST OF VALUES OF EXTERNAL EDGES: (... (EDGNAME . NUMBER) ...)$
begin
  scalar s,v$
  alst:=actual_alst (alst,                   %SELECT ONLY THOSE
                     s_world_names world)$    %EDGES WICH ARE IN WORLD
  v:=s_world_var world $                      %SELECT DATA BASE
  s:=assoc(alst,cdr v)$                       %CALC. PREVIOSLY?
  if s then return cdr s$                     %PREV. RESULT$
  s:=reval
     calc_atlas (s_world_atlas world,alst)$   %REAL CALCULATION
  nconc (v,list(alst . s))$                   %MODIFY DATA BASE
  return s
end$

symbolic procedure actual_alst (alst,namelist)$
if null alst then nil
else
  if memq(caar alst,namelist) then
     car alst . actual_alst (cdr alst,namelist)
  else actual_alst (cdr alst,namelist)$

symbolic procedure calc_atlas (atlas,alst)$
        calc_map_2d (s_atlas_map_ atlas,
                     s_atlas_den_om atlas,
                     s_atlas_coeff atlas,
                     alst) $

symbolic procedure calc_coeff (worldlist,alst)$
if null worldlist then list 1
else
 (lambda x$
      if x=0 then list 0
      else x . calc_coeff (cdr worldlist,alst))
  calc_world (car worldlist,alst)$

symbolic procedure calc_map_2d (map_2,den_om,coeff,alst)$
coeff_calc (mk_names_map_2 caar map_2 .
                          cdar map_2 .
                          cadr map_2 .
                          den_om     ,
            coeff,
            mk_binding (caddr map_2,alst)) $

symbolic procedure mk_names_map_2 edgespair$
% EDGESPAIR IS PAIR OF LISTS OF EDGES
%   EDGELISTOLD . EDGELISTNEW         $
for each edge in append(car edgespair,cdr edgespair)
    collect s_edge_name edge$

symbolic procedure calc_coeffmap_ (s,coeff,alst)$
(lambda z$
    if z = 0 then 0
    else 'times . (z . calc_coeff (coeff,alst)))
 calc_map_ (s,alst)$

symbolic procedure calc_map_ (mvd,alst)$
begin
  scalar map_,v,names,s,den_om,al,d$
  names:=car mvd$                               %NAMES OF ALL EDGES
  map_:=cadr mvd$                               %SELECT MAP_
  v:=caddr mvd$                                 %SELECT DATA BASE
  den_om:=cdddr mvd$                            %SELECT DEN_OMINATOR
  al:=actual_alst (alst,names)$                 %ACTUAL ALIST
  if null al and names then return 0$           %NO VARIANTS OF
                                                %COLOURING
  s:=assoc(al,cdr v)$                           %PREV.CALCULATED?
  if s then  s:=cdr s                           %YES, TAKE IT
  else <<                                       %ELSE
       s:=reval calc_map_tar (map_,al)$         %REAL CALCULATION
       nconc(v,list(al . s))                    %MODIFY DATA BASE
       >>  $
  d:=calc_den_tar (den_om,alst)$                %CALC. DEN_OMINATOR
  return
        if d = 1 then s
        else list('quotient,s,d)                % 09.06.90 RT
end$

%SYMBOLIC PROCEDURE CALC_MAP_TAR (MAP_,BINDING)$
%1$

%SYMBOLIC PROCEDURE CALC_DEN_TAR (DEN_OMINATOR,BINDING)$
%1$

symbolic procedure coeff_calc (s,coeff,binding)$
%S IS EDGENAMES . MAP_ . DATABASE . DEN_OMINATOR $
 reval
     ('plus . coeff1_calc (s,coeff,binding))$

symbolic procedure coeff1_calc (s,coeff,binding)$
if null binding then list 0
else calc_coeffmap_ (s,coeff,car binding) .
     coeff1_calc (s,coeff,cdr binding) $

%TOPTOPTOPTOPTOPTOPTOPTOPTOPTOPTOPTOPTOPTOPTOPTOPTOPTOPTOPTOPTOPTOPTOP$

symbolic procedure calc_spur0 u$
begin
 scalar s$
 if null u then return u$
 s:=transform_map_ u$
 old_edge_list := !_0edge . old_edge_list $
 s:=find_bubltr s$
 return
        calc_world (world_from_atlas s,
                     for each edge in old_edge_list
                         collect s_edge_name edge .
                                 car s_edge_prop_ edge )
end$

symbolic procedure calc_spur u$
 simp!* calc_spur0 u$                %FOR KRYUKOV NEEDS$

endmodule;

end;
