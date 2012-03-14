module map2strn;

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


%************* TRANSFORMATION OF MAP TO STRAND **********************$
%                                                                    $
%                       25.11.87                                     $
%                                                                    $
%********************************************************************$

  exports    color!-strand,contract!-strand $
  imports  nil$

%---------------- utility added 09.06.90 ---------------------------
symbolic procedure constimes u;
% u=list of terms
% inspect u, delete all 1's
% and form smar product   $
 cstimes(u,nil)$

symbolic procedure cstimes(u,s);
if null u then
  if null s then 1
  else if null cdr s then car s
       else 'times . s
else if car u = 1 then cstimes(cdr u,s)
     else cstimes(cdr u,car u . s)$

symbolic procedure consrecip u;
% do same as consTimes
if or(car u = 1,car u = -1) then car u
else 'recip . u$

symbolic procedure listquotient(u,v)$
% the same !!!
if v=1 then u
else
  if v = u then 1
  else list('quotient,u,v)$

symbolic procedure consplus u;
% u=list of terms
% inspect u, delete all 0's
% and form smar sum   $
 csplus(u,nil)$

symbolic procedure csplus(u,s);
if null u then
  if null s then 0
  else if null cdr s then car s
       else 'plus . s
else if car u = 0 then csplus(cdr u,s)
     else csplus(cdr u,car u . s)$

%--------------------------------------------------------------------



%---------------- CONVERTING OF MAP TO STRAND DIAGRAM ---------------$

symbolic procedure map_!-to!-strand(edges,map_)$
%.....................................................................
% ACTION: CONVERTS "MAP_" WITH "EDGES" INTO STRAND DIAGRAM.
% STRAND ::= <LIST OF STRAND VERTICES>,
% STRAND VERTEX ::= <SVERTEX NAME> . (<LIST1 OF ROADS> <LIST2 ...>),
% ROAD ::= <ATOM> . <NUMBER>.
% LIST1,2 CORRESPOND TO OPPOSITE SIDES OF STRAND VERTEX.
% ROADS LISTED CLOCKWISE.
%....................................................................$
if null edges then nil
else mk!-strand!-vertex(car edges,map_) .
                                map_!-to!-strand(cdr edges,map_)$

%YMBOLIC PROCEDURE MAP_!-TO!-STRAND(EDGES,MAP_)$
%F NULL EDGES THEN NIL
%LSE (LAMBDA SVERT$ IF SVERT THEN SVERT .
%                               MAP_!-TO!-STRAND(CDR EDGES,MAP_)
%                   ELSE MAP_!-TO!-STRAND(CDR EDGES,MAP_) )
%          MK!-STRAND!-VERTEX(CAR EDGES,MAP_)$

symbolic procedure mk!-strand!-vertex(edge,map_)$
begin
  scalar vert1,vert2,tail$
         tail:=incident(edge,map_,1)$
         vert1:=car tail$
         tail:=incident(edge,cdr tail,add1 cdar vert1)$
         vert2:= if null tail then mk!-external!-leg edge
                 else car tail$
   return %F NULL VERT2 THEN NIL
               mk!-strand!-vertex2(edge,vert1,vert2)
end$

symbolic procedure incident(edge,map_,vertno)$
if null map_ then nil
else (lambda z$ if z then  z . cdr map_
                else incident(edge,cdr map_,add1 vertno) )
             incident1(              edge,car map_,vertno)$

symbolic procedure incident1(edname,vertex,vertno)$
if eq(edname,s!-edge!-name car vertex) then
             mk!-road!-name(cadr vertex,caddr vertex,vertno)
else if eq(edname,s!-edge!-name cadr vertex) then
         mk!-road!-name(caddr vertex,car vertex,vertno)
     else if eq(edname,s!-edge!-name caddr vertex) then
                 mk!-road!-name(car vertex,cadr vertex,vertno)
          else nil$

symbolic procedure mk!-strand!-vertex2(edge,vert1,vert2)$
list(edge,   vert1,             vert2)$

%------------------ COLOURING OF ROADS IN STRAND --------------------$

symbolic procedure color!-strand(alst,map_,count)$
%.....................................................................
% ACTION: GENERATE REC. ALIST COLORING STRAND, CORRESPONDING TO "MAP_".
% COLORING OF STRAND INDUCED BY "MAP_" COLORING, DEFINED BY ALIST
% "ALST". "COUNT" COUNTS MAP_ VERTICES. INITIALLY IS 1.
% REC.ALIST::= ( ... <(ATOM1 . COL1 ATOM2 . COL2 ...) . NUMBER> ... )
% WHERE COL1 IS COLOR OF ROAD=ATOM1 . NUMBER.
%....................................................................$
if null map_ then nil
else  (color!-roads(alst,car map_) . count) .
                          color!-strand(alst,cdr map_,add1 count)$

symbolic procedure color!-roads(alst,vertex)$
begin
    scalar e1,e2,e3,lines$
    e1:=getedge(s!-edge!-name car vertex,alst)$
    e2:=getedge(s!-edge!-name cadr vertex,alst)$
    e3:=getedge(s!-edge!-name caddr vertex,alst)$
    lines:=(e1+e2+e3)/2$
    e1:=lines-e1$
    e2:=lines-e2$
    e3:=lines-e3$
  return list(
               s!-edge!-name car vertex . e1,
               s!-edge!-name cadr vertex . e2,
               s!-edge!-name caddr vertex . e3)
end$

symbolic procedure zero!-roads l$
%---------------------------------------------------------------------
% L IS OUTPUT OF COLOR!-STRAND
%--------------------------------------------------------------------$
if null l then nil
else (lambda z$ if z then z . zero!-roads cdr l
                else zero!-roads cdr l)
              z!-roads car l$

symbolic procedure z!-roads y$
(lambda w$  w and (car w . cdr y))
    ( if (0=cdr caar y)then caar y
      else if  (0=cdr cadar y) then cadar y
           else if (0=cdr caddar y) then caddar y
                else nil)$

%------------------- CONTRACTION OF STRAND --------------------------$

symbolic procedure deletez1(strand,alst)$
%.....................................................................
% ACTION: DELETES FROM "STRAND" VERTICES WITH NAMES HAVING 0-COLOR
% VIA MAP_-COLORING ALIST "ALST".
%....................................................................$
if null strand then nil
else if 0 = cdr assoc(caar strand,alst) then
                          deletez1(cdr strand,alst)
     else car strand . deletez1(cdr strand,alst)$

symbolic procedure contract!-strand(strand,slst)$
%.....................................................................
% ACTION: CONTRACTS "STRAND".
% "SLST" IS REC. ALIST COLORING "STRAND"
%....................................................................$
contr!-strand(strand,zero!-roads slst)$

symbolic procedure contr!-strand(strand,zlst)$
if null zlst then strand
else contr!-strand(contr1!-strand(strand,car zlst),cdr zlst)$

symbolic procedure contr1!-strand(strand,rname)$
contr2!-strand(strand,rname,nil,nil)$

symbolic procedure contr2!-strand(st,rname,rand,flag_)$
if null st then rand
else (lambda z$
          if z then
            if member(car z,cdr z) then sappend(st,rand)  % 16.12 ****$
            else
                if null flag_ then
                  contr2!-strand(contr2(z,cdr st,rand),rname,nil,t)
                else contr2(z,cdr st,rand)
          else contr2!-strand(cdr st,rname,car st . rand,nil)  )
      contrsp(car st,rname)$

symbolic procedure contrsp(svertex,rname)$
contrsp2(cadr svertex,caddr svertex,rname)
                or
contrsp2(caddr svertex,cadr svertex,rname)$

symbolic procedure contrsp2(l1,l2,rname)$
if 2 = length l1 then
 if rname = car l1 then (cadr l1) . l2
 else if rname = cadr l1 then (car l1) . l2
else nil$

symbolic procedure contr2(single,st,rand)$
if null st then contr(single,rand)
else if null rand then contr(single,st)
     else split!-road(single,car st) . contr2(single,cdr st,rand)$

symbolic procedure contr(single,strand)$
if null strand then nil
else split!-road(single,car strand) . contr(single,cdr strand)$

symbolic procedure split!-road(single,svertex)$
list(car svertex,
sroad(car single,cdr single,cadr svertex),
             sroad(car single,cdr single,caddr svertex))$

symbolic procedure sroad(line_,lines,lst)$
if null lst then nil
else if line_ = car lst then sappend(lines,cdr lst)
     else car lst . sroad(line_,lines,cdr lst)$

endmodule;

end;
